package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.*;


public class FluidDrainTileEntity extends TileEntitySimpleFluidMachine {

	public FluidDrainTileEntity() {
		super( FluidContainerRegistry.BUCKET_VOLUME, FluidDrainTileEntity.class.getName());
	}

	
	
	
	
	///// Logic and implementation /////
	
	
	@Override
	public void powerUpdate(){
		// send fluid into pipes
		
		FluidTank tank = getTank();
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.down(), EnumFacing.UP);
//		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.north(), EnumFacing.SOUTH);
//		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.east(), EnumFacing.WEST);
//		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.south(), EnumFacing.NORTH);
//		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.west(), EnumFacing.EAST);
		fluidScan:{
			// pull fluid from above
			final EnumFacing[] cardinals = {EnumFacing.UP,EnumFacing.NORTH,EnumFacing.EAST,EnumFacing.SOUTH,EnumFacing.WEST,EnumFacing.DOWN};
			for(int k = 0; k < cardinals.length; k++){
				BlockPos space = this.pos.offset(cardinals[k]);
				// from fluid container
				if(getWorld().getBlockState(space).getBlock() instanceof ITileEntityProvider && getWorld().getTileEntity(space) instanceof IFluidHandler){
					IFluidHandler other = (IFluidHandler) getWorld().getTileEntity(space);
					FluidTankInfo[] tanks = other.getTankInfo(cardinals[k].getOpposite());
					for(int i = 0; i < tanks.length; i++){
						FluidTankInfo t = tanks[i];
						if((t.fluid == null) || (tank.getFluidAmount() > 0 && tank.getFluid().getFluid() != t.fluid.getFluid())){
							continue;
						}
						if(other.canDrain(cardinals[k].getOpposite(), t.fluid.getFluid())){
							FluidStack fluid = other.drain(cardinals[k].getOpposite(), tank.getCapacity() - tank.getFluidAmount(), true);
							tank.fill(fluid,true);
							break fluidScan;
						}
					}
				} 
			}
			// from fluid source block
			BlockPos space = this.pos.up();
			if(tank.getFluidAmount() <= 0){
				IBlockState bs = getWorld().getBlockState(space);
				if(bs.getBlock() instanceof BlockLiquid || bs.getBlock() instanceof IFluidBlock){
					Block block = bs.getBlock();
					Fluid fluid;
					if(block == Blocks.water || block == Blocks.flowing_water){
						// Minecraft fluid
						fluid = FluidRegistry.WATER;
					} else if(block == Blocks.lava || block == Blocks.flowing_lava){
						// Minecraft fluid
						fluid = FluidRegistry.LAVA;
					} else if(block instanceof IFluidBlock){
						fluid = ((IFluidBlock)block).getFluid();
					}else {
						// Minecraft fluid?
						fluid = FluidRegistry.lookupFluidForBlock(block);
					}
	
					// flowing minecraft fluid block
					Material m = block.getMaterial(bs);
					// is flowing block, follow upstream to find source block
					int limit = 16;
					BlockPos coord = this.pos.up();
					do{
						int Q = getFluidLevel(coord,fluid);
						if(Q == 100){
							// source block
							break;
						} else if(Q == 0){
							// non-liquid block (shouldn't happen)
							limit = 0;
							break;
						} else {
							if(getFluidLevel(coord.up(),fluid) > 0){
								// go up, regardless
								coord = coord.up();
								continue;
							}
							if(getFluidLevel(coord.north(),fluid) > Q){
								coord = coord.north();
							} else if(getFluidLevel(coord.east(),fluid) > Q){
								coord = coord.east();
							} else if(getFluidLevel(coord.south(),fluid) > Q){
								coord = coord.south();
							} else if(getFluidLevel(coord.west(),fluid) > Q){
								coord = coord.west();
							} else {
								// failed to find upstream block
								limit = 0;
							}
	
						}
						limit--;
					}while(limit > 0);
					if(getFluidLevel(coord,fluid) == 100){
						// found source block
						tank.fill(new FluidStack(fluid,FluidContainerRegistry.BUCKET_VOLUME), true);
						getWorld().setBlockToAir(coord);
						break fluidScan;
					}
	
				}
			}
		}
		super.powerUpdate();
	}
	
	private int getFluidLevel(BlockPos coord, Fluid fluid){
		Block fblock = fluid.getBlock();
		IBlockState state = getWorld().getBlockState(coord);
		Block b = state.getBlock();
		Block upBlock = getWorld().getBlockState(coord.up()).getBlock();
		if(b instanceof BlockLiquid && b.getMaterial(state) == fblock.getMaterial(state)){
			Integer L = (Integer)getWorld().getBlockState(coord).getValue(BlockDynamicLiquid.LEVEL);
			if(L == null) return 0;
			if(L == 0) return 100; // source block
			if(upBlock instanceof BlockLiquid && upBlock.getMaterial(state) == fblock.getMaterial(state)){
				return 99;
			}
			if(L < 8) {
				return (8 - L) * 10; // 1-7 are horizontal flow blocks with increasing value per decreasing level
			} else {
				return -1; // 8 or greator means vertical falling liquid blocks
			} 
		} else if(b instanceof BlockFluidClassic && ((IFluidBlock)b).getFluid() == fluid){
			if(((BlockFluidClassic)b).isSourceBlock(getWorld(), coord)){
				// is source block
				return 100;
			} else {
				// not source block
				if(upBlock instanceof IFluidBlock && ((IFluidBlock)upBlock).getFluid() == fluid){
					return 99;
				}
				return (int)(80 * ((IFluidBlock)b).getFilledPercentage(worldObj, coord));
			}
		} else if(b instanceof BlockFluidFinite && ((IFluidBlock)b).getFluid() == fluid){
			return ((BlockFluidFinite)b).getQuantaValue(getWorld(), coord);
		} else if(b instanceof IFluidBlock && ((IFluidBlock)b).getFluid() == fluid){
			if(((IFluidBlock)b).canDrain(getWorld(), coord)){
				// source block?
				return 100;
			} else if(upBlock instanceof IFluidBlock && ((IFluidBlock)upBlock).getFluid() == fluid){
				return 99;
			} else {
				return (int)(80 * ((IFluidBlock)b).getFilledPercentage(worldObj, coord));
			}
		}
		// non-liquid block (or wrong liquid)
		return 0;
	}
	
	private void tryPushFluid(BlockPos coord, EnumFacing otherFace){
		TileEntity e = getWorld().getTileEntity(coord);
		if(e instanceof IFluidHandler){
			IFluidHandler fh = (IFluidHandler)e;
			if(fh.canFill(otherFace, getTank().getFluid().getFluid())){
				getTank().drain(fh.fill(otherFace, getTank().getFluid(), true), true);
				this.sync();
			}
		}
	}
	
	
	
	
	public FluidStack getFluid(){
		if(getTank().getFluidAmount() <= 0) return null;
		return getTank().getFluid();
	}
	
	public int getFluidCapacity(){
		return getTank().getCapacity();
	}
	
	///// Synchronization /////
	@Override public void readFromNBT(NBTTagCompound root)
	{
		super.readFromNBT(root);
	}
	
	@Override public void writeToNBT(NBTTagCompound root)
	{
		super.writeToNBT(root);
	}

	/**
	 * Checks whether a given fluid is appropriate for this machine. For example, a water tank would return fallse for
	 * all Fluids except for <code>FluidRegistry.WATER</code>
	 *
	 * @param fluid The fluid to test
	 * @return True if this machine should accept this fluid type, false to reject it.
	 */
	@Override
	public boolean canAccept(Fluid fluid) {
		return true;
	}


	///// Boiler Plate /////
	
	private String customName = null;
	

	@Override
	protected ItemStack[] getInventory() {
		return new ItemStack[0];
	}
	
	
	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}




	public int getRedstoneOutput() {
		return this.getTank().getFluidAmount() * 15 / this.getTank().getCapacity();
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


	//////////
}
