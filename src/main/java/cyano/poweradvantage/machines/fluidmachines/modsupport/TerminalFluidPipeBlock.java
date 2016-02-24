package cyano.poweradvantage.machines.fluidmachines.modsupport;

import cyano.poweradvantage.init.Blocks;
import cyano.poweradvantage.machines.fluidmachines.FluidPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
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
		if(!w.isRemote){
			if(numberOfAdjacentFluidHandlers(w,pos) == 0){
				w.setBlockState(pos, Blocks.fluid_pipe.getDefaultState());
			}
		}
	}
	
	
	
}
