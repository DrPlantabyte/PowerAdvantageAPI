package cyano.poweradvantage.machines.fluidmachines;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Multimap;

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

	public void onBlockPlacedBy(final World world, final BlockPos coord, final IBlockState bs, final EntityLivingBase player, final ItemStack item) {
		super.onBlockPlacedBy(world, coord, bs, player, item);
		if( item.hasTagCompound() && item.getTagCompound().hasKey("FluidID") && item.getTagCompound().hasKey("Volume")){
			StorageTankTileEntity st = (StorageTankTileEntity)world.getTileEntity(coord);
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(item.getTagCompound().getInteger("FluidID")),item.getTagCompound().getInteger("Volume"));
			st.getTank().setFluid(fs);
		}
    }
	
	/**
     * Override of default block behavior to show the player the GUI for this 
     * block. Calls <code>player.openGui(this.getGuiOwner(), this.getGuiID(),world,pos);</code> on 
     * right-click.
     * @return true if the interaction resulted in opening the GUI, false 
     * otherwise
     */
    @Override
    public boolean onBlockActivated(final World w, final BlockPos coord, final IBlockState bs, 
    		final EntityPlayer player, final EnumFacing facing, final float f1, final float f2, 
    		final float f3) {
    	if (w.isRemote) {
            return true;
        }
        final TileEntity tileEntity = w.getTileEntity(coord);
        if (tileEntity == null || player.isSneaking()) {
        	return false;
        }
    	ItemStack item = player.getCurrentEquippedItem();
    	if(item != null && FluidContainerRegistry.isContainer(item)){
    		if(tileEntity instanceof StorageTankTileEntity){
    			StorageTankTileEntity te = (StorageTankTileEntity)tileEntity;
    			if(FluidContainerRegistry.isEmptyContainer(item)){
    				// pull from tank
    				if(te.getTank().getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME){
    					FluidStack drain = te.drain(facing, FluidContainerRegistry.BUCKET_VOLUME, true);
    					ItemStack newBucket = FluidContainerRegistry.fillFluidContainer(drain, item);
    					if(item.stackSize == 1){
    						player.setCurrentItemOrArmor(0, newBucket);
    					} else {
    						item.stackSize--;
    						w.spawnEntityInWorld(new EntityItem(w,player.posX,player.posY,player.posZ, newBucket));
    					}
    					return true;
    				}
    				return false;
    			} else {
    				FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
    				if(te.getTank().getFluidAmount() <= 0 && te.getTank().getCapacity() >= fluid.amount){
    					// pour into empty tank
    					te.getTank().setFluid(fluid);
    					ItemStack newBucket = FluidContainerRegistry.drainFluidContainer(item);
    					if(item.stackSize == 1){
    						player.setCurrentItemOrArmor(0, newBucket);
    					} else {
    						item.stackSize--;
    						w.spawnEntityInWorld(new EntityItem(w,player.posX,player.posY,player.posZ, newBucket));
    					}
    					return true;
    				} else if(te.getTank().getFluid().getFluid().equals(fluid.getFluid()) 
    						&& (te.getTank().getFluidAmount() + fluid.amount) <= te.getTank().getCapacity()) {
    					// pour into non-empty tank
    					te.getTank().fill(fluid, true);
    					ItemStack newBucket = FluidContainerRegistry.drainFluidContainer( item);
    					if(item.stackSize == 1){
    						player.setCurrentItemOrArmor(0, newBucket);
    					} else {
    						item.stackSize--;
    						w.spawnEntityInWorld(new EntityItem(w,player.posX,player.posY,player.posZ, newBucket));
    					}
    					return true;
    				} else {
    					return false;
    				}
    			}
    		}
    	}
    	return super.onBlockActivated(w, coord, bs, player, facing, f1, f2, f3);
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
    
    @Override
    public void harvestBlock(final World world, final EntityPlayer player, final BlockPos coord, final IBlockState bs, final TileEntity tileEntity) {
    	player.triggerAchievement(StatList.mineBlockStatArray[getIdFromBlock(this)]);
    	player.addExhaustion(0.025f);

    	this.harvesters.set(player);
    	this.doItemDrop(world, coord, tileEntity);
    	this.harvesters.set(null);
    }
    
    public ItemStack createItem(StorageTankTileEntity te){
		ItemStack item = new ItemStack(this,1);
		if(te.getTank().getFluidAmount() > 0){
			NBTTagCompound dataTag = new NBTTagCompound();
			dataTag.setInteger("FluidID", te.getTank().getFluid().getFluid().getID());
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
			message = (StatCollector.translateToLocal("tooltip.poweradvantage.buckets_of")
					.replace("%x", String.valueOf(buckets))
					.replace("%y",FluidRegistry.getFluidName(dataTag.getInteger("FluidID"))));
		} else {
			message = (StatCollector.translateToLocal("tooltip.poweradvantage.empty"));
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
