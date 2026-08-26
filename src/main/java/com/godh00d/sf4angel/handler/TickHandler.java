package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class TickHandler {

    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> idleTimers = new HashMap<>();
    private static final Map<String, Integer> healthWarnTimers = new HashMap<>();
    private static final Map<UUID, Integer> advScanTimers = new HashMap<>();
    private static final Map<UUID, Integer> movementTimers = new HashMap<>();
    private static final int MIN_IDLE_TICKS = 6000;
    private static final int MAX_IDLE_TICKS = 12000;
    private static final int ADV_SCAN_INTERVAL = 200;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.world.isRemote) return;

        EntityPlayer player = event.player;
        EntityPlayerMP mp = (EntityPlayerMP) player;

        TypewriterHandler.tick(player);

        handleDespawn(mp);

        handleIdleChatter(mp);

        handleAngelFollow(mp);

        handleHealthWarning(mp);

        handleAdvancementScan(mp);
    }

    private static void handleDespawn(EntityPlayerMP player) {
        if (TypewriterHandler.shouldDespawn(player)) {
            World world = player.world;
            AxisAlignedBB searchBox = new AxisAlignedBB(
                player.posX - 10, player.posY - 5, player.posZ - 10,
                player.posX + 10, player.posY + 10, player.posZ + 10
            );
            List<EntityAngel> angels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);
            for (EntityAngel angel : angels) {
                angel.startDespawn(0);
            }
        }
    }

    private static void handleIdleChatter(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        int timer = idleTimers.getOrDefault(id, 0) + 1;

        if (timer >= MIN_IDLE_TICKS + RANDOM.nextInt(MAX_IDLE_TICKS - MIN_IDLE_TICKS)) {
            if (!TypewriterHandler.hasActiveMessages(player)) {
                World world = player.world;
                AxisAlignedBB searchBox = new AxisAlignedBB(
                    player.posX - 10, player.posY - 5, player.posZ - 10,
                    player.posX + 10, player.posY + 10, player.posZ + 10
                );
                List<EntityAngel> angels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);
                if (!angels.isEmpty()) {
                    String quip = AngelPersonality.getRandomSmallTalk();
                    TypewriterHandler.queueMessage(player, quip, 0, 0);
                }
            }
            timer = 0;
        }

        idleTimers.put(id, timer);
    }

    private static void handleAngelFollow(EntityPlayerMP player) {
        World world = player.world;
        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.posX - 15, player.posY - 5, player.posZ - 15,
            player.posX + 15, player.posY + 15, player.posZ + 15
        );
        List<EntityAngel> angels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);

        for (EntityAngel angel : angels) {
            if (angel.getOwnerId() != null && angel.getOwnerId().equals(player.getUniqueID())) {
                UUID id = player.getUniqueID();
                int timer = movementTimers.getOrDefault(id, 0) + 1;
                movementTimers.put(id, timer);

                double yaw = Math.toRadians(player.rotationYaw);
                double forwardX = -Math.sin(yaw);
                double forwardZ = Math.cos(yaw);
                double rightX = Math.cos(yaw);
                double rightZ = Math.sin(yaw);
                double wave = Math.sin(timer * 0.035D);
                int mode = (timer / 240) % 3;

                double targetX;
                double targetZ;
                if (mode == 0) {
                    targetX = player.posX + forwardX * 6.0D;
                    targetZ = player.posZ + forwardZ * 6.0D;
                } else if (mode == 1) {
                    double orbit = timer * 0.015D;
                    targetX = player.posX + Math.cos(orbit) * 5.0D;
                    targetZ = player.posZ + Math.sin(orbit) * 5.0D;
                } else {
                    targetX = player.posX + forwardX * 4.5D + rightX * wave * 2.0D;
                    targetZ = player.posZ + forwardZ * 4.5D + rightZ * wave * 2.0D;
                }

                double targetY = player.posY + player.getEyeHeight() - 0.45D + Math.sin(timer * 0.05D) * 0.35D;

                double dx = targetX - angel.posX;
                double dy = targetY - angel.posY;
                double dz = targetZ - angel.posZ;

                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist > 0.25D) {
                    double speed = isPlayerLookingAt(player, angel) ? 0.045D : 0.11D;
                    angel.motionX += (dx / dist) * speed;
                    angel.motionY += (dy / dist) * speed;
                    angel.motionZ += (dz / dist) * speed;
                }

                angel.motionX *= 0.86D;
                angel.motionY *= 0.86D;
                angel.motionZ *= 0.86D;
            }
        }
    }

    private static boolean isPlayerLookingAt(EntityPlayer player, EntityAngel angel) {
        Vec3d lookVec = player.getLookVec();
        Vec3d toAngel = new Vec3d(
            angel.posX - player.posX,
            angel.posY - (player.posY + player.getEyeHeight()),
            angel.posZ - player.posZ
        );

        if (toAngel.lengthVector() < 0.001D) return false;
        double dot = lookVec.dotProduct(toAngel.normalize());
        return dot > 0.95;
    }

    private static void handleHealthWarning(EntityPlayerMP player) {
        if (player.getHealth() < player.getMaxHealth() * 0.3f) {
            UUID id = player.getUniqueID();
            String lastWarnKey = id.toString() + "_healthwarn";
            int lastWarn = healthWarnTimers.getOrDefault(lastWarnKey, 0);

            if (lastWarn == 0 || lastWarn > 200) {
                if (!TypewriterHandler.hasActiveMessages(player)) {
                    String warning = AngelPersonality.getRandomHealthWarning();
                    TypewriterHandler.queueMessage(player, warning, 0, 0);
                    healthWarnTimers.put(lastWarnKey, 1);
                }
            }
        }
    }

    private static void handleAdvancementScan(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        int timer = advScanTimers.getOrDefault(id, 0) + 1;
        if (timer >= ADV_SCAN_INTERVAL) {
            AchievementHandler.checkAdvancementProgress(player);
            timer = 0;
        }
        advScanTimers.put(id, timer);
    }
}
