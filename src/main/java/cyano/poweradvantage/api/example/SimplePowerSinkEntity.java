package cyano.poweradvantage.api.example;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerSinkEntity;

public class SimplePowerSinkEntity extends PowerSinkEntity  implements ISidedInventory{

	
	private final ConductorType type;
	private final float energyBufferSize;
	private float energyBuffer = 0;
	
	public static final float energyPerTick = 1.0f; 
	
	private ItemStack[] inventory = new ItemStack[2];
	private final int[] insertableSlots = {0};
	private final int[] extractableSlots = {1};
    private short cookTime;
    private short totalCookTime;
    
    private boolean isCooking = false;
    
    private String customName = null;
    
    private final String unlocalizedName;
    
    
    public SimplePowerSinkEntity(){
    	energyBufferSize = 10 * (80 * 20); // equivalent to a block of coal
		this.type = new ConductorType("energy");
		this.unlocalizedName = "poweradvantage.simplegenerator";
    }
	
	@Override
	public ConductorType getEnergyType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getEnergyBufferCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEnergyBuffer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setEnergy(float energy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canPushEnergyTo(EnumFacing blockFace,
			ConductorType requestType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			final boolean flag = this.isBurning();
			boolean flag2 = false;
			if(cookTime >= getSmeltTime(inventory[0]) && this.canSmelt(this.inventory[0], this.inventory[1])){
				// finished smelting current item
				ItemStack done = FurnaceRecipes.instance().getSmeltingResult(this.inventory[0]);
				if(this.inventory[1] == null){
					this.inventory[1] = done.copy();
				} else {
					this.inventory[1].stackSize += done.stackSize;
				}
				this.inventory[0].stackSize--;
				if(this.inventory[0].stackSize < 1){
					this.inventory[0] = null;
				}
				cookTime = 0;
			}
			if(this.getEnergyBuffer() >= energyPerTick && this.canSmelt(this.inventory[0], this.inventory[1])){
				this.subtractEnergy(energyPerTick);
				this.cookTime++;
			} else if(cookTime > 0){
				this.cookTime--;
			}
			if(cookTime > 0){
				this.isCooking = true;
			} else {
				this.isCooking = false;
			}
			if (flag != this.isBurning()) {
				flag2 = true;
				BlockSimplePoweredFurnace.setState(this.isBurning(), this.worldObj, this.pos);
			}
			if (flag2) {
				this.markDirty();
			}
		}
	}
	
	/**
	 * Reads data from NBT, which came from either a saved chunk or a network 
	 * packet.
	 */
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if(tagRoot.hasKey("Items")){
	        final NBTTagList nbttaglist = tagRoot.getTagList("Items", 10);
			this.inventory = new ItemStack[this.getSizeInventory()];
	        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
	            final NBTTagCompound itemTag = nbttaglist.getCompoundTagAt(i);
	            final byte slotIndex = itemTag.getByte("Slot");
	            if (slotIndex >= 0 && slotIndex < this.inventory.length) {
	                this.inventory[slotIndex] = ItemStack.loadItemStackFromNBT(itemTag);
	            }
	        }
		}
		if(tagRoot.hasKey("CookTime")) this.cookTime = tagRoot.getShort("CookTime");
        if (tagRoot.hasKey("CustomName", 8)) {
            this.customName = tagRoot.getString("CustomName");
        }
	}
	/**
	 * Saves the state of this entity to an NBT for saving or synching across 
	 * the network.
	 */
	@Override
	public void writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setShort("CookTime", cookTime);
		final NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                final NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(itemTag);
                nbttaglist.appendTag(itemTag);
            }
        }
        tagRoot.setTag("Items", nbttaglist);
        if (this.hasCustomName()) {
        	tagRoot.setString("CustomName", this.customName);
        }
	}
	
	
	public boolean isBurning() {
        return isCooking;
    }

	public static int getSmeltTime(ItemStack item){
		return 200;
	}
	
	public void smeltItem() {
        if (this.canSmelt(inventory[0],inventory[1])) {
            final ItemStack output = FurnaceRecipes.instance().getSmeltingResult(inventory[0]);
            if (this.inventory[1] == null) {
                this.inventory[1] = output.copy();
            }
            else if (this.inventory[1].getItem() == output.getItem()) {
                final ItemStack outputSlot = this.inventory[1];
                outputSlot.stackSize += output.stackSize;
            }
            
            final ItemStack src = this.inventory[0];
            --src.stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
        }
    }
	
	@Override
	public void powerUpdate() {
		// TODO Auto-generated method stub
		
	}
	
	public static boolean canSmelt(ItemStack inputSlot, ItemStack outputSlot) {
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
        if (!outputSlot.isItemEqual(inputSlot)) {
            return false;
        }
        final int result = outputSlot.stackSize + output.stackSize;
        return result <= 64 && result <= outputSlot.getMaxStackSize();
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
                return this.cookTime;
            }
            case 1: {
                return this.totalCookTime;
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
                this.cookTime = (short)fieldValue;
                break;
            }
            case 1: {
                this.totalCookTime = (short)fieldValue;
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
		return slot == 0 && canSmelt(item,null);
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


}
