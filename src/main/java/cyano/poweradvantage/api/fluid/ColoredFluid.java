package cyano.poweradvantage.api.fluid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class ColoredFluid extends Fluid{

	
	private final int color;
	
	public ColoredFluid(String fluidName, ResourceLocation still, ResourceLocation flowing, int tintARGB) {
		super(fluidName, still, flowing);
		this.color = tintARGB;
	}
	
	@Override
	public int getColor(){
		return color;
	}

}
