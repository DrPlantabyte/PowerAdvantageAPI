package cyano.poweradvantage.fluids.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;

public class FluidDrainBlock extends GUIBlock{
// TODO: add new creative tab
	public FluidDrainBlock() {
		super(Material.iron);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		return new FluidDrainTileEntity(w,m);
	}
	

	/**
	 * Override of default block behavior
	 */
    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(this);
    }
    /**
     * Destroys the TileEntity associated with this block when this block 
     * breaks.
     */
    @Override
    public void breakBlock(final World world, final BlockPos coord, final IBlockState bs) {
        final TileEntity tileEntity = world.getTileEntity(coord);
        if (tileEntity instanceof TileEntitySimplePowerSource) {
            InventoryHelper.dropInventoryItems(world, coord, (IInventory)tileEntity);
            world.updateComparatorOutputLevel(coord, this);
        }
        super.breakBlock(world, coord, bs);
    }
    
    // TODO: redstone output
    
    /**
     * (Client-only) Override of default block behavior
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World world, final BlockPos coord) {
        return Item.getItemFromBlock(this);
    }
}
