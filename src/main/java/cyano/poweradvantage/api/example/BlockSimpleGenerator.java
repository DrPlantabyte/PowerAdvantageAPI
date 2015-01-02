package cyano.poweradvantage.api.example;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Predicate;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.ITypedConductor;

public class BlockSimpleGenerator extends BlockContainer implements ITypedConductor {


    public static final PropertyDirection FACING;
    private final boolean isBurning;
    private static boolean keepInventory;

    public static BlockSimpleGenerator globalBlockInstance_unlit = null;
    public static BlockSimpleGenerator globalBlockInstance_lit = null;
    private final int guiID;
	public BlockSimpleGenerator(final boolean isBurning, int guiID) {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
        this.isBurning = isBurning;
        if(globalBlockInstance_unlit == null && isBurning == false){
        	globalBlockInstance_unlit = this;
    	    this.setCreativeTab(CreativeTabs.tabDecorations);
        }
        if(globalBlockInstance_lit == null && isBurning == true){
        	globalBlockInstance_lit = this;
        }
        this.guiID = guiID;
    }
	
	private static final ConductorType type = new ConductorType("energy");
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

    @Override
    public Item getItemDropped(final IBlockState state, final Random prng, final int i3) {
        return Item.getItemFromBlock(globalBlockInstance_unlit);
    }
    
    @Override
    public void onBlockAdded(final World world, final BlockPos coord, final IBlockState state) {
        this.setDefaultFacing(world, coord, state);
    }
    
    private void setDefaultFacing(final World w, final BlockPos coord, final IBlockState state) {
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(final World w, final BlockPos coord, final IBlockState state, final Random prng) {
        if (!this.isBurning) {
            return;
        }
        final EnumFacing enumFacing = (EnumFacing)state.getValue(FACING);
        final double x = coord.getX() + 0.5;
        final double y = coord.getY() + prng.nextDouble() * 6.0 / 16.0;
        final double z = coord.getZ() + 0.5;
        final double offset = 0.52;
        final double randOffset = prng.nextDouble() * 0.6 - 0.3;
        switch (enumFacing) {
            case EAST: {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - offset, y, z + randOffset, 0.0, 0.0, 0.0, new int[0]);
                w.spawnParticle(EnumParticleTypes.FLAME, x - offset, y, z + randOffset, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            case WEST: {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + offset, y, z + randOffset, 0.0, 0.0, 0.0, new int[0]);
                w.spawnParticle(EnumParticleTypes.FLAME, x + offset, y, z + randOffset, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            case SOUTH: {
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + randOffset, y, z + offset, 0.0, 0.0, 0.0, new int[0]);
                w.spawnParticle(EnumParticleTypes.FLAME, x + randOffset, y, z + offset, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            default: { // NORTH
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + randOffset, y, z - offset, 0.0, 0.0, 0.0, new int[0]);
                w.spawnParticle(EnumParticleTypes.FLAME, x + randOffset, y, z - offset, 0.0, 0.0, 0.0, new int[0]);
                break;
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
        player.openGui(PowerAdvantage.getInstance(), guiID, w, coord.getX(), coord.getY(), coord.getZ());
        return true;
       
    }
    
    public static void setState(final boolean isBurning, final World world, final BlockPos coord) {
        final IBlockState blockState = world.getBlockState(coord);
        final TileEntity tileEntity = world.getTileEntity(coord);
        keepInventory = true;
        if (isBurning) {
            world.setBlockState(coord, globalBlockInstance_lit.getDefaultState().withProperty((IProperty) FACING, blockState.getValue( FACING)), 3);
            world.setBlockState(coord, globalBlockInstance_lit.getDefaultState().withProperty((IProperty) FACING, blockState.getValue( FACING)), 3);
        }
        else {
            world.setBlockState(coord, globalBlockInstance_unlit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue( FACING)), 3);
            world.setBlockState(coord, globalBlockInstance_unlit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue( FACING)), 3);
        }
        keepInventory = false;
        if (tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(coord, tileEntity);
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(final World p_createNewTileEntity_1_, final int p_createNewTileEntity_2_) {
        return new SimplePowerSourceEntity();
    }
    
    @Override
    public IBlockState onBlockPlaced(final World p_onBlockPlaced_1_, final BlockPos p_onBlockPlaced_2_, final EnumFacing p_onBlockPlaced_3_, final float p_onBlockPlaced_4_, final float p_onBlockPlaced_5_, final float p_onBlockPlaced_6_, final int p_onBlockPlaced_7_, final EntityLivingBase p_onBlockPlaced_8_) {
        return this.getDefaultState().withProperty( FACING, p_onBlockPlaced_8_.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public void onBlockPlacedBy(final World p_onBlockPlacedBy_1_, final BlockPos p_onBlockPlacedBy_2_, final IBlockState p_onBlockPlacedBy_3_, final EntityLivingBase p_onBlockPlacedBy_4_, final ItemStack p_onBlockPlacedBy_5_) {
        p_onBlockPlacedBy_1_.setBlockState(p_onBlockPlacedBy_2_, p_onBlockPlacedBy_3_.withProperty((IProperty) FACING, (Comparable)p_onBlockPlacedBy_4_.getHorizontalFacing().getOpposite()), 2);
        if (p_onBlockPlacedBy_5_.hasDisplayName()) {
            final TileEntity tileEntity = p_onBlockPlacedBy_1_.getTileEntity(p_onBlockPlacedBy_2_);
            if (tileEntity instanceof SimplePowerSourceEntity) {
                ((SimplePowerSourceEntity)tileEntity).setCustomInventoryName(p_onBlockPlacedBy_5_.getDisplayName());
            }
        }
    }
    
    @Override
    public void breakBlock(final World p_breakBlock_1_, final BlockPos p_breakBlock_2_, final IBlockState p_breakBlock_3_) {
        if (! keepInventory) {
            final TileEntity tileEntity = p_breakBlock_1_.getTileEntity(p_breakBlock_2_);
            if (tileEntity instanceof SimplePowerSourceEntity) {
                InventoryHelper.dropInventoryItems(p_breakBlock_1_, p_breakBlock_2_, (IInventory)tileEntity);
                p_breakBlock_1_.updateComparatorOutputLevel(p_breakBlock_2_, this);
            }
        }
        super.breakBlock(p_breakBlock_1_, p_breakBlock_2_, p_breakBlock_3_);
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(final World p_getComparatorInputOverride_1_, final BlockPos p_getComparatorInputOverride_2_) {
        return Container.calcRedstone(p_getComparatorInputOverride_1_.getTileEntity(p_getComparatorInputOverride_2_));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World p_getItem_1_, final BlockPos p_getItem_2_) {
        return Item.getItemFromBlock(globalBlockInstance_unlit);
    }
    
    @Override
    public int getRenderType() {
        return 3;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IBlockState getStateForEntityRender(final IBlockState p_getStateForEntityRender_1_) {
        return this.getDefaultState().withProperty( FACING, EnumFacing.SOUTH);
    }
    
    @Override
    public IBlockState getStateFromMeta(final int p_getStateFromMeta_1_) {
        EnumFacing enumFacing = EnumFacing.getFront(p_getStateFromMeta_1_);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty( FACING, enumFacing);
    }
    
    @Override
    public int getMetaFromState(final IBlockState p_getMetaFromState_1_) {
        return ((EnumFacing)p_getMetaFromState_1_.getValue( FACING)).getIndex();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {  FACING });
    }
    
    static {
        FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
    }
}