package com.godh00d.sf4angel.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Fail-closed integrations for optional goals that require an observed mod operation. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class OptionalIntegrationHandler {

    private static final String OPTIONAL = "sf4angel:optional/";
    private static final String BUILD_COUNT = "sf4angelBuildingGadgetPlacements";
    private static final Map<Entity, YoyoThrow> YOYOS = new IdentityHashMap<>();
    private static final Map<Entity, GadgetTask> GADGET_TASKS = new IdentityHashMap<>();
    private static final Map<OperationKey, GadgetOperation> GADGET_OPERATIONS = new HashMap<>();
    private static final Map<PositionKey, BitChange> BIT_CHANGES = new HashMap<>();
    private static final Map<UUID, Boolean> ANDROID_STATES = new HashMap<>();
    private static final Set<UUID> ANDROID_PENDING = new HashSet<>();
    private static final Map<Object, RobotProgress> ROBOTS = new IdentityHashMap<>();

    private OptionalIntegrationHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        String className = event.getEntity().getClass().getName();
        if ("com.jozufozu.yoyos.common.EntityYoyo".equals(className)) {
            trackYoyo(event.getEntity());
        } else if ("com.direwolf20.buildinggadgets.common.entities.BlockBuildEntity".equals(className)) {
            trackGadgetTask(event.getEntity());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof IMob)) return;
        Entity immediate = event.getSource().getImmediateSource();
        YoyoThrow tracked = YOYOS.get(immediate);
        if (tracked == null || !tracked.tinkers) return;
        double dx = immediate.posX - tracked.x;
        double dy = immediate.posY - tracked.y;
        double dz = immediate.posZ - tracked.z;
        if (tracked.distance + Math.sqrt(dx * dx + dy * dy + dz * dz) < 40.0D) return;
        EntityPlayerMP player = player(immediate == null ? null : Access.invoke(immediate, "getThrower"));
        if (player != null && player.getUniqueID().equals(tracked.owner)) {
            grant(player, "around_the_void_in_eighty_throws");
        }
    }

    /** Optional-mod events are received through Forge's root event listener to avoid hard mod links. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOptionalEvent(Event event) {
        String name = event.getClass().getName();
        if ("mod.chiselsandbits.api.EventBlockBitModification".equals(name)) {
            beginBitChange(event);
        } else if ("mod.chiselsandbits.api.EventBlockBitPostModification".equals(name)) {
            finishBitChange(event, false);
        } else if ("mod.chiselsandbits.api.EventFullBlockRestoration".equals(name)) {
            finishBitChange(event, true);
        } else if ("li.cil.oc.api.event.RobotBreakBlockEvent$Post".equals(name)) {
            recordRobotAction(event, true);
        } else if ("li.cil.oc.api.event.RobotPlaceBlockEvent$Post".equals(name)) {
            recordRobotAction(event, false);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        updateYoyos(event.world);
        updateGadgetTasks(event.world);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
            || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        updateAndroid(player);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.toDim != 144 || event.fromDim == 144) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NBTTagCompound data = player.getEntityData();
        NBTTagList history = data.getTagList("compactmachines3-coordHistory", 10);
        if (history.tagCount() < 1) return;
        int coordinate = history.getCompoundTagAt(history.tagCount() - 1).getInteger("coord");
        Object savedData = Access.staticField("org.dave.compactmachines3.world.WorldSavedDataMachines", "INSTANCE");
        Object machine = Access.invoke(savedData, "getMachine", new Class<?>[]{int.class}, coordinate);
        Object size = Access.invoke(machine, "getSize");
        Object metadata = Access.invoke(size, "getMeta");
        if (metadata instanceof Number && ((Number) metadata).intValue() == 5) {
            grant(player, "maximum_minimum_space");
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.player.getUniqueID();
        ANDROID_STATES.remove(id);
        ANDROID_PENDING.remove(id);
    }

    private static void trackYoyo(Entity entity) {
        EntityPlayerMP owner = player(Access.invoke(entity, "getThrower"));
        Object stack = Access.invoke(entity, "getYoyoStack");
        if (owner == null || !(stack instanceof ItemStack)) return;
        ItemStack yoyo = (ItemStack) stack;
        boolean tinkers = yoyo.hasTagCompound()
            && yoyo.getTagCompound().hasKey("TinkerData", 10)
            && yoyo.getTagCompound().hasKey("Stats", 10);
        YOYOS.put(entity, new YoyoThrow(owner.getUniqueID(), entity.posX, entity.posY, entity.posZ, tinkers));
    }

    private static void updateYoyos(World world) {
        Iterator<Map.Entry<Entity, YoyoThrow>> iterator = YOYOS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, YoyoThrow> entry = iterator.next();
            Entity entity = entry.getKey();
            if (entity.world != world) continue;
            if (entity.isDead) {
                iterator.remove();
                continue;
            }
            YoyoThrow state = entry.getValue();
            double dx = entity.posX - state.x;
            double dy = entity.posY - state.y;
            double dz = entity.posZ - state.z;
            state.distance += Math.sqrt(dx * dx + dy * dy + dz * dz);
            state.x = entity.posX;
            state.y = entity.posY;
            state.z = entity.posZ;
        }
    }

    private static void trackGadgetTask(Entity entity) {
        Object source = Access.field(entity, "spawnedBy");
        EntityPlayerMP owner = player(source);
        Object modeValue = Access.field(entity, "mode");
        Object positionValue = Access.field(entity, "setPos");
        Object stateValue = Access.field(entity, "setBlock");
        if (owner == null || !(modeValue instanceof Number) || !(positionValue instanceof BlockPos)
            || !(stateValue instanceof IBlockState)) return;
        int mode = ((Number) modeValue).intValue();
        if (mode != 1 && mode != 2) return;
        long tick = entity.world.getTotalWorldTime();
        OperationKey key = new OperationKey(owner.getUniqueID(), entity.dimension, tick, mode);
        GadgetOperation operation = GADGET_OPERATIONS.computeIfAbsent(key,
            ignored -> new GadgetOperation(owner.getUniqueID(), entity.dimension, tick, mode));
        operation.remaining++;
        operation.total++;
        IBlockState expected = mode == 2 ? net.minecraft.init.Blocks.AIR.getDefaultState() : (IBlockState) stateValue;
        GADGET_TASKS.put(entity, new GadgetTask(operation, ((BlockPos) positionValue).toImmutable(), expected));
    }

    private static void updateGadgetTasks(World world) {
        Iterator<Map.Entry<Entity, GadgetTask>> iterator = GADGET_TASKS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, GadgetTask> entry = iterator.next();
            Entity entity = entry.getKey();
            if (entity.world != world || !entity.isDead) continue;
            GadgetTask task = entry.getValue();
            if (world.getBlockState(task.position).equals(task.expected)) task.operation.succeeded++;
            task.operation.remaining--;
            iterator.remove();
        }

        long now = world.getTotalWorldTime();
        Iterator<Map.Entry<OperationKey, GadgetOperation>> operations = GADGET_OPERATIONS.entrySet().iterator();
        while (operations.hasNext()) {
            GadgetOperation operation = operations.next().getValue();
            if (operation.dimension != world.provider.getDimension() || operation.remaining != 0
                || now <= operation.createdTick) continue;
            EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(operation.owner);
            if (owner != null && operation.succeeded == operation.total) {
                if (operation.mode == 1) {
                    NBTTagCompound persisted = persisted(owner);
                    long total = Math.min(Integer.MAX_VALUE,
                        (long) persisted.getInteger(BUILD_COUNT) + operation.succeeded);
                    persisted.setInteger(BUILD_COUNT, (int) total);
                } else if (operation.total >= 64 && persisted(owner).getInteger(BUILD_COUNT) >= 10_000) {
                    grant(owner, "undo_the_apocalypse");
                }
            }
            operations.remove();
        }
    }

    private static void beginBitChange(Event event) {
        Object placing = Access.invoke(event, "isPlacing");
        EntityPlayerMP player = player(Access.invoke(event, "getPlayer"));
        Object worldValue = Access.invoke(event, "getWorld");
        Object posValue = Access.invoke(event, "getPos");
        if (!Boolean.TRUE.equals(placing) || player == null || !(worldValue instanceof World)
            || ((World) worldValue).isRemote || !(posValue instanceof BlockPos)) return;
        World world = (World) worldValue;
        BlockPos pos = (BlockPos) posValue;
        Integer occupied = occupiedBits(world, pos);
        if (occupied == null) return;
        BIT_CHANGES.put(new PositionKey(world.provider.getDimension(), pos),
            new BitChange(player.getUniqueID(), occupied));
    }

    private static void finishBitChange(Event event, boolean restored) {
        Object worldValue = Access.invoke(event, "getWorld");
        Object posValue = Access.invoke(event, "getPos");
        if (!(worldValue instanceof World) || ((World) worldValue).isRemote || !(posValue instanceof BlockPos)) return;
        World world = (World) worldValue;
        PositionKey key = new PositionKey(world.provider.getDimension(), (BlockPos) posValue);
        BitChange change = BIT_CHANGES.get(key);
        if (change == null) return;
        Integer occupied = restored ? Integer.valueOf(4096) : occupiedBits(world, key.position);
        if (occupied == null) return;
        BIT_CHANGES.remove(key);
        int placed = occupied - change.before;
        if (placed <= 0) return;
        EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(change.player);
        if (player == null) return;
        NBTTagCompound persisted = persisted(player);
        long total = Math.min(Integer.MAX_VALUE,
            (long) persisted.getInteger("sf4angelChiselsBitsPlaced") + placed);
        persisted.setInteger("sf4angelChiselsBitsPlaced", (int) total);
        if (total >= 1024 && occupied == 4096) grant(player, "pixel_perfect_masonry");
    }

    private static Integer occupiedBits(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !"mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled"
            .equals(tile.getClass().getName())) return null;
        Object access = Access.invoke(tile, "getBitAccess");
        Object result = Access.invoke(access, "queryBitRange",
            new Class<?>[]{BlockPos.class, BlockPos.class}, BlockPos.ORIGIN, new BlockPos(15, 15, 15));
        Object solid = Access.field(result, "solid");
        Object fluid = Access.field(result, "fluid");
        if (!(solid instanceof Number) || !(fluid instanceof Number)) return null;
        return ((Number) solid).intValue() + ((Number) fluid).intValue();
    }

    private static void updateAndroid(EntityPlayerMP player) {
        if (!Loader.isModLoaded("matteroverdrive")) return;
        Object capability = Access.invokeStatic(
            "matteroverdrive.entity.player.MOPlayerCapabilityProvider", "GetAndroidCapability",
            new Class<?>[]{Entity.class}, player);
        Object currentValue = Access.invoke(capability, "isAndroid");
        if (!(currentValue instanceof Boolean)) return;
        UUID id = player.getUniqueID();
        boolean current = (Boolean) currentValue;
        Boolean previous = ANDROID_STATES.put(id, current);
        if (ANDROID_PENDING.remove(id) && current) grant(player, "android_dreams");
        if (previous != null && !previous && current) ANDROID_PENDING.add(id);
        if (!current) ANDROID_PENDING.remove(id);
    }

    private static void recordRobotAction(Event event, boolean breaking) {
        Object agent = Access.field(event, "agent");
        Object ownerValue = Access.invoke(agent, "ownerUUID");
        if (!(ownerValue instanceof UUID)) return;
        UUID owner = (UUID) ownerValue;
        Object worldValue = Access.invoke(agent, "world");
        if (!(worldValue instanceof World) || ((World) worldValue).isRemote) return;
        RobotProgress progress = ROBOTS.computeIfAbsent(agent, ignored -> new RobotProgress(owner));
        if (!progress.owner.equals(owner)) return;
        if (breaking) progress.broke = true;
        else progress.placed = true;
        if (!progress.broke || !progress.placed) return;
        World world = (World) worldValue;
        EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(owner);
        if (player != null) grant(player, "robot_did_it");
    }

    private static EntityPlayerMP player(Object value) {
        return value instanceof EntityPlayerMP ? (EntityPlayerMP) value : null;
    }

    private static NBTTagCompound persisted(EntityPlayerMP player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayerMP.PERSISTED_NBT_TAG, 10)) {
            data.setTag(EntityPlayerMP.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayerMP.PERSISTED_NBT_TAG);
    }

    private static void grant(EntityPlayerMP player, String path) {
        AchievementHandler.grantCriterion(player, OPTIONAL + path);
    }

    private static final class YoyoThrow {
        private final UUID owner;
        private final boolean tinkers;
        private double x;
        private double y;
        private double z;
        private double distance;

        private YoyoThrow(UUID owner, double x, double y, double z, boolean tinkers) {
            this.owner = owner;
            this.x = x;
            this.y = y;
            this.z = z;
            this.tinkers = tinkers;
        }
    }

    private static final class GadgetTask {
        private final GadgetOperation operation;
        private final BlockPos position;
        private final IBlockState expected;

        private GadgetTask(GadgetOperation operation, BlockPos position, IBlockState expected) {
            this.operation = operation;
            this.position = position;
            this.expected = expected;
        }
    }

    private static final class GadgetOperation {
        private final UUID owner;
        private final int dimension;
        private final long createdTick;
        private final int mode;
        private int total;
        private int succeeded;
        private int remaining;

        private GadgetOperation(UUID owner, int dimension, long createdTick, int mode) {
            this.owner = owner;
            this.dimension = dimension;
            this.createdTick = createdTick;
            this.mode = mode;
        }
    }

    private static final class OperationKey {
        private final UUID owner;
        private final int dimension;
        private final long tick;
        private final int mode;

        private OperationKey(UUID owner, int dimension, long tick, int mode) {
            this.owner = owner;
            this.dimension = dimension;
            this.tick = tick;
            this.mode = mode;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof OperationKey)) return false;
            OperationKey that = (OperationKey) other;
            return dimension == that.dimension && tick == that.tick && mode == that.mode && owner.equals(that.owner);
        }

        @Override
        public int hashCode() {
            int result = owner.hashCode();
            result = result * 31 + dimension;
            result = result * 31 + (int) (tick ^ tick >>> 32);
            return result * 31 + mode;
        }
    }

    private static final class PositionKey {
        private final int dimension;
        private final BlockPos position;

        private PositionKey(int dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PositionKey)) return false;
            PositionKey that = (PositionKey) other;
            return dimension == that.dimension && position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return dimension * 31 + position.hashCode();
        }
    }

    private static final class BitChange {
        private final UUID player;
        private final int before;

        private BitChange(UUID player, int before) {
            this.player = player;
            this.before = before;
        }
    }

    private static final class RobotProgress {
        private final UUID owner;
        private boolean broke;
        private boolean placed;

        private RobotProgress(UUID owner) {
            this.owner = owner;
        }
    }

    /** Members are resolved once; an absent or incompatible optional API returns no evidence. */
    private static final class Access {
        private static final Map<String, Class<?>> CLASSES = new HashMap<>();
        private static final Map<String, Method> METHODS = new HashMap<>();
        private static final Map<String, Field> FIELDS = new HashMap<>();
        private static final Set<String> MISSING = new HashSet<>();

        private Access() {
        }

        private static Object invoke(Object target, String name) {
            return invoke(target, name, new Class<?>[0]);
        }

        private static Object invoke(Object target, String name, Class<?>[] parameters, Object... arguments) {
            if (target == null) return null;
            String key = target.getClass().getName() + '#' + name + signature(parameters);
            if (MISSING.contains(key)) return null;
            try {
                Method method = METHODS.get(key);
                if (method == null) {
                    method = findMethod(target.getClass(), name, parameters);
                    method.setAccessible(true);
                    METHODS.put(key, method);
                }
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                MISSING.add(key);
                return null;
            }
        }

        private static Object invokeStatic(String className, String name, Class<?>[] parameters, Object... arguments) {
            Class<?> type = type(className);
            return type == null ? null : invoke(type, null, name, parameters, arguments);
        }

        private static Object invoke(Class<?> type, Object target, String name,
                                     Class<?>[] parameters, Object... arguments) {
            String key = type.getName() + '#' + name + signature(parameters);
            if (MISSING.contains(key)) return null;
            try {
                Method method = METHODS.get(key);
                if (method == null) {
                    method = findMethod(type, name, parameters);
                    method.setAccessible(true);
                    METHODS.put(key, method);
                }
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                MISSING.add(key);
                return null;
            }
        }

        private static Object field(Object target, String name) {
            if (target == null) return null;
            return field(target.getClass(), target, name);
        }

        private static Object staticField(String className, String name) {
            Class<?> type = type(className);
            return type == null ? null : field(type, null, name);
        }

        private static Object field(Class<?> type, Object target, String name) {
            String key = type.getName() + '#' + name;
            if (MISSING.contains(key)) return null;
            try {
                Field field = FIELDS.get(key);
                if (field == null) {
                    Class<?> cursor = type;
                    while (cursor != null) {
                        try {
                            field = cursor.getDeclaredField(name);
                            break;
                        } catch (NoSuchFieldException ignored) {
                            cursor = cursor.getSuperclass();
                        }
                    }
                    if (field == null) throw new NoSuchFieldException(name);
                    field.setAccessible(true);
                    FIELDS.put(key, field);
                }
                return field.get(target);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                MISSING.add(key);
                return null;
            }
        }

        private static Class<?> type(String name) {
            if (MISSING.contains(name)) return null;
            Class<?> cached = CLASSES.get(name);
            if (cached != null) return cached;
            try {
                Class<?> type = Class.forName(name);
                CLASSES.put(name, type);
                return type;
            } catch (ClassNotFoundException | LinkageError exception) {
                MISSING.add(name);
                return null;
            }
        }

        private static Method findMethod(Class<?> type, String name, Class<?>[] parameters)
            throws NoSuchMethodException {
            Class<?> cursor = type;
            while (cursor != null) {
                try {
                    return cursor.getDeclaredMethod(name, parameters);
                } catch (NoSuchMethodException ignored) {
                    cursor = cursor.getSuperclass();
                }
            }
            return type.getMethod(name, parameters);
        }

        private static String signature(Class<?>[] parameters) {
            StringBuilder result = new StringBuilder("(");
            for (Class<?> parameter : parameters) result.append(parameter.getName()).append(';');
            return result.append(')').toString();
        }
    }
}
