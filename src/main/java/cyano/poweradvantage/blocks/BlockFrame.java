package cyano.poweradvantage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is for blocks that should act like glass (renderring-wise), but can be harvested 
 * normally.
 * @author DrCyano
 *
 */
public class BlockFrame extends Block {

	public BlockFrame(Material m) {
		super(m);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}
	


	@Override
	public boolean isOpaqueCube() {
		return false;
	}


}
