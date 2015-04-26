package cyano.poweradvantage.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is used to create a creative tab for PowerAdvantage items
 * @author DrCyano
 *
 */
public class PowerAdvantageCreativeTab extends CreativeTabs {
	/**
	 * Constructor
	 * @param unlocalizedName Unlocalized name for the tab
	 */
	public PowerAdvantageCreativeTab( String unlocalizedName) {
		super(unlocalizedName);
	}

	/**
	 * Gets the item used in the tab icon
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public Item getTabIconItem() {
		// TODO: choose better item for icon
		return cyano.poweradvantage.init.Items.bioplastic_ingot;
	}
}

