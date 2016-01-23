package cyano.poweradvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import cyano.poweradvantage.registry.FuelRegistry;

public abstract class Fuels {

	public static final int CRUDE_OIL_FUEL_PER_FLUID_UNIT = 5;
	public static final int REFINED_OIL_FUEL_PER_FLUID_UNIT = 25;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;

		addFuel(cyano.basemetals.init.Items.carbon_powder,1600);
		addFuel(cyano.poweradvantage.init.Items.bucket_crude_oil,CRUDE_OIL_FUEL_PER_FLUID_UNIT * FluidContainerRegistry.BUCKET_VOLUME);
		addFuel(cyano.poweradvantage.init.Items.bucket_refined_oil,REFINED_OIL_FUEL_PER_FLUID_UNIT * FluidContainerRegistry.BUCKET_VOLUME);
		
		initDone = true;
	}
	
	private static void addFuel(Item fuelItem, Number fuelValue){
		FuelRegistry.getInstance().registerFuel(fuelItem, fuelValue);
	}
	
	private static void addFuel(Block fuelItem, Number fuelValue){
		FuelRegistry.getInstance().registerFuel(fuelItem, fuelValue);
	}
	
	private static void addFuel(ItemStack fuelItem, Number fuelValue){
		FuelRegistry.getInstance().registerFuel(fuelItem, fuelValue);
	}
}
