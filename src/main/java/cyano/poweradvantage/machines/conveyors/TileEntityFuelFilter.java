package cyano.poweradvantage.machines.conveyors;


import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFuelFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		return TileEntityFurnace.isItemFuel(item);
	}

}
