package com.godh00d.sf4angel.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/** Runtime integrations kept compile-time independent from all optional mods. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class DigitalIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String SIMULATION_THEORY = "sf4angel:core/simulation_theory";
    private static final String LOGIC_IN_THE_VOID = "sf4angel:core/logic_in_the_void";
    private static final String ITEMS_IN_TRANSIT = "sf4angel:core/items_in_transit";
    private static final String ACCEPTABLE_ENERGY = "sf4angel:core/acceptable_energy";
    private static final String ME_MYSELF_AND_I = "sf4angel:core/me_myself_and_i";
    private static final String AUTOCRAFT_AUTHORITY = "sf4angel:core/autocraft_authority";

    private static final String BLOCK_OWNER_PREFIX = "digital:block:";
    private static final String PART_OWNER_PREFIX = "digital:part:";
    private static final Map<World, Map<WorldPos, UUID>> BLOCK_OWNERS = new WeakHashMap<>();
    private static final Map<World, Map<PartKey, UUID>> PART_OWNERS = new WeakHashMap<>();
    private static final Map<UUID, PendingPart> PENDING_PARTS = new HashMap<>();
    private static final Map<UUID, PlayerState> PLAYER_STATES = new HashMap<>();
    private static final Map<World, Map<WorldPos, SimulationState>> SIMULATIONS = new WeakHashMap<>();
    private static final Map<World, Map<WorldPos, Double>> ACCEPTOR_ENERGY = new WeakHashMap<>();
    private static final Map<World, Map<PartKey, Map<String, Map<String, Integer>>>> INTERFACE_COUNTS = new WeakHashMap<>();

    private DigitalIntegrationHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        ResourceLocation name = event.getPlacedBlock().getBlock().getRegistryName();
        if (name == null) return;
        String id = name.toString();
        if (!"deepmoblearning:simulation_chamber".equals(id)
            && !"appliedenergistics2:energy_acceptor".equals(id)) return;
        IntegrationOwnershipData.get(event.getWorld()).put(event.getWorld(), BLOCK_OWNER_PREFIX + id,
            event.getPos(), event.getPlayer().getUniqueID());
        blockOwners(event.getWorld()).put(new WorldPos(event.getWorld().provider.getDimension(), event.getPos()),
            event.getPlayer().getUniqueID());
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        WorldPos key = new WorldPos(event.getWorld().provider.getDimension(), event.getPos());
        IntegrationOwnershipData.get(event.getWorld()).removeAt(event.getWorld(), event.getPos());
        blockOwners(event.getWorld()).remove(key);
        simulations(event.getWorld()).remove(key);
        acceptorEnergy(event.getWorld()).remove(key);
        partOwners(event.getWorld()).keySet().removeIf(part -> part.position.equals(key));
        interfaceCounts(event.getWorld()).keySet().removeIf(part -> part.position.equals(key));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPartUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP)) return;
        ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());
        String partName = DynamicsAdapter.heldPartName(held);
        if (partName == null) return;

        BlockPos firstPos = event.getPos();
        EnumFacing firstSide = event.getFace();
        BlockPos secondPos = event.getPos().offset(event.getFace());
        EnumFacing secondSide = event.getFace().getOpposite();
        Set<PartKey> absent = new HashSet<>();
        PartKey first = new PartKey(event.getWorld().provider.getDimension(), firstPos, firstSide);
        PartKey second = new PartKey(event.getWorld().provider.getDimension(), secondPos, secondSide);
        if (DynamicsAdapter.partName(event.getWorld(), first) == null) absent.add(first);
        if (DynamicsAdapter.partName(event.getWorld(), second) == null) absent.add(second);
        if (!absent.isEmpty()) {
            PENDING_PARTS.put(event.getEntityPlayer().getUniqueID(),
                new PendingPart(event.getWorld(), partName, absent, event.getWorld().getTotalWorldTime() + 2));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
            || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        PlayerState state = PLAYER_STATES.computeIfAbsent(player.getUniqueID(), ignored -> new PlayerState());
        resolvePartPlacement(player);
        checkTerminalInteraction(player, state);
        checkAutocrafting(player, state);

        if (++state.slowTick < 5) return;
        state.slowTick = 0;
        checkSimulation(player, state);
        checkDynamics(player, state);
        checkEnergyAcceptor(player, state);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.player.getUniqueID();
        PLAYER_STATES.remove(id);
        PENDING_PARTS.remove(id);
    }

    private static void resolvePartPlacement(EntityPlayerMP player) {
        PendingPart pending = PENDING_PARTS.get(player.getUniqueID());
        if (pending == null) return;
        if (pending.world != player.world) {
            PENDING_PARTS.remove(player.getUniqueID());
            return;
        }
        for (PartKey key : pending.candidates) {
            if (key.dimension != player.dimension || !player.world.isBlockLoaded(key.position.position)) continue;
            if (pending.partName.equals(DynamicsAdapter.partName(player.world, key))) {
                IntegrationOwnershipData.get(player.world).put(player.world,
                    PART_OWNER_PREFIX + key.side.getName() + ":" + pending.partName,
                    key.position.position, player.getUniqueID());
                partOwners(player.world).put(key, player.getUniqueID());
                PENDING_PARTS.remove(player.getUniqueID());
                return;
            }
        }
        if (player.world.getTotalWorldTime() > pending.expiresAt) PENDING_PARTS.remove(player.getUniqueID());
    }

    private static void checkSimulation(EntityPlayerMP player, PlayerState playerState) {
        if (playerState.granted.contains(SIMULATION_THEORY)) return;
        for (Map.Entry<WorldPos, UUID> entry : new ArrayList<>(blockOwners(player.world).entrySet())) {
            WorldPos key = entry.getKey();
            if (!entry.getValue().equals(player.getUniqueID()) || key.dimension != player.dimension
                || !player.world.isBlockLoaded(key.position)) continue;
            TileEntity tile = player.world.getTileEntity(key.position);
            SimulationView view = DeepMobAdapter.read(tile);
            if (view == null) continue;
            SimulationState before = simulations(player.world).get(key);
            if (before != null && before.crafting && before.trained && view.trained
                && before.modelKey.equals(view.modelKey) && view.totalSimulations > before.totalSimulations) {
                grant(player, playerState, SIMULATION_THEORY);
                return;
            }
            simulations(player.world).put(key,
                new SimulationState(view.crafting, view.trained, view.modelKey, view.totalSimulations));
        }
    }

    private static void checkDynamics(EntityPlayerMP player, PlayerState state) {
        boolean needLogic = !state.granted.contains(LOGIC_IN_THE_VOID);
        boolean needTransit = !state.granted.contains(ITEMS_IN_TRANSIT);
        if (!needLogic && !needTransit) return;

        for (Map.Entry<PartKey, UUID> entry : new ArrayList<>(partOwners(player.world).entrySet())) {
            PartKey owned = entry.getKey();
            if (!entry.getValue().equals(player.getUniqueID()) || owned.dimension != player.dimension
                || !player.world.isBlockLoaded(owned.position.position)) continue;
            DynamicsNetworkView network = DynamicsAdapter.network(player.world, owned);
            if (network == null) continue;
            if (needLogic && network.hasReader && network.hasActiveSink) {
                grant(player, state, LOGIC_IN_THE_VOID);
                needLogic = false;
            }
            if (needTransit && "interface_item".equals(network.ownedPartName)
                && completedItemTransfer(player.world, owned, network.interfaceCounts)) {
                grant(player, state, ITEMS_IN_TRANSIT);
                needTransit = false;
            }
            if (!needLogic && !needTransit) return;
        }
    }

    private static boolean completedItemTransfer(World world, PartKey owned,
                                                 Map<String, Map<String, Integer>> current) {
        Map<String, Map<String, Integer>> before = interfaceCounts(world).put(owned, current);
        if (before == null || current.size() < 2 || !current.keySet().equals(before.keySet())) return false;
        String ownedId = owned.toString();
        Map<String, Integer> ownedBefore = before.get(ownedId);
        Map<String, Integer> ownedAfter = current.get(ownedId);
        if (ownedBefore == null || ownedAfter == null) return false;
        for (String item : ownedBefore.keySet()) {
            if (ownedAfter.getOrDefault(item, 0) >= ownedBefore.get(item)) continue;
            for (String position : current.keySet()) {
                if (!position.equals(ownedId) && current.get(position).getOrDefault(item, 0)
                    > before.get(position).getOrDefault(item, 0)) return true;
            }
        }
        return false;
    }

    private static void checkEnergyAcceptor(EntityPlayerMP player, PlayerState state) {
        if (state.granted.contains(ACCEPTABLE_ENERGY)) return;
        for (Map.Entry<WorldPos, UUID> entry : new ArrayList<>(blockOwners(player.world).entrySet())) {
            WorldPos key = entry.getKey();
            if (!entry.getValue().equals(player.getUniqueID()) || key.dimension != player.dimension
                || !player.world.isBlockLoaded(key.position)) continue;
            TileEntity tile = player.world.getTileEntity(key.position);
            EnergyView energy = Ae2Adapter.energy(tile);
            if (energy == null) continue;
            Double before = acceptorEnergy(player.world).put(key, energy.stored);
            if (before != null && energy.stored >= 1.0D && energy.stored > before
                && energy.averageInjection > 0.0D) {
                grant(player, state, ACCEPTABLE_ENERGY);
                return;
            }
        }
    }

    private static void checkTerminalInteraction(EntityPlayerMP player, PlayerState state) {
        TerminalView current = Ae2Adapter.terminal(player.openContainer);
        if (current == null) {
            state.terminalOpen = false;
            return;
        }
        state.lastTerminalTick = player.world.getTotalWorldTime();
        if (!state.granted.contains(ME_MYSELF_AND_I) && state.terminalOpen && state.terminal != null
            && current.powered && current.hasFormattedCell
            && inverseItemChange(state.terminal.playerItems, playerItems(player),
                state.terminal.networkItems, current.networkItems)) {
            grant(player, state, ME_MYSELF_AND_I);
        }
        state.terminal = new TerminalSnapshot(playerItems(player), current.networkItems, current.monitor, current.grid);
        state.terminalOpen = true;
    }

    private static boolean inverseItemChange(Map<String, Long> playerBefore, Map<String, Long> playerAfter,
                                             Map<String, Long> networkBefore, Map<String, Long> networkAfter) {
        Set<String> keys = new HashSet<>(playerBefore.keySet());
        keys.addAll(playerAfter.keySet());
        keys.addAll(networkBefore.keySet());
        keys.addAll(networkAfter.keySet());
        for (String key : keys) {
            long playerDelta = playerAfter.getOrDefault(key, 0L) - playerBefore.getOrDefault(key, 0L);
            long networkDelta = networkAfter.getOrDefault(key, 0L) - networkBefore.getOrDefault(key, 0L);
            if (playerDelta != 0L && playerDelta == -networkDelta) return true;
        }
        return false;
    }

    private static void checkAutocrafting(EntityPlayerMP player, PlayerState state) {
        if (state.granted.contains(AUTOCRAFT_AUTHORITY)) return;
        CraftView active = Ae2Adapter.playerCraft(state, player);
        if (active != null) {
            if (state.craft == null || state.craft.cpu != active.cpu) {
                state.craft = new CraftState(active.cpu, active.monitor, active.outputKey,
                    active.outputCount, active.assemblerActive);
            } else {
                state.craft.assemblerActive |= active.assemblerActive;
            }
            return;
        }
        if (state.craft == null) return;
        CraftCompletion completion = Ae2Adapter.completed(state.craft);
        if (completion != null && completion.complete && state.craft.assemblerActive
            && completion.outputCount >= state.craft.outputCount + 1L) {
            grant(player, state, AUTOCRAFT_AUTHORITY);
        }
        if (completion == null || completion.complete) state.craft = null;
    }

    private static Map<String, Long> playerItems(EntityPlayerMP player) {
        Map<String, Long> result = new HashMap<>();
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            add(result, player.inventory.getStackInSlot(slot));
        }
        return result;
    }

    private static void add(Map<String, Long> counts, ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return;
        String key = itemKey(stack);
        counts.put(key, counts.getOrDefault(key, 0L) + stack.getCount());
    }

    private static String itemKey(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return stack.getItem().getRegistryName() + "@" + stack.getMetadata() + "#" + (tag == null ? "" : tag.toString());
    }

    private static void grant(EntityPlayerMP player, PlayerState state, String advancement) {
        if (!state.granted.add(advancement)) return;
        AchievementHandler.grantCriterion(player, advancement);
    }

    private static Map<WorldPos, UUID> blockOwners(World world) {
        Map<WorldPos, UUID> result = BLOCK_OWNERS.computeIfAbsent(world, ignored -> new HashMap<>());
        result.clear();
        IntegrationOwnershipData ownership = IntegrationOwnershipData.get(world);
        for (IntegrationOwnershipData.Record record : ownership.records(world, BLOCK_OWNER_PREFIX)) {
            String expected = record.kind.substring(BLOCK_OWNER_PREFIX.length());
            if (world.isBlockLoaded(record.position)) {
                ResourceLocation current = world.getBlockState(record.position).getBlock().getRegistryName();
                if (current == null || !expected.equals(current.toString())) {
                    ownership.remove(world, record.kind, record.position);
                    continue;
                }
            }
            result.put(new WorldPos(world.provider.getDimension(), record.position), record.owner);
        }
        return result;
    }

    private static Map<PartKey, UUID> partOwners(World world) {
        Map<PartKey, UUID> result = PART_OWNERS.computeIfAbsent(world, ignored -> new HashMap<>());
        result.clear();
        IntegrationOwnershipData ownership = IntegrationOwnershipData.get(world);
        for (IntegrationOwnershipData.Record record : ownership.records(world, PART_OWNER_PREFIX)) {
            String serialized = record.kind.substring(PART_OWNER_PREFIX.length());
            int separator = serialized.indexOf(':');
            EnumFacing side = EnumFacing.byName(separator < 0 ? serialized : serialized.substring(0, separator));
            if (side == null || separator < 0) {
                ownership.remove(world, record.kind, record.position);
                continue;
            }
            String expected = serialized.substring(separator + 1);
            PartKey key = new PartKey(world.provider.getDimension(), record.position, side);
            if (world.isBlockLoaded(record.position)
                && !expected.equals(DynamicsAdapter.partName(world, key))) {
                ownership.remove(world, record.kind, record.position);
                continue;
            }
            result.put(key, record.owner);
        }
        return result;
    }

    private static Map<WorldPos, SimulationState> simulations(World world) {
        return SIMULATIONS.computeIfAbsent(world, ignored -> new HashMap<>());
    }

    private static Map<WorldPos, Double> acceptorEnergy(World world) {
        return ACCEPTOR_ENERGY.computeIfAbsent(world, ignored -> new HashMap<>());
    }

    private static Map<PartKey, Map<String, Map<String, Integer>>> interfaceCounts(World world) {
        return INTERFACE_COUNTS.computeIfAbsent(world, ignored -> new HashMap<>());
    }

    private static final class DeepMobAdapter {
        private static final ReflectionGate GATE = new ReflectionGate("Deep Mob Learning", "deepmoblearning");
        private static Class<?> tileClass;
        private static Method isCrafting;
        private static Method getDataModel;

        private static SimulationView read(TileEntity tile) {
            if (!initialize() || tile == null || !tileClass.isInstance(tile)) return null;
            try {
                boolean crafting = (Boolean) isCrafting.invoke(tile);
                ItemStack model = (ItemStack) getDataModel.invoke(tile);
                if (model.isEmpty() || model.getItem().getRegistryName() == null
                    || !"deepmoblearning".equals(model.getItem().getRegistryName().getResourceDomain())) return null;
                NBTTagCompound tag = model.getTagCompound();
                return new SimulationView(crafting, tag != null && tag.getInteger("tier") > 0, itemKey(model),
                    tag == null ? 0 : tag.getInteger("totalSimulationCount"));
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static boolean initialize() {
            if (!GATE.begin()) return GATE.available;
            try {
                tileClass = Class.forName("xt9.deepmoblearning.common.tiles.TileEntitySimulationChamber");
                isCrafting = tileClass.getMethod("isCrafting");
                getDataModel = tileClass.getMethod("getDataModel");
                GATE.available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
            }
            return GATE.available;
        }
    }

    private static final class DynamicsAdapter {
        private static final ReflectionGate GATE = new ReflectionGate("Integrated Dynamics/Tunnels", "integrateddynamics");
        private static Class<?> tileClass;
        private static Class<?> itemPartClass;
        private static Method tileGetNetwork;
        private static Method tileGetPartContainer;
        private static Method containerGetPart;
        private static Method itemGetPart;
        private static Method partGetName;
        private static Method networkIsInitialized;
        private static Method networkIsKilled;
        private static Method networkIsCrashed;
        private static Method networkGetElements;
        private static Class<?> partElementClass;
        private static Method elementGetPart;
        private static Method elementGetPartState;
        private static Method elementGetTarget;
        private static Method elementGetPosition;
        private static Method elementGetSide;
        private static Method stateIsEnabled;
        private static Method targetGetTarget;
        private static Method partPosGetPos;
        private static Method partPosGetSide;
        private static Method dimPosGetBlockPos;

        private static String heldPartName(ItemStack stack) {
            if (!initialize() || stack.isEmpty() || !itemPartClass.isInstance(stack.getItem())) return null;
            try {
                return (String) partGetName.invoke(itemGetPart.invoke(stack.getItem()));
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static String partName(World world, PartKey key) {
            if (!initialize() || key.dimension != world.provider.getDimension() || !world.isBlockLoaded(key.position.position)) return null;
            TileEntity tile = world.getTileEntity(key.position.position);
            if (tile == null || !tileClass.isInstance(tile)) return null;
            try {
                Object part = containerGetPart.invoke(tileGetPartContainer.invoke(tile), key.side);
                return part == null ? null : (String) partGetName.invoke(part);
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static DynamicsNetworkView network(World world, PartKey owned) {
            if (!initialize()) return null;
            TileEntity tile = world.getTileEntity(owned.position.position);
            if (tile == null || !tileClass.isInstance(tile)) return null;
            try {
                Object network = tileGetNetwork.invoke(tile);
                if (network == null || !(Boolean) networkIsInitialized.invoke(network)
                    || (Boolean) networkIsKilled.invoke(network) || (Boolean) networkIsCrashed.invoke(network)) return null;
                boolean ownedFound = false;
                boolean reader = false;
                boolean activeSink = false;
                String ownedName = null;
                Map<PartKey, UUID> owners = partOwners(world);
                UUID owner = owners.get(owned);
                Map<String, Map<String, Integer>> interfaces = new HashMap<>();
                for (Object element : (Iterable<?>) networkGetElements.invoke(network)) {
                    if (!partElementClass.isInstance(element)) continue;
                    Object part = elementGetPart.invoke(element);
                    Object partState = elementGetPartState.invoke(element);
                    String name = (String) partGetName.invoke(part);
                    BlockPos pos = (BlockPos) dimPosGetBlockPos.invoke(elementGetPosition.invoke(element));
                    EnumFacing side = (EnumFacing) elementGetSide.invoke(element);
                    PartKey key = new PartKey(world.provider.getDimension(), pos, side);
                    if (key.equals(owned)) {
                        ownedFound = true;
                        ownedName = name;
                    }
                    boolean enabled = (Boolean) stateIsEnabled.invoke(partState);
                    boolean playerOwned = owner != null && owner.equals(owners.get(key));
                    if (playerOwned && enabled && name.endsWith("_reader")) reader = true;
                    if (playerOwned && enabled && (name.endsWith("_writer") || name.contains("display"))
                        && hasActiveVariable(partState, name)) activeSink = true;
                    if ("interface_item".equals(name)) {
                        Map<String, Integer> counts = targetItemCounts(world, element);
                        if (counts != null) interfaces.put(key.toString(), counts);
                    }
                }
                return ownedFound ? new DynamicsNetworkView(ownedName, reader, activeSink, interfaces) : null;
            } catch (ReflectiveOperationException | LinkageError | ClassCastException exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static boolean hasActiveVariable(Object state, String name) throws ReflectiveOperationException {
            Method hasVariable = cachedMethod(state.getClass(), "hasVariable");
            if (hasVariable == null || !(Boolean) hasVariable.invoke(state)) return false;
            Method deactivated = cachedMethod(state.getClass(), "isDeactivated");
            if (deactivated != null && (Boolean) deactivated.invoke(state)) return false;
            if (name.endsWith("_writer")) {
                Method activeAspect = cachedMethod(state.getClass(), "getActiveAspect");
                return activeAspect != null && activeAspect.invoke(state) != null;
            }
            return true;
        }

        private static Map<String, Integer> targetItemCounts(World world, Object element) throws ReflectiveOperationException {
            Object target = targetGetTarget.invoke(elementGetTarget.invoke(element));
            BlockPos pos = (BlockPos) dimPosGetBlockPos.invoke(partPosGetPos.invoke(target));
            EnumFacing side = (EnumFacing) partPosGetSide.invoke(target);
            if (!world.isBlockLoaded(pos)) return null;
            TileEntity targetTile = world.getTileEntity(pos);
            if (targetTile == null || !targetTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) return null;
            IItemHandler handler = targetTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) return null;
            Map<String, Integer> counts = new HashMap<>();
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) counts.merge(itemKey(stack), stack.getCount(), Integer::sum);
            }
            return counts;
        }

        private static boolean initialize() {
            if (!GATE.begin()) return GATE.available;
            try {
                tileClass = Class.forName("org.cyclops.integrateddynamics.core.tileentity.TileMultipartTicking");
                itemPartClass = Class.forName("org.cyclops.integrateddynamics.core.item.ItemPart");
                Class<?> partType = Class.forName("org.cyclops.integrateddynamics.api.part.IPartType");
                Class<?> container = Class.forName("org.cyclops.integrateddynamics.api.part.IPartContainer");
                Class<?> network = Class.forName("org.cyclops.integrateddynamics.api.network.INetwork");
                partElementClass = Class.forName("org.cyclops.integrateddynamics.api.network.IPartNetworkElement");
                Class<?> state = Class.forName("org.cyclops.integrateddynamics.api.part.IPartState");
                Class<?> target = Class.forName("org.cyclops.integrateddynamics.api.part.PartTarget");
                Class<?> partPos = Class.forName("org.cyclops.integrateddynamics.api.part.PartPos");
                Class<?> dimPos = Class.forName("org.cyclops.cyclopscore.datastructure.DimPos");
                tileGetNetwork = tileClass.getMethod("getNetwork");
                tileGetPartContainer = tileClass.getMethod("getPartContainer");
                containerGetPart = container.getMethod("getPart", EnumFacing.class);
                itemGetPart = itemPartClass.getMethod("getPart");
                partGetName = partType.getMethod("getName");
                networkIsInitialized = network.getMethod("isInitialized");
                networkIsKilled = network.getMethod("isKilled");
                networkIsCrashed = network.getMethod("isCrashed");
                networkGetElements = network.getMethod("getElements");
                elementGetPart = partElementClass.getMethod("getPart");
                elementGetPartState = partElementClass.getMethod("getPartState");
                elementGetTarget = partElementClass.getMethod("getTarget");
                elementGetPosition = partElementClass.getMethod("getPosition");
                elementGetSide = partElementClass.getMethod("getSide");
                stateIsEnabled = state.getMethod("isEnabled");
                targetGetTarget = target.getMethod("getTarget");
                partPosGetPos = partPos.getMethod("getPos");
                partPosGetSide = partPos.getMethod("getSide");
                dimPosGetBlockPos = dimPos.getMethod("getBlockPos");
                GATE.available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
            }
            return GATE.available;
        }
    }

    private static final class Ae2Adapter {
        private static final ReflectionGate GATE = new ReflectionGate("Applied Energistics 2", "appliedenergistics2");
        private static Class<?> acceptorClass;
        private static Class<?> terminalClass;
        private static Class<?> driveClass;
        private static Class<?> assemblerClass;
        private static Class<?> energyGridClass;
        private static Class<?> craftingGridClass;
        private static Class<?> cpuClass;
        private static Method actionableNode;
        private static Method containerNode;
        private static Method nodeGetGrid;
        private static Method nodeGetMachine;
        private static Method nodeIsActive;
        private static Method gridGetCache;
        private static Method gridGetMachines;
        private static Method energyStored;
        private static Method averageInjection;
        private static Method driveCellCount;
        private static Method driveCellStatus;
        private static Method monitorStorageList;
        private static Method listIterator;
        private static Method aeStackSize;
        private static Method aeCreateStack;
        private static Method craftingCpus;
        private static Method cpuBusy;
        private static Method cpuSource;
        private static Method sourcePlayer;
        private static Method assemblerProgress;
        private static Field terminalMonitor;
        private static Field cpuComplete;
        private static Field cpuOutput;

        private static EnergyView energy(TileEntity tile) {
            if (!initialize() || tile == null || !acceptorClass.isInstance(tile)) return null;
            try {
                Object grid = grid(tile);
                if (grid == null) return null;
                Object energy = gridGetCache.invoke(grid, energyGridClass);
                return new EnergyView(((Number) energyStored.invoke(energy)).doubleValue(),
                    ((Number) averageInjection.invoke(energy)).doubleValue());
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static TerminalView terminal(Container container) {
            if (!initialize() || container == null || !terminalClass.isInstance(container)) return null;
            try {
                Object node = containerNode.invoke(container);
                if (node == null || !(Boolean) nodeIsActive.invoke(node)) return null;
                Object grid = nodeGetGrid.invoke(node);
                Object monitor = terminalMonitor.get(container);
                if (grid == null || monitor == null) return null;
                Object energy = gridGetCache.invoke(grid, energyGridClass);
                boolean powered = ((Number) energyStored.invoke(energy)).doubleValue() >= 1.0D;
                return new TerminalView(powered, hasCell(grid), monitorItems(monitor), monitor, grid);
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static CraftView playerCraft(PlayerState state, EntityPlayerMP player) {
            if (!initialize() || player.world.getTotalWorldTime() - state.lastTerminalTick > 200L
                || state.terminal == null) return null;
            try {
                Object grid = state.terminal.grid;
                Object crafting = gridGetCache.invoke(grid, craftingGridClass);
                for (Object cpu : (Iterable<?>) craftingCpus.invoke(crafting)) {
                    if (!cpuClass.isInstance(cpu) || !(Boolean) cpuBusy.invoke(cpu)) continue;
                    Object source = cpuSource.invoke(cpu);
                    Object optional = sourcePlayer.invoke(source);
                    if (!(optional instanceof Optional) || !((Optional<?>) optional).isPresent()
                        || !player.getUniqueID().equals(((net.minecraft.entity.player.EntityPlayer) ((Optional<?>) optional).get()).getUniqueID())) continue;
                    Object output = cpuOutput.get(cpu);
                    ItemStack stack = output == null ? ItemStack.EMPTY : (ItemStack) aeCreateStack.invoke(output);
                    if (stack.isEmpty()) continue;
                    String key = itemKey(stack);
                    long count = monitorItems(state.terminal.monitor).getOrDefault(key, 0L);
                    return new CraftView(cpu, state.terminal.monitor, key, count, assemblerActive(grid));
                }
                return null;
            } catch (ReflectiveOperationException | LinkageError | ClassCastException exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static CraftCompletion completed(CraftState craft) {
            if (!initialize()) return null;
            try {
                if ((Boolean) cpuBusy.invoke(craft.cpu)) return new CraftCompletion(false, craft.outputCount);
                boolean complete = (Boolean) cpuComplete.get(craft.cpu);
                long count = monitorItems(craft.monitor).getOrDefault(craft.outputKey, 0L);
                return new CraftCompletion(complete, count);
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
                return null;
            }
        }

        private static boolean hasCell(Object grid) throws ReflectiveOperationException {
            Object machines = gridGetMachines.invoke(grid, driveClass);
            for (Object node : (Iterable<?>) machines) {
                Object drive = nodeGetMachine.invoke(node);
                if (!driveClass.isInstance(drive)) continue;
                int cells = ((Number) driveCellCount.invoke(drive)).intValue();
                for (int slot = 0; slot < cells; slot++) {
                    if (((Number) driveCellStatus.invoke(drive, slot)).intValue() > 0) return true;
                }
            }
            return false;
        }

        private static boolean assemblerActive(Object grid) throws ReflectiveOperationException {
            Object machines = gridGetMachines.invoke(grid, assemblerClass);
            for (Object node : (Iterable<?>) machines) {
                Object assembler = nodeGetMachine.invoke(node);
                if (assemblerClass.isInstance(assembler)
                    && ((Number) assemblerProgress.invoke(assembler)).intValue() > 0) return true;
            }
            return false;
        }

        private static Map<String, Long> monitorItems(Object monitor) throws ReflectiveOperationException {
            Map<String, Long> result = new HashMap<>();
            Object list = monitorStorageList.invoke(monitor);
            Iterator<?> iterator = (Iterator<?>) listIterator.invoke(list);
            while (iterator.hasNext()) {
                Object aeStack = iterator.next();
                long size = ((Number) aeStackSize.invoke(aeStack)).longValue();
                if (size <= 0L) continue;
                ItemStack stack = (ItemStack) aeCreateStack.invoke(aeStack);
                if (!stack.isEmpty()) result.put(itemKey(stack), size);
            }
            return result;
        }

        private static Object grid(Object tile) throws ReflectiveOperationException {
            Object node = actionableNode.invoke(tile);
            return node == null ? null : nodeGetGrid.invoke(node);
        }

        private static boolean initialize() {
            if (!GATE.begin()) return GATE.available;
            try {
                acceptorClass = Class.forName("appeng.tile.networking.TileEnergyAcceptor");
                terminalClass = Class.forName("appeng.container.implementations.ContainerMEMonitorable");
                driveClass = Class.forName("appeng.tile.storage.TileDrive");
                assemblerClass = Class.forName("appeng.tile.crafting.TileMolecularAssembler");
                energyGridClass = Class.forName("appeng.api.networking.energy.IEnergyGrid");
                craftingGridClass = Class.forName("appeng.api.networking.crafting.ICraftingGrid");
                cpuClass = Class.forName("appeng.me.cluster.implementations.CraftingCPUCluster");
                Class<?> actionHost = Class.forName("appeng.api.networking.security.IActionHost");
                Class<?> node = Class.forName("appeng.api.networking.IGridNode");
                Class<?> grid = Class.forName("appeng.api.networking.IGrid");
                Class<?> energy = Class.forName("appeng.api.networking.energy.IEnergyGrid");
                Class<?> monitor = Class.forName("appeng.api.storage.IMEMonitor");
                Class<?> itemList = Class.forName("appeng.api.storage.data.IItemList");
                Class<?> aeStack = Class.forName("appeng.api.storage.data.IAEStack");
                Class<?> aeItemStack = Class.forName("appeng.api.storage.data.IAEItemStack");
                Class<?> craftingGrid = Class.forName("appeng.api.networking.crafting.ICraftingGrid");
                Class<?> cpu = Class.forName("appeng.api.networking.crafting.ICraftingCPU");
                Class<?> source = Class.forName("appeng.api.networking.security.IActionSource");
                actionableNode = actionHost.getMethod("getActionableNode");
                containerNode = terminalClass.getMethod("getNetworkNode");
                nodeGetGrid = node.getMethod("getGrid");
                nodeGetMachine = node.getMethod("getMachine");
                nodeIsActive = node.getMethod("isActive");
                gridGetCache = grid.getMethod("getCache", Class.class);
                gridGetMachines = grid.getMethod("getMachines", Class.class);
                energyStored = energy.getMethod("getStoredPower");
                averageInjection = energy.getMethod("getAvgPowerInjection");
                driveCellCount = driveClass.getMethod("getCellCount");
                driveCellStatus = driveClass.getMethod("getCellStatus", int.class);
                monitorStorageList = monitor.getMethod("getStorageList");
                listIterator = itemList.getMethod("iterator");
                aeStackSize = aeStack.getMethod("getStackSize");
                aeCreateStack = aeItemStack.getMethod("createItemStack");
                craftingCpus = craftingGrid.getMethod("getCpus");
                cpuBusy = cpu.getMethod("isBusy");
                cpuSource = cpu.getMethod("getActionSource");
                sourcePlayer = source.getMethod("player");
                assemblerProgress = assemblerClass.getMethod("getCraftingProgress");
                terminalMonitor = terminalClass.getDeclaredField("monitor");
                terminalMonitor.setAccessible(true);
                cpuComplete = cpuClass.getDeclaredField("isComplete");
                cpuComplete.setAccessible(true);
                cpuOutput = cpuClass.getDeclaredField("finalOutput");
                cpuOutput.setAccessible(true);
                GATE.available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                GATE.disable(exception);
            }
            return GATE.available;
        }
    }

    private static final Map<Class<?>, Map<String, Method>> METHOD_CACHE = new IdentityHashMap<>();

    private static Method cachedMethod(Class<?> type, String name) {
        Map<String, Method> methods = METHOD_CACHE.computeIfAbsent(type, ignored -> new HashMap<>());
        if (methods.containsKey(name)) return methods.get(name);
        Class<?> current = type;
        while (current != null) {
            try {
                Method method = current.getDeclaredMethod(name);
                method.setAccessible(true);
                methods.put(name, method);
                return method;
            } catch (NoSuchMethodException ignored) {
                current = current.getSuperclass();
            }
        }
        methods.put(name, null);
        return null;
    }

    private static final class ReflectionGate {
        private final String name;
        private final String modId;
        private boolean initialized;
        private boolean available;
        private boolean warned;

        private ReflectionGate(String name, String modId) {
            this.name = name;
            this.modId = modId;
        }

        private boolean begin() {
            if (initialized) return false;
            initialized = true;
            if (!Loader.isModLoaded(modId)) warn("mod is not loaded", null);
            return Loader.isModLoaded(modId);
        }

        private void disable(Throwable cause) {
            available = false;
            warn("installed classes are incompatible", cause);
        }

        private void warn(String reason, Throwable cause) {
            if (warned) return;
            warned = true;
            if (cause == null) LOGGER.warn("{} integration disabled: {}", name, reason);
            else LOGGER.warn("{} integration disabled: {} ({})", name, reason, cause.toString());
        }
    }

    private static final class PlayerState {
        private final Set<String> granted = new HashSet<>();
        private int slowTick;
        private long lastTerminalTick = Long.MIN_VALUE;
        private boolean terminalOpen;
        private TerminalSnapshot terminal;
        private CraftState craft;
    }

    private static final class PendingPart {
        private final World world;
        private final String partName;
        private final Set<PartKey> candidates;
        private final long expiresAt;

        private PendingPart(World world, String partName, Set<PartKey> candidates, long expiresAt) {
            this.world = world;
            this.partName = partName;
            this.candidates = candidates;
            this.expiresAt = expiresAt;
        }
    }

    private static final class SimulationView {
        private final boolean crafting;
        private final boolean trained;
        private final String modelKey;
        private final int totalSimulations;

        private SimulationView(boolean crafting, boolean trained, String modelKey, int totalSimulations) {
            this.crafting = crafting;
            this.trained = trained;
            this.modelKey = modelKey;
            this.totalSimulations = totalSimulations;
        }
    }

    private static final class SimulationState {
        private final boolean crafting;
        private final boolean trained;
        private final String modelKey;
        private final int totalSimulations;

        private SimulationState(boolean crafting, boolean trained, String modelKey, int totalSimulations) {
            this.crafting = crafting;
            this.trained = trained;
            this.modelKey = modelKey;
            this.totalSimulations = totalSimulations;
        }
    }

    private static final class DynamicsNetworkView {
        private final String ownedPartName;
        private final boolean hasReader;
        private final boolean hasActiveSink;
        private final Map<String, Map<String, Integer>> interfaceCounts;

        private DynamicsNetworkView(String ownedPartName, boolean hasReader, boolean hasActiveSink,
                                     Map<String, Map<String, Integer>> interfaceCounts) {
            this.ownedPartName = ownedPartName;
            this.hasReader = hasReader;
            this.hasActiveSink = hasActiveSink;
            this.interfaceCounts = interfaceCounts;
        }
    }

    private static final class EnergyView {
        private final double stored;
        private final double averageInjection;

        private EnergyView(double stored, double averageInjection) {
            this.stored = stored;
            this.averageInjection = averageInjection;
        }
    }

    private static final class TerminalView {
        private final boolean powered;
        private final boolean hasFormattedCell;
        private final Map<String, Long> networkItems;
        private final Object monitor;
        private final Object grid;

        private TerminalView(boolean powered, boolean hasFormattedCell, Map<String, Long> networkItems,
                             Object monitor, Object grid) {
            this.powered = powered;
            this.hasFormattedCell = hasFormattedCell;
            this.networkItems = networkItems;
            this.monitor = monitor;
            this.grid = grid;
        }
    }

    private static final class TerminalSnapshot {
        private final Map<String, Long> playerItems;
        private final Map<String, Long> networkItems;
        private final Object monitor;
        private final Object grid;

        private TerminalSnapshot(Map<String, Long> playerItems, Map<String, Long> networkItems,
                                 Object monitor, Object grid) {
            this.playerItems = playerItems;
            this.networkItems = networkItems;
            this.monitor = monitor;
            this.grid = grid;
        }
    }

    private static final class CraftView {
        private final Object cpu;
        private final Object monitor;
        private final String outputKey;
        private final long outputCount;
        private final boolean assemblerActive;

        private CraftView(Object cpu, Object monitor, String outputKey, long outputCount, boolean assemblerActive) {
            this.cpu = cpu;
            this.monitor = monitor;
            this.outputKey = outputKey;
            this.outputCount = outputCount;
            this.assemblerActive = assemblerActive;
        }
    }

    private static final class CraftState {
        private final Object cpu;
        private final Object monitor;
        private final String outputKey;
        private final long outputCount;
        private boolean assemblerActive;

        private CraftState(Object cpu, Object monitor, String outputKey, long outputCount, boolean assemblerActive) {
            this.cpu = cpu;
            this.monitor = monitor;
            this.outputKey = outputKey;
            this.outputCount = outputCount;
            this.assemblerActive = assemblerActive;
        }
    }

    private static final class CraftCompletion {
        private final boolean complete;
        private final long outputCount;

        private CraftCompletion(boolean complete, long outputCount) {
            this.complete = complete;
            this.outputCount = outputCount;
        }
    }

    private static class WorldPos {
        private final int dimension;
        private final BlockPos position;

        private WorldPos(int dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position.toImmutable();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof WorldPos)) return false;
            WorldPos that = (WorldPos) other;
            return dimension == that.dimension && position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return dimension * 31 + position.hashCode();
        }

        @Override
        public String toString() {
            return dimension + ":" + position.getX() + "," + position.getY() + "," + position.getZ();
        }
    }

    private static final class PartKey {
        private final WorldPos position;
        private final int dimension;
        private final EnumFacing side;

        private PartKey(int dimension, BlockPos position, EnumFacing side) {
            this.position = new WorldPos(dimension, position);
            this.dimension = dimension;
            this.side = side;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PartKey)) return false;
            PartKey that = (PartKey) other;
            return position.equals(that.position) && side == that.side;
        }

        @Override
        public int hashCode() {
            return position.hashCode() * 31 + side.ordinal();
        }

        @Override
        public String toString() {
            return position + "/" + side.getName();
        }
    }
}
