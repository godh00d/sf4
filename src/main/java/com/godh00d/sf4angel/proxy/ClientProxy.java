package com.godh00d.sf4angel.proxy;

import com.godh00d.sf4angel.entity.EntityAngelRender;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        RenderingRegistry.registerEntityRenderingHandler(com.godh00d.sf4angel.entity.EntityAngel.class, EntityAngelRender::new);
    }
}
