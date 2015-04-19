package cyano.poweradvantage.fluids.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerSource;

public class FluidDrainBlock extends BlockSimplePowerSource{
// TODO: add new creative tab
	public FluidDrainBlock(int guiId, Object guiOwner) {
		super(Material.iron, 3f, new ConduitType("fluid"), guiId, guiOwner);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public PoweredEntity createNewTileEntity(World w, int m) {
		// TODO: fix unlocalized name business
		return new FluidDrainTileEntity("fix-me2");
	}
	

	/**
	 * Override of default block behavior
	 */
    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(this);
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


	@Override
	public boolean hasComparatorInputOverride() {
		return false;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		return 0;
	}
}
