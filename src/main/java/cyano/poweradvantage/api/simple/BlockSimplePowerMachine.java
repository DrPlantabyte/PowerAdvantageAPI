package cyano.poweradvantage.api.simple;

import cyano.poweradvantage.api.*;
import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This block class provides all of the standard code for creating a machine 
 * block with an inventory and user interface that gives power to connected 
 * machines.<br>
 * Example usage:<br><pre>
int guiID = cyano.poweradvantage.registry.MachineGUIRegistry.addGUI(new MySimpleMachineGUI());
Block myMachineBlock = new MyBlockSimplePowerSource(guiID,PowerAdvantage.getInstance());
myMachineBlock.setUnlocalizedName(MODID+"."+"my_machine");
GameRegistry.registerBlock(myMachineBlock,"my_machine");
 * </pre>
 * @author DrCyano
 *
 */
public abstract class BlockSimplePowerMachine extends GUIBlock implements ITypedConduit {
	private final ConduitType[] type;

	/**
	 * Blockstate property indicating which direction the block is facing
	 */
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    /**
     * Standard constructor for a machine block. Remember to set the GUI ID so that a GUI can pop-up 
     * when the player interacts with this block.
     * @param blockMaterial This is the material for the block. Typically is set 
	 * to net.minecraft.block.material.Material.piston, though any material can 
	 * be used.
     * @param hardness This affects how long it takes to break the block. 0.5 is 
	 * a good value if you want it to be easy to break.
     * @param energyType This is the energy type for this block. This is used by 
     * power conduits to determine whether to connect to this block.
     */
	public BlockSimplePowerMachine(Material blockMaterial, float hardness, ConduitType... energyType){
		super(blockMaterial);
		this.type = energyType;
    	super.setHardness(hardness);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	/**
	 * Creates a TileEntity for this block when the block is placed into the 
	 * world.
	 * @return A new TileEntity instance, probably one that extends 
	 * <b>TileEntitySimplePowerMachine</b>.
	 */
	@Override
    public abstract PoweredEntity createNewTileEntity(final World world, final int metaDataValue);
	
	/**
	 * Override of default block behavior
	 */
	@Override
	public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
		this.setDefaultFacing(world, coord, state);
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
	 * Used to decides whether or not a conduit should connect to this block 
	 * based on its energy type.
	 * @return The type of energy for this block 
	 */
	@Override
	public ConduitType[] getTypes() {
		return type;
	}

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param connection A context object that provides the power type and block direction information
	 * @return True if power should be allowed to flow through this connection, false otherwise
	 */
	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection){
		ConduitType[] myTypes = getTypes();
		for(int i = 0; i < myTypes.length; i++){
			if(ConduitType.areSameType(myTypes[i],connection.powerType)) return true;
		}
		return false;
	}
	
	/**
	 * Override of default block behavior
	 */
    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(this);
    }
    
   
    

	/**
	 * Creates the blockstate of this block when it is placed in the world
	 */
    @Override
    public IBlockState onBlockPlaced(final World p_onBlockPlaced_1_, final BlockPos p_onBlockPlaced_2_, final EnumFacing p_onBlockPlaced_3_, final float p_onBlockPlaced_4_, final float p_onBlockPlaced_5_, final float p_onBlockPlaced_6_, final int p_onBlockPlaced_7_, final EntityLivingBase p_onBlockPlaced_8_) {
        return this.getDefaultState().withProperty( FACING, p_onBlockPlaced_8_.getHorizontalFacing().getOpposite());
    }
    
    /**
	 * Creates the blockstate of this block when it is placed in the world
	 */
    @Override
    public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, 
    		final EntityLivingBase placer, final ItemStack srcItemStack) {
        world.setBlockState(coord, bs.withProperty((IProperty) FACING, (Comparable)placer.getHorizontalFacing().getOpposite()), 2);
        if (srcItemStack.hasDisplayName()) {
        	final TileEntity tileEntity = world.getTileEntity(coord);
        	if (tileEntity instanceof PoweredEntity){
        		((PoweredEntity)tileEntity).setCustomInventoryName(srcItemStack.getDisplayName());
        	}
        }
    }
    
    /**
     * Destroys the TileEntity associated with this block when this block 
     * breaks.
     */
    @Override
    public void breakBlock(final World world, final BlockPos coord, final IBlockState bs) {
        final TileEntity tileEntity = world.getTileEntity(coord);
        if (tileEntity instanceof TileEntitySimplePowerMachine) {
            InventoryHelper.dropInventoryItems(world, coord, (IInventory)tileEntity);
            world.updateComparatorOutputLevel(coord, this);
        }
        super.breakBlock(world, coord, bs);
    }
    
    
    /**
     * Sets the default blockstate
     * @param w World instance
     * @param coord Block coordinate
     * @param state Block state
     */
    protected void setDefaultFacing(final World w, final BlockPos coord, final IBlockState state) {
        if (w.isRemote) {
            return;
        }
        final IBlockState block = w.getBlockState(coord.north());
        final IBlockState block2 = w.getBlockState(coord.south());
        final IBlockState block3 = w.getBlockState(coord.west());
        final IBlockState block4 = w.getBlockState(coord.east());
        EnumFacing enumFacing = (EnumFacing)state.getValue(FACING);
        if (enumFacing == EnumFacing.NORTH && block.isFullBlock() && !block2.isFullBlock()) {
            enumFacing = EnumFacing.SOUTH;
        }
        else if (enumFacing == EnumFacing.SOUTH && block2.isFullBlock() && !block.isFullBlock()) {
            enumFacing = EnumFacing.NORTH;
        }
        else if (enumFacing == EnumFacing.WEST && block3.isFullBlock() && !block4.isFullBlock()) {
            enumFacing = EnumFacing.EAST;
        }
        else if (enumFacing == EnumFacing.EAST && block4.isFullBlock() && !block3.isFullBlock()) {
            enumFacing = EnumFacing.WEST;
        }
        w.setBlockState(coord, state.withProperty((IProperty) FACING, (Comparable)enumFacing), 2);
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
	 * @param state The blockstate of this block
    * @return true if this block can be measured by a redstone comparator, 
    * false otherwise
    */
    @Override
    public abstract boolean hasComparatorInputOverride(IBlockState state);
    
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
	 * @param state The blockstate of this block
     * @param world World object
     * @param coord Coordinates of this block
     * @return a number from 0 to 15
     */
    @Override
    public  abstract  int getComparatorInputOverride(IBlockState state, final World world, final BlockPos coord);
    

    /**
     * Converts metadata into blockstate
     */
    @Override
    public IBlockState getStateFromMeta(final int metaValue) {
        EnumFacing enumFacing = EnumFacing.getFront(metaValue);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty( FACING, enumFacing);
    }
    
    /**
     * Converts blockstate into metadata
     */
    @Override
    public int getMetaFromState(final IBlockState bs) {
        return ((EnumFacing)bs.getValue( FACING)).getIndex();
    }
    
    /**
     * Creates a blockstate
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {  FACING });
    }

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	/**
	 * Determines whether this block/entity should receive energy of this type. If this is not a sink, then it
	 * will never be given power by a power source.
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public abstract boolean isPowerSink(ConduitType powerType) ;

	/**
	 * Determines whether this block/entity can provide energy of this type.
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public abstract boolean isPowerSource(ConduitType powerType) ;

    
}
