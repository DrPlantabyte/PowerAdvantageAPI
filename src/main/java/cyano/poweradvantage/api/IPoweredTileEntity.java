package cyano.poweradvantage.api;

import net.minecraft.util.EnumFacing;

public interface IPoweredTileEntity {
	/**
	 * Gets the amount of energy that can be stored in this conductor.
	 * @return Size of the energy buffer
	 */
	public abstract float getEnergyBufferCapacity();
	/**
	 * Gets the amount of energy stored in this conductor
	 * @return The amount of energy in the buffer
	 */
	public abstract float getEnergyBuffer();
	/**
	 * Sets the amount of energy in the buffer
	 * @param energy The energy to be added to the buffer
	 */
	public abstract void setEnergy(float energy); 
	
	/**
	 * Determine whether or not another conductor is allowed to withdraw energy 
	 * from this conductor on the indicated face of this block. 
	 * @param blockFace The face of this block from which we are asking to pull 
	 * energy. 
	 * @param requestType The energy type requested
	 * @return True if energy can be pulled from this face, false otherwise
	 */
	public abstract boolean canPullEnergyFrom(EnumFacing blockFace, ConductorType requestType);
	/**
	 * Determine whether or not another conductor is allowed to add energy to  
	 * this conductor on the indicated face of this block. 
	 * @param blockFace The face of this block from which we are asking to push 
	 * energy. 
	 * @param requestType The energy type requested
	 * @return True if energy can be pulled from this face, false otherwise
	 */
	public abstract boolean canPushEnergyTo(EnumFacing blockFace, ConductorType requestType);
	
}
