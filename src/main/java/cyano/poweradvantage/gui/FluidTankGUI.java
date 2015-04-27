package cyano.poweradvantage.gui;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.fluid.FluidPoweredEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;

public class FluidTankGUI extends SimpleMachineGUI{

	
	public FluidTankGUI(ResourceLocation image) {
		super(image,
				new Integer2D[0]);
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){

		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		// draw indicator
		if(srcEntity instanceof FluidPoweredEntity){
			FluidStack fs = ((FluidPoweredEntity)srcEntity).getTank().getFluid();
			if(fs != null && ((FluidPoweredEntity)srcEntity).getTank().getFluidAmount() > 0){
				int h = 60 * fs.amount / ((FluidPoweredEntity)srcEntity).getTank().getCapacity();
				guiContainer.drawRect(x+80, y+69-h,x+80+16, y+69,fs.getFluid().getBlock()
						.getMaterial().getMaterialMapColor().colorValue | 0xFF000000); 
			}
		}
		GlStateManager.color(1f,1f,1f,1f); // reset tint
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	


}
