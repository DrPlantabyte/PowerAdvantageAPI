package cyano.poweradvantage.init;

import net.minecraft.creativetab.CreativeTabs;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.gui.PowerAdvantageCreativeTab;

public class ItemGroups {
	
	public static CreativeTabs tab_powerAdvantage;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		tab_powerAdvantage = new PowerAdvantageCreativeTab( PowerAdvantage.MODID);
		
		initDone = true;
	}
}
