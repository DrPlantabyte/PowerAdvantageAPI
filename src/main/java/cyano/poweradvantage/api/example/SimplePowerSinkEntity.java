package cyano.poweradvantage.api.example;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
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
    private int cookTime;
    private int totalCookTime;
    
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
		final boolean flag = this.isBurning();
		boolean flag2 = false;
		if (this.isBurning()) {
			--this.furnaceBurnTime;
		}
		if (!this.worldObj.isRemote) {
			if (!this.isBurning() && (this.furnaceItemStacks[1] == null || this.furnaceItemStacks[0] == null)) {
				if (!this.isBurning() && this.cookTime > 0) {
					this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
				}
			}
			else {
				if (!this.isBurning() && this.canSmelt()) {
					final int itemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
					this.furnaceBurnTime = itemBurnTime;
					this.currentItemBurnTime = itemBurnTime;
					if (this.isBurning()) {
						flag2 = true;
						if (this.furnaceItemStacks[1] != null) {
							final ItemStack itemStack = this.furnaceItemStacks[1];
							--itemStack.stackSize;
							if (this.furnaceItemStacks[1].stackSize == 0) {
								this.furnaceItemStacks[1] = this.furnaceItemStacks[1].getItem().getContainerItem(this.furnaceItemStacks[1]);
							}
						}
					}
				}
				if (this.isBurning() && this.canSmelt()) {
					++this.cookTime;
					if (this.cookTime == this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.func_174904_a(this.furnaceItemStacks[0]);
						this.smeltItem();
						flag2 = true;
					}
				}
				else {
					this.cookTime = 0;
				}
			}
			if (flag != this.isBurning()) {
				flag2 = true;
				BlockFurnace.setState(this.isBurning(), this.worldObj, this.pos);
			}
		}
		if (flag2) {
			this.markDirty();
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
                this.cookTime = fieldValue;
                break;
            }
            case 1: {
                this.totalCookTime = fieldValue;
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
