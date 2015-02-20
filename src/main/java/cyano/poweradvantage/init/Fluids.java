package cyano.poweradvantage.init;

import net.minecraftforge.fluids.*;

public abstract class Fluids {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		// TODO: add crude oil (and make crude oil cause slowness upon exposure)
		
		initDone = true;
	}
}
