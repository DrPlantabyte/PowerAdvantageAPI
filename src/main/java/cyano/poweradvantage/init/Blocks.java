package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.fluid.InteractiveFluidBlock;
import cyano.poweradvantage.blocks.BlockFrame;
import cyano.poweradvantage.blocks.BlockPowerSwitch;
import cyano.poweradvantage.machines.conveyors.*;
import cyano.poweradvantage.machines.creative.InfiniteEnergyBlock;
import cyano.poweradvantage.machines.fluidmachines.*;
import cyano.poweradvantage.machines.fluidmachines.modsupport.TerminalFluidPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static GUIBlock fluid_drain; 
	public static GUIBlock fluid_discharge; 
	public static GUIBlock storage_tank;
	public static GUIBlock metal_storage_tank;
	public static GUIBlock still;
	public static Block fluid_pipe;
	public static Block fluid_pipe_terminal;
	public static Block fluid_switch;
	public static GUIBlock item_conveyor;
	public static GUIBlock item_filter_block;
	public static GUIBlock item_filter_food;
	public static GUIBlock item_filter_fuel;
	public static GUIBlock item_filter_inventory;
	public static GUIBlock item_filter_ore;
	public static GUIBlock item_filter_plant;
	public static GUIBlock item_filter_smelt;
	public static GUIBlock item_filter_overflow;
	public static Block steel_frame;
	

	public static GUIBlock infinite_steam;
	public static GUIBlock infinite_electricity;
	public static GUIBlock infinite_quantum;
	

	public static BlockFluidBase crude_oil_block;
	public static BlockFluidBase refined_oil_block;
	
	

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		ItemGroups.init();
		Fluids.init();
		cyano.poweradvantage.init.Materials.init();
		
		
		fluid_drain = (GUIBlock)addBlock(new FluidDrainBlock(),"fluid_drain");
		fluid_discharge = (GUIBlock)addBlock(new FluidDischargeBlock(),"fluid_discharge");
		storage_tank = (GUIBlock)addBlock(new StorageTankBlock(),"fluid_storage_tank");
		metal_storage_tank = (GUIBlock)addBlock(new MetalTankBlock(),"fluid_metal_tank");
		still = (GUIBlock)addBlock(new StillBlock(),"still");
		fluid_pipe = addBlock(new FluidPipeBlock(),"fluid_pipe");
		fluid_pipe_terminal = addBlock(new TerminalFluidPipeBlock(),"fluid_pipe_terminal");
		fluid_pipe_terminal.setUnlocalizedName("fluid_pipe");
		fluid_pipe_terminal.setCreativeTab(null);
		fluid_switch = addBlock(new BlockPowerSwitch(Fluids.fluidConduit_general),"fluid_switch");
		OreDictionary.registerOre("pipe", fluid_pipe);
		steel_frame = addBlock(new BlockFrame(net.minecraft.block.material.Material.PISTON)
				.setResistance(cyano.basemetals.init.Materials.steel.getBlastResistance())
				.setHardness(0.75f),"steel_frame");
		OreDictionary.registerOre("frameSteel", steel_frame);
		
		final float defaultMachineHardness = 0.75f;
		final Material defaultMachineMaterial = Material.PISTON;
		item_conveyor = (GUIBlock)addBlock(new BlockConveyor(defaultMachineMaterial,defaultMachineHardness),"item_conveyor");
		item_filter_block = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityBlockFilter.class),"item_filter_block");
		item_filter_food = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityFoodFilter.class),"item_filter_food");
		item_filter_fuel = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityFuelFilter.class),"item_filter_fuel");
		item_filter_inventory = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityInventoryFilter.class),"item_filter_inventory");
		item_filter_ore = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityOreFilter.class),"item_filter_ore");
		item_filter_plant = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityPlantFilter.class),"item_filter_plant");
		item_filter_smelt = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntitySmeltableFilter.class),"item_filter_smelt");
		item_filter_overflow = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityOverflowFilter.class),"item_filter_overflow");
		

		infinite_steam = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("steam")),"infinite_steam");
		infinite_electricity = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("electricity")),"infinite_electricity");
		infinite_quantum = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("quantum")),"infinite_quantum");

		
		refined_oil_block = (BlockFluidBase)addBlock(new InteractiveFluidBlock(Fluids.refined_oil,true,(World w, EntityLivingBase e)->{
			e.addPotionEffect(new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("nausea")),200));
		}),"refined_oil");
		
		crude_oil_block = (BlockFluidBase)addBlock(new InteractiveFluidBlock(Fluids.crude_oil,true,(World w, EntityLivingBase e)->{
			e.addPotionEffect(new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("slowness")),200,2));
		}),"crude_oil");
		
		
		initDone = true;
	}

	/**
	 * Adds fluid block models to the game and applies them to the fluid blocks
	 */
	@SideOnly(Side.CLIENT)
	public static void bakeModels(){
		String modID = PowerAdvantage.MODID;
		for(Map.Entry<String,Block> e : allBlocks.entrySet()){
			Block b = e.getValue();
			String name = e.getKey();
			if(b instanceof BlockFluidBase){
				BlockFluidBase block = (BlockFluidBase)b;
				Fluid fluid = block.getFluid();
				Item item = Item.getItemFromBlock(block);
				final ModelResourceLocation fluidModelLocation = new ModelResourceLocation(
						modID.toLowerCase() + ":" + name, "fluid");
				ModelBakery.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
				{
					public ModelResourceLocation getModelLocation(ItemStack stack)
					{
						return fluidModelLocation;
					}
				});
				ModelLoader.setCustomStateMapper(block, new StateMapperBase()
				{
					protected ModelResourceLocation getModelResourceLocation(IBlockState state)
					{
						return fluidModelLocation;
					}
				});
			}
		}
	}

	private static Block addBlock(Block block, String name ){
		block.setUnlocalizedName(PowerAdvantage.MODID+"."+name);
		GameRegistry.registerBlock(block, name);
		if((block instanceof BlockFluidBase) == false)block.setCreativeTab(ItemGroups.tab_powerAdvantage);
		allBlocks.put(name, block);
		return block;
	}
	
	public static Map<String,Block> getModBlockRegistry(){
		return Collections.unmodifiableMap(allBlocks);
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
