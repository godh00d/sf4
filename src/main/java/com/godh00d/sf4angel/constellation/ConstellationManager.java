package com.godh00d.sf4angel.constellation;

import com.godh00d.sf4angel.SF4Angel;
import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.entity.EntityConstellationObservatory;
import com.godh00d.sf4angel.network.MessageConstellationProgress;
import com.godh00d.sf4angel.network.PacketHandler;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sf4angel")
public final class ConstellationManager {

    public static final byte ABSENT = 0;
    public static final byte COMPLETED = 1;
    public static final byte AVAILABLE = 2;
    public static final byte MYSTERY = 3;

    private static final String DATA_KEY = "sf4angelConstellation";
    private static final int OBSERVATORY_DIMENSION = 0;
    private static final double CENTER_Y = 512.0D;
    private static final double ARRIVAL_OFFSET_X = -66.0D;
    private static final double ARRIVAL_OFFSET_Y = -56.0D;
    private static final double ARRIVAL_OFFSET_Z = 14.0D;
    private static final float ARRIVAL_YAW = -99.7F;
    private static final float ARRIVAL_PITCH = -24.3F;
    private static final double BOUNDARY_RADIUS = 110.0D;
    private static final double MIN_Y = CENTER_Y - BOUNDARY_RADIUS;
    private static final double MAX_Y = CENTER_Y + BOUNDARY_RADIUS;
    private static final int CELL_SPACING = 256;
    private static final int CELL_GRID = 78;
    private static final int CELL_ORIGIN = 10000;
    private static final Map<UUID, Long> INTERACTION_COOLDOWNS = new HashMap<>();

    private ConstellationManager() {
    }

    public static boolean isInside(EntityPlayer player) {
        NBTTagCompound data = getData(player);
        return data.getBoolean("Active") && player.dimension == OBSERVATORY_DIMENSION;
    }

    public static boolean hasActiveSession(EntityPlayer player) {
        NBTTagCompound data = getData(player);
        return data.getBoolean("Active") || data.getBoolean("Entering") || data.getBoolean("Exiting");
    }

    public static void interact(EntityAngel angel, EntityPlayerMP player) {
        if (player.isSpectator() && !angel.isConstellationAnchor()) return;
        long now = System.nanoTime();
        Long last = INTERACTION_COOLDOWNS.get(player.getUniqueID());
        if (last != null && now - last < 500000000L) return;
        INTERACTION_COOLDOWNS.put(player.getUniqueID(), now);
        if (angel.isConstellationAnchor()) {
            player.sendStatusMessage(new TextComponentString("I will return you to the exact place we left."), true);
            exit(player);
        } else {
            player.sendStatusMessage(new TextComponentString(
                "Follow me. Fly to explore, aim at visible lights, and right-click me again to return."), true);
            enter(player);
        }
    }

    public static void enter(EntityPlayerMP player) {
        NBTTagCompound data = getData(player);
        if (hasActiveSession(player)) return;

        captureSource(player, data);
        Cell cell = allocateCell(player);
        data.setDouble("CenterX", cell.x);
        data.setDouble("CenterZ", cell.z);
        data.setBoolean("Entering", true);
        repairTemporaryFlight(player);

        WorldServer overworld = player.getServer().getWorld(OBSERVATORY_DIMENSION);
        double arrivalX = cell.x + ARRIVAL_OFFSET_X;
        double arrivalY = CENTER_Y + ARRIVAL_OFFSET_Y;
        double arrivalZ = cell.z + ARRIVAL_OFFSET_Z;
        try {
            overworld.getChunkFromBlockCoords(new BlockPos(arrivalX, arrivalY, arrivalZ));
            if (player.dimension == OBSERVATORY_DIMENSION) {
                player.connection.setPlayerLocation(arrivalX, arrivalY, arrivalZ, ARRIVAL_YAW, ARRIVAL_PITCH);
            } else {
                player.changeDimension(OBSERVATORY_DIMENSION,
                    new FixedTeleporter(arrivalX, arrivalY, arrivalZ, ARRIVAL_YAW, ARRIVAL_PITCH));
            }
        } catch (RuntimeException exception) {
            SF4Angel.logger.error("Constellation entry failed for {}", player.getName(), exception);
        }

        boolean arrived = player.dimension == OBSERVATORY_DIMENSION && player.world == overworld
            && atPosition(player, arrivalX, arrivalY, arrivalZ);
        if (!arrived) {
            rollbackFlight(player, data);
            clearSession(data);
            sendClear(player);
            return;
        }

        data.setBoolean("Entering", false);
        data.setBoolean("Active", true);
        player.fallDistance = 0.0F;
        spawnObservatory(player);
        sendSnapshot(player);
        explainObservatory(player, data);
    }

    public static void exit(EntityPlayerMP player) {
        NBTTagCompound data = getData(player);
        if (!data.getBoolean("Active") || data.getBoolean("Entering") || data.getBoolean("Exiting")) {
            if (!data.getBoolean("Active")) sendClear(player);
            return;
        }

        Destination destination = destination(player, data);
        data.setBoolean("Exiting", true);
        try {
            if (player.dimension == destination.dimension) {
                player.connection.setPlayerLocation(destination.x, destination.y, destination.z,
                    destination.yaw, destination.pitch);
            } else {
                player.changeDimension(destination.dimension, new FixedTeleporter(destination.x, destination.y,
                    destination.z, destination.yaw, destination.pitch));
            }
        } catch (RuntimeException exception) {
            SF4Angel.logger.error("Constellation exit failed for {}", player.getName(), exception);
        }

        WorldServer target = player.getServer().getWorld(destination.dimension);
        boolean arrived = player.dimension == destination.dimension && player.world == target
            && atPosition(player, destination.x, destination.y, destination.z);
        if (!arrived) {
            data.setBoolean("Exiting", false);
            data.setBoolean("Active", true);
            repairTemporaryFlight(player);
            ensureObservatory(player);
            sendSnapshot(player);
            return;
        }

        restoreSavedFlight(player, data);
        removeObservatories(player);
        clearSession(data);
        sendClear(player);
    }

    public static void refresh(EntityPlayerMP player) {
        if (isInside(player)) sendSnapshot(player);
    }

    private static void captureSource(EntityPlayerMP player, NBTTagCompound data) {
        data.setInteger("SourceDimension", player.dimension);
        data.setDouble("SourceX", player.posX);
        data.setDouble("SourceY", player.posY);
        data.setDouble("SourceZ", player.posZ);
        data.setFloat("SourceYaw", player.rotationYaw);
        data.setFloat("SourcePitch", player.rotationPitch);
        data.setBoolean("AllowFlying", player.capabilities.allowFlying);
        data.setBoolean("IsFlying", player.capabilities.isFlying);
        data.setBoolean("SourceCreative", player.isCreative());
        data.setBoolean("SourceSpectator", player.isSpectator());
    }

    private static Destination destination(EntityPlayerMP player, NBTTagCompound data) {
        int dimension = data.getInteger("SourceDimension");
        double x = data.getDouble("SourceX");
        double y = data.getDouble("SourceY");
        double z = data.getDouble("SourceZ");
        float yaw = data.getFloat("SourceYaw");
        float pitch = data.getFloat("SourcePitch");
        if (!data.hasKey("SourceDimension") || !DimensionManager.isDimensionRegistered(dimension)
            || player.getServer().getWorld(dimension) == null) {
            WorldServer overworld = player.getServer().getWorld(OBSERVATORY_DIMENSION);
            BlockPos spawn = overworld.getSpawnPoint();
            dimension = OBSERVATORY_DIMENSION;
            x = spawn.getX() + 0.5D;
            y = spawn.getY();
            z = spawn.getZ() + 0.5D;
        }
        return new Destination(dimension, x, y, z, yaw, pitch);
    }

    private static void restoreSavedFlight(EntityPlayerMP player, NBTTagCompound data) {
        if (player.isSpectator()) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
        } else if (player.isCreative()) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = data.getBoolean("IsFlying");
        } else {
            boolean sourcePrivileged = data.getBoolean("SourceCreative") || data.getBoolean("SourceSpectator");
            player.capabilities.allowFlying = !sourcePrivileged && data.getBoolean("AllowFlying");
            player.capabilities.isFlying = player.capabilities.allowFlying && data.getBoolean("IsFlying");
        }
        player.sendPlayerAbilities();
    }

    private static void rollbackFlight(EntityPlayerMP player, NBTTagCompound data) {
        player.capabilities.allowFlying = data.getBoolean("AllowFlying");
        player.capabilities.isFlying = data.getBoolean("IsFlying");
        player.sendPlayerAbilities();
    }

    private static void repairTemporaryFlight(EntityPlayerMP player) {
        if (!player.capabilities.allowFlying || !player.capabilities.isFlying) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }
    }

    private static void clearSession(NBTTagCompound data) {
        data.setBoolean("Active", false);
        data.removeTag("Entering");
        data.removeTag("Exiting");
        data.removeTag("SourceDimension");
        data.removeTag("SourceX");
        data.removeTag("SourceY");
        data.removeTag("SourceZ");
        data.removeTag("SourceYaw");
        data.removeTag("SourcePitch");
        data.removeTag("AllowFlying");
        data.removeTag("IsFlying");
        data.removeTag("SourceCreative");
        data.removeTag("SourceSpectator");
        data.removeTag("CenterX");
        data.removeTag("CenterZ");
        data.removeTag("NextObservatoryComment");
    }

    private static void sendSnapshot(EntityPlayerMP player) {
        byte[] states = progressStates(player);
        PacketHandler.INSTANCE.sendTo(new MessageConstellationProgress(states.length,
            AchievementConstellationCatalog.HASH, states), player);
    }

    private static byte[] progressStates(EntityPlayerMP player) {
        AchievementConstellationCatalog.Node[] nodes = AchievementConstellationCatalog.nodes();
        byte[] states = new byte[nodes.length];
        boolean[] stageEligible = new boolean[nodes.length];
        Map<String, Integer> indexes = AchievementConstellationCatalog.indexes();

        for (int i = 0; i < nodes.length; i++) {
            AchievementConstellationCatalog.Node node = nodes[i];
            stageEligible[i] = ownsStages(player, node);
            Advancement advancement = advancement(player, node.id);
            AdvancementProgress progress = advancement == null ? null : player.getAdvancements().getProgress(advancement);
            if (progress != null && progress.isDone()) states[i] = COMPLETED;
        }
        for (int i = 0; i < nodes.length; i++) {
            if (states[i] == COMPLETED || !stageEligible[i]) continue;
            boolean parentsComplete = true;
            for (String parent : nodes[i].parents) {
                Integer parentIndex = indexes.get(parent);
                if (parentIndex == null || states[parentIndex] != COMPLETED) {
                    parentsComplete = false;
                    break;
                }
            }
            if (parentsComplete) states[i] = AVAILABLE;
        }
        applyMysteryFrontier(nodes, states, stageEligible);
        return states;
    }

    private static void explainObservatory(EntityPlayerMP player, NBTTagCompound data) {
        TypewriterHandler.clearMessages(player);
        TypewriterHandler.queueMessage(player,
            "This is your living constellation. Fly to explore it, and aim at a visible light to identify its path.",
            0, 20);
        TypewriterHandler.queueMessage(player,
            "Gold remembers what you completed, blue marks the next step, and grey veils one more dead path beyond it.",
            0, 20);
        TypewriterHandler.queueMessage(player,
            "The paths beyond that horizon remain hidden. I will stay beside you; right-click me when you wish to return.",
            0, 20);
        data.setLong("NextObservatoryComment", player.world.getTotalWorldTime() + 1200L);
    }

    private static void commentOnProgress(EntityPlayerMP player, NBTTagCompound data) {
        if (TypewriterHandler.hasActiveMessages(player)) return;
        byte[] states = progressStates(player);
        AchievementConstellationCatalog.Node[] nodes = AchievementConstellationCatalog.nodes();
        int completed = 0;
        int available = 0;
        String availableTitle = null;
        for (int i = 0; i < states.length; i++) {
            if (states[i] == COMPLETED) completed++;
            if (states[i] == AVAILABLE) {
                available++;
                if (availableTitle == null) availableTitle = nodes[i].title;
            }
        }

        long cycle = player.world.getTotalWorldTime() / 1200L % 3L;
        if (cycle == 0L || availableTitle == null) {
            TypewriterHandler.queueMessage(player,
                "You have awakened " + completed + " of " + states.length + " lights. Each one changes the shape of this sky.",
                0, 0);
        } else if (cycle == 1L) {
            TypewriterHandler.queueMessage(player,
                availableTitle + " burns blue among " + available + " paths now within your reach.", 0, 0);
        } else {
            TypewriterHandler.queueMessage(player,
                "I can see only two steps beyond your completed path, but no farther. What remains hidden must be earned.",
                0, 0);
        }
        data.setLong("NextObservatoryComment", player.world.getTotalWorldTime() + 1200L);
    }

    private static boolean ownsStages(EntityPlayerMP player, AchievementConstellationCatalog.Node node) {
        for (String stage : node.stages) {
            if (!GameStageAccess.hasStage(player, stage)) return false;
        }
        return true;
    }

    static void applyMysteryFrontier(AchievementConstellationCatalog.Node[] nodes, byte[] states,
                                     boolean[] stageEligible) {
        if (nodes.length != states.length || nodes.length != stageEligible.length) {
            throw new IllegalArgumentException("Constellation frontier arrays differ in length");
        }
        Deque<int[]> frontier = new ArrayDeque<>();
        for (int i = 0; i < states.length; i++) {
            if (states[i] == AVAILABLE) {
                frontier.addLast(new int[] {i, 0});
            }
        }

        boolean[] visited = new boolean[nodes.length];
        while (!frontier.isEmpty()) {
            int[] current = frontier.removeFirst();
            if (current[1] >= 1) continue;
            for (int child : nodes[current[0]].children()) {
                validateChild(child, nodes.length);
                if (!stageEligible[child] || states[child] != ABSENT) continue;
                states[child] = MYSTERY;
                if (!visited[child]) {
                    visited[child] = true;
                    frontier.addLast(new int[] {child, current[1] + 1});
                }
            }
        }
    }

    private static void validateChild(int child, int count) {
        if (child < 0 || child >= count) {
            throw new IllegalStateException("Invalid constellation child index " + child);
        }
    }

    private static Advancement advancement(EntityPlayerMP player, String id) {
        return player.getServerWorld().getAdvancementManager().getAdvancement(new ResourceLocation(id));
    }

    private static Cell allocateCell(EntityPlayerMP player) {
        int cellCount = CELL_GRID * CELL_GRID;
        int start = Math.floorMod(player.getUniqueID().hashCode(), cellCount);
        for (int offset = 0; offset < cellCount; offset++) {
            int index = (start + offset) % cellCount;
            Cell candidate = cell(index % CELL_GRID, index / CELL_GRID);
            boolean occupied = false;
            for (EntityPlayerMP other : player.getServer().getPlayerList().getPlayers()) {
                if (other == player) continue;
                NBTTagCompound otherData = getData(other);
                if (!otherData.getBoolean("Active") && !otherData.getBoolean("Entering")) continue;
                if (otherData.getDouble("CenterX") == candidate.x
                    && otherData.getDouble("CenterZ") == candidate.z) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) return candidate;
        }
        throw new IllegalStateException("No constellation observatory cells are available");
    }

    private static Cell cell(int gridX, int gridZ) {
        return new Cell(CELL_ORIGIN + gridX * CELL_SPACING, CELL_ORIGIN + gridZ * CELL_SPACING);
    }

    private static void spawnObservatory(EntityPlayerMP player) {
        removeObservatories(player);
        createObservatory(player);
    }

    private static void ensureObservatory(EntityPlayerMP player) {
        for (EntityConstellationObservatory observatory : player.world.getEntities(
            EntityConstellationObservatory.class,
            entity -> player.getUniqueID().equals(entity.getOwnerId()))) return;
        createObservatory(player);
    }

    private static void createObservatory(EntityPlayerMP player) {
        NBTTagCompound data = getData(player);
        if (!data.getBoolean("Active") || player.dimension != OBSERVATORY_DIMENSION) return;
        double centerX = data.getDouble("CenterX");
        double centerZ = data.getDouble("CenterZ");
        EntityConstellationObservatory observatory = new EntityConstellationObservatory(player.world);
        observatory.setOwnerId(player.getUniqueID());
        observatory.setSceneCenter(centerX, CENTER_Y, centerZ);
        observatory.setPosition(player.posX, player.posY + player.getEyeHeight() - 0.5D,
            player.posZ - 2.0D);
        player.world.spawnEntity(observatory);
    }

    private static void removeObservatories(EntityPlayerMP player) {
        WorldServer world = player.getServer().getWorld(OBSERVATORY_DIMENSION);
        if (world == null) return;
        for (EntityConstellationObservatory observatory : world.getEntities(
            EntityConstellationObservatory.class,
            entity -> player.getUniqueID().equals(entity.getOwnerId()))) observatory.setDead();
    }

    private static void constrainToCell(EntityPlayerMP player, NBTTagCompound data) {
        double centerX = data.getDouble("CenterX");
        double centerZ = data.getDouble("CenterZ");
        double x = Math.max(centerX - BOUNDARY_RADIUS, Math.min(centerX + BOUNDARY_RADIUS, player.posX));
        double y = Math.max(MIN_Y, Math.min(MAX_Y, player.posY));
        double z = Math.max(centerZ - BOUNDARY_RADIUS, Math.min(centerZ + BOUNDARY_RADIUS, player.posZ));
        if (x == player.posX && y == player.posY && z == player.posZ) return;
        player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        player.motionX = player.motionY = player.motionZ = 0.0D;
        player.fallDistance = 0.0F;
    }

    private static boolean atPosition(EntityPlayerMP player, double x, double y, double z) {
        double dx = player.posX - x;
        double dy = player.posY - y;
        double dz = player.posZ - z;
        return dx * dx + dy * dy + dz * dz < 0.01D;
    }

    private static void sendClear(EntityPlayerMP player) {
        PacketHandler.INSTANCE.sendTo(new MessageConstellationProgress(
            AchievementConstellationCatalog.COUNT, AchievementConstellationCatalog.HASH, new byte[0]), player);
    }

    private static NBTTagCompound getData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(DATA_KEY)) persisted.setTag(DATA_KEY, new NBTTagCompound());
        return persisted.getCompoundTag(DATA_KEY);
    }

    private static void recover(EntityPlayerMP player) {
        NBTTagCompound data = getData(player);
        if (data.getBoolean("Entering")) {
            data.setBoolean("Entering", false);
            data.setBoolean("Active", true);
            exit(player);
            return;
        }
        if (data.getBoolean("Exiting")) data.setBoolean("Exiting", false);
        if (data.getBoolean("Active")) exit(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) recover((EntityPlayerMP) event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote) recover((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player.world.isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NBTTagCompound data = getData(player);
        if (data.getBoolean("Entering") || data.getBoolean("Exiting")) return;
        if (data.getBoolean("Active") && event.toDim != OBSERVATORY_DIMENSION) {
            player.getServerWorld().addScheduledTask(() -> exit(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NBTTagCompound data = getData(player);
        if (!data.getBoolean("Active")) return;
        if (player.dimension != OBSERVATORY_DIMENSION) {
            if (player.ticksExisted % 20 == 0) exit(player);
            return;
        }
        repairTemporaryFlight(player);
        player.fallDistance = 0.0F;
        constrainToCell(player, data);
        if (player.ticksExisted % 20 == 0) sendSnapshot(player);
        if (player.ticksExisted % 100 == 0) ensureObservatory(player);
        if (player.world.getTotalWorldTime() >= data.getLong("NextObservatoryComment")) {
            commentOnProgress(player, data);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        INTERACTION_COOLDOWNS.remove(event.player.getUniqueID());
        if (!event.player.world.isRemote) removeObservatories((EntityPlayerMP) event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && isInside((EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && isInside((EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || !(player instanceof EntityPlayerMP)
            || !getData(player).getBoolean("Active")) return;
        Destination destination = destination((EntityPlayerMP) player, getData(player));
        WorldServer world = player.getServer().getWorld(destination.dimension);
        for (EntityItem drop : event.getDrops()) {
            EntityItem safeDrop = new EntityItem(world, destination.x, destination.y, destination.z,
                drop.getItem().copy());
            safeDrop.setPickupDelay(40);
            world.spawnEntity(safeDrop);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (player.world.isRemote || !getData(player).getBoolean("Active")) return;
        Destination destination = destination(player, getData(player));
        WorldServer world = player.getServer().getWorld(destination.dimension);
        int experience = event.getDroppedExperience();
        event.setDroppedExperience(0);
        while (experience > 0) {
            int split = EntityXPOrb.getXPSplit(experience);
            experience -= split;
            world.spawnEntity(new EntityXPOrb(world, destination.x, destination.y, destination.z, split));
        }
    }

    private static final class Cell {
        private final double x;
        private final double z;

        private Cell(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }

    private static final class Destination {
        private final int dimension;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        private Destination(int dimension, double x, double y, double z, float yaw, float pitch) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    private static final class FixedTeleporter implements ITeleporter {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        private FixedTeleporter(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public void placeEntity(World world, Entity entity, float ignoredYaw) {
            entity.setLocationAndAngles(x, y, z, yaw, pitch);
            entity.motionX = entity.motionY = entity.motionZ = 0.0D;
        }
    }
}
