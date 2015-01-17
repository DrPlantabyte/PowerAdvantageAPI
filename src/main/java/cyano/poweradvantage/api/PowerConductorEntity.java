package cyano.poweradvantage.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cyano.poweradvantage.api.simple.TileEntitySimplePowerConductor;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
/**
 * This class is the super-class for all powered blocks. Wires are plain 
 * conductors, but generators and machines are also conductors (they should 
 * extend the PowerSourceEntity and PowerSinkEntity classes respectively). 
 * <p>
 * Conceptually, a conductor is any block that has an internal energy buffer, 
 * which can be added-to or subtracted-from by adjacent (conductor) blocks.
 * </p>
 * @author DrCyano
 *
 */
public abstract class PowerConductorEntity extends TileEntity implements IUpdatePlayerListBox, ITypedConductor{
	
	private final int powerUpdateInterval = 8;
	
	
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
	public void powerUpdate() {
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().up()));
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().north()));
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().west()));
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().south()));
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().east()));
		safeSyncPowerSourceData(worldObj.getTileEntity(this.getPos().down()));
		revalidatePowerSourceList();
	}
	

	protected void safeSyncPowerSourceData(TileEntity other){
		if(other instanceof PowerConductorEntity)syncPowerSourceData((PowerConductorEntity)other);
	}
	
	protected void syncPowerSourceData(PowerConductorEntity other){
		if(other instanceof PowerSourceEntity){
			this.getKnownPowerSources().add((PowerSourceEntity)other);
		}
		this.getKnownPowerSources().addAll(other.getKnownPowerSources());
	}
	/**
	 * Returns false if this code is executing on the client and true if this 
	 * code is executing on the server
	 * @return true if on server world, false otherwise
	 */
	public boolean isServer(){
		return !this.getWorld().isRemote;
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
		int y = coord.getX();
		int z = coord.getX();
		return ((z & 1) << 2) | ((x & 1) << 1) | ((y & 1) );
	}
	
	/**
	 * Removes destroyed and obsolete TileEntities from the list of power 
	 * sources.
	 */
	protected void revalidatePowerSourceList(){
		java.util.Set<PowerSourceEntity> powerSources = getKnownPowerSources();
		List<PowerSourceEntity> invalidEntities = new ArrayList<>();
		for(PowerSourceEntity src : powerSources){
			if(src.isInvalid()){
				// remove destroyed tile entities
				invalidEntities.add(src);
			}
		}
		powerSources.removeAll(invalidEntities);
	}

	/** Maps power-source to power-limit */
	private Set<PowerSourceEntity> powerSources = new HashSet<PowerSourceEntity>();
	public Set<PowerSourceEntity> getKnownPowerSources() {
		return powerSources;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName()+"@("+this.getPos().getX()+","+this.getPos().getY()+","+this.getPos().getZ()+")";
	}
	
	public int hashCode(){
		return this.getPos().hashCode();
	}
}
