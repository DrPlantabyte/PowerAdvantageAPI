package cyano.poweradvantage.api;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


// TODO: update documentation
public abstract class FluidPipeBlock extends Block implements IFluidHandlerBlock{
	/** power type identifier */
	private final ConductorType type;
	/** radius of the pipe model, in meters (0.0625 per pixel) */
	private final float pipeRadius; // in fraction of a block (aka meters)
	/** Blockstate property */
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	/** Blockstate property */
    public static final PropertyBool NORTH = PropertyBool.create("north");
	/** Blockstate property */
    public static final PropertyBool EAST = PropertyBool.create("east");
	/** Blockstate property */
    public static final PropertyBool WEST = PropertyBool.create("west");
	/** Blockstate property */
    public static final PropertyBool UP = PropertyBool.create("up");
	/** Blockstate property */
    public static final PropertyBool DOWN = PropertyBool.create("down");
	/**
	 * Standard constructor. Must be called in the first line of code in the 
	 * constructor of extending classes.
	 * @param blockMaterial This is the material for the block. Typically is set 
	 * to net.minecraft.block.material.Material.piston, though any material can 
	 * be used.
	 * @param hardness This affects how long it takes to break the block. 0.5 is 
	 * a good value if you want it to be easy to break.
	 * @param pipeRadius This is used to determine the size of the hit-boxes 
	 * around the conduit pipes. For each pixel of radius, add 0.0625 (1/16). 
	 * For example, if your block model files use 
	 * <i>poweradvantage:block/pipe3_*</i> as the parent model, then the radius 
	 * of the model will be 3 pixels (6 pixels wide) and you will want to use 
	 * 0.1875 (3/16) as the pipeRadius. A pixel in Minecraft is defines as 1/16 
	 * of a block, regardless of the texture resolution. 
	 * @param energyType This is the energy type for this block. This block will 
	 * automatically connect to neighboring blocks of the same energy type.
	 */
	public FluidPipeBlock(Material blockMaterial, float hardness, float pipeRadius, ConductorType energyType){
		super(blockMaterial);
		this.type = energyType;
    	super.setHardness(hardness);
    	this.pipeRadius = pipeRadius;
    	this.setDefaultState(this.blockState.getBaseState()
	    		.withProperty(WEST, false)
	    		.withProperty(DOWN, false)
	    		.withProperty(SOUTH, false)
	    		.withProperty(EAST, false)
	    		.withProperty(UP, false)
	    		.withProperty(NORTH, false));
	}

	public boolean canConnect(EnumFacing enumFacing){
		return true;
	}
	/**
	 * Creates a TileEntity for this block when the block is placed into the 
	 * world.
	 * @return A new TileEntity instance, probably one that extends 
	 * <b>TileEntitySimplePowerConduit</b>.
	 */
    public abstract FluidPipeEntity createNewTileEntity(final World world, final int metaDataValue);

	/**
	 * Creates a blockstate instance.
	 */
	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { WEST, DOWN, SOUTH, EAST, UP, NORTH });
    }
	
	/**
	 * Sets the data values of a BlockState instance to represent this block
	 */
    @Override
    public IBlockState getActualState(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
        return bs
        		.withProperty(WEST, this.canConnectTo(world,EnumFacing.EAST, coord.west()))
        		.withProperty(DOWN, this.canConnectTo(world,EnumFacing.UP, coord.down()))
        		.withProperty(SOUTH, this.canConnectTo(world,EnumFacing.NORTH, coord.south()))
        		.withProperty(EAST, this.canConnectTo(world,EnumFacing.WEST, coord.east()))
        		.withProperty(UP, this.canConnectTo(world,EnumFacing.DOWN, coord.up()))
        		.withProperty(NORTH, this.canConnectTo(world,EnumFacing.SOUTH, coord.north()));
    }
    
	
    /**
     * Calculates the collision boxes for this block.
     */
	@Override
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final BlockPos coord) {
        final boolean connectNorth = this.canConnectTo(world, EnumFacing.SOUTH,coord.north());
        final boolean connectSouth = this.canConnectTo(world, EnumFacing.NORTH, coord.south());
        final boolean connectWest = this.canConnectTo(world, EnumFacing.EAST, coord.west());
        final boolean connectEast = this.canConnectTo(world, EnumFacing.WEST, coord.east());
        final boolean connectUp = this.canConnectTo(world, EnumFacing.DOWN, coord.up());
        final boolean connectDown = this.canConnectTo(world, EnumFacing.UP, coord.down());
        
        float radius = pipeRadius;
        float rminus = 0.5f - radius;
        float rplus = 0.5f + radius;
        
        float x1 = rminus;
        float x2 = rplus;
        float y1 = rminus;
        float y2 = rplus;
        float z1 = rminus;
        float z2 = rplus;
        if (connectNorth) {
            z1 = 0.0f;
        }
        if (connectSouth) {
            z2 = 1.0f;
        }
        if (connectWest) {
            x1 = 0.0f;
        }
        if (connectEast) {
            x2 = 1.0f;
        }
        if(connectDown){
        	y1 = 0.0f;
        }
        if(connectUp){
        	y2 = 1.0f;
        }
        this.setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Calculates the collision boxes for this block.
     */
	@Override
    public void addCollisionBoxesToList(final World world, final BlockPos coord, 
    		final IBlockState bs, final AxisAlignedBB box, final List collisionBoxList, 
    		final Entity entity) {
        final boolean connectNorth = this.canConnectTo(world, EnumFacing.SOUTH,coord.north());
        final boolean connectSouth = this.canConnectTo(world, EnumFacing.NORTH, coord.south());
        final boolean connectWest = this.canConnectTo(world, EnumFacing.EAST, coord.west());
        final boolean connectEast = this.canConnectTo(world, EnumFacing.WEST, coord.east());
        final boolean connectUp = this.canConnectTo(world, EnumFacing.DOWN, coord.up());
        final boolean connectDown = this.canConnectTo(world, EnumFacing.UP, coord.down());
        
        float radius = pipeRadius;
        float rminus = 0.5f - radius;
        float rplus = 0.5f + radius;
        
        this.setBlockBounds(rminus, rminus, rminus, rplus, rplus, rplus);
        super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);

        if(connectUp){
            this.setBlockBounds(rminus, rminus, rminus, rplus, 1f, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectDown){
            this.setBlockBounds(rminus, 0f, rminus, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectEast){
            this.setBlockBounds(rminus, rminus, rminus, 1f, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectWest){
            this.setBlockBounds(0f, rminus, rminus, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectSouth){
            this.setBlockBounds(rminus, rminus, rminus, rplus, rplus, 1f);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
        if(connectNorth){
            this.setBlockBounds(rminus, rminus, 0f, rplus, rplus, rplus);
            super.addCollisionBoxesToList(world, coord, bs, box, collisionBoxList, entity);
        }
    }
	
	/**
	 * This method determines whether to connect to a neighboring block. 
	 * Override this method to change block connection behavior. 
	 * @param w World instance
	 * @param coord Coordinate of neighboring block
	 * @return Default implementation: true if the neighboring block implements 
	 * ITypedConductor and has the same energy type as this block. Overriding 
	 * the canConnectTo(ConductorType) method will change the results of this 
	 * method.
	 */
	protected boolean canConnectTo(IBlockAccess w, EnumFacing otherSide, BlockPos coord){
		Block other = w.getBlockState(coord).getBlock();
		if(other instanceof IFluidHandlerBlock){
			return ((IFluidHandlerBlock)other).canConnect(otherSide);
		} else {
			return false;
		}
	}
	
	/**
	 * Override of default block behavior
	 */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    /**
	 * Override of default block behavior
	 */
    @Override
    public boolean isFullCube() {
        return false;
    }
    
    /**
	 * Override of default block behavior
	 */
    @Override
    public boolean isPassable(final IBlockAccess world, final BlockPos coord) {
        return false;
    }
    
    
    /**
	 * Metadata not used.
	 */
    @Override
    public int getMetaFromState(final IBlockState bs) {
        return 0;
    }

    /**
	 * Override of default block behavior
	 */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(final IBlockAccess world, final BlockPos coord, final EnumFacing face) {
        return true;
    }
}
