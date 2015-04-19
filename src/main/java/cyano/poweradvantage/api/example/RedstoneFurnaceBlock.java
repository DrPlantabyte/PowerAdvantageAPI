package cyano.poweradvantage.api.example;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerConsumer;

public class RedstoneFurnaceBlock extends BlockSimplePowerConsumer{

	public RedstoneFurnaceBlock(int guiHandlerID, Object ownerOfGUIHandler) {
		super(Material.piston, 0.5f, new ConduitType("redstone"), guiHandlerID, ownerOfGUIHandler);
		super.setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
    public void onBlockClicked(World w, BlockPos p, EntityPlayer player){
    	if(w.isRemote)return;
    	PoweredEntity e = (PoweredEntity)w.getTileEntity(p);
    	player.addChatMessage(new net.minecraft.util.ChatComponentText(e.getType()+": "+e.getEnergy()));
    	
    }
	
	@Override
	public PoweredEntity createNewTileEntity(World world, int metaDataValue) {
		return new RedstoneFurnaceTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te instanceof RedstoneFurnaceTileEntity){
			RedstoneFurnaceTileEntity rsf = (RedstoneFurnaceTileEntity)te;
			if(rsf.getInventory()[0] == null) return 0;
			return (int)(15 * ((float)rsf.getInventory()[0].stackSize / 64f));
		} else {
			return 0;
		}
	}

}
