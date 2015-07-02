package cyano.poweradvantage.api.modsupport;

import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConduitType;

/**
 * This class is a generic wrapper to wrap all blocks in the event that one of them might use 
 * Redstone Flux (RF). Used for automated RF compatibility (not recommended).
 * @author DrCyano
 *
 */
public class MaybeRFPowerAcceptor implements ILightWeightPowerAcceptor{
	private final Map<ConduitType,Number> conversionTable;

	public MaybeRFPowerAcceptor(Map<ConduitType,Number> conversionTable){
		this.conversionTable = conversionTable;
	}
	
	@Override
	public boolean canAcceptEnergyType(ConduitType powerType) {
		return conversionTable.containsKey(powerType);
	}

	@Override
	public float getEnergyDemand(TileEntity te, ConduitType powerType) {
		if(te instanceof cofh.api.energy.IEnergyReceiver){
			cofh.api.energy.IEnergyReceiver r = (cofh.api.energy.IEnergyReceiver)te;
			float rfDeficit = r.getMaxEnergyStored(EnumFacing.UP) - r.getEnergyStored(EnumFacing.UP);
			return rfDeficit / conversionTable.get(powerType).floatValue();
		}
		return 0;
	}

	@Override
	public float addEnergy(TileEntity te, float amountAdded,
			ConduitType powerType) {
		if(te instanceof cofh.api.energy.IEnergyReceiver){
			cofh.api.energy.IEnergyReceiver r = (cofh.api.energy.IEnergyReceiver)te;
			float rfDeficit = r.getMaxEnergyStored(EnumFacing.UP) - r.getEnergyStored(EnumFacing.UP);
			float powerDeficit = rfDeficit / conversionTable.get(powerType).floatValue();
			float delta = Math.min(powerDeficit, amountAdded);
			return r.receiveEnergy(EnumFacing.UP,(int)(conversionTable.get(powerType).floatValue() * delta),false)
					/ conversionTable.get(powerType).floatValue();
		} else {
			return 0;
		}
	}

}
