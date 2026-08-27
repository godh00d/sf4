package com.godh00d.sf4angel.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Fail-closed integrations for the six Prestige-only custom criteria. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class PrestigeIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String PRESTIGE = "sf4angel:prestige/";
    private static final String PROJECTE_TABLE = "projecte:transmutation_table";
    private static final String PORTAL_GUN = "portalgun:item_portalgun";
    private static final String PARABOX = "parabox:parabox";
    private static final String EMPOWERED_PARABOX = "parabox:empowered_parabox";
    private static final String PARABOX_PROOF = "sf4angelPrestigeParaboxProof";
    private static final String PARABOX_OWNER_PREFIX = "prestige:parabox:";
    private static final int POLL_INTERVAL = 5;

    private static final Map<UUID, Integer> TICK_DIVIDERS = new HashMap<>();
    private static final Map<UUID, Integer> TABLE_COUNTS = new HashMap<>();
    private static final Map<UUID, List<Object>> PORTAL_SHOTS = new HashMap<>();
    private static final Map<MachineKey, OwnedParabox> OWNED_PARABOXES = new HashMap<>();

    private PrestigeIntegrationHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
            || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        int ticks = TICK_DIVIDERS.getOrDefault(player.getUniqueID(), 0) + 1;
        if (ticks < POLL_INTERVAL) {
            TICK_DIVIDERS.put(player.getUniqueID(), ticks);
            return;
        }
        TICK_DIVIDERS.put(player.getUniqueID(), 0);

        PrestigeState prestige = RuntimeAccess.prestige(player);
        if (prestige == null || !prestige.enabled) return;
        if (prestige.points >= 1L) grant(player, "prestige_worldwide", null);
        checkProjectEAcquisition(player, prestige);
        checkPortalShots(player, prestige);
        checkParaboxRollback(player, prestige);
        pollOwnedParaboxes(player, prestige);
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP) || event.getEntityPlayer().world.isRemote) return;
        checkDirectAcquisition((EntityPlayerMP) event.getEntityPlayer(), event.getItem());
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.player.world.isRemote) return;
        checkDirectAcquisition((EntityPlayerMP) event.player, event.crafting);
    }

    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.player.world.isRemote) return;
        checkDirectAcquisition((EntityPlayerMP) event.player, event.smelting);
    }

    private static void checkDirectAcquisition(EntityPlayerMP player, EntityItem item) {
        if (item != null) checkDirectAcquisition(player, item.getItem());
    }

    private static void checkDirectAcquisition(EntityPlayerMP player, ItemStack stack) {
        if (!isStack(stack, PROJECTE_TABLE, 0)) return;
        PrestigeState prestige = RuntimeAccess.prestige(player);
        if (prestige != null && prestige.enabled && prestige.unlocked.contains("projecte")) {
            grant(player, "equivalent_ambition_unlocked", "projecte");
        }
    }

    private static void checkProjectEAcquisition(EntityPlayerMP player, PrestigeState prestige) {
        int current = countInventory(player, PROJECTE_TABLE, 0);
        Integer previous = TABLE_COUNTS.put(player.getUniqueID(), current);
        if (previous != null && current > previous && prestige.unlocked.contains("projecte")) {
            grant(player, "equivalent_ambition_unlocked", "projecte");
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !RuntimeAccess.isExactClass(event.getEntity(),
            "me.ichun.mods.portalgun.common.entity.EntityPortalProjectile")) return;
        Entity shooter = RuntimeAccess.entityField(event.getEntity(), "shooter");
        Object info = RuntimeAccess.field(event.getEntity(), "portalInfo");
        if (!(shooter instanceof EntityPlayerMP) || info == null) return;

        EntityPlayerMP player = (EntityPlayerMP) shooter;
        PrestigeState prestige = RuntimeAccess.prestige(player);
        if (prestige == null || !prestige.enabled || !prestige.unlocked.contains("portalgun")) return;
        String owner = RuntimeAccess.stringField(info, "uuid");
        String channel = RuntimeAccess.stringField(info, "channelName");
        if (!matchesPortalGun(player.getHeldItemMainhand(), player, owner, channel)
            && !matchesPortalGun(player.getHeldItemOffhand(), player, owner, channel)) return;
        PORTAL_SHOTS.computeIfAbsent(player.getUniqueID(), ignored -> new ArrayList<>()).add(info);
    }

    private static boolean matchesPortalGun(ItemStack stack, EntityPlayerMP player, String owner, String channel) {
        if (!isStack(stack, PORTAL_GUN, 0) || !stack.hasTagCompound() || owner == null || channel == null) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !channel.equals(tag.getString("channelName"))) return false;
        String stackOwner = normalizeUuid(tag.getString("uuid"));
        return normalizeUuid(owner).equals(stackOwner)
            && normalizeUuid(player.getUniqueID().toString()).equals(stackOwner);
    }

    private static void checkPortalShots(EntityPlayerMP player, PrestigeState prestige) {
        List<Object> shots = PORTAL_SHOTS.get(player.getUniqueID());
        if (shots == null || !prestige.unlocked.contains("portalgun")) return;
        Iterator<Object> iterator = shots.iterator();
        while (iterator.hasNext()) {
            Object info = iterator.next();
            Object pair = RuntimeAccess.invoke(info, "getPair");
            if (pair == null) continue;
            Object firstTile = RuntimeAccess.invoke(info, "getTileEntity", player.world);
            Object secondTile = RuntimeAccess.invoke(pair, "getTileEntity", player.world);
            if (firstTile != null && secondTile != null) {
                grant(player, "aperture_unlocked", "portalgun");
                iterator.remove();
            }
        }
        if (shots.isEmpty()) PORTAL_SHOTS.remove(player.getUniqueID());
    }

    /** Mystcraft's optional API event is consumed as its Forge Event base to avoid linking its types. */
    @SubscribeEvent
    public static void onOptionalForgeEvent(Event event) {
        if (!"com.xcompwiz.mystcraft.api.event.LinkEvent$LinkEventEnterWorld"
            .equals(event.getClass().getName())) return;
        Entity entity = RuntimeAccess.entityField(event, "entity");
        World destination = RuntimeAccess.worldField(event, "destination");
        Object info = RuntimeAccess.field(event, "info");
        if (!(entity instanceof EntityPlayerMP) || destination == null || destination.isRemote || info == null
            || !"com.xcompwiz.mystcraft.world.WorldProviderMyst".equals(destination.provider.getClass().getName())) return;

        EntityPlayerMP player = (EntityPlayerMP) entity;
        PrestigeState prestige = RuntimeAccess.prestige(player);
        if (prestige == null || !prestige.enabled || !prestige.unlocked.contains("mystcraft")) return;
        Object ageData = RuntimeAccess.field(destination.provider, "agedata");
        Object targetUuid = RuntimeAccess.invoke(info, "getTargetUUID");
        Object ageUuid = RuntimeAccess.invoke(ageData, "getUUID");
        Object authors = RuntimeAccess.invoke(ageData, "getAuthors");
        if (!(targetUuid instanceof UUID) || !targetUuid.equals(ageUuid) || !(authors instanceof Collection)) return;
        Collection<?> names = (Collection<?>) authors;
        if (names.contains(player.getDisplayNameString()) || names.contains(player.getName())) {
            grant(player, "written_in_another_age", "mystcraft");
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        ResourceLocation name = event.getPlacedBlock().getBlock().getRegistryName();
        if (name == null || (!PARABOX.equals(name.toString()) && !EMPOWERED_PARABOX.equals(name.toString()))) return;
        MachineKey key = new MachineKey(event.getWorld().provider.getDimension(), event.getPos());
        OWNED_PARABOXES.put(key, new OwnedParabox(event.getPlayer().getUniqueID(), name.toString()));
        IntegrationOwnershipData.get(event.getWorld()).put(event.getWorld(), PARABOX_OWNER_PREFIX + name,
            event.getPos(), event.getPlayer().getUniqueID());
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote) {
            OWNED_PARABOXES.remove(new MachineKey(event.getWorld().provider.getDimension(), event.getPos()));
            IntegrationOwnershipData ownership = IntegrationOwnershipData.get(event.getWorld());
            ownership.remove(event.getWorld(), PARABOX_OWNER_PREFIX + PARABOX, event.getPos());
            ownership.remove(event.getWorld(), PARABOX_OWNER_PREFIX + EMPOWERED_PARABOX, event.getPos());
        }
    }

    private static void pollOwnedParaboxes(EntityPlayerMP player, PrestigeState prestige) {
        syncOwnedParaboxes(player.world);
        Iterator<Map.Entry<MachineKey, OwnedParabox>> iterator = OWNED_PARABOXES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<MachineKey, OwnedParabox> entry = iterator.next();
            MachineKey key = entry.getKey();
            OwnedParabox owned = entry.getValue();
            if (!owned.owner.equals(player.getUniqueID()) || key.dimension != player.dimension
                || !player.world.isBlockLoaded(key.position)) continue;
            TileEntity tile = player.world.getTileEntity(key.position);
            if (tile == null || !RuntimeAccess.isParabox(tile, owned.blockName)) {
                iterator.remove();
                IntegrationOwnershipData.get(player.world).remove(player.world,
                    PARABOX_OWNER_PREFIX + owned.blockName, key.position);
                clearParaboxProof(player);
                continue;
            }
            Boolean active = RuntimeAccess.booleanResult(tile, "isActive");
            Integer points = RuntimeAccess.integerResult(tile, "getGeneratedPoints");
            if (active == null || points == null || points < 0) continue;
            if (active && !owned.active) {
                beginParaboxProof(player, prestige.points, PARABOX.equals(owned.blockName));
            } else if (!active && owned.active) {
                clearParaboxProof(player);
            }
            if (active) updateParaboxProof(player, points);
            owned.active = active;
        }
    }

    private static void beginParaboxProof(EntityPlayerMP player, long prestige, boolean basic) {
        NBTTagCompound proof = new NBTTagCompound();
        proof.setLong("PrestigeBefore", prestige);
        proof.setInteger("PendingPoints", 0);
        proof.setBoolean("Basic", basic);
        proof.setBoolean("Eligible", true);
        persisted(player).setTag(PARABOX_PROOF, proof);
    }

    private static void updateParaboxProof(EntityPlayerMP player, int points) {
        NBTTagCompound data = persisted(player);
        if (!data.hasKey(PARABOX_PROOF, 10)) return;
        NBTTagCompound proof = data.getCompoundTag(PARABOX_PROOF);
        if (points > proof.getInteger("PendingPoints")) proof.setInteger("PendingPoints", points);
    }

    private static void checkParaboxRollback(EntityPlayerMP player, PrestigeState prestige) {
        if (hasActiveOwnedParabox(player.getUniqueID())) return;
        NBTTagCompound data = persisted(player);
        if (!data.hasKey(PARABOX_PROOF, 10)) return;
        NBTTagCompound proof = data.getCompoundTag(PARABOX_PROOF);
        int pending = proof.getInteger("PendingPoints");
        long before = proof.getLong("PrestigeBefore");
        boolean accepted = proof.getBoolean("Eligible") && pending > 0 && prestige.points >= before + pending;
        if (accepted) removePersistedParaboxes(player);
        if (accepted && proof.getBoolean("Basic")) grant(player, "time_is_a_flat_parabox", null);
        if (accepted && pending >= 10 && prestige.unlocked.contains("parabox_two")) {
            grant(player, "empowered_recursion", "parabox_two");
        }
        clearParaboxProof(player);
    }

    private static boolean hasActiveOwnedParabox(UUID owner) {
        for (OwnedParabox parabox : OWNED_PARABOXES.values()) {
            if (parabox.owner.equals(owner) && parabox.active) return true;
        }
        return false;
    }

    private static void syncOwnedParaboxes(World world) {
        Set<MachineKey> persisted = new java.util.HashSet<>();
        for (IntegrationOwnershipData.Record record : IntegrationOwnershipData.get(world)
            .records(world, PARABOX_OWNER_PREFIX)) {
            String blockName = record.kind.substring(PARABOX_OWNER_PREFIX.length());
            if (!PARABOX.equals(blockName) && !EMPOWERED_PARABOX.equals(blockName)) continue;
            MachineKey key = new MachineKey(world.provider.getDimension(), record.position);
            persisted.add(key);
            OwnedParabox current = OWNED_PARABOXES.get(key);
            if (current == null || !current.owner.equals(record.owner) || !current.blockName.equals(blockName)) {
                OWNED_PARABOXES.put(key, new OwnedParabox(record.owner, blockName));
            }
        }
        int dimension = world.provider.getDimension();
        OWNED_PARABOXES.entrySet().removeIf(entry -> entry.getKey().dimension == dimension
            && !persisted.contains(entry.getKey()));
    }

    private static void removePersistedParaboxes(EntityPlayerMP player) {
        IntegrationOwnershipData.get(player.world).removeOwner(PARABOX_OWNER_PREFIX, player.getUniqueID());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.player.getUniqueID();
        TICK_DIVIDERS.remove(id);
        TABLE_COUNTS.remove(id);
        PORTAL_SHOTS.remove(id);
        NBTTagCompound data = persisted(event.player);
        if (data.hasKey(PARABOX_PROOF, 10) && !RuntimeAccess.paraboxCollapsing()) {
            data.getCompoundTag(PARABOX_PROOF).setBoolean("Eligible", false);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID id = event.player.getUniqueID();
        if (!persisted(event.player).hasKey(PARABOX_PROOF, 10)) return;
        // An accepted collapse may restart an integrated server without unloading this mod's static state.
        OWNED_PARABOXES.entrySet().removeIf(entry -> entry.getValue().owner.equals(id));
    }

    private static void clearParaboxProof(EntityPlayer player) {
        persisted(player).removeTag(PARABOX_PROOF);
    }

    private static void grant(EntityPlayerMP player, String path, String requiredUnlock) {
        PrestigeState state = RuntimeAccess.prestige(player);
        if (state == null || !state.enabled
            || (requiredUnlock != null && !state.unlocked.contains(requiredUnlock))) return;
        AchievementHandler.grantCriterion(player, PRESTIGE + path);
    }

    private static int countInventory(EntityPlayer player, String registryName, int metadata) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (isStack(stack, registryName, metadata)) count += stack.getCount();
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            if (isStack(stack, registryName, metadata)) count += stack.getCount();
        }
        return count;
    }

    private static boolean isStack(ItemStack stack, String registryName, int metadata) {
        if (stack == null || stack.isEmpty() || stack.getMetadata() != metadata) return false;
        ResourceLocation name = stack.getItem().getRegistryName();
        return name != null && registryName.equals(name.toString());
    }

    private static String normalizeUuid(String value) {
        return value == null ? "" : value.replace("-", "").toLowerCase(Locale.ROOT);
    }

    private static NBTTagCompound persisted(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static final class PrestigeState {
        private final boolean enabled;
        private final long points;
        private final Set<String> unlocked;

        private PrestigeState(boolean enabled, long points, Set<String> unlocked) {
            this.enabled = enabled;
            this.points = points;
            this.unlocked = unlocked;
        }
    }

    private static final class MachineKey {
        private final int dimension;
        private final BlockPos position;

        private MachineKey(int dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof MachineKey)) return false;
            MachineKey key = (MachineKey) other;
            return dimension == key.dimension && position.equals(key.position);
        }

        @Override
        public int hashCode() {
            return dimension * 31 + position.hashCode();
        }
    }

    private static final class OwnedParabox {
        private final UUID owner;
        private final String blockName;
        private boolean active;

        private OwnedParabox(UUID owner, String blockName) {
            this.owner = owner;
            this.blockName = blockName;
        }
    }

    /** Optional-mod members are resolved once; unavailable proof paths return null and never grant. */
    private static final class RuntimeAccess {
        private static final Map<String, Class<?>> CLASSES = new HashMap<>();
        private static final Map<String, Field> FIELDS = new HashMap<>();
        private static final Map<String, Method> METHODS = new HashMap<>();
        private static final Map<String, Boolean> MISSING = new HashMap<>();

        private RuntimeAccess() {
        }

        private static PrestigeState prestige(EntityPlayerMP player) {
            if (!Loader.isModLoaded("prestige")) return null;
            try {
                Class<?> prestigeClass = type("com.jarhax.prestige.Prestige");
                Class<?> dataClass = type("com.jarhax.prestige.data.GlobalPrestigeData");
                if (prestigeClass == null || dataClass == null) return null;
                Object enabled = staticField(prestigeClass, "prestigeEnabled");
                Object data = staticInvoke(dataClass, "getPlayerData", player);
                Object points = invoke(data, "getPrestige");
                Object rewards = invoke(data, "getUnlockedRewards");
                if (!(enabled instanceof Boolean) || !(points instanceof Number) || !(rewards instanceof Collection)) {
                    return null;
                }
                Set<String> identifiers = new java.util.HashSet<>();
                for (Object reward : (Collection<?>) rewards) {
                    Object identifier = invoke(reward, "getIdentifier");
                    if (!(identifier instanceof String)) return null;
                    identifiers.add((String) identifier);
                }
                return new PrestigeState((Boolean) enabled, ((Number) points).longValue(), identifiers);
            } catch (RuntimeException exception) {
                warnOnce("prestige-state", exception);
                return null;
            }
        }

        private static boolean isParabox(TileEntity tile, String blockName) {
            String expected = EMPOWERED_PARABOX.equals(blockName)
                ? "net.darkhax.parabox.block.v2.TileEntityParaboxV2"
                : "net.darkhax.parabox.block.TileEntityParabox";
            return isExactClass(tile, expected);
        }

        private static boolean paraboxCollapsing() {
            if (!Loader.isModLoaded("parabox")) return false;
            Class<?> manager = type("net.darkhax.parabox.util.WorldSpaceTimeManager");
            Object worldData = manager == null ? null : staticInvoke(manager, "getWorldData");
            Object deleting = invoke(worldData, "isShouldDelete");
            return deleting instanceof Boolean && (Boolean) deleting;
        }

        private static boolean isExactClass(Object value, String className) {
            Class<?> expected = type(className);
            return expected != null && value != null && value.getClass() == expected;
        }

        private static Entity entityField(Object target, String name) {
            Object value = field(target, name);
            return value instanceof Entity ? (Entity) value : null;
        }

        private static World worldField(Object target, String name) {
            Object value = field(target, name);
            return value instanceof World ? (World) value : null;
        }

        private static String stringField(Object target, String name) {
            Object value = field(target, name);
            return value instanceof String ? (String) value : null;
        }

        private static Boolean booleanResult(Object target, String name) {
            Object value = invoke(target, name);
            return value instanceof Boolean ? (Boolean) value : null;
        }

        private static Integer integerResult(Object target, String name) {
            Object value = invoke(target, name);
            return value instanceof Number ? ((Number) value).intValue() : null;
        }

        private static Object field(Object target, String name) {
            if (target == null) return null;
            String key = target.getClass().getName() + '#' + name;
            if (MISSING.containsKey(key)) return null;
            try {
                Field field = FIELDS.get(key);
                if (field == null) {
                    Class<?> current = target.getClass();
                    while (current != null) {
                        try {
                            field = current.getDeclaredField(name);
                            break;
                        } catch (NoSuchFieldException ignored) {
                            current = current.getSuperclass();
                        }
                    }
                    if (field == null) throw new NoSuchFieldException(name);
                    field.setAccessible(true);
                    FIELDS.put(key, field);
                }
                return field.get(target);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                missing(key, exception);
                return null;
            }
        }

        private static Object staticField(Class<?> type, String name) {
            String key = type.getName() + '#' + name;
            if (MISSING.containsKey(key)) return null;
            try {
                Field field = FIELDS.get(key);
                if (field == null) {
                    field = type.getField(name);
                    field.setAccessible(true);
                    FIELDS.put(key, field);
                }
                return field.get(null);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                missing(key, exception);
                return null;
            }
        }

        private static Object staticInvoke(Class<?> type, String name, Object... arguments) {
            return invokeResolved(type, null, name, arguments);
        }

        private static Object invoke(Object target, String name, Object... arguments) {
            return target == null ? null : invokeResolved(target.getClass(), target, name, arguments);
        }

        private static Object invokeResolved(Class<?> type, Object target, String name, Object... arguments) {
            String key = type.getName() + '#' + name + '/' + arguments.length;
            if (MISSING.containsKey(key)) return null;
            try {
                Method method = METHODS.get(key);
                if (method == null) {
                    method = findMethod(type, name, target == null, arguments);
                    method.setAccessible(true);
                    METHODS.put(key, method);
                }
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
                missing(key, exception);
                return null;
            }
        }

        private static Method findMethod(Class<?> type, String name, boolean requireStatic, Object[] arguments)
            throws NoSuchMethodException {
            Class<?> current = type;
            while (current != null) {
                for (Method method : current.getDeclaredMethods()) {
                    if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length
                        || (requireStatic && !Modifier.isStatic(method.getModifiers()))) continue;
                    Class<?>[] parameters = method.getParameterTypes();
                    boolean compatible = true;
                    for (int index = 0; index < parameters.length; index++) {
                        if (arguments[index] != null && !parameters[index].isInstance(arguments[index])) {
                            compatible = false;
                            break;
                        }
                    }
                    if (compatible) return method;
                }
                current = current.getSuperclass();
            }
            throw new NoSuchMethodException(type.getName() + '.' + name);
        }

        private static Class<?> type(String name) {
            if (MISSING.containsKey(name)) return null;
            Class<?> result = CLASSES.get(name);
            if (result != null) return result;
            try {
                result = Class.forName(name);
                CLASSES.put(name, result);
                return result;
            } catch (ClassNotFoundException | LinkageError | RuntimeException exception) {
                missing(name, exception);
                return null;
            }
        }

        private static void missing(String key, Throwable cause) {
            MISSING.put(key, Boolean.TRUE);
            warnOnce(key, cause);
        }

        private static void warnOnce(String key, Throwable cause) {
            String warning = "warning:" + key;
            if (MISSING.put(warning, Boolean.TRUE) == null) {
                LOGGER.warn("Prestige integration proof unavailable for {}: {}", key, cause.toString());
            }
        }
    }
}
