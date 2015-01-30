package cyano.poweradvantage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.registry.FuelRegistry;

public class Items {

	public static Item stone_rockHammer;
	public static Item iron_rockHammer;
	public static Item steel_rockHammer;
	public static Item diamond_rockHammer;
	public static Item powderedCoal;
	
	
	public static void initializeItems(FMLPreInitializationEvent event){
		stone_rockHammer = init(ItemRockHammer.createTool(0),"stone_rockhammer",CreativeTabs.tabTools);
		iron_rockHammer = init(ItemRockHammer.createTool(1),"iron_rockhammer",CreativeTabs.tabTools);
		steel_rockHammer = init(ItemRockHammer.createTool(2),"steel_rockhammer",CreativeTabs.tabTools);
		diamond_rockHammer = init(ItemRockHammer.createTool(3),"diamond_rockhammer",CreativeTabs.tabTools);
		powderedCoal = init(new Item(),"powderedCoal",CreativeTabs.tabMaterials, "dustCoal");
		OreDictionary.registerOre("dustCharcoal",powderedCoal);
		OreDictionary.registerOre("dustCarbon",powderedCoal);
		FuelRegistry.getInstance().registerFuel(powderedCoal, 1600);
	}
	
	private static Item init(Item item, String name, CreativeTabs tab){
		item.setUnlocalizedName(PowerAdvantage.MODID +"."+ name);
		item.setCreativeTab(tab);
		GameRegistry.registerItem(item,name);
		return item;
	}
	private static Item init(Item item, String name, CreativeTabs tab, String oreDict){
		item = init(item,name,tab);
		OreDictionary.registerOre(oreDict, item);
		return item;
	}
}
