package cyano.poweradvantage.api.fluid;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import java.util.List;

/**
 * <p>
 * Fluids in PowerAdvantage are transmitted the same way as energy, except that the conduit type is 
 * <code>Fluids.fluidConduit_general</code> but the fluid sources ask the sinks to make 
 * PowerRequests based on the specific fluid provided by that fluid source. Thus fluid pump, tank, 
 * and fluid pipe blocks will all report to be of type <code>Fluids.fluidConduit_general</code> when 
 * you call <code>getType()</code>, but a water pump will poll the network with the "water" 
 * ConduitType and the tank will file a PowerRequest based on how much water it can hold.
 * </p><p>
 * This class provides a number of simplifications so that implementations don't need to worry about 
 * implementing the IFluidHandler interface. You probably want to use the BlockSimpleFluidConduit 
 * class in the cyano.poweradvantage.api.simple package. 
 * </p>
 * @author DrCyano
 *
 */
public abstract class FluidPoweredEntity extends PoweredEntity implements IFluidHandler {

	
	/**
	 * For simplicity's sake, this method wraps the <code>getFluidRequest(...)</code> and sends it 
	 * of through the normal power network algorithm.
	 * @return A power request representing this machines request for fluids
	 */
	@Override
	public PowerRequest getPowerRequest(ConduitType type) {
		// Type will be lava or water or other specific fluid
		// implementation of getFluidRequest() decides whether this machine wants it
		if(this.isFluidSink() && Fluids.isFluidType(type)){
			if(type.equals(Fluids.fluidConduit_general)) return PowerRequest.REQUEST_NOTHING; // not a specific fluid type
			return getFluidRequest(Fluids.conduitTypeToFluid(type));
		}else{
			return PowerRequest.REQUEST_NOTHING;
		}
	}
	
	/**
	 * Gets the FluidTank instance used to handle fluids
	 * @return An instance of FluidTank
	 */
	public abstract FluidTank getTank();
	
	
	/**
	 * Collects all requests for energy from power sinks connected to this tile entity
	 * @param powerType The type of fluid (e.g. water) available to donate to the sinks
	 * @return Returns a list of all connected entities that want energy, in order from highest 
	 * priority to lowest (energy storage will have lower priority than machines).
	 */
	protected List<PowerRequest> getRequestsForFluid(ConduitType powerType){
		return ConduitRegistry.getInstance().getRequestsForPower(getWorld(), getPos(), Fluids.fluidConduit_general,powerType);
	}
	
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
        NBTTagCompound tankTag = new NBTTagCompound();
        this.getTank().writeToNBT(tankTag);
        tagRoot.setTag("Tank", tankTag);
		return tagRoot;
	}
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
    	super.readFromNBT(tagRoot);
        if (tagRoot.hasKey("Tank")) {
            NBTTagCompound tankTag = tagRoot.getCompoundTag("Tank");
    		getTank().readFromNBT(tankTag);
    		if(tankTag.hasKey("Empty")){
    			// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
    			getTank().setFluid(null);
    		}
        }
    }

	/**
	 * Adds "energy" as a fluid to the FluidTank returned by getTank(). This implementation ignores 
	 * all non-fluid energy types.
	 * @param amount amount of "energy" (fluid) to add
	 * @param type the type of energy/fluid being added.
	 * @return The amount that was actually added
	 */
    @Override
	public float addEnergy(float amount, ConduitType type){
    	if(Fluids.isFluidType(type)){
			if(this.canFill(null, Fluids.conduitTypeToFluid(type))){
				return this.fill(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true);
			} else {
				return 0;
			}
		}else{
			return 0;
		}
	}
    /**
     * Sets the tank contents using the energy API method
	 * @param amount amount of "energy" (fluid) to add
	 * @param type the type of energy/fluid being added.
     */
	@Override
	public void setEnergy(float amount,ConduitType type) {
		if(Fluids.isFluidType(type)){
			getTank().setFluid(new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount));
		}else{
			// do nothing
		}
	}
	/**
	 * Subtracts "energy" as a fluid to the FluidTank returned by getTank(). This implementation 
	 * ignores all non-fluid energy types.
	 * @param amount amount of "energy" (fluid) to add
	 * @param type the type of energy/fluid being added.
	 * @return The amount that was actually added
	 */
    @Override
	public float subtractEnergy(float amount, ConduitType type){
		if(Fluids.isFluidType(type)){
			if(this.canDrain(null, Fluids.conduitTypeToFluid(type))){
				return this.drain(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)amount), true).amount;
			} else {
				return 0;
			}
		}else{
			return 0;
		}
	}
	
	/**
	 * Implement this method to request fluid from a fluid source that is offering the given fluid.
	 * @param type The type of fluid on offer
	 * @return A FluidRequest describing how much fluid you want and what priority your request has. 
	 * If you don't want this fluid, return <code>FluidRequest.REQUEST_NOTHING</code> 
	 */
	public abstract FluidRequest getFluidRequest(Fluid type);


	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public int fill(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() <= 0 || getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().fill(fluid, forReal);
		} else {
			return 0;
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public FluidStack drain(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() > 0 && getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().drain(fluid.amount,forReal);
		} else {
			return new FluidStack(fluid.getFluid(),0);
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param amount The amount of fluid being added/removed
	 * @param forReal if true, then the fluid in the tank will change
	 */
	@Override
	public FluidStack drain(EnumFacing face, int amount, boolean forReal) {
		if(getTank().getFluidAmount() > 0 ){
			return getTank().drain(amount,forReal);
		} else {
			return null;
		}
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canFill(EnumFacing face, Fluid fluid) {
		if(getTank().getFluid() == null) return true;
		return getTank().getFluidAmount() <= getTank().getCapacity() && fluid.equals(getTank().getFluid().getFluid());
	}
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canDrain(EnumFacing face, Fluid fluid) {
		if(getTank().getFluid() == null) return false;
		return getTank().getFluidAmount() > 0 && fluid.equals(getTank().getFluid().getFluid());
	}
	
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @return array of FluidTankInfo describing all of the FluidTanks
	 */
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		FluidTankInfo[] arr = new FluidTankInfo[1];
		arr[0] = getTank().getInfo();
		return arr;
	}

	/**
	 * checks whether this machine should transmit a given power type to power consumers
	 * @param type The type of power
	 * @return true to transmit energy, false otherwise
	 */
	@Override
	public boolean isPowerSource(ConduitType type){
		if(Fluids.isFluidType(type)) {
			return isFluidSource();
		} else {
			return false;
		}
	}
	/**
	 * checks whether this machine should consume a given power type
	 * @param type The type of power
	 * @return true to receive energy, false otherwise
	 */
	@Override
	public boolean isPowerSink(ConduitType type){
		if(Fluids.isFluidType(type)) {
			return isFluidSink();
		} else {
			return false;
		}
	}

	/**
	 * Checks whether this fluid machine should send out its fluid to other fluid machines
	 * @return true to send fluids to other machines
	 */
	public abstract boolean isFluidSource();

	/**
	 * Checks whether this fluid machine should receive fluids from other fluid machines
	 * @return true to receive fluids from other machines
	 */
	public abstract boolean isFluidSink();
}
