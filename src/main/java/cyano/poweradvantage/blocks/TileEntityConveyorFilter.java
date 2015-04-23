package cyano.poweradvantage.blocks;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public abstract class TileEntityConveyorFilter extends TileEntityConveyor {

	public TileEntityConveyorFilter(){
		this(1);
	}
	

	public abstract boolean matchesFilter(ItemStack item);
	@Override
	public void update() {
		// TODO Implementation
		if(matchesFilter(this.getInventory()[0])){
			dropItem();
		}
	}
	
	private void dropItem() {
		// TODO implementation
	}


	public TileEntityConveyorFilter(int inventorySize){
		super(inventorySize);
	}
		
}
