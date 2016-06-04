package cyano.poweradvantage.api.modsupport;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

/**
 * This class is a superclass used by machines that convert between other types of power. The way it 
 * works is that when its Power Advantage energy buffer is less than half-full, it pulls energy from 
 * the other mod's energy system to fill the Power Advantage energy up to half-full. If the energy 
 * buffer is greater than half-full, it turns power advantage energy into the other mod's energy 
 * type.
 * @author DrCyano
 *
 */
public abstract class TileEntityConverter extends TileEntitySimplePowerMachine {

	private final float halfFull;
	
	final int[] dataArray = new int[3];
	final int INDEX_ENERGY = 0;
	final int INDEX_OTHER_ENERGY_LOWER = 1;
	final int INDEX_OTHER_ENERGY_UPPER = 2;
	private final ConduitType type;
	
	public TileEntityConverter(ConduitType type, float powerAdvantageEnergyBuffer, String unlocalizedName) {
		super(type, powerAdvantageEnergyBuffer, unlocalizedName);
		halfFull = powerAdvantageEnergyBuffer * 0.5f;
		this.type = type;
	}

	final int[] dataArrayOld = new int[dataArray.length];
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		float delta = getEnergy(type) - halfFull;
		if(delta > 0){
			// convert to other mod energy
			if(getOtherEnergy() < getOtherEnergyCapacity()){
				this.subtractEnergy(
						addEnergyToOther(delta, type), type);
			}
		} else if(delta < 0){
			// convert from other mod energy
			if(getOtherEnergy() > 0){
				this.addEnergy(
						-1 * subtractEnergyFromOther(-1 * delta,type), type);
			}
		}
		if(!Arrays.equals(dataArray, dataArrayOld)){
			this.sync();
		}
		System.arraycopy(dataArray, 0, dataArrayOld, 0, dataArrayOld.length);
	}

	/**
	 * Gets the amount of energy in the non-power-advantage energy buffer 
	 * @return Energy in non-power-advantage energy buffer
	 */
	public abstract double getOtherEnergy();
	/**
	 * Gets the energy capacity of the non-power-advantage energy buffer 
	 * @return Energy capacity
	 */
	public abstract double getOtherEnergyCapacity();
	/**
	 * Sets the amount of energy in the non-power-advantage energy buffer 
	 * @param value Energy in non-power-advantage energy buffer
	 */
	public abstract void setOtherEnergy(double value);
	/**
	 * Determines whether the given Power Advantage energy type can be converted
	 * @param type Power Advantage energy type
	 * @return true if the energy can be converted, false otherwise
	 */
	public abstract boolean canConvertEnergy(ConduitType type);
	/**
	 * Adds Power Advantage energy to the non-power-advantage energy buffer. The implementation must 
	 * convert the energy to the other energy type and return the amount of Power Advantage energy 
	 * that was actually consumed.
	 * @param amount Amount of Power Advantage energy being added
	 * @param type The type of Power Advantage energy
	 * @return The amount of Power Advantage that was actually used to add to the other energy 
	 * buffer
	 */
	public abstract float addEnergyToOther(float amount, ConduitType type);
	/**
	 * Withdraws Power Advantage energy to the non-power-advantage energy buffer. The implementation 
	 * must convert the energy to the other energy type and return the amount of Power Advantage 
	 * energy that was actually produced.
	 * @param amount Requested amount of Power Advantage energy
	 * @param type The type of Power Advantage energy
	 * @return The amount of Power Advantage that was actually produced from the other energy 
	 * buffer
	 */
	public abstract float subtractEnergyFromOther(float amount, ConduitType type);
	/**
	 * Performs an energy conversion between Power Advantage energy and the other energy type.
	 * @param amountOfOther Amount of non-power-advantage energy to convert
	 * @param type Type of Power Advantage energy
	 * @return Amount of Power Advantage energy equivalent to the input energy.
	 */
	public abstract float convertOtherToEnergy(double amountOfOther, ConduitType type);
	/**
	 * Performs an energy conversion between Power Advantage energy and the other energy type.
	 * @param amountOfEnergy Amount of Power Advantage energy to convert
	 * @param type Type of Power Advantage energy
	 * @return Amount of non-power-advantage energy equivalent to the input energy.
	 */
	public abstract double convertEnergyToOther(float amountOfEnergy, ConduitType type);
	
	
	private final ItemStack[] inv = new ItemStack[0];
	@Override
	protected ItemStack[] getInventory() {
		// no inventory
		return inv;
	}

	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		dataArray[INDEX_ENERGY] = Float.floatToRawIntBits(getEnergy(type));
		long otherBits = Double.doubleToRawLongBits(getOtherEnergy());
		dataArray[INDEX_OTHER_ENERGY_LOWER] = (int)((otherBits      ) & 0xFFFFFFFF);
		dataArray[INDEX_OTHER_ENERGY_UPPER] = (int)((otherBits >> 32) & 0xFFFFFFFF);
	}

	@Override
	public void onDataFieldUpdate() {
		this.setEnergy(Float.intBitsToFloat(dataArray[INDEX_ENERGY]), type);
		long otherBits = ((long)dataArray[INDEX_OTHER_ENERGY_UPPER] << 32) | ((long)dataArray[INDEX_OTHER_ENERGY_LOWER]);
		this.setOtherEnergy(Double.longBitsToDouble(otherBits));
	}

	
	@Override
	public PowerRequest getPowerRequest(ConduitType offer){
		if(ConduitType.areSameType(offer, type)){
			return new PowerRequest(PowerRequest.LOW_PRIORITY,this.getEnergyCapacity(type) - this.getEnergy(type), this);
		}
		return PowerRequest.REQUEST_NOTHING;
	}
	
	@Override
	public byte getMinimumSinkPriority(){
		return PowerRequest.LAST_PRIORITY;
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if(tagRoot.hasKey("Other")){
			this.setOtherEnergy(tagRoot.getDouble("Other"));
		}
	}
	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setDouble("Other", this.getOtherEnergy());
		return tagRoot;
	}


	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return true;
	}

	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return true;
	}
}
