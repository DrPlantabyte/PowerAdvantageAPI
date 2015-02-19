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
 * Musket (slow-loading ranged weapon)
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
 * Electric Boiler (makes steam power from electricity)
 * Electric Battery
 * Electric Furnace
 * Electric Arc Furnace (expanded furnace 3x3)
 * Electric Rock Crusher
 * Electric Drill
 * Electric Assembler
 * Revolver Pistol (six-shots between slow reloads)
 * Electric Defense Cannon (auto-aiming and automatically attacks hostile mobs)
 * Electric Lift (electric version of steam lift)
 * Electric Tools
 * Electric Charging Station
 * Electric Robot Station
 * Robots:
 * - Farm-bot (farms)
 * - Lumber-bot (cuts and plants trees)
 * - Kill-bot (kills mobs with arrows or bullets)
 * - Drill-bot (digs)
 * - Construction-bot (builds villager homes)
 * - Chef-bot (cooks food)
 * - Fish-bot (fishes)
 * 
 * -- MagicAdvantage -- (power from colored mana)
 * Mana:
 * - Red (fire)
 * - Blue (water)
 * - White (air)
 * - Brown (earth)
 * - Purple (darkness, not found naturally in overworld)
 * - Yellow (light, not found naturally in overworld)
 * mana crystal generates in the world and slowly grow over time 
 * fully grown mana crystals are hazardous to be near and sprout new crystals in neighboring air blocks
 * mining a mana crystal drops mana
 * magical machines are powered by mana and have extra effects depending on mane used for power
 * Mana Pool (stores mana and powers near-by magic machines)
 * Mana Focus (giant crystal that extends the range of the mana pool)
 * Red Mana Condenser (turns fuel into red mana)
 * Blue Mana Condenser (generates blue mana if submerged in water)
 * Brown Mana Condenser (turns plants into brown mana)
 * White Mana Condenser (generates white mana while high in the sky)
 * Yellow Mana Condenser (generates yellow mana while sunlight shines on it)
 * Purple Mana Condenser (hurts nearby entities and generates purple mana)
 * Magical Excavator (mines the area)
 * - red mana: smelt items as they're mined
 * - blue mana: mine faster
 * - brown mana: fill mined area with dirt
 * - white mana: mine larger area
 * - yellow mana: occasionally mines a duplicate block (+10% yield)
 * - purple mana: destroy worthless blocks instead of mining them
 * Magical Conjurer (summons entities/items)
 * - red mana: random treasure chest loot item
 * - blue mana: random fishing result
 * - brown mana: random ore or crop
 * - white mana: random animal
 * - yellow mana: random mana shard
 * - purple mana: random hostile mob, occasionally from Nether
 * Magical Spirit Shrine
 * - red mana: attacks hostile mobs and smelts near-by blocks (turns trees into charcoal and cobblestone into stone)
 * - blue mana: plants crops and trees using items in nearby chests
 * - brown mana: adds mining haste effect to nearby players
 * - white mana: harvests crops and leaves and picks-up nearby items and puts them in a chest
 * - yellow mana: lights up the area (places fae lights)
 * - purple mana: hurts all mobs and steals from player inventories
 * Magical Furnace (expanded furnace 3x3)
 * - red mana: smelts more items per mana shard
 * - blue mana: unsmelts items
 * - brown mana: has small change of spitting out a random mana shard after each smelt
 * - white mana: smelts faster (but does not smelt more items per mana shard)
 * - yellow mana: has chance of not consuming input items
 * - purple mana: smelts super fast, with 12.5% loss
 * Magical Mystery Box (does stuff to items placed inside)
 * - red mana: smelts items
 * - blue mana: turns water buckets into ice blocks, lava buckets into obsidian
 * - brown mana: grows/duplicates plant items
 * - white mana: crafts blocks into fancier forms (e.g. stone into stone bricks)
 * - yellow mana: repairs items
 * - purple mana: destroys items, creating XP orbs
 * 
 * -- QuantumAdvantage -- (wireless power)
 * Fusion Generator (burns water for electricity)
 * Quantum Entanglement Generator (powered by electricity, wirelessly sends power to quantum machines and also recharges quantum batteries in player inventories)
 * Quantum Relay (range extender for Quantum Entanglement Generator)
 * Quantum Furnace (super-fast 3x3 expanded furnace)
 * Quantum Teleporter
 * Quantum Disassembler (like crusher, but also yields 1 stone block per ore)
 * Quantum Defense Cannon (automatically hurts all enemy mobs within range)
 * Quantum Quarry (like BuildCraft Quarry)
 * Quantum Farm (manages farming in nearby area)
 * Quantum Tools (powered by a quantum battery in player inventory)
 * Ray Gun (gun with no reload time, powered by a quantum battery in player inventory)
 * Private Quantum Storage Chest (like  ender chest with bigger inventory)
 * Public Quantum Storage Chest (like ender chest, but all players on same team have access)
 * Quantum Shield (worn as chestplate, provides full armor protection and repairs with quantum energy)
 * Quantum Goggles (worn as helmet, lets user see ores and entity through walls and see in the dark)
 * Quantum Leg Enhancements (worn as leggings, bestows fast speed and player can walk up 1-block height change without jumping)
 * Quantum Shoes (slow fall and air jumping, allows for primitive flight)
 * Instant XXX (place on ground to make building instantly appear)
 * Instant Camp
 * Instant Farm
 * Instant House
 * Instant Castle
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
