package com.godh00d.sf4angel.typewriter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class TypewriterHandler {

    private static final int CHARS_PER_TICK = 1;
    private static final int SHOW_TICKS = 80;
    private static final int DESPAWN_DELAY_TICKS = 200;

    private static final Map<UUID, PlayerTypewriterState> states = new HashMap<>();

    public static void queueMessage(EntityPlayer player, String message, int delayAfter) {
        queueMessage(player, message, 0, delayAfter);
    }

    public static void queueMessage(EntityPlayer player, String message, int delayBefore, int delayAfter) {
        PlayerTypewriterState state = states.computeIfAbsent(player.getUniqueID(), k -> new PlayerTypewriterState());
        state.queue.add(new TypewriterMessage(message, delayBefore, delayAfter));
        state.despawnDelay = 0;
    }

    public static void queueRedMessage(EntityPlayer player, String message, int delayBefore, int delayAfter) {
        PlayerTypewriterState state = states.computeIfAbsent(player.getUniqueID(), k -> new PlayerTypewriterState());
        state.queue.clear();
        state.currentMessage = new TypewriterMessage(message, delayBefore, delayAfter, TextFormatting.RED);
        state.phase = delayBefore > 0 ? Phase.WAITING_DELAY : Phase.APPEAR;
        state.charIndex = 0;
        state.tickCount = 0;
        state.despawnDelay = 0;
    }

    public static boolean hasActiveMessages(EntityPlayer player) {
        PlayerTypewriterState state = states.get(player.getUniqueID());
        return state != null && (!state.queue.isEmpty() || state.currentMessage != null);
    }

    public static void clearMessages(EntityPlayer player) {
        PlayerTypewriterState state = states.get(player.getUniqueID());
        if (state != null) {
            boolean cancelledDialogue = state.currentMessage != null || !state.queue.isEmpty();
            state.queue.clear();
            state.currentMessage = null;
            state.phase = Phase.IDLE;
            state.tickCount = 0;
            state.charIndex = 0;
            if (state.despawnWhenReady && cancelledDialogue) {
                state.despawnDelay = DESPAWN_DELAY_TICKS;
            }
        }
    }

    public static void tick(EntityPlayer player) {
        if (player.world.isRemote) return;

        PlayerTypewriterState state = states.get(player.getUniqueID());
        if (state == null) return;

        state.tickCount++;

        switch (state.phase) {
            case IDLE:
                if (state.despawnDelay > 0) {
                    state.despawnDelay--;
                    return;
                }
                advanceQueue(state);
                break;

            case WAITING_DELAY:
                state.currentMessage.delayBefore--;
                if (state.currentMessage.delayBefore <= 0) {
                    state.phase = Phase.APPEAR;
                    state.tickCount = 0;
                }
                break;

            case APPEAR:
                state.phase = Phase.TYPING;
                state.tickCount = 0;
                state.blipCooldown = 0;
                state.charIndex = Math.min(CHARS_PER_TICK, state.currentMessage.text.length());
                sendPartialMessage(player, state);
                playSpeechBlip(player, state);
                break;

            case TYPING:
                state.charIndex += CHARS_PER_TICK;
                if (state.charIndex >= state.currentMessage.text.length()) {
                    state.charIndex = state.currentMessage.text.length();
                    state.phase = Phase.SHOWING;
                    state.tickCount = 0;
                }
                sendPartialMessage(player, state);
                playSpeechBlip(player, state);
                break;

            case SHOWING:
                sendPartialMessage(player, state);
                if (state.tickCount >= SHOW_TICKS) {
                    state.phase = Phase.DISAPPEAR;
                    state.tickCount = 0;
                }
                break;

            case DISAPPEAR:
                state.charIndex -= CHARS_PER_TICK;
                if (state.charIndex > 0) {
                    sendPartialMessage(player, state);
                    break;
                }
                state.charIndex = 0;
                sendPartialMessage(player, state);
                clearActionBar(player);
                state.currentMessage = null;
                state.despawnDelay = state.queue.isEmpty() && state.despawnWhenReady ? DESPAWN_DELAY_TICKS : 0;
                state.tickCount = 0;
                state.phase = Phase.IDLE;
                break;
        }
    }

    private static void advanceQueue(PlayerTypewriterState state) {
        if (state.queue.isEmpty()) return;

        TypewriterMessage next = state.queue.peek();
        if (next.delayBefore > 0) {
            next.delayBefore--;
            return;
        }
        state.queue.poll();
        state.currentMessage = next;
        state.phase = Phase.APPEAR;
        state.tickCount = 0;
        state.charIndex = 0;
    }

    private static void sendPartialMessage(EntityPlayer player, PlayerTypewriterState state) {
        if (state.currentMessage == null) return;

        String full = state.currentMessage.text;
        int len = Math.min(state.charIndex, full.length());

        String typed = full.substring(0, Math.max(0, len));
        boolean showCursor = state.phase == Phase.TYPING || state.phase == Phase.APPEAR
            || state.phase == Phase.DISAPPEAR;

        TextComponentString component = new TextComponentString("\u2726 ");
        component.getStyle().setColor(TextFormatting.GOLD);
        TextComponentString text = new TextComponentString(typed);
        text.getStyle().setColor(state.currentMessage.color);
        component.appendSibling(text);
        if (showCursor) {
            TextComponentString cursor = new TextComponentString("_");
            cursor.getStyle().setColor(TextFormatting.DARK_GRAY);
            component.appendSibling(cursor);
        }
        player.sendStatusMessage(component, true);
    }

    private static void clearActionBar(EntityPlayer player) {
        player.sendStatusMessage(new TextComponentString(""), true);
    }

    private static void playSpeechBlip(EntityPlayer player, PlayerTypewriterState state) {
        if (!(player instanceof EntityPlayerMP) || state.currentMessage == null || state.charIndex <= 0) return;
        char character = state.currentMessage.text.charAt(state.charIndex - 1);
        if (!Character.isLetterOrDigit(character)) return;
        if (state.blipCooldown > 0) {
            state.blipCooldown--;
            return;
        }
        int voice = Math.abs(state.currentMessage.text.hashCode() % 7);
        int syllable = Character.toLowerCase(character) % 5;
        float pitch = 1.34F + voice * 0.045F + syllable * 0.025F;
        ((EntityPlayerMP) player).playSound(SoundEvents.BLOCK_NOTE_HARP, 0.06F, pitch);
        state.blipCooldown = 1;
    }

    public static void despawnWhenReady(EntityPlayer player) {
        PlayerTypewriterState state = states.get(player.getUniqueID());
        if (state != null) {
            state.despawnWhenReady = true;
        }
    }

    public static boolean shouldDespawn(EntityPlayer player) {
        PlayerTypewriterState state = states.get(player.getUniqueID());
        return state != null && state.despawnWhenReady && state.queue.isEmpty() && state.currentMessage == null && state.despawnDelay == 0;
    }

    public static void removePlayer(EntityPlayer player) {
        states.remove(player.getUniqueID());
    }

    private static class PlayerTypewriterState {
        final Queue<TypewriterMessage> queue = new LinkedList<>();
        TypewriterMessage currentMessage;
        Phase phase = Phase.IDLE;
        int tickCount = 0;
        int charIndex = 0;
        int blipCooldown = 0;
        boolean despawnWhenReady = false;
        int despawnDelay = 0;
    }

    public static class TypewriterMessage {
        public final String text;
        public int delayBefore;
        public final int delayAfter;
        public final TextFormatting color;

        public TypewriterMessage(String text, int delayBefore, int delayAfter) {
            this(text, delayBefore, delayAfter, TextFormatting.WHITE);
        }

        public TypewriterMessage(String text, int delayBefore, int delayAfter, TextFormatting color) {
            this.text = text;
            this.delayBefore = delayBefore;
            this.delayAfter = delayAfter;
            this.color = color;
        }
    }

    public enum Phase {
        IDLE, WAITING_DELAY, APPEAR, TYPING, SHOWING, DISAPPEAR
    }
}
