package cyano.poweradvantage.nei;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import codechicken.core.ReflectionManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.registry.ExpandedFurnaceRecipeRegistry;
import cyano.poweradvantage.registry.recipe.ExpandedFurnaceRecipe;

public class ExpandedFurnaceShapedHandler extends TemplateRecipeHandler{

	///// meat and potatoes /////
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("shapedfurnace") && getClass() == ExpandedFurnaceShapedHandler.class) { // avoid over-stepping subclasses
			for (ExpandedFurnaceRecipe r : ExpandedFurnaceRecipeRegistry.getInstance().getAllRecipes()) {
				IRecipe irecipe = r.getRecipe();
				CachedShapedRecipe recipe = null;
				if (irecipe instanceof ShapedRecipes)
					recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
				else if (irecipe instanceof ShapedOreRecipe)
					recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
				if (recipe == null)
					continue;
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}
	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (ExpandedFurnaceRecipe r : ExpandedFurnaceRecipeRegistry.getInstance().getAllRecipes()) {
			IRecipe irecipe = r.getRecipe();
			if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
				CachedShapedRecipe recipe = null;
				if (irecipe instanceof ShapedRecipes)
					recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
				else if (irecipe instanceof ShapedOreRecipe)
					recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
				if (recipe == null)
					continue;
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (ExpandedFurnaceRecipe r : ExpandedFurnaceRecipeRegistry.getInstance().getAllRecipes()) {
			IRecipe irecipe = r.getRecipe();
			CachedShapedRecipe recipe = null;
			if (irecipe instanceof ShapedRecipes)
				recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
			else if (irecipe instanceof ShapedOreRecipe)
				recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);
			if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem()))
				continue;
			recipe.computeVisuals();
			if (recipe.contains(recipe.ingredients, ingredient)) {
				recipe.setIngredientPermutation(recipe.ingredients, ingredient);
				arecipes.add(recipe);
			}
		}
	}
	public CachedShapedRecipe forgeShapedRecipe(ShapedOreRecipe recipe) {
		int width;
		int height;
		try {
			// XXX: When you update to new forge, double-check that width and height are the 5th and 6th member variables of class ShapedOreRecipe
			width = getIntFieldByIndex(ShapedOreRecipe.class, recipe, 4);
			height = getIntFieldByIndex(ShapedOreRecipe.class, recipe, 5);
		} catch (Exception e) {
			NEIClientConfig.logger.error("Error loading recipe", e);
			return null;
		}
		Object[] items = recipe.getInput();
		for (Object item : items)
			if (item instanceof List && ((List<?>) item).isEmpty())//ore handler, no ores
				return null;
		return new CachedShapedRecipe(width, height, items, recipe.getRecipeOutput());
	}
	
	private static int getIntFieldByIndex(Class cast, Object target, int index) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = cast.getDeclaredFields();
		if(fields[index].isAccessible() == false){
			fields[index].setAccessible(true);
		}
		int value = fields[index].getInt(target);
		return value;
	}
	
	///// Boiler plate /////
	@Override
	public String getOverlayIdentifier() {
		return "shapedfurnace";
	}

	@Override
	public String getGuiTexture() {
		return PowerAdvantage.MODID+":textures/gui/nei/nei_expanded_furnace.png";
	}
	
	

	@Override
	public TemplateRecipeHandler newInstance() {
		return super.newInstance();
	}
	
	

	@Override
	public String getRecipeName() {
		String key = "nei."+PowerAdvantage.MODID+".recipehandler.shapedfurnace.name";
		if(StatCollector.canTranslate(key)){
			return StatCollector.translateToLocal(key); 
		} else {
			return "shapedfurnace";
		}
	}
	

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "shapedfurnace"));
	}
	
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return ExpandedFurnaceGUIContainer.class;
	}
	
	
	
	public class CachedShapedRecipe extends CachedRecipe
	{
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;
		public CachedShapedRecipe(int width, int height, Object[] items, ItemStack out) {
			result = new PositionedStack(out, 119, 24);
			ingredients = new ArrayList<PositionedStack>();
			setIngredients(width, height, items);
		}
		public CachedShapedRecipe(ShapedRecipes recipe) {
			this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, recipe.getRecipeOutput());
		}
		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (items[y * width + x] == null)
						continue;
					PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
					stack.setMaxSize(1);
					ingredients.add(stack);
				}
			}
		}
		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}
		public PositionedStack getResult() {
			return result;
		}
		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();
		}
	}
}
