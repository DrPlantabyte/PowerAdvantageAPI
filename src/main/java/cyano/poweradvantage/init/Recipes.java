package cyano.poweradvantage.init;

import cyano.basemetals.BaseMetals;
import cyano.basemetals.registry.CrusherRecipeRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.RecipeMode;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;


public abstract class Recipes {

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

		OreDictionary.registerOre("bread", net.minecraft.init.Items.bread);
		OreDictionary.registerOre("coal", net.minecraft.init.Items.coal);
		OreDictionary.registerOre("furnace", net.minecraft.init.Blocks.furnace);

		OreDictionary.registerOre("potato", net.minecraft.init.Items.poisonous_potato);
		OreDictionary.registerOre("potato", net.minecraft.init.Items.potato);
		CrusherRecipeRegistry.addNewCrusherRecipe("potato", new ItemStack(Items.starch,1));
		GameRegistry.addSmelting(Items.starch, new ItemStack(Items.bioplastic_ingot,1), 0.1f);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.rotator_tool,1),"xx","x*"," x",'x',"ingotIron",'*',"sprocket"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_conveyor,5),"xxx","ghg","xxx",'x',"plateSteel",'g',"sprocket",'h',net.minecraft.init.Blocks.hopper));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_block,1),"x","y","z",'y',"stone",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_food,1),"x","y","z",'y',"bread",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_fuel,1),"x","y","z",'y',"coal",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_inventory,1),"x","y","z",'y',"chest",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_ore,1),"x","y","z",'y',"ingotGold",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_plant,1),"x","y","z",'y',"treeSapling",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_smelt,1),"x","y","z",'y',"furnace",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.item_filter_overflow,1),"x","z",'x',Blocks.item_conveyor,'z',net.minecraft.init.Blocks.wooden_pressure_plate));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.steel_frame,1),"xxx","x x","xxx",'x',"barsSteel"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.storage_tank,1),"xxx","xpx","xxx",'x',"ingotPlastic",'p',"pipe"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.metal_storage_tank,1),"xxx","xpx","xxx",'x',"ingotSteel",'p',"pipe"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_drain,1)," x ","w#w","ppp",'x',"bars",'w',"plateSteel",'#',"frameSteel",'p',"pipe"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_discharge,1),"ppp","w#w"," x ",'x',"bars",'w',"plateSteel",'#',"frameSteel",'p',"pipe"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.still,1),"bpb"," f ",'b',net.minecraft.init.Items.bucket,'p',"pipe",'f',net.minecraft.init.Blocks.furnace));
		

		// fluid recipes
		DistillationRecipeRegistry.addDistillationRecipe(new FluidStack(Fluids.crude_oil,2), new FluidStack(Fluids.refined_oil,1));
		
		// recipe modes
		if(PowerAdvantage.recipeMode == RecipeMode.NORMAL){
			// normal means easy
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.sprocket,4)," x ","x x"," x ",'x',"ingotSteel"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.sprocket,4)," x ","x x"," x ",'x',"ingotIron"));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,6),"xxx","   ","xxx",'x',"ingotIron"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,6),"xxx","   ","xxx",'x',"ingotCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.metal_storage_tank,1),"xxx","xpx","xxx",'x',"ingotIron",'p',"pipe"));
		}else if(PowerAdvantage.recipeMode == RecipeMode.TECH_PROGRESSION){
			// make things a little more complicated with tech-progression mode
			BaseMetals.strongHammers = false;
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.sprocket,4)," x ","x/x"," x ",'x',"ingotSteel",'/',"stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,6),"xxx","   ","xxx",'x',"ingotIron"));
		}else if(PowerAdvantage.recipeMode == RecipeMode.APOCALYPTIC){
			// apocalyptic means some things are not craftable, but some stuff can be recycled
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_conveyor, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_block, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_food, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_fuel, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_inventory, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_ore, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_plant, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_smelt, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.item_filter_overflow, new ItemStack(cyano.basemetals.init.Blocks.steel_plate,1));

			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.storage_tank, new ItemStack(Blocks.fluid_pipe,1));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.fluid_discharge, new ItemStack(Blocks.fluid_pipe,2));
			CrusherRecipeRegistry.addNewCrusherRecipe(Blocks.fluid_drain, new ItemStack(Blocks.fluid_pipe,2));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,3),"xxx","   ","xxx",'x',"ingotIron"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,3),"xxx","   ","xxx",'x',"ingotCopper"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fluid_pipe,3),"xxx","   ","xxx",'x',"ingotLead"));
		}
		
		
		initDone = true;
	}
	
}
