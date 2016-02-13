package cyano.poweradvantage.api.modsupport.rf;

import cyano.poweradvantage.api.ConduitType;

public class TileEntityRFQuantumConverter extends TileEntityRFConverter{

	public TileEntityRFQuantumConverter() {
		super(new ConduitType("quantum"), 1000, 1000, TileEntityRFQuantumConverter.class.getSimpleName());
	}

}
