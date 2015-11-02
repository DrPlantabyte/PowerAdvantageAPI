package cyano.poweradvantage.registry.still.recipe;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryDistillationRecipe extends DistillationRecipe{

	private final String fluidInputName;
	private final int inputFluidAmount;
	private final String itemInputName;
	private final String fluidOutputName;
	private final int outputFluidAmount;
	private final String itemOutputName;
	private final int outputItemstackQuantity;
	/**
	 * Full constructor
	 * @param inputFluid name of input fluid (can be null if no fluid is used for input)
	 * @param inputAmount minimum amount of input fluid to apply the recipe
	 * @param inputItem Input item, by OreDictionary name (can be null if no item is used for input)
	 * @param outputFluid name of output fluid (can be null if no fluid is produced)
	 * @param outputAmount amount of output fluid produced
	 * @param outputItem Output item by OreDictionary name  (can be null if no item is produced)
	 * @param outputItemStacksize stack-size out output item
	 */
	public OreDictionaryDistillationRecipe(String inputFluid, int inputAmount, String inputItem,
			String outputFluid, int outputAmount, String outputItem, int outputItemStacksize){
		this.fluidInputName = inputFluid;
		this.inputFluidAmount = inputAmount;
		this.itemInputName = inputItem;
		this.fluidOutputName = outputFluid;
		this.outputFluidAmount = outputAmount;
		this.itemOutputName = outputItem;
		this.outputItemstackQuantity = outputItemStacksize;
	}
	
	/**
	 * Gets the output from applying this recipe. Note that if the recipe output is fluid-only then 
	 * the ItemStack variable will be null (and vice-versa)
	 * @return An ItemStack instance of the result of this recipe, or null if this recipe does not 
	 * produce an item.
	 */
	@Override
	public RecipeData getOutput() {
		ItemStack outItem = null;
		FluidStack outFluid = null;
		boolean noRecipe = true;
		if(fluidOutputName != null && outputFluidAmount > 0){
			Fluid f = FluidRegistry.getFluid(fluidOutputName);
			if(f != null){
				outFluid = new FluidStack(f,outputFluidAmount);
				noRecipe = false;
			}
		}
		if(itemOutputName != null && outputItemstackQuantity > 0){
			java.util.List<ItemStack> items = OreDictionary.getOres(itemOutputName);
			if(!items.isEmpty()){
				outItem = new ItemStack(items.get(0).getItem(),outputItemstackQuantity, items.get(0).getMetadata());
				noRecipe = false;
			}
		}
		if(noRecipe){
			// no output from recipe (means that no appropriate items/fluids have been added to the game)
			return null;
		} else {
			return new RecipeData(outFluid,outItem);
		}
	}

	@Override
	public boolean isValidInput(RecipeData input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<ItemStack> getValidInputs() {
		// TODO Auto-generated method stub
		return null;
	}

}
