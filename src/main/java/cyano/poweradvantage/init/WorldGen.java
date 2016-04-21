package cyano.poweradvantage.init;

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
			+ "					\"blockID\":\"poweradvantage:crude_oil\",\n"
			+ "					\"size\":64,\n"
			+ "					\"variation\":50,\n"
			+ "					\"frequency\":0.25,\n"
			+ "					\"minHeight\":16,\n"
			+ "					\"maxHeight\":64,\n"
			+ "					\"biomes\":[]\n"
			+ "				}\n"
			+ "			]\n"
			+ "		}\n"
			+ "	]\n"
			+ "}";
}
