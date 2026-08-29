# Contributing

SF4 Angel Guide targets one fixed environment: Minecraft 1.12.2 with SkyFactory 4.2.4. Changes should preserve that scope unless an issue explicitly agrees on broader compatibility first.

Open an issue before undertaking a large progression, rendering, or integration rewrite. Bug fixes with a narrow cause can go directly to a pull request.

## Development Setup

Use a 64-bit JDK 8. ForgeGradle 3 and this Minecraft version are not supported by current JDK releases.

```powershell
./gradlew.bat clean build
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ./instance-config/generate_triumph.ps1 -Check
```

The first build downloads and prepares the Forge workspace. Local caches, runtime worlds, logs, and build output are intentionally ignored by Git.

## Pull Requests

A pull request should explain the player-visible behavior being changed, identify any affected achievements or integrations, and report the validation performed. Keep generated Triumph files synchronized with `ACHIEVEMENT_PLAN.md`, `ACHIEVEMENT_TREE.md`, and the Java catalog; do not hand-edit generated scripts without updating their source.

Before opening the request, run the clean build, the Triumph validator with `-Check`, and `git diff --check`. Changes to rendering, movement, mod integrations, or session recovery also need a short description of the in-game scenario tested.

Do not include a built jar, Minecraft assets from the base game or modpack, instance logs, worlds, or third-party mod files in a commit.
