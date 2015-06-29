package cyano.poweradvantage.conduitnetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.block.Block;
import cyano.poweradvantage.api.ILightWeightPowerAcceptor;

// TODO: put example in comment
/**
 * This class provides a place to register blocks from other mods as power consumers.
 * @author DrCyano
 *
 */
public class LightWeightPowerRegistry {

	// thread-safe singleton instantiation
	private static LightWeightPowerRegistry instance = null;
	private static final Lock initLock = new ReentrantLock();
	
	/** Map of blocks that are power consumers and the implementation needed to interface that block */
	private final Map<Block,ILightWeightPowerAcceptor> externalPowerSinks = new HashMap<>();
	
	/**
	 * Thread-safe singleton instantiation
	 * @return A singleton instance of this class 
	 */
	public static LightWeightPowerRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					instance = new LightWeightPowerRegistry();
				}
			}finally{
				initLock.unlock();
			}
		}
		return instance;
	}

	public static void registerLightWeightPowerAcceptor(Block machineBlock, ILightWeightPowerAcceptor powerAcceptorImplementation){
		// TODO: implementation
	}
	
}
