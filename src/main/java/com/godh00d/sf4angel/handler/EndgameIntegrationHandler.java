package com.godh00d.sf4angel.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Fail-closed endgame integrations proven from machines placed by the awarded player. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class EndgameIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String CORE = "sf4angel:core/";
    private static final String OWNER_PREFIX = "endgame:";
    private static final Map<World, Map<MachineKey, OwnedMachine>> MACHINES = new WeakHashMap<>();
    private static final Map<UUID, Integer> TICK_DIVIDERS = new HashMap<>();

    private EndgameIntegrationHandler() {
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        ResourceLocation name = event.getPlacedBlock().getBlock().getRegistryName();
        MachineType type = classify(name == null ? "" : name.toString());
        if (type == null) return;
        MachineKey key = new MachineKey(event.getWorld().provider.getDimension(), event.getPos());
        IntegrationOwnershipData.get(event.getWorld()).put(event.getWorld(), OWNER_PREFIX + type.name(),
            event.getPos(), event.getPlayer().getUniqueID());
        machines(event.getWorld()).put(key, new OwnedMachine(event.getPlayer().getUniqueID(), type));
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        IntegrationOwnershipData.get(event.getWorld()).removeAt(event.getWorld(), event.getPos());
        machines(event.getWorld()).remove(new MachineKey(event.getWorld().provider.getDimension(), event.getPos()));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
            || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        int divider = TICK_DIVIDERS.getOrDefault(player.getUniqueID(), 0) + 1;
        if (divider < 5) {
            TICK_DIVIDERS.put(player.getUniqueID(), divider);
            return;
        }
        TICK_DIVIDERS.put(player.getUniqueID(), 0);
        checkOwnedMachines(player);
    }

    private static MachineType classify(String name) {
        if (name.startsWith("nuclearcraft:manufactory_")) return MachineType.MANUFACTORY;
        if (name.startsWith("nuclearcraft:alloy_furnace_")) return MachineType.ALLOY_FURNACE;
        if (name.startsWith("nuclearcraft:fission_controller")) return MachineType.FISSION;
        if ("nuclearcraft:fusion_core".equals(name)) return MachineType.FUSION;
        if (name.startsWith("nuclearcraft:")) return MachineType.NUCLEAR_FLUID;
        if ("matteroverdrive:matter_analyzer".equals(name)) return MachineType.ANALYZER;
        if ("matteroverdrive:decomposer".equals(name)) return MachineType.DECOMPOSER;
        if ("matteroverdrive:replicator".equals(name)) return MachineType.REPLICATOR;
        if ("extendedcrafting:compressor".equals(name)) return MachineType.COMPRESSOR;
        if ("industrialforegoing:black_hole_unit".equals(name)) return MachineType.BLACK_HOLE_UNIT;
        if ("realfilingcabinet:modelcabinet".equals(name)) return MachineType.FILING_CABINET;
        return null;
    }

    private static void checkOwnedMachines(EntityPlayerMP player) {
        Iterator<Map.Entry<MachineKey, OwnedMachine>> iterator = machines(player.world).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<MachineKey, OwnedMachine> entry = iterator.next();
            MachineKey key = entry.getKey();
            OwnedMachine machine = entry.getValue();
            if (!machine.owner.equals(player.getUniqueID()) || key.dimension != player.dimension) continue;
            World world = player.world;
            if (!world.isBlockLoaded(key.position)) continue;
            TileEntity tile = world.getTileEntity(key.position);
            if (tile == null) continue;
            if (!machine.type.accepts(tile)) {
                iterator.remove();
                IntegrationOwnershipData.get(world).remove(world, OWNER_PREFIX + machine.type.name(), key.position);
                continue;
            }
            if (!RuntimeAccess.ownerMatchesWhenPresent(tile, machine.owner)) continue;
            checkMachine(player, tile, machine);
        }
    }

    private static void checkMachine(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        switch (machine.type) {
            case MANUFACTORY:
                checkProcessor(player, tile, machine, "manufactory_warranty_void");
                break;
            case ALLOY_FURNACE:
                checkProcessor(player, tile, machine, "alloyed_allegiance");
                break;
            case FISSION:
                checkFission(player, tile, machine);
                break;
            case FUSION:
                checkFusion(player, tile, machine);
                break;
            case NUCLEAR_FLUID:
                checkNuclearFluid(player, tile, machine);
                break;
            case ANALYZER:
                checkAnalyzer(player, tile, machine);
                break;
            case DECOMPOSER:
                checkDecomposer(player, tile, machine);
                break;
            case REPLICATOR:
                checkReplicator(player, tile, machine);
                break;
            case COMPRESSOR:
                checkCompressor(player, tile, machine);
                break;
            case BLACK_HOLE_UNIT:
                checkBlackHoleUnit(player, tile);
                break;
            case FILING_CABINET:
                checkFilingCabinet(player, tile);
                break;
            default:
                break;
        }
    }

    private static void checkProcessor(EntityPlayerMP player, TileEntity tile, OwnedMachine machine, String id) {
        Boolean processing = RuntimeAccess.bool(tile, "isProcessing");
        Double time = RuntimeAccess.decimal(tile, "time");
        SlotCounts counts = RuntimeAccess.processorSlotCounts(tile);
        if (processing == null || time == null || counts == null) return;
        boolean completed = machine.initialized && machine.processing
            && (time < machine.progress || !processing)
            && counts.inputs < machine.inputItems && counts.outputs > machine.outputItems;
        if (completed) grant(player, id);
        machine.processing = processing;
        machine.progress = time;
        machine.inputItems = counts.inputs;
        machine.outputItems = counts.outputs;
        machine.initialized = true;
    }

    private static void checkFission(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Integer complete = RuntimeAccess.integer(tile, "complete");
        Integer cells = RuntimeAccess.integer(tile, "cells");
        if (complete == null || cells == null) return;
        if (complete == 1 && cells > 0) grant(player, "positive_fission");
        Double heat = RuntimeAccess.decimal(tile, "heat");
        Integer maxHeat = RuntimeAccess.callInteger(tile, "getMaxHeat");
        if (heat != null && maxHeat != null && heat < maxHeat) {
            checkGeneratingReactor(player, tile, machine, "gone_fission");
        }
    }

    private static void checkFusion(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Integer complete = RuntimeAccess.integer(tile, "complete");
        Integer size = RuntimeAccess.integer(tile, "size");
        if (complete == null || size == null) return;
        if (complete == 1 && size > 0) grant(player, "fusion_cuisine");
        checkGeneratingReactor(player, tile, machine, "pocket_star");
    }

    private static void checkGeneratingReactor(EntityPlayerMP player, TileEntity tile, OwnedMachine machine, String id) {
        Boolean processing = RuntimeAccess.callBoolean(tile, "isProcessing");
        Double time = RuntimeAccess.decimal(tile, "time");
        long adjacentEnergy = adjacentEnergy(tile);
        long machineEnergy = energyStored(tile);
        if (processing == null || time == null || adjacentEnergy < 0L || machineEnergy < 0L) return;
        if (machine.initialized && processing && time != machine.progress
            && adjacentEnergy > machine.adjacentEnergy && machineEnergy < machine.machineEnergy) {
            grant(player, id);
        }
        machine.processing = processing;
        machine.progress = time;
        machine.adjacentEnergy = adjacentEnergy;
        machine.machineEnergy = machineEnergy;
        machine.initialized = true;
    }

    private static void checkNuclearFluid(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Boolean processing = RuntimeAccess.bool(tile, "isProcessing");
        Double time = RuntimeAccess.decimal(tile, "time");
        int deuterium = fluidAmount(tile, "deuterium");
        int tritium = fluidAmount(tile, "tritium");
        if (processing == null || time == null || (deuterium < 0 && tritium < 0)) return;
        if (machine.initialized && processing && time != machine.progress) {
            if (deuterium >= 1000 && deuterium > machine.deuterium) grant(player, "deuterium_duty");
            if (tritium >= 1000 && tritium > machine.tritium) grant(player, "tritium_triumph");
        }
        machine.progress = time;
        if (deuterium >= 0) machine.deuterium = deuterium;
        if (tritium >= 0) machine.tritium = tritium;
        machine.initialized = true;
    }

    private static void checkAnalyzer(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Boolean active = RuntimeAccess.callBoolean(tile, "getServerActive");
        Float progress = RuntimeAccess.callFloat(tile, "getProgress");
        int input = RuntimeAccess.inventorySlot(tile, RuntimeAccess.integer(tile, "input_slot"));
        if (active == null || progress == null || input < 0) return;
        ItemStack currentInput = RuntimeAccess.inventoryStack(tile, RuntimeAccess.integer(tile, "input_slot"));
        if (machine.initialized && machine.processing && machine.slotItems > input
            && progress < machine.progress && !machine.selectedItem.isEmpty()
            && RuntimeAccess.analyzerHasPattern(tile, machine.selectedItem)) {
            grant(player, "matter_of_analysis");
        }
        if (active && !currentInput.isEmpty()) machine.selectedItem = currentInput.copy();
        machine.processing = active;
        machine.progress = progress;
        machine.slotItems = input;
        machine.initialized = true;
    }

    private static void checkDecomposer(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Integer slot = RuntimeAccess.integer(tile, "INPUT_SLOT_ID");
        int input = RuntimeAccess.inventorySlot(tile, slot);
        Integer matter = RuntimeAccess.matterStored(tile);
        Boolean active = RuntimeAccess.callBoolean(tile, "isDecomposing");
        if (input < 0 || matter == null || active == null) return;
        if (machine.initialized && machine.processing && input < machine.slotItems && matter > machine.matter) {
            grant(player, "decompose_yourself");
        }
        machine.slotItems = input;
        machine.matter = matter;
        machine.processing = active;
        machine.initialized = true;
    }

    private static void checkReplicator(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Integer matter = RuntimeAccess.matterStored(tile);
        Integer first = RuntimeAccess.integer(tile, "OUTPUT_SLOT_ID");
        Integer second = RuntimeAccess.integer(tile, "SECOND_OUTPUT_SLOT_ID");
        ItemStack output = RuntimeAccess.inventoryStack(tile, first);
        ItemStack selected = RuntimeAccess.replicatorSelection(tile);
        if (matter == null || first == null || second == null) return;
        if (machine.initialized && matter < machine.matter && !machine.selectedItem.isEmpty()
            && sameItem(machine.selectedItem, output) && output.getCount() > machine.outputItems) {
            grant(player, "replication_nation");
        }
        if (!selected.isEmpty()) machine.selectedItem = selected.copy();
        machine.matter = matter;
        machine.outputItems = sameItem(machine.selectedItem, output) ? output.getCount() : 0;
        machine.initialized = true;
    }

    private static void checkCompressor(EntityPlayerMP player, TileEntity tile, OwnedMachine machine) {
        Integer material = RuntimeAccess.callInteger(tile, "getMaterialCount");
        Integer progress = RuntimeAccess.callInteger(tile, "getProgress");
        ItemStack output = RuntimeAccess.inventoryStack(tile, 0);
        if (material == null || progress == null) return;
        if (machine.initialized && machine.progress > 0.0D && material < machine.matter
            && progress < machine.progress && !output.isEmpty()
            && output.getCount() > machine.outputItems) {
            grant(player, "quantum_compression");
        }
        machine.matter = material;
        machine.progress = progress;
        machine.outputItems = output.getCount();
        machine.initialized = true;
    }

    private static void checkBlackHoleUnit(EntityPlayerMP player, TileEntity tile) {
        Integer amount = RuntimeAccess.callInteger(tile, "getAmount");
        IItemHandler handler = itemHandler(tile);
        if (amount != null && handler != null && handler.getSlots() == 1
            && amount == handler.getSlotLimit(0) && amount > 0) {
            grant(player, "black_hole_filled");
        }
    }

    private static void checkFilingCabinet(EntityPlayerMP player, TileEntity tile) {
        Long count = RuntimeAccess.cabinetItemCount(tile);
        if (count != null && count >= 1_000_000L) grant(player, "million_item_paperwork");
    }

    private static void grant(EntityPlayerMP player, String path) {
        AchievementHandler.grantCriterion(player, CORE + path);
    }

    private static Map<MachineKey, OwnedMachine> machines(World world) {
        Map<MachineKey, OwnedMachine> states = MACHINES.computeIfAbsent(world, ignored -> new HashMap<>());
        Map<MachineKey, IntegrationOwnershipData.Record> persisted = new HashMap<>();
        for (IntegrationOwnershipData.Record record : IntegrationOwnershipData.get(world).records(world, OWNER_PREFIX)) {
            try {
                MachineType type = MachineType.valueOf(record.kind.substring(OWNER_PREFIX.length()));
                persisted.put(new MachineKey(world.provider.getDimension(), record.position), record);
                MachineKey key = new MachineKey(world.provider.getDimension(), record.position);
                OwnedMachine current = states.get(key);
                if (current == null || current.type != type || !current.owner.equals(record.owner)) {
                    states.put(key, new OwnedMachine(record.owner, type));
                }
            } catch (IllegalArgumentException ignored) {
                // Unknown kinds are retained for a newer version that may understand them.
            }
        }
        states.keySet().retainAll(persisted.keySet());
        return states;
    }

    private static IItemHandler itemHandler(TileEntity tile) {
        if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) return null;
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    private static boolean hasFluid(TileEntity tile, String fluidName, int amount) {
        for (EnumFacing side : EnumFacing.values()) {
            if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) continue;
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            for (IFluidTankProperties tank : handler.getTankProperties()) {
                FluidStack fluid = tank.getContents();
                if (fluid != null && fluid.amount >= amount && fluid.getFluid() != null
                    && fluidName.equals(fluid.getFluid().getName())) return true;
            }
        }
        return false;
    }

    private static int fluidAmount(TileEntity tile, String fluidName) {
        int best = -1;
        for (EnumFacing side : EnumFacing.values()) {
            if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) continue;
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) continue;
            for (IFluidTankProperties tank : handler.getTankProperties()) {
                FluidStack fluid = tank.getContents();
                if (!tank.canFill() && fluid != null && fluid.getFluid() != null
                    && fluidName.equals(fluid.getFluid().getName())) {
                    best = Math.max(best, fluid.amount);
                }
            }
        }
        return best;
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        return !first.isEmpty() && !second.isEmpty() && first.isItemEqual(second)
            && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static long energyStored(TileEntity tile) {
        for (EnumFacing side : EnumFacing.values()) {
            if (!tile.hasCapability(CapabilityEnergy.ENERGY, side)) continue;
            IEnergyStorage energy = tile.getCapability(CapabilityEnergy.ENERGY, side);
            if (energy != null && energy.canExtract()) return energy.getEnergyStored();
        }
        return -1L;
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

    private enum MachineType {
        MANUFACTORY("nc.tile.processor.TileProcessor$Manufactory"),
        ALLOY_FURNACE("nc.tile.processor.TileProcessor$AlloyFurnace"),
        FISSION("nc.tile.generator.TileFissionController"),
        FUSION("nc.tile.generator.TileFusionCore"),
        NUCLEAR_FLUID("nc.tile."),
        ANALYZER("matteroverdrive.machines.analyzer.TileEntityMachineMatterAnalyzer"),
        DECOMPOSER("matteroverdrive.machines.decomposer.TileEntityMachineDecomposer"),
        REPLICATOR("matteroverdrive.machines.replicator.TileEntityMachineReplicator"),
        COMPRESSOR("com.blakebr0.extendedcrafting.tile.TileCompressor"),
        BLACK_HOLE_UNIT("com.buuz135.industrial.tile.misc.BlackHoleUnitTile"),
        FILING_CABINET("com.bafomdad.realfilingcabinet.blocks.tiles.TileFilingCabinet");

        private final String className;
        private boolean initialized;
        private Class<?> runtimeClass;

        MachineType(String className) {
            this.className = className;
        }

        private boolean accepts(TileEntity tile) {
            if (this == NUCLEAR_FLUID) return tile.getClass().getName().startsWith(className);
            if (!initialized) {
                initialized = true;
                try {
                    runtimeClass = Class.forName(className);
                } catch (ClassNotFoundException | LinkageError exception) {
                    LOGGER.warn("Integration tile class unavailable for {}: {}", className, exception.toString());
                }
            }
            return runtimeClass != null && runtimeClass.isInstance(tile);
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
            MachineKey that = (MachineKey) other;
            return dimension == that.dimension && position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return dimension * 31 + position.hashCode();
        }
    }

    private static final class OwnedMachine {
        private final UUID owner;
        private final MachineType type;
        private boolean initialized;
        private boolean processing;
        private double progress;
        private int inputItems;
        private int outputItems;
        private int slotItems;
        private int matter;
        private long adjacentEnergy;
        private long machineEnergy;
        private int deuterium;
        private int tritium;
        private ItemStack selectedItem = ItemStack.EMPTY;

        private OwnedMachine(UUID owner, MachineType type) {
            this.owner = owner;
            this.type = type;
        }
    }

    /** Reflection is resolved once per declaring class/member and disables only the unavailable proof path. */
    private static final class RuntimeAccess {
        private static final Map<String, Field> FIELDS = new HashMap<>();
        private static final Map<String, Method> METHODS = new HashMap<>();
        private static final Map<String, Boolean> MISSING = new HashMap<>();
        private static Capability<?> folderCapability;
        private static Method folderGetCount;
        private static boolean folderInitialized;

        private RuntimeAccess() {
        }

        private static Integer integer(Object target, String name) {
            Object value = fieldValue(target, name);
            return value instanceof Number ? ((Number) value).intValue() : null;
        }

        private static Double decimal(Object target, String name) {
            Object value = fieldValue(target, name);
            return value instanceof Number ? ((Number) value).doubleValue() : null;
        }

        private static Boolean bool(Object target, String name) {
            Object value = fieldValue(target, name);
            return value instanceof Boolean ? (Boolean) value : null;
        }

        private static Integer callInteger(Object target, String name) {
            Object value = invoke(target, name);
            return value instanceof Number ? ((Number) value).intValue() : null;
        }

        private static Float callFloat(Object target, String name) {
            Object value = invoke(target, name);
            return value instanceof Number ? ((Number) value).floatValue() : null;
        }

        private static Boolean callBoolean(Object target, String name) {
            Object value = invoke(target, name);
            return value instanceof Boolean ? (Boolean) value : null;
        }

        private static Object fieldValue(Object target, String name) {
            if (target == null) return null;
            String key = target.getClass().getName() + '#' + name;
            if (MISSING.containsKey(key)) return null;
            try {
                Field field = FIELDS.get(key);
                if (field == null) {
                    Class<?> type = target.getClass();
                    while (type != null) {
                        try {
                            field = type.getDeclaredField(name);
                            break;
                        } catch (NoSuchFieldException ignored) {
                            type = type.getSuperclass();
                        }
                    }
                    if (field == null) throw new NoSuchFieldException(name);
                    field.setAccessible(true);
                    FIELDS.put(key, field);
                }
                return field.get(target);
            } catch (ReflectiveOperationException | LinkageError exception) {
                disable(key, exception);
                return null;
            }
        }

        private static Object invoke(Object target, String name) {
            if (target == null) return null;
            String key = target.getClass().getName() + '#' + name + "()";
            if (MISSING.containsKey(key)) return null;
            try {
                Method method = METHODS.get(key);
                if (method == null) {
                    Class<?> type = target.getClass();
                    while (type != null) {
                        try {
                            method = type.getDeclaredMethod(name);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            type = type.getSuperclass();
                        }
                    }
                    if (method == null) throw new NoSuchMethodException(name);
                    method.setAccessible(true);
                    METHODS.put(key, method);
                }
                return method.invoke(target);
            } catch (ReflectiveOperationException | LinkageError exception) {
                disable(key, exception);
                return null;
            }
        }

        private static boolean ownerMatchesWhenPresent(TileEntity tile, UUID expected) {
            String name = tile.getClass().getName();
            if (!name.startsWith("matteroverdrive.") && !name.startsWith("com.bafomdad.realfilingcabinet.")) {
                return true;
            }
            Object owner = invoke(tile, "getOwner");
            return owner instanceof UUID && expected.equals(owner);
        }

        private static int inventorySlot(TileEntity tile, Integer slot) {
            if (slot == null || slot < 0 || !(tile instanceof IInventory)) return -1;
            IInventory inventory = (IInventory) tile;
            if (slot >= inventory.getSizeInventory()) return -1;
            return inventory.getStackInSlot(slot).getCount();
        }

        private static ItemStack inventoryStack(TileEntity tile, Integer slot) {
            if (slot == null || slot < 0 || !(tile instanceof IInventory)) return ItemStack.EMPTY;
            IInventory inventory = (IInventory) tile;
            if (slot >= inventory.getSizeInventory()) return ItemStack.EMPTY;
            return inventory.getStackInSlot(slot);
        }

        private static boolean analyzerHasPattern(TileEntity tile, ItemStack stack) {
            Object component = fieldValue(tile, "taskProcessingComponent");
            Object value = invokeWith(component, "networkHasPattern", new Class<?>[] {ItemStack.class}, stack);
            return Boolean.TRUE.equals(value);
        }

        private static ItemStack replicatorSelection(TileEntity tile) {
            Object queue = invokeWith(tile, "getTaskQueue", new Class<?>[] {int.class}, 0);
            Object task = invoke(queue, "peek");
            Object pattern = invoke(task, "getPattern");
            Object stack = invokeWith(pattern, "toItemStack", new Class<?>[] {boolean.class}, false);
            return stack instanceof ItemStack ? (ItemStack) stack : ItemStack.EMPTY;
        }

        private static Object invokeWith(Object target, String name, Class<?>[] parameters, Object... arguments) {
            if (target == null) return null;
            String key = target.getClass().getName() + '#' + name + java.util.Arrays.toString(parameters);
            if (MISSING.containsKey(key)) return null;
            try {
                Method method = METHODS.get(key);
                if (method == null) {
                    Class<?> type = target.getClass();
                    while (type != null) {
                        try {
                            method = type.getDeclaredMethod(name, parameters);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            type = type.getSuperclass();
                        }
                    }
                    if (method == null) throw new NoSuchMethodException(name);
                    method.setAccessible(true);
                    METHODS.put(key, method);
                }
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException | LinkageError exception) {
                disable(key, exception);
                return null;
            }
        }

        private static SlotCounts processorSlotCounts(TileEntity tile) {
            Integer inputSize = integer(tile, "itemInputSize");
            Integer outputSize = integer(tile, "itemOutputSize");
            if (inputSize == null || outputSize == null || inputSize < 1 || outputSize < 1
                || !(tile instanceof IInventory)) return null;
            IInventory inventory = (IInventory) tile;
            if (inputSize + outputSize > inventory.getSizeInventory()) return null;
            int inputs = 0;
            int outputs = 0;
            for (int slot = 0; slot < inputSize; slot++) inputs += inventory.getStackInSlot(slot).getCount();
            for (int slot = inputSize; slot < inputSize + outputSize; slot++) {
                outputs += inventory.getStackInSlot(slot).getCount();
            }
            return new SlotCounts(inputs, outputs);
        }

        private static Integer matterStored(TileEntity tile) {
            Object storage = invoke(tile, "getMatterStorage");
            Object value = invoke(storage, "getMatterStored");
            return value instanceof Number ? ((Number) value).intValue() : null;
        }

        private static Long cabinetItemCount(TileEntity tile) {
            initializeFolderCapability();
            if (folderCapability == null || folderGetCount == null) return null;
            Object inventory = invoke(tile, "getInventory");
            if (!(inventory instanceof IItemHandler)) return null;
            long total = 0L;
            IItemHandler handler = (IItemHandler) inventory;
            try {
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    ItemStack folder = handler.getStackInSlot(slot);
                    if (folder.isEmpty() || !folder.hasCapability(folderCapability, null)) continue;
                    Object capability = folder.getCapability(folderCapability, null);
                    Object count = capability == null ? null : folderGetCount.invoke(capability);
                    if (!(count instanceof Number) || ((Number) count).longValue() < 0L) return null;
                    long value = ((Number) count).longValue();
                    if (Long.MAX_VALUE - total < value) return Long.MAX_VALUE;
                    total += value;
                }
                return total;
            } catch (ReflectiveOperationException | LinkageError exception) {
                folderCapability = null;
                warn("Real Filing Cabinet count integration disabled", exception);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private static void initializeFolderCapability() {
            if (folderInitialized) return;
            folderInitialized = true;
            if (!Loader.isModLoaded("realfilingcabinet")) return;
            try {
                Class<?> provider = Class.forName(
                    "com.bafomdad.realfilingcabinet.items.capabilities.CapabilityProviderFolder");
                Field capabilityField = provider.getField("FOLDER_CAP");
                Object capability = capabilityField.get(null);
                if (!(capability instanceof Capability)) return;
                folderCapability = (Capability<?>) capability;
                Class<?> folder = Class.forName(
                    "com.bafomdad.realfilingcabinet.items.capabilities.CapabilityFolder");
                folderGetCount = folder.getMethod("getCount");
            } catch (ReflectiveOperationException | LinkageError exception) {
                folderCapability = null;
                warn("Real Filing Cabinet count integration unavailable", exception);
            }
        }

        private static void disable(String key, Throwable cause) {
            MISSING.put(key, Boolean.TRUE);
            warn("Integration proof unavailable for " + key, cause);
        }

        private static void warn(String message, Throwable cause) {
            LOGGER.warn("{}: {}", message, cause.toString());
        }
    }

    private static final class SlotCounts {
        private final int inputs;
        private final int outputs;

        private SlotCounts(int inputs, int outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }
    }
}
