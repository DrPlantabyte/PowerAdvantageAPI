package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.gui.*;
import cyano.poweradvantage.registry.MachineGUIRegistry;

public abstract class GUI {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		Blocks.fluid_drain.setGuiID(MachineGUIRegistry.addGUI(new FluidDrainGUI()));
		Blocks.fluid_drain.setGuiOwner(PowerAdvantage.getInstance());
		
		Blocks.fluid_discharge.setGuiID(MachineGUIRegistry.addGUI(new FluidDischargeGUI()));
		Blocks.fluid_discharge.setGuiOwner(PowerAdvantage.getInstance());
		
		// TODO: block GUIs
		
		initDone = true;
	}
}
