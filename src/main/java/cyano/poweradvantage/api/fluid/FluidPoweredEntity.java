package cyano.poweradvantage.api.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.init.Fluids;

public abstract class FluidPoweredEntity extends PoweredEntity implements IFluidHandler {

	

	@Override
	public PowerRequest getPowerRequest(ConduitType type) {
		// Type will be lava or water or other specific fluid
		// implementation of getFluidRequest() decides whether this machine wants it
		return getFluidRequest(type);
	}
	

	protected abstract FluidTank getTank();
	
	
	@Override
    public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
        NBTTagCompound tankTag = new NBTTagCompound();
        this.getTank().writeToNBT(tankTag);
        tagRoot.setTag("Tank", tankTag);
	}
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

    @Override
	public float addEnergy(float energy, ConduitType type){
    	if(Fluids.isFluidType(type)){
			if(this.canFill(null, Fluids.conduitTypeToFluid(type))){
				return this.fill(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)energy), true);
			} else {
				return 0;
			}
		}else{
			return super.addEnergy(energy, type);
		}
	}
	@Override
	public void setEnergy(float energy,ConduitType type) {
		if(Fluids.isFluidType(type)){
			getTank().setFluid(new FluidStack(Fluids.conduitTypeToFluid(type),(int)energy));
		}else{
			// do nothing
		}
	}
    @Override
	public float subtractEnergy(float energy, ConduitType type){
		if(Fluids.isFluidType(type)){
			if(this.canDrain(null, Fluids.conduitTypeToFluid(type))){
				return this.drain(null, new FluidStack(Fluids.conduitTypeToFluid(type),(int)energy), true).amount;
			} else {
				return 0;
			}
		}else{
			return super.subtractEnergy(energy, type);
		}
	}
	
	
	public abstract FluidRequest getFluidRequest(ConduitType type);


	
	@Override
	public int fill(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() <= 0 || getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().fill(fluid, forReal);
		} else {
			return 0;
		}
	}
	@Override
	public FluidStack drain(EnumFacing face, FluidStack fluid, boolean forReal) {
		if(getTank().getFluidAmount() > 0 && getTank().getFluid().getFluid().equals(fluid.getFluid())){
			return getTank().drain(fluid.amount,forReal);
		} else {
			return new FluidStack(getTank().getFluid().getFluid(),0);
		}
	}
	@Override
	public FluidStack drain(EnumFacing face, int amount, boolean forReal) {
		if(getTank().getFluidAmount() > 0 ){
			return getTank().drain(amount,forReal);
		} else {
			return null;
		}
	}
	@Override
	public boolean canFill(EnumFacing face, Fluid fluid) {
		if(getTank().getFluid() == null) return true;
		return getTank().getFluidAmount() <= getTank().getCapacity() && fluid.equals(getTank().getFluid().getFluid());
	}
	@Override
	public boolean canDrain(EnumFacing face, Fluid fluid) {
		if(getTank().getFluid() == null) return false;
		return getTank().getFluidAmount() > 0 && fluid.equals(getTank().getFluid().getFluid());
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		FluidTankInfo[] arr = new FluidTankInfo[1];
		arr[0] = getTank().getInfo();
		return arr;
	}

}
