package cyano.poweradvantage.registry.still.recipe;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Superclass for all fluid distillation recipes.
 * @author DrCyano
 *
 */
public abstract class DistillationRecipe {
	
	/**
	 * Gets the output from applying this recipe. 
	 * @return The output fluid per application of this recipe
	 */
	public abstract FluidStack getOutput();
	/**
	 * Checks if the given FluidStack instance can be input for this recipe.
	 * @param input Input for the recipe to test.
	 * @return Returns true if and only if this recipe should produce an output from the given 
	 * input.
	 */
	public abstract boolean isValidInput(FluidStack input);
	/**
	 * Returns a list of all registered fluids for which <code>isValidInput(...)</code> would 
	 * return true. This method is only used for displaying recipes in NEI and does not need to be 
	 * performance optimized.
	 * @return A collection of allowed inputs.
	 */
	public Collection<FluidStack> getValidInputs(){
		LinkedList<FluidStack> l = new LinkedList<>();
		for(Fluid f : FluidRegistry.getRegisteredFluids().values()){
			FluidStack stack = new FluidStack(f,FluidContainerRegistry.BUCKET_VOLUME);
			if(this.isValidInput(stack)){
				l.add(stack);
			}
		}
		return l;
	}
	
	/**
	 * Applies a recipe to a given input, returning null if the input is not valid for this recipe.
	 * @param input The input fluid (it will have it's amount decreased)
	 * @return The output fluid produced, or null if the input is not valid
	 */
	public abstract FluidStack applyRecipe(FluidStack input);
	
	
}
