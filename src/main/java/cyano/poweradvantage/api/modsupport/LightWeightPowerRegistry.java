package cyano.poweradvantage.api.modsupport;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.IPowerMachine;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.FMLLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
		if(te instanceof IPowerMachine){
			return ((IPowerMachine)te).getPowerRequest(energyType).amount;
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
		if(te instanceof IPowerMachine){
			return ((IPowerMachine)te).addEnergy(amount, energyType);
		} else if(externalPowerSinks.containsKey(b) && externalPowerSinks.get(b).canAcceptEnergyType(energyType)){
			return externalPowerSinks.get(b).addEnergy(te, amount, energyType);
		}
		return 0;
		
	}

	
}
