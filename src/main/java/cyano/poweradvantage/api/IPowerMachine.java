package cyano.poweradvantage.api;

/**
 * Tile entities that wish to create or receive power must implement this interface, though it is 
 * usually better to extend the PoweredEntity class instead of creating your own implementation for 
 * this interface. Note that the corresponding block class for the tile entity must implement the 
 * <code>ITypedConduit</code> interface or the power network managers will not be able to find the 
 * tile entity.
 * @author DrCyano
 *
 */
public interface IPowerMachine extends ITypedConduit {
	/**
	 * Gets the amount of energy that can be stored in this machine.
	 * @param energyType The type of energy being polled
	 * @return Size of the energy buffer
	 */
	public abstract float getEnergyCapacity(ConduitType energyType);
	/**
	 * Gets the amount of energy stored in this machine
	 * @param energyType The type of energy being polled
	 * @return The amount of energy in the energy buffer
	 */
	public abstract float getEnergy(ConduitType energyType);
	/**
	 * Sets the amount of energy in the buffer
	 * @param energy The amount of energy to be added to the buffer
	 * @param energyType The type of energy to be added to the buffer
	 */
	public abstract void setEnergy(float energy, ConduitType energyType); 
	/**
	 * Adds energy to this conductor, up to the maximum allowed energy. The 
	 * amount of energy that was actually added (or subtracted) to the energy 
	 * buffer is returned. 
	 * @param energy The amount of energy to add (can be negative to subtract 
	 * energy).
	 * @param energyType The type of energy to be added to the buffer
	 * @return The actual change to the internal energy buffer.
	 */
	public float addEnergy(float energy, ConduitType energyType);
	
	/**
	 * Subtracts energy from the energy buffer and returns the actual change to 
	 * the energy buffer (a negative number).
	 * @param energy The amount of energy to subtract (a positive number).
	 * @param energyType The type of energy to be subtracted to the buffer
	 * @return The actual change to the internal energy buffer
	 */
	public float subtractEnergy(float energy, ConduitType energyType);
	
	/**
	 * Specify how much energy this power sink wants from a power generator. If this tile entity is 
	 * not a sink, then simply return PowerRequest.REQUEST_NOTHING
	 * @param energyType The type of energy available upon request
	 * @return A PowerRequest instance indicated how much power you'd like to get
	 */
	public abstract PowerRequest getPowerRequest(ConduitType energyType);
}
