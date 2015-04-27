package cyano.poweradvantage.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.machines.StorageTankTileEntity;

public class StorageTankItem extends net.minecraft.item.Item {
	// TODO: delete this class (if redundant)
	public StorageTankItem(){
		this.setMaxStackSize(1);
	}

	public ItemStack createItem(StorageTankTileEntity te){
		FMLLog.info("Tank holds "+te.getTank().getFluidAmount()+" units of "+(te.getTank().getFluid() == null ? "nothing" : te.getTank().getFluid().getFluid().getName())); // TODO: remove debug code
		ItemStack item = new ItemStack(this,1);
		if(te.getTank().getFluidAmount() > 0){
			NBTTagCompound dataTag = new NBTTagCompound();
			dataTag.setInteger("FluidID", te.getTank().getFluid().getFluid().getID());
			dataTag.setInteger("Volume", te.getTank().getFluidAmount());
			FMLLog.info("adding NBT tag to item"); // TODO: remove debug code
			item.setTagCompound(dataTag);
		}
		return item;
	}
	
	@Override
	public boolean onItemUse(final ItemStack item, final EntityPlayer player, final World world, 
			BlockPos coord, EnumFacing face, 
			final float partialX, final float partialY, final float partialZ) {
		ItemBlock b;
		boolean result = net.minecraft.item.Item.getItemFromBlock(cyano.poweradvantage.init.Blocks.storage_tank).onItemUse(item, player, world, coord, face, partialX, partialY, partialZ);
		if(result && item.hasTagCompound() && item.getTagCompound().hasKey("FluidID") && item.getTagCompound().hasKey("Volume")){
			StorageTankTileEntity st = (StorageTankTileEntity)world.getTileEntity(coord);
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(item.getTagCompound().getInteger("FluidID")),item.getTagCompound().getInteger("Volume"));
			st.getTank().setFluid(fs);
			item.stackSize--;
		}
		return result;
	}
	
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b){
		if(stack.hasTagCompound()){
			NBTTagCompound dataTag = stack.getTagCompound();
			float buckets = (float)dataTag.getInteger("Volume") / (float)FluidContainerRegistry.BUCKET_VOLUME;
			list.add(StatCollector.translateToLocal("tooltip.poweradvantage.buckets_of")
					.replace("%x", String.valueOf(buckets))
					.replace("%y",FluidRegistry.getFluidName(dataTag.getInteger("FluidID"))));
		} else {
			list.add(StatCollector.translateToLocal("tooltip.poweradvantage.empty"));
		}
	}

}
