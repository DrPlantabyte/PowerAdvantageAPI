package cyano.poweradvantage.conduitnetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConduitType;
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
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	

	private void bulkAddBlocksToRegistryAsNewNetwork(BlockPos4D... blocks){
		lock.writeLock().lock();
		try{
			Set<BlockPos4D> net = new HashSet<>();
			for(int i = 0; i < blocks.length; i++){
				net.add(blocks[i]);
				registry.put(blocks[i], net);
			}
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
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	private void removeNetworkFromRegistry(BlockPos4D src){
		lock.writeLock().lock();
		try{
			registry.remove(src);
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Adds a block to an existing network
	 * @param src Block in existing network
	 * @param neighbor Block to add to the network
	 */
	public void addBlockToNetwork(BlockPos4D src, BlockPos4D neighbor){
		addBlockNeighborToNetworkRegistry(src,neighbor);
	}
	
	
	private void mergeNetworks(Set<BlockPos4D> net1, Set<BlockPos4D> net2){
		if(net1 == net2) return;
		lock.writeLock().lock();
		try{
			registry.removeValue(net2);
			net1.addAll(net2);
			for(BlockPos4D b : net2){
				registry.put(b, net1);
			}
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

	public void createNewNetwork(BlockPos4D coord) {
		getNetworkFromRegistry(coord,true);
	}

	public boolean areInSameNetwork(BlockPos4D a, BlockPos4D b) {
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
		for(int i = 0; i < size; i++){
			int[] net = list.getIntArray(String.valueOf(i));
			BlockPos4D[] blocks = new BlockPos4D[net.length/4];
			for(int n = 0; n < blocks.length; n++){
				blocks[n] = new BlockPos4D(net[n*4],net[n*4+1],net[n*4+2],net[n*4+3]);
			}
			this.bulkAddBlocksToRegistryAsNewNetwork(blocks);
		}
	}
}
