package cyano.poweradvantage.api.example;

import net.minecraft.util.ResourceLocation;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.api.simple.SimpleMachineGUI.GUIContainer;
import cyano.poweradvantage.math.Integer2D;

public class RedstoneFurnaceGUI  extends SimpleMachineGUI {
	public RedstoneFurnaceGUI(){
		super(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/example_redstone_furnace.png"), new Integer2D[]{new Integer2D(58,34), new Integer2D(109,34)});
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){
		// draw flame
		if(srcEntity instanceof RedstoneFurnaceTileEntity){
			float cookFraction = ((RedstoneFurnaceTileEntity)srcEntity).getCookFraction();
			int arrowLength = (int)(cookFraction * 24f);
			System.out.println("Draw arrow "+((RedstoneFurnaceTileEntity)srcEntity).cookTime);
			guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
			guiContainer.drawTexturedModalRect(x+79, y+35, 177, 15, arrowLength, 17); // x, y, textureOffsetX, textureOffsetY, width, height)
			
		}
	}
}
