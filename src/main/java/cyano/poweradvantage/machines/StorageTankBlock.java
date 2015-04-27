package cyano.poweradvantage.machines;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
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
    	FMLLog.info("Right-clicked with "+item); // TODO: remove debug code
    	if(item != null && FluidContainerRegistry.isContainer(item)){
    		FMLLog.info("Entity is a "+tileEntity.getClass().getSimpleName()); // TODO: remove debug code
    		if(tileEntity instanceof StorageTankTileEntity){
    			StorageTankTileEntity te = (StorageTankTileEntity)tileEntity;
    			FMLLog.info("Tank holds "+te.getTank().getFluidAmount()+" units of "+(te.getTank().getFluid() == null ? "nothing" : te.getTank().getFluid().getFluid().getName())); // TODO: remove debug code
    			if(FluidContainerRegistry.isEmptyContainer(item)){
    				// pull from tank
    				FMLLog.info("pulling fluid"); // TODO: remove debug code
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
    					FMLLog.info("pushing fluid into empty tank"); // TODO: remove debug code
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
    					FMLLog.info("pushing fluid"); // TODO: remove debug code
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
    	FMLLog.info("forward to superclass"); // TODO: remove debug code
    	return super.onBlockActivated(w, coord, bs, player, facing, f1, f2, f3);
    }
   
    
    
    public void doItemDrop(final World world, final BlockPos coord, TileEntity te ) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
			FMLLog.info("making item drop from "+(te==null?"nothing":te.getClass().getSimpleName())); // TODO: remove debug code
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
    	FMLLog.info("TileEntity is "+(tileEntity==null?"nothing":tileEntity.getClass().getSimpleName())); // TODO: remove debug code
    	player.triggerAchievement(StatList.mineBlockStatArray[getIdFromBlock(this)]);
    	player.addExhaustion(0.025f);

    	this.harvesters.set(player);
    	this.doItemDrop(world, coord, tileEntity);
    	this.harvesters.set(null);
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
		item.setStackDisplayName(this.getLocalizedName()+" ("+information(item)+")");
		return item;
	}


	
	public String information(ItemStack stack){
		if(stack.hasTagCompound()){
			NBTTagCompound dataTag = stack.getTagCompound();
			float buckets = (float)dataTag.getInteger("Volume") / (float)FluidContainerRegistry.BUCKET_VOLUME;
			return (StatCollector.translateToLocal("tooltip.poweradvantage.buckets_of")
					.replace("%x", String.valueOf(buckets))
					.replace("%y",FluidRegistry.getFluidName(dataTag.getInteger("FluidID"))));
		} else {
			return (StatCollector.translateToLocal("tooltip.poweradvantage.empty"));
		}
	}

}
