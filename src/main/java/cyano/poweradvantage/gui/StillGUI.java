package cyano.poweradvantage.gui;

import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.machines.fluidmachines.StillTileEntity;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class StillGUI extends SimpleMachineGUI{

	
	
	public StillGUI(ResourceLocation image) {
		super(image,
				Integer2D.fromCoordinates(80,52));
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float z){
		// draw indicator
		if(srcEntity instanceof StillTileEntity){
			if(((StillTileEntity)srcEntity).isBurning()){
				int h = (int)(14*((StillTileEntity)srcEntity).getBurnFraction());
				int d = 14 - h;
				guiContainer.drawTexturedModalRect(x+80, y+49-h, 198, d, 14, h); // x, y, u, v, width, height
				guiContainer.drawTexturedModalRect(x+77, y+16, 198, 14, 24, 16); // x, y, u, v, width, height
			}
			FluidStack fs1 = ((StillTileEntity)srcEntity).getInputTank().getFluid();
			if(fs1 != null && ((StillTileEntity)srcEntity).getInputTank().getFluidAmount() > 0){
				FluidTankGUI.drawFluidBar(fs1, 
						(float)((StillTileEntity)srcEntity).getInputTank().getFluidAmount()/(float)((StillTileEntity)srcEntity).getInputTank().getCapacity(), 
						40, 9, guiDisplayImage, guiContainer, x, y, z);
			}
			FluidStack fs2 = ((StillTileEntity)srcEntity).getTank().getFluid();
			if(fs2 != null && ((StillTileEntity)srcEntity).getTank().getFluidAmount() > 0){
				FluidTankGUI.drawFluidBar(fs2, 
						(float)((StillTileEntity)srcEntity).getTank().getFluidAmount()/(float)((StillTileEntity)srcEntity).getTank().getCapacity(), 
						120, 9, guiDisplayImage, guiContainer, x, y, z);
			}
		}
	}

	

	


}
