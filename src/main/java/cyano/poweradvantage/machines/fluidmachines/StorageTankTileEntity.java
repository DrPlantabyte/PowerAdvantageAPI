package cyano.poweradvantage.machines.fluidmachines;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidSource;
import cyano.poweradvantage.init.Fluids;

public class StorageTankTileEntity  extends TileEntitySimpleFluidSource{

	public StorageTankTileEntity() {
		super(FluidContainerRegistry.BUCKET_VOLUME * 4, StorageTankTileEntity.class.getName());
	}

	
	/**
	 * Generates a request for fluid based on what is being offered. 
	 * @param offer The type of fluid being offered by a fluid producer
	 * @return A FluidRequest object representing how much of the offered fluid you want to take. 
	 * If you don't want any, return <code>FluidRequest.REQUEST_NOTHING</code>
	 */
	@Override
	public FluidRequest getFluidRequest(Fluid offer) {
		if(getTank().getFluidAmount() > 0 && offer.equals(getTank().getFluid().getFluid())){
			FluidRequest req = new FluidRequest(FluidRequest.BACKUP_PRIORITY+1,
					(getTank().getCapacity() - getTank().getFluidAmount()),
					this);
			return req;
		} else if(getTank().getFluidAmount() <= 0 && canAccept(offer)){
			FluidRequest req = new FluidRequest(FluidRequest.BACKUP_PRIORITY,
					getTank().getCapacity(),
					this);
			return req;
		} else {
			return FluidRequest.REQUEST_NOTHING;
		}
	}
	
	 /**
     * Specifies the minimum priority of power sinks whose requests for power will be filled. Power 
     * storage tile entities should override this method and return 
     * <code>PowerRequest.BACKUP_PRIORITY+1</code> to avoid needlessly transferring power between 
     * storage devices.
     * @return The lowest priority of power request that will be filled.
     */
	@Override
    protected byte getMinimumSinkPriority(){
    	return PowerRequest.BACKUP_PRIORITY+2;
    }
	
	/**
	 * This method is used to restrict what types of fluids are allowed in the tank. If you want to 
	 * store any kind of fluid, just return true, but if you only want to store, for example, lava 
	 * then return false unless the provided fluid is lava
	 * @param fluid A fluid that can or cannot go into your tank
	 * @return true if the fluid can go into the tank, false otherwise
	 */
	public boolean canAccept(Fluid fluid){
		// store any kind of liquid
		return true;
	}
	
	private final ItemStack[] noninventory = new ItemStack[0];
	@Override
	protected ItemStack[] getInventory() {
		return noninventory;
	}

	private FluidStack lastTime = null;
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld && this.getWorld().getTotalWorldTime() % 11 == 0){
			// send update
			if((this.getTank().getFluid() != null && !this.getTank().getFluid().isFluidStackIdentical(lastTime))
					|| (this.getTank().getFluid() == null && lastTime != null)){
				this.sync();
				lastTime = (this.getTank().getFluid() == null) ? null : this.getTank().getFluid().copy();
			}
		}
	}

	public int getRedstoneOutput() {
		return this.getTank().getFluidAmount() * 15 / this.getTank().getCapacity();
	}
	
	/**
	 * Determines whether this block/entity should receive energy 
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(){
		return true;
	}
	/**
	 * Determines whether this block/entity can provide energy 
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(){
		return true;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return this.getStackInSlot(index);
	}
}
