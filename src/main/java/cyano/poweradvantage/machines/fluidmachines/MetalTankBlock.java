package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetalTankBlock extends BlockSimpleFluidMachine {

	public MetalTankBlock() {
		super(Material.piston, 3f);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new MetalTankTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs) {
		return true;
	}


	@Override
	public int getComparatorInputOverride(IBlockState bs, World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof MetalTankTileEntity){
			return ((MetalTankTileEntity)te).getRedstoneOutput();
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
