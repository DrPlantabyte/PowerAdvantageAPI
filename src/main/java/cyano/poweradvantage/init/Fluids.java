package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.fluid.ColoredFluid;
import cyano.poweradvantage.util.ReversibleHashMap;
import cyano.poweradvantage.util.ReversibleMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;

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
	 * black gold
	 */
	public static Fluid crude_oil;
	/**
	 * general purpose (fuel) oil
	 */
	public static Fluid refined_oil;
	
	public static void init(){
		if(initDone) return;
		
		cyano.poweradvantage.init.Materials.init();

		fluidConduit_water = new ConduitType("water");
		fluidConduit_lava = new ConduitType("lava");
		fluidConduitLUT.put(FluidRegistry.WATER, fluidConduit_water);
		fluidConduitLUT.put(FluidRegistry.LAVA, fluidConduit_lava);

		crude_oil = newFluid(PowerAdvantage.MODID,"crude_oil",850,6000,300,0,    0xFFFFFFFF);//0xFF100808);
		FluidRegistry.registerFluid(crude_oil);
		refined_oil = newFluid(PowerAdvantage.MODID,"refined_oil",720,1000,300,0,0xFFFFFFFF);//0xFFfef08e);
		FluidRegistry.registerFluid(refined_oil);
		
		initDone = true;
	}
	

	
	
	/**
	 * Creates a new fluid and registers it with the game.
	 * @param modID The ID of the mod that owns this fluid
	 * @param name The game registry name of the fluid
	 * @param density The density of the fluid (water is 1000)
	 * @param viscosity The viscosity of the fluid (water is 1000)
	 * @param temperature Temperature of the fluid, in Kelvin (default is 300)
	 * @param luminosity Glow factor for the fluid
	 * @param tintColor ARGB pixel value describing the color tint to apply to the fluid (use 
	 * 0xFFFFFFFF if the texture is already colored)
	 * @return A new fluid instance.
	 */
	private static Fluid newFluid(String modID, String name, int density, int viscosity, int temperature, int luminosity, int tintColor) {
		if(PowerAdvantage.useOtherFluids && FluidRegistry.isFluidRegistered(name)){
			// fluid already exists. Use that one.
			FMLLog.warning("Using fluid %s from %s instead of creating a new fluid %s:%s",FluidRegistry.getFluid(name).getName(),
					FluidRegistry.getFluid(name).getFlowing().getResourceDomain(), modID,name);
			return FluidRegistry.getFluid(name);
		}
		Fluid f = new ColoredFluid(name,new ResourceLocation(modID+":blocks/"+name+"_still"),new ResourceLocation(modID+":blocks/"+name+"_flow"),tintColor);
		f.setDensity(density);
		f.setViscosity(viscosity);
		f.setTemperature(temperature);
		f.setLuminosity(luminosity);
		f.setUnlocalizedName(modID+"."+name);
		FluidRegistry.registerFluid(f);
		registerNewFluid(f);
		FluidRegistry.addBucketForFluid(f);
		return f;
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
