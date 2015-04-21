package cyano.poweradvantage.init;

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
	
	public static void init(){
		if(initDone) return;

		fluidConduit_water = new ConduitType("water");
		fluidConduit_lava = new ConduitType("lava");
		fluidConduitLUT.put(FluidRegistry.WATER, fluidConduit_water);
		fluidConduitLUT.put(FluidRegistry.LAVA, fluidConduit_lava);
		
		// TODO: add crude oil (and make crude oil cause slowness upon exposure)
		// TODO: add refined oil (made by distillation of crude oil)
		
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
