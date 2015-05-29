package cyano.poweradvantage.blocks;

import net.minecraft.block.material.MapColor;

public class BlockMaterialFluid extends net.minecraft.block.material.MaterialLiquid{

	public BlockMaterialFluid(MapColor color) {
		super(color);
		super.setNoPushMobility();
	}

}
