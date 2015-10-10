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
			ISidedInventory dt = InventoryWrapper.wrap((IInventory)target);
			int[] slots = dt.getSlotsForFace(f.getOpposite());
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				if(dt.isItemValidForSlot(slot, item)){
					ItemStack targetSlot = dt.getStackInSlot(slot);
					return (targetSlot == null) || 
							(ItemStack.areItemsEqual(item, targetSlot) 
									&& !targetSlot.getItem().isDamageable()
									&& targetSlot.stackSize < targetSlot.getMaxStackSize()
									&& targetSlot.stackSize < dt.getInventoryStackLimit());
				}
			}
			return false;
		}else{
			return false;
		}
	}

}
