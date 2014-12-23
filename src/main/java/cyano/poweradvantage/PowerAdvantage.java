package cyano.poweradvantage;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = PowerAdvantage.MODID, version = PowerAdvantage.VERSION, name=PowerAdvantage.NAME)
public class PowerAdvantage
{
    public static final String MODID = "poweradvantge";
    public static final String NAME = "Power Advantage";
    public static final String VERSION = "0.0.1";

    
    Tester test = new Tester();
    
    @SidedProxy(clientSide="cyano.poweradvantage.ClientProxy", serverSide="cyano.poweradvantage.ServerProxy")
    public static Proxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	config.load();
    	// TODO
    	test.preInit(event, proxy, config);
    	
    	config.save();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// TODO
    	test.init(event, proxy);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	// TODO
    	test.postInit(event, proxy);
    }
    
}
