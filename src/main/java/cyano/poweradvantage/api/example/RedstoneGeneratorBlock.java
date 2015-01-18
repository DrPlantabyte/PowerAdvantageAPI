package cyano.poweradvantage.api.example;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.PowerSourceEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerSource;

public class RedstoneGeneratorBlock extends BlockSimplePowerSource{

	public RedstoneGeneratorBlock(int guiHandlerID, Object ownerOfGUIHandler) {
		super(Material.piston, 0.5f, new ConductorType("redstone"), guiHandlerID, ownerOfGUIHandler);
		super.setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
    public void onBlockClicked(World w, BlockPos p, EntityPlayer player){
    	if(w.isRemote)return;
    	PowerConductorEntity e = (PowerConductorEntity)w.getTileEntity(p);
    	player.addChatMessage(new net.minecraft.util.ChatComponentText(e.getEnergyType()+": "+e.getEnergyBuffer()));
    }
	
	@Override
	public PowerSourceEntity createNewTileEntity(World world,
			int metaDataValue) {
		return new RedstoneGeneratorTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if(te instanceof RedstoneGeneratorTileEntity){
			RedstoneGeneratorTileEntity rsg = (RedstoneGeneratorTileEntity)te;
			return (int)(15 * (rsg.getEnergyBuffer() / rsg.getEnergyBufferCapacity()));
		} else {
			return 0;
		}
	}
	

}
