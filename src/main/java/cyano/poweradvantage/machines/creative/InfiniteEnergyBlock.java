package cyano.poweradvantage.machines.creative;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerConsumer;

public class InfiniteEnergyBlock extends BlockSimplePowerConsumer{

	public InfiniteEnergyBlock(ConduitType energyType) {
		super(Material.iron, 0.75f, energyType);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new InfiniteEnergyTileEntity(this.getType());
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return false;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		return 0;
	}

}
