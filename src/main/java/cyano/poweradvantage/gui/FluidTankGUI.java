package cyano.poweradvantage.gui;

import cyano.poweradvantage.api.fluid.FluidPoweredEntity;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

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

		FluidTankGUI.drawFluidFilledRectangle(guiContainer, fs,x+xPos,y+yPos+barSlotHeight-h,w,h,zLevel);
		//guiContainer.drawModalRectWithCustomSizedTexture(x+xPos, y+yPos+barSlotHeight-h, 0, 0, w, h, 16, h);//h * texPerPixel); // x, y, u, v, width, height, textureWidth, textureHeight
		
		guiContainer.mc.renderEngine.bindTexture(displayImage);
		guiContainer.drawTexturedModalRect(x+xPos-3, y+yPos-3, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	public static void drawFluidFilledRectangle(GUIContainer guiContainer, FluidStack fs,
												int x, int y, int width, int height,
												float zLevel){
		if(fs == null || fs.getFluid() == null) return;
		ResourceLocation fluidTexture = realTextureLocationCache.computeIfAbsent(fs.getFluid().getStill(fs),
				(ResourceLocation r) -> new ResourceLocation(r.getResourceDomain(),"textures/".concat(r.getResourcePath()).concat(".png"))
		);
		guiContainer.mc.renderEngine.bindTexture(fluidTexture);

		int colorARGB = fs.getFluid().getColor(fs);
		float oneOver256 = 0.00390625F;
		//float alpha = oneOver256 * ((colorARGB >>> 24) & 0xFF);
		float alpha = 1f;
		float red   = oneOver256 * ((colorARGB >>> 16) & 0xFF);
		float green = oneOver256 * ((colorARGB >>>  8) & 0xFF);
		float blue  = oneOver256 * ((colorARGB       ) & 0xFF);

		float f1 = 0.00390625F * 16;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexbuffer
				.pos((double)(x + 0), (double)(y + height), (double)zLevel)
				.tex(0, f1)
				.color(red,green,blue,alpha).endVertex();
		vertexbuffer
				.pos((double)(x + width), (double)(y + height), (double)zLevel)
				.tex(f1, f1)
				.color(red,green,blue,alpha).endVertex();
		vertexbuffer
				.pos((double)(x + width), (double)(y + 0), (double)zLevel)
				.tex(f1, 0)
				.color(red,green,blue,alpha).endVertex();
		vertexbuffer
				.pos((double)(x + 0), (double)(y + 0), (double)zLevel)
				.tex(0, 0)
				.color(red,green,blue,alpha).endVertex();
		tessellator.draw();
	}


}
