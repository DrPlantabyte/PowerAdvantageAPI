package cyano.poweradvantage.nei;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cyano.poweradvantage.PowerAdvantage;

public class ExpandedFurnaceGUIContainer extends GuiContainer{

	ResourceLocation guiDisplayImage = new ResourceLocation(PowerAdvantage.MODID+":textures/gui/nei/nei_expanded_furnace.png");
	public ExpandedFurnaceGUIContainer(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.mc.renderEngine.bindTexture(guiDisplayImage);
		this.drawTexturedModalRect(x, y, 0, 0, 176, 76); // x, y, textureOffsetX, textureOffsetY, width, height)
		int flamePercent = (int)(System.currentTimeMillis() % 4000) / 40;
		int h = 14 * (100-flamePercent) / 100;
		int w = 24 * flamePercent / 100;
// TODO: figure out why animation isn't displaying
		if(h > 0)this.drawTexturedModalRect(x+93, y+55-h, 176, 14-h, 14, h); // x, y, textureOffsetX, textureOffsetY, width, height)
		if(w > 0)this.drawTexturedModalRect(x+88, y+34, 176, 14, w, 16); // x, y, textureOffsetX, textureOffsetY, width, height)
	}
	

}
