package cyano.poweradvantage.api.modsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PoweredEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.FMLLog;

/**
 * <p>This class provides a place to register blocks from other mods as power consumers. Such mods are 
 * considered "external" power systems because their power implementation is not controlled by Power 
 * Advantage. </p>
 * <p>For example, you can add Power Advantage compatibility to an existing mod like this:</p><pre>
	if(Loader.isModLoaded("poweradvantage")){
		LightWeightPowerRegistry.registerLightWeightPowerAcceptor(Blocks.myMachine, 
				new ILightWeightPowerAcceptor(){

					public boolean canAcceptEnergyType(ConduitType powerType) {
						return ConduitType.areSameType(powerType, "steam") || ConduitType.areSameType(powerType, "electricity");
					}

					public float getEnergyDemand(TileEntity yourMachine,
							ConduitType powerType) {
						TileEntityMyMachine m = (TileEntityMyMachine)yourMachine;
						return m.getMaxEnergyStored() - m.getEnergyStored();
					}

					public float addEnergy(TileEntity yourMachine,
							float amountAdded, ConduitType powerType) {
						TileEntityMyMachine m = (TileEntityMyMachine)yourMachine;
						return m.receiveEnergy(amountAdded,true);
					}
			
		});
	}
</pre>
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

	/**
	 * Registers a block (and its associated TileEntity) as being able to receive power from the 
	 * Power Advantage power sources in spite of not extending the Power Advantage classes.
	 * @param machineBlock The block that has a machine that you want to receive power
	 * @param powerAcceptorImplementation Implemetnation of ILightWeightPowerAcceptor that allows 
	 * Power Advantage to ask the TileEntity associated with the given block how much power it wants 
	 * and how to add power to that TileEntity
	 */
	public static void registerLightWeightPowerAcceptor(Block machineBlock, ILightWeightPowerAcceptor powerAcceptorImplementation){
		FMLLog.info("Registered external power acceptor interfce for block "+machineBlock.getUnlocalizedName());
		getInstance().externalPowerSinks.put(machineBlock, powerAcceptorImplementation);
	}
	
	/**
	 * Determines whether a block usese power, whether from Power Advantage or from an external 
	 * power mod.
	 * @param w The World instance
	 * @param coord The BlockPos being tested
	 * @param type The type of power that the block might acccept
	 * @return True if the block is a Power Advantage machine or registered external machine that is 
	 * compatible with the provided power type.
	 */
	public boolean isPowerAcceptor(IBlockAccess w, BlockPos coord, ConduitType type){
		Block b = w.getBlockState(coord).getBlock();
		if(b instanceof ITypedConduit){
			return ((ITypedConduit)b).isPowerSink();
		}
		if(externalPowerSinks.containsKey(b)){
			return externalPowerSinks.get(b).canAcceptEnergyType(type);
		}
		return false;
	}
	/**
	 * Checks if a block is a registered external powered machine
	 * @param b The block to test
	 * @return true if the block has been registered as an external block that should be able to 
	 * receive power from Power Advantage, false otherwise.
	 */
	public boolean isExternalPowerBlock(Block b){
		return externalPowerSinks.containsKey(b);
	}
	
	/**
	 * Gets the amount of power that a machine block wants, whether it is a Power Advantage machine 
	 * or a registered external machine.
	 * @param w The world instance
	 * @param coord The coordinate of the machine to test
	 * @param energyType The type of energy being offered
	 * @return How much of the offered energy the target machine would like to receive.
	 */
	public float getRequestedPowerAmount(IBlockAccess w, BlockPos coord, ConduitType energyType){
		Block b = w.getBlockState(coord).getBlock();
		TileEntity te = w.getTileEntity(coord);
		if(te == null) return 0;
		if(te instanceof PoweredEntity){
			return ((PoweredEntity)te).getPowerRequest(energyType).amount;
		} else if(externalPowerSinks.containsKey(b) && externalPowerSinks.get(b).canAcceptEnergyType(energyType)){
			return externalPowerSinks.get(b).getEnergyDemand(te, energyType);
		}
		return 0;
	}
	
	/**
	 * Adds power to a machine block. This method is intended for external machines, but it also 
	 * works with Power Advantage machine blocks.
	 * @param w The world instance
	 * @param coord The location of the machine to add power to
	 * @param energyType The type of energy to add
	 * @param amount The amount of energy to add
	 * @return The amount of energy that the target machine actually added (may be less than the 
	 * amount provided)
	 */
	public float addPower(IBlockAccess w, BlockPos coord, ConduitType energyType, float amount){
		Block b = w.getBlockState(coord).getBlock();
		TileEntity te = w.getTileEntity(coord);
		if(te == null) return 0;
		if(te instanceof PoweredEntity){
			return ((PoweredEntity)te).addEnergy(amount, energyType);
		} else if(externalPowerSinks.containsKey(b) && externalPowerSinks.get(b).canAcceptEnergyType(energyType)){
			return externalPowerSinks.get(b).addEnergy(te, amount, energyType);
		}
		return 0;
		
	}
	
	/**
	 * Returns whether a block can connect to power sources of the provided type. The block can be 
	 * either a Power Advantage block or an external machine block.
	 * @param b A block
	 * @param energyType The type of power being connected
	 * @param face the face being connected
	 * @return true if the block can accept that kind of power, false otherwise.
	 */
	public boolean canAcceptType(IBlockState b, ConduitType energyType, EnumFacing face){
		if(b.getBlock()  instanceof ITypedConduit){
			return ((ITypedConduit)b.getBlock()).canAcceptType(b,energyType,face);
		} else if(externalPowerSinks.containsKey(b.getBlock())){
			return externalPowerSinks.get(b.getBlock()).canAcceptEnergyType(energyType,face);
		}
		return false;
	}
	
}
