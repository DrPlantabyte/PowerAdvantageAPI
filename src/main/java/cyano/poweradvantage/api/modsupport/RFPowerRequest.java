package cyano.poweradvantage.api.modsupport;

import cofh.api.energy.IEnergyReceiver;
import cyano.poweradvantage.api.PowerRequest;
import net.minecraft.util.EnumFacing;

public class RFPowerRequest extends PowerRequest {

	public final IEnergyReceiver RFPowerAcceptor;
	/**
	 * Constructs a new request for an external machine block.
	 * @param requestSize Amount of energy requested
	 * @param source The source IEnergyReceiver instance
	 */
	public RFPowerRequest(float requestSize, IEnergyReceiver source) {
		super(0, requestSize, null);
		RFPowerAcceptor = source;
	}

	
	public int fillRequest(int addedRF){
		for(EnumFacing f : EnumFacing.values()){
			if(RFPowerAcceptor.canConnectEnergy(f)){
				return RFPowerAcceptor.receiveEnergy(f, addedRF, false);
			}
		}
		return 0;
	}
}
