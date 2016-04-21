package cyano.poweradvantage.api.modsupport.rf;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.modsupport.TileEntityConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEntityRFConverter extends TileEntityConverter implements cofh.api.energy.IEnergyProvider, cofh.api.energy.IEnergyReceiver{

	private final int rfBufferSize;
	private final int halfBuffer;
	private int rfBuffer = 0;
	
	public TileEntityRFConverter(ConduitType type, float powerAdvantageEnergyBuffer, int rfBuffer, String unlocalizedName) {
		super(type, powerAdvantageEnergyBuffer, unlocalizedName);
		this.rfBufferSize = rfBuffer;
		this.halfBuffer = rfBufferSize / 2;
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			World w = getWorld();
			int sub = ((int)(w.getTotalWorldTime() & 0x0FFFFFFF) % 11);
			EnumFacing[] sides = EnumFacing.values();
			
			int delta = rfBuffer - halfBuffer;
			if(delta < 0){
				if(sub < sides.length){
					EnumFacing dir = sides[sub];
					TileEntity te = w.getTileEntity(getPos().offset(dir));
					if(te instanceof cofh.api.energy.IEnergyProvider){
						cofh.api.energy.IEnergyProvider rfMachine = (cofh.api.energy.IEnergyProvider)te;
						if(rfMachine.canConnectEnergy(dir.getOpposite()) && rfMachine.getEnergyStored(dir.getOpposite()) > 0){
							this.receiveEnergy(dir, rfMachine.extractEnergy(dir.getOpposite(), -1 * delta,false), false);
						}
					}
				}
			} else if (delta > 0){
				if(sub < sides.length){
					EnumFacing dir = sides[sub];
					TileEntity te = w.getTileEntity(getPos().offset(dir));
					if(te instanceof cofh.api.energy.IEnergyReceiver){
						cofh.api.energy.IEnergyReceiver rfMachine = (cofh.api.energy.IEnergyReceiver)te;
						if(rfMachine.canConnectEnergy(dir.getOpposite()) && rfMachine.getEnergyStored(dir.getOpposite()) < rfMachine.getMaxEnergyStored(dir.getOpposite())){
							rfMachine.receiveEnergy(dir.getOpposite(), this.extractEnergy(dir, delta,false), false);
						}
					}
				}
			}
		}
	}
	
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return rfBuffer;
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return rfBufferSize;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int delta = Math.min(this.getMaxEnergyStored(from) - this.getEnergyStored(from), 
				maxReceive);
		if(!simulate){
			rfBuffer += delta;
		}
		return delta;
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		int delta = Math.min(this.getEnergyStored(from), 
				maxExtract);
		if(!simulate){
			rfBuffer -= delta;
		}
		return delta;
	}
	@Override
	public double getOtherEnergy() {
		return rfBuffer;
	}
	@Override
	public double getOtherEnergyCapacity() {
		return rfBufferSize;
	}
	@Override
	public void setOtherEnergy(double value) {
		rfBuffer = (int)value;
	}
	@Override
	public boolean canConvertEnergy(ConduitType type) {
		return PowerAdvantage.rfConversionTable.containsKey(type);
	}
	@Override
	public float addEnergyToOther(float amount, ConduitType type) {
		if(canConvertEnergy(type)){
			int delta = (int)Math.min(this.getOtherEnergyCapacity() - getOtherEnergy(), 
					this.convertEnergyToOther( amount,type));
			rfBuffer += delta;
			return convertOtherToEnergy(delta,type);
		} else {
			return 0;
		}
	}
	@Override
	public float subtractEnergyFromOther(float amount, ConduitType type) {
		if(canConvertEnergy(type)){
			int delta = -1 * (int)Math.min(getOtherEnergy(), 
					this.convertEnergyToOther( amount,type));
			rfBuffer += delta;
			return convertOtherToEnergy(delta,type);
		} else {
			return 0;
		}
	}
	@Override
	public float convertOtherToEnergy(double amountOfOther, ConduitType type) {
		return (float)(amountOfOther / PowerAdvantage.rfConversionTable.get(type));
	}
	@Override
	public double convertEnergyToOther(float amountOfEnergy, ConduitType type) {
		return amountOfEnergy * PowerAdvantage.rfConversionTable.get(type);
	}


}
