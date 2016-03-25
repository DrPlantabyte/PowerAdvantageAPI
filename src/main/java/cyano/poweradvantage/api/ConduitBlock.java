package cyano.poweradvantage.api;

import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * 
 * This is the superclass for blocks that conduct power from power generators to 
 * the machines that need power. If you are making an add-on mod, you probably 
 * want to extend the 
 * <b>cyano.poweradvantage.api.simple.BlockSimplePowerConduit</b> class 
 * instead of this class.
 * <br><br>
 * Note that the conduit blocks do not have tile entities. They do not store data for energy 
 * transmission. Simply implementing the ITypedConduit interface adds them to the energy 
 * transmission algorithm that finds the connections between generators and machines. 
 * 
 * 
 * @author DrCyano
 *
 */
public abstract class ConduitBlock extends net.minecraft.block.Block implements  ITypedConduit{
	/**
	 * Block constructor
	 * @param mat Block material, typically Material.iron
	 */
	protected ConduitBlock(Material mat) {
		super(mat);
	}
	/**
	 * This method is called whenever the block is placed into the world
	 */
	@Override
	public void onBlockAdded(World w, BlockPos coord, IBlockState state){
		super.onBlockAdded(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(w, w.provider.getDimension(), coord, getTypes());
	}
	
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, getTypes());
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, getTypes());
	}
	
}
