package cyano.poweradvantage.api.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class TesterGUIHandler  implements IGuiHandler {
	private static final AtomicInteger guiIDCounter = new AtomicInteger(0);
	private static final Map<Integer,ITileEntityGUI> guiTable = new HashMap<Integer,ITileEntityGUI>();
	/**
	 * 
	 * @param gui
	 * @return The gui id that the gui was registered to.
	 */
	public static int addGUI(ITileEntityGUI gui){
		int id = guiIDCounter.getAndIncrement();
		guiTable.put(id, gui);
		return id;
	}
	
	
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x,y,z));
		if(guiTable.containsKey(id)) {
			return guiTable.get(id).getContainerGUI(tileEntity, player);
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x,y,z));
		if(guiTable.containsKey(id)) {
			return guiTable.get(id).getContainer(tileEntity, player);
		}
		return null;
	}

}
