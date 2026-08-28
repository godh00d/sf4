package com.godh00d.sf4angel.personality;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AchievementReactions {

    private static final Map<String, String> REACTIONS;

    static {
        Map<String, String> reactions = new LinkedHashMap<>();

        reactions.put("sf4angel:core/starting_from_the_bottom", "Rock bottom has a log now. Upward!");
        reactions.put("sf4angel:core/that_is_dir_tree", "A dirt sapling? Soil your expectations.");
        reactions.put("sf4angel:core/dirty_dancing", "Actual dirt! Nobody puts this block in a corner.");
        reactions.put("sf4angel:core/captain_hook", "Crook acquired. Captain status remains under review.");
        reactions.put("sf4angel:core/stone_tree_oath", "You swore on a petrified sapling. Very binding.");
        reactions.put("sf4angel:core/stone_from_trees", "Cobblestone from leaves. Geology has resigned.");
        reactions.put("sf4angel:core/gravel_travel", "That gravel sapling really gets around.");
        reactions.put("sf4angel:core/beachfront_property", "Sand tree secured. The voidfront view is free.");
        reactions.put("sf4angel:core/clay_aiken", "That clay sapling is invisible enough.");
        reactions.put("sf4angel:core/water_you_waiting_for", "Bucket filled. The waiting has evaporated.");
        reactions.put("sf4angel:core/clay_bucket_gamble", "The clay bucket fired. Your gamble held water.");
        reactions.put("sf4angel:core/coal_in_bloom", "Coal blossoms: black petals, bright future.");
        reactions.put("sf4angel:core/ironwood", "Ironwood achieved. Lumberjacks need magnets now.");
        reactions.put("sf4angel:core/oh_the_irony", "An iron ingot from a tree. Oh, the forestry.");
        reactions.put("sf4angel:core/red_tree_redemption", "The redstone sapling has a powerful character arc.");
        reactions.put("sf4angel:core/trees_to_diamonds", "A diamond sapling. De Beers fears your orchard.");
        reactions.put("sf4angel:core/diamond_clarity", "Diamond in hand. Your priorities are crystal clear.");
        reactions.put("sf4angel:core/tiny_tree_big_plans", "One tiny bonsai pot, one enormous spreadsheet.");
        reactions.put("sf4angel:core/drop_it_like_its_hopping", "Hopping bonsai: it drops so you do not have to.");
        reactions.put("sf4angel:core/orchard_on_autopilot", "The orchard harvested itself. Management suits you.");

        reactions.put("sf4angel:core/paperwork_begins", "Cabinet filed. The paperwork has taken root.");
        reactions.put("sf4angel:core/bulk_by_barrel", "A barrel of storage, without a barrel of laughs.");
        reactions.put("sf4angel:core/network_attached_chest", "Chest network online. Your clutter has bandwidth.");
        reactions.put("sf4angel:core/remote_possibilities", "Remote storage opened. Distance just lost custody.");
        reactions.put("sf4angel:core/market_forces", "Market built. Supply has met a suspiciously blocky demand.");
        reactions.put("sf4angel:core/seeds_of_life", "An animal seed. Evolution took the scenic route.");
        reactions.put("sf4angel:core/barnyard_beginnings", "A baby animal! The barnyard has begun negotiations.");
        reactions.put("sf4angel:core/hog_tied", "Resource Hog acquired. Commodity futures now have snouts.");
        reactions.put("sf4angel:core/truffle_shuffle", "One truffle dug. That hog knows the underground market.");
        reactions.put("sf4angel:core/snad_together", "Snad placed. Sand has entered its rebellious phase.");
        reactions.put("sf4angel:core/cane_and_able", "Cane on Snad. Agriculture is moving unnaturally fast.");
        reactions.put("sf4angel:core/tooling_up", "Tool station ready. Your hands may file for retirement.");
        reactions.put("sf4angel:core/parts_department", "Part builder acquired. Assembly required, dignity optional.");
        reactions.put("sf4angel:core/melting_point", "Melter built. You have reached a constructive breakdown.");
        reactions.put("sf4angel:core/smeltery_authority", "Smeltery validated. You may now cast judgment.");
        reactions.put("sf4angel:core/cast_away", "Metal cast secured. Wilson was not required.");
        reactions.put("sf4angel:core/forge_ahead", "Tool forge complete. Go ahead, it is in the name.");
        reactions.put("sf4angel:core/modifier_motive", "Tool modified. The motive was clearly improvement.");
        reactions.put("sf4angel:core/level_headed_tool", "Your tool leveled up without letting it go to its head.");
        reactions.put("sf4angel:core/armor_by_committee", "Armor forge assembled. The committee votes: less stabbing.");

        reactions.put("sf4angel:core/first_spark", "Generator acquired. A small block for power, a giant utility bill.");
        reactions.put("sf4angel:core/battery_included", "Energy Tablet included. Batteries finally read the label.");
        reactions.put("sf4angel:core/redstone_in_a_box", "Metallurgic Infuser: redstone now comes boxed and angry.");
        reactions.put("sf4angel:core/steel_yourself", "Steel made. Consider yourself successfully steeled.");
        reactions.put("sf4angel:core/enriched_expectations", "Enrichment Chamber built. Expectations are now concentrated.");
        reactions.put("sf4angel:core/crush_depth", "Crusher online. That machine has deep-seated pressure issues.");
        reactions.put("sf4angel:core/triple_threat", "Ore tripled. Arithmetic has become industrial.");
        reactions.put("sf4angel:core/five_times_the_charm", "Fivefold ore. The fifth time was extremely charming.");
        reactions.put("sf4angel:core/hydrogen_economy", "Water split. Hydrogen and oxygen cite irreconcilable chemistry.");
        reactions.put("sf4angel:core/gas_grass_or_rf", "Ethylene burned for RF. The grass chose gas.");
        reactions.put("sf4angel:core/hdpeasy", "HDPE made. Plastic chemistry played on easy mode.");
        reactions.put("sf4angel:core/ultimate_capacity", "Ultimate Energy Cube secured. Power has room to overthink.");
        reactions.put("sf4angel:core/factory_settings", "Factory installed. Default setting: relentlessly productive.");
        reactions.put("sf4angel:core/digital_prospector", "Digital Miner exported ore. The gold rush has a user interface.");
        reactions.put("sf4angel:core/latex_intentions", "Latex extracted. That log had elastic ambitions.");
        reactions.put("sf4angel:core/plastic_industry", "Plastic produced. The industry is no longer a stretch.");
        reactions.put("sf4angel:core/sow_automatic", "The sower planted alone. Seeds now have automated delivery.");
        reactions.put("sf4angel:core/reap_automatic", "The harvester reaped alone. Grim efficiency, cheerful crops.");
        reactions.put("sf4angel:core/mob_rules", "Mob crusher confirmed. First rule: mobs lose.");
        reactions.put("sf4angel:core/black_hole_inventory", "Black Hole Unit acquired. Storage has crossed the event horizon.");
        reactions.put("sf4angel:core/learn_deeply", "Deep Learner acquired. Please study beyond bedrock.");
        reactions.put("sf4angel:core/model_citizen", "Mob model made. An exemplary citizen, statistically speaking.");
        reactions.put("sf4angel:core/data_with_experience", "Model reached Basic. Its resume now includes field data.");
        reactions.put("sf4angel:core/simulation_theory", "Simulation complete. Reality remains cautiously peer-reviewed.");
        reactions.put("sf4angel:core/reality_armor", "Glitch armor equipped. Reality has failed its dress code.");

        reactions.put("sf4angel:core/cake_to_hell", "Nether cake baked. Dessert has never looked so infernal.");
        reactions.put("sf4angel:core/nether_say_never", "Nether entered. Never has officially been rescheduled.");
        reactions.put("sf4angel:core/blaze_of_glory", "Blaze rod claimed. Glory is surprisingly flammable.");
        reactions.put("sf4angel:core/wither_or_not", "Wither defeated. The answer was emphatically not.");
        reactions.put("sf4angel:core/the_hunting_trip", "Hunting Dimension reached. Pack snacks; unpack weapons.");
        reactions.put("sf4angel:core/lost_and_found", "Lost Cities found. Their naming department is devastated.");
        reactions.put("sf4angel:core/cake_at_the_end", "End cake ready. Save room for the final course.");
        reactions.put("sf4angel:core/the_void_blinks_back", "The End stared back, then blinked first.");
        reactions.put("sf4angel:core/dragon_eviction_notice", "Dragon evicted. The End lease had a fatal clause.");
        reactions.put("sf4angel:core/borrowed_wings", "Elytra acquired. Those wings are on indefinite loan.");
        reactions.put("sf4angel:core/into_the_twilight", "Twilight entered. Day and night have agreed to share custody.");
        reactions.put("sf4angel:core/naga_have_i_ever", "Naga slain. Never have you ever answered so sharply.");
        reactions.put("sf4angel:core/lich_please", "Lich defeated. Please was apparently the magic word.");
        reactions.put("sf4angel:core/hydra_expectations", "Hydra down. Its headcount exceeded expectations.");
        reactions.put("sf4angel:core/ice_queen_cometh", "Snow Queen dethroned. Winter received a cold dismissal.");

        reactions.put("sf4angel:core/menril_state_of_mind", "Menril crystal found. Logic is beginning to crystallize.");
        reactions.put("sf4angel:core/logic_in_the_void", "Logic network active. Even the void accepts variables.");
        reactions.put("sf4angel:core/items_in_transit", "Item exported by tunnel. Your inventory has public transport.");
        reactions.put("sf4angel:core/fluix_of_the_matter", "Fluix made. Matter has entered a charged state of mind.");
        reactions.put("sf4angel:core/pressing_engagement", "All four presses gathered. This engagement is official.");
        reactions.put("sf4angel:core/acceptable_energy", "AE accepted. The network found your energy acceptable.");
        reactions.put("sf4angel:core/me_myself_and_i", "ME Terminal transacted. Myself and I approve the network.");
        reactions.put("sf4angel:core/sixty_four_k_and_counting", "64K cell secured. Counting bytes beats counting chests.");
        reactions.put("sf4angel:core/autocraft_authority", "Autocraft completed. You delegated with molecular precision.");
        reactions.put("sf4angel:core/manufactory_warranty_void", "Manufactory ran. The warranty vanished exactly as forecast.");
        reactions.put("sf4angel:core/alloyed_allegiance", "Alloy forged. Those metals have pledged a stronger union.");
        reactions.put("sf4angel:core/positive_fission", "Fission reactor assembled. A positively splitting achievement.");
        reactions.put("sf4angel:core/gone_fission", "Fission power exported. The atoms have gone productive.");
        reactions.put("sf4angel:core/deuterium_duty", "A bucket of deuterium. Heavy water, light work.");
        reactions.put("sf4angel:core/tritium_triumph", "Tritium tanked. Three cheers for radioactive hydrogen.");
        reactions.put("sf4angel:core/fusion_cuisine", "Fusion reactor assembled. The stars sent their recipe.");
        reactions.put("sf4angel:core/pocket_star", "Fusion power exported. Please keep your pocket star ventilated.");
        reactions.put("sf4angel:core/matter_of_analysis", "Matter pattern analyzed. The item has been thoroughly profiled.");
        reactions.put("sf4angel:core/decompose_yourself", "Item decomposed. Matter took that breakup constructively.");
        reactions.put("sf4angel:core/replication_nation", "Item replicated. Originality is now a scarce resource.");

        reactions.put("sf4angel:core/matrix_reloaded", "Induction Matrix complete. Capacity has been reloaded.");
        reactions.put("sf4angel:core/turbine_service", "Turbine exported power. Steam has entered public service.");
        reactions.put("sf4angel:core/crafting_core_values", "Crafting Core acquired. Its values are nine-by-nine.");
        reactions.put("sf4angel:core/quantum_compression", "Quantum compression complete. Big ambition, tiny footprint.");
        reactions.put("sf4angel:core/metals_into_points", "Metal singularity made. Every ingot had a point after all.");
        reactions.put("sf4angel:core/cookie_bacon_donut_collapse", "Cookie, bacon, donut: breakfast collapsed into infinity.");
        reactions.put("sf4angel:core/ultimate_singularity", "Ultimate Singularity acquired. Everything led to this point.");
        reactions.put("sf4angel:core/black_hole_filled", "Black Hole Unit filled. Even infinity has cupboard limits.");
        reactions.put("sf4angel:core/million_item_paperwork", "One million items filed. Bureaucracy has achieved density.");
        reactions.put("sf4angel:core/the_sky_finally_claps", "The whole sky applauds. Mind the thunderous standing ovation.");

        reactions.put("sf4angel:optional/sticky_keys", "Twenty sapling squats. Sticky Keys wants a word.");
        reactions.put("sf4angel:optional/nap_time", "You slept until morning. Even heroes need scheduled downtime.");
        reactions.put("sf4angel:optional/milk_without_the_moo", "Cow Jar made milk. The moo was successfully outsourced.");
        reactions.put("sf4angel:optional/a_balanced_sky_diet", "Twenty foods eaten. Your sky diet passed the taste audit.");
        reactions.put("sf4angel:optional/mob_factory_floor", "One hundred factory kills. The mob floor needs a mop.");
        reactions.put("sf4angel:optional/armored_to_the_teeth", "Four modified armor pieces. Even your teeth feel underdressed.");
        reactions.put("sf4angel:optional/unbreakable_resolve", "Unbreakable tool confirmed. Durability has lost the argument.");
        reactions.put("sf4angel:optional/undo_the_apocalypse", "Ten thousand blocks placed, then 64 unmade. Apocalypse edited.");
        reactions.put("sf4angel:optional/pixel_perfect_masonry", "A 4,096-bit block. Masonry has achieved pixel perfection.");
        reactions.put("sf4angel:optional/around_the_void_in_eighty_throws", "Forty-block yo-yo kill. The void got strung along.");
        reactions.put("sf4angel:optional/android_dreams", "Android conversion stable. Electric sheep remain optional.");
        reactions.put("sf4angel:optional/robot_did_it", "Robot broke and placed a block. Plausible deniability installed.");
        reactions.put("sf4angel:optional/maximum_minimum_space", "Maximum machine entered. Minimum space, maximum contradiction.");

        reactions.put("sf4angel:prestige/prestige_worldwide", "First Prestige point earned. Your reputation has gone global.");
        reactions.put("sf4angel:prestige/equivalent_ambition_unlocked", "Transmutation unlocked. Ambition now has an exchange rate.");
        reactions.put("sf4angel:prestige/aperture_unlocked", "Linked portals opened. Your shortcuts now have apertures.");
        reactions.put("sf4angel:prestige/written_in_another_age", "A new Age written and entered. Excellent worldbuilding.");
        reactions.put("sf4angel:prestige/time_is_a_flat_parabox", "Parabox rolled time back. The point still stands.");
        reactions.put("sf4angel:prestige/empowered_recursion", "Ten points banked in one loop. Recursion paid dividends.");

        REACTIONS = Collections.unmodifiableMap(reactions);
    }

    private AchievementReactions() {
    }

    public static boolean contains(String achievementId) {
        return REACTIONS.containsKey(achievementId);
    }

    public static String get(String achievementId) {
        return REACTIONS.get(achievementId);
    }
}
