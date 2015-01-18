package cyano.poweradvantage.api.example;

import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConduit;

public class RedstonePowerConductorTileEntity extends TileEntitySimplePowerConduit {

	public RedstonePowerConductorTileEntity() {
		super(new ConductorType("redstone"), 100);
	}

}
