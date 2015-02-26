package cyano.poweradvantage.api;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
/**
 * This is the superclass for blocks that conduct power from power generators to 
 * the machines that need power. If you are making an add-on mod, you probably 
 * want to extend the 
 * <b>cyano.poweradvantage.api.simple.BlockSimplePowerConduit</b> class 
 * instead of this class.
 * @author DrCyano
 *
 */
public abstract class PowerConductorBlock extends net.minecraft.block.Block implements net.minecraft.block.ITileEntityProvider, ITypedConductor{
	/**
	 * Block constructor
	 * @param mat
	 */
	protected PowerConductorBlock(Material mat) {
		super(mat);
	}

	
	/**
	 * Determines whether this conductor block visually connects to a 
	 * neighboring conductor of a specified energy type. 
	 * @param energyType The type of energy of the neighbor
	 * @return true if this conductor should connect to the neighboring block, 
	 * false otherwise.
	 */
	public abstract boolean canConnectTo(ConductorType energyType);
	

	/**
     * Destroys the TileEntity associated with this block when this block 
     * breaks and drops its items.
     */
    @Override
    public void breakBlock(final World world, final BlockPos coord, final IBlockState bs) {
        final TileEntity tileEntity = world.getTileEntity(coord);
        if (tileEntity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, coord, (IInventory)tileEntity);
            world.updateComparatorOutputLevel(coord, this);
        }
        super.breakBlock(world, coord, bs);
    }
	
    /**
     * Adds tile entity on placement
     */
    @Override
    public boolean onBlockEventReceived(final World world, final BlockPos coord, final IBlockState blockState, 
    		final int i1, final int i2) {
        super.onBlockEventReceived(world, coord, blockState, i1, i2);
        final TileEntity tileEntity = world.getTileEntity(coord);
        return tileEntity != null && tileEntity.receiveClientEvent(i1, i2);
    }

}
