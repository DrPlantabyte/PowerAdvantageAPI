package cyano.poweradvantage.fluids.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.FluidPipeBlock;
import cyano.poweradvantage.api.FluidPipeEntity;
import cyano.poweradvantage.api.PowerConductorEntity;

public class CopperPipeBlock extends FluidPipeBlock{

	public CopperPipeBlock() {
		super(Material.iron, cyano.basemetals.init.Materials.copper.getMetalBlockHardness(), 3f/16f);
		super.setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	// TODO: remove debug code
	@Override
    public void onBlockClicked(World w, BlockPos p, EntityPlayer player){
    	if(w.isRemote)return;
    	FluidPipeEntity e = (FluidPipeEntity)w.getTileEntity(p);
    	player.addChatMessage(new net.minecraft.util.ChatComponentText(e.getFluid().getUnlocalizedName()+": "+e.getFluidAmount(e.getFluid())));
    }
	

	@Override
	public FluidPipeEntity createNewTileEntity(World world, int metaDataValue) {
		return new CopperPipeTileEntity();
	}

}
