package cyano.poweradvantage.init;

public abstract class Villages {
	// TODO: add machinist villager

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Entities.init();
		
		initDone = true;
	}
}
