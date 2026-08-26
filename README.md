# SF4 Angel Guide

A floating angel entity that acts as an in-game guide for Sky Factory 4, replacing the vanilla advancement book.

## What It Does

- A white glowing cube with a golden square halo and 4 spinning beams appears on first login
- Speaks to you via action bar typewriter messages
- Greets you on login, death, and after each advancement
- Shows you the **next goal** in the main progression path after every advancement
- Gives stage hints based on your current progress
- Floats nearby, bobs gently, emits particles (end rods, enchantment glyphs, happy villager, clouds, fireworks)
- Three spawn/despawn animations: descend from above, spin+grow, smoke form, ascend, spin+shrink

## What Was Removed

- Advancement Book mod (disabled in config)
- Old SF4 Triumph advancement scripts (all 8 age directories cleared)
- Prayer/chat command system
- Item sacrifice/dropping mechanics
- Starting items (player spawns with nothing)

## Installation

1. Place `sf4angel-1.0.0.jar` in your `mods/` folder
2. Delete any old `sf4angel-1.0.0.jar` first
3. The mod requires Forge 1.12.2-14.23.5.2859

## Config Files Modified

| File | Change |
|------|--------|
| `config/advancementbook.cfg` | `giveOnFirstJoin=false` |
| `config/triumph/Triumph.txt` | Page order set to sf4angel pages, vanilla advancements removed |
| `config/triumph/script/sf4angel/` | 8 age directories with ~300 Triumph script files |
| `resources/skyfactory4/lang/en_us.lang` | All advancement descriptions rewritten |
| `config/triumph/functions/startingitems.txt` | Cleared |
| `config/triumph/functions/sky_islands_start.txt` | Cleared |

## Building From Source

Requires JDK 8 (path configured in build):

```
set JAVA_HOME=C:\path\to\jdk8
gradlew.bat clean build
```

Output: `build/libs/sf4angel-1.0.0.jar`

## GitHub

https://github.com/godh00d/sf4

## Mod Structure

```
src/main/java/com/godh00d/sf4angel/
  SF4Angel.java              - Main mod class (@Mod entry point)
  Reference.java             - Mod ID, name, version constants
  entity/
    EntityAngel.java         - Angel entity (invulnerable, no gravity, 9999 HP)
    EntityAngelRender.java   - Renderer: white cube, square halo, 4 spinning beams, particles
  handler/
    AchievementHandler.java  - Listens for AdvancementEvent, shows next goal in PROGRESSION_PATH
    PlayerJoinHandler.java   - Clears inventory, spawns angel on login/respawn
    TickHandler.java         - Idle chatter, despawn logic, health warnings
  knowledge/
    AngelOracle.java         - Stage detection, goal/hint system
    ChestScanner.java        - Scans player inventory for advice
    KnowledgeBase.java       - ~50 guide topics (sieve, tinker, AE2, mekanism, etc.)
  network/
    PacketHandler.java       - Network channel registration
    MessageAngelState.java   - Syncs angel state to client
    MessageTypewriter.java   - Sends typewriter messages to client
  personality/
    AngelPersonality.java    - All dialogue text (small talk, death lines, attack responses, etc.)
  proxy/
    ClientProxy.java         - Registers entity renderer (client only)
    CommonProxy.java         - Registers event handlers, initializes systems
    ServerProxy.java         - Server-side proxy (empty)
  typewriter/
    TypewriterHandler.java   - Action bar message display with character-by-character animation
```

## Triumph Advancement Scripts

8 age directories under `config/triumph/script/sf4angel/`:

| Age | Files | Description |
|-----|-------|-------------|
| basic | ~65 | First steps: log, crafting, sieve, iron, diamond |
| farming | ~40 | Bonsai, animals, crops, market |
| enhancement | ~45 | Tinkers, glitch armor, enchanting |
| power | ~62 | Generators, deep mob, mekanism, nuclear |
| storage | ~44 | Barrels, drawers, AE2, simple storage |
| exploration | ~40 | Nether, end, twilight forest, ores |
| endgame | ~36 | Singularities, reactors, completionist |
| angel | ~13 | Meta-achievements: deaths, playtime, mob kills |

## Progression Path

The `AchievementHandler.PROGRESSION_PATH` maps ~180 advancements in a linear chain. When you complete one, the angel tells you the next main-branch goal. Not all advancements are in the path — only the main storyline.

## Known Issues

- The angel texture is a 16x16 pure white PNG (placeholder) — replace `assets/sf4angel/textures/entity/angel.png` with a custom texture
- Some Triumph script errors in logs (wrong item IDs, missing criteria) — these are non-blocking
