package cyano.poweradvantage.blocks;

import net.minecraft.block.properties.PropertyDirection;
import cyano.poweradvantage.api.GUIBlock;


/**
 * Conveyor blocks basically act like the item ducts frmo the item duct mod. They pull items out of 
 * an inventory behind them and insert them into an inventory in front of them. 
 * @author DrCyano
 *
 */
public class BlockConveyor extends GUIBlock{
	/**
	 * Blockstate property
	 */
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
}
