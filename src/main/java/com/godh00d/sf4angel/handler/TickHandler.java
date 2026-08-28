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

        IntegrationEngine.tick(mp);

        handleDespawn(mp);

        handleIdleChatter(mp);

        handleAngelFollow(mp);

        handleHealthWarning(mp);

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
                double[] target = getMovementTarget(player, movement, movement.mode, movement.elapsed,
                    yaw, forwardX, forwardZ, rightX, rightZ);
                if (movement.transitionElapsed < MovementState.TRANSITION_TICKS) {
                    double[] previous = getMovementTarget(player, movement, movement.previousMode,
                        movement.previousElapsed + movement.transitionElapsed, yaw,
                        forwardX, forwardZ, rightX, rightZ);
                    double blend = movement.getTransitionBlend();
                    target[0] = previous[0] + (target[0] - previous[0]) * blend;
                    target[1] = previous[1] + (target[1] - previous[1]) * blend;
                }
                double targetX = target[0];
                double targetZ = target[1];

                double targetY = player.posY + player.getEyeHeight() - 0.55D
                    + Math.sin(movement.phase + movement.elapsed * 0.04D) * 0.32D;
                if (movement.transitionElapsed < MovementState.TRANSITION_TICKS) {
                    double previousY = player.posY + player.getEyeHeight() - 0.55D
                        + Math.sin(movement.phase + (movement.previousElapsed
                        + movement.transitionElapsed) * 0.04D) * 0.32D;
                    double blend = movement.getTransitionBlend();
                    targetY = previousY + (targetY - previousY) * blend;
                }

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

    private static double[] getMovementTarget(EntityPlayer player, MovementState movement, int mode,
                                               int elapsed, double yaw, double forwardX, double forwardZ,
                                               double rightX, double rightZ) {
        double wave = Math.sin(movement.phase + elapsed * 0.025D);
        switch (mode) {
            case 0: // Hold position in front and acknowledge the player.
                return new double[] {
                    player.posX + forwardX * 6.5D + rightX * wave * 0.55D,
                    player.posZ + forwardZ * 6.5D + rightZ * wave * 0.55D
                };
            case 1: // Make one unhurried world-space orbit.
                double orbit = movement.phase + elapsed * 0.009D;
                return new double[] {
                    player.posX + Math.cos(orbit) * 6.2D,
                    player.posZ + Math.sin(orbit) * 6.2D
                };
            case 2: // Deliberately cross to the other side of the player's view.
                return new double[] {
                    player.posX + forwardX * 5.8D + rightX * movement.side * (1.7D + wave * 0.8D),
                    player.posZ + forwardZ * 5.8D + rightZ * movement.side * (1.7D + wave * 0.8D)
                };
            case 3: // Move closer briefly, as if inspecting the player.
                double closeDistance = 5.0D + wave * 0.3D;
                return new double[] {
                    player.posX + forwardX * closeDistance + rightX * movement.side * 0.65D,
                    player.posZ + forwardZ * closeDistance + rightZ * movement.side * 0.65D
                };
            default: // Trace a loose arc, then return to the front.
                double wanderAngle = yaw + movement.phase + Math.sin(elapsed * 0.012D) * 0.75D;
                double wanderDistance = 5.5D + Math.sin(elapsed * 0.019D) * 0.8D;
                return new double[] {
                    player.posX - Math.sin(wanderAngle) * wanderDistance,
                    player.posZ + Math.cos(wanderAngle) * wanderDistance
                };
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
        healthWarnTimers.remove(id.toString() + "_healthwarn");
        AchievementHandler.removePlayer(id);
        IntegrationEngine.removePlayer(id);
    }

    private static class MovementState {
        static final int TRANSITION_TICKS = 50;
        private static final int[] ROUTE = {0, 2, 0, 3, 0, 1, 4};

        int routeIndex;
        int mode = ROUTE[0];
        int previousMode = mode;
        int elapsed;
        int previousElapsed;
        int transitionElapsed = TRANSITION_TICKS;
        int duration = getDuration(mode);
        int side = RANDOM.nextBoolean() ? 1 : -1;
        double phase = RANDOM.nextDouble() * Math.PI * 2.0D;

        void tick() {
            elapsed++;
            if (transitionElapsed < TRANSITION_TICKS) transitionElapsed++;
            if (elapsed < duration) return;

            previousMode = mode;
            previousElapsed = elapsed;
            routeIndex = (routeIndex + 1) % ROUTE.length;
            mode = ROUTE[routeIndex];
            elapsed = 0;
            transitionElapsed = 0;
            duration = getDuration(mode);
            if (mode == 2 || mode == 3) side *= -1;
        }

        double getTransitionBlend() {
            double progress = (double) transitionElapsed / TRANSITION_TICKS;
            return progress * progress * (3.0D - 2.0D * progress);
        }

        private static int getDuration(int mode) {
            switch (mode) {
                case 1: return 420 + RANDOM.nextInt(100);
                case 2: return 190 + RANDOM.nextInt(70);
                case 3: return 100 + RANDOM.nextInt(45);
                case 4: return 240 + RANDOM.nextInt(90);
                default: return 260 + RANDOM.nextInt(120);
            }
        }
    }
}
