package cyano.poweradvantage.api.modsupport.techreborn;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Chris on 4/9/2016.
 */
public class BlockTRConverter extends BlockSimplePowerMachine {

	private final ConduitType powerAdvantageType;
	/**
	 * Standard constructor for a machine block. Remember to set the GUI ID so that a GUI can pop-up
	 * when the player interacts with this block.
	 *
	 * @param blockMaterial This is the material for the block. Typically is set
	 *                      to net.minecraft.block.material.Material.piston, though any material can
	 *                      be used.
	 * @param hardness      This affects how long it takes to break the block. 0.5 is
	 *                      a good value if you want it to be easy to break.
	 * @param energyType    This is the energy type for this block. This is used by
	 */
	public BlockTRConverter(Material blockMaterial, float hardness, ConduitType energyType) {
		super(blockMaterial, hardness, energyType);
		powerAdvantageType = energyType;
	}

	/**
	 * Creates a TileEntity for this block when the block is placed into the
	 * world.
	 *
	 * @param world World object
	 * @param metaDataValue Block data meta value
	 * @return A new TileEntity instance, probably one that extends
	 * <b>TileEntitySimplePowerMachine</b>.
	 */
	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new TileEntityTRConverter(powerAdvantageType);
	}

	/**
	 * This method tells Minecraft whether this block has a signal that can be
	 * measured by a redstone comparator.
	 * <br><br>
	 * You are encouraged to integrate redstone control into your machines.
	 * This means outputin a redstone signal with the
	 * <code>getComparatorInputOverride(...)</code> method and (in your
	 * TileEntity class) reading the redstone input with the
	 * <code>World.isBlockPowered(...)</code> method.<br><br>
	 * Typically, machines output a redstone value proportional to the amount of
	 * stuff in their inventory and are disabled when they receive a redstone
	 * signal.
	 *
	 * @param state The blockstate of this block
	 * @return true if this block can be measured by a redstone comparator,
	 * false otherwise
	 */
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return false;
	}

	/**
	 * This method gets the output for a redstone comparator placed against this
	 * block.
	 * <br><br>
	 * You are encouraged to integrate redstone control into your machines.
	 * This means outputin a redstone signal with the
	 * <code>getComparatorInputOverride(...)</code> method and (in your
	 * TileEntity class) reading the redstone input with the
	 * <code>World.isBlockPowered(...)</code> method.<br><br>
	 * Typically, machines output a redstone value proportional to the amount of
	 * stuff in their inventory and are disabled when they receive a redstone
	 * signal.
	 *
	 * @param state The blockstate of this block
	 * @param world World object
	 * @param coord Coordinates of this block
	 * @return a number from 0 to 15
	 */
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos coord) {
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
