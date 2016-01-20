package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.simple.TileEntitySimpleFluidSource;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipe;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

public class StillTileEntity extends TileEntitySimpleFluidSource{

	
	private final ItemStack[] inventory = new ItemStack[1];
	private final FluidTank inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	
	private boolean redstone = true;

	
	private short burnTime = 0;
	private short totalBurnTime = 0;
	
	
	public StillTileEntity() {
		super(FluidContainerRegistry.BUCKET_VOLUME, StillTileEntity.class.getName());
	}

	@Override
	protected ItemStack[] getInventory() {
		return inventory;
	}

	@Override
	public void tickUpdate(boolean isServerWorld) {
		if(isServerWorld){
			if(burnTime > 0){
				burnTime--;
				if(canDistill()){
					distill();
				}
			} else {
				short fuel = getFuelBurnTime(inventory[0]);
				if( fuel > 0 && (!redstone) && (canDistill())){
					burnTime = fuel;
					totalBurnTime = fuel;
					decrementFuel();
				}
			}
		}
	}
	
	private int oldInput = 0;
	private int oldOutput = 0;
	private short oldBurntime = 0;
	@Override
	public void powerUpdate(){
		super.powerUpdate();
		
		boolean updateFlag = false;
		
		if(oldInput != this.inputTank.getFluidAmount()){
			updateFlag = true;
			oldInput = this.inputTank.getFluidAmount();
		}
		if(oldOutput != this.tank.getFluidAmount()){
			updateFlag = true;
			oldOutput = this.tank.getFluidAmount();
		}
		if(oldBurntime != this.burnTime){
			updateFlag = true;
			oldBurntime = this.burnTime;
		}
		
		
		
		if(updateFlag ){
			this.sync();
		}
		redstone = getWorld().isBlockPowered(getPos());
	}
	
	private boolean canDistill(){
		if(inputTank.getFluidAmount() <= 0) return false;
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid(inputTank.getFluid().getFluid());
		if(recipe == null) return false;
		if(recipe.isValidInput(inputTank.getFluid())){
			if(tank.getFluidAmount() <= 0) return true;
			return recipe.isValidOutput(tank.getFluid()) && 
					(tank.getFluidAmount() + recipe.getOutput().amount <= tank.getCapacity());
		} else {
			return false;
		}
	}
	
	private void distill(){
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid(inputTank.getFluid().getFluid());
		if(tank.getFluidAmount() <= 0){
			tank.setFluid(recipe.getOutput());
		} else {
			tank.fill(recipe.getOutput(), true);
		}
	}
	
	private static short getFuelBurnTime(ItemStack item) {
		if(item == null) return 0;
		return (short)TileEntityFurnace.getItemBurnTime(item);
	}
	
	private void decrementFuel() {
		if(inventory[0].stackSize == 1 && inventory[0].getItem().getContainerItem(inventory[0]) != null){
			inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
		} else {
			this.decrStackSize(0, 1);
		}
	}
	
	// TODO: multi-tank fluid machine

}
