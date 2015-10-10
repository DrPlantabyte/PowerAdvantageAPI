package cyano.poweradvantage.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.machines.conveyors.TileEntityBlockFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityConveyor;
import cyano.poweradvantage.machines.conveyors.TileEntityFoodFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityFuelFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityInventoryFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityOreFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityOverflowFilter;
import cyano.poweradvantage.machines.conveyors.TileEntityPlantFilter;
import cyano.poweradvantage.machines.conveyors.TileEntitySmeltableFilter;
import cyano.poweradvantage.machines.creative.InfiniteEnergyTileEntity;
import cyano.poweradvantage.machines.fluidmachines.FluidDischargeTileEntity;
import cyano.poweradvantage.machines.fluidmachines.FluidDrainTileEntity;
import cyano.poweradvantage.machines.fluidmachines.MetalTankTileEntity;
import cyano.poweradvantage.machines.fluidmachines.StorageTankTileEntity;

public abstract class Entities {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		GameRegistry.registerTileEntity(FluidDrainTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_drain");
		GameRegistry.registerTileEntity(FluidDischargeTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_discharge");
		GameRegistry.registerTileEntity(StorageTankTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_storage_tank");
		GameRegistry.registerTileEntity(MetalTankTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_metal_tank");
		GameRegistry.registerTileEntity(TileEntityConveyor.class, PowerAdvantage.MODID+"."+"item_conveyor");
		GameRegistry.registerTileEntity(TileEntityBlockFilter.class, PowerAdvantage.MODID+"."+"item_filter_block");
		GameRegistry.registerTileEntity(TileEntityFoodFilter.class, PowerAdvantage.MODID+"."+"item_filter_food");
		GameRegistry.registerTileEntity(TileEntityFuelFilter.class, PowerAdvantage.MODID+"."+"item_filter_fuel");
		GameRegistry.registerTileEntity(TileEntityInventoryFilter.class, PowerAdvantage.MODID+"."+"item_filter_inventory");
		GameRegistry.registerTileEntity(TileEntityOreFilter.class, PowerAdvantage.MODID+"."+"item_filter_ore");
		GameRegistry.registerTileEntity(TileEntityPlantFilter.class, PowerAdvantage.MODID+"."+"item_filter_plant");
		GameRegistry.registerTileEntity(TileEntitySmeltableFilter.class, PowerAdvantage.MODID+"."+"item_filter_smelt");
		GameRegistry.registerTileEntity(TileEntityOverflowFilter.class, PowerAdvantage.MODID+"."+"item_filter_overflow");
		
		GameRegistry.registerTileEntity(InfiniteEnergyTileEntity.class, PowerAdvantage.MODID+"."+"infinite_energy_source");
		
		
		initDone = true;
	}
}
