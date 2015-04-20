package cyano.poweradvantage.api.fluid;

import net.minecraftforge.fluids.FluidContainerRegistry;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;

public class FluidRequest extends PowerRequest{

	public static final float BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;
	
	public FluidRequest(byte priority, float requestSize,
			PoweredEntity requestSource) {
		super(priority, requestSize, requestSource);
	}

}
