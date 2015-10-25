package cyano.poweradvantage.machines.conveyors;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;

public class TileEntityPlantFilter extends TileEntityConveyorFilter{

	@Override
	public boolean matchesFilter(ItemStack item) {
		if(item == null) return false;
		if (item.getItem() instanceof IPlantable) return true;
		if(item.getItem() instanceof ItemBlock){
			Block b = ((ItemBlock)item.getItem()).getBlock();
			if(b instanceof IPlantable) return true;
			if(b instanceof IGrowable) return true;
			
		}
		return false;
	}

}
