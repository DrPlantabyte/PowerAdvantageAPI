package cyano.poweradvantage.registry.still.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class RegistryNameFluidReference extends FluidReference{

	
	private final String reference;
	
	/**
	 * Standard constructor
	 * @param registryName The fluid that is being referenced, by its registry name
	 */
	public RegistryNameFluidReference(String registryName){
		if(registryName == null) throw new NullPointerException("Cannot create reference to null fluid name");
		this.reference = registryName;
	}
	/**
	 * Tests a fluid object to see if it qualifies as being the same as this fluid reference
	 * @param fluid A Fluid instance
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	@Override
	public boolean matches(Fluid fluid) {
		return reference.equals(fluid.getName());
	}
	/**
	 * Tests a fluid name to see if it qualifies as being the same as this fluid reference
	 * @param fluidName registry name of a fluid
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	@Override
	public boolean matches(String fluidName) {
		return reference.equals(fluidName);
	}
	/**
	 * Gets a fluid that matches this reference
	 * @return A Fluid instance for this reference
	 */
	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid(reference);
	}
	/** a hashcode
	 * 
	 * @return a number that will be different for different fluids
	 */
	@Override
	public int hashCode() {
		return reference.hashCode();
	}

}
