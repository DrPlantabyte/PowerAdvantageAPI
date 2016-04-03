package cyano.poweradvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class TreasureChests {

	private static boolean initDone = false;
	public static void init(Path configFolder){
		if(initDone)return;

		Path chestFolder = configFolder.resolve(Paths.get("additional-loot-tables",PowerAdvantage.MODID,"chests"));
		writeLootFile(chestFolder.resolve("abandoned_mineshaft.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("simple_dungeon.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("village_blacksmith.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("stronghold_corridor.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("stronghold_crossing.json"), LOOT_POOL);
		
		initDone = true;
	}

	private static void writeLootFile(Path file, String content){
		try {
			Files.write(file, Arrays.asList(content), Charset.forName("UTF-8"));
		} catch (IOException e) {
			FMLLog.log(Level.ERROR,e,"Error writing additional-loot-table files");
		}
	}

	static final String LOOT_POOL = "{\n" +
			"    \"pools\": [\n" +
			"        {\n" +
			"            \"__comment\":\"25% chance of a Power Advantage item\",\n" +
			"            \"rolls\": 1,\n" +
			"            \"entries\": [\n" +
			"                {\n" +
			"                    \"type\": \"empty\",\n" +
			"                    \"weight\": 60\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:sprocket\",\n" +
			"                    \"weight\": 5,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 2,\n" +
			"                                \"max\": 6\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:fluid_pipe\",\n" +
			"                    \"weight\": 5,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 4\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:fluid_storage_tank\",\n" +
			"                    \"weight\": 3\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:fluid_drain\",\n" +
			"                    \"weight\": 2\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:fluid_discharge\",\n" +
			"                    \"weight\": 2\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"poweradvantage:item_conveyor\",\n" +
			"                    \"weight\": 3,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 5\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                }\n" +
			"            ]\n" +
			"        }\n" +
			"    ]\n" +
			"}\n";
}
