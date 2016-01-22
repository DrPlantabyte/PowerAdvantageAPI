package cyano.poweradvantage.blocks;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

/**
 * This block class is a user-friendly power switch for turning power on and off. You do not need to 
 * extend this class, just specify the type of power being switched in the constructor.
 * @author DrCyano
 *
 */
public class BlockPowerSwitch extends Block implements ITypedConduit{
	/** Blockstate peroperty. If true, conduct power, if false, don't */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	private final ConduitType powerType;
	/**
	 * Constructor for making a power switch
	 * @param powerType Type of power to switch
	 * @param blockMaterial The block material (usually piston material)
	 * @param blockHardness The hardness of the block (0.75 is a good value)
	 * @param mapColor Color on a map
	 */
	public BlockPowerSwitch(ConduitType powerType, Material blockMaterial, float blockHardness, MapColor mapColor) {
		super(blockMaterial, mapColor);
		this.powerType = powerType;
		this.setHardness(blockHardness);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false));
	}
	/**
	 * Constructor for making a power switch
	 * @param powerType Type of power to switch
	 */
	public BlockPowerSwitch(ConduitType powerType){
		this(powerType,Material.piston,0.75f,MapColor.ironColor);
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ACTIVE });
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
	
	@Override
	public boolean canAcceptType(IBlockState blockstate, ConduitType type, EnumFacing blockFace) {
		return (Boolean)blockstate.getValue(ACTIVE) && ConduitType.areSameType(getType(), type);
	}
	@Override
	public ConduitType getType() {
		return powerType;
	}
	@Override
	public boolean isPowerSink() {
		return false;
	}
	@Override
	public boolean isPowerSource() {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube()
	{
		return true;
	}

	public boolean isFullCube()
	{
		return true;
	}
	
	/**
	 * Override of default block behavior
	 */
	@Override
	public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
		ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimensionId(), coord, getType());
	}
	/**
	 * This method is called when the block is removed from the world by an entity.
	 */
	@Override
	public void onBlockDestroyedByPlayer(World w, BlockPos coord, IBlockState state){
		super.onBlockDestroyedByPlayer(w, coord, state);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}
	/**
	 * This method is called when the block is destroyed by an explosion.
	 */
	@Override
	public void onBlockDestroyedByExplosion(World w, BlockPos coord, Explosion boom){
		super.onBlockDestroyedByExplosion(w, coord, boom);
		ConduitRegistry.getInstance().conduitBlockRemovedEvent(w, w.provider.getDimensionId(), coord, getType());
	}
	
	/**
	 * Called when a neighboring block changes.
	 */
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		// redstone power given or taken
		boolean redstone = world.isBlockPowered(pos);
		world.setBlockState(pos, state.withProperty(ACTIVE, redstone), 2);
		if(redstone){
			ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimensionId(), pos, getType());
		} else {
			ConduitRegistry.getInstance().conduitBlockRemovedEvent(world, world.provider.getDimensionId(), pos, getType());
		}
	}
	/**
	 * Called when right-clicked by a player
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, 
			EnumFacing face, float hitX, float hitY, float hitZ)
	{ 
		// player right-clicked
		boolean newState = !(state.getValue(ACTIVE));
		world.setBlockState(pos, state.withProperty(ACTIVE, newState), 2);
		if(newState){
			ConduitRegistry.getInstance().conduitBlockPlacedEvent(world, world.provider.getDimensionId(), pos, getType());
		} else {
			ConduitRegistry.getInstance().conduitBlockRemovedEvent(world, world.provider.getDimensionId(), pos, getType());
		}
		return true;
	}
}
