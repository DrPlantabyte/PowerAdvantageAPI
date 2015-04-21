package cyano.poweradvantage.fluids.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
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

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidSource;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;


public class FluidDrainTileEntity extends TileEntitySimpleFluidSource{

	public FluidDrainTileEntity() {
		super( FluidContainerRegistry.BUCKET_VOLUME, FluidDrainTileEntity.class.getName());
		// TODO fix fluid handling blocks/entities
	}

	
	
	
	
	///// Logic and implementation /////
	
	@Override
	public void powerUpdate(){
		// send fluid into pipes
		FMLLog.info("Drain: tank currently holds " 
		+ (getTank().getFluid() == null ? 0 
				: getTank().getFluidAmount()) 
				+ " units of " + (getTank().getFluid() == null 
				?"nothing": String.valueOf(
						getTank()
						.getFluid()
						.getFluid().getName()))
						);// TODO: remove debug code
		
		FluidTank tank = getTank();
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.down(), EnumFacing.UP);
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.north(), EnumFacing.SOUTH);
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.east(), EnumFacing.WEST);
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.south(), EnumFacing.NORTH);
		if(tank.getFluidAmount() > 0) tryPushFluid(this.pos.west(), EnumFacing.EAST);
		// pull fluid from above
		if(tank.getFluidAmount() <= 0){
			IBlockState bs = worldObj.getBlockState(this.pos.up());
			if(bs.getBlock() instanceof BlockLiquid || bs.getBlock() instanceof IFluidBlock){
				Block block = (BlockLiquid)bs.getBlock();
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
				Material m = block.getMaterial();
				// is flowing block, follow upstream to find source block
				int limit = 16;
				BlockPos coord = this.pos.up();
				do{
					int Q = getFluidLevel(coord,fluid);
					if(Q == 16){
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
						if(Q >= 8) Q = -1; // vertical block must be downstream
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
				if(getFluidLevel(coord,fluid) == 16){
					// found source block
					tank.fill(new FluidStack(fluid,FluidContainerRegistry.BUCKET_VOLUME), true);
					worldObj.setBlockToAir(coord);
					this.sync();
				}

			}
		}
		super.powerUpdate();
	}
	
	private int getFluidLevel(BlockPos coord, Fluid fluid){
		Block fblock = fluid.getBlock();
		Block b = worldObj.getBlockState(coord).getBlock();
		if(b instanceof BlockLiquid && b.getMaterial() == fblock.getMaterial()){
			Integer L = (Integer)worldObj.getBlockState(coord).getValue(BlockDynamicLiquid.LEVEL);
			if(L == null) return 0;
			if(L == 0) return 16; // source block
			if(L < 8) return 8 - L; // 1-7 are horizontal flow blocks with increasing value per decreasing level
			return 8; // 8 or greator means vertical falling liquid blocks
		} else if(b instanceof IFluidBlock && ((IFluidBlock)b).getFluid() == fluid){
			return (int)(16 * ((IFluidBlock)b).getFilledPercentage(worldObj, coord));
		}
		// non-liquid block (or wrong liquid)
		return 0;
	}
	
	private void tryPushFluid(BlockPos coord, EnumFacing otherFace){
		TileEntity e = worldObj.getTileEntity(coord);
		if(e instanceof IFluidHandler){
			IFluidHandler fh = (IFluidHandler)e;
			if(fh.canFill(otherFace, getTank().getFluid().getFluid())){
				getTank().drain(fh.fill(otherFace, getTank().getFluid(), true), true);
				this.sync();
			}
		}
	}
	
	
	public void sync(){
		// cause data update to be sent to client
		worldObj.markBlockForUpdate(getPos());
		this.markDirty();
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
	
	
	
	///// Boiler Plate /////
	
	private String customName = null;
	

	@Override
	protected ItemStack[] getInventory() {
		return new ItemStack[0];
	}
	
	// TODO: data field sync stuff
	@Override
	public int[] getDataFieldArray() {
		return new int[0];
	}

	@Override
	public void prepareDataFieldsForSync() {
		// do nothing
		
	}

	@Override
	public void onDataFieldUpdate() {
		// do nothing
		
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}









	
	
	

	
	
	//////////
}
