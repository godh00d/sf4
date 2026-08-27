# SF4 Achievement Registry Manifest

This manifest resolves the 85 `RVR` targets formerly present in `ACHIEVEMENT_PLAN.md` against the installed SkyFactory 4 instance. Metadata is `0` and NBT is absent unless stated otherwise. "Integration-only" means the registry anchor is confirmed, but inventory/location criteria cannot prove the required operation or state.

## Evidence keys

- `ITEMS`: `C:\Users\madla\curseforge\minecraft\Instances\SkyFactory 4\config\AppliedEnergistics2\items.csv` (runtime-generated item variants and display names).
- `CT`: `C:\Users\madla\curseforge\minecraft\Instances\SkyFactory 4\scripts\crafttweaker\` (loaded recipes, staging, integrations, and item representations; loading is recorded in `crafttweaker.log`).
- `TRIUMPH`: `C:\Users\madla\curseforge\minecraft\Instances\SkyFactory 4\config\triumph\script\` (shipped known-good representations only; stale entries contradicted by the runtime export or load errors were rejected).
- `JAR`: the named installed jar under `C:\Users\madla\curseforge\minecraft\Instances\SkyFactory 4\mods\`, using its registry-named blockstates, item models, recipes, loot tables, or classes.
- `CFG`: the named installed file under `C:\Users\madla\curseforge\minecraft\Instances\SkyFactory 4\config\`, or `logs\latest.log` for the Compact Machines registration.

## Bootstrap and storage

| Achievement | Resolution | Evidence | Confidence |
| --- | --- | --- | --- |
| `core/that_is_dir_tree` | `sky_orchards:sapling_dirt` | `ITEMS:15521`; `TRIUMPH:tutorial/tutorial/dirtsapling.txt` | High |
| `core/captain_hook` | `inspirations:wooden_crook` | `ITEMS:8980`; `TRIUMPH:tutorial/tutorial/woodencrook.txt` | High |
| `core/stone_tree_oath` | `sky_orchards:sapling_petrified` | `ITEMS:15525`; `TRIUMPH:tutorial/tutorial/copied/petrifiedsapling.txt` | High |
| `core/gravel_travel` | `sky_orchards:sapling_gravel` | `ITEMS:15537`; corresponding tutorial Triumph script | High |
| `core/beachfront_property` | `sky_orchards:sapling_sand` | `ITEMS:15533`; corresponding tutorial Triumph script | High |
| `core/clay_aiken` | `sky_orchards:sapling_clay` | `ITEMS:15529`; corresponding tutorial Triumph script | High |
| `core/clay_bucket_gamble` | `ceramics:clay_bucket`; lava form NBT `{fluids:{FluidName:"lava",Amount:1000}}` | `ITEMS:11772-11774`; `TRIUMPH:tutorial/tutorial/copied/lava.txt` | High |
| `core/coal_in_bloom` | `sky_orchards:sapling_coal` | `ITEMS:15541`; corresponding tutorial Triumph script | High |
| `core/ironwood` | `sky_orchards:sapling_iron` | `ITEMS:15545`; corresponding tutorial Triumph script | High |
| `core/red_tree_redemption` | `sky_orchards:sapling_redstone` | `ITEMS:15557` | High |
| `core/trees_to_diamonds` | `sky_orchards:sapling_diamond` | `ITEMS:15561`; corresponding tutorial Triumph script | High |
| `core/paperwork_begins` | `realfilingcabinet:modelcabinet` | `ITEMS:14754`; `realfilingcabinet-1.12-0.2.0.21.jar` item model/recipe | High |
| `core/bulk_by_barrel` | `bdsandm:wood_barrel` | `ITEMS:9330`; `BarrelsDrumsStorageAndMore-0.0.24.jar` blockstate | High |
| `core/network_attached_chest` | Integration-only: `storagenetwork:master` plus `storagenetwork:storage_kabel` | `ITEMS:19801,19804`; `SimpleStorageNetwork-1.12.2-1.7.10.jar` | High |
| `core/remote_possibilities` | Integration-only: `storagenetwork:remote` metadata `0`-`3` | `ITEMS:19813-19816`; Simple Storage jar item models | High |
| `core/market_forces` | `farmingforblockheads:market` | `ITEMS:13249`; `TRIUMPH:sf4angel/farming/market.txt` | High |
| `core/seeds_of_life` | `animalcrops:seeds` with string NBT `entity` naming the crop entity | `CT:recipes/mods/animalcrops.zs:15-262`; `ITEMS:8919` | High |
| `core/hog_tied` | `resourcehogs:mud_bucket` with string NBT `ResourceType` | `TRIUMPH:tutorial/tutorial/copied/resourcehogs.txt`; `CT:itemModifiers/tooltips.zs:16-18` | High |
| `core/snad_together` | `snad:snad` metadata `0` | `ITEMS:14902`; Snad jar blockstate | High |
| `core/cane_and_able` | Block `snad:snad` metadata `0` | Same as `core/snad_together` | High |
| `core/tooling_up` | `tconstruct:tooltables` metadata `3` | `ITEMS:11506` | High |
| `core/parts_department` | `tconstruct:tooltables` metadata `2` | `ITEMS:11458-11504` | High |
| `core/melting_point` | `tcomplement:melter` metadata `0` | `ITEMS:14925`; `TRIUMPH:sf4angel/enhancement/melter.txt` | High |
| `core/cast_away` | Integration-only: reusable `tconstruct:cast` metadata `0` or `tconstruct:cast_custom` metadata `0`-`4`; reject `clay_cast` and blank cast state | `ITEMS:11609-11651`; `TRIUMPH:sf4angel/enhancement/cast.txt` | High |
| `core/forge_ahead` | `tconstruct:toolforge` metadata `0` | `ITEMS:11507-11539` | High |
| `core/armor_by_committee` | `conarm:armorforge` metadata `0` | `ITEMS:12825-12857`; shipped Triumph scripts | High |

## Power and processing

| Achievement | Resolution | Evidence | Confidence |
| --- | --- | --- | --- |
| `core/first_spark` | Eight metadata `0` IDs: `simplegenerators:{combustion,culinary,ender,nether,soul,geothermal,fluid_combustion,turbine}_simple` | `ITEMS:19785-19799`; `TRIUMPH:compactmachines/test/5x5x52.txt` | High |
| `core/battery_included` | `cyclicmagic:battery` | `Cyclic-1.12.2-1.20.3.jar` blockstate/item model and `BlockBattery` class | High |
| `core/redstone_in_a_box` | `mekanism:machineblock` metadata `8` | `ITEMS:13464`; shipped Triumph Metallurgic Infuser script | High |
| `core/steel_yourself` | `mekanism:ingot` metadata `4` | `ITEMS:13285`; `TRIUMPH:sf4angel/basic/steel_ingot.txt` | High |
| `core/enriched_expectations` | `mekanism:machineblock` metadata `0` | `ITEMS:13432` | High |
| `core/crush_depth` | `mekanism:machineblock` metadata `3` | `ITEMS:13435` | High |
| `core/hydrogen_economy` | Integration-only: `mekanism:machineblock2` metadata `4` | `ITEMS:13476`; Mekanism recipe resources | High |
| `core/gas_grass_or_rf` | Integration-only: `mekanismgenerators:generator` metadata `3` | `ITEMS:13873`; Mekanism Generators jar | High |
| `core/hdpeasy` | `mekanism:polyethene` metadata `0` | `ITEMS:13379`; Mekanism `hdpepellet` item model | High |
| `core/ultimate_capacity` | `mekanism:energycube` metadata `0`, NBT `{tier:3}` | `ITEMS:13501-13510`; `TRIUMPH:prestige/prestige/energy_cube.txt` establishes `tier` NBT | High |
| `core/factory_settings` | `mekanism:machineblock` metadata `5`, `6`, or `7`; recipe subtype remains Mekanism item data | `ITEMS:13437-13463` | High |
| `core/digital_prospector` | Integration-only: `mekanism:machineblock` metadata `4` | `ITEMS:13436` | High |
| `core/latex_intentions` | Integration-only: `industrialforegoing:tree_fluid_extractor` | `ITEMS:19285`; Industrial Foregoing jar blockstate/recipe | High |
| `core/plastic_industry` | `industrialforegoing:plastic` | `ITEMS:19324`; Industrial Foregoing item model | High |
| `core/sow_automatic` | Integration-only: `industrialforegoing:crop_sower` | `ITEMS:19274`; Industrial Foregoing blockstate | High |
| `core/reap_automatic` | Integration-only: `industrialforegoing:crop_recolector` | `ITEMS:19276`; Industrial Foregoing blockstate | High |
| `core/mob_rules` | Integration-only: `industrialforegoing:mob_relocator` (runtime display name "Mob Crusher") | `ITEMS:19270` | High |
| `core/black_hole_inventory` | `industrialforegoing:black_hole_unit` | `ITEMS:19277`; Industrial Foregoing blockstate/recipes | High |
| `core/learn_deeply` | `deepmoblearning:deep_learner` | `ITEMS:12979`; shipped Triumph script | High |
| `core/model_citizen` | Integration-only registry family `deepmoblearning:data_model_*`, excluding `data_model_blank` | `ITEMS:12980,12991-13011`; Deep Mob Learning jar models/recipes | High |
| `core/simulation_theory` | Integration-only: `deepmoblearning:simulation_chamber` | `ITEMS:12973`; shipped Triumph script | High |
| `core/reality_armor` | Integration-only: `deepmoblearning:glitch_infused_{helmet,chestplate,leggings,boots}` | `ITEMS:12986-12989`; `TRIUMPH:sf4angel/enhancement/full_glitch.txt` | High |

## Dimensions and bosses

| Achievement | Resolution | Evidence | Confidence |
| --- | --- | --- | --- |
| `core/cake_to_hell` | `telepastries:nether_cake` | `ITEMS:14912`; `CT:prestige.zs:150` | High |
| `core/the_hunting_trip` | Dimension ID `28885` | `CFG:huntingdim/huntingdim.cfg:36-37` | High |
| `core/lost_and_found` | Dimension ID `111` | `CFG:lostcities/general.cfg:49-53` | High |
| `core/cake_at_the_end` | `telepastries:end_cake` | `ITEMS:14913`; TelePastries jar | High |
| `core/into_the_twilight` | Dimension ID `7` | `CFG:twilightforest.cfg:62-64` | High |
| `core/naga_have_i_ever` | Entity `twilightforest:naga` | `twilightforest-1.12.2-3.11.1021-universal.jar` loot table `entities/naga.json` | High |
| `core/lich_please` | Entity `twilightforest:lich` | Same jar, `entities/lich.json` | High |
| `core/hydra_expectations` | Entity `twilightforest:hydra` | Same jar, `entities/hydra.json` | High |
| `core/ice_queen_cometh` | Entity `twilightforest:snow_queen` | Same jar, `entities/snow_queen.json` | High |

## Logic and advanced industry

| Achievement | Resolution | Evidence | Confidence |
| --- | --- | --- | --- |
| `core/menril_state_of_mind` | `integrateddynamics:crystalized_menril_chunk` | `ITEMS:13813`; `CT:modIntegrations/nuclearcraft.zs:86,88` | High |
| `core/fluix_of_the_matter` | `appliedenergistics2:material` metadata `7` | `ITEMS:8828`; loaded CT recipes | High |
| `core/pressing_engagement` | `appliedenergistics2:material`: calculation `13`, engineering `14`, logic `15`, silicon `19` | `ITEMS:8801,8822,8835,8845` | High |
| `core/sixty_four_k_and_counting` | `appliedenergistics2:storage_cell_64k` | `ITEMS:5723`; shipped Triumph script | High |
| `core/manufactory_warranty_void` | Integration-only: `nuclearcraft:manufactory_idle`/`manufactory_active` | `ITEMS:1891-1892`; NuclearCraft tile/container classes | High |
| `core/alloyed_allegiance` | Integration-only: `nuclearcraft:alloy_furnace_idle`/`alloy_furnace_active` | `ITEMS:1899-1900`; NuclearCraft advancement/resource | High |
| `core/positive_fission` | Integration-only assembled NuclearCraft fission controller; installed IDs are `fission_controller_idle`, `fission_controller_active`, `fission_controller_new_idle`, `fission_controller_new_active`, and `fission_controller_new_fixed` | `ITEMS:1930-1934`; NuclearCraft blockstates/classes | High |
| `core/deuterium_duty` | Integration-only fluid registry `deuterium`; block `nuclearcraft:fluid_deuterium` | `ITEMS:2372`; fluid container variants `11804,11882` | High |
| `core/tritium_triumph` | Integration-only fluid registry `tritium`; block `nuclearcraft:fluid_tritium` | `ITEMS:2373`; fluid container variants `11805,11883` | High |
| `core/fusion_cuisine` | Integration-only: `nuclearcraft:fusion_core` | `ITEMS:1936`; NuclearCraft `TileFusionCore` and blockstate | High |
| `core/matter_of_analysis` | Integration-only: `matteroverdrive:matter_analyzer` | `ITEMS:15267`; Matter Overdrive blockstate/tile classes | High |
| `core/decompose_yourself` | Integration-only: `matteroverdrive:decomposer` | `ITEMS:15260`; Matter Overdrive blockstate/tile classes | High |
| `core/replication_nation` | Integration-only: `matteroverdrive:replicator` | `ITEMS:15262`; Matter Overdrive blockstate/tile classes | High |

## Endgame, optional, and prestige

| Achievement | Resolution | Evidence | Confidence |
| --- | --- | --- | --- |
| `core/crafting_core_values` | `extendedcrafting:crafting_core` | `ITEMS:13178`; Extended Crafting jar blockstate/recipe | High |
| `core/quantum_compression` | Integration-only: `extendedcrafting:compressor` | `ITEMS:13184`; Extended Crafting jar blockstate | High |
| `core/metals_into_points` | `extendedcrafting:singularity`; installed metal metadata `0-7,17-19,22-25,27-28,32,34,48-50,64-66` | `ITEMS:13220-13244` | High |
| `core/cookie_bacon_donut_collapse` (Cookie) | `extendedcrafting:singularity_custom` metadata `1` | `ITEMS:13245`; Simple Generators culinary fuel config | High |
| `core/cookie_bacon_donut_collapse` (Bacon) | `extendedcrafting:singularity_custom` metadata `2` | `ITEMS:13246`; same config | High |
| `core/cookie_bacon_donut_collapse` (Donut) | `extendedcrafting:singularity_custom` metadata `3` | `ITEMS:13247`; same config | High |
| `core/ultimate_singularity` | `extendedcrafting:singularity_ultimate` | `ITEMS:13248`; Extended Crafting jar item model | High |
| `core/black_hole_filled` | Integration-only: `industrialforegoing:black_hole_unit`; capacity must come from the live tile/item handler | `ITEMS:19277`; Industrial Foregoing tile resources | High |
| `core/million_item_paperwork` | Integration-only: `realfilingcabinet:modelcabinet` plus folder capability/NBT | `ITEMS:14719,14754`; Real Filing Cabinet API/tile classes | High |
| `optional/milk_without_the_moo` | Integration-only: `cookingforblockheads:cow_jar` | `ITEMS:12866`; `CT:recipes/mods/cookingForBlockheads.zs:18-22` | High |
| `optional/maximum_minimum_space` | Integration-only: `compactmachines3:machine` metadata `5`, dimension ID `144` | `ITEMS:12544`; `logs/latest.log:614-615` | High |
| `prestige/equivalent_ambition_unlocked` | Integration-only unlock plus `projecte:transmutation_table` | `ITEMS:14620`; `TRIUMPH:projecte/projecte/root.txt` | High |
| `prestige/aperture_unlocked` | Integration-only unlock/link state plus `portalgun:item_portalgun` metadata `0` | `ITEMS:19775-19780`; Portal Gun jar | High |
| `prestige/time_is_a_flat_parabox` | Integration-only cycle/rollback state plus `parabox:parabox` | `ITEMS:14529`; `TRIUMPH:prestige/prestige/parabox.txt`; loaded Parabox CT script | High |

## Rejected stale evidence and implementation blockers

- The shipped `sf4angel` Triumph scripts use `simpleservernetwork:*`, but `logs/stdout-logs.txt:26633,26637` reports those IDs invalid. Runtime evidence resolves them to `storagenetwork:*`.
- The old Tinkers Triumph scripts use metadata that conflicts with `ITEMS`; the runtime export is authoritative: Tool Station is metadata `3`, Part Builder is `2`, and Tool Forge is the separate `tconstruct:toolforge` item.
- The old endgame Triumph scripts use removed `extrautils2:singularity` IDs; `logs/stdout-logs.txt:26149` reports the ID invalid. Runtime evidence resolves all requested singularities to `extendedcrafting:*`.
- Thirty-one resolved targets remain integration-only because their trigger depends on ownership, machine completion, multiblock validity, dynamic NBT/capability state, dimension linkage, or a mod operation. Their registry resolution is complete, but implementation requires the relevant 1.12.2 mod API/tile hooks and cannot be replaced by broad inventory criteria.
