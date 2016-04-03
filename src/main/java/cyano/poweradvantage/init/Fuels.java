package cyano.poweradvantage.init;

import cyano.poweradvantage.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class Fuels {

	public static final int CRUDE_OIL_FUEL_PER_FLUID_UNIT = 5;
	public static final int REFINED_OIL_FUEL_PER_FLUID_UNIT = 25;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;

		addFuel(cyano.basemetals.init.Items.carbon_powder,1600);
		ItemStack bucket = new ItemStack(cyano.basemetals.init.Items.universal_bucket);

		/*
		// Universal Bucket won't work in the fuel registry because it doesn't use item NBT tags
		ItemStack crudeOilBucket = bucket.copy();
		cyano.basemetals.init.Items.universal_bucket.fill(crudeOilBucket,
				new FluidStack(Fluids.crude_oil,cyano.basemetals.init.Items.universal_bucket.getCapacity(bucket)),true);

		ItemStack refinedOilBucket = bucket.copy();
		cyano.basemetals.init.Items.universal_bucket.fill(refinedOilBucket,
				new FluidStack(Fluids.refined_oil,cyano.basemetals.init.Items.universal_bucket.getCapacity(bucket)),true);


		addFuel(crudeOilBucket,CRUDE_OIL_FUEL_PER_FLUID_UNIT * FluidContainerRegistry.BUCKET_VOLUME);
		addFuel(refinedOilBucket,REFINED_OIL_FUEL_PER_FLUID_UNIT * FluidContainerRegistry.BUCKET_VOLUME);
		*/
		
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
