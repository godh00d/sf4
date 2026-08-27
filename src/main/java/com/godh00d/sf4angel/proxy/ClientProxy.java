package com.godh00d.sf4angel.proxy;

import com.godh00d.sf4angel.entity.EntityAngelRender;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientProxy extends CommonProxy {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(
            com.godh00d.sf4angel.entity.EntityAngel.class,
            EntityAngelRender::new
        );
        LOGGER.info("Registered EntityAngelRender during preInit");
    }

    @Override
    public void init() {
        super.init();
    }
}
