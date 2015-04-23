package cyano.poweradvantage.blocks;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class TileEntityFoodFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		return item.getItem() instanceof ItemFood;
	}

}
