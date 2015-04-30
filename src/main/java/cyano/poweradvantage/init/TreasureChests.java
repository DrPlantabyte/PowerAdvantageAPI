package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public abstract class TreasureChests {

	private static boolean initDone = false;
	public static void init(){
		if(initDone)return;
		Blocks.init();
		Items.init();

		addChestLoot(new ItemStack(Items.sprocket,1),20f,2,4);
		addChestLoot(new ItemStack(Blocks.fluid_pipe,1),10f,1,3);
		addChestLoot(new ItemStack(Blocks.storage_tank,1),3f,1,0);
		addChestLoot(new ItemStack(Blocks.fluid_discharge,1),2f,1,0);
		addChestLoot(new ItemStack(Blocks.fluid_drain,1),2f,1,0);
		addChestLoot(new ItemStack(Blocks.item_conveyor,1),5f,1,0);
		
		initDone = true;
	}
	
	private static WeightedRandomChestContent makeChestLootEntry(ItemStack itemStack, int spawnWeight, int minQuantity,int maxQuantity){
		if(itemStack == null) return null;
		if(spawnWeight <= 0) return null;
		return new WeightedRandomChestContent(itemStack,minQuantity,maxQuantity,spawnWeight);
	}
	
	private static void addChestLoot(ItemStack item, float weight, int number, int range){
		WeightedRandomChestContent loot = makeChestLootEntry(item,(int)(weight*PowerAdvantage.chestLootFactor),number,number+range);
		if(loot != null){
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(loot);
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(loot);
		}
	}
}
