package cyano.poweradvantage.api.fluid;

import net.minecraftforge.fluids.FluidContainerRegistry;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;

public class FluidRequest extends PowerRequest{

	public static final float BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;
	
	public static final FluidRequest REQUEST_NOTHING = new FluidRequest(LAST_PRIORITY,0,null);
	
	public FluidRequest(byte priority, int requestSize,
			PoweredEntity requestSource) {
		super(priority, requestSize, requestSource);
	}

}
