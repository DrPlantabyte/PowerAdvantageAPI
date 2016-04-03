package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
		// from fluid container
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
				int limit = 16;
				Material m = fluidBlock.getMaterial(fluidBlock.getDefaultState());// flowing minecraft fluid block
				if(fluidBlock instanceof BlockLiquid || fluidBlock instanceof IFluidBlock){
					do{
						while(coord.getY() > 0 && canPlace(coord.down(),fluid)){
							coord = coord.down();
						}
						int num = 0;
						BlockPos[] neighbors = new BlockPos[4];
						if(canPlace(coord.north(),fluid)){neighbors[num] = coord.north(); num++;}
						if(canPlace(coord.east(),fluid)){neighbors[num] = coord.east(); num++;}
						if(canPlace(coord.south(),fluid)){neighbors[num] = coord.south(); num++;}
						if(canPlace(coord.west(),fluid)){neighbors[num] = coord.west(); num++;}
						if(num == 0){
							break;
						}
						coord = neighbors[worldObj.rand.nextInt(num)];
						limit--;
					}while(limit > 0);
					if(canPlace(coord,fluid)){
						// not a source block
						worldObj.setBlockState(coord, fluidBlock.getDefaultState());
						worldObj.notifyBlockOfStateChange(coord, fluidBlock);
						this.drain(EnumFacing.DOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
					}
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
		if(b instanceof BlockLiquid && b.getMaterial(bs) == fluid.getBlock().getMaterial(bs)){
			Integer L = (Integer)worldObj.getBlockState(coord).getValue(BlockDynamicLiquid.LEVEL);
			return L != 0;
		} if(b instanceof IFluidBlock){
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
	
	@Override public void writeToNBT(NBTTagCompound root)
	{
		super.writeToNBT(root);
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
    public Packet getDescriptionPacket(){
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
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink() {
		return true;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource() {
		return false;
	}


	//////////
}
