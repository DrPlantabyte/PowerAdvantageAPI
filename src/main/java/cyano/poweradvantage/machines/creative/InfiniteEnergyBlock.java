package cyano.poweradvantage.machines.creative;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfiniteEnergyBlock extends BlockSimplePowerMachine {

	public InfiniteEnergyBlock(ConduitType energyType) {
		super(Material.iron, 0.75f, energyType);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new InfiniteEnergyTileEntity(this.getTypes()[0]);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs) {
		return false;
	}

	@Override
	public int getComparatorInputOverride(IBlockState bs, World world, BlockPos coord) {
		return 0;
	}

	/**
	 * Determines whether this block/entity should receive energy. If this is not a sink, then it
	 * will never be given power by a power source.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return false;
	}

	/**
	 * Determines whether this block/entity can provide energy.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return true;
	}
}
