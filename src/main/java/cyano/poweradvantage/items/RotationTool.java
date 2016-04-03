package cyano.poweradvantage.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotationTool extends net.minecraft.item.Item{

	public RotationTool(){
		super();
		super.setMaxStackSize(1);
	}
	
	@Override
	public EnumActionResult onItemUse(final ItemStack item, final EntityPlayer player, final World w,
									  final BlockPos coord, final EnumHand hand, final EnumFacing facing,
									  final float partialX, final float partialY, final float partialZ) {
		IBlockState block = w.getBlockState(coord);
		// new way
		boolean changed = w.setBlockState(coord, block.withRotation(Rotation.COUNTERCLOCKWISE_90));
		if(changed) return EnumActionResult.SUCCESS;
		/* // Old way
		for(Object o : block.getProperties().entrySet()){
			Map.Entry e = (Map.Entry)o;
			if(e.getKey() instanceof PropertyDirection){
				final TileEntity save = w.getTileEntity(coord);
				w.setBlockState(coord, block.cycleProperty((PropertyDirection)e.getKey()),3);
				if(save != null){
					w.removeTileEntity(coord);
					save.validate();
					w.setTileEntity(coord, save);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		*/
		return EnumActionResult.PASS;
	}
}
