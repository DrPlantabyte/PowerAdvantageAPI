package cyano.poweradvantage.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This data structure class is used to reduce the parameter list of the ITypedConduit methods to a single pointer.
 * @author DrCyano
 */
public class PowerConnectorContext {

	/** World instance */
	public final World world;
	/** Type of power */
	public final ConduitType powerType;
	/** Block that is asking for the connection */
	public final IBlockState connectorBlock;
	/** Position of the block that is asking for the connection */
	public final BlockPos connectorPosition;
	/** Face on the block that is asking for the connection */
	public final EnumFacing connectorSide;
	/** Block that is being asked for the connection */
	public final IBlockState connecteeBlock;
	/** Position of the block that is being asked for the connection */
	public final BlockPos connecteePosition;
	/** Face on the block that is being asked for the connection */
	public final EnumFacing connecteeSide;

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
			final World world,
			final IBlockState connectorBlock,
			final BlockPos connectorPosition,
			final EnumFacing connectorSide,
			final IBlockState connecteeBlock,
			final BlockPos connecteePosition,
			final EnumFacing connecteeSide
	){
		this.world = world;
		this.powerType = powerType;
		this.connectorBlock = connectorBlock;
		this.connectorPosition = connectorPosition;
		this.connectorSide = connectorSide;
		this.connecteeBlock = connecteeBlock;
		this.connecteePosition = connecteePosition;
		this.connecteeSide = connecteeSide;
	}

	public PowerConnectorContext(
			final ConduitType powerType,
			final World world,
			final IBlockState connectorBlock,
			final BlockPos connectorPosition,
			final EnumFacing connectorSide
	){
		this.world = world;
		this.powerType = powerType;
		this.connectorBlock = connectorBlock;
		this.connectorPosition = connectorPosition;
		this.connectorSide = connectorSide;
		this.connecteePosition = connectorPosition.offset(connectorSide);
		this.connecteeSide = connectorSide.getOpposite();
		this.connecteeBlock = world.getBlockState(connecteePosition);
	}

	/**
	 * Swaps the connector and connectee blocks. This is the same connection view from the opposite direction
	 * @return A reversed instance of this PowerConnectorContext
	 */
	public final PowerConnectorContext reverse(){
		return new PowerConnectorContext(powerType,world,connecteeBlock,connecteePosition,connecteeSide,connectorBlock,connectorPosition,connectorSide);
	}
}
