package cyano.poweradvantage.fluids.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.FMLLog;


public class FluidDrainTileEntity extends TileEntity implements IUpdatePlayerListBox, IFluidHandler, ISidedInventory{

	public final FluidTank tank = new FluidTank( FluidContainerRegistry.BUCKET_VOLUME );
	
	private final int updateInterval = 20;
	private final int updateOffset;
	
	public FluidDrainTileEntity(World w, int i){
		updateOffset = w.rand.nextInt( updateInterval);
	}
	public FluidDrainTileEntity(){
		updateOffset = 0;
	}
	
	///// Logic and implementation /////
	
	@Override
	public void update(){
		if(!worldObj.isRemote){
			// server-side
			if((worldObj.getTotalWorldTime() + updateOffset) % updateInterval == 0){
				FMLLog.info("Drain contains "+ tank.getFluidAmount() + " units of " + tank.getFluid()); // TODO: remove debug code
				if(tank.getFluidAmount() <= 0){
					IBlockState bs = worldObj.getBlockState(this.pos.up());
					if(bs.getBlock() instanceof IFluidBlock){
						IFluidBlock block = (IFluidBlock)bs.getBlock();
						FMLLog.info("Drain right under a block of "+ block.getFluid()); // TODO: remove debug code
						Fluid fluid = block.getFluid();
						if(fluid != null){
							if(block.canDrain(worldObj, this.pos.up())){
								// is source block
								FMLLog.info("Drain right under source block"); // TODO: remove debug code
								tank.fill(new FluidStack(fluid,FluidContainerRegistry.BUCKET_VOLUME), true);
								worldObj.setBlockToAir(this.pos.up());
								this.markDirty();
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
									this.markDirty();
								}
							}
						}
					}
				}
			}
		}
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
		return totalUsed;
	}
	@Override
	public FluidStack drain(EnumFacing from, int maxEmpty, boolean doDrain) {
		return tank.drain(maxEmpty, doDrain);
	}
	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (resource == null)
			return null;
		if (!resource.isFluidEqual(tank.getFluid()))
			return null;
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
