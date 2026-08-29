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

## Achievement Constellation

[Explore the public interactive 3D Achievement Constellation](https://godh00d.github.io/sf4/). The visualization shows the complete fixed catalog of all 129 achievements and their dependency routes; visibility and state colors inside the game remain specific to each player's progress and eligible stages.

Right-click your visible angel with the main hand to enter the constellation. The player moves to an isolated, owner-specific observatory cell in the normal overworld and receives temporary flight. Inside, the Angel Guide glides along the player's recent movement trail with damped acceleration, explains the room, comments periodically on completed and available paths, and remains within reach; right-click it to return to the exact dimension, position, view direction, and entry-time flight state from which you entered. Left-clicking a normal angel retains its existing irritated lightning response.

Players cannot take normal damage in the constellation, including void damage and `/kill` damage routed through Forge. If another mod forces death without a damage event, respawn recovery returns the player and transfers any generated drops to the saved return location so they are not stranded in the void.

The mod records and restores the entry-time Survival flight flags. Creative and Spectator flight entitlement is never disabled on exit. A flight-providing mod that changes ownership while the player is already inside cannot be identified generically; in that bounded case the entry-time flags are restored and the provider should reassert its current entitlement on its next update.

The constellation is a client-rendered graph; it does not create an entity for each achievement and has no clickable nodes. Aim at a star to see its title. The visual states are:

- Green: completed
- Gold: currently available because every parent is complete and all stage requirements are eligible
- Blue: mystery achievement one step beyond the currently available frontier
- Absent: farther progress or stage-gated content that is not yet eligible

Every visible parent relationship is drawn, including multi-parent requirements and parallel technical branches. Only completed achievements and exactly two steps beyond them are rendered: currently available gold achievements, followed by one blue mystery layer. No fallback reveals blocked or later catalog nodes. A deterministic weighted spanning tree gives the catalog a broad bottom-rooted fractal silhouette: dominant paths form short rising trunks, while branch points recursively divide angular sectors into longer limbs. The generated source layout has at least seven blocks between centers and is rendered at 41 percent scale with bounded sway, keeping moving stars more than two blocks apart. Primary branches use bright flowing tendrils and traveling sparks; additional multi-parent requirements remain visible as finer ethereal filaments. Progress is player-specific and refreshes while the player is inside the constellation.

The constellation renderer belongs to the owner-only return angel entity and draws a view-centered violet sky shell, hundreds of subtly flickering background stars, layered mystical tendrils, and pulsating achievement stars through Minecraft's normal entity-rendering pipeline. Each achievement combines a crystalline 3D core, seven crossed spatial rays, and a breathing additive bloom. The shell stays inside the camera far plane, and clouds and weather are suppressed only for the active session before the player's original settings are restored on return or disconnect, so the normal overworld sky cannot flash through as the player looks around. The server allocates cells far from the active island and above build terrain, resolves collisions between active players, enforces the virtual room boundary, and recovers interrupted sessions on login or respawn. No custom dimension, physical constellation blocks, full-screen GUI, or world-last render hook is used.

## Achievement Catalog

The catalog contains:

- 110 core achievements
- 13 optional challenges
- 6 Prestige achievements

Dependencies follow the installed pack's progression rather than imposing a single sequence on unrelated systems. Parallel technologies remain independent, while Prestige content remains hidden until the corresponding stage is available.

Triumph handles inventory, location, and parent-completion criteria. The Forge mod handles machine operation, automation, ownership, multiblocks, and other conditions that require server-side state. Integrations include Mekanism, NuclearCraft, Deep Mob Learning, Applied Energistics 2, Industrial Foregoing, Matter Overdrive, and other systems included in the pack.

The complete catalog is documented in [ACHIEVEMENT_PLAN.md](ACHIEVEMENT_PLAN.md). Dependency diagrams are available in [ACHIEVEMENT_TREE.md](ACHIEVEMENT_TREE.md).

### Reading the Progression

All achievement sections describe one connected dependency graph, split into smaller diagrams only for readability. The early core, technology, midgame, and endgame sections reconnect through repeated achievement IDs at their boundaries; a repeated node is the same achievement, not a duplicate.

- An arrow points from a prerequisite to the achievement it unlocks.
- Multiple incoming arrows are an AND requirement: every listed parent must be complete.
- Achievements without parents attach to their Triumph page root for display, but the page root is not a completion requirement.
- Core branches may progress in parallel and reconnect where a recipe or machine genuinely requires several systems.
- Optional achievements may depend on core progress, but never block or unlock core progression.
- Prestige achievements form a separate gated branch and never block core or optional progression.

Use [ACHIEVEMENT_TREE.md](ACHIEVEMENT_TREE.md) to follow the arrows visually, then use the matching ID in [ACHIEVEMENT_PLAN.md](ACHIEVEMENT_PLAN.md) for the exact criterion, parent list, stage, and implementation type.

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
