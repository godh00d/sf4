package com.godh00d.sf4angel.sound;

import com.godh00d.sf4angel.entity.EntityAngel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AngelSoundscape {

    private static final Map<UUID, List<Cue>> CUES = new HashMap<>();

    private AngelSoundscape() {
    }

    public static void playAppearance(EntityPlayerMP player, int animation) {
        if (animation == EntityAngel.ANIM_SMOKE) {
            cue(player, 0, SoundEvents.ENTITY_FIREWORK_BLAST, 0.18F, 0.68F);
            cue(player, 4, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.28F, 1.18F);
            cue(player, 8, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.24F, 1.72F);
        } else if (animation == EntityAngel.ANIM_SPIN) {
            cue(player, 0, SoundEvents.ENTITY_FIREWORK_LAUNCH, 0.16F, 1.30F);
            cue(player, 4, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.22F, 1.46F);
            cue(player, 9, SoundEvents.BLOCK_NOTE_HARP, 0.24F, 1.78F);
        } else {
            cue(player, 0, SoundEvents.ENTITY_ENDEREYE_LAUNCH, 0.14F, 0.72F);
            cue(player, 7, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.26F, 0.88F);
            cue(player, 13, SoundEvents.BLOCK_NOTE_HARP, 0.24F, 1.48F);
        }
    }

    public static void playDeparture(EntityPlayerMP player, int animation) {
        float openingPitch = animation == EntityAngel.ANIM_SPIN ? 1.62F
            : animation == EntityAngel.ANIM_SMOKE ? 1.32F : 1.48F;
        cue(player, 0, SoundEvents.BLOCK_NOTE_HARP, 0.20F, openingPitch);
        cue(player, 4, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.22F, 0.72F);
        cue(player, 9, SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.12F, 1.38F);
    }

    public static void playAchievement(EntityPlayerMP player, boolean finale) {
        cue(player, 9, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, finale ? 0.34F : 0.24F, 1.22F);
        cue(player, 13, SoundEvents.BLOCK_NOTE_HARP, finale ? 0.40F : 0.28F, 1.50F);
        cue(player, 18, finale ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.BLOCK_NOTE_HARP,
            finale ? 0.48F : 0.26F, finale ? 0.86F : 1.88F);
    }

    public static void playObservatoryEntry(EntityPlayerMP player) {
        cue(player, 0, SoundEvents.ENTITY_ENDEREYE_LAUNCH, 0.16F, 0.58F);
        cue(player, 5, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.30F, 0.72F);
        cue(player, 11, SoundEvents.BLOCK_NOTE_HARP, 0.26F, 1.42F);
    }

    public static void playObservatoryExit(EntityPlayerMP player) {
        cue(player, 0, SoundEvents.BLOCK_NOTE_HARP, 0.22F, 1.42F);
        cue(player, 4, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.24F, 0.66F);
        cue(player, 8, SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.12F, 1.28F);
    }

    public static void playHealthWarning(EntityPlayerMP player) {
        cue(player, 0, SoundEvents.BLOCK_NOTE_BASS, 0.30F, 0.62F);
        cue(player, 5, SoundEvents.BLOCK_NOTE_BASS, 0.22F, 0.52F);
    }

    public static void playIrritation(EntityPlayerMP player) {
        cue(player, 0, SoundEvents.ENTITY_ENDEREYE_LAUNCH, 0.24F, 0.48F);
    }

    public static void tick(EntityPlayerMP player) {
        List<Cue> cues = CUES.get(player.getUniqueID());
        if (cues == null) return;
        Iterator<Cue> iterator = cues.iterator();
        while (iterator.hasNext()) {
            Cue cue = iterator.next();
            if (cue.delay-- > 0) continue;
            player.playSound(cue.sound, cue.volume, cue.pitch);
            iterator.remove();
        }
        if (cues.isEmpty()) CUES.remove(player.getUniqueID());
    }

    public static void removePlayer(UUID playerId) {
        CUES.remove(playerId);
    }

    private static void cue(EntityPlayerMP player, int delay, SoundEvent sound, float volume, float pitch) {
        CUES.computeIfAbsent(player.getUniqueID(), ignored -> new ArrayList<>())
            .add(new Cue(delay, sound, volume, pitch));
    }

    private static final class Cue {
        private int delay;
        private final SoundEvent sound;
        private final float volume;
        private final float pitch;

        private Cue(int delay, SoundEvent sound, float volume, float pitch) {
            this.delay = delay;
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
    }
}
