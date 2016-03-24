package cyano.poweradvantage.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;

/**
 * This class allows you to treat an instance of IInventory as if it were an instance of 
 * ISidedInventory. 
 * @author DrCyano
 *
 */
public class InventoryWrapper implements ISidedInventory {
	
	private final IInventory inventory;
	private final int[] slots;
	private static final int[] NO_SLOTS = new int[0];
	private static final InventoryWrapper NULL_INVENTORY = new InventoryWrapper();
	
	/**
	 * Constructs an ISidedInventory wrapper for the given IInventory instance
	 * @param inv An net.minecraft.inventory.IInventory that you want to treat as a 
	 * net.minecraft.inventory.ISidedInventory
	 */
	protected InventoryWrapper(IInventory inv){
		this.inventory = inv;
		slots = new int[inventory.getSizeInventory()];
		net.minecraft.tileentity.TileEntityChest g;
		for(int i = 0; i < slots.length; i++){
			slots[i] = i;
		}
	}

	/**
	 * Creates an ISidedInventory instance that has no inventory
	 */
	protected InventoryWrapper(){
		slots = NO_SLOTS;
		inventory = new IInventory() {
			@Override
			public int getSizeInventory() {
				return 0;
			}

			@Override
			public ItemStack getStackInSlot(int i) {
				return null;
			}

			@Override
			public ItemStack decrStackSize(int i, int i1) {
				return null;
			}

			@Override
			public ItemStack removeStackFromSlot(int i) {
				return null;
			}

			@Override
			public void setInventorySlotContents(int i, ItemStack itemStack) {

			}

			@Override
			public int getInventoryStackLimit() {
				return 0;
			}

			@Override
			public void markDirty() {

			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
				return false;
			}

			@Override
			public void openInventory(EntityPlayer entityPlayer) {

			}

			@Override
			public void closeInventory(EntityPlayer entityPlayer) {

			}

			@Override
			public boolean isItemValidForSlot(int i, ItemStack itemStack) {
				return false;
			}

			@Override
			public int getField(int i) {
				return 0;
			}

			@Override
			public void setField(int i, int i1) {

			}

			@Override
			public int getFieldCount() {
				return 0;
			}

			@Override
			public void clear() {

			}

			@Override
			public String getName() {
				return "Inventory";
			}

			@Override
			public boolean hasCustomName() {
				return false;
			}

			@Override
			public IChatComponent getDisplayName() {
				return new net.minecraft.util.ChatComponentText("Inventory");
			}
		};
	}
	/**
	 * Gets whether this is a locked container
	 * @return true if the wrapped inventory is an instance of ILockableContainer and is locked
	 */
	public boolean isLocked(){
		if(inventory instanceof ILockableContainer){
			return ((ILockableContainer)inventory).isLocked();
		}
		return false;
	}
	/**
	 * Constructs an ISidedInventory wrapper for the given IInventory instance. If the provided 
	 * instance is already an ISidedInventory, then that instance is simply returned instead of 
	 * making a wrapper.
	 * @param inv An net.minecraft.inventory.IInventory that you want to treat as a 
	 * net.minecraft.inventory.ISidedInventory
	 * @return An instance of ISidedInventory that forwards all methods to the given IInventory
	 */
	public static ISidedInventory wrap(IInventory inv){
		if(inv == null) return NULL_INVENTORY;
		if(inv instanceof ISidedInventory) return (ISidedInventory)inv;
		return new InventoryWrapper(inv);
	}

	@Override
	public IChatComponent getDisplayName() {
		return inventory.getDisplayName();
	}

	@Override
	public String getName() {
		return inventory.getName();
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {
		inventory.closeInventory(arg0);
	}

	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		return inventory.decrStackSize(arg0, arg1);
	}

	@Override
	public int getField(int arg0) {
		return inventory.getField(arg0);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int arg0) {
		return inventory.getStackInSlot(arg0);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
		return inventory.isItemValidForSlot(arg0, arg1);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0) {
		return inventory.isUseableByPlayer(arg0);
	}

	@Override
	public void markDirty() {
		inventory.markDirty();
	}

	@Override
	public void openInventory(EntityPlayer arg0) {
		inventory.openInventory(arg0);
	}

	@Override
	public void setField(int arg0, int arg1) {
		inventory.setField(arg0, arg1);
	}

	@Override
	public void setInventorySlotContents(int arg0, ItemStack arg1) {
		inventory.setInventorySlotContents(arg0, arg1);
	}

	@Override
	public boolean canExtractItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		if(isLocked())return false;
		return true;
	}

	@Override
	public boolean canInsertItem(int arg0, ItemStack arg1, EnumFacing arg2) {
		if(isLocked())return false;
		return true;
	}

	
	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		if(isLocked())return NO_SLOTS;
		return slots;
	}
	

}
