package cyano.poweradvantage.machines;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidSource;

public class StorageTankBlock extends BlockSimpleFluidSource{

	public StorageTankBlock() {
		super(Material.piston, 3f);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new StorageTankTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}


	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof StorageTankTileEntity){
			return ((StorageTankTileEntity)te).getRedstoneOutput();
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
