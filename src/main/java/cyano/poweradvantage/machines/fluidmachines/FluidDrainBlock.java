package cyano.poweradvantage.machines.fluidmachines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidSource;
import cyano.poweradvantage.init.ItemGroups;

public class FluidDrainBlock extends BlockSimpleFluidSource{
	
	
	public FluidDrainBlock() {
		super(Material.piston, 3f);
		super.setCreativeTab(ItemGroups.tab_powerAdvantage);
	}

	@Override
	public PoweredEntity createNewTileEntity(World w, int m) {
		return new FluidDrainTileEntity();
	}
	

	/**
	 * Override of default block behavior
	 */
    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(this);
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
		if(te != null && te instanceof FluidDrainTileEntity){
			return ((FluidDrainTileEntity)te).getRedstoneOutput();
		}
		return 0;
	}
}
