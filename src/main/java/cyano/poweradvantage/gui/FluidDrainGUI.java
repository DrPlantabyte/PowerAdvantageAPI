package cyano.poweradvantage.gui;

import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.fluids.block.FluidDrainTileEntity;
import cyano.poweradvantage.math.Integer2D;

public class FluidDrainGUI extends SimpleMachineGUI{

	public FluidDrainGUI() {
		super(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_drain_gui.png"),
				new Integer2D[0]);
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){
		// draw flame
		if(srcEntity instanceof FluidDrainTileEntity){
			FluidStack fs = ((FluidDrainTileEntity)srcEntity).getFluid();
			if(fs != null){
				int h = 50 * fs.amount / ((FluidDrainTileEntity)srcEntity).getFluidCapacity();
				guiContainer.drawTexturedModalRect(x+80, y+9, fs.getFluid().getIcon(), 16, 50);

				guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
				guiContainer.drawTexturedModalRect(x+80, y+59-h, 80, 9, 16, 50-h); // x, y, textureOffsetX, textureOffsetY, width, height)
			}
		}
		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 56); // x, y, textureOffsetX, textureOffsetY, width, height)
	}

}
