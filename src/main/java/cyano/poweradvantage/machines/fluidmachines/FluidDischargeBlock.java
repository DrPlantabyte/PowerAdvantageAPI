package cyano.poweradvantage.machines.fluidmachines;

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
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidConsumer;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;
import cyano.poweradvantage.init.ItemGroups;

public class FluidDischargeBlock extends BlockSimpleFluidConsumer{
	
	public FluidDischargeBlock() {
		super(Material.piston, 3f);
		super.setCreativeTab(ItemGroups.tab_powerAdvantage);
	}

	@Override
	public PoweredEntity createNewTileEntity(World w, int m) {
		return new FluidDischargeTileEntity();
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
    
    /**
     * (Client-only) Override of default block behavior
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World world, final BlockPos coord) {
        return Item.getItemFromBlock(this);
    }

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof FluidDischargeTileEntity){
			return ((FluidDischargeTileEntity)te).getRedstoneOutput();
		}
		return 0;
	}
}
