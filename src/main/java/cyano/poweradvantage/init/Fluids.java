package cyano.poweradvantage.init;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.util.ReversibleHashMap;
import cyano.poweradvantage.util.ReversibleMap;

public abstract class Fluids {


	private static boolean initDone = false;

	public static final ConduitType fluidConduit_general = new ConduitType("fluid");
	private static ReversibleMap<Fluid,ConduitType> fluidConduitLUT= new ReversibleHashMap<>(); 
	public static ConduitType fluidConduit_water;
	public static ConduitType fluidConduit_lava;

	public static Fluid crude_oil;
	
	public static void init(){
		if(initDone) return;
		
		cyano.poweradvantage.init.Materials.init();

		fluidConduit_water = new ConduitType("water");
		fluidConduit_lava = new ConduitType("lava");
		fluidConduitLUT.put(FluidRegistry.WATER, fluidConduit_water);
		fluidConduitLUT.put(FluidRegistry.LAVA, fluidConduit_lava);
		
		crude_oil = new Fluid("crude_oil").setDensity(850).setViscosity(6000);
		FluidRegistry.registerFluid(crude_oil);
		// TODO: (when Forge fluids render properly) add crude oil spawning and refined oil fluid
		
		initDone = true;
	}
	
	public static void registerNewFluid(Fluid f){
		ConduitType fluidType = new ConduitType(f.getUnlocalizedName());
		fluidConduitLUT.put(f, fluidType);
	}
	
	public static boolean isFluidType(ConduitType type){
		return type.equals(fluidConduit_general) || fluidConduitLUT.containsValue(type);
	}
	
	public static ConduitType fluidToConduitType(Fluid f){
		if(fluidConduitLUT.containsKey(f)){
			return fluidConduitLUT.get(f);
		} else {
			ConduitType fluidType = new ConduitType(f.getUnlocalizedName());
			fluidConduitLUT.put(f, fluidType);
			return fluidType;
		}
	}
	
	public static Fluid conduitTypeToFluid(ConduitType t){
		if(fluidConduitLUT.containsValue(t)){
			return fluidConduitLUT.getKey(t);
		} else {
			return null;
		}
	}
}
