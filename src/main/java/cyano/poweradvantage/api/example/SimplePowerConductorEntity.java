package cyano.poweradvantage.api.example;

import net.minecraft.block.state.IBlockState;
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
			IBlockState bs = this.worldObj.getBlockState(this.pos);
			
			this.tryEnergyPullFrom(EnumFacing.UP);
			this.tryEnergyPullFrom(EnumFacing.NORTH);
			this.tryEnergyPullFrom(EnumFacing.WEST);
			this.tryEnergyPullFrom(EnumFacing.SOUTH);
			this.tryEnergyPullFrom(EnumFacing.EAST);
			this.tryEnergyPullFrom(EnumFacing.DOWN);
		}
	}
	
	void tryEnergyPullFrom(EnumFacing dir){
		float deficit = Math.min(getEnergyBufferCapacity() - getEnergyBuffer(), energyRequestSize);
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

}
