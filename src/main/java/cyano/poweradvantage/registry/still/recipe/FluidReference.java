package cyano.poweradvantage.registry.still.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
/**
 * This is a simple class used to identify fluids by either registry name or explicit object 
 * reference.
 * @author DrCyano
 *
 */
public abstract class FluidReference {

	/**
	 * Tests a fluid object to see if it qualifies as being the same as this fluid reference
	 * @param fluid A Fluid instance
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	public abstract boolean matches(Fluid fluid);

	/**
	 * Tests a fluid name to see if it qualifies as being the same as this fluid reference
	 * @param fluidName registry name of a fluid
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	public abstract boolean matches(String fluidName);

	/**
	 * Tests a fluid object to see if it qualifies as being the same as this fluid reference
	 * @param fluidStack A FluidStack instance
	 * @return Returns true if the fluid parameter is "equal" to this reference, false otherwise
	 */
	public boolean matches(FluidStack fluidStack){
		return this.matches(fluidStack.getFluid());
	}
	
	/**
	 * Gets a fluid that matches this reference
	 * @return A Fluid instance for this reference
	 */
	public abstract Fluid getFluid();

	/**
	 * Implementations must implement a hash code algorithm
	 * @return A proper hash code
	 */
	@Override
	public abstract int hashCode();
	/**
	 * Checks for equality, doing a special comparison if the other object is also a FluidReference
	 * @param other The other object
	 * @return True if the two objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
		if(this.hashCode() != other.hashCode()) return false;
		if(other instanceof FluidReference){
			return areEquivalent(this, (FluidReference)other);
		}
		return false;
	}
	
	/**
	 * Determines whether two FluidReference instances refer to the same fluid or not
	 * @param a a FluidReference instance
	 * @param b another FluidReference instance
	 * @return True if both references are functionally identical
	 */
	public static boolean areEquivalent(FluidReference a, FluidReference b){
		if(a == b) return true;
		return a.matches(b.getFluid()) && b.matches(a.getFluid());
	}
}
