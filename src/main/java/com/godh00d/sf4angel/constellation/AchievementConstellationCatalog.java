package com.godh00d.sf4angel.constellation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Generated from ACHIEVEMENT_PLAN.md by instance-config/generate_triumph.ps1. */
public final class AchievementConstellationCatalog {

    public static final int COUNT = 129;
    public static final String HASH = "ec3096c6936d3ee26f601bd40c8d4ad5f745b98f72a0d548c17f9a132a890c20";
    private static final Node[] NODES = new Node[] {
        new Node("sf4angel:core/starting_from_the_bottom", "Starting from the Bottom", strings(), strings(),
            ints(1, 3, 21, 31, 32, 111, 118), 40, 56, 0),
        new Node("sf4angel:core/that_is_dir_tree", "That's Dir-tree", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(2, 4), 75, 72, -6),
        new Node("sf4angel:core/dirty_dancing", "Dirty Dancing", strings("sf4angel:core/that_is_dir_tree"), strings(),
            ints(27), 81, 88, -29),
        new Node("sf4angel:core/captain_hook", "Captain Hook", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 55, 72, 18),
        new Node("sf4angel:core/stone_tree_oath", "Stone Tree Oath", strings("sf4angel:core/that_is_dir_tree"), strings(),
            ints(5, 6, 7, 8), 111, 88, -11),
        new Node("sf4angel:core/stone_from_trees", "Stone from Trees", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(), 118, 104, -33),
        new Node("sf4angel:core/gravel_travel", "Gravel Travel", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(12, 33, 34), 120, 104, -47),
        new Node("sf4angel:core/beachfront_property", "Beachfront Property", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(11, 29, 33, 34), 146, 104, -18),
        new Node("sf4angel:core/clay_aiken", "Clay Aiken", strings("sf4angel:core/stone_tree_oath"), strings(),
            ints(10, 12, 17, 24, 33), 127, 104, 10),
        new Node("sf4angel:core/water_you_waiting_for", "Water You Waiting For?", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(27, 60, 61, 64, 80), 215, 168, -78),
        new Node("sf4angel:core/clay_bucket_gamble", "Clay Bucket Gamble", strings("sf4angel:core/clay_aiken"), strings(),
            ints(34, 60), 149, 120, 19),
        new Node("sf4angel:core/coal_in_bloom", "Coal in Bloom", strings("sf4angel:core/beachfront_property"), strings(),
            ints(12, 60), 181, 120, -26),
        new Node("sf4angel:core/ironwood", "Ironwood", strings("sf4angel:core/coal_in_bloom", "sf4angel:core/clay_aiken", "sf4angel:core/gravel_travel"), strings(),
            ints(13, 14), 187, 136, -27),
        new Node("sf4angel:core/oh_the_irony", "Oh, the Irony", strings("sf4angel:core/ironwood"), strings(),
            ints(9, 18, 20, 35, 36, 39, 40, 54, 102, 103, 117), 212, 152, -51),
        new Node("sf4angel:core/red_tree_redemption", "Red Tree Redemption", strings("sf4angel:core/ironwood"), strings(),
            ints(15, 40, 54, 60, 61, 102, 103), 220, 152, -14),
        new Node("sf4angel:core/trees_to_diamonds", "Trees to Diamonds", strings("sf4angel:core/red_tree_redemption"), strings(),
            ints(16), 226, 168, -12),
        new Node("sf4angel:core/diamond_clarity", "Diamond Clarity", strings("sf4angel:core/trees_to_diamonds"), strings(),
            ints(22, 42, 64, 65, 70, 117), 232, 184, -10),
        new Node("sf4angel:core/tiny_tree_big_plans", "Tiny Tree, Big Plans", strings("sf4angel:core/clay_aiken"), strings(),
            ints(18), 144, 120, 26),
        new Node("sf4angel:core/drop_it_like_its_hopping", "Drop It Like It's Hopping", strings("sf4angel:core/tiny_tree_big_plans", "sf4angel:core/oh_the_irony"), strings(),
            ints(19), 222, 168, -72),
        new Node("sf4angel:core/orchard_on_autopilot", "Orchard on Autopilot", strings("sf4angel:core/drop_it_like_its_hopping"), strings(),
            ints(), 225, 184, -77),
        new Node("sf4angel:core/paperwork_begins", "Paperwork Begins", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(108), 225, 168, -85),
        new Node("sf4angel:core/bulk_by_barrel", "Bulk by Barrel", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 50, 72, 23),
        new Node("sf4angel:core/network_attached_chest", "Network Attached Chest", strings("sf4angel:core/diamond_clarity"), strings(),
            ints(23), 251, 200, -24),
        new Node("sf4angel:core/remote_possibilities", "Remote Possibilities", strings("sf4angel:core/network_attached_chest"), strings(),
            ints(), 256, 216, -28),
        new Node("sf4angel:core/market_forces", "Market Forces", strings("sf4angel:core/clay_aiken"), strings(),
            ints(25), 134, 120, 34),
        new Node("sf4angel:core/seeds_of_life", "Seeds of Life", strings("sf4angel:core/market_forces"), strings(),
            ints(26, 28, 112), 136, 136, 40),
        new Node("sf4angel:core/barnyard_beginnings", "Barnyard Beginnings", strings("sf4angel:core/seeds_of_life"), strings(),
            ints(69), 150, 152, 59),
        new Node("sf4angel:core/hog_tied", "Hog Tied", strings("sf4angel:core/water_you_waiting_for", "sf4angel:core/dirty_dancing"), strings(),
            ints(28), 204, 184, -99),
        new Node("sf4angel:core/truffle_shuffle", "Truffle Shuffle", strings("sf4angel:core/hog_tied", "sf4angel:core/seeds_of_life"), strings(),
            ints(), 201, 200, -104),
        new Node("sf4angel:core/snad_together", "Snad Together", strings("sf4angel:core/beachfront_property"), strings(),
            ints(30), 160, 120, 0),
        new Node("sf4angel:core/cane_and_able", "Cane and Able", strings("sf4angel:core/snad_together"), strings(),
            ints(), 164, 136, 5),
        new Node("sf4angel:core/tooling_up", "Tooling Up", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(36, 37, 38, 119), 55, 72, 7),
        new Node("sf4angel:core/parts_department", "Parts Department", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(37, 38), 60, 72, 31),
        new Node("sf4angel:core/melting_point", "Melting Point", strings("sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_aiken"), strings(),
            ints(), 121, 120, -53),
        new Node("sf4angel:core/smeltery_authority", "Smeltery Authority", strings("sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_bucket_gamble"), strings(),
            ints(42), 155, 136, 21),
        new Node("sf4angel:core/cast_away", "Cast Away", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(84), 221, 168, -65),
        new Node("sf4angel:core/forge_ahead", "Forge Ahead", strings("sf4angel:core/tooling_up", "sf4angel:core/oh_the_irony"), strings(),
            ints(), 228, 168, -68),
        new Node("sf4angel:core/modifier_motive", "Modifier Motive", strings("sf4angel:core/tooling_up", "sf4angel:core/parts_department"), strings(),
            ints(116), 78, 88, 7),
        new Node("sf4angel:core/level_headed_tool", "Level-Headed Tool", strings("sf4angel:core/tooling_up", "sf4angel:core/parts_department"), strings(),
            ints(), 76, 88, 17),
        new Node("sf4angel:core/armor_by_committee", "Armor by Committee", strings("sf4angel:core/oh_the_irony"), strings(),
            ints(), 231, 168, -80),
        new Node("sf4angel:core/first_spark", "First Spark", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(43, 48, 53, 55, 63, 89, 97, 98), 226, 168, -59),
        new Node("sf4angel:core/battery_included", "Battery Included", strings("sf4angel:core/redstone_in_a_box"), strings(),
            ints(51), 281, 216, -22),
        new Node("sf4angel:core/redstone_in_a_box", "Redstone in a Box", strings("sf4angel:core/smeltery_authority", "sf4angel:core/diamond_clarity"), strings(),
            ints(41, 43, 48, 83), 264, 200, -6),
        new Node("sf4angel:core/steel_yourself", "Steel Yourself", strings("sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"), strings(),
            ints(44, 45, 52, 89, 100, 101), 295, 216, -4),
        new Node("sf4angel:core/enriched_expectations", "Enriched Expectations", strings("sf4angel:core/steel_yourself"), strings(),
            ints(46), 312, 232, -20),
        new Node("sf4angel:core/crush_depth", "Crush Depth", strings("sf4angel:core/steel_yourself"), strings(),
            ints(46, 49, 51, 53), 319, 232, -14),
        new Node("sf4angel:core/triple_threat", "Triple Threat", strings("sf4angel:core/enriched_expectations", "sf4angel:core/crush_depth", "sf4angel:core/hydrogen_economy"), strings(),
            ints(47), 316, 248, -24),
        new Node("sf4angel:core/five_times_the_charm", "Five Times the Charm", strings("sf4angel:core/triple_threat"), strings(),
            ints(), 320, 264, -28),
        new Node("sf4angel:core/hydrogen_economy", "Hydrogen Economy", strings("sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"), strings(),
            ints(46, 49), 279, 216, 12),
        new Node("sf4angel:core/gas_grass_or_rf", "Gas, Grass, or RF", strings("sf4angel:core/hydrogen_economy", "sf4angel:core/crush_depth"), strings(),
            ints(50), 335, 248, -31),
        new Node("sf4angel:core/hdpeasy", "HDPEasy", strings("sf4angel:core/gas_grass_or_rf"), strings(),
            ints(), 339, 264, -35),
        new Node("sf4angel:core/ultimate_capacity", "Ultimate Capacity", strings("sf4angel:core/crush_depth", "sf4angel:core/battery_included"), strings(),
            ints(), 341, 248, -23),
        new Node("sf4angel:core/factory_settings", "Factory Settings", strings("sf4angel:core/steel_yourself"), strings(),
            ints(), 318, 232, -6),
        new Node("sf4angel:core/digital_prospector", "Digital Prospector", strings("sf4angel:core/crush_depth", "sf4angel:core/first_spark"), strings(),
            ints(), 342, 248, -13),
        new Node("sf4angel:core/latex_intentions", "Latex Intentions", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(55), 237, 168, -60),
        new Node("sf4angel:core/plastic_industry", "Plastic Industry", strings("sf4angel:core/latex_intentions", "sf4angel:core/first_spark"), strings(),
            ints(56, 57, 58, 59), 243, 184, -62),
        new Node("sf4angel:core/sow_automatic", "Sow Automatic", strings("sf4angel:core/plastic_industry"), strings(),
            ints(), 259, 200, -80),
        new Node("sf4angel:core/reap_automatic", "Reap Automatic", strings("sf4angel:core/plastic_industry"), strings(),
            ints(), 264, 200, -73),
        new Node("sf4angel:core/mob_rules", "Mob Rules", strings("sf4angel:core/plastic_industry"), strings(),
            ints(114), 266, 200, -65),
        new Node("sf4angel:core/black_hole_inventory", "Black Hole Inventory", strings("sf4angel:core/plastic_industry"), strings(),
            ints(107), 266, 200, -57),
        new Node("sf4angel:core/learn_deeply", "Learn Deeply", strings("sf4angel:core/coal_in_bloom", "sf4angel:core/red_tree_redemption", "sf4angel:core/clay_bucket_gamble", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(), 211, 184, -101),
        new Node("sf4angel:core/model_citizen", "Model Citizen", strings("sf4angel:core/red_tree_redemption", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(62), 221, 184, -102),
        new Node("sf4angel:core/data_with_experience", "Data with Experience", strings("sf4angel:core/model_citizen"), strings(),
            ints(63, 64), 222, 200, -108),
        new Node("sf4angel:core/simulation_theory", "Simulation Theory", strings("sf4angel:core/data_with_experience", "sf4angel:core/first_spark"), strings(),
            ints(), 219, 216, -131),
        new Node("sf4angel:core/reality_armor", "Reality Armor", strings("sf4angel:core/data_with_experience", "sf4angel:core/diamond_clarity", "sf4angel:core/water_you_waiting_for"), strings(),
            ints(109), 232, 216, -129),
        new Node("sf4angel:core/cake_to_hell", "Cake to Hell", strings("sf4angel:core/diamond_clarity"), strings(),
            ints(66), 246, 200, 13),
        new Node("sf4angel:core/nether_say_never", "Nether Say Never", strings("sf4angel:core/cake_to_hell"), strings(),
            ints(67), 249, 216, 18),
        new Node("sf4angel:core/blaze_of_glory", "Blaze of Glory", strings("sf4angel:core/nether_say_never"), strings(),
            ints(68, 70, 71), 252, 232, 23),
        new Node("sf4angel:core/wither_or_not", "Wither or Not", strings("sf4angel:core/blaze_of_glory"), strings(),
            ints(), 272, 248, 34),
        new Node("sf4angel:core/the_hunting_trip", "The Hunting Trip", strings("sf4angel:core/barnyard_beginnings"), strings(),
            ints(), 154, 168, 64),
        new Node("sf4angel:core/lost_and_found", "Lost and Found", strings("sf4angel:core/blaze_of_glory", "sf4angel:core/diamond_clarity"), strings(),
            ints(84, 92, 97, 98), 265, 248, 44),
        new Node("sf4angel:core/cake_at_the_end", "Cake at the End", strings("sf4angel:core/blaze_of_glory"), strings(),
            ints(72), 252, 248, 46),
        new Node("sf4angel:core/the_void_blinks_back", "The Void Blinks Back", strings("sf4angel:core/cake_at_the_end"), strings(),
            ints(73), 252, 264, 52),
        new Node("sf4angel:core/dragon_eviction_notice", "Dragon Eviction Notice", strings("sf4angel:core/the_void_blinks_back"), strings(),
            ints(74), 252, 280, 58),
        new Node("sf4angel:core/borrowed_wings", "Borrowed Wings", strings("sf4angel:core/dragon_eviction_notice"), strings(),
            ints(109), 252, 296, 64),
        new Node("sf4angel:core/into_the_twilight", "Into the Twilight", strings(), strings("twilight_forest"),
            ints(76), 68, 56, 0),
        new Node("sf4angel:core/naga_have_i_ever", "Naga Have I Ever", strings("sf4angel:core/into_the_twilight"), strings(),
            ints(77), 69, 72, 5),
        new Node("sf4angel:core/lich_please", "Lich, Please", strings("sf4angel:core/naga_have_i_ever"), strings(),
            ints(78, 79), 70, 88, 11),
        new Node("sf4angel:core/hydra_expectations", "Hydra Expectations", strings("sf4angel:core/lich_please"), strings(),
            ints(), 80, 104, 32),
        new Node("sf4angel:core/ice_queen_cometh", "Ice Queen Cometh", strings("sf4angel:core/lich_please"), strings(),
            ints(109), 67, 104, 34),
        new Node("sf4angel:core/menril_state_of_mind", "Menril State of Mind", strings("sf4angel:core/water_you_waiting_for"), strings(),
            ints(81, 83), 230, 184, -96),
        new Node("sf4angel:core/logic_in_the_void", "Logic in the Void", strings("sf4angel:core/menril_state_of_mind"), strings(),
            ints(82), 234, 200, -101),
        new Node("sf4angel:core/items_in_transit", "Items in Transit", strings("sf4angel:core/logic_in_the_void"), strings(),
            ints(), 238, 216, -106),
        new Node("sf4angel:core/fluix_of_the_matter", "Fluix of the Matter", strings("sf4angel:core/menril_state_of_mind", "sf4angel:core/redstone_in_a_box"), strings(),
            ints(85), 272, 216, 19),
        new Node("sf4angel:core/pressing_engagement", "Pressing Engagement", strings("sf4angel:core/cast_away", "sf4angel:core/alloyed_allegiance", "sf4angel:core/lost_and_found"), strings(),
            ints(86), 350, 264, 1),
        new Node("sf4angel:core/acceptable_energy", "Acceptable Energy", strings("sf4angel:core/fluix_of_the_matter"), strings(),
            ints(86), 274, 232, 25),
        new Node("sf4angel:core/me_myself_and_i", "ME, Myself, and I", strings("sf4angel:core/acceptable_energy", "sf4angel:core/pressing_engagement"), strings(),
            ints(87), 356, 280, 0),
        new Node("sf4angel:core/sixty_four_k_and_counting", "Sixty-Four K and Counting", strings("sf4angel:core/me_myself_and_i"), strings(),
            ints(88), 362, 296, -1),
        new Node("sf4angel:core/autocraft_authority", "Autocraft Authority", strings("sf4angel:core/sixty_four_k_and_counting"), strings(),
            ints(), 368, 312, -2),
        new Node("sf4angel:core/manufactory_warranty_void", "Manufactory Warranty Void", strings("sf4angel:core/steel_yourself", "sf4angel:core/first_spark"), strings(),
            ints(90), 321, 232, 4),
        new Node("sf4angel:core/alloyed_allegiance", "Alloyed Allegiance", strings("sf4angel:core/manufactory_warranty_void"), strings(),
            ints(84, 91, 93, 94), 327, 248, 6),
        new Node("sf4angel:core/positive_fission", "Positive Fission", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(92), 350, 264, 9),
        new Node("sf4angel:core/gone_fission", "Gone Fission", strings("sf4angel:core/positive_fission", "sf4angel:core/lost_and_found"), strings(),
            ints(), 356, 280, 10),
        new Node("sf4angel:core/deuterium_duty", "Deuterium Duty", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(95), 348, 264, 17),
        new Node("sf4angel:core/tritium_triumph", "Tritium Triumph", strings("sf4angel:core/alloyed_allegiance"), strings(),
            ints(95), 343, 264, 24),
        new Node("sf4angel:core/fusion_cuisine", "Fusion Cuisine", strings("sf4angel:core/deuterium_duty", "sf4angel:core/tritium_triumph"), strings(),
            ints(96), 353, 280, 20),
        new Node("sf4angel:core/pocket_star", "Pocket Star", strings("sf4angel:core/fusion_cuisine"), strings(),
            ints(109), 358, 296, 23),
        new Node("sf4angel:core/matter_of_analysis", "Matter of Analysis", strings("sf4angel:core/lost_and_found", "sf4angel:core/first_spark"), strings(),
            ints(99), 283, 264, 59),
        new Node("sf4angel:core/decompose_yourself", "Decompose Yourself", strings("sf4angel:core/lost_and_found", "sf4angel:core/first_spark"), strings(),
            ints(99), 271, 264, 67),
        new Node("sf4angel:core/replication_nation", "Replication Nation", strings("sf4angel:core/matter_of_analysis", "sf4angel:core/decompose_yourself"), strings(),
            ints(109), 288, 280, 63),
        new Node("sf4angel:core/matrix_reloaded", "Matrix Reloaded", strings("sf4angel:core/steel_yourself"), strings(),
            ints(), 313, 232, 11),
        new Node("sf4angel:core/turbine_service", "Turbine Service", strings("sf4angel:core/steel_yourself"), strings(),
            ints(109), 305, 232, 19),
        new Node("sf4angel:core/crafting_core_values", "Crafting Core Values", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(), 235, 168, -52),
        new Node("sf4angel:core/quantum_compression", "Quantum Compression", strings("sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"), strings(),
            ints(104, 105), 232, 168, -43),
        new Node("sf4angel:core/metals_into_points", "Metals into Points", strings("sf4angel:core/quantum_compression"), strings(),
            ints(106), 255, 184, -41),
        new Node("sf4angel:core/cookie_bacon_donut_collapse", "Cookie, Bacon, Donut, Collapse", strings("sf4angel:core/quantum_compression"), strings(),
            ints(106), 250, 184, -28),
        new Node("sf4angel:core/ultimate_singularity", "Ultimate Singularity", strings("sf4angel:core/metals_into_points", "sf4angel:core/cookie_bacon_donut_collapse"), strings(),
            ints(109), 261, 200, -40),
        new Node("sf4angel:core/black_hole_filled", "Black Hole Filled", strings("sf4angel:core/black_hole_inventory"), strings(),
            ints(109), 272, 216, -56),
        new Node("sf4angel:core/million_item_paperwork", "Million-Item Paperwork", strings("sf4angel:core/paperwork_begins"), strings(),
            ints(109), 222, 184, -86),
        new Node("sf4angel:core/the_sky_finally_claps", "The Sky Finally Claps", strings("sf4angel:core/ice_queen_cometh", "sf4angel:core/ultimate_singularity", "sf4angel:core/black_hole_filled", "sf4angel:core/million_item_paperwork", "sf4angel:core/turbine_service", "sf4angel:core/pocket_star", "sf4angel:core/replication_nation", "sf4angel:core/borrowed_wings", "sf4angel:core/reality_armor"), strings(),
            ints(), 363, 312, 26),
        new Node("sf4angel:optional/sticky_keys", "Sticky Keys", strings(), strings(),
            ints(), 60, 56, 20),
        new Node("sf4angel:optional/nap_time", "Nap Time", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 36, 72, 19),
        new Node("sf4angel:optional/milk_without_the_moo", "Milk Without the Moo", strings("sf4angel:core/seeds_of_life"), strings(),
            ints(), 137, 152, 63),
        new Node("sf4angel:optional/a_balanced_sky_diet", "A Balanced Sky Diet", strings(), strings(),
            ints(), 40, 56, 28),
        new Node("sf4angel:optional/mob_factory_floor", "Mob Factory Floor", strings("sf4angel:core/mob_rules"), strings(),
            ints(), 272, 216, -66),
        new Node("sf4angel:optional/armored_to_the_teeth", "Armored to the Teeth", strings(), strings(),
            ints(), 20, 56, 20),
        new Node("sf4angel:optional/unbreakable_resolve", "Unbreakable Resolve", strings("sf4angel:core/modifier_motive"), strings(),
            ints(), 84, 104, 7),
        new Node("sf4angel:optional/undo_the_apocalypse", "Undo the Apocalypse", strings("sf4angel:core/oh_the_irony", "sf4angel:core/diamond_clarity"), strings(),
            ints(), 239, 200, 12),
        new Node("sf4angel:optional/pixel_perfect_masonry", "Pixel Perfect Masonry", strings("sf4angel:core/starting_from_the_bottom"), strings(),
            ints(), 44, 72, 41),
        new Node("sf4angel:optional/around_the_void_in_eighty_throws", "Around the Void in Eighty Throws", strings("sf4angel:core/tooling_up"), strings(),
            ints(), 70, 88, 25),
        new Node("sf4angel:optional/android_dreams", "Android Dreams of Electric Sheep", strings(), strings("android"),
            ints(), 12, 56, 0),
        new Node("sf4angel:optional/robot_did_it", "Robot Did It", strings(), strings("open_computers"),
            ints(), 20, 56, -20),
        new Node("sf4angel:optional/maximum_minimum_space", "Maximum Minimum Space", strings(), strings(),
            ints(), 40, 56, -28),
        new Node("sf4angel:prestige/prestige_worldwide", "Prestige Worldwide", strings(), strings("parabox"),
            ints(127), 60, 56, -20),
        new Node("sf4angel:prestige/equivalent_ambition_unlocked", "Equivalent Ambition Unlocked", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "project_e"),
            ints(), 70, 88, -47),
        new Node("sf4angel:prestige/aperture_unlocked", "Aperture Unlocked", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "portal_gun"),
            ints(), 77, 88, -43),
        new Node("sf4angel:prestige/written_in_another_age", "Written in Another Age", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "mystcraft"),
            ints(), 83, 88, -37),
        new Node("sf4angel:prestige/time_is_a_flat_parabox", "Time Is a Flat Parabox", strings("sf4angel:prestige/prestige_worldwide"), strings("parabox"),
            ints(124, 125, 126, 128), 64, 72, -24),
        new Node("sf4angel:prestige/empowered_recursion", "Empowered Recursion", strings("sf4angel:prestige/time_is_a_flat_parabox"), strings("parabox", "parabox_two"),
            ints(), 88, 88, -44)
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
