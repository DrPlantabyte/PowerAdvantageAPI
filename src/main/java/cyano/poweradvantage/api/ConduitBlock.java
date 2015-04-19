package cyano.poweradvantage.api;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
/**
 * This is the superclass for blocks that conduct power from power generators to 
 * the machines that need power. If you are making an add-on mod, you probably 
 * want to extend the 
 * <b>cyano.poweradvantage.api.simple.BlockSimplePowerConduit</b> class 
 * instead of this class.
 * @author DrCyano
 *
 */
public abstract class ConduitBlock extends net.minecraft.block.Block implements  ITypedConduit{
	/**
	 * Block constructor
	 * @param mat Block material
	 */
	protected ConduitBlock(Material mat) {
		super(mat);
	}
	
	@Override
	public void onBlockAdded(World w, BlockPos coord, IBlockState state){
		super.onBlockAdded(w, coord, state);
		FMLLog.info("conduit block added at "+coord); // TODO: remove debug code
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(w, w.provider.getDimensionId(), coord, getType());
	}
	

	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		FMLLog.info("conduit block destroyed by player at "+coord); // TODO: remove debug code
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}

	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		FMLLog.info("conduit block blown-up at "+coord); // TODO: remove debug code
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}
	
}
