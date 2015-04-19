package cyano.poweradvantage.data;

import cyano.poweradvantage.conduitnetwork.ConduitRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;


public class PowerAdvantageWorldSaveData extends net.minecraft.world.WorldSavedData {

	public static final String KEY = "PowerAdvantage";
	
	public static PowerAdvantageWorldSaveData getDataForWorld(World w){
		 MapStorage storage = w.getPerWorldStorage();
		 PowerAdvantageWorldSaveData result = (PowerAdvantageWorldSaveData)storage.loadData(PowerAdvantageWorldSaveData.class, KEY);
	      if (result == null) {
	         result = new PowerAdvantageWorldSaveData(KEY);
	         storage.setData(KEY, result);
	      }
	      return result;
	}
	
	public PowerAdvantageWorldSaveData(String key) {
		super(key);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound root) {
		ConduitRegistry.getInstance().loadFromNBT(root);
	}

	@Override
	public void writeToNBT(NBTTagCompound root) {
		ConduitRegistry.getInstance().saveToNBT(root);
	}

}
