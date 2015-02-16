package cyano.poweradvantage.registry.recipe;

import java.util.Collection;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedFurnaceRecipe extends ExpandedFurnaceRecipe{
	
	
	private final ShapedOreRecipe recipe;
	private int hash;
	
	public ShapedFurnaceRecipe(ShapedOreRecipe recipe){
		this.recipe = recipe;
		int hash = recipe.getRecipeOutput().toString().hashCode();
		for(Object o : recipe.getInput()){
			hash = (57 * hash) + o.hashCode();
		}
		this.hash = hash;
	}
	
	@Override
	public boolean isValidInput(InventoryCrafting inventory, World world) {
		return recipe.matches(inventory, world);
	}
	@Override
	public ItemStack craftRecipe(InventoryCrafting inventory) {
		return recipe.getCraftingResult(inventory);
	}
	@Override
	public IRecipe getRecipe(){
		return recipe;
	}
	
	@Override
	public ItemStack getOutput() {
		return recipe.getRecipeOutput();
	}


	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if(this == other) return true;
		if(this.hashCode() == other.hashCode() && other instanceof ShapedFurnaceRecipe){
			if(ItemStack.areItemsEqual(this.recipe.getRecipeOutput(),((ShapedFurnaceRecipe)other).recipe.getRecipeOutput())
					&& this.recipe.getInput().length == ((ShapedFurnaceRecipe)other).recipe.getInput().length){
				for(int i = 0; i < this.recipe.getInput().length ; i++){
					if(!this.recipe.getInput()[i].equals(((ShapedFurnaceRecipe)other).recipe.getInput()[i])){
						return false;
					}
				}
				return true;
			} 
		}
		return false;
	}

}
