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

import com.google.common.collect.ImmutableMap;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;


public class FluidDrainTileEntity extends TileEntitySimplePowerSource implements  IFluidHandler{

	public FluidDrainTileEntity(String unlocalizedName) {
		super(new ConduitType("fluid"), FluidContainerRegistry.BUCKET_VOLUME, unlocalizedName);
		// TODO fix fluid handling blocks/entities
	}

	public final FluidTank tank = new FluidTank( FluidContainerRegistry.BUCKET_VOLUME );
	
	
	
	
	///// Logic and implementation /////
	
	@Override
	public void powerUpdate(){
		// send fluid into pipes
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
			if(fh.canFill(otherFace, tank.getFluid().getFluid())){
				tank.drain(fh.fill(otherFace, tank.getFluid(), true), true);
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
		NBTTagCompound tankTag = root.getCompoundTag("Tank");
		tank.readFromNBT(tankTag);
		if(tankTag.hasKey("Empty")){
			// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
			tank.setFluid(null);
		}
	}
	
	@Override public void writeToNBT(NBTTagCompound root)
	{
		super.writeToNBT(root);
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		root.setTag("Tank", tankTag);
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
    	tank.writeToNBT(nbtTag);
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
    	tank.readFromNBT(nbtTag);
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
	
	///// Boiler Plate /////
	
	private String customName = null;
	///// IFluidHandler /////

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource == null) {
			return 0;
		}
		FluidStack resourceCopy = resource.copy();
		int totalUsed = 0;
		totalUsed += tank.fill(resourceCopy, doFill);
		if(doFill)this.sync();
		return totalUsed;
	}
	@Override
	public FluidStack drain(EnumFacing from, int maxEmpty, boolean doDrain) {
		if(doDrain) this.sync();
		return tank.drain(maxEmpty, doDrain);
	}
	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (resource == null)
			return null;
		if (!resource.isFluidEqual(tank.getFluid()))
			return null;
		if(doDrain) this.sync();
		return drain(from, resource.amount, doDrain);
	}
	private FluidTankInfo[] tankInfo = new FluidTankInfo[1];
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing direction) {
		tankInfo[0] = tank.getInfo();
		return tankInfo;
	}
	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}
	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return true;
	}
	public int getFillLevel(){
		return tank.getFluidAmount();
	}
	public int getMaxFill(){
		return tank.getCapacity();
	}
	public Fluid getCurrentFluid(){
		if(tank.getFluid() == null)return null;
		return tank.getFluid().getFluid();
	}
	

	///// ISidedInventroy /////
	
	@Override
	public void clear() {
		// do nothing
		
	}
	@Override
	public void closeInventory(EntityPlayer arg0) {
		// do nothing
		
	}
	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		// do nothing
		return null;
	}
	@Override
	public int getField(int arg0) {
		// do nothing
		return 0;
	}
	@Override
	public int getFieldCount() {
		// do nothing
		return 0;
	}
	@Override
	public int getInventoryStackLimit() {
		// do nothing
		return 0;
	}
	@Override
	public int getSizeInventory() {
		// do nothing
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int arg0) {
		// do nothing
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int arg0) {
		// do nothing
		return null;
	}
	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
	// do nothing
		return false;
	}
	@Override
	public boolean isUseableByPlayer(final EntityPlayer p_isUseableByPlayer_1_) {
	    return this.worldObj.getTileEntity(this.pos) == this && p_isUseableByPlayer_1_.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}
	@Override
	public void openInventory(EntityPlayer arg0) {
		// do nothing
		
	}
	@Override
	public void setField(int arg0, int arg1) {
		// do nothing
		
	}
	@Override
	public void setInventorySlotContents(int arg0, ItemStack arg1) {
		// do nothing
		
	}
	@Override
    public IChatComponent getDisplayName() {
        if (this.hasCustomName()) {
            return new ChatComponentText(this.getName());
        }
        return new ChatComponentTranslation(this.getName(), new Object[0]);
    }
	@Override
	public String getName() {
        return this.hasCustomName() ? this.customName : cyano.poweradvantage.init.Blocks.fluid_drain.getUnlocalizedName();
	}
	@Override
	public boolean hasCustomName() {
		return this.customName != null;
	}
	@Override
	public boolean canExtractItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		// do nothing
		return false;
	}
	@Override
	public boolean canInsertItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		// do nothing
		return false;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return new int[0];
	}

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
