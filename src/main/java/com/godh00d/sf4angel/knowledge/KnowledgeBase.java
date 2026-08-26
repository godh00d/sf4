package com.godh00d.sf4angel.knowledge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.*;

public class KnowledgeBase {

    private static final Map<String, GuideTopic> topics = new LinkedHashMap<>();
    private static final Random RANDOM = new Random();

    public static void init() {
        loadTopics();
    }

    private static void loadTopics() {
        try {
            InputStream is = KnowledgeBase.class.getResourceAsStream("/assets/sf4angel/sf4angel/guides.json");
            if (is == null) {
                loadDefaultTopics();
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            Gson gson = new Gson();
            JsonObject root = gson.fromJson(sb.toString(), JsonObject.class);
            JsonArray topicsArray = root.getAsJsonArray("topics");
            for (JsonElement e : topicsArray) {
                JsonObject obj = e.getAsJsonObject();
                GuideTopic topic = new GuideTopic();
                topic.keyword = obj.get("keyword").getAsString();
                topic.title = obj.get("title").getAsString();
                topic.content = obj.get("content").getAsString();
                topic.mod = obj.has("mod") ? obj.get("mod").getAsString() : "general";
                topic.age = obj.has("age") ? obj.get("age").getAsString() : "any";
                topics.put(topic.keyword.toLowerCase(), topic);
            }
        } catch (Exception e) {
            loadDefaultTopics();
        }
    }

    private static void loadDefaultTopics() {
        addTopic("help", "Prayer Commands", "pray guide <topic> - learn about a topic | pray how <topic> - step by step | pray explain <topic> - deep dive | pray tip - random tip | pray topics - list all topics | pray goal - your current goal | pray stage - your current stage", "general", "any");
        addTopic("topics", "All Topics", "sieve, cobblegen, bonsai, tinker, ae2, mekanism, nuclear, rftools, deepmob, storage, barrels, twilight, nether, end, lostcities, compactmachines, singularities, prestige, parabox, hogs, crops, animals, market, power, generators, enchanting, glitch, collectibles", "general", "any");
        addTopic("sieve", "The Sieve", "The sieve is your primary resource generator. Place it on top of a chest or drawer. Use a hammer on blocks (cobble, gravel, sand, dust) to get materials, then sift them through the sieve. Mesh tiers: flint < iron < diamond. Higher tiers give better loot. Automate with hoppers or Simple Storage cables.", "ex_nihilo", "age1");
        addTopic("cobblegen", "Cobblestone Generator", "Basic cobble gen: flowing lava next to flowing water. Place water on one side, lava on the other, with a gap between. Cobblestone forms in the gap. Upgrade to compressed generators for more output. Essential early game for infinite materials.", "ex_nihilo", "age1");
        addTopic("bonsai", "Bonsai Trees", "Bonsai pots grow trees in miniature. Place sapling on pot, wait. Results go to hopper below or drawer. Each sapling type gives different drops. Oak: apples, sticks. Birch: saplings. Spruce: more wood. Automate with hoppers for resource generation.", "bonsai_trees", "age_farming");
        addTopic("tinker", "Tinkers Construct", "Build a smeltery: seared bricks from casting table. Smelt ores 2x. Tools: pickaxe, axe, sword, hammer. Materials: manyullyn (best), cobalt, ardite, osmium. Modifiers: redstone (speed), lapis (fortune), gold (additional modifier slots), diamond, emerald. Tool stations for crafting, tool works for modifying.", "tconstruct", "age_enhancement");
        addTopic("ae2", "Applied Energistics 2", "Digital storage. ME Controller (7x7x7 multiblock), ME Drive (stores storage cells), Crafting Terminal (access all items), Molecular Assembler (auto-crafting). Channels: 32 per controller side. Storage cells: 1k, 4k, 16k, 64k, 256k. Acceleration cards speed up crafting. Import/export buses move items in/out.", "ae2", "age_storage");
        addTopic("mekanism", "Mekanism", "Ore processing chain: Enrichment Chamber (2x) -> Crusher (3x) -> Combiner (reverse) -> Metallurgic Infuser (alloys). Generators: Heat Generator, Solar Generator, Gas-Burning Generator. Digital Miner: auto-mine specific ores. Teleporter: instant travel between points. Jetpack: flight.", "mekanism", "age_power");
        addTopic("nuclear", "NuclearCraft", "Fission reactor: reactor casing, controller, fuel rods, coolant. Fusion reactor: much more powerful, requires Tritium and D-T fuel. Processors: alloy smelter, electrolyzer, ingot Former, melter. RTG: radioisotope generators for steady power.", "nuclearcraft", "age_power");
        addTopic("rftools", "RFTools", "Powercell: wireless RF transfer. Crafter: auto-crafting. Dimension Builder: create custom dimensions (costs RF). Screen Controller: in-game displays. Scanner: find blocks in world. Spawner: spawn any mob. Composer: combine dimlets.", "rftools", "age_power");
        addTopic("deepmob", "Deep Mob Learning", "Simulation Chamber: generates mob data without killing. Deep Learner: stores data models. Data Models: train by killing mobs or simulating. pristine matters give best loot. Glitch armor: runs on mob data, gives flight.", "deep_mob_learning", "age_power");
        addTopic("storage", "Simple Storage Network", "Storage Network Master: connects inventories. Storage Cable: connects nodes. Storage Terminal: access all items. Request Table: pull items on demand. Limit: 500 items per network. Upgrade with Crafting Upgrade for auto-crafting.", "simple_storage", "age_storage");
        addTopic("barrels", "Barrels and Drawers", "Storage Drawers: store one item type per drawer. Drawer Controller: access all drawers from one point. Upgrades: void upgrade (destroys excess), emerald upgrade (infinite storage), compacting (stores ingots + blocks). Barrels: similar but for blocks.", "storagedrawers", "age_storage");
        addTopic("twilight", "Twilight Forest", "Enter via: 2x2 water surrounded by flowers, throw diamond in. Bosses: Naga (forest), Lich (tower), Ghast (swamp), Hydra (nexus), Ur-Gast (fire), Knight Phantom (castle), Snow Queen (ice). Loot: ironwood, fiery blood, knightly metal. Castle: explore for loot.", "twilightforest", "age_exploration");
        addTopic("nether", "Nether", "Build portal: obsidian frame, light with flint and steel. Nether has: quartz, glowstone, nether wart, blaze rods, wither skeleton skulls, ghast tears. Be careful: lava, pigmen, ghasts. Bring fire resistance potions. Use teleporters for safe travel.", "minecraft", "age_exploration");
        addTopic("end", "The End", "Craft eye of ender (blaze powder + ender pearl). Locate stronghold, activate portal. Ender Dragon fight: destroy crystals first, then hit dragon. End cities have elytra and shulker boxes. Dragon drops: dragon egg (trophy), dragon breath.", "minecraft", "age_exploration");
        addTopic("lostcities", "Lost Cities", "Enter via Lost Cities dimension (RFTools dimension or craft). City buildings have: furniture, loot, streets, skyscrapers. Some are dangerous (hostile mobs). Loot: rare materials, books, furniture. Subway system connects buildings.", "lostcities", "age_exploration");
        addTopic("compactmachines", "Compact Machines", "Personal Shrinking Device: craft to enter. Machine walls: insulate the inside. Put machines inside for lag reduction. Build a factory in a 13x13x13 room. Cable in/out for items and RF.", "compactmachines", "age_exploration");
        addTopic("singularities", "Singularities", "Compress 4096 of one item into a singularity. Use Extended Crafting table (9x9). Singularities used for endgame recipes. Types: cobblestone, stone, dirt, sand, gravel, etc. Time-consuming but powerful.", "extended_crafting", "age_allthethingsomglol");
        addTopic("prestige", "Prestige Points", "Earn prestige points by completing all advancements in an age. Spend on: extra lives, starting items, bonus traits. Prestige mode unlocks after first completion. Parabox: time-travel device for prestige.", "prestige", "any");
        addTopic("parabox", "The Parabox", "Time-travel machine. Generates prestige points passively. Expensive to build. Endgame goal. Place in a safe location. Requires massive RF input.", "prestige", "age_allthethingsomglol");
        addTopic("hogs", "Resource Hogs", "Breed hogs for resources. Feed them: coal = iron, gold ore = gold, redstone = redstone, etc. Pen them near your base. Use animal feed for breeding. Each hog type gives specific drops. Automate with hoppers.", "sky_orchards", "age_farming");
        addTopic("crops", "Crops", "Plant crops on farmland. Use watering can for speed. Crops: wheat, carrot, potato, beetroot, nether wart, cocoa, melon, pumpkin. Fertile soil from mulch. Use bonsai pots for automation.", "minecraft", "age_farming");
        addTopic("animals", "Animal Husbandry", "Breed animals with food. Cow: wheat. Pig: carrot. Chicken: seeds. Sheep: wheat. Use Animal Crops for easier breeding. Animals give: leather, pork, chicken, wool, eggs.", "minecraft", "age_farming");
        addTopic("market", "The Market", "Buy items with emeralds. Craft Market: 4 planks + 2 emeralds + chest. Sells: saplings, seeds, food, animals. Random stock refreshes. Great for getting items you cannot sieve.", "farming_for_blockheads", "age_farming");
        addTopic("power", "Power Generation", "Generators: Stirling (burns fuel), Solar (sunlight), Magmatic (lava), Hydro (water), Bio (organic). Store in Energy Cells or RFTools Powercells. Transfer with Fluxducts or Energy Conduits.", "general", "age_power");
        addTopic("generators", "Generator Types", "Stirling: burns coal/charcoal. Solar: free power but slow. Advanced Solar: better. Magmatic: lava fuel. Hydro: water wheels. Bio: organic material. Nuclear: uranium (high output, dangerous). Gas-Burning: Mekanism hydrogen.", "general", "age_power");
        addTopic("enchanting", "Enchanting", "Enchanting Table + bookshelves (15 for max). Enchant with lapis. Levels: 1-30. Best enchantments: Fortune, Efficiency, Unbreaking, Sharpness, Protection. Use Anvil to combine. Tinker tools: use modifier slots instead.", "minecraft", "age_enhancement");
        addTopic("glitch", "Glitch Armor", "Craft from Glitch fragments (from Deep Mob Learning). Full set gives: flight, damage reduction, speed. Powered by Mob Data in Deep Learner. Best armor in the game when fully powered.", "deep_mob_learning", "age_enhancement");
        addTopic("collectibles", "Collectible Hunter", "Collect special items scattered across the world. Display in Collectible Case. Each collectible gives a small bonus. Find them in structures, dungeons, and hidden locations.", "sf4", "age_allthethingsomglol");
        addTopic("sacrifice", "Sacrifice System", "Drop items near the Angel. Angel analyzes your inventory and gives advice based on what you have and what you need. More items = better advice. Angel returns items it does not need.", "sf4angel", "any");
        addTopic("prayer", "Prayer Commands", "Type pray <command> in chat. Angel appears and responds. Commands: guide, how, explain, tip, topics, goal, stage, help. The angel knows everything about your current progression.", "sf4angel", "any");
        addTopic("skyorchards", "Sky Orchards", "Custom resource tree for SF4. Trees drop specific resources based on type. Oak: wood, apples. Birch: saplings. Resource trees: iron, gold, diamond, coal. Automate with bonsai pots.", "sky_orchards", "age1");
        addTopic("void", "The Void", "Do not fall. Forgiving Void mod: you respawn on top instead of dying. But you lose your items. Build platforms. Use Jetpack or Glitch Armor for safety. The void is always watching.", "forgiving_void", "age1");
        addTopic("watering", "Watering Can", "Speeds up crop and bonsai growth. Craft with iron + water bucket. Right-click to use. Essential for farming automation. Keep it filled.", "agricraft", "age_farming");
        addTopic("mulch", "Mulch Types", "Mulch improves soil fertility. Types: green (basic), blue (better), purple (best), red (faster growth). Apply to dirt to make fertile soil. Use for farming.", "bonsai_trees", "age_farming");
        addTopic("mesh", "Sieve Meshes", "Mesh tiers: flint (basic), iron (good), diamond (best). Each tier increases loot quality. Diamond mesh: rare drops, diamonds, emeralds. Upgrade as soon as possible.", "ex_nihilo", "age1");
        addTopic("hammer", "The Hammer", "Breaks 3x3 blocks. Essential for resource gathering. Craft from Tinkers or vanilla. Use on: cobblestone, gravel, sand, dust. Get more materials per block broken.", "ex_nihilo", "age1");
        addTopic("redstone", "Redstone Automation", "Redstone: the blood of machines. Use repeaters, comparators, observers. Automate sieves with hopper timers. Create sorting systems. Compact machines for lag reduction.", "minecraft", "age1");
        addTopic("hopper", "Hoppers", "Move items between inventories. Point into chest/barrel/drawer. Use for automation. Combine with redstone for timing. Essential early game automation.", "minecraft", "age1");
        addTopic("chest", "Storage", "Basic chest: 27 slots. Double chest: 54. Upgrade to: Iron Chest (54), Gold (72), Diamond (108), Obsidian (108+void). Storage Drawers for single-item bulk. Barrels for blocks.", "iron_chests", "age1");
        addTopic("tools", "Tools and Weapons", "Vanilla tools break. Tinkers tools do not. Craft Tinkers tools at Tool Station. Repair with materials in Tinker table. Upgrade materials: wood -> stone -> flint -> iron -> cobalt -> manyullyn.", "tconstruct", "age_enhancement");
        addTopic("armor", "Armor", "Vanilla armor: leather < iron < diamond < netherite. Tinkers armor via Construct's Armory. Glitch Armor: endgame, powered by mob data. Accessories: Baubles for rings, amulets.", "tconstruct", "age_enhancement");
        addTopic("ores", "Ore Processing", "1x: mine ore. 2x: Enrichment Chamber. 3x: Crusher + Compressor. 4x: Mekanism tier 3. 5x: Mekanism tier 4. Always process ores for maximum yield. Never smelt raw.", "mekanism", "age_power");
        addTopic("automation", "Automation Basics", "Hoppers for basic. Pipes for complex. Use Simple Storage for bulk. AE2 for digital. Refined Storage as alternative. Mekanism for processing. Deep Mob for mob drops.", "general", "any");
        addTopic("bosses", "Boss Fights", "Ender Dragon: destroy crystals, hit head. Twilight bosses: Naga (chase), Lich (shields+minions), Hydra (cut heads), Snow Queen (platforms). Wither: build bedrock frame. Prepare armor and food.", "general", "age_exploration");
        addTopic("food", "Food and Survival", "Cook food in furnace or Culinary Construct. Saturation matters. Golden carrots: best. Cake: easy. Bonsai apples help early. Watch hunger bar. Death by starvation is embarrassing.", "minecraft", "any");
        addTopic("flying", "Flight", "Flight options: Glitch Armor (RF-powered), Jetpack (Mekanism/iron), ViesCraft airship, RFTools dimension. End-game: creative flight via prestige. Never look down.", "general", "any");
        addTopic("lucky", "Lucky Blocks", "Break for random outcomes. Could be diamonds or lava. Worth the risk? Always. Keep backups of important items before breaking.", "lucky_blocks", "any");
        addTopic("chisel", "Chisel and Bits", "Micro-blocks for decoration. Chisel: re-texture blocks. Chisels and Bits: cut blocks into tiny pieces. Build detailed structures. Warning: lag if overused.", "chisel", "any");
        addTopic("building", "Building Tips", "Sky block = limited resources. Use Chisel for variety. Chisels and Bits for detail. Place torches everywhere (mob spawn prevention). Build platforms for safety. Keep backup gear.", "general", "any");
        addTopic("mob", "Mob Spawning", "Mobs spawn on solid blocks at light level 7 or less. Light up everything. Use mob grinder for drops. Spawner: RFTools spawner or vanilla spawner + upgrades. Hostile mobs drop useful items.", "general", "any");
        addTopic("wither", "The Wither", "Build with soul sand/soil in T-shape, place 3 wither skeleton skulls. Drops Nether Star for beacon. Use bedrock exploit for easy kill. Prepare healing potions.", "minecraft", "age_exploration");
        addTopic("beacon", "Beacon", "Craft from Nether Star + glass + obsidian. Requires pyramid of iron/gold/diamond/emerald/netherite blocks. Effects: Speed, Haste, Resistance, Jump Boost, Strength. Level 4 pyramid = all effects.", "minecraft", "age_exploration");
        addTopic("trading", "Villager Trading", "Cure zombie villagers for discounts. Librarians: enchanted books. Farmers: emeralds for crops. Weaponsmiths: diamond gear. Trade early and often for emeralds.", "minecraft", "any");
        addTopic("mobgrinder", "Mob Grinder", "Build a dark room with water channels pushing mobs to a kill point. Use hopper + chest for drops. Add Mekanism crusher for 3x drops. Essential for gunpowder, bones, ender pearls.", "general", "age_power");
        addTopic("dimension", "Dimensions", "Nether (portal), End (eyes of ender), Twilight Forest (water+flowers+diamond), RFTools (dimension builder), Lost Cities, Compact Machines, Hunting Dimension. Each has unique resources.", "general", "age_exploration");
        addTopic("rf", "RF Power", "RF = Redstone Flux. Generated by generators. Stored in energy cells. Transferred by fluxducts/conduits. Used by machines. Always have excess. Build multiple generators.", "general", "age_power");
        addTopic("fluxduct", "Power Transfer", "Fluxducts: Cryo-Stabilized (infinite), Redstone (basic), Signalum (mid), Ender (high). Energy Conduits (Ender IO) alternative. Connect generators to machines to storage.", "thermaldynamics", "age_power");
        addTopic("pipe", "Item Pipes", "Itemducts (Thermal), Conduits (Ender IO), Pipes (Buildcraft). Sort items with filters. Use for automation. Combine with redstone for conditional routing.", "general", "age_power");
        addTopic("sorting", "Item Sorting", "Simple Storage: auto-sort. AE2: precise sorting. Vanilla: hoppers + comparators. Storage Drawers: one item per drawer. Barrels: bulk storage. Route with pipes.", "general", "age_storage");
        addTopic("emerald", "Emeralds", "Get from: mining, trading, sieving. Use at Market for items. Trade with villagers for enchanted books. Emerald blocks for beacon pyramids. Precious early game.", "minecraft", "any");
        addTopic("diamond", "Diamonds", "Rare drop from diamond mesh sieve. Mekanism enrichment: 2x yield. Fortune III: more drops. Essential for tools, enchanting, and beacon. Hoard them.", "minecraft", "any");
        addTopic("obsidian", "Obsidian", "Lava + water = obsidian. Mine with diamond pick. Use for: Nether portal, enchanting table, Ender Chest, crafting. Cobble gen can make it with compressed.", "minecraft", "any");
        addTopic("lava", "Lava", "Lava: power source, obsidian maker, nether fuel. Get from: sieving, crucible, Nether. Use in Magmatic Generator. Never fall in. Essential for cobble gen.", "general", "any");
        addTopic("water", "Water", "Infinite water source: 2x2 water. Essential for cobble gen, crops,animals. Watering can for speed. Never enough water in sky block.", "minecraft", "any");
        addTopic("torch", "Torches", "Light prevents mob spawns. Place everywhere. Charcoal torches early game. Redstone torches for automation. Glowstone for decoration. Light level 8+ = safe.", "minecraft", "any");
        addTopic("charcoal", "Charcoal", "Burn wood in furnace. Better than coal for early game. Use in Stirling Generator. Make torches. Essential early resource.", "minecraft", "age1");
        addTopic("string", "String", "Get from: spiders, sieving, market. Use for: bows, wool (crafting), Tinkers nets. Important early resource. Sieve gravel/sand for it.", "minecraft", "any");
        addTopic("bone", "Bones", "Get from: skeletons, sieving. Bone meal: speed up crop growth. Essential for farming. Grind in Mekanism for more bone meal.", "minecraft", "age_farming");
        addTopic("gunpowder", "Gunpowder", "Get from: creepers, sieving, witches. Use for: TNT, fireworks, brewing. Automate with mob grinder.", "minecraft", "any");
        addTopic("ender_pearl", "Ender Pearls", "Get from: Endermen, sieving (end stone), trading. Use for: Eye of Ender, Ender Chest, teleportation. Essential for End progression.", "minecraft", "age_exploration");
        addTopic("blaze", "Blaze Rods", "Get from: Blazes in Nether, sieving. Use for: Eyes of Ender, brewing stands, Blaze Powder. Essential for End progression.", "minecraft", "age_exploration");
        addTopic("netherwart", "Nether Wart", "Found in Nether fortresses. Grow on soul sand. Use for: potions. Essential for brewing. Grow a farm early.", "minecraft", "age_exploration");
        addTopic("potions", "Potions", "Brewing Stand + Blaze Powder. Essential potions: Fire Resistance (Nether), Healing, Strength (bosses), Night Vision (exploration), Speed. Splash potions for combat.", "minecraft", "any");
        addTopic("anvil", "Anvil", "Combine enchanted books with tools. Repair items. Rename items. Requires iron. Essential for enchanting workflow.", "minecraft", "any");
        addTopic("brewing", "Brewing", "Brewing Stand + Blaze Powder fuel. Nether Wart base. Add modifiers: Magma Cream (Fire Res), Glistering Melon (Healing), Spider Eye (Poison), Ghast Tear (Regeneration).", "minecraft", "any");
        addTopic("automation_tips", "Automation Tips", "Start simple: hoppers. Progress to: Simple Storage. End game: AE2. Always automate: sieves, bonsai, mob drops, ore processing. Never automate: decorative building.", "general", "any");
        addTopic("lag", "Lag Prevention", "Limit: tile entities, entities, particles. Use Compact Machines for dense setups. Limit render distance. Use Storage Drawers over barrels. Reduce mob caps with lights.", "general", "any");
        addTopic("death", "Death and Respawning", "You will die. Accept it. Forgiving Void: respawn on platform. Keep backup gear. Do not carry everything. Back up important builds. The angel will find you.", "general", "any");
        addTopic("start", "Getting Started", "1. Punch tree. 2. Make crafting table. 3. Make tools. 4. Plant sapling on dirt. 5. Build cobble gen. 6. Get sieve. 7. Sift for resources. 8. Build upwards. Do not fall.", "general", "age1");
        addTopic("progression", "Game Progression", "Age 1: basic resources -> Age Farming: food and animals -> Age Enhancement: tools and armor -> Age Power: machines -> Age Storage: organization -> Age Exploration: dimensions -> Age OMG: endgame.", "general", "any");
        addTopic("ex_nihilo", "Ex Nihilo Overview", "Core sky block mod. Sieve, hammer, crucible, barrel. Sieve: sift sifted blocks for resources. Hammer: break 3x3 blocks. Crucible: melt blocks into water/lava. Barrel: compost organic material into dirt. Mesh tiers: flint < iron < diamond. Automate with hoppers and Simple Storage cables.", "ex_nihilo", "age1");
        addTopic("crucible", "The Crucible", "Place over heat source (torch, lava). Put blocks in to melt. Cobblestone -> water. Organic blocks -> lava. Essential for infinite water and lava. Use stone barrel for better efficiency.", "ex_nihilo", "age1");
        addTopic("barrel_mechanic", "Ex Nihilo Barrel", "Compost organic material (leaves, saplings, cactus) into dirt. Place over water to make clay. Place over lava to make obsidian. Essential early game for dirt production.", "ex_nihilo", "age1");
        addTopic("compressed", "Compressed Blocks", "Craft 9 of a block into compressed version. Compressed cobble, compressed dirt, compressed sand. Sieve compressed blocks for better loot. CompressedObsidian: endgame crafting material.", "ex_nihilo", "age1");
        addTopic("dust", "Dust", "Craft from compressed sand. Sieve dust for: redstone, glowstone, string, bone meal. Essential resource in sky block. Dust mesh: iron tier recommended.", "ex_nihilo", "age1");
        addTopic("gravel", "Gravel", "Hammer cobblestone to get gravel. Sieve gravel for: iron, gold, copper, tin, lead, silver, flint. Essential early game resource loop. Automate with auto-hammer.", "ex_nihilo", "age1");
        addTopic("sand", "Sand", "Hammer gravel to get sand. Sieve sand for: silicon, aluminum, emeralds, lapis, quartz. Important mid-game resource. compressed sand -> dust when hammered.", "ex_nihilo", "age1");
        addTopic("hammer_auto", "Auto Hammer", "Craft from iron and flint. Automatically hammers blocks. Place on top of chest. Feed with cobblestone. Outputs: gravel -> sand -> dust. Automate with hopper input/output.", "ex_nihilo", "age1");
        addTopic("stone_generation", "Stone Generation", "Cobble gen: lava + water. Stone gen: cobble in furnace. Basalt: lava over soul sand. Granite/Diorite/Andesite: automate withstone cuts. Compress everything for sieve.", "general", "age1");
        addTopic("string_farm", "String Farm", "Sift sand/dust for string. Or: spider spawner + grinder. String used for: bows, wool (crafting), Tinkers nets. Essential for early game tools.", "general", "age1");
        addTopic("flint_hunting", "Flint", "Gravel drops flint when sifted (iron mesh). Use for: flint tools, flint mesh, arrows. First mesh material. Important early game.", "ex_nihilo", "age1");
        addTopic("lapis", "Lapis Lazuli", "Sift sand for lapis. Use for: enchanting (required), blue dye, Lapis Block. Essential for enchanting workflow. Hoard it.", "minecraft", "age1");
        addTopic("redstone_farm", "Redstone Farm", "Sift gravel/dust for redstone. Or: deep mob learning data models. Redstone needed for: automation, Mekanism, RFTools, AE2 channels. Never enough.", "general", "age1");
        addTopic("glowstone", "Glowstone", "Sift dust for glowstone dust. Craft into blocks. Use for: lighting, crafting, decoration. Or: Glowstone generator (Mekanism).", "minecraft", "age1");
        addTopic("quartz", "Nether Quartz", "Mine in Nether. Use for: Redstone comparators, Tinkers tools (modifier), Observers. Essential for automation and redstone.", "minecraft", "age_exploration");
        addTopic("coal", "Coal", "Sift gravel for coal. Use for: torches, Stirling Generator fuel, steel production. Compress for storage. Essential resource.", "minecraft", "age1");
        addTopic("copper", "Copper", "Sift gravel for copper. Used in: Thermal Dynamics pipes, Mekanism cables, Tinkers tools. Mid-game essential. Process 2x with Mekanism.", "thermalfoundation", "age1");
        addTopic("tin", "Tin", "Sift gravel for tin. Used in: Tin Cans (food storage), Basic Circuit (Mekanism), Bronze crafting. Combine with copper for bronze.", "thermalfoundation", "age1");
        addTopic("lead", "Lead", "Sift gravel for lead. Used in: Lead Storage Blocks, Ender IO conduits, Mekanism. Heavy but useful. Process with Mekanism for yield.", "thermaldynamics", "age1");
        addTopic("silver", "Silver", "Sift gravel for silver. Used in: Signalum (Thermal), Manyullyn alloy (Tinkers), Lunar crucible. Rare but valuable.", "thermalfoundation", "age1");
        addTopic("aluminum", "Aluminum", "Sift sand for aluminum. Used in: Tinkers Construct (aluminum brass for casts), Clear Glass. Important for smeltery setup.", "tconstruct", "age_enhancement");
        addTopic("cobalt", "Cobalt", "Mine in Nether (blue ore). Used for: Tinkers tools (fastest mining), Cobalt tools. Requires diamond pick or Tinkers pick. Mine with fortune.", "tconstruct", "age_exploration");
        addTopic("ardite", "Ardite", "Mine in Nether (orange ore). Used for: Manyullyn alloy (Cobalt + Ardite), Tinkers tools (stonebound). Never breaks. Mine with fortune.", "tconstruct", "age_exploration");
        addTopic("manyullyn", "Manyullyn", "Smelt Cobalt + Ardite in smeltery. Best Tinkers material. Extremely durable, fast mining, no special ability. Endgame tool material.", "tconstruct", "age_enhancement");
        addTopic("osmium", "Osmium", "Sift gravel for osmium. Mekanism core resource. Used for: Basic machines, Osmium Compressor, Mekanism tools/armor. Essential for Mekanism progression.", "mekanism", "age_power");
        addTopic("steel", "Steel", "Coal + Iron in Metallurgic Infuser (Mekanism). Or: Iron + Carbon in NuclearCraft. Used for: Mekanism machines, NuclearCraft components, Structural Compressor.", "mekanism", "age_power");
        addTopic("bronze", "Bronze", "3 Copper + 1 Tin in Metallurgic Infuser. Used for: Tinkers tools (better than iron), Thermal machines, Structural Compressor. Good mid-game alloy.", "thermalfoundation", "age_enhancement");
        addTopic("signalum", "Signalum", "4 Silver + 4 Redstone + Enderium in Induction Smelter. Used for: Signalum Fluxducts (high RF transfer), Thermal machines. Endgame Thermal material.", "thermaldynamics", "age_power");
        addTopic("enderium", "Enderium", "2 Platinum + 1 Ender Pearl + 2 Resonant Ender in Induction Smelter. Best Thermal material. Used for: Enderium Fluxducts (infinite transfer), endgame machines.", "thermaldynamics", "age_power");
        addTopic("platinum", "Platinum", "Rare drop from Osmium ore with Fortune. Used for: Enderium, Flux Cores, endgame crafting. Very rare, process with Mekanism for yield.", "thermalfoundation", "age_power");
        addTopic("dsu", "Deep Storage Unit", "CompactStorage mod. Store massive amounts of one item type. Place next to drawer controller. Useful for bulk storage of cobblestone, dirt.", "compactstorage", "age_storage");
        addTopic("ender_chest", "Ender Chest", "3 Eyes of Ender + Obsidian. Access same inventory from any Ender Chest. Use for: remote access to important items. Place one at base, one in Nether/End.", "minecraft", "age_exploration");
        addTopic("ender_io", "Ender IO Overview", "Machines, conduits, alloy smelter, SAG Mill. SAG Mill: 2x ore output. Alloy Smelter: make alloys. Conduits: item/fluid/energy in one block. Capacitor Bank: RF storage.", "enderio", "age_power");
        addTopic("sag_mill", "SAG Mill", "Ender IO machine. Grind ores for 2x output. Use with Dark Steel Ball for bonus. Essential for ore processing before Mekanism. Needs RF power.", "enderio", "age_power");
        addTopic("alloy_smelter", "Alloy Smelter", "Ender IO machine. Make alloys: Electrical Steel, Energetic Alloy, Vibrant Alloy, Pulsating Iron. Essential for conduit and machine crafting.", "enderio", "age_power");
        addTopic("conduits", "Ender IO Conduits", "Item conduits, fluid conduits, energy conduits, redstone conduits, ME conduits. All share same block space. Place multiple in one block. Essential for compact automation.", "enderio", "age_power");
        addTopic("capacitor_bank", "Capacitor Bank", "Ender IO RF storage. Basic -> Capacitor -> Vibrant -> Millent. Store power for machines. Place next to generators. Essential for power buffering.", "enderio", "age_power");
        addTopic("thermal_expansion", "Thermal Expansion Overview", "Machines, dynamos, fluxducts, cell. Machines: Pulverizer (2x ore), Sawmill, Induction Smelter. Dynamos: Reactant, Magmatic, Numismatic. Fluxducts: power transfer.", "thermalexpansion", "age_power");
        addTopic("pulverizer", "Pulverizer", "Thermal Expansion machine. Crush ores for 2x output. Use with Fluxed Augment for 3x. Essential early-mid game ore processing. Needs RF.", "thermalexpansion", "age_power");
        addTopic("induction_smelter", "Induction Smelter", "Thermal Expansion machine. Smelt 2 ores at once, 3x with Rich Slag. Make alloys: Enderium, Signalum. Essential for Thermal progression.", "thermalexpansion", "age_power");
        addTopic("redstone_fluxduct", "Fluxducts", "Thermal Dynamics power transfer. Basic (low), Hardened (mid), Signalum (high), Enderium (infinite). Cryo-Stabilized: infinite transfer, endgame. Use for all power networks.", "thermaldynamics", "age_power");
        addTopic("itemduct", "Itemducts", "Thermal Dynamics item transfer. Basic, Hardened, Signalum. Use pneumatic servo for filtering. Connect machines to chests/drawers. Essential for automation.", "thermaldynamics", "age_power");
        addTopic("fluiduct", "Fluiducts", "Thermal Dynamics fluid transfer. Basic, Hardened, Signalum. Transfer water, lava, molten metals. Connect crucible to tanks. Essential for fluid automation.", "thermaldynamics", "age_power");
        addTopic("mekanism_generators", "Mekanism Generators", "Heat Generator (passive), Solar Generator (daylight), Gas-Burning Generator (hydrogen), Turbine (steam). Essential for Mekanism power. Build multiple.", "mekanism_generators", "age_power");
        addTopic("heat_generator", "Heat Generator", "Mekanism passive power. Place next to lava or hot blocks. Produces RF continuously. Good early Mekanism generator. No fuel needed.", "mekanism_generators", "age_power");
        addTopic("gas_burning_generator", "Gas-Burning Generator", "Burns Hydrogen for RF. Hydrogen from Electrolytic Separator (water split). High output generator. Essential for mid-game Mekanism power.", "mekanism_generators", "age_power");
        addTopic("enrichment_chamber", "Enrichment Chamber", "Mekanism machine. Ore -> Enriched Ore (2x output). First step in ore processing chain. Use on all ores. Essential for resource multiplication.", "mekanism", "age_power");
        addTopic("crusher_mek", "Crusher", "Mekanism machine. Enriched Ore -> Dust (3x output). Second step in ore processing. Requires Crusher from Mekanism. High RF cost.", "mekanism", "age_power");
        addTopic("combiner", "Combiner", "Mekanism machine. Reverse of Crusher. Dust + Enriched Ore -> Compressed Block. Use for storage or compression. Rarely used but useful.", "mekanism", "age_power");
        addTopic("metallurgic_infuser", "Metallurgic Infuser", "Mekanism machine. Infuse materials: Carbon into Iron = Steel. Redstone into Osmium = Osmium Compressor input. Essential for alloys.", "mekanism", "age_power");
        addTopic("digital_miner", "Digital Miner", "Mekanism endgame machine. Auto-mine specific ores. Set filter: diamond, emerald, etc. Requires massive RF. Place at bedrock. Teleport items to base.", "mekanism", "age_power");
        addTopic("teleporter", "Teleporter", "Mekanism teleporter. Place at base and destination. Requires Teleportation Core. Instant travel between dimensions. Essential for late-game.", "mekanism", "age_power");
        addTopic("mekanism_ore_process", "Mekanism Ore Processing Chain", "Step 1: Enrichment Chamber (ore->enriched, 2x). Step 2: Crusher (enriched->dust, 3x). Step 3: Precision Sawmill (log->planks, 3x). Step 4: Chemical Injection Chamber (4x, endgame). Step 5: Chemical Dissolution Chamber (5x, ultimate). Always process ores.", "mekanism", "age_power");
        addTopic("jetpack", "Mekanism Jetpack", "Craft from Jetpack Unit + Chestplate. Press SPACE to fly. Use hydrogen fuel. Essential for sky block traversal. Refuel with Hydrogen Canister.", "mekanism", "age_enhancement");
        addTopic("mekanism_armor", "Mekanism Armor", "Mekasuit: full Mekanism endgame armor. Powered by RF. Provides: damage protection, auto-feeding, fall damage negation, sprint boost. Expensive but worth it.", "mekanism", "age_enhancement");
        addTopic("mekanism_tools", "Mekanism Tools", "Atomic Disassembler: multi-tool, modes (3x3, 5x5, vein mine). Paxel: pickaxe+axe+shovel. Meka-Armor: powered armor set. All need RF.", "mekanism", "age_enhancement");
        addTopic("chemical_dissolution", "Chemical Dissolution Chamber", "Mekanism endgame. Dissolve ores into Slurry. 5x processing. Requires massive infrastructure. Ultimate ore processing.", "mekanism", "age_power");
        addTopic("chemical_washer", "Chemical Washer", "Mekanism machine. Clean Dirty Slurry into Clean Slurry. Required in 5x ore processing chain. Needs Water and RF.", "mekanism", "age_power");
        addTopic("chemical_crystallizer", "Chemical Crystallizer", "Mekanism endgame. Convert Clean Slurry back to crystals. Final step in 5x processing. Outputs enriched crystals for maximum yield.", "mekanism", "age_power");
        addTopic("bin", "Bin", "Mekanism storage. Store massive amounts of one item type. Basic -> Advanced -> Elite -> Ultimate. Upgrade with Bin Additions. Essential for bulk storage.", "mekanism", "age_storage");
        addTopic("rotary_condensentrator", "Rotary Condensentrator", "Mekanism machine. Convert between Gas and Liquid forms. Essential for: Hydrogen Canister, Oxygen production. Mid-game Mekanism machine.", "mekanism", "age_power");
        addTopic("electrolytic_separator", "Electrolytic Separator", "Mekanism machine. Split Water into Hydrogen + Oxygen. Hydrogen for Gas-Burning Generator. Oxygen for chemical processes. Essential for Mekanism power.", "mekanism", "age_power");
        addTopic("nuclearcraft_fission", "NuclearCraft Fission", "Reactor: reactor casing + controller + fuel rods + coolant. Generate massive RF. Fuel: Uranium, LEU-235, MOX. DANGER: overheating causes explosion. Monitor temperature.", "nuclearcraft", "age_power");
        addTopic("nuclearcraft_fusion", "NuclearCraft Fusion", "Fusion reactor: much more powerful than fission. Fuel: Tritium + D-T Fuel. Requires starter energy. Endgame power source. Place in void to avoid explosion damage.", "nuclearcraft", "age_power");
        addTopic("nuclearcraft_alloys", "NuclearCraft Alloys", "Alloy Furnace: make Tough Alloy, Hard Carbon, Magnesium Diboride, etc. Used for: advanced machines, fusion components, RTG. Essential for NuclearCraft progression.", "nuclearcraft", "age_power");
        addTopic("rtg", "RTG", "Radioisotope Thermoelectric Generator. Uses Plutonium-238. Steady, low-power output. Place next to machines. No fuel management needed. Good for passive power.", "nuclearcraft", "age_power");
        addTopic("nuclearcraft_processors", "NuclearCraft Processors", "Alloy Smelter, Electrolyzer, Ingot Former, Melter, Manufactory. Each does specific processing. Essential for NuclearCraft item production. Requires RF.", "nuclearcraft", "age_power");
        addTopic("uranium", "Uranium", "Mine in Nether or sift for it. NuclearCraft fuel. Process: Uranium Ingot -> LEU-235 -> HEU-235. Also for Mekanism Enrichment. Essential for nuclear power.", "nuclearcraft", "age_exploration");
        addTopic("plutonium", "Plutonium", "From spent NuclearCraft fuel. Used for: RTG, MOX fuel, Plutonium processing. Store in lead-lined cells. Dangerous but powerful.", "nuclearcraft", "age_power");
        addTopic("rftools_crafter", "RFTools Crafter", "Auto-crafting machine. Set recipes, store materials, craft on demand. Connect to storage network. Essential for automation of complex recipes.", "rftools", "age_power");
        addTopic("rftools_spawner", "RFTools Spawner", "Spawn any mob using DNA samples. Place in dark room. Connect to mob grinder. Essential for mob drop automation. No spawner needed.", "rftools", "age_power");
        addTopic("rftools_scanner", "RFTools Scanner", "Scan for specific blocks in range. Use for finding ores, structures. Place at base. Scan results shown on screen controller.", "rftools", "age_power");
        addTopic("rftools_screen", "RFTools Screen", "In-game display. Show power, items, mob data. Place screens next to screen controller. Essential for monitoring automation setups.", "rftools", "age_power");
        addTopic("dimlet", "Dimlets", "RFTools dimension creation components. Each dimlet adds a property: terrain, features, time, weather. Combine in Dimension Builder. Create custom worlds.", "rftools", "age_power");
        addTopic("dimension_builder", "Dimension Builder", "RFTools machine. Build custom dimensions using dimlets. Costs RF to maintain. Create resource-rich dimensions for mining. Endgame power sink.", "rftools", "age_power");
        addTopic("powercell", "Powercell", "RFTools wireless RF transfer. Place multiple, connect via network. Transfer power between locations without cables. Essential for remote bases.", "rftools", "age_power");
        addTopic("simulation_chamber", "Simulation Chamber", "Deep Mob Learning machine. Simulate mob kills without spawning mobs. Train data models. Output: pristine matters for crafting. Needs RF.", "deep_mob_learning", "age_power");
        addTopic("deep_learner", "Deep Learner", "Deep Mob Learning item. Wear to collect mob data from kills. Each model trains as you kill. Full training = pristine drops. Essential for Glitch Armor.", "deep_mob_learning", "age_power");
        addTopic("data_model", "Data Models", "Deep Mob Learning items. Each model trains on specific mob type. Training levels: Basic -> Improved -> Superior -> Ultimate. Higher = better pristine chance.", "deep_mob_learning", "age_power");
        addTopic("pristine", "Pristine Matters", "Deep Mob Learning drops. From fully trained data models in Simulation Chamber. Used for: Glitch Armor, advanced crafting. Essential endgame material.", "deep_mob_learning", "age_enhancement");
        addTopic("glitch_fragment", "Glitch Fragments", "Deep Mob Learning drops. From Simulation Chamber. Craft into Glitch Armor. 4 fragments = 1 Glitch piece. Essential for best armor set.", "deep_mob_learning", "age_enhancement");
        addTopic("ae2_controller_multiblock", "AE2 Controller Multiblock", "ME Controller: 7x7x7 max. Must have at least 1 controller. Controller provides 32 channels per face. Ring structure recommended. Use ME Cable to connect.", "ae2", "age_storage");
        addTopic("ae2_channels", "AE2 Channels", "Each device needs 1 channel. Controller provides 32 per face. Dense Cable carries 32. Regular Cable carries 8. Plan channel layout carefully.", "ae2", "age_storage");
        addTopic("ae2_storage_cells", "AE2 Storage Cells", "1k -> 4k -> 16k -> 64k -> 256k. Higher = more items stored. Craft in Molecular Assembler. Use Storage Housing for portability. Essential for digital storage.", "ae2", "age_storage");
        addTopic("ae2_autocrafting", "AE2 Autocrafting", "Molecular Assembler: craft items automatically. Set patterns in pattern terminal. Connect to ME network. Essential for mass production. Needs CPU for crafting.", "ae2", "age_storage");
        addTopic("ae2_pattern", "AE2 Patterns", "Encoded recipes for autocrafting. Pattern Terminal: encode patterns. Molecular Assembler: execute patterns. Essential for AE2 automation.", "ae2", "age_storage");
        addTopic("ae2_cpucore", "AE2 Crafting CPU", "Crafting CPU: multi-block for autocrafting. Each CPU handles one crafting job. Make bigger for complex recipes. Essential for AE2 autocrafting.", "ae2", "age_storage");
        addTopic("ae2_import_bus", "AE2 Import Bus", "Pull items FROM inventory INTO ME network. Place on chest, barrel, machine. Use for: auto-storing items from machines. Essential for automation.", "ae2", "age_storage");
        addTopic("ae2_export_bus", "AE2 Export Bus", "Push items FROM ME network INTO inventory. Place on furnace, machine. Use for: auto-supplying machines. Essential for automation.", "ae2", "age_storage");
        addTopic("ae2_level_emitter", "AE2 Level Emitter", "Emit redstone signal when item count reaches threshold. Use for: auto-crafting triggers, overflow protection. Essential for smart automation.", "ae2", "age_storage");
        addTopic("ae2_energy_cell", "AE2 Energy Cell", "Store RF for ME network. Place next to controller. Essential for stable power supply. Make multiple for large networks.", "ae2", "age_storage");
        addTopic("ae2_p2p", "AE2 P2P Tunnels", "Point-to-Point tunnels. Transfer items/fluids/RF/channels through one cable. Essential for compact networks. Use memory card to link.", "ae2", "age_storage");
        addTopic("simple_storage_cable", "Simple Storage Cables", "Connect inventories to network. Place on chests, barrels, drawers. Limited to 500 items per network. Essential for early-mid game storage.", "simple_storage", "age_storage");
        addTopic("simple_storage_terminal", "Simple Storage Terminal", "Access all connected items. Click to pull items. Essential for Simple Storage network. Limited compared to AE2 but easier to build.", "simple_storage", "age_storage");
        addTopic("simple_storage_request", "Request Table", "Pull items on demand from Simple Storage network. Like AE2 terminal but simpler. Essential for mid-game crafting.", "simple_storage", "age_storage");
        addTopic("drawer_controller", "Drawer Controller", "Storage Drawers master. Place in center of drawer wall. Access all drawers from one point. Connect to storage network. Essential for drawer setups.", "storagedrawers", "age_storage");
        addTopic("drawer_upgrades", "Drawer Upgrades", "Void Upgrade: destroy excess items. Emerald Upgrade: infinite storage. Compacting: stores ingots+blocks. Obsidian: protects from explosions. Essential for drawers.", "storagedrawers", "age_storage");
        addTopic("compact_storage_crafting", "Compact Storage Crafting", "Craft compact storage blocks. 9x9 crafting table needed. Store massive amounts in small space. Essential for endgame storage.", "compactstorage", "age_storage");
        addTopic("twilight_forest_bosses", "Twilight Forest Bosses", "Naga (forest, chase fight), Lich (tower, shields+minions), Ghast (swamp, ranged), Hydra (nexus, cut heads), Ur-Gast (fire), Knight Phantom (castle), Snow Queen (ice). Each drops unique loot.", "twilightforest", "age_exploration");
        addTopic("twilight_forest_portal", "Twilight Forest Portal", "2x2 water surrounded by flowers/plants. Throw Diamond in. Portal activates. Enter for Twilight dimension. Bring good gear.", "twilightforest", "age_exploration");
        addTopic("ironwood", "Ironwood", "Twilight Forest material. From Ironwood roots. Used for: Ironwood tools/armor. Better than iron. Mine Ironwood trees in forest.", "twilightforest", "age_exploration");
        addTopic("fiery", "Fiery Material", "Twilight Forest boss drops. Used for: Fiery tools/armor. Sets mobs on fire. From Hydra and Knight Phantom. Endgame Twilight material.", "twilightforest", "age_exploration");
        addTopic("knightly_metal", "Knightly Metal", "Twilight Forest material. From Knight Phantom. Used for: Knightly tools/armor. Good stats. Requires boss kill to obtain.", "twilightforest", "age_exploration");
        addTopic("naga_scale", "Naga Scale", "Twilight Forest. Naga drops scales. Used for: Naga Scale armor. Good early-mid Twilight armor. Naga fight: chase it through the forest.", "twilightforest", "age_exploration");
        addTopic("lich_fang", "Lich Fang", "Twilight Forest. Lich drops fangs. Used for: Lich Sword (summons minions). Useful weapon. Lich fight: break shields, attack when vulnerable.", "twilightforest", "age_exploration");
        addTopic("lost_cities_furniture", "Lost Cities Furniture", "Found in buildings. Chairs, tables, lamps. Decorative only. Useful for base building. Some buildings have better loot than others.", "lostcities", "age_exploration");
        addTopic("lost_cities_subway", "Lost Cities Subway", "Underground tunnel system. Connects buildings. Contains loot chests. Can be dangerous (hostile mobs). Use for fast travel between buildings.", "lostcities", "age_exploration");
        addTopic("compact_machine_sizes", "Compact Machine Sizes", "1: 3x3x3. 2: 5x5x5. 3: 7x7x7. 4: 9x9x9. 5: 11x11x11. 6: 13x13x13. Larger = more machines inside. Use Personal Shrinking Device to enter.", "compactmachines", "age_exploration");
        addTopic("personal_shrinking_device", "Personal Shrinking Device", "Craft to enter Compact Machines. Right-click on machine wall to enter. Exit by right-clicking inside wall. Essential for Compact Machines.", "compactmachines", "age_exploration");
        addTopic("extended_crafting", "Extended Crafting", "9x9 crafting table. Use for: Singularities, complex recipes. Automation with Crafters. Essential for endgame crafting.", "extended_crafting", "age_allthethingsomglol");
        addTopic("singularity_crafting", "Singularity Crafting", "Compress 4096 of one item into Singularity. Use Extended Crafting table. Takes time but worth it. Used for endgame recipes.", "extended_crafting", "age_allthethingsomglol");
        addTopic("prestige_points", "Prestige Points", "Earn by completing age advancements. Spend on: extra lives, starting items, bonuses. Prestige mode: harder but more rewards. Endgame replayability.", "prestige", "any");
        addTopic("parabox_time", "Parabox Time Travel", "Build Parabox. Place in base. Generates prestige points passively. Requires massive RF input. Endgame goal. Time-travel device.", "prestige", "age_allthethingsomglol");
        addTopic("collectible_case", "Collectible Case", "Display collectibles. Craft from glass and iron. Place on wall. Each collectible gives small bonus. Find all for completionist achievement.", "sf4", "age_allthethingsomglol");
        addTopic("tinkers_materials", "Tinkers Materials Guide", "Wood (basic) -> Stone (better) -> Flint (decent) -> Iron (good) -> Copper (thermal) -> Bronze (alloy) -> Alumite (ender) -> Cobalt (fast) -> Ardite (unbreaking) -> Manyullyn (best). Each has stats.", "tconstruct", "age_enhancement");
        addTopic("tinkers_modifiers", "Tinkers Modifiers", "Redstone (speed), Lapis (fortune), Gold (extra slot), Diamond (durability+mining), Emerald (durability), Quartz (sharpness), Moss (auto-repair), Diamond + Gold (auto-repair). Apply at tool station.", "tconstruct", "age_enhancement");
        addTopic("tinkers_smeltery_setup", "Smeltery Setup", "Seared Bricks (crafting table): Smeltery Controller, Seared Tank, Casting Table, Casting Basin. Place in 3x3x2 structure. Fill with lava. Smelt ores 2x.", "tconstruct", "age_enhancement");
        addTopic("tinkers_casting", "Tinkers Casting", "Cast pattern on Casting Table. Pour molten metal. Make: tool parts, blocks, ingots. Essential for Tinkers progression. Use for all tool crafting.", "tconstruct", "age_enhancement");
        addTopic("construct_armory", "Construct's Armory", "Tinkers armor mod. Armor Station: craft armor. Armor Anvil: modify armor. Materials: same as Tinkers tools. Essential for Tinkers armor progression.", "conarm", "age_enhancement");
        addTopic("tool_leveling", "Tinkers Tool Leveling", "Tools gain XP from use. Level up to add modifiers. Each level = 1 modifier slot. Essential for maxing out tools. Use Tool Belt for storage.", "tinkerstoolleveling", "age_enhancement");
        addTopic("bonsai_types", "Bonsai Tree Types", "Oak: wood, apples, sticks, saplings. Birch: saplings, sticks. Spruce: wood, pine cones. Jungle: wood, cocoa. Dark Oak: wood, apples. Acacia: wood. Each type = different drops.", "bonsai_trees", "age_farming");
        addTopic("bonsai_hopper", "Bonsai Hopper", "Place under bonsai pot. Auto-collect drops. Essential for bonsai automation. Connect to drawer or chest. Never manually harvest again.", "bonsai_trees", "age_farming");
        addTopic("animal_crops_types", "Animal Crops Types", "Each crop grows into animal. Cow, Pig, Chicken, Sheep, Rabbit, Wolf, Ocelot, Horse. Use seeds on farmland. Break when grown. Essential for animal farming.", "animal_crops", "age_farming");
        addTopic("resource_hog_breeding", "Resource Hog Breeding", "Feed hogs specific items for resources. Coal hog -> iron. Gold ore hog -> gold. Redstone hog -> redstone. Diamond hog -> diamond. Breed with Animal Feed.", "sky_orchards", "age_farming");
        addTopic("fertile_soil", "Fertile Soil", "Craft from dirt + mulch. Increases crop growth speed. Essential for farming. Place under farmland for bonus. Multiple tiers available.", "bonsai_trees", "age_farming");
        addTopic("watering_can_upgrade", "Watering Can Upgrade", "Basic -> Improved -> Ultimate. Each tier: faster growth, larger area. Craft with iron + water. Essential for farming. Keep filled.", "agricraft", "age_farming");
        addTopic("farmers_delight", "Farming for Blockheads", "Market: buy seeds and animals with emeralds. Fertilized farmland: better crop growth. Essential for getting rare items you cannot sieve.", "farming_for_blockheads", "age_farming");
        addTopic("ender_crop", "Ender Crop", "Grow Ender Pearls. Plant Ender Seed on farmland. Needs: Ender Pearl. Slow growth but produces Ender Pearls. Essential for late-game.", "ender_crop", "age_farming");
        addTopic("cactus_farm", "Cactus Farm", "Place cactus on sand. Break when grown. Use for: green dye, composting into dirt, crucible for water. Essential early game resource.", "minecraft", "age_farming");
        addTopic("sugar_cane", "Sugar Cane Farm", "Plant on sand next to water. Grow for: paper, sugar, books. Automate with hopper. Essential for: enchanting table (bookshelves).", "minecraft", "age_farming");
        addTopic("mob_grinder_setup", "Mob Grinder Setup", "Dark room (light level 0) with water channels pushing mobs to kill point. Use Mekanism Crusher for drops. Hopper + chest for collection. Essential for mob drops.", "general", "age_power");
        addTopic("xp_farm", "XP Farm", "Mob grinder with experience orbs. Stand near kill point for XP. Use for: enchanting, anvil repairs. Essential for enchanting workflow.", "general", "age_power");
        addTopic("ender_pearl_farm", "Ender Pearl Farm", "RFTools Spawner: spawn Endermen. Dark room with water. Kill for pearls. Essential for End progression. Or: Ender Crop for passive production.", "general", "age_exploration");
        addTopic("blaze_farm_setup", "Blaze Farm", "Nether fortress spawner. Dark room with water channels. Kill for blaze rods. Essential for: Eyes of Ender, brewing stands. Dangerous to set up.", "general", "age_exploration");
        addTopic("witherskeleton_farm", "Wither Skeleton Farm", "Nether fortress spawner. Dark room with water. Kill for skulls. Need 3 for Wither. Rare drop rate. Use Looting III sword.", "general", "age_exploration");
        addTopic("ghast_farm", "Ghast Farm", "Nether ceiling. Build platform. Ghasts spawn in open spaces. Kill for: Ghast Tears (regeneration potions), Gunpowder. Dangerous but valuable.", "general", "age_exploration");
        addTopic("pigman_farm", "Zombie Pigman Farm", "Nether portal-based or natural spawning. Kill for: Gold Nuggets, Gold Ingots, Rotten Flesh. Gold is essential for: powered rails, golden apples.", "general", "age_exploration");
        addTopic("slime_farm", "Slime Farm", "Slime chunks in Nether or Overworld. Dark room. Kill for: Slimeballs. Use for: sticky pistons, leads, slime blocks. Essential for redstone.", "minecraft", "age_exploration");
        addTopic("witch_farm", "Witch Farm", "Witch hut-based. Dark room with water. Kill for: Glowstone, Redstone, Sugar, Sticks, Bottles, Spider Eyes. Essential for potion ingredients.", "general", "age_exploration");
        addTopic("skeleton_farm", "Skeleton Farm", "Skeleton spawner. Dark room with water. Kill for: Bones (bone meal), Arrows, Bows. Essential for farming and combat.", "general", "age1");
        addTopic("zombie_farm", "Zombie Farm", "Zombie spawner. Dark room with water. Kill for: Rotten Flesh, Iron Ingots (rare), Carrots (rare). Use for: Cleric trading.", "general", "age1");
        addTopic("spider_farm", "Spider Farm", "Spider spawner. Dark room. Kill for: String, Spider Eyes. Essential for: bows, brewing, wool crafting.", "general", "age1");
        addTopic("creeper_farm", "Creeper Farm", "Dark room. Kill for: Gunpowder. Essential for: TNT, rockets (elytra). Use cat to scare creepers into trap.", "general", "age1");
        addTopic("enderman_farm", "Enderman Farm", "End platform. Spawn Endermen. Kill for: Ender Pearls. Most efficient in End dimension. Use Endermite to attract.", "general", "age_exploration");
        addTopic("iron_golem_farm", "Iron Golem Farm", "Village-based. Spawn iron golems. Kill for: Iron Ingots. Essential for iron production. Use water to push golems to kill point.", "general", "age_power");
        addTopic("trading_farm", "Villager Trading Farm", "Cure zombie villagers for discounts. Librarians: enchanted books. Farmers: emeralds for crops. Weaponsmiths: diamond gear. Trade early and often.", "minecraft", "any");
        addTopic("emerald_farm", "Emerald Farm", "Trade crops with farmers for emeralds. Use emeralds at Market. Essential for: Market purchases, beacon pyramids, trading.", "minecraft", "any");
        addTopic("food自动化", "Food Automation", "Bonsai: apples. Animal Crops: meat. Farming: wheat->bread. Culinary Construct: sandwiches. Automate everything. Never manually cook again.", "general", "any");
        addTopic("power_automation", "Power Automation", "Multiple generators: Solar + Stirling + Mekanism. Store in cells. Transfer with fluxducts. Monitor with RFTools screens. Always have excess power.", "general", "age_power");
        addTopic("item_automation", "Item Automation", "Hoppers (basic) -> Simple Storage (mid) -> AE2 (end). Sort items. Route to machines. Store in drawers. Essential for progression.", "general", "any");
        addTopic("fluid_automation", "Fluid Automation", "Fluiducts (Thermal) or Fluid Conduits (Ender IO). Transfer water, lava, molten metals. Connect crucible to smeltery. Essential for fluid management.", "general", "any");
        addTopic("redstone_automation", "Redstone Automation", "Hoppers + comparators for timing. Observers for block detection. Repeaters for delay. Essential for: auto-sieve, auto-sort, mob farms.", "general", "any");
        addTopic("ender_io_conduit_tips", "Ender IO Conduit Tips", "Multiple conduits in one block space. Item + Fluid + Energy + Redstone. Use conduit bundle for compact automation. Essential for tight builds.", "enderio", "age_power");
        addTopic("thermal_machine_tips", "Thermal Machine Tips", "Augments: expand machines. Fluxed Augment: higher output. Auxiliary Reception Coil: faster. Redstone Control: conditional. Essential for optimizing machines.", "thermalexpansion", "age_power");
        addTopic("mekanism_gear_tip", "Mekanism Gear Tips", "Atomic Disassembler: best multi-tool. Jetpack: flight. Mekasuit: endgame armor. Meka-Armor: powered helmet. All need RF. Keep charged.", "mekanism", "age_enhancement");
        addTopic("nuclear_safety", "Nuclear Safety", "Fission reactor: monitor temperature. Overheat = explosion. Cool with water. Start small, scale up. Fusion: safer but more expensive. Always have fire resistance.", "nuclearcraft", "age_power");
        addTopic("ae2_network_tips", "AE2 Network Tips", "Start small: 1 controller + drives. Add terminals as needed. Plan channels: 32 per face. Use dense cables for main lines. Upgrade cells gradually.", "ae2", "age_storage");
        addTopic("simple_vs_ae2", "Simple Storage vs AE2", "Simple Storage: easy, limited (500 items). AE2: complex, unlimited. Start with Simple, migrate to AE2 when needed. Both can coexist.", "general", "any");
        addTopic("dimension_farming", "Dimension Farming", "RFTools dimension: create resource-rich world. Mekanism Digital Miner: auto-mine. Essential for endgame resources. Build dimension builder.", "rftools", "age_power");
        addTopic("void_safety", "Void Safety", "Forgiving Void: respawn on platform. But items drop. Keep backup gear. Build safety nets. Never carry everything. Angel watches you fall.", "forgiving_void", "any");
        addTopic("angel_knowledge", "Angel Knowledge System", "The angel knows everything about your progression. It tracks your stage, inventory, and advancement history. Its advice is based on deep mod knowledge. Trust it.", "sf4angel", "any");
        addTopic("angel_behavior", "Angel Behavior", "Appears on: first login, achievements, death, idle chatter. Talks via action bar typewriter. Follows player. Freezes when looked at. Absorbs dropped items for advice. Cannot be killed.", "sf4angel", "any");
    }

    private static void addTopic(String keyword, String title, String content, String mod, String age) {
        GuideTopic topic = new GuideTopic();
        topic.keyword = keyword;
        topic.title = title;
        topic.content = content;
        topic.mod = mod;
        topic.age = age;
        topics.put(keyword.toLowerCase(), topic);
    }

    public static GuideTopic findTopic(String query) {
        String q = query.toLowerCase().trim();
        if (topics.containsKey(q)) return topics.get(q);
        for (GuideTopic topic : topics.values()) {
            if (topic.keyword.contains(q) || topic.title.toLowerCase().contains(q)) {
                return topic;
            }
        }
        return null;
    }

    public static String searchTopics(String query) {
        List<GuideTopic> matches = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (GuideTopic topic : topics.values()) {
            if (topic.keyword.contains(q) || topic.title.toLowerCase().contains(q) || topic.content.toLowerCase().contains(q)) {
                matches.add(topic);
            }
        }
        if (matches.isEmpty()) return "No topics found for: " + query;
        StringBuilder sb = new StringBuilder("Found: ");
        for (int i = 0; i < Math.min(matches.size(), 5); i++) {
            if (i > 0) sb.append(", ");
            sb.append(matches.get(i).keyword);
        }
        if (matches.size() > 5) sb.append(" and " + (matches.size() - 5) + " more");
        return sb.toString();
    }

    public static String getRandomTip() {
        List<GuideTopic> list = new ArrayList<>(topics.values());
        GuideTopic topic = list.get(RANDOM.nextInt(list.size()));
        return topic.title + ": " + topic.content.substring(0, Math.min(100, topic.content.length())) + "...";
    }

    public static List<String> getAllTopicKeywords() {
        return new ArrayList<>(topics.keySet());
    }

    public static int getTopicCount() {
        return topics.size();
    }

    public static class GuideTopic {
        public String keyword = "";
        public String title = "";
        public String content = "";
        public String mod = "general";
        public String age = "any";
    }
}
