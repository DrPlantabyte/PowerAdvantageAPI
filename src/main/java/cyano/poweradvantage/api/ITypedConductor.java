package cyano.poweradvantage.api;

public interface ITypedConductor {
	
	/**
	 * Gets the energy type of this conductor. Most conductors can only interact 
	 * with other conductors of the same type. 
	 * @return The energy type f this conductor
	 */
	public abstract ConductorType getEnergyType();

}
