package com.godh00d.sf4angel.constellation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Generated from ACHIEVEMENT_PLAN.md by instance-config/generate_triumph.ps1. */
public final class AchievementConstellationCatalog {

    public static final int COUNT = 129;
    public static final String HASH = "76b0460ce748211f223f4ac6637e0c845cf8615993c46dea38eb80470eca9c47";
    private static final Node[] NODES = new Node[] {
        new Node("sf4angel:core/starting_from_the_bottom", "Starting from the Bottom", strings(), strings(),
            ints(1, 3, 21, 31, 32, 111, 118), 8, 87, 0),
        new Node("sf4angel:core/that_is_dir_tree", "That's Dir-tree", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(2, 4), 12, 87, 0),
        new Node("sf4angel:core/dirty_dancing", "Dirty Dancing", strings("sf4angel:core/that_is_dir_tree"), strings(),
            ints(27), 16, 87, 0),
        new Node("sf4angel:core/captain_hook", "Captain Hook", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 12, 90, 0),
        new Node("sf4angel:core/stone_tree_oath", "Stone Tree Oath", strings("sf4angel:core/that_is_dir_tree"), strings(),
            ints(5, 6, 7, 8), 16, 90, 0),
        new Node("sf4angel:core/stone_from_trees", "Stone from Trees", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(), 20, 87, 0),
        new Node("sf4angel:core/gravel_travel", "Gravel Travel", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(12, 33, 34), 20, 90, 0),
        new Node("sf4angel:core/beachfront_property", "Beachfront Property", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(11, 29, 33, 34), 20, 93, 0),
        new Node("sf4angel:core/clay_aiken", "Clay Aiken", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(10, 12, 17, 24, 33), 20, 96, 0),
        new Node("sf4angel:core/water_you_waiting_for", "Water You Waiting For?", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(27, 60, 61, 64, 80), 36, 87, 0),
        new Node("sf4angel:core/clay_bucket_gamble", "Clay Bucket Gamble", strings("sf4angel:core/clay_aiken"), strings(),
            ints(34, 60), 24, 87, 0),
        new Node("sf4angel:core/coal_in_bloom", "Coal in Bloom", strings("sf4angel:core/beachfront_property"), strings(),
            ints(12, 60), 24, 90, 0),
        new Node("sf4angel:core/ironwood", "Ironwood", strings("sf4angel:core/coal_in_bloom", "sf4angel:core/clay_aiken", "sf4angel:core/gravel_travel"), strings(),
            ints(13, 14), 28, 87, 0),
        new Node("sf4angel:core/oh_the_irony", "Oh, the Irony", strings("sf4angel:core/ironwood"), strings(),
            ints(9, 18, 20, 35, 36, 39, 40, 54, 102, 103, 117), 32, 87, 0),
        new Node("sf4angel:core/red_tree_redemption", "Red Tree Redemption", strings("sf4angel:core/ironwood"), strings(),
            ints(15, 40, 54, 60, 61, 102, 103), 32, 90, 0),
        new Node("sf4angel:core/trees_to_diamonds", "Trees to Diamonds", strings("sf4angel:core/red_tree_redemption"), strings(),
            ints(16), 36, 90, 0),
        new Node("sf4angel:core/diamond_clarity", "Diamond Clarity", strings("sf4angel:core/trees_to_diamonds"), strings(),
            ints(22, 42, 64, 65, 70, 117), 40, 87, 0),
        new Node("sf4angel:core/tiny_tree_big_plans", "Tiny Tree, Big Plans", strings("sf4angel:core/clay_aiken"), strings(),
            ints(18), 24, 93, 0),
        new Node("sf4angel:core/drop_it_like_its_hopping", "Drop It Like It's Hopping", strings("sf4angel:core/tiny_tree_big_plans", "sf4angel:core/oh_the_irony"), strings(),
            ints(19), 36, 93, 0),
        new Node("sf4angel:core/orchard_on_autopilot", "Orchard on Autopilot", strings("sf4angel:core/drop_it_like_its_hopping"), strings(),
            ints(), 40, 90, 0),
        new Node("sf4angel:core/paperwork_begins", "Paperwork Begins", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(108), 36, 96, 0),
        new Node("sf4angel:core/bulk_by_barrel", "Bulk by Barrel", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 12, 93, 0),
        new Node("sf4angel:core/network_attached_chest", "Network Attached Chest", strings("sf4angel:core/diamond_clarity"), strings(),
            ints(23), 44, 87, 0),
        new Node("sf4angel:core/remote_possibilities", "Remote Possibilities", strings("sf4angel:core/network_attached_chest"), strings(),
            ints(), 48, 87, 0),
        new Node("sf4angel:core/market_forces", "Market Forces", strings("sf4angel:core/clay_aiken"), strings(),
            ints(25), 24, 96, 0),
        new Node("sf4angel:core/seeds_of_life", "Seeds of Life", strings("sf4angel:core/market_forces"), strings(),
            ints(26, 28, 112), 28, 90, 0),
        new Node("sf4angel:core/barnyard_beginnings", "Barnyard Beginnings", strings("sf4angel:core/seeds_of_life"), strings(),
            ints(69), 32, 93, 0),
        new Node("sf4angel:core/hog_tied", "Hog Tied", strings("sf4angel:core/water_you_waiting_for", "sf4angel:core/dirty_dancing"), strings(),
            ints(28), 40, 93, 0),
        new Node("sf4angel:core/truffle_shuffle", "Truffle Shuffle", strings("sf4angel:core/hog_tied", "sf4angel:core/seeds_of_life"), strings(),
            ints(), 44, 90, 0),
        new Node("sf4angel:core/snad_together", "Snad Together", strings("sf4angel:core/beachfront_property"), strings(),
            ints(30), 24, 99, 0),
        new Node("sf4angel:core/cane_and_able", "Cane and Able", strings("sf4angel:core/snad_together"), strings(),
            ints(), 28, 93, 0),
        new Node("sf4angel:core/tooling_up", "Tooling Up", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(36, 37, 38, 119), 12, 96, 0),
        new Node("sf4angel:core/parts_department", "Parts Department", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(37, 38), 12, 99, 0),
        new Node("sf4angel:core/melting_point", "Melting Point", strings("sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_aiken"), strings(),
            ints(), 24, 102, 0),
        new Node("sf4angel:core/smeltery_authority", "Smeltery Authority", strings("sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_bucket_gamble"), strings(),
            ints(42), 28, 96, 0),
        new Node("sf4angel:core/cast_away", "Cast Away", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(84), 36, 99, 0),
        new Node("sf4angel:core/forge_ahead", "Forge Ahead", strings("sf4angel:core/tooling_up", "sf4angel:core/oh_the_irony"), strings(),
            ints(), 36, 102, 0),
        new Node("sf4angel:core/modifier_motive", "Modifier Motive", strings("sf4angel:core/tooling_up", "sf4angel:core/parts_department"), strings(),
            ints(116), 16, 93, 0),
        new Node("sf4angel:core/level_headed_tool", "Level-Headed Tool", strings("sf4angel:core/tooling_up", "sf4angel:core/parts_department"), strings(),
            ints(), 16, 96, 0),
        new Node("sf4angel:core/armor_by_committee", "Armor by Committee", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(), 36, 105, 0),
        new Node("sf4angel:core/first_spark", "First Spark", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(43, 48, 53, 55, 63, 89, 97, 98), 36, 87, 5),
        new Node("sf4angel:core/battery_included", "Battery Included", strings("sf4angel:core/redstone_in_a_box"), strings(),
            ints(51), 48, 90, 0),
        new Node("sf4angel:core/redstone_in_a_box", "Redstone in a Box", strings("sf4angel:core/smeltery_authority", "sf4angel:core/diamond_clarity"), strings(),
            ints(41, 43, 48, 83), 44, 93, 0),
        new Node("sf4angel:core/steel_yourself", "Steel Yourself", strings("sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"), strings(),
            ints(44, 45, 52, 89, 100, 101), 48, 93, 0),
        new Node("sf4angel:core/enriched_expectations", "Enriched Expectations", strings("sf4angel:core/steel_yourself"), strings(),
            ints(46), 52, 87, 0),
        new Node("sf4angel:core/crush_depth", "Crush Depth", strings("sf4angel:core/steel_yourself"), strings(),
            ints(46, 49, 51, 53), 52, 90, 0),
        new Node("sf4angel:core/triple_threat", "Triple Threat", strings("sf4angel:core/enriched_expectations", "sf4angel:core/crush_depth", "sf4angel:core/hydrogen_economy"), strings(),
            ints(47), 56, 87, 0),
        new Node("sf4angel:core/five_times_the_charm", "Five Times the Charm", strings("sf4angel:core/triple_threat"), strings(),
            ints(), 60, 87, 0),
        new Node("sf4angel:core/hydrogen_economy", "Hydrogen Economy", strings("sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"), strings(),
            ints(46, 49), 48, 96, 0),
        new Node("sf4angel:core/gas_grass_or_rf", "Gas, Grass, or RF", strings("sf4angel:core/hydrogen_economy", "sf4angel:core/crush_depth"), strings(),
            ints(50), 56, 90, 0),
        new Node("sf4angel:core/hdpeasy", "HDPEasy", strings("sf4angel:core/gas_grass_or_rf"), strings(),
            ints(), 60, 90, 0),
        new Node("sf4angel:core/ultimate_capacity", "Ultimate Capacity", strings("sf4angel:core/crush_depth", "sf4angel:core/battery_included"), strings(),
            ints(), 56, 93, 0),
        new Node("sf4angel:core/factory_settings", "Factory Settings", strings("sf4angel:core/steel_yourself"), strings(),
            ints(), 52, 93, 0),
        new Node("sf4angel:core/digital_prospector", "Digital Prospector", strings("sf4angel:core/crush_depth", "sf4angel:core/first_spark"), strings(),
            ints(), 56, 96, 0),
        new Node("sf4angel:core/latex_intentions", "Latex Intentions", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(55), 36, 90, 5),
        new Node("sf4angel:core/plastic_industry", "Plastic Industry", strings("sf4angel:core/latex_intentions", "sf4angel:core/first_spark"), strings(),
            ints(56, 57, 58, 59), 40, 96, 0),
        new Node("sf4angel:core/sow_automatic", "Sow Automatic", strings("sf4angel:core/plastic_industry"), strings(),
            ints(), 44, 96, 0),
        new Node("sf4angel:core/reap_automatic", "Reap Automatic", strings("sf4angel:core/plastic_industry"), strings(),
            ints(), 44, 99, 0),
        new Node("sf4angel:core/mob_rules", "Mob Rules", strings("sf4angel:core/plastic_industry"), strings(),
            ints(114), 44, 102, 0),
        new Node("sf4angel:core/black_hole_inventory", "Black Hole Inventory", strings("sf4angel:core/plastic_industry"), strings(),
            ints(107), 44, 105, 0),
        new Node("sf4angel:core/learn_deeply", "Learn Deeply", strings("sf4angel:core/coal_in_bloom", "sf4angel:core/red_tree_redemption", "sf4angel:core/clay_bucket_gamble", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(), 40, 99, 0),
        new Node("sf4angel:core/model_citizen", "Model Citizen", strings("sf4angel:core/red_tree_redemption", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(62), 40, 102, 0),
        new Node("sf4angel:core/data_with_experience", "Data with Experience", strings("sf4angel:core/model_citizen"), strings(),
            ints(63, 64), 44, 87, 5),
        new Node("sf4angel:core/simulation_theory", "Simulation Theory", strings("sf4angel:core/data_with_experience", "sf4angel:core/first_spark"), strings(),
            ints(), 48, 99, 0),
        new Node("sf4angel:core/reality_armor", "Reality Armor", strings("sf4angel:core/data_with_experience", "sf4angel:core/diamond_clarity", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(109), 48, 102, 0),
        new Node("sf4angel:core/cake_to_hell", "Cake to Hell", strings("sf4angel:core/diamond_clarity"), strings(),
            ints(66), 44, 90, 5),
        new Node("sf4angel:core/nether_say_never", "Nether Say Never", strings("sf4angel:core/cake_to_hell"), strings(),
            ints(67), 48, 105, 0),
        new Node("sf4angel:core/blaze_of_glory", "Blaze of Glory", strings("sf4angel:core/nether_say_never"), strings(),
            ints(68, 70, 71), 52, 96, 0),
        new Node("sf4angel:core/wither_or_not", "Wither or Not", strings("sf4angel:core/blaze_of_glory"), strings(),
            ints(), 56, 99, 0),
        new Node("sf4angel:core/the_hunting_trip", "The Hunting Trip", strings("sf4angel:core/barnyard_beginnings"), strings(),
            ints(), 36, 93, 5),
        new Node("sf4angel:core/lost_and_found", "Lost and Found", strings("sf4angel:core/blaze_of_glory", "sf4angel:core/diamond_clarity"), strings(),
            ints(84, 92, 97, 98), 56, 102, 0),
        new Node("sf4angel:core/cake_at_the_end", "Cake at the End", strings("sf4angel:core/blaze_of_glory"), strings(),
            ints(72), 56, 105, 0),
        new Node("sf4angel:core/the_void_blinks_back", "The Void Blinks Back", strings("sf4angel:core/cake_at_the_end"), strings(),
            ints(73), 60, 93, 0),
        new Node("sf4angel:core/dragon_eviction_notice", "Dragon Eviction Notice", strings("sf4angel:core/the_void_blinks_back"), strings(),
            ints(74), 64, 87, 0),
        new Node("sf4angel:core/borrowed_wings", "Borrowed Wings", strings("sf4angel:core/dragon_eviction_notice"), strings(),
            ints(109), 68, 87, 0),
        new Node("sf4angel:core/into_the_twilight", "Into the Twilight", strings(), strings("twilight_forest"),
            ints(76), 8, 90, 0),
        new Node("sf4angel:core/naga_have_i_ever", "Naga Have I Ever", strings("sf4angel:core/into_the_twilight"), strings(),
            ints(77), 12, 102, 0),
        new Node("sf4angel:core/lich_please", "Lich, Please", strings("sf4angel:core/naga_have_i_ever"), strings(),
            ints(78, 79), 16, 99, 0),
        new Node("sf4angel:core/hydra_expectations", "Hydra Expectations", strings("sf4angel:core/lich_please"), strings(),
            ints(), 20, 99, 0),
        new Node("sf4angel:core/ice_queen_cometh", "Ice Queen Cometh", strings("sf4angel:core/lich_please"), strings(),
            ints(109), 20, 102, 0),
        new Node("sf4angel:core/menril_state_of_mind", "Menril State of Mind", strings("sf4angel:core/water_you_waiting_for"), strings(),
            ints(81, 83), 40, 105, 0),
        new Node("sf4angel:core/logic_in_the_void", "Logic in the Void", strings("sf4angel:core/menril_state_of_mind"), strings(),
            ints(82), 44, 93, 5),
        new Node("sf4angel:core/items_in_transit", "Items in Transit", strings("sf4angel:core/logic_in_the_void"), strings(),
            ints(), 48, 87, 5),
        new Node("sf4angel:core/fluix_of_the_matter", "Fluix of the Matter", strings("sf4angel:core/menril_state_of_mind", "sf4angel:core/redstone_in_a_box"), strings(),
            ints(85), 48, 90, 5),
        new Node("sf4angel:core/pressing_engagement", "Pressing Engagement", strings("sf4angel:core/cast_away", "sf4angel:core/alloyed_allegiance", "sf4angel:core/lost_and_found"), strings(),
            ints(86), 60, 96, 0),
        new Node("sf4angel:core/acceptable_energy", "Acceptable Energy", strings("sf4angel:core/fluix_of_the_matter"), strings(),
            ints(86), 52, 99, 0),
        new Node("sf4angel:core/me_myself_and_i", "ME, Myself, and I", strings("sf4angel:core/acceptable_energy", "sf4angel:core/pressing_engagement"), strings(),
            ints(87), 64, 90, 0),
        new Node("sf4angel:core/sixty_four_k_and_counting", "Sixty-Four K and Counting", strings("sf4angel:core/me_myself_and_i"), strings(),
            ints(88), 68, 90, 0),
        new Node("sf4angel:core/autocraft_authority", "Autocraft Authority", strings("sf4angel:core/sixty_four_k_and_counting"), strings(),
            ints(), 72, 87, 0),
        new Node("sf4angel:core/manufactory_warranty_void", "Manufactory Warranty Void", strings("sf4angel:core/steel_yourself", "sf4angel:core/first_spark"), strings(),
            ints(90), 52, 102, 0),
        new Node("sf4angel:core/alloyed_allegiance", "Alloyed Allegiance", strings("sf4angel:core/manufactory_warranty_void"), strings(),
            ints(84, 91, 93, 94), 56, 87, 5),
        new Node("sf4angel:core/positive_fission", "Positive Fission", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(92), 60, 99, 0),
        new Node("sf4angel:core/gone_fission", "Gone Fission", strings("sf4angel:core/positive_fission", "sf4angel:core/lost_and_found"), strings(),
            ints(), 64, 93, 0),
        new Node("sf4angel:core/deuterium_duty", "Deuterium Duty", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(95), 60, 102, 0),
        new Node("sf4angel:core/tritium_triumph", "Tritium Triumph", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(95), 60, 105, 0),
        new Node("sf4angel:core/fusion_cuisine", "Fusion Cuisine", strings("sf4angel:core/deuterium_duty", "sf4angel:core/tritium_triumph"), strings(),
            ints(96), 64, 96, 0),
        new Node("sf4angel:core/pocket_star", "Pocket Star", strings("sf4angel:core/fusion_cuisine"), strings(),
            ints(109), 68, 93, 0),
        new Node("sf4angel:core/matter_of_analysis", "Matter of Analysis", strings("sf4angel:core/lost_and_found", "sf4angel:core/first_spark"), strings(),
            ints(99), 60, 87, 5),
        new Node("sf4angel:core/decompose_yourself", "Decompose Yourself", strings("sf4angel:core/lost_and_found", "sf4angel:core/first_spark"), strings(),
            ints(99), 60, 90, 5),
        new Node("sf4angel:core/replication_nation", "Replication Nation", strings("sf4angel:core/matter_of_analysis", "sf4angel:core/decompose_yourself"), strings(),
            ints(109), 64, 99, 0),
        new Node("sf4angel:core/matrix_reloaded", "Matrix Reloaded", strings("sf4angel:core/steel_yourself"), strings(),
            ints(), 52, 105, 0),
        new Node("sf4angel:core/turbine_service", "Turbine Service", strings("sf4angel:core/steel_yourself"), strings(),
            ints(109), 52, 87, 5),
        new Node("sf4angel:core/crafting_core_values", "Crafting Core Values", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(), 36, 96, 5),
        new Node("sf4angel:core/quantum_compression", "Quantum Compression", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(104, 105), 36, 99, 5),
        new Node("sf4angel:core/metals_into_points", "Metals into Points", strings("sf4angel:core/quantum_compression"), strings(),
            ints(106), 40, 87, 5),
        new Node("sf4angel:core/cookie_bacon_donut_collapse", "Cookie, Bacon, Donut, Collapse", strings("sf4angel:core/quantum_compression"), strings(),
            ints(106), 40, 90, 5),
        new Node("sf4angel:core/ultimate_singularity", "Ultimate Singularity", strings("sf4angel:core/metals_into_points", "sf4angel:core/cookie_bacon_donut_collapse"), strings(),
            ints(109), 44, 96, 5),
        new Node("sf4angel:core/black_hole_filled", "Black Hole Filled", strings("sf4angel:core/black_hole_inventory"), strings(),
            ints(109), 48, 93, 5),
        new Node("sf4angel:core/million_item_paperwork", "Million-Item Paperwork", strings("sf4angel:core/paperwork_begins"), strings(),
            ints(109), 40, 93, 5),
        new Node("sf4angel:core/the_sky_finally_claps", "The Sky Finally Claps", strings("sf4angel:core/ice_queen_cometh", "sf4angel:core/ultimate_singularity", "sf4angel:core/black_hole_filled", "sf4angel:core/million_item_paperwork", "sf4angel:core/turbine_service", "sf4angel:core/pocket_star", "sf4angel:core/replication_nation", "sf4angel:core/borrowed_wings", "sf4angel:core/reality_armor"), strings(),
            ints(), 72, 90, 0),
        new Node("sf4angel:optional/sticky_keys", "Sticky Keys", strings(), strings(),
            ints(), 8, 87, -24),
        new Node("sf4angel:optional/nap_time", "Nap Time", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 12, 87, -24),
        new Node("sf4angel:optional/milk_without_the_moo", "Milk Without the Moo", strings("sf4angel:core/seeds_of_life"), strings(),
            ints(), 32, 87, -24),
        new Node("sf4angel:optional/a_balanced_sky_diet", "A Balanced Sky Diet", strings(), strings(),
            ints(), 8, 90, -24),
        new Node("sf4angel:optional/mob_factory_floor", "Mob Factory Floor", strings("sf4angel:core/mob_rules"), strings(),
            ints(), 48, 87, -24),
        new Node("sf4angel:optional/armored_to_the_teeth", "Armored to the Teeth", strings(), strings(),
            ints(), 8, 93, -24),
        new Node("sf4angel:optional/unbreakable_resolve", "Unbreakable Resolve", strings("sf4angel:core/modifier_motive"), strings(),
            ints(), 20, 87, -24),
        new Node("sf4angel:optional/undo_the_apocalypse", "Undo the Apocalypse", strings("sf4angel:core/oh_the_irony", "sf4angel:core/diamond_clarity"), strings(),
            ints(), 44, 87, -24),
        new Node("sf4angel:optional/pixel_perfect_masonry", "Pixel Perfect Masonry", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 12, 90, -24),
        new Node("sf4angel:optional/around_the_void_in_eighty_throws", "Around the Void in Eighty Throws", strings("sf4angel:core/tooling_up"), strings(),
            ints(), 16, 87, -24),
        new Node("sf4angel:optional/android_dreams", "Android Dreams of Electric Sheep", strings(), strings("android"),
            ints(), 8, 96, -24),
        new Node("sf4angel:optional/robot_did_it", "Robot Did It", strings(), strings("open_computers"),
            ints(), 8, 99, -24),
        new Node("sf4angel:optional/maximum_minimum_space", "Maximum Minimum Space", strings(), strings(),
            ints(), 8, 102, -24),
        new Node("sf4angel:prestige/prestige_worldwide", "Prestige Worldwide", strings(), strings("parabox"),
            ints(127), 8, 87, 24),
        new Node("sf4angel:prestige/equivalent_ambition_unlocked", "Equivalent Ambition Unlocked", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "project_e"),
            ints(), 16, 87, 24),
        new Node("sf4angel:prestige/aperture_unlocked", "Aperture Unlocked", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "portal_gun"),
            ints(), 16, 90, 24),
        new Node("sf4angel:prestige/written_in_another_age", "Written in Another Age", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "mystcraft"),
            ints(), 16, 93, 24),
        new Node("sf4angel:prestige/time_is_a_flat_parabox", "Time Is a Flat Parabox", strings("sf4angel:prestige/prestige_worldwide"), strings("parabox"),
            ints(124, 125, 126, 128), 12, 87, 24),
        new Node("sf4angel:prestige/empowered_recursion", "Empowered Recursion", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "parabox_two"),
            ints(), 16, 96, 24)
    };
    private static final Map<String, Integer> INDEXES;

    static {
        if (NODES.length != COUNT) throw new IllegalStateException("Catalog count mismatch");
        Map<String, Integer> indexes = new LinkedHashMap<>();
        for (int i = 0; i < NODES.length; i++) {
            if (indexes.put(NODES[i].id, i) != null) throw new IllegalStateException("Duplicate catalog ID");
        }
        for (int i = 0; i < NODES.length; i++) {
            for (int child : NODES[i].children) {
                if (child < 0 || child >= NODES.length || !NODES[child].parents.contains(NODES[i].id)) {
                    throw new IllegalStateException("Invalid catalog child edge for " + NODES[i].id);
                }
            }
            for (String parent : NODES[i].parents) {
                Integer parentIndex = indexes.get(parent);
                if (parentIndex == null || !contains(NODES[parentIndex].children, i)) {
                    throw new IllegalStateException("Invalid catalog parent edge for " + NODES[i].id);
                }
            }
        }
        INDEXES = Collections.unmodifiableMap(indexes);
    }

    private AchievementConstellationCatalog() {
    }

    public static Node[] nodes() {
        return NODES.clone();
    }

    public static Map<String, Integer> indexes() {
        return INDEXES;
    }

    private static List<String> strings(String... values) {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values);
        return Collections.unmodifiableList(result);
    }

    private static int[] ints(int... values) {
        return values;
    }

    private static boolean contains(int[] values, int expected) {
        for (int value : values) if (value == expected) return true;
        return false;
    }

    public static final class Node {
        public final String id;
        public final String title;
        public final List<String> parents;
        public final List<String> stages;
        public final int x;
        public final int y;
        public final int z;
        private final int[] children;

        private Node(String id, String title, List<String> parents, List<String> stages,
                     int[] children, int x, int y, int z) {
            this.id = id;
            this.title = title;
            this.parents = parents;
            this.stages = stages;
            this.children = children;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int[] children() {
            return children.clone();
        }
    }
}
