package cyano.poweradvantage.machines.conveyors;

import cyano.poweradvantage.util.InventoryWrapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityOverflowFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null || item.getItem() == null) return false;
		boolean canDrop = canInsert(item, EnumFacing.DOWN);
		boolean canPass = canInsert(item, this.getFacing());
		if(canPass){
			return canDrop;
		} else {
			return true;
		}
	}

	private boolean canInsert(ItemStack item, EnumFacing f){
		TileEntity target = getWorld().getTileEntity(getPos().offset(f));
		if(target instanceof IInventory){
			EnumFacing side = f.getOpposite();
			ISidedInventory dt = InventoryWrapper.wrap((IInventory)target);
			int[] slots = dt.getSlotsForFace(side);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				if(dt.canInsertItem(slot, item, side)){
					ItemStack targetSlot = dt.getStackInSlot(slot);
					if( (targetSlot == null) || 
							(ItemStack.areItemsEqual(item, targetSlot) 
									&& !targetSlot.getItem().isDamageable()
									&& targetSlot.stackSize < targetSlot.getMaxStackSize()
									&& targetSlot.stackSize < dt.getInventoryStackLimit())){
						return true;
					}
				}
			}
			return false;
		}else{
			return false;
		}
	}
	
	@Override
	protected boolean isValidItemFor(ItemStack item, TileEntity target, EnumFacing side){
		// do all of the logic in the matchesFilter method
		return true;
	}

}
