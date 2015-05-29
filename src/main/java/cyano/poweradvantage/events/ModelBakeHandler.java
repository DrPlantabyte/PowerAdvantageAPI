package cyano.poweradvantage.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.graphics.FluidBlockModel;
import cyano.poweradvantage.init.Fluids;


@SideOnly(Side.CLIENT)
public class ModelBakeHandler {

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{
		event.modelRegistry.putObject(new ModelResourceLocation("poweradvantage:block_oil", "normal"), new FluidBlockModel());
		
		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		Fluids.crude_oil.setIcons(textureMap.getAtlasSprite("poweradvantage:blocks/block_oil_still"), textureMap.getAtlasSprite("minestuck:blocks/block_oil_flowing"));
		
	}
}
