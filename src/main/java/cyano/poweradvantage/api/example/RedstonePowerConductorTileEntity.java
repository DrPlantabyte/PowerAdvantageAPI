package cyano.poweradvantage.api.example;

import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConductor;

public class RedstonePowerConductorTileEntity extends TileEntitySimplePowerConductor {

	public RedstonePowerConductorTileEntity() {
		super(new ConductorType("redstone"), 384);
	}

}
