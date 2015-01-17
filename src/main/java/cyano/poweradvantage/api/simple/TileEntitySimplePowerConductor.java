package cyano.poweradvantage.api.simple;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.PowerSourceEntity;

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


	
}
