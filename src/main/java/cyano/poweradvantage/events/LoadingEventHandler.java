package cyano.poweradvantage.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cyano.poweradvantage.data.PowerAdvantageWorldSaveData;

public class LoadingEventHandler{


	public static final Map<World,PowerAdvantageWorldSaveData> worldData = new HashMap<>();

	@SubscribeEvent(priority=EventPriority.LOW) 
	public void onWorldLoad(WorldEvent.Load loadEvent) {
		// TODO: figure out why there are duplicate networks in the data
		FMLLog.info("Loading data saved in world"); // TODO: remove debug code
		PowerAdvantageWorldSaveData data = PowerAdvantageWorldSaveData.getDataForWorld(loadEvent.world);
		data.markDirty();
		worldData.put(loadEvent.world, data);
		FMLLog.info("Finished loading world data"); // TODO: remove debug code
	}
	@SubscribeEvent(priority=EventPriority.LOW) 
	public void onWorldUnload(WorldEvent.Unload unloadEvent) {
		// do nothing, I guess
		FMLLog.info("World unloading"); // TODO: remove debug code
//		if(worldData.containsKey(unloadEvent.world)){
//			worldData.get(unloadEvent.world).
//		}
	}

}
