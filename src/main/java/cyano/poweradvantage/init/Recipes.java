package cyano.poweradvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cyano.poweradvantage.registry.FuelRegistry;

public abstract class Recipes {

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		
		initDone = true;
	}
	
}
