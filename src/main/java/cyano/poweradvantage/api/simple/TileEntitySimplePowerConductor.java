package cyano.poweradvantage.api.simple;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;

public abstract class TileEntitySimplePowerConductor extends PowerConductorEntity{
	private final ConductorType type;
	
	public TileEntitySimplePowerConductor(ConductorType energyType){
		this.type = energyType;
	}
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		// do nothing
	}


	
	private static final EnumFacing[] faces = {EnumFacing.UP,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.NORTH,EnumFacing.WEST,EnumFacing.DOWN};
	
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
		final float[] deficits = new float[6];
		int count = 0;
		float sum = 0;
		for(int n = 0; n < 6; n++){
			TileEntity e = worldObj.getTileEntity(coords[n]);
			if(e instanceof PowerConductorEntity && ((PowerConductorEntity)e).canPushEnergyTo(faces[n], type)){
				deficits[count] = (((PowerConductorEntity)e).getEnergyBufferCapacity() - ((PowerConductorEntity)e).getEnergyBuffer()); // positive number
				if(deficits[count] > 0 && deficits[count] > myDeficit){
					neighbors[count] = (PowerConductorEntity)e;
					sum += deficits[count];
					count++;
				}
			}
		}
		float c  = availableEnergy / sum;
		for(int n = 0; n < count; n++){
			this.subtractEnergy(neighbors[n].addEnergy(availableEnergy / count));//deficits[n] * c));
		}
	}
}
