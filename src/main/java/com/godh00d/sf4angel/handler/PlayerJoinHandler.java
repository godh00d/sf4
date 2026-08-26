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

        AchievementHandler.onPlayerJoin(player.getUniqueID());

        if (!greetedPlayers.contains(player.getUniqueID())) {
            greetedPlayers.add(player.getUniqueID());
            spawnFreshAngel(mp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;
        if (!event.wasDeath) return;

        EntityPlayerMP mp = (EntityPlayerMP) player;
        respawnAngel(mp);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TypewriterHandler.removePlayer(event.player);
    }

    private static void spawnFreshAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        angel.setPosition(player.posX, player.posY + 5, player.posZ);
        world.spawnEntity(angel);

        String[] intro = AngelPersonality.getFirstLoginIntro();
        String goalHint = AngelOracle.getGoalAndHint(player);

        for (int i = 0; i < intro.length; i++) {
            String line = intro[i];
            if (line.contains("{GOAL_AND_HINT}")) {
                line = goalHint;
            }
            int delay = (i == 0) ? 0 : 80;
            TypewriterHandler.queueMessage(player, line, delay, 0);
        }

        TypewriterHandler.despawnWhenReady(player);
    }

    private static void respawnAngel(EntityPlayerMP player) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        angel.setPosition(player.posX, player.posY + 5, player.posZ);
        world.spawnEntity(angel);

        String deathLine = AngelPersonality.getRandomDeathLine();
        TypewriterHandler.queueMessage(player, deathLine, 0, 0);
        TypewriterHandler.despawnWhenReady(player);
    }
}
