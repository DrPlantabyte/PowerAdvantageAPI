package cyano.poweradvantage.api.modsupport;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.IPowerMachine;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.api.PowerRequest;
import net.minecraft.util.EnumFacing;

/**
 * Created by Chris on 4/9/2016.
 */
public abstract class Wrappers {
	/**
	 * Wraps a Redstone Flux machine in a PowerRequest
	 * @param rfMachine The Redstone Flux machine to interface
	 * @param powerType The Power Advantage energy type
	 * @param conversionFactor of RF units per power unit
	 * @return Returns a Power Advantage compatible PowerRequest instance
	 */
	public static PowerRequest wrapRFPowerRequest(final cofh.api.energy.IEnergyReceiver rfMachine, ConduitType powerType, final float conversionFactor){
		final float invertedConversionFactor = 1.0F / conversionFactor;
		EnumFacing s = null;
		for(int i = 0; i < EnumFacing.values().length; i++){
			EnumFacing f = EnumFacing.values()[i];
			if(rfMachine.canConnectEnergy(f)){
				s = f;
				break;
			}
		}
		if(s == null) return PowerRequest.REQUEST_NOTHING;
		final EnumFacing side = s;
		final float requestSize = (rfMachine.getMaxEnergyStored(side) - rfMachine.getEnergyStored(side)) * invertedConversionFactor;
		IPowerMachine wrap = new IPowerMachine() {
			@Override
			public float getEnergyCapacity(ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return rfMachine.getMaxEnergyStored(side) * invertedConversionFactor;
				} else{
					return 0;
				}
			}

			@Override
			public float getEnergy(ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return rfMachine.getEnergyStored(side) * invertedConversionFactor;
				} else{
					return 0;
				}
			}

			@Override
			public void setEnergy(float energy, ConduitType energyType) {
					// do nothing
			}

			@Override
			public float addEnergy(float energy, ConduitType energyType) {
						if(ConduitType.areSameType(powerType,energyType)) {
							return invertedConversionFactor * rfMachine.receiveEnergy(side,(int)(energy * conversionFactor),false);
						} else{
							return 0;
						}
			}

			@Override
			public float subtractEnergy(float energy, ConduitType energyType) {
				// do nothing
				return 0;
			}

			@Override
			public PowerRequest getPowerRequest(ConduitType energyType) {
				return PowerRequest.REQUEST_NOTHING;
			}

			@Override
			public boolean canAcceptConnection(PowerConnectorContext connection) {
				return false;
			}

			@Override
			public ConduitType[] getTypes() {
				return new ConduitType[0];
			}

			@Override
			public boolean isPowerSink(ConduitType powerType) {
				return true;
			}

			@Override
			public boolean isPowerSource(ConduitType powerType) {
				return false;
			}
		};

		return new PowerRequest(PowerRequest.LOW_PRIORITY,requestSize,wrap);
	}

	/**
	 * Wraps a Tech Reborn machine in a PowerRequest
	 * @param trMachine The Tech Reborn machine to interface
	 * @param powerType The Power Advantage energy type
	 * @param conversionFactor of RF units per power unit
	 * @return Returns a Power Advantage compatible PowerRequest instance
	 */
	public static PowerRequest wrapTRPowerRequest(final reborncore.api.power.IEnergyInterfaceTile trMachine, ConduitType powerType, final float conversionFactor){
		final float invertedConversionFactor = 1.0F / conversionFactor;
		final float requestSize = (float)Math.min(trMachine.getMaxInput(),trMachine.getMaxPower() - trMachine.getEnergy()) * invertedConversionFactor;
		if(requestSize == 0) return PowerRequest.REQUEST_NOTHING;
		IPowerMachine wrap = new IPowerMachine() {
			@Override
			public float getEnergyCapacity(ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return (float)(trMachine.getMaxPower() * invertedConversionFactor);
				} else{
					return 0;
				}
			}

			@Override
			public float getEnergy(ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return (float)(trMachine.getEnergy() * invertedConversionFactor);
				} else{
					return 0;
				}
			}

			@Override
			public void setEnergy(float energy, ConduitType energyType) {
				// do nothing
			}

			@Override
			public float addEnergy(float energy, ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return invertedConversionFactor * (float)trMachine.addEnergy(energy * conversionFactor);
				} else{
					return 0;
				}
			}

			@Override
			public float subtractEnergy(float energy, ConduitType energyType) {
				if(ConduitType.areSameType(powerType,energyType)) {
					return -1 * invertedConversionFactor * (float)trMachine.useEnergy(energy * conversionFactor);
				} else{
					return 0;
				}
			}

			@Override
			public PowerRequest getPowerRequest(ConduitType energyType) {
				return PowerRequest.REQUEST_NOTHING;
			}

			@Override
			public boolean canAcceptConnection(PowerConnectorContext connection) {
				return true;
			}

			@Override
			public ConduitType[] getTypes() {
				return new ConduitType[0];
			}

			@Override
			public boolean isPowerSink(ConduitType powerType) {
				return true;
			}

			@Override
			public boolean isPowerSource(ConduitType powerType) {
				return false;
			}
		};

		return new PowerRequest(PowerRequest.LOW_PRIORITY,requestSize,wrap);
	}
}
