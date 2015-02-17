package cyano.poweradvantage.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cyano.poweradvantage.registry.recipe.ExpandedFurnaceRecipe;
import cyano.poweradvantage.registry.recipe.ShapedFurnaceRecipe;
import cyano.poweradvantage.registry.recipe.ShapelessFurnaceRecipe;

// TODO documentation
public class ExpandedFurnaceRecipeRegistry {

	
	private final List<ExpandedFurnaceRecipe> allRecipes = new ArrayList<>();
	

	private static final Lock initLock = new ReentrantLock();
	private static ExpandedFurnaceRecipeRegistry instance = null;
	
	private ExpandedFurnaceRecipeRegistry(){
		// do nothing
	}
	
	/**
	 * Gets a singleton instance of CrusherRecipeRegistry
	 * @return A global instance of CrusherRecipeRegistry
	 */
	public static ExpandedFurnaceRecipeRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					// thread-safe singleton instantiation
					instance = new ExpandedFurnaceRecipeRegistry();
				}
			} finally{
				initLock.unlock();
			}
		}
		return instance;
	}
	
	public void addRecipe(ExpandedFurnaceRecipe recipe){
		allRecipes.add(recipe);
	}
	
	public static void newShapelessRecipe(ItemStack output, Object... ingredients){
		newRecipe(new ShapelessOreRecipe(output, ingredients));
	}
	
	public static void newShapedRecipe(ItemStack output, Object... recipeFormula){
		newRecipe(new ShapedOreRecipe(output, recipeFormula));
	}
	
	public static void newRecipe(ShapedOreRecipe recipe){
		ExpandedFurnaceRecipe r = new ShapedFurnaceRecipe(recipe);
		getInstance().addRecipe(r);
	}
	
	public static void newRecipe(ShapelessOreRecipe recipe){
		ExpandedFurnaceRecipe r = new ShapelessFurnaceRecipe(recipe);
		getInstance().addRecipe(r);
	}
	
	public List<ExpandedFurnaceRecipe> getRecipesForOutputItem(ItemStack output){
		List<ExpandedFurnaceRecipe> result = new LinkedList<>();
		for(ExpandedFurnaceRecipe recipe : allRecipes){
			ItemStack o = recipe.getOutput();
			if(ItemStack.areItemsEqual(o, output)){
				result.add(recipe);
			}
		}
		return result;
	}
	
	public List<ExpandedFurnaceRecipe> getRecipesForInputItem(ItemStack input){
		List<ExpandedFurnaceRecipe> result = new LinkedList<>();
		for(ExpandedFurnaceRecipe recipe : allRecipes){
			if(recipe.usesItem(input)){
				result.add(recipe);
			}
		}
		return result;
	}
	
	public boolean canCraft(InventoryCrafting inventory, World world){
		for(ExpandedFurnaceRecipe recipe : allRecipes){
			if(recipe.isValidInput(inventory, world)){
				return true;
			}
		}
		return false;
	}
	
	public ExpandedFurnaceRecipe getRecipe(InventoryCrafting inventory, World world){
		for(ExpandedFurnaceRecipe recipe : allRecipes){
			if(recipe.isValidInput(inventory, world)){
				return recipe;
			}
		}
		return null;
	}
	
	public List<ExpandedFurnaceRecipe> getAllRecipes(){
		return Collections.unmodifiableList(allRecipes);
	}
}
