package cyano.poweradvantage.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.base.Predicate;

import cyano.poweradvantage.api.GUIBlock;

public class BlockConveyorFilter extends GUIBlock{

	private final Class<? extends TileEntityConveyor> tileEntityClass;
	
	public BlockConveyorFilter(Material m, Class<? extends TileEntityConveyor> tileEntityClass) {
		super(m);
		this.tileEntityClass = tileEntityClass; 
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * Blockstate property
	 */
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		try {
			return tileEntityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			FMLLog.severe("Failed to create instance of class "+tileEntityClass.getName() 
					+ "! Did you forget to give it a no-arg constructor?");
			return null;
		}
	}
}
