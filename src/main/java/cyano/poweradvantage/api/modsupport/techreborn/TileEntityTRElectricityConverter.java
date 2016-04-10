package cyano.poweradvantage.api.modsupport.techreborn;

import cyano.poweradvantage.api.ConduitType;

/**
 * Created by Chris on 4/10/2016.
 */
public class TileEntityTRElectricityConverter extends TileEntityTRConverter {
	public TileEntityTRElectricityConverter() {
		super(new ConduitType("electricity"), TileEntityTRElectricityConverter.class.getSimpleName());
	}
}
