package cyano.poweradvantage.init;

import cyano.basemetals.entities.EntityBetterVillager;
import net.minecraft.entity.passive.EntityVillager;

public abstract class Villages {
	// TODO: add machinist villager

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Entities.init();
		
		// TODO: switch to VillagerTradeHelper
			EntityBetterVillager.addVillagerTrades(3, 3, 1, new EntityVillager.ListItemForEmeralds(Items.sprocket, new EntityVillager.PriceInfo(-4,-1)));
			EntityBetterVillager.addVillagerTrades(0, 1, 1, new EntityVillager.ListItemForEmeralds(Items.bioplastic_ingot, new EntityVillager.PriceInfo(-8,-4)));

		
		initDone = true;
	}
}
