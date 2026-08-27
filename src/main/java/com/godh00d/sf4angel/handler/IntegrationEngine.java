package com.godh00d.sf4angel.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/** Small fail-closed runtime for integrations that can be proven from player-visible state. */
@Mod.EventBusSubscriber(modid = "sf4angel")
public final class IntegrationEngine {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String CAST_AWAY = "sf4angel:core/cast_away";
    private static final String MODIFIER_MOTIVE = "sf4angel:core/modifier_motive";
    private static final String LEVEL_HEADED_TOOL = "sf4angel:core/level_headed_tool";
    private static final String MODEL_CITIZEN = "sf4angel:core/model_citizen";
    private static final String DATA_WITH_EXPERIENCE = "sf4angel:core/data_with_experience";
    private static final String REALITY_ARMOR = "sf4angel:core/reality_armor";
    private static final String ORCHARD_ON_AUTOPILOT = "sf4angel:core/orchard_on_autopilot";
    private static final String SEEDS_OF_LIFE = "sf4angel:core/seeds_of_life";
    private static final String HOG_TIED = "sf4angel:core/hog_tied";
    private static final String ARMORED_TO_THE_TEETH = "sf4angel:optional/armored_to_the_teeth";
    private static final String UNBREAKABLE_RESOLVE = "sf4angel:optional/unbreakable_resolve";
    private static final Set<String> GLITCH_ARMOR = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "deepmoblearning:glitch_infused_helmet", "deepmoblearning:glitch_infused_chestplate",
        "deepmoblearning:glitch_infused_leggings", "deepmoblearning:glitch_infused_boots")));
    private static final List<IntegrationTrigger> TRIGGERS = Arrays.asList(
        trigger(CAST_AWAY, IntegrationEngine::hasReusableCast),
        trigger(MODEL_CITIZEN, IntegrationEngine::hasDataModel),
        trigger(REALITY_ARMOR, IntegrationEngine::hasGlitchArmor),
        trigger(ARMORED_TO_THE_TEETH, IntegrationEngine::hasModifiedConstructArmor),
        trigger(UNBREAKABLE_RESOLVE, IntegrationEngine::holdsUnbreakableTool));
    private static final Map<UUID, PlayerState> PLAYER_STATES = new HashMap<>();
    private static final String BONSAI_OWNER = "bonsai";
    private static final Map<World, Map<BlockPos, PotState>> TRACKED_POTS = new WeakHashMap<>();

    private IntegrationEngine() {
    }

    public static void tick(EntityPlayerMP player) {
        PlayerState state = PLAYER_STATES.computeIfAbsent(player.getUniqueID(), ignored -> new PlayerState());
        for (IntegrationTrigger trigger : TRIGGERS) {
            if (!state.submittedAdvancements.contains(trigger.advancementId) && trigger.matches(player, state)) {
                grant(player, trigger.advancementId);
            }
        }
        checkTaggedInventoryMilestones(player, state);
        checkToolLevel(player, state);
        checkModificationSession(player, state);
        checkDataModelLevel(player, state);
        checkOwnedBonsaiPots(player);
    }

    public static void removePlayer(UUID playerId) {
        PLAYER_STATES.remove(playerId);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        ResourceLocation blockName = event.getPlacedBlock().getBlock().getRegistryName();
        if (blockName == null || !"bonsaitrees:bonsaipot".equals(blockName.toString())) return;
        if (event.getPlacedBlock().getBlock().getMetaFromState(event.getPlacedBlock()) != 1) return;
        IntegrationOwnershipData.get(event.getWorld()).put(event.getWorld(), BONSAI_OWNER, event.getPos(),
            event.getPlayer().getUniqueID());
        potStates(event.getWorld()).put(event.getPos().toImmutable(), new PotState());
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote) return;
        IntegrationOwnershipData.get(event.getWorld()).removeAt(event.getWorld(), event.getPos());
        potStates(event.getWorld()).remove(event.getPos());
    }

    private static void grant(EntityPlayerMP player, String advancementId) {
        PlayerState state = PLAYER_STATES.computeIfAbsent(player.getUniqueID(), ignored -> new PlayerState());
        if (!state.submittedAdvancements.add(advancementId)) return;
        AchievementHandler.grantCriterion(player, advancementId);
    }

    private static IntegrationTrigger trigger(String advancementId, PlayerPredicate predicate) {
        return new IntegrationTrigger(advancementId, predicate);
    }

    private static boolean hasReusableCast(EntityPlayerMP player, PlayerState state) {
        for (ItemStack stack : player.inventory.mainInventory) {
            String name = registryName(stack);
            if ("tconstruct:cast_custom".equals(name) && stack.getMetadata() >= 0 && stack.getMetadata() <= 4
                && TinkersAdapter.isCastItem(stack.getItem())) {
                return true;
            }
            if (!"tconstruct:cast".equals(name) || stack.getMetadata() != 0 || !stack.hasTagCompound()) continue;
            String partType = stack.getTagCompound().getString("PartType");
            try {
                Item part = partType.isEmpty() ? null : Item.REGISTRY.getObject(new ResourceLocation(partType));
                if (part != null && TinkersAdapter.isToolPart(part)) return true;
            } catch (IllegalArgumentException ignored) {
                // Malformed external NBT is not evidence of a valid reusable cast.
            }
        }
        return false;
    }

    private static boolean hasDataModel(EntityPlayerMP player, PlayerState state) {
        for (ItemStack stack : allCarriedItems(player)) {
            if (isDataModel(stack)) return true;
        }
        return !readLearnerModels(player).isEmpty();
    }

    private static boolean hasGlitchArmor(EntityPlayerMP player, PlayerState state) {
        Set<String> equipped = new HashSet<>();
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && stack.getMetadata() == 0) equipped.add(registryName(stack));
        }
        return equipped.containsAll(GLITCH_ARMOR);
    }

    private static boolean hasModifiedConstructArmor(EntityPlayerMP player, PlayerState state) {
        if (player.inventory.armorInventory.size() != 4) return false;
        for (ItemStack stack : player.inventory.armorInventory) {
            ResourceLocation name = stack.isEmpty() ? null : stack.getItem().getRegistryName();
            if (name == null || !"conarm".equals(name.getResourceDomain()) || !hasNonDefaultModifier(stack)) return false;
        }
        return true;
    }

    private static boolean hasNonDefaultModifier(ItemStack stack) {
        for (NBTTagCompound modifier : modifierMap(stack).values()) {
            if (modifier.hasKey("level", 3) && modifier.getInteger("level") > 0) return true;
        }
        return false;
    }

    private static boolean holdsUnbreakableTool(EntityPlayerMP player, PlayerState state) {
        return isUnbreakable(player.getHeldItemMainhand()) || isUnbreakable(player.getHeldItemOffhand());
    }

    private static void checkTaggedInventoryMilestones(EntityPlayerMP player, PlayerState state) {
        boolean needsSeeds = !state.submittedAdvancements.contains(SEEDS_OF_LIFE);
        boolean needsMudBucket = !state.submittedAdvancements.contains(HOG_TIED);
        if (!needsSeeds && !needsMudBucket) return;

        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getMetadata() != 0 || !stack.hasTagCompound()) continue;
            NBTTagCompound tag = stack.getTagCompound();
            String name = registryName(stack);
            if (needsSeeds && "animalcrops:seeds".equals(name)
                && tag.hasKey("entity", 8) && !tag.getString("entity").isEmpty()) {
                grant(player, SEEDS_OF_LIFE);
                needsSeeds = false;
            } else if (needsMudBucket && "resourcehogs:mud_bucket".equals(name)
                && tag.hasKey("ResourceType", 8) && !tag.getString("ResourceType").isEmpty()) {
                grant(player, HOG_TIED);
                needsMudBucket = false;
            }
            if (!needsSeeds && !needsMudBucket) return;
        }
    }

    private static boolean isUnbreakable(ItemStack stack) {
        if (!isTinkersTool(stack)) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getCompoundTag("Stats").getBoolean("Unbreakable");
    }

    private static void checkToolLevel(EntityPlayerMP player, PlayerState state) {
        ItemStack[] held = {player.getHeldItemMainhand(), player.getHeldItemOffhand()};
        for (int hand = 0; hand < held.length; hand++) {
            ItemStack stack = held[hand];
            int level = toolLevel(stack);
            if (stack == state.heldTools[hand] && level > state.heldLevels[hand] && state.heldLevels[hand] >= 0) {
                grant(player, LEVEL_HEADED_TOOL);
            }
            state.heldTools[hand] = stack;
            state.heldLevels[hand] = level;
        }
    }

    private static int toolLevel(ItemStack stack) {
        NBTTagCompound modifier = modifierMap(stack).get("toolleveling");
        return modifier == null ? -1 : modifier.getInteger("level");
    }

    private static void checkModificationSession(EntityPlayerMP player, PlayerState state) {
        boolean modificationContainer = isModificationContainer(player.openContainer.getClass().getName());
        if (modificationContainer && !state.inModificationContainer) {
            state.modifierBaseline = snapshotModifierBaselines(player);
        } else if (!modificationContainer && state.inModificationContainer) {
            if (hasNewModifier(player, state.modifierBaseline)) grant(player, MODIFIER_MOTIVE);
            state.modifierBaseline = Collections.emptyMap();
        }
        state.inModificationContainer = modificationContainer;
    }

    private static boolean isModificationContainer(String className) {
        return "slimeknights.tconstruct.tools.common.inventory.ContainerToolStation".equals(className)
            || "slimeknights.tconstruct.tools.common.inventory.ContainerToolForge".equals(className);
    }

    private static Map<String, Set<String>> snapshotModifierBaselines(EntityPlayerMP player) {
        Map<String, Set<String>> result = new HashMap<>();
        for (ItemStack stack : containerAndCarriedItems(player)) {
            if (!isTinkersTool(stack)) continue;
            Set<String> modifiers = modifierMap(stack).keySet();
            result.computeIfAbsent(toolSignature(stack), ignored -> new HashSet<>()).addAll(modifiers);
        }
        return result;
    }

    private static boolean hasNewModifier(EntityPlayerMP player, Map<String, Set<String>> baseline) {
        for (ItemStack stack : allCarriedItems(player)) {
            if (!isTinkersTool(stack)) continue;
            Set<String> before = baseline.get(toolSignature(stack));
            Set<String> after = modifierMap(stack).keySet();
            if (before != null && after.containsAll(before) && after.size() > before.size()) return true;
        }
        return false;
    }

    private static String toolSignature(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        String materials = tag == null ? "" : tag.getCompoundTag("TinkerData").getTagList("Materials", 8).toString();
        return registryName(stack) + '|' + materials;
    }

    private static void checkDataModelLevel(EntityPlayerMP player, PlayerState state) {
        if (state.submittedAdvancements.contains(DATA_WITH_EXPERIENCE)) return;
        for (ItemStack stack : allCarriedItems(player)) {
            if (isDataModel(stack) && modelTier(stack) >= 1) {
                grant(player, DATA_WITH_EXPERIENCE);
                return;
            }
        }
        for (ModelProgress model : readLearnerModels(player).values()) {
            if (model.tier >= 1) {
                grant(player, DATA_WITH_EXPERIENCE);
                return;
            }
        }
    }

    private static int modelTier(ItemStack model) {
        return model.hasTagCompound() ? model.getTagCompound().getInteger("tier") : 0;
    }

    private static Map<ModelSlot, ModelProgress> readLearnerModels(EntityPlayerMP player) {
        Map<ModelSlot, ModelProgress> result = new HashMap<>();
        for (ItemStack learner : allCarriedItems(player)) {
            if (!"deepmoblearning:deep_learner".equals(registryName(learner)) || !learner.hasTagCompound()) continue;
            NBTTagList inventory = learner.getTagCompound().getTagList("inventory", 10);
            for (int slot = 0; slot < inventory.tagCount(); slot++) {
                ItemStack model = new ItemStack(inventory.getCompoundTagAt(slot));
                if (!isDataModel(model)) continue;
                result.put(new ModelSlot(learner, slot, registryName(model)),
                    new ModelProgress(modelTier(model)));
            }
        }
        return result;
    }

    private static void checkOwnedBonsaiPots(EntityPlayerMP player) {
        IntegrationOwnershipData ownership = IntegrationOwnershipData.get(player.world);
        Map<BlockPos, PotState> states = potStates(player.world);
        for (IntegrationOwnershipData.Record record : ownership.records(player.world, BONSAI_OWNER)) {
            if (!BONSAI_OWNER.equals(record.kind) || !record.owner.equals(player.getUniqueID())) continue;
            BlockPos position = record.position;
            if (!player.world.isBlockLoaded(position)) continue;
            ResourceLocation blockName = player.world.getBlockState(position).getBlock().getRegistryName();
            if (blockName == null || !"bonsaitrees:bonsaipot".equals(blockName.toString())) {
                ownership.remove(player.world, BONSAI_OWNER, position);
                states.remove(position);
                continue;
            }
            PotState state = states.computeIfAbsent(position, ignored -> new PotState());
            BonsaiView view = BonsaiAdapter.read(player.world.getTileEntity(position));
            if (view == null || !view.hopping) continue;
            Map<String, Integer> destination = itemCounts(player.world.getTileEntity(position.down()), EnumFacing.UP);
            boolean cycle = state.progress >= 0.0D && view.progress < state.progress;
            boolean directExport = cycle && transferredItem(state.bufferItems, view.bufferItems,
                destination, state.destinationItems);
            boolean bufferedExport = state.harvestPending && view.bufferCount < state.bufferCount
                && transferredItem(state.bufferItems, view.bufferItems, destination, state.destinationItems);
            if (directExport || bufferedExport) {
                grant(player, ORCHARD_ON_AUTOPILOT);
                state.harvestPending = false;
            } else if (cycle && (view.bufferCount > state.bufferCount || view.bufferCount > 0)) {
                state.harvestPending = true;
            }
            state.progress = view.progress;
            state.bufferCount = view.bufferCount;
            state.bufferItems = view.bufferItems;
            state.destinationItems = destination;
        }
    }

    private static boolean transferredItem(Map<String, Integer> sourceBefore, Map<String, Integer> sourceAfter,
                                            Map<String, Integer> targetAfter, Map<String, Integer> targetBefore) {
        for (String item : sourceBefore.keySet()) {
            if (sourceBefore.get(item) > sourceAfter.getOrDefault(item, 0)
                && targetAfter.getOrDefault(item, 0) > targetBefore.getOrDefault(item, 0)) return true;
        }
        return false;
    }

    private static Map<String, Integer> itemCounts(TileEntity tile, EnumFacing side) {
        if (tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            return Collections.emptyMap();
        }
        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (handler == null) return Collections.emptyMap();
        Map<String, Integer> result = new HashMap<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) result.merge(itemKey(stack), stack.getCount(), Integer::sum);
        }
        return result;
    }

    private static String itemKey(ItemStack stack) {
        return registryName(stack) + '@' + stack.getMetadata() + '#'
            + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "");
    }

    private static Map<BlockPos, PotState> potStates(World world) {
        return TRACKED_POTS.computeIfAbsent(world, ignored -> new HashMap<>());
    }

    private static int itemCount(TileEntity tile, EnumFacing side) {
        if (tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) return -1;
        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        return handler == null ? -1 : itemCount(handler);
    }

    private static int itemCount(IItemHandler handler) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) count += handler.getStackInSlot(slot).getCount();
        return count;
    }

    private static boolean isDataModel(ItemStack stack) {
        ResourceLocation name = stack.isEmpty() ? null : stack.getItem().getRegistryName();
        return name != null && stack.getMetadata() == 0 && "deepmoblearning".equals(name.getResourceDomain())
            && name.getResourcePath().startsWith("data_model_") && !"data_model_blank".equals(name.getResourcePath());
    }

    private static boolean isTinkersTool(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag.hasKey("TinkerData", 10) && tag.hasKey("Stats", 10) && tag.hasKey("Modifiers", 9);
    }

    private static Map<String, NBTTagCompound> modifierMap(ItemStack stack) {
        if (!isTinkersTool(stack)) return Collections.emptyMap();
        Map<String, NBTTagCompound> result = new LinkedHashMap<>();
        NBTTagList modifiers = stack.getTagCompound().getTagList("Modifiers", 10);
        for (int index = 0; index < modifiers.tagCount(); index++) {
            NBTTagCompound modifier = modifiers.getCompoundTagAt(index);
            String identifier = modifier.getString("identifier");
            if (!identifier.isEmpty()) result.put(identifier, modifier);
        }
        return result;
    }

    private static List<ItemStack> allCarriedItems(EntityPlayerMP player) {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.addAll(player.inventory.mainInventory);
        stacks.addAll(player.inventory.offHandInventory);
        stacks.addAll(player.inventory.armorInventory);
        return stacks;
    }

    private static List<ItemStack> containerAndCarriedItems(EntityPlayerMP player) {
        List<ItemStack> stacks = allCarriedItems(player);
        player.openContainer.inventorySlots.forEach(slot -> stacks.add(slot.getStack()));
        return stacks;
    }

    private static String registryName(ItemStack stack) {
        ResourceLocation name = stack.isEmpty() ? null : stack.getItem().getRegistryName();
        return name == null ? "" : name.toString();
    }

    private interface PlayerPredicate {
        boolean test(EntityPlayerMP player, PlayerState state);
    }

    private static final class IntegrationTrigger {
        private final String advancementId;
        private final PlayerPredicate predicate;

        private IntegrationTrigger(String advancementId, PlayerPredicate predicate) {
            this.advancementId = advancementId;
            this.predicate = predicate;
        }

        private boolean matches(EntityPlayerMP player, PlayerState state) {
            return predicate.test(player, state);
        }
    }

    private static final class PlayerState {
        private final ItemStack[] heldTools = {ItemStack.EMPTY, ItemStack.EMPTY};
        private final int[] heldLevels = {-1, -1};
        private final Set<String> submittedAdvancements = new HashSet<>();
        private boolean inModificationContainer;
        private Map<String, Set<String>> modifierBaseline = Collections.emptyMap();
    }

    private static final class ModelSlot {
        private final ItemStack learner;
        private final int slot;
        private final String modelId;

        private ModelSlot(ItemStack learner, int slot, String modelId) {
            this.learner = learner;
            this.slot = slot;
            this.modelId = modelId;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ModelSlot)) return false;
            ModelSlot that = (ModelSlot) other;
            return learner == that.learner && slot == that.slot && modelId.equals(that.modelId);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(learner) * 31 * 31 + slot * 31 + modelId.hashCode();
        }
    }

    private static final class ModelProgress {
        private final int tier;

        private ModelProgress(int tier) {
            this.tier = tier;
        }
    }

    private static final class PotState {
        private double progress = -1.0D;
        private int bufferCount = -1;
        private Map<String, Integer> bufferItems = Collections.emptyMap();
        private Map<String, Integer> destinationItems = Collections.emptyMap();
        private boolean harvestPending;

        private PotState() {
        }
    }

    private static final class BonsaiView {
        private final boolean hopping;
        private final double progress;
        private final int bufferCount;
        private final Map<String, Integer> bufferItems;

        private BonsaiView(boolean hopping, double progress, int bufferCount, Map<String, Integer> bufferItems) {
            this.hopping = hopping;
            this.progress = progress;
            this.bufferCount = bufferCount;
            this.bufferItems = bufferItems;
        }
    }

    private static final class BonsaiAdapter {
        private static boolean initialized;
        private static boolean available;
        private static boolean warned;
        private static Class<?> tileClass;
        private static Method isHoppingPot;
        private static Method getProgress;
        private static Method getHoppingItemBuffer;

        private BonsaiAdapter() {
        }

        private static BonsaiView read(TileEntity tile) {
            initialize();
            if (!available || tile == null || !tileClass.isInstance(tile)) return null;
            try {
                boolean hopping = (Boolean) isHoppingPot.invoke(tile);
                double progress = ((Number) getProgress.invoke(tile)).doubleValue();
                Object buffer = getHoppingItemBuffer.invoke(tile);
                if (!(buffer instanceof IItemHandler)) return null;
                IItemHandler handler = (IItemHandler) buffer;
                Map<String, Integer> items = new HashMap<>();
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (!stack.isEmpty()) items.merge(itemKey(stack), stack.getCount(), Integer::sum);
                }
                return new BonsaiView(hopping, progress, itemCount(handler), items);
            } catch (ReflectiveOperationException | LinkageError exception) {
                available = false;
                warn("Bonsai integration disabled after an incompatible runtime call", exception);
                return null;
            }
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            if (!Loader.isModLoaded("bonsaitrees")) {
                warn("Bonsai integration unavailable because bonsaitrees is not loaded", null);
                return;
            }
            try {
                tileClass = Class.forName("org.dave.bonsaitrees.tile.TileBonsaiPot");
                isHoppingPot = tileClass.getMethod("isHoppingPot");
                getProgress = tileClass.getMethod("getProgress");
                getHoppingItemBuffer = tileClass.getMethod("getHoppingItemBuffer");
                available = true;
            } catch (ReflectiveOperationException | LinkageError exception) {
                warn("Bonsai integration unavailable for the installed API", exception);
            }
        }

        private static void warn(String message, Throwable cause) {
            if (warned) return;
            warned = true;
            if (cause == null) LOGGER.warn(message);
            else LOGGER.warn("{}: {}", message, cause.toString());
        }
    }

    private static final class TinkersAdapter {
        private static Class<?> castClass;
        private static Class<?> toolPartClass;
        private static boolean initialized;

        private static boolean isCastItem(Item item) {
            initialize();
            return castClass != null && castClass.isInstance(item);
        }

        private static boolean isToolPart(Item item) {
            initialize();
            return toolPartClass != null && toolPartClass.isInstance(item);
        }

        private static void initialize() {
            if (initialized) return;
            initialized = true;
            try {
                castClass = Class.forName("slimeknights.tconstruct.smeltery.item.ItemCastCustom");
                toolPartClass = Class.forName("slimeknights.tconstruct.library.tools.ToolPart");
            } catch (ReflectiveOperationException | LinkageError exception) {
                LOGGER.warn("Tinkers cast validation unavailable: {}", exception.toString());
                castClass = null;
                toolPartClass = null;
            }
        }
    }
}
