# SF4 Angel Guide

SF4 Angel Guide is a custom progression overhaul for SkyFactory 4.2.4. It adds a small in-world companion and replaces the usual rapid checklist with 129 achievements built around the pack's actual progression.

The angel appears when something matters: joining a world, returning after death, completing an achievement, or running low on health. Messages are shown through the action bar instead of a separate quest window.

The current design is a glowing white cube that acts as one large eye. Its pupil follows players and nearby objects across every face of the cube, including around edges and corners. The eye has a neon gold halo, restrained idle particles, and different movement patterns for curiosity, concern, pride, and irritation.

## Achievement Overhaul

The catalog contains:

- 110 core achievements
- 13 optional challenges
- 6 Prestige achievements

The tree follows recipes, dimensions, machine tiers, and GameStages from the installed pack. Parallel systems stay parallel, so completing one machine does not arbitrarily gate an unrelated branch. Prestige content remains hidden until its stage is available.

Simple conditions are handled by Triumph scripts. Machine operation, automation, ownership, multiblocks, and other stateful goals are checked by the Java mod. This is especially important for achievements where merely holding an output would not prove that the player used the system.

See [ACHIEVEMENT_TREE.md](ACHIEVEMENT_TREE.md) for the dependency map and [ACHIEVEMENT_PLAN.md](ACHIEVEMENT_PLAN.md) for the full catalog.

## Other Features

- Per-player angels that work in multiplayer
- Action-bar dialogue with typed and erased text
- Reactions to achievements, attacks, health, login, and respawn
- Natural following movement that slows when watched
- Persistent counters used by long-term achievements
- Integrations for Mekanism, NuclearCraft, Deep Mob Learning, AE2, Industrial Foregoing, Matter Overdrive, and other pack systems

## Requirements

- Minecraft 1.12.2
- SkyFactory 4.2.4
- Triumph 3.19.2
- Minecraft Forge 14.23.5.2859 or the pack's newer 14.23.5.2860 build
- Java 8 to build from source

The mod and Triumph configuration are a matched set. Installing only the jar will not add the achievement tree.

## Installation

There is not a packaged release yet. To install the current development version:

1. Build `sf4angel-1.0.0.jar` or use a jar produced from this revision.
2. Put the jar in the SkyFactory 4 `mods` folder on the client and server.
3. Copy `instance-config/triumph/` into the instance's `config/triumph/` directory.
4. Start the pack and check `logs/latest.log` if Triumph reports a script error.

Back up an existing Triumph configuration before replacing it.

## Building

ForgeGradle for Minecraft 1.12.2 requires a 64-bit JDK 8.

```powershell
$env:JAVA_HOME = "C:\path\to\jdk8"
.\gradlew.bat clean build
```

The finished jar is written to `build/libs/sf4angel-1.0.0.jar`.

To regenerate and validate the Triumph scripts:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\instance-config\generate_triumph.ps1
```

## Repository Guide

| Path | Contents |
| --- | --- |
| `ACHIEVEMENT_PLAN.md` | Achievement IDs, triggers, stages, and prerequisites |
| `ACHIEVEMENT_TREE.md` | Dependency diagrams for the complete tree |
| `REGISTRY_MANIFEST.md` | Registry IDs and integration evidence from the pack |
| `instance-config/triumph/` | Generated Triumph configuration |
| `instance-config/generate_triumph.ps1` | Generator and consistency checks |
| `src/main/java/` | Angel behavior, rendering, dialogue, and achievement integrations |

## Current Status

The project builds and the 129-achievement configuration passes its generator checks. It is still instance-specific and needs broader in-game testing, particularly for mod integrations that inspect machine state through reflection. There is no automated Minecraft test suite.
