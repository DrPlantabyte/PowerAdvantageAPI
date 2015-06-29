package cyano.poweradvantage.machines.creative;

import net.minecraft.item.ItemStack;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;

public class InfiniteEnergyTileEntity extends TileEntitySimplePowerSource{

	final ItemStack[] inventory = new ItemStack[0];
	ConduitType type = null;
	
	public InfiniteEnergyTileEntity(String energyType) {
		super(new ConduitType(energyType), 100f, InfiniteEnergyTileEntity.class.getName());
		this.type = new ConduitType(energyType);
	}
	
	@Override
	public ConduitType getType(){
		return type;
	}

	
	@Override
	protected ItemStack[] getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getDataFieldArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareDataFieldsForSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataFieldUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		// TODO Auto-generated method stub
		
	}

}
