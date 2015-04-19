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
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.math.BlockPos4D;

public class ConduitRegistry {

	private final Map<ConduitType,ConduitNetworkManager> networkManagers = new HashMap<>();
	
	
	// TODO: save and load networks
	
	// thread-safe singleton instantiation
	private static ConduitRegistry instance = null;
	private static final Lock initLock = new ReentrantLock();
	
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
	
	public Collection<PoweredEntity> getConnectedNetwork(World w, BlockPos coord, ConduitType type){
		Set<PoweredEntity> entities = new HashSet<>();
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		Set<BlockPos4D> net = manager.getConnectedNetwork(new BlockPos4D(w.provider.getDimensionId(),coord));
		for(BlockPos4D pos : net){
			Block b = w.getBlockState(pos.pos).getBlock();
			if(b  instanceof ITileEntityProvider ){
				TileEntity e = w.getTileEntity(pos.pos);
				if(e != null && e instanceof PoweredEntity){
					entities.add((PoweredEntity)e);
				}
			}
		}
		return entities;
	}
	
	public List<PowerRequest> getRequestsForPower(World w, BlockPos coord, ConduitType type){
		List<PowerRequest> requests = new ArrayList<>();
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		Set<BlockPos4D> net = manager.getConnectedNetwork(new BlockPos4D(w.provider.getDimensionId(),coord));
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
	
	public void conduitBlockPlacedEvent(World w, int dimension, BlockPos location, ConduitType type){
		BlockPos4D coord = new BlockPos4D(dimension, location);
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		boolean noNetworks = true;
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			if(manager.areInSameNetwork(coord,n)) {continue;}
			Block block = w.getBlockState(n.pos).getBlock();
			if(block instanceof ITypedConduit && areConnectable(w,coord.pos,face)){
				manager.addBlockToNetwork(n, coord);
				noNetworks = false;
			}
		}
		if(noNetworks){
			manager.createNewNetwork(coord);
		}
	}
	
	public void conduitBlockRemovedEvent(World w, int dimension, BlockPos location, ConduitType type){
		BlockPos4D coord = new BlockPos4D(dimension, location);
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		// remove current network
		manager.deleteNetwork(coord);
		// rescan neighbors
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			recusiveScan(w,n,type,manager);
		}
	}
	
	private void scanForNetwork(World w, BlockPos4D coord, ConduitType type){
		ConduitNetworkManager manager = getConduitNetworkManager(type);
		if(!manager.isPartOfNetwork(coord)){
			manager.createNewNetwork(coord);
		}
		recusiveScan(w,coord,type,manager);
		
	}
	
	private void recusiveScan(World w, BlockPos4D coord, ConduitType type,ConduitNetworkManager manager){
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing face = EnumFacing.values()[i];
			BlockPos4D n = coord.offset(face);
			if(manager.areInSameNetwork(coord,n)) {continue;}
			Block block = w.getBlockState(n.pos).getBlock();
			if(block instanceof ITypedConduit && areConnectable(w,coord.pos,face)){
				manager.addBlockToNetwork(coord, n);
				recusiveScan(w,n,type,manager);
			}
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
	
	public void saveToNBT(NBTTagCompound packet){
		NBTTagCompound root = new NBTTagCompound();
		for(Map.Entry<ConduitType,ConduitNetworkManager> type : networkManagers.entrySet()){
			NBTTagCompound network = new NBTTagCompound();
			type.getValue().saveToNBT(network);
			root.setTag(type.getKey().toString(),network);
		}
		packet.setTag("ConduitRegistry",root);
	}
	

	public void loadFromNBT(NBTTagCompound packet){
		NBTTagCompound root = packet.getCompoundTag("ConduitRegistry");
		for(Object typeName : root.getKeySet()){
			String type = (String)typeName;
			NBTTagCompound network = root.getCompoundTag(type);
			ConduitNetworkManager manager = this.getConduitNetworkManager(new ConduitType(type));
			manager.readFromNBT(network);
		}
	}
	
	/**
	 * Determines whether a conduit block can interact with a neighboring (conduit) block 
	 * @param w The World instance
	 * @param B1 The block in question
	 * @param faceOnB1 The neighboring block, specified by direction
	 * @return Returns true if either the given block or its neighbor can accept the type of the 
	 * other block
	 */
	public static boolean areConnectable(World w, BlockPos B1, EnumFacing faceOnB1){
		Block a1 = w.getBlockState(B1).getBlock();
		Block a2 = w.getBlockState(B1.offset(faceOnB1)).getBlock();
		return areConnectable(a1,faceOnB1,a2);
	}
	/**
	 * Determines whether a conduit block can interact with a neighboring (conduit) block 
	 * @param a1 The block in question
	 * @param faceOnB1 The direction to the neighboring block (from B1)
	 * @param a2 The neighbor of the block in question
	 * @return Returns true if either the given block or its neighbor can accept the type of the 
	 * other block
	 */
	public static boolean areConnectable( Block a1, EnumFacing faceOnB1,  Block a2){
		if(a1 instanceof ITypedConduit && a2 instanceof ITypedConduit){
			return ((ITypedConduit)a1).canAcceptType(((ITypedConduit)a2).getType(), faceOnB1) || ((ITypedConduit)a2).canAcceptType(((ITypedConduit)a1).getType(), faceOnB1.getOpposite());
		}
		return false;
	}
}
