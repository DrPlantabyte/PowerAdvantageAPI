package cyano.poweradvantage.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.blocks.*;
import cyano.poweradvantage.machines.*;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static GUIBlock fluid_drain; 
	public static GUIBlock fluid_discharge; 
	public static Block copper_pipe;
	public static GUIBlock item_conveyor;
	public static GUIBlock item_filter_block;
	public static GUIBlock item_filter_food;
	public static GUIBlock item_filter_fuel;
	public static GUIBlock item_filter_inventory;
	public static GUIBlock item_filter_ore;
	public static GUIBlock item_filter_plant;
	public static GUIBlock item_filter_smelt;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		ItemGroups.init();
		Fluids.init();
		
		
		fluid_drain = (GUIBlock)addBlock(new FluidDrainBlock(),"fluid_drain");
		fluid_discharge = (GUIBlock)addBlock(new FluidDischargeBlock(),"fluid_discharge");
		copper_pipe = addBlock(new CopperPipeBlock(),"copper_pipe");
		
		final float defaultMachineHardness = 0.75f;
		final Material defaultMachineMaterial = Material.piston;
		item_conveyor = (GUIBlock)addBlock(new BlockConveyor(defaultMachineMaterial,defaultMachineHardness),"item_conveyor");
		item_filter_block = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityBlockFilter.class),"item_filter_block");
		item_filter_food = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityFoodFilter.class),"item_filter_food");
		item_filter_fuel = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityFuelFilter.class),"item_filter_fuel");
		item_filter_inventory = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityInventoryFilter.class),"item_filter_inventory");
		item_filter_ore = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityOreFilter.class),"item_filter_ore");
		item_filter_plant = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityPlantFilter.class),"item_filter_plant");
		item_filter_smelt = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntitySmeltableFilter.class),"item_filter_smelt");

		initDone = true;
	}
	

	private static Block addBlock(Block block, String name ){
		block.setUnlocalizedName(PowerAdvantage.MODID+"."+name);
		GameRegistry.registerBlock(block, name);
		block.setCreativeTab(ItemGroups.tab_powerAdvantage);
		allBlocks.put(name, block);
		return block;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event){
		for(Map.Entry<String, Block> e : allBlocks.entrySet()){
			String name = e.getKey();
			Block block = e.getValue();
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(net.minecraft.item.Item.getItemFromBlock(block), 0, 
				new ModelResourceLocation(PowerAdvantage.MODID+":"+name, "inventory"));
		}
	}

	
}
