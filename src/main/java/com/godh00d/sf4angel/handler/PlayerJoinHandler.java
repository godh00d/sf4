package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.constellation.ConstellationManager;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;
        EntityPlayerMP mp = (EntityPlayerMP) player;
        if (ConstellationManager.hasActiveSession(mp)) return;

        removeExistingAngels(mp);

        if (AchievementHandler.getRecordedAngelAppearances(player) == 0) {
            spawnFreshAngel(mp);
        } else {
            spawnReturningAngel(mp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;

        EntityPlayerMP mp = (EntityPlayerMP) player;
        if (ConstellationManager.hasActiveSession(mp)) return;
        removeExistingAngels(mp);
        respawnAngel(mp);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TypewriterHandler.removePlayer(event.player);
        TickHandler.removePlayer(event.player.getUniqueID());
    }

    private static void removeExistingAngels(EntityPlayerMP player) {
        for (EntityAngel angel : player.world.getEntities(EntityAngel.class, entity -> true)) {
            UUID owner = angel.getOwnerId();
            if (owner != null && owner.equals(player.getUniqueID())) {
                angel.setDead();
            }
        }
    }

    private static void spawnFreshAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        double yaw = Math.toRadians(player.rotationYaw);
        angel.setPosition(
            player.posX - Math.sin(yaw) * 1.8D + Math.cos(yaw) * 1.45D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 1.8D + Math.sin(yaw) * 1.45D
        );
        world.spawnEntity(angel);
        AchievementHandler.recordAngelAppearance(player);

        String[] intro = AngelPersonality.getFirstLoginIntro();
        String nextGoal = AchievementHandler.getNextGoal(player);

        for (int i = 0; i < intro.length; i++) {
            String line = intro[i];
            if (line.contains("{NEXT_GOAL}")) {
                line = nextGoal != null ? "Your first task: " + nextGoal : "The sky awaits your first move.";
            }
            int delay = (i == 0) ? 0 : 80;
            TypewriterHandler.queueMessage(player, line, delay, 0);
        }

        TypewriterHandler.despawnWhenReady(player);
    }

    private static void spawnReturningAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        double yaw = Math.toRadians(player.rotationYaw);
        angel.setPosition(
            player.posX - Math.sin(yaw) * 1.8D + Math.cos(yaw) * 1.45D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 1.8D + Math.sin(yaw) * 1.45D
        );
        world.spawnEntity(angel);
        AchievementHandler.recordAngelAppearance(player);

        String nextGoal = AchievementHandler.getNextGoal(player);
        String welcomeBack = AngelPersonality.getRandomWelcomeBack();
        if (nextGoal != null && !nextGoal.isEmpty()) {
            welcomeBack += " Next goal: " + nextGoal;
        }

        TypewriterHandler.queueMessage(player, welcomeBack, 0, 0);
        TypewriterHandler.despawnWhenReady(player);
    }

    private static void respawnAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        double yaw = Math.toRadians(player.rotationYaw);
        angel.setPosition(
            player.posX - Math.sin(yaw) * 1.8D + Math.cos(yaw) * 1.45D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 1.8D + Math.sin(yaw) * 1.45D
        );
        world.spawnEntity(angel);
        AchievementHandler.recordAngelAppearance(player);

        String deathLine = AngelPersonality.getRandomDeathLine();
        String nextGoal = AchievementHandler.getNextGoal(player);
        if (nextGoal != null && !nextGoal.isEmpty()) {
            deathLine += " Next goal: " + nextGoal;
        }

        TypewriterHandler.queueMessage(player, deathLine, 0, 0);
        TypewriterHandler.despawnWhenReady(player);
    }
}
