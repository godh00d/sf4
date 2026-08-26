package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class AchievementHandler {

    private static final Map<UUID, Integer> angelAppearanceCount = new HashMap<>();
    private static final Map<UUID, Long> joinTimes = new HashMap<>();

    private static final LinkedHashMap<String, String> PROGRESSION_PATH = new LinkedHashMap<>();

    static {
        PROGRESSION_PATH.put("sf4angel:basic/log", "sf4angel:basic/crafting_table");
        PROGRESSION_PATH.put("sf4angel:basic/crafting_table", "sf4angel:basic/sand");
        PROGRESSION_PATH.put("sf4angel:basic/sand", "sf4angel:basic/gravel");
        PROGRESSION_PATH.put("sf4angel:basic/gravel", "sf4angel:basic/dirt");
        PROGRESSION_PATH.put("sf4angel:basic/dirt", "sf4angel:basic/petrified_sapling");
        PROGRESSION_PATH.put("sf4angel:basic/petrified_sapling", "sf4angel:basic/cobblestone");
        PROGRESSION_PATH.put("sf4angel:basic/cobblestone", "sf4angel:basic/sieve");
        PROGRESSION_PATH.put("sf4angel:basic/sieve", "sf4angel:basic/watering_can");
        PROGRESSION_PATH.put("sf4angel:basic/watering_can", "sf4angel:basic/tinkers_station");
        PROGRESSION_PATH.put("sf4angel:basic/tinkers_station", "sf4angel:basic/furnace");
        PROGRESSION_PATH.put("sf4angel:basic/furnace", "sf4angel:basic/iron");
        PROGRESSION_PATH.put("sf4angel:basic/iron", "sf4angel:basic/anvil");
        PROGRESSION_PATH.put("sf4angel:basic/anvil", "sf4angel:basic/lava");
        PROGRESSION_PATH.put("sf4angel:basic/lava", "sf4angel:basic/obsidian");
        PROGRESSION_PATH.put("sf4angel:basic/obsidian", "sf4angel:basic/diamond");
        PROGRESSION_PATH.put("sf4angel:basic/diamond", "sf4angel:basic/flint_and_steel");
        PROGRESSION_PATH.put("sf4angel:basic/flint_and_steel", "sf4angel:basic/ender_pearl");
        PROGRESSION_PATH.put("sf4angel:basic/ender_pearl", "sf4angel:basic/ender_cake");
        PROGRESSION_PATH.put("sf4angel:basic/ender_cake", "sf4angel:basic/nether_portal");
        PROGRESSION_PATH.put("sf4angel:basic/nether_portal", "sf4angel:basic/drying_rack");
        PROGRESSION_PATH.put("sf4angel:basic/drying_rack", "sf4angel:basic/diamond_sapling");
        PROGRESSION_PATH.put("sf4angel:basic/diamond_sapling", "sf4angel:basic/soul_vial");
        PROGRESSION_PATH.put("sf4angel:basic/soul_vial", "sf4angel:basic/mob_head");
        PROGRESSION_PATH.put("sf4angel:basic/mob_head", "sf4angel:basic/enchanting_table");
        PROGRESSION_PATH.put("sf4angel:basic/enchanting_table", "sf4angel:basic/obsidian_spawner");
        PROGRESSION_PATH.put("sf4angel:basic/obsidian_spawner", "sf4angel:basic/nether_spawner");
        PROGRESSION_PATH.put("sf4angel:basic/nether_spawner", "sf4angel:basic/tree_router");
        PROGRESSION_PATH.put("sf4angel:basic/tree_router", "sf4angel:basic/pouch");
        PROGRESSION_PATH.put("sf4angel:basic/pouch", "sf4angel:basic/hopper");
        PROGRESSION_PATH.put("sf4angel:basic/hopper", "sf4angel:basic/chest");
        PROGRESSION_PATH.put("sf4angel:basic/chest", "sf4angel:farming/bonsai_pot");
        PROGRESSION_PATH.put("sf4angel:farming/bonsai_pot", "sf4angel:farming/hopping_bonsai");
        PROGRESSION_PATH.put("sf4angel:farming/hopping_bonsai", "sf4angel:farming/fertile_soil");
        PROGRESSION_PATH.put("sf4angel:farming/fertile_soil", "sf4angel:farming/watering_can");
        PROGRESSION_PATH.put("sf4angel:farming/watering_can", "sf4angel:farming/sprinkler");
        PROGRESSION_PATH.put("sf4angel:farming/sprinkler", "sf4angel:farming/animal_seeds");
        PROGRESSION_PATH.put("sf4angel:farming/animal_seeds", "sf4angel:farming/chicken_feed");
        PROGRESSION_PATH.put("sf4angel:farming/chicken_feed", "sf4angel:farming/market");
        PROGRESSION_PATH.put("sf4angel:farming/market", "sf4angel:farming/sky_orchards");
        PROGRESSION_PATH.put("sf4angel:farming/sky_orchards", "sf4angel:farming/farming_complete");
        PROGRESSION_PATH.put("sf4angel:farming/farming_complete", "sf4angel:enhancement/tinkers_tool_station");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_tool_station", "sf4angel:enhancement/tinkers_part_builder");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_part_builder", "sf4angel:enhancement/tinkers_stencil_table");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_stencil_table", "sf4angel:enhancement/tinkers_cast");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_cast", "sf4angel:enhancement/tinkers_pattern_chest");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_pattern_chest", "sf4angel:enhancement/tinkers_tool_forge");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_tool_forge", "sf4angel:enhancement/tinkers_tool_modifier");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_tool_modifier", "sf4angel:enhancement/tinkers_level_up");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_level_up", "sf4angel:enhancement/tinkers_unbreakable");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_unbreakable", "sf4angel:enhancement/tinkers_mattock");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_mattock", "sf4angel:enhancement/tinkers_lumber_axe");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_lumber_axe", "sf4angel:enhancement/tinkers_yoyo");
        PROGRESSION_PATH.put("sf4angel:enhancement/tinkers_yoyo", "sf4angel:enhancement/construct_armory_station");
        PROGRESSION_PATH.put("sf4angel:enhancement/construct_armory_station", "sf4angel:enhancement/construct_armory_forge");
        PROGRESSION_PATH.put("sf4angel:enhancement/construct_armory_forge", "sf4angel:enhancement/construct_armory_modifiers");
        PROGRESSION_PATH.put("sf4angel:enhancement/construct_armory_modifiers", "sf4angel:enhancement/melter");
        PROGRESSION_PATH.put("sf4angel:enhancement/melter", "sf4angel:enhancement/alloy_tank");
        PROGRESSION_PATH.put("sf4angel:enhancement/alloy_tank", "sf4angel:enhancement/heater");
        PROGRESSION_PATH.put("sf4angel:enhancement/heater", "sf4angel:enhancement/mekanism_speed");
        PROGRESSION_PATH.put("sf4angel:enhancement/mekanism_speed", "sf4angel:enhancement/mekanism_energy");
        PROGRESSION_PATH.put("sf4angel:enhancement/mekanism_energy", "sf4angel:enhancement/mekanism_muffling");
        PROGRESSION_PATH.put("sf4angel:enhancement/mekanism_muffling", "sf4angel:enhancement/nc_speed");
        PROGRESSION_PATH.put("sf4angel:enhancement/nc_speed", "sf4angel:enhancement/nc_energy");
        PROGRESSION_PATH.put("sf4angel:enhancement/nc_energy", "sf4angel:enhancement/ender_pouch");
        PROGRESSION_PATH.put("sf4angel:enhancement/ender_pouch", "sf4angel:enhancement/glitch_armor");
        PROGRESSION_PATH.put("sf4angel:enhancement/glitch_armor", "sf4angel:enhancement/enchanting_table");
        PROGRESSION_PATH.put("sf4angel:enhancement/enchanting_table", "sf4angel:enhancement/weirding_gadget");
        PROGRESSION_PATH.put("sf4angel:enhancement/weirding_gadget", "sf4angel:enhancement/paxel");
        PROGRESSION_PATH.put("sf4angel:enhancement/paxel", "sf4angel:enhancement/heart_container");
        PROGRESSION_PATH.put("sf4angel:enhancement/heart_container", "sf4angel:enhancement/crafting_upgrade");
        PROGRESSION_PATH.put("sf4angel:enhancement/crafting_upgrade", "sf4angel:enhancement/inventory_upgrade");
        PROGRESSION_PATH.put("sf4angel:enhancement/inventory_upgrade", "sf4angel:enhancement/damage_20");
        PROGRESSION_PATH.put("sf4angel:enhancement/damage_20", "sf4angel:enhancement/modifier_master");
        PROGRESSION_PATH.put("sf4angel:enhancement/modifier_master", "sf4angel:enhancement/enhancement_complete");
        PROGRESSION_PATH.put("sf4angel:enhancement/enhancement_complete", "sf4angel:power/simple_generator");
        PROGRESSION_PATH.put("sf4angel:power/simple_generator", "sf4angel:power/upgradeable_generator");
        PROGRESSION_PATH.put("sf4angel:power/upgradeable_generator", "sf4angel:power/speed_mod");
        PROGRESSION_PATH.put("sf4angel:power/speed_mod", "sf4angel:power/charger_1");
        PROGRESSION_PATH.put("sf4angel:power/charger_1", "sf4angel:power/charger_2");
        PROGRESSION_PATH.put("sf4angel:power/charger_2", "sf4angel:power/charger_3");
        PROGRESSION_PATH.put("sf4angel:power/charger_3", "sf4angel:power/rf_over_10k");
        PROGRESSION_PATH.put("sf4angel:power/rf_over_10k", "sf4angel:power/rf_over_100k");
        PROGRESSION_PATH.put("sf4angel:power/rf_over_100k", "sf4angel:power/deep_learner");
        PROGRESSION_PATH.put("sf4angel:power/deep_learner", "sf4angel:power/data_model_zombie");
        PROGRESSION_PATH.put("sf4angel:power/data_model_zombie", "sf4angel:power/data_model_enderman");
        PROGRESSION_PATH.put("sf4angel:power/data_model_enderman", "sf4angel:power/data_model_ghast");
        PROGRESSION_PATH.put("sf4angel:power/data_model_ghast", "sf4angel:power/data_model_blaze");
        PROGRESSION_PATH.put("sf4angel:power/data_model_blaze", "sf4angel:power/simulation_chamber");
        PROGRESSION_PATH.put("sf4angel:power/simulation_chamber", "sf4angel:power/overworldian_matter");
        PROGRESSION_PATH.put("sf4angel:power/overworldian_matter", "sf4angel:power/hellish_matter");
        PROGRESSION_PATH.put("sf4angel:power/hellish_matter", "sf4angel:power/extraterrestrial_matter");
        PROGRESSION_PATH.put("sf4angel:power/extraterrestrial_matter", "sf4angel:power/twilight_matter");
        PROGRESSION_PATH.put("sf4angel:power/twilight_matter", "sf4angel:power/mekanism_factory");
        PROGRESSION_PATH.put("sf4angel:power/mekanism_factory", "sf4angel:power/enrichment_chamber");
        PROGRESSION_PATH.put("sf4angel:power/enrichment_chamber", "sf4angel:power/crusher");
        PROGRESSION_PATH.put("sf4angel:power/crusher", "sf4angel:power/metallurgic_infuser");
        PROGRESSION_PATH.put("sf4angel:power/metallurgic_infuser", "sf4angel:power/mekanism_ore_3x");
        PROGRESSION_PATH.put("sf4angel:power/mekanism_ore_3x", "sf4angel:power/digital_miner");
        PROGRESSION_PATH.put("sf4angel:power/digital_miner", "sf4angel:power/teleporter");
        PROGRESSION_PATH.put("sf4angel:power/teleporter", "sf4angel:power/mekanism_jetpack");
        PROGRESSION_PATH.put("sf4angel:power/mekanism_jetpack", "sf4angel:power/mekasuit");
        PROGRESSION_PATH.put("sf4angel:power/mekasuit", "sf4angel:power/atomic_disassembler");
        PROGRESSION_PATH.put("sf4angel:power/atomic_disassembler", "sf4angel:power/nuclear_reactor");
        PROGRESSION_PATH.put("sf4angel:power/nuclear_reactor", "sf4angel:power/fusion_reactor");
        PROGRESSION_PATH.put("sf4angel:power/fusion_reactor", "sf4angel:power/rtg");
        PROGRESSION_PATH.put("sf4angel:power/rtg", "sf4angel:power/powercell_low");
        PROGRESSION_PATH.put("sf4angel:power/powercell_low", "sf4angel:power/powercell_med");
        PROGRESSION_PATH.put("sf4angel:power/powercell_med", "sf4angel:power/powercell_high");
        PROGRESSION_PATH.put("sf4angel:power/powercell_high", "sf4angel:power/rftools_powercell");
        PROGRESSION_PATH.put("sf4angel:power/rftools_powercell", "sf4angel:power/rftools_dimension");
        PROGRESSION_PATH.put("sf4angel:power/rftools_dimension", "sf4angel:power/rftools_spawner");
        PROGRESSION_PATH.put("sf4angel:power/rftools_spawner", "sf4angel:power/rftools_screen");
        PROGRESSION_PATH.put("sf4angel:power/rftools_screen", "sf4angel:power/autoclicker");
        PROGRESSION_PATH.put("sf4angel:power/autoclicker", "sf4angel:power/auto_packager");
        PROGRESSION_PATH.put("sf4angel:power/auto_packager", "sf4angel:power/dehydrator");
        PROGRESSION_PATH.put("sf4angel:power/dehydrator", "sf4angel:power/extraction_chamber");
        PROGRESSION_PATH.put("sf4angel:power/extraction_chamber", "sf4angel:power/ingot_former");
        PROGRESSION_PATH.put("sf4angel:power/ingot_former", "sf4angel:power/froster");
        PROGRESSION_PATH.put("sf4angel:power/froster", "sf4angel:power/uncrafting_grinder");
        PROGRESSION_PATH.put("sf4angel:power/uncrafting_grinder", "sf4angel:power/building_gadget");
        PROGRESSION_PATH.put("sf4angel:power/building_gadget", "sf4angel:power/exchange_tool");
        PROGRESSION_PATH.put("sf4angel:power/exchange_tool", "sf4angel:power/configurator");
        PROGRESSION_PATH.put("sf4angel:power/configurator", "sf4angel:power/treadmill");
        PROGRESSION_PATH.put("sf4angel:power/treadmill", "sf4angel:power/information_screen");
        PROGRESSION_PATH.put("sf4angel:power/information_screen", "sf4angel:power/wireless_charger");
        PROGRESSION_PATH.put("sf4angel:power/wireless_charger", "sf4angel:power/ultimate_installer");
        PROGRESSION_PATH.put("sf4angel:power/ultimate_installer", "sf4angel:power/mob_grinder");
        PROGRESSION_PATH.put("sf4angel:power/mob_grinder", "sf4angel:power/xp_farm");
        PROGRESSION_PATH.put("sf4angel:power/xp_farm", "sf4angel:power/automation_king");
        PROGRESSION_PATH.put("sf4angel:power/automation_king", "sf4angel:power/power_complete");
        PROGRESSION_PATH.put("sf4angel:power/power_complete", "sf4angel:storage/wooden_barrel");
        PROGRESSION_PATH.put("sf4angel:storage/wooden_barrel", "sf4angel:storage/metal_barrel");
        PROGRESSION_PATH.put("sf4angel:storage/metal_barrel", "sf4angel:storage/shipping_container");
        PROGRESSION_PATH.put("sf4angel:storage/shipping_container", "sf4angel:storage/barrel_upgrade_1");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_1", "sf4angel:storage/barrel_upgrade_2");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_2", "sf4angel:storage/barrel_upgrade_3");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_3", "sf4angel:storage/barrel_upgrade_4");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_4", "sf4angel:storage/barrel_upgrade_5");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_5", "sf4angel:storage/barrel_upgrade_6");
        PROGRESSION_PATH.put("sf4angel:storage/barrel_upgrade_6", "sf4angel:storage/wooden_crate");
        PROGRESSION_PATH.put("sf4angel:storage/wooden_crate", "sf4angel:storage/metal_crate");
        PROGRESSION_PATH.put("sf4angel:storage/metal_crate", "sf4angel:storage/simple_storage_master");
        PROGRESSION_PATH.put("sf4angel:storage/simple_storage_master", "sf4angel:storage/simple_storage_cable");
        PROGRESSION_PATH.put("sf4angel:storage/simple_storage_cable", "sf4angel:storage/simple_storage_remote");
        PROGRESSION_PATH.put("sf4angel:storage/simple_storage_remote", "sf4angel:storage/simple_storage_request");
        PROGRESSION_PATH.put("sf4angel:storage/simple_storage_request", "sf4angel:storage/simple_storage_controller");
        PROGRESSION_PATH.put("sf4angel:storage/simple_storage_controller", "sf4angel:storage/wooden_hopper");
        PROGRESSION_PATH.put("sf4angel:storage/wooden_hopper", "sf4angel:storage/hopper");
        PROGRESSION_PATH.put("sf4angel:storage/hopper", "sf4angel:storage/upper");
        PROGRESSION_PATH.put("sf4angel:storage/upper", "sf4angel:storage/drawer_controller");
        PROGRESSION_PATH.put("sf4angel:storage/drawer_controller", "sf4angel:storage/drawer_void");
        PROGRESSION_PATH.put("sf4angel:storage/drawer_void", "sf4angel:storage/drawer_emerald");
        PROGRESSION_PATH.put("sf4angel:storage/drawer_emerald", "sf4angel:storage/drawer_compact");
        PROGRESSION_PATH.put("sf4angel:storage/drawer_compact", "sf4angel:storage/ae2_silicon_press");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_silicon_press", "sf4angel:storage/ae2_logic_press");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_logic_press", "sf4angel:storage/ae2_logic_processor");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_logic_processor", "sf4angel:storage/ae2_engineering_press");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_engineering_press", "sf4angel:storage/ae2_calculation_press");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_calculation_press", "sf4angel:storage/ae2_fluix_crystal");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_fluix_crystal", "sf4angel:storage/ae2_energy_acceptor");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_energy_acceptor", "sf4angel:storage/ae2_controller");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_controller", "sf4angel:storage/ae2_terminal");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_terminal", "sf4angel:storage/ae2_drive_bay");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_drive_bay", "sf4angel:storage/ae2_1k_cell");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_1k_cell", "sf4angel:storage/ae2_4k_cell");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_4k_cell", "sf4angel:storage/ae2_16k_cell");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_16k_cell", "sf4angel:storage/ae2_64k_cell");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_64k_cell", "sf4angel:storage/ae2_autocraft");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_autocraft", "sf4angel:storage/ae2_energy_cell");
        PROGRESSION_PATH.put("sf4angel:storage/ae2_energy_cell", "sf4angel:storage/compact_storage_chest");
        PROGRESSION_PATH.put("sf4angel:storage/compact_storage_chest", "sf4angel:storage/compact_storage_backpack");
        PROGRESSION_PATH.put("sf4angel:storage/compact_storage_backpack", "sf4angel:storage/storage_50k");
        PROGRESSION_PATH.put("sf4angel:storage/storage_50k", "sf4angel:storage/uninstall_upgrade");
        PROGRESSION_PATH.put("sf4angel:storage/uninstall_upgrade", "sf4angel:storage/storage_complete");
        PROGRESSION_PATH.put("sf4angel:storage/storage_complete", "sf4angel:exploration/nether");
        PROGRESSION_PATH.put("sf4angel:exploration/nether", "sf4angel:exploration/water_in_nether");
        PROGRESSION_PATH.put("sf4angel:exploration/water_in_nether", "sf4angel:exploration/nether_wart_farm");
        PROGRESSION_PATH.put("sf4angel:exploration/nether_wart_farm", "sf4angel:exploration/blaze_rod");
        PROGRESSION_PATH.put("sf4angel:exploration/blaze_rod", "sf4angel:exploration/wither_skull");
        PROGRESSION_PATH.put("sf4angel:exploration/wither_skull", "sf4angel:exploration/wither_kill");
        PROGRESSION_PATH.put("sf4angel:exploration/wither_kill", "sf4angel:exploration/nether_star");
        PROGRESSION_PATH.put("sf4angel:exploration/nether_star", "sf4angel:exploration/beacon");
        PROGRESSION_PATH.put("sf4angel:exploration/beacon", "sf4angel:exploration/end_cake");
        PROGRESSION_PATH.put("sf4angel:exploration/end_cake", "sf4angel:exploration/ender_pearl");
        PROGRESSION_PATH.put("sf4angel:exploration/ender_pearl", "sf4angel:exploration/eye_of_ender");
        PROGRESSION_PATH.put("sf4angel:exploration/eye_of_ender", "sf4angel:exploration/ender_dragon");
        PROGRESSION_PATH.put("sf4angel:exploration/ender_dragon", "sf4angel:exploration/dragon_breath");
        PROGRESSION_PATH.put("sf4angel:exploration/dragon_breath", "sf4angel:exploration/elytra");
        PROGRESSION_PATH.put("sf4angel:exploration/elytra", "sf4angel:exploration/lost_cities");
        PROGRESSION_PATH.put("sf4angel:exploration/lost_cities", "sf4angel:exploration/lost_cities_loot");
        PROGRESSION_PATH.put("sf4angel:exploration/lost_cities_loot", "sf4angel:exploration/twilight_forest");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_forest", "sf4angel:exploration/twilight_naga");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_naga", "sf4angel:exploration/twilight_lich");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_lich", "sf4angel:exploration/twilight_hydra");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_hydra", "sf4angel:exploration/twilight_snow_queen");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_snow_queen", "sf4angel:exploration/twilight_all_bosses");
        PROGRESSION_PATH.put("sf4angel:exploration/twilight_all_bosses", "sf4angel:exploration/compact_machine");
        PROGRESSION_PATH.put("sf4angel:exploration/compact_machine", "sf4angel:exploration/compact_machine_large");
        PROGRESSION_PATH.put("sf4angel:exploration/compact_machine_large", "sf4angel:exploration/hunting_dimension");
        PROGRESSION_PATH.put("sf4angel:exploration/hunting_dimension", "sf4angel:exploration/ardite_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/ardite_ore", "sf4angel:exploration/cobalt_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/cobalt_ore", "sf4angel:exploration/boron_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/boron_ore", "sf4angel:exploration/lithium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/lithium_ore", "sf4angel:exploration/magnesium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/magnesium_ore", "sf4angel:exploration/thorium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/thorium_ore", "sf4angel:exploration/uranium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/uranium_ore", "sf4angel:exploration/dilithium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/dilithium_ore", "sf4angel:exploration/tritanium_ore");
        PROGRESSION_PATH.put("sf4angel:exploration/tritanium_ore", "sf4angel:exploration/viescraft");
        PROGRESSION_PATH.put("sf4angel:exploration/viescraft", "sf4angel:exploration/void_crossing");
        PROGRESSION_PATH.put("sf4angel:exploration/void_crossing", "sf4angel:exploration/deep_sky");
        PROGRESSION_PATH.put("sf4angel:exploration/deep_sky", "sf4angel:exploration/sky_explorer");
        PROGRESSION_PATH.put("sf4angel:exploration/sky_explorer", "sf4angel:exploration/overworld_cake");
        PROGRESSION_PATH.put("sf4angel:exploration/overworld_cake", "sf4angel:exploration/exploration_complete");
        PROGRESSION_PATH.put("sf4angel:exploration/exploration_complete", "sf4angel:endgame/cookie");
        PROGRESSION_PATH.put("sf4angel:endgame/cookie", "sf4angel:endgame/cookie_singularity");
        PROGRESSION_PATH.put("sf4angel:endgame/cookie_singularity", "sf4angel:endgame/smore");
        PROGRESSION_PATH.put("sf4angel:endgame/smore", "sf4angel:endgame/darko_sandwich");
        PROGRESSION_PATH.put("sf4angel:endgame/darko_sandwich", "sf4angel:endgame/truffles");
        PROGRESSION_PATH.put("sf4angel:endgame/truffles", "sf4angel:endgame/statues");
        PROGRESSION_PATH.put("sf4angel:endgame/statues", "sf4angel:endgame/singularity_bacon");
        PROGRESSION_PATH.put("sf4angel:endgame/singularity_bacon", "sf4angel:endgame/singularity_donut");
        PROGRESSION_PATH.put("sf4angel:endgame/singularity_donut", "sf4angel:endgame/singularity_ultimate");
        PROGRESSION_PATH.put("sf4angel:endgame/singularity_ultimate", "sf4angel:endgame/black_hole_tank");
        PROGRESSION_PATH.put("sf4angel:endgame/black_hole_tank", "sf4angel:endgame/black_hole_unit");
        PROGRESSION_PATH.put("sf4angel:endgame/black_hole_unit", "sf4angel:endgame/quantum_compressor");
        PROGRESSION_PATH.put("sf4angel:endgame/quantum_compressor", "sf4angel:endgame/file_cabinet");
        PROGRESSION_PATH.put("sf4angel:endgame/file_cabinet", "sf4angel:endgame/fission_reactor_max");
        PROGRESSION_PATH.put("sf4angel:endgame/fission_reactor_max", "sf4angel:endgame/fusion_reactor_max");
        PROGRESSION_PATH.put("sf4angel:endgame/fusion_reactor_max", "sf4angel:endgame/max_reactor");
        PROGRESSION_PATH.put("sf4angel:endgame/max_reactor", "sf4angel:endgame/max_spawner");
        PROGRESSION_PATH.put("sf4angel:endgame/max_spawner", "sf4angel:endgame/fully_upgraded_gen");
        PROGRESSION_PATH.put("sf4angel:endgame/fully_upgraded_gen", "sf4angel:endgame/nc_energy_upgrade");
        PROGRESSION_PATH.put("sf4angel:endgame/nc_energy_upgrade", "sf4angel:endgame/nc_fusion");
        PROGRESSION_PATH.put("sf4angel:endgame/nc_fusion", "sf4angel:endgame/armormodifiers");
        PROGRESSION_PATH.put("sf4angel:endgame/armormodifiers", "sf4angel:endgame/modifier_master");
        PROGRESSION_PATH.put("sf4angel:endgame/modifier_master", "sf4angel:endgame/16k_storage_cell");
        PROGRESSION_PATH.put("sf4angel:endgame/16k_storage_cell", "sf4angel:endgame/4096k_gas");
        PROGRESSION_PATH.put("sf4angel:endgame/4096k_gas", "sf4angel:endgame/4096k_fluid");
        PROGRESSION_PATH.put("sf4angel:endgame/4096k_fluid", "sf4angel:endgame/octuple_netherrack");
        PROGRESSION_PATH.put("sf4angel:endgame/octuple_netherrack", "sf4angel:endgame/octuple_sugarcane");
        PROGRESSION_PATH.put("sf4angel:endgame/octuple_sugarcane", "sf4angel:endgame/yoyo_master");
        PROGRESSION_PATH.put("sf4angel:endgame/yoyo_master", "sf4angel:endgame/prestige_1");
        PROGRESSION_PATH.put("sf4angel:endgame/prestige_1", "sf4angel:endgame/prestige_5");
        PROGRESSION_PATH.put("sf4angel:endgame/prestige_5", "sf4angel:endgame/prestige_all");
        PROGRESSION_PATH.put("sf4angel:endgame/prestige_all", "sf4angel:endgame/collectible_10");
        PROGRESSION_PATH.put("sf4angel:endgame/collectible_10", "sf4angel:endgame/collectible_50");
        PROGRESSION_PATH.put("sf4angel:endgame/collectible_50", "sf4angel:endgame/collectible_all");
        PROGRESSION_PATH.put("sf4angel:endgame/collectible_all", "sf4angel:endgame/endgame_complete");
    }

    public static void onPlayerJoin(UUID playerId) {
        joinTimes.put(playerId, System.currentTimeMillis());
    }

    public static int getAngelAppearances(EntityPlayer player) {
        return angelAppearanceCount.getOrDefault(player.getUniqueID(), 0);
    }

    private static String getNextAdvancement(String completedAdvancement) {
        for (Map.Entry<String, String> entry : PROGRESSION_PATH.entrySet()) {
            if (entry.getKey().equals(completedAdvancement)) {
                String nextId = entry.getValue();
                String shortName = nextId.substring(nextId.lastIndexOf('/') + 1).replace('_', ' ');
                return capitalizeWords(shortName);
            }
        }
        return null;
    }

    private static String capitalizeWords(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == ' ') {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) return;

        Advancement advancement = event.getAdvancement();
        if (advancement == null || advancement.getId() == null) return;

        String advName = advancement.getId().getResourcePath();
        String advNamespace = advancement.getId().getResourceDomain();
        if (advName.contains("root")) return;

        angelAppearanceCount.merge(player.getUniqueID(), 1, Integer::sum);

        EntityPlayerMP mp = (EntityPlayerMP) player;
        World world = player.world;

        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.posX - 10, player.posY - 5, player.posZ - 10,
            player.posX + 10, player.posY + 10, player.posZ + 10
        );

        List<EntityAngel> nearbyAngels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);

        if (!nearbyAngels.isEmpty()) {
            sendAchievementMessage(mp, advancement);
        } else {
            spawnAngelForAchievement(mp, advancement);
        }
    }

    private static void spawnAngelForAchievement(EntityPlayerMP player, Advancement advancement) {
        World world = player.world;
        EntityAngel angel = new EntityAngel(world);
        angel.setOwnerId(player.getUniqueID());
        angel.setPosition(player.posX, player.posY + 5, player.posZ);
        world.spawnEntity(angel);

        sendAchievementMessage(player, advancement);
    }

    private static void sendAchievementMessage(EntityPlayerMP player, Advancement advancement) {
        String advId = advancement.getId().toString();

        String advTitle = advancement.getDisplay() != null
            ? advancement.getDisplay().getTitle().getFormattedText()
            : advancement.getId().getResourcePath();

        String greeting = AngelPersonality.getAdvancementGreeting(advTitle);
        TypewriterHandler.queueMessage(player, greeting, 0, 0);

        String nextAdv = getNextAdvancement(advId);
        if (nextAdv != null) {
            TypewriterHandler.queueMessage(player, "Next goal: " + nextAdv, 60, 0);
        }

        String stageHint = AngelOracle.getHintForCurrentStage(player);
        if (stageHint != null && !stageHint.isEmpty()) {
            TypewriterHandler.queueMessage(player, "Hint: " + stageHint, 100, 0);
        }

        AngelOracle.checkInventoryAndAdvance(player);

        int appearances = getAngelAppearances(player);
        if (appearances >= 50) {
            TypewriterHandler.queueMessage(player, "The angel smiles upon you, faithful companion.", 150, 0);
        }

        String farewell = AngelPersonality.getRandomDepartureLine();
        TypewriterHandler.queueMessage(player, farewell, 200, 0);

        TypewriterHandler.despawnWhenReady(player);
    }
}
