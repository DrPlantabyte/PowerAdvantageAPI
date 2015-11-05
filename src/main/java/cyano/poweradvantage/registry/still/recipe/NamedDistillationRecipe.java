package cyano.poweradvantage.registry.still.recipe;

import java.util.Collection;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Distillation recipe that looks-up fluids based on their name.
 * @author DrCyano
 *
 */
public class NamedDistillationRecipe extends DistillationRecipe{

	private final String inputFluid;
	private final int inputAmount;
	private final String outputFluid;
	private final int outputAmount;
	private FluidStack outputCache = null;
	/**
	 * Constructs a distillation recipe by fluid registry names
	 * @param inputName Name of input fluid
	 * @param inputVolume Amount of input fluid per amount of output fluid created
	 * @param outputName Name of output fluid
	 * @param outputVolume Amount of output fluid per amount of input fluid consumed
	 */
	public NamedDistillationRecipe(String inputName, int inputVolume, String outputName, int outputVolume){
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
	 * Gets the output of this recipe
	 * @return A new fluid stack representing the amount of fluid produced each time this recipe is 
	 * applied
	 */
	@Override
	public FluidStack getOutput() {
		if(outputCache == null && FluidRegistry.isFluidRegistered(outputFluid)){
			outputCache = new FluidStack(FluidRegistry.getFluid(outputFluid),outputAmount);
		}
		return outputCache;
	}

	/**
	 * Determines whether the input is valid for applying this recipe
	 * @param input
	 * @return
	 */
	@Override
	public boolean isValidInput(FluidStack input) {
		if(input == null || input.getFluid() == null){
			return false;
		}
		return inputFluid.equals(input.getFluid().getName()) && input.amount >= this.inputAmount;
	}
	
	/**
	 * Applies a recipe to a given input, returning null if the input is not valid for this recipe.
	 * @param input The input fluid (it will have it's amount decreased)
	 * @return The output fluid produced, or null if the input is not valid
	 */
	@Override
	public FluidStack applyRecipe(FluidStack input){
		if(this.isValidInput(input)){
			input.amount -= this.inputAmount;
			return this.getOutput();
		} else {
			return null;
		}
	}

}
