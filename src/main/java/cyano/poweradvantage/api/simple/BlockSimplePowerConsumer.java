package cyano.poweradvantage.api.simple;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.ITypedConductor;
import cyano.poweradvantage.api.PowerSourceEntity;

public abstract class BlockSimplePowerConsumer  extends BlockContainer implements ITypedConductor {
	private final int guiID;
	private final ConductorType type;
	private final Object guiHandlerOwner;

    public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
    
	public BlockSimplePowerConsumer(Material blockMaterial, float hardness, ConductorType energyType, int guiHandlerID, Object ownerOfGUIHandler){
		super(blockMaterial);
		this.guiID = guiHandlerID;
		this.type = energyType;
		this.guiHandlerOwner = ownerOfGUIHandler;
    	super.setHardness(hardness);
	}
	
	
	@Override
    public abstract TileEntity createNewTileEntity(final World world, final int metaDataValue);
	
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(this);
    }
    
    @Override
    public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
        this.setDefaultFacing(world, coord, state);
    }
    

    @Override
    public IBlockState onBlockPlaced(final World p_onBlockPlaced_1_, final BlockPos p_onBlockPlaced_2_, final EnumFacing p_onBlockPlaced_3_, final float p_onBlockPlaced_4_, final float p_onBlockPlaced_5_, final float p_onBlockPlaced_6_, final int p_onBlockPlaced_7_, final EntityLivingBase p_onBlockPlaced_8_) {
        return this.getDefaultState().withProperty( FACING, p_onBlockPlaced_8_.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, 
    		final EntityLivingBase placer, final ItemStack srcItemStack) {
        world.setBlockState(coord, bs.withProperty((IProperty) FACING, (Comparable)placer.getHorizontalFacing().getOpposite()), 2);
        if (srcItemStack.hasDisplayName()) {
        	final TileEntity tileEntity = world.getTileEntity(coord);
        	if (tileEntity instanceof PowerSourceEntity){
        		((PowerSourceEntity)tileEntity).setCustomInventoryName(srcItemStack.getDisplayName());
        	}
        }
    }
    
    
    
    

    @Override
    public boolean onBlockActivated(final World w, final BlockPos coord, final IBlockState bs, 
    		final EntityPlayer player, final EnumFacing facing, final float f1, final float f2, 
    		final float f3) {
        if (w.isRemote) {
            return true;
        }
        final TileEntity tileEntity = w.getTileEntity(coord);
        if (tileEntity == null || player.isSneaking()) {
        	return false;
        }
        // open GUI
        if(guiHandlerOwner == null) return false;
        player.openGui(guiHandlerOwner, guiID, w, coord.getX(), coord.getY(), coord.getZ());
        return true;
    }
    
    @Override
    public void breakBlock(final World world, final BlockPos coord, final IBlockState bs) {
        final TileEntity tileEntity = world.getTileEntity(coord);
        if (tileEntity instanceof TileEntitySimplePowerConsumer) {
            InventoryHelper.dropInventoryItems(world, coord, (IInventory)tileEntity);
            world.updateComparatorOutputLevel(coord, this);
        }
        super.breakBlock(world, coord, bs);
    }
    
    
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
    
    @Override
    public abstract boolean hasComparatorInputOverride();
    
    @Override
    public  abstract  int getComparatorInputOverride(final World world, final BlockPos coord);
    
    
    @Override
    public int getRenderType() {
        return 3;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int metaValue) {
        EnumFacing enumFacing = EnumFacing.getFront(metaValue);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty( FACING, enumFacing);
    }
    
    @Override
    public int getMetaFromState(final IBlockState bs) {
        return ((EnumFacing)bs.getValue( FACING)).getIndex();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {  FACING });
    }
    
    ///// CLIENT-SIDE CODE /////

    @SideOnly(Side.CLIENT)
    @Override
    public IBlockState getStateForEntityRender(final IBlockState bs) {
        return this.getDefaultState().withProperty( FACING, EnumFacing.SOUTH);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World world, final BlockPos coord) {
        return Item.getItemFromBlock(this);
    }
    
}
