package cyano.poweradvantage.machines.fluidmachines;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.BlockSimpleFluidConduit;

public class FluidPipeBlock extends BlockSimpleFluidConduit{

	public FluidPipeBlock() {
		super(Material.piston, 0.75f, 3f/16f);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}

	
}
