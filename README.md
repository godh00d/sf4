# SF4 Angel Guide

SF4 Angel Guide is a progression companion for SkyFactory 4.2.4. It pairs a Forge mod with a 129-achievement Triumph catalog built around the pack's actual recipes, machines, dimensions, and Prestige stages.

The Angel appears for player-specific events and delivers short guidance through the action bar. Right-clicking it opens an owner-only observatory where completed achievements, available goals, and the next hidden frontier form a navigable 3D constellation. Progress remains server-authoritative and isolated per player.

[Open the constellation map](https://godh00d.github.io/sf4/) | [Download a release](https://github.com/godh00d/sf4/releases)

## Using The Guide

The Angel appears on login, respawn, achievement completion, and low health. It stays beside the player's view, reacts to events, and leaves after its queued dialogue finishes.

Right-click the Angel with your main hand to enter the observatory. Aim at a star to read its name and requirement. Gold stars are complete, blue stars are available, and grey stars mark the next unrevealed layer. Right-click the Angel inside the observatory to return to the exact position and dimension from which you entered.

The observatory grants temporary flight and suppresses ordinary damage while the session is active. Existing Creative or Spectator flight is preserved when leaving.

## Compatibility

| Component | Supported version |
| --- | --- |
| Minecraft | 1.12.2 |
| SkyFactory | 4.2.4 |
| Forge | 14.23.5.2859 or 14.23.5.2860 |
| Triumph | 3.19.2 |
| Java for building | 64-bit JDK 8 |

Both the mod jar and the matching Triumph configuration are required on the server and every connecting client. A release archive contains both in the correct instance-relative directories.

## Installation

1. Back up the SkyFactory 4 instance and stop any server using it.
2. Download the latest `SF4-Angel-Guide-*.zip` from [GitHub Releases](https://github.com/godh00d/sf4/releases).
3. Extract the archive into the SkyFactory 4 instance root and allow its `mods` and `config` directories to merge.
4. Repeat the installation for the dedicated server and each client.
5. Start the pack and confirm that `logs/latest.log` contains no Triumph parsing errors.

Detailed upgrade and removal notes are in [docs/INSTALLATION.md](docs/INSTALLATION.md).

## Building

Set `JAVA_HOME` to a 64-bit JDK 8, then run:

```powershell
./gradlew.bat clean build
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ./instance-config/generate_triumph.ps1 -Check
```

The reobfuscated mod is written to `build/libs/sf4angel-<version>.jar`. Create the same archive used by releases with:

```powershell
./scripts/package-release.ps1 -Version 1.0.0
```

## Project Reference

| Document | Purpose |
| --- | --- |
| [ACHIEVEMENT_PLAN.md](ACHIEVEMENT_PLAN.md) | Achievement IDs, criteria, stages, and prerequisites |
| [ACHIEVEMENT_TREE.md](ACHIEVEMENT_TREE.md) | Complete dependency graph |
| [REGISTRY_MANIFEST.md](REGISTRY_MANIFEST.md) | Verified SkyFactory registry IDs and integration evidence |
| [instance-config/README.md](instance-config/README.md) | Triumph generation and validation rules |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Development and pull-request requirements |
| [docs/RELEASING.md](docs/RELEASING.md) | Versioning and distribution process |

The project is licensed under the [MIT License](LICENSE).
