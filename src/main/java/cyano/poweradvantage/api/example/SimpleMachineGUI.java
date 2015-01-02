package cyano.poweradvantage.api.example;

import java.awt.geom.Point2D;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cyano.poweradvantage.PowerAdvantage;

public class SimpleMachineGUI implements ITileEntityGUI {

	
	public final static ResourceLocation playerInventoryBox  = new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/player_inventory.png");
	
	protected final ResourceLocation guiDisplayImage;
	protected final int guiWidth, guiHeight;
	protected final Integer2D[] inventorySlotCoordinates;
	
	public SimpleMachineGUI(ResourceLocation guiImage, int guiImageWidth, int guiImageHeight, Integer2D[] inventorySlotCoordinates){
		this.guiDisplayImage = guiImage;
		this.guiHeight = guiImageHeight;
		this.guiWidth = guiImageWidth;
		this.inventorySlotCoordinates = inventorySlotCoordinates;
	}
	
	public class Container extends net.minecraft.inventory.Container{

		private final IInventory entity;
		public Container(InventoryPlayer playerItems, IInventory entity){
			this.entity = entity;
			int index = 0;
			for(Integer2D pt : inventorySlotCoordinates){
				this.addSlotToContainer(new net.minecraft.inventory.Slot(entity,index,pt.X,pt.Y));
				index++;
			}
			bindPlayerInventory(playerItems, 132+9);
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
		
//		@Override
//		public ItemStack transferStackInSlot(final EntityPlayer player, final int slotIndex) {
//			ItemStack copy = null;
//			final Slot slot = (Slot)this.inventorySlots.get(slotIndex);
//			if (slot != null && slot.getHasStack()) {
//				final ItemStack stack = slot.getStack();
//				copy = stack.copy();
//				if (slotIndex < inventorySlotCoordinates.length) {
//					if (!this.mergeItemStack(stack, inventorySlotCoordinates.length, this.inventorySlots.size(), true)) {
//						return null;
//					}
//				}
//				else if (!this.mergeItemStack(stack, 0, inventorySlotCoordinates.length, false)) {
//					return null;
//				}
//				if (stack.stackSize == 0) {
//					slot.putStack(null);
//				}
//				else {
//					slot.onSlotChanged();
//				}
//			}
//			return copy;
//		}
//		
	}
	
	public void drawGUIDecorations(Object srcEntity){}
	
	public class GUIContainer extends net.minecraft.client.gui.inventory.GuiContainer{
		private final Object entity;
		public GUIContainer(InventoryPlayer playerItems, IInventory entity) {
			super(new Container(playerItems,entity));
			this.entity = entity;
		}
		@Override
		protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(playerInventoryBox);
			int x = (width - xSize) / 2;
			int y = (height - ySize) / 2;
			final int inventoryYOffset = 132; // this many pixels of space for main GUI
			final int playerInventoryWidth = 176;
			final int playerInventoryHeight = 91;
			this.drawTexturedModalRect(x, y + inventoryYOffset, 0, 0, playerInventoryWidth, playerInventoryHeight); // x, y, textureOffsetX, textureOffsetY, width, height)
			this.mc.renderEngine.bindTexture(guiDisplayImage);
			this.drawTexturedModalRect(x, y, 0, 0, guiWidth, guiHeight); // x, y, textureOffsetX, textureOffsetY, width, height)
			drawGUIDecorations(entity);
		}
		
	}

	@Override
	public net.minecraft.inventory.Container getContainer(TileEntity e, EntityPlayer player) {
		return new Container(player.inventory,(IInventory)e);
	}

	@Override
	public GuiContainer getContainerGUI(TileEntity e, EntityPlayer player) {
		return new GUIContainer(player.inventory,(IInventory)e);
	}
}
