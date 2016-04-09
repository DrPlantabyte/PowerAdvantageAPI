package cyano.poweradvantage.util;

import cofh.api.energy.IEnergyReceiver;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerConnectorContext;
import net.minecraft.block.ITileEntityProvider;
import reborncore.api.power.IEnergyInterfaceTile;

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
		if(connection.thisBlock.getBlock() instanceof ITypedConduit){
			if(connection.otherBlock.getBlock() instanceof ITypedConduit){
				// both blocks are Power Advantage conductors
				return ((ITypedConduit)connection.thisBlock.getBlock()).canAcceptConnection(connection)
						&& ((ITypedConduit)connection.otherBlock.getBlock()).canAcceptConnection(connection.reverse());
			} else if(connection.otherBlock.getBlock() instanceof ITileEntityProvider){
				if(PowerAdvantage.detectedRF
					&& PowerAdvantage.rfConversionTable.containsKey(connection.powerType)) {
					// RF cross-mod compatibility
					return connection.world.getTileEntity(connection.otherBlockPosition) instanceof IEnergyReceiver;
				}
				if(PowerAdvantage.detectedTechReborn
						&& PowerAdvantage.trConversionTable.containsKey(connection.powerType)) {
					// TR cross-mod compatibility
					return connection.world.getTileEntity(connection.otherBlockPosition) instanceof IEnergyInterfaceTile;
				}
			}
		}
		return false;
	}
}
