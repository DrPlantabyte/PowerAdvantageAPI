package cyano.poweradvantage.machines.creative;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;

public class InfiniteEnergyTileEntity extends TileEntitySimplePowerSource{

	private final ItemStack[] inventory = new ItemStack[0];
	private final int[] dataArray = new int[0];
	private ConduitType type = null;
	
	public InfiniteEnergyTileEntity(ConduitType energyType) {
		super(energyType, 1000f, InfiniteEnergyTileEntity.class.getName());
		this.type = energyType;
	}
	

	public InfiniteEnergyTileEntity() {
		super(new ConduitType("power"), 1000f, InfiniteEnergyTileEntity.class.getName());
		this.type = new ConduitType("power");
	}
	
	public void setPowerType(ConduitType type){
		this.type = type;
	}
	
	@Override
	public ConduitType getType(){
		return type;
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
		this.setEnergy(this.getEnergyCapacity(), this.getType());
	}


	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setString("InfType", this.getType().toString());
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if (tagRoot.hasKey("InfType")) {
			this.type = new ConduitType(tagRoot.getString("InfType"));
		}
	}


	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return this.getStackInSlot(index);
	}
}
