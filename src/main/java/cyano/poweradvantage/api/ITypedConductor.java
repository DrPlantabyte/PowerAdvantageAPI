package cyano.poweradvantage.api;
/**
 * Implement this method in every entity and block that interacts with energy. 
 * This ensures that all relevant Blocks and TileEntities can check the energy 
 * type of a Blocks or TileEntity. 
 * @author DrCyano
 *
 */
public interface ITypedConductor {
	
	/**
	 * Gets the energy type of this conductor. Most conductors can only interact 
	 * with other conductors of the same type. 
	 * @return The energy type of this conductor
	 */
	public abstract ConductorType getEnergyType();

}
