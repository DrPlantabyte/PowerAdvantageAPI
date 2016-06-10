package cyano.poweradvantage.blocks;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PowerConnectorContext;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This block class is a user-friendly power switch for turning power on and off. You do not need to 
 * extend this class, just specify the type of power being switched in the constructor.
 * @author DrCyano
 *
 */
public class BlockPowerSwitch extends Block implements ITypedConduit {
	/** Blockstate peroperty. If true, conduct power, if false, don't */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	private final ConduitType[] powerType;
	/**
	 * Constructor for making a power switch
	 * @param powerType Type of power to switch
	 * @param blockMaterial The block material (usually piston material)
	 * @param blockHardness The hardness of the block (0.75 is a good value)
	 * @param mapColor Color on a map
	 */
	public BlockPowerSwitch(ConduitType powerType, Material blockMaterial, float blockHardness, MapColor mapColor) {
		super(blockMaterial, mapColor);
		this.powerType = new ConduitType[1];
		this.powerType[0] = powerType;
		this.setHardness(blockHardness);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false));
	}
	/**
	 * Constructor for making a power switch
	 * @param powerType Type of power to switch
	 */
	public BlockPowerSwitch(ConduitType powerType){
		this(powerType,Material.PISTON,0.75f,MapColor.IRON);
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ACTIVE });
	}
	/**
	 * Converts metadata into blockstate
	 */
	@Override
	public IBlockState getStateFromMeta(final int metaValue) {
		return this.getDefaultState()
				.withProperty(ACTIVE, metaValue  > 0);
	}
	
	/**
	 * Converts blockstate into metadata
	 */
	@Override
	public int getMetaFromState(final IBlockState bs) {
		if((Boolean)(bs.getValue(ACTIVE))){
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 *
	 * @param connection A context object that provides the power type and block direction information
	 * @return True if power should be allowed to flow through this connection, false otherwise
	 */
	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection) {
		return (Boolean)connection.thisBlock.getValue(ACTIVE) && ConduitType.areSameType(powerType[0],connection.powerType);
	}

	@Override
	public ConduitType[] getTypes() {
		return powerType;
	}
	@Override
	public boolean isPowerSink(ConduitType powerType) {
		return false;
	}
	@Override
	public boolean isPowerSource(ConduitType powerType) {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState bs)
	{
		return true;
	}
@Override
	public boolean isFullCube(IBlockState bs)
	{
		return true;
	}
	
	/**
	 * Override of default block behavior
	 */
	@Override
	public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimension(), coord, getTypes());
	}
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, getTypes());
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimension(), coord, getTypes());
	}
	
	/**
	 * Called when a neighboring block changes.
	 */
	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos neighbor) {
		if (w instanceof World) {
			World world = (World)w;
			IBlockState state = world.getBlockState(pos);
			// redstone power given or taken
			boolean activated = !world.isBlockPowered(pos);
			world.setBlockState(pos, state.withProperty(ACTIVE, activated), 2);
			if (activated) {
				ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimension(), pos, getTypes());
			} else {
				ConduitRegistry.getInstance().conduitBlockRemovedEvent(world, world.provider.getDimension(), pos, getTypes());
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block){
		onNeighborChange(world,pos,pos);
	}

}
