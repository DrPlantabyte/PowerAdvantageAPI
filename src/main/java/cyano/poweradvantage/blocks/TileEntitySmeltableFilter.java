package cyano.poweradvantage.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class TileEntitySmeltableFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		return FurnaceRecipes.instance().getSmeltingResult(item) != null;
	}

}
