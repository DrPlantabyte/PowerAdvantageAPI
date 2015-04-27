package cyano.poweradvantage.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.blocks.*;
import cyano.poweradvantage.machines.*;

public abstract class Entities {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		GameRegistry.registerTileEntity(FluidDrainTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_drain");
		GameRegistry.registerTileEntity(FluidDischargeTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_discharge");
		GameRegistry.registerTileEntity(StorageTankTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_storage_tank");
		GameRegistry.registerTileEntity(TileEntityConveyor.class, PowerAdvantage.MODID+"."+"item_conveyor");
		GameRegistry.registerTileEntity(TileEntityBlockFilter.class, PowerAdvantage.MODID+"."+"item_filter_block");
		GameRegistry.registerTileEntity(TileEntityFoodFilter.class, PowerAdvantage.MODID+"."+"item_filter_food");
		GameRegistry.registerTileEntity(TileEntityFuelFilter.class, PowerAdvantage.MODID+"."+"item_filter_fuel");
		GameRegistry.registerTileEntity(TileEntityInventoryFilter.class, PowerAdvantage.MODID+"."+"item_filter_inventory");
		GameRegistry.registerTileEntity(TileEntityOreFilter.class, PowerAdvantage.MODID+"."+"item_filter_ore");
		GameRegistry.registerTileEntity(TileEntityPlantFilter.class, PowerAdvantage.MODID+"."+"item_filter_plant");
		GameRegistry.registerTileEntity(TileEntitySmeltableFilter.class, PowerAdvantage.MODID+"."+"item_filter_smelt");
		
		
		initDone = true;
	}
}
