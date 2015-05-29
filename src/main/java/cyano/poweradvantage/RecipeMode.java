package cyano.poweradvantage;

/**
 * Differnet recipe modes change the difficulty and means by which the players make the core items 
 * used in machine recipes. In NORMAL mode, everything needed is craftable from vanilla items and 
 * the machines are available pretty much as soon as the player returns from their first mining 
 * expedition. In APOCALYPTIC mode, some important items are not craftable, but can be found in 
 * treasure chests, requiring the players to pillage for their machines. In TECH_PROGRESSION mode, 
 * important items are very complicated to craft using vanilla items, but are easy to duplicate 
 * once they are made. This gives the players a sense of invention and rising throught he ages from 
 * stone-age to space-age.
 * @author DrCyano
 *
 */
public enum RecipeMode {
	/** Simple crafting recipes */
	NORMAL, 
	/** missing crafting recipes, parts found in treasure chests */
	APOCALYPTIC, 
	/** complicated recipes to make your first machine, then easy recipes for duplicating machines */
	TECH_PROGRESSION
}
