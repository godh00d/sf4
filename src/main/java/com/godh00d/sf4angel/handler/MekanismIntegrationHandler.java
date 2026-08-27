package com.godh00d.sf4angel.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Runtime-only integrations. No optional-mod type is linked at compile time. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class MekanismIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String DATA_NAME = "sf4angel_machine_owners";
    private static final int POLL_INTERVAL = 5;
    private static final long LINE_WINDOW_TICKS = 2400;
    private static final double MAX_LINE_DISTANCE_SQ = 64 * 64;
    private static final String TRIPLE_ID = "sf4angel:core/triple_threat";
    private static final String FIVE_ID = "sf4angel:core/five_times_the_charm";
    private static final String SMELTERY_ID = "sf4angel:core/smeltery_authority";
    private static final String HYDROGEN_ID = "sf4angel:core/hydrogen_economy";
    private static final String GAS_GENERATOR_ID = "sf4angel:core/gas_grass_or_rf";
    private static final String DIGITAL_MINER_ID = "sf4angel:core/digital_prospector";
    private static final String MATRIX_ID = "sf4angel:core/matrix_reloaded";
    private static final String TURBINE_ID = "sf4angel:core/turbine_service";
    private static final Map<MachineKey, MachineState> STATES = new HashMap<>();
    private static final Map<MachineKey, LineObservation> LINE_STATES = new HashMap<>();
    private static final Map<String, Method> CACHED_METHODS = new HashMap<>();
    private static final Map<String, Field> CACHED_FIELDS = new HashMap<>();

    private static final ReflectionType SMELTERY_TYPE = new ReflectionType(
        "slimeknights.tconstruct.smeltery.tileentity.TileSmeltery");
    private static final ReflectionType SEPARATOR_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityElectrolyticSeparator");
    private static final ReflectionType GAS_GENERATOR_TYPE = new ReflectionType(
        "mekanism.generators.common.tile.TileEntityGasGenerator");
    private static final ReflectionType MINER_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityDigitalMiner");
    private static final ReflectionType MATRIX_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityInductionCasing");
    private static final ReflectionType TURBINE_TYPE = new ReflectionType(
        "mekanism.generators.common.tile.turbine.TileEntityTurbineCasing");
    private static final ReflectionType DRAIN_TYPE = new ReflectionType(
        "slimeknights.tconstruct.smeltery.tileentity.TileDrain");
    private static final ReflectionType PURIFICATION_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityPurificationChamber");
    private static final ReflectionType CRUSHER_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityCrusher");
    private static final ReflectionType ENRICHMENT_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityEnrichmentChamber");
    private static final ReflectionType SMELTER_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityEnergizedSmelter");
    private static final ReflectionType DISSOLUTION_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityChemicalDissolutionChamber");
    private static final ReflectionType WASHER_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityChemicalWasher");
    private static final ReflectionType CRYSTALLIZER_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityChemicalCrystallizer");
    private static final ReflectionType INJECTION_TYPE = new ReflectionType(
        "mekanism.common.tile.TileEntityChemicalInjectionChamber");

    private MekanismIntegrationHandler() {
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getWorld() instanceof WorldServer)
            || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        WorldServer world = (WorldServer) event.getWorld();
        TileEntity tile = world.getTileEntity(event.getPos());
        MachineKind kind = classify(tile);
        if (kind == null) return;

        OwnershipData data = ownership(world);
        data.put(world.provider.getDimension(), event.getPos(), event.getPlayer().getUniqueID(), kind);
        MachineKey key = new MachineKey(world.provider.getDimension(), event.getPos());
        STATES.put(key, initialState(kind, tile, world));
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || !(event.getWorld() instanceof WorldServer)) return;
        WorldServer world = (WorldServer) event.getWorld();
        OwnershipData data = ownership(world);
        if (data.remove(world.provider.getDimension(), event.getPos())) {
            MachineKey key = new MachineKey(world.provider.getDimension(), event.getPos());
            STATES.remove(key);
            LINE_STATES.remove(key);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || !(event.world instanceof WorldServer)
            || event.world.getTotalWorldTime() % POLL_INTERVAL != 0) return;
        poll((WorldServer) event.world);
    }

    private static void poll(WorldServer world) {
        OwnershipData data = ownership(world);
        Iterator<Map.Entry<Long, OwnerRecord>> iterator = data.records.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, OwnerRecord> entry = iterator.next();
            OwnerRecord record = entry.getValue();
            if (record.dimension != world.provider.getDimension()) {
                iterator.remove();
                data.markDirty();
                continue;
            }
            BlockPos pos = BlockPos.fromLong(entry.getKey());
            if (!world.isBlockLoaded(pos)) continue;
            TileEntity tile = world.getTileEntity(pos);
            if (!record.kind.type.isInstance(tile)) {
                iterator.remove();
                STATES.remove(new MachineKey(record.dimension, pos));
                data.markDirty();
                continue;
            }
            EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(record.owner);
            MachineKey key = new MachineKey(record.dimension, pos);
            MachineState previous = STATES.get(key);
            MachineState current = readState(record.kind, tile, world);
            if (current == null) {
                STATES.remove(key);
                continue;
            }
            if (owner != null && previous != null && completed(record.kind, previous, current)) {
                AchievementHandler.grantCriterion(owner, record.kind.advancement);
            }
            STATES.put(key, current);
        }
        pollProcessingLines(world, data);
    }

    private static MachineKind classify(TileEntity tile) {
        if (SMELTERY_TYPE.isInstance(tile)) return MachineKind.SMELTERY;
        if (SEPARATOR_TYPE.isInstance(tile)) return MachineKind.SEPARATOR;
        if (GAS_GENERATOR_TYPE.isInstance(tile)) return MachineKind.GAS_GENERATOR;
        if (MINER_TYPE.isInstance(tile)) return MachineKind.DIGITAL_MINER;
        if (MATRIX_TYPE.isInstance(tile)) return MachineKind.MATRIX;
        if (TURBINE_TYPE.isInstance(tile)) return MachineKind.TURBINE;
        if (PURIFICATION_TYPE.isInstance(tile)) return MachineKind.PURIFICATION;
        if (CRUSHER_TYPE.isInstance(tile)) return MachineKind.CRUSHER;
        if (ENRICHMENT_TYPE.isInstance(tile)) return MachineKind.ENRICHMENT;
        if (SMELTER_TYPE.isInstance(tile)) return MachineKind.SMELTER;
        if (DISSOLUTION_TYPE.isInstance(tile)) return MachineKind.DISSOLUTION;
        if (WASHER_TYPE.isInstance(tile)) return MachineKind.WASHER;
        if (CRYSTALLIZER_TYPE.isInstance(tile)) return MachineKind.CRYSTALLIZER;
        if (INJECTION_TYPE.isInstance(tile)) return MachineKind.INJECTION;
        return null;
    }

    private static MachineState initialState(MachineKind kind, TileEntity tile, World world) {
        // Before this placement the new controller/casing could not have a valid structure.
        if (kind == MachineKind.SMELTERY || kind == MachineKind.MATRIX) return new MachineState();
        MachineState state = readState(kind, tile, world);
        return state == null ? new MachineState() : state;
    }

    private static MachineState readState(MachineKind kind, TileEntity tile, World world) {
        try {
            switch (kind) {
                case SMELTERY:
                    return readSmeltery(tile, world);
                case SEPARATOR:
                    return readSeparator(tile);
                case GAS_GENERATOR:
                    return readGasGenerator(tile);
                case DIGITAL_MINER:
                    return readMiner(tile);
                case MATRIX:
                    return readMatrix(tile);
                case TURBINE:
                    return readTurbine(tile);
                case PURIFICATION:
                case CRUSHER:
                case ENRICHMENT:
                case SMELTER:
                case DISSOLUTION:
                case WASHER:
                case CRYSTALLIZER:
                case INJECTION:
                    return new MachineState();
                default:
                    return null;
            }
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            kind.type.disable(exception);
            return null;
        }
    }

    private static MachineState readSmeltery(TileEntity tile, World world) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        boolean active = (Boolean) SMELTERY_TYPE.method("isActive").invoke(tile);
        Object tank = SMELTERY_TYPE.method("getTank").invoke(tile);
        int capacity = ((Number) cachedMethod(tank.getClass(), "getCapacity").invoke(tank)).intValue();
        @SuppressWarnings("unchecked")
        List<BlockPos> tanks = (List<BlockPos>) SMELTERY_TYPE.field("tanks").get(tile);
        BlockPos min = (BlockPos) SMELTERY_TYPE.method("getMinPos").invoke(tile);
        BlockPos max = (BlockPos) SMELTERY_TYPE.method("getMaxPos").invoke(tile);
        boolean drain = false;
        if (active && min != null && max != null) {
            for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max)) {
                if (world.isBlockLoaded(pos) && DRAIN_TYPE.isInstance(world.getTileEntity(pos))) {
                    drain = true;
                    break;
                }
            }
        }
        state.valid = active && tanks != null && !tanks.isEmpty() && drain && capacity >= 18 * 144;
        return state;
    }

    private static MachineState readSeparator(TileEntity tile) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        Object fluidTank = SEPARATOR_TYPE.field("fluidTank").get(tile);
        state.first = ((Number) cachedMethod(fluidTank.getClass(), "getFluidAmount").invoke(fluidTank)).longValue();
        GasValue left = gasValue(SEPARATOR_TYPE.field("leftTank").get(tile));
        GasValue right = gasValue(SEPARATOR_TYPE.field("rightTank").get(tile));
        state.second = left.amount;
        state.third = right.amount;
        state.firstName = left.name;
        state.secondName = right.name;
        return state;
    }

    private static MachineState readGasGenerator(TileEntity tile) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        GasValue fuel = gasValue(GAS_GENERATOR_TYPE.field("fuelTank").get(tile));
        state.first = fuel.amount;
        state.firstName = fuel.name;
        state.second = ((Number) GAS_GENERATOR_TYPE.field("burnTicks").get(tile)).longValue();
        state.number = ((Number) GAS_GENERATOR_TYPE.field("generationRate").get(tile)).doubleValue();
        return state;
    }

    private static MachineState readMiner(TileEntity tile) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        @SuppressWarnings("unchecked")
        Map<Object, BitSet> ores = (Map<Object, BitSet>) MINER_TYPE.field("oresToMine").get(tile);
        long remaining = 0;
        if (ores != null) for (BitSet bits : ores.values()) remaining += bits.cardinality();
        state.first = remaining;
        state.valid = (Boolean) MINER_TYPE.field("running").get(tile)
            && (Boolean) MINER_TYPE.field("doEject").get(tile)
            && ((Number) cachedMethod(MINER_TYPE.field("filters").get(tile).getClass(), "size")
                .invoke(MINER_TYPE.field("filters").get(tile))).intValue() > 0;
        @SuppressWarnings("unchecked")
        List<ItemStack> inventory = (List<ItemStack>) MINER_TYPE.field("inventory").get(tile);
        state.ownItems = itemCounts(inventory);
        Object destination = MINER_TYPE.method("getEjectInv").invoke(tile);
        state.items = itemCounts(destination instanceof TileEntity ? (TileEntity) destination : null);
        return state;
    }

    private static MachineState readMatrix(TileEntity tile) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        Object structure = MATRIX_TYPE.field("structure").get(tile);
        int cells = ((Number) MATRIX_TYPE.method("getCellCount").invoke(tile)).intValue();
        int providers = ((Number) MATRIX_TYPE.method("getProviderCount").invoke(tile)).intValue();
        state.valid = structure != null && cells > 0 && providers > 0;
        return state;
    }

    private static MachineState readTurbine(TileEntity tile) throws ReflectiveOperationException {
        MachineState state = new MachineState();
        Object structure = TURBINE_TYPE.field("structure").get(tile);
        if (structure == null) return state;
        state.valid = true;
        state.first = ((Number) cachedField(structure.getClass(), "clientFlow").get(structure)).longValue();
        state.second = ((Number) cachedField(structure.getClass(), "lastSteamInput").get(structure)).longValue();
        state.number = ((Number) TURBINE_TYPE.method("getEnergy").invoke(tile)).doubleValue();
        state.third = adjacentEnergy(tile);
        return state;
    }

    private static boolean completed(MachineKind kind, MachineState before, MachineState after) {
        switch (kind) {
            case SMELTERY:
            case MATRIX:
                return !before.valid && after.valid;
            case SEPARATOR:
                return before.first > after.first && after.second > before.second && after.third > before.third
                    && isHydrogenAndOxygen(after.firstName, after.secondName);
            case GAS_GENERATOR:
                return "ethylene".equals(before.firstName) && before.first > after.first
                    && after.second > before.second && after.number > 0.0D;
            case DIGITAL_MINER:
                after.pendingItems.putAll(before.pendingItems);
                if (before.valid && after.valid && before.first > after.first) {
                    addPositiveItemDeltas(before.ownItems, after.ownItems, after.pendingItems);
                }
                return exportedPendingItem(before, after);
            case TURBINE:
                return before.valid && after.valid && after.first > 0 && after.second > 0
                    && before.third >= 0 && after.third > before.third && after.number < before.number;
            default:
                return false;
        }
    }

    private static boolean isHydrogenAndOxygen(String left, String right) {
        return ("hydrogen".equals(left) && "oxygen".equals(right))
            || ("oxygen".equals(left) && "hydrogen".equals(right));
    }

    private static void pollProcessingLines(WorldServer world, OwnershipData data) {
        Set<MachineKey> seenSources = new HashSet<>();
        for (Map.Entry<Long, OwnerRecord> entry : data.records.entrySet()) {
            OwnerRecord source = entry.getValue();
            LineDefinition definition = LineDefinition.forSource(source.kind);
            if (definition == null) continue;
            MachineKey key = new MachineKey(source.dimension, BlockPos.fromLong(entry.getKey()));
            seenSources.add(key);
            try {
                LineSnapshot snapshot = readLineSnapshot(world, data, source, key.position, definition);
                if (snapshot == null) {
                    LINE_STATES.remove(key);
                    continue;
                }
                LineObservation observation = LINE_STATES.get(key);
                long now = world.getTotalWorldTime();
                if (observation == null || now > observation.expiresAt || !observation.matches(snapshot)) {
                    LINE_STATES.put(key, new LineObservation(snapshot, now + LINE_WINDOW_TICKS));
                    continue;
                }
                observation.record(snapshot);
                if (observation.sourceConsumed > 0 && observation.outputProduced >= definition.outputCount) {
                    EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(source.owner);
                    if (owner != null) AchievementHandler.grantCriterion(owner, definition.advancement);
                    LINE_STATES.remove(key);
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                source.kind.type.disable(exception);
                LINE_STATES.remove(key);
            }
        }
        Iterator<MachineKey> observations = LINE_STATES.keySet().iterator();
        while (observations.hasNext()) {
            MachineKey key = observations.next();
            if (key.dimension == world.provider.getDimension() && !seenSources.contains(key)) observations.remove();
        }
    }

    private static LineSnapshot readLineSnapshot(WorldServer world, OwnershipData data, OwnerRecord source,
                                                  BlockPos sourcePos, LineDefinition definition)
        throws ReflectiveOperationException {
        if (source.dimension != world.provider.getDimension()) return null;
        MachineKey[] machines = new MachineKey[definition.required.length];
        TileEntity sourceTile = null;
        TileEntity outputTile = null;
        for (int index = 0; index < definition.required.length; index++) {
            MachineKind required = definition.required[index];
            MachineKey nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (Map.Entry<Long, OwnerRecord> entry : data.records.entrySet()) {
                OwnerRecord candidate = entry.getValue();
                if (candidate.dimension != source.dimension || candidate.kind != required
                    || !candidate.owner.equals(source.owner)) continue;
                BlockPos pos = BlockPos.fromLong(entry.getKey());
                double distance = pos.distanceSq(sourcePos);
                if (distance > MAX_LINE_DISTANCE_SQ || distance >= nearestDistance || !world.isBlockLoaded(pos)) continue;
                TileEntity tile = world.getTileEntity(pos);
                if (!required.type.isInstance(tile)) continue;
                nearest = new MachineKey(candidate.dimension, pos);
                nearestDistance = distance;
            }
            if (nearest == null) return null;
            TileEntity tile = world.getTileEntity(nearest.position);
            if (!(Boolean) cachedMethod(tile.getClass(), "getActive").invoke(tile)) return null;
            machines[index] = nearest;
            if (required == definition.source) sourceTile = tile;
            if (required == definition.output) outputTile = tile;
        }
        if (sourceTile == null || outputTile == null) return null;
        ItemStack sourceStack = inventorySlot(sourceTile, definition.sourceSlot);
        Set<String> materials = oreMaterials(sourceStack);
        if (sourceStack.isEmpty() || materials.isEmpty()) return null;
        ItemStack outputStack = inventorySlot(outputTile, definition.outputSlot);
        int output = correspondingOutputCount(outputStack, definition.outputPrefix, materials);
        return new LineSnapshot(machines, materials, sourceStack.getCount(), output);
    }

    @SuppressWarnings("unchecked")
    private static ItemStack inventorySlot(TileEntity tile, int slot) throws ReflectiveOperationException {
        List<ItemStack> inventory = (List<ItemStack>) cachedField(tile.getClass(), "inventory").get(tile);
        if (inventory == null || slot < 0 || slot >= inventory.size()) return ItemStack.EMPTY;
        return inventory.get(slot);
    }

    private static Set<String> oreMaterials(ItemStack stack) {
        Set<String> materials = new HashSet<>();
        if (stack.isEmpty()) return materials;
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith("ore") && name.length() > 3) materials.add(name.substring(3));
        }
        return materials;
    }

    private static int correspondingOutputCount(ItemStack stack, String prefix, Set<String> materials) {
        if (stack.isEmpty()) return 0;
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith(prefix) && materials.contains(name.substring(prefix.length()))) {
                return stack.getCount();
            }
        }
        return 0;
    }

    private static GasValue gasValue(Object tank) throws ReflectiveOperationException {
        int amount = ((Number) cachedMethod(tank.getClass(), "getStored").invoke(tank)).intValue();
        Object gas = cachedMethod(tank.getClass(), "getGasType").invoke(tank);
        String name = gas == null ? "" : (String) cachedMethod(gas.getClass(), "getName").invoke(gas);
        return new GasValue(name, amount);
    }

    private static Method cachedMethod(Class<?> type, String name) throws NoSuchMethodException {
        String key = type.getName() + '#' + name;
        Method method = CACHED_METHODS.get(key);
        if (method == null) {
            method = type.getMethod(name);
            method.setAccessible(true);
            CACHED_METHODS.put(key, method);
        }
        return method;
    }

    private static Field cachedField(Class<?> type, String name) throws NoSuchFieldException {
        String key = type.getName() + '#' + name;
        Field field = CACHED_FIELDS.get(key);
        if (field == null) {
            field = type.getField(name);
            field.setAccessible(true);
            CACHED_FIELDS.put(key, field);
        }
        return field;
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
                if (stack.isEmpty() || stack.getItem().getRegistryName() == null) continue;
                String key = stack.getItem().getRegistryName() + "@" + stack.getMetadata();
                result.merge(key, stack.getCount(), Integer::sum);
            }
            break;
        }
        return result;
    }

    private static Map<String, Integer> itemCounts(List<ItemStack> inventory) {
        Map<String, Integer> result = new HashMap<>();
        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || stack.getItem().getRegistryName() == null) continue;
            String key = stack.getItem().getRegistryName() + "@" + stack.getMetadata();
            result.merge(key, stack.getCount(), Integer::sum);
        }
        return result;
    }

    private static void addPositiveItemDeltas(Map<String, Integer> before, Map<String, Integer> after,
                                               Map<String, Integer> pending) {
        for (Map.Entry<String, Integer> entry : after.entrySet()) {
            int increase = entry.getValue() - before.getOrDefault(entry.getKey(), 0);
            if (increase > 0) pending.merge(entry.getKey(), increase, Integer::sum);
        }
    }

    private static boolean exportedPendingItem(MachineState before, MachineState after) {
        for (Map.Entry<String, Integer> entry : before.pendingItems.entrySet()) {
            String item = entry.getKey();
            int leftMiner = before.ownItems.getOrDefault(item, 0) - after.ownItems.getOrDefault(item, 0);
            int enteredTarget = after.items.getOrDefault(item, 0) - before.items.getOrDefault(item, 0);
            if (leftMiner > 0 && enteredTarget > 0) return true;
        }
        return false;
    }

    private static long adjacentEnergy(TileEntity tile) {
        long total = 0L;
        boolean found = false;
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity neighbor = tile.getWorld().getTileEntity(tile.getPos().offset(side));
            EnumFacing face = side.getOpposite();
            if (neighbor == null || !neighbor.hasCapability(CapabilityEnergy.ENERGY, face)) continue;
            IEnergyStorage energy = neighbor.getCapability(CapabilityEnergy.ENERGY, face);
            if (energy == null || !energy.canReceive()) continue;
            found = true;
            total += energy.getEnergyStored();
        }
        return found ? total : -1L;
    }

    private static OwnershipData ownership(WorldServer world) {
        OwnershipData data = (OwnershipData) world.getPerWorldStorage().getOrLoadData(OwnershipData.class, DATA_NAME);
        if (data == null) {
            data = new OwnershipData(DATA_NAME);
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }
        return data;
    }

    private enum MachineKind {
        SMELTERY("smeltery", SMELTERY_ID, SMELTERY_TYPE),
        SEPARATOR("separator", HYDROGEN_ID, SEPARATOR_TYPE),
        GAS_GENERATOR("gas_generator", GAS_GENERATOR_ID, GAS_GENERATOR_TYPE),
        DIGITAL_MINER("digital_miner", DIGITAL_MINER_ID, MINER_TYPE),
        MATRIX("matrix", MATRIX_ID, MATRIX_TYPE),
        TURBINE("turbine", TURBINE_ID, TURBINE_TYPE),
        PURIFICATION("purification", "", PURIFICATION_TYPE),
        CRUSHER("crusher", "", CRUSHER_TYPE),
        ENRICHMENT("enrichment", "", ENRICHMENT_TYPE),
        SMELTER("smelter", "", SMELTER_TYPE),
        DISSOLUTION("dissolution", "", DISSOLUTION_TYPE),
        WASHER("washer", "", WASHER_TYPE),
        CRYSTALLIZER("crystallizer", "", CRYSTALLIZER_TYPE),
        INJECTION("injection", "", INJECTION_TYPE);

        private final String serializedName;
        private final String advancement;
        private final ReflectionType type;

        MachineKind(String serializedName, String advancement, ReflectionType type) {
            this.serializedName = serializedName;
            this.advancement = advancement;
            this.type = type;
        }

        private static MachineKind fromName(String name) {
            for (MachineKind kind : values()) if (kind.serializedName.equals(name)) return kind;
            return null;
        }
    }

    public static final class OwnershipData extends WorldSavedData {
        private final Map<Long, OwnerRecord> records = new HashMap<>();

        public OwnershipData() {
            super(DATA_NAME);
        }

        public OwnershipData(String name) {
            super(name);
        }

        private void put(int dimension, BlockPos pos, UUID owner, MachineKind kind) {
            records.put(pos.toLong(), new OwnerRecord(dimension, owner, kind));
            markDirty();
        }

        private boolean remove(int dimension, BlockPos pos) {
            OwnerRecord record = records.get(pos.toLong());
            if (record == null || record.dimension != dimension) return false;
            records.remove(pos.toLong());
            markDirty();
            return true;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            records.clear();
            NBTTagList list = nbt.getTagList("Machines", 10);
            for (int index = 0; index < list.tagCount(); index++) {
                NBTTagCompound tag = list.getCompoundTagAt(index);
                MachineKind kind = MachineKind.fromName(tag.getString("Kind"));
                if (kind == null || !tag.hasUniqueId("Owner")) continue;
                records.put(tag.getLong("Position"),
                    new OwnerRecord(tag.getInteger("Dimension"), tag.getUniqueId("Owner"), kind));
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<Long, OwnerRecord> entry : records.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setLong("Position", entry.getKey());
                tag.setInteger("Dimension", entry.getValue().dimension);
                tag.setUniqueId("Owner", entry.getValue().owner);
                tag.setString("Kind", entry.getValue().kind.serializedName);
                list.appendTag(tag);
            }
            nbt.setTag("Machines", list);
            return nbt;
        }
    }

    private static final class OwnerRecord {
        private final int dimension;
        private final UUID owner;
        private final MachineKind kind;

        private OwnerRecord(int dimension, UUID owner, MachineKind kind) {
            this.dimension = dimension;
            this.owner = owner;
            this.kind = kind;
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

    private static final class MachineState {
        private boolean valid;
        private long first;
        private long second;
        private long third;
        private double number;
        private String firstName = "";
        private String secondName = "";
        private Map<String, Integer> items = new HashMap<>();
        private Map<String, Integer> ownItems = new HashMap<>();
        private Map<String, Integer> pendingItems = new HashMap<>();
    }

    private enum LineDefinition {
        TRIPLE(MachineKind.PURIFICATION, MachineKind.ENRICHMENT, 0, 2, "dust", 3, TRIPLE_ID,
            new MachineKind[] {MachineKind.PURIFICATION, MachineKind.CRUSHER, MachineKind.ENRICHMENT}),
        FIVE(MachineKind.DISSOLUTION, MachineKind.SMELTER, 1, 2, "ingot", 5, FIVE_ID,
            new MachineKind[] {MachineKind.DISSOLUTION, MachineKind.WASHER, MachineKind.CRYSTALLIZER,
                MachineKind.INJECTION, MachineKind.PURIFICATION, MachineKind.CRUSHER,
                MachineKind.ENRICHMENT, MachineKind.SMELTER});

        private final MachineKind source;
        private final MachineKind output;
        private final int sourceSlot;
        private final int outputSlot;
        private final String outputPrefix;
        private final int outputCount;
        private final String advancement;
        private final MachineKind[] required;

        LineDefinition(MachineKind source, MachineKind output, int sourceSlot, int outputSlot,
                       String outputPrefix, int outputCount, String advancement, MachineKind[] required) {
            this.source = source;
            this.output = output;
            this.sourceSlot = sourceSlot;
            this.outputSlot = outputSlot;
            this.outputPrefix = outputPrefix;
            this.outputCount = outputCount;
            this.advancement = advancement;
            this.required = required;
        }

        private static LineDefinition forSource(MachineKind kind) {
            for (LineDefinition definition : values()) if (definition.source == kind) return definition;
            return null;
        }
    }

    private static final class LineSnapshot {
        private final MachineKey[] machines;
        private final Set<String> materials;
        private final int sourceCount;
        private final int outputCount;

        private LineSnapshot(MachineKey[] machines, Set<String> materials, int sourceCount, int outputCount) {
            this.machines = machines;
            this.materials = materials;
            this.sourceCount = sourceCount;
            this.outputCount = outputCount;
        }
    }

    private static final class LineObservation {
        private final MachineKey[] machines;
        private final Set<String> materials;
        private final long expiresAt;
        private int lastSourceCount;
        private int lastOutputCount;
        private int sourceConsumed;
        private int outputProduced;

        private LineObservation(LineSnapshot snapshot, long expiresAt) {
            this.machines = snapshot.machines;
            this.materials = snapshot.materials;
            this.expiresAt = expiresAt;
            this.lastSourceCount = snapshot.sourceCount;
            this.lastOutputCount = snapshot.outputCount;
        }

        private boolean matches(LineSnapshot snapshot) {
            if (!materials.equals(snapshot.materials) || machines.length != snapshot.machines.length) return false;
            for (int index = 0; index < machines.length; index++) {
                if (!machines[index].equals(snapshot.machines[index])) return false;
            }
            return true;
        }

        private void record(LineSnapshot snapshot) {
            if (snapshot.sourceCount < lastSourceCount) sourceConsumed += lastSourceCount - snapshot.sourceCount;
            if (snapshot.outputCount > lastOutputCount) outputProduced += snapshot.outputCount - lastOutputCount;
            lastSourceCount = snapshot.sourceCount;
            lastOutputCount = snapshot.outputCount;
        }
    }

    private static final class GasValue {
        private final String name;
        private final int amount;

        private GasValue(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }

    private static final class ReflectionType {
        private final String className;
        private final Map<String, Method> methods = new HashMap<>();
        private final Map<String, Field> fields = new HashMap<>();
        private Class<?> type;
        private boolean initialized;
        private boolean available;
        private boolean warned;

        private ReflectionType(String className) {
            this.className = className;
        }

        private boolean isInstance(Object value) {
            initialize();
            return available && value != null && type.isInstance(value);
        }

        private Method method(String name) throws NoSuchMethodException {
            initialize();
            if (!available) throw new NoSuchMethodException(className);
            Method method = methods.get(name);
            if (method == null) {
                method = type.getMethod(name);
                method.setAccessible(true);
                methods.put(name, method);
            }
            return method;
        }

        private Field field(String name) throws NoSuchFieldException {
            initialize();
            if (!available) throw new NoSuchFieldException(className);
            Field field = fields.get(name);
            if (field != null) return field;
            Class<?> current = type;
            while (current != null) {
                try {
                    field = current.getDeclaredField(name);
                    field.setAccessible(true);
                    fields.put(name, field);
                    return field;
                } catch (NoSuchFieldException ignored) {
                    current = current.getSuperclass();
                }
            }
            throw new NoSuchFieldException(className + '.' + name);
        }

        private void initialize() {
            if (initialized) return;
            initialized = true;
            try {
                type = Class.forName(className);
                available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                disable(exception);
            }
        }

        private void disable(Throwable cause) {
            available = false;
            if (warned) return;
            warned = true;
            LOGGER.warn("Integration disabled for {}: {}", className, cause.toString());
        }
    }
}
