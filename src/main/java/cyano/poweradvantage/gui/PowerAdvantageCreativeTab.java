package cyano.poweradvantage.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerAdvantageCreativeTab extends CreativeTabs {
	// TODO: documentation
	public PowerAdvantageCreativeTab( String unlocalizedName) {
		super(unlocalizedName);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getTabIconItem() {
		// TODO: choose better item for icon
		return cyano.poweradvantage.init.Items.bioplastic_ingot;
	}
}

