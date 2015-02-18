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
		ExpandedFurnaceShapedHandler shaped = new ExpandedFurnaceShapedHandler();
		codechicken.nei.api.API.registerRecipeHandler(shaped);
		codechicken.nei.api.API.registerUsageHandler(shaped);

		ExpandedFurnaceShapelessHandler shapeless = new ExpandedFurnaceShapelessHandler();
		codechicken.nei.api.API.registerRecipeHandler(shapeless);
		codechicken.nei.api.API.registerUsageHandler(shapeless);
	}

}
