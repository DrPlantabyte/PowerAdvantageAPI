package cyano.poweradvantage.util;

import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerConnectorContext;

/**
 * Collection of utility methods
 * @author DrCyano
 *
 */
public abstract class PowerHelper {

	/**
	 * Checks if a connection is valid by looking at the two blocks and asking each if they will connect to the other.
	 * @param connection The connection to check
	 * @return True if the two blocks cen send power to (or through) eachother, false otherwise
	 */
	public static boolean areConnectable(PowerConnectorContext connection) {
		if(connection.connectorBlock.getBlock() instanceof ITypedConduit){
			if(connection.connecteeBlock.getBlock() instanceof ITypedConduit){
				// both blocks are Power Advantage conductors
				return ((ITypedConduit)connection.connectorBlock.getBlock()).canAcceptConnection(connection)
						&& ((ITypedConduit)connection.connecteeBlock.getBlock()).canAcceptConnection(connection.reverse());
			}
			// TODO: reimplement cross-mod support
		}
		return false;
	}
}
