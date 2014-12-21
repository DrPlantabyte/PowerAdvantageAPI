package cyano.poweradvantage.api;

import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
/**
 * This class is the super-class for all powered blocks. Wires are plain 
 * conductors, but generators and machines are also conductors (they should 
 * extend the PowerSourceEntity and PowerSinkEntity classes respectively). 
 * <p>
 * Conceptually, a conductor is any block that has an internal energy buffer, 
 * which can be added-to or subtracted-from by adjacent (conductor) blocks.
 * </p>
 * @author DrCyano
 *
 */
public abstract class PowerConductorEntity extends TileEntity implements IUpdatePlayerListBox{
	
	private final int powerUpdateInterval = 16;
	
	/**
	 * Gets the energy type of this conductor. Most conductors can only interact 
	 * with other conductors of the same type. 
	 * @return The energy type f this conductor
	 */
	public abstract ConductorType getEnergyType();
	
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
	 * Adds energy to this conductor, up to the maximum allowed energy. The 
	 * amount of energy that was actually added (or subtracted) to the energy 
	 * buffer is returned. 
	 * @param energy The amount of energy to add (can be negative to subtract 
	 * energy).
	 * @return The actual change to the internal energy buffer.
	 */
	public float addEnergy(float energy){
		float newValue = this.getEnergyBuffer() + energy;
		if(newValue < 0){
			float delta = -1 * this.getEnergyBuffer();
			this.setEnergy(0); 
			return delta;
		} else if(newValue > this.getEnergyBufferCapacity()){
			float delta = this.getEnergyBufferCapacity() - this.getEnergyBuffer();
			this.setEnergy(this.getEnergyBufferCapacity());
			return delta;
		}
		this.setEnergy(newValue);
		return energy;
	}
	/**
	 * Subtracts energy from the energy buffer and returns the actual change to 
	 * the energy buffer (which may not be as much as requested). This method 
	 * simply multiplies the input energy value by -1 and then calls the method 
	 * addEnergy(...).  
	 * @param energy The amount of energy to subtract.
	 * @return The actual change to the internal energy buffer
	 */
	public float subtractEnergy(float energy){
		return addEnergy(-1 * energy);
	}
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
	
	/**
	 * Method net.minecraft.server.gui.IUpdatePlayerListBox.update() is invoked 
	 * to do tick updates
	 */
	@Override
    public final void update() {
		// this method was moved from TileEntity to IUpdatePlayerListBox
		this.tickUpdate(this.isServer());
		if(this.isServer() && ((this.getWorld().getTotalWorldTime() + this.getTickOffset()) % powerUpdateInterval == 0)){
			this.powerUpdate();
		}
	}
	/**
	 * Called every tick to update. Note that tickUpdate happens before 
	 * powerUpdate().
	 * @param isServerWorld Will be true if the code is executing server-side, 
	 * and false if executing client-side 
	 */
	public abstract void tickUpdate(boolean isServerWorld);
	/**
	 * Updates the power status. This method is called avery few ticks and is 
	 * only called on the server world.
	 */
	public abstract void powerUpdate();
	/**
	 * Returns flase if this code is executing on the client and true if this 
	 * code is executing on the server
	 * @return true if on server world, false otherwise
	 */
	public boolean isServer(){
		return !this.getWorld().isRemote;
	}
	/**
	 * Returns a number that is used to spread the power updates in a chunk 
	 * across multiple ticks. Also keeps adjacent conductors from updating in 
	 * the same tick. 
	 * @return
	 */
	private final int getTickOffset(){
		BlockPos coord = this.getPos();
		int x = coord.getX();
		int y = coord.getX();
		int z = coord.getX();
		return ((z & 1) << 2) | ((x & 1) << 1) | ((y & 1) );
	}
}
