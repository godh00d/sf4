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
    private static final Map<UUID, MovementState> movementStates = new HashMap<>();
    private static final Map<UUID, Integer> counterCheckTimers = new HashMap<>();
    private static final int MIN_IDLE_TICKS = 6000;
    private static final int MAX_IDLE_TICKS = 12000;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.world.isRemote) return;

        EntityPlayer player = event.player;
        EntityPlayerMP mp = (EntityPlayerMP) player;

        TypewriterHandler.tick(player);

        AchievementHandler.checkTwerk(mp);

        handleDespawn(mp);

        handleIdleChatter(mp);

        handleAngelFollow(mp);

        handleHealthWarning(mp);

        int counterTimer = counterCheckTimers.getOrDefault(player.getUniqueID(), 0) + 1;
        if (counterTimer >= 20) {
            AchievementHandler.checkCustomCounters(mp);
            counterTimer = 0;
        }
        counterCheckTimers.put(player.getUniqueID(), counterTimer);

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
                if (player.getUniqueID().equals(angel.getOwnerId())) {
                    angel.startDespawn(0);
                }
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
                boolean hasOwnedAngel = false;
                for (EntityAngel angel : angels) {
                    if (player.getUniqueID().equals(angel.getOwnerId())) {
                        hasOwnedAngel = true;
                        break;
                    }
                }
                if (hasOwnedAngel) {
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
                if (angel.getVisualState() != EntityAngel.STATE_VISIBLE) {
                    angel.motionX = 0.0D;
                    angel.motionY = 0.0D;
                    angel.motionZ = 0.0D;
                    continue;
                }
                UUID id = player.getUniqueID();
                MovementState movement = movementStates.computeIfAbsent(id, ignored -> new MovementState());
                movement.tick();

                double yaw = Math.toRadians(player.rotationYaw);
                double forwardX = -Math.sin(yaw);
                double forwardZ = Math.cos(yaw);
                double rightX = Math.cos(yaw);
                double rightZ = Math.sin(yaw);
                double wave = Math.sin(movement.phase + movement.elapsed * 0.025D);

                double targetX;
                double targetZ;
                switch (movement.mode) {
                    case 0: // Watch from in front, gently shifting weight.
                        targetX = player.posX + forwardX * 6.5D + rightX * wave * 0.55D;
                        targetZ = player.posZ + forwardZ * 6.5D + rightZ * wave * 0.55D;
                        break;
                    case 1: // Slow orbit that remains near the player's field of view.
                        double orbit = yaw + movement.phase + movement.elapsed * 0.009D;
                        targetX = player.posX + Math.cos(orbit) * 6.2D;
                        targetZ = player.posZ + Math.sin(orbit) * 6.2D;
                        break;
                    case 2: // Drift from one side of the player to the other.
                        targetX = player.posX + forwardX * 5.8D + rightX * movement.side * (1.7D + wave * 0.8D);
                        targetZ = player.posZ + forwardZ * 5.8D + rightZ * movement.side * (1.7D + wave * 0.8D);
                        break;
                    case 3: // Briefly move closer as if inspecting what the player is doing.
                        double closeDistance = 5.0D + wave * 0.3D;
                        targetX = player.posX + forwardX * closeDistance + rightX * movement.side * 0.65D;
                        targetZ = player.posZ + forwardZ * closeDistance + rightZ * movement.side * 0.65D;
                        break;
                    default: // Wander in a loose arc without leaving the player behind.
                        double wanderAngle = yaw + movement.phase + Math.sin(movement.elapsed * 0.012D) * 0.75D;
                        double wanderDistance = 5.5D + Math.sin(movement.elapsed * 0.019D) * 0.8D;
                        targetX = player.posX - Math.sin(wanderAngle) * wanderDistance;
                        targetZ = player.posZ + Math.cos(wanderAngle) * wanderDistance;
                        break;
                }

                double targetY = player.posY + player.getEyeHeight() - 0.55D
                    + Math.sin(movement.phase + movement.elapsed * 0.04D) * 0.32D;

                double dx = targetX - angel.posX;
                double dy = targetY - angel.posY;
                double dz = targetZ - angel.posZ;

                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double playerDx = angel.posX - player.posX;
                double playerDz = angel.posZ - player.posZ;
                double horizontalDistance = Math.sqrt(playerDx * playerDx + playerDz * playerDz);
                if (horizontalDistance < 4.75D) {
                    double safeDistance = Math.max(horizontalDistance, 0.01D);
                    double push = (4.75D - horizontalDistance) * 0.055D + 0.035D;
                    angel.motionX += playerDx / safeDistance * push;
                    angel.motionZ += playerDz / safeDistance * push;
                }
                if (dist > 0.25D) {
                    double speed = isPlayerLookingAt(player, angel) ? 0.025D : 0.045D;
                    if (dist > 8.0D) speed = 0.09D;
                    angel.motionX += (dx / dist) * speed;
                    angel.motionY += (dy / dist) * speed;
                    angel.motionZ += (dz / dist) * speed;
                }

                angel.motionX *= 0.89D;
                angel.motionY *= 0.89D;
                angel.motionZ *= 0.89D;
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

    public static void removePlayer(UUID id) {
        idleTimers.remove(id);
        movementStates.remove(id);
        counterCheckTimers.remove(id);
        healthWarnTimers.remove(id.toString() + "_healthwarn");
        AchievementHandler.removePlayer(id);
    }

    private static class MovementState {
        int mode = RANDOM.nextInt(5);
        int elapsed;
        int duration = 180 + RANDOM.nextInt(260);
        int side = RANDOM.nextBoolean() ? 1 : -1;
        double phase = RANDOM.nextDouble() * Math.PI * 2.0D;

        void tick() {
            elapsed++;
            if (elapsed < duration) return;

            int previous = mode;
            do {
                mode = RANDOM.nextInt(5);
            } while (mode == previous);
            elapsed = 0;
            duration = 180 + RANDOM.nextInt(260);
            side = RANDOM.nextBoolean() ? 1 : -1;
            phase = RANDOM.nextDouble() * Math.PI * 2.0D;
        }
    }
}
