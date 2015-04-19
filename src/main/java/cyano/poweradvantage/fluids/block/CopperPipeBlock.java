package cyano.poweradvantage.fluids.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.BlockSimplePowerConduit;

public class CopperPipeBlock extends BlockSimplePowerConduit{

	public CopperPipeBlock() {
		super(Material.iron, cyano.basemetals.init.Materials.copper.getMetalBlockHardness(), 3f/16f, new ConduitType("fluid"));
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}
	
}
