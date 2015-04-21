package cyano.poweradvantage.machines;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.BlockSimpleFluidConduit;

public class CopperPipeBlock extends BlockSimpleFluidConduit{

	public CopperPipeBlock() {
		super(Material.piston, cyano.basemetals.init.Materials.copper.getMetalBlockHardness(), 3f/16f);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}

	
}
