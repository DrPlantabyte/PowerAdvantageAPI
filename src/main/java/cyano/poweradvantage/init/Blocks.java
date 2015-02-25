package cyano.poweradvantage.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.fluids.block.CopperPipeBlock;
import cyano.poweradvantage.fluids.block.FluidDrainBlock;

public abstract class Blocks {
	private static final Map<String,Block> allBlocks = new HashMap<>();
	
	public static GUIBlock fluid_drain; 
	public static Block copper_pipe;
	
	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		fluid_drain = (GUIBlock)addBlock(new FluidDrainBlock(),"fluid_drain");
		copper_pipe = addBlock(new CopperPipeBlock(),"copper_pipe");
		// TODO: add Power Gauge block that displays the power in a neighboring conductor
		// TODO: add fluid pipes
		
		initDone = true;
	}
	

	private static Block addBlock(Block block, String name ){
		block.setUnlocalizedName(PowerAdvantage.MODID+"."+name);
		GameRegistry.registerBlock(block, name);
		allBlocks.put(name, block);
		return block;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event){
		for(String name : allBlocks.keySet()){
			if(allBlocks.get(name) instanceof BlockDoor) continue;// do not add door blocks
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(net.minecraft.item.Item.getItemFromBlock(allBlocks.get(name)), 0, 
				new ModelResourceLocation(PowerAdvantage.MODID+":"+name, "inventory"));
		}
	}

	
}
