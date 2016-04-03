package cyano.poweradvantage.api.fluid;

import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;

/**
 * A FluidRequest is a specialized PowerRequest.
 * @author DrCyano
 *
 */
public class FluidRequest extends PowerRequest{
	/**
	 * Amount representing a single full bucket or block of fluid
	 */
	public static final float BUCKET_VOLUME = 1000; // container registry is going to be removed from Forge
	/**
	 * Return this to say "no thanks"
	 */
	public static final FluidRequest REQUEST_NOTHING = new FluidRequest(LAST_PRIORITY,0,null);
	
	/**
	 * Standard constructor for making a fluid power request
	 * @param priority Priority level (determines order of reception). If demand exceeds 
	 * supply, lower priority machines receive no fluid.
	 * @param requestSize How much to ask for of the given type.
	 * @param requestSource The TileEntity that created this FluidRequest
	 */
	public FluidRequest(byte priority, int requestSize,
			PoweredEntity requestSource) {
		super(priority, requestSize, requestSource);
	}

	
	/**
	 * Standard constructor for making a fluid power request
	 * @param priority Priority level (determines order of reception). If demand exceeds 
	 * supply, lower priority machines receive no fluid.
	 * @param requestSize How much to ask for of the given type.
	 * @param requestSource The TileEntity that created this FluidRequest
	 */
	public FluidRequest(int priority, int requestSize,
			PoweredEntity requestSource) {
		this((byte)priority, requestSize, requestSource);
	}
}
