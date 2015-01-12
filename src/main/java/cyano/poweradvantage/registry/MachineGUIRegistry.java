package cyano.poweradvantage.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MachineGUIRegistry  implements IGuiHandler {
	private static final AtomicInteger guiIDCounter = new AtomicInteger(1);
	private static final Map<Integer,ITileEntityGUI> guiTable = new HashMap<Integer,ITileEntityGUI>();
	
	private MachineGUIRegistry(){
		// using singleton instantiation
	}
	
	private static final Lock initLock = new ReentrantLock();
	private static MachineGUIRegistry instance = null;
	public static MachineGUIRegistry getInstance(){
		if(instance == null){
			initLock.lock();
			try{
				if(instance == null){
					// thread-safe singleton instantiation
					instance = new MachineGUIRegistry();
				}
			} finally{
				initLock.unlock();
			}
		}
		return instance;
	}
	
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
	/**
	 * Removes a GUI by its ID that was returned by the addGUI(...) method.
	 * @param guiID
	 */
	public void removeGUI(int guiID){
		guiTable.remove(guiID);
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
