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
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.math.BlockPos4D;

public class ConduitNetworkManager {
	// TODO: delete unused utility classes and functions
	
	private final Map<BlockPos4D,List<BlockPos4D>> networkCache; // Note that calling Map.values() will return duplicate references (store result in a Set<> to remove duplicates)
	private final ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();
	private final ConduitType type;
	
	public ConduitNetworkManager(ConduitType type){
		this.type = type;
		this.networkCache = new HashMap<>();
	}
	
	

	protected void removeNetwork(List<BlockPos4D> net){
		lock.writeLock().lock();
		try{
			FMLLog.info("deleting network"+java.util.Arrays.toString(net.toArray())); // TODO: remove debug code
			for(BlockPos4D n : net){
				networkCache.remove(n);
			}
			net.clear();
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	protected void addBlockToNetwork(BlockPos4D netBlock, BlockPos4D newBlock){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = networkCache.get(netBlock);
			FMLLog.info("adding block "+newBlock+" to network "+(net == null ? "null" : java.util.Arrays.toString(net.toArray()))); // TODO: remove debug code
			if(net == null){
				net = createNewNetwork(netBlock);
			}
			net.add(newBlock);
			networkCache.put(newBlock, net);
			FMLLog.info("   new network: "+java.util.Arrays.toString(net.toArray())); // TODO: remove debug code
		}finally{
			lock.writeLock().unlock();
		}
	} 
	
	protected List<BlockPos4D> createNewNetwork(BlockPos4D netBlock){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = new ArrayList<>();
			net.add(netBlock);
			networkCache.put(netBlock, net);
			FMLLog.info("created new network for "+netBlock); // TODO: remove debug code
			return net;
		}finally{
			lock.writeLock().unlock();
		}
	} 
	
	public void invalidate(BlockPos4D coord){
		lock.writeLock().lock();
		try{
			FMLLog.info("invalidating network at "+coord); // TODO: remove debug code
			List<BlockPos4D> net = networkCache.get(coord);
			if(net == null) return;
			removeNetwork(net);
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	public void revalidate(BlockPos4D coord, World w, ConduitType type){
		lock.writeLock().lock();
		try{
			FMLLog.info("revalidating network at "+coord); // TODO: remove debug code
			invalidate(coord);
			FMLLog.info("scanning for "+type+" network from "+coord+"..."); // TODO: remove debug code
			recursiveScan(w,coord,type);
			FMLLog.info("...scan done"); // TODO: remove debug code
		}finally{
			lock.writeLock().unlock();
		}
	}
	
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
	
	protected void recursiveScan(World w, BlockPos4D coord, ConduitType type){
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			if(areInSameNetwork(coord,n)) {continue;}
			Block block = w.getBlockState(n.pos).getBlock();
			if(block instanceof ITypedConduit && ConduitType.areConnectable(w,coord.pos,face)){
				addBlockToNetwork(coord, n);
				recursiveScan(w,n,type);
			}
		}
	}
	
	public boolean isValidatedNetwork(BlockPos4D coord){
		lock.readLock().lock();
		try{
			return networkCache.get(coord) != null;
		}finally{
			lock.readLock().unlock();
		}
	}
	public List<BlockPos4D> getNetwork(BlockPos4D coord){
		lock.readLock().lock();
		try{
			FMLLog.info("getting network at "+coord); // TODO: remove debug code
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
