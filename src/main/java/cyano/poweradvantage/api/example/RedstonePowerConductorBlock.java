package cyano.poweradvantage.api.example;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.PowerConductorEntity;
import cyano.poweradvantage.api.simple.BlockSimplePowerConductor;
import cyano.poweradvantage.api.simple.TileEntitySimplePowerConductor;

public class RedstonePowerConductorBlock extends BlockSimplePowerConductor{

	
	 public RedstonePowerConductorBlock() {
		super(Material.piston, 0.5f, 0.20f, new ConductorType("redstone"));
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
	
	@SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(final World world, final BlockPos coord, final IBlockState bs, final Random prng) {
        TileEntity e = world.getTileEntity(coord);
        if(e != null && e instanceof PowerConductorEntity){
        	if(((PowerConductorEntity)e).getEnergyBuffer() > 0){
        		final double x = coord.getX() + 0.5 + (prng.nextFloat() - 0.5) * 0.2;
                final double y = coord.getY() + 0.5 + (prng.nextFloat() - 0.5) * 0.2;
                final double z = coord.getZ() + 0.5 + (prng.nextFloat() - 0.5) * 0.2;
                world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 1f, 1f, 1f, new int[0]);
        	}
        }
    }
}
