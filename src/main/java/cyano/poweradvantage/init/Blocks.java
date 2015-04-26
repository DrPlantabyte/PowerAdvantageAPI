package cyano.poweradvantage.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.blocks.BlockConveyor;
import cyano.poweradvantage.blocks.BlockConveyorFilter;
import cyano.poweradvantage.blocks.TileEntityBlockFilter;
import cyano.poweradvantage.blocks.TileEntityFoodFilter;
import cyano.poweradvantage.blocks.TileEntityFuelFilter;
import cyano.poweradvantage.blocks.TileEntityInventoryFilter;
import cyano.poweradvantage.blocks.TileEntityOreFilter;
import cyano.poweradvantage.blocks.TileEntityPlantFilter;
import cyano.poweradvantage.blocks.TileEntitySmeltableFilter;
import cyano.poweradvantage.machines.CopperPipeBlock;
import cyano.poweradvantage.machines.FluidDischargeBlock;
import cyano.poweradvantage.machines.FluidDrainBlock;

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
	

	//public static BlockDynamicLiquid crude_oil_flowing;
	//public static BlockStaticLiquid crude_oil_still;
	public static BlockFluidBase crude_oil_block;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		ItemGroups.init();
		Fluids.init();
		cyano.poweradvantage.init.Materials.init();
		
		
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

//		crude_oil_still = (BlockStaticLiquid)addBlock(new CustomBlockStaticLiquid(cyano.poweradvantage.init.Materials.crude_oil).setHardness(100.0f),"crude_oil_still");
//		crude_oil_flowing = (BlockDynamicLiquid)addBlock(new CustomBlockDynamicLiquid(cyano.poweradvantage.init.Materials.crude_oil).setHardness(100.0f),"crude_oil_flowing");
		crude_oil_block = new BlockFluidClassic(Fluids.crude_oil,cyano.poweradvantage.init.Materials.crude_oil);
		crude_oil_block.setUnlocalizedName(PowerAdvantage.MODID+"."+"crude_oil");
		GameRegistry.registerBlock(crude_oil_block, "crude_oil");
		
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
