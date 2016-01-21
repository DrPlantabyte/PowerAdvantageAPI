package cyano.poweradvantage.util;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * Collection of utility methods
 * @author DrCyano
 *
 */
public abstract class PowerHelper {
	
	/**
	 * Determines whether a conduit block can interact with a neighboring (conduit) block 
	 * @param w The World instance
	 * @param B1 The block in question
	 * @param faceOnB1 The neighboring block, specified by direction
	 * @return Returns true if either the given block or its neighbor can accept the type of the 
	 * other block
	 */
	public static boolean areConnectable(IBlockAccess w, BlockPos B1, EnumFacing faceOnB1){
		Block a1 = w.getBlockState(B1).getBlock();
		Block a2 = w.getBlockState(B1.offset(faceOnB1)).getBlock();
		if(PowerAdvantage.enableExtendedModCompatibility){
			if(a1 instanceof ITypedConduit){
				if(a2 instanceof ITypedConduit){
					return areConnectable(a1,faceOnB1,a2);
				} else if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(a2)){
					return LightWeightPowerRegistry.getInstance().canAcceptType(a2, ((ITypedConduit)a1).getType());
				}
			}else if(a2 instanceof ITypedConduit){
				if(a1 instanceof ITypedConduit){
					return areConnectable(a1,faceOnB1,a2);
				} else if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(a1)){
					return LightWeightPowerRegistry.getInstance().canAcceptType(a1, ((ITypedConduit)a2).getType());
				}
			}
			return false;
		}
		return areConnectable(a1,faceOnB1,a2);
	}
	/**
	 * Determines whether a conduit block can interact with a neighboring (conduit) block 
	 * @param a1 The block in question
	 * @param faceOnB1 The direction to the neighboring block (from B1)
	 * @param a2 The neighbor of the block in question
	 * @return Returns true if either the given block or its neighbor can accept the type of the 
	 * other block
	 */
	// TODO: switch to blockstates instead of blocks for conductivity
	public static boolean areConnectable( Block a1, EnumFacing faceOnB1,  Block a2){
		if(a1 instanceof ITypedConduit && a2 instanceof ITypedConduit){
			return ((ITypedConduit)a1).canAcceptType(((ITypedConduit)a2).getType(), faceOnB1) || ((ITypedConduit)a2).canAcceptType(((ITypedConduit)a1).getType(), faceOnB1.getOpposite());
		}
		return false;
	}

}
