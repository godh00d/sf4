package com.godh00d.sf4angel.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTypewriter implements IMessage {

    private String text;
    private int phase;

    public MessageTypewriter() {}

    public MessageTypewriter(String text, int phase) {
        this.text = text;
        this.phase = phase;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf);
        phase = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
        buf.writeInt(phase);
    }

    public String getText() { return text; }
    public int getPhase() { return phase; }

    public static class Handler implements IMessageHandler<MessageTypewriter, IMessage> {
        @Override
        public IMessage onMessage(MessageTypewriter message, MessageContext ctx) {
            return null;
        }
    }
}
