package cyano.poweradvantage;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.api.example.*;

public class Tester {
	public void preInit(FMLPreInitializationEvent event, Proxy proxy, Configuration config)
    {
		//
    }
	
	public void init(FMLInitializationEvent event, Proxy proxy)
    {
		//
		Block cable = new BlockSimpleConductor();
		cable.setUnlocalizedName(PowerAdvantage.MODID+"."+"powercable");
		GameRegistry.registerBlock(cable,"powercable");

		Block poweredFurnaceUnlit = new BlockSimplePoweredFurnace(false);
		poweredFurnaceUnlit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powerfurnace");
		GameRegistry.registerBlock(poweredFurnaceUnlit,"powerfurnace");
		Block poweredFurnaceLit = new BlockSimplePoweredFurnace(true);
		poweredFurnaceLit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powerfurnace_lit");
		GameRegistry.registerBlock(poweredFurnaceLit,"powerfurnace_lit");
		

		Block powergen = new BlockSimpleGenerator(false);
		powergen.setUnlocalizedName(PowerAdvantage.MODID+"."+"powergenerator");
		GameRegistry.registerBlock(powergen,"powergenerator");
		Block powergenLit = new BlockSimpleGenerator(true);
		powergenLit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powergenerator_lit");
		GameRegistry.registerBlock(powergenLit,"powergenerator_lit");
		
		if(proxy instanceof ClientProxy){
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(cable),"powercable");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(poweredFurnaceUnlit),"powerfurnace");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(poweredFurnaceLit),"powerfurnace_lit");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(powergen),"powergenerator");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(powergenLit),"powergenerator_lit");
		}
    }
	
	private void registerItemRender(Item i, String itemName){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0, new ModelResourceLocation(PowerAdvantage.MODID+":"+itemName, "inventory"));
	}
	
	public void postInit(FMLPostInitializationEvent event, Proxy proxy)
    {
		//
    }
}
