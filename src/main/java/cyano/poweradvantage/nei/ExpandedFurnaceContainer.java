package cyano.poweradvantage.nei;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ExpandedFurnaceContainer extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return false;
	}

}
