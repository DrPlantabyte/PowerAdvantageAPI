package cyano.poweradvantage.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.modsupport.rf.TileEntityRFSteamConverter;
import cyano.poweradvantage.machines.conveyors.*;
import cyano.poweradvantage.machines.creative.InfiniteEnergyTileEntity;
import cyano.poweradvantage.machines.fluidmachines.*;
import cyano.poweradvantage.machines.fluidmachines.modsupport.TerminalFluidPipeTileEntity;

public abstract class Entities {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		GameRegistry.registerTileEntity(TerminalFluidPipeTileEntity.class,PowerAdvantage.MODID+".tileentity."+"pipe_terminal");
		GameRegistry.registerTileEntity(FluidDrainTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_drain");
		GameRegistry.registerTileEntity(FluidDischargeTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_discharge");
		GameRegistry.registerTileEntity(StorageTankTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_storage_tank");
		GameRegistry.registerTileEntity(MetalTankTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_metal_tank");
		GameRegistry.registerTileEntity(StillTileEntity.class,PowerAdvantage.MODID+".tileentity."+"still");
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
