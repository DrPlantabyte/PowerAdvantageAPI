package cyano.poweradvantage.api.fluid;

import net.minecraft.util.ResourceLocation;
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
