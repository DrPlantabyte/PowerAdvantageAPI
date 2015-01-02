package cyano.poweradvantage.api.example;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;

public class SimplePowerConductorEntity extends PowerConductorEntity {

	private final ConductorType type;
	private final float energyBufferSize;
	private final float energyRequestSize;
	private float energyBuffer = 0;
	
	public SimplePowerConductorEntity(){
		energyBufferSize = 90f;
		this.type = new ConductorType("energy");
		energyRequestSize = energyBufferSize / 6;
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
	public boolean canPushEnergyTo(EnumFacing blockFace,
			ConductorType requestType) {
		return ConductorType.areSameType(getEnergyType(), requestType);
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}

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
	
	void tryEnergyPullFrom(EnumFacing dir){
		float deficit = Math.max(getEnergyBufferCapacity() - getEnergyBuffer(), energyRequestSize);
		if(deficit > 0){
			EnumFacing otherDir = null;
			BlockPos coord = null;
			switch(dir){
			case UP:
				coord = getPos().add(0,1,0);
				otherDir = EnumFacing.DOWN;
			case DOWN:
				coord = getPos().add(0,-1,0);
				otherDir = EnumFacing.UP;
			case NORTH:
				coord = getPos().add(0,0,-1);
				otherDir = EnumFacing.SOUTH;
			case SOUTH:
				coord = getPos().add(0,0,1);
				otherDir = EnumFacing.NORTH;
			case EAST:
				coord = getPos().add(1,0,0);
				otherDir = EnumFacing.WEST;
			case WEST:
				coord = getPos().add(-1,0,0);
				otherDir = EnumFacing.EAST;
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

}
