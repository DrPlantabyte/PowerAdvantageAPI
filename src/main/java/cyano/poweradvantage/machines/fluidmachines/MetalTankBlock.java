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

public class MetalTankBlock extends BlockSimpleFluidSource{

	public MetalTankBlock() {
		super(Material.piston, 3f);
	}

	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new MetalTankTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}


	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
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
			if(tileEntity instanceof MetalTankTileEntity){
				MetalTankTileEntity te = (MetalTankTileEntity)tileEntity;
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


	
	

}
