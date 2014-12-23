package cyano.poweradvantage.api;

import net.minecraft.util.EnumFacing;

public abstract class PowerSourceEntity extends PowerConductorEntity{
	// TODO: implementation
	

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
	
}
