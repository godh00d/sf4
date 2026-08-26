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
        addStage("s0", "First Steps", "age1",
            "Punch a tree, craft a table, get dirt.",
            "The Descent of Dirt", "oak_sapling", "dirt", "cobblestone");
        addStage("s1", "Cobblestone Age", "age1",
            "You have stone. Now shape it.",
            "Cobblestone Generator", "cobblestone", "gravel", "iron_ingot");
        addStage("s2", "Sifting Beginnings", "age1",
            "The sieve holds secrets. Shake it.",
            "Sieve", "sieve", "hammer", "iron_mesh");
        addStage("s3", "Iron Age", "age1",
            "Iron changes everything.",
            "Iron Ingot", "iron_ingot", "iron_block", "anvil");
        addStage("s4", "Tree Variety", "age1",
            "One sapling is not enough.",
            "Collect All Saplings", "oak_sapling", "birch_sapling", "spruce_sapling");
        addStage("s5", "Basic Machines", "age1",
            "Machines hum. The sky awakens.",
            "Furnace", "generator", "cobblestone_generator", "redstone");
        addStage("s6", "Bonsai Beginnings", "age_farming",
            "Bonsai: tiny trees, huge potential.",
            "Bonsai Pot", "bonsai_pot", "oak_sapling", "cocoa_beans");
        addStage("s7", "Tinkers Basics", "age_enhancement",
            "Tinkers makes tools worth keeping.",
            "Tinkers Station", "smeltery", "seared_brick", "casting_table");
        addStage("s8", "Power Generation", "age_power",
            "RF flows. Build generators.",
            "Simple Generator", "generator", "flux_capacitor", "energy_cell");
        addStage("s9", "Simple Storage", "age_storage",
            "Items go digital.",
            "Storage Network", "storage_network", "storage_cable", "storage_terminal");
        addStage("s10", "Nether Gateway", "age_exploration",
            "The Nether awaits.",
            "Nether Portal", "nether_portal", "obsidian", "flint_and_steel");
        addStage("s11", "The End Dimension", "age_exploration",
            "The End is not the end.",
            "Ender Dragon", "ender_dragon", "ender_pearl", "eye_of_ender");
        addStage("s12", "Endgame", "age_allthethingsomglol",
            "You have reached the end of the sky.",
            "Your Place Among Stars", "star", "prestige_point", "parabox");
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
