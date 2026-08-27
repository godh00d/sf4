# SF4 Angel Guide

SF4 Angel Guide is a client-and-server progression extension for SkyFactory 4.2.4. It combines a Forge mod with a custom Triumph configuration containing 129 achievements based on the pack's recipes, dimensions, machines, and GameStages.

## Angel Guide

The mod adds an owner-specific guide entity that appears when a player joins, respawns, completes an achievement, or reaches low health. Guidance and progression messages are presented through the action bar.

The entity is rendered as a luminous white cube that forms a single eye. Its pupil tracks players and nearby objects across all six faces, including transitions around edges and corners. A neon gold halo, restrained particles, inertial movement, and event-specific behavior distinguish calm, curious, concerned, proud, and irritated states.

Additional behavior includes:

- Per-player spawning and ownership suitable for multiplayer
- Typed and erased action-bar dialogue
- Login, respawn, health, attack, and achievement responses
- Following movement that slows while the player is observing the entity
- Persistent counters for long-term achievement conditions

## Achievement Catalog

The catalog contains:

- 110 core achievements
- 13 optional challenges
- 6 Prestige achievements

Dependencies follow the installed pack's progression rather than imposing a single sequence on unrelated systems. Parallel technologies remain independent, while Prestige content remains hidden until the corresponding stage is available.

Triumph handles inventory, location, and parent-completion criteria. The Forge mod handles machine operation, automation, ownership, multiblocks, and other conditions that require server-side state. Integrations include Mekanism, NuclearCraft, Deep Mob Learning, Applied Energistics 2, Industrial Foregoing, Matter Overdrive, and other systems included in the pack.

The complete catalog is documented in [ACHIEVEMENT_PLAN.md](ACHIEVEMENT_PLAN.md). Dependency diagrams are available in [ACHIEVEMENT_TREE.md](ACHIEVEMENT_TREE.md).

## Requirements

- Minecraft 1.12.2
- SkyFactory 4.2.4
- Triumph 3.19.2
- Minecraft Forge 14.23.5.2859 or 14.23.5.2860
- Java 8 for source builds

The mod jar and Triumph configuration must be installed together. The jar does not contain the achievement scripts.

## Installation

No packaged release is currently available. To install a development build:

1. Build `sf4angel-1.0.0.jar` from the required revision.
2. Place the jar in the SkyFactory 4 `mods` directory on each client and server.
3. Copy `instance-config/triumph/` into the instance's `config/triumph/` directory.
4. Start the pack and verify that Triumph loads the configuration without errors.

Back up an existing Triumph configuration before replacing it.

## Building

ForgeGradle for Minecraft 1.12.2 requires a 64-bit JDK 8.

```powershell
$env:JAVA_HOME = "C:\path\to\jdk8"
.\gradlew.bat clean build
```

The reobfuscated jar is written to `build/libs/sf4angel-1.0.0.jar`.

Generate and validate the Triumph configuration with:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\instance-config\generate_triumph.ps1
```

## Repository Structure

| Path | Contents |
| --- | --- |
| `ACHIEVEMENT_PLAN.md` | Achievement identifiers, criteria, stages, and prerequisites |
| `ACHIEVEMENT_TREE.md` | Dependency diagrams for the complete catalog |
| `REGISTRY_MANIFEST.md` | Registry identifiers and integration evidence |
| `instance-config/triumph/` | Generated Triumph configuration |
| `instance-config/generate_triumph.ps1` | Configuration generator and validator |
| `src/main/java/` | Entity behavior, rendering, dialogue, and achievement integrations |

## Development Status

The Forge project builds successfully, and the generated configuration passes catalog, dependency, criteria, stage, and Java parity checks. Broader in-game validation remains necessary for integrations that inspect modded machine state through reflection. The project does not currently include an automated Minecraft integration test suite.
