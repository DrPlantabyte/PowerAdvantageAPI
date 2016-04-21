package cyano.poweradvantage.machines.conveyors;

import net.minecraft.item.ItemStack;

public class TileEntityInventoryFilter extends TileEntityConveyorFilter{

	
	public static final int NUM_FILTER_SLOTS = 9;
	
	public TileEntityInventoryFilter(){
		super(NUM_FILTER_SLOTS+1);
	}
	
	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		for(int i = 1; i < this.getInventory().length; i++){
			if(this.getInventory()[i] != null && ItemStack.areItemsEqual(this.getInventory()[i], item)){
				return true;
			}
		}
		return false;
	}

}
