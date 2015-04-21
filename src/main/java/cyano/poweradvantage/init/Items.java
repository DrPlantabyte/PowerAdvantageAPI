package cyano.poweradvantage.init;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cyano.basemetals.BaseMetals;
import cyano.poweradvantage.PowerAdvantage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class Items {

	public static Item starch;
	public static Item bioplastic_ingot;
	public static final Map<String,Item> allItems = new HashMap<>();
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Blocks.init();
		Fluids.init();

		starch = addItem("starch", new Item().setCreativeTab(CreativeTabs.tabMaterials),"starch");
		bioplastic_ingot = addItem("bioplastic_ingot", new Item().setCreativeTab(CreativeTabs.tabMaterials),"plastic","ingotPlastic");
		
		//OreDictionary.initVanillaEntries();
		initDone = true;
	}

	private static Item addItem(String unlocalizedName, Item i,String... oreDictNames){
		Item n = addItem(unlocalizedName,i);
		for(String oreDictName : oreDictNames){
			OreDictionary.registerOre(oreDictName, n);
		}
		return n;
	}
	private static Item addItem(String unlocalizedName, Item i){
		i.setUnlocalizedName(PowerAdvantage.MODID+"."+unlocalizedName);
		GameRegistry.registerItem(i, unlocalizedName);
		allItems.put(unlocalizedName, i);
		return i;
	}
	
	public static Item getItemByName(String unlocalizedName){
		return allItems.get(unlocalizedName);
	}
	
	public static Set<Map.Entry<String, Item>> getAllRegisteredItems(){
		return allItems.entrySet();
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event) {
		for(Map.Entry<String, Item> e :  getAllRegisteredItems()){
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(e.getValue(), 0, 
					new ModelResourceLocation(PowerAdvantage.MODID+":"+e.getKey(), "inventory"));
		}
	}
	
}
