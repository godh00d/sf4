package com.godh00d.sf4angel.network;

import com.godh00d.sf4angel.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE =
        NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    private static int packetId = 0;

    public static void init() {
        INSTANCE.registerMessage(MessageAngelState.Handler.class, MessageAngelState.class, packetId++, Side.CLIENT);
        INSTANCE.registerMessage(MessageTypewriter.Handler.class, MessageTypewriter.class, packetId++, Side.CLIENT);
        INSTANCE.registerMessage(MessageConstellationProgress.Handler.class,
            MessageConstellationProgress.class, packetId++, Side.CLIENT);
    }
}
