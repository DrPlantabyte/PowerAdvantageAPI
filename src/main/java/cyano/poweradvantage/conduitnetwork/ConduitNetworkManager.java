package cyano.poweradvantage.conduitnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.math.BlockPos4D;
import cyano.poweradvantage.util.ReversibleHashMap;
import cyano.poweradvantage.util.ReversibleMap;

public class ConduitNetworkManager {
	private final ReversibleMap<BlockPos4D,Set<BlockPos4D>> registry;
	private final ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();
	private final ConduitType type;
	
	public ConduitNetworkManager(ConduitType type){
		this.type = type;
		registry = new ReversibleHashMap<BlockPos4D,Set<BlockPos4D>>();
	}
	
	private Set<BlockPos4D> getNetworkFromRegistry(BlockPos4D coordinate, boolean createNew){
		lock.readLock().lock();
		try{
			Set<BlockPos4D> net = registry.get(coordinate);
			if(createNew && net == null){
				net = new HashSet<BlockPos4D>();
				net.add(coordinate);
				lock.writeLock().lock();
				try{
					registry.put(coordinate, net);
					FMLLog.info("Made new network for "+coordinate+". Registry now has "+registry.size()+" networks"); // TODO: remove debug code
				}finally{
					lock.writeLock().unlock();
				}
			}
			return net;
		}finally{
			lock.readLock().unlock();
		}
	}
	
	private void addBlockNeighborsToNetworkRegistry(BlockPos4D src, BlockPos4D[] neighbors){
		lock.writeLock().lock();
		try{
			Set<BlockPos4D> net = getNetworkFromRegistry(src,true);
			for(int i = 0; i < neighbors.length; i++){
				if(neighbors[i] == null) continue;
				if(isAlreadyInRegistry(neighbors[i])){
					mergeNetworks(net,getNetworkFromRegistry(neighbors[i],false));
				}
				net.add(neighbors[i]);
				registry.put(neighbors[i], net);
				FMLLog.info("Added "+neighbors[i]+" to same network as "+src); // TODO: remove debug code
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	

	private void bulkAddBlocksToRegistryAsNewNetwork(BlockPos4D... blocks){
		lock.writeLock().lock();
		try{
			FMLLog.info("Bulk block add"); // TODO: remove debug code
			Set<BlockPos4D> net = new HashSet<>();
			for(int i = 0; i < blocks.length; i++){
				net.add(blocks[i]);
				registry.put(blocks[i], net);
				FMLLog.info("added "+blocks[i]); // TODO: remove debug code
			}
			FMLLog.info("Bulk add complete, registry now has "+registry.size()+" networks"); // TODO: remove debug code
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	private void addBlockNeighborToNetworkRegistry(BlockPos4D src, BlockPos4D neighbor){
		lock.writeLock().lock();
		try{
			Set<BlockPos4D> net = getNetworkFromRegistry(src,true);
			if(isAlreadyInRegistry(neighbor)){
				mergeNetworks(net,getNetworkFromRegistry(neighbor,false));
			}
			net.add(neighbor);
			registry.put(neighbor, net);
			FMLLog.info("added "+neighbor + " to same network as "+src); // TODO: remove debug code
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	private void removeNetworkFromRegistry(BlockPos4D src){
		lock.writeLock().lock();
		try{
			if(registry.containsKey(src)){
				Set<BlockPos4D> net = registry.get(src);
				for(BlockPos4D b : net){
					registry.remove(b);
				}
				registry.removeValue(net);
				FMLLog.info("Removed network for block "+src+" from conduit registry. Registry now has "+registry.size()+" networks"); // TODO: remove debug code
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Adds a block to an existing network
	 * @param src Block in existing network
	 * @param neighbor Block to add to the network
	 */
	public void _addBlockToNetwork(BlockPos4D src, BlockPos4D neighbor){
		addBlockNeighborToNetworkRegistry(src,neighbor);
	}
	
	
	private void mergeNetworks(Set<BlockPos4D> net1, Set<BlockPos4D> net2){
		if(net1 == net2) return;
		lock.writeLock().lock();
		try{
			FMLLog.info("Merging two networks (sized "+net1.size()+" and "+net2.size()+")"); // TODO: remove debug code
			registry.removeValue(net2);
			net1.addAll(net2);
			for(BlockPos4D b : net2){
				registry.put(b, net1);
			}
			FMLLog.info("Merge complete"); // TODO: remove debug code
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	private boolean isAlreadyInRegistry(BlockPos4D b){
		lock.readLock().lock();
		try{
			return registry.containsKey(b);
		}finally{
			lock.readLock().unlock();
		}
	}


	public boolean isPartOfNetwork(BlockPos4D coord) {
		return isAlreadyInRegistry(coord);
	}

	public void _createNewNetwork(BlockPos4D coord) {
		getNetworkFromRegistry(coord,true);
	}

	public boolean _areInSameNetwork(BlockPos4D a, BlockPos4D b) {
		Set x = getNetworkFromRegistry(a,false);
		Set y = getNetworkFromRegistry(b,false);
		if(x == null) return false;
		return x == y;
	}

	public void deleteNetwork(BlockPos4D coord) {
		removeNetworkFromRegistry(coord);
	}
	
	public Set<BlockPos4D> getConnectedNetwork(BlockPos4D src){
		if(isAlreadyInRegistry(src)){
			return java.util.Collections.unmodifiableSet(getNetworkFromRegistry(src,false));
		}else{
			return Collections.EMPTY_SET;
		}
	}

	public void saveToNBT(NBTTagCompound network) {
		NBTTagCompound list = new NBTTagCompound();
		int size = 0;
		for(Set<BlockPos4D> blockNet : registry.values()){
			FMLLog.info("Saving network #"+size+" (size="+blockNet.size()+")"); // TODO: remove debug code
			int[] rawData = new int[blockNet.size() * 4];
			int i = 0;
			for(BlockPos4D block : blockNet){
				rawData[i++] = block.dimension;
				rawData[i++] = block.pos.getX();
				rawData[i++] = block.pos.getY();
				rawData[i++] = block.pos.getZ();
			}
			list.setTag(String.valueOf(size), new NBTTagIntArray(rawData));
			size++;
		}
		list.setInteger("count", size);
		if(size > 0)network.setTag("ConNet", list);
	}

	public void readFromNBT(NBTTagCompound network) {
		if(!network.hasKey("ConNet"))return;
		NBTTagCompound list = network.getCompoundTag("ConNet");
		int size = list.getInteger("count");
		FMLLog.info("found "+size+" networks in saved data"); // TODO: remove debug code
		for(int i = 0; i < size; i++){
			int[] net = list.getIntArray(String.valueOf(i));
			BlockPos4D[] blocks = new BlockPos4D[net.length/4];
			for(int n = 0; n < blocks.length; n++){
				blocks[n] = new BlockPos4D(net[n*4],net[n*4+1],net[n*4+2],net[n*4+3]);
			}
			this.bulkAddBlocksToRegistryAsNewNetwork(blocks);
		}
	}
	
	Map<BlockPos4D,List<BlockPos4D>> networkCache; // Note that calling Map.values() will return duplicate references (store result in a Set<> to remove duplicates)
	
	protected void addNetwork(List<BlockPos4D> net){
		lock.writeLock().lock();
		try{
			for(BlockPos4D n : net){
				networkCache.put(n, net);
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	

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
	
	protected List<BlockPos4D> createNewNetwork(BlockPos4D netBlock){
		lock.writeLock().lock();
		try{
			List<BlockPos4D> net = new ArrayList<>();
			networkCache.put(netBlock, net);
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
			recursiveScan(w,coord,type);
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
			FMLLog.info("invalidating network at "+coord); // TODO: remove debug code
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
