package cyano.poweradvantage.api;

import net.minecraft.util.EnumFacing;

public abstract class PowerSinkEntity extends PowerConductorEntity{
	// TODO: implementation

	/**
	 * Determine whether or not another conductor is allowed to withdraw energy 
	 * from this conductor on the indicated face of this block. 
	 * @param blockFace The face of this block from which we are asking to pull 
	 * energy. 
	 * @param requestType The energy type requested
	 * @return True if energy can be pulled from this face, false otherwise
	 */
	public boolean canPullEnergyFrom(EnumFacing blockFace, ConductorType requestType){
		return false;
	}
}
