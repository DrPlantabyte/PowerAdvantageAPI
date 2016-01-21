package cyano.poweradvantage.gui;

import java.util.HashMap;
import java.util.Map;

import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.machines.fluidmachines.StillTileEntity;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class StillGUI extends SimpleMachineGUI{

	
	private static final Map<ResourceLocation,ResourceLocation> realTextureLocationCache = new HashMap<>();
	
	public StillGUI(ResourceLocation image) {
		super(image,
				Integer2D.fromCoordinates(79,51));
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float z){
		// draw indicator
		if(srcEntity instanceof StillTileEntity){
			FluidStack fs1 = ((StillTileEntity)srcEntity).getInputTank().getFluid();
			if(fs1 != null && ((StillTileEntity)srcEntity).getInputTank().getFluidAmount() > 0){
				final int w = 16;
				final int h = 60 * fs1.amount / ((StillTileEntity)srcEntity).getInputTank().getCapacity();
				ResourceLocation fluidTexture = realTextureLocationCache.computeIfAbsent(fs1.getFluid().getStill(fs1),
						(ResourceLocation r) -> new ResourceLocation(r.getResourceDomain(),"textures/".concat(r.getResourcePath()).concat(".png"))
						);
				guiContainer.mc.renderEngine.bindTexture(fluidTexture);
				int tintARGB = fs1.getFluid().getColor();
				GlStateManager.color(
						((tintARGB >> 24) & 0xFF)/255f,
						((tintARGB >> 16) & 0xFF)/255f,
						((tintARGB >> 8) & 0xFF)/255f,
						((tintARGB     ) & 0xFF)/255f); // set fluid tint
				for(int m = h; m > 0; m -= w){
					int hp = Math.min(m, w);
					guiContainer.drawModalRectWithCustomSizedTexture(x+40, y+69-m, 0, 0, w, hp, 16, 512); // x, y, u, v, width, height, textureWidth, textureHeight
				} 
			}
			FluidStack fs2 = ((StillTileEntity)srcEntity).getTank().getFluid();
			if(fs2 != null && ((StillTileEntity)srcEntity).getTank().getFluidAmount() > 0){
				final int w = 16;
				final int h = 60 * fs2.amount / ((StillTileEntity)srcEntity).getTank().getCapacity();
				ResourceLocation fluidTexture = realTextureLocationCache.computeIfAbsent(fs2.getFluid().getStill(fs2),
						(ResourceLocation r) -> new ResourceLocation(r.getResourceDomain(),"textures/".concat(r.getResourcePath()).concat(".png"))
						);
				guiContainer.mc.renderEngine.bindTexture(fluidTexture);
				int tintARGB = fs2.getFluid().getColor();
				GlStateManager.color(
						((tintARGB >> 24) & 0xFF)/255f,
						((tintARGB >> 16) & 0xFF)/255f,
						((tintARGB >> 8) & 0xFF)/255f,
						((tintARGB     ) & 0xFF)/255f); // set fluid tint
				for(int m = h; m > 0; m -= w){
					int hp = Math.min(m, w);
					guiContainer.drawModalRectWithCustomSizedTexture(x+120, y+69-m, 0, 0, w, hp, 16, 512); // x, y, u, v, width, height, textureWidth, textureHeight
				} 
			}
			if(((StillTileEntity)srcEntity).isBurning()){
				int h = (int)(14*((StillTileEntity)srcEntity).getBurnFraction());
				int d = 14 - h;
				guiContainer.drawModalRectWithCustomSizedTexture(x+80, y+49-h, 198, d, 14, h, 14, h); // x, y, u, v, width, height, textureWidth, textureHeight
				guiContainer.drawModalRectWithCustomSizedTexture(x+77, y+16, 198, 14, 24, 16, 24,16); // x, y, u, v, width, height, textureWidth, textureHeight
			}
		}
		GlStateManager.color(1f,1f,1f,1f); // reset tint
		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	


}
