package cyano.poweradvantage.api.simple;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.init.Fluids;

public abstract class BlockSimpleFluidConsumer extends BlockSimplePowerConsumer {
	
	public BlockSimpleFluidConsumer(Material blockMaterial, float hardness, int guiHandlerID, Object ownerOfGUIHandler) {
		super( blockMaterial,  hardness, Fluids.fluidConduit_general,  guiHandlerID,  ownerOfGUIHandler);
	}

}
