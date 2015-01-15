package cyano.poweradvantage.api.example;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.ClientProxy;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.Proxy;
import cyano.poweradvantage.api.example.*;
import cyano.poweradvantage.api.example.scrap.BlockSimpleConductor;
import cyano.poweradvantage.api.example.scrap.BlockSimpleGenerator;
import cyano.poweradvantage.api.example.scrap.BlockSimplePoweredFurnace;
import cyano.poweradvantage.api.example.scrap.SimplePowerConductorEntity;
import cyano.poweradvantage.api.example.scrap.SimplePowerSinkEntity;
import cyano.poweradvantage.api.example.scrap.SimplePowerSourceEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import cyano.poweradvantage.registry.MachineGUIRegistry;

public class ExamplePowerMod {
	public void preInit(FMLPreInitializationEvent event, Proxy proxy, Configuration config)
    {
		//
    }
	
	public void init(FMLInitializationEvent event, Proxy proxy)
    {
		
		
		int rsGen_guiID = MachineGUIRegistry.addGUI(new RedstoneGeneratorGUI());
		Block rsGen_block = new RedstoneGeneratorBlock(rsGen_guiID,PowerAdvantage.getInstance());
		rsGen_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"example_redstone_generator");
		GameRegistry.registerBlock(rsGen_block,"example_redstone_generator");
		GameRegistry.registerTileEntity(RedstoneGeneratorTileEntity.class,PowerAdvantage.MODID+"."+"example_redstone_generator");
		

		int rsFurn_guiID = MachineGUIRegistry.addGUI(new RedstoneGeneratorGUI());
		Block rsFurn_block = new RedstoneFurnaceBlock(rsFurn_guiID,PowerAdvantage.getInstance());
		rsFurn_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"example_redstone_furnace");
		GameRegistry.registerBlock(rsFurn_block,"example_redstone_furnace");
		GameRegistry.registerTileEntity(RedstoneFurnaceTileEntity.class,PowerAdvantage.MODID+"."+"example_redstone_furnace");
		
		Block rsCon_block = new RedstonePowerConductorBlock();
		rsCon_block.setUnlocalizedName("example_redstone_conductor");
		GameRegistry.registerBlock(rsCon_block,"example_redstone_conductor");
		GameRegistry.registerTileEntity(RedstonePowerConductorTileEntity.class,PowerAdvantage.MODID+"."+"example_redstone_conductor");
		
		
		if(proxy instanceof ClientProxy){
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsGen_block),"example_redstone_generator");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsFurn_block),"example_redstone_conductor");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsCon_block),"example_redstone_furnace");
		}
		
		
		int id_gen = MachineGUIRegistry.addGUI(new SimpleMachineGUI(new ResourceLocation(
				PowerAdvantage.MODID+":textures/gui/container/simplegenerator.png"),new Integer2D[] {new Integer2D(56,53)}));
		int id_fur = MachineGUIRegistry.addGUI(new SimpleMachineGUI(new ResourceLocation(
				PowerAdvantage.MODID+":textures/gui/container/simplefurnace.png"),new Integer2D[] 
						{new Integer2D(56,17),new Integer2D(116,35)}));
		

		//
		Block cable = new BlockSimpleConductor();
		cable.setUnlocalizedName(PowerAdvantage.MODID+"."+"powercable");
		GameRegistry.registerBlock(cable,"powercable");
		GameRegistry.registerTileEntity(SimplePowerConductorEntity.class,PowerAdvantage.MODID+"."+"SimplePowerConductorEntity");

		Block poweredFurnaceUnlit = new BlockSimplePoweredFurnace(false,id_fur);
		poweredFurnaceUnlit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powerfurnace");
		GameRegistry.registerBlock(poweredFurnaceUnlit,"powerfurnace");
		Block poweredFurnaceLit = new BlockSimplePoweredFurnace(true,id_fur);
		poweredFurnaceLit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powerfurnace_lit");
		GameRegistry.registerBlock(poweredFurnaceLit,"powerfurnace_lit");
		GameRegistry.registerTileEntity(SimplePowerSourceEntity.class,PowerAdvantage.MODID+"."+"SimplePowerSourceEntity");
		
		
		
		Block powergen = new BlockSimpleGenerator(false,id_gen);
		powergen.setUnlocalizedName(PowerAdvantage.MODID+"."+"powergenerator");
		GameRegistry.registerBlock(powergen,"powergenerator");
		Block powergenLit = new BlockSimpleGenerator(true,id_gen);
		powergenLit.setUnlocalizedName(PowerAdvantage.MODID+"."+"powergenerator_lit");
		GameRegistry.registerBlock(powergenLit,"powergenerator_lit");
		GameRegistry.registerTileEntity(SimplePowerSinkEntity.class,PowerAdvantage.MODID+"."+"SimplePowerSinkEntity");
		
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