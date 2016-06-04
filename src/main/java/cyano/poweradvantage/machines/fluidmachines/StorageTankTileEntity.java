package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class StorageTankTileEntity  extends TileEntitySimpleFluidMachine {

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


	private Fluid lastFluid = null;
	private int lastFluidAmount = -1;
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld && this.getWorld().getTotalWorldTime() % 11 == 0){
			// send update
			FluidStack current = getTank().getFluid();
			Fluid currentFluid = (current != null ? current.getFluid() : null);
			int currentAmount = getTank().getFluidAmount();
			if(lastFluid != currentFluid
					|| lastFluidAmount != currentAmount){
				this.sync();
				lastFluid = currentFluid;
				lastFluidAmount = currentAmount;
			}
		}
	}

	public int getRedstoneOutput() {
		return this.getTank().getFluidAmount() * 15 / this.getTank().getCapacity();
	}


	/**
	 * Checks whether this fluid machine should send out its fluid to other fluid machines
	 *
	 * @return true to send fluids to other machines
	 */
	@Override
	public boolean isFluidSource() {
		return true;
	}

	/**
	 * Checks whether this fluid machine should receive fluids from other fluid machines
	 *
	 * @return true to receive fluids from other machines
	 */
	@Override
	public boolean isFluidSink() {
		return true;
	}
}
