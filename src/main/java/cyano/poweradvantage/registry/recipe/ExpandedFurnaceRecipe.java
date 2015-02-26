package cyano.poweradvantage.registry.recipe;

import java.util.Collection;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Power Advantage introduces furnaces with crafting grids. Like normal crafting 
 * recipes, these recipes involve plasing items on a grid and then either a 
 * shaped or shapeless recipe determines which item to produce upon completion 
 * of the smelting process.
 * @author DrCyano
 *
 */
public abstract class ExpandedFurnaceRecipe {
 // TODO: document this class and subclasses
	public boolean isValidInput(ItemStack... input){
		final net.minecraft.inventory.Container container = new net.minecraft.inventory.Container(){
			@Override
			public boolean canInteractWith(EntityPlayer arg0) {
				return false;
			}
		};
		for(int i = 0; i < 9; i++){
			if(i >= input.length){
				container.inventoryItemStacks.add((ItemStack)null);
			} else {
				container.inventoryItemStacks.add(input[i]);
			}
		}
		final InventoryCrafting fauxInv = new InventoryCrafting(container,3,3);
		return isValidInput(fauxInv, Minecraft.getMinecraft().theWorld);
	}
	
	public abstract boolean isValidInput(InventoryCrafting inventory, World world);
	
	public abstract ItemStack craftRecipe(InventoryCrafting inventory);
	
	public abstract ItemStack getOutput();
	
	public abstract IRecipe getRecipe();
	
	
	/**
	 * The NEI recipe display expects 9 itemstack slots (3x3), but a recipe 
	 * might have a 2x2 input area (or 1x1). This method properly generates a 
	 * an array to represent the recipe on a 3x33 crafting grid. 
	 * @param itemStacks an array of size 1 or 4
	 * @return an array of size 9. Note that an empty space is a null ItemStack. 
	 */
	protected ItemStack[] convert2x2To3x3(ItemStack... itemStacks ){
		ItemStack[][] temp = new ItemStack[3][3]; // x,y
		for(int i = 0; i < itemStacks.length; i++){
			temp[i % 2][i / 2] = itemStacks[i];
		}
		ItemStack[] output = new ItemStack[9];
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 3; x++){
				output[(y*3) + (x % 3)] = temp[x][y];
			}
		}
		return output;
	}
	

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object other);

	public abstract boolean usesItem(ItemStack input);
	
	protected boolean areOreDictionaryItemsEqual(ItemStack a, ItemStack b){
		if(a == null || b == null){
			ItemStack.areItemsEqual(a, b);
		}
		if(a.getMetadata() == OreDictionary.WILDCARD_VALUE || b.getMetadata() == OreDictionary.WILDCARD_VALUE){
			return a.getItem() == b.getItem();
		} else {
			return ItemStack.areItemsEqual(a, b);
		}
	}
}
