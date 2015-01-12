package cyano.poweradvantage.registry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface ITileEntityGUI {
	public abstract net.minecraft.inventory.Container getContainer(TileEntity e, EntityPlayer player);
	public abstract net.minecraft.client.gui.inventory.GuiContainer getContainerGUI(TileEntity e, EntityPlayer player);
}
