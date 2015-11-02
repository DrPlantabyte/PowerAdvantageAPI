package cyano.poweradvantage.registry.still.recipe;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Superclass for all fluid distillation recipes.
 * @author DrCyano
 *
 */
public abstract class DistillationRecipe {
	
	/**
	 * Gets the output from applying this recipe. Note that if the recipe output is fluid-only then 
	 * the ItemStack variable will be null (and vice-versa)
	 * @return An instance of the result of this recipe. Note that if the recipe input is fluid-only 
	 * then the ItemStack variable will be null (and vice-versa). Also note that some 
	 * implementations will return null instead of a RecipeData instance to indicate that this 
	 * recipe is unusable for some reason
	 */
	public abstract RecipeData getOutput();
	/**
	 * Checks if the given ItemStack instance is the input for this recipe.
	 * @param input Input for the recipe to test. Note that if the recipe input is fluid-only then 
	 * the ItemStack variable will be null (and vice-versa)
	 * @return Returns true if and only if this recipe should produce an output from the given 
	 * inputs.
	 */
	public abstract boolean isValidInput(RecipeData input);
	/**
	 * Returns a list of all registered blocks/items for which <code>isValidInput(...)</code> would 
	 * return true. This method is only used for displaying recipes in NEI and does not need to be 
	 * performance optimized.
	 * @return A list of allowed inputs.
	 */
	public abstract Collection<ItemStack> getValidInputs();
	
	/**
	 * Applies a recipe to a given input, returning null if the input is not valid for this recipe.
	 * @param input
	 * @return
	 */
	public RecipeData applyRecipe(RecipeData input){
		if(this.isValidInput(input)){
			return this.getOutput();
		} else {
			return null;
		}
	}
	
	/**
	 * Utility data structure
	 * @author DrCyano
	 *
	 */
	public static final class RecipeData{
		public final FluidStack fluid;
		public final ItemStack item;
		
		public RecipeData(FluidStack fluid, ItemStack item){
			this.fluid = fluid;
			this.item = item;
		}
		
		public int hashCode(){
			int h = 57;
			if(fluid != null && fluid.getFluid() != null){
				h *= fluid.getFluid().getName().hashCode();
			}
			if(item != null && item.getItem() != null){
				h *= item.getUnlocalizedName().hashCode();
			}
			return h;
		}
		
		public boolean equals(Object other){
			if(this == other)return true;
			if(other instanceof RecipeData){
				RecipeData od = (RecipeData) other;
				if((this.fluid == null && od.fluid != null)||(od.fluid == null && this.fluid != null)) return false;
				if((this.item == null && od.item != null)||(od.item == null && this.item != null)) return false;
				if(this.fluid != null && fluid.getFluid() != null && this.fluid.getFluid().equals(od.fluid.getFluid()) == false) return false;
				if(this.item != null && item.getItem() != null && this.item.getItem().equals(od.item.getItem()) == false) return false;
				return true;
			} else {
				return false;
			}
		}
	}

}
