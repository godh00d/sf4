// Generated from ACHIEVEMENT_PLAN.md by instance-config/generate_triumph.ps1.
window.SF4_CONSTELLATION = {
  version: 1,
  catalogHash: "077cfaf77da7989a85d498922f8ce158963b24b80681fcc700639b2d8c9b65ce",
  count: 129,
  nodes: [
    {
      id: "sf4angel:core/starting_from_the_bottom",
      title: "Starting from the Bottom",
      category: "core",
      x: 40, y: 56, z: 0,
      parents: [],
      children: ["sf4angel:core/that_is_dir_tree", "sf4angel:core/captain_hook", "sf4angel:core/bulk_by_barrel", "sf4angel:core/tooling_up", "sf4angel:core/parts_department", "sf4angel:optional/nap_time", "sf4angel:optional/pixel_perfect_masonry"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/that_is_dir_tree",
      title: "That's Dir-tree",
      category: "core",
      x: 58, y: 64, z: -3,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: ["sf4angel:core/dirty_dancing", "sf4angel:core/stone_tree_oath"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/dirty_dancing",
      title: "Dirty Dancing",
      category: "core",
      x: 61, y: 72, z: -14,
      parents: ["sf4angel:core/that_is_dir_tree"],
      children: ["sf4angel:core/hog_tied"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/captain_hook",
      title: "Captain Hook",
      category: "core",
      x: 48, y: 64, z: 9,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/stone_tree_oath",
      title: "Stone Tree Oath",
      category: "core",
      x: 76, y: 72, z: -6,
      parents: ["sf4angel:core/that_is_dir_tree"],
      children: ["sf4angel:core/stone_from_trees", "sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_aiken"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/stone_from_trees",
      title: "Stone from Trees",
      category: "core",
      x: 79, y: 80, z: -17,
      parents: ["sf4angel:core/stone_tree_oath"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/gravel_travel",
      title: "Gravel Travel",
      category: "core",
      x: 81, y: 80, z: -31,
      parents: ["sf4angel:core/stone_tree_oath"],
      children: ["sf4angel:core/ironwood", "sf4angel:core/melting_point", "sf4angel:core/smeltery_authority"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/beachfront_property",
      title: "Beachfront Property",
      category: "core",
      x: 94, y: 80, z: -10,
      parents: ["sf4angel:core/stone_tree_oath"],
      children: ["sf4angel:core/coal_in_bloom", "sf4angel:core/snad_together", "sf4angel:core/melting_point", "sf4angel:core/smeltery_authority"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/clay_aiken",
      title: "Clay Aiken",
      category: "core",
      x: 84, y: 80, z: 4,
      parents: ["sf4angel:core/stone_tree_oath"],
      children: ["sf4angel:core/clay_bucket_gamble", "sf4angel:core/ironwood", "sf4angel:core/tiny_tree_big_plans", "sf4angel:core/market_forces", "sf4angel:core/melting_point"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/water_you_waiting_for",
      title: "Water You Waiting For?",
      category: "core",
      x: 123, y: 112, z: -43,
      parents: ["sf4angel:core/oh_the_irony"],
      children: ["sf4angel:core/hog_tied", "sf4angel:core/learn_deeply", "sf4angel:core/model_citizen", "sf4angel:core/reality_armor", "sf4angel:core/menril_state_of_mind"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/clay_bucket_gamble",
      title: "Clay Bucket Gamble",
      category: "core",
      x: 95, y: 88, z: 8,
      parents: ["sf4angel:core/clay_aiken"],
      children: ["sf4angel:core/smeltery_authority", "sf4angel:core/learn_deeply"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/coal_in_bloom",
      title: "Coal in Bloom",
      category: "core",
      x: 111, y: 88, z: -14,
      parents: ["sf4angel:core/beachfront_property"],
      children: ["sf4angel:core/ironwood", "sf4angel:core/learn_deeply"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/ironwood",
      title: "Ironwood",
      category: "core",
      x: 114, y: 96, z: -15,
      parents: ["sf4angel:core/coal_in_bloom", "sf4angel:core/clay_aiken", "sf4angel:core/gravel_travel"],
      children: ["sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/oh_the_irony",
      title: "Oh, the Irony",
      category: "core",
      x: 124, y: 104, z: -29,
      parents: ["sf4angel:core/ironwood"],
      children: ["sf4angel:core/water_you_waiting_for", "sf4angel:core/drop_it_like_its_hopping", "sf4angel:core/paperwork_begins", "sf4angel:core/cast_away", "sf4angel:core/forge_ahead", "sf4angel:core/armor_by_committee", "sf4angel:core/first_spark", "sf4angel:core/latex_intentions", "sf4angel:core/crafting_core_values", "sf4angel:core/quantum_compression", "sf4angel:optional/undo_the_apocalypse"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/red_tree_redemption",
      title: "Red Tree Redemption",
      category: "core",
      x: 131, y: 104, z: -11,
      parents: ["sf4angel:core/ironwood"],
      children: ["sf4angel:core/trees_to_diamonds", "sf4angel:core/first_spark", "sf4angel:core/latex_intentions", "sf4angel:core/learn_deeply", "sf4angel:core/model_citizen", "sf4angel:core/crafting_core_values", "sf4angel:core/quantum_compression"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/trees_to_diamonds",
      title: "Trees to Diamonds",
      category: "core",
      x: 134, y: 112, z: -10,
      parents: ["sf4angel:core/red_tree_redemption"],
      children: ["sf4angel:core/diamond_clarity"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/diamond_clarity",
      title: "Diamond Clarity",
      category: "core",
      x: 137, y: 120, z: -9,
      parents: ["sf4angel:core/trees_to_diamonds"],
      children: ["sf4angel:core/network_attached_chest", "sf4angel:core/redstone_in_a_box", "sf4angel:core/reality_armor", "sf4angel:core/cake_to_hell", "sf4angel:core/lost_and_found", "sf4angel:optional/undo_the_apocalypse"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/tiny_tree_big_plans",
      title: "Tiny Tree, Big Plans",
      category: "core",
      x: 88, y: 88, z: 17,
      parents: ["sf4angel:core/clay_aiken"],
      children: ["sf4angel:core/drop_it_like_its_hopping"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/drop_it_like_its_hopping",
      title: "Drop It Like It's Hopping",
      category: "core",
      x: 122, y: 112, z: -35,
      parents: ["sf4angel:core/tiny_tree_big_plans", "sf4angel:core/oh_the_irony"],
      children: ["sf4angel:core/orchard_on_autopilot"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/orchard_on_autopilot",
      title: "Orchard on Autopilot",
      category: "core",
      x: 121, y: 120, z: -38,
      parents: ["sf4angel:core/drop_it_like_its_hopping"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/paperwork_begins",
      title: "Paperwork Begins",
      category: "core",
      x: 129, y: 112, z: -54,
      parents: ["sf4angel:core/oh_the_irony"],
      children: ["sf4angel:core/million_item_paperwork"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/bulk_by_barrel",
      title: "Bulk by Barrel",
      category: "core",
      x: 42, y: 64, z: 14,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/network_attached_chest",
      title: "Network Attached Chest",
      category: "core",
      x: 146, y: 128, z: -16,
      parents: ["sf4angel:core/diamond_clarity"],
      children: ["sf4angel:core/remote_possibilities"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/remote_possibilities",
      title: "Remote Possibilities",
      category: "core",
      x: 148, y: 136, z: -18,
      parents: ["sf4angel:core/network_attached_chest"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/market_forces",
      title: "Market Forces",
      category: "core",
      x: 89, y: 88, z: 2,
      parents: ["sf4angel:core/clay_aiken"],
      children: ["sf4angel:core/seeds_of_life"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/seeds_of_life",
      title: "Seeds of Life",
      category: "core",
      x: 92, y: 96, z: 1,
      parents: ["sf4angel:core/market_forces"],
      children: ["sf4angel:core/barnyard_beginnings", "sf4angel:core/truffle_shuffle", "sf4angel:optional/milk_without_the_moo"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/barnyard_beginnings",
      title: "Barnyard Beginnings",
      category: "core",
      x: 102, y: 104, z: -6,
      parents: ["sf4angel:core/seeds_of_life"],
      children: ["sf4angel:core/the_hunting_trip"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/hog_tied",
      title: "Hog Tied",
      category: "core",
      x: 116, y: 120, z: -52,
      parents: ["sf4angel:core/water_you_waiting_for", "sf4angel:core/dirty_dancing"],
      children: ["sf4angel:core/truffle_shuffle"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/truffle_shuffle",
      title: "Truffle Shuffle",
      category: "core",
      x: 114, y: 128, z: -54,
      parents: ["sf4angel:core/hog_tied", "sf4angel:core/seeds_of_life"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/snad_together",
      title: "Snad Together",
      category: "core",
      x: 101, y: 88, z: -1,
      parents: ["sf4angel:core/beachfront_property"],
      children: ["sf4angel:core/cane_and_able"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/cane_and_able",
      title: "Cane and Able",
      category: "core",
      x: 103, y: 96, z: 1,
      parents: ["sf4angel:core/snad_together"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/tooling_up",
      title: "Tooling Up",
      category: "core",
      x: 48, y: 64, z: -3,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: ["sf4angel:core/forge_ahead", "sf4angel:core/modifier_motive", "sf4angel:core/level_headed_tool", "sf4angel:optional/around_the_void_in_eighty_throws"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/parts_department",
      title: "Parts Department",
      category: "core",
      x: 55, y: 64, z: 21,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: ["sf4angel:core/modifier_motive", "sf4angel:core/level_headed_tool"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/melting_point",
      title: "Melting Point",
      category: "core",
      x: 82, y: 88, z: -34,
      parents: ["sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_aiken"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/smeltery_authority",
      title: "Smeltery Authority",
      category: "core",
      x: 98, y: 96, z: 9,
      parents: ["sf4angel:core/gravel_travel", "sf4angel:core/beachfront_property", "sf4angel:core/clay_bucket_gamble"],
      children: ["sf4angel:core/redstone_in_a_box"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/cast_away",
      title: "Cast Away",
      category: "core",
      x: 129, y: 112, z: -39,
      parents: ["sf4angel:core/oh_the_irony"],
      children: ["sf4angel:core/pressing_engagement"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/forge_ahead",
      title: "Forge Ahead",
      category: "core",
      x: 139, y: 112, z: -28,
      parents: ["sf4angel:core/tooling_up", "sf4angel:core/oh_the_irony"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/modifier_motive",
      title: "Modifier Motive",
      category: "core",
      x: 51, y: 72, z: -6,
      parents: ["sf4angel:core/tooling_up", "sf4angel:core/parts_department"],
      children: ["sf4angel:optional/unbreakable_resolve"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/level_headed_tool",
      title: "Level-Headed Tool",
      category: "core",
      x: 59, y: 72, z: -7,
      parents: ["sf4angel:core/tooling_up", "sf4angel:core/parts_department"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/armor_by_committee",
      title: "Armor by Committee",
      category: "core",
      x: 149, y: 112, z: -49,
      parents: ["sf4angel:core/oh_the_irony"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/first_spark",
      title: "First Spark",
      category: "core",
      x: 127, y: 112, z: -17,
      parents: ["sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"],
      children: ["sf4angel:core/steel_yourself", "sf4angel:core/hydrogen_economy", "sf4angel:core/digital_prospector", "sf4angel:core/plastic_industry", "sf4angel:core/simulation_theory", "sf4angel:core/manufactory_warranty_void", "sf4angel:core/matter_of_analysis", "sf4angel:core/decompose_yourself"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/battery_included",
      title: "Battery Included",
      category: "core",
      x: 162, y: 136, z: -15,
      parents: ["sf4angel:core/redstone_in_a_box"],
      children: ["sf4angel:core/ultimate_capacity"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/redstone_in_a_box",
      title: "Redstone in a Box",
      category: "core",
      x: 153, y: 128, z: -7,
      parents: ["sf4angel:core/smeltery_authority", "sf4angel:core/diamond_clarity"],
      children: ["sf4angel:core/battery_included", "sf4angel:core/steel_yourself", "sf4angel:core/hydrogen_economy", "sf4angel:core/fluix_of_the_matter"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/steel_yourself",
      title: "Steel Yourself",
      category: "core",
      x: 169, y: 136, z: -6,
      parents: ["sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/enriched_expectations", "sf4angel:core/crush_depth", "sf4angel:core/factory_settings", "sf4angel:core/manufactory_warranty_void", "sf4angel:core/matrix_reloaded", "sf4angel:core/turbine_service"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/enriched_expectations",
      title: "Enriched Expectations",
      category: "core",
      x: 178, y: 144, z: -14,
      parents: ["sf4angel:core/steel_yourself"],
      children: ["sf4angel:core/triple_threat"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/crush_depth",
      title: "Crush Depth",
      category: "core",
      x: 176, y: 144, z: -6,
      parents: ["sf4angel:core/steel_yourself"],
      children: ["sf4angel:core/triple_threat", "sf4angel:core/gas_grass_or_rf", "sf4angel:core/ultimate_capacity", "sf4angel:core/digital_prospector"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/triple_threat",
      title: "Triple Threat",
      category: "core",
      x: 180, y: 152, z: -16,
      parents: ["sf4angel:core/enriched_expectations", "sf4angel:core/crush_depth", "sf4angel:core/hydrogen_economy"],
      children: ["sf4angel:core/five_times_the_charm"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/five_times_the_charm",
      title: "Five Times the Charm",
      category: "core",
      x: 182, y: 160, z: -18,
      parents: ["sf4angel:core/triple_threat"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/hydrogen_economy",
      title: "Hydrogen Economy",
      category: "core",
      x: 161, y: 136, z: 2,
      parents: ["sf4angel:core/redstone_in_a_box", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/triple_threat", "sf4angel:core/gas_grass_or_rf"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/gas_grass_or_rf",
      title: "Gas, Grass, or RF",
      category: "core",
      x: 187, y: 152, z: -11,
      parents: ["sf4angel:core/hydrogen_economy", "sf4angel:core/crush_depth"],
      children: ["sf4angel:core/hdpeasy"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/hdpeasy",
      title: "HDPEasy",
      category: "core",
      x: 190, y: 160, z: -12,
      parents: ["sf4angel:core/gas_grass_or_rf"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/ultimate_capacity",
      title: "Ultimate Capacity",
      category: "core",
      x: 183, y: 152, z: -1,
      parents: ["sf4angel:core/crush_depth", "sf4angel:core/battery_included"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/factory_settings",
      title: "Factory Settings",
      category: "core",
      x: 182, y: 144, z: -21,
      parents: ["sf4angel:core/steel_yourself"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/digital_prospector",
      title: "Digital Prospector",
      category: "core",
      x: 196, y: 152, z: 10,
      parents: ["sf4angel:core/crush_depth", "sf4angel:core/first_spark"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/latex_intentions",
      title: "Latex Intentions",
      category: "core",
      x: 136, y: 112, z: -36,
      parents: ["sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"],
      children: ["sf4angel:core/plastic_industry"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/plastic_industry",
      title: "Plastic Industry",
      category: "core",
      x: 139, y: 120, z: -38,
      parents: ["sf4angel:core/latex_intentions", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/sow_automatic", "sf4angel:core/reap_automatic", "sf4angel:core/mob_rules", "sf4angel:core/black_hole_inventory"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/sow_automatic",
      title: "Sow Automatic",
      category: "core",
      x: 144, y: 128, z: -48,
      parents: ["sf4angel:core/plastic_industry"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/reap_automatic",
      title: "Reap Automatic",
      category: "core",
      x: 142, y: 128, z: -41,
      parents: ["sf4angel:core/plastic_industry"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/mob_rules",
      title: "Mob Rules",
      category: "core",
      x: 150, y: 128, z: -43,
      parents: ["sf4angel:core/plastic_industry"],
      children: ["sf4angel:optional/mob_factory_floor"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/black_hole_inventory",
      title: "Black Hole Inventory",
      category: "core",
      x: 146, y: 128, z: -34,
      parents: ["sf4angel:core/plastic_industry"],
      children: ["sf4angel:core/black_hole_filled"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/learn_deeply",
      title: "Learn Deeply",
      category: "core",
      x: 120, y: 120, z: -68,
      parents: ["sf4angel:core/coal_in_bloom", "sf4angel:core/red_tree_redemption", "sf4angel:core/clay_bucket_gamble", "sf4angel:core/water_you_waiting_for"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/model_citizen",
      title: "Model Citizen",
      category: "core",
      x: 124, y: 120, z: -55,
      parents: ["sf4angel:core/red_tree_redemption", "sf4angel:core/water_you_waiting_for"],
      children: ["sf4angel:core/data_with_experience"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/data_with_experience",
      title: "Data with Experience",
      category: "core",
      x: 124, y: 128, z: -58,
      parents: ["sf4angel:core/model_citizen"],
      children: ["sf4angel:core/simulation_theory", "sf4angel:core/reality_armor"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/simulation_theory",
      title: "Simulation Theory",
      category: "core",
      x: 121, y: 136, z: -69,
      parents: ["sf4angel:core/data_with_experience", "sf4angel:core/first_spark"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/reality_armor",
      title: "Reality Armor",
      category: "core",
      x: 128, y: 136, z: -83,
      parents: ["sf4angel:core/data_with_experience", "sf4angel:core/diamond_clarity", "sf4angel:core/water_you_waiting_for"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/cake_to_hell",
      title: "Cake to Hell",
      category: "core",
      x: 144, y: 128, z: 2,
      parents: ["sf4angel:core/diamond_clarity"],
      children: ["sf4angel:core/nether_say_never"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/nether_say_never",
      title: "Nether Say Never",
      category: "core",
      x: 146, y: 136, z: 5,
      parents: ["sf4angel:core/cake_to_hell"],
      children: ["sf4angel:core/blaze_of_glory"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/blaze_of_glory",
      title: "Blaze of Glory",
      category: "core",
      x: 148, y: 144, z: 7,
      parents: ["sf4angel:core/nether_say_never"],
      children: ["sf4angel:core/wither_or_not", "sf4angel:core/lost_and_found", "sf4angel:core/cake_at_the_end"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/wither_or_not",
      title: "Wither or Not",
      category: "core",
      x: 159, y: 152, z: 10,
      parents: ["sf4angel:core/blaze_of_glory"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/the_hunting_trip",
      title: "The Hunting Trip",
      category: "core",
      x: 104, y: 112, z: -8,
      parents: ["sf4angel:core/barnyard_beginnings"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/lost_and_found",
      title: "Lost and Found",
      category: "core",
      x: 152, y: 152, z: 21,
      parents: ["sf4angel:core/blaze_of_glory", "sf4angel:core/diamond_clarity"],
      children: ["sf4angel:core/pressing_engagement", "sf4angel:core/gone_fission", "sf4angel:core/matter_of_analysis", "sf4angel:core/decompose_yourself"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/cake_at_the_end",
      title: "Cake at the End",
      category: "core",
      x: 152, y: 152, z: 4,
      parents: ["sf4angel:core/blaze_of_glory"],
      children: ["sf4angel:core/the_void_blinks_back"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/the_void_blinks_back",
      title: "The Void Blinks Back",
      category: "core",
      x: 154, y: 160, z: 2,
      parents: ["sf4angel:core/cake_at_the_end"],
      children: ["sf4angel:core/dragon_eviction_notice"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/dragon_eviction_notice",
      title: "Dragon Eviction Notice",
      category: "core",
      x: 156, y: 168, z: 0,
      parents: ["sf4angel:core/the_void_blinks_back"],
      children: ["sf4angel:core/borrowed_wings"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/borrowed_wings",
      title: "Borrowed Wings",
      category: "core",
      x: 158, y: 176, z: -2,
      parents: ["sf4angel:core/dragon_eviction_notice"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/into_the_twilight",
      title: "Into the Twilight",
      category: "core",
      x: 54, y: 56, z: 0,
      parents: [],
      children: ["sf4angel:core/naga_have_i_ever"],
      requiredStages: ["twilight_forest"]
    },
    {
      id: "sf4angel:core/naga_have_i_ever",
      title: "Naga Have I Ever",
      category: "core",
      x: 58, y: 64, z: -14,
      parents: ["sf4angel:core/into_the_twilight"],
      children: ["sf4angel:core/lich_please"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/lich_please",
      title: "Lich, Please",
      category: "core",
      x: 60, y: 72, z: -31,
      parents: ["sf4angel:core/naga_have_i_ever"],
      children: ["sf4angel:core/hydra_expectations", "sf4angel:core/ice_queen_cometh"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/hydra_expectations",
      title: "Hydra Expectations",
      category: "core",
      x: 58, y: 80, z: -43,
      parents: ["sf4angel:core/lich_please"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/ice_queen_cometh",
      title: "Ice Queen Cometh",
      category: "core",
      x: 65, y: 80, z: -42,
      parents: ["sf4angel:core/lich_please"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/menril_state_of_mind",
      title: "Menril State of Mind",
      category: "core",
      x: 124, y: 120, z: -48,
      parents: ["sf4angel:core/water_you_waiting_for"],
      children: ["sf4angel:core/logic_in_the_void", "sf4angel:core/fluix_of_the_matter"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/logic_in_the_void",
      title: "Logic in the Void",
      category: "core",
      x: 125, y: 128, z: -51,
      parents: ["sf4angel:core/menril_state_of_mind"],
      children: ["sf4angel:core/items_in_transit"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/items_in_transit",
      title: "Items in Transit",
      category: "core",
      x: 126, y: 136, z: -54,
      parents: ["sf4angel:core/logic_in_the_void"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/fluix_of_the_matter",
      title: "Fluix of the Matter",
      category: "core",
      x: 154, y: 136, z: 8,
      parents: ["sf4angel:core/menril_state_of_mind", "sf4angel:core/redstone_in_a_box"],
      children: ["sf4angel:core/acceptable_energy"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/pressing_engagement",
      title: "Pressing Engagement",
      category: "core",
      x: 161, y: 160, z: -12,
      parents: ["sf4angel:core/cast_away", "sf4angel:core/alloyed_allegiance", "sf4angel:core/lost_and_found"],
      children: ["sf4angel:core/me_myself_and_i"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/acceptable_energy",
      title: "Acceptable Energy",
      category: "core",
      x: 154, y: 144, z: 11,
      parents: ["sf4angel:core/fluix_of_the_matter"],
      children: ["sf4angel:core/me_myself_and_i"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/me_myself_and_i",
      title: "ME, Myself, and I",
      category: "core",
      x: 159, y: 168, z: -14,
      parents: ["sf4angel:core/acceptable_energy", "sf4angel:core/pressing_engagement"],
      children: ["sf4angel:core/sixty_four_k_and_counting"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/sixty_four_k_and_counting",
      title: "Sixty-Four K and Counting",
      category: "core",
      x: 157, y: 176, z: -16,
      parents: ["sf4angel:core/me_myself_and_i"],
      children: ["sf4angel:core/autocraft_authority"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/autocraft_authority",
      title: "Autocraft Authority",
      category: "core",
      x: 155, y: 184, z: -18,
      parents: ["sf4angel:core/sixty_four_k_and_counting"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/manufactory_warranty_void",
      title: "Manufactory Warranty Void",
      category: "core",
      x: 182, y: 144, z: -2,
      parents: ["sf4angel:core/steel_yourself", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/alloyed_allegiance"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/alloyed_allegiance",
      title: "Alloyed Allegiance",
      category: "core",
      x: 171, y: 152, z: -3,
      parents: ["sf4angel:core/manufactory_warranty_void"],
      children: ["sf4angel:core/pressing_engagement", "sf4angel:core/positive_fission", "sf4angel:core/deuterium_duty", "sf4angel:core/tritium_triumph"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/positive_fission",
      title: "Positive Fission",
      category: "core",
      x: 168, y: 160, z: 9,
      parents: ["sf4angel:core/alloyed_allegiance"],
      children: ["sf4angel:core/gone_fission"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/gone_fission",
      title: "Gone Fission",
      category: "core",
      x: 167, y: 168, z: 12,
      parents: ["sf4angel:core/positive_fission", "sf4angel:core/lost_and_found"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/deuterium_duty",
      title: "Deuterium Duty",
      category: "core",
      x: 161, y: 160, z: -20,
      parents: ["sf4angel:core/alloyed_allegiance"],
      children: ["sf4angel:core/fusion_cuisine"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/tritium_triumph",
      title: "Tritium Triumph",
      category: "core",
      x: 156, y: 160, z: -5,
      parents: ["sf4angel:core/alloyed_allegiance"],
      children: ["sf4angel:core/fusion_cuisine"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/fusion_cuisine",
      title: "Fusion Cuisine",
      category: "core",
      x: 159, y: 168, z: -23,
      parents: ["sf4angel:core/deuterium_duty", "sf4angel:core/tritium_triumph"],
      children: ["sf4angel:core/pocket_star"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/pocket_star",
      title: "Pocket Star",
      category: "core",
      x: 157, y: 176, z: -25,
      parents: ["sf4angel:core/fusion_cuisine"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/matter_of_analysis",
      title: "Matter of Analysis",
      category: "core",
      x: 158, y: 160, z: 31,
      parents: ["sf4angel:core/lost_and_found", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/replication_nation"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/decompose_yourself",
      title: "Decompose Yourself",
      category: "core",
      x: 147, y: 160, z: 38,
      parents: ["sf4angel:core/lost_and_found", "sf4angel:core/first_spark"],
      children: ["sf4angel:core/replication_nation"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/replication_nation",
      title: "Replication Nation",
      category: "core",
      x: 160, y: 168, z: 34,
      parents: ["sf4angel:core/matter_of_analysis", "sf4angel:core/decompose_yourself"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/matrix_reloaded",
      title: "Matrix Reloaded",
      category: "core",
      x: 173, y: 144, z: 7,
      parents: ["sf4angel:core/steel_yourself"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/turbine_service",
      title: "Turbine Service",
      category: "core",
      x: 186, y: 144, z: 14,
      parents: ["sf4angel:core/steel_yourself"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/crafting_core_values",
      title: "Crafting Core Values",
      category: "core",
      x: 130, y: 112, z: -27,
      parents: ["sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:core/quantum_compression",
      title: "Quantum Compression",
      category: "core",
      x: 137, y: 112, z: -44,
      parents: ["sf4angel:core/oh_the_irony", "sf4angel:core/red_tree_redemption"],
      children: ["sf4angel:core/metals_into_points", "sf4angel:core/cookie_bacon_donut_collapse"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/metals_into_points",
      title: "Metals into Points",
      category: "core",
      x: 142, y: 120, z: -55,
      parents: ["sf4angel:core/quantum_compression"],
      children: ["sf4angel:core/ultimate_singularity"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/cookie_bacon_donut_collapse",
      title: "Cookie, Bacon, Donut, Collapse",
      category: "core",
      x: 147, y: 120, z: -50,
      parents: ["sf4angel:core/quantum_compression"],
      children: ["sf4angel:core/ultimate_singularity"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/ultimate_singularity",
      title: "Ultimate Singularity",
      category: "core",
      x: 143, y: 128, z: -58,
      parents: ["sf4angel:core/metals_into_points", "sf4angel:core/cookie_bacon_donut_collapse"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/black_hole_filled",
      title: "Black Hole Filled",
      category: "core",
      x: 149, y: 136, z: -33,
      parents: ["sf4angel:core/black_hole_inventory"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/million_item_paperwork",
      title: "Million-Item Paperwork",
      category: "core",
      x: 131, y: 120, z: -71,
      parents: ["sf4angel:core/paperwork_begins"],
      children: ["sf4angel:core/the_sky_finally_claps"],
      requiredStages: []
    },
    {
      id: "sf4angel:core/the_sky_finally_claps",
      title: "The Sky Finally Claps",
      category: "core",
      x: 155, y: 184, z: -27,
      parents: ["sf4angel:core/ice_queen_cometh", "sf4angel:core/ultimate_singularity", "sf4angel:core/black_hole_filled", "sf4angel:core/million_item_paperwork", "sf4angel:core/turbine_service", "sf4angel:core/pocket_star", "sf4angel:core/replication_nation", "sf4angel:core/borrowed_wings", "sf4angel:core/reality_armor"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/sticky_keys",
      title: "Sticky Keys",
      category: "optional",
      x: 50, y: 56, z: 10,
      parents: [],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/nap_time",
      title: "Nap Time",
      category: "optional",
      x: 31, y: 64, z: 8,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/milk_without_the_moo",
      title: "Milk Without the Moo",
      category: "optional",
      x: 104, y: 104, z: 1,
      parents: ["sf4angel:core/seeds_of_life"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/a_balanced_sky_diet",
      title: "A Balanced Sky Diet",
      category: "optional",
      x: 40, y: 56, z: 14,
      parents: [],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/mob_factory_floor",
      title: "Mob Factory Floor",
      category: "optional",
      x: 153, y: 136, z: -44,
      parents: ["sf4angel:core/mob_rules"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/armored_to_the_teeth",
      title: "Armored to the Teeth",
      category: "optional",
      x: 30, y: 56, z: 10,
      parents: [],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/unbreakable_resolve",
      title: "Unbreakable Resolve",
      category: "optional",
      x: 53, y: 80, z: -8,
      parents: ["sf4angel:core/modifier_motive"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/undo_the_apocalypse",
      title: "Undo the Apocalypse",
      category: "optional",
      x: 135, y: 128, z: 7,
      parents: ["sf4angel:core/oh_the_irony", "sf4angel:core/diamond_clarity"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/pixel_perfect_masonry",
      title: "Pixel Perfect Masonry",
      category: "optional",
      x: 40, y: 64, z: 31,
      parents: ["sf4angel:core/starting_from_the_bottom"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/around_the_void_in_eighty_throws",
      title: "Around the Void in Eighty Throws",
      category: "optional",
      x: 55, y: 72, z: 3,
      parents: ["sf4angel:core/tooling_up"],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:optional/android_dreams",
      title: "Android Dreams of Electric Sheep",
      category: "optional",
      x: 26, y: 56, z: 0,
      parents: [],
      children: [],
      requiredStages: ["android"]
    },
    {
      id: "sf4angel:optional/robot_did_it",
      title: "Robot Did It",
      category: "optional",
      x: 30, y: 56, z: -10,
      parents: [],
      children: [],
      requiredStages: ["open_computers"]
    },
    {
      id: "sf4angel:optional/maximum_minimum_space",
      title: "Maximum Minimum Space",
      category: "optional",
      x: 40, y: 56, z: -14,
      parents: [],
      children: [],
      requiredStages: []
    },
    {
      id: "sf4angel:prestige/prestige_worldwide",
      title: "Prestige Worldwide",
      category: "prestige",
      x: 50, y: 56, z: -10,
      parents: [],
      children: ["sf4angel:prestige/time_is_a_flat_parabox"],
      requiredStages: ["parabox"]
    },
    {
      id: "sf4angel:prestige/equivalent_ambition_unlocked",
      title: "Equivalent Ambition Unlocked",
      category: "prestige",
      x: 49, y: 72, z: -37,
      parents: ["sf4angel:prestige/time_is_a_flat_parabox"],
      children: [],
      requiredStages: ["parabox", "project_e"]
    },
    {
      id: "sf4angel:prestige/aperture_unlocked",
      title: "Aperture Unlocked",
      category: "prestige",
      x: 54, y: 72, z: -52,
      parents: ["sf4angel:prestige/time_is_a_flat_parabox"],
      children: [],
      requiredStages: ["parabox", "portal_gun"]
    },
    {
      id: "sf4angel:prestige/written_in_another_age",
      title: "Written in Another Age",
      category: "prestige",
      x: 66, y: 72, z: -26,
      parents: ["sf4angel:prestige/time_is_a_flat_parabox"],
      children: [],
      requiredStages: ["parabox", "mystcraft"]
    },
    {
      id: "sf4angel:prestige/time_is_a_flat_parabox",
      title: "Time Is a Flat Parabox",
      category: "prestige",
      x: 53, y: 64, z: -26,
      parents: ["sf4angel:prestige/prestige_worldwide"],
      children: ["sf4angel:prestige/equivalent_ambition_unlocked", "sf4angel:prestige/aperture_unlocked", "sf4angel:prestige/written_in_another_age", "sf4angel:prestige/empowered_recursion"],
      requiredStages: ["parabox"]
    },
    {
      id: "sf4angel:prestige/empowered_recursion",
      title: "Empowered Recursion",
      category: "prestige",
      x: 62, y: 72, z: -49,
      parents: ["sf4angel:prestige/time_is_a_flat_parabox"],
      children: [],
      requiredStages: ["parabox", "parabox_two"]
    }
  ]
};
