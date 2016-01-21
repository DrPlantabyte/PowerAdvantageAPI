package cyano.poweradvantage.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

public abstract class WorldGen {

	private static boolean initDone = false;
	public static void init(){
		if(initDone)return;
		Items.init();
		Blocks.init();
		
		
		initDone = true;
	}
	
	
	
	public static final String ORESPAWN_FILE_CONTENTS = "{\n"
			+ "	\"dimensions\":[\n"
			+ "		{\n"
			+ "			\"dimension\":\"+\",\n"
			+ "			\"ores\":[\n"
			+ "				{\n"
			+ "					\"blockID\":\"poweradvantage:crude_oil_block\",\n"
			+ "					\"size\":64,\n"
			+ "					\"variation\":50,\n"
			+ "					\"frequency\":0.1,\n"
			+ "					\"minHeight\":32,\n"
			+ "					\"maxHeight\":64,\n"
			+ "					\"biomes\":[]\n"
			+ "				}\n"
			+ "			]\n"
			+ "		}\n"
			+ "	]\n"
			+ "}";
}
