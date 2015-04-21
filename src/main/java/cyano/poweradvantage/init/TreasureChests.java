package cyano.poweradvantage.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

public abstract class TreasureChests {

	private static boolean initDone = false;
	public static void init(){
		if(initDone)return;
		Items.init();
		
		
		initDone = true;
	}
	
	private static WeightedRandomChestContent makeChestLootEntry(Item item, int spawnWeight, int maxQuantity){
		if(item == null) return null;
		if(spawnWeight <= 0) return null;
		ItemStack itemStack = new ItemStack(item,1);
		int minSpawnNumber=1;
		int maxSpawnNumber=maxQuantity;
		return new WeightedRandomChestContent(itemStack,minSpawnNumber,maxSpawnNumber,spawnWeight);
	}
}
