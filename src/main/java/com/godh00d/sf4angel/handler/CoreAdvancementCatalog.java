package com.godh00d.sf4angel.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The reduced core catalog and its prerequisite DAG, in display priority order. */
final class CoreAdvancementCatalog {

    private static final Map<String, List<String>> PREREQUISITES = new LinkedHashMap<>();

    static {
        add("starting_from_the_bottom");
        add("that_is_dir_tree", "starting_from_the_bottom");
        add("dirty_dancing", "that_is_dir_tree");
        add("captain_hook", "starting_from_the_bottom");
        add("stone_tree_oath", "that_is_dir_tree");
        add("stone_from_trees", "stone_tree_oath");
        add("gravel_travel", "stone_tree_oath");
        add("beachfront_property", "stone_tree_oath");
        add("clay_aiken", "stone_tree_oath");
        add("water_you_waiting_for", "oh_the_irony");
        add("clay_bucket_gamble", "clay_aiken");
        add("coal_in_bloom", "beachfront_property");
        add("ironwood", "coal_in_bloom", "clay_aiken", "gravel_travel");
        add("oh_the_irony", "ironwood");
        add("red_tree_redemption", "ironwood");
        add("trees_to_diamonds", "red_tree_redemption");
        add("diamond_clarity", "trees_to_diamonds");
        add("tiny_tree_big_plans", "clay_aiken");
        add("drop_it_like_its_hopping", "tiny_tree_big_plans", "oh_the_irony");
        add("orchard_on_autopilot", "drop_it_like_its_hopping");

        add("paperwork_begins", "oh_the_irony");
        add("bulk_by_barrel", "starting_from_the_bottom");
        add("network_attached_chest", "diamond_clarity");
        add("remote_possibilities", "network_attached_chest");
        add("market_forces", "clay_aiken");
        add("seeds_of_life", "market_forces");
        add("barnyard_beginnings", "seeds_of_life");
        add("hog_tied", "water_you_waiting_for", "dirty_dancing");
        add("truffle_shuffle", "hog_tied", "seeds_of_life");
        add("snad_together", "beachfront_property");
        add("cane_and_able", "snad_together");
        add("tooling_up", "starting_from_the_bottom");
        add("parts_department", "starting_from_the_bottom");
        add("melting_point", "gravel_travel", "beachfront_property", "clay_aiken");
        add("smeltery_authority", "gravel_travel", "beachfront_property", "clay_bucket_gamble");
        add("cast_away", "oh_the_irony");
        add("forge_ahead", "tooling_up", "oh_the_irony");
        add("modifier_motive", "tooling_up", "parts_department");
        add("level_headed_tool", "tooling_up", "parts_department");
        add("armor_by_committee", "oh_the_irony");

        add("first_spark", "oh_the_irony", "red_tree_redemption");
        add("battery_included", "redstone_in_a_box");
        add("redstone_in_a_box", "smeltery_authority", "diamond_clarity");
        add("steel_yourself", "redstone_in_a_box", "first_spark");
        add("enriched_expectations", "steel_yourself");
        add("crush_depth", "steel_yourself");
        add("triple_threat", "enriched_expectations", "crush_depth", "hydrogen_economy");
        add("five_times_the_charm", "triple_threat");
        add("hydrogen_economy", "redstone_in_a_box", "first_spark");
        add("gas_grass_or_rf", "hydrogen_economy", "crush_depth");
        add("hdpeasy", "gas_grass_or_rf");
        add("ultimate_capacity", "crush_depth", "battery_included");
        add("factory_settings", "steel_yourself");
        add("digital_prospector", "crush_depth", "first_spark");
        add("latex_intentions", "oh_the_irony", "red_tree_redemption");
        add("plastic_industry", "latex_intentions", "first_spark");
        add("sow_automatic", "plastic_industry");
        add("reap_automatic", "plastic_industry");
        add("mob_rules", "plastic_industry");
        add("black_hole_inventory", "plastic_industry");
        add("learn_deeply", "coal_in_bloom", "red_tree_redemption", "clay_bucket_gamble",
            "water_you_waiting_for");
        add("model_citizen", "red_tree_redemption", "water_you_waiting_for");
        add("data_with_experience", "model_citizen");
        add("simulation_theory", "data_with_experience", "first_spark");
        add("reality_armor", "data_with_experience", "diamond_clarity", "water_you_waiting_for");

        add("cake_to_hell", "diamond_clarity");
        add("nether_say_never", "cake_to_hell");
        add("blaze_of_glory", "nether_say_never");
        add("wither_or_not", "blaze_of_glory");
        add("the_hunting_trip", "barnyard_beginnings");
        add("lost_and_found", "blaze_of_glory", "diamond_clarity");
        add("cake_at_the_end", "blaze_of_glory");
        add("the_void_blinks_back", "cake_at_the_end");
        add("dragon_eviction_notice", "the_void_blinks_back");
        add("borrowed_wings", "dragon_eviction_notice");
        add("into_the_twilight");
        add("naga_have_i_ever", "into_the_twilight");
        add("lich_please", "naga_have_i_ever");
        add("hydra_expectations", "lich_please");
        add("ice_queen_cometh", "lich_please");

        add("menril_state_of_mind", "water_you_waiting_for");
        add("logic_in_the_void", "menril_state_of_mind");
        add("items_in_transit", "logic_in_the_void");
        add("fluix_of_the_matter", "menril_state_of_mind", "redstone_in_a_box");
        add("pressing_engagement", "cast_away", "alloyed_allegiance", "lost_and_found");
        add("acceptable_energy", "fluix_of_the_matter");
        add("me_myself_and_i", "acceptable_energy", "pressing_engagement");
        add("sixty_four_k_and_counting", "me_myself_and_i");
        add("autocraft_authority", "sixty_four_k_and_counting");
        add("manufactory_warranty_void", "steel_yourself", "first_spark");
        add("alloyed_allegiance", "manufactory_warranty_void");
        add("positive_fission", "alloyed_allegiance");
        add("gone_fission", "positive_fission", "lost_and_found");
        add("deuterium_duty", "alloyed_allegiance");
        add("tritium_triumph", "alloyed_allegiance");
        add("fusion_cuisine", "deuterium_duty", "tritium_triumph");
        add("pocket_star", "fusion_cuisine");
        add("matter_of_analysis", "lost_and_found", "first_spark");
        add("decompose_yourself", "lost_and_found", "first_spark");
        add("replication_nation", "matter_of_analysis", "decompose_yourself");

        add("matrix_reloaded", "steel_yourself");
        add("turbine_service", "steel_yourself");
        add("crafting_core_values", "oh_the_irony", "red_tree_redemption");
        add("quantum_compression", "oh_the_irony", "red_tree_redemption");
        add("metals_into_points", "quantum_compression");
        add("cookie_bacon_donut_collapse", "quantum_compression");
        add("ultimate_singularity", "metals_into_points", "cookie_bacon_donut_collapse");
        add("black_hole_filled", "black_hole_inventory");
        add("million_item_paperwork", "paperwork_begins");
        add("the_sky_finally_claps", "ice_queen_cometh", "ultimate_singularity",
            "black_hole_filled", "million_item_paperwork", "turbine_service", "pocket_star",
            "replication_nation", "borrowed_wings", "reality_armor");
    }

    private CoreAdvancementCatalog() {
    }

    private static void add(String path, String... prerequisites) {
        List<String> ids = new ArrayList<>();
        for (String prerequisite : prerequisites) {
            ids.add(id(prerequisite));
        }
        PREREQUISITES.put(id(path), Collections.unmodifiableList(ids));
    }

    private static String id(String path) {
        return "sf4angel:core/" + path;
    }

    static Map<String, List<String>> prerequisites() {
        return Collections.unmodifiableMap(PREREQUISITES);
    }
}
