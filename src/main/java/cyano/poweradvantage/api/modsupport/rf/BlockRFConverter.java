package cyano.poweradvantage.api.modsupport.rf;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRFConverter extends cyano.poweradvantage.api.simple.BlockSimplePowerSource{

	
	public BlockRFConverter(Material blockMaterial, float hardness, ConduitType energyType) {
		super(blockMaterial, hardness, energyType);
	}

	@Override
	public boolean isPowerSink() {
		return true;
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		final ConduitType t = this.getType();
		final String powerName = t.toString();
		switch(powerName){
		case "steam": return new TileEntityRFSteamConverter();
		case "electricity": return new TileEntityRFElectricityConverter();
		case "quantum": return new TileEntityRFQuantumConverter();
		default: return null;
		}
	}
	
	@Override
	public boolean isNormalCube(){
		return false;
	}


	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return false;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		return 0;
	}
}
