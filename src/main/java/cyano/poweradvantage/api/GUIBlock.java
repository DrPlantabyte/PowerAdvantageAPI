package cyano.poweradvantage.api;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


// TODO: documentation
public abstract class GUIBlock extends net.minecraft.block.BlockContainer{

	public GUIBlock(Material m) {
		super(m);
	}
	
	private int guiId = 0;
	private Object guiOwner = null;
	
	public void setGuiID(int idNumber){
		this.guiId = idNumber;
	}
	
	public int getGuiID(){
		return guiId;
	}
	
	public void setGuiOwner(Object modInstance){
		this.guiOwner = modInstance;
	}
	
	public Object getGuiOwner(){
		return this.guiOwner;
	}
	
	/**
     * Override of default block behavior to show the player the GUI for this 
     * block
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
        // open GUI
        if(this.getGuiOwner() == null) return false;
        player.openGui(this.getGuiOwner(), this.getGuiID(), w, coord.getX(), coord.getY(), coord.getZ());
        return true;
    }

	
}
