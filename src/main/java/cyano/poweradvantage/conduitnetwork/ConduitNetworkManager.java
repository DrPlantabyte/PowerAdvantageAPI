package cyano.poweradvantage.conduitnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry;
import cyano.poweradvantage.math.BlockPos4D;

/**
 * This class manages a global (trans-dimensional) cache of power networks of a single power type.
 * All methods in this class are thread safe via read-write locks
 * @author DrCyano
 *
 */
public class ConduitNetworkManager {
	
	private final Map<BlockPos4D,List<BlockPos4D>> networkCache; // Note that calling Map.values() will return duplicate references (store result in a Set<> to remove duplicates)
	private final ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();
	private final ConduitType type;
	/**
	 * Constructs a new network cache manager for a given energy type
	 * @param type The energy type in question
	 */
	public ConduitNetworkManager(ConduitType type){
		this.type = type;
		this.networkCache = new HashMap<>();
	}
	
	
	/**
	 * Removes a network from the cache by its object reference
	 * @param net A cached network you with to remove from the cache
	 */
	protected void removeNetwork(List<BlockPos4D> net){
		lock.writeLock().lock();
		try{
			for(BlockPos4D n : net){
				networkCache.remove(n);
			}
			net.clear();
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Adds a block to the same network as a previous block, creating a new network for the first 
	 * block if necessary
	 * @param netBlock A block that should already be part of a network (if not, that's okay, a new 
	 * one will be made for it)
	 * @param newBlock A block you want to add to the same network as <code>netBlock</code>
	 */
	protected void addBlockToNetwork(BlockPos4D netBlock, BlockPos4D newBlock){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = networkCache.get(netBlock);
			if(net == null){
				net = createNewNetwork(netBlock);
			}
			net.add(newBlock);
			networkCache.put(newBlock, net);
		}finally{
			lock.writeLock().unlock();
		}
	} 
	/**
	 * Creates a new network for a block
	 * @param netBlock Block that needs a new network
	 * @return The new cached network for that block
	 */
	protected List<BlockPos4D> createNewNetwork(BlockPos4D netBlock){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = new ArrayList<>();
			net.add(netBlock);
			networkCache.put(netBlock, net);
			return net;
		}finally{
			lock.writeLock().unlock();
		}
	} 
	/**
	 * finds the cached network for a block and deletes it (and removes references for all the 
	 * blocks on that network).
	 * @param coord The block coordinate to invalidate
	 */
	public void invalidate(BlockPos4D coord){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = networkCache.get(coord);
			if(net == null) return;
			removeNetwork(net);
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Invalidates the cached network at a block location and then rescans the world to make a new 
	 * cached network
	 * @param coord The block position that needs revalidating
	 * @param w The world instance for the same dimension as the coord
	 * @param type The energy type that is triggering the revalidation (should be the same type as 
	 * this manager)
	 */
	public void revalidate(BlockPos4D coord, World w, ConduitType type){
		lock.writeLock().lock();
		try{
			invalidate(coord);
			recursiveScan(w,coord,type);
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Checks if to blocks are in the same cached network
	 * @param a A block position
	 * @param b Another block position
	 * @return True if both are connected in a cached network, false otherwise
	 */
	public boolean areInSameNetwork(BlockPos4D a, BlockPos4D b){
		lock.readLock().lock();
		try{
			return networkCache.containsKey(a) 
					&& networkCache.containsKey(b)
					&& networkCache.get(a) == networkCache.get(b);
		}finally{
			lock.readLock().unlock();
		}
	}
	/**
	 * scans the world to make a new cached network
	 * @param w The world instance for the same dimension as the coord
	 * @param coord The block position to start scanning from
	 * @param type The type of energy that will flow throught the scanned network
	 */
	protected void recursiveScan(World w, BlockPos4D coord, ConduitType type){
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			if(areInSameNetwork(coord,n)) {continue;}
			Block block = w.getBlockState(n.pos).getBlock();
			if(block instanceof ITypedConduit && ConduitType.areConnectable(w,coord.pos,face)){
				addBlockToNetwork(coord, n);
				recursiveScan(w,n,type);
			} else if(PowerAdvantage.enableExtendedModCompatibility){
				if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(block) && ConduitType.areConnectable(w,coord.pos,face)){
					addBlockToNetwork(coord, n);
					recursiveScan(w,n,type);
				}
			}
		}
	}
	/**
	 * Checks if a given block is part of a cached network
	 * @param coord The block position to check
	 * @return true if this block is in a cached network, false otherwise
	 */
	public boolean isValidatedNetwork(BlockPos4D coord){
		lock.readLock().lock();
		try{
			return networkCache.get(coord) != null;
		}finally{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Gets all of the blocks in a cached network as a read-only list 
	 * @param coord One block in the network
	 * @return All blocks in the same network as the given coord
	 */
	public List<BlockPos4D> getNetwork(BlockPos4D coord){
		lock.readLock().lock();
		try{
			if(networkCache.containsKey(coord)){
				return Collections.unmodifiableList(networkCache.get(coord));
			} else {
				return Collections.EMPTY_LIST;
			}
		}finally{
			lock.readLock().unlock();
		}
	}
}
