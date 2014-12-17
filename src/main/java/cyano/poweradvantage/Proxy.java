package cyano.poweradvantage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class Proxy {
	@EventHandler
    public abstract void preInit(FMLPreInitializationEvent event, Configuration config);
    
    @EventHandler
    public abstract void init(FMLInitializationEvent event);
    
    @EventHandler
    public abstract void postInit(FMLPostInitializationEvent event);
}
