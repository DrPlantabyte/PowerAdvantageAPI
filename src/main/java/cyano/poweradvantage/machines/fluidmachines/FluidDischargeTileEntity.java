package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;


public class FluidDischargeTileEntity extends TileEntitySimpleFluidMachine {


	
	public FluidDischargeTileEntity() {
		super( FluidContainerRegistry.BUCKET_VOLUME, FluidDischargeTileEntity.class.getName());
	}

	

	
	///// Logic and implementation /////
	private int oldLevel = -1;
	
	@Override
	public void powerUpdate(){
		// server-side
		FluidTank tank = getTank();
		// push fluid to below
		BlockPos space = this.pos.down();
		// to fluid container
		if(tank.getFluidAmount() > 0 && worldObj.getTileEntity(space) instanceof IFluidHandler){
			IFluidHandler other = (IFluidHandler) worldObj.getTileEntity(space);
			FluidTankInfo[] tanks = other.getTankInfo(EnumFacing.DOWN);
			for(int i = 0; i < tanks.length; i++){
				FluidTankInfo t = tanks[i];
				if(t.fluid != null && tank.getFluid().getFluid() != t.fluid.getFluid()){
					continue;
				}
				if(other.canFill(EnumFacing.UP, tank.getFluid().getFluid())){
					int amount = other.fill(EnumFacing.UP, tank.getFluid(), true);
					tank.drain(amount,true);
				}
			}
		} else 
		// place fluid block
		if(tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME){
			FluidStack fstack = tank.getFluid();
			Fluid fluid = fstack.getFluid();
			Block fluidBlock = fluid.getBlock();
			BlockPos coord = space;
			if(worldObj.isAirBlock(coord)){
				worldObj.setBlockState(coord, fluidBlock.getDefaultState());
				worldObj.notifyBlockOfStateChange(coord, fluidBlock);
				this.drain(EnumFacing.DOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
			} else if(worldObj.getBlockState(coord).getBlock() == fluidBlock){
				// follow the flow
				coord = scanFluidSpaceForNonsourceBlock(getWorld(),coord,fluid,32);

				if(coord != null){
					// not a source block
					worldObj.setBlockState(coord, fluidBlock.getDefaultState());
					worldObj.notifyBlockOfStateChange(coord, fluidBlock);
					this.drain(EnumFacing.DOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
				}
			}


		}
		if(this.getTank().getFluidAmount() != oldLevel){
			oldLevel = this.getTank().getFluidAmount();
			this.sync();
		}
		super.powerUpdate();
	}
	
	@Override
	public void tickUpdate(boolean isServer){
		// do nothing
	}
	
	
	private boolean canPlace(BlockPos coord, Fluid fluid){
		if(worldObj.isAirBlock(coord)) return true;
		IBlockState bs = worldObj.getBlockState(coord);
		Block b = bs.getBlock();
		if(fluid == FluidRegistry.WATER && b == Blocks.FLOWING_WATER){
			return true;
		} else if(fluid == FluidRegistry.LAVA && b == Blocks.FLOWING_LAVA){
			return true;
		} else if(b instanceof IFluidBlock && ((IFluidBlock)b).getFluid().equals(fluid)){
			return ((IFluidBlock)b).getFilledPercentage(worldObj, coord) < 1.0f;
		}
		return false;
	}
	
	
	
	
	public FluidStack getFluid(){
		if(tank.getFluidAmount() <= 0) return null;
		return tank.getFluid();
	}
	
	public int getFluidCapacity(){
		return tank.getCapacity();
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
     * Creates a NBT to send a synchronization update using this block's data 
     * field array (see <code>getDataFieldArray()</code>). Before this method 
     * executes, it calls <code>prepareDataFieldsForSync()</code>.
     * @return A synchronization NBT holding values from the 
     * <code>getDataFieldArray()</code> array
     */
    public NBTTagCompound createUpdateTag(){
    	NBTTagCompound nbtTag = new NBTTagCompound();
    	getTank().writeToNBT(nbtTag);
    	return nbtTag;
    }
    /**
     * Reads a synchronization update NBT and stores it in this blocks data 
     * field array (see <code>getDataFieldArray()</code>). After this method 
     * executes, it calls <code>onDataFieldUpdate()</code>.
     * @param nbtTag A synchronization NBT holding values from the 
     * <code>getDataFieldArray()</code> array
     */
    public void readUpdateTag(NBTTagCompound nbtTag){
    	getTank().readFromNBT(nbtTag);
    }

    /**
     * Turns the data field NBT into a network packet
     */
    @Override 
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound nbtTag = createUpdateTag();
    	return new SPacketUpdateTileEntity(this.pos, 0, nbtTag);
    }
    /**
     * Receives the network packet made by <code>getDescriptionPacket()</code>
     */
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
    	readUpdateTag(packet.getNbtCompound());
    }
	
	
	

	@Override
	protected ItemStack[] getInventory() {
		return new ItemStack[0];
	}

	


	@Override
	public boolean canAccept(Fluid f) {
		return true;
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
		return true;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return false;
	}


	//////////

	/**
	 * Checks whether this fluid machine should send out its fluid to other fluid machines
	 *
	 * @return true to send fluids to other machines
	 */
	@Override
	public boolean isFluidSource() {
		return false;
	}

	/**
	 * Checks whether this fluid machine should receive fluids from other fluid machines
	 *
	 * @return true to receive fluids from other machines
	 */
	@Override
	public boolean isFluidSink() {
		return true;
	}


	public static BlockPos scanFluidSpaceForNonsourceBlock(World w, BlockPos initial, Fluid fluid, int range){
		final EnumFacing[] dirs = {EnumFacing.DOWN,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.SOUTH,EnumFacing.EAST};
		BlockPos pos = initial;
		final BlockPos[] next = new BlockPos[5];
		final Block[] neighborBlocks = new Block[5];
		Block currentBlock = w.getBlockState(pos).getBlock();
		while (range > 0){
			if(!isFluidSourceBlock(currentBlock,fluid,w,pos)) return pos;
			int count = 0;
			for(int i = 0; i < 5; i++){
				BlockPos p = pos.offset(dirs[i]);
				Block bb = w.getBlockState(p).getBlock();
				if(bb == Blocks.AIR || isFluidBlock(bb,fluid)){
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
		return FluidDrainTileEntity.isFluidBlock(block,f);
	}

	public static boolean isFluidSourceBlock(Block block, Fluid f, World w, BlockPos p){
		return FluidDrainTileEntity.isFluidSourceBlock(block,f,w,p);
	}

	private static boolean areEqual(Object o1, Object o2) {
		if( o1 != null){
			return o1.equals(o2);
		} else {
			return o2 == null;
		}
	}
}
