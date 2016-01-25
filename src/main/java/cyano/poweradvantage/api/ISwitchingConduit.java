package cyano.poweradvantage.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This interface is used by switch blocks to allow them to disconnect and reconnect from the power 
 * network.
 * @author DrCyano
 *
 */
public interface ISwitchingConduit extends ITypedConduit {

	/**
	 * If this method returns true, then this block/entity will act like a normal conduit, but it 
	 * it returns false, then it will not conduct power through it.
	 * @param state The blockstate of this block
	 * @return True for power on, false for power off
	 */
	public abstract boolean canConduct(IBlockState state);
}
