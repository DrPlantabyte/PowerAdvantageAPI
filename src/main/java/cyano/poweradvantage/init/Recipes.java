package cyano.poweradvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cyano.poweradvantage.registry.ExpandedFurnaceRecipeRegistry;
import cyano.poweradvantage.registry.FuelRegistry;

public abstract class Recipes {

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		ExpandedFurnaceRecipeRegistry.newShapedRecipe(new ItemStack(net.minecraft.init.Blocks.iron_ore), "xxx","xix","xxx",'x',"cobblestone",'i',"ingotIron"); // TODO: remove test code
		ExpandedFurnaceRecipeRegistry.newShapedRecipe(new ItemStack(net.minecraft.init.Blocks.iron_ore), "xxx","xix","xxx",'x',"cobblestone",'i',"dustIron"); // TODO: remove test code
		
		initDone = true;
	}
	
}
