package cyano.poweradvantage.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This data structure class is used to reduce the parameter list of the ITypedConduit methods to a single pointer.
 * @author DrCyano
 */
public class PowerConnectorContext {

	/** World instance */
	public final IBlockAccess world;
	/** Type of power */
	public final ConduitType powerType;
	/** Block that is asking for the connection */
	public final IBlockState thisBlock;
	/** Position of the block that is asking for the connection */
	public final BlockPos thisBlockPosition;
	/** Face on the block that is asking for the connection */
	public final EnumFacing thisBlockSide;
	/** Block that is being asked for the connection */
	public final IBlockState otherBlock;
	/** Position of the block that is being asked for the connection */
	public final BlockPos otherBlockPosition;
	/** Face on the block that is being asked for the connection */
	public final EnumFacing otherBlockSide;

	/**
	 * Constructs a data structure instance
	 * @param powerType Power type (e.g. "steam")
	 * @param world The world instance that can access this block
	 * @param connectorBlock The block that is testing its neighbors
	 * @param connectorPosition The position of the block that is testing its neighbors
	 * @param connectorSide The side of the block that is testing its neighbors
	 * @param connecteePosition The neighboring block's position
	 * @param connecteeSide The neighboring block's side
	 */
	public PowerConnectorContext(
			final ConduitType powerType,
			final IBlockAccess world,
			final IBlockState connectorBlock,
			final BlockPos connectorPosition,
			final EnumFacing connectorSide,
			final IBlockState connecteeBlock,
			final BlockPos connecteePosition,
			final EnumFacing connecteeSide
	){
		this.world = world;
		this.powerType = powerType;
		this.thisBlock = connectorBlock;
		this.thisBlockPosition = connectorPosition;
		this.thisBlockSide = connectorSide;
		this.otherBlock = connecteeBlock;
		this.otherBlockPosition = connecteePosition;
		this.otherBlockSide = connecteeSide;
	}

	public PowerConnectorContext(
			final ConduitType powerType,
			final IBlockAccess world,
			final IBlockState connectorBlock,
			final BlockPos connectorPosition,
			final EnumFacing connectorSide
	){
		this.world = world;
		this.powerType = powerType;
		this.thisBlock = connectorBlock;
		this.thisBlockPosition = connectorPosition;
		this.thisBlockSide = connectorSide;
		this.otherBlockPosition = connectorPosition.offset(connectorSide);
		this.otherBlockSide = connectorSide.getOpposite();
		this.otherBlock = world.getBlockState(otherBlockPosition);
	}

	/**
	 * Swaps the connector and connectee blocks. This is the same connection view from the opposite direction
	 * @return A reversed instance of this PowerConnectorContext
	 */
	public final PowerConnectorContext reverse(){
		return new PowerConnectorContext(powerType,world, otherBlock, otherBlockPosition, otherBlockSide, thisBlock, thisBlockPosition, thisBlockSide);
	}
}
