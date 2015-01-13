package cyano.poweradvantage.api.example;

import net.minecraft.util.ResourceLocation;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.api.simple.SimpleMachineGUI.GUIContainer;
import cyano.poweradvantage.math.Integer2D;

public class RedstoneGeneratorGUI extends SimpleMachineGUI {

	public RedstoneGeneratorGUI() {
		super(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/example_redstone_generator.png"), new Integer2D[]{new Integer2D(78,22)});
	}

	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){
		// draw flame
		if(srcEntity instanceof RedstoneGeneratorTileEntity){
			int flameHeight = (int)(((RedstoneGeneratorTileEntity)srcEntity).getBurnFraction() * 14);
			guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
			guiContainer.drawTexturedModalRect(x+79, y+42, 177, 14-flameHeight, 14, flameHeight); // x, y, textureOffsetX, textureOffsetY, width, height)
			
		}
	}
}
