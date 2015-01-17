package cyano.poweradvantage.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class PowerSinkEntity extends PowerMachineEntity{

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

	// TODO: documentation
	@Override
	public void powerUpdate() {
		super.powerUpdate();
		// implementations should handle power requests
	}
	
	

    /** Maps power-source to power-limit */
	private Set<PowerSourceEntity> powerSources = new HashSet<PowerSourceEntity>();
	@Override
	public Set<PowerSourceEntity> getKnownPowerSources() {
		return powerSources;
	}
}
