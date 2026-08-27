package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class AchievementHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final String CUSTOM = "custom";
    private static final int CROUCH_WINDOW_TICKS = 20 * 10;
    private static final int CROUCH_TARGET = 20;
    private static final Map<UUID, Integer> angelAppearanceCount = new HashMap<>();
    private static final Map<UUID, Boolean> sneakingStates = new HashMap<>();
    private static final Map<UUID, Deque<Long>> crouchTransitions = new HashMap<>();
    private static final Map<UUID, SleepState> sleepingStates = new HashMap<>();

    private AchievementHandler() {
    }

    public static void grantCriterion(EntityPlayerMP player, String advancementId) {
        Advancement advancement = getAdvancement(player, advancementId);
        if (advancement == null) {
            LOGGER.warn("Cannot grant missing advancement {}", advancementId);
            return;
        }
        if (!advancement.getCriteria().containsKey(CUSTOM)) {
            LOGGER.warn("Cannot grant missing criterion {} for advancement {}", CUSTOM, advancementId);
            return;
        }
        player.getAdvancements().grantCriterion(advancement, CUSTOM);
    }

    public static void checkTwerk(EntityPlayerMP player) {
        checkSleepCycle(player);
        UUID id = player.getUniqueID();
        boolean sneaking = player.isSneaking();
        boolean wasSneaking = sneakingStates.getOrDefault(id, sneaking);
        sneakingStates.put(id, sneaking);
        if (!sneaking || wasSneaking || !isNearSapling(player)) return;

        long now = player.world.getTotalWorldTime();
        Deque<Long> transitions = crouchTransitions.computeIfAbsent(id, ignored -> new ArrayDeque<>());
        while (!transitions.isEmpty() && now - transitions.peekFirst() > CROUCH_WINDOW_TICKS) {
            transitions.removeFirst();
        }
        transitions.addLast(now);
        if (transitions.size() >= CROUCH_TARGET) {
            grantCriterion(player, "sf4angel:optional/sticky_keys");
            transitions.clear();
        }
    }

    private static boolean isNearSapling(EntityPlayerMP player) {
        BlockPos origin = new BlockPos(player);
        for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(-5, -5, -5), origin.add(5, 5, 5))) {
            if (pos.distanceSq(origin) > 25.0D || !player.world.isBlockLoaded(pos)) continue;
            Block block = player.world.getBlockState(pos).getBlock();
            if (isSaplingLike(block, block.getMetaFromState(player.world.getBlockState(pos)))) return true;
        }
        return false;
    }

    private static boolean isSaplingLike(Block block, int metadata) {
        if (block == Blocks.SAPLING) return true;
        Item item = Item.getItemFromBlock(block);
        if (item != null) {
            ItemStack candidate = new ItemStack(item, 1, metadata);
            for (ItemStack sapling : OreDictionary.getOres("treeSapling", false)) {
                if (OreDictionary.itemMatches(sapling, candidate, false)) return true;
            }
        }
        ResourceLocation name = block.getRegistryName();
        return name != null && name.getResourcePath().toLowerCase(Locale.ROOT).contains("sapling");
    }

    private static void checkSleepCycle(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        SleepState state = sleepingStates.get(id);
        if (player.isPlayerSleeping()) {
            if (state == null) {
                sleepingStates.put(id, new SleepState(player.world.getWorldTime()));
            }
            return;
        }
        if (state == null) return;
        sleepingStates.remove(id);
        long wakeTime = player.world.getWorldTime();
        long wakeDay = wakeTime / 24000L;
        long startDay = state.startTime / 24000L;
        if (wakeDay > startDay && wakeTime % 24000L < 1000L && player.world.isDaytime()) {
            grantCriterion(player, "sf4angel:optional/nap_time");
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        NBTTagCompound persisted = getPersistedData(event.getPlayer());
        persisted.setInteger("sf4angelBlocksMined", persisted.getInteger("sf4angelBlocksMined") + 1);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        if (event.getPlacedBlock().getBlock() != Blocks.REEDS) return;
        net.minecraft.block.state.IBlockState belowState = event.getWorld().getBlockState(event.getPos().down());
        Block below = belowState.getBlock();
        ResourceLocation belowName = below.getRegistryName();
        if (belowName != null && "snad:snad".equals(belowName.toString())
            && below.getMetaFromState(belowState) == 0) {
            grantCriterion((EntityPlayerMP) event.getPlayer(), "sf4angel:core/cane_and_able");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        EntityPlayer player = event.getCausedByPlayer();
        if (!(player instanceof EntityPlayerMP) || player.world.isRemote) return;
        if (!(event.getParentA() instanceof EntityAnimal) || !(event.getParentB() instanceof EntityAnimal)) return;
        EntityAnimal first = (EntityAnimal) event.getParentA();
        EntityAnimal second = (EntityAnimal) event.getParentB();
        if (first.isChild() || second.isChild() || event.getChild() == null) return;

        grantCriterion((EntityPlayerMP) player, "sf4angel:core/barnyard_beginnings");
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        Entity source = event.getSource().getTrueSource();
        if (!(source instanceof EntityPlayerMP)) return;
        if (event.getEntityLiving() instanceof EntityWither) {
            grantCriterion((EntityPlayerMP) source, "sf4angel:core/wither_or_not");
        } else if (event.getEntityLiving() instanceof EntityDragon) {
            grantCriterion((EntityPlayerMP) source, "sf4angel:core/dragon_eviction_notice");
        }

        ResourceLocation entityName = EntityList.getKey(event.getEntityLiving());
        if (entityName == null) return;
        switch (entityName.toString()) {
            case "twilightforest:naga":
                grantCriterion((EntityPlayerMP) source, "sf4angel:core/naga_have_i_ever");
                break;
            case "twilightforest:lich":
                grantCriterion((EntityPlayerMP) source, "sf4angel:core/lich_please");
                break;
            case "twilightforest:hydra":
                grantCriterion((EntityPlayerMP) source, "sf4angel:core/hydra_expectations");
                break;
            case "twilightforest:snow_queen":
                grantCriterion((EntityPlayerMP) source, "sf4angel:core/ice_queen_cometh");
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.player.world.isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (event.toDim == -1) {
            grantCriterion(player, "sf4angel:core/nether_say_never");
        } else if (event.toDim == 1) {
            grantCriterion(player, "sf4angel:core/the_void_blinks_back");
        }
    }

    @SubscribeEvent
    public static void onFoodConsumed(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP) || event.getEntityLiving().world.isRemote) return;
        ItemStack consumed = event.getItem();
        if (consumed.isEmpty() || !(consumed.getItem() instanceof ItemFood)) return;
        ResourceLocation name = consumed.getItem().getRegistryName();
        if (name == null) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        NBTTagCompound persisted = getPersistedData(player);
        NBTTagList foods = persisted.getTagList("sf4angelDistinctFoods", 8);
        String key = name + "@" + consumed.getMetadata();
        for (int i = 0; i < foods.tagCount(); i++) {
            if (key.equals(foods.getStringTagAt(i))) return;
        }
        foods.appendTag(new NBTTagString(key));
        persisted.setTag("sf4angelDistinctFoods", foods);
        if (foods.tagCount() >= 20) {
            grantCriterion(player, "sf4angel:optional/a_balanced_sky_diet");
        }
    }

    public static void recordAngelAppearance(EntityPlayerMP player) {
        NBTTagCompound persisted = getPersistedData(player);
        int appearances = persisted.getInteger("sf4angelAppearances") + 1;
        persisted.setInteger("sf4angelAppearances", appearances);
    }

    public static void removePlayer(UUID id) {
        sneakingStates.remove(id);
        crouchTransitions.remove(id);
        sleepingStates.remove(id);
    }

    public static int getAngelAppearances(EntityPlayer player) {
        return angelAppearanceCount.getOrDefault(player.getUniqueID(), 0);
    }

    public static String getNextGoal(EntityPlayerMP player) {
        for (Map.Entry<String, List<String>> node : CoreAdvancementCatalog.prerequisites().entrySet()) {
            Advancement advancement = getAdvancement(player, node.getKey());
            if (advancement != null && !player.getAdvancements().getProgress(advancement).isDone()
                && areComplete(player, node.getValue())) {
                return getAdvancementTitle(player, node.getKey());
            }
        }
        return null;
    }

    private static boolean areComplete(EntityPlayerMP player, List<String> ids) {
        for (String id : ids) {
            if (!isComplete(player, id)) return false;
        }
        return true;
    }

    private static boolean isComplete(EntityPlayerMP player, String id) {
        Advancement advancement = getAdvancement(player, id);
        return advancement != null && player.getAdvancements().getProgress(advancement).isDone();
    }

    private static Advancement getAdvancement(EntityPlayerMP player, String id) {
        return player.getServerWorld().getAdvancementManager().getAdvancement(new ResourceLocation(id));
    }

    private static String getAdvancementTitle(EntityPlayerMP player, String id) {
        Advancement advancement = getAdvancement(player, id);
        if (advancement != null && advancement.getDisplay() != null) {
            return advancement.getDisplay().getTitle().getUnformattedText();
        }
        String path = id.substring(id.lastIndexOf('/') + 1).replace('_', ' ');
        StringBuilder title = new StringBuilder(path.length());
        boolean capitalize = true;
        for (char character : path.toCharArray()) {
            title.append(capitalize ? Character.toUpperCase(character) : character);
            capitalize = character == ' ';
        }
        return title.toString();
    }

    public static void onAdvancementCompleted(EntityPlayerMP player, String advancementId) {
        angelAppearanceCount.merge(player.getUniqueID(), 1, Integer::sum);
        LOGGER.info("Advancement completed for {}: {}", player.getName(), advancementId);

        TypewriterHandler.queueMessage(player,
            AngelPersonality.getAdvancementGreeting(getAdvancementTitle(player, advancementId)), 0, 0);
        String nextGoal = getNextGoal(player);
        if (nextGoal != null) {
            TypewriterHandler.queueMessage(player, "Next goal: " + nextGoal, 80, 0);
        }
        if (getAngelAppearances(player) == 50) {
            TypewriterHandler.queueMessage(player, "The angel smiles upon you, faithful companion.", 120, 0);
        }
        TypewriterHandler.queueMessage(player, AngelPersonality.getRandomDepartureLine(), 160, 0);
        TypewriterHandler.despawnWhenReady(player);
        spawnAngelIfAbsent(player);
        setAngelMood(player, EntityAngel.MOOD_PROUD, 100);
    }

    private static void setAngelMood(EntityPlayerMP player, int mood, int ticks) {
        AxisAlignedBB box = new AxisAlignedBB(player.posX - 15, player.posY - 5, player.posZ - 15,
            player.posX + 15, player.posY + 15, player.posZ + 15);
        for (EntityAngel angel : player.world.getEntitiesWithinAABB(EntityAngel.class, box)) {
            if (player.getUniqueID().equals(angel.getOwnerId())) {
                angel.setMood(mood, ticks);
                angel.setLookTarget(player);
                return;
            }
        }
    }

    private static void spawnAngelIfAbsent(EntityPlayerMP player) {
        World world = player.world;
        AxisAlignedBB box = new AxisAlignedBB(player.posX - 15, player.posY - 5, player.posZ - 15,
            player.posX + 15, player.posY + 15, player.posZ + 15);
        for (EntityAngel angel : world.getEntitiesWithinAABB(EntityAngel.class, box)) {
            if (player.getUniqueID().equals(angel.getOwnerId())) return;
        }
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        double yaw = Math.toRadians(player.rotationYaw);
        angel.setPosition(player.posX - Math.sin(yaw) * 6.0D,
            player.posY + player.getEyeHeight() - 0.5D, player.posZ + Math.cos(yaw) * 6.0D);
        world.spawnEntity(angel);
        recordAngelAppearance(player);
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        Advancement advancement = event.getAdvancement();
        if (player.world.isRemote || advancement == null || advancement.getId() == null) return;
        String id = advancement.getId().toString();
        if (!CoreAdvancementCatalog.prerequisites().containsKey(id)) return;
        onAdvancementCompleted((EntityPlayerMP) player, id);
    }

    private static NBTTagCompound getPersistedData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static final class SleepState {
        private final long startTime;

        private SleepState(long startTime) {
            this.startTime = startTime;
        }
    }
}
