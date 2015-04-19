package cyano.poweradvantage.events;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.math.BlockPos4D;

public class BlockEventHandler {

	

	@SubscribeEvent(priority=EventPriority.LOW) 
	public void blockBreakEvent(net.minecraftforge.event.world.BlockEvent.BreakEvent be){
		if(be.state.getBlock() instanceof ITypedConduit){
			FMLLog.info("Broke conduit at "+be.pos); // TODO: remove debug code
			ConduitType type = ((ITypedConduit)be.state.getBlock()).getType();
			ConduitRegistry.getInstance().conduitBlockRemovedEvent(be.world,be.world.provider.getDimensionId(), be.pos, type);
		}
	}
	

	@SubscribeEvent(priority=EventPriority.LOW) 
	public void blockPlaceEvent(net.minecraftforge.event.world.BlockEvent.PlaceEvent be){
		if(be.state.getBlock() instanceof ITypedConduit ){
			FMLLog.info("Placed conduit at " + be.pos); // TODO: remove debug code
			ConduitType type = ((ITypedConduit)be.state.getBlock()).getType();
			ConduitRegistry.getInstance().conduitBlockPlacedEvent(be.world,be.world.provider.getDimensionId(), be.pos, type);
		}
	}
}
