package cyano.poweradvantage.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

/**
 * The fuel registry keeps track of the cooking time for new burnables added by PowerAdvantage. The 
 * actual registration of these burnables occurs in <code>cyano.poweradvantage.init.Fluels</code>
 * @author DrCyano
 *
 */
public class FuelRegistry implements IFuelHandler{

	
	private final Map<ItemLookupReference,Short> burnMap = new HashMap<>();

	private FuelRegistry(){}
	
	private static final Lock initLock = new ReentrantLock();
	private static FuelRegistry instance = null;
	
	/**
	 * Gets a singleton instance of FuelRegistry
	 * @return A global instance of FuelRegistry
	 */
	public static FuelRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					// thread-safe singleton instantiation
					instance = new FuelRegistry();
				}
			} finally{
				initLock.unlock();
			}
		}
		return instance;
	}
	
	
	private void registerFuel(ItemLookupReference fuelItem, Number burnTicks){
		burnMap.put(fuelItem, burnTicks.shortValue());
	}
	private void removeFuel(ItemLookupReference fuelItem){
		burnMap.remove(fuelItem);
	}
	/**
	 * Adds a new burnable fuel to Minecraft
	 * @param fuelItem The burnable item
	 * @param burnTicks How many ticks the item burns for (a piece of coal burns for 1600 ticks and 
	 * a single smelting event in a standard furnace uses 200 ticks).
	 */
	public void registerFuel(ItemStack fuelItem, Number burnTicks){
		registerFuel(new ItemLookupReference(fuelItem),burnTicks);
	}
	/**
	 * Adds a new burnable fuel to Minecraft
	 * @param fuelItem The burnable item
	 * @param burnTicks How many ticks the item burns for (a piece of coal burns for 1600 ticks and 
	 * a single smelting event in a standard furnace uses 200 ticks).
	 */
	public void registerFuel(Item fuelItem, Number burnTicks){
		registerFuel(new ItemLookupReference(fuelItem),burnTicks);
	}
	/**
	 * Adds a new burnable fuel to Minecraft
	 * @param fuelItem The burnable item
	 * @param burnTicks How many ticks the item burns for (a piece of coal burns for 1600 ticks and 
	 * a single smelting event in a standard furnace uses 200 ticks).
	 */
	public void registerFuel(Block fuelItem, Number burnTicks){
		registerFuel(new ItemLookupReference(fuelItem),burnTicks);
	}
	/**
	 * Removes an added burnable fuel from PowerAdvantage
	 * @param fuelItem The burnable item to be removed
	 */
	public void removeFuel(ItemStack fuelItem){
		removeFuel(new ItemLookupReference(fuelItem));
	}
	/**
	 * Removes an added burnable fuel from PowerAdvantage
	 * @param fuelItem The burnable item to be removed
	 */
	public void removeFuel(Item fuelItem){
		removeFuel(new ItemLookupReference(fuelItem));
	}
	/**
	 * Removes an added burnable fuel from PowerAdvantage
	 * @param fuelItem The burnable item to be removed
	 */
	public void removeFuel(Block fuelItem){
		removeFuel(new ItemLookupReference(fuelItem));
	}
	
	
	/**
	 * Gets the burn time for an item
	 * @param item Item to look-up in PowerAdvantage's fuel table
	 * @return The number of ticks that the item will burn for, or 0 if that item is not registered 
	 * as burnable in PowerAdvantage (it still could be burnable according to another mod).
	 */
	@Override
	public int getBurnTime(ItemStack item) {
		// see TileEntityFurnace.getItemBurnTime(...) for reference
		Number value = burnMap.get(new ItemLookupReference(item));
		if(value == null){
			return 0;
		}
		return value.intValue();
	}
	
	private static final class ItemLookupReference{
		final Item item;
		final int metaData;
		final int hashCache;

		public ItemLookupReference(ItemStack inputItem){
			item = inputItem.getItem();
			metaData = inputItem.getItemDamage();
			hashCache = item.getUnlocalizedName().hashCode() + (57 * metaData);
		}
		public ItemLookupReference(Item inputItem){
			this(new ItemStack(inputItem,1,0));
		}
		public ItemLookupReference(Block inputItem){
			this(new ItemStack(inputItem,1,0));
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof ItemLookupReference){
				ItemLookupReference that = (ItemLookupReference)other;
				return this.item.equals(that.item) && this.metaData == that.metaData;
			} else if(other instanceof ItemStack){
				ItemStack that = (ItemStack)other;
				return this.item.equals(that.getItem()) && this.metaData == that.getMetadata();
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
