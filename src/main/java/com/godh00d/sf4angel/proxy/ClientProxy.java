package com.godh00d.sf4angel.proxy;

import com.godh00d.sf4angel.entity.EntityAngelRender;
import com.godh00d.sf4angel.client.ConstellationClientState;
import com.godh00d.sf4angel.entity.EntityConstellationObservatory;
import com.godh00d.sf4angel.entity.RenderConstellationObservatory;
import com.godh00d.sf4angel.network.MessageConstellationProgress;
import com.godh00d.sf4angel.network.MessageAngelState;
import com.godh00d.sf4angel.entity.EntityAngel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
        RenderingRegistry.registerEntityRenderingHandler(
            EntityConstellationObservatory.class,
            RenderConstellationObservatory::new
        );
        LOGGER.info("Registered EntityAngelRender during preInit");
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void handleConstellationProgress(MessageConstellationProgress message) {
        ConstellationClientState.accept(message);
    }

    @Override
    public void handleAngelState(MessageAngelState message) {
        if (Minecraft.getMinecraft().world == null) return;
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityId());
        if (!(entity instanceof EntityAngel)) return;
        EntityAngel angel = (EntityAngel) entity;
        angel.setVisualState(message.getVisualState());
        angel.setAnimationType(message.getAnimationType());
        angel.setStateTimer(message.getStateTimer());
    }
}
