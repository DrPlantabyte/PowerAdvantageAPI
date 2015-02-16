package cyano.poweradvantage.nei;

import codechicken.nei.api.IConfigureNEI;
import cyano.poweradvantage.PowerAdvantage;

public class NEIPowerAdvantageConfig implements IConfigureNEI{

	// see https://www.youtube.com/watch?v=8CtcExhsplg for a tutorial
	
	// NEI is supposed to automatically detect classes with the name NEI*Config 
	@Override
	public String getName() {
		return "PowerAdvantage NEI Plugin";
	}

	@Override
	public String getVersion() {
		return PowerAdvantage.VERSION;
	}

	@Override
	public void loadConfig() {
	//	codechicken.nei.api.API.registerRecipeHandler(Handler);
	//	codechicken.nei.api.API.registerUsageHandler(Handler);
		// TODO register NEI stuff
	}

}
