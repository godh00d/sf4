package com.godh00d.sf4angel.knowledge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.*;

public class AngelOracle {

    private static final Map<String, Stage> stages = new LinkedHashMap<>();
    private static final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void init() {
        loadStages();
    }

    private static void loadStages() {
        try {
            InputStream is = AngelOracle.class.getResourceAsStream("/assets/sf4angel/sf4angel/sf4_knowledge.json");
            if (is == null) {
                loadDefaultStages();
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            Gson gson = new Gson();
            JsonObject root = gson.fromJson(sb.toString(), JsonObject.class);

            JsonArray stagesArray = root.getAsJsonArray("stages");
            for (JsonElement e : stagesArray) {
                JsonObject obj = e.getAsJsonObject();
                Stage stage = new Stage();
                stage.id = obj.get("id").getAsString();
                stage.name = obj.get("name").getAsString();
                stage.age = obj.get("age").getAsString();
                stage.hint = obj.has("hint") ? obj.get("hint").getAsString() : "";
                stage.nextGoal = obj.has("next_goal") ? obj.get("next_goal").getAsString() : "";
                stage.items = new ArrayList<>();
                if (obj.has("items")) {
                    for (JsonElement ie : obj.getAsJsonArray("items")) {
                        stage.items.add(ie.getAsString());
                    }
                }
                stages.put(stage.id, stage);
            }
        } catch (Exception e) {
            loadDefaultStages();
        }
    }

    private static void loadDefaultStages() {
        addStage("s0", "First Steps", "age1", "Plant a sapling on dirt. Everything begins there.",
            "Get a cobblestone generator running", "oak_sapling", "dirt", "cobblestone");
        addStage("s1", "Cobblestone Age", "age1", "You have stone. Now shape it.",
            "Build a simple sieve", "cobblestone", "gravel", "iron_ingot");
        addStage("s2", "Sifting Beginnings", "age1", "The sieve holds secrets. Shake it.",
            "Get iron from sifted materials", "sieve", "hammer", "iron_mesh");
        addStage("s3", "Iron Age", "age1", "Iron changes everything. Use it well.",
            "Upgrade to a mesh tier 2", "iron_ingot", "iron_block", "anvil");
        addStage("s4", "Resource Hogs", "age1", "The hogs provide. Do not question them.",
            "Breed resource hogs for materials", "resource_hog", "animal_feed", "pen");
        addStage("s5", "Basic Machines", "age1", "Machines hum. The sky awakens.",
            "Build a basic generator", "generator", "cobblestone_generator", "redstone");
        addStage("s6", "Redstone Logic", "age1", "Redstone is the blood of progress.",
            "Create an automated sieve", "redstone", "hopper", "chest");
        addStage("s7", "Tree Variety", "age1", "One sapling is not enough. Grow them all.",
            "Collect all tree sapling types", "oak_sapling", "birch_sapling", "spruce_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling");
        addStage("s8", "Bonsai Beginnings", "age_farming", "Bonsai: tiny trees, huge potential.",
            "Set up 3 bonsai pots", "bonsai_pot", "oak_sapling", "cocoa_beans");
        addStage("s9", "Crop Mastery", "age_farming", "Farming is not optional. It is survival.",
            "Grow every crop type at least once", "wheat", "carrot", "potato", "nether_wart");
        addStage("s10", "Animal Husbandry", "age_farming", "Animals are resources with legs.",
            "Breed all animal types", "cow", "pig", "chicken", "sheep");
        addStage("s11", "Market Trading", "age_farming", "The Market sells what the sky forgets.",
            "Buy 10 items from the Market", "market", "emerald");
        addStage("s12", "Tinkers Basics", "age_enhancement", "Tinkers makes tools worth keeping.",
            "Build a Tinkers' smeltery", "smeltery", "seared_brick", "casting_table");
        addStage("s13", "Modifier Mastery", "age_enhancement", "Modifiers are the soul of your tools.",
            "Apply 5 different modifiers to one tool", "modifier", "tool工作站");
        addStage("s14", "Enchanting Arts", "age_enhancement", "The enchantment table whispers.",
            "Enchant a diamond tool to level 30", "enchanting_table", "lapis_lazuli", "diamond_pickaxe");
        addStage("s15", "Glitch Armor", "age_enhancement", "Glitch: when reality breaks, armor it.",
            "Craft and equip a full Glitch Armor set", "glitch_helmet", "glitch_chestplate", "glitch_leggings", "glitch_boots");
        addStage("s16", "Deep Mob Learning", "age_power", "The mob data holds power you cannot imagine.",
            "Set up a Deep Mob Learning simulation chamber", "simulation_chamber", "data_model", "deep_learner");
        addStage("s17", "Power Generation", "age_power", "RF flows. Build generators.",
            "Generate 10,000 RF in one tick", "generator", "flux_capacitor", "energy_cell");
        addStage("s18", "Mekanism Machines", "age_power", "Mekanism bends atoms. You bend the sky.",
            "Build a full Mekanism processing chain", "mekanism_factory", "enrichment_chamber", "crusher");
        addStage("s19", "Nuclearcraft", "age_power", "Split the atom. Carefully.",
            "Build and power a nuclear reactor", "reactor", "fission_controller", "uranium");
        addStage("s20", "RFTools Power", "age_power", "RFTools powers the impossible.",
            "Build an RFTools powercell network", "powercell", "rftools_crafter", "dimension_builder");
        addStage("s21", "Simple Storage", "age_storage", "Simple storage. Complex satisfaction.",
            "Connect 10 inventories to Simple Storage", "storage_network", "storage_cable", "storage_terminal");
        addStage("s22", "AE2 Mastery", "age_storage", "Applied Energistics: where items go digital.",
            "Build a full AE2 ME system with autocrafting", "me_controller", "me_drive", "molecular_assembler");
        addStage("s23", "Barrel Empire", "age_storage", "Barrels hold the world. One item at a time.",
            "Fill 50 barrels with different items", "drawer", "drawer_controller", "upgrade");
        addStage("s24", "Nether Gateway", "age_exploration", "The Nether awaits. Bring fire resistance.",
            "Build and enter a Nether portal", "nether_portal", "obsidian", "flint_and_steel");
        addStage("s25", "The End Dimension", "age_exploration", "The End is not the end. It is the beginning.",
            "Defeat the Ender Dragon", "ender_dragon", "ender_pearl", "eye_of_ender");
        addStage("s26", "Twilight Forest", "age_exploration", "A forest between worlds. Beware the bosses.",
            "Enter the Twilight Forest and defeat the first boss", "twilight_portal", "naga_scale", "lich_fang");
        addStage("s27", "Lost Cities", "age_exploration", "Civilization fell. Loot what remains.",
            "Explore 5 Lost City buildings and find rare loot", "lost_city_map", "ruined_building", "lost_city_chest");
        addStage("s28", "Compact Machines", "age_exploration", "A dimension in a block. Do not question it.",
            "Build and enter a Compact Machine", "compact_machine", "personal_shrinking_device");
        addStage("s29", "Singularities", "age_allthethingsomglol", "Compress the universe into one block.",
            "Craft a Singularity", "singularity", "compressed_cobblestone", "compressed_obsidian");
        addStage("s30", "Max Reactor", "age_allthethingsomglol", "The reactor must be MAXIMUM.",
            "Build the largest possible reactor", "reactor_casing", "reactor_controller", "reactor_fuel_rod");
        addStage("s31", "Collectible Hunter", "age_allthethingsomglol", "The collectibles call to you.",
            "Collect every collectible in the game", "collectible", "collectible_case", "trophy");
        addStage("s32", "The Final Age", "age_allthethingsomglol", "You have reached the end of the sky. Or have you?",
            "Complete all advancements and earn Your Place Among Stars", "star", "prestige_point", "parabox");

        addStage("s33", "Ex Nihilo Mastery", "age1", "The sieve is your best friend. Treat it well.",
            "Automate sieve with hopper and chest", "sieve", "hammer", "hopper", "chest");
        addStage("s34", "Tree Farm Automation", "age1", "Trees grow. Machines harvest. The sky provides.",
            "Set up automated tree farm with hopper collection", "sapling", "oak_sapling", "hopper", "chest");
        addStage("s35", "Resource Loop", "age1", "Cobble -> gravel -> sand -> dust. The cycle continues.",
            "Complete the full hammering chain", "cobblestone", "gravel", "sand", "dust");
        addStage("s36", "Ender Pearl Path", "age_exploration", "Ender Pearls are your ticket to the End.",
            "Collect 16 Ender Pearls", "ender_pearl", "eye_of_ender");
        addStage("s37", "Potion Brewing", "age_exploration", "The brewing stand holds secrets the sky whispers.",
            "Brew 5 different potions", "brewing_stand", "nether_wart", "blaze_powder");
        addStage("s38", "Iron Golem Guardian", "age_power", "Iron Golems protect what you build.",
            "Spawn an Iron Golem", "iron_block", "pumpkin");
        addStage("s39", "Wither Skeleton Hunt", "age_exploration", "Three skulls. One soul sand body. Infinite regret.",
            "Collect 3 Wither Skeleton Skulls", "wither_skeleton_skull", "soul_sand");
        addStage("s40", "Dimension Hopper", "age_exploration", "Every dimension has something you need.",
            "Visit Nether, End, and Twilight Forest", "nether_portal", "twilight_portal", "ender_pearl");
        addStage("s41", "Power Grid", "age_power", "A network of generators, cables, and storage. The sky hums.",
            "Connect 5 generators to one storage cell", "generator", "fluxduct", "energy_cell");
        addStage("s42", "Mekanism Full Chain", "age_power", "Enrichment -> Crusher -> Infuser. The complete Mekanism loop.",
            "Build full Mekanism ore processing chain", "enrichment_chamber", "crusher", "metallurgic_infuser");
        addStage("s43", "Digital Storage Era", "age_storage", "Items go digital. The sky becomes data.",
            "Set up AE2 ME system with 4 storage cells", "me_controller", "me_drive", "storage_cell_4k");
        addStage("s44", "Automation Empire", "age_power", "Machines do your bidding. The sky bends to automation.",
            "Automate 10 different processes", "hopper", "itemduct", "me_import_bus");
        addStage("s45", "Mob Data Harvest", "age_power", "Kill, simulate, repeat. Mob data is power.",
            "Train 5 data models to pristine", "data_model", "simulation_chamber", "deep_learner");
        addStage("s46", "Nuclear Power Plant", "age_power", "Split the atom. The sky trembles with energy.",
            "Build and power a NuclearCraft fission reactor", "reactor_controller", "fuel_rod", "coolant");
        addStage("s47", "Fusion Dreams", "age_power", "Fuse atoms. Create stars. The sky bows to fusion.",
            "Build a NuclearCraft fusion reactor", "fusion_controller", "tritium", "dt_fuel");
        addStage("s48", "Dimension Creator", "age_power", "You define reality now. RFTools dimensions await.",
            "Create an RFTools dimension with dimlets", "dimension_builder", "dimlet", "rftools_dim");
        addStage("s49", "All Twilight Bosses", "age_exploration", "Seven bosses. One forest. Total domination.",
            "Defeat all Twilight Forest bosses", "naga_scale", "lich_fang", "hydra_head", "snow_queen_trophy");
        addStage("s50", "Endgame Collection", "age_allthethingsomglol", "Collect everything. The sky demands completeness.",
            "Collect 50 collectibles", "collectible", "collectible_case");
        addStage("s51", "Singularity Master", "age_allthethingsomglol", "4096 becomes 1. The ultimate compression.",
            "Craft 5 different Singularities", "singularity", "extended_crafting_table");
        addStage("s52", "Prestige Hunter", "age_allthethingsomglol", "Prestige points unlock reality-warping powers.",
            "Earn 10 Prestige Points", "prestige_point", "parabox");
        addStage("s53", "The Sky Remembers", "age_allthethingsomglol", "You have done everything. The sky will remember you.",
            "Complete all advancements and earn Your Place Among Stars", "nether_star", "dragon_egg", "prestige_point");
    }

    private static void addStage(String id, String name, String age, String hint, String nextGoal, String... items) {
        Stage stage = new Stage();
        stage.id = id;
        stage.name = name;
        stage.age = age;
        stage.hint = hint;
        stage.nextGoal = nextGoal;
        stage.items = Arrays.asList(items);
        stages.put(id, stage);
    }

    public static Stage getCurrentStage(EntityPlayer player) {
        PlayerProgress progress = playerProgress.computeIfAbsent(player.getUniqueID(), k -> new PlayerProgress());
        return stages.getOrDefault(progress.currentStageId, stages.get("s0"));
    }

    public static void advanceStage(EntityPlayer player) {
        PlayerProgress progress = playerProgress.computeIfAbsent(player.getUniqueID(), k -> new PlayerProgress());
        List<String> keys = new ArrayList<>(stages.keySet());
        int idx = keys.indexOf(progress.currentStageId);
        if (idx >= 0 && idx < keys.size() - 1) {
            progress.currentStageId = keys.get(idx + 1);
        }
    }

    public static void setStage(EntityPlayer player, String stageId) {
        if (stages.containsKey(stageId)) {
            PlayerProgress progress = playerProgress.computeIfAbsent(player.getUniqueID(), k -> new PlayerProgress());
            progress.currentStageId = stageId;
        }
    }

    public static String getHintForCurrentStage(EntityPlayer player) {
        Stage stage = getCurrentStage(player);
        return stage != null ? stage.hint : "The sky has no guidance for you yet.";
    }

    public static String getNextGoal(EntityPlayer player) {
        Stage stage = getCurrentStage(player);
        return stage != null ? stage.nextGoal : "Explore. Build. Survive.";
    }

    public static String getGoalAndHint(EntityPlayer player) {
        Stage stage = getCurrentStage(player);
        if (stage == null) return "The sky awaits your first move.";
        return "Current goal: " + stage.nextGoal + " | Hint: " + stage.hint;
    }

    public static boolean hasItemInInventory(EntityPlayer player, String itemId) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            net.minecraft.item.ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().getRegistryName() != null) {
                if (stack.getItem().getRegistryName().toString().contains(itemId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void checkInventoryAndAdvance(EntityPlayer player) {
        Stage stage = getCurrentStage(player);
        if (stage == null) return;

        int found = 0;
        for (String item : stage.items) {
            if (hasItemInInventory(player, item)) found++;
        }

        if (found >= stage.items.size()) {
            advanceStage(player);
        }
    }

    public static Map<String, Stage> getAllStages() {
        return Collections.unmodifiableMap(stages);
    }

    public static int getStageIndex(String stageId) {
        return new ArrayList<>(stages.keySet()).indexOf(stageId);
    }

    public static int getTotalStages() {
        return stages.size();
    }

    public static List<String> getStagesByAge(String age) {
        List<String> result = new ArrayList<>();
        for (Stage stage : stages.values()) {
            if (stage.age.equals(age)) result.add(stage.id);
        }
        return result;
    }

    public static class Stage {
        public String id = "";
        public String name = "";
        public String age = "";
        public String hint = "";
        public String nextGoal = "";
        public List<String> items = new ArrayList<>();
    }

    private static class PlayerProgress {
        String currentStageId = "s0";
        int advancementsCompleted = 0;
        Set<String> completedAdvancements = new HashSet<>();
    }
}
