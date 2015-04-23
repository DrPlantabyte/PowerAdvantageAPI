package cyano.poweradvantage.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;

public class TileEntityInventoryFilter extends TileEntityConveyorFilter{

	
	public static final int NUM_FILTER_SLOTS = 9;
	
	public TileEntityInventoryFilter(){
		super(NUM_FILTER_SLOTS+1);
	}
	
	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		for(int i = 1; i < this.getInventory().length; i++){
			if(ItemStack.areItemsEqual(this.getInventory()[i], item)){
				return true;
			}
		}
		return false;
	}

}
