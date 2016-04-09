package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.modsupport.rf.BlockRFConverter;
import cyano.poweradvantage.api.modsupport.rf.TileEntityRFElectricityConverter;
import cyano.poweradvantage.api.modsupport.rf.TileEntityRFQuantumConverter;
import cyano.poweradvantage.api.modsupport.rf.TileEntityRFSteamConverter;
import cyano.poweradvantage.api.modsupport.techreborn.BlockTRConverter;
import cyano.poweradvantage.api.modsupport.techreborn.TileEntityTRConverter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ModSupport {
	

	public static Block converter_rf_steam;
	public static Block converter_rf_electricity;
	public static Block converter_rf_quantum;
	public static Block converter_tr_electricity;
	
	private static final Map<String,Block> allBlocks = new HashMap<>();

	private static boolean initDone = false;
	public static void init(boolean rfSupport, boolean techRebornSupport){
		if(initDone) return;
		
		final float defaultMachineHardness = 0.75f;
		final Material defaultMachineMaterial = Material.piston;
		
		if(rfSupport){
			FMLLog.info("Initializing RF interface content");
			converter_rf_steam = addBlock(new BlockRFConverter(defaultMachineMaterial,defaultMachineHardness,new ConduitType("steam"),TileEntityRFSteamConverter.class),"converter_rf_steam");
			converter_rf_electricity = addBlock(new BlockRFConverter(defaultMachineMaterial,defaultMachineHardness,new ConduitType("electricity"),TileEntityRFElectricityConverter.class),"converter_rf_electricity");
			converter_rf_quantum = addBlock(new BlockRFConverter(defaultMachineMaterial,defaultMachineHardness,new ConduitType("quantum"),TileEntityRFQuantumConverter.class),"converter_rf_quantum");

			GameRegistry.registerTileEntity(TileEntityRFSteamConverter.class, PowerAdvantage.MODID+"."+"rf_steam_converter_tileentity");
			GameRegistry.registerTileEntity(TileEntityRFElectricityConverter.class, PowerAdvantage.MODID+"."+"rf_electricity_converter_tileentity");
			GameRegistry.registerTileEntity(TileEntityRFQuantumConverter.class, PowerAdvantage.MODID+"."+"rf_quantum_converter_tileentity");

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converter_rf_steam,1),
					"xyz",
					'x',"governor",'y',"frameSteel",'z',"blockRedstone"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converter_rf_electricity,1),
					"xyz",
					'x',"PSU",'y',"frameSteel",'z',"blockRedstone"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converter_rf_quantum,1),
					"xyz",
					'x',net.minecraft.init.Items.ender_pearl,'y',"frameSteel",'z',"blockRedstone"));
		}
		if(techRebornSupport){
			// first, register transformers in Ore Dictionary
			Iterator<Block> blockIterator = Block.blockRegistry.iterator();
			while(blockIterator.hasNext()){
				Block b = blockIterator.next();
				if(b == null) continue;
				if(b.getClass().getPackage() == null) continue; // air block is an anonymous class?
				if(b.getClass().getPackage().getName().equals("techreborn.blocks.transformers")
						&& b.getClass().getSimpleName().equals("BlockTransformer")){
					// is a transformer block from Tech Reborn
					OreDictionary.registerOre("transformerEU",b);
					FMLLog.info("%s: registerring block %s as '%s' in the Ore Dictionary", PowerAdvantage.MODID, b.getUnlocalizedName(),"transformerEU");
				}
			}

			converter_tr_electricity = addBlock(new BlockTRConverter(defaultMachineMaterial,defaultMachineHardness,new ConduitType("electricity")),"converter_tr_electricity");
			GameRegistry.registerTileEntity(TileEntityTRConverter.class, PowerAdvantage.MODID+"."+"tr_electricity_converter_tileentity");
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(converter_tr_electricity,1),
					"PSU","transformerEU"));
		}
		initDone = true;
	}
	
	private static Block addBlock(Block block, String name ){
		block.setUnlocalizedName(PowerAdvantage.MODID+"."+name);
		GameRegistry.registerBlock(block, name);
		if((block instanceof BlockFluidBase) == false)block.setCreativeTab(ItemGroups.tab_powerAdvantage);
		allBlocks.put(name, block);
		return block;
	}
	

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(FMLInitializationEvent event){
		for(Map.Entry<String, Block> e : allBlocks.entrySet()){
			String name = e.getKey();
			Block block = e.getValue();
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(net.minecraft.item.Item.getItemFromBlock(block), 0, 
				new ModelResourceLocation(PowerAdvantage.MODID+":"+name, "inventory"));
		}
	}
}
