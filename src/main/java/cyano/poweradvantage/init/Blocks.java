package cyano.poweradvantage.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.fluid.InteractiveFluidBlock;
import cyano.poweradvantage.blocks.BlockFrame;
import cyano.poweradvantage.machines.conveyors.BlockConveyor;
import cyano.poweradvantage.machines.conveyors.BlockConveyorFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityBlockFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityFoodFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityFuelFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityInventoryFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityOreFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityOverflowFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityPlantFilter;
import cyano.poweradvantage.machines.conveyors.TileEntitySmeltableFilter;
import cyano.poweradvantage.machines.creative.InfiniteEnergyBlock;
import cyano.poweradvantage.machines.fluidmachines.FluidDischargeBlock;
import cyano.poweradvantage.machines.fluidmachines.FluidDrainBlock;
import cyano.poweradvantage.machines.fluidmachines.FluidPipeBlock;
import cyano.poweradvantage.machines.fluidmachines.MetalTankBlock;
import cyano.poweradvantage.machines.fluidmachines.StorageTankBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();

	public static GUIBlock fluid_drain; 
	public static GUIBlock fluid_discharge; 
	public static GUIBlock storage_tank;
	public static GUIBlock metal_storage_tank;
	public static Block fluid_pipe;
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
	/* Hope is not lost yet for fluids:
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=14 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=15 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=10 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=11 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=12 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=13 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#inventory not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=9 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=8 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=7 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=6 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=5 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=4 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=3 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=2 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=1 not found
[20:18:57] [Client thread/ERROR] [FML/]: Model definition for location poweradvantage:crude_oil#level=0 not found 
	 */
	
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
		fluid_pipe = addBlock(new FluidPipeBlock(),"fluid_pipe");
		OreDictionary.registerOre("pipe", fluid_pipe);
		steel_frame = addBlock(new BlockFrame(net.minecraft.block.material.Material.piston)
				.setResistance(cyano.basemetals.init.Materials.steel.getBlastResistance())
				.setHardness(0.75f)
				.setStepSound(Block.soundTypeMetal),"steel_frame");
		OreDictionary.registerOre("frameSteel", steel_frame);
		
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
		item_filter_overflow = (GUIBlock)addBlock(new BlockConveyorFilter(defaultMachineMaterial,defaultMachineHardness,TileEntityOverflowFilter.class),"item_filter_overflow");
		

		infinite_steam = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("steam")),"infinite_steam");
		infinite_electricity = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("electricity")),"infinite_electricity");
		infinite_quantum = (GUIBlock)addBlock(new InfiniteEnergyBlock(new ConduitType("quantum")),"infinite_quantum");

		crude_oil_block = (BlockFluidBase)addBlock(new InteractiveFluidBlock(Fluids.crude_oil,true,(World w, EntityLivingBase e)->{
			e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,200,2));
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
				ModelBakery.addVariantName(item);
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
		block.setCreativeTab(ItemGroups.tab_powerAdvantage);
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
