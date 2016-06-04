package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.*;

public class MetalTankTileEntity  extends TileEntitySimpleFluidMachine {

	public MetalTankTileEntity() {
		super(FluidContainerRegistry.BUCKET_VOLUME * 10, MetalTankTileEntity.class.getName());
	}

	
	/**
	 * Generates a request for fluid based on what is being offered. 
	 * @param offer The type of fluid being offered by a fluid producer
	 * @return A FluidRequest object representing how much of the offered fluid you want to take. 
	 * If you don't want any, return <code>FluidRequest.REQUEST_NOTHING</code>
	 */
	@Override
	public FluidRequest getFluidRequest(Fluid offer) {
		FluidStack filterFluid = getFilter();
		if(filterFluid != null
				&& !(FluidRegistry.getFluidName(offer).equalsIgnoreCase(FluidRegistry.getFluidName(filterFluid)))){
			// offer blocked by filter
			return FluidRequest.REQUEST_NOTHING;
		}
		if(getTank().getFluidAmount() > 0 && offer.equals(getTank().getFluid().getFluid())){
			FluidRequest req = new FluidRequest(FluidRequest.BACKUP_PRIORITY,
					(getTank().getCapacity() - getTank().getFluidAmount()),
					this);
			return req;
		} else if(getTank().getFluidAmount() <= 0 && canAccept(offer)){
			FluidRequest req = new FluidRequest(FluidRequest.BACKUP_PRIORITY - 1,
					getTank().getCapacity(),
					this);
			return req;
		} else {
			return FluidRequest.REQUEST_NOTHING;
		}
	}

	private FluidStack getFilter() {
		ItemStack item = filterInventory[0];
		if(item == null) return null;
		if(item.getItem() instanceof UniversalBucket){
			UniversalBucket bucket = (UniversalBucket)item.getItem();
			FluidStack drain = bucket.drain(item,bucket.getCapacity(item),false);
			if(drain != null && drain.amount > 0){
				return drain;
			} else {
				return null;
			}
		}
		return FluidContainerRegistry.getFluidForFilledItem(item);
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
    	return PowerRequest.BACKUP_PRIORITY+1;
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
	
	private final ItemStack[] filterInventory = new ItemStack[1];
	@Override
	protected ItemStack[] getInventory() {
		return filterInventory;
	}
	
	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		if(this.getInventory() == null) return false;
		if(slot >= this.getInventory().length) return false;
		if(item.getItem() == ForgeModContainer.getInstance().universalBucket) return true;
		return FluidContainerRegistry.isFilledContainer(item)
				&& FluidContainerRegistry.getFluidForFilledItem(item) != null; 
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
	 * Determines whether this block/entity should receive energy
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType){
		return true;
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

	/**
	 * Determines whether this block/entity can provide energy
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType){
		return true;
	}

}
