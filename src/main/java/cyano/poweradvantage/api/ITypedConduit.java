package cyano.poweradvantage.api;

/**
 * Implement this method in every TileEntity and Block that interacts with energy. 
 * This ensures that all relevant Blocks and TileEntities can check the energy 
 * type of a Blocks or TileEntity. 
 * @author DrCyano
 *
 */
public interface ITypedConduit {

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param connection A context object that provides the power type and block direction information
	 * @return True if power should be allowed to flow through this connection, false otherwise
	 */
	public abstract boolean canAcceptConnection(PowerConnectorContext connection);
	
	/**
	 * Gets the energy type(s) of this conduit. Most conduits can only interact
	 * with other conduits of the same type. 
	 * @return The energy type(s) of this conduit
	 */
	public abstract ConduitType[] getTypes();
	/**
	 * Determines whether this block/entity should receive energy. If this is not a sink, then it 
	 * will never be given power by a power source. 
	 * @return true if this block/entity should receive energy
	 */
	public abstract boolean isPowerSink();
	/**
	 * Determines whether this block/entity can provide energy. 
	 * @return true if this block/entity can provide energy
	 */
	public abstract boolean isPowerSource();

}
