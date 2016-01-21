package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidSource;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class StillBlock extends BlockSimpleFluidSource{

	
	/**
	 * Blockstate property
	 */
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	
	public StillBlock() {
		super(Material.piston, 3f);
		this.setDefaultState(getDefaultState().withProperty(ACTIVE, false));
	}
	
	/**
	 * Creates a blockstate
	 */
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { ACTIVE,FACING });
	}
	/**
	 * Converts metadata into blockstate
	 */
	@Override
	public IBlockState getStateFromMeta(final int metaValue) {
		EnumFacing enumFacing = metaToFacing(metaValue);
		if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
			enumFacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty( FACING, enumFacing)
				.withProperty(ACTIVE, (metaValue & 0x4) != 0);
	}
	
	/**
	 * Converts blockstate into metadata
	 */
	@Override
	public int getMetaFromState(final IBlockState bs) {
		int extraBit;
		if((Boolean)(bs.getValue(ACTIVE))){
			extraBit = 0x4;
		} else {
			extraBit = 0;
		}
		return facingToMeta((EnumFacing)bs.getValue( FACING)) | extraBit;
	}
	
	private int facingToMeta(EnumFacing f){
		switch(f){
			case NORTH: return 0;
			case WEST: return 1;
			case SOUTH: return 2;
			case EAST: return 3;
			default: return 0;
		}
	}
	private EnumFacing metaToFacing(int i){
		int f = i & 0x03;
		switch(f){
			case 0: return EnumFacing.NORTH;
			case 1: return EnumFacing.WEST;
			case 2: return EnumFacing.SOUTH;
			case 3: return EnumFacing.EAST;
			default: return EnumFacing.NORTH;
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos coord){
		IBlockState bs = world.getBlockState(coord);
		if(bs instanceof StillBlock){
			if((Boolean)bs.getValue(ACTIVE)){
				return 12;
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new StillTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te instanceof StillTileEntity){
			return ((StillTileEntity)te).getRedstoneOutput();
		}
		return 0;
	}
	
	/**
	 * Determines whether this block/entity should receive energy 
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(){
		return true;
	}
	/**
	 * Determines whether this block/entity can provide energy 
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(){
		return true;
	}

}
