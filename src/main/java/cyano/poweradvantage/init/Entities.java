package cyano.poweradvantage.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.fluids.block.FluidDrainTileEntity;
import cyano.poweradvantage.gui.FluidDrainGUI;
import cyano.poweradvantage.registry.MachineGUIRegistry;

public abstract class Entities {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		
		GameRegistry.registerTileEntity(FluidDrainTileEntity.class,PowerAdvantage.MODID+".tileentity."+"fluid_drain");
		
		// TODO: block GUIs
		
		initDone = true;
	}
}
