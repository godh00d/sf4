# SF4 Angel Achievement Plan

This is the implementation contract for the reduced 129-achievement catalog: **110 core**, **13 optional**, and **6 prestige-only**. IDs are stable and must be used unchanged by Triumph, Java grants, localization, and the angel progression map.

## Design and evidence rules

- Pace rewards around gameplay systems, not recipe steps. Do not add ingredient/output pairs unless acquiring the ingredient and producing the output prove separate gameplay systems.
- Do not create arbitrary two-minute reward bursts. A player should normally configure, operate, travel, fight, or automate between adjacent grants.
- A parent controls visibility/progression only. The measurable trigger must also be satisfied.
- Registry names, metadata, NBT discriminators, and integration anchors were validated against the installed pack and are recorded in `REGISTRY_MANIFEST.md`.
- Confirmed bonsai evidence: `bonsaitrees:bonsaipot` metadata `0` is the Bonsai Pot and metadata `1` is the Hopping Bonsai Pot.
- Recipe audit evidence makes the Bonsai Pot depend on Clay Aiken, the Hopping Bonsai Pot depend on both the pot and iron acquisition, the Energy Tablet depend on the Metallurgic Infuser, and the Ultimate Energy Cube depend on the Crusher and Energy Tablet. The parent tables below are the exact audited dependency map rather than a narrative progression order.
- Deep Mob Learning's installed pacifist recipes create concrete models at NBT `tier:1`; Basic-tier detection therefore checks current carried state and does not require a tier-zero kill transition.
- Achievements with `[]` prerequisites attach to their page root only for display. Page-root attachment is not an achievement prerequisite and does not call `setRequiresParents()`.
- Canonical Minecraft 1.12.2 targets shown as `minecraft:*` may be used directly. No other mod item ID in this document is claimed as confirmed.

## Implementation types

| Type | Implementation contract |
| --- | --- |
| `T-ITEM` | Triumph inventory criterion for the named item and metadata; grant on the first inventory state satisfying the trigger. |
| `T-ANY` | Multiple exact Triumph inventory criteria with `setRequirements("any")`; grant when any listed alternative is present. |
| `T-ADV` | Triumph criterion requiring all named parent advancements; used only when parent completion is itself the trigger. |
| `T-LOCATION` | Triumph location/dimension criterion, after validating the dimension identifier in the runtime pack. |
| `J-EVENT` | Forge event or persisted player counter; grant once the exact event/count condition is true. |
| `J-INTEGRATION` | Dedicated mod/tile/capability/NBT integration. It must observe the stated operation or state, not an inventory proxy. |
| `J-COMPOSITE` | Java grant after all stated advancement parents and any additional stated condition are true. |

`[]` in the Parent IDs column means no achievement parent. Multiple parents are an AND requirement.

## Core catalog (110)

### 1. Bootstrap and resource trees (20)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/starting_from_the_bottom` | Starting from the Bottom | First inventory acquisition of at least 1 `minecraft:log` (any metadata). | `[]` | `T-ITEM` |
| `sf4angel:core/that_is_dir_tree` | That's Dir-tree | First inventory acquisition of at least 1 `sky_orchards:sapling_dirt` metadata `0`. | `sf4angel:core/starting_from_the_bottom` | `T-ITEM` |
| `sf4angel:core/dirty_dancing` | Dirty Dancing | First inventory acquisition of at least 1 `minecraft:dirt` metadata `0`. | `sf4angel:core/that_is_dir_tree` | `T-ITEM` |
| `sf4angel:core/captain_hook` | Captain Hook | First inventory acquisition of at least 1 `inspirations:wooden_crook` metadata `0`. | `sf4angel:core/starting_from_the_bottom` | `T-ITEM` |
| `sf4angel:core/stone_tree_oath` | Stone Tree Oath | First inventory acquisition of at least 1 `sky_orchards:sapling_petrified` metadata `0`. | `sf4angel:core/that_is_dir_tree` | `T-ITEM` |
| `sf4angel:core/stone_from_trees` | Stone from Trees | First inventory acquisition of at least 1 `minecraft:cobblestone`. | `sf4angel:core/stone_tree_oath` | `T-ITEM` |
| `sf4angel:core/gravel_travel` | Gravel Travel | First inventory acquisition of at least 1 `sky_orchards:sapling_gravel` metadata `0`. | `sf4angel:core/stone_tree_oath` | `T-ITEM` |
| `sf4angel:core/beachfront_property` | Beachfront Property | First inventory acquisition of at least 1 `sky_orchards:sapling_sand` metadata `0`. | `sf4angel:core/stone_tree_oath` | `T-ITEM` |
| `sf4angel:core/clay_aiken` | Clay Aiken | First inventory acquisition of at least 1 `sky_orchards:sapling_clay` metadata `0`. | `sf4angel:core/stone_tree_oath` | `T-ITEM` |
| `sf4angel:core/water_you_waiting_for` | Water You Waiting For? | First inventory acquisition of at least 1 `minecraft:water_bucket`. | `sf4angel:core/oh_the_irony` | `T-ITEM` |
| `sf4angel:core/clay_bucket_gamble` | Clay Bucket Gamble | First inventory acquisition of at least 1 fired `ceramics:clay_bucket` metadata `0`; an empty bucket needs no NBT and a lava-filled bucket has `{fluids:{FluidName:"lava",Amount:1000}}`. | `sf4angel:core/clay_aiken` | `T-ITEM` |
| `sf4angel:core/coal_in_bloom` | Coal in Bloom | First inventory acquisition of at least 1 `sky_orchards:sapling_coal` metadata `0`. | `sf4angel:core/beachfront_property` | `T-ITEM` |
| `sf4angel:core/ironwood` | Ironwood | First inventory acquisition of at least 1 `sky_orchards:sapling_iron` metadata `0`. | `sf4angel:core/coal_in_bloom`, `sf4angel:core/clay_aiken`, `sf4angel:core/gravel_travel` | `T-ITEM` |
| `sf4angel:core/oh_the_irony` | Oh, the Irony | First inventory acquisition of at least 1 `minecraft:iron_ingot`. | `sf4angel:core/ironwood` | `T-ITEM` |
| `sf4angel:core/red_tree_redemption` | Red Tree Redemption | First inventory acquisition of at least 1 `sky_orchards:sapling_redstone` metadata `0`. | `sf4angel:core/ironwood` | `T-ITEM` |
| `sf4angel:core/trees_to_diamonds` | Trees to Diamonds | First inventory acquisition of at least 1 `sky_orchards:sapling_diamond` metadata `0`. | `sf4angel:core/red_tree_redemption` | `T-ITEM` |
| `sf4angel:core/diamond_clarity` | Diamond Clarity | First inventory acquisition of at least 1 `minecraft:diamond`. | `sf4angel:core/trees_to_diamonds` | `T-ITEM` |
| `sf4angel:core/tiny_tree_big_plans` | Tiny Tree, Big Plans | First inventory acquisition of `bonsaitrees:bonsaipot` metadata `0`. | `sf4angel:core/clay_aiken` | `T-ITEM` |
| `sf4angel:core/drop_it_like_its_hopping` | Drop It Like It's Hopping | First inventory acquisition of `bonsaitrees:bonsaipot` metadata `1`. | `sf4angel:core/tiny_tree_big_plans`, `sf4angel:core/oh_the_irony` | `T-ITEM` |
| `sf4angel:core/orchard_on_autopilot` | Orchard on Autopilot | A player-owned Hopping Bonsai Pot completes one growth cycle and inserts at least 1 harvested output into an adjacent inventory. | `sf4angel:core/drop_it_like_its_hopping` | `J-INTEGRATION` |

### 2. Storage, farming, and tools (20)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/paperwork_begins` | Paperwork Begins | First inventory acquisition of at least 1 `realfilingcabinet:modelcabinet` metadata `0`. | `sf4angel:core/oh_the_irony` | `T-ITEM` |
| `sf4angel:core/bulk_by_barrel` | Bulk by Barrel | First inventory acquisition of at least 1 `bdsandm:wood_barrel` metadata `0`. | `sf4angel:core/starting_from_the_bottom` | `T-ITEM` |
| `sf4angel:core/network_attached_chest` | Network Attached Chest | A `storagenetwork:master` metadata `0` is connected to at least one inventory by `storagenetwork:storage_kabel` and reports that inventory to the owning player. | `sf4angel:core/diamond_clarity` | `J-INTEGRATION` |
| `sf4angel:core/remote_possibilities` | Remote Possibilities | The player successfully opens that Simple Storage Network through `storagenetwork:remote` metadata `0`, `1`, `2`, or `3` while at least 16 blocks from its controller. | `sf4angel:core/network_attached_chest` | `J-INTEGRATION` |
| `sf4angel:core/market_forces` | Market Forces | First inventory acquisition of at least 1 `farmingforblockheads:market` metadata `0`. | `sf4angel:core/clay_aiken` | `T-ITEM` |
| `sf4angel:core/seeds_of_life` | Seeds of Life | First inventory acquisition of at least 1 `animalcrops:seeds` metadata `0` with a nonempty string NBT key `entity`. | `sf4angel:core/market_forces` | `J-INTEGRATION` |
| `sf4angel:core/barnyard_beginnings` | Barnyard Beginnings | The player causes one successful `BabyEntitySpawnEvent` for two non-hostile adult animals. | `sf4angel:core/seeds_of_life` | `J-EVENT` |
| `sf4angel:core/hog_tied` | Hog Tied | First inventory acquisition of at least 1 `resourcehogs:mud_bucket` metadata `0` with a nonempty string NBT key `ResourceType`. | `sf4angel:core/water_you_waiting_for`, `sf4angel:core/dirty_dancing` | `J-INTEGRATION` |
| `sf4angel:core/truffle_shuffle` | Truffle Shuffle | A Resource Hog owned or bred by the player produces one truffle through its normal digging/production mechanic. | `sf4angel:core/hog_tied`, `sf4angel:core/seeds_of_life` | `J-INTEGRATION` |
| `sf4angel:core/snad_together` | Snad Together | First inventory acquisition of at least 1 `snad:snad` metadata `0`. | `sf4angel:core/beachfront_property` | `T-ITEM` |
| `sf4angel:core/cane_and_able` | Cane and Able | The player places `minecraft:reeds` on top of `snad:snad` metadata `0`; both blocks remain in that arrangement after the placement event. | `sf4angel:core/snad_together` | `J-EVENT` |
| `sf4angel:core/tooling_up` | Tooling Up | First inventory acquisition of at least 1 `tconstruct:tooltables` metadata `3`. | `sf4angel:core/starting_from_the_bottom` | `T-ITEM` |
| `sf4angel:core/parts_department` | Parts Department | First inventory acquisition of at least 1 `tconstruct:tooltables` metadata `2`. | `sf4angel:core/starting_from_the_bottom` | `T-ITEM` |
| `sf4angel:core/melting_point` | Melting Point | First inventory acquisition of at least 1 `tcomplement:melter` metadata `0`. | `sf4angel:core/gravel_travel`, `sf4angel:core/beachfront_property`, `sf4angel:core/clay_aiken` | `T-ITEM` |
| `sf4angel:core/smeltery_authority` | Smeltery Authority | The player completes a valid Tinkers' Construct smeltery multiblock with an active controller, at least one tank, one drain, and an internal capacity of at least 18 ingots. | `sf4angel:core/gravel_travel`, `sf4angel:core/beachfront_property`, `sf4angel:core/clay_bucket_gamble` | `J-INTEGRATION` |
| `sf4angel:core/cast_away` | Cast Away | First inventory acquisition of at least 1 reusable metal `tconstruct:cast` metadata `0` or `tconstruct:cast_custom` metadata `0`-`4`; integration must reject clay casts and blank casts and inspect the Tinkers material/cast state. | `sf4angel:core/oh_the_irony` | `J-INTEGRATION` |
| `sf4angel:core/forge_ahead` | Forge Ahead | First inventory acquisition of at least 1 `tconstruct:toolforge` metadata `0`. | `sf4angel:core/tooling_up`, `sf4angel:core/oh_the_irony` | `T-ITEM` |
| `sf4angel:core/modifier_motive` | Modifier Motive | The player removes a Tinkers' tool from a tool-modification inventory after its modifier list has gained at least one modifier during that interaction. | `sf4angel:core/tooling_up`, `sf4angel:core/parts_department` | `J-INTEGRATION` |
| `sf4angel:core/level_headed_tool` | Level-Headed Tool | A Tinkers' tool held by the player increases its Tinker Tool Leveling level at least once. | `sf4angel:core/tooling_up`, `sf4angel:core/parts_department` | `J-INTEGRATION` |
| `sf4angel:core/armor_by_committee` | Armor by Committee | First inventory acquisition of at least 1 `conarm:armorforge` metadata `0`. | `sf4angel:core/oh_the_irony` | `T-ITEM` |

### 3. Power and processing (25)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/first_spark` | First Spark | First inventory acquisition of at least 1 metadata `0` block from the confirmed Simple Generators set: `simplegenerators:combustion_simple`, `culinary_simple`, `ender_simple`, `nether_simple`, `soul_simple`, `geothermal_simple`, `fluid_combustion_simple`, or `turbine_simple` (each under the `simplegenerators` namespace). | `sf4angel:core/oh_the_irony`, `sf4angel:core/red_tree_redemption` | `T-ANY` |
| `sf4angel:core/battery_included` | Battery Included | First inventory acquisition of at least 1 `mekanism:energytablet` metadata `0`. | `sf4angel:core/redstone_in_a_box` | `T-ITEM` |
| `sf4angel:core/redstone_in_a_box` | Redstone in a Box | First inventory acquisition of at least 1 `mekanism:machineblock` metadata `8`. | `sf4angel:core/smeltery_authority`, `sf4angel:core/diamond_clarity` | `T-ITEM` |
| `sf4angel:core/steel_yourself` | Steel Yourself | First inventory acquisition of at least 1 `mekanism:ingot` metadata `4`. | `sf4angel:core/redstone_in_a_box`, `sf4angel:core/first_spark` | `T-ITEM` |
| `sf4angel:core/enriched_expectations` | Enriched Expectations | First inventory acquisition of at least 1 `mekanism:machineblock` metadata `0`. | `sf4angel:core/steel_yourself` | `T-ITEM` |
| `sf4angel:core/crush_depth` | Crush Depth | First inventory acquisition of at least 1 `mekanism:machineblock` metadata `3`. | `sf4angel:core/steel_yourself` | `T-ITEM` |
| `sf4angel:core/triple_threat` | Triple Threat | A player-owned complete required Mekanism purification machine chain remains active and correlated source input decreases while corresponding final output increases by at least 3 within a bounded observation window. | `sf4angel:core/enriched_expectations`, `sf4angel:core/crush_depth`, `sf4angel:core/hydrogen_economy` | `J-INTEGRATION` |
| `sf4angel:core/five_times_the_charm` | Five Times the Charm | A player-owned complete required Mekanism dissolution machine chain remains active and correlated source input decreases while corresponding final output increases by at least 5 within a bounded observation window. | `sf4angel:core/triple_threat` | `J-INTEGRATION` |
| `sf4angel:core/hydrogen_economy` | Hydrogen Economy | A player-owned `mekanism:machineblock2` metadata `4` processes water and outputs at least 1 mB each of hydrogen and oxygen. | `sf4angel:core/redstone_in_a_box`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/gas_grass_or_rf` | Gas, Grass, or RF | A player-owned `mekanismgenerators:generator` metadata `3` consumes ethylene and produces at least 1 RF. | `sf4angel:core/hydrogen_economy`, `sf4angel:core/crush_depth` | `J-INTEGRATION` |
| `sf4angel:core/hdpeasy` | HDPEasy | First inventory acquisition of at least 1 `mekanism:polyethene` metadata `0`. | `sf4angel:core/gas_grass_or_rf` | `T-ITEM` |
| `sf4angel:core/ultimate_capacity` | Ultimate Capacity | First inventory acquisition of at least 1 `mekanism:energycube` metadata `0` with NBT `{tier:3}`. | `sf4angel:core/crush_depth`, `sf4angel:core/battery_included` | `T-ITEM` |
| `sf4angel:core/factory_settings` | Factory Settings | First inventory acquisition of at least 1 `mekanism:machineblock` metadata `5`, `6`, or `7` (factory recipe type is carried in Mekanism item data). | `sf4angel:core/steel_yourself` | `T-ITEM` |
| `sf4angel:core/digital_prospector` | Digital Prospector | A player-owned `mekanism:machineblock` metadata `4` successfully mines and exports at least 1 block matching its configured filter. | `sf4angel:core/crush_depth`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/latex_intentions` | Latex Intentions | A player-owned `industrialforegoing:tree_fluid_extractor` metadata `0` outputs at least 100 mB latex from a log. | `sf4angel:core/oh_the_irony`, `sf4angel:core/red_tree_redemption` | `J-INTEGRATION` |
| `sf4angel:core/plastic_industry` | Plastic Industry | First inventory acquisition of at least 1 `industrialforegoing:plastic` metadata `0`. | `sf4angel:core/latex_intentions`, `sf4angel:core/first_spark` | `T-ITEM` |
| `sf4angel:core/sow_automatic` | Sow Automatic | A player-owned `industrialforegoing:crop_sower` metadata `0` plants one crop or sapling in the world. | `sf4angel:core/plastic_industry` | `J-INTEGRATION` |
| `sf4angel:core/reap_automatic` | Reap Automatic | A player-owned `industrialforegoing:crop_recolector` metadata `0` harvests one mature crop or tree and exports at least one drop. | `sf4angel:core/plastic_industry` | `J-INTEGRATION` |
| `sf4angel:core/mob_rules` | Mob Rules | A player-owned `industrialforegoing:mob_relocator` metadata `0` kills one hostile mob and outputs its drops or essence. | `sf4angel:core/plastic_industry` | `J-INTEGRATION` |
| `sf4angel:core/black_hole_inventory` | Black Hole Inventory | First inventory acquisition of at least 1 `industrialforegoing:black_hole_unit` metadata `0`. | `sf4angel:core/plastic_industry` | `T-ITEM` |
| `sf4angel:core/learn_deeply` | Learn Deeply | First inventory acquisition of at least 1 `deepmoblearning:deep_learner` metadata `0`. | `sf4angel:core/coal_in_bloom`, `sf4angel:core/red_tree_redemption`, `sf4angel:core/clay_bucket_gamble`, `sf4angel:core/water_you_waiting_for` | `T-ITEM` |
| `sf4angel:core/model_citizen` | Model Citizen | First inventory acquisition of any concrete Deep Mob Learning mob model, including `deepmoblearning:data_model_zombie` metadata `0`; exclude `deepmoblearning:data_model_blank`. | `sf4angel:core/red_tree_redemption`, `sf4angel:core/water_you_waiting_for` | `J-INTEGRATION` |
| `sf4angel:core/data_with_experience` | Data with Experience | A carried concrete Deep Mob Learning model, either directly in inventory or inside a carried Deep Learner, is Basic tier (`tier` NBT at least `1`) or higher, whether trained by kills or created by a pacifist recipe. | `sf4angel:core/model_citizen` | `J-INTEGRATION` |
| `sf4angel:core/simulation_theory` | Simulation Theory | A player-owned `deepmoblearning:simulation_chamber` metadata `0` completes one simulation using a trained Data Model. | `sf4angel:core/data_with_experience`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/reality_armor` | Reality Armor | The player simultaneously equips metadata `0` `deepmoblearning:glitch_infused_helmet`, `glitch_infused_chestplate`, `glitch_infused_leggings`, and `glitch_infused_boots` (each under the `deepmoblearning` namespace). | `sf4angel:core/data_with_experience`, `sf4angel:core/diamond_clarity`, `sf4angel:core/water_you_waiting_for` | `J-INTEGRATION` |

### 4. Dimensions and bosses (15)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/cake_to_hell` | Cake to Hell | First inventory acquisition of at least 1 `telepastries:nether_cake` metadata `0`. | `sf4angel:core/diamond_clarity` | `T-ITEM` |
| `sf4angel:core/nether_say_never` | Nether Say Never | Player enters the Nether dimension (`minecraft:the_nether`) for the first time. | `sf4angel:core/cake_to_hell` | `T-LOCATION` |
| `sf4angel:core/blaze_of_glory` | Blaze of Glory | First inventory acquisition of at least 1 `minecraft:blaze_rod`. | `sf4angel:core/nether_say_never` | `T-ITEM` |
| `sf4angel:core/wither_or_not` | Wither or Not | Player receives the killing-credit event for one `minecraft:wither`. | `sf4angel:core/blaze_of_glory` | `J-EVENT` |
| `sf4angel:core/the_hunting_trip` | The Hunting Trip | Player enters dimension ID `28885` (Hunting Dimension) for the first time. | `sf4angel:core/barnyard_beginnings` | `T-LOCATION` |
| `sf4angel:core/lost_and_found` | Lost and Found | Player enters dimension ID `111` (Lost Cities) for the first time. | `sf4angel:core/blaze_of_glory`, `sf4angel:core/diamond_clarity` | `T-LOCATION` |
| `sf4angel:core/cake_at_the_end` | Cake at the End | First inventory acquisition of at least 1 `telepastries:end_cake` metadata `0`. | `sf4angel:core/blaze_of_glory` | `T-ITEM` |
| `sf4angel:core/the_void_blinks_back` | The Void Blinks Back | Player enters the End dimension (`minecraft:the_end`) for the first time. | `sf4angel:core/cake_at_the_end` | `T-LOCATION` |
| `sf4angel:core/dragon_eviction_notice` | Dragon Eviction Notice | Player receives the killing-credit event for one `minecraft:ender_dragon`. | `sf4angel:core/the_void_blinks_back` | `J-EVENT` |
| `sf4angel:core/borrowed_wings` | Borrowed Wings | First inventory acquisition of at least 1 `minecraft:elytra`. | `sf4angel:core/dragon_eviction_notice` | `T-ITEM` |
| `sf4angel:core/into_the_twilight` | Into the Twilight | While game stage `twilight_forest` is present, player enters dimension ID `7` (Twilight Forest) for the first time. | `[]` | `T-LOCATION` |
| `sf4angel:core/naga_have_i_ever` | Naga Have I Ever | Player receives killing credit for one `twilightforest:naga`. | `sf4angel:core/into_the_twilight` | `J-EVENT` |
| `sf4angel:core/lich_please` | Lich, Please | Player receives killing credit for one `twilightforest:lich`. | `sf4angel:core/naga_have_i_ever` | `J-EVENT` |
| `sf4angel:core/hydra_expectations` | Hydra Expectations | Player receives killing credit for one `twilightforest:hydra`. | `sf4angel:core/lich_please` | `J-EVENT` |
| `sf4angel:core/ice_queen_cometh` | Ice Queen Cometh | Player receives killing credit for one `twilightforest:snow_queen`. | `sf4angel:core/lich_please` | `J-EVENT` |

### 5. Logic, storage, and advanced industry (20)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/menril_state_of_mind` | Menril State of Mind | First inventory acquisition of at least 1 `integrateddynamics:crystalized_menril_chunk` metadata `0`. | `sf4angel:core/water_you_waiting_for` | `T-ITEM` |
| `sf4angel:core/logic_in_the_void` | Logic in the Void | A player-owned Integrated Dynamics network has at least one reader, one display/writer, and one variable card connected and active at the same time. | `sf4angel:core/menril_state_of_mind` | `J-INTEGRATION` |
| `sf4angel:core/items_in_transit` | Items in Transit | A player-owned Integrated Tunnels item interface exports at least 1 item through the network into a different inventory. | `sf4angel:core/logic_in_the_void` | `J-INTEGRATION` |
| `sf4angel:core/fluix_of_the_matter` | Fluix of the Matter | First inventory acquisition of at least 1 `appliedenergistics2:material` metadata `7`. | `sf4angel:core/menril_state_of_mind`, `sf4angel:core/redstone_in_a_box` | `T-ITEM` |
| `sf4angel:core/pressing_engagement` | Pressing Engagement | Player inventory simultaneously contains `appliedenergistics2:material` metadata `13` (calculation press), `14` (engineering), `15` (logic), and `19` (silicon). | `sf4angel:core/cast_away`, `sf4angel:core/alloyed_allegiance`, `sf4angel:core/lost_and_found` | `T-ITEM` |
| `sf4angel:core/acceptable_energy` | Acceptable Energy | A player-owned AE2 Energy Acceptor receives power and the connected ME network stores at least 1 AE. | `sf4angel:core/fluix_of_the_matter` | `J-INTEGRATION` |
| `sf4angel:core/me_myself_and_i` | ME, Myself, and I | Player opens an ME Terminal on a powered network containing an ME Drive with at least one formatted storage cell and inserts or extracts one item. | `sf4angel:core/acceptable_energy`, `sf4angel:core/pressing_engagement` | `J-INTEGRATION` |
| `sf4angel:core/sixty_four_k_and_counting` | Sixty-Four K and Counting | First inventory acquisition of at least 1 `appliedenergistics2:storage_cell_64k` metadata `0`. | `sf4angel:core/me_myself_and_i` | `T-ITEM` |
| `sf4angel:core/autocraft_authority` | Autocraft Authority | A player requests a craft from an ME terminal and that ME network completes the requested item through a crafting CPU and molecular assembler. | `sf4angel:core/sixty_four_k_and_counting` | `J-INTEGRATION` |
| `sf4angel:core/manufactory_warranty_void` | Manufactory Warranty Void | A player-owned NuclearCraft Manufactory (`nuclearcraft:manufactory_idle`/`manufactory_active`, metadata `0`) completes one recipe; integration-only machine-state trigger. | `sf4angel:core/steel_yourself`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/alloyed_allegiance` | Alloyed Allegiance | A player-owned NuclearCraft Alloy Furnace (`nuclearcraft:alloy_furnace_idle`/`alloy_furnace_active`, metadata `0`) completes one alloy recipe; integration-only machine-state trigger. | `sf4angel:core/manufactory_warranty_void` | `J-INTEGRATION` |
| `sf4angel:core/positive_fission` | Positive Fission | Player completes a valid NuclearCraft fission reactor identified through an assembled controller tile from the installed `nuclearcraft:fission_controller_*` registry set documented in `REGISTRY_MANIFEST.md`; integration-only multiblock trigger. | `sf4angel:core/alloyed_allegiance` | `J-INTEGRATION` |
| `sf4angel:core/gone_fission` | Gone Fission | That player-owned fission reactor consumes fuel and exports at least 1 RF while remaining below its configured failure temperature. | `sf4angel:core/positive_fission`, `sf4angel:core/lost_and_found` | `J-INTEGRATION` |
| `sf4angel:core/deuterium_duty` | Deuterium Duty | Player-owned NuclearCraft processing stores at least 1,000 mB fluid `deuterium` (block registry `nuclearcraft:fluid_deuterium`) in one tank. | `sf4angel:core/alloyed_allegiance` | `J-INTEGRATION` |
| `sf4angel:core/tritium_triumph` | Tritium Triumph | Player-owned NuclearCraft processing stores at least 1,000 mB fluid `tritium` (block registry `nuclearcraft:fluid_tritium`) in one tank. | `sf4angel:core/alloyed_allegiance` | `J-INTEGRATION` |
| `sf4angel:core/fusion_cuisine` | Fusion Cuisine | Player completes a valid NuclearCraft fusion reactor identified through `nuclearcraft:fusion_core` metadata `0`; integration-only multiblock trigger. | `sf4angel:core/deuterium_duty`, `sf4angel:core/tritium_triumph` | `J-INTEGRATION` |
| `sf4angel:core/pocket_star` | Pocket Star | That player-owned fusion reactor consumes fusion fuel and exports at least 1 RF. | `sf4angel:core/fusion_cuisine` | `J-INTEGRATION` |
| `sf4angel:core/matter_of_analysis` | Matter of Analysis | A player-owned `matteroverdrive:matter_analyzer` metadata `0` completes one analysis and records a matter pattern. | `sf4angel:core/lost_and_found`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/decompose_yourself` | Decompose Yourself | A player-owned `matteroverdrive:decomposer` metadata `0` consumes one item and increases stored matter. | `sf4angel:core/lost_and_found`, `sf4angel:core/first_spark` | `J-INTEGRATION` |
| `sf4angel:core/replication_nation` | Replication Nation | A player-owned `matteroverdrive:replicator` metadata `0` consumes stored matter and outputs one selected item from a recorded pattern. | `sf4angel:core/matter_of_analysis`, `sf4angel:core/decompose_yourself` | `J-INTEGRATION` |

### 6. Endgame (10)

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:core/matrix_reloaded` | Matrix Reloaded | Player completes a valid Mekanism Induction Matrix containing at least one induction provider and one induction cell. | `sf4angel:core/steel_yourself` | `J-INTEGRATION` |
| `sf4angel:core/turbine_service` | Turbine Service | A valid player-owned Mekanism Industrial Turbine converts steam and exports at least 1 RF. | `sf4angel:core/steel_yourself` | `J-INTEGRATION` |
| `sf4angel:core/crafting_core_values` | Crafting Core Values | First inventory acquisition of at least 1 `extendedcrafting:crafting_core` metadata `0`. | `sf4angel:core/oh_the_irony`, `sf4angel:core/red_tree_redemption` | `T-ITEM` |
| `sf4angel:core/quantum_compression` | Quantum Compression | A player-owned `extendedcrafting:compressor` metadata `0` completes one compression recipe. | `sf4angel:core/oh_the_irony`, `sf4angel:core/red_tree_redemption` | `J-INTEGRATION` |
| `sf4angel:core/metals_into_points` | Metals into Points | First inventory acquisition of at least 1 `extendedcrafting:singularity` whose metadata is one of the installed metal variants listed in `REGISTRY_MANIFEST.md`. | `sf4angel:core/quantum_compression` | `T-ANY` |
| `sf4angel:core/cookie_bacon_donut_collapse` | Cookie, Bacon, Donut, Collapse | Player inventory simultaneously contains `extendedcrafting:singularity_custom` metadata `1` (Cookie), `2` (Bacon), and `3` (Donut). | `sf4angel:core/quantum_compression` | `T-ITEM` |
| `sf4angel:core/ultimate_singularity` | Ultimate Singularity | First inventory acquisition of at least 1 `extendedcrafting:singularity_ultimate` metadata `0`. | `sf4angel:core/metals_into_points`, `sf4angel:core/cookie_bacon_donut_collapse` | `T-ITEM` |
| `sf4angel:core/black_hole_filled` | Black Hole Filled | One player-owned `industrialforegoing:black_hole_unit` metadata `0` reaches its exact runtime maximum item capacity; integration-only tile-state trigger. | `sf4angel:core/black_hole_inventory` | `J-INTEGRATION` |
| `sf4angel:core/million_item_paperwork` | Million-Item Paperwork | One player-owned `realfilingcabinet:modelcabinet` metadata `0` cabinet/folder system reports at least 1,000,000 stored items in aggregate; integration-only capability/NBT trigger. | `sf4angel:core/paperwork_begins` | `J-INTEGRATION` |
| `sf4angel:core/the_sky_finally_claps` | The Sky Finally Claps | All nine parent advancements are complete; no inventory proxy or additional timer is used. | `sf4angel:core/ice_queen_cometh`, `sf4angel:core/ultimate_singularity`, `sf4angel:core/black_hole_filled`, `sf4angel:core/million_item_paperwork`, `sf4angel:core/turbine_service`, `sf4angel:core/pocket_star`, `sf4angel:core/replication_nation`, `sf4angel:core/borrowed_wings`, `sf4angel:core/reality_armor` | `T-ADV` |

## Optional catalog (13)

Optional achievements never parent a core achievement.

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:optional/sticky_keys` | Sticky Keys | While within 5 blocks of a sapling, player completes 20 standing-to-sneaking transitions within one rolling 10-second window. | `[]` | `J-EVENT` |
| `sf4angel:optional/nap_time` | Nap Time | Player completes a sleep cycle that advances world time to morning. | `sf4angel:core/starting_from_the_bottom` | `J-EVENT` |
| `sf4angel:optional/milk_without_the_moo` | Milk Without the Moo | A player-owned `cookingforblockheads:cow_jar` metadata `0` produces at least 1 mB milk. | `sf4angel:core/seeds_of_life` | `J-INTEGRATION` |
| `sf4angel:optional/a_balanced_sky_diet` | A Balanced Sky Diet | Persisted counter reaches 20 distinct edible registry-name-plus-metadata values fully consumed by the player; repeated foods do not count. | `[]` | `J-EVENT` |
| `sf4angel:optional/mob_factory_floor` | Mob Factory Floor | Player-owned automated mob-killing blocks receive killing attribution for 100 hostile mobs in aggregate. | `sf4angel:core/mob_rules` | `J-INTEGRATION` |
| `sf4angel:optional/armored_to_the_teeth` | Armored to the Teeth | Player simultaneously equips four Construct's Armory pieces, each with at least one non-default modifier. | `[]` | `J-INTEGRATION` |
| `sf4angel:optional/unbreakable_resolve` | Unbreakable Resolve | Player holds a Tinkers' tool whose runtime modifier data reports the Unbreakable trait/modifier. | `sf4angel:core/modifier_motive` | `J-INTEGRATION` |
| `sf4angel:optional/undo_the_apocalypse` | Undo the Apocalypse | Across confirmed Building Gadgets placement operations, player places at least 10,000 blocks and then successfully undoes one operation of at least 64 blocks. | `sf4angel:core/oh_the_irony`, `sf4angel:core/diamond_clarity` | `J-INTEGRATION` |
| `sf4angel:optional/pixel_perfect_masonry` | Pixel Perfect Masonry | Player places at least 1,024 Chisels & Bits bits in aggregate and completes one block space containing exactly 4,096 bits. | `sf4angel:core/starting_from_the_bottom` | `J-INTEGRATION` |
| `sf4angel:optional/around_the_void_in_eighty_throws` | Around the Void in Eighty Throws | A Tinkers' Construct yo-yo thrown by the player deals the killing blow to any hostile mob after traveling at least 40 blocks on that throw. | `sf4angel:core/tooling_up` | `J-INTEGRATION` |
| `sf4angel:optional/android_dreams` | Android Dreams of Electric Sheep | While game stage `android` is present, player's Matter Overdrive capability changes from organic to Android and remains Android after one server tick. | `[]` | `J-INTEGRATION` |
| `sf4angel:optional/robot_did_it` | Robot Did It | While game stage `open_computers` is present, a player-owned OpenComputers robot successfully breaks one block and places one block through robot component calls. | `[]` | `J-INTEGRATION` |
| `sf4angel:optional/maximum_minimum_space` | Maximum Minimum Space | Player enters Compact Machines dimension ID `144` through a matching `compactmachines3:machine` metadata `5` (Maximum Compact Machine); integration-only link validation. | `[]` | `J-INTEGRATION` |

## Prestige-only catalog (6)

Prestige-only achievements are hidden and ineligible unless the runtime pack reports Prestige mode enabled for the world/player. They never parent core or optional achievements.

| ID | Title | Exact measurable trigger | Parent IDs | Type |
| --- | --- | --- | --- | --- |
| `sf4angel:prestige/prestige_worldwide` | Prestige Worldwide | Runtime Prestige integration reports Prestige mode enabled and the player has earned at least 1 spendable Prestige point. | `[]` | `J-INTEGRATION` |
| `sf4angel:prestige/equivalent_ambition_unlocked` | Equivalent Ambition Unlocked | Prestige integration reports the ProjectE unlock purchased, and player first acquires `projecte:transmutation_table` metadata `0`. | `sf4angel:prestige/time_is_a_flat_parabox` | `J-INTEGRATION` |
| `sf4angel:prestige/aperture_unlocked` | Aperture Unlocked | Prestige integration reports the Portal Gun unlock purchased, and player successfully creates one linked portal pair with `portalgun:item_portalgun` metadata `0`. | `sf4angel:prestige/time_is_a_flat_parabox` | `J-INTEGRATION` |
| `sf4angel:prestige/written_in_another_age` | Written in Another Age | Prestige integration reports Mystcraft unlocked, and player writes, links to, and enters one player-created Mystcraft Age. | `sf4angel:prestige/time_is_a_flat_parabox` | `J-INTEGRATION` |
| `sf4angel:prestige/time_is_a_flat_parabox` | Time Is a Flat Parabox | A player-owned `parabox:parabox` metadata `0` completes one full active cycle and the player accepts the rollback for at least 1 Prestige point. | `sf4angel:prestige/prestige_worldwide` | `J-INTEGRATION` |
| `sf4angel:prestige/empowered_recursion` | Empowered Recursion | In one continuous Parabox activation, the displayed pending reward reaches at least 10 Prestige points and the player accepts that rollback. | `sf4angel:prestige/time_is_a_flat_parabox` | `J-INTEGRATION` |

## Implementation gate

`REGISTRY_MANIFEST.md` is the instance-derived registry authority. Integration-only targets must use the stated Java integration and fail closed if their runtime API or state cannot be verified; they must never fall back to a broad inventory proxy.
