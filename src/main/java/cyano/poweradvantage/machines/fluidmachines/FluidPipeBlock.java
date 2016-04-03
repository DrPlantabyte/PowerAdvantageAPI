package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.simple.BlockSimpleFluidConduit;
import cyano.poweradvantage.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidPipeBlock extends BlockSimpleFluidConduit{

	public FluidPipeBlock() {
		super(Material.piston, 0.75f, 4f/16f);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}
	

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World w, BlockPos pos, IBlockState state, Block neighborBlock){
		pipeCheck(w,pos);
	}
	
	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		pipeCheck(worldIn,pos);
	}

	private void pipeCheck(World w, BlockPos pos) {
		if(!w.isRemote){
			if(numberOfAdjacentFluidHandlers(w,pos) > 0){
				w.setBlockState(pos, Blocks.fluid_pipe_terminal.getDefaultState());
			}
		}
	}


	protected static int numberOfAdjacentFluidHandlers(World w, BlockPos pos){
		int sum = 0;
		for(EnumFacing f : EnumFacing.values()){
			TileEntity e = w.getTileEntity(pos.offset(f));
			if(e instanceof IFluidHandler && !(e instanceof cyano.poweradvantage.api.PoweredEntity)){
				sum++;
			}
		}
		return sum;
	}
}
