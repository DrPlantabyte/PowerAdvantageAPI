package cyano.poweradvantage.api.example;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerSourceEntity;

public class SimplePowerSourceEntity extends PowerSourceEntity implements ISidedInventory{


	public static final float energyPerFuelTick = 1.0f;

	private final ConductorType type;
	private final float energyBufferSize;
	private float energyBuffer = 0;
	
	private ItemStack[] inventory = new ItemStack[1];
	private final int[] insertableSlots;
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    
    private String customName = null;
    
    private final String unlocalizedName;
	
	public SimplePowerSourceEntity(){
		energyBufferSize = 10 * (80 * 20); // equivalent to a block of coal
		this.type = new ConductorType("energy");
		this.unlocalizedName = "poweradvantage.simplegenerator";
		insertableSlots = new int[inventory.length];
		for(int i = 0; i < inventory.length; i++)
			insertableSlots[i] = i;
	}
	
	
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
        super.readFromNBT(tagRoot);
        final NBTTagList nbttaglist = tagRoot.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final byte b0 = nbttagcompound1.getByte("Slot");
            if (b0 >= 0 && b0 < this.inventory.length) {
                this.inventory[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
        this.furnaceBurnTime = tagRoot.getShort("BurnTime");
        this.currentItemBurnTime = TileEntityFurnace.getItemBurnTime(this.inventory[0]);
        if (tagRoot.hasKey("CustomName", 8)) {
            this.customName = tagRoot.getString("CustomName");
        }
    }
    
    @Override
    public void writeToNBT(final NBTTagCompound tagRoot) {
        super.writeToNBT(tagRoot);
        tagRoot.setShort("BurnTime", (short)this.furnaceBurnTime);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        tagRoot.setTag("Items", nbttaglist);
        if (this.hasCustomName()) {
            tagRoot.setString("CustomName", this.customName);
        }
    }
	
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

	@Override
	public float getEnergyBufferCapacity() {
		return energyBufferSize;
	}

	@Override
	public float getEnergyBuffer() {
		return energyBuffer;
	}

	@Override
	public void setEnergy(float energy) {
		energyBuffer = energy;
		
	}

	@Override
	public boolean canPullEnergyFrom(EnumFacing blockFace,
			ConductorType requestType) {
		return ConductorType.areSameType(getEnergyType(), requestType);
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		final boolean flag = this.isBurning();
        boolean flag2 = false;
        if (this.isBurning()) {
            --this.furnaceBurnTime;
        }
        if (isServerWorld) {
            
                if (!this.isBurning() && this.getEnergyBuffer() < this.getEnergyBufferCapacity()) {
                    final int itemBurnTime = TileEntityFurnace.getItemBurnTime(this.inventory[0]);
                    this.furnaceBurnTime = itemBurnTime;
                    this.currentItemBurnTime = itemBurnTime;
                    if (this.isBurning()) {
                        flag2 = true;
                        if (this.inventory[0] != null) {
                            final ItemStack itemStack = this.inventory[0];
                            --itemStack.stackSize;
                            if (this.inventory[0].stackSize == 0) {
                                this.inventory[0] = this.inventory[0].getItem().getContainerItem(this.inventory[0]);
                            }
                        }
                    }
                } else {
                	// is burning, add energy to buffer
                	this.addEnergy(energyPerFuelTick);
                }
                
            
            if (flag != this.isBurning()) {
                flag2 = true;
                BlockSimpleGenerator.setState(this.isBurning(), this.worldObj, this.pos);
            }
        }
        if (flag2) {
            this.markDirty();
        }
	}

	/**
	 * Shift items down a slot if the first slot is empty
	 */
	private void shiftInventory(){
		if(inventory[0] == null && inventory.length > 1){
			for(int i = 1; i < inventory.length; i++){
				inventory[i-1] = inventory[i];
			}
			inventory[inventory.length-1] = null;
			this.markDirty();
		}
	}
	@Override
	public void powerUpdate() {
		// do nothing
		
	}
	

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }


    
    public void setCustomInventoryName(final String newName) {
        this.customName = newName;
    }
	///// ISidedInventory methods /////
	@Override
	public void clear() {
		for(int i = 0; i < inventory.length; i++){
			inventory[i] = null;
		}
		
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {
		// do nothing
		
	}

	@Override
	public ItemStack decrStackSize(int slot, int decrement) {
		if (this.inventory[slot] == null) {
            return null;
        }
        if (this.inventory[slot].stackSize <= decrement) {
            final ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        }
        final ItemStack itemstack = this.inventory[slot].splitStack(decrement);
        if (this.inventory[slot].stackSize == 0) {
            this.inventory[slot] = null;
        }
        return itemstack;
	}

	@Override
    public int getField(final int fieldIndex) {
        switch (fieldIndex) {
            case 0: {
                return this.furnaceBurnTime;
            }
            case 1: {
                return this.currentItemBurnTime;
            }
            default: {
                return 0;
            }
        }
    }

	@Override
    public void setField(final int fieldIndex, final int fieldValue) {
        switch (fieldIndex) {
            case 0: {
                this.furnaceBurnTime = fieldValue;
                break;
            }
            case 1: {
                this.currentItemBurnTime = fieldValue;
                break;
            }
        }
    }
	
	@Override
	public int getFieldCount() {
		return 2;
	}
	

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		if (this.inventory[slot] != null) {
			final ItemStack itemstack = this.inventory[slot];
			this.inventory[slot] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		return (TileEntityFurnace.isItemFuel(item) || SlotFurnaceFuel.isBucket(item));
	}

	@Override
    public boolean isUseableByPlayer(final EntityPlayer p_isUseableByPlayer_1_) {
        return this.worldObj.getTileEntity(this.pos) == this && p_isUseableByPlayer_1_.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

	@Override
	public void openInventory(EntityPlayer arg0) {
		// do nothing
		
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		final boolean flag = item != null && item.isItemEqual(this.inventory[slot]) && ItemStack.areItemStackTagsEqual(item, this.inventory[slot]);
		this.inventory[slot] = item;
		if (item != null && item.stackSize > this.getInventoryStackLimit()) {
			item.stackSize = this.getInventoryStackLimit();
		}
		if (slot == 0 && !flag) {
			this.markDirty();
		}
	}


    @Override
    public IChatComponent getDisplayName() {
        if (this.hasCustomName()) {
            return new ChatComponentText(this.getName());
        }
        return new ChatComponentTranslation(this.getName(), new Object[0]);
    }

	@Override
	public String getName() {
        return this.hasCustomName() ? this.customName : unlocalizedName;
	}

	@Override
	public boolean hasCustomName() {
		return customName != null;
	}

	@Override
    public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
        return false;
    }

	@Override
    public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
        return this.isItemValidForSlot(slot, srcItem);
    }

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return insertableSlots;
	}

}
