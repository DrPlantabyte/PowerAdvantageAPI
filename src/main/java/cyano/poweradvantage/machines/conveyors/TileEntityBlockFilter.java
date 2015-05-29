package cyano.poweradvantage.machines.conveyors;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TileEntityBlockFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		return item.getItem() instanceof ItemBlock;
	}

}
