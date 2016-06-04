package cyano.poweradvantage.machines.fluidmachines;

import cyano.poweradvantage.api.fluid.FluidRequest;
import cyano.poweradvantage.api.simple.TileEntitySimpleFluidMachine;
import cyano.poweradvantage.registry.FuelRegistry;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipe;
import cyano.poweradvantage.registry.still.recipe.DistillationRecipeRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

public class StillTileEntity extends TileEntitySimpleFluidMachine {

	
	private final ItemStack[] inventory = new ItemStack[1];
	private final FluidTank inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	private int[] dataFields = new int[6];
	private static final int DATAFIELD_FLUID_ID1 = 0; // index in the dataFields array
	private static final int DATAFIELD_FLUID_VOLUME1 = 1; // index in the dataFields array
	private static final int DATAFIELD_FLUID_ID2 = 2; // index in the dataFields array
	private static final int DATAFIELD_FLUID_VOLUME2 = 3; // index in the dataFields array
	private static final int DATAFIELD_BURNTIME = 4; // index in the dataFields array
	private static final int DATAFIELD_TOTALBURN = 5; // index in the dataFields array
	private static final int speed = 1;
	
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
				if(burnTime <= 0){
					getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()).withProperty(StillBlock.ACTIVE, false));
				}
			} else {
				short fuel = getFuelBurnTime(inventory[0]);
				if( fuel > 0 && (!redstone) && (canDistill())){
					burnTime = fuel;
					totalBurnTime = fuel;
					decrementFuel();
					getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()).withProperty(StillBlock.ACTIVE, true));
				}
			}
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		// used to allow change in blockstate without interrupting the TileEntity or the GUI
		return (oldState.getBlock() != newState.getBlock());
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
		return canDistill(inputTank.getFluid());
	}
	
	private boolean canDistill(FluidStack fluid){
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid(fluid.getFluid());
		if(recipe == null) return false;
		if(recipe.isValidInput(fluid)){
			if(tank.getFluidAmount() <= 0) return true;
			return recipe.isValidOutput(tank.getFluid()) && 
					(tank.getFluidAmount() + recipe.getOutput().amount <= tank.getCapacity());
		} else {
			return false;
		}
	}
	
	private void distill(){
		DistillationRecipe recipe = DistillationRecipeRegistry.getInstance().getDistillationRecipeForFluid(inputTank.getFluid().getFluid());
		FluidStack output = recipe.applyRecipe(inputTank.getFluid(), speed);
		getTank().fill(output, true);
	}
	
	private static short getFuelBurnTime(ItemStack item) {
		return FuelRegistry.getActualBurntimeForItem(item);
	}
	
	private void decrementFuel() {
		inventory[0] = FuelRegistry.decrementFuelItem(inventory[0]);
	}
	

	
	public FluidTank getInputTank(){
		return inputTank;
	}
	
	public boolean isBurning(){
		return burnTime > (short)0;
	}
	
	public float getBurnFraction(){
		return (float)burnTime / (float)totalBurnTime;
	}
	
	///// Overrides to use one tank for input and another tank for output /////
	
	/**
	 * Generates a request for fluid based on what is being offered. 
	 * @param offer The type of fluid being offered by a fluid producer
	 * @return A FluidRequest object representing how much of the offered fluid you want to take. 
	 * If you don't want any, return <code>FluidRequest.REQUEST_NOTHING</code>
	 */
	@Override
	public FluidRequest getFluidRequest(Fluid offer) {
		if(inputTank.getFluidAmount() > 0 && offer.equals(inputTank.getFluid().getFluid())){
			FluidRequest req = new FluidRequest(FluidRequest.MEDIUM_PRIORITY,
					(inputTank.getCapacity() - inputTank.getFluidAmount()),
					this);
			return req;
		} else if(inputTank.getFluidAmount() <= 0 && canDistill(new FluidStack(offer,inputTank.getCapacity()))){
			FluidRequest req = new FluidRequest(FluidRequest.MEDIUM_PRIORITY,
					inputTank.getCapacity(),
					this);
			return req;
		} else {
			return FluidRequest.REQUEST_NOTHING;
		}
	}

	/**
	 * Checks whether a given fluid is appropriate for this machine. For example, a water tank would return fallse for
	 * all Fluids except for <code>FluidRegistry.WATER</code>
	 *
	 * @param fluid The fluid to test
	 * @return True if this machine should accept this fluid type, false to reject it.
	 */
	@Override
	public boolean canAccept(Fluid fluid) {
		return true;
	}


	/**
	 * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
	 * @param from Orientation the Fluid is pumped in from.
	 * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
	 * @param doFill If false, fill will only be simulated.
	 * @return Amount of resource that was (or would have been, if simulated) filled.
	 */
	public int fill(EnumFacing from, FluidStack resource, boolean doFill){
		if(resource == null) return 0;
		if(inputTank.getFluidAmount() <= 0){
			if(canDistill(resource)){
				return inputTank.fill(resource, doFill);
			}
		} else if(inputTank.getFluid().getFluid().equals(resource.getFluid())) {
			return inputTank.fill(resource, doFill);
		}
		return 0;
		
	}
	
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @param fluid The fluid being added/removed
	 */
	@Override
	public boolean canFill(EnumFacing face, Fluid fluid) {
		if(inputTank.getFluid() == null) return canDistill(new FluidStack(fluid,inputTank.getCapacity()));
		return inputTank.getFluidAmount() <= inputTank.getCapacity() && fluid.equals(inputTank.getFluid().getFluid());
	}
	
	
	

	private final FluidTankInfo[] tankInfoArray = new FluidTankInfo[2];
	/**
	 * Implementation of IFluidHandler
	 * @param face Face of the block being polled
	 * @return array of FluidTankInfo describing all of the FluidTanks
	 */
	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing face) {
		tankInfoArray[0] = inputTank.getInfo();
		tankInfoArray[1] = getTank().getInfo();
		return tankInfoArray;
	}
	
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		NBTTagCompound tankTag2 = new NBTTagCompound();
		inputTank.writeToNBT(tankTag2);
		tagRoot.setTag("InputTank", tankTag2);
		tagRoot.setShort("burnTime", this.burnTime);
		tagRoot.setShort("totalBurnTime", this.totalBurnTime);
		return tagRoot;
	}
	
	/**
	 * Handles data saving and loading
	 * @param tagRoot An NBT tag
	 */
	@Override
	public void readFromNBT(final NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		if (tagRoot.hasKey("InputTank")) {
			NBTTagCompound tankTag2 = tagRoot.getCompoundTag("InputTank");
			inputTank.readFromNBT(tankTag2);
			if(tankTag2.hasKey("Empty")){
				// empty the tank if NBT says its empty (not default behavior of Tank.readFromNBT(...) )
				inputTank.setFluid(null);
			}
		}
		if(tagRoot.hasKey("burnTime")){
			this.burnTime = tagRoot.getShort("burnTime");
		}
		if(tagRoot.hasKey("totalBurnTime")){
			this.totalBurnTime = tagRoot.getShort("totalBurnTime");
		}
	}

	
	/**
     * Gets the integer array used to pass synchronization data from the server 
     * to the clients.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code>onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     * @return An int[] that you update to match local variables when 
     * <code>prepareDataFieldsForSync()</code> is called and read from to update 
     * local variables when <code>onDataFieldUpdate()</code> is called.
     */
	public int[] getDataFieldArray() {
		return dataFields;
	}
	/**
     * This method is invoked after receiving an update packet from the server. 
     * At the time that this method is invoked, the array returned by 
     * <code>getDataFieldArray()</code> now holds the updated variable values.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code> onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     */
	public void onDataFieldUpdate() {
		// used for server-to-client sync
		int fluidID1 = dataFields[DATAFIELD_FLUID_ID1];
		int fluidVolume1 = dataFields[DATAFIELD_FLUID_VOLUME1];
		int fluidID2 = dataFields[DATAFIELD_FLUID_ID2];
		int fluidVolume2 = dataFields[DATAFIELD_FLUID_VOLUME2];
		if(fluidVolume1 <= 0){
			inputTank.setFluid(new FluidStack(FluidRegistry.WATER,0));
		} else {
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(fluidID1),fluidVolume1);
			inputTank.setFluid(fs);
		}
		if(fluidVolume2 <= 0){
			getTank().setFluid(new FluidStack(FluidRegistry.WATER,0));
		} else {
			FluidStack fs = new FluidStack(FluidRegistry.getFluid(fluidID2),fluidVolume2);
			getTank().setFluid(fs);
		}
		this.burnTime = (short)dataFields[DATAFIELD_BURNTIME];
		this.totalBurnTime = (short)dataFields[DATAFIELD_TOTALBURN];
	}
	
	/**
     * This method is invoked before sending an update packet to the server. 
     * After this method returns, the array returned by 
     * <code>getDataFieldArray()</code> should hold the updated variable values.
     * <p>
     * Data fields are used for server-client synchronization of specific 
     * variables. When this TileEntity is marked for synchronization, the 
     * server executes the <code>prepareDataFieldsForSync()</code> method and 
     * then transmits the contents of the array returned by 
     * <code>getDataFieldArray()</code> to the clients in an update packet. When 
     * the client receives this packet, it sets the values in the array from 
     * <code>getDataFieldArray()</code> (not executed on the client-side) and 
     * then executes the <code>onDataFieldUpdate()</code> method.
     * </p><p>
     * For this to work, you should store values that you want sync'd in an int 
     * array in the <code>prepareDataFieldsForSync()</code> method and read them 
     * back in the <code> onDataFieldUpdate()</code> method.
     * </p>
     */
	public void prepareDataFieldsForSync(){
		if(inputTank.getFluid() == null || inputTank.getFluidAmount() <= 0){
			dataFields[DATAFIELD_FLUID_ID1] = FluidRegistry.getFluidID(FluidRegistry.WATER);
			dataFields[DATAFIELD_FLUID_VOLUME1] = 0;
		} else {
			dataFields[DATAFIELD_FLUID_ID1] = FluidRegistry.getFluidID(inputTank.getFluid().getFluid());
			dataFields[DATAFIELD_FLUID_VOLUME1] = inputTank.getFluidAmount();
		}
		if(getTank().getFluid() == null || getTank().getFluidAmount() <= 0){
			dataFields[DATAFIELD_FLUID_ID2] = FluidRegistry.getFluidID(FluidRegistry.WATER);
			dataFields[DATAFIELD_FLUID_VOLUME2] = 0;
		} else {
			dataFields[DATAFIELD_FLUID_ID2] = FluidRegistry.getFluidID(getTank().getFluid().getFluid());
			dataFields[DATAFIELD_FLUID_VOLUME2] = getTank().getFluidAmount();
		}
		dataFields[DATAFIELD_BURNTIME]  = this.burnTime;
		dataFields[DATAFIELD_TOTALBURN] = this.totalBurnTime;
	}
	
	///// end of multi-tank overrides /////
	
	public int getRedstoneOutput() {
		return inputTank.getFluidAmount() * 15 / inputTank.getCapacity();
	}



	/**
	 * Checks whether this fluid machine should send out its fluid to other fluid machines
	 *
	 * @return true to send fluids to other machines
	 */
	@Override
	public boolean isFluidSource() {
		return true;
	}

	/**
	 * Checks whether this fluid machine should receive fluids from other fluid machines
	 *
	 * @return true to receive fluids from other machines
	 */
	@Override
	public boolean isFluidSink() {
		return true;
	}
}
