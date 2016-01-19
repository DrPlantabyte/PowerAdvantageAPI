package cyano.poweradvantage.registry.still.recipe;

import net.minecraftforge.fluids.Fluid;

/**
 * Implementation of FluidReference that compares specific fluids (fluids of same name but different 
 * from different mods will be considered unequal).
 * @author DrCyano
 *
 */
public class ExplicitFluidReference extends FluidReference{

	
	private final Fluid reference;
	
	/**
	 * Standard constructor
	 * @param ref The fluid that is being referenced, by instance object
	 */
	public ExplicitFluidReference(Fluid ref){
		if(ref == null) throw new NullPointerException("Cannot create reference to null fluid");
		this.reference = ref;
	}
	/**
	 * Tests a fluid object to see if it qualifies as being the same as this fluid reference
	 * @param fluid A Fluid instance
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	@Override
	public boolean matches(Fluid fluid) {
		return reference == fluid;
	}
	/**
	 * Tests a fluid name to see if it qualifies as being the same as this fluid reference
	 * @param fluidName registry name of a fluid
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	@Override
	public boolean matches(String fluidName) {
		return reference.getName() == fluidName; // yes, I do want to check instance rather than text content
	}
	/**
	 * Gets a fluid that matches this reference
	 * @return A Fluid instance for this reference
	 */
	@Override
	public Fluid getFluid() {
		return reference;
	}
	/** a hashcode
	 * 
	 * @return a number that will be different for different fluids
	 */
	@Override
	public int hashCode() {
		return reference.getName().hashCode();
	}

}
