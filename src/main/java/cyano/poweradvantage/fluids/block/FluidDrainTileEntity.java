package cyano.poweradvantage.fluids.block;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
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


public class FluidDrainTileEntity extends TileEntity implements IUpdatePlayerListBox, IFluidHandler, ISidedInventory{

	public final FluidTank tank = new FluidTank( FluidContainerRegistry.BUCKET_VOLUME );
	
	private final static int updateInterval = 20;
	private final int updateOffset;
	
	public FluidDrainTileEntity(World w, int i){
		this(w.rand.nextInt( updateInterval));
		
	}
	public FluidDrainTileEntity(int tickOffset){
		updateOffset = tickOffset;
	}
	
	public FluidDrainTileEntity(){
		this(0);
	}
	
	///// Logic and implementation /////
	
	@Override
	public void update(){
		if(!worldObj.isRemote){
			// server-side
			if((worldObj.getTotalWorldTime() + updateOffset) % updateInterval == 0){
				if(tank.getFluid() != null)FMLLog.info("Drain contains "+ tank.getFluidAmount() + " units of " + tank.getFluid().getUnlocalizedName()); // TODO: remove debug code
				if(tank.getFluidAmount() <= 0){
					IBlockState bs = worldObj.getBlockState(this.pos.up());
					FMLLog.info("Looking for fluids, found "+bs.getBlock().getUnlocalizedName()+" ("+bs.getBlock().getClass().getName()+")"); // TODO: remove debug code
					if(bs.getBlock() instanceof IFluidBlock){
						// Forge fluid
						IFluidBlock block = (IFluidBlock)bs.getBlock();
						FMLLog.info("Drain right under a block of "+ block.getFluid()); // TODO: remove debug code
						Fluid fluid = block.getFluid();
						if(fluid != null){
							if(block.canDrain(worldObj, this.pos.up())){
								// is source block
								FMLLog.info("Drain right under source block"); // TODO: remove debug code
								tank.fill(new FluidStack(fluid,FluidContainerRegistry.BUCKET_VOLUME), true);
								worldObj.setBlockToAir(this.pos.up());
								this.sync();
							} else {
								// is flowing block, follow upstream to find source block
								int limit = 16;
								BlockPos coord = this.pos.up();
								do{
									if(worldObj.getBlockState(coord).getBlock() instanceof IFluidBlock
											&& ((IFluidBlock)worldObj.getBlockState(coord).getBlock()).canDrain(worldObj, coord)){
										break;
									} else {
										if(worldObj.getBlockState(coord.up()).getBlock() instanceof IFluidBlock
												&& ((IFluidBlock)worldObj.getBlockState(coord.up()).getBlock()).getFluid() == fluid){
											coord = coord.up();
										} else {
											float Q = ((IFluidBlock)worldObj.getBlockState(coord).getBlock()).getFilledPercentage(worldObj, coord);
											if(worldObj.getBlockState(coord.north()).getBlock() instanceof IFluidBlock
													&& ((IFluidBlock)worldObj.getBlockState(coord.north()).getBlock()).getFluid() == fluid
													&& ((IFluidBlock)worldObj.getBlockState(coord.north()).getBlock()).getFilledPercentage(worldObj, coord) > Q){
												coord = coord.north();
											} else if(worldObj.getBlockState(coord.east()).getBlock() instanceof IFluidBlock
													&& ((IFluidBlock)worldObj.getBlockState(coord.east()).getBlock()).getFluid() == fluid
													&& ((IFluidBlock)worldObj.getBlockState(coord.east()).getBlock()).getFilledPercentage(worldObj, coord) > Q){
												coord = coord.east();
											} else if(worldObj.getBlockState(coord.south()).getBlock() instanceof IFluidBlock
													&& ((IFluidBlock)worldObj.getBlockState(coord.south()).getBlock()).getFluid() == fluid
													&& ((IFluidBlock)worldObj.getBlockState(coord.south()).getBlock()).getFilledPercentage(worldObj, coord) > Q){
												coord = coord.south();
											} else if(worldObj.getBlockState(coord.west()).getBlock() instanceof IFluidBlock
													&& ((IFluidBlock)worldObj.getBlockState(coord.west()).getBlock()).getFluid() == fluid
													&& ((IFluidBlock)worldObj.getBlockState(coord.west()).getBlock()).getFilledPercentage(worldObj, coord) > Q){
												coord = coord.west();
											}
										}
									}
									limit--;
								}while(limit > 0);
								if(limit > 0 && ((IFluidBlock)worldObj.getBlockState(coord).getBlock()).canDrain(worldObj, coord)){
									// found source block
									FMLLog.info("Drain found source block at"+coord); // TODO: remove debug code
									tank.fill(new FluidStack(fluid,FluidContainerRegistry.BUCKET_VOLUME), true);
									worldObj.setBlockToAir(coord);
									this.sync();
								}
							}
						}
					} else if(bs.getBlock() instanceof BlockLiquid){
						// Minecraft fluid
						BlockLiquid block = (BlockLiquid)bs.getBlock();
						FMLLog.info("Drain right under a vanilla block of "+ block.getUnlocalizedName() + " " + mapToString(bs.getProperties())); // TODO: remove debug code

						// flowing minecraft fluid block
						Material m = block.getMaterial();
						// is flowing block, follow upstream to find source block
						int limit = 16;
						BlockPos coord = this.pos.up();
						do{
							
							int Q = (Integer)worldObj.getBlockState(coord).getValue(BlockDynamicLiquid.LEVEL); // 0 for source block, 1-7 for flowing blocks (lower number = closer to source)
							FMLLog.info("Block at "+ coord + ": "+ worldObj.getBlockState(coord).getBlock().getUnlocalizedName() + " " + mapToString(worldObj.getBlockState(coord).getProperties())); // TODO: remove debug code
							FMLLog.info("Neighbor at "+ coord.north() + ": "+ worldObj.getBlockState(coord.north()).getBlock().getUnlocalizedName() + " " + mapToString(worldObj.getBlockState(coord.north()).getProperties())); // TODO: remove debug code
							FMLLog.info("Neighbor at "+ coord.east() + ": "+ worldObj.getBlockState(coord.east()).getBlock().getUnlocalizedName() + " " + mapToString(worldObj.getBlockState(coord.east()).getProperties())); // TODO: remove debug code
							FMLLog.info("Neighbor at "+ coord.south() + ": "+ worldObj.getBlockState(coord.south()).getBlock().getUnlocalizedName() + " " + mapToString(worldObj.getBlockState(coord.south()).getProperties())); // TODO: remove debug code
							FMLLog.info("Neighbor at "+ coord.west() + ": "+ worldObj.getBlockState(coord.west()).getBlock().getUnlocalizedName() + " " + mapToString(worldObj.getBlockState(coord.west()).getProperties())); // TODO: remove debug code
							if(Q == 0){
								// source block
								break;
							} else {
								
								if(worldObj.getBlockState(coord.up()).getBlock() instanceof BlockLiquid
										&& worldObj.getBlockState(coord.up()).getBlock().getMaterial() == m){
									coord = coord.up();
									FMLLog.info("Up");
								} else {
									if(worldObj.getBlockState(coord.north()).getBlock() instanceof BlockLiquid
											&& worldObj.getBlockState(coord.north()).getBlock().getMaterial() == m
											&& ((Integer)worldObj.getBlockState(coord.north()).getValue(BlockDynamicLiquid.LEVEL) < Q  || (Integer)worldObj.getBlockState(coord.north()).getValue(BlockDynamicLiquid.LEVEL) == 9)){
										coord = coord.north();
										FMLLog.info("North");
									} else if(worldObj.getBlockState(coord.east()).getBlock() instanceof BlockLiquid
											&& worldObj.getBlockState(coord.east()).getBlock().getMaterial() == m
											&& ((Integer)worldObj.getBlockState(coord.east()).getValue(BlockDynamicLiquid.LEVEL) < Q  || (Integer)worldObj.getBlockState(coord.east()).getValue(BlockDynamicLiquid.LEVEL) == 9)){
										coord = coord.east();
										FMLLog.info("East");
									} else if(worldObj.getBlockState(coord.south()).getBlock() instanceof BlockLiquid
											&& worldObj.getBlockState(coord.south()).getBlock().getMaterial() == m
											&& ((Integer)worldObj.getBlockState(coord.south()).getValue(BlockDynamicLiquid.LEVEL) < Q  || (Integer)worldObj.getBlockState(coord.south()).getValue(BlockDynamicLiquid.LEVEL) == 9)){
										coord = coord.south();
										FMLLog.info("South");
									} else if(worldObj.getBlockState(coord.west()).getBlock() instanceof BlockLiquid
											&& worldObj.getBlockState(coord.west()).getBlock().getMaterial() == m
											&& ((Integer)worldObj.getBlockState(coord.west()).getValue(BlockDynamicLiquid.LEVEL) < Q  || (Integer)worldObj.getBlockState(coord.west()).getValue(BlockDynamicLiquid.LEVEL) == 9)){
										coord = coord.west();
										FMLLog.info("West");
									} else {
										// failed to find upstream block
										limit = 0;
									}
								}
							}
							limit--;
						}while(limit > 0);
						if(limit > 0 && worldObj.getBlockState(coord).getBlock() instanceof BlockStaticLiquid){
							// found source block
							FMLLog.info("Drain found source block at"+coord); // TODO: remove debug code
							if(m == Material.water){
								tank.fill(new FluidStack(FluidRegistry.WATER,FluidContainerRegistry.BUCKET_VOLUME), true);
							} else if(m == Material.lava){
								tank.fill(new FluidStack(FluidRegistry.LAVA,FluidContainerRegistry.BUCKET_VOLUME), true);
							} else {
								FMLLog.warning("Liquid source block at "+coord+" has unknown fluid material "+m); // TODO: remove debug code
								return;
							}
							worldObj.setBlockToAir(coord);
							this.sync();
						}

					}
				}
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
		this.sync();
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
	

	
	
	//////////
}
