package cyano.poweradvantage.api.example;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerConductor;

public class RedstonePowerConductorBlock extends BlockSimplePowerConductor{

	
	 public RedstonePowerConductorBlock() {
		super(Material.piston, 0.5f, 0.35f, new ConductorType("redstone"));
	}

	@Override
	    public void onBlockClicked(World w, BlockPos p, EntityPlayer player){
	    	if(w.isRemote)return;
	    	PowerConductorEntity e = (PowerConductorEntity)w.getTileEntity(p);
	    	player.addChatMessage(new net.minecraft.util.ChatComponentText(e.getEnergyType()+": "+e.getEnergyBuffer()));
	    }

	@Override
	public TileEntity createNewTileEntity(World world, int metaDataValue) {
		return new RedstonePowerConductorTileEntity();
	}
}
