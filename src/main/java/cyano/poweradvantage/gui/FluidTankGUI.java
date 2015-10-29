package cyano.poweradvantage.gui;

import java.util.HashMap;
import java.util.Map;

import cyano.poweradvantage.api.fluid.FluidPoweredEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankGUI extends SimpleMachineGUI{

	
	private static final Map<ResourceLocation,ResourceLocation> realTextureLocationCache = new HashMap<>();
	
	public FluidTankGUI(ResourceLocation image) {
		super(image,
				new Integer2D[0]);
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float z){
		// draw indicator
		if(srcEntity instanceof FluidPoweredEntity){
			FluidStack fs = ((FluidPoweredEntity)srcEntity).getTank().getFluid();
			if(fs != null && ((FluidPoweredEntity)srcEntity).getTank().getFluidAmount() > 0){
				final int w = 16;
				final int h = 60 * fs.amount / ((FluidPoweredEntity)srcEntity).getTank().getCapacity();
				ResourceLocation fluidTexture = realTextureLocationCache.computeIfAbsent(fs.getFluid().getStill(fs),
						(ResourceLocation r) -> new ResourceLocation(r.getResourceDomain(),"textures/".concat(r.getResourcePath()).concat(".png"))
						);
				guiContainer.mc.renderEngine.bindTexture(fluidTexture);
				int tintARGB = fs.getFluid().getColor();
				GlStateManager.color(
						((tintARGB >> 24) & 0xFF)/255f,
						((tintARGB >> 16) & 0xFF)/255f,
						((tintARGB >> 8) & 0xFF)/255f,
						((tintARGB     ) & 0xFF)/255f); // set fluid tint
				for(int m = h; m > 0; m -= w){
					int hp = Math.min(m, w);
					guiContainer.drawModalRectWithCustomSizedTexture(x+80, y+69-m, 0, 0, w, hp, 16, 512); // x, y, u, v, width, height, textureWidth, textureHeight
				} 
			}
		}
		GlStateManager.color(1f,1f,1f,1f); // reset tint
		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	


}
