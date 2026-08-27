package com.godh00d.sf4angel.proxy;

import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.knowledge.KnowledgeBase;
import com.godh00d.sf4angel.network.PacketHandler;

public class CommonProxy {

    public void preInit() {
        PacketHandler.init();
    }

    public void init() {
        AngelOracle.init();
        KnowledgeBase.init();
    }

    public void postInit() {
    }
}
