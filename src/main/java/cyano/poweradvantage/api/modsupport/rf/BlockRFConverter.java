package cyano.poweradvantage.api.modsupport.rf;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.simple.BlockSimplePowerMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRFConverter extends BlockSimplePowerMachine {

	private final Class tileEntityClass;
	public BlockRFConverter(Material blockMaterial, float hardness, ConduitType energyType, Class<? extends TileEntityRFConverter> tileEntityClass) {
		super(blockMaterial, hardness, energyType);
		this.tileEntityClass = tileEntityClass;
	}


	@Override
	public TileEntityRFConverter createNewTileEntity(World world, int metaDataValue) {
		try {
			return (TileEntityRFConverter)tileEntityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	@Override
	public boolean isNormalCube(IBlockState bs){
		return false;
	}


	@Override
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
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
	 * Determines whether this block/entity should receive energy of this type. If this is not a sink, then it
	 * will never be given power by a power source.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return true;
	}

	/**
	 * Determines whether this block/entity can provide energy of this type.
	 *
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return true;
	}
}
