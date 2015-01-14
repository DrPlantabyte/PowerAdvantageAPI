package cyano.poweradvantage.api.simple;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.PowerSinkEntity;

public abstract class TileEntitySimplePowerConsumer extends PowerSinkEntity implements ISidedInventory {

	private final ConductorType type;
	private final float energyBufferSize;
	private float energyBuffer = 0;
	
	private String customName = null;
    
    private final String unlocalizedName;

    
    public TileEntitySimplePowerConsumer(ConductorType type,float energyBufferSize, String unlocalizedName){
    	this.type = type;
    	this.energyBufferSize = energyBufferSize;
    	this.unlocalizedName = unlocalizedName;
    }
    
    public String getUnlocalizedName(){
    	return unlocalizedName;
    }
    
    protected abstract ItemStack[] getInventory();
    

	public abstract  int[] getDataFieldArray();
    
    @Override
    public abstract void tickUpdate(boolean isServerWorld);
    
    @Override
	public void powerUpdate() {
		if(getEnergyBuffer() < getEnergyBufferCapacity()){
			// get power from neighbors who have more than this conductor
			this.tryEnergyPullFrom(EnumFacing.UP);
			this.tryEnergyPullFrom(EnumFacing.NORTH);
			this.tryEnergyPullFrom(EnumFacing.WEST);
			this.tryEnergyPullFrom(EnumFacing.SOUTH);
			this.tryEnergyPullFrom(EnumFacing.EAST);
			this.tryEnergyPullFrom(EnumFacing.DOWN);
		}
	}
	
	protected void tryEnergyPullFrom(EnumFacing dir){
		float deficit = getEnergyBufferCapacity() - getEnergyBuffer();
		if(deficit > 0){
			EnumFacing otherDir = null;
			BlockPos coord = null;
			switch(dir){
			case UP:
				coord = getPos().up();
				otherDir = EnumFacing.DOWN;
				break;
			case DOWN:
				coord = getPos().down();
				otherDir = EnumFacing.UP;
				break;
			case NORTH:
				coord = getPos().north();
				otherDir = EnumFacing.SOUTH;
				break;
			case SOUTH:
				coord = getPos().south();
				otherDir = EnumFacing.NORTH;
				break;
			case EAST:
				coord = getPos().east();
				otherDir = EnumFacing.WEST;
				break;
			case WEST:
				coord = getPos().west();
				otherDir = EnumFacing.EAST;
				break;
			}
			TileEntity e = getWorld().getTileEntity(coord);
			if(e instanceof PowerConductorEntity){
				PowerConductorEntity pce = (PowerConductorEntity)e;
				if(pce.canPullEnergyFrom(otherDir, type)
						&& pce.getEnergyBuffer() > this.getEnergyBuffer()){
					this.addEnergy(-1*pce.subtractEnergy(deficit));
				}
			}
		}
	}
    

    @Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
        super.readFromNBT(tagRoot);
        ItemStack[] inventory = this.getInventory();
        if(inventory != null ){
	        final NBTTagList nbttaglist = tagRoot.getTagList("Items", 10);
	        for (int i = 0; i < nbttaglist.tagCount() && i < inventory.length; ++i) {
	            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
	            final byte n = nbttagcompound1.getByte("Slot");
	            if (n >= 0 && n < inventory.length) {
	                inventory[n] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
	            }
	        }
        }
        if (tagRoot.hasKey("CustomName", 8)) {
            this.customName = tagRoot.getString("CustomName");
        }
    }
    
    @Override
    public void writeToNBT(final NBTTagCompound tagRoot) {
        super.writeToNBT(tagRoot);
        ItemStack[] inventory = this.getInventory();
        if(inventory != null ){
	        final NBTTagList nbttaglist = new NBTTagList();
	        for (int i = 0; i < inventory.length; ++i) {
	            if (inventory[i] != null) {
	                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	                nbttagcompound1.setByte("Slot", (byte)i);
	                inventory[i].writeToNBT(nbttagcompound1);
	                nbttaglist.appendTag(nbttagcompound1);
	            }
	        }
	        tagRoot.setTag("Items", nbttaglist);
        }
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
	public boolean canPushEnergyTo(EnumFacing blockFace,
			ConductorType requestType) {
		return ConductorType.areSameType(type, requestType);
	}
	


    public void setCustomInventoryName(final String newName) {
        this.customName = newName;
    }
    
///// ISidedInventory methods /////
	@Override
	public void clear() {
		for(int i = 0; i < this.getInventory().length; i++){
			this.getInventory()[i] = null;
		}
		
	}

	
	@Override
	public void openInventory(EntityPlayer arg0) {
		// do nothing
		
	}
	
	@Override
	public void closeInventory(EntityPlayer arg0) {
		// do nothing
		
	}

	@Override
	public ItemStack decrStackSize(int slot, int decrement) {
		if (this.getInventory()[slot] == null) {
            return null;
        }
        if (this.getInventory()[slot].stackSize <= decrement) {
            final ItemStack itemstack = this.getInventory()[slot];
            this.getInventory()[slot] = null;
            return itemstack;
        }
        final ItemStack itemstack = this.getInventory()[slot].splitStack(decrement);
        if (this.getInventory()[slot].stackSize == 0) {
            this.getInventory()[slot] = null;
        }
        return itemstack;
	}

	
	@Override
    public int getField(final int fieldIndex) {
        if(this.getDataFieldArray() != null){
        	return this.getDataFieldArray()[fieldIndex];
        } else {
        	return 0;
        }
    }
	
	@Override
    public void setField(final int fieldIndex, final int fieldValue) {
		if(this.getDataFieldArray() != null){
        	this.getDataFieldArray()[fieldIndex] = fieldValue;
        } else {
        	// do nothing
        }
    }
	
	@Override
	public int getFieldCount() {
		if(this.getDataFieldArray() != null){
			return this.getDataFieldArray().length;
		} else {
			return 0;
		}
	}
	

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		if(this.getInventory() != null){
			return this.getInventory().length;
		} else {
			return 0;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(this.getInventory() != null){
			return this.getInventory()[slot];
		} else {
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		if(this.getInventory() == null) return null;
		if (this.getInventory()[slot] != null) {
			final ItemStack itemstack = this.getInventory()[slot];
			this.getInventory()[slot] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		if(this.getInventory() == null) return false;
		return slot < this.getInventory().length; 
	}

	@Override
    public boolean isUseableByPlayer(final EntityPlayer p_isUseableByPlayer_1_) {
        return this.worldObj.getTileEntity(this.pos) == this && p_isUseableByPlayer_1_.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		if(this.getInventory() == null) return;
		final boolean flag = item != null && item.isItemEqual(this.getInventory()[slot]) 
				&& ItemStack.areItemStackTagsEqual(item, this.getInventory()[slot]);
		this.getInventory()[slot] = item;
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
		if(this.getInventory() == null) return false;
		return slot < this.getInventory().length;
    }

	@Override
    public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		if(this.getInventory() == null) return false;
        return this.isItemValidForSlot(slot, srcItem);
    }

	
	private int[] slotAccessCache = null;
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(slotAccessCache != null) return slotAccessCache;
		if(this.getInventory() == null){
			slotAccessCache = new int[0];
		} else {
			slotAccessCache = new int[this.getInventory().length];
			for(int i = 0; i < slotAccessCache.length; i++){
				slotAccessCache[i] = i;
			}
		}
		return slotAccessCache;
	}

	
}
