package cyano.poweradvantage.api.modsupport;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;
import net.minecraft.item.ItemStack;

/**
 * This class is a superclass used by machines that convert between other types of power. The way it 
 * works is that when its Power Advantage energy buffer is less than half-full, it pulls energy from 
 * the other mod's energy system to fill the Power Advantage energy up to half-full. If the energy 
 * buffer is greater than half-full, it turns power advantage energy into the other mod's energy 
 * type.
 * @author DrCyano
 *
 */
public class TileEntityConverter extends TileEntitySimplePowerSource{

	private final float halfFull;
	
	int[] dataArray = new int[2];
	final int INDEX_ENERGY = 0;
	final int INDEX_OTHER_ENERGY = 1;
	
	
	public TileEntityConverter(ConduitType type, float powerAdvantageEnergyBuffer, String unlocalizedName) {
		super(type, powerAdvantageEnergyBuffer, unlocalizedName);
		halfFull = powerAdvantageEnergyBuffer * 0.5f;
	}

	
	@Override
	public void powerUpdate(){
		if(this.getEnergy() > halfFull){
			// TODO: convert to other mod energy
		} else if(this.getEnergy() < halfFull){
			// TODO: convert from other mod energy
		}
	}

	public abstract Number getOtherEnergy();
	public abstract Number getOtherEnergyCapacity();
	public abstract boolean canConvertEnergy(ConduitType type);
	public abstract Number convertEnergy(float amount, ConduitType type);
	public abstract void setOtherEnergy(Number value);
	
	
	private final ItemStack[] inv = new ItemStack[0];
	@Override
	protected ItemStack[] getInventory() {
		// no inventory
		return inv;
	}

	@Override
	public int[] getDataFieldArray() {
		// TODO: data sync
		return null;
	}

	@Override
	public void prepareDataFieldsForSync() {
		// TODO: data sync
	}

	@Override
	public void onDataFieldUpdate() {
		// TODO: data sync
	}

	@Override
	public boolean isPowerSink() {
		return true;
	}
	
	@Override
	public PowerRequest getPowerRequest(ConduitType offer){
		if(ConduitType.areSameType(offer, getType())){
			return new PowerRequest(PowerRequest.LAST_PRIORITY,this.getEnergyCapacity() - this.getEnergy(), this);
		}
		return PowerRequest.REQUEST_NOTHING;
	}
	
	@Override
	public byte getMinimumSinkPriority(){
		return PowerRequest.BACKUP_PRIORITY;
	}
}
