package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
				Block block = bs.getBlock();
				if(block instanceof BlockLiquid || block instanceof IFluidBlock){
					Fluid fluid = null;
					if(block == Blocks.WATER || block == Blocks.FLOWING_WATER){
						// Minecraft fluid
						fluid = FluidRegistry.WATER;
					} else if(block == Blocks.LAVA || block == Blocks.FLOWING_LAVA){
						// Minecraft fluid
						fluid = FluidRegistry.LAVA;
					} else if(block instanceof IFluidBlock){
						fluid = ((IFluidBlock)block).getFluid();
					}else {
						// Minecraft fluid?
						fluid = FluidRegistry.lookupFluidForBlock(block);
					}

					if(fluid != null){
						BlockPos srcPos = scanFluidSpaceForSourceBlock(getWorld(),space,fluid,32);
						if(srcPos != null) {
							// found source block
							tank.fill(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), true);
							getWorld().setBlockToAir(srcPos);
							break fluidScan;
						}
					}
	
				}
			}
		}
		super.powerUpdate();
	}

	
	private void tryPushFluid(BlockPos coord, EnumFacing otherFace){
		if(this.getTank().getFluidAmount() <= 0) return; // no fluid to push
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
	
	@Override public NBTTagCompound writeToNBT(NBTTagCompound root)
	{
		return super.writeToNBT(root);
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
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return false;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return true;
	}


	//////////


	/**
	 * Checks whether this fluid machine should send out its fluid to other fluid machines
	 *
	 * @return true to send fluids to other machines
	 */
	@Override
	public boolean isFluidSource() {
		return true;
	}

	/**
	 * Checks whether this fluid machine should receive fluids from other fluid machines
	 *
	 * @return true to receive fluids from other machines
	 */
	@Override
	public boolean isFluidSink() {
		return false;
	}


	public static BlockPos scanFluidSpaceForSourceBlock(World w, BlockPos initial, Fluid fluid, int range){
		final EnumFacing[] dirs = {EnumFacing.UP,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.SOUTH,EnumFacing.EAST};
		BlockPos pos = initial;
		final BlockPos[] next = new BlockPos[5];
		final Block[] neighborBlocks = new Block[5];
		Block currentBlock = w.getBlockState(pos).getBlock();
		while (range > 0){
			if(isFluidSourceBlock(currentBlock,fluid,w,pos)) return pos;
			int count = 0;
			for(int i = 0; i < 5; i++){
				BlockPos p = pos.offset(dirs[i]);
				Block bb = w.getBlockState(p).getBlock();
				if(isFluidBlock(bb,fluid)){
					next[count] = p;
					neighborBlocks[count] = bb;
					count++;
				}
			}
			if(count == 0) break; // dead end

			int r = (count < 2 ? 0 : w.rand.nextInt(count));
			pos = next[r];
			currentBlock = neighborBlocks[r];

			range--;
		}
		return null;
	}

	public static boolean isFluidBlock(Block block, Fluid f){
		if(f == FluidRegistry.WATER){
			return block.equals(Blocks.FLOWING_WATER) || block.equals(Blocks.WATER);
		} else if(f == FluidRegistry.LAVA){
			return block.equals(Blocks.FLOWING_LAVA) || block.equals(Blocks.LAVA);
		} else if(block instanceof IFluidBlock){
			IFluidBlock fb = (IFluidBlock)block;
			return areEqual(fb.getFluid(),f);
		} else {
			return false;
		}
	}

	public static boolean isFluidSourceBlock(Block block, Fluid f, World w, BlockPos p){
		if(f == FluidRegistry.WATER){
			return block.equals(Blocks.WATER) && w.getBlockState(p).getValue(BlockLiquid.LEVEL) == 0;
		} else if(f == FluidRegistry.LAVA){
			return block.equals(Blocks.LAVA) && w.getBlockState(p).getValue(BlockLiquid.LEVEL) == 0;
		} else if(block instanceof IFluidBlock){
			IFluidBlock fb = (IFluidBlock)block;
			return fb.canDrain(w,p);
		} else {
			return false;
		}
	}

	private static boolean areEqual(Object o1, Object o2) {
		if( o1 != null){
			return o1.equals(o2);
		} else {
			return o2 == null;
		}
	}
}
