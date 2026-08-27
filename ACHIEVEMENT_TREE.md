# SF4 Angel Achievement Tree

These diagrams cover all **110 core**, **13 optional**, and **6 prestige-only** achievements. Every arrow is an exact parent relationship from `ACHIEVEMENT_PLAN.md`; repeated pale nodes are cross-phase parents. Node labels omit only the common `sf4angel:` prefix.

## Complete overview

```mermaid
flowchart LR
    B["core/starting_from_the_bottom<br/>through core/orchard_on_autopilot<br/>20 core"]
    F["Storage, farming, and tools<br/>20 core"]
    P["Power and processing<br/>25 core"]
    D["Dimensions and bosses<br/>15 core"]
    L["Logic, storage, and industry<br/>20 core"]
    E["Endgame<br/>10 core"]
    O["Optional systems<br/>13 optional"]
    R["Prestige mode<br/>6 prestige-only"]

    B --> F
    B --> P
    F --> P
    P --> D
    B --> D
    P --> L
    D --> L
    L --> E
    D --> E
    P --> E
    F -. optional parents .-> O
    P -. optional parents .-> O
    D -. optional parents .-> O
    L -. optional parents .-> O
    E --> R
```

## Bootstrap and resource trees

```mermaid
flowchart TD
    C01["core/starting_from_the_bottom"] --> C02["core/that_is_dir_tree"]
    C02 --> C03["core/dirty_dancing"]
    C02 --> C04["core/captain_hook"]
    C04 --> C05["core/stone_tree_oath"]
    C05 --> C06["core/stone_from_trees"]
    C05 --> C07["core/gravel_travel"]
    C07 --> C08["core/beachfront_property"]
    C03 --> C09["core/clay_aiken"]
    C08 --> C09
    C09 --> C10["core/water_you_waiting_for"]
    C09 --> C11["core/clay_bucket_gamble"]
    C08 --> C12["core/coal_in_bloom"]
    C12 --> C13["core/ironwood"]
    C11 --> C13
    C13 --> C14["core/oh_the_irony"]
    C13 --> C15["core/red_tree_redemption"]
    C15 --> C16["core/trees_to_diamonds"]
    C13 --> C16
    C16 --> C17["core/diamond_clarity"]
    C14 --> C18["core/tiny_tree_big_plans"]
    C17 --> C18
    C18 --> C19["core/drop_it_like_its_hopping"]
    C19 --> C20["core/orchard_on_autopilot"]
```

## Storage, farming, and tools

```mermaid
flowchart TD
    C20["core/orchard_on_autopilot"]:::prior --> C21["core/paperwork_begins"]
    C20 --> C22["core/bulk_by_barrel"]
    C21 --> C23["core/network_attached_chest"]
    C22 --> C23
    C23 --> C24["core/remote_possibilities"]

    C14["core/oh_the_irony"]:::prior --> C25["core/market_forces"]
    C25 --> C26["core/seeds_of_life"]
    C26 --> C27["core/barnyard_beginnings"]
    C26 --> C28["core/hog_tied"]
    C28 --> C29["core/truffle_shuffle"]
    C08["core/beachfront_property"]:::prior --> C30["core/snad_together"]
    C14 --> C30
    C30 --> C31["core/cane_and_able"]

    C06["core/stone_from_trees"]:::prior --> C32["core/tooling_up"]
    C32 --> C33["core/parts_department"]
    C14 --> C34["core/melting_point"]
    C11["core/clay_bucket_gamble"]:::prior --> C34
    C34 --> C35["core/smeltery_authority"]
    C35 --> C36["core/cast_away"]
    C33 --> C37["core/forge_ahead"]
    C36 --> C37
    C37 --> C38["core/modifier_motive"]
    C38 --> C39["core/level_headed_tool"]
    C35 --> C40["core/armor_by_committee"]
    C37 --> C40

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Power and processing

```mermaid
flowchart TD
    C14["core/oh_the_irony"]:::prior --> C41["core/first_spark"]
    C15["core/red_tree_redemption"]:::prior --> C41
    C41 --> C42["core/battery_included"]
    C41 --> C43["core/redstone_in_a_box"]
    C43 --> C44["core/steel_yourself"]
    C43 --> C45["core/enriched_expectations"]
    C44 --> C45
    C45 --> C46["core/crush_depth"]
    C45 --> C47["core/triple_threat"]
    C46 --> C47
    C47 --> C48["core/five_times_the_charm"]
    C45 --> C49["core/hydrogen_economy"]
    C49 --> C50["core/gas_grass_or_rf"]
    C46 --> C50
    C50 --> C51["core/hdpeasy"]
    C44 --> C52["core/ultimate_capacity"]
    C42 --> C52
    C47 --> C53["core/factory_settings"]
    C53 --> C54["core/digital_prospector"]
    C52 --> C54

    C20["core/orchard_on_autopilot"]:::prior --> C55["core/latex_intentions"]
    C41 --> C55
    C55 --> C56["core/plastic_industry"]
    C56 --> C57["core/sow_automatic"]
    C57 --> C58["core/reap_automatic"]
    C56 --> C59["core/mob_rules"]
    C59 --> C60["core/black_hole_inventory"]
    C58 --> C60

    C41 --> C61["core/learn_deeply"]
    C61 --> C62["core/model_citizen"]
    C62 --> C63["core/data_with_experience"]
    C63 --> C64["core/simulation_theory"]
    C41 --> C64
    C64 --> C65["core/reality_armor"]

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Dimensions and bosses

```mermaid
flowchart TD
    C17["core/diamond_clarity"]:::prior --> C66["core/cake_to_hell"]
    C44["core/steel_yourself"]:::prior --> C66
    C66 --> C67["core/nether_say_never"]
    C67 --> C68["core/blaze_of_glory"]
    C68 --> C69["core/wither_or_not"]
    C65["core/reality_armor"]:::prior --> C69

    C59["core/mob_rules"]:::prior --> C70["core/the_hunting_trip"]
    C54["core/digital_prospector"]:::prior --> C71["core/lost_and_found"]
    C68 --> C72["core/cake_at_the_end"]
    C72 --> C73["core/the_void_blinks_back"]
    C73 --> C74["core/dragon_eviction_notice"]
    C65 --> C74
    C74 --> C75["core/borrowed_wings"]

    C17 --> C76["core/into_the_twilight"]
    C65 --> C76
    C76 --> C77["core/naga_have_i_ever"]
    C77 --> C78["core/lich_please"]
    C78 --> C79["core/hydra_expectations"]
    C79 --> C80["core/ice_queen_cometh"]

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Logic, storage, and advanced industry

```mermaid
flowchart TD
    C17["core/diamond_clarity"]:::prior --> C81["core/menril_state_of_mind"]
    C81 --> C82["core/logic_in_the_void"]
    C41["core/first_spark"]:::prior --> C82
    C82 --> C83["core/items_in_transit"]

    C81 --> C84["core/fluix_of_the_matter"]
    C67["core/nether_say_never"]:::prior --> C84
    C84 --> C85["core/pressing_engagement"]
    C71["core/lost_and_found"]:::prior --> C85
    C85 --> C86["core/acceptable_energy"]
    C52["core/ultimate_capacity"]:::prior --> C86
    C86 --> C87["core/me_myself_and_i"]
    C83 --> C87
    C87 --> C88["core/sixty_four_k_and_counting"]
    C88 --> C89["core/autocraft_authority"]

    C44["core/steel_yourself"]:::prior --> C90["core/manufactory_warranty_void"]
    C41 --> C90
    C90 --> C91["core/alloyed_allegiance"]
    C91 --> C92["core/positive_fission"]
    C71 --> C92
    C92 --> C93["core/gone_fission"]
    C93 --> C94["core/deuterium_duty"]
    C49["core/hydrogen_economy"]:::prior --> C94
    C94 --> C95["core/tritium_triumph"]
    C95 --> C96["core/fusion_cuisine"]
    C96 --> C97["core/pocket_star"]

    C71 --> C98["core/matter_of_analysis"]
    C41 --> C98
    C98 --> C99["core/decompose_yourself"]
    C99 --> C100["core/replication_nation"]
    C52 --> C100

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Endgame

```mermaid
flowchart TD
    C52["core/ultimate_capacity"]:::prior --> C101["core/matrix_reloaded"]
    C89["core/autocraft_authority"]:::prior --> C101
    C101 --> C102["core/turbine_service"]
    C97["core/pocket_star"]:::prior --> C102

    C89 --> C103["core/crafting_core_values"]
    C100["core/replication_nation"]:::prior --> C103
    C103 --> C104["core/quantum_compression"]
    C102 --> C104
    C104 --> C105["core/metals_into_points"]
    C105 --> C106["core/cookie_bacon_donut_collapse"]
    C106 --> C107["core/ultimate_singularity"]

    C60["core/black_hole_inventory"]:::prior --> C108["core/black_hole_filled"]
    C89 --> C108
    C21["core/paperwork_begins"]:::prior --> C109["core/million_item_paperwork"]
    C89 --> C109

    C80["core/ice_queen_cometh"]:::prior --> C110["core/the_sky_finally_claps"]
    C107 --> C110
    C108 --> C110
    C109 --> C110
    C102 --> C110
    C97 --> C110
    C100 --> C110
    C75["core/borrowed_wings"]:::prior --> C110
    C65["core/reality_armor"]:::prior --> C110

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Optional systems

```mermaid
flowchart LR
    C02["core/that_is_dir_tree"]:::prior --> O01["optional/sticky_keys"]
    C03["core/dirty_dancing"]:::prior --> O02["optional/nap_time"]
    C27["core/barnyard_beginnings"]:::prior --> O03["optional/milk_without_the_moo"]
    C25["core/market_forces"]:::prior --> O04["optional/a_balanced_sky_diet"]
    C59["core/mob_rules"]:::prior --> O05["optional/mob_factory_floor"]

    C40["core/armor_by_committee"]:::prior --> O06["optional/armored_to_the_teeth"]
    C38["core/modifier_motive"]:::prior --> O06
    C39["core/level_headed_tool"]:::prior --> O07["optional/unbreakable_resolve"]
    C52["core/ultimate_capacity"]:::prior --> O08["optional/undo_the_apocalypse"]
    C37["core/forge_ahead"]:::prior --> O09["optional/pixel_perfect_masonry"]
    C38 --> O10["optional/around_the_void_in_eighty_throws"]

    C98["core/matter_of_analysis"]:::prior --> O11["optional/android_dreams"]
    C82["core/logic_in_the_void"]:::prior --> O12["optional/robot_did_it"]
    C52 --> O12
    C71["core/lost_and_found"]:::prior --> O13["optional/maximum_minimum_space"]
    C52 --> O13

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```

## Prestige-only systems

```mermaid
flowchart TD
    C110["core/the_sky_finally_claps"]:::prior --> P01["prestige/prestige_worldwide"]
    P01 --> P02["prestige/equivalent_ambition_unlocked"]
    P01 --> P03["prestige/aperture_unlocked"]
    P01 --> P04["prestige/written_in_another_age"]
    P01 --> P05["prestige/time_is_a_flat_parabox"]
    P05 --> P06["prestige/empowered_recursion"]
    P02 --> P06
    P03 --> P06
    P04 --> P06

    classDef prior fill:#f4f4f4,stroke:#999,color:#555;
```
