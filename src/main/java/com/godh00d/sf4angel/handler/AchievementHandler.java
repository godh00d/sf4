package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.advancements.Advancement;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class AchievementHandler {

    private static final Map<UUID, Integer> angelAppearanceCount = new HashMap<>();
    private static final Map<UUID, Long> joinTimes = new HashMap<>();

    public static void onPlayerJoin(UUID playerId) {
        joinTimes.put(playerId, System.currentTimeMillis());
    }

    public static int getAngelAppearances(EntityPlayer player) {
        return angelAppearanceCount.getOrDefault(player.getUniqueID(), 0);
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) return;

        Advancement advancement = event.getAdvancement();
        if (advancement == null || advancement.getId() == null) return;

        String advName = advancement.getId().getPath();
        if (advName.contains("root")) return;

        angelAppearanceCount.merge(player.getUniqueID(), 1, Integer::sum);

        EntityPlayerMP mp = (EntityPlayerMP) player;
        World world = player.world;

        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.posX - 10, player.posY - 5, player.posZ - 10,
            player.posX + 10, player.posY + 10, player.posZ + 10
        );

        List<EntityAngel> nearbyAngels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);

        if (!nearbyAngels.isEmpty()) {
            sendAchievementMessage(mp, advancement);
        } else {
            spawnAngelForAchievement(mp, advancement);
        }
    }

    private static void spawnAngelForAchievement(EntityPlayerMP player, Advancement advancement) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        angel.setPosition(player.posX, player.posY + 5, player.posZ);
        world.spawnEntity(angel);

        sendAchievementMessage(player, advancement);
    }

    private static void sendAchievementMessage(EntityPlayerMP player, Advancement advancement) {
        String advName = advancement.getDisplay() != null
            ? advancement.getDisplay().getTitle().getFormattedText()
            : advancement.getId().getPath();

        String greeting = AngelPersonality.getAdvancementGreeting(advName);
        TypewriterHandler.queueMessage(player, greeting, 0, 0);

        String stageHint = AngelOracle.getHintForCurrentStage(player);
        if (stageHint != null && !stageHint.isEmpty()) {
            TypewriterHandler.queueMessage(player, "Next: " + stageHint, 60, 0);
        }

        AngelOracle.checkInventoryAndAdvance(player);

        String ageComment = AngelPersonality.getRandomAgeComment();
        TypewriterHandler.queueMessage(player, ageComment, 120, 0);

        int appearances = getAngelAppearances(player);
        if (appearances >= 50) {
            TypewriterHandler.queueMessage(player, "The angel smiles upon you, faithful companion.", 150, 0);
        }

        long joinTime = joinTimes.getOrDefault(player.getUniqueID(), System.currentTimeMillis());
        long playMinutes = (System.currentTimeMillis() - joinTime) / 60000;
        if (playMinutes >= 600 && playMinutes < 3000) {
            TypewriterHandler.queueMessage(player, "10 hours in the sky. You are dedicated.", 180, 0);
        } else if (playMinutes >= 3000 && playMinutes < 6000) {
            TypewriterHandler.queueMessage(player, "50 hours. The sky is your home now.", 180, 0);
        } else if (playMinutes >= 6000) {
            TypewriterHandler.queueMessage(player, "100 hours. The angel considers you a friend.", 180, 0);
        }

        String farewell = AngelPersonality.getRandomDepartureLine();
        TypewriterHandler.queueMessage(player, farewell, 300, 0);

        TypewriterHandler.despawnWhenReady(player);
    }
}
