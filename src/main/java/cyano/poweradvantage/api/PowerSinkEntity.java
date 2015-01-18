package cyano.poweradvantage.api;

import net.minecraft.util.EnumFacing;
/**
 * This is the superclass for machines that consume power. See also 
 * <b>cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer</b>
 * <p>
 * Powered machines are divided into two categories: Sources and Sinks. Both are 
 * types of Conductors, but Sources produce power (and therefore refuse to 
 * accept power from other conductors) and sinks consume power (and therefore do 
 * not share the power that they have). If you wanted to make a machine that does 
 * both, make is a sink that also pushes energy onto neighboring conductors in 
 * the powerUpdate() function.
 * </p>
 * @author DrCyano
 *
 */
public abstract class PowerSinkEntity extends PowerConductorEntity{

	/**
	 * Determine whether or not another conductor is allowed to withdraw energy 
	 * from this conductor on the indicated face of this block. 
	 * @param blockFace The face of this block from which we are asking to pull 
	 * energy. 
	 * @param requestType The energy type requested
	 * @return True if energy can be pulled from this face, false otherwise
	 */
	public boolean canPullEnergyFrom(EnumFacing blockFace, ConductorType requestType){
		return false;
	}
	/**
	 * This method is invoked when the block is placed using an item that has 
	 * been renamed. Implementations can carry the name over to the placed 
	 * block, but that feature is optional.
	 * @param newName The name of the item that was placed.
	 */
	public void setCustomInventoryName(String newName){
		// optional method
	}
}
