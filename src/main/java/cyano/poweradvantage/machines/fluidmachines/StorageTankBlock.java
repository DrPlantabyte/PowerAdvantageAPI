package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimpleFluidMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class StorageTankBlock extends BlockSimpleFluidMachine {

	public StorageTankBlock() {
		super(Material.piston, 3f);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new StorageTankTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs) {
		return true;
	}


	@Override
	public int getComparatorInputOverride(IBlockState bs, World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te != null && te instanceof StorageTankTileEntity){
			return ((StorageTankTileEntity)te).getRedstoneOutput();
		}
		return 0;
	}
	
	/**
	 * Determines whether this block/entity should receive energy
	 * @param powerType Type of power
	 * @return true if this block/entity should receive energy
	 */
	@Override
	public boolean isPowerSink(ConduitType powerType){
		return true;
	}
	/**
	 * Determines whether this block/entity can provide energy
	 * @param powerType Type of power
	 * @return true if this block/entity can provide energy
	 */
	@Override
	public boolean isPowerSource(ConduitType powerType){
		return true;
	}

	public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, final EntityLivingBase player, final ItemStack item) {
		super.onBlockPlacedBy(world, coord, bs, player, item);
		if( item.hasTagCompound() && item.getTagCompound().hasKey("FluidID") && item.getTagCompound().hasKey("Volume")){
			StorageTankTileEntity st = (StorageTankTileEntity)world.getTileEntity(coord);
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(item.getTagCompound().getString("FluidID")),item.getTagCompound().getInteger("Volume"));
			st.getTank().setFluid(fs);
		}
    }
	

    
    
    public void doItemDrop(final World world, final BlockPos coord, TileEntity te ) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
			if(te instanceof StorageTankTileEntity){
        		ItemStack item = createItem((StorageTankTileEntity)te);
        		spawnAsEntity(world, coord, item);
        	} else {
        		ItemStack item = new ItemStack(this,1);
        		spawnAsEntity(world, coord, item);
        	}
        }
    }

    
    public ItemStack createItem(StorageTankTileEntity te){
		ItemStack item = new ItemStack(this,1);
		if(te.getTank().getFluidAmount() > 0){
			NBTTagCompound dataTag = new NBTTagCompound();
			dataTag.setString("FluidID", te.getTank().getFluid().getFluid().getName());
			dataTag.setInteger("Volume", te.getTank().getFluidAmount());
			item.setTagCompound(dataTag);
		}
		displayInformation(item);
		return item;
	}


	
	public void displayInformation(ItemStack stack){
		String message;
		NBTTagCompound dataTag;
		if(!stack.hasTagCompound()){
			dataTag = new NBTTagCompound();
		} else {
			dataTag = stack.getTagCompound();
		}
		if(dataTag.hasKey("Volume") && dataTag.hasKey("FluidID")){
			float buckets = (float)dataTag.getInteger("Volume") / (float)FluidContainerRegistry.BUCKET_VOLUME;
			message = (I18n.translateToLocal("tooltip.poweradvantage.buckets_of")
					.replace("%x", String.valueOf(buckets))
					.replace("%y",FluidRegistry.getFluid(dataTag.getString("FluidID")).getName()));
		} else {
			message = (I18n.translateToLocal("tooltip.poweradvantage.empty"));
		}
		
		NBTTagCompound displayTag;
		if(dataTag.hasKey("display")){
			displayTag = dataTag.getCompoundTag("display");
		} else {
			displayTag = new NBTTagCompound();
			dataTag.setTag("display", displayTag);
		}
		NBTTagList loreTag;
		if(dataTag.hasKey("Lore")){
			loreTag = displayTag.getTagList("Lore",8);
		} else {
			loreTag = new NBTTagList();
			displayTag.setTag("Lore", loreTag);
		}
		loreTag.appendTag(new NBTTagString(message));
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	


}
