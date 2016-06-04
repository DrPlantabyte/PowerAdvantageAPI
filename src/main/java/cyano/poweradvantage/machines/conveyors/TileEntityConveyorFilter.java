package cyano.poweradvantage.machines.conveyors;

import cyano.poweradvantage.util.InventoryWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
				BlockPos targetPos = getPos().offset(myDir);
				TileEntity target = w.getTileEntity(targetPos);
				if(target != null){
					if( target instanceof IInventory){
						ISidedInventory them = InventoryWrapper.wrap(TileEntityHopper.getInventoryAtPosition(getWorld(),targetPos.getX(), targetPos.getY(), targetPos.getZ()));
						if(transferItem(them,theirDir,this,myDir)){
							this.markDirty();
						}
					}
				}
			} else {
				EnumFacing myDir = dir;
				EnumFacing theirDir = dir.getOpposite();
				BlockPos targetPos = getPos().offset(myDir);
				TileEntity target = w.getTileEntity(targetPos);
				TileEntity dropTarget = w.getTileEntity(getPos().offset(EnumFacing.DOWN));
				if(matchesFilter(this.getInventory()[0])){
					if(isValidItemFor(getInventory()[0],dropTarget,EnumFacing.UP)){
						dropItem(dropTarget);
						return;
					}
				} else
				if( target instanceof IInventory){
					ISidedInventory them = InventoryWrapper.wrap(TileEntityHopper.getInventoryAtPosition(getWorld(),targetPos.getX(), targetPos.getY(), targetPos.getZ()));
					if(transferItem(this,myDir,them,theirDir)){
						this.markDirty();
					}
				}
			}

			transferCooldown = transferInvterval;
		}
	}
	
	protected boolean isValidItemFor(ItemStack item, TileEntity target, EnumFacing side){
		if(item == null) return false;
		if(target instanceof IInventory){
			BlockPos targetPos = target.getPos();
			ISidedInventory dt = InventoryWrapper.wrap(TileEntityHopper.getInventoryAtPosition(getWorld(),targetPos.getX(), targetPos.getY(), targetPos.getZ()));
			int[] slots = dt.getSlotsForFace(side);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				if(dt.canInsertItem(slot, item, side)){
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
		IBlockState bs = w.getBlockState(getPos().down());
		if(target == null && !(bs.getMaterial().blocksMovement())){
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
			BlockPos targetPos = target.getPos();
			ISidedInventory targetInv = InventoryWrapper.wrap(TileEntityHopper.getInventoryAtPosition(getWorld(),targetPos.getX(), targetPos.getY(), targetPos.getZ()));
			if(transferItem(this,myDir,targetInv,theirDir)){
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
		boolean filterMatch =  matchesFilter(targetItem);
		if(slot != 0) return false;
		if(side == EnumFacing.DOWN) {
			return filterMatch;
		} else {
			return (!filterMatch) && super.canExtractItem(slot, targetItem, side) ;
		}
	}
}
