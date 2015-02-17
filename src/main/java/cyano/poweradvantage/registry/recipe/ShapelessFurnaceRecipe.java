package cyano.poweradvantage.registry.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
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
	

	@Override
	public boolean usesItem(ItemStack input) {
		if(input == null) return false;
		List formula = recipe.getInput();
		for(Object i : formula){
			if(i instanceof ItemStack && areOreDictionaryItemsEqual(input,(ItemStack)i)) return true;
			if(i instanceof List<?> ){
				List l = (List<ItemStack>)i;
				for(Object i2 : l){
					if(i2 instanceof ItemStack && areOreDictionaryItemsEqual(input,(ItemStack)i2)) return true;
				}
			}
			if(i instanceof String){
				List<ItemStack> l = OreDictionary.getOres((String)i);
				for(ItemStack i2 : l){
					if(areOreDictionaryItemsEqual(input,(ItemStack)i2)) return true;
				}
			}
		}
		return false;
	}
}
