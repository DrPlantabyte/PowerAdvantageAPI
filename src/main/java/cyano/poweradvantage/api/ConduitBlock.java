package cyano.poweradvantage.api;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
/**
 * This is the superclass for blocks that conduct power from power generators to 
 * the machines that need power. If you are making an add-on mod, you probably 
 * want to extend the 
 * <b>cyano.poweradvantage.api.simple.BlockSimplePowerConduit</b> class 
 * instead of this class.
 * @author DrCyano
 *
 */
public abstract class ConduitBlock extends net.minecraft.block.Block implements  ITypedConduit{
	/**
	 * Block constructor
	 * @param mat Block material
	 */
	protected ConduitBlock(Material mat) {
		super(mat);
	}
	
}
