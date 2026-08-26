package com.godh00d.sf4angel.proxy;

import com.godh00d.sf4angel.handler.AchievementHandler;
import com.godh00d.sf4angel.handler.PlayerJoinHandler;
import com.godh00d.sf4angel.handler.TickHandler;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.knowledge.KnowledgeBase;
import com.godh00d.sf4angel.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void preInit() {
        PacketHandler.init();
    }

    public void init() {
        AngelOracle.init();
        KnowledgeBase.init();
        MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());
        MinecraftForge.EVENT_BUS.register(new AchievementHandler());
        MinecraftForge.EVENT_BUS.register(new TickHandler());
    }

    public void postInit() {
    }
}
