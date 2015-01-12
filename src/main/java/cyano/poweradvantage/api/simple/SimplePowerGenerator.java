package cyano.poweradvantage.api.simple;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cyano.poweradvantage.api.ConductorType;
import cyano.poweradvantage.api.ITypedConductor;
import cyano.poweradvantage.api.PowerSourceEntity;
import cyano.poweradvantage.api.simple.util.SimpleMachineGUI;
import cyano.poweradvantage.math.Integer2D;
import cyano.poweradvantage.registry.MachineGUIRegistry;

/**
 * 
 * @author DrCyano
 *
 */
public /*abstract*/class SimplePowerGenerator { // TODO: make abstract
	// Block data
	protected final Material blockMaterial;
	protected final float blockHardness;
	protected final String blockName_inactive; // unlocalized name
	protected final String blockName_active; // unlocalized name
	// Entity data
	protected final ConductorType energyType;
	protected final String entityName; // unlocalized name
	// GUI data
	protected final ResourceLocation guiTexture;
	protected final Integer2D[] inventorySlotCoordinates;
	protected final int guiID;
	protected final SimpleMachineGUI guiInstance;
	// Instances
	protected final MachineBlock inactiveBlock;
	protected final MachineBlock activeBlock;
	
	// TODO: change things around a little so that the programmer is extending the TileEntity class 
	// (because the Forge registry registers the TileEntity class rather than an object instance) 
	
	public SimplePowerGenerator(ConductorType energyType, String unlocalizedBlockName_base, float blockHardness, ResourceLocation guiTexture, Integer2D[] inventorySlotCoordinates){
		this.blockMaterial = Material.piston;
		this.blockHardness = blockHardness;
		this.blockName_inactive = unlocalizedBlockName_base;
		this.blockName_active = unlocalizedBlockName_base + "_active";
		this.entityName = unlocalizedBlockName_base+"_entity";
		this.energyType = energyType;
		this.guiTexture = guiTexture;
		this.inventorySlotCoordinates = inventorySlotCoordinates;
		this.guiInstance = new SimpleMachineGUI(guiTexture,inventorySlotCoordinates);
		this.guiID = MachineGUIRegistry.addGUI(guiInstance);
	}
	
	public String getInactiveBlockUnlocalizedName(){
		return blockName_inactive;
	}
	
	public String getActiveBlockUnlocalizedName(){
		return blockName_active;
	}
	public String getTileEntityUnlocalizedName(){
		return entityName;
	}
	
	public Block getInactiveBlock(){
		// TODO: implementation
	}
	public Block getActiveBlock(){
		// TODO: implementation
	}
	public MachineEntity createTileEntity(World world, int i){
		// TODO: implementation
	}
	
	public /*abstract*/ void tickUpdate(boolean isServerWorld) {
		// TODO: make abstract
		// TODO: implement active/inactive logic.
	}
	
	public /*abstract*/ int[] getInventorySlotsForFace(EnumFacing side) {
		// TODO: make abstract
	}
	
    public /*abstract*/ boolean canExtractItem(final int slot, final ItemStack targetItem, final EnumFacing side) {
    	// TODO: make abstract
    }

    public /*abstract*/ boolean canInsertItem(final int slot, final ItemStack srcItem, final EnumFacing side) {
    	// TODO: make abstract
    }
	
    
    public class MachineBlock extends BlockContainer implements ITypedConductor {

		protected MachineBlock(boolean isActive, Material mat) {
			super(mat);
			// TODO constructor initialization
		}
		
		// TODO: implementation
    	
    }
    
}
