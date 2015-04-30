package cyano.poweradvantage.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import cyano.basemetals.BaseMetals;
import cyano.basemetals.registry.CrusherRecipeRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.RecipeMode;


public abstract class Recipes {

	// TODO: recipes modes:
	/*
	 * Normal - can craft all necessary machine parts
	 * Apocalyptic - must find key parts as treasure in chests
	 * Tech-progression - making the first key part is very complicated, but once made, key parts can be duplicated fairly easily
	 */
	
	// TODO: add recipes
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		Blocks.init();
		Items.init();
		
		if(PowerAdvantage.recipeMode == RecipeMode.TECH_PROGRESSION){
			// make things a little more complicated with tech-progression mode
			BaseMetals.strongHammers = false;
		}

		OreDictionary.registerOre("potato", net.minecraft.init.Items.poisonous_potato);
		OreDictionary.registerOre("potato", net.minecraft.init.Items.potato);
		CrusherRecipeRegistry.addNewCrusherRecipe("potato", new ItemStack(Items.starch,1));
		GameRegistry.addSmelting(Items.starch, new ItemStack(Items.bioplastic_ingot,1), 0.1f);
		
		
		if(PowerAdvantage.recipeMode != RecipeMode.APOCALYPTIC){
			// recipes that are not available in post-apocalypse mode
			// TODO: recipe for sprocket
		} else {
			// salvage recipes unique to post-apocalypse mode
			// TODO: smash machines with hammer to get sprockets and other rare resources back
		}
		initDone = true;
	}
	
}
