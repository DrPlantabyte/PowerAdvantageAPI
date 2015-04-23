package cyano.poweradvantage.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class TileEntityConveyor extends TileEntity implements IUpdatePlayerListBox, ISidedInventory {


	
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
		// TODO testing
		World w = getWorld();
		if(w.isRemote) return;
		if(transferCooldown > 0) transferCooldown--;
		if(transferCooldown == 0){
			// do tick update
			EnumFacing dir =this.getFacing(); 
			if(getInventory()[0] == null){
				// not holding item, get item
				EnumFacing myDir = dir.getOpposite();
				EnumFacing theirDir = dir;
				TileEntity target = w.getTileEntity(getPos().offset(myDir));
				if(target != null && target instanceof ISidedInventory){
					ISidedInventory them = (ISidedInventory)target;
					if(transferItem(them,theirDir,this,myDir)){
						transferCooldown = transferInvterval;
						this.markDirty();
					}
				}
			} else {
				EnumFacing myDir = dir;
				EnumFacing theirDir = dir.getOpposite();
				TileEntity target = w.getTileEntity(getPos().offset(myDir));
				if(target != null && target instanceof ISidedInventory){
					ISidedInventory them = (ISidedInventory)target;
					if(transferItem(this,myDir,them,theirDir)){
						transferCooldown = transferInvterval;
						this.markDirty();
					}
				}
			}
		}
	}
	
	protected boolean transferItem(ISidedInventory src, EnumFacing srcFace, ISidedInventory dest, EnumFacing destFace){
		int[] srcValidSlots = src.getSlotsForFace(srcFace);
		for(int i = 0; i < srcValidSlots.length; i++){
			ItemStack item = src.getStackInSlot(srcValidSlots[i]);
			if(item != null){
				int[] destValidSlots = dest.getSlotsForFace(destFace);
				for(int j = 0; j < destValidSlots.length; j++){
					if(dest.canInsertItem(destValidSlots[j], item, destFace)){
						if(dest.getStackInSlot(destValidSlots[j]) == null){
							dest.setInventorySlotContents(destValidSlots[j], src.decrStackSize(srcValidSlots[i], 1));
							return true;
						}else if( dest.getStackInSlot(destValidSlots[j]).stackSize < (dest.getInventoryStackLimit() - 1)){
							src.decrStackSize(srcValidSlots[i], 1);
							dest.getStackInSlot(destValidSlots[j]).stackSize++;
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
	 * @return
	 */
	public EnumFacing getFacing(){
		IBlockState state = getWorld().getBlockState(getPos());
		if(state.getProperties().containsKey("facing")){
			return (EnumFacing) state.getProperties().get("facing");
		}else{
			FMLLog.warning(this.getClass().getName()+".getFacing(): blockstate for block at this position ("+getPos()+") does not have property \"facing\"!");
			return EnumFacing.NORTH;
		}
	}
	
	protected ItemStack[] getInventory(){
		return inventory;
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
        if (this.getInventory()[slot].stackSize == 0) {
            this.getInventory()[slot] = null;
        }
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
		return 1;
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
	public ItemStack getStackInSlotOnClosing(final int slot) {
		if (this.getInventory()[slot] != null) {
			final ItemStack itemstack = this.getInventory()[slot];
			this.getInventory()[slot] = null;
			return itemstack;
		}
		return null;
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
	public IChatComponent getDisplayName() {
        if (this.hasCustomName()) {
            return new ChatComponentText(this.getName());
        }
        return new ChatComponentTranslation(this.getName(), new Object[0]);
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
