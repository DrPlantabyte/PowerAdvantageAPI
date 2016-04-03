package cyano.poweradvantage.api.simple;

import cyano.poweradvantage.init.Fluids;
import net.minecraft.block.material.Material;

/**
 * This block class provides all of the standard code for creating a machine 
 * block with an inventory and user interface that gives fluids to connected 
 * fluid consumers.<br>
 * Example usage:<br><pre>
int guiID = cyano.poweradvantage.registry.MachineGUIRegistry.addGUI(new MySimpleMachineGUI());
Block myMachineBlock = new MyBlockSimpleFluidSource(guiID,PowerAdvantage.getInstance());
myMachineBlock.setUnlocalizedName(MODID+"."+"my_machine");
GameRegistry.registerBlock(myMachineBlock,"my_machine");
 * </pre>
 * @author DrCyano
 *
 */
public abstract class BlockSimpleFluidMachine extends BlockSimplePowerMachine {
	/**
     * Standard constructor for a machine block. 
     * @param blockMaterial This is the material for the block. Typically is set 
	 * to net.minecraft.block.material.Material.piston, though any material can 
	 * be used.
     * @param hardness This affects how long it takes to break the block. 0.5 is 
	 * a good value if you want it to be easy to break.
     */
	public BlockSimpleFluidMachine(Material blockMaterial, float hardness) {
		super( blockMaterial,  hardness, Fluids.fluidConduit_general);
	}

}
