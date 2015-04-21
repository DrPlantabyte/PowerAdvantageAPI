package cyano.poweradvantage.api.example;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConsumer;


public class RedstoneFurnaceTileEntity extends TileEntitySimplePowerConsumer {


	private ItemStack[] inventory = new ItemStack[2]; // [input slot, output slot]
	private final int[] insertableSlots = {0};
	private final int[] extractableSlots = {1};
	
	private int[] dataFields = new int[2];
	private static final int DATAFIELD_COOKTIME = 0; // index in the dataFields array
	private static final int DATAFIELD_ENERGY = 1; // index in the dataFields array
	
    /*private*/ short cookTime;

    private final float ENERGY_PER_TICK = 2f;
    private final int COOKING_PER_TICK = 2;
    
	public RedstoneFurnaceTileEntity() {
		super(new ConduitType("redstone"), 200, PowerAdvantage.MODID+".redstoneFurnace");
	}

	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}
	
	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		return slot == 0 && canSmelt(item,null);
	}

	@Override
    public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
        return slot == 1;
    }

	@Override
    public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
        return this.isItemValidForSlot(slot, srcItem);
    }

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.DOWN) return extractableSlots;
		return insertableSlots;
	}

	
	@Override
	public int[] getDataFieldArray() {
		return dataFields;
	}
	

	@Override
	public void onDataFieldUpdate() {
		// used for server-to-client sync
		this.cookTime = (short)dataFields[DATAFIELD_COOKTIME];
		this.setEnergy(Float.intBitsToFloat(dataFields[DATAFIELD_ENERGY]),this.getType());
	}
	
	@Override
	public void prepareDataFieldsForSync(){
		dataFields[DATAFIELD_COOKTIME] = cookTime;
		dataFields[DATAFIELD_ENERGY] = Float.floatToIntBits(this.getEnergy());
	}

	private float oldEnergy = 0f;
	private short oldCookTime = 0;
	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			final boolean flag = this.isBurning();
			boolean flag2 = false;
			if(worldObj.getWorldTime() % 8 == 0){
				// GUI synchronization
				if(this.getEnergy() != oldEnergy){
					flag2 = true;
					oldEnergy = this.getEnergy();
				}
				if(this.cookTime != oldCookTime){
					flag2 = true;
					oldCookTime = this.cookTime;
				}
			}
			if(cookTime >= getSmeltTime(inventory[0]) && this.canSmelt(this.inventory[0], this.inventory[1])){
				// finished smelting current item
				smeltItem();
				cookTime = 0;
				flag2 = true;
			}
			if(this.getEnergy() >= ENERGY_PER_TICK 
					&& this.canSmelt(this.inventory[0], this.inventory[1]) 
					&& !worldObj.isBlockPowered(getPos()) // disable on redstone signal
					){
				this.subtractEnergy(ENERGY_PER_TICK,this.getType());
				this.cookTime += COOKING_PER_TICK;
			} else if(cookTime > 0){
				this.cookTime--;
			}
			if (flag != this.isBurning()) {
				flag2 = true;
				// this is where you'd invoke Block.setState(this.isBurning(), this.worldObj, this.pos)
				// if you wanted to show that the machine is active
			}
			if (flag2) {
				worldObj.markBlockForUpdate(this.pos);
				this.markDirty();
			}
		}
	}

	
	private int getSmeltTime(ItemStack item){
		return 200;
	}
	
	private void smeltItem() {
        final ItemStack output = FurnaceRecipes.instance().getSmeltingResult(inventory[0]);
        if (this.inventory[1] == null) {
            this.inventory[1] = output.copy();
        } else {
            final ItemStack outputSlot = this.inventory[1];
            outputSlot.stackSize += output.stackSize;
        }
        
        final ItemStack src = this.inventory[0];
        src.stackSize--;
        if (this.inventory[0].stackSize <= 0) {
            this.inventory[0] = null;
        }
    }
	
	private boolean canSmelt(ItemStack inputSlot, ItemStack outputSlot) {
        if (inputSlot == null) {
            return false;
        }
        final ItemStack output = FurnaceRecipes.instance().getSmeltingResult(inputSlot);
        if (output == null) {
            return false;
        }
        if (outputSlot == null) {
            return true;
        }
        if (outputSlot.isItemEqual(output)) {
        	final int stackSize = outputSlot.stackSize + output.stackSize;
        	return stackSize <= 64 && stackSize <= outputSlot.getMaxStackSize();
        }
        return false;
    }
	
	public boolean isBurning() {
        return cookTime > 0;
    }
	
	@Override
    public void writeToNBT(final NBTTagCompound tagRoot) {
        super.writeToNBT(tagRoot);
        // inventory is already handled by the TileEntitySimplePowerSource class
        tagRoot.setShort("CookTime", this.cookTime);
	}
	
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
        super.readFromNBT(tagRoot);
        // inventory is already handled by the TileEntitySimplePowerSource class
        if(tagRoot.hasKey("CookTime")){
        	this.cookTime = tagRoot.getShort("CookTime");
        } else {
        	this.cookTime = 0;
        }
	}

	public float getCookFraction() {
		if(inventory[0] == null){
			return (float) cookTime / 200f;
		}
		return (float) cookTime / (float)getSmeltTime(inventory[0]);
	}


}
