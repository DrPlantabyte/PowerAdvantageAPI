package cyano.poweradvantage.machines;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidSource;

public class StorageTankTileEntity  extends TileEntitySimpleFluidSource{

	public StorageTankTileEntity() {
		super(FluidContainerRegistry.BUCKET_VOLUME * 8, StorageTankTileEntity.class.getName());
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
			return new FluidRequest(FluidRequest.BACKUP_PRIORITY,
					(getTank().getCapacity() - getTank().getFluidAmount()),
					this);
		} else if(getTank().getFluidAmount() <= 0 && canAccept(offer)){
			return new FluidRequest(FluidRequest.BACKUP_PRIORITY,
					getTank().getCapacity(),
					this);
		} else {
			return FluidRequest.REQUEST_NOTHING;
		}
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
			if(this.getTank().getFluid() != lastTime){
				this.markDirty();
				lastTime = this.getTank().getFluid();
			}
		}
	}

	public int getRedstoneOutput() {
		return this.getTank().getFluidAmount() * 15 / this.getTank().getCapacity();
	}
}
