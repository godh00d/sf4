package com.godh00d.sf4angel.network;

import com.godh00d.sf4angel.SF4Angel;
import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class MessageConstellationProgress implements IMessage {

    private int catalogCount;
    private String catalogHash;
    private byte[] states;

    public MessageConstellationProgress() {
    }

    public MessageConstellationProgress(int catalogCount, String catalogHash, byte[] states) {
        validate(catalogCount, catalogHash, states);
        this.catalogCount = catalogCount;
        this.catalogHash = catalogHash;
        this.states = states;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        require(buf, 3, "catalog header");
        catalogCount = buf.readUnsignedShort();
        int hashLength = buf.readUnsignedByte();
        if (catalogCount != AchievementConstellationCatalog.COUNT) {
            throw new DecoderException("Invalid constellation catalog count: " + catalogCount);
        }
        if (hashLength != 64) throw new DecoderException("Invalid constellation hash length: " + hashLength);
        require(buf, 66, "catalog hash and state count");
        byte[] hash = new byte[hashLength];
        buf.readBytes(hash);
        for (byte value : hash) {
            int character = value & 0xFF;
            if (!((character >= '0' && character <= '9') || (character >= 'a' && character <= 'f'))) {
                throw new DecoderException("Constellation hash is not lowercase ASCII hexadecimal");
            }
        }
        catalogHash = new String(hash, StandardCharsets.US_ASCII);
        int stateCount = buf.readUnsignedShort();
        if (stateCount != 0 && stateCount != AchievementConstellationCatalog.COUNT) {
            throw new DecoderException("Invalid constellation state count: " + stateCount);
        }
        require(buf, stateCount, "constellation states");
        states = new byte[stateCount];
        buf.readBytes(states);
        for (byte state : states) {
            if (state < 0 || state > 3) throw new DecoderException("Invalid constellation state: " + state);
        }
        if (buf.isReadable()) throw new DecoderException("Trailing constellation packet data");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        validate(catalogCount, catalogHash, states);
        byte[] hash = catalogHash.getBytes(StandardCharsets.US_ASCII);
        buf.writeShort(catalogCount);
        buf.writeByte(hash.length);
        buf.writeBytes(hash);
        buf.writeShort(states.length);
        buf.writeBytes(states);
    }

    public int getCatalogCount() {
        return catalogCount;
    }

    public String getCatalogHash() {
        return catalogHash;
    }

    public byte[] getStates() {
        return states;
    }

    private static void require(ByteBuf buf, int count, String field) {
        if (buf.readableBytes() < count) throw new DecoderException("Truncated " + field);
    }

    private static void validate(int count, String hash, byte[] values) {
        if (count != AchievementConstellationCatalog.COUNT || hash == null || hash.length() != 64
            || values == null || (values.length != 0 && values.length != AchievementConstellationCatalog.COUNT)) {
            throw new IllegalArgumentException("Invalid constellation snapshot shape");
        }
        for (int i = 0; i < hash.length(); i++) {
            char character = hash.charAt(i);
            if (!((character >= '0' && character <= '9') || (character >= 'a' && character <= 'f'))) {
                throw new IllegalArgumentException("Invalid constellation snapshot hash");
            }
        }
        for (byte value : values) {
            if (value < 0 || value > 3) throw new IllegalArgumentException("Invalid constellation snapshot state");
        }
    }

    public static class Handler implements IMessageHandler<MessageConstellationProgress, IMessage> {
        @Override
        public IMessage onMessage(MessageConstellationProgress message, MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler)
                .addScheduledTask(() -> SF4Angel.proxy.handleConstellationProgress(message));
            return null;
        }
    }
}
