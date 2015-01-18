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
/**
 * This is the main mod class for Power Advantage. Forge/FML will create an 
 * instance of this class and call its initializer functions. There are some 
 * utility functions in this class that makers of add-on mods may wish to use. 
 * @author DrCyano
 *
 */
@Mod(modid = PowerAdvantage.MODID, version = PowerAdvantage.VERSION, name=PowerAdvantage.NAME)
public class PowerAdvantage
{
	/** The identifier for this mod */
    public static final String MODID = "poweradvantage";
    /** The display name for this mod */
    public static final String NAME = "Power Advantage";
    /** The version of this mod, in the format major.minor.update */
    public static final String VERSION = "0.0.3";

    /** if true, example machines will be added to the game */
    public static boolean DEMO_MODE = false;
    /** singleton instance */
    private static PowerAdvantage instance;
    /** Demo mod content */
    private ExamplePowerMod exampleMod = null;
    /** This proxy is used to divide client-only content from server-side execution */
    @SidedProxy(clientSide="cyano.poweradvantage.ClientProxy", serverSide="cyano.poweradvantage.ServerProxy")
    public static Proxy proxy;
    
    
    /**
     * Pre-initialization step. Used for initializing objects and reading the 
     * config file
     * @param event FML event object
     */
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
    	
    	if(DEMO_MODE){
    		exampleMod = new ExamplePowerMod();
        	exampleMod.preInit(event, proxy, config);
    	}
    	
    	config.save();
    	
    	// keep this comment, it is useful for finding Vanilla recipes
    	//OreDictionary.initVanillaEntries();
    }

    /**
     * Initialization step. Used for adding renderers and most content to the 
     * game
     * @param event FML event object
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {

		NetworkRegistry.INSTANCE.registerGuiHandler(PowerAdvantage.getInstance(), MachineGUIRegistry.getInstance());
    	
    	if(DEMO_MODE)exampleMod.init(event, proxy);
    }

    /**
     * Post-initialization step. Used for cross-mod options
     * @param event FML event object
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	if(DEMO_MODE)exampleMod.postInit(event, proxy);
    }
/**
 * Gets a singleton instance of this mod. Is null until the completion of the 
 * pre-initialization step
 * @return The global instance of this mod
 */
	public static PowerAdvantage getInstance() {
		return instance;
	}
    
}
