package cyano.poweradvantage.api;

import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is the superclass for all machines. 
 * If you are making an add-on mod, you probably want to extend the 
 * <b>cyano.poweradvantage.api.simple.TileEntitySimplePowerConduit</b> class 
 * instead of this class.
 * <p>
 * Conceptually, a PoweredEntity is any block that has an internal energy buffer, 
 * which can be added-to or subtracted-from by other machines connected by conduits.
 * </p>
 * @author DrCyano
 *
 */
public abstract class PoweredEntity extends TileEntity implements ITickable, IPowerMachine{
	
	private final int powerUpdateInterval = 8;
	
	/**
	 * Gets the amount of energy that can be stored in this machine.
	 * @param energyType The type of energy being polled
	 * @return Size of the energy buffer
	 */
	@Override
	public abstract float getEnergyCapacity(ConduitType energyType);
	/**
	 * Gets the amount of energy stored in this machine
	 * @param energyType The type of energy being polled
	 * @return The amount of energy in the energy buffer
	 */
	@Override
	public abstract float getEnergy(ConduitType energyType);
	/**
	 * Sets the amount of energy in the buffer
	 * @param energy The amount of energy to be added to the buffer
	 * @param type The type of energy to be added to the buffer
	 */
	@Override
	public abstract void setEnergy(float energy, ConduitType type); 
	/**
	 * Adds energy to this conductor, up to the maximum allowed energy. The 
	 * amount of energy that was actually added (or subtracted) to the energy 
	 * buffer is returned. 
	 * @param energy The amount of energy to add (can be negative to subtract 
	 * energy).
	 * @param type The type of energy to be added to the buffer
	 * @return The actual change to the internal energy buffer. A negative
	 * number should be returned when subtracting energy and positive when
	 * adding energy.
	 */
	@Override
	public float addEnergy(float energy, ConduitType type){
		if(this.getEnergyCapacity(type) == 0) return 0;
		float newValue = this.getEnergy(type) + energy;
		if(newValue < 0){
			float delta = -1 * this.getEnergy(type);
			this.setEnergy(0,type); 
			return delta;
		} else if(newValue > this.getEnergyCapacity(type)){
			float delta = this.getEnergyCapacity(type) - this.getEnergy(type);
			this.setEnergy(this.getEnergyCapacity(type),type);
			return delta;
		}
		this.setEnergy(newValue,type);
		return energy;
	}
	/**
	 * Subtracts energy from the energy buffer and returns the actual change to 
	 * the energy buffer (which may not be as much as requested). This method 
	 * simply multiplies the input energy value by -1 and then calls the method 
	 * addEnergy(...).  
	 * @param energy The amount of energy to subtract.
	 * @param type The type of energy to be subtracted to the buffer
	 * @return The actual change to the internal energy buffer (a negative number)
	 */
	@Override
	public float subtractEnergy(float energy, ConduitType type){
		return addEnergy(-1 * energy,type);
	}
	
	/**
	 * Tells Minecraft to synchronize this tile entity
	 */
	protected final void sync(){
		this.markDirty();
		//this.getWorld().markBlockForUpdate(getPos());
		Packet packet = this.getUpdatePacket();
		if(packet == null) return;
		List<EntityPlayerMP> players = this.getWorld().getPlayers(EntityPlayerMP.class, (EntityPlayerMP p) -> p.getPosition().distanceSq(getPos()) < 256);
		for(EntityPlayerMP player : players){
			player.connection.sendPacket(packet);
		}
	}

	/**
	 * Override to keep the tile entity from being deleted each time the blockstate is updated
	 * @param world world instance
	 * @param pos coordinate
	 * @param oldState State before change
	 * @param newSate State after change
	 * @return true if this TileEntity should be invalidated (deleted and recreated), false otherwise
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	/**
	 * Method net.minecraft.server.gui.IUpdatePlayerListBox.update() is invoked 
	 * to do tick updates
	 */
	@Override
    public final void update() {
		// this method was moved from TileEntity to IUpdatePlayerListBox
		this.tickUpdate(this.isServer());
		if(this.isServer() && ((this.getWorld().getTotalWorldTime() + this.getTickOffset()) % powerUpdateInterval == 0)){
			this.powerUpdate();
		}
	}
	/**
	 * Called every tick to update. Note that tickUpdate happens before 
	 * powerUpdate().
	 * @param isServerWorld Will be true if the code is executing server-side, 
	 * and false if executing client-side 
	 */
	public abstract void tickUpdate(boolean isServerWorld);
	/**
	 * Updates the power status. This method is called every few ticks and is 
	 * only called on the server world.
	 */
	public abstract void powerUpdate();
	/**
	 * Returns false if this code is executing on the client and true if this 
	 * code is executing on the server
	 * @return true if on server world, false otherwise
	 */
	public boolean isServer(){
		return !this.getWorld().isRemote;
	}
	/**
	 * Returns true if this code is executing on the client and false if this 
	 * code is executing on the server
	 * @return false if on server world, true if on client
	 */
	public boolean isClient(){
		return this.getWorld().isRemote;
	}
	/**
	 * Returns a number that is used to spread the power updates in a chunk 
	 * across multiple ticks. Also keeps adjacent conductors from updating in 
	 * the same tick. 
	 * @return
	 */
	private final int getTickOffset(){
		BlockPos coord = this.getPos();
		int x = coord.getX();
		int y = coord.getY();
		int z = coord.getZ();
		return ((z & 1) << 2) | ((x & 1) << 1) | ((y & 1) );
	}
	/**
	 * Reads data from NBT, which came from either a saved chunk or a network 
	 * packet.
	 */
	@Override
    public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if(tagRoot.hasKey("Energy")){
			int[] data = tagRoot.getIntArray("Energy");
			for(int i = 0; i < data.length; i++){
				if(i >= this.getTypes().length)break;
				this.setEnergy(Float.intBitsToFloat(data[i]),this.getTypes()[i]);
			}
		}
	}
	/**
	 * Saves the state of this entity to an NBT for saving or synching across 
	 * the network.
	 */
	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		int[] data = new int[this.getTypes().length];
		for(int i = 0; i < data.length; i++){
			data[i] = Float.floatToIntBits(this.getEnergy(this.getTypes()[i]));
		}
		tagRoot.setIntArray("Energy", data);
		return tagRoot;
	}
	
	

	/**
	 * Collects all requests for energy from power sinks connected to this tile entity
	 * @param powerType The type of power available to donate to the sinks
	 * @return Returns a list of all connected entities that want energy, in order from highest 
	 * priority to lowest (energy storage will have lower priority than machines).
	 */
	protected List<PowerRequest> getRequestsForPower(ConduitType powerType){
		return ConduitRegistry.getInstance().getRequestsForPower(getWorld(), getPos(), powerType,powerType);
	}
	
	/**
	 * Specify how much energy this power sink wants from a power generator. If this tile entity is 
	 * not a sink, then simply return PowerRequest.REQUEST_NOTHING
	 * @param type The type of energy available upon request
	 * @return A PowerRequest instance indicated how much power you'd like to get
	 */
	@Override
	public abstract PowerRequest getPowerRequest(ConduitType type);
	
	/**
	 * This method is invoked when the block is placed using an item that has 
	 * been renamed. Implementations can carry the name over to the placed 
	 * block, but that feature is optional.
	 * @param newName The name of the item that was placed.
	 */
	public void setCustomInventoryName(String newName){
		// optional method
	}
}
