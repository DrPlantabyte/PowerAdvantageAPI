package cyano.poweradvantage.api.example;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerSource;

public class RedstoneGeneratorTileEntity extends TileEntitySimplePowerSource {

	private ItemStack[] inventory = new ItemStack[1];
	private int[] dataFields = new int[2];
	private static final int DATAFIELD_BURNTIME = 0; // index in the dataFields array
	private static final int DATAFIELD_ENERGY = 1; // index in the dataFields array
	
    private short burnTime;
    private final float energyPerFuelTick = 4.0f;
    private final int ticksPerFuel = 1600; 
	
	public RedstoneGeneratorTileEntity() {
		super(new ConduitType("redstone"), 10000, PowerAdvantage.MODID+".redstoneGenerator");
	}

	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	
	private float oldEnergy = 0f;
	private short oldBurnTime = 0;
	@Override
	public void tickUpdate(boolean isServerWorld) {
		final boolean flag = this.isBurning();
		boolean flag2 = false;

		if (this.isBurning()) {
			this.burnTime--;
		}
		if (isServerWorld) {
			if(worldObj.getWorldTime() % 8 == 0){
				// GUI synchronization
				if(this.getEnergy() != oldEnergy){
					flag2 = true;
					oldEnergy = this.getEnergy();
				}
				if(this.burnTime != oldBurnTime){
					flag2 = true;
					oldBurnTime = this.burnTime;
				}
			}

			if (!this.isBurning() 
					&& this.getEnergy() < this.getEnergyCapacity() 
					&& isItemValidForSlot(0,this.inventory[0]) 
					&& !worldObj.isBlockPowered(getPos()) // disable on redstone signal
					) {
				final int itemBurnTime = ticksPerFuel;
				this.burnTime = itemBurnTime;
				flag2 = true;
				if (this.inventory[0] != null) {
					final ItemStack itemStack = this.inventory[0];
					itemStack.stackSize--;
					if (this.inventory[0].stackSize <= 0) {
						this.inventory[0] = null;
					}
				}
			} else if(this.isBurning() && this.getEnergy() < this.getEnergyCapacity()) {
				// is burning, add energy to buffer
				this.addEnergy(energyPerFuelTick,this.getType());
			}


			if (flag != this.isBurning()) {
				flag2 = true;
				// this is where you'd call setState(this.isBurning(), this.worldObj, this.pos) 
				// if you wanted to make the block change apearance when active
			}
			if (flag2) {
				worldObj.markBlockForUpdate(this.pos);
				this.markDirty();
			}
		}
	}
	
	public boolean isBurning() {
        return this.burnTime > 0;
    }
	
	public float getBurnFraction(){
		return (float)burnTime / (float) ticksPerFuel;
	}

	
	@Override
    public void writeToNBT(final NBTTagCompound tagRoot) {
        super.writeToNBT(tagRoot);
        tagRoot.setShort("BurnTime", this.burnTime);
	}
	
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
        super.readFromNBT(tagRoot);
        // inventory is already handled by the TileEntitySimplePowerSource class
        if(tagRoot.hasKey("BurnTime")){
        	this.burnTime = tagRoot.getShort("BurnTime");
        } else {
        	this.burnTime = 0;
        }
	}
	
	@Override
	public int[] getDataFieldArray() {
		return dataFields;
	}
	
	@Override
	public void onDataFieldUpdate() {
		// used for server-to-client sync
		this.burnTime = (short)dataFields[DATAFIELD_BURNTIME];
		this.setEnergy(Float.intBitsToFloat(dataFields[DATAFIELD_ENERGY]),this.getType());
	}
	
	@Override
	public void prepareDataFieldsForSync(){
		dataFields[DATAFIELD_BURNTIME] = burnTime;
		dataFields[DATAFIELD_ENERGY] = Float.floatToIntBits(this.getEnergy());
	}
	
	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		if(slot != 0) return false;
		List<ItemStack> redstoneDusts = OreDictionary.getOres("dustRedstone");
		for(ItemStack ore : redstoneDusts ){
			if(ItemStack.areItemsEqual(item, ore)){
				return true;
			}
		}
		return false;
	}
	
	

}
