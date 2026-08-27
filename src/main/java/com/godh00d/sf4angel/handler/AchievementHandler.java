package com.godh00d.sf4angel.handler;

import com.godh00d.sf4angel.entity.EntityAngel;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = "sf4angel")
public class AchievementHandler {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static final Map<UUID, Integer> angelAppearanceCount = new HashMap<>();
    private static final Map<UUID, Boolean> sneakingStates = new HashMap<>();
    private static final Map<UUID, Integer> twerkWindows = new HashMap<>();

    private static final LinkedHashMap<String, String> PROGRESSION_PATH = new LinkedHashMap<>();
    private static final Map<String, String> CREATIVE_NAMES = new HashMap<>();

    static {
        CREATIVE_NAMES.put("sf4angel:basic/log", "The First Branch");
        CREATIVE_NAMES.put("sf4angel:basic/crafting_table", "The Carpenter's Gift");
        CREATIVE_NAMES.put("sf4angel:basic/sand", "Whispers of Sand");
        CREATIVE_NAMES.put("sf4angel:basic/gravel", "The Gravel Path");
        CREATIVE_NAMES.put("sf4angel:basic/dirt", "The Descent of Dirt");
        CREATIVE_NAMES.put("sf4angel:basic/petrified_sapling", "Petrified Promise");
        CREATIVE_NAMES.put("sf4angel:basic/cobblestone", "Stone Eternal");
        CREATIVE_NAMES.put("sf4angel:basic/sieve", "The Sifter's Truth");
        CREATIVE_NAMES.put("sf4angel:basic/watering_can", "Rain in a Can");
        CREATIVE_NAMES.put("sf4angel:basic/tinkers_station", "The Tinker's Dawn");
        CREATIVE_NAMES.put("sf4angel:basic/furnace", "Fire's Embrace");
        CREATIVE_NAMES.put("sf4angel:basic/iron", "Iron Will");
        CREATIVE_NAMES.put("sf4angel:basic/anvil", "The Anvil's Song");
        CREATIVE_NAMES.put("sf4angel:basic/lava", "Lava's Vein");
        CREATIVE_NAMES.put("sf4angel:basic/obsidian", "Obsidian Heart");
        CREATIVE_NAMES.put("sf4angel:basic/diamond", "Diamond Clarity");
        CREATIVE_NAMES.put("sf4angel:basic/flint_and_steel", "Spark of Creation");
        CREATIVE_NAMES.put("sf4angel:basic/ender_pearl", "Ender's Tear");
        CREATIVE_NAMES.put("sf4angel:basic/ender_cake", "The Ender Cake");
        CREATIVE_NAMES.put("sf4angel:basic/nether_portal", "Gateway to Fire");
        CREATIVE_NAMES.put("sf4angel:basic/drying_rack", "Sun-Dried Fortune");
        CREATIVE_NAMES.put("sf4angel:basic/diamond_sapling", "Sapling of Stars");
        CREATIVE_NAMES.put("sf4angel:basic/soul_vial", "Vessel of Souls");
        CREATIVE_NAMES.put("sf4angel:basic/mob_head", "Trophy of the Fallen");
        CREATIVE_NAMES.put("sf4angel:basic/enchanting_table", "The Enchanter's Altar");
        CREATIVE_NAMES.put("sf4angel:basic/obsidian_spawner", "Obsidian Cradle");
        CREATIVE_NAMES.put("sf4angel:basic/nether_spawner", "Nether's Cradle");
        CREATIVE_NAMES.put("sf4angel:basic/tree_router", "The Tree Router");
        CREATIVE_NAMES.put("sf4angel:basic/pouch", "Pouch of Holding");
        CREATIVE_NAMES.put("sf4angel:basic/hopper", "The Hopper's Grasp");
        CREATIVE_NAMES.put("sf4angel:basic/chest", "Chest of Origins");
        CREATIVE_NAMES.put("sf4angel:farming/bonsai_pot", "Tiny Tree, Grand Vision");
        CREATIVE_NAMES.put("sf4angel:farming/hopping_bonsai", "The Hopping Grove");
        CREATIVE_NAMES.put("sf4angel:farming/fertile_soil", "Blessed Earth");
        CREATIVE_NAMES.put("sf4angel:farming/watering_can", "Rain Caller");
        CREATIVE_NAMES.put("sf4angel:farming/sprinkler", "Automated Rain");
        CREATIVE_NAMES.put("sf4angel:farming/animal_seeds", "Seeds of Life");
        CREATIVE_NAMES.put("sf4angel:farming/chicken_feed", "Feathered Feast");
        CREATIVE_NAMES.put("sf4angel:farming/market", "The Merchant's Call");
        CREATIVE_NAMES.put("sf4angel:farming/sky_orchards", "Orchards in the Void");
        CREATIVE_NAMES.put("sf4angel:farming/farming_complete", "Harvest Lord");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_tool_station", "The Tool Station");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_part_builder", "Part by Part");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_stencil_table", "Stenciled Dreams");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_cast", "Cast in Metal");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_pattern_chest", "Pattern Keeper");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_tool_forge", "Forge of Tools");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_tool_modifier", "Modifying Fate");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_level_up", "Leveling Up");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_unbreakable", "Unbreakable Will");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_mattock", "The Mattock's Might");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_lumber_axe", "Lumberjack's Dream");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinkers_yoyo", "Yo-Yo Mastery");
        CREATIVE_NAMES.put("sf4angel:enhancement/construct_armory_station", "Armory Station");
        CREATIVE_NAMES.put("sf4angel:enhancement/construct_armory_forge", "Forge of Armor");
        CREATIVE_NAMES.put("sf4angel:enhancement/construct_armory_modifiers", "Armor Modified");
        CREATIVE_NAMES.put("sf4angel:enhancement/melter", "The Melter's Flame");
        CREATIVE_NAMES.put("sf4angel:enhancement/alloy_tank", "Alloy Alchemy");
        CREATIVE_NAMES.put("sf4angel:enhancement/heater", "Heated Resolve");
        CREATIVE_NAMES.put("sf4angel:enhancement/mekanism_speed", "Speed of Mekanism");
        CREATIVE_NAMES.put("sf4angel:enhancement/mekanism_energy", "Energy Unleashed");
        CREATIVE_NAMES.put("sf4angel:enhancement/mekanism_muffling", "Silent Machines");
        CREATIVE_NAMES.put("sf4angel:enhancement/nc_speed", "Nuclear Velocity");
        CREATIVE_NAMES.put("sf4angel:enhancement/nc_energy", "Atomic Power");
        CREATIVE_NAMES.put("sf4angel:enhancement/ender_pouch", "Ender's Pocket");
        CREATIVE_NAMES.put("sf4angel:enhancement/glitch_armor", "Glitch in the Fabric");
        CREATIVE_NAMES.put("sf4angel:enhancement/enchanting_table", "The Enchanter's Table");
        CREATIVE_NAMES.put("sf4angel:enhancement/weirding_gadget", "Weirding Way");
        CREATIVE_NAMES.put("sf4angel:enhancement/paxel", "The Paxel's Power");
        CREATIVE_NAMES.put("sf4angel:enhancement/heart_container", "Heart of the Void");
        CREATIVE_NAMES.put("sf4angel:enhancement/crafting_upgrade", "Crafting Ascended");
        CREATIVE_NAMES.put("sf4angel:enhancement/inventory_upgrade", "Inventory Infinite");
        CREATIVE_NAMES.put("sf4angel:enhancement/damage_20", "Damage Dealt");
        CREATIVE_NAMES.put("sf4angel:enhancement/modifier_master", "Master of Modifiers");
        CREATIVE_NAMES.put("sf4angel:enhancement/enhancement_complete", "Enhancement Complete");
        CREATIVE_NAMES.put("sf4angel:power/simple_generator", "First Spark");
        CREATIVE_NAMES.put("sf4angel:power/upgradeable_generator", "Generator Evolved");
        CREATIVE_NAMES.put("sf4angel:power/speed_mod", "Speed Infused");
        CREATIVE_NAMES.put("sf4angel:power/charger_1", "Charger I");
        CREATIVE_NAMES.put("sf4angel:power/charger_2", "Charger II");
        CREATIVE_NAMES.put("sf4angel:power/charger_3", "Charger III");
        CREATIVE_NAMES.put("sf4angel:power/rf_over_10k", "Ten Thousand RF");
        CREATIVE_NAMES.put("sf4angel:power/rf_over_100k", "Hundred Thousand RF");
        CREATIVE_NAMES.put("sf4angel:power/deep_learner", "Deep Learning");
        CREATIVE_NAMES.put("sf4angel:power/data_model_zombie", "Zombie Data");
        CREATIVE_NAMES.put("sf4angel:power/data_model_enderman", "Enderman Data");
        CREATIVE_NAMES.put("sf4angel:power/data_model_ghast", "Ghast Data");
        CREATIVE_NAMES.put("sf4angel:power/data_model_blaze", "Blaze Data");
        CREATIVE_NAMES.put("sf4angel:power/simulation_chamber", "Simulation Chamber");
        CREATIVE_NAMES.put("sf4angel:power/overworldian_matter", "Overworldian Matter");
        CREATIVE_NAMES.put("sf4angel:power/hellish_matter", "Hellish Matter");
        CREATIVE_NAMES.put("sf4angel:power/extraterrestrial_matter", "Alien Matter");
        CREATIVE_NAMES.put("sf4angel:power/twilight_matter", "Twilight Matter");
        CREATIVE_NAMES.put("sf4angel:power/mekanism_factory", "Mekanism Factory");
        CREATIVE_NAMES.put("sf4angel:power/enrichment_chamber", "Enrichment Chamber");
        CREATIVE_NAMES.put("sf4angel:power/crusher", "The Crusher");
        CREATIVE_NAMES.put("sf4angel:power/metallurgic_infuser", "Metallurgic Infusion");
        CREATIVE_NAMES.put("sf4angel:power/mekanism_ore_3x", "Triple Ore Yield");
        CREATIVE_NAMES.put("sf4angel:power/digital_miner", "Digital Miner");
        CREATIVE_NAMES.put("sf4angel:power/teleporter", "Teleportation Master");
        CREATIVE_NAMES.put("sf4angel:power/mekanism_jetpack", "Jetpack Soaring");
        CREATIVE_NAMES.put("sf4angel:power/mekasuit", "Mekasuit Online");
        CREATIVE_NAMES.put("sf4angel:power/atomic_disassembler", "Atomic Disassembly");
        CREATIVE_NAMES.put("sf4angel:power/nuclear_reactor", "Nuclear Dawn");
        CREATIVE_NAMES.put("sf4angel:power/fusion_reactor", "Fusion Ignition");
        CREATIVE_NAMES.put("sf4angel:power/rtg", "RTG Steady");
        CREATIVE_NAMES.put("sf4angel:power/powercell_low", "Powercell I");
        CREATIVE_NAMES.put("sf4angel:power/powercell_med", "Powercell II");
        CREATIVE_NAMES.put("sf4angel:power/powercell_high", "Powercell III");
        CREATIVE_NAMES.put("sf4angel:power/rftools_powercell", "RFTools Powercell");
        CREATIVE_NAMES.put("sf4angel:power/rftools_dimension", "Dimension Builder");
        CREATIVE_NAMES.put("sf4angel:power/rftools_spawner", "RFTools Spawner");
        CREATIVE_NAMES.put("sf4angel:power/rftools_screen", "RFTools Screen");
        CREATIVE_NAMES.put("sf4angel:power/autoclicker", "Auto Clicker");
        CREATIVE_NAMES.put("sf4angel:power/auto_packager", "Auto Packager");
        CREATIVE_NAMES.put("sf4angel:power/dehydrator", "Dehydrator");
        CREATIVE_NAMES.put("sf4angel:power/extraction_chamber", "Extraction Chamber");
        CREATIVE_NAMES.put("sf4angel:power/ingot_former", "Ingot Former");
        CREATIVE_NAMES.put("sf4angel:power/froster", "The Froster");
        CREATIVE_NAMES.put("sf4angel:power/uncrafting_grinder", "Uncrafting Grinder");
        CREATIVE_NAMES.put("sf4angel:power/building_gadget", "Building Gadget");
        CREATIVE_NAMES.put("sf4angel:power/exchange_tool", "Exchange Tool");
        CREATIVE_NAMES.put("sf4angel:power/configurator", "Configurator");
        CREATIVE_NAMES.put("sf4angel:power/treadmill", "Treadmill");
        CREATIVE_NAMES.put("sf4angel:power/information_screen", "Info Screen");
        CREATIVE_NAMES.put("sf4angel:power/wireless_charger", "Wireless Charge");
        CREATIVE_NAMES.put("sf4angel:power/ultimate_installer", "Ultimate Installer");
        CREATIVE_NAMES.put("sf4angel:power/mob_grinder", "Mob Grinder");
        CREATIVE_NAMES.put("sf4angel:power/xp_farm", "XP Farm");
        CREATIVE_NAMES.put("sf4angel:power/automation_king", "Automation King");
        CREATIVE_NAMES.put("sf4angel:power/power_complete", "Power Complete");
        CREATIVE_NAMES.put("sf4angel:storage/wooden_barrel", "Wooden Barrel");
        CREATIVE_NAMES.put("sf4angel:storage/metal_barrel", "Metal Barrel");
        CREATIVE_NAMES.put("sf4angel:storage/shipping_container", "Shipping Container");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_1", "Barrel Upgrade I");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_2", "Barrel Upgrade II");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_3", "Barrel Upgrade III");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_4", "Barrel Upgrade IV");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_5", "Barrel Upgrade V");
        CREATIVE_NAMES.put("sf4angel:storage/barrel_upgrade_6", "Barrel Upgrade VI");
        CREATIVE_NAMES.put("sf4angel:storage/wooden_crate", "Wooden Crate");
        CREATIVE_NAMES.put("sf4angel:storage/metal_crate", "Metal Crate");
        CREATIVE_NAMES.put("sf4angel:storage/simple_storage_master", "Simple Storage Master");
        CREATIVE_NAMES.put("sf4angel:storage/simple_storage_cable", "Storage Cable");
        CREATIVE_NAMES.put("sf4angel:storage/simple_storage_remote", "Storage Remote");
        CREATIVE_NAMES.put("sf4angel:storage/simple_storage_request", "Request Table");
        CREATIVE_NAMES.put("sf4angel:storage/simple_storage_controller", "Storage Controller");
        CREATIVE_NAMES.put("sf4angel:storage/wooden_hopper", "Wooden Hopper");
        CREATIVE_NAMES.put("sf4angel:storage/hopper", "The Hopper");
        CREATIVE_NAMES.put("sf4angel:storage/upper", "Upper Storage");
        CREATIVE_NAMES.put("sf4angel:storage/drawer_controller", "Drawer Controller");
        CREATIVE_NAMES.put("sf4angel:storage/drawer_void", "Void Drawer");
        CREATIVE_NAMES.put("sf4angel:storage/drawer_emerald", "Emerald Drawer");
        CREATIVE_NAMES.put("sf4angel:storage/drawer_compact", "Compact Drawer");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_silicon_press", "Silicon Press");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_logic_press", "Logic Press");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_logic_processor", "Logic Processor");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_engineering_press", "Engineering Press");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_calculation_press", "Calculation Press");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_fluix_crystal", "Fluix Crystal");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_energy_acceptor", "Energy Acceptor");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_controller", "ME Controller");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_terminal", "ME Terminal");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_drive_bay", "Drive Bay");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_1k_cell", "1K Cell");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_4k_cell", "4K Cell");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_16k_cell", "16K Cell");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_64k_cell", "64K Cell");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_autocraft", "Auto Crafting");
        CREATIVE_NAMES.put("sf4angel:storage/ae2_energy_cell", "Energy Cell");
        CREATIVE_NAMES.put("sf4angel:storage/compact_storage_chest", "Compact Chest");
        CREATIVE_NAMES.put("sf4angel:storage/compact_storage_backpack", "Compact Backpack");
        CREATIVE_NAMES.put("sf4angel:storage/storage_50k", "50K Storage");
        CREATIVE_NAMES.put("sf4angel:storage/uninstall_upgrade", "Uninstall Upgrade");
        CREATIVE_NAMES.put("sf4angel:storage/storage_complete", "Storage Complete");
        CREATIVE_NAMES.put("sf4angel:exploration/nether", "Hell Knocked First");
        CREATIVE_NAMES.put("sf4angel:exploration/water_in_nether", "Smuggling the Wet Stuff");
        CREATIVE_NAMES.put("sf4angel:exploration/nether_wart_farm", "Warts and All");
        CREATIVE_NAMES.put("sf4angel:exploration/blaze_rod", "Pocket Sun on a Stick");
        CREATIVE_NAMES.put("sf4angel:exploration/wither_skull", "Three Heads Are Worse Than One");
        CREATIVE_NAMES.put("sf4angel:exploration/wither_kill", "Taxing the Wither");
        CREATIVE_NAMES.put("sf4angel:exploration/nether_star", "Star Stolen from Hell");
        CREATIVE_NAMES.put("sf4angel:exploration/beacon", "Sky Lighthouse");
        CREATIVE_NAMES.put("sf4angel:exploration/end_cake", "Dessert at the End of Reality");
        CREATIVE_NAMES.put("sf4angel:exploration/ender_pearl", "Pearl of Bad Decisions");
        CREATIVE_NAMES.put("sf4angel:exploration/eye_of_ender", "The Void Blinks Back");
        CREATIVE_NAMES.put("sf4angel:exploration/ender_dragon", "Dear Dragon, Pay Rent");
        CREATIVE_NAMES.put("sf4angel:exploration/dragon_breath", "Bottled Boss Breath");
        CREATIVE_NAMES.put("sf4angel:exploration/elytra", "Borrowed Wings");
        CREATIVE_NAMES.put("sf4angel:exploration/lost_cities", "Urban Decay Vacation");
        CREATIVE_NAMES.put("sf4angel:exploration/lost_cities_loot", "Looting the Landlord");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_forest", "Twilight Tourist Trap");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_naga", "Scale Mail Express");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_lich", "Evict the Lich");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_hydra", "Too Many Neck Problems");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_snow_queen", "Cold Royalty, Warm Loot");
        CREATIVE_NAMES.put("sf4angel:exploration/twilight_all_bosses", "Forest Management Complete");
        CREATIVE_NAMES.put("sf4angel:exploration/compact_machine", "Factory in a Lunchbox");
        CREATIVE_NAMES.put("sf4angel:exploration/compact_machine_large", "Bigger on the Inside, Still Weird");
        CREATIVE_NAMES.put("sf4angel:exploration/hunting_dimension", "Vacation for Violence");
        CREATIVE_NAMES.put("sf4angel:exploration/ardite_ore", "Orange Rock Fever");
        CREATIVE_NAMES.put("sf4angel:exploration/cobalt_ore", "Blue Rock Swagger");
        CREATIVE_NAMES.put("sf4angel:exploration/boron_ore", "Boron Again");
        CREATIVE_NAMES.put("sf4angel:exploration/lithium_ore", "Mood Stabilizer Mining");
        CREATIVE_NAMES.put("sf4angel:exploration/magnesium_ore", "Flash Powder Fortune");
        CREATIVE_NAMES.put("sf4angel:exploration/thorium_ore", "Spicy Reactor Pebble");
        CREATIVE_NAMES.put("sf4angel:exploration/uranium_ore", "Glow Rock Negotiations");
        CREATIVE_NAMES.put("sf4angel:exploration/dilithium_ore", "Warp Core Shopping List");
        CREATIVE_NAMES.put("sf4angel:exploration/tritanium_ore", "Space Metal Bragging Rights");
        CREATIVE_NAMES.put("sf4angel:exploration/viescraft", "Airship with Questionable Insurance");
        CREATIVE_NAMES.put("sf4angel:exploration/void_crossing", "Bridge Over Absolutely Nothing");
        CREATIVE_NAMES.put("sf4angel:exploration/deep_sky", "Where the Map Gives Up");
        CREATIVE_NAMES.put("sf4angel:exploration/sky_explorer", "Professional Cloud Trespasser");
        CREATIVE_NAMES.put("sf4angel:exploration/overworld_cake", "Homecoming, Frosted");
        CREATIVE_NAMES.put("sf4angel:exploration/exploration_complete", "Cartographer of Bad Ideas");
        CREATIVE_NAMES.put("sf4angel:endgame/cookie", "One Cookie to Rule Them All");
        CREATIVE_NAMES.put("sf4angel:endgame/cookie_singularity", "The Cookie Collapses");
        CREATIVE_NAMES.put("sf4angel:endgame/smore", "Campfire Economics");
        CREATIVE_NAMES.put("sf4angel:endgame/darko_sandwich", "Forbidden Lunchbox");
        CREATIVE_NAMES.put("sf4angel:endgame/truffles", "Pig-Approved Luxury");
        CREATIVE_NAMES.put("sf4angel:endgame/statues", "Monuments to Inventory Abuse");
        CREATIVE_NAMES.put("sf4angel:endgame/singularity_bacon", "Bacon Beyond Physics");
        CREATIVE_NAMES.put("sf4angel:endgame/singularity_donut", "Donut of Dense Regret");
        CREATIVE_NAMES.put("sf4angel:endgame/singularity_ultimate", "Oops, All Singularity");
        CREATIVE_NAMES.put("sf4angel:endgame/black_hole_tank", "Drink from the Abyss");
        CREATIVE_NAMES.put("sf4angel:endgame/black_hole_unit", "Storage That Stares Back");
        CREATIVE_NAMES.put("sf4angel:endgame/quantum_compressor", "Squish Reality Harder");
        CREATIVE_NAMES.put("sf4angel:endgame/file_cabinet", "Bureaucracy of Blocks");
        CREATIVE_NAMES.put("sf4angel:endgame/fission_reactor_max", "Maximum Split Personality");
        CREATIVE_NAMES.put("sf4angel:endgame/fusion_reactor_max", "Pocket Star, No Warranty");
        CREATIVE_NAMES.put("sf4angel:endgame/max_reactor", "Reactor Sized Ego");
        CREATIVE_NAMES.put("sf4angel:endgame/max_spawner", "Mob Factory Overclocked");
        CREATIVE_NAMES.put("sf4angel:endgame/fully_upgraded_gen", "Generator Wearing a Crown");
        CREATIVE_NAMES.put("sf4angel:endgame/nc_energy_upgrade", "Nuclear Juice Box");
        CREATIVE_NAMES.put("sf4angel:endgame/nc_fusion", "Fusion Cuisine");
        CREATIVE_NAMES.put("sf4angel:endgame/armormodifiers", "Fashionably Overpowered");
        CREATIVE_NAMES.put("sf4angel:endgame/modifier_master", "The Modifier Whisperer");
        CREATIVE_NAMES.put("sf4angel:endgame/16k_storage_cell", "Sixteen Thousand Tiny Drawers");
        CREATIVE_NAMES.put("sf4angel:endgame/4096k_gas", "Gas Giant in a Box");
        CREATIVE_NAMES.put("sf4angel:endgame/4096k_fluid", "Ocean, but Spreadsheet");
        CREATIVE_NAMES.put("sf4angel:endgame/octuple_netherrack", "Netherrack Lasagna");
        CREATIVE_NAMES.put("sf4angel:endgame/octuple_sugarcane", "Cane Compression Crimes");
        CREATIVE_NAMES.put("sf4angel:endgame/yoyo_master", "String Theory Champion");
        CREATIVE_NAMES.put("sf4angel:endgame/prestige_1", "Prestige: First Halo");
        CREATIVE_NAMES.put("sf4angel:endgame/prestige_5", "Prestige: Five Lives Later");
        CREATIVE_NAMES.put("sf4angel:endgame/prestige_all", "Prestige: Reality Unlocked");
        CREATIVE_NAMES.put("sf4angel:endgame/collectible_10", "Ten Shiny Distractions");
        CREATIVE_NAMES.put("sf4angel:endgame/collectible_50", "Fifty Reasons to Hoard");
        CREATIVE_NAMES.put("sf4angel:endgame/collectible_all", "Museum of Everything");
        CREATIVE_NAMES.put("sf4angel:endgame/endgame_complete", "The Sky Finally Claps");
    }

    static {
        CREATIVE_NAMES.put("sf4angel:basic/root", "Age of One Block");
        CREATIVE_NAMES.put("sf4angel:basic/age1_complete", "Dirt Age Diploma");
        CREATIVE_NAMES.put("sf4angel:basic/aluminum_ingot", "Aluminum Arrival");
        CREATIVE_NAMES.put("sf4angel:basic/bacon_sapling", "Breakfast Tree Theology");
        CREATIVE_NAMES.put("sf4angel:basic/bone_sapling", "Bone Orchard");
        CREATIVE_NAMES.put("sf4angel:basic/bronze_ingot", "Bronze Before Brains");
        CREATIVE_NAMES.put("sf4angel:basic/cauldron", "The Sky Soup Pot");
        CREATIVE_NAMES.put("sf4angel:basic/clay_bucket", "Clay Bucket Gamble");
        CREATIVE_NAMES.put("sf4angel:basic/clay_sapling", "Mud Tree Miracle");
        CREATIVE_NAMES.put("sf4angel:basic/coal", "Pocket Night Fuel");
        CREATIVE_NAMES.put("sf4angel:basic/coal_sapling", "Coal in Bloom");
        CREATIVE_NAMES.put("sf4angel:basic/cobble_gen", "Infinite Stone Cheat Code");
        CREATIVE_NAMES.put("sf4angel:basic/cobblestone", "The First Forever Block");
        CREATIVE_NAMES.put("sf4angel:basic/cooked_acorn", "Nut Cuisine Begins");
        CREATIVE_NAMES.put("sf4angel:basic/copper_ingot", "Copper Coin of Progress");
        CREATIVE_NAMES.put("sf4angel:basic/cottonwood_sapling", "Cloud Cotton Tree");
        CREATIVE_NAMES.put("sf4angel:basic/crushing_tub", "Stomp Bucket Ritual");
        CREATIVE_NAMES.put("sf4angel:basic/diamond", "Clear Rock Royalty");
        CREATIVE_NAMES.put("sf4angel:basic/diamond_mesh", "Mesh of Greed");
        CREATIVE_NAMES.put("sf4angel:basic/diamond_sapling", "Carbon Royal Sapling");
        CREATIVE_NAMES.put("sf4angel:basic/dirt", "The Descent of Dirt");
        CREATIVE_NAMES.put("sf4angel:basic/dirt_acorn", "Dirt Nut Prophecy");
        CREATIVE_NAMES.put("sf4angel:basic/donut_sapling", "Pastry Botany");
        CREATIVE_NAMES.put("sf4angel:basic/drying_rack", "Jerky Architecture");
        CREATIVE_NAMES.put("sf4angel:basic/first_auto", "Machine Does the Chores");
        CREATIVE_NAMES.put("sf4angel:basic/first_machine", "First Metal Assistant");
        CREATIVE_NAMES.put("sf4angel:basic/first_sieve", "Shake the Sky Loose");
        CREATIVE_NAMES.put("sf4angel:basic/generator", "The First Hum");
        CREATIVE_NAMES.put("sf4angel:basic/glowstone", "Lantern Dust from Heaven");
        CREATIVE_NAMES.put("sf4angel:basic/gold_ingot", "Soft Rich Metal");
        CREATIVE_NAMES.put("sf4angel:basic/gold_sapling", "Greed Grows on Trees");
        CREATIVE_NAMES.put("sf4angel:basic/gravel_sapling", "Pebble Orchard");
        CREATIVE_NAMES.put("sf4angel:basic/green_slimy_dirt", "The Goo Underfoot");
        CREATIVE_NAMES.put("sf4angel:basic/iron_bucket", "Bucket of Responsibility");
        CREATIVE_NAMES.put("sf4angel:basic/iron_ingot", "Iron Enters the Chat");
        CREATIVE_NAMES.put("sf4angel:basic/iron_mesh", "Mesh With Muscle");
        CREATIVE_NAMES.put("sf4angel:basic/iron_plate", "Flat Iron Theology");
        CREATIVE_NAMES.put("sf4angel:basic/iron_sapling", "Ironwood, Literally");
        CREATIVE_NAMES.put("sf4angel:basic/lapis", "Blue Pebble Magic");
        CREATIVE_NAMES.put("sf4angel:basic/lapis_sapling", "Blue Tree Conspiracy");
        CREATIVE_NAMES.put("sf4angel:basic/lava", "Liquid Bad Idea");
        CREATIVE_NAMES.put("sf4angel:basic/lead_ingot", "Heavy Metal Debut");
        CREATIVE_NAMES.put("sf4angel:basic/lead_sapling", "Lead Leaves, Somehow");
        CREATIVE_NAMES.put("sf4angel:basic/obsidian", "Black Glass Backbone");
        CREATIVE_NAMES.put("sf4angel:basic/petrified_sapling", "Stone Tree Oath");
        CREATIVE_NAMES.put("sf4angel:basic/plant_sapling", "Life Above Nothing");
        CREATIVE_NAMES.put("sf4angel:basic/redstone", "Dust With Opinions");
        CREATIVE_NAMES.put("sf4angel:basic/redstone_sapling", "Logic Grows Leaves");
        CREATIVE_NAMES.put("sf4angel:basic/resource_hog", "Bacon-Scented Industry");
        CREATIVE_NAMES.put("sf4angel:basic/resource_monopoly", "Treeconomy Takeover");
        CREATIVE_NAMES.put("sf4angel:basic/sand_sapling", "Beach Tree Heresy");
        CREATIVE_NAMES.put("sf4angel:basic/silver_ingot", "Moon Metal");
        CREATIVE_NAMES.put("sf4angel:basic/silver_sapling", "Moonlight Orchard");
        CREATIVE_NAMES.put("sf4angel:basic/slabs", "Half Block, Full Panic");
        CREATIVE_NAMES.put("sf4angel:basic/sleep", "Horizontal Progress");
        CREATIVE_NAMES.put("sf4angel:basic/slime_sapling", "Wobblewood Genesis");
        CREATIVE_NAMES.put("sf4angel:basic/steel_ingot", "Iron With Ambition");
        CREATIVE_NAMES.put("sf4angel:basic/stone_crook", "Crook With Commitment");
        CREATIVE_NAMES.put("sf4angel:basic/string", "Thread of Survival");
        CREATIVE_NAMES.put("sf4angel:basic/tin_ingot", "Tin Can Future");
        CREATIVE_NAMES.put("sf4angel:basic/tin_sapling", "Tin Twig Accord");
        CREATIVE_NAMES.put("sf4angel:basic/twerk", "The Sacred Wiggle");
        CREATIVE_NAMES.put("sf4angel:basic/water", "Wet Achievement Unlocked");
        CREATIVE_NAMES.put("sf4angel:basic/wooden_crook", "Hooked on Leaves");

        CREATIVE_NAMES.put("sf4angel:farming/root", "Age of Many Mouths");
        CREATIVE_NAMES.put("sf4angel:farming/all_animals", "Noah's Tiny Checklist");
        CREATIVE_NAMES.put("sf4angel:farming/amber_mulch", "Amber Growth Gospel");
        CREATIVE_NAMES.put("sf4angel:farming/animal_army", "Barnyard Battalion");
        CREATIVE_NAMES.put("sf4angel:farming/animal_crops", "Livestock from Dirt");
        CREATIVE_NAMES.put("sf4angel:farming/animal_seeds", "Seeds With Hooves");
        CREATIVE_NAMES.put("sf4angel:farming/auto_farm", "The Farm Farms Back");
        CREATIVE_NAMES.put("sf4angel:farming/black_mulch", "Black Mulch Magic");
        CREATIVE_NAMES.put("sf4angel:farming/blue_mulch", "Blue Growth Potion");
        CREATIVE_NAMES.put("sf4angel:farming/bonsai", "Tiny Forest Contract");
        CREATIVE_NAMES.put("sf4angel:farming/bonsai_master", "Pocket Arborist");
        CREATIVE_NAMES.put("sf4angel:farming/bonsai_pot", "Tree in a Teacup");
        CREATIVE_NAMES.put("sf4angel:farming/brown_mulch", "Brown Mulch Blessing");
        CREATIVE_NAMES.put("sf4angel:farming/cake_birthday", "Cake Before Civilization");
        CREATIVE_NAMES.put("sf4angel:farming/chicken_feed", "Chicken Contract Law");
        CREATIVE_NAMES.put("sf4angel:farming/cook_everything", "Pan-Fried Completionism");
        CREATIVE_NAMES.put("sf4angel:farming/cow_jar", "Milk in Captivity");
        CREATIVE_NAMES.put("sf4angel:farming/crop_variety", "Salad Bar Empire");
        CREATIVE_NAMES.put("sf4angel:farming/ender_crop", "Crops That Teleport Emotionally");
        CREATIVE_NAMES.put("sf4angel:farming/ender_seeds", "Seeds from Somewhere Else");
        CREATIVE_NAMES.put("sf4angel:farming/farm_complete", "Harvest Crown Claimed");
        CREATIVE_NAMES.put("sf4angel:farming/farming_complete", "Harvest Crown Claimed");
        CREATIVE_NAMES.put("sf4angel:farming/fence_overhaul", "Fence Science");
        CREATIVE_NAMES.put("sf4angel:farming/fertile_soil", "Blessed Dirt Deluxe");
        CREATIVE_NAMES.put("sf4angel:farming/food_variety", "Menu of Survival");
        CREATIVE_NAMES.put("sf4angel:farming/hopping_bonsai", "Tree With a Hopper Hat");
        CREATIVE_NAMES.put("sf4angel:farming/hydrogel", "Wet Dirt Technology");
        CREATIVE_NAMES.put("sf4angel:farming/magic_beans", "Definitely Not Suspicious Beans");
        CREATIVE_NAMES.put("sf4angel:farming/market", "Capitalism Cube");
        CREATIVE_NAMES.put("sf4angel:farming/mattock", "Hoe-Pick Diplomacy");
        CREATIVE_NAMES.put("sf4angel:farming/mob_swab", "Creature Q-Tip");
        CREATIVE_NAMES.put("sf4angel:farming/nether_wart", "Hell Pickles");
        CREATIVE_NAMES.put("sf4angel:farming/red_mulch", "Red Mulch Rush");
        CREATIVE_NAMES.put("sf4angel:farming/resource_hog_breed", "Industrial Pig Romance");
        CREATIVE_NAMES.put("sf4angel:farming/ruby_mulch", "Ruby Mulch Royalty");
        CREATIVE_NAMES.put("sf4angel:farming/sky_orchards", "Orchard Above Oblivion");
        CREATIVE_NAMES.put("sf4angel:farming/spawn_egg", "Egg of Consequences");
        CREATIVE_NAMES.put("sf4angel:farming/sprinkler", "Rain Machine Ritual");
        CREATIVE_NAMES.put("sf4angel:farming/sugar_cane_sand", "Beach Candy Setup");
        CREATIVE_NAMES.put("sf4angel:farming/sugar_cane_seed", "Cane Genesis");
        CREATIVE_NAMES.put("sf4angel:farming/watering_can", "Handheld Weather");
        CREATIVE_NAMES.put("sf4angel:farming/wheat_empire", "Bread Barony");
        CREATIVE_NAMES.put("sf4angel:farming/wheat_seeds", "The First Loaf Begins");
        CREATIVE_NAMES.put("sf4angel:farming/yellow_mulch", "Yellow Mulch Momentum");

        CREATIVE_NAMES.put("sf4angel:enhancement/root", "Age of Sharp Ideas");
        CREATIVE_NAMES.put("sf4angel:enhancement/alloy_tank", "Tank of Mixed Feelings");
        CREATIVE_NAMES.put("sf4angel:enhancement/armor_forge", "Armor Forge Authority");
        CREATIVE_NAMES.put("sf4angel:enhancement/armor_station", "Wardrobe of War");
        CREATIVE_NAMES.put("sf4angel:enhancement/anvil_master", "Anvil Whisperer");
        CREATIVE_NAMES.put("sf4angel:enhancement/cast", "Metal Mold Mischief");
        CREATIVE_NAMES.put("sf4angel:enhancement/crafting_upgrade", "Crafting Hands Extended");
        CREATIVE_NAMES.put("sf4angel:enhancement/diamond_tools", "Diamond Tool Swagger");
        CREATIVE_NAMES.put("sf4angel:enhancement/enchanted_golden_apple", "Snack of the Gods");
        CREATIVE_NAMES.put("sf4angel:enhancement/enchanting_table", "Library of Sparkles");
        CREATIVE_NAMES.put("sf4angel:enhancement/ender_pouch", "Pocket to Elsewhere");
        CREATIVE_NAMES.put("sf4angel:enhancement/enh_complete", "Enhancement Crown Claimed");
        CREATIVE_NAMES.put("sf4angel:enhancement/energy_upgrade", "Battery Blessing");
        CREATIVE_NAMES.put("sf4angel:enhancement/first_manyullyn", "First Taste of Purple Metal");
        CREATIVE_NAMES.put("sf4angel:enhancement/flight_enabled", "Gravity Resignation Letter");
        CREATIVE_NAMES.put("sf4angel:enhancement/full_glitch", "Reality Armor Complete");
        CREATIVE_NAMES.put("sf4angel:enhancement/glitch_boots", "Bugged Boots");
        CREATIVE_NAMES.put("sf4angel:enhancement/glitch_chestplate", "Chestplate.exe");
        CREATIVE_NAMES.put("sf4angel:enhancement/glitch_helmet", "Head Full of Errors");
        CREATIVE_NAMES.put("sf4angel:enhancement/glitch_leggings", "Leggings of Lag");
        CREATIVE_NAMES.put("sf4angel:enhancement/heart_container", "Heart Storage Upgrade");
        CREATIVE_NAMES.put("sf4angel:enhancement/heater", "Warm Machine Thoughts");
        CREATIVE_NAMES.put("sf4angel:enhancement/inventory_upgrade", "Pockets Beyond Reason");
        CREATIVE_NAMES.put("sf4angel:enhancement/jetpack", "Backpack Rocket Etiquette");
        CREATIVE_NAMES.put("sf4angel:enhancement/level_30_enchant", "Thirty Levels of Gambling");
        CREATIVE_NAMES.put("sf4angel:enhancement/levelup", "Tool Learns Violence");
        CREATIVE_NAMES.put("sf4angel:enhancement/lumberaxe", "Tree Divorce Papers");
        CREATIVE_NAMES.put("sf4angel:enhancement/manyullyn_tools", "Purple Tool Supremacy");
        CREATIVE_NAMES.put("sf4angel:enhancement/melter", "Tiny Smeltery Ambition");
        CREATIVE_NAMES.put("sf4angel:enhancement/muffling_upgrade", "Machine Mute Button");
        CREATIVE_NAMES.put("sf4angel:enhancement/nuclear_speed", "Atomic Haste");
        CREATIVE_NAMES.put("sf4angel:enhancement/part_builder", "Parts Department Open");
        CREATIVE_NAMES.put("sf4angel:enhancement/pattern_chest", "Pattern Hoarder");
        CREATIVE_NAMES.put("sf4angel:enhancement/paxel", "One Tool Problem Solver");
        CREATIVE_NAMES.put("sf4angel:enhancement/speed_upgrade", "Speed Gremlin Installed");
        CREATIVE_NAMES.put("sf4angel:enhancement/stencil_table", "Blueprint Bench");
        CREATIVE_NAMES.put("sf4angel:enhancement/tank", "Fluid Box Diplomacy");
        CREATIVE_NAMES.put("sf4angel:enhancement/tcon_modifier", "Modifier Mischief");
        CREATIVE_NAMES.put("sf4angel:enhancement/tinker_smeltery", "Smeltery Baptism");
        CREATIVE_NAMES.put("sf4angel:enhancement/tool_forge", "Tool Forge Coronation");
        CREATIVE_NAMES.put("sf4angel:enhancement/tool_modifier_5", "Five Tweaks Too Far");
        CREATIVE_NAMES.put("sf4angel:enhancement/tool_station", "Bench of Better Tools");
        CREATIVE_NAMES.put("sf4angel:enhancement/twenty_damage", "Twenty Damage Apology");
        CREATIVE_NAMES.put("sf4angel:enhancement/unbreakable_pick", "Pickaxe Refuses Death");
        CREATIVE_NAMES.put("sf4angel:enhancement/weirding_gadget", "Chunk-Sitter Idol");
        CREATIVE_NAMES.put("sf4angel:enhancement/yoyo", "Weaponized Childhood");

        CREATIVE_NAMES.put("sf4angel:power/root", "Age of Loud Boxes");
        CREATIVE_NAMES.put("sf4angel:power/crusher_mek", "Crusher With Teeth");
        CREATIVE_NAMES.put("sf4angel:power/data_model_train", "Training the Data Pet");
        CREATIVE_NAMES.put("sf4angel:power/enderman_model", "Tall Man Statistics");
        CREATIVE_NAMES.put("sf4angel:power/energized_smelter", "Electric Furnace Swagger");
        CREATIVE_NAMES.put("sf4angel:power/ghast_model", "Crying Cube Data");
        CREATIVE_NAMES.put("sf4angel:power/pow_complete", "Power Crown Claimed");
        CREATIVE_NAMES.put("sf4angel:power/pristine_enderman", "Perfect Enderman Sample");
        CREATIVE_NAMES.put("sf4angel:power/pristine_ghast", "Perfect Ghast Sample");
        CREATIVE_NAMES.put("sf4angel:power/pristine_wither", "Perfect Wither Sample");
        CREATIVE_NAMES.put("sf4angel:power/pristine_zombie", "Perfect Zombie Sample");
        CREATIVE_NAMES.put("sf4angel:power/upgradeable_gen", "Generator With Ambition");
        CREATIVE_NAMES.put("sf4angel:power/wither_model", "Wither by Spreadsheet");
        CREATIVE_NAMES.put("sf4angel:power/zombie_model", "Zombie Data Internship");

        CREATIVE_NAMES.put("sf4angel:storage/root", "Age of Too Much Stuff");
        CREATIVE_NAMES.put("sf4angel:storage/stor_complete", "Storage Crown Claimed");

        CREATIVE_NAMES.put("sf4angel:exploration/root", "Age of Leaving Home");
        CREATIVE_NAMES.put("sf4angel:exploration/expl_complete", "Exploration Crown Claimed");

        CREATIVE_NAMES.put("sf4angel:endgame/root", "Age of Excessive Proof");

        CREATIVE_NAMES.put("sf4angel:angel/root", "Angel's Ledger");
        CREATIVE_NAMES.put("sf4angel:angel/angel_first", "First Heavenly Visit");
        CREATIVE_NAMES.put("sf4angel:angel/angel_friend", "Friend of the Floating Cube");
        CREATIVE_NAMES.put("sf4angel:angel/angel_killed_by", "Smote by Customer Support");
        CREATIVE_NAMES.put("sf4angel:angel/angel_strike", "Do Not Tap the Angel");
        CREATIVE_NAMES.put("sf4angel:angel/death_50", "Fifty Ways to Fall");
        CREATIVE_NAMES.put("sf4angel:angel/first_death", "First Appointment With Gravity");
        CREATIVE_NAMES.put("sf4angel:angel/kill_100_mobs", "One Hundred Bad Neighbors");
        CREATIVE_NAMES.put("sf4angel:angel/kill_1000_mobs", "A Thousand Regrets Later");
        CREATIVE_NAMES.put("sf4angel:angel/mine_1000_blocks", "One Thousand Blocks Borrowed");
        CREATIVE_NAMES.put("sf4angel:angel/mine_10000_blocks", "Ten Thousand Block Problem");
        CREATIVE_NAMES.put("sf4angel:angel/play_10_hours", "Ten Hours in the Void");
        CREATIVE_NAMES.put("sf4angel:angel/play_50_hours", "Fifty Hours, Still Falling");
    }

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

    public static void grantAdvancement(EntityPlayerMP player, String advancementId) {
        WorldServer world = (WorldServer) player.world;
        Advancement advancement = world.getAdvancementManager().getAdvancement(new ResourceLocation(advancementId));
        if (advancement == null) {
            LOGGER.warn("Cannot grant missing advancement {}", advancementId);
            return;
        }

        PlayerAdvancements advancements = player.getAdvancements();
        List<String> remaining = new ArrayList<>();
        for (String criterion : advancements.getProgress(advancement).getRemaningCriteria()) {
            remaining.add(criterion);
        }
        for (String criterion : remaining) {
            advancements.grantCriterion(advancement, criterion);
        }
    }

    public static void checkCustomCounters(EntityPlayerMP player) {
        int playTicks = player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
        int mobKills = player.getStatFile().readStat(StatList.MOB_KILLS);
        int deaths = player.getStatFile().readStat(StatList.DEATHS);
        NBTTagCompound persisted = getPersistedData(player);
        int blocksMined = persisted.getInteger("sf4angelBlocksMined");

        int inventoryItems = 0;
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (!player.inventory.getStackInSlot(slot).isEmpty()) {
                inventoryItems += player.inventory.getStackInSlot(slot).getCount();
            }
        }

        if (playTicks >= 20 * 60 * 60 * 10) grantAdvancement(player, "sf4angel:angel/play_10_hours");
        if (playTicks >= 20 * 60 * 60 * 50) grantAdvancement(player, "sf4angel:angel/play_50_hours");
        if (mobKills >= 100) grantAdvancement(player, "sf4angel:angel/kill_100_mobs");
        if (mobKills >= 1000) grantAdvancement(player, "sf4angel:angel/kill_1000_mobs");
        if (deaths >= 1) grantAdvancement(player, "sf4angel:angel/first_death");
        if (deaths >= 50) grantAdvancement(player, "sf4angel:angel/death_50");
        if (blocksMined >= 1000) grantAdvancement(player, "sf4angel:angel/mine_1000_blocks");
        if (blocksMined >= 10000) grantAdvancement(player, "sf4angel:angel/mine_10000_blocks");
        if (player.experienceLevel >= 1) grantAdvancement(player, "sf4angel:enhancement/levelup");
        if (inventoryItems >= 1000) grantAdvancement(player, "sf4angel:basic/resource_monopoly");

        int dimension = player.dimension;
        recordVisitedDimension(persisted, dimension);
        if (dimension == 7) grantAdvancement(player, "sf4angel:exploration/twilight_forest");
        if (dimension == 111) grantAdvancement(player, "sf4angel:exploration/lost_cities");
        if (dimension == 144) grantAdvancement(player, "sf4angel:exploration/compact_machine");
        if (dimension == 28885) grantAdvancement(player, "sf4angel:exploration/hunting_dimension");
        if (player.posY <= 5.0D) grantAdvancement(player, "sf4angel:exploration/deep_sky");
        if (player.posY <= 0.0D) grantAdvancement(player, "sf4angel:exploration/void_crossing");
        if (player.world.provider.getDimensionType().getName().toLowerCase(Locale.ROOT).contains("rftools")) {
            grantAdvancement(player, "sf4angel:power/rftools_dimension");
        }
        if (persisted.getTagList("sf4angelVisitedDimensions", 8).tagCount() >= 6) {
            grantAdvancement(player, "sf4angel:exploration/sky_explorer");
        }
    }

    public static void checkTwerk(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        boolean sneaking = player.isSneaking();
        boolean wasSneaking = sneakingStates.getOrDefault(id, sneaking);
        sneakingStates.put(id, sneaking);

        int window = Math.max(0, twerkWindows.getOrDefault(id, 0) - 1);
        if (sneaking && !wasSneaking && isNearSapling(player)) {
            NBTTagCompound persisted = getPersistedData(player);
            int changes = window > 0 ? persisted.getInteger("sf4angelTwerkChanges") + 1 : 1;
            persisted.setInteger("sf4angelTwerkChanges", changes);
            window = 100;
            if (changes >= 5) {
                grantAdvancement(player, "sf4angel:basic/twerk");
                persisted.setInteger("sf4angelTwerkChanges", 0);
                window = 0;
            }
        }
        twerkWindows.put(id, window);
    }

    private static boolean isNearSapling(EntityPlayerMP player) {
        int originX = (int) Math.floor(player.posX);
        int originY = (int) Math.floor(player.posY);
        int originZ = (int) Math.floor(player.posZ);
        for (int x = originX - 4; x <= originX + 4; x++) {
            for (int y = originY - 2; y <= originY + 2; y++) {
                for (int z = originZ - 4; z <= originZ + 4; z++) {
                    if (player.world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z)).getBlock() == Blocks.SAPLING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void recordAngelAppearance(EntityPlayerMP player) {
        NBTTagCompound persisted = getPersistedData(player);
        int appearances = persisted.getInteger("sf4angelAppearances") + 1;
        persisted.setInteger("sf4angelAppearances", appearances);
        if (appearances >= 500) {
            grantAdvancement(player, "sf4angel:angel/angel_friend");
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        NBTTagCompound persisted = getPersistedData(player);
        persisted.setInteger("sf4angelBlocksMined", persisted.getInteger("sf4angelBlocksMined") + 1);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) return;
        if (event.getWorld().provider.getDimension() == -1
            && (event.getPlacedBlock().getBlock() == Blocks.WATER
                || event.getPlacedBlock().getBlock() == Blocks.FLOWING_WATER)) {
            grantAdvancement((EntityPlayerMP) event.getPlayer(), "sf4angel:exploration/water_in_nether");
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        EntityPlayer player = event.getCausedByPlayer();
        if (!(player instanceof EntityPlayerMP) || player.world.isRemote) return;

        EntityPlayerMP mp = (EntityPlayerMP) player;
        NBTTagCompound persisted = getPersistedData(mp);
        int bred = persisted.getInteger("sf4angelAnimalsBred") + 1;
        persisted.setInteger("sf4angelAnimalsBred", bred);
        if (bred >= 50) {
            grantAdvancement(mp, "sf4angel:farming/animal_army");
        }

        ResourceLocation childId = EntityList.getKey(event.getChild());
        if (childId != null && "resourcehogs".equals(childId.getResourceDomain())) {
            grantAdvancement(mp, "sf4angel:farming/resource_hog_breed");
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntityLiving().world.isRemote || event.getAmount() < 20.0F) return;
        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            grantAdvancement((EntityPlayerMP) event.getSource().getTrueSource(), "sf4angel:enhancement/twenty_damage");
        }
    }

    public static void removePlayer(UUID id) {
        sneakingStates.remove(id);
        twerkWindows.remove(id);
    }

    private static NBTTagCompound getPersistedData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void recordVisitedDimension(NBTTagCompound persisted, int dimension) {
        NBTTagList visited = persisted.getTagList("sf4angelVisitedDimensions", 8);
        String id = Integer.toString(dimension);
        for (int i = 0; i < visited.tagCount(); i++) {
            if (id.equals(visited.getStringTagAt(i))) return;
        }
        visited.appendTag(new NBTTagString(id));
        persisted.setTag("sf4angelVisitedDimensions", visited);
    }

    public static int getAngelAppearances(EntityPlayer player) {
        return angelAppearanceCount.getOrDefault(player.getUniqueID(), 0);
    }

    private static String getNextAdvancement(EntityPlayerMP player, String completedAdvancement) {
        String candidate = PROGRESSION_PATH.get(completedAdvancement);
        WorldServer world = (WorldServer) player.world;
        for (int checked = 0; candidate != null && checked <= PROGRESSION_PATH.size(); checked++) {
            Advancement advancement = world.getAdvancementManager().getAdvancement(new ResourceLocation(candidate));
            if (advancement != null && !player.getAdvancements().getProgress(advancement).isDone()) {
                return getCreativeName(candidate);
            }
            candidate = PROGRESSION_PATH.get(candidate);
        }
        return null;
    }

    private static String getCreativeName(String advancementId) {
        String creativeName = CREATIVE_NAMES.get(advancementId);
        if (creativeName != null) return creativeName;

        String path = advancementId;
        String category = "unknown";
        int colon = advancementId.indexOf(':');
        int slash = advancementId.indexOf('/');
        if (colon >= 0 && slash > colon) {
            category = advancementId.substring(colon + 1, slash);
            path = advancementId.substring(slash + 1);
        } else if (slash >= 0) {
            path = advancementId.substring(slash + 1);
        }

        String subject = capitalizeWords(path.replace('_', ' '));
        if (category.equals("basic")) return "Rite of " + subject;
        if (category.equals("farming")) return "Bloom of " + subject;
        if (category.equals("enhancement")) return "Ascension of " + subject;
        if (category.equals("power")) return "Engine of " + subject;
        if (category.equals("storage")) return "Vault of " + subject;
        if (category.equals("exploration")) return "Pilgrimage to " + subject;
        if (category.equals("endgame")) return "Crown of " + subject;
        if (category.equals("angel")) return "Angel's Omen: " + subject;
        return "Omen of " + subject;
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

    private static boolean isNextProgressionAdvancement(EntityPlayerMP player, String advancementId) {
        WorldServer world = (WorldServer) player.world;
        for (String id : PROGRESSION_PATH.keySet()) {
            Advancement advancement = world.getAdvancementManager().getAdvancement(new ResourceLocation(id));
            if (advancement == null) continue;
            if (id.equals(advancementId)) return true;
            if (!player.getAdvancements().getProgress(advancement).isDone()) return false;
        }
        return false;
    }

    public static void onAdvancementCompleted(EntityPlayerMP player, String advancementId) {
        boolean advancesProgression = isNextProgressionAdvancement(player, advancementId);

        angelAppearanceCount.merge(player.getUniqueID(), 1, Integer::sum);
        int count = getAngelAppearances(player);

        LOGGER.info("Advancement completed for {}: {}", player.getName(), advancementId);

        String advTitle = getCreativeName(advancementId);

        String greeting = AngelPersonality.getAdvancementGreeting(advTitle);
        TypewriterHandler.queueMessage(player, greeting, 0, 0);

        String nextAdv = advancesProgression ? getNextAdvancement(player, advancementId) : null;
        if (nextAdv != null) {
            TypewriterHandler.queueMessage(player, "Next goal: " + nextAdv, 80, 0);
        }

        if (advancesProgression) {
            AngelOracle.checkInventoryAndAdvance(player);
        }

        if (count == 50) {
            TypewriterHandler.queueMessage(player, "The angel smiles upon you, faithful companion.", 120, 0);
        }

        String farewell = AngelPersonality.getRandomDepartureLine();
        TypewriterHandler.queueMessage(player, farewell, 160, 0);

        TypewriterHandler.despawnWhenReady(player);

        World world = player.world;
        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.posX - 15, player.posY - 5, player.posZ - 15,
            player.posX + 15, player.posY + 15, player.posZ + 15
        );
        List<EntityAngel> nearbyAngels = world.getEntitiesWithinAABB(EntityAngel.class, searchBox);
        boolean hasOwnedAngel = false;
        for (EntityAngel nearby : nearbyAngels) {
            if (player.getUniqueID().equals(nearby.getOwnerId())) {
                hasOwnedAngel = true;
                break;
            }
        }
        if (!hasOwnedAngel) {
            EntityAngel angel = new EntityAngel(world);
            angel.setOwnerId(player.getUniqueID());
            double yaw = Math.toRadians(player.rotationYaw);
            angel.setPosition(
                player.posX - Math.sin(yaw) * 6.0D,
                player.posY + player.getEyeHeight() - 0.5,
                player.posZ + Math.cos(yaw) * 6.0D
            );
            world.spawnEntity(angel);
            recordAngelAppearance(player);
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) return;

        Advancement advancement = event.getAdvancement();
        if (advancement == null || advancement.getId() == null) return;

        String advId = advancement.getId().toString();
        String advName = advancement.getId().getResourcePath();
        if (!advId.startsWith("sf4angel:")) return;
        if (advName.equals("root") || advName.endsWith("/root")) return;

        EntityPlayerMP mp = (EntityPlayerMP) player;
        onAdvancementCompleted(mp, advId);
    }

}
