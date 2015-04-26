package cyano.poweradvantage.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import cyano.basemetals.registry.CrusherRecipeRegistry;


public abstract class Recipes {

	// TODO: recipes modes:
	/*
	 * Normal - can craft all necessary machine parts
	 * Apocalyptic - must find key parts as treasure in chests
	 * Tech-progression - making the first key part is very complicated, but once made, key parts can be duplicated fairly easily
	 */
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Blocks.init();
		Items.init();

		OreDictionary.registerOre("potato", net.minecraft.init.Items.poisonous_potato);
		OreDictionary.registerOre("potato", net.minecraft.init.Items.potato);
		CrusherRecipeRegistry.addNewCrusherRecipe("potato", new ItemStack(Items.starch,1));
		GameRegistry.addSmelting(Items.starch, new ItemStack(Items.bioplastic_ingot,1), 0.1f);
		
		initDone = true;
	}
	
}
