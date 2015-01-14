package cyano.poweradvantage.api.simple;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.ITypedConductor;
import cyano.poweradvantage.api.PowerConductorBlock;

public abstract class BlockSimplePowerConductor  extends PowerConductorBlock implements ITileEntityProvider{

	private final ConductorType type;
	private final float pipeRadius; // in fraction of a block (aka meters)
	
	public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
	
	public BlockSimplePowerConductor(Material blockMaterial, float hardness, float pipeRadius, ConductorType energyType){
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

	@Override
	public ConductorType getEnergyType() {
		return type;
	}
	
	@Override
	public boolean canConnectTo(ConductorType energyType) {
		return ConductorType.areSameType(type, energyType);
	}

	@Override
    public abstract TileEntity createNewTileEntity(final World world, final int metaDataValue);

	
	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { WEST, DOWN, SOUTH, EAST, UP, NORTH });
    }
	

    @Override
    public IBlockState getActualState(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
        return bs
        		.withProperty(WEST, this.canConnectTo(world, coord.west()))
        		.withProperty(DOWN, this.canConnectTo(world, coord.down()))
        		.withProperty(SOUTH, this.canConnectTo(world, coord.south()))
        		.withProperty(EAST, this.canConnectTo(world, coord.east()))
        		.withProperty(UP, this.canConnectTo(world, coord.up()))
        		.withProperty(NORTH, this.canConnectTo(world, coord.north()));
    }
    
	

	@Override
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final BlockPos coord) {
        final boolean connectNorth = this.canConnectTo(world, coord.north());
        final boolean connectSouth = this.canConnectTo(world, coord.south());
        final boolean connectWest = this.canConnectTo(world, coord.west());
        final boolean connectEast = this.canConnectTo(world, coord.east());
        final boolean connectUp = this.canConnectTo(world, coord.up());
        final boolean connectDown = this.canConnectTo(world, coord.down());
        
        float radius = 4f / 16f / 2f;
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
	
	@Override
    public void addCollisionBoxesToList(final World world, final BlockPos coord, 
    		final IBlockState bs, final AxisAlignedBB box, final List collisionBoxList, 
    		final Entity entity) {
        final boolean connectNorth = this.canConnectTo(world, coord.north());
        final boolean connectSouth = this.canConnectTo(world, coord.south());
        final boolean connectWest = this.canConnectTo(world, coord.west());
        final boolean connectEast = this.canConnectTo(world, coord.east());
        final boolean connectUp = this.canConnectTo(world, coord.up());
        final boolean connectDown = this.canConnectTo(world, coord.down());
        
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
	
	protected boolean canConnectTo(IBlockAccess w, BlockPos coord){
		Block other = w.getBlockState(coord).getBlock();
		if(other instanceof ITypedConductor){
			return canConnectTo(((ITypedConductor)other).getEnergyType());
		} else {
			return false;
		}
	}
	
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean isFullCube() {
        return false;
    }
    
    @Override
    public boolean isPassable(final IBlockAccess world, final BlockPos coord) {
        return false;
    }
    
    
    @Override
    public int getMetaFromState(final IBlockState bs) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(final IBlockAccess world, final BlockPos coord, final EnumFacing face) {
        return true;
    }
}
