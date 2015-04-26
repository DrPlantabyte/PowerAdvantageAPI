package cyano.poweradvantage.api.simple;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Predicate;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.api.ITypedConduit;
import cyano.poweradvantage.api.PoweredEntity;
/**
 * This block class provides all of the standard code for creating a machine 
 * block with an inventory and user interface that receives power from connected 
 * power sources.<br>
 * Example usage:<br><pre>
int guiID = cyano.poweradvantage.registry.MachineGUIRegistry.addGUI(new MySimpleMachineGUI());
Block myMachineBlock = new MyBlockSimplePowerConsumer(guiID,PowerAdvantage.getInstance());
myMachineBlock.setUnlocalizedName(MODID+"."+"my_machine");
GameRegistry.registerBlock(myMachineBlock,"my_machine");
 * </pre>
 * @author DrCyano
 *
 */
public abstract class BlockSimplePowerConsumer  extends GUIBlock implements ITypedConduit {
	private final ConduitType type;
	/**
	 * Blockstate property
	 */
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
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
	public BlockSimplePowerConsumer(Material blockMaterial, float hardness, ConduitType energyType){
		super(blockMaterial);
		this.type = energyType;
    	super.setHardness(hardness);
	}
	
	/**
	 * Creates a TileEntity for this block when the block is placed into the 
	 * world.
	 * @return A new TileEntity instance, probably one that extends 
	 * <b>TileEntitySimplePowerConsumer</b>.
	 */
	@Override
    public abstract PoweredEntity createNewTileEntity(final World world, final int metaDataValue);
	
	/**
	 * Used to decides whether or not a conduit should connect to this block 
	 * based on its energy type.
	 * @return The type of energy for this block 
	 */
	@Override
	public ConduitType getType() {
		return type;
	}

	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 * @param type The type of energy in the conduit
	 * @param blockFace The side through-which the energy is flowing
	 * @return true if this conduit can flow the given energy type through the given face, false 
	 * otherwise
	 */
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace){
		return ConduitType.areSameType(getType(), type);
	}
	/**
	 * Determines whether this conduit is compatible with a type of energy through any side
	 * @param type The type of energy in the conduit
	 * @return true if this conduit can flow the given energy type through one or more of its block 
	 * faces, false otherwise
	 */
	public boolean canAcceptType(ConduitType type){
		return ConduitType.areSameType(getType(), type);
	}
	
	/**
	 * Determines whether this block/entity should receive energy 
	 * @return true if this block/entity should receive energy
	 */
	public boolean isPowerSink(){
		return true;
	}
	/**
	 * Determines whether this block/entity can provide energy 
	 * @return true if this block/entity can provide energy
	 */
	public boolean isPowerSource(){
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
    * Override of default block behavior
    */
   @Override
   public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
	   this.setDefaultFacing(world, coord, state);
   }
    
	/**
	 * Creates the blockstate of this block when it is placed in the world
	 */
    @Override
    public IBlockState onBlockPlaced(final World world, final BlockPos coord, final EnumFacing facing, 
    		final float f1, final float f2, final float f3, 
    		final int meta, final EntityLivingBase player) {
        return this.getDefaultState().withProperty( FACING, player.getHorizontalFacing().getOpposite());
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
        if (tileEntity instanceof TileEntitySimplePowerConsumer) {
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
        final Block block = w.getBlockState(coord.north()).getBlock();
        final Block block2 = w.getBlockState(coord.south()).getBlock();
        final Block block3 = w.getBlockState(coord.west()).getBlock();
        final Block block4 = w.getBlockState(coord.east()).getBlock();
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
     * @return true if this block can be measured by a redstone comparator, 
     * false otherwise
     */
    @Override
    public abstract boolean hasComparatorInputOverride();
    
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
     * @param world World object
     * @param coord Coordinates of this block
     * @return a number from 0 to 15
     */
    @Override
    public  abstract  int getComparatorInputOverride(final World world, final BlockPos coord);
    
    /**
     * Override of default block behavior
     */
    @Override
    public int getRenderType() {
        return 3;
    }
    
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
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {  FACING });
    }
    
    ///// CLIENT-SIDE CODE /////

    /**
     * (Client-only) Gets the blockstate used for GUI and such.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public IBlockState getStateForEntityRender(final IBlockState bs) {
        return this.getDefaultState().withProperty( FACING, EnumFacing.SOUTH);
    }
    
    /**
     * (Client-only) Override of default block behavior
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World world, final BlockPos coord) {
        return Item.getItemFromBlock(this);
    }
    
}
