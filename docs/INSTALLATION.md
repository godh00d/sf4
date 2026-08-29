# Installation

SF4 Angel Guide is an addon for an existing SkyFactory 4.2.4 instance. It is not a standalone modpack and does not contain SkyFactory or its third-party mods.

## Install A Release

Stop Minecraft and any dedicated server, extract `SF4-Angel-Guide-<version>.zip`, and double-click `Install-SF4Angel.bat`. The installer detects the standard CurseForge SkyFactory 4 location. If that location does not exist, it asks you to select the instance folder.

Before changing anything, the installer copies the existing SF4 Angel jar, Triumph root configuration, and SF4 Angel scripts into `sf4angel-backups/install-<timestamp>` inside the instance. It then installs this layout:

```text
mods/
  sf4angel-<version>.jar
config/
  triumph/
    Triumph.txt
    script/sf4angel/...
```

The installer replaces only old SF4 Angel jars, the Triumph root configuration, and scripts under `config/triumph/script/sf4angel`. It does not remove unrelated Triumph script pages.

Install the same release on the dedicated server and every client. Mixing a jar from one release with scripts from another can leave achievements missing or impossible to complete.

Install a dedicated server or a nonstandard instance from a command prompt with:

```text
Install-SF4Angel.bat "C:\path\to\instance"
```

Manual installation remains possible by merging the release archive's `mods` and `config` directories into the instance root.

## Upgrade

Back up the world and `config/triumph`, remove the old `sf4angel-*.jar`, then extract the new archive over the instance. Do not delete player advancement data. Existing progress uses stable advancement IDs and should remain intact unless a release note explicitly documents a migration.

## Remove

Stop the game, remove `mods/sf4angel-*.jar`, and remove `config/triumph/script/sf4angel`. Edit `config/triumph/Triumph.txt` only if the installed release changed page ordering that you no longer want. Removing the addon does not remove SkyFactory, Triumph, or other Triumph pages.

## Troubleshooting

If the Angel does not appear or the catalog is absent, verify that the jar exists on both sides and that `config/triumph/script/sf4angel` contains the release scripts. Search `logs/latest.log` for `sf4angel`, `Triumph`, missing parents, invalid registry names, or a missing `custom` criterion before filing a bug report.
