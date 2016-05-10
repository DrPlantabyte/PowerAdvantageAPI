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
		IBlockState bs = w.getBlockState(coord);
		/*  // comment this line to turn the wrench into a hacker tool
		try { // hacker info
			FMLLog.info("%s",new StringBuilder()
					.append("Hacker output:\n")
					.append("\nBlockstate = ").append(block)
					.append("\n\nBlock class dump:\n").append(superDump(block.getBlock()))
					.append("\n\nTileEntity class dump:\n")
					.append(w.getTileEntity(coord) == null ? "null" : superDump(w.getTileEntity(coord)))
			.toString());
		} catch (IllegalAccessException e) {
			FMLLog.log(Level.WARN,e,"Hacker fail!");
		}
		//*/

		if(bs.getBlock().rotateBlock(w,coord, facing)) return EnumActionResult.SUCCESS;
		if(bs.withRotation(Rotation.COUNTERCLOCKWISE_90) != bs) {
			w.setBlockState(coord, bs.withRotation(Rotation.COUNTERCLOCKWISE_90));
			return EnumActionResult.SUCCESS;
		}


		return EnumActionResult.PASS;
	}
}
