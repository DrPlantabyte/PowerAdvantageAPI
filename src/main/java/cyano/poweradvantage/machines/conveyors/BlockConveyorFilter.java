package cyano.poweradvantage.machines.conveyors;

import com.google.common.base.Predicate;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.init.ItemGroups;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class BlockConveyorFilter extends GUIBlock{

	private final Class<? extends TileEntityConveyor> tileEntityClass;
	
	public BlockConveyorFilter(Material m, float hardness, Class<? extends TileEntityConveyor> tileEntityClass) {
		super(m);
		this.setHardness(hardness);
		this.tileEntityClass = tileEntityClass; 
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(ItemGroups.tab_powerAdvantage);
    	this.setDefaultState(this.blockState.getBaseState()
	    		.withProperty(FACING,EnumFacing.NORTH));
	}
	
	

	/**
	 * Blockstate property
	 */
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		net.minecraft.block.BlockPistonBase k;
		try {
			return tileEntityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			FMLLog.severe("Failed to create instance of class "+tileEntityClass.getName() 
					+ "! Did you forget to give it a no-arg constructor?");
			return null;
		}
	}
	

	
	
	@Override
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}


    @Override
    public boolean isFullCube(IBlockState bs) {
        return false;
    }
    
	@Override
	public IBlockState onBlockPlaced(final World w, final BlockPos coord, final EnumFacing face,
									 final float partialX, final float partialY, final float partialZ,
									 final int i, final EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public IBlockState getStateFromMeta(final int meta) {
		if(meta < 2) return this.getDefaultState();
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

    @Override
    public int getMetaFromState(final IBlockState bs) {
        int i = ((EnumFacing)bs.getValue(FACING)).getIndex();
        return i;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { FACING });
    }
    
    
    @Override
	public boolean hasComparatorInputOverride(final IBlockState bs) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(final IBlockState bs, World world,BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof TileEntityConveyorFilter){
			if(((TileEntityConveyorFilter)te).getStackInSlot(0) == null){
				return 0;
			} else {
				if(((TileEntityConveyorFilter)te).matchesFilter(((TileEntityConveyorFilter)te).getStackInSlot(0))){
					return 15;
				}
				return 7;
			}
		}
		return 0;
	}
	
}
