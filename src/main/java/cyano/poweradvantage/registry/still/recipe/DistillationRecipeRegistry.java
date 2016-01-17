package cyano.poweradvantage.registry.still.recipe;

import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This class handles all of the recipes for machines that "smelt" fluids.
 * @author DrCyano
 *
 */
public class DistillationRecipeRegistry {
	
	private final List<DistillationRecipe> recipes = new ArrayList<>(); 

	private final Map<FluidLookupReference,DistillationRecipe> recipeByInputCache = new HashMap<>();
	private final Map<FluidLookupReference,List<DistillationRecipe>> recipeByOutputCache = new HashMap<>();
	
	private static final Lock initLock = new ReentrantLock();
	private static DistillationRecipeRegistry instance = null;
	
	/**
	 * Gets a singleton instance of DistillationRecipeRegistry
	 * @return A global instance of DistillationRecipeRegistry
	 */
	public static DistillationRecipeRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					// thread-safe singleton instantiation
					instance = new DistillationRecipeRegistry();
				}
			} finally{
				initLock.unlock();
			}
		}
		return instance;
	}

	/**
	 * Adds a new Distillation recipe (for the crack hammer and other rock Distillations) 
	 * where the input item is an OreDictionary name. This means that any item 
	 * registered with the OreDictionary will be converted into the specified 
	 * output item. 
	 * @param oreDictionaryName Name in the ore dictionary (e.g. "logWood")
	 * @param output The item to create as the result of this Distillation recipe.
	 */
	public static void addNewDistillationRecipe(final String oreDictionaryName, final ItemStack output){ // preferred method
		getInstance().addRecipe(new OreDictionaryDistillationRecipe(oreDictionaryName,output));
	}
	/**
	 * Adds a new Distillation recipe (for the crack hammer and other rock Distillations) 
	 * where the input item is specified by an ItemStack. This means that only 
	 * the specified item will be converted into the specified output item. 
	 * @param input Item to be crushed
	 * @param output The item to create as the result of this Distillation recipe.
	 */
	public static void addNewDistillationRecipe(final ItemStack input, final ItemStack output){
		getInstance().addRecipe(new ArbitraryDistillationRecipe(input,output));
	}

	/**
	 * Adds a new Distillation recipe (for the crack hammer and other rock Distillations) 
	 * where the input item is specified by an Item instance. This means that 
	 * only the specified item will be converted into the specified output item. 
	 * Note that this will assume an item metadata value of 0.
	 * @param input Item to be crushed
	 * @param output The item to create as the result of this Distillation recipe.
	 */
	public static void addNewDistillationRecipe(final Item input, final ItemStack output){
		getInstance().addRecipe(new FluidLookupReference(){

			@Override
			public ItemStack getOutput() {
				return output;
			}

			@Override
			public boolean isValidInput(ItemStack in) {
				return input.equals(in.getItem());
			}

			@Override
			public Collection<ItemStack> getValidInputs() {
				return Arrays.asList(new ItemStack(input));
			}
			
		});
	}
	/**
	 * Adds a new Distillation recipe (for the crack hammer and other rock Distillations) 
	 * where the input item is a block. This means that any block of this type 
	 * will be converted into the specified output item. If you want to restrict 
	 * the block inputs to certain metadata values, convert the block into an 
	 * ItemStack instead of providing it as a block instance.
	 * @param input Block to be crushed
	 * @param output The item to create as the result of this Distillation recipe.
	 */
	public static void addNewDistillationRecipe(final Block input, final ItemStack output){
		getInstance().addRecipe(new FluidLookupReference(){

			@Override
			public ItemStack getOutput() {
				return output;
			}

			@Override
			public boolean isValidInput(ItemStack in) {
				return input.equals(Block.getBlockFromItem(in.getItem()));
			}

			@Override
			public Collection<ItemStack> getValidInputs() {
				return Arrays.asList(new ItemStack(input));
			}
			
		});
	}
	
	/**
	 * Clears the fast-look-up cache. If recipes are added after the game 
	 * starts, call this method to ensure that the new recipes will be used. 
	 */
	public void clearCache(){
		recipeByInputCache.clear();
		recipeByOutputCache.clear();
	}
	
	/**
	 * This is the universal method for adding new Distillation recipes
	 * @param DistillationRecipe An implementation of the FluidLookupReference interface. 
	 */
	public void addRecipe(FluidLookupReference DistillationRecipe){
		recipes.add(DistillationRecipe);
	}
	
	/**
	 * Gets a list of Distillation recipes whose output is equal to the specified 
	 * output item. If there are no such recipes, then null is returned (instead  
	 * of an empty list).
	 * @param output The item resulting from the crushing of another item or block  
	 * @return A list of recipes producing the requested item, or null if no 
	 * such recipes exist 
	 */
	public List<FluidLookupReference> getRecipesForOutputItem(ItemStack output){
		FluidLookupReference ref = new FluidLookupReference(output);
		if(recipeByOutputCache.containsKey(ref)){
			List<FluidLookupReference> recipeCache = recipeByOutputCache.get(ref);
			if(recipeCache.isEmpty()) return null;
			return recipeCache;
		} else {
			// add recipe cache
			List<FluidLookupReference> recipeCache = new ArrayList<>();
			for(FluidLookupReference r : recipes){
				if(ItemStack.areItemsEqual(r.getOutput(), output)){
					recipeCache.add(r);
				}
			}
			recipeByOutputCache.put(ref, recipeCache);
			if(recipeCache.isEmpty()) return null;
			return recipeCache;
		}
	}
	/**
	 * Gets a list of Distillation recipes whose output is equal to the specified 
	 * output item. If there are no such recipes, then null is returned (instead  
	 * of an empty list).
	 * @param output The block resulting from the crushing of another item or block  
	 * @return A list of recipes producing the requested item, or null if no 
	 * such recipes exist 
	 */
	public List<FluidLookupReference> getRecipesForOutputItem(IBlockState output){
		FluidLookupReference ref = new FluidLookupReference(output);
		if(recipeByOutputCache.containsKey(ref)){
			List<FluidLookupReference> recipeCache = recipeByOutputCache.get(ref);
			if(recipeCache.isEmpty()) return null;
			return recipeCache;
		} else {
			// add recipe cache
			List<FluidLookupReference> recipeCache = new ArrayList<>();
			for(FluidLookupReference r : recipes){
				if(ref.equals(r.getOutput())){
					recipeCache.add(r);
				}
			}
			recipeByOutputCache.put(ref, recipeCache);
			if(recipeCache.isEmpty()) return null;
			return recipeCache;
		}
	}
	/**
	 * Gets the recipe for crushing the specified item, or null if ther is no 
	 * recipe accepting the item.
	 * @param input The item/block to crush
	 * @return The Distillation recipe for crushing this item/block, or null if no 
	 * such recipe exists
	 */
	public FluidLookupReference getRecipeForInputItem(ItemStack input){
		FluidLookupReference ref = new FluidLookupReference(input);
		if(recipeByInputCache.containsKey(ref)){
			return recipeByInputCache.get(ref);
		} else {
			for(FluidLookupReference r : recipes){
				if(r.isValidInput(input)){
					recipeByInputCache.put(ref, r);
					return r;
				}
			}
			// no recipes, cache null result
			recipeByInputCache.put(ref, null);
			return null;
		}
	}
	/**
	 * Gets the recipe for crushing the specified item, or null if ther is no 
	 * recipe accepting the item.
	 * @param input The item/block to crush
	 * @return The Distillation recipe for crushing this item/block, or null if no 
	 * such recipe exists
	 */
	public FluidLookupReference getRecipeForInputItem(IBlockState input){
		FluidLookupReference ref = new FluidLookupReference(input);
		ItemStack stack = new ItemStack(input.getBlock(),1,ref.metaData);
		if(recipeByInputCache.containsKey(ref)){
			return recipeByInputCache.get(ref);
		} else {
			for(FluidLookupReference r : recipes){
				if(r.isValidInput(stack)){
					recipeByInputCache.put(ref, r);
					return r;
				}
			}
			// no recipes, cache null result
			recipeByInputCache.put(ref, null);
			return null;
		}
	}

	/**
	 * Gets all registered Distillation recipes
	 * @return An unmodifiable list of all registered Distillation recipes
	 */
	public Collection<FluidLookupReference> getAllRecipes() {
		return Collections.unmodifiableList(recipes);
	}
	
	private static final class FluidLookupReference{
		final Fluid fluid;
		final int hashCache;
		
		public FluidLookupReference(FluidStack input){
			this(input.getFluid());
		}
		
		public FluidLookupReference(Fluid input){
			fluid = input;
			hashCache = (input == null) ? 0 : fluid.getName().hashCode();
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof FluidLookupReference){
				FluidLookupReference that = (FluidLookupReference)other;
				return this.hashCache == that.hashCache && this.fluid == that.fluid;
			} else if(other instanceof FluidStack){
				FluidStack that = (FluidStack)other;
				return this.fluid == that.getFluid();
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode(){
			return hashCache;
		}
	}
}
