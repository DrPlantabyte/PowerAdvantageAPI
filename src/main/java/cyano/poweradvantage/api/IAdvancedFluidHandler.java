package cyano.poweradvantage.api;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

public interface IAdvancedFluidHandler extends IFluidHandler{
	// TODO: documentation
	public abstract int getTotalFluidAmount();
	
	public abstract int getFluidAmount(Fluid type);
	
	public abstract int getFluidCapacity(Fluid type);
}
