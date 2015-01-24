package cyano.poweradvantage.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import cyano.poweradvantage.registry.CrusherRecipeRegistry;

public abstract class CrusherRecipeInitializer {

	public static void initVanillaCrusherRecipes(FMLInitializationEvent event){
		// see OreDictionary.initVanillaEntries() for vanilla oreDict names
		CrusherRecipeRegistry.addNewCrusherRecipe("stone", new ItemStack(Blocks.cobblestone,1));
		CrusherRecipeRegistry.addNewCrusherRecipe("cobblestone", new ItemStack(Blocks.sand,1));
		CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.gravel, new ItemStack(Items.flint,1));
		CrusherRecipeRegistry.addNewCrusherRecipe("sandstone", new ItemStack(Blocks.sand,4));
		CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.glowstone, new ItemStack(Items.glowstone_dust,4));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreLapis", new ItemStack(Items.dye,8,4));
		CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.redstone_ore, new ItemStack(Items.redstone,8));
		CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.redstone_block, new ItemStack(Items.redstone,9));
		CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.reeds, new ItemStack(Items.sugar,2));
		CrusherRecipeRegistry.addNewCrusherRecipe(Items.bone, new ItemStack(Items.dye,6,15));
		CrusherRecipeRegistry.addNewCrusherRecipe(Items.blaze_rod, new ItemStack(Items.blaze_powder,4));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreQuartz", new ItemStack(Items.quartz,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("blockQuartz", new ItemStack(Items.quartz,4));
		
		
	}
	public static void initPowerAdvantageCrusherRecipes(FMLInitializationEvent event){
		//CrusherRecipeRegistry.addNewCrusherRecipe("blockGlass", new ItemStack(cyano.poweradvantage.item.Items.powderedGlass,1));
		/*
		 
		CrusherRecipeRegistry.addNewCrusherRecipe("oreIron", new ItemStack(cyano.poweradvantage.item.Items.powderedIron,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreGold", new ItemStack(cyano.poweradvantage.item.Items.powderedGold,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreSilver", new ItemStack(cyano.poweradvantage.item.Items.powderedSilver,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreCopper", new ItemStack(cyano.poweradvantage.item.Items.powderedCopper,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreTin", new ItemStack(cyano.poweradvantage.item.Items.powderedTin,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreZinc", new ItemStack(cyano.poweradvantage.item.Items.powderedZinc,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("oreLead", new ItemStack(cyano.poweradvantage.item.Items.powderedLead,2)); // make lead powder edible and cause lethal wither effect
		CrusherRecipeRegistry.addNewCrusherRecipe("oreMercury", new ItemStack(cyano.poweradvantage.item.Items.powderedMercuryOxide,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("cropPotato", new ItemStack(cyano.poweradvantage.item.Items.starch,1)); // for bioplastic recipe
		 */

		CrusherRecipeRegistry.addNewCrusherRecipe("oreCoal", new ItemStack(cyano.poweradvantage.item.Items.powderedCoal,2));
		CrusherRecipeRegistry.addNewCrusherRecipe("blockCoal", new ItemStack(cyano.poweradvantage.item.Items.powderedCoal,9));
		CrusherRecipeRegistry.addNewCrusherRecipe(Items.coal, new ItemStack(cyano.poweradvantage.item.Items.powderedCoal,1));
		CrusherRecipeRegistry.addNewCrusherRecipe(new ItemStack(Items.coal,1,1), new ItemStack(cyano.poweradvantage.item.Items.powderedCoal,1));
		// TODO: crusher recipes
	}
}
