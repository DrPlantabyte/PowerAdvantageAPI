package cyano.poweradvantage.api.modsupport;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import cyano.poweradvantage.api.PowerRequest;

// TODO: documentation
public class ExternalPowerRequest extends PowerRequest {

	public final TileEntity externalPowerAcceptor;
	public final BlockPos pos;
	public ExternalPowerRequest(float requestSize, TileEntity source) {
		super(0, requestSize, null);
		externalPowerAcceptor = source;
		pos = source.getPos();
	}

}
