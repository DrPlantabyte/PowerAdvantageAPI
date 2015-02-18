package cyano.poweradvantage.fluids.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class DrainTileEntity extends TileEntity implements IFluidHandler{

	private FluidTank tank;
	
	@Override
	public int fill(EnumFacing p0, FluidStack p1, boolean p2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing p0, FluidStack p1, boolean p2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing p0, int p1, boolean p2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(EnumFacing p0, Fluid p1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing p0, Fluid p1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing p0) {
		// TODO Auto-generated method stub
		return null;
	}

}
