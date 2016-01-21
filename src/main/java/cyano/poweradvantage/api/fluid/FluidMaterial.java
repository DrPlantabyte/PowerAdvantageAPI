package cyano.poweradvantage.api.fluid;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;

/**
 * Extension of the fluid materials to let you create a fluid with a custom map color (stead of 
 * using water).
 * @author DrCyano
 *
 */
public class FluidMaterial extends MaterialLiquid {

	/**
	 * Creates a new material with the same properties as water
	 * @param color Color on the map
	 */
	public FluidMaterial(MapColor color) {
		super(color);
		setNoPushMobility();
	}

}
