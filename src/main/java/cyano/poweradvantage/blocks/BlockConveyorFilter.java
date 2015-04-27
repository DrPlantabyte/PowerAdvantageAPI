package cyano.poweradvantage.blocks;

import java.util.List;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.base.Predicate;

import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.init.ItemGroups;

public class BlockConveyorFilter extends GUIBlock{

	private final Class<? extends TileEntityConveyor> tileEntityClass;
	
	public BlockConveyorFilter(Material m, float hardness, Class<? extends TileEntityConveyor> tileEntityClass) {
		super(m);
		this.setHardness(hardness);
		this.tileEntityClass = tileEntityClass; 
        this.setStepSound(BlockPistonBase.soundTypeMetal);
        this.setCreativeTab(ItemGroups.tab_powerAdvantage);
    	this.setDefaultState(this.blockState.getBaseState()
	    		.withProperty(FACING,EnumFacing.NORTH));
	}
	
	

	/**
	 * Blockstate property
	 */
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);

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
	public boolean isOpaqueCube() {
		return false;
	}


    @Override
    public boolean isFullCube() {
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
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

    @Override
    public int getMetaFromState(final IBlockState bs) {
        int i = ((EnumFacing)bs.getValue(FACING)).getIndex();
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { FACING });
    }
    
    
    @Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
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
