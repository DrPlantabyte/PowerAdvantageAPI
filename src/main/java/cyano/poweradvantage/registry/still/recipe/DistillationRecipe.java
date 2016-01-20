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
public class DistillationRecipe {
	

	private final FluidReference inputFluid;
	private final int inputAmount;
	private final FluidReference outputFluid;
	private final int outputAmount;
	private FluidStack outputCache = null;
	
	/**
	 * Constructs a distillation recipe by fluid registry names
	 * @param inputName Input fluid reference
	 * @param inputVolume Amount of input fluid per amount of output fluid created (e.g. 2 in a 2:1 
	 * conversion ratio)
	 * @param outputName Output fluid reference
	 * @param outputVolume Amount of output fluid per amount of input fluid consumed (e.g. 1 in a 2:1 
	 * conversion ratio)
	 */
	public DistillationRecipe(FluidReference inputName, int inputVolume, FluidReference outputName, int outputVolume){
		// sanity checks
		if(inputName == null || inputVolume <= 0 || outputName == null || outputVolume <= 0){
			throw new IllegalArgumentException(String.format("Constructor %s(%s,%s,%s,%s) contains invalid input parameters", 
					this.getClass().getName(),inputName,inputVolume,outputName,outputVolume));
		}
		// init
		this.inputFluid = inputName;
		this.inputAmount = inputVolume;
		this.outputFluid = outputName;
		this.outputAmount = outputVolume;
	}
	
	/**
	 * Gets the output from applying this recipe. 
	 * @return The output fluid per application of this recipe
	 */
	public FluidStack getOutput(){
		if(outputCache == null ){
			outputCache = new FluidStack(outputFluid.getFluid(),outputAmount);
		}
		return outputCache.copy();
	}
	/**
	 * Checks if the given FluidStack instance can be input for this recipe.
	 * @param input Input for the recipe to test.
	 * @return Returns true if and only if this recipe should produce an output from the given 
	 * input.
	 */
	public boolean isValidInput(FluidStack input){
		if(input == null ){
			return false;
		}
		return isValidInput(input.getFluid()) && input.amount >= this.inputAmount;
	}
	/**
	 * Checks if the given Fluid instance can be input for this recipe.
	 * @param input Input fluid for the recipe to test.
	 * @return Returns true if and only if this recipe should produce an output from the given 
	 * input.
	 */
	public boolean isValidInput(Fluid input){
		if(input == null ){
			return false;
		}
		return inputFluid.matches(input);
	}
	/**
	 * This method is used to check if you can add to an already existing fluid stack
	 * @param output A fluid stack that you may or may not consider equivalent to the output of this 
	 * recipe
	 * @return True if it is appropriate to add the output of this recipe to the given fluid stack, 
	 * false if the given fluid stack is different from the output of this recipe.
	 */
	public boolean isValidOutput(FluidStack output){
		return outputFluid.matches(output);
	}

	/**
	 * This method is used to check if you can add to an already existing fluid stack
	 * @param output A fluid that you may or may not consider equivalent to the output of this 
	 * recipe
	 * @return True if it is appropriate to add the output of this recipe to the given fluid, 
	 * false if the given fluid is different from the output of this recipe.
	 */
	public boolean isValidOutput(Fluid output){
		return outputFluid.matches(output);
	}
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
	 * @param numberApplications Number of times to apply the recipe
	 * @return The output fluid produced, or null if the input is not valid
	 */
	public FluidStack applyRecipe(FluidStack input, int numberApplications){
		if(this.isValidInput(input)){
			int n = Math.min(numberApplications, input.amount / this.inputAmount);
			input.amount -= (this.inputAmount * n);
			FluidStack out =  this.getOutput();
			out.amount *= n;
			return out;
		} else {
			return null;
		}
	}
	
	
}
