package cyano.poweradvantage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.events.BucketHandler;
import cyano.poweradvantage.init.WorldGen;
import cyano.poweradvantage.registry.FuelRegistry;
import cyano.poweradvantage.registry.MachineGUIRegistry;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipeRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// NOTE: other mods dependant on this one need to add the following to their @Mod annotation:
// dependencies = "required-after:poweradvantage" 

/* TODO list
 * drain block ✓
 * fluid pipes ✓
 * portable tank block ✓
 * item chute ✓
 * fluid discharge block ✓
 * example crusher machine (SteamAfvantage will be the example mod) ✓
 * -- SteamAdvantage mod --
 * Coal-Fired Steam Boiler ✓
 * Boiler Tank ✓
 * Steam Conduit ✓
 * Steam Powered Rock Crusher ✓
 * Steam Powered Blast Furnace ✓
 * Steam Powered Drill ✓
 * Steam Powered Lift (pushes up special lift blocks, like an extendable piston) ✓
 * Steam Powered Machine Shop (automatable crafter)
 * Musket (slow-loading ranged weapon) ✓
 * Steam Powered Defense Cannon (manual aiming and requires redstone trigger)
 * Oil-Burning Steam Boiler
 * Bioreactor (slowly makes liquid fuel from organic matter)
 * -- ElectricalAdvantage --
 * Electricity Conduit ✓
 * Steam Powered Generator ✓
 * Solar Generator ✓
 * Water Turbine Generator ✓
 * Wind Turbine Generator 
 * Electric Boiler (makes steam power from electricity)
 * Electric Battery ✓
 * Electric Furnace ✓
 * Electric Arc Furnace ✓
 * Electric Rock Crusher ✓
 * Electric Drill ✓
 * Electric Assembler ✓
 * Revolver Pistol (six-shots between slow reloads)
 * Electric Defense Cannon (auto-aiming and automatically attacks hostile mobs) ✓
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
 * IDEAS:
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
 * Instant *** (place on ground to make building instantly appear)
 * Instant Camp
 * Instant Farm
 * Instant House
 * Instant Castle
 */

/**
 * <p>This is the main mod class for Power Advantage. Forge/FML will create an 
 * instance of this class and call its initializer functions. There are some 
 * utility functions in this class that makers of add-on mods may wish to use, 
 * such as PowerAdvantage.getInstance(). </p>
 * <p>
 * Note that to make a mod that uses the PowerAdvantage API, you will need to make a few 
 * adjustments to add the appropriate dependancies.
 * </p><p>
 * First, you need to add the required libraries to your project. Create a folder in your project 
 * called <b>lib</b>. Then grab the PowerAdvantage and BaseMetals API .jar files and put them in 
 * the <b>lib</b> folder. It will also be helpful to put the javadoc .zip files in this folder as 
 * well.
 * </p><p>
 * Next, you need to specify the dependencies in the &#64;mod annotation. For example:<br>
 * <code>@Mod(modid = MyMod.MODID, version = MyMod.VERSION, name=MyMod.NAME, dependencies = "required-after:poweradvantage;required-after:basemetals")</code> 
 * </p><p>
 * After that, you need to update your gradle build script to include the dependencies. Add the 
 * following to <b>build.gradle</b> (adjusting the version numbers on the .jar filenames as 
 * neessary):<br><code>

allprojects {
    apply plugin: 'java'
    sourceCompatibility = 1.7
    targetCompatibility = 1.7
}

dependencies {
    compile files(
        'lib/basemetals-1.6.0-dev.jar'
        'lib/PowerAdvantage-API-1.4.0.jar'
    )

}
</code><br>This is necessary to include the API in your compile build path and also specify that you 
 * are compiling with Java 7 (Java 6 will not work). 
 * </p><p>
 * Finally, you need to add the API .jar files to your IDE project configuration. In Eclipse, 
 * right-click on your project and go to <i>Properties</i>, then click on <i>Java Build Path</i> and 
 * click on the <i>Libraries</i> tab. Then click <i>Add Jars...</i> and select the BaseMetals and 
 * PowerAdvantage API jars in your <b>lib</b> folder and close the window by clicking OK.
 * </p>
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
	public static final String VERSION = "1.4.2";
	
	// TODO: add oil compatibility with BuildCraft
	
	// TODO: add condenser (makes water from steam)
	
	// TODO: add electric distillation machine
	// TODO: add plastic refinery (makes plastic from oil)
	// TODO: add ice machine (uses water and electricity to make blocks of ice)

	/** singleton instance */
	private static PowerAdvantage instance;
	/**
	 * This is the recipe mode set for this mod. Add-on mods may want to adjust their recipes 
	 * accordingly.
	 */
	public static RecipeMode recipeMode = RecipeMode.NORMAL;
	public static boolean useOtherFluids = false;
	/** adjustment for frequency of loot spawns in treasure chests */
	public static float chestLootFactor = 0.5f;
	/** If true, plastic will be registered as rubber in ore dictionary */
	public static boolean plasticIsAlsoRubber = true;
	/** Set to false if all power system mods are add-ons to Power Advantage and you want to improve performance */
	public static boolean enableExtendedModCompatibility = true;
	/** used to convert energy to RF types when attempting autmoatic conversion */
	public static final Map<ConduitType, Float> rfConversionTable = new HashMap<>();

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
		String[] presets = {"NORMAL", "TECH_PROGRESSION", "APOCALYPTIC"};
		String mode = config.getString("recipe_mode", "options", "NORMAL", "NORMAL, APOCALYPTIC, or TECH_PROGRESSION. \n"
		+ "Sets the style of recipes used in your game. \n"
		+ "In NORMAL mode, everything needed is craftable from vanilla items and the machines are \n"
		+ "available pretty much as soon as the player returns from their first mining expedition. \n"
		+ "In APOCALYPTIC mode, some important items are not craftable, but can be found in \n"
		+ "treasure chests, requiring the players to pillage for their machines. \n"
		+ "In TECH_PROGRESSION mode, important items are very complicated to craft using vanilla \n"
		+ "items, but are easy to duplicate once they are made. This gives the players a sense of \n"
		+ "invention and rising throught the ages from stone-age to space-age.",presets).toUpperCase(Locale.US).trim();
		switch (mode){
		case "NORMAL":
			recipeMode = RecipeMode.NORMAL;
			break;

		case "APOCALYPTIC":
			recipeMode = RecipeMode.APOCALYPTIC;
			break;

		case "TECH_PROGRESSION":
			recipeMode = RecipeMode.TECH_PROGRESSION;
			break;

		default:
			FMLLog.severe(MODID+" does not recognize recipe_mode '"+mode+"'");
			throw new IllegalArgumentException("'"+mode+"' is not valid for config option 'recipe_mode'. Valid options are: NORMAL, APOCALYPTIC, or TECH_PROGRESSION");
		}


		chestLootFactor  = config.getFloat("treasure_chest_loot_factor", "options", 0.5f, 0.0f, 1000.0f, 
				"Controls the rarity of items from this mod being found in treasure chests relative to \n"
						+  "the frequency of other chest loot items. Set to 0 to disable metal ingots from \n"
						+  "appearing in treasure chests.");
		
		plasticIsAlsoRubber = config.getBoolean("plastic_equals_rubber", "options", plasticIsAlsoRubber, 
				"If true, then plastic will be useable in recipes as if it were rubber (for cross-mod compatibility)");
		
		useOtherFluids = config.getBoolean("use_other_fluids", "Other Power Mods", useOtherFluids, 
				"If true, then Power Advantage will use existing fluids added by other mods where possible");
		
		
		enableExtendedModCompatibility = config.getBoolean("extended_compatibility", "Other Power Mods", enableExtendedModCompatibility, 
				"If false, then you may have less lag. If true, then some mods that are not Power \n"
				+ "Advantage add-ons may be able to draw power from Power Advantage power generators.");
		if(enableExtendedModCompatibility){
			FMLLog.info("Enabled external power mod interactions. If the server lags when using large power networks, consider disabling the 'extended_compatibility' option");
		}
		
		if(enableExtendedModCompatibility){
			String[] conversions = config.getString("RF_conversions", "Other Power Mods", 
					"steam=1;electricity=0.03125;quantum=1", 
					"List of conversions from Power Advantage power types to RF")
					.split(";");
			for(String c : conversions){
				if(!c.contains("=")) continue;
				String name = c.substring(0, c.indexOf('=')).trim().toLowerCase(Locale.US);
				String val = c.substring(c.indexOf('=')+1).trim();
				try{
					Number d;
					if(val.contains(".")){
						d = new Double(Double.parseDouble(val));
					} else {
						d = Long.parseLong(val);
					}
					FMLLog.info("Adding conversion factor of "+d+" units of RF per unit of "+name);
					rfConversionTable.put(new ConduitType(name), d.floatValue());
				}catch(NumberFormatException ex){
					FMLLog.severe("Cannot parse '"+val+"' as number");
				}
			}
		}

		
		Path orespawnFolder = Paths.get(event.getSuggestedConfigurationFile().toPath().getParent().toString(),"orespawn");
		Path orespawnFile = Paths.get(orespawnFolder.toString(),MODID+".json");
		if(!Files.exists(orespawnFile)){
			try{
				Files.createDirectories(orespawnFile.getParent());
				Files.write(orespawnFile, Arrays.asList(WorldGen.ORESPAWN_FILE_CONTENTS.split("\n")), Charset.forName("UTF-8"));
				cyano.basemetals.BaseMetals.oreSpawnConfigFiles.add(orespawnFile);
			} catch (IOException e) {
				FMLLog.severe(MODID+": Error: Failed to write file "+orespawnFile);
			}
		}
		
		config.save();

		cyano.poweradvantage.init.Fluids.init(); 
		cyano.poweradvantage.init.Blocks.init();
		cyano.poweradvantage.init.Items.init();

		// keep this next comment, it is useful for finding Vanilla recipes
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
		cyano.poweradvantage.init.Blocks.bakeModels();
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
		cyano.poweradvantage.init.Entities.init();
		cyano.poweradvantage.init.Recipes.init();
		cyano.poweradvantage.init.Villages.init(); 
		cyano.poweradvantage.init.GUI.init();
		cyano.poweradvantage.init.TreasureChests.init();

		MinecraftForge.EVENT_BUS.register(BucketHandler.getInstance());

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
		cyano.poweradvantage.init.Items.registerItemRenders(event);
		cyano.poweradvantage.init.Blocks.registerItemRenders(event);
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
		// Clear caches
		DistillationRecipeRegistry.clearRecipeCache();
		
		// Handle inter-mod action
		
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
