package cyano.poweradvantage.api.fluid;

import cyano.poweradvantage.api.ConduitBlock;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.block.material.Material;

/**
 * <p>
 * Fluids in PowerAdvantage are transmitted the same way as energy, except that the conduit type is 
 * <code>Fluids.fluidConduit_general</code> but the fluid sources ask the sinks to make 
 * PowerRequests based on the specific fluid provided by that fluid source. Thus fluid pump, tank, 
 * and fluid pipe blocks will all report to be of type <code>Fluids.fluidConduit_general</code> when 
 * you call <code>getType()</code>, but a water pump will poll the network with the "water" 
 * ConduitType and the tank will file a PowerRequest based on how much water it can hold.
 * </p><p>
 * You probably want to use the BlockSimpleFluidConduit class in the cyano.poweradvantage.api.simple 
 * package.
 * </p>
 * @author DrCyano
 *
 */
public abstract class FluidConduitBlock extends ConduitBlock{

	/**
	 * Block constructor
	 * @param mat Block material, typically Material.iron
	 */
	protected FluidConduitBlock(Material mat) {
		super(mat);
	}


	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 *
	 * @param connection A context object that provides the power type and block direction information
	 * @return True if the power type represents a fluid, false otherwise.
	 */
	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection) {
		return Fluids.isFluidType(connection.powerType);
	}


	private final ConduitType[] typeArray = {Fluids.fluidConduit_general};
	/**
	 * Fluid conduits return <code>Fluids.fluidConduit_general</code> as their type.
	 * @return returns <code>Fluids.fluidConduit_general</code>
	 */
	@Override
	public ConduitType[] getTypes() {
		return typeArray;
	}


}
