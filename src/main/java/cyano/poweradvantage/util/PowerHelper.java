package cyano.poweradvantage.util;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.FMLLog;

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
		IBlockState a1 = w.getBlockState(B1);
		IBlockState a2 = w.getBlockState(B1.offset(faceOnB1));
		if(PowerAdvantage.enableExtendedModCompatibility){
			if(a1.getBlock() instanceof ITypedConduit){
				if(a2.getBlock() instanceof ITypedConduit){
					return areConnectable(a1,faceOnB1,a2);
				} else if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(a2.getBlock())){
					return LightWeightPowerRegistry.getInstance().canAcceptType(a2, ((ITypedConduit)a1.getBlock()).getType(),faceOnB1.getOpposite());
				}
			} else if(a2.getBlock() instanceof ITypedConduit){
				if(a1.getBlock() instanceof ITypedConduit){
					return areConnectable(a1,faceOnB1,a2);
				} else if(LightWeightPowerRegistry.getInstance().isExternalPowerBlock(a1.getBlock())){
					return LightWeightPowerRegistry.getInstance().canAcceptType(a1, ((ITypedConduit)a2.getBlock()).getType(), faceOnB1);
				}
			}
			return false;
		}
		return areConnectable(a1,faceOnB1,a2);
	}
	/**
	 * Determines whether a conduit block can interact with a neighboring (conduit) block 
	 * @param a1 The block in question
	 * @param faceOnA1 The direction to the neighboring block (from B1)
	 * @param a2 The neighbor of the block in question
	 * @return Returns true if either the given block or its neighbor can accept the type of the 
	 * other block
	 */
	public static boolean areConnectable( IBlockState a1, EnumFacing faceOnA1,  IBlockState a2){
		if(a1.getBlock() instanceof ITypedConduit && a2.getBlock() instanceof ITypedConduit){
			return ((ITypedConduit)a1.getBlock()).canAcceptType(a1,((ITypedConduit)a2.getBlock()).getType(), faceOnA1) 
					&& ((ITypedConduit)a2.getBlock()).canAcceptType(a2,((ITypedConduit)a1.getBlock()).getType(), faceOnA1.getOpposite());
		}
		return false;
	}

}
