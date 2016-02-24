package cyano.poweradvantage.machines.fluidmachines.modsupport;

import cyano.poweradvantage.init.Blocks;
import cyano.poweradvantage.machines.fluidmachines.FluidPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TerminalFluidPipeBlock extends FluidPipeBlock implements ITileEntityProvider{

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TerminalFluidPipeTileEntity();
	}


	@Override
	public boolean isPowerSink(){
		return true;
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
			if(numberOfAdjacentFluidHandlers(w,pos) == 0){
				w.setBlockState(pos, Blocks.fluid_pipe.getDefaultState());
			}
		}
	}
	
	
	
}
