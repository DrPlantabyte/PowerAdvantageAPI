package cyano.poweradvantage.init;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cyano.poweradvantage.registry.ExpandedFurnaceRecipeRegistry;
import cyano.poweradvantage.registry.FuelRegistry;

public abstract class Recipes {

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		ExpandedFurnaceRecipeRegistry.newShapedRecipe(new ItemStack(net.minecraft.init.Blocks.iron_ore), "xx","xi",'x',"cobblestone",'i',"ingotIron"); // TODO: remove test code
		ExpandedFurnaceRecipeRegistry.newShapedRecipe(new ItemStack(net.minecraft.init.Blocks.iron_ore), "xx","xi",'x',"cobblestone",'i',"dustIron"); // TODO: remove test code
		
		
		// TODO: replace ingredients with OreDictionary strings
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.brass_ingot,3), cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.zinc_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.bronze_ingot,4), cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.tin_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.steel_ingot,9), cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.iron_powder, cyano.basemetals.init.Items.carbon_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.invar_ingot,3), cyano.basemetals.init.Items.iron_powder,cyano.basemetals.init.Items.iron_powder,cyano.basemetals.init.Items.nickel_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.electrum_ingot,2), cyano.basemetals.init.Items.silver_powder,cyano.basemetals.init.Items.gold_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.mithril_ingot,3), cyano.basemetals.init.Items.silver_powder,cyano.basemetals.init.Items.silver_powder,cyano.basemetals.init.Items.coldiron_powder,cyano.basemetals.init.Items.mercury_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.aquarium_ingot,3), cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.copper_powder,cyano.basemetals.init.Items.zinc_powder, Items.prismarine_crystals, Items.prismarine_crystals, Items.prismarine_crystals);
		
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.brass_ingot,3), cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.zinc_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.bronze_ingot,4), cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.tin_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.steel_ingot,9), Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, cyano.basemetals.init.Items.carbon_powder);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.steel_ingot,9), Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, Items.iron_ingot, "coal");
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.invar_ingot,3), Items.iron_ingot,Items.iron_ingot,cyano.basemetals.init.Items.nickel_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.electrum_ingot,2), cyano.basemetals.init.Items.silver_ingot,Items.gold_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.mithril_ingot,3), cyano.basemetals.init.Items.silver_ingot,cyano.basemetals.init.Items.silver_ingot,cyano.basemetals.init.Items.coldiron_ingot,cyano.basemetals.init.Items.mercury_ingot);
		ExpandedFurnaceRecipeRegistry.newShapelessRecipe(new ItemStack(cyano.basemetals.init.Items.aquarium_ingot,3), cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.copper_ingot,cyano.basemetals.init.Items.zinc_ingot, Items.prismarine_crystals, Items.prismarine_crystals, Items.prismarine_crystals);
		
		
		initDone = true;
	}
	
}
