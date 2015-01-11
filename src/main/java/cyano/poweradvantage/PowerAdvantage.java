package cyano.poweradvantage;

import cyano.poweradvantage.api.example.Tester;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = PowerAdvantage.MODID, version = PowerAdvantage.VERSION, name=PowerAdvantage.NAME)
public class PowerAdvantage
{
    public static final String MODID = "poweradvantage";
    public static final String NAME = "Power Advantage";
    public static final String VERSION = "0.0.2";

    public static boolean DEMO_MODE = false;
    
    private static PowerAdvantage instance;
    
    private Tester test = null;
    
    @SidedProxy(clientSide="cyano.poweradvantage.ClientProxy", serverSide="cyano.poweradvantage.ServerProxy")
    public static Proxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	instance = this;
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	config.load();
    	
    	// demonstration code and examples
    	// TODO: default to false
    	//DEMO_MODE = config.getBoolean("demo", "options", false,
    	DEMO_MODE = config.getBoolean("demo", "options", true,
 "If true, then example machines will be loaded. For testing/example use only!");
    	
    	// TODO
    	
    	if(DEMO_MODE){
    		test = new Tester();
        	test.preInit(event, proxy, config);
    	}
    	
    	config.save();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// TODO
    	if(DEMO_MODE)test.init(event, proxy);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	// TODO
    	if(DEMO_MODE)test.postInit(event, proxy);
    }

	public static PowerAdvantage getInstance() {
		return instance;
	}
    
}
