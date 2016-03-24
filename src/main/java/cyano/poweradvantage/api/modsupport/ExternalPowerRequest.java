package cyano.poweradvantage.api.modsupport;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import cyano.poweradvantage.api.PowerRequest;

/**
 * This derivation of the PowerRequest class is used for non-PowerAdvantage power sinks.
 * @author DrCyano
 *
 */
public class ExternalPowerRequest extends PowerRequest {

	public final TileEntity externalPowerAcceptor;
	public final BlockPos pos;
	/**
	 * Constructs a new request for an external machine block.
	 * @param requestSize Amount of energy requested
	 * @param source The source TileEntity (which does not extend PoweredEntity)
	 */
	public ExternalPowerRequest(float requestSize, TileEntity source) {
		super(0, requestSize, null);
		externalPowerAcceptor = source;
		pos = source.getPos();
	}

}
