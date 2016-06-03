package cyano.poweradvantage.machines.fluidmachines.modsupport;

import cyano.poweradvantage.init.Blocks;
import cyano.poweradvantage.machines.fluidmachines.FluidPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Random;

public class TerminalFluidPipeBlock extends FluidPipeBlock implements ITileEntityProvider{

	private final Block parentBlock;

	public TerminalFluidPipeBlock(Block parent){
		this.parentBlock = parent;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(parentBlock);
	}

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
	
	/**
	 * This method determines whether to connect to a neighboring block. 
	 * Override this method to change block connection behavior. 
	 * @param w World instance
	 * @param thisBlock The block that is checking its neighbor
	 * @param bs Block state of this block
	 * @param face The face on the first block through which the connection would happen
	 * @param otherBlock Coordinate of neighboring block
	 * @return Default implementation: true if the neighboring block implements 
	 * ITypedConductor and has the same energy type as this block. Overriding 
	 * the canConnectTo(ConductorType) method will change the results of this 
	 * method.
	 */
	@Override
	protected boolean canConnectTo(IBlockAccess w, BlockPos thisBlock, IBlockState bs, EnumFacing face, BlockPos otherBlock){
		return super.canConnectTo(w, thisBlock, bs, face, otherBlock) || w.getTileEntity(otherBlock) instanceof IFluidHandler;
	}
	
	
	
}
