package cyano.poweradvantage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.example.ExamplePowerMod;
import cyano.poweradvantage.registry.FuelRegistry;
import cyano.poweradvantage.registry.MachineGUIRegistry;

// NOTE: other mods dependant on this one need to add the following to their @Mod annotation:
// dependencies = "required-after:poweradvantage" 

// TODO: list
/* TODO list
 * drain block
 * fluid pipes
 * spigot block
 * gauge block
 * item chute
 * example crusher machine
 * example expanded furnace machine
 * -- SteamAdvantage mod --
 * Coal-Fired Steam Boiler
 * Boiler Tank
 * Steam Conduit
 * Steam Powered Rock Crusher
 * Steam Powered Blast Furnace (expanded furnace 2x2)
 * Steam Powered Drill
 * Steam Powered Lift (pushes up special lift blocks, like an extendable piston)
 * Steam Powered Assembler (auto-crafter)
 * Steam Powered Defense Cannon (manual aiming and requires redstone trigger)
 * Oil-Burning Steam Boiler
 * Bioreactor (slowly makes liquid fuel from organic matter)
 * -- ElectricalAdvantage --
 * Electricity Conduit
 * Steam Powered Generator
 * Combustion Generator (fuel burning)
 * Solar Generator
 * Water Turbine Generator
 * Wind Turbine Generator 
 * Electric Battery
 * Electric Furnace
 * Electric Arc Furnace (expanded furnace 3x3)
 * Electric Rock Crusher
 * Electric Drill
 * Electric Assembler
 * Electric Defense Cannon (auto-aiming and automatically attacks hostile mobs)
 * Electric Tools
 * Electric Charging Station
 * Electric Robot Station
 * Robots:
 * - Farm-bot (farms)
 * - Kill-bot (kills mobs)
 * - Drill-bot (digs)
 * - Builder-bot (builds villager homes)
 * 
 */

/**
 * This is the main mod class for Power Advantage. Forge/FML will create an 
 * instance of this class and call its initializer functions. There are some 
 * utility functions in this class that makers of add-on mods may wish to use. 
 * @author DrCyano
 *
 */
@Mod(modid = PowerAdvantage.MODID, version = PowerAdvantage.VERSION, name=PowerAdvantage.NAME, dependencies = "required-after:basemetals")
public class PowerAdvantage
{
	/** The identifier for this mod */
    public static final String MODID = "poweradvantage";
    /** The display name for this mod */
    public static final String NAME = "Power Advantage";
    /** The version of this mod, in the format major.minor.update */
    public static final String VERSION = "0.0.4";

    /** if true, example machines will be added to the game */
    public static boolean DEMO_MODE = false;
    /** singleton instance */
    private static PowerAdvantage instance;
    /** Demo mod content */
    private ExamplePowerMod exampleMod = null;
    
    
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
        	exampleMod.preInit(event, config);
    	}
    	
    	config.save();
		// TODO: have a "post-apocalypse" mode where certain key technologies are not craftable but can be found in treasure chests
		// TODO: have a "invention" mode where certain key technologies are dependant on other technologies to be crafted, forcing players to advance up a tech tree
    	
    	cyano.poweradvantage.init.Blocks.init();
    	cyano.poweradvantage.init.Items.init();
    	
    	// keep this comment, it is useful for finding Vanilla recipes
    	//OreDictionary.initVanillaEntries();
    	if(event.getSide() == Side.CLIENT){
			clientPreInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverPreInit(event);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void clientPreInit(FMLPreInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPreInit(FMLPreInitializationEvent event){
		// client-only code
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
		GameRegistry.registerFuelHandler(FuelRegistry.getInstance());
	
		cyano.poweradvantage.init.Fuels.init();
		cyano.poweradvantage.init.Recipes.init();
		cyano.poweradvantage.init.Villages.init();  
		
    	if(DEMO_MODE)exampleMod.init(event);
    	
    	if(event.getSide() == Side.CLIENT){
			clientInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverInit(event);
		}
	}
	

	@SideOnly(Side.CLIENT)
	private void clientInit(FMLInitializationEvent event){
		// client-only code
		cyano.basemetals.init.Items.registerItemRenders(event);
		cyano.basemetals.init.Blocks.registerItemRenders(event);
	}
	@SideOnly(Side.SERVER)
	private void serverInit(FMLInitializationEvent event){
		// client-only code
	}

    /**
     * Post-initialization step. Used for cross-mod options
     * @param event FML event object
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	if(DEMO_MODE)exampleMod.postInit(event);
    	
    }
	

	@SideOnly(Side.CLIENT)
	private void clientPostInit(FMLPostInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPostInit(FMLPostInitializationEvent event){
		// client-only code
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
