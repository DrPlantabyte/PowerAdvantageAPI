package cyano.poweradvantage.api.fluid;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConduitBlock;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.init.Fluids;

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
	 * This default implementation assumes that all sides are valid for fluid input/output and that 
	 * this block does not handle power in addition to fluids. Override this method if that is not 
	 * true.
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	@Override
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace) {
		return canAcceptType(type);
	}

	/**
	 * This default implementation assumes that this block does not handle power in addition to 
	 * fluids. Override this method if that is not true.
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given type through one or more of its block faces, 
	 * false otherwise
	 */
	@Override
	public boolean canAcceptType(ConduitType type) {
		return ConduitType.areSameType(type, Fluids.fluidConduit_general) ;
	}

	/**
	 * Fluid conduits return <code>Fluids.fluidConduit_general</code> as their type.
	 * @return returns <code>Fluids.fluidConduit_general</code>
	 */
	@Override
	public ConduitType getType() {
		return Fluids.fluidConduit_general;
	}


}
