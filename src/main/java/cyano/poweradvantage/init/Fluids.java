package cyano.poweradvantage.init;

import cyano.poweradvantage.api.ConduitType;

public abstract class Fluids {


	private static boolean initDone = false;

	public static final ConduitType fluidConduit_general = new ConduitType("fluid");
	public static ConduitType fluidConduit_water;
	public static ConduitType fluidConduit_lava;
	
	public static void init(){
		if(initDone) return;

		fluidConduit_water = new ConduitType("water");
		fluidConduit_lava = new ConduitType("lava");
		
		// TODO: add crude oil (and make crude oil cause slowness upon exposure)
		
		initDone = true;
	}
}
