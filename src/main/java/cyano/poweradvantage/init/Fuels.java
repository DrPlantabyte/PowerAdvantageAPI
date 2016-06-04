package cyano.poweradvantage.init;

import cyano.poweradvantage.registry.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;



public abstract class Fuels {

	public static final int CRUDE_OIL_FUEL_PER_FLUID_UNIT = 5;
	public static final int REFINED_OIL_FUEL_PER_FLUID_UNIT = 25;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;

		FuelRegistry.getInstance().registerFuel(cyano.basemetals.init.Items.carbon_powder,(short)1600);
		ItemStack bucket = new ItemStack(ForgeModContainer.getInstance().universalBucket);


		FuelRegistry.getInstance().registerFuel(ForgeModContainer.getInstance().universalBucket,(ItemStack ub)->{
			if(ub.getItem() instanceof UniversalBucket){
				UniversalBucket ubItem = (UniversalBucket) ub.getItem();
				FluidStack fs = ubItem.getFluid(ub);
				if (fs != null && fs.amount > 0){
					if(fs.getFluid() == Fluids.crude_oil) return (short)(fs.amount * CRUDE_OIL_FUEL_PER_FLUID_UNIT);
					if(fs.getFluid() == Fluids.refined_oil) return (short)(fs.amount * REFINED_OIL_FUEL_PER_FLUID_UNIT);
				}
			}
			return (short)0;
		});

		FuelRegistry.getInstance().registerPostBurnItem(ForgeModContainer.getInstance().universalBucket,(ItemStack sb)->new ItemStack(net.minecraft.init.Items.BUCKET));

		
		initDone = true;
	}

}
