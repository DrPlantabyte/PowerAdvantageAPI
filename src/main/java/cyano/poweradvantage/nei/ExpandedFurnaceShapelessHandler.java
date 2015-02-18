package cyano.poweradvantage.nei;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.registry.ExpandedFurnaceRecipeRegistry;
import cyano.poweradvantage.registry.recipe.ExpandedFurnaceRecipe;

public class ExpandedFurnaceShapelessHandler extends TemplateRecipeHandler{

	public final int[][] stackorder = new int[][]{
			{0, 0},
			{1, 0},
			{0, 1},
			{1, 1},
			{0, 2},
			{1, 2},
			{2, 0},
			{2, 1},
			{2, 2}};
	
	///// meat and potatoes /////
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("crafting") && getClass() == ExpandedFurnaceShapelessHandler.class) { // avoid overstepping subclasses
			for (ExpandedFurnaceRecipe r : ExpandedFurnaceRecipeRegistry.getInstance().getAllRecipes()) {
				IRecipe irecipe = r.getRecipe();
				CachedShapelessRecipe recipe = null;
				if (irecipe instanceof ShapelessRecipes)
					recipe = shapelessRecipe((ShapelessRecipes) irecipe);
				else if (irecipe instanceof ShapelessOreRecipe)
					recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
				if (recipe == null)
					continue;
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
				CachedShapelessRecipe recipe = null;
				if (irecipe instanceof ShapelessRecipes)
					recipe = shapelessRecipe((ShapelessRecipes) irecipe);
				else if (irecipe instanceof ShapelessOreRecipe)
					recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
				if (recipe == null)
					continue;
				arecipes.add(recipe);
			}
		}
	}
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (ExpandedFurnaceRecipe r : ExpandedFurnaceRecipeRegistry.getInstance().getAllRecipes()) {
			IRecipe irecipe = r.getRecipe();
			CachedShapelessRecipe recipe = null;
			if (irecipe instanceof ShapelessRecipes)
				recipe = shapelessRecipe((ShapelessRecipes) irecipe);
			else if (irecipe instanceof ShapelessOreRecipe)
				recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
			if (recipe == null)
				continue;
			if (recipe.contains(recipe.ingredients, ingredient)) {
				recipe.setIngredientPermutation(recipe.ingredients, ingredient);
				arecipes.add(recipe);
			}
		}
	}
	private CachedShapelessRecipe shapelessRecipe(ShapelessRecipes recipe) {
		if(recipe.recipeItems == null) //because some mod subclasses actually do this
			return null;
		return new CachedShapelessRecipe(recipe.recipeItems, recipe.getRecipeOutput());
	}
	public CachedShapelessRecipe forgeShapelessRecipe(ShapelessOreRecipe recipe) {
		ArrayList<Object> items = recipe.getInput();
		for (Object item : items)
			if (item instanceof List && ((List<?>) item).isEmpty())//ore handler, no ores
				return null;
		return new CachedShapelessRecipe(items, recipe.getRecipeOutput());
	}
	
	public boolean isRecipe2x2(int recipe) {
		return getIngredientStacks(recipe).size() <= 4;
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
		return "shapelessfurnace";
	}

	@Override
	public String getGuiTexture() {
		return PowerAdvantage.MODID+":textures/gui/nei/nei_expanded_furnace.png";
	}
	
	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(87, 43, 176, 0, 14, 14, 48, 7);
		drawProgressBar(83, 24, 176, 14, 24, 16, 48, 0);
	}

	@Override
	public TemplateRecipeHandler newInstance() {
		return super.newInstance();
	}
	
	

	@Override
	public String getRecipeName() {
		String key = "nei."+PowerAdvantage.MODID+".recipehandler.shapelessfurnace.name";
		if(StatCollector.canTranslate(key)){
			return StatCollector.translateToLocal(key); 
		} else {
			return "shapelessfurnace";
		}
	}
	

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "shapelessfurnace"));
	}
	
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return ExpandedFurnaceGUIContainer.class;
	}
	
	
	
	public class CachedShapelessRecipe extends CachedRecipe
	{
		public CachedShapelessRecipe() {
			ingredients = new ArrayList<PositionedStack>();
		}
		public CachedShapelessRecipe(ItemStack output) {
			this();
			setResult(output);
		}
		public CachedShapelessRecipe(Object[] input, ItemStack output) {
			this(Arrays.asList(input), output);
		}
		public CachedShapelessRecipe(List<?> input, ItemStack output) {
			this(output);
			setIngredients(input);
		}
		public void setIngredients(List<?> items) {
			ingredients.clear();
			for (int ingred = 0; ingred < items.size(); ingred++) {
				PositionedStack stack = new PositionedStack(items.get(ingred), 25 + stackorder[ingred][0] * 18, 6 + stackorder[ingred][1] * 18);
				stack.setMaxSize(1);
				ingredients.add(stack);
			}
		}
		public void setResult(ItemStack output) {
			result = new PositionedStack(output, 119, 24);
		}
		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}
		@Override
		public PositionedStack getResult() {
			return result;
		}
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;
	}
}
