package cyano.poweradvantage.conduitnetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.math.BlockPos4D;

/**
 * This is the master keeper of power networks.
 * @author DrCyano
 *
 */
public class ConduitRegistry {

	private final Map<ConduitType,ConduitNetworkManager> networkManagers = new HashMap<>();
	
	
	
	// thread-safe singleton instantiation
	private static ConduitRegistry instance = null;
	private static final Lock initLock = new ReentrantLock();
	
	/**
	 * Thread-safe singleton instantiation
	 * @return A singleton instance of this class 
	 */
	public static ConduitRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					instance = new ConduitRegistry();
				}
			}finally{
				initLock.unlock();
			}
		}
		return instance;
	}
	
	
	/**
	 * Scans the power network for a given coordinate and returns a list of power requests from 
	 * all power sinks on that network.
	 * @param w The world instance for this dimension
	 * @param coord The block asking for power requests
	 * @param type the type of energy being offered
	 * @return A list of PowerRequest instances, sorted in order of highest priority to lowest 
	 * priority. 
	 */
	public List<PowerRequest> getRequestsForPower(World w, BlockPos coord, ConduitType type){
		List<PowerRequest> requests = new ArrayList<>();
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		BlockPos4D bp = new BlockPos4D(w.provider.getDimensionId(), coord);
		if(!manager.isValidatedNetwork(bp)){
			manager.revalidate(bp, w, type);
		}
		List<BlockPos4D> net = manager.getNetwork(bp);
		for(BlockPos4D pos : net){
			Block b = w.getBlockState(pos.pos).getBlock();
			if(b  instanceof ITileEntityProvider ){
				TileEntity e = w.getTileEntity(pos.pos);
				if(e != null && e instanceof PoweredEntity && ((ITypedConduit)e).isPowerSink()){
					PowerRequest req = ((PoweredEntity)e).getPowerRequest(type);
					if(req != PowerRequest.REQUEST_NOTHING)requests.add(req);
				}
			}
		}
		Collections.sort(requests);
		return requests;
	}
	/**
	 * Invoke this method anytime a conduit block enters the world
	 * @param w The world instance for this dimension
	 * @param dimension The ID number for this dimension
	 * @param location The position of the block being added
	 * @param type The native energy type of the added conduit block
	 */
	public void conduitBlockPlacedEvent(World w, int dimension, BlockPos location, ConduitType type){
		if(w.isRemote)return; // ignore client-side
		BlockPos4D coord = new BlockPos4D(dimension, location);
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			manager.invalidate(n);
		}
	}

	/**
	 * Invoke this method anytime a conduit block is removed from the world
	 * @param w The world instance for this dimension
	 * @param dimension The ID number for this dimension
	 * @param location The position of the block being removed
	 * @param type The native energy type of the removed conduit block
	 */
	public void conduitBlockRemovedEvent(World w, int dimension, BlockPos location, ConduitType type){
		if(w.isRemote)return; // ignore client-side
		BlockPos4D coord = new BlockPos4D(dimension, location);
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			manager.invalidate(n);
		}
	}
	
	


	private ConduitNetworkManager getConduitNetworkManager(ConduitType type) {
		if(networkManagers.containsKey(type)){
			return networkManagers.get(type);
		} else {
			ConduitNetworkManager manager = new ConduitNetworkManager(type);
			networkManagers.put(type, manager);
			return manager;
		}
	}
	
	
	
}
