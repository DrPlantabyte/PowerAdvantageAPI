package cyano.poweradvantage.items;

import java.util.Map;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class RotationTool extends net.minecraft.item.Item{

	public RotationTool(){
		super();
		super.setMaxStackSize(1);
	}
	
	@Override
	public boolean onItemUse(final ItemStack item, final EntityPlayer player, final World w, 
			final BlockPos coord, final EnumFacing facing, 
			final float partialX, final float partialY, final float partialZ) {
		IBlockState block = w.getBlockState(coord);
		for(Object o : block.getProperties().entrySet()){
			Map.Entry e = (Map.Entry)o;
			if(e.getKey() instanceof PropertyDirection){
				w.setBlockState(coord, block.cycleProperty((PropertyDirection)e.getKey()));
				return true;
			}
		}
		return false;
	}
}
