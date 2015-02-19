package cyano.poweradvantage.fluids.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;


public class DrainTileEntity extends TileEntity implements IUpdatePlayerListBox, IFluidHandler{

	public final FluidTank tank = new FluidTank( FluidContainerRegistry.BUCKET_VOLUME );
	
	private final int updateInterval = 20;
	private final int updateOffset;
	
	public DrainTileEntity(World w){
		updateOffset = (int)(w.getTotalWorldTime() % updateInterval);
	}
	public DrainTileEntity(){
		updateOffset = 0;
	}
	
	///// Logic and implementation /////
	
	@Override
	public void update(){
		if(!worldObj.isRemote){
			// server-side
			if((worldObj.getTotalWorldTime() + updateOffset) % updateInterval == 0){
				if(tank.getFluidAmount() <= 0){
					IBlockState bs = worldObj.getBlockState(this.pos.up());
					if(bs.getBlock() instanceof IFluidBlock){
						IFluidBlock block = (IFluidBlock)bs.getBlock();
						Fluid fluid = block.getFluid();
						if(fluid != null){
							if(block.canDrain(worldObj, this.pos.up())){
								// is source block
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
	//////////
}
