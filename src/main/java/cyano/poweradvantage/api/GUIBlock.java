package cyano.poweradvantage.api;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.registry.MachineGUIRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
    public boolean isFullCube() {
        return false;
    }
    
	/**
	 * Boilerplate code
	 */
	@Override
    public boolean isOpaqueCube() {
        return false;
    }
	
	/**
	 * 3 = normal block (model specified in assets folder as .json model)<br>
	 * -1 = special renderer
	 */
	@Override
    public int getRenderType() {
        return 3;
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
        // handle buckets and fluid containers
        ItemStack item = player.getCurrentEquippedItem();
        if(item != null && FluidContainerRegistry.isContainer(item) && tileEntity instanceof IFluidHandler){
        	boolean bucketed = handleBucketInteraction(item,player,facing,(IFluidHandler)tileEntity,w);
        	if(bucketed){
        		return true;
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
	 */
    public static boolean handleBucketInteraction(ItemStack bucket,final EntityPlayer player, 
			final EnumFacing blockFace, IFluidHandler target, final World world) {
		if(FluidContainerRegistry.isEmptyContainer(bucket)){
			// pull from tank
			FluidStack practice = target.drain(blockFace, FluidContainerRegistry.BUCKET_VOLUME, false);
			if(practice != null && practice.amount ==  FluidContainerRegistry.BUCKET_VOLUME
					&& FluidContainerRegistry.fillFluidContainer(practice, bucket) != null){
				FluidStack drain = target.drain(blockFace, FluidContainerRegistry.BUCKET_VOLUME, true);
				ItemStack newBucket = FluidContainerRegistry.fillFluidContainer(drain, bucket);
				if(bucket.stackSize == 1){
					player.setCurrentItemOrArmor(0, newBucket);
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
					player.setCurrentItemOrArmor(0, newBucket);
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
