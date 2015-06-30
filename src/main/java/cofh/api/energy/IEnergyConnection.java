package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on TileEntities which should connect to energy transportation blocks. This is intended for blocks which generate energy but do not
 * accept it; otherwise just use IEnergyHandler.
 * <p>
 * 
 * @author King Lemming
 * 
 */
public interface IEnergyConnection {

	/**
	 * Returns TRUE if the TileEntity can connect on a given side.
	 * @param facing
	 *            Orientation the energy is received from.
	 * @return true if can connect
	 */
	boolean canConnectEnergy(EnumFacing facing);

}
