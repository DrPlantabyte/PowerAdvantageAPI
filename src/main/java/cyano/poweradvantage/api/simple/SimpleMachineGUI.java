package cyano.poweradvantage.api.simple;


import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.math.Integer2D;
import cyano.poweradvantage.registry.ITileEntityGUI;

/**
 * This class provides a simple way to add a GUI for a machine. Instances of 
 * this class can be added to the MachineGUIRegistry. Simply initializing this 
 * class will create a static GUI with inventory slots, but if you want to have 
 * any animations/indicators (e.g. a progress bar), you will need to extend this 
 * class and override the <b>drawGUIDecorations(...)</b> method to draw the 
 * animated parts of the GUI. 
 * @author DrCyano
 *
 */
public class SimpleMachineGUI implements ITileEntityGUI {

	/**
	 * This is the texture resource for drawing the player's inventory.
	 */
	public final static ResourceLocation playerInventoryBox  = new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/player_inventory.png");
	/** Texture resource for the machine's GUI */ 
	protected final ResourceLocation guiDisplayImage;
	/** Dimensions of the GUI texture image */ 
	protected final int guiWidth, guiHeight;
	/** List of pixel coordinates for the upper-left corner of each inventory 
	 * slot that should appear on the screen */
	protected final Integer2D[] inventorySlotCoordinates;
	/**
	 * Standard constructor for SimpleMachineGUI. You must provide a texture 
	 * resource location for the GUI background image. The inventory slots are 
	 * positioned besed on the array of coordinates (as Integer2D objects) 
	 * passed in to this constructor.
	 * @param guiImage The location of the texture resource for the GUI
	 * @param inventorySlotCoordinates List of pixel coordinates (relative to 
	 * the top-left corner of the GUI image) to draw the inventory slots. If you 
	 * don't want to show GUI slots, you can provide a null or empty array. It 
	 * is assumed that the index in this list of coordinates corresponds to the 
	 * inventory index of the TileEntity's inventory.  
	 */
	public SimpleMachineGUI(ResourceLocation guiImage, Integer2D... inventorySlotCoordinates){
		this.guiDisplayImage = guiImage;
		this.guiWidth = 176;
		this.guiHeight = 222;
		if(inventorySlotCoordinates == null){
			this.inventorySlotCoordinates = new Integer2D[0];
		} else {
			this.inventorySlotCoordinates = inventorySlotCoordinates;
		}
	}
	
	/**
	 * Standard constructor for SimpleMachineGUI. You must provide a texture 
	 * resource location for the GUI background image. The inventory slots are 
	 * positioned besed on the array of coordinates (as Integer2D objects) 
	 * passed in to this constructor.
	 * @param guiImage The location of the texture resource for the GUI
	 * @param inventorySlotCoordinates List of pixel coordinates (relative to 
	 * the top-left corner of the GUI image) to draw the inventory slots. If you 
	 * don't want to show GUI slots, you can provide a null or empty array. It 
	 * is assumed that the index in this list of coordinates corresponds to the 
	 * inventory index of the TileEntity's inventory.  
	 */
	public SimpleMachineGUI(String guiImage, Integer2D... inventorySlotCoordinates){
		this(new ResourceLocation(guiImage),inventorySlotCoordinates);
	}
	
	

	/**
	 * Override this method to draw on the GUI window.
	 * <br><br>
	 * This method is invoked when drawing the GUI so that you can draw 
	 * animations and other foreground decorations to the GUI.
	 * @param srcEntity This is the TileEntity (or potentially a LivingEntity) 
	 * for whom we are drawing this interface
	 * @param guiContainer This is the instance of GUIContainer that is drawing 
	 * the GUI. You need to use it to draw on the screen. For example:<br>
	   <pre>
guiContainer.mc.renderEngine.bindTexture(arrowTexture);
guiContainer.drawTexturedModalRect(x+79, y+35, 0, 0, arrowLength, 17); // x, y, textureOffsetX, textureOffsetY, width, height)
	   </pre>
	 * @param x This is the x coordinate (in pixels) from the top-left corner of 
	 * the GUI
	 * @param y This is the y coordinate (in pixels) from the top-left corner of 
	 * the GUI
	 * @param z This is the z coordinate (no units) into the depth of the screen
	 */
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y, float z){}

	/**
	 * Gets an instance of net.minecraft.inventory.Container for the 
	 * server-side portion of a GUI interaction.
	 * @param e The TileEntity that is presenting this GUI
	 * @param player The player that is going to use the GUI
	 * @return An instance of net.minecraft.inventory.Container
	 */
	@Override
	public net.minecraft.inventory.Container getContainer(TileEntity e, EntityPlayer player) {
		return new Container(player.inventory,(IInventory)e);
	}

	/**
	 * Gets an instance of net.minecraft.client.gui.inventory.GuiContainer for 
	 * the client-side portion of a GUI interaction.
	 * @param e The TileEntity that is presenting this GUI
	 * @param player The player that is going to use the GUI
	 * @return An instance of net.minecraft.client.gui.inventory.GuiContainer
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getContainerGUI(TileEntity e, EntityPlayer player) {
		return new GUIContainer(player.inventory,(IInventory)e);
	}
	
	
	/**
	 * Member class used to provide an instance of 
	 * net.minecraft.inventory.Container to fulfill the requirements of the 
	 * interface ITileEntityGUI.
	 */
	public class Container extends net.minecraft.inventory.Container{

		private final IInventory entity;
		public Container(InventoryPlayer playerItems, IInventory entity){
			this.entity = entity;
			int index = 0;
			for(Integer2D pt : inventorySlotCoordinates){
				this.addSlotToContainer(new net.minecraft.inventory.Slot(entity,index,pt.X,pt.Y));
				index++;
			}
			bindPlayerInventory(playerItems, 140);
		}
		
		@Override
		public boolean canInteractWith(EntityPlayer entityplayer) {
			return entity.isUseableByPlayer(entityplayer);
		}
		
		protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int yOffset) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 9; j++) {
					addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, yOffset + i * 18));
				}
			}
			for (int i = 0; i < 9; i++) {
				addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, yOffset+58));
			}
		}
		
		@Override
		public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
			int hostSize = inventorySlotCoordinates.length;
			ItemStack stack = null;
			Slot slotObject = (Slot) inventorySlots.get(slot);
FMLLog.info("transferStackInSlot slot #"+slot+", slot item is "+slotObject.getStack()); // TODO: remove debug code
			//null checks and checks if the item can be stacked (maxStackSize > 1)
			if (slotObject != null && slotObject.getHasStack()) {
				ItemStack stackInSlot = slotObject.getStack();
				stack = stackInSlot.copy();

				//merges the item into player inventory since its in the tileEntity
				if (slot < hostSize) {
					if (!this.mergeItemStack(stackInSlot, hostSize, 36+hostSize, true)) {
						return null;
					}
				}
				//places it into the tileEntity if possible since it's in the player inventory
				else if (!this.mergeItemStack(stackInSlot, 0, hostSize, false)) {
					return null;
				}

				if (stackInSlot.stackSize == 0) {
					slotObject.putStack(null);
				} else {
					slotObject.onSlotChanged();
				}

				if (stackInSlot.stackSize == stack.stackSize) {
					return null;
				}
				slotObject.onPickupFromSlot(player, stackInSlot);
			}
			return stack;
		}
		
	}
	
	/**
	 * Member class used to provide an instance of 
	 * net.minecraft.client.gui.inventory.GuiContainer to fulfill the 
	 * requirements of the interface ITileEntityGUI.
	 */

	@SideOnly(Side.CLIENT)
	public class GUIContainer extends net.minecraft.client.gui.inventory.GuiContainer{
		private final Object entity;
		public GUIContainer(InventoryPlayer playerItems, IInventory entity) {
			super(new Container(playerItems,entity));
//System.out.println("Size: "+this.xSize+"x"+this.ySize+" at coordinate ("+this.guiLeft+","+this.guiTop+")");
			this.xSize = guiWidth;
			this.ySize = guiHeight;
			this.entity = entity;
		}
		@Override
		protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(playerInventoryBox);
			int x = (width - xSize) / 2;
			int y = (height - ySize) / 2;
			final int playerInventoryWidth = 176;
			final int playerInventoryHeight = 222;
			this.drawTexturedModalRect(x, y, 0, 0, playerInventoryWidth, playerInventoryHeight); // x, y, textureOffsetX, textureOffsetY, width, height)
			this.mc.renderEngine.bindTexture(guiDisplayImage);
			this.drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight); // x, y, textureOffsetX, textureOffsetY, width, height)
			drawGUIDecorations(entity, this, x, y, this.zLevel);
		}
		/**
		 * Gets the z-coordinate of this GUI element
		 * @return screen depth coordinate
		 */
		public float getZLevel(){
			return this.zLevel;
		}
		/**
		 * Gets the width of the GUI
		 * @return size of GUI
		 */
		public int getXSize(){
			return this.xSize;
		}

		/**
		 * Gets the height of the GUI
		 * @return size of GUI
		 */
		public int getYSize(){
			return this.ySize;
		}
		/**
		 * Gets the GUI offset from left side of screen, in pixels
		 * @return pixels to left of GUI
		 */
		public int getLeft(){
			return this.guiLeft;
		}

		/**
		 * Gets the GUI offset from top of screen, in pixels
		 * @return pixels to above the GUI
		 */
		public int getTop(){
			return this.guiTop;
		}
		
	}
}
