package cyano.poweradvantage.registry;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import cyano.poweradvantage.registry.recipe.*;

public class CrusherRecipeRegistry {
	
	private final Set<ICrusherRecipe> recipes = new LinkedHashSet<>(); 
	
	private static final Lock initLock = new ReentrantLock();
	private static CrusherRecipeRegistry instance = null;
	
	/**
	 * Gets a singleton instance of CrusherRecipeRegistry
	 * @return A global instance of CrusherRecipeRegistry
	 */
	public static CrusherRecipeRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					// thread-safe singleton instantiation
					instance = new CrusherRecipeRegistry();
				}
			} finally{
				initLock.unlock();
			}
		}
		return instance;
	}
}
