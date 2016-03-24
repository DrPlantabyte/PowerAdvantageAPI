package cyano.poweradvantage.conduitnetwork;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.IPowerMachine;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.modsupport.ExternalPowerRequest;
import cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry;
import cyano.poweradvantage.api.modsupport.RFPowerRequest;
import cyano.poweradvantage.math.BlockPos4D;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	 * all power sinks on that network. If transmitting a subtype of enrgy (such as a type of 
	 * fluid), then conduitType is the super type (used for connecting conduits) and energyType is 
	 * the specific subtype being transmitted. 
	 * @param w The world instance for this dimension
	 * @param coord The block asking for power requests
	 * @param conduitType the type of energy transmission network (and type of energy) to poll. If 
	 * the conduit type and the type of energy energy being transmitted are different (e.g. sending 
	 * a subtype of energy), then use the 
	 * <code>getRequestsForPower(World, BlockPos, ConduitType, ConduitType)</code> method.
	 * @return A list of PowerRequest instances, sorted in order of highest priority to lowest 
	 * priority. 
	 */
	public List<PowerRequest> getRequestsForPower(World w, BlockPos coord, ConduitType conduitType){
		return getRequestsForPower( w,  coord,  conduitType, conduitType);
	}
	
	/**
	 * Scans the power network for a given coordinate and returns a list of power requests from 
	 * all power sinks on that network. If transmitting a subtype of enrgy (such as a type of 
	 * fluid), then conduitType is the super type (used for connecting conduits) and energyType is 
	 * the specific subtype being transmitted. 
	 * @param w The world instance for this dimension
	 * @param coord The block asking for power requests
	 * @param conduitType the type of energy transmission network to poll
	 * @param energyType the type of energy being offered (usually is the same as conduitType)
	 * @return A list of PowerRequest instances, sorted in order of highest priority to lowest 
	 * priority. 
	 */
	public List<PowerRequest> getRequestsForPower(World w, BlockPos coord, ConduitType conduitType, ConduitType energyType){
		List<PowerRequest> requests = new ArrayList<>();
		ConduitNetworkManager manager = getConduitNetworkManager(conduitType);
		BlockPos4D bp = new BlockPos4D(w.provider.getDimension(), coord);
		if(!manager.isValidatedNetwork(bp)){
			manager.revalidate(bp, w, conduitType);
		}
		List<BlockPos4D> net = manager.getNetwork(bp);
		for(BlockPos4D pos : net){
			Block b = w.getBlockState(pos.pos).getBlock();
			if(b  instanceof ITileEntityProvider ){
				TileEntity e = w.getTileEntity(pos.pos);
				if(e != null && e instanceof IPowerMachine && ((ITypedConduit)e).isPowerSink()){
					PowerRequest req = ((IPowerMachine)e).getPowerRequest(energyType);
					if(req != PowerRequest.REQUEST_NOTHING)requests.add(req);
				} else if(PowerAdvantage.enableExtendedModCompatibility){
					if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(b)){
						if(e != null ){
							PowerRequest req = new ExternalPowerRequest(LightWeightPowerRegistry.getInstance()
									.getRequestedPowerAmount(w, pos.pos, energyType),e);
							if(req.amount > 0){
								requests.add(req);
							}
						}
					} else if(PowerAdvantage.detectedRF && PowerAdvantage.rfConversionTable.containsKey(conduitType)){
						if(e instanceof cofh.api.energy.IEnergyReceiver){
							float RFDemand = getRFDemand((cofh.api.energy.IEnergyReceiver)e);
							if(RFDemand > 0){
								requests.add( new RFPowerRequest(RFDemand,(cofh.api.energy.IEnergyReceiver)e));
							}
						}
					}
				}
			}
		}
		Collections.sort(requests);
		return requests;
	}
	
	/**
	 * Sends the provided energy out to all machines requesting energy that are connected to the 
	 * given TileEntity. The amount of energy actually sent out is returned
	 * @param availableEnergy Maximum amount of energy to send
	 * @param powerType The type of energy being sent
	 * @param minimumPriority The lowest priority of power request that will be filled (prevents 
	 * battery machines from sending circular power)
	 * @param provider The source TileEntity sending the power
	 * @return The amount of energy that was actually consumed by the requests
	 */
	public static float transmitPowerToConsumers(final float availableEnergy, ConduitType powerType, byte minimumPriority,
			TileEntity provider){
		return transmitPowerToConsumers(availableEnergy, powerType,powerType,minimumPriority,provider.getWorld(),provider.getPos(),provider);
	}
	
	/**
	 * Sends the provided energy out to all machines requesting energy that are connected to the 
	 * given TileEntity. The amount of energy actually sent out is returned
	 * @param availableEnergy Maximum amount of energy to send
	 * @param networkType The power network to access (almost always the same as 
	 * <code>powerType</code>).
	 * @param powerType The type of energy being sent
	 * @param minimumPriority The lowest priority of power request that will be filled (prevents 
	 * battery machines from sending circular power)
	 * @param world World object instance
	 * @param srcPos A position within the power network that you wish to transmit power into. This 
	 * is usually the position of the tile entity of the generator machine.
	 * @param providerInstance The source sending the power (used to prevent the power source from 
	 * adding to itself, this parameter can be null).
	 * @return The amount of energy that was actually consumed by the requests
	 */
	public static float transmitPowerToConsumers(final float availableEnergy, ConduitType networkType, ConduitType powerType, byte minimumPriority,
			World world, BlockPos srcPos, Object providerInstance){
		List<PowerRequest> requests = ConduitRegistry.getInstance().getRequestsForPower(world, srcPos, networkType,powerType);
		float e = availableEnergy;
		for(PowerRequest req : requests){
			if(req.amount <= 0) continue;
			if(req.entity == providerInstance) continue;
			if(req.priority < minimumPriority) continue;
			if(PowerAdvantage.enableExtendedModCompatibility){
				if(req instanceof ExternalPowerRequest){
					e -= LightWeightPowerRegistry.getInstance().addPower(world, ((ExternalPowerRequest)req).pos, 
							powerType, Math.min(e, req.amount));
					if(e <= 0) break;
					continue;
				} else if(PowerAdvantage.detectedRF 
						&& PowerAdvantage.rfConversionTable.containsKey(powerType)
						&& req instanceof RFPowerRequest){
					int rf = (int)Math.min(req.amount, e * PowerAdvantage.rfConversionTable.get(powerType).floatValue());
					e -= ((RFPowerRequest)req).fillRequest(rf);
					if(e <= 0) break;
					continue;
				}
			}
			if(req.entity == null) continue;
			if(req.amount < e){
				e -= req.entity.addEnergy(req.amount,powerType);
			} else {
				req.entity.addEnergy(e,powerType);
				e = 0;
				break;
			}
		}
		return availableEnergy - e;
	}
	
	
	private static float getRFDemand(Object o){
		if(PowerAdvantage.detectedRF){
			if(o instanceof cofh.api.energy.IEnergyReceiver){
				cofh.api.energy.IEnergyReceiver er = (cofh.api.energy.IEnergyReceiver) o;
				for(EnumFacing f : EnumFacing.values()){
					if(er.canConnectEnergy(f)){
						return er.getMaxEnergyStored(f) - er.getEnergyStored(f);
					}
				}
			}
		}
		return 0;
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
		manager.invalidate(coord);
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
		manager.invalidate(coord);
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
