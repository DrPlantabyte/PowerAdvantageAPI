package cyano.poweradvantage.api;

import java.util.HashMap;

import java.util.Arrays;
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
		final TileEntity[] entities = new TileEntity[6]; 
		coords[0] = this.pos.down();
		coords[1] = this.pos.north();
		coords[2] = this.pos.west();
		coords[3] = this.pos.south();
		coords[4] = this.pos.east();
		coords[5] = this.pos.up();
		for(int n = 0; n < 6; n++){
			entities[n] = worldObj.getTileEntity(coords[n]);
		}
		// send fluid into neighbors
		pipeDist:{
			Fluid fluid = tank.getFluid().getFluid();
			// first, fill down
			if(entities[0] instanceof IFluidHandler && ((IFluidHandler)entities[0]).canFill(facesOther[0], fluid)){
				this.drain(faces[0], ((IFluidHandler)entities[0]).fill(facesOther[0], tank.getFluid(), true), true);
			}
			if(tank.getFluidAmount() <= 0) break pipeDist;
			// then fill sideways
			final HashMap<Integer, IPipeFluidHandler> neighbors = new HashMap<>();
			final HashMap< IPipeFluidHandler, EnumFacing> neighborFaces = new HashMap<>();
			for(int n = 1; n < 5; n++){
				if(entities[n] instanceof IPipeFluidHandler){
					IPipeFluidHandler pipe = (IPipeFluidHandler)entities[n];
					if(pipe.canFill(facesOther[n], fluid)){
						neighbors.put(pipe.getFluidAmount(fluid), pipe);
						neighborFaces.put(pipe, facesOther[n]);
					}
				} else if(entities[n] instanceof IFluidHandler){
					// dump fluid into non-pipes
					IFluidHandler fh = (IFluidHandler)entities[n];
					if(fh.canFill(facesOther[n], fluid)){
						this.drain(faces[n], ((IFluidHandler)entities[0]).fill(facesOther[n], tank.getFluid(), true), true);
						if(tank.getFluidAmount() <= 0) break pipeDist;
					}
				}
			}
			Integer[] keys = neighbors.keySet().toArray(new Integer[neighbors.size()]);
			Arrays.sort(keys);
			for(int j = keys.length - 1; j >= 0; j--){
				int levelDelta = tank.getFluidAmount() - ((tank.getFluidAmount() + neighbors.get(keys[j]).getFluidAmount(fluid)) / 2);
				// todo: a little bit of code clean-up here
				if(levelDelta > 0){
					neighbors
					.get(keys[j])
					.fill(neighborFaces
							.get(keys[j]), 
							this.drain(neighborFaces
									.get(neighbors.get(keys[j]))
									.getOpposite(), 
									levelDelta, true), true);
				}
			}
				
			// then fill up
			int surplus = tank.getFluidAmount() - tank.getCapacity() / 2;
			if(surplus > 0 && entities[5] instanceof IFluidHandler && ((IFluidHandler)entities[5]).canFill(facesOther[5], fluid)){
				((IFluidHandler)entities[0]).fill(facesOther[5], this.drain(faces[5], surplus, true), true);
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
