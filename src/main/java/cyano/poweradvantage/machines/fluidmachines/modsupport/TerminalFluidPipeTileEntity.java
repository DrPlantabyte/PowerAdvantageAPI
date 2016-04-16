package cyano.poweradvantage.machines.fluidmachines.modsupport;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PowerRequest;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import cyano.poweradvantage.init.Fluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class TerminalFluidPipeTileEntity extends PoweredEntity{

	private final EnumFacing[] faces = EnumFacing.values();
	private final IFluidHandler[] neighbors = new IFluidHandler[6]; 
	
	public TerminalFluidPipeTileEntity() {
		// constructor
	}


	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			World w = getWorld();
			for(int i = 0; i < 6; i++){
				TileEntity te = w.getTileEntity(getPos().offset(faces[i]));
				if(te instanceof IFluidHandler){
					neighbors[i] = (IFluidHandler)te;
				} else {
					neighbors[i] = null;
				}
			}
		}
	}
	
	

	@Override
	public boolean isPowerSink(){
		return true;
	}
	
	@Override
	public boolean isPowerSource(){
		return true;
	}


	@Override
	public boolean canAcceptType(IBlockState blockstate, ConduitType type, EnumFacing blockFace) {
		return ConduitType.areSameType(type,Fluids.fluidConduit_general) || (Fluids.conduitTypeToFluid(type) != null);
	}


	@Override
	public ConduitType getType() {
		return Fluids.fluidConduit_general;
	}



	@Override
	public float getEnergyCapacity() {
		return 0;
	}


	@Override
	public float getEnergy() {
		int sum = 0;
		for(int i = 0; i < 6; i++){
			if(neighbors[i] == null) continue;
			IFluidHandler f = neighbors[i];
			if(f != null) sum += neighbors[i].drain(faces[i].getOpposite(), Integer.MAX_VALUE, false).amount;
		}
		return sum;
	}


	@Override
	public void powerUpdate() {
		for(int i = 0; i < 6; i++){
			if(neighbors[i] == null) continue;
			IFluidHandler n = neighbors[i];
			EnumFacing face = faces[i].getOpposite();
			FluidStack available = n.drain(face, Integer.MAX_VALUE, false);
			if(available != null && available.getFluid() != null && available.amount > 0){
				int delta = transmitFluidToConsumers(available,PowerRequest.LOW_PRIORITY);
				n.drain(face, delta, true);
			}
		}
	}
	
	/**
	 * This class distributes fluids to connected fluid consumers using the standard power network 
	 * registry.
	 * @param available The available fluid to distribute
	 * @param minumimPriority The lowest priority of power request that will be filled.
	 * @return How much fluid was sent out
	 */
	protected int transmitFluidToConsumers(FluidStack available, byte minumimPriority){
		ConduitType type = Fluids.fluidToConduitType(available.getFluid());
		List<PowerRequest> requests = ConduitRegistry.getInstance()
				.getRequestsForPower(getWorld(), getPos(), Fluids.fluidConduit_general,type);
		int bucket = available.amount;
		for(PowerRequest req : requests){
			if(req.entity == this) continue;
			if(req.priority < minumimPriority)break; // relies on list being sorted properly
			if(req.amount <= 0)continue;
			if(req.amount < bucket){
				bucket -= req.entity.addEnergy(req.amount,type);
			} else {
				req.entity.addEnergy(bucket,type);
				bucket = 0;
				break;
			}

		}
		return available.amount - bucket;
	}


	@Override
	public void setEnergy(float energy, ConduitType type) {
		// do nothing
	}
	
	/**
	 * Adds energy to this conductor, up to the maximum allowed energy. The 
	 * amount of energy that was actually added (or subtracted) to the energy 
	 * buffer is returned. 
	 * @param energy The amount of energy to add (can be negative to subtract 
	 * energy).
	 * @param type The type of energy to be added to the buffer
	 * @return The actual change to the internal energy buffer.
	 */
	@Override
	public float addEnergy(float energy, ConduitType type){
		Fluid f = Fluids.conduitTypeToFluid(type);
		if(f == null) return 0;
		int delta = 0, original = (int) energy;
		for(int i = 0; i < 6; i++){
			IFluidHandler n = neighbors[i];
			EnumFacing face = faces[i].getOpposite();
			if(n != null){
				FluidStack fs = new FluidStack(f,original - delta);
				delta += n.fill(face, fs, true);
			}
			if(delta >= original) break;
		}
		return delta;
	}


	@Override
	public PowerRequest getPowerRequest(ConduitType type) {
		Fluid f = Fluids.conduitTypeToFluid(type);
		if(f == null) return PowerRequest.REQUEST_NOTHING;
		FluidStack offer = new FluidStack(f,Integer.MAX_VALUE);
		int demand = 0;
		for(int i = 0; i < 6; i++){
			IFluidHandler n = neighbors[i];
			EnumFacing face = faces[i].getOpposite();
			if(n != null){
				demand += n.fill(face, offer, false);
			}
		}
		return new FluidRequest(PowerRequest.LOW_PRIORITY-1,demand,this);
	}
	
}
