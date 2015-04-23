package cyano.poweradvantage.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;

public class TileEntityPlantFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		return item.getItem() instanceof IPlantable;
	}

}
