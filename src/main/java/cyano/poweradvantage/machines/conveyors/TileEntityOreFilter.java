package cyano.poweradvantage.machines.conveyors;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityOreFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		Item i = item.getItem();
		int[] oreIDs = OreDictionary.getOreIDs(item);
		if(oreIDs == null) return false;
		for(int n = 0; n < oreIDs.length; n++){
			String dictName = OreDictionary.getOreName(oreIDs[n]);
			if(dictName.startsWith("ingot") || dictName.startsWith("ore") || dictName.startsWith("dust")){
				return true;
			}
		}
		return false;
	}

}
