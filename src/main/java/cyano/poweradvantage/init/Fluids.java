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

	/**
	 * This is the conduit type reported by pipes that transport fluids. Note that fluid machines 
	 * report <code>Fluids.fluidConduit_general</code> for their type but offer specific fluids 
	 * (such as water or lava) when fluid sources transmit their fluids to fluid consumers (allowing 
	 * the consumers to generate requests based on the type of fluid on offer).
	 */
	public static final ConduitType fluidConduit_general = new ConduitType("fluid");
	private static ReversibleMap<Fluid,ConduitType> fluidConduitLUT= new ReversibleHashMap<>();
	/**
	 * This is the conduit type equivalent of water
	 */
	public static ConduitType fluidConduit_water;
	/**
	 * This is the conduit type equivalent of lava
	 */
	public static ConduitType fluidConduit_lava;

	/**
	 * Coming soon...
	 */
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
	/**
	 * Adds a fluid to the PowerAdvantage registry of fluids, generating the necessary conduit type 
	 * object to represent the fluid in a power network
	 * @param f A new fluid
	 */
	public static void registerNewFluid(Fluid f){
		ConduitType fluidType = new ConduitType(f.getUnlocalizedName());
		fluidConduitLUT.put(f, fluidType);
	}
	/**
	 * Checks if the given conduit type represents a fluid
	 * @param type A conduit type
	 * @return True if type is a fluid, false otherwise
	 */
	public static boolean isFluidType(ConduitType type){
		return type.equals(fluidConduit_general) || fluidConduitLUT.containsValue(type);
	}

	/**
	 * Converts a fluid to its conduit type representation
	 * @param f A fluid
	 * @return a conduit type associated with the fluid
	 */
	public static ConduitType fluidToConduitType(Fluid f){
		if(fluidConduitLUT.containsKey(f)){
			return fluidConduitLUT.get(f);
		} else {
			ConduitType fluidType = new ConduitType(f.getUnlocalizedName());
			fluidConduitLUT.put(f, fluidType);
			return fluidType;
		}
	}
	

	/**
	 * Converts a conduit type to the fluid that it represents
	 * @param t A conduit type
	 * @return the associated fluid, or null if t does not represent a fluid
	 */
	public static Fluid conduitTypeToFluid(ConduitType t){
		if(fluidConduitLUT.containsValue(t)){
			return fluidConduitLUT.getKey(t);
		} else {
			return null;
		}
	}
}
