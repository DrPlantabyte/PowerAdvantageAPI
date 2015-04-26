package cyano.poweradvantage.init;

import net.minecraft.block.material.*;

public class Materials {

	public static Material crude_oil;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		crude_oil = new MaterialLiquid(MapColor.blackColor);
		
		initDone = true;
	}
}
