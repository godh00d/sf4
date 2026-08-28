package com.godh00d.sf4angel.network;

import com.godh00d.sf4angel.SF4Angel;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAngelState implements IMessage {

    private int entityId;
    private int visualState;
    private int animationType;
    private int stateTimer;

    public MessageAngelState() {}

    public MessageAngelState(int entityId, int visualState, int animationType, int stateTimer) {
        this.entityId = entityId;
        this.visualState = visualState;
        this.animationType = animationType;
        this.stateTimer = stateTimer;
    }

    public int getEntityId() { return entityId; }
    public int getVisualState() { return visualState; }
    public int getAnimationType() { return animationType; }
    public int getStateTimer() { return stateTimer; }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        visualState = buf.readInt();
        animationType = buf.readInt();
        stateTimer = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(visualState);
        buf.writeInt(animationType);
        buf.writeInt(stateTimer);
    }

    public static class Handler implements IMessageHandler<MessageAngelState, IMessage> {
        @Override
        public IMessage onMessage(MessageAngelState message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler)
                .addScheduledTask(() -> SF4Angel.proxy.handleAngelState(message));
            return null;
        }
    }
}
