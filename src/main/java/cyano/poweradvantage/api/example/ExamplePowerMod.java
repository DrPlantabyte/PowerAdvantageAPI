package cyano.poweradvantage.api.example;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.registry.MachineGUIRegistry;

public class ExamplePowerMod {
	public void preInit(FMLPreInitializationEvent event,  Configuration config)
    {
		//
    }
	
	public void init(FMLInitializationEvent event)
    {
		
		
		int rsGen_guiID = MachineGUIRegistry.addGUI(new RedstoneGeneratorGUI());
		GUIBlock rsGen_block = new RedstoneGeneratorBlock();
		rsGen_block.setGuiID(rsGen_guiID,PowerAdvantage.getInstance());
		rsGen_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"example_redstone_generator");
		GameRegistry.registerBlock(rsGen_block,"example_redstone_generator");
		GameRegistry.registerTileEntity(RedstoneGeneratorTileEntity.class,PowerAdvantage.MODID+"."+"example_redstone_generator");
		

		int rsFurn_guiID = MachineGUIRegistry.addGUI(new RedstoneFurnaceGUI());
		GUIBlock rsFurn_block = new RedstoneFurnaceBlock();
		rsFurn_block.setGuiID(rsFurn_guiID,PowerAdvantage.getInstance());
		rsFurn_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"example_redstone_furnace");
		GameRegistry.registerBlock(rsFurn_block,"example_redstone_furnace");
		GameRegistry.registerTileEntity(RedstoneFurnaceTileEntity.class,PowerAdvantage.MODID+"."+"example_redstone_furnace");
		
		Block rsCon_block = new RedstonePowerConductorBlock();
		rsCon_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"example_redstone_conductor");
		GameRegistry.registerBlock(rsCon_block,"example_redstone_conductor");
		
		
		if(event.getSide() == Side.CLIENT){
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsGen_block),"example_redstone_generator");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsCon_block),"example_redstone_conductor");
			registerItemRender(net.minecraft.item.Item.getItemFromBlock(rsFurn_block),"example_redstone_furnace");
		}
		
		
    }
	
	private void registerItemRender(Item i, String itemName){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0, new ModelResourceLocation(PowerAdvantage.MODID+":"+itemName, "inventory"));
	}
	
	public void postInit(FMLPostInitializationEvent event)
    {
		//
    }
}
