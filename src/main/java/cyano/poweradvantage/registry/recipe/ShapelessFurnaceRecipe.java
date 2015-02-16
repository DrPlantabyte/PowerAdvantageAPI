package cyano.poweradvantage.registry.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessFurnaceRecipe extends ExpandedFurnaceRecipe{
	
	
	private final ShapelessOreRecipe recipe;
	private int hash;
	
	public ShapelessFurnaceRecipe(ShapelessOreRecipe recipe){
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
		if(this.hashCode() == other.hashCode() && other instanceof ShapelessFurnaceRecipe){
			if(ItemStack.areItemsEqual(this.recipe.getRecipeOutput(),((ShapelessFurnaceRecipe)other).recipe.getRecipeOutput())
					&& this.recipe.getInput().size() == ((ShapelessFurnaceRecipe)other).recipe.getInput().size()){
				for(int i = 0; i < this.recipe.getInput().size() ; i++){
					if(!this.recipe.getInput().get(i).equals(((ShapelessFurnaceRecipe)other).recipe.getInput().get(i))){
						return false;
					}
				}
				return true;
			} 
		}
		return false;
	}
}
