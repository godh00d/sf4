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
        add("captain_hook", "that_is_dir_tree");
        add("stone_tree_oath", "captain_hook");
        add("stone_from_trees", "stone_tree_oath");
        add("gravel_travel", "stone_tree_oath");
        add("beachfront_property", "gravel_travel");
        add("clay_aiken", "dirty_dancing", "beachfront_property");
        add("water_you_waiting_for", "clay_aiken");
        add("clay_bucket_gamble", "clay_aiken");
        add("coal_in_bloom", "beachfront_property");
        add("ironwood", "coal_in_bloom", "clay_bucket_gamble");
        add("oh_the_irony", "ironwood");
        add("red_tree_redemption", "ironwood");
        add("trees_to_diamonds", "red_tree_redemption", "ironwood");
        add("diamond_clarity", "trees_to_diamonds");
        add("tiny_tree_big_plans", "oh_the_irony", "diamond_clarity");
        add("drop_it_like_its_hopping", "tiny_tree_big_plans");
        add("orchard_on_autopilot", "drop_it_like_its_hopping");

        add("paperwork_begins", "orchard_on_autopilot");
        add("bulk_by_barrel", "orchard_on_autopilot");
        add("network_attached_chest", "paperwork_begins", "bulk_by_barrel");
        add("remote_possibilities", "network_attached_chest");
        add("market_forces", "oh_the_irony");
        add("seeds_of_life", "market_forces");
        add("barnyard_beginnings", "seeds_of_life");
        add("hog_tied", "seeds_of_life");
        add("truffle_shuffle", "hog_tied");
        add("snad_together", "beachfront_property", "oh_the_irony");
        add("cane_and_able", "snad_together");
        add("tooling_up", "stone_from_trees");
        add("parts_department", "tooling_up");
        add("melting_point", "oh_the_irony", "clay_bucket_gamble");
        add("smeltery_authority", "melting_point");
        add("cast_away", "smeltery_authority");
        add("forge_ahead", "parts_department", "cast_away");
        add("modifier_motive", "forge_ahead");
        add("level_headed_tool", "modifier_motive");
        add("armor_by_committee", "smeltery_authority", "forge_ahead");

        add("first_spark", "oh_the_irony", "red_tree_redemption");
        add("battery_included", "first_spark");
        add("redstone_in_a_box", "first_spark");
        add("steel_yourself", "redstone_in_a_box");
        add("enriched_expectations", "redstone_in_a_box", "steel_yourself");
        add("crush_depth", "enriched_expectations");
        add("triple_threat", "enriched_expectations", "crush_depth");
        add("five_times_the_charm", "triple_threat");
        add("hydrogen_economy", "enriched_expectations");
        add("gas_grass_or_rf", "hydrogen_economy", "crush_depth");
        add("hdpeasy", "gas_grass_or_rf");
        add("ultimate_capacity", "steel_yourself", "battery_included");
        add("factory_settings", "triple_threat");
        add("digital_prospector", "factory_settings", "ultimate_capacity");
        add("latex_intentions", "orchard_on_autopilot", "first_spark");
        add("plastic_industry", "latex_intentions");
        add("sow_automatic", "plastic_industry");
        add("reap_automatic", "sow_automatic");
        add("mob_rules", "plastic_industry");
        add("black_hole_inventory", "mob_rules", "reap_automatic");
        add("learn_deeply", "first_spark");
        add("model_citizen", "learn_deeply");
        add("data_with_experience", "model_citizen");
        add("simulation_theory", "data_with_experience", "first_spark");
        add("reality_armor", "simulation_theory");

        add("cake_to_hell", "diamond_clarity", "steel_yourself");
        add("nether_say_never", "cake_to_hell");
        add("blaze_of_glory", "nether_say_never");
        add("wither_or_not", "blaze_of_glory", "reality_armor");
        add("the_hunting_trip", "mob_rules");
        add("lost_and_found", "digital_prospector");
        add("cake_at_the_end", "blaze_of_glory");
        add("the_void_blinks_back", "cake_at_the_end");
        add("dragon_eviction_notice", "the_void_blinks_back", "reality_armor");
        add("borrowed_wings", "dragon_eviction_notice");
        add("into_the_twilight", "diamond_clarity", "reality_armor");
        add("naga_have_i_ever", "into_the_twilight");
        add("lich_please", "naga_have_i_ever");
        add("hydra_expectations", "lich_please");
        add("ice_queen_cometh", "hydra_expectations");

        add("menril_state_of_mind", "diamond_clarity");
        add("logic_in_the_void", "menril_state_of_mind", "first_spark");
        add("items_in_transit", "logic_in_the_void");
        add("fluix_of_the_matter", "menril_state_of_mind", "nether_say_never");
        add("pressing_engagement", "fluix_of_the_matter", "lost_and_found");
        add("acceptable_energy", "pressing_engagement", "ultimate_capacity");
        add("me_myself_and_i", "acceptable_energy", "items_in_transit");
        add("sixty_four_k_and_counting", "me_myself_and_i");
        add("autocraft_authority", "sixty_four_k_and_counting");
        add("manufactory_warranty_void", "steel_yourself", "first_spark");
        add("alloyed_allegiance", "manufactory_warranty_void");
        add("positive_fission", "alloyed_allegiance", "lost_and_found");
        add("gone_fission", "positive_fission");
        add("deuterium_duty", "gone_fission", "hydrogen_economy");
        add("tritium_triumph", "deuterium_duty");
        add("fusion_cuisine", "tritium_triumph");
        add("pocket_star", "fusion_cuisine");
        add("matter_of_analysis", "lost_and_found", "first_spark");
        add("decompose_yourself", "matter_of_analysis");
        add("replication_nation", "decompose_yourself", "ultimate_capacity");

        add("matrix_reloaded", "ultimate_capacity", "autocraft_authority");
        add("turbine_service", "matrix_reloaded", "pocket_star");
        add("crafting_core_values", "autocraft_authority", "replication_nation");
        add("quantum_compression", "crafting_core_values", "turbine_service");
        add("metals_into_points", "quantum_compression");
        add("cookie_bacon_donut_collapse", "metals_into_points");
        add("ultimate_singularity", "cookie_bacon_donut_collapse");
        add("black_hole_filled", "black_hole_inventory", "autocraft_authority");
        add("million_item_paperwork", "paperwork_begins", "autocraft_authority");
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
