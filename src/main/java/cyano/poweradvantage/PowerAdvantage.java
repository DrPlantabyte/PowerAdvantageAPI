package cyano.poweradvantage;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import cyano.poweradvantage.api.example.ExamplePowerMod;
import cyano.poweradvantage.registry.MachineGUIRegistry;

// NOTE: other mods dependant on this one need to add the following to their @Mod annotation:
// dependencies = "required-after:poweradvantage" 
@Mod(modid = PowerAdvantage.MODID, version = PowerAdvantage.VERSION, name=PowerAdvantage.NAME)
public class PowerAdvantage
{
    public static final String MODID = "poweradvantage";
    public static final String NAME = "Power Advantage";
    public static final String VERSION = "0.0.3";

    public static boolean DEMO_MODE = false;
    
    private static PowerAdvantage instance;
    
    private ExamplePowerMod exampleMod = null;
    
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
    		exampleMod = new ExamplePowerMod();
        	exampleMod.preInit(event, proxy, config);
    	}
    	
    	config.save();
    	
    	//OreDictionary.initVanillaEntries();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {

		NetworkRegistry.INSTANCE.registerGuiHandler(PowerAdvantage.getInstance(), MachineGUIRegistry.getInstance());
    	
		// TODO
    	if(DEMO_MODE)exampleMod.init(event, proxy);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	// TODO
    	if(DEMO_MODE)exampleMod.postInit(event, proxy);
    }

	public static PowerAdvantage getInstance() {
		return instance;
	}
    
}
