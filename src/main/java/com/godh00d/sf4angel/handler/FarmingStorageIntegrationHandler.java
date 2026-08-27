package com.godh00d.sf4angel.handler;

import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/** Operation-level integrations for farming and storage mods, without compile-time mod dependencies. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class FarmingStorageIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String NETWORK_CHEST = "sf4angel:core/network_attached_chest";
    private static final String REMOTE = "sf4angel:core/remote_possibilities";
    private static final String TRUFFLE = "sf4angel:core/truffle_shuffle";
    private static final String LATEX = "sf4angel:core/latex_intentions";
    private static final String SOWER = "sf4angel:core/sow_automatic";
    private static final String REAPER = "sf4angel:core/reap_automatic";
    private static final String MOB_RULES = "sf4angel:core/mob_rules";
    private static final String MILK = "sf4angel:optional/milk_without_the_moo";
    private static final String MOB_FLOOR = "sf4angel:optional/mob_factory_floor";

    private static final String MASTER_BLOCK = "storagenetwork:master";
    private static final String STORAGE_CABLE_BLOCK = "storagenetwork:storage_kabel";
    private static final String REMOTE_ITEM = "storagenetwork:remote";
    private static final String TRUFFLE_BLOCK = "resourcehogs:truffle";
    private static final String TREE_EXTRACTOR = "industrialforegoing:tree_fluid_extractor";
    private static final String CROP_SOWER = "industrialforegoing:crop_sower";
    private static final String CROP_REAPER = "industrialforegoing:crop_recolector";
    private static final String MOB_CRUSHER = "industrialforegoing:mob_relocator";
    private static final String MOB_SLAUGHTER = "industrialforegoing:mob_slaughter_factory";
    private static final String MILK_JAR = "cookingforblockheads:milk_jar";
    private static final String COW_JAR = "cookingforblockheads:cow_jar";
    private static final String MOB_SAW = "mob_grinding_utils:saw";
    private static final String MOB_SPIKES = "mob_grinding_utils:spikes";

    private static final Set<String> TRACKED_BLOCKS = new HashSet<>(Arrays.asList(
        MASTER_BLOCK, TREE_EXTRACTOR, CROP_SOWER, CROP_REAPER, MOB_CRUSHER, MOB_SLAUGHTER,
        MILK_JAR, COW_JAR, MOB_SAW, MOB_SPIKES));
    private static final String OWNER_PREFIX = "farming:";
    private static final long HOG_STALE_TICKS = 200L;
    private static final Map<World, Map<BlockPos, OwnedBlock>> OWNED_BLOCKS = new WeakHashMap<>();
    private static final Map<UUID, HogState> BRED_HOGS = new HashMap<>();
    private static final Map<UUID, Set<String>> SUBMITTED = new HashMap<>();
    private static final List<PendingCrusherKill> PENDING_CRUSHER_KILLS = new ArrayList<>();

    private FarmingStorageIntegrationHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        String blockId = blockName(event.getPlacedBlock().getBlock().getRegistryName());

        if (player instanceof FakePlayer) {
            if (!CROP_SOWER.equals(blockId)) recordSowerOperation(event.getPos(), player.getServerWorld());
            return;
        }
        if (!TRACKED_BLOCKS.contains(blockId) || event.getPlacedBlock().getBlock().getMetaFromState(event.getPlacedBlock()) != 0) {
            return;
        }
        BlockKey key = new BlockKey(player.dimension, event.getPos());
        IntegrationOwnershipData.get(event.getWorld()).put(event.getWorld(), OWNER_PREFIX + blockId,
            event.getPos(), player.getUniqueID());
        ownedBlocks(event.getWorld()).put(event.getPos().toImmutable(),
            new OwnedBlock(player.getUniqueID(), blockId, key));
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        IntegrationOwnershipData.get(event.getWorld()).removeAt(event.getWorld(), event.getPos());
        ownedBlocks(event.getWorld()).remove(event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHogBred(BabyEntitySpawnEvent event) {
        if (event.getChild() == null || event.getChild().world.isRemote
            || !(event.getCausedByPlayer() instanceof EntityPlayerMP)
            || !ResourceHogAdapter.isResourceHog(event.getChild())) return;
        EntityPlayerMP breeder = (EntityPlayerMP) event.getCausedByPlayer();
        UUID hogId = event.getChild().getUniqueID();
        BlockPos position = new BlockPos(event.getChild());
        long seenAt = event.getChild().world.getTotalWorldTime();
        ResourceHogAttributionData.get(event.getChild().world).put(
            new ResourceHogAttributionData.EntityRecord(hogId, event.getChild().world, position, seenAt),
            breeder.getUniqueID());
        BRED_HOGS.put(hogId, new HogState(breeder.getUniqueID(), event.getChild().dimension,
            position, seenAt));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (ResourceHogAdapter.isResourceHog(event.getEntityLiving())) {
            UUID hogId = event.getEntityLiving().getUniqueID();
            ResourceHogAttributionData.get(event.getEntityLiving().world).remove(hogId);
            BRED_HOGS.remove(hogId);
        }
        if (!(event.getEntityLiving() instanceof IMob)) return;
        WorldServer world = (WorldServer) event.getEntityLiving().world;
        BlockPos deathPos = new BlockPos(event.getEntityLiving());
        String damageType = event.getSource().getDamageType();

        OwnedBlock crusher = uniqueMachineAt(world, deathPos, MOB_CRUSHER);
        if (crusher != null && "mob_crusher".equals(damageType)) {
            TileEntity tile = world.getTileEntity(crusher.key.position);
            PENDING_CRUSHER_KILLS.add(new PendingCrusherKill(crusher, itemCounts(tile), fluidAmount(tile, "essence"),
                world.getTotalWorldTime() + 4L));
            return;
        }

        OwnedBlock slaughter = uniqueMachineAt(world, deathPos, MOB_SLAUGHTER);
        if (slaughter != null && "if_custom".equals(damageType) && event.getSource().getTrueSource() == null) {
            recordFactoryKill(world, slaughter.owner);
            return;
        }

        Entity source = event.getSource().getTrueSource();
        if (source instanceof FakePlayer && source.getName().contains("mob_masher")) {
            OwnedBlock saw = ownedAdjacentBlock(world, deathPos, MOB_SAW);
            if (saw != null) recordFactoryKill(world, saw.owner);
            return;
        }
        OwnedBlock spikes = ownedAdjacentBlock(world, deathPos, MOB_SPIKES);
        if (spikes != null && "spikes".equals(damageType)) {
            recordFactoryKill(world, spikes.owner);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
            || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        checkSuccessfulRemoteOpen(player);
        checkOwnedBlocks(player);
        checkBredHogs(player);
        checkPendingCrusherKills(player);
    }

    private static void checkOwnedBlocks(EntityPlayerMP player) {
        Map<BlockPos, OwnedBlock> blocks = ownedBlocks(player.world);
        Iterator<Map.Entry<BlockPos, OwnedBlock>> iterator = blocks.entrySet().iterator();
        while (iterator.hasNext()) {
            OwnedBlock owned = iterator.next().getValue();
            if (!owned.owner.equals(player.getUniqueID()) || owned.key.dimension != player.dimension) continue;
            if (!player.world.isBlockLoaded(owned.key.position)) continue;
            String current = blockName(player.world.getBlockState(owned.key.position).getBlock().getRegistryName());
            if (!owned.matchesCurrentBlock(current)) {
                iterator.remove();
                IntegrationOwnershipData.get(player.world).remove(player.world, OWNER_PREFIX + owned.blockId,
                    owned.key.position);
                continue;
            }
            TileEntity tile = player.world.getTileEntity(owned.key.position);
            switch (owned.blockId) {
                case MASTER_BLOCK:
                    if (StorageAdapter.hasReportedInventory(tile)) grant(player, NETWORK_CHEST);
                    break;
                case TREE_EXTRACTOR:
                    checkLatex(player, owned, tile);
                    break;
                case CROP_REAPER:
                    checkReaper(player, owned, tile);
                    break;
                case CROP_SOWER:
                    checkSower(player, owned, tile);
                    break;
                case MILK_JAR:
                case COW_JAR:
                    checkCowJar(player, owned, tile, current);
                    break;
                default:
                    break;
            }
        }
    }

    private static void checkLatex(EntityPlayerMP player, OwnedBlock owned, TileEntity tile) {
        int current = extractedFluidAmount(tile, "latex");
        if (current < 0) return;
        if (owned.lastFluid >= 0 && current > owned.lastFluid && hasAdjacentWood(player, owned.key.position)) {
            owned.producedFluid += current - owned.lastFluid;
            if (owned.producedFluid >= 100) grant(player, LATEX);
        }
        owned.lastFluid = current;
    }

    private static boolean hasAdjacentWood(EntityPlayerMP player, BlockPos position) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos adjacent = position.offset(side);
            if (player.world.isBlockLoaded(adjacent)
                && player.world.getBlockState(adjacent).getBlock() instanceof BlockLog) return true;
        }
        return false;
    }

    private static void checkReaper(EntityPlayerMP player, OwnedBlock owned, TileEntity tile) {
        NBTTagCompound tag = tileNbt(tile);
        if (tag == null || !tag.hasKey("operation", Constants.NBT.TAG_INT)) return;
        int operation = tag.getInteger("operation");
        int items = itemCount(tile);
        int sludge = fluidAmount(tile, "sludge");
        if (owned.lastOperation >= 0 && operation != owned.lastOperation
            && ((items >= 0 && items > owned.lastItems) || (sludge >= 0 && sludge > owned.lastFluid))) {
            grant(player, REAPER);
        }
        owned.lastOperation = operation;
        owned.lastItems = items;
        owned.lastFluid = sludge;
    }

    private static void checkSower(EntityPlayerMP player, OwnedBlock owned, TileEntity tile) {
        int items = itemCount(tile);
        if (owned.pendingSowerItems >= 0) {
            if (items >= 0 && items < owned.pendingSowerItems) grant(player, SOWER);
            if (player.world.getTotalWorldTime() > owned.pendingSowerExpiry || items < owned.pendingSowerItems) {
                owned.pendingSowerItems = -1;
            }
        }
        owned.lastItems = items;
    }

    private static void checkCowJar(EntityPlayerMP player, OwnedBlock owned, TileEntity tile, String currentBlock) {
        if (!COW_JAR.equals(currentBlock)) return;
        int milk = fluidAmount(tile, "milk");
        if (milk < 0) {
            NBTTagCompound tag = tileNbt(tile);
            milk = tag != null && tag.hasKey("MilkAmount", Constants.NBT.TAG_FLOAT)
                ? (int) tag.getFloat("MilkAmount") : -1;
        }
        if (owned.lastFluid >= 0 && milk > owned.lastFluid) grant(player, MILK);
        owned.lastFluid = milk;
    }

    private static void recordSowerOperation(BlockPos plantedPos, WorldServer operationWorld) {
        OwnedBlock match = null;
        for (OwnedBlock owned : ownedBlocks(operationWorld).values()) {
            if (!CROP_SOWER.equals(owned.blockId)) continue;
            WorldServer world = operationWorld;
            if (world == null || !world.isBlockLoaded(owned.key.position)) continue;
            TileEntity tile = world.getTileEntity(owned.key.position);
            AxisAlignedBB area = IndustrialAdapter.workingArea(tile);
            if (area == null || !area.contains(new Vec3d(plantedPos).addVector(0.5D, 0.5D, 0.5D))) continue;
            if (match != null) return;
            match = owned;
        }
        if (match != null && match.lastItems >= 0) {
            match.pendingSowerItems = match.lastItems;
            match.pendingSowerExpiry = operationWorld.getTotalWorldTime() + 4L;
        }
    }

    private static void checkSuccessfulRemoteOpen(EntityPlayerMP player) {
        RemoteView remote = StorageAdapter.readRemoteContainer(player.openContainer);
        if (remote == null || remote.master == null || remote.stack.isEmpty()
            || !REMOTE_ITEM.equals(itemName(remote.stack)) || remote.stack.getMetadata() < 0
            || remote.stack.getMetadata() > 3) return;
        BlockKey masterKey = new BlockKey(remote.master.getWorld().provider.getDimension(), remote.master.getPos());
        OwnedBlock master = ownedBlocks(remote.master.getWorld()).get(masterKey.position);
        if (master == null || !master.owner.equals(player.getUniqueID()) || !MASTER_BLOCK.equals(master.blockId)) return;
        double distance = player.dimension == masterKey.dimension
            ? player.getDistanceSqToCenter(masterKey.position) : Double.POSITIVE_INFINITY;
        if (distance >= 16.0D * 16.0D && StorageAdapter.hasReportedInventory(remote.master)) grant(player, REMOTE);
    }

    private static void checkBredHogs(EntityPlayerMP player) {
        ResourceHogAttributionData data = ResourceHogAttributionData.get(player.world);
        Set<UUID> persisted = new HashSet<>();
        for (ResourceHogAttributionData.Record record : data.records(player.world, player.getUniqueID())) {
            persisted.add(record.entity);
            HogState current = BRED_HOGS.get(record.entity);
            if (current == null || !current.owner.equals(record.owner) || current.dimension != record.dimension) {
                BRED_HOGS.put(record.entity, new HogState(record.owner, record.dimension,
                    record.position, record.seenAt));
            }
        }
        BRED_HOGS.entrySet().removeIf(entry -> entry.getValue().owner.equals(player.getUniqueID())
            && entry.getValue().dimension == player.dimension && !persisted.contains(entry.getKey()));

        Iterator<Map.Entry<UUID, HogState>> iterator = BRED_HOGS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, HogState> entry = iterator.next();
            HogState state = entry.getValue();
            if (!state.owner.equals(player.getUniqueID()) || state.dimension != player.dimension) continue;
            Entity hog = player.getServerWorld().getEntityFromUuid(entry.getKey());
            if (hog == null) {
                if (state.lastPosition != null && player.world.isBlockLoaded(state.lastPosition)
                    && player.world.getTotalWorldTime() - state.lastSeen >= HOG_STALE_TICKS) {
                    iterator.remove();
                    data.remove(entry.getKey());
                }
                continue;
            }
            if (!hog.isEntityAlive() || !ResourceHogAdapter.isResourceHog(hog)) {
                iterator.remove();
                data.remove(entry.getKey());
                continue;
            }
            BlockPos position = new BlockPos(hog);
            boolean truffle = TRUFFLE_BLOCK.equals(blockName(player.world.getBlockState(position).getBlock().getRegistryName()));
            if (state.observed && position.equals(state.lastPosition) && !state.lastTruffle && truffle) {
                grant(player, TRUFFLE);
            }
            long now = player.world.getTotalWorldTime();
            if (!position.equals(state.lastPosition) || now - state.lastSeen >= HOG_STALE_TICKS) {
                data.update(entry.getKey(), position, now);
                state.lastSeen = now;
            }
            state.lastPosition = position.toImmutable();
            state.lastTruffle = truffle;
            state.observed = true;
        }
    }

    private static void checkPendingCrusherKills(EntityPlayerMP player) {
        Iterator<PendingCrusherKill> iterator = PENDING_CRUSHER_KILLS.iterator();
        while (iterator.hasNext()) {
            PendingCrusherKill pending = iterator.next();
            if (!pending.machine.owner.equals(player.getUniqueID())) continue;
            if (pending.machine.key.dimension != player.dimension) continue;
            TileEntity tile = player.world.getTileEntity(pending.machine.key.position);
            Map<String, Integer> items = itemCounts(tile);
            int fluid = fluidAmount(tile, "essence");
            if (hasPositiveItemDelta(pending.items, items) || (fluid >= 0 && fluid > pending.fluid)) {
                grant(player, MOB_RULES);
                recordFactoryKill(player.getServerWorld(), pending.machine.owner);
                iterator.remove();
            } else if (player.world.getTotalWorldTime() > pending.expiresAt) {
                iterator.remove();
            }
        }
    }

    private static OwnedBlock uniqueMachineAt(WorldServer world, BlockPos position, String blockId) {
        OwnedBlock match = null;
        for (OwnedBlock owned : ownedBlocks(world).values()) {
            if (owned.key.dimension != world.provider.getDimension() || !blockId.equals(owned.blockId)) continue;
            AxisAlignedBB area = IndustrialAdapter.workingArea(world.getTileEntity(owned.key.position));
            if (area == null || !area.contains(new Vec3d(position).addVector(0.5D, 0.5D, 0.5D))) continue;
            if (match != null) return null;
            match = owned;
        }
        return match;
    }

    private static OwnedBlock ownedAdjacentBlock(WorldServer world, BlockPos position, String blockId) {
        OwnedBlock match = null;
        for (BlockPos candidate : BlockPos.getAllInBoxMutable(position.add(-1, -1, -1), position.add(1, 1, 1))) {
            OwnedBlock owned = ownedBlocks(world).get(candidate);
            if (owned == null || !blockId.equals(owned.blockId)) continue;
            if (match != null) return null;
            match = owned;
        }
        return match;
    }

    private static void recordFactoryKill(WorldServer world, UUID owner) {
        EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(owner);
        if (player == null) return;
        NBTTagCompound persisted = persistedData(player);
        int kills = persisted.getInteger("sf4angelOwnedFactoryKills") + 1;
        persisted.setInteger("sf4angelOwnedFactoryKills", kills);
        if (kills >= 100) grant(player, MOB_FLOOR);
    }

    private static NBTTagCompound persistedData(EntityPlayerMP player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayerMP.PERSISTED_NBT_TAG, Constants.NBT.TAG_COMPOUND)) {
            entityData.setTag(EntityPlayerMP.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayerMP.PERSISTED_NBT_TAG);
    }

    private static int itemCount(TileEntity tile) {
        if (tile == null) return -1;
        int best = -1;
        List<EnumFacing> sides = new ArrayList<>(Arrays.asList(EnumFacing.values()));
        sides.add(null);
        for (EnumFacing side : sides) {
            if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) continue;
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            int count = 0;
            for (int slot = 0; slot < handler.getSlots(); slot++) count += handler.getStackInSlot(slot).getCount();
            best = Math.max(best, count);
        }
        return best;
    }

    private static Map<String, Integer> itemCounts(TileEntity tile) {
        Map<String, Integer> result = new HashMap<>();
        if (tile == null) return result;
        for (EnumFacing side : EnumFacing.values()) {
            if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) continue;
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) result.merge(itemName(stack) + '@' + stack.getMetadata() + '#'
                    + (stack.hasTagCompound() ? stack.getTagCompound().toString() : ""), stack.getCount(), Integer::sum);
            }
            break;
        }
        return result;
    }

    private static boolean hasPositiveItemDelta(Map<String, Integer> before, Map<String, Integer> after) {
        for (String item : after.keySet()) {
            if (after.get(item) > before.getOrDefault(item, 0)) return true;
        }
        return false;
    }

    private static int fluidAmount(TileEntity tile, String requiredFluid) {
        if (tile == null) return -1;
        int best = -1;
        List<EnumFacing> sides = new ArrayList<>(Arrays.asList(EnumFacing.values()));
        sides.add(null);
        for (EnumFacing side : sides) {
            if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) continue;
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            int amount = 0;
            boolean found = requiredFluid == null;
            for (IFluidTankProperties property : handler.getTankProperties()) {
                FluidStack contents = property.getContents();
                if (contents == null || contents.getFluid() == null) continue;
                if (requiredFluid == null || requiredFluid.equals(contents.getFluid().getName())) {
                    amount += contents.amount;
                    found = true;
                }
            }
            if (found) best = Math.max(best, amount);
        }
        return best;
    }

    private static int extractedFluidAmount(TileEntity tile, String requiredFluid) {
        if (tile == null) return -1;
        int best = -1;
        for (EnumFacing side : EnumFacing.values()) {
            if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) continue;
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            int amount = 0;
            boolean found = false;
            for (IFluidTankProperties property : handler.getTankProperties()) {
                FluidStack contents = property.getContents();
                if (property.canFill() || contents == null || contents.getFluid() == null
                    || !requiredFluid.equals(contents.getFluid().getName())) continue;
                amount += contents.amount;
                found = true;
            }
            if (found) best = Math.max(best, amount);
        }
        return best;
    }

    private static NBTTagCompound tileNbt(TileEntity tile) {
        if (tile == null) return null;
        try {
            return tile.writeToNBT(new NBTTagCompound());
        } catch (RuntimeException | LinkageError exception) {
            return null;
        }
    }

    private static void grant(EntityPlayerMP player, String advancement) {
        Set<String> submitted = SUBMITTED.computeIfAbsent(player.getUniqueID(), ignored -> new HashSet<>());
        if (submitted.add(advancement)) AchievementHandler.grantCriterion(player, advancement);
    }

    private static String itemName(ItemStack stack) {
        return stack.isEmpty() ? "" : blockName(stack.getItem().getRegistryName());
    }

    private static String blockName(ResourceLocation name) {
        return name == null ? "" : name.toString();
    }

    private static Map<BlockPos, OwnedBlock> ownedBlocks(World world) {
        Map<BlockPos, OwnedBlock> states = OWNED_BLOCKS.computeIfAbsent(world, ignored -> new HashMap<>());
        Set<BlockPos> persisted = new HashSet<>();
        for (IntegrationOwnershipData.Record record : IntegrationOwnershipData.get(world).records(world, OWNER_PREFIX)) {
            String blockId = record.kind.substring(OWNER_PREFIX.length());
            if (!TRACKED_BLOCKS.contains(blockId)) continue;
            persisted.add(record.position);
            OwnedBlock current = states.get(record.position);
            if (current == null || !current.owner.equals(record.owner) || !current.blockId.equals(blockId)) {
                BlockKey key = new BlockKey(world.provider.getDimension(), record.position);
                states.put(record.position, new OwnedBlock(record.owner, blockId, key));
            }
        }
        states.keySet().retainAll(persisted);
        return states;
    }

    private static final class BlockKey {
        private final int dimension;
        private final BlockPos position;

        private BlockKey(int dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof BlockKey)) return false;
            BlockKey that = (BlockKey) other;
            return dimension == that.dimension && position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return dimension * 31 + position.hashCode();
        }
    }

    private static final class OwnedBlock {
        private final UUID owner;
        private final String blockId;
        private final BlockKey key;
        private int lastFluid = -1;
        private int producedFluid;
        private int lastOperation = -1;
        private int lastItems = -1;
        private int pendingSowerItems = -1;
        private long pendingSowerExpiry;

        private OwnedBlock(UUID owner, String blockId, BlockKey key) {
            this.owner = owner;
            this.blockId = blockId;
            this.key = key;
        }

        private boolean matchesCurrentBlock(String current) {
            return blockId.equals(current) || (MILK_JAR.equals(blockId) && COW_JAR.equals(current));
        }
    }

    private static final class HogState {
        private final UUID owner;
        private final int dimension;
        private BlockPos lastPosition;
        private long lastSeen;
        private boolean lastTruffle;
        private boolean observed;

        private HogState(UUID owner, int dimension, BlockPos lastPosition, long lastSeen) {
            this.owner = owner;
            this.dimension = dimension;
            this.lastPosition = lastPosition.toImmutable();
            this.lastSeen = lastSeen;
        }
    }

    private static final class PendingCrusherKill {
        private final OwnedBlock machine;
        private final Map<String, Integer> items;
        private final int fluid;
        private final long expiresAt;

        private PendingCrusherKill(OwnedBlock machine, Map<String, Integer> items, int fluid, long expiresAt) {
            this.machine = machine;
            this.items = items;
            this.fluid = fluid;
            this.expiresAt = expiresAt;
        }
    }

    private static final class RemoteView {
        private final TileEntity master;
        private final ItemStack stack;

        private RemoteView(TileEntity master, ItemStack stack) {
            this.master = master;
            this.stack = stack;
        }
    }

    private static final class StorageAdapter {
        private static boolean initialized;
        private static boolean available;
        private static boolean warned;
        private static Class<?> masterClass;
        private static Class<?> remoteContainerClass;
        private static Class<?> cableClass;
        private static Method getConnectablePositions;
        private static Method getStacks;
        private static Method getDimPosBlockPos;
        private static Method getCableFacingPosition;
        private static Method getRemoteMaster;
        private static Method getRemoteStack;

        private static boolean hasReportedInventory(TileEntity tile) {
            initialize();
            if (!available || tile == null || !masterClass.isInstance(tile)) return false;
            try {
                Object positions = getConnectablePositions.invoke(tile);
                Object stacks = getStacks.invoke(tile);
                if (!(positions instanceof Collection) || !(stacks instanceof List)) return false;
                for (Object dimPos : (Collection<?>) positions) {
                    Object value = getDimPosBlockPos.invoke(dimPos);
                    if (!(value instanceof BlockPos)) continue;
                    BlockPos cablePos = (BlockPos) value;
                    if (!tile.getWorld().isBlockLoaded(cablePos)
                        || !STORAGE_CABLE_BLOCK.equals(blockName(tile.getWorld().getBlockState(cablePos)
                        .getBlock().getRegistryName()))) continue;
                    TileEntity cable = tile.getWorld().getTileEntity(cablePos);
                    if (cable == null || !cableClass.isInstance(cable)) continue;
                    Object facingPosition = getCableFacingPosition.invoke(cable);
                    if (!(facingPosition instanceof BlockPos)) continue;
                    TileEntity inventory = tile.getWorld().getTileEntity((BlockPos) facingPosition);
                    if (itemCount(inventory) >= 0) return true;
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                disable("Simple Storage integration disabled after an incompatible call", exception);
            }
            return false;
        }

        private static RemoteView readRemoteContainer(Object container) {
            initialize();
            if (!available || container == null || !remoteContainerClass.isInstance(container)) return null;
            try {
                Object master = getRemoteMaster.invoke(container);
                Object stack = getRemoteStack.invoke(container);
                return master instanceof TileEntity && stack instanceof ItemStack
                    ? new RemoteView((TileEntity) master, (ItemStack) stack) : null;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                disable("Simple Storage remote integration disabled after an incompatible call", exception);
                return null;
            }
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            if (!Loader.isModLoaded("storagenetwork")) return;
            try {
                masterClass = Class.forName("mrriegel.storagenetwork.block.master.TileMaster");
                remoteContainerClass = Class.forName("mrriegel.storagenetwork.item.remote.ContainerRemote");
                cableClass = Class.forName("mrriegel.storagenetwork.block.cable.TileCableWithFacing");
                Class<?> dimPosClass = Class.forName("mrriegel.storagenetwork.api.data.DimPos");
                getConnectablePositions = masterClass.getMethod("getConnectablePositions");
                getStacks = masterClass.getMethod("getStacks");
                getDimPosBlockPos = dimPosClass.getMethod("getBlockPos");
                getCableFacingPosition = cableClass.getMethod("getFacingPosition");
                getRemoteMaster = remoteContainerClass.getMethod("getTileMaster");
                getRemoteStack = remoteContainerClass.getMethod("getItemRemote");
                available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                disable("Simple Storage integration unavailable for the installed API", exception);
            }
        }

        private static void disable(String message, Throwable exception) {
            available = false;
            if (!warned) {
                warned = true;
                LOGGER.warn("{}: {}", message, exception.toString());
            }
        }
    }

    private static final class IndustrialAdapter {
        private static boolean initialized;
        private static boolean available;
        private static boolean warned;
        private static Class<?> workingMachineClass;
        private static Method getWorkingArea;

        private static AxisAlignedBB workingArea(TileEntity tile) {
            initialize();
            if (!available || tile == null || !workingMachineClass.isInstance(tile)) return null;
            try {
                Object area = getWorkingArea.invoke(tile);
                return area instanceof AxisAlignedBB ? (AxisAlignedBB) area : null;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                available = false;
                if (!warned) {
                    warned = true;
                    LOGGER.warn("Industrial Foregoing integration disabled after an incompatible call: {}",
                        exception.toString());
                }
                return null;
            }
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            if (!Loader.isModLoaded("industrialforegoing")) return;
            try {
                workingMachineClass = Class.forName("com.buuz135.industrial.tile.WorkingAreaElectricMachine");
                getWorkingArea = workingMachineClass.getMethod("getWorkingArea");
                available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                if (!warned) {
                    warned = true;
                    LOGGER.warn("Industrial Foregoing integration unavailable for the installed API: {}",
                        exception.toString());
                }
            }
        }
    }

    private static final class ResourceHogAdapter {
        private static boolean initialized;
        private static Class<?> hogClass;

        private static boolean isResourceHog(Entity entity) {
            if (!initialized) {
                initialized = true;
                if (Loader.isModLoaded("resourcehogs")) {
                    try {
                        hogClass = Class.forName("net.darkhax.resourcehogs.entity.EntityResourceHog");
                    } catch (ReflectiveOperationException | LinkageError exception) {
                        LOGGER.warn("Resource Hogs integration unavailable for the installed API: {}",
                            exception.toString());
                    }
                }
            }
            return hogClass != null && hogClass.isInstance(entity);
        }
    }
}
