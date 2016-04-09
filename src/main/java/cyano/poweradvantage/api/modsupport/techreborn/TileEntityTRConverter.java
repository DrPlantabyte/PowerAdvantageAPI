package cyano.poweradvantage.api.modsupport.techreborn;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.modsupport.TileEntityConverter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import reborncore.api.power.EnumPowerTier;

/**
 * Created by Chris on 4/9/2016.
 */
public class TileEntityTRConverter extends TileEntityConverter implements reborncore.api.power.IEnergyInterfaceTile {

	private final double MAX_INPUT = 2048;
	private final double euBufferMax = MAX_INPUT;
	private final double halfBuffer = 0.5 * euBufferMax;
	private double euBuffer = 0;
	private final ConduitType powerAdvantageType;

	public TileEntityTRConverter(ConduitType powerAdvantageType){
		super(powerAdvantageType,1024F,TileEntityTRConverter.class.getSimpleName());
		this.powerAdvantageType = powerAdvantageType;
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			World w = getWorld();
			int sub = ((int)(w.getTotalWorldTime() & 0x0FFFFFFF) % 11);
			EnumFacing[] sides = EnumFacing.values();

			double delta = euBuffer - halfBuffer;
			if(delta < 0){
				if(sub < sides.length){
					EnumFacing dir = sides[sub];
					TileEntity te = w.getTileEntity(getPos().offset(dir));
					if(te instanceof reborncore.api.power.IEnergyInterfaceTile){
						reborncore.api.power.IEnergyInterfaceTile trMachine = (reborncore.api.power.IEnergyInterfaceTile)te;
						if(trMachine.canProvideEnergy(dir.getOpposite()) && trMachine.getEnergy() > 0){
							double amount = min(this.getMaxInput(), trMachine.getMaxOutput(), trMachine.getEnergy(),-1*delta);
							this.addEnergy(trMachine.useEnergy(amount));
						}
					}
				}
			} else if (delta > 0){
				if(sub < sides.length){
					EnumFacing dir = sides[sub];
					TileEntity te = w.getTileEntity(getPos().offset(dir));
					if(te instanceof reborncore.api.power.IEnergyInterfaceTile){
						reborncore.api.power.IEnergyInterfaceTile trMachine = (reborncore.api.power.IEnergyInterfaceTile)te;
						if(trMachine.canAcceptEnergy(dir.getOpposite()) && trMachine.getEnergy() < trMachine.getMaxPower()){
							double amount = min(trMachine.getMaxInput(), this.getMaxOutput(), trMachine.getMaxPower() - trMachine.getEnergy(),delta);
							trMachine.addEnergy(this.useEnergy(amount));
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the amount of energy in the non-power-advantage energy buffer
	 *
	 * @return Energy in non-power-advantage energy buffer
	 */
	@Override
	public double getOtherEnergy() {
		return euBuffer;
	}

	/**
	 * Gets the energy capacity of the non-power-advantage energy buffer
	 *
	 * @return Energy capacity
	 */
	@Override
	public double getOtherEnergyCapacity() {
		return euBufferMax;
	}

	/**
	 * Sets the amount of energy in the non-power-advantage energy buffer
	 *
	 * @param value Energy in non-power-advantage energy buffer
	 */
	@Override
	public void setOtherEnergy(double value) {
		this.euBuffer = value;
	}

	/**
	 * Determines whether the given Power Advantage energy type can be converted
	 *
	 * @param type Power Advantage energy type
	 * @return true if the energy can be converted, false otherwise
	 */
	@Override
	public boolean canConvertEnergy(ConduitType type) {
		return  ConduitType.areSameType(powerAdvantageType,type);
	}

	@Override
	public float addEnergyToOther(float amount, ConduitType type) {
		if(canConvertEnergy(type)){
			int delta = (int)Math.min(this.getOtherEnergyCapacity() - getOtherEnergy(),
					this.convertEnergyToOther( amount,type));
			euBuffer += delta;
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
			euBuffer += delta;
			return convertOtherToEnergy(delta,type);
		} else {
			return 0;
		}
	}

	/**
	 * Performs an energy conversion between Power Advantage energy and the other energy type.
	 *
	 * @param amountOfOther Amount of non-power-advantage energy to convert
	 * @param type          Type of Power Advantage energy
	 * @return Amount of Power Advantage energy equivalent to the input energy.
	 */
	@Override
	public float convertOtherToEnergy(double amountOfOther, ConduitType type) {
		return (float)(amountOfOther / PowerAdvantage.trConversionTable.get(powerAdvantageType));
	}

	/**
	 * Performs an energy conversion between Power Advantage energy and the other energy type.
	 *
	 * @param amountOfEnergy Amount of Power Advantage energy to convert
	 * @param type           Type of Power Advantage energy
	 * @return Amount of non-power-advantage energy equivalent to the input energy.
	 */
	@Override
	public double convertEnergyToOther(float amountOfEnergy, ConduitType type) {
		return amountOfEnergy * PowerAdvantage.trConversionTable.get(powerAdvantageType);
	}

	private static double min(double... vals){
		double n = Double.POSITIVE_INFINITY;
		for (int i = 0; i < vals.length; i++){
			if(vals[i] < n) n = vals[i];
		}
		return n;
	}
	private static double max(double... vals){
		double n = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < vals.length; i++){
			if(vals[i] > n) n = vals[i];
		}
		return n;
	}

	@Override
	public double getEnergy() {
		return euBuffer;
	}

	@Override
	public void setEnergy(double v) {
		euBuffer = v;
	}

	@Override
	public double getMaxPower() {
		return euBufferMax;
	}

	@Override
	public boolean canAddEnergy(double v) {
		return true;
	}

	@Override
	public double addEnergy(double v) {
		return addEnergy(v,false);
	}

	@Override
	public double addEnergy(double v, boolean b) {
		double delta = Math.min(euBufferMax - euBuffer,v);
		if(!b) euBuffer += delta;
		return delta;
	}

	@Override
	public boolean canUseEnergy(double v) {
		return true;
	}

	@Override
	public double useEnergy(double v) {
		return 0;
	}

	@Override
	public double useEnergy(double v, boolean b) {
		double delta = Math.min(euBuffer,v);
		if(!b) euBuffer -= delta;
		return delta;
	}

	@Override
	public boolean canAcceptEnergy(EnumFacing enumFacing) {
		return true;
	}

	@Override
	public boolean canProvideEnergy(EnumFacing enumFacing) {
		return true;
	}

	@Override
	public double getMaxOutput() {
		return MAX_INPUT;
	}

	@Override
	public double getMaxInput() {
		return MAX_INPUT;
	}

	@Override
	public EnumPowerTier getTier() {
		return EnumPowerTier.LOW;
	}
}
