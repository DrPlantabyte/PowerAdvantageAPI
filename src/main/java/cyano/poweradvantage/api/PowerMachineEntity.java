package cyano.poweradvantage.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * This is the Super-Class for both PowerSinkEntity and PowerSourceEntity
 * @author DrCyano
 *
 */
public abstract class PowerMachineEntity extends PowerConductorEntity implements IPoweredTileEntity {
	/**
	 * This method is invoked when the block is placed using an item that has 
	 * been renamed. Implementations can carry the name over to the placed 
	 * block, but that feature is optional.
	 * @param newName The name of the item that was placed.
	 */
	public void setCustomInventoryName(String newName){
		// optional method
	}
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
	 * Reads data from NBT, which came from either a saved chunk or a network 
	 * packet.
	 */
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if(tagRoot.hasKey("Energy")){
			float energy = tagRoot.getFloat("Energy");
			this.setEnergy(energy);
		}
	}
	/**
	 * Saves the state of this entity to an NBT for saving or synching across 
	 * the network.
	 */
	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setFloat("Energy", this.getEnergyBuffer());
	}
}
