package cyano.poweradvantage.api.simple;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.fluid.FluidPoweredEntity;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.init.Fluids;


/**
 * This block implements the cyano.poweradvantage.api.fluid.FluidPoweredEntity 
 * class and handles most of the mundane details for you. You just need to 
 * implement your machine logic in the <b>tickUpdate()</b> method and provide a 
 * few methods for inventory handling. <br>
 * <b>Remember to override the readFromNBT(...) and writeToNBT(...) methods</b>     
 * @author DrCyano
 *
 */
public abstract class TileEntitySimpleFluidConsumer extends FluidPoweredEntity implements ISidedInventory {
	/**
	 * This FluidTank holds the fluids for this machine
	 */
	protected final FluidTank tank;
	
	private String customName = null;
    
    private final String unlocalizedName;


	private int[] dataFields = new int[2];
	private static final int DATAFIELD_FLUID_ID = 0; // index in the dataFields array
	private static final int DATAFIELD_FLUID_VOLUME = 1; // index in the dataFields array
   /**
    * Main constructor for this abstract class. Your implementing class <b>must have a no-argument 
    * constructor</b> or else Minecraft will crash when it tries to load your entity from a saved 
    * game file.
    * @param fluidTankCapacity How much fluid can be stored in this entity
    * @param unlocalizedName The default name for this TileEntity, should the player interact with 
    * the TileEnmtity in a way that gets the TileEntity's name (if in doubt, use the class name).
    */
    public TileEntitySimpleFluidConsumer(int fluidTankCapacity, String unlocalizedName){
    	this.tank = new FluidTank(fluidTankCapacity);
    	this.unlocalizedName = unlocalizedName;
    }
    /**
     * Gets the unlocalized String passed to the constructor
     * @return A String used in name localization by client GUI 
     */
    public String getUnlocalizedName(){
    	return unlocalizedName;
    }
    /**
     * Gets the inventory for this machine. If this machine does not have an 
     * inventory, return null; 
     * @return This block's inventory as an array, or null
     */
    protected abstract ItemStack[] getInventory();
    
    

	
    /**
     * This method is called once every world tick. Implement your machine logic 
     * here, but make sure that you don't do any processing that will consume 
     * large amounts of computer CPU time. If you do have a computationally 
     * intensive calculation, break it into smaller computations that you can 
     * spread over multiple tick updates.
     * @param isServerWorld Will be true on the server-side and false on the client-side. Becareful 
     * to avoid using any graphics-related code on the server (which has no graphics drivers).
     */
    @Override
    public abstract void tickUpdate(boolean isServerWorld);
    
    
    private int oldLevel = -1;
    /**
     * This method is called when the power transmission is computed (not every 
     * tick). You do not need to override this method, but if you do, be sure to 
     * start with <code>super.powerUpdate()</code>
     */
    @Override
	public void powerUpdate() {
		if(this.getTank().getFluidAmount() != oldLevel){
			oldLevel = this.getTank().getFluidAmount();
			this.sync();
		}
	}
	

	/**
	 * Specify how much energy this power sink wants from a power generator. If this tile entity is 
	 * not a sink, then simply return PowerRequest.REQUEST_NOTHING
	 * @param type The type of energy available upon request
	 * @return A PowerRequest instance indicated how much power you'd like to get
	 */
	public PowerRequest getPowerRequest(ConduitType type){
		float space = this.getEnergyCapacity() - this.getEnergy(); 
		if(this.canAcceptType(type) && space > 0){
			return new PowerRequest(PowerRequest.MEDIUM_PRIORITY,space,this);
		} else {
			return PowerRequest.REQUEST_NOTHING;
		}
	}
    
    /**
     * You must override this method and call super.readFromNBT(...).<br><br>
     * This method reads data from an NBT data tag (either from a data packet 
     * or loaded from the Chunk).
     * @param tagRoot The root of the NBT
     */
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
    
    /**
     * You must override this method and call super.writeToNBT(...).<br><br>
     * This method writes data to an NBT data tag (either to a data packet 
     * or saved to the Chunk).
     * @param tagRoot The root of the NBT
     */
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
    

    /**
	 * Gets the energy type for this block.
	 * @return The type of energy/power for this block
	 */
	@Override
	public ConduitType getType() {
		return Fluids.fluidConduit_general;
	}

	/**
	 * Gets the maximum amount of energy that can be stored in this block.
	 * @return Maximum energy storage
	 */
	@Override
	public float getEnergyCapacity() {
		return tank.getCapacity();
	}

	/**
	 * Gets the amount of energy currently stored in this block
	 * @return Current energy level
	 */
	@Override
	public float getEnergy() {
		return tank.getFluidAmount();
	}

	/**
	 * Sets the amount of energy stored in this block
	 * @param energy New value of the energy buffer
	 */
	@Override
	public void setEnergy(float energy,ConduitType type) {
		
	}
	
	
	
	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param type The type of energy in the conduit
	 * @param blockFace The side through-which the energy is flowing
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	@Override
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace) {
		return canAcceptType(type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	@Override
	public boolean canAcceptType(ConduitType type) {
		return ConduitType.areSameType(type,Fluids.fluidConduit_general) || (Fluids.conduitTypeToFluid(type) != null);
	}
	/**
	 * Returns true because this is a machine that wants to make power requests
	 * @return true
	 */
	@Override
	public boolean isPowerSink() {
		return true;
	}
	/**
	 * Returns false because this is a machine that does not produce power
	 * @return false
	 */
	@Override
	public boolean isPowerSource() {
		return false;
	}


	/**
	 * This method is called when a block is renamed (e.g. with an anvil) and 
	 * then placed into the world.
	 * @param newName The custom name of the block
	 */
    public void setCustomInventoryName(final String newName) {
        this.customName = newName;
    }


    
    // SYNCHRONIZATION OF DATA FIELDS
    /**
     * Creates a NBT to send a synchronization update using this block's data 
     * field array (see <code>getDataFieldArray()</code>). Before this method 
     * executes, it calls <code>prepareDataFieldsForSync()</code>.
     * @return A synchronization NBT holding values from the 
     * <code>getDataFieldArray()</code> array
     */
    public NBTTagCompound createDataFieldUpdateTag(){
    	this.prepareDataFieldsForSync();
    	int[] dataFields = this.getDataFieldArray();
    	NBTTagCompound nbtTag = new NBTTagCompound();
    	nbtTag.setIntArray("[]", dataFields);
    	return nbtTag;
    }
    /**
     * Reads a synchronization update NBT and stores it in this blocks data 
     * field array (see <code>getDataFieldArray()</code>). After this method 
     * executes, it calls <code>onDataFieldUpdate()</code>.
     * @param tag A synchronization NBT holding values from the 
     * <code>getDataFieldArray()</code> array
     */
    public void readDataFieldUpdateTag(NBTTagCompound tag){
    	int[] newData = tag.getIntArray("[]");
    	System.arraycopy(newData, 0, this.getDataFieldArray(), 0, Math.min(newData.length, this.getDataFieldArray().length));
    	this.onDataFieldUpdate();
    }

    /**
     * Turns the data field NBT into a network packet
     */
    @Override 
    public Packet getDescriptionPacket(){
    	NBTTagCompound nbtTag = createDataFieldUpdateTag();
    	return new S35PacketUpdateTileEntity(this.pos, 0, nbtTag);
    }
    /**
     * Receives the network packet made by <code>getDescriptionPacket()</code>
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
    	readDataFieldUpdateTag(packet.getNbtCompound());
    }

    
///// ISidedInventory methods /////
    /**
     * Removes all items from this machine's inventory.
     */
	@Override
	public void clear() {
		if(this.getInventory() == null) return;
		for(int i = 0; i < this.getInventory().length; i++){
			this.getInventory()[i] = null;
		}
		
	}

	/**
	 * executes when a player right-clicks and opens the inventory GUI
	 */
	@Override
	public void openInventory(EntityPlayer arg0) {
		// do nothing
		
	}
	/**
	 * executes when a player stops interacting with this machine 
	 */
	@Override
	public void closeInventory(EntityPlayer arg0) {
		// do nothing
		
	}

	/**
	 * boilerplate inventory code
	 */
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

	/**
	 * gets a field from the data array
	 */
	@Override
    public int getField(final int fieldIndex) {
        if(this.getDataFieldArray() != null){
        	return this.getDataFieldArray()[fieldIndex];
        } else {
        	return 0;
        }
    }
	/**
	 * sets a field in the data array
	 */
	@Override
    public void setField(final int fieldIndex, final int fieldValue) {
		if(this.getDataFieldArray() != null){
        	this.getDataFieldArray()[fieldIndex] = fieldValue;
        } else {
        	// do nothing
        }
    }
	/**
	 * Gets the number of data fields
	 * @return The size of the data array
	 */
	@Override
	public int getFieldCount() {
		if(this.getDataFieldArray() != null){
			return this.getDataFieldArray().length;
		} else {
			return 0;
		}
	}
	

	/**
	 * boilerplate inventory code
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * boilerplate inventory code
	 * @return The size of this machine's inventory (returns 0 if there is no 
	 * inventory)
	 */
	@Override
	public int getSizeInventory() {
		if(this.getInventory() != null){
			return this.getInventory().length;
		} else {
			return 0;
		}
	}

	/**
	 * boilerplate inventory code
	 * @return Item in a given inventory slot (null means no item)
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(this.getInventory() != null){
			return this.getInventory()[slot];
		} else {
			return null;
		}
	}
	
	/**
	 * boilerplate inventory code
	 */
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

	/**
	 * Determines whether a given item is allowed to be added to a given 
	 * inventory slot.
	 * @param slot Index in the inventory where Minecraft is trying to put the 
	 * item 
	 * @param item The item in question 
	 * @return true if the item is allowed to go in the indicated slot, false 
	 * otherwise 
	 */
	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack item) {
		if(this.getInventory() == null) return false;
		return slot < this.getInventory().length; 
	}
	
	/**
	 * boilerplate inventory code
	 */
	@Override
    public boolean isUseableByPlayer(final EntityPlayer p_isUseableByPlayer_1_) {
        return this.worldObj.getTileEntity(this.pos) == this && p_isUseableByPlayer_1_.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

	/**
	 * boilerplate inventory code
	 */
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
	

	/**
	 * boilerplate inventory code
	 */
	@Override
    public IChatComponent getDisplayName() {
        if (this.hasCustomName()) {
            return new ChatComponentText(this.getName());
        }
        return new ChatComponentTranslation(this.getName(), new Object[0]);
    }

	/**
	 * boilerplate inventory code
	 * @return either the unlocalized name for this entity or a custom name 
	 * given by the player (i.e. with an anvil)
	 */
	@Override
	public String getName() {
        return this.hasCustomName() ? this.customName : this.getUnlocalizedName();
	}

	/**
	 * boilerplate inventory code
	 * @return true if this block has been renamed, false otherwise
	 */
	@Override
	public boolean hasCustomName() {
		return customName != null;
	}

	/**
	 * Determines whether another block (such as a Hopper) is allowed to pull an 
	 * item from this block out through a given face
	 * @param slot The inventory slot (index) of the item in question
	 * @param targetItem The item to be pulled
	 * @param side The side of the block through which to pull the item
	 * @return true if the item is allowed to be pulled, false otherwise
	 */
	@Override
    public boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
		if(this.getInventory() == null) return false;
		return slot < this.getInventory().length;
    }
	/**
	 * Determines whether another block (such as a Hopper) is allowed to put an 
	 * item into this block through a given face
	 * @param slot The inventory slot (index) of the item in question
	 * @param srcItem The item to be inserted
	 * @param side The side of the block through which to insertt the item
	 * @return true if the item is allowed to be inserted, false otherwise
	 */
	@Override
    public boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
		if(this.getInventory() == null) return false;
        return this.isItemValidForSlot(slot, srcItem);
    }

	/** cache for default implementation of getSlotsForFace(...)*/
	private int[] slotAccessCache = null;
	/**
	 * This method is used to tell other blocks (such as a hopper) which 
	 * inventory slots they are allowed to interact with, based on which face of 
	 * this block they are touching. Override this method to make output slots 
	 * go out the bottom of this block and input slots go in the top (like a 
	 * furnace).<br><br>
	 * The default implementation here makes all inventory slots accessible from 
	 * all sides.
	 * @param side The side of this block through which the other entity wants to 
	 * access the inventory 
	 * @return An array listing all of the inventory indices that are accessible 
	 * through the given side of this block
	 */
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
	/**
	 * Gets the tank used to store fluids in this machine
	 * @return an instance of FluidTank
	 */
	@Override
	protected FluidTank getTank() {
		return tank;
	}
	
	/**
	 * This method is used to restrict what types of fluids are allowed in the tank. If you want to 
	 * store any kind of fluid, just return true, but if you only want to store, for example, lava 
	 * then return false unless the provided fluid is lava
	 * @param fluid A fluid that can or cannot go into your tank
	 * @return true if the fluid can go into the tank, false otherwise
	 */
	public abstract boolean canAccept(Fluid fluid);
	
	
	/**
	 * Generates a request for fluid based on what is being offered. 
	 * @param offer The type of fluid being offered by a fluid producer
	 * @return A FluidRequest object representing how much of the offered fluid you want to take. 
	 * If you don't want any, return <code>FluidRequest.REQUEST_NOTHING</code>
	 */
	@Override
	public FluidRequest getFluidRequest(Fluid offer) {
		if(getTank().getFluidAmount() > 0 && offer.equals(getTank().getFluid().getFluid())){
			return new FluidRequest(FluidRequest.MEDIUM_PRIORITY,
					(getTank().getCapacity() - getTank().getFluidAmount()),
					this);
		} else if(getTank().getFluidAmount() <= 0 && canAccept(offer)){
			return new FluidRequest(FluidRequest.MEDIUM_PRIORITY,
					getTank().getCapacity(),
					this);
		} else {
			return FluidRequest.REQUEST_NOTHING;
		}
	}

	/**
     * Gets the integer array used to pass synchronization data from the server 
     * to the clients.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code>onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     * @return An int[] that you update to match local variables when 
     * <code>prepareDataFieldsForSync()</code> is called and read from to update 
     * local variables when <code>onDataFieldUpdate()</code> is called.
     */
	public int[] getDataFieldArray() {
		return dataFields;
	}
	/**
     * This method is invoked after receiving an update packet from the server. 
     * At the time that this method is invoked, the array returned by 
     * <code>getDataFieldArray()</code> now holds the updated variable values.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code> onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     */
	public void onDataFieldUpdate() {
		// used for server-to-client sync
		int fluidID = dataFields[DATAFIELD_FLUID_ID];
		int fluidVolume = dataFields[DATAFIELD_FLUID_VOLUME];
		if(fluidVolume <= 0){
			getTank().setFluid(new FluidStack(FluidRegistry.WATER,0));
		} else {
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(fluidID),fluidVolume);
			getTank().setFluid(fs);
		}
	}
	
	/**
     * This method is invoked before sending an update packet to the server. 
     * After this method returns, the array returned by 
     * <code>getDataFieldArray()</code> should hold the updated variable values.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code>onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     */
	public void prepareDataFieldsForSync(){
		if(getTank().getFluid() == null || getTank().getFluidAmount() <= 0){
			dataFields[DATAFIELD_FLUID_ID] = FluidRegistry.WATER.getID();
			dataFields[DATAFIELD_FLUID_VOLUME] = 0;
		} else {
			dataFields[DATAFIELD_FLUID_ID] = getTank().getFluid().getFluid().getID();
			dataFields[DATAFIELD_FLUID_VOLUME] = getTank().getFluidAmount();
		}
	}


	/**
	 * Causes Minecraft to send an update packet for this TileEntity
	 */
	public void sync(){
		// cause data update to be sent to client
		worldObj.markBlockForUpdate(getPos());
		this.markDirty();
	}
	
	
}
