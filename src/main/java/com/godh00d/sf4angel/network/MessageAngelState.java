package com.godh00d.sf4angel.network;

import com.godh00d.sf4angel.entity.EntityAngel;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
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
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;
                Entity entity = world.getEntityByID(message.entityId);
                if (entity instanceof EntityAngel) {
                    EntityAngel angel = (EntityAngel) entity;
                    angel.setVisualState(message.visualState);
                    angel.setAnimationType(message.animationType);
                    angel.setStateTimer(message.stateTimer);
                }
            });
            return null;
        }
    }
}
