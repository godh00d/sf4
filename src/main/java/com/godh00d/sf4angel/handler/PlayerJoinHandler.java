package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class PlayerJoinHandler {

    private static final Set<UUID> greetedPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;
        EntityPlayerMP mp = (EntityPlayerMP) player;

        clearPlayerInventory(mp);

        AchievementHandler.onPlayerJoin(player.getUniqueID());

        if (!greetedPlayers.contains(player.getUniqueID())) {
            greetedPlayers.add(player.getUniqueID());
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
        clearPlayerInventory(mp);
        respawnAngel(mp);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TypewriterHandler.removePlayer(event.player);
    }

    private static void clearPlayerInventory(EntityPlayerMP player) {
        player.inventory.clear();
        player.inventory.markDirty();
    }

    private static void spawnFreshAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        double yaw = Math.toRadians(player.rotationYaw);
        angel.setPosition(
            player.posX - Math.sin(yaw) * 6.0D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 6.0D
        );
        world.spawnEntity(angel);

        String[] intro = AngelPersonality.getFirstLoginIntro();
        String nextGoal = AngelOracle.getNextGoal(player);

        for (int i = 0; i < intro.length; i++) {
            String line = intro[i];
            if (line.contains("{NEXT_GOAL}")) {
                line = nextGoal != null ? "Your first task: " + nextGoal : "Your first task: The Descent of Dirt.";
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
            player.posX - Math.sin(yaw) * 6.0D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 6.0D
        );
        world.spawnEntity(angel);

        String nextGoal = AngelOracle.getNextGoal(player);
        String welcomeBack = "Welcome back. The sky missed you.";
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
            player.posX - Math.sin(yaw) * 6.0D,
            player.posY + player.getEyeHeight() - 0.5,
            player.posZ + Math.cos(yaw) * 6.0D
        );
        world.spawnEntity(angel);

        String deathLine = AngelPersonality.getRandomDeathLine();
        String nextGoal = AngelOracle.getNextGoal(player);
        if (nextGoal != null && !nextGoal.isEmpty()) {
            deathLine += " Next goal: " + nextGoal;
        }

        TypewriterHandler.queueMessage(player, deathLine, 0, 0);
        TypewriterHandler.despawnWhenReady(player);
    }
}
