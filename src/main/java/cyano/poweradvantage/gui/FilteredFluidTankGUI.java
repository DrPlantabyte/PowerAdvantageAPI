package cyano.poweradvantage.gui;

import cyano.poweradvantage.api.fluid.FluidPoweredEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class FilteredFluidTankGUI extends SimpleMachineGUI{

	
	private static final Map<ResourceLocation,ResourceLocation> realTextureLocationCache = new HashMap<>();
	
	public FilteredFluidTankGUI(ResourceLocation image) {
		super(image,
				Integer2D.fromCoordinates(31, 31));
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float z){
		// draw indicator
		if(srcEntity instanceof FluidPoweredEntity){
			FluidStack fs = ((FluidPoweredEntity)srcEntity).getTank().getFluid();
			if(fs != null && ((FluidPoweredEntity)srcEntity).getTank().getFluidAmount() > 0){
				drawFluidBar(fs,
						(float)fs.amount / (float)((FluidPoweredEntity)srcEntity).getTank().getCapacity(),
						80, 9,
						guiDisplayImage, guiContainer, x, y, z);
			}
		}
		GlStateManager.color(1f,1f,1f,1f); // reset tint
		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	public static void drawFluidBar(FluidStack fs, float barHeight, int xPos, int yPos, 
			ResourceLocation displayImage, GUIContainer guiContainer, int x, int y, float zLevel){
		final int w = 16;
		final int barSlotHeight = 60;
		final int h = (int)(barSlotHeight * barHeight);


		FluidTankGUI.drawFluidFilledRectangle(guiContainer,fs,x+xPos,y+yPos+barSlotHeight-h,w,h,zLevel);
		//guiContainer.drawModalRectWithCustomSizedTexture(x+xPos, y+yPos+barSlotHeight-h, 0, 0, w, h, 16, h);//h * texPerPixel); // x, y, u, v, width, height, textureWidth, textureHeight
		
		guiContainer.mc.renderEngine.bindTexture(displayImage);
		guiContainer.drawTexturedModalRect(x+xPos-3, y+yPos-3, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}
	


}
