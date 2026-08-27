# SF4 Angel Guide

SF4 Angel Guide is a client-and-server Forge mod for Minecraft 1.12.2 that gives SkyFactory 4 a visible in-world guide. A glowing cube-shaped angel appears for important moments, presents progression messages through an action-bar typewriter, and reacts to the custom `sf4angel` Triumph advancement tree.

The mod is designed for a customized SkyFactory 4 instance. It is not a standalone replacement for the modpack's quests or advancement configuration.

## Features

- Owner-specific angel entities for multiplayer-safe spawning and despawning
- White rotating cube model with an independently rotating gold square halo
- Emissive rendering, particles, body sway, and several distant movement patterns
- Typewriter messages with a moving cursor, reverse-untyping, queue priorities, and immediate red warnings
- Login, respawn, health, attack, and achievement reactions
- A congratulation for every completed non-root `sf4angel:*` advancement
- Linear next-goal prompts without forcing unrelated achievements into that sequence
- Persisted counters for play time, kills, deaths, mined blocks, appearances, breeding, and dimension visits
- Java-backed conditions for achievements Triumph cannot express reliably

## Requirements

- Minecraft 1.12.2
- Minecraft Forge 14.23.5.2859
- Java Development Kit 8 for building
- SkyFactory 4 and its configured Triumph 3.19.2 advancement scripts at runtime

## Installation

1. Build the mod or obtain `sf4angel-1.0.0.jar` from a trusted release.
2. Place the jar in the SkyFactory 4 instance's `mods` directory.
3. Install the matching custom Triumph scripts under `config/triumph/script/sf4angel/`.
4. Start the pack and confirm that the log contains `Registered EntityAngelRender during preInit`.

The mod jar alone does not contain the planned 129-achievement Triumph tree. Advancement layout, titles, parent relationships, and most item criteria are supplied by the instance configuration.

## Build

Use a 64-bit JDK 8. Newer Java versions are not supported by this Minecraft/ForgeGradle toolchain.

```powershell
$env:JAVA_HOME = "C:\path\to\jdk8"
.\gradlew.bat clean build
```

The reobfuscated mod jar is written to `build/libs/sf4angel-1.0.0.jar`.

The first build downloads ForgeGradle, Forge, mappings, and other dependencies, so it requires network access and can take several minutes.

## Runtime Integration

Triumph handles declarative inventory, location, and parent-completion criteria. Java handlers cover conditions that require server-side events or verified mod state, including:

- Breeding, sleeping, food variety, boss kills, dimension entry, and timed crouching near saplings
- Tinkers tools, Deep Mob Learning models, automated farming, storage networks, and digital crafting
- Mekanism, NuclearCraft, Matter Overdrive, Industrial Foregoing, and Extended Crafting machine operation
- Optional challenge and Prestige progress with persistent player or machine attribution

Ambiguous goals are never granted from broad possession proxies. Operation-based achievements require correlated machine state, output, ownership, and progression evidence and fail closed when an installed mod API cannot be verified.

## Project Layout

| Path | Purpose |
| --- | --- |
| `src/main/java/com/godh00d/sf4angel/entity/` | Angel entity, renderer, and model |
| `src/main/java/com/godh00d/sf4angel/handler/` | Player lifecycle, ticks, achievements, and counters |
| `src/main/java/com/godh00d/sf4angel/typewriter/` | Per-player action-bar message queues |
| `src/main/java/com/godh00d/sf4angel/personality/` | Angel dialogue selection |
| `src/main/java/com/godh00d/sf4angel/knowledge/` | Goal and inventory guidance |
| `src/main/java/com/godh00d/sf4angel/network/` | Forge SimpleNetworkWrapper messages |
| `src/main/resources/` | Forge metadata, language data, and the angel texture |

## Troubleshooting

- **The build uses the wrong Java version:** set `JAVA_HOME` to a JDK 8 installation in the same terminal before running Gradle.
- **The angel is invisible:** verify client-side installation and look for the renderer registration line in `logs/latest.log`.
- **Achievements are absent or malformed:** inspect Triumph parsing errors in `logs/latest.log` and verify the instance's `config/triumph/script/sf4angel/` directory.
- **A Java-managed achievement does not complete:** both client and server must run the same jar, and the Triumph advancement ID must match the ID used by `AchievementHandler`.
- **A replaced jar remains locked:** fully close Minecraft and its launcher process before replacing the file.

## Development Status

This repository is an instance-specific work in progress. The Java mod builds successfully, but there is currently no automated test suite. Validation consists of a clean Gradle build, Triumph script checks, and an in-game Forge log/runtime pass.

## Achievement Tree

The reduced catalog contains **110 core**, **13 optional**, and **6 prestige-only** achievements (**129 total**). It rewards distinct gameplay systems rather than recipe trivia: do not split ingredients from outputs unless they prove separate systems, and do not create arbitrary two-minute reward bursts. All modded targets are resolved against the installed pack in `REGISTRY_MANIFEST.md`; operation-based goals remain Java integrations rather than inventory proxies.

```mermaid
flowchart LR
    BOOT["Bootstrap and resource trees<br/>20 core"] --> FARM["Storage, farming, and tools<br/>20 core"]
    BOOT --> POWER["Power and processing<br/>25 core"]
    FARM --> POWER
    POWER --> DIM["Dimensions and bosses<br/>15 core"]
    POWER --> LOGIC["Logic, storage, and industry<br/>20 core"]
    DIM --> LOGIC
    LOGIC --> END["Endgame<br/>10 core"]
    DIM --> END
    END --> FINAL(["core/the_sky_finally_claps"])
    FARM -.-> OPTIONAL["13 optional"]
    POWER -.-> OPTIONAL
    DIM -.-> OPTIONAL
    LOGIC -.-> OPTIONAL
    FINAL --> PRESTIGE["6 prestige-only"]
```

- [Detailed achievement trees](ACHIEVEMENT_TREE.md)
- [Implementation-ready catalog of all 129 achievements](ACHIEVEMENT_PLAN.md)

### Early-Game Branches

```mermaid
flowchart TD
    A1["core/starting_from_the_bottom"] --> A2["core/that_is_dir_tree"]
    A2 --> A3["core/dirty_dancing"]
    A2 --> A4["core/captain_hook"] --> A5["core/stone_tree_oath"]
    A5 --> A6["core/stone_from_trees"]
    A5 --> A7["core/gravel_travel"] --> A8["core/beachfront_property"]
    A3 --> A9["core/clay_aiken"]
    A8 --> A9
    A9 --> A10["core/water_you_waiting_for"]
    A9 --> A11["core/clay_bucket_gamble"]
    A8 --> A12["core/coal_in_bloom"]
    A12 --> A13["core/ironwood"]
    A11 --> A13
    A13 --> A14["core/oh_the_irony"]
    A13 --> A15["core/red_tree_redemption"]
    A15 --> A16["core/trees_to_diamonds"]
    A13 --> A16
    A16 --> A17["core/diamond_clarity"]
    A14 --> A18["core/tiny_tree_big_plans"]
    A17 --> A18
    A18 --> A19["core/drop_it_like_its_hopping"]
    A19 --> A20["core/orchard_on_autopilot"]
```

### Optional Branches

```mermaid
flowchart LR
    EARLY["Early core"] --> O1["optional/sticky_keys"]
    EARLY --> O2["optional/nap_time"]
    FARM["Farming core"] --> O3["optional/milk_without_the_moo"]
    FARM --> O4["optional/a_balanced_sky_diet"]
    FARM --> O5["optional/mob_factory_floor"]
    TOOLS["Tools core"] --> O6["optional/armored_to_the_teeth"]
    TOOLS --> O7["optional/unbreakable_resolve"]
    TOOLS --> O8["optional/undo_the_apocalypse"]
    TOOLS --> O9["optional/pixel_perfect_masonry"]
    TOOLS --> O10["optional/around_the_void_in_eighty_throws"]
    INDUSTRY["Industry core"] --> O11["optional/android_dreams"]
    INDUSTRY --> O12["optional/robot_did_it"]
    EXPLORE["Exploration core"] --> O13["optional/maximum_minimum_space"]

    FINAL["core/the_sky_finally_claps"] --> P1["prestige/prestige_worldwide"]
    P1 --> P2["prestige/equivalent_ambition_unlocked"]
    P1 --> P3["prestige/aperture_unlocked"]
    P1 --> P4["prestige/written_in_another_age"]
    P1 --> P5["prestige/time_is_a_flat_parabox"]
    P2 --> P6["prestige/empowered_recursion"]
    P3 --> P6
    P4 --> P6
    P5 --> P6
```
