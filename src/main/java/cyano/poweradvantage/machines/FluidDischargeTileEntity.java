package cyano.poweradvantage.machines;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.collect.ImmutableMap;

import cyano.poweradvantage.api.simple.TileEntitySimpleFluidConsumer;


public class FluidDischargeTileEntity extends TileEntitySimpleFluidConsumer{


	
	public FluidDischargeTileEntity() {
		super( FluidContainerRegistry.BUCKET_VOLUME, FluidDischargeTileEntity.class.getName());
	}

	

	
	///// Logic and implementation /////
	private int oldLevel = -1;
	
	@Override
	public void powerUpdate(){
		// server-side
		FMLLog.info("Discharge: tank currently holds " 
		+ (getTank().getFluid() == null ? 0 
				: getTank().getFluidAmount()) 
		+ " units of " 
		+ (getTank().getFluid() == null 
		?"nothing": String.valueOf(getTank().getFluid().getFluid().getName())));// TODO: remove debug code
		FluidTank tank = getTank();
		// place fluid block
		if(tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME){
			FluidStack fstack = tank.getFluid();
			Fluid fluid = fstack.getFluid();
			Block fluidBlock = fluid.getBlock();
			BlockPos coord = this.pos.down();
			if(worldObj.isAirBlock(coord)){
				worldObj.setBlockState(coord, fluidBlock.getDefaultState());
				worldObj.notifyBlockOfStateChange(coord, fluidBlock);
				this.drain(EnumFacing.DOWN, FluidContainerRegistry.BUCKET_VOLUME, true);
			} else if(worldObj.getBlockState(coord).getBlock() == fluidBlock){
				// follow the flow
				int limit = 16;
				Material m = fluidBlock.getMaterial();// flowing minecraft fluid block
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
		Block b = worldObj.getBlockState(coord).getBlock();
		if(b instanceof BlockLiquid && b.getMaterial() == fluid.getBlock().getMaterial()){
			Integer L = (Integer)worldObj.getBlockState(coord).getValue(BlockDynamicLiquid.LEVEL);
			return L != 0;
		} if(b instanceof IFluidBlock){
			return ((IFluidBlock)b).getFilledPercentage(worldObj, coord) < 1.0f;
		}
		return false;
	}
	
	
	
	private String mapToString(ImmutableMap properties) {
		// TODO delete this method
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for(Object key : properties.keySet()){
			if(!first)sb.append(",");
			sb.append(String.valueOf(key)).append("=").append(String.valueOf(properties.get(key)));
			first = false;
		}
		sb.append("}");
		return sb.toString();
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
    	return new S35PacketUpdateTileEntity(this.pos, 0, nbtTag);
    }
    /**
     * Receives the network packet made by <code>getDescriptionPacket()</code>
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
    	readUpdateTag(packet.getNbtCompound());
    }
	
	
	

	@Override
	protected ItemStack[] getInventory() {
		return new ItemStack[0];
	}
	// TODO: clean-up data field stuff

	


	@Override
	public boolean canAccept(Fluid f) {
		return true;
	}
	
	

	
	
	//////////
}
