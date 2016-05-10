package cyano.poweradvantage.machines.conveyors;

import cyano.poweradvantage.util.InventoryWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TileEntityConveyor extends TileEntity implements ITickable, ISidedInventory {

	
	
	public static String unlocalizedName = TileEntityConveyorFilter.class.getName();
	
	protected static final Random globalRand = new Random();
	
	private final ItemStack[] inventory;

	protected int transferCooldown = 0;
	protected final int transferInvterval = 7;
	
	
	public TileEntityConveyor(){
		this(1);
	}
	
	public TileEntityConveyor(int inventorySize){
		super();
		inventory = new ItemStack[inventorySize];
	}
	
	
	@Override
	public void update() {
		World w = getWorld();
		if(w.isRemote) return;
		if(transferCooldown > 0) transferCooldown--;
		if(transferCooldown == 0){
			// do tick update
			EnumFacing dir =this.getFacing();
			
			// deposit item
			EnumFacing myDir = dir;
			EnumFacing theirDir = dir.getOpposite();
			TileEntity target = w.getTileEntity(getPos().offset(myDir));
			if(target != null) {
				if( target instanceof IInventory){
					ISidedInventory them;TileEntityHopper p;
					if(target instanceof  TileEntityChest){
						// special handling for chests in case of double-chest
						IInventory realChest = handleChest((TileEntityChest)target);
						if(realChest == null) return; // chest cannot open or is not initialized
						them = InventoryWrapper.wrap(realChest);
					} else {
						them = InventoryWrapper.wrap((IInventory)target);
					}
					if(transferItem(this,myDir,them,theirDir)){
						transferCooldown = transferInvterval;
						this.markDirty();
					}
				}
			}
			
			// get item
			myDir = dir.getOpposite();
			theirDir = dir;
			BlockPos upstreamBlock = getPos().offset(myDir);
			boolean pickedUpItem = false;
			if(w.isAirBlock(upstreamBlock)){
				// grab items sitting on the ground behind the conveyor
				List<EntityItem> list = w.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(
						upstreamBlock.getX(), upstreamBlock.getY(), upstreamBlock.getZ(), 
						upstreamBlock.getX()+1, upstreamBlock.getY()+1, upstreamBlock.getZ()+1));
				if(!list.isEmpty()){
					EntityItem e = list.get(0);
					if(!e.isAirBorne){
						if(canInsertItemInto(e.getEntityItem(),this,myDir)){
							ItemStack newItem = e.getEntityItem().copy();
							newItem.stackSize = 1;
							e.getEntityItem().stackSize--;
							if(getInventory()[0] == null){
								getInventory()[0] = newItem;
							} else {
								getInventory()[0].stackSize++;
							}
							if(e.getEntityItem().stackSize <= 0){
								e.setDead();
							}
							this.markDirty();
							pickedUpItem = true;
						}
					}
				}
			} else {
				target = w.getTileEntity(upstreamBlock);
				if(target != null){
					if( target instanceof IInventory){
						ISidedInventory them;
						if(target instanceof  TileEntityChest){
							// special handling for chests in case of double-chest
							IInventory realChest = handleChest((TileEntityChest)target);
							if(realChest == null) return; // chest cannot open or is not initialized
							them = InventoryWrapper.wrap(realChest);
						} else {
							them = InventoryWrapper.wrap((IInventory)target);
						}
						if(transferItem(them,theirDir,this,myDir)){
							this.markDirty();
							pickedUpItem = true;
						}
					}
				}
			}
			transferCooldown = transferInvterval;
		}
	}
	
	protected static boolean isLocked(Object o){
		return o instanceof ILockableContainer && ((ILockableContainer)o).isLocked();
	}

	protected IInventory handleChest(TileEntityChest chest){
		final Block block = getWorld().getBlockState(chest.getPos()).getBlock();
		if (block instanceof BlockChest) {
			// Note: BlockChest.getLockableContainer(...) returns null if chest is blocked from opening
			return ((BlockChest)block).getLockableContainer(getWorld(), chest.getPos());
		}
		return chest;
	}
	
	protected static boolean canInsertItemInto(ItemStack item, ISidedInventory dest, EnumFacing destFace){
		if(item == null || item.getItem() == null || isLocked(dest)){
			return false;
		}
		int[] slots = dest.getSlotsForFace(destFace);
		for(int i = 0; i < slots.length; i++){
			int slot = slots[i];
			if(dest.canInsertItem(slot, item, destFace)){
				ItemStack destItem = dest.getStackInSlot(slot);
				if(destItem == null) {
					return true;
				} else {
					return ItemStack.areItemsEqual(item, destItem);
				}
			}
		}
		return false;
	}
	
	protected static boolean transferItem(ISidedInventory src, EnumFacing srcFace, ISidedInventory dest, EnumFacing destFace){
		if(isLocked(src) || isLocked(dest)){
			return false;
		}
		int[] srcValidSlots = src.getSlotsForFace(srcFace);
		for(int i = 0; i < srcValidSlots.length; i++){
			ItemStack item = src.getStackInSlot(srcValidSlots[i]);
			if(item == null) continue;
			if(src.canExtractItem(srcValidSlots[i], item, srcFace)){
					int[] destValidSlots = dest.getSlotsForFace(destFace);
					// First look for stackable items
					for(int j = 0; j < destValidSlots.length; j++){
						if(dest.canInsertItem(destValidSlots[j], item, destFace)){
							ItemStack otherItem = dest.getStackInSlot(destValidSlots[j]);
							if( ItemStack.areItemsEqual(item, otherItem)
									&& otherItem.stackSize < dest.getInventoryStackLimit()
									&& otherItem.stackSize < otherItem.getItem().getItemStackLimit(otherItem)){
								src.decrStackSize(srcValidSlots[i], 1);
								otherItem.stackSize++;
								return true;
							}
						}
					}
					// then look for empty slots
					for(int j = 0; j < destValidSlots.length; j++){
						if(dest.canInsertItem(destValidSlots[j], item, destFace)){
							ItemStack otherItem = dest.getStackInSlot(destValidSlots[j]);
							if(otherItem == null){
								dest.setInventorySlotContents(destValidSlots[j], src.decrStackSize(srcValidSlots[i], 1));
								return true;
							}
						}
					}
			}
		}
		return false;
	}
	
	
	
	/**
	 * Gets the direction that the blockstate is facing
	 * @return an EnumfFacing
	 */
	public EnumFacing getFacing(){
		IBlockState state = getWorld().getBlockState(getPos());
		if(state.getProperties().containsKey(BlockConveyor.FACING)){
			return (EnumFacing) state.getProperties().get(BlockConveyor.FACING);
		}else{
			FMLLog.warning(this.getClass().getName()+".getFacing(): blockstate for block at this position ("+getPos()+") does not have property \"BlockConveyor.FACING\"!");
			return EnumFacing.NORTH;
		}
	}
	
	protected ItemStack[] getInventory(){
		return inventory;
	}

	
	/**
	 * You must override this method and call super.readFromNBT(...).<br><br>
	 * This method reads data from an NBT data tag (either from a data packet 
	 * or loaded from the Chunk).
	 * @param tagRoot The root of the NBT
	 */
	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		ItemStack[] inventory = this.getInventory();
		if(inventory != null ){
			final NBTTagList nbttaglist = tagRoot.getTagList("Items", 10);
			for (int i = 0; i < nbttaglist.tagCount() && i < inventory.length; ++i) {
				final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				final byte n = nbttagcompound1.getByte("Slot");
				if (n >= 0 && n < inventory.length) {
					inventory[n] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			}
		}
		if (tagRoot.hasKey("CustomName", 8)) {
			this.customName = tagRoot.getString("CustomName");
		}
	}
	
	/**
	 * You must override this method and call super.writeToNBT(...).<br><br>
	 * This method writes data to an NBT data tag (either to a data packet 
	 * or saved to the Chunk).
	 * @param tagRoot The root of the NBT
	 */
	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		ItemStack[] inventory = this.getInventory();
		if(inventory != null ){
			final NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < inventory.length; ++i) {
				if (inventory[i] != null) {
					final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte)i);
					inventory[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			tagRoot.setTag("Items", nbttaglist);
		}
		if (this.hasCustomName()) {
			tagRoot.setString("CustomName", this.customName);
		}
	}

	/**
	 * Override to keep the tile entity from being deleted each time the blockstate is updated
	 * @param world world instance
	 * @param pos coordinate
	 * @param oldState State before change
	 * @param newSate State after change
	 * @return true if this TileEntity should be invalidated (deleted and recreated), false otherwise
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	///// ISidedInventory METHODS /////
	@Override
	public void clear() {
		Arrays.fill(inventory, null);
	}

	@Override
	public void closeInventory(EntityPlayer p) {
		// do nothing
	}

	@Override
	public ItemStack decrStackSize(int slot, int decrement) {
		if (this.getInventory()[slot] == null) {
			return null;
		}
		if (this.getInventory()[slot].stackSize <= decrement) {
			final ItemStack itemstack = this.getInventory()[slot];
			this.getInventory()[slot] = null;
			return itemstack;
		}
		final ItemStack itemstack = this.getInventory()[slot].splitStack(decrement);
		return itemstack;
	}

	@Override
	public int getField(int n) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return getInventory().length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.getInventory()[slot];
	}
	
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack i = this.getInventory()[slot];
		this.getInventory()[slot] = null;
		return i;
	}



	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		return slot < getInventory().length && getInventory()[0] == null; 
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer p_isUseableByPlayer_1_) {
		return this.worldObj.getTileEntity(this.pos) 
				== this && p_isUseableByPlayer_1_.getDistanceSq(
						(double)this.pos.getX() + 0.5, 
						(double)this.pos.getY() + 0.5, 
						(double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public void openInventory(EntityPlayer arg0) {
		// do nothing
	}

	@Override
	public void setField(int arg0, int arg1) {
		// do nothing
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		final boolean flag = item != null && item.isItemEqual(this.getInventory()[slot]) 
				&& ItemStack.areItemStackTagsEqual(item, this.getInventory()[slot]);
		this.getInventory()[slot] = item;
		if (item != null && item.stackSize > this.getInventoryStackLimit()) {
			item.stackSize = this.getInventoryStackLimit();
		}
		if (slot == 0 && !flag) {
			this.markDirty();
		}
	}

	@Override
	public TextComponentBase getDisplayName() {
		if (this.hasCustomName()) {
			return new TextComponentString(this.getName());
		}
		return new TextComponentTranslation(this.getName(), new Object[0]);
	}

	private String customName = null;
	/**
	 * boilerplate inventory code
	 * @return either the unlocalized name for this entity or a custom name 
	 * given by the player (i.e. with an anvil)
	 */
	@Override
	public String getName() {
		return this.hasCustomName() ? this.customName : this.getUnlocalizedName();
	}
	
	/**
	 * Gets the unlocalized String passed to the constructor
	 * @return A String used in name localization by client GUI 
	 */
	public String getUnlocalizedName(){
		return unlocalizedName;
	}

	/**
	 * boilerplate inventory code
	 * @return true if this block has been renamed, false otherwise
	 */
	@Override
	public boolean hasCustomName() {
		return customName != null;
	}


	/**
	 * Determines whether another block (such as a Hopper) is allowed to pull an 
	 * item from this block out through a given face
	 * @param slot The inventory slot (index) of the item in question
	 * @param targetItem The item to be pulled
	 * @param side The side of the block through which to pull the item
	 * @return true if the item is allowed to be pulled, false otherwise
	 */
	@Override
	public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
		return slot == 0;
	}
	/**
	 * Determines whether another block (such as a Hopper) is allowed to put an 
	 * item into this block through a given face
	 * @param slot The inventory slot (index) of the item in question
	 * @param srcItem The item to be inserted
	 * @param side The side of the block through which to insertt the item
	 * @return true if the item is allowed to be inserted, false otherwise
	 */
	@Override
	public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		return slot == 0 && this.isItemValidForSlot(slot, srcItem);
	}

	/** cache for default implementation of getSlotsForFace(...)*/
	private int[] slotAccessCache = {0};
	/**
	 * This method is used to tell other blocks (such as a hopper) which 
	 * inventory slots they are allowed to interact with, based on which face of 
	 * this block they are touching. Override this method to make output slots 
	 * go out the bottom of this block and input slots go in the top (like a 
	 * furnace).<br><br>
	 * The default implementation here makes all inventory slots accessible from 
	 * all sides.
	 * @param side The side of this block through which the other entity wants to 
	 * access the inventory 
	 * @return An array listing all of the inventory indices that are accessible 
	 * through the given side of this block
	 */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return slotAccessCache;
	}
	//////////  //////////

	
	
	

}
