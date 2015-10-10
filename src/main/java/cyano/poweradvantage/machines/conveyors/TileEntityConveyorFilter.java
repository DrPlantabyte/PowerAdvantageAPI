package cyano.poweradvantage.machines.conveyors;

import cyano.poweradvantage.util.InventoryWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public abstract class TileEntityConveyorFilter extends TileEntityConveyor {

	
	public TileEntityConveyorFilter(){
		this(1);
	}
	

	public abstract boolean matchesFilter(ItemStack item);
	
	
	@Override
	public void update() {
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
				if(target != null){
					if( target instanceof IInventory){
						ISidedInventory them;
						if(target instanceof  TileEntityChest){
							// special handling for chests in case of double-chest
							them = InventoryWrapper.wrap(handleChest((TileEntityChest)target));
						} else {
							them = InventoryWrapper.wrap((IInventory)target);
						}
						if(transferItem(them,theirDir,this,myDir)){
							transferCooldown = transferInvterval;
							this.markDirty();
						}
					}
				}
			} else {
				EnumFacing myDir = dir;
				EnumFacing theirDir = dir.getOpposite();
				TileEntity target = w.getTileEntity(getPos().offset(myDir));
				TileEntity dropTarget = w.getTileEntity(getPos().offset(EnumFacing.DOWN));
				if(matchesFilter(this.getInventory()[0]) && isValidItem(getInventory()[0],dropTarget,EnumFacing.UP)){
					dropItem(dropTarget);
					return;
				}
				if(target != null){
					if( target instanceof IInventory){
						ISidedInventory them;
						if(target instanceof  TileEntityChest){
							// special handling for chests in case of double-chest
							them = InventoryWrapper.wrap(handleChest((TileEntityChest)target));
						} else {
							them = InventoryWrapper.wrap((IInventory)target);
						}
						if(transferItem(this,myDir,them,theirDir)){
							transferCooldown = transferInvterval;
							this.markDirty();
						}
					}
				}
			}
		}
	}
	
	private boolean isValidItem(ItemStack item, TileEntity target, EnumFacing side){
		if(item == null) return false;
		if(target instanceof IInventory){
			ISidedInventory dt = InventoryWrapper.wrap((IInventory)target);
			int[] slots = dt.getSlotsForFace(side);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				if(dt.isItemValidForSlot(slot, item)){
					return true;
				}
			}
			return false;
		}else{
			return true;
		}
	}
	
	private void dropItem(TileEntity target) {
		World w = getWorld();
		if(target == null && !(w.getBlockState(getPos().down()).getBlock().getMaterial().blocksMovement())){
			// drop item in the air
			EntityItem ie = new EntityItem(w,getPos().getX()+0.5,getPos().getY()-0.5,getPos().getZ()+0.5,getInventory()[0]);
			ie.motionX = 0;
			ie.motionZ = 0;
			ie.motionY = 0;
			w.spawnEntityInWorld(ie);
			getInventory()[0] = null;
			transferCooldown = transferInvterval;
			this.markDirty();
		} else if(target instanceof IInventory){
			// add item to inventory
			EnumFacing myDir = EnumFacing.DOWN;
			EnumFacing theirDir = EnumFacing.UP;
			if(transferItem(this,myDir,InventoryWrapper.wrap((IInventory)target),theirDir)){
				transferCooldown = transferInvterval;
				this.markDirty();
			}
		}
	}


	public TileEntityConveyorFilter(int inventorySize){
		super(inventorySize);
	}
		
	
	/**
	 * Gets the direction that the blockstate is facing
	 * @return an EnumfFacing
	 */
	@Override
	public EnumFacing getFacing(){
		IBlockState state = getWorld().getBlockState(getPos());
		if(state.getProperties().containsKey(BlockConveyorFilter.FACING)){
			return (EnumFacing) state.getProperties().get(BlockConveyorFilter.FACING);
		}else{
			FMLLog.warning(this.getClass().getName()+".getFacing(): blockstate for block at this position ("+getPos()+") does not have property \"BlockConveyorFilter.FACING\"!");
			return EnumFacing.NORTH;
		}
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
		if(side == EnumFacing.DOWN) {
			return slot == 0 && matchesFilter(targetItem);
		} else {
			return super.canExtractItem(slot, targetItem, side) && (!matchesFilter(targetItem));
		}
    }
}
