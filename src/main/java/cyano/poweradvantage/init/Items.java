package cyano.poweradvantage.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.registry.FuelRegistry;

public abstract class Items {

	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		initDone = true;
	}
	

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event) {
		// TODO implementation
		
	}
	
}
