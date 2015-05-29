package cyano.poweradvantage.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

public abstract class WorldGen {

	private static boolean initDone = false;
	public static void init(){
		if(initDone)return;
		Items.init();
		Blocks.init();
		
		
		initDone = true;
	}
	
}
