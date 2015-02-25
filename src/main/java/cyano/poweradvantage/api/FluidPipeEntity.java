package cyano.poweradvantage.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.FMLLog;

// TODO: Update Documentation
/**
 * This class is the superclass for all TileEntities that transfer fluids.
 * @author DrCyano
 *
 */
public abstract class FluidPipeEntity extends TileEntity implements IUpdatePlayerListBox, IPipeFluidHandler{
	
	private final int updateInterval = 8;
	
	protected final FluidTank tank;
	
	
	public FluidPipeEntity(int capacity){
		tank = new FluidTank(capacity);
	}
	

	@Override
	public int fill(EnumFacing face, FluidStack typeAndAmount, boolean forReal) {
		return tank.fill(typeAndAmount, forReal);
	}

	@Override
	public FluidStack drain(EnumFacing face, FluidStack typeAndAmount, boolean forReal) {
		if(typeAndAmount != null && typeAndAmount.isFluidEqual(tank.getFluid())){
			return drain(face,typeAndAmount.amount, forReal);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing face, int amount, boolean forReal) {
		return tank.drain(amount, forReal);
	}

	@Override
	public boolean canFill(EnumFacing face, Fluid type) {
		if(tank.getFluid() == null || tank.getFluidAmount() <= 0) return true;
		return tank.getFluid().isFluidEqual(new FluidStack(type,1));
	}

	@Override
	public boolean canDrain(EnumFacing face, Fluid type) {
		return tank.getFluidAmount() > 0 && tank.getFluid().isFluidEqual(new FluidStack(type,1));
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		final FluidTankInfo[] infos = new FluidTankInfo[1];
		infos[0] = new FluidTankInfo(tank);
		return infos;
	}
	
	
	@Override
	public int getTotalFluidAmount(){
		return tank.getFluidAmount();
	}
	
	@Override 
	public  int getFluidAmount(Fluid type){
		if(tank.getFluid() != null && tank.getFluid().isFluidEqual(new FluidStack(type,1))){
			return tank.getFluidAmount();
		} else {
			return 0;
		}
	}
	
	@Override
	public int getFluidCapacity(Fluid type){
		if(tank.getFluid() == null || tank.getFluid().isFluidEqual(new FluidStack(type,1))){
			return tank.getCapacity();
		} else {
			return 0;
		}
	}
	/**
	 * Method net.minecraft.server.gui.IUpdatePlayerListBox.update() is invoked 
	 * to do tick updates
	 */
	@Override
    public final void update() {
		// this method was moved from TileEntity to IUpdatePlayerListBox
		this.tickUpdate(this.isServer());
		if(this.isServer() && ((this.getWorld().getTotalWorldTime() + this.getTickOffset()) % updateInterval == 0)){
			this.fluidUpdate();
		}
	}
	/**
	 * Called every tick to update. Note that tickUpdate happens before 
	 * fluidUpdate().
	 * @param isServerWorld Will be true if the code is executing server-side, 
	 * and false if executing client-side 
	 */
	public abstract void tickUpdate(boolean isServerWorld);
	
	/** implementation detail for the fluidUpdate() method. Do not touch! */
	private static final EnumFacing[] facesOther = {EnumFacing.UP,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.DOWN};
	/** implementation detail for the fluidUpdate() method. Do not touch! */
	private static final EnumFacing[] faces = {EnumFacing.DOWN,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.UP};
	
	/**
	 * This method handles the transmission of fluid. If you override this 
	 * method, be sure to call super.fluidUpdate() or power will not flow 
	 * through this block.
	 */
	public void fluidUpdate() {
		if(tank.getFluid() == null || tank.getFluidAmount() <= 0)return;
		final BlockPos[] coords = new BlockPos[6];
		coords[0] = this.pos.down();
		coords[1] = this.pos.north();
		coords[2] = this.pos.west();
		coords[3] = this.pos.south();
		coords[4] = this.pos.east();
		coords[5] = this.pos.up();
		int numNeighbors = 0;
		for(int n = 1; n < 5; n++){
			final TileEntity te = worldObj.getTileEntity(coords[n]);
			if(te instanceof IPipeFluidHandler){
				numNeighbors++;
			}
		}
		for(int n = 0; n < 6; n++){
			if(tank.getFluid() == null || tank.getFluidAmount() <= 0)continue;
			final TileEntity te = worldObj.getTileEntity(coords[n]);
			if(te instanceof IPipeFluidHandler){
				// another pipe
				final IPipeFluidHandler e = (IPipeFluidHandler) te;
				final Fluid fluid = tank.getFluid().getFluid();
				int delta = this.getFluidAmount(fluid) - e.getFluidAmount(fluid);
				int space = e.getFluidCapacity(fluid) - e.getFluidAmount(fluid);
				if(delta > space){
					delta = space;
				}
				if(delta > this.getFluidAmount(fluid) / numNeighbors){
					delta = this.getFluidAmount(fluid) / numNeighbors;
				}
				// TODO: remove debug code
				FMLLog.info(this.getFluidAmount(getFluid())+" units vs " 
						+ e.getFluidAmount(getFluid())+" units. Delta = "+delta );
				if(delta > 0 && e.canFill(facesOther[n], fluid)){
					FMLLog.info("Transferring liquid");
					e.fill(facesOther[n],this.drain(faces[n], delta, true),true);
				}
			} else if (te instanceof IFluidHandler) {
				// a non-pipe
				final IFluidHandler e = (IFluidHandler) te;
				final Fluid fluid = tank.getFluid().getFluid();
				if(e.canFill(facesOther[n], fluid)){
					this.drain(faces[n], e.fill(facesOther[n], this.drain(faces[n], this.tank.getFluidAmount(), false), true), true);
				}
			}
		}
		for(int n = 0; n < 6; n++){
			final TileEntity te = worldObj.getTileEntity(coords[n]);
			if (te instanceof IFluidHandler) {
				// a non-pipe
				final IFluidHandler e = (IFluidHandler) te;
				final FluidStack fs = e.drain(facesOther[n], this.tank.getCapacity() - this.tank.getFluidAmount(), false);
				if(fs != null && this.canFill(faces[n], fs.getFluid())){
					this.fill(faces[n], e.drain(facesOther[n], fs, true), true);
				}
			}
		}
	}
	/**
	 * Returns false if this code is executing on the client and true if this 
	 * code is executing on the server
	 * @return true if on server world, false otherwise
	 */
	public boolean isServer(){
		return !this.getWorld().isRemote;
	}
	/**
	 * Returns a number that is used to spread the power updates in a chunk 
	 * across multiple ticks. Also keeps adjacent conductors from updating in 
	 * the same tick. 
	 * @return
	 */
	private final int getTickOffset(){
		BlockPos coord = this.getPos();
		int x = coord.getX();
		int y = coord.getX();
		int z = coord.getX();
		return ((z & 1) << 2) | ((x & 1) << 1) | ((y & 1) );
	}
	/**
	 * Reads data from NBT, which came from either a saved chunk or a network 
	 * packet.
	 */
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if(tagRoot.hasKey("Tank")){
			tank.readFromNBT(tagRoot.getCompoundTag("Tank"));
		}
	}
	/**
	 * Saves the state of this entity to an NBT for saving or synching across 
	 * the network.
	 */
	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tagRoot.setTag("Tank", tankTag);
	}


	public Fluid getFluid() {
		if(tank.getFluid() == null || tank.getFluidAmount() <= 0) return null;
		return tank.getFluid().getFluid();
	}

	
}
