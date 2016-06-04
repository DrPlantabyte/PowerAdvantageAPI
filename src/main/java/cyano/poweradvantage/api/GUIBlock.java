package cyano.poweradvantage.api;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.*;

import static net.minecraft.init.Items.BUCKET;

/**
 * <p>
 * The GUIBlock is a convenient abstract class for all blocks that should show a GUI when the player 
 * right-clicks on the block. After creating an instance of the GUIBlock class, get a GUI index 
 * by calling <code>int gui_index = MachineGUIRegistry.addGUI(...)</code> and set the GUI indes and 
 * GUI owner with 
 * <code>myGUIBlock.setGuiID(gui_index); myGUIBlock.setGuiOwner(PowerAdvantage.getInstance())</code>. 
 * Of course, if you are managing the GUIs yourself, then you will provide your own GUI index and 
 * use your mod's class instance as the owner instead of PowerAdvantage.
 * </p> 
 * @author DrCyano
 *
 */
public abstract class GUIBlock extends net.minecraft.block.BlockContainer{

	/**
	 * Constructor for GUI block
	 * @param m Material for the block (determines what tools can break it and how it interacts with 
	 * other Minecraft rules).
	 */
	public GUIBlock(Material m) {
		super(m);
        this.setLightOpacity(0);
	}
	
	private int guiId = 0;
	private Object guiOwner = null;
	/**
	 * Sets the GUI index number for the GUI to show when this block is right-clicked by the player. 
	 * In short, when the player right-clicks this block, the following code is called<br>
	 * <code>player.openGui(this.getGuiOwner(), this.getGuiID(),world,pos);</code>
	 * @param idNumber The number of the GUI to show according to the Forge GUI system.
	 * @param guiOwner This is the object that was used to register the GUI handler (e.g. <i>PowerAdvantage.getInstance()</i> in 
	 * <code>NetworkRegistry.INSTANCE.registerGuiHandler(PowerAdvantage.getInstance(), MachineGUIRegistry.getInstance());</code>
	 * ). This is usually the mod's main class, or if you are using the <b>MachineGUIRegistry</b>, 
	 * then you would set the GUI owner to PowerAdvantage.getInstance().
	 */
	public void setGuiID(int idNumber, Object guiOwner){
		this.guiId = idNumber;
		this.guiOwner = guiOwner;
	}
	/**
	 * Gets the GUI index number for the GUI to show when this block is right-clicked by the player. 
	 * In short, when the player right-clicks this block, the following code is called<br>
	 * <code>player.openGui(this.getGuiOwner(), this.getGuiID(),world,pos);</code>
	 * @return The number of the GUI to show according to the Forge GUI system.
	 */
	public int getGuiID(){
		return guiId;
	}
	/**
	 * Gets the object that was used to register the GUI handler (e.g. <i>PowerAdvantage.getInstance()</i> in 
	 * <code>NetworkRegistry.INSTANCE.registerGuiHandler(PowerAdvantage.getInstance(), MachineGUIRegistry.getInstance());</code>
	 * ). This is usually the mod's main class, or if you are using the <b>MachineGUIRegistry</b>, 
	 * then you would set the GUI owner to PowerAdvantage.getInstance(). 
	 * In short, when the player right-clicks this block, the following code is called<br>
	 * <code>player.openGui(this.getGuiOwner(), this.getGuiID(),world,pos);</code>
	 * @return The owner of the GUI when you registered the GUI handler (not the GUI handler 
	 * itself).
	 */
	public Object getGuiOwner(){
		return this.guiOwner;
	}
	/**
	 * Boilerplate code
	 */
	@Override
    public boolean isFullCube(IBlockState bs) {
        return false;
    }
    
	/**
	 * Boilerplate code
	 */
	@Override
    public boolean isOpaqueCube(IBlockState bs) {
        return false;
    }
	
	/**
	 * 3 = normal block (model specified in assets folder as .json model)<br>
	 * -1 = special renderer
	 */
	@Override
    public EnumBlockRenderType getRenderType(IBlockState bs) {
        return EnumBlockRenderType.MODEL;
    }
	
	/**
     * Override of default block behavior to show the player the GUI for this 
     * block. Calls <code>player.openGui(this.getGuiOwner(), this.getGuiID(),world,pos);</code> on 
     * right-click.
     * @return true if the interaction resulted in opening the GUI, false 
     * otherwise
     */
    @Override
    public boolean onBlockActivated(World w, BlockPos coord, IBlockState bs,
									EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing facing,
									float hitX, float hitY, float hitZ) {
        if (w.isRemote) {
            return true;
        }
        final TileEntity tileEntity = w.getTileEntity(coord);
        if (tileEntity == null || player.isSneaking()) {
        	return false;
        }
        // handle buckets and fluid containers
		ItemStack item = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if(tileEntity instanceof IFluidHandler && item != null) {
			/// NEW WAY - IFluidContainerItem and the UniversalBucket FTW!
			IFluidHandler target = (IFluidHandler) tileEntity;
			if (item.getItem() instanceof IFluidContainerItem){
				// fill from bucket
				IFluidContainerItem container = (IFluidContainerItem) item.getItem();
				if (container.getFluid(item) != null && container.getFluid(item).amount > 0) {
					if (target.fill(facing,
							container.drain(item,container.getFluid(item).amount,false),
							false)
							== container.getFluid(item).amount){
						// simulated fill-drain succeeded, do it for real
						FluidStack drained = container.drain(item,container.getFluid(item).amount,!player.capabilities.isCreativeMode);
						target.fill(facing,drained,true);
						return true;
					}
				}
			}else if (item.getItem() == BUCKET) {
				// make universal bucket
				for(FluidTankInfo tank : target.getTankInfo(facing)){
					if(tank.fluid != null){
						// special handling for water and lava (no universal bucket)
						if(tank.fluid.getFluid() == FluidRegistry.WATER){
							ItemStack filledBucket = new ItemStack(Items.WATER_BUCKET);
							if(tank.fluid.amount >= 1000) {
								FluidStack drain = tank.fluid.copy();
								drain.amount = 1000;
								if(target.drain(facing,drain,false).amount == drain.amount){
									target.drain(facing,drain,true);
									if(!player.capabilities.isCreativeMode)player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, filledBucket);
								}
							}
							return true;
						}
						if(tank.fluid.getFluid() == FluidRegistry.LAVA){
							ItemStack filledBucket = new ItemStack(Items.LAVA_BUCKET);
							if(tank.fluid.amount >= 1000) {
								FluidStack drain = tank.fluid.copy();
								drain.amount = 1000;
								if(target.drain(facing,drain,false).amount == drain.amount){
									target.drain(facing,drain,true);
									if(!player.capabilities.isCreativeMode)player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, filledBucket);
								}
							}
							return true;
						}
						// back to your regularly scheduled algorithm...
						UniversalBucket bucket = ForgeModContainer.getInstance().universalBucket;
						ItemStack filledBucket = new ItemStack(bucket);
						if(tank.fluid.amount >= bucket.getCapacity(filledBucket)) {
							FluidStack drain = tank.fluid.copy();
							drain.amount = bucket.getCapacity(filledBucket);
							if(target.drain(facing,drain,false).amount == bucket.fill(filledBucket,drain,false)){
								bucket.fill(filledBucket,target.drain(facing,drain,true),true);
								if(!player.capabilities.isCreativeMode)player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, filledBucket);
								return true;
							}
						}
					}
				}
			} else if (item != null && FluidContainerRegistry.isContainer(item) && tileEntity instanceof IFluidHandler) {
				/// OLD WAY - deprecated (but still might be used by other mods)
				boolean bucketed = handleBucketInteraction(item, player, facing, (IFluidHandler) tileEntity, w);
				if (bucketed) {
					return true;
				}
			}
		}

        // open GUI
        if(this.getGuiOwner() == null) return false;
        player.openGui(this.getGuiOwner(), this.getGuiID(), w, coord.getX(), coord.getY(), coord.getZ());
        return true;
    }

    
   
	/**
	 * This method is used for filling IFluidContainer instances with liquids from a player's 
	 * bucket.
	 * @param bucket A bucket (or other registered container item) held by the player
	 * @param player The player interacting with the block
	 * @param blockFace The face on the block that the player clicked on
	 * @param target The IFluidHandler that the player interacted with
	 * @param world World instance
	 * @return true if fluids were transferred, false otherwise
	 * @deprecated this method will no longer work in later versions of Minecraft Forge 1.9.x
	 */
	@Deprecated
    public static boolean handleBucketInteraction(ItemStack bucket,final EntityPlayer player, 
			final EnumFacing blockFace, IFluidHandler target, final World world) {
		/// OLD WAY - deprecated (but still might be used by other mods)
		if(FluidContainerRegistry.isEmptyContainer(bucket)){
			// pull from tank
			FluidStack practice = target.drain(blockFace, FluidContainerRegistry.BUCKET_VOLUME, false);
			if(practice != null && practice.amount ==  FluidContainerRegistry.BUCKET_VOLUME
					&& FluidContainerRegistry.fillFluidContainer(practice, bucket) != null){
				FluidStack drain = target.drain(blockFace, FluidContainerRegistry.BUCKET_VOLUME, true);
				ItemStack newBucket = FluidContainerRegistry.fillFluidContainer(drain, bucket);
				if(bucket.stackSize == 1){
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, newBucket);
				} else {
					bucket.stackSize--;
					if(newBucket != null)
						world.spawnEntityInWorld(new EntityItem(world,player.posX,player.posY,player.posZ, newBucket));
				}
				return true;
			} else {
				return false;
			}
		} else if(FluidContainerRegistry.isFilledContainer(bucket)){
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
			int practice = target.fill(blockFace, fluid, false);
			if(practice == FluidContainerRegistry.getContainerCapacity(bucket)){
				// pour into empty tank
				target.fill(blockFace, fluid, true);
				ItemStack newBucket = FluidContainerRegistry.drainFluidContainer(bucket);
				if(bucket.stackSize == 1){
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, newBucket);
				} else {
					bucket.stackSize--;
					world.spawnEntityInWorld(new EntityItem(world,player.posX,player.posY,player.posZ, newBucket));
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
    

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInventory)
		{
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
			((IInventory)te).clear();
			world.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(world, pos, state);
	}
	
}
