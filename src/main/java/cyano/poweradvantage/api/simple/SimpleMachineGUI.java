package cyano.poweradvantage.api.simple;


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
import cyano.poweradvantage.math.Integer2D;
import cyano.poweradvantage.registry.ITileEntityGUI;

public class SimpleMachineGUI implements ITileEntityGUI {

	
	public final static ResourceLocation playerInventoryBox  = new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/player_inventory.png");
	
	protected final ResourceLocation guiDisplayImage;
	protected final int guiWidth, guiHeight;
	protected final Integer2D[] inventorySlotCoordinates;
	
	public SimpleMachineGUI(ResourceLocation guiImage, Integer2D[] inventorySlotCoordinates){
		this.guiDisplayImage = guiImage;
		this.guiWidth = 176;
		this.guiHeight = 222;
		if(inventorySlotCoordinates == null){
			this.inventorySlotCoordinates = new Integer2D[0];
		} else {
			this.inventorySlotCoordinates = inventorySlotCoordinates;
		}
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
			ItemStack stack = null;
			Slot slotObject = (Slot) inventorySlots.get(slot);

			//null checks and checks if the item can be stacked (maxStackSize > 1)
			if (slotObject != null && slotObject.getHasStack()) {
				ItemStack stackInSlot = slotObject.getStack();
				stack = stackInSlot.copy();

				//merges the item into player inventory since its in the tileEntity
				if (slot < 9) {
					if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
						return null;
					}
				}
				//places it into the tileEntity is possible since its in the player inventory
				else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
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
	
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){}
	
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
			drawGUIDecorations(entity, this, x, y);
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
