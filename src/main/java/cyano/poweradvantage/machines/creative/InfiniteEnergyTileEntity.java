package cyano.poweradvantage.machines.creative;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InfiniteEnergyTileEntity extends TileEntitySimplePowerMachine {

	private final ItemStack[] inventory = new ItemStack[0];
	private final int[] dataArray = new int[0];
	private final ConduitType[] type = new ConduitType[1];
	
	public InfiniteEnergyTileEntity(ConduitType energyType) {
		super(energyType, 1000f, InfiniteEnergyTileEntity.class.getName());
		this.type[0] = energyType;
	}
	

	public InfiniteEnergyTileEntity() {
		super(new ConduitType("power"), 1000f, InfiniteEnergyTileEntity.class.getName());
		this.type[0] = new ConduitType("power");
	}
	
	public void setPowerType(ConduitType type){
		this.type[0] = type;
	}
	
	@Override
	public ConduitType[] getTypes(){
		return type;
	}

	/**
	 * Determines whether this block/entity should receive energy. If this is not a sink, then it
	 * will never be given power by a power source.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return false;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return true;
	}


	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	@Override
	public int[] getDataFieldArray() {
		return dataArray;
	}

	@Override
	public void prepareDataFieldsForSync() {
		// do nothing
		
	}

	@Override
	public void onDataFieldUpdate() {
		// do nothing
		
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}
	
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		this.setEnergy(this.getEnergyCapacity(this.getTypes()[0]), this.getTypes()[0]);
	}


	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setString("InfType", this.type[0].toString());
		return tagRoot;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if (tagRoot.hasKey("InfType")) {
			this.type[0] = new ConduitType(tagRoot.getString("InfType"));
		}
	}
}
