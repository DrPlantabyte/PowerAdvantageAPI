package cyano.poweradvantage.api.simple;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.PoweredEntity;
import cyano.poweradvantage.init.Fluids;
/**
 * This block class provides all of the standard code for creating a machine 
 * block with an inventory and user interface that receives fluids connected 
 * fluid sources.<br>
 * Example usage:<br><pre>
int guiID = cyano.poweradvantage.registry.MachineGUIRegistry.addGUI(new MySimpleMachineGUI());
Block myMachineBlock = new MyBlockSimpleFluidConsumer(guiID,PowerAdvantage.getInstance());
myMachineBlock.setUnlocalizedName(MODID+"."+"my_machine");
GameRegistry.registerBlock(myMachineBlock,"my_machine");
 * </pre>
 * @author DrCyano
 *
 */
public abstract class BlockSimpleFluidConsumer extends BlockSimplePowerConsumer {
	/**
     * Standard constructor for a machine block. 
     * @param blockMaterial This is the material for the block. Typically is set 
	 * to net.minecraft.block.material.Material.piston, though any material can 
	 * be used.
     * @param hardness This affects how long it takes to break the block. 0.5 is 
	 * a good value if you want it to be easy to break.
     * @param guiHandlerID This is the numerical ID in the GUI registry, used by 
     * the Forge API to show a custom GUI when you right-click on this block. 
     * For the sake of simplicity, PowerAdvatageAPI has a GUI registry that will 
     * automatically handle all of the details for you and give you a number to 
     * use. To use this registry, call 
     * <code>int guiID = cyano.poweradvantage.registry.MachineGUIRegistry.addGUI(new SimpleMachineGUI(...));</code>
     * and then use the returned guiID number as the guiHandlerID in this 
     * consructor.
     * @param ownerOfGUIHandler This is the object instance that is registered 
     * with Forge's GUI handler registry. If you don't have a GUI for this 
     * block, make this value null. If you used the MachineGUIRegistry from 
     * PowerAdvantageAPI, then this parameter should have the value 
     * <code>cyano.poweradvantage.PowerAdvantage.getInstance()</code>
     */
	public BlockSimpleFluidConsumer(Material blockMaterial, float hardness, int guiHandlerID, Object ownerOfGUIHandler) {
		super( blockMaterial,  hardness, Fluids.fluidConduit_general,  guiHandlerID,  ownerOfGUIHandler);
	}

}
