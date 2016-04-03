package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidMachine;
import cyano.poweradvantage.init.ItemGroups;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class FluidDrainBlock extends BlockSimpleFluidMachine {
	
	
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
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this);
	}

    @Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof FluidDrainTileEntity){
			return ((FluidDrainTileEntity)te).getRedstoneOutput();
		}
		return 0;
	}

	/**
	 * Determines whether this block/entity should receive energy. If this is not a sink, then it
	 * will never be given power by a power source.
	 *
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink() {
		return false;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource() {
		return true;
	}
}
