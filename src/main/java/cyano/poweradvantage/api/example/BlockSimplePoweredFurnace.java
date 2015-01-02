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

public class BlockSimplePoweredFurnace  extends BlockContainer implements ITypedConductor{

	public static final PropertyDirection FACING=PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
    private final boolean isBurning;
    private static boolean keepInventory;

    public static BlockSimplePoweredFurnace instance_unlit = null;
    public static BlockSimplePoweredFurnace instance_lit = null;
    
    final int guiID;
	
	public BlockSimplePoweredFurnace(boolean burning, int guiID) {
		super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockSimplePoweredFurnace.FACING, (Comparable)EnumFacing.NORTH));
        this.isBurning = burning;
        if(instance_lit == null && burning){
        	instance_lit = this;
        } else if(instance_unlit == null) {
        	instance_unlit = this;
    	    this.setCreativeTab(CreativeTabs.tabDecorations);
        }
        this.guiID = guiID;
	}
	
	private static final ConductorType type = new ConductorType("energy");
	@Override
	public ConductorType getEnergyType() {
		return type;
	}

	@Override
    public Item getItemDropped(final IBlockState bd, final Random prng, final int i) {
        return Item.getItemFromBlock(instance_unlit);
    }
    
    @Override
    public void onBlockAdded(final World world, final BlockPos pos, final IBlockState bs) {
        this.setDefaultFacing(world, pos, bs);
    }
    
    private void setDefaultFacing(final World w, final BlockPos coord, final IBlockState bs) {
        if (w.isRemote) {
            return;
        }
        final Block block = w.getBlockState(coord.north()).getBlock();
        final Block block2 = w.getBlockState(coord.south()).getBlock();
        final Block block3 = w.getBlockState(coord.west()).getBlock();
        final Block block4 = w.getBlockState(coord.east()).getBlock();
        EnumFacing enumFacing = (EnumFacing)bs.getValue(FACING);
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
        w.setBlockState(coord, bs.withProperty((IProperty)FACING, (Comparable)enumFacing), 2);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(final World world, final BlockPos coord, final IBlockState bs, 
    		final Random prng) {
        if (!this.isBurning) {
            return;
        }
        final EnumFacing enumFacing = (EnumFacing)bs.getValue(FACING);
        final double x = coord.getX() + 0.5;
        final double y = coord.getY() + prng.nextDouble() * 6.0 / 16.0;
        final double z = coord.getZ() + 0.5;
        final double offset = 0.52;
        final double rand = prng.nextDouble() * 0.6 - 0.3;
        switch (enumFacing) {
            case WEST: {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - offset, y, z + rand, 0.0, 0.0, 0.0, new int[0]);
                world.spawnParticle(EnumParticleTypes.FLAME, x - offset, y, z + rand, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            case EAST: {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + offset, y, z + rand, 0.0, 0.0, 0.0, new int[0]);
                world.spawnParticle(EnumParticleTypes.FLAME, x + offset, y, z + rand, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            case NORTH: {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rand, y, z - offset, 0.0, 0.0, 0.0, new int[0]);
                world.spawnParticle(EnumParticleTypes.FLAME, x + rand, y, z - offset, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            case SOUTH: {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rand, y, z + offset, 0.0, 0.0, 0.0, new int[0]);
                world.spawnParticle(EnumParticleTypes.FLAME, x + rand, y, z + offset, 0.0, 0.0, 0.0, new int[0]);
                break;
            }
            default: {
            	// shouldn't happen
                // do nothing
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
    
    public static void setState(final boolean burning, final World w, final BlockPos coord) {
        final IBlockState blockState = w.getBlockState(coord);
        final TileEntity tileEntity = w.getTileEntity(coord);
        keepInventory = true;
        if (burning) {
            w.setBlockState(coord, instance_lit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue(FACING)), 3);
            w.setBlockState(coord, instance_lit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue(FACING)), 3);
        }
        else {
            w.setBlockState(coord, instance_unlit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue(FACING)), 3);
            w.setBlockState(coord, instance_unlit.getDefaultState().withProperty((IProperty)FACING, blockState.getValue(FACING)), 3);
        }
        keepInventory = false;
        if (tileEntity != null) {
            tileEntity.validate();
            w.setTileEntity(coord, tileEntity);
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(final World w, final int meta) {
        return new SimplePowerSinkEntity();
    }
    
    @Override
    public IBlockState onBlockPlaced(final World world, final BlockPos coord, final EnumFacing blockFace, 
    		final float f1, final float f2, final float f3, final int i, final EntityLivingBase player) {
        return this.getDefaultState().withProperty(FACING, player.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, 
    		final EntityLivingBase player, final ItemStack src) {
        world.setBlockState(coord, bs.withProperty((IProperty)FACING, (Comparable)player.getHorizontalFacing().getOpposite()), 2);
        if (src.hasDisplayName()) {
            final TileEntity tileEntity = world.getTileEntity(coord);
            if (tileEntity instanceof SimplePowerSinkEntity) {
                ((SimplePowerSinkEntity)tileEntity).setCustomInventoryName(src.getDisplayName());
            }
        }
    }
    
    @Override
    public void breakBlock(final World world, final BlockPos coord, final IBlockState bs) {
        if (!keepInventory) {
            final TileEntity tileEntity = world.getTileEntity(coord);
            if (tileEntity instanceof SimplePowerSinkEntity) {
                InventoryHelper.dropInventoryItems(world, coord, (IInventory)tileEntity);
                world.updateComparatorOutputLevel(coord, this);
            }
        }
        super.breakBlock(world, coord, bs);
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(final World world, final BlockPos coord) {
        return Container.calcRedstone(world.getTileEntity(coord));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(final World w, final BlockPos coord) {
        return Item.getItemFromBlock(this.instance_unlit);
    }
    
    @Override
    public int getRenderType() {
        return 3;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IBlockState getStateForEntityRender(final IBlockState blockState) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        EnumFacing enumFacing = EnumFacing.getFront(meta);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, enumFacing);
    }
    
    @Override
    public int getMetaFromState(final IBlockState blockState) {
        return ((EnumFacing)blockState.getValue(FACING)).getIndex();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { FACING });
    }

}
