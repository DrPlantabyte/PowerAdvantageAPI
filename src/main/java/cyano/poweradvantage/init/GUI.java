package cyano.poweradvantage.init;

import net.minecraft.util.ResourceLocation;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.gui.FluidTankGUI;
import cyano.poweradvantage.math.Integer2D;
import cyano.poweradvantage.registry.MachineGUIRegistry;

public abstract class GUI {


	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();

		Blocks.fluid_drain.setGuiID(MachineGUIRegistry.addGUI(new FluidTankGUI(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_drain_gui.png"))),PowerAdvantage.getInstance());
		
		Blocks.fluid_discharge.setGuiID(MachineGUIRegistry.addGUI(new FluidTankGUI(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_discharge_gui.png"))),PowerAdvantage.getInstance());

		Blocks.storage_tank.setGuiID(MachineGUIRegistry.addGUI(new FluidTankGUI(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_storage_tank_gui.png"))),PowerAdvantage.getInstance());
		
		Blocks.metal_storage_tank.setGuiID(MachineGUIRegistry.addGUI(new FluidTankGUI(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_metal_tank_gui.png"))),PowerAdvantage.getInstance());

		Blocks.item_conveyor.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor.png",
				Integer2D.fromCoordinates(80,31))),PowerAdvantage.getInstance());
		Blocks.item_filter_block.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_block.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_food.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_food.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_fuel.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_fuel.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_inventory.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_inventory.png",
				Integer2D.fromCoordinates(80,12, 8,56, 26,56, 44,56, 62,56, 80,56, 98,56, 116,56, 134,56, 152,56 ))),PowerAdvantage.getInstance());
		Blocks.item_filter_ore.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_ore.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_plant.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_plant.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_smelt.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_smelt.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		Blocks.item_filter_overflow.setGuiID(MachineGUIRegistry.addGUI(new SimpleMachineGUI(
				PowerAdvantage.MODID+":textures/gui/container/item_conveyor_overflow.png",
				Integer2D.fromCoordinates(80,12))),PowerAdvantage.getInstance());
		
		initDone = true;
	}
}
