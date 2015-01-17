package cyano.poweradvantage.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.EnumFacing;

public abstract class PowerSourceEntity extends PowerMachineEntity{
	

	/**
	 * Determine whether or not another conductor is allowed to add energy to  
	 * this conductor on the indicated face of this block. 
	 * @param blockFace The face of this block from which we are asking to push 
	 * energy. 
	 * @param requestType The energy type requested
	 * @return True if energy can be pulled from this face, false otherwise
	 */
	public boolean canPushEnergyTo(EnumFacing blockFace, ConductorType requestType){
		return false;
	}
	
	/**
	 * Implementation should add the request to a queue (most likely a HashMap) 
	 * so that this power source entity can distribute power to all requesting 
	 * sinks on powerUpdate()
	 * @param petitioner The power sink that wants power
	 * @param amount The amount of power requested
	 * @return Returns true is the request was accepted, false if the request 
	 * was rejected.
	 */
	public abstract boolean requestPower(PowerSinkEntity petitioner, float amount);
	
	
	// TODO: documentation
	@Override
	public void powerUpdate() {
		// do nothing, but implementations should handle power requests
	}
}
