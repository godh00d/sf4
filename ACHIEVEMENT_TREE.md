# SF4 Angel Achievement Tree

These diagrams cover all **110 core**, **13 optional**, and **6 prestige-only** achievements. Every arrow is an achievement prerequisite from `ACHIEVEMENT_PLAN.md`. Page-root display attachment for achievements with no prerequisites is intentionally omitted because it is not a completion dependency.

## Early core

```mermaid
flowchart TD
    C01["core/starting_from_the_bottom"]
    C01 --> C02["core/that_is_dir_tree"]
    C02 --> C03["core/dirty_dancing"]
    C01 --> C04["core/captain_hook"]
    C02 --> C05["core/stone_tree_oath"]
    C05 --> C06["core/stone_from_trees"]
    C05 --> C07["core/gravel_travel"]
    C05 --> C08["core/beachfront_property"]
    C05 --> C09["core/clay_aiken"]
    C14 --> C10["core/water_you_waiting_for"]
    C09 --> C11["core/clay_bucket_gamble"]
    C08 --> C12["core/coal_in_bloom"]
    C12 --> C13["core/ironwood"]
    C09 --> C13
    C07 --> C13
    C13 --> C14["core/oh_the_irony"]
    C13 --> C15["core/red_tree_redemption"]
    C15 --> C16["core/trees_to_diamonds"]
    C16 --> C17["core/diamond_clarity"]
    C09 --> C18["core/tiny_tree_big_plans"]
    C18 --> C19["core/drop_it_like_its_hopping"]
    C14 --> C19
    C19 --> C20["core/orchard_on_autopilot"]

    C14 --> C21["core/paperwork_begins"]
    C01 --> C22["core/bulk_by_barrel"]
    C17 --> C23["core/network_attached_chest"]
    C23 --> C24["core/remote_possibilities"]
    C09 --> C25["core/market_forces"]
    C25 --> C26["core/seeds_of_life"]
    C26 --> C27["core/barnyard_beginnings"]
    C10 --> C28["core/hog_tied"]
    C03 --> C28
    C28 --> C29["core/truffle_shuffle"]
    C26 --> C29
    C08 --> C30["core/snad_together"]
    C30 --> C31["core/cane_and_able"]
    C01 --> C32["core/tooling_up"]
    C01 --> C33["core/parts_department"]
    C07 --> C34["core/melting_point"]
    C08 --> C34
    C09 --> C34
    C07 --> C35["core/smeltery_authority"]
    C08 --> C35
    C11 --> C35
    C14 --> C36["core/cast_away"]
    C32 --> C37["core/forge_ahead"]
    C14 --> C37
    C32 --> C38["core/modifier_motive"]
    C33 --> C38
    C32 --> C39["core/level_headed_tool"]
    C33 --> C39
    C14 --> C40["core/armor_by_committee"]
```

## Technology

```mermaid
flowchart TD
    C14["core/oh_the_irony"] --> C41["core/first_spark"]
    C15["core/red_tree_redemption"] --> C41
    C43 --> C42["core/battery_included"]
    C35["core/smeltery_authority"] --> C43["core/redstone_in_a_box"]
    C17["core/diamond_clarity"] --> C43
    C43 --> C44["core/steel_yourself"]
    C41 --> C44
    C44 --> C45["core/enriched_expectations"]
    C44 --> C46["core/crush_depth"]
    C45 --> C47["core/triple_threat"]
    C46 --> C47
    C49 --> C47
    C47 --> C48["core/five_times_the_charm"]
    C43 --> C49["core/hydrogen_economy"]
    C41 --> C49
    C49 --> C50["core/gas_grass_or_rf"]
    C46 --> C50
    C50 --> C51["core/hdpeasy"]
    C46 --> C52["core/ultimate_capacity"]
    C42 --> C52
    C44 --> C53["core/factory_settings"]
    C46 --> C54["core/digital_prospector"]
    C41 --> C54
    C14 --> C55["core/latex_intentions"]
    C15 --> C55
    C55 --> C56["core/plastic_industry"]
    C41 --> C56
    C56 --> C57["core/sow_automatic"]
    C56 --> C58["core/reap_automatic"]
    C56 --> C59["core/mob_rules"]
    C56 --> C60["core/black_hole_inventory"]
    C12["core/coal_in_bloom"] --> C61["core/learn_deeply"]
    C15 --> C61
    C11["core/clay_bucket_gamble"] --> C61
    C10["core/water_you_waiting_for"] --> C61
    C15 --> C62["core/model_citizen"]
    C10 --> C62
    C62 --> C63["core/data_with_experience"]
    C63 --> C64["core/simulation_theory"]
    C41 --> C64
    C63 --> C65["core/reality_armor"]
    C17 --> C65
    C10 --> C65
```

## Midgame

```mermaid
flowchart TD
    C17["core/diamond_clarity"] --> C66["core/cake_to_hell"]
    C66 --> C67["core/nether_say_never"]
    C67 --> C68["core/blaze_of_glory"]
    C68 --> C69["core/wither_or_not"]
    C27["core/barnyard_beginnings"] --> C70["core/the_hunting_trip"]
    C68 --> C71["core/lost_and_found"]
    C17 --> C71
    C68 --> C72["core/cake_at_the_end"]
    C72 --> C73["core/the_void_blinks_back"]
    C73 --> C74["core/dragon_eviction_notice"]
    C74 --> C75["core/borrowed_wings"]
    C76["core/into_the_twilight"]
    C76 --> C77["core/naga_have_i_ever"]
    C77 --> C78["core/lich_please"]
    C78 --> C79["core/hydra_expectations"]
    C78 --> C80["core/ice_queen_cometh"]

    C10["core/water_you_waiting_for"] --> C81["core/menril_state_of_mind"]
    C81 --> C82["core/logic_in_the_void"]
    C82 --> C83["core/items_in_transit"]
    C81 --> C84["core/fluix_of_the_matter"]
    C43["core/redstone_in_a_box"] --> C84
    C36["core/cast_away"] --> C85["core/pressing_engagement"]
    C91 --> C85
    C71 --> C85
    C84 --> C86["core/acceptable_energy"]
    C86 --> C87["core/me_myself_and_i"]
    C85 --> C87
    C87 --> C88["core/sixty_four_k_and_counting"]
    C88 --> C89["core/autocraft_authority"]
    C44["core/steel_yourself"] --> C90["core/manufactory_warranty_void"]
    C41["core/first_spark"] --> C90
    C90 --> C91["core/alloyed_allegiance"]
    C91 --> C92["core/positive_fission"]
    C92 --> C93["core/gone_fission"]
    C71 --> C93
    C91 --> C94["core/deuterium_duty"]
    C91 --> C95["core/tritium_triumph"]
    C94 --> C96["core/fusion_cuisine"]
    C95 --> C96
    C96 --> C97["core/pocket_star"]
    C71 --> C98["core/matter_of_analysis"]
    C41 --> C98
    C71 --> C99["core/decompose_yourself"]
    C41 --> C99
    C98 --> C100["core/replication_nation"]
    C99 --> C100
```

## Endgame

```mermaid
flowchart TD
    C44["core/steel_yourself"] --> C101["core/matrix_reloaded"]
    C44 --> C102["core/turbine_service"]
    C14["core/oh_the_irony"] --> C103["core/crafting_core_values"]
    C15["core/red_tree_redemption"] --> C103
    C14 --> C104["core/quantum_compression"]
    C15 --> C104
    C104 --> C105["core/metals_into_points"]
    C104 --> C106["core/cookie_bacon_donut_collapse"]
    C105 --> C107["core/ultimate_singularity"]
    C106 --> C107
    C60["core/black_hole_inventory"] --> C108["core/black_hole_filled"]
    C21["core/paperwork_begins"] --> C109["core/million_item_paperwork"]
    C80["core/ice_queen_cometh"] --> C110["core/the_sky_finally_claps"]
    C107 --> C110
    C108 --> C110
    C109 --> C110
    C102 --> C110
    C97["core/pocket_star"] --> C110
    C100["core/replication_nation"] --> C110
    C75["core/borrowed_wings"] --> C110
    C65["core/reality_armor"] --> C110
```

## Optional

```mermaid
flowchart TD
    O01["optional/sticky_keys"]
    C01["core/starting_from_the_bottom"] --> O02["optional/nap_time"]
    C26["core/seeds_of_life"] --> O03["optional/milk_without_the_moo"]
    O04["optional/a_balanced_sky_diet"]
    C59["core/mob_rules"] --> O05["optional/mob_factory_floor"]
    O06["optional/armored_to_the_teeth"]
    C38["core/modifier_motive"] --> O07["optional/unbreakable_resolve"]
    C14["core/oh_the_irony"] --> O08["optional/undo_the_apocalypse"]
    C17["core/diamond_clarity"] --> O08
    C01 --> O09["optional/pixel_perfect_masonry"]
    C32["core/tooling_up"] --> O10["optional/around_the_void_in_eighty_throws"]
    O11["optional/android_dreams"]
    O12["optional/robot_did_it"]
    O13["optional/maximum_minimum_space"]
```

## Prestige

```mermaid
flowchart TD
    P01["prestige/prestige_worldwide"]
    P01 --> P05["prestige/time_is_a_flat_parabox"]
    P05 --> P02["prestige/equivalent_ambition_unlocked"]
    P05 --> P03["prestige/aperture_unlocked"]
    P05 --> P04["prestige/written_in_another_age"]
    P05 --> P06["prestige/empowered_recursion"]
```
