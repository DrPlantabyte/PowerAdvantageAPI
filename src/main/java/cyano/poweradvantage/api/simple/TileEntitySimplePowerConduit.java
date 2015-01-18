package cyano.poweradvantage.api.simple;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.PowerSinkEntity;

/**
 * This block implements the cyano.poweradvantage.api.PowerConductorEntity 
 * class and handles all of the mundane details for you. In fact, you do not 
 * need to override any methods to create your own power conduit.  
 * @author DrCyano
 *
 */
public abstract class TileEntitySimplePowerConduit extends PowerConductorEntity{
	private final ConductorType type;
	private final float energyBufferSize;
	private final float energyRequestSize;
	private float energyBuffer = 0;
	
	/**
	 * Precomputed value for improved performance
	 */
	private boolean isEmpty = true;
	/**
	 * 
	 * @param energyType This is the type of energy that is allowed to flow 
	 * through this block.
	 * @param bufferSize This is the amount of energy that this block can store. 
	 * Increasing this value will increase the amount of power that can flow 
	 * through this block.
	 */
	public TileEntitySimplePowerConduit(ConductorType energyType, float bufferSize){
		this.type = energyType;
		this.energyBufferSize = bufferSize;
		this.energyRequestSize = this.energyBufferSize;// * 0.25f; 
	}
	/**
	 * Gets the energy type for this block.
	 * @return The type of energy/power for this block
	 */
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

	/**
	 * Gets the maximum amount of energy that can be stored in this block.
	 * @return Maximum energy storage
	 */
	@Override
	public float getEnergyBufferCapacity() {
		return energyBufferSize;
	}

	/**
	 * Gets the amount of energy currently stored in this block
	 * @return Current energy level
	 */
	@Override
	public float getEnergyBuffer() {
		return energyBuffer;
	}

	/**
	 * Sets the amount of energy stored in this block
	 * @param energy New value of the energy buffer
	 */
	@Override
	public void setEnergy(float energy) {
		energyBuffer = energy;
		isEmpty = energy <= 0;
	}
	/**
	 * Determines whether energy can be pulled from this block by a conductor of 
	 * the indicated energy type
	 * @param blockFace The side of the block from which someone wants to pull 
	 * the energy 
	 * @param requestType The type of energy of the other conductor that wants 
	 * energy from this block 
	 * @return True if energy is allowed to be pulled from the given side of 
	 * this block
	 */
	@Override
	public boolean canPullEnergyFrom(EnumFacing blockFace,
			ConductorType requestType) {
		return ConductorType.areSameType(getEnergyType(), requestType);
	}
	
	/**
	 * Determines whether energy can be added to this block by a conductor of 
	 * the indicated energy type
	 * @param blockFace The side of the block into which someone wants to push 
	 * the energy 
	 * @param requestType The type of energy of the other conductor that wants 
	 * to add energy to this block 
	 * @return True if energy is allowed to be pushed into the given side of 
	 * this block
	 */
	@Override
	public boolean canPushEnergyTo(EnumFacing blockFace,
			ConductorType requestType) {
		return ConductorType.areSameType(getEnergyType(), requestType);
	}
	/**
	 * Invoked on each world tick
	 * @param isServerWorld true if this code is executing on the server, false 
	 * if it is executing on the client. Most logic should only run on the 
	 * server to avoid synchronization issues.
	 */
	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}

	/** implementation detail for the powerUpdate() method. Do not touch! */
	private static final EnumFacing[] faces = {EnumFacing.UP,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.DOWN};
	
	/**
	 * This method handles the transmission of energy. If you override this 
	 * method, be sure to call super.powerUpdate() or power will not flow 
	 * through this block.
	 */
	@Override
	public void powerUpdate() {
		if(isEmpty)return;
		float myDeficit = this.energyBufferSize - this.energyBuffer;
		float availableEnergy = Math.min(energyBuffer, energyRequestSize);
		final BlockPos[] coords = new BlockPos[6];
		coords[0] = this.pos.down();
		coords[1] = this.pos.north();
		coords[2] = this.pos.west();
		coords[3] = this.pos.south();
		coords[4] = this.pos.east();
		coords[5] = this.pos.up();
		final PowerConductorEntity[] neighbors = new PowerConductorEntity[6];
		final float[] buffers = new float[6];
		int count = 0;
		float sum = energyBuffer;
		for(int n = 0; n < 6; n++){
			TileEntity e = worldObj.getTileEntity(coords[n]);
			if(e instanceof PowerConductorEntity && ((PowerConductorEntity)e).canPushEnergyTo(faces[n], type)){
				if(e instanceof PowerSinkEntity) { // make sinks always appear empty
					neighbors[count] = (PowerConductorEntity)e;
					count++;
					continue;
				}
				neighbors[count] = (PowerConductorEntity)e;
				sum += ((PowerConductorEntity)e).getEnergyBuffer();
				count++;
			}
		}
		if(count == 0) return;
		float ave = sum / (count+1);
		for(int n = 0; n < count; n++){
			if(neighbors[n] instanceof PowerSinkEntity){
				this.subtractEnergy(neighbors[n].addEnergy(ave));
			}else {
				this.subtractEnergy(neighbors[n].addEnergy(ave - neighbors[n].getEnergyBuffer()));
			}
		}
	}
}
