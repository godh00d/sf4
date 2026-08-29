# Installation

SF4 Angel Guide is an addon for an existing SkyFactory 4.2.4 instance. It is not a standalone modpack and does not contain SkyFactory or its third-party mods.

## Install A Release

Stop Minecraft and the dedicated server before changing files. Back up the instance, then extract `SF4-Angel-Guide-<version>.zip` into the instance root. The archive has this layout:

```text
mods/
  sf4angel-<version>.jar
config/
  triumph/
    Triumph.txt
    script/sf4angel/...
```

Allow `mods` and `config` to merge with the existing directories. The archive replaces only the SF4 Angel scripts under `config/triumph/script/sf4angel`; it does not contain unrelated Triumph pages.

Install the same release on the dedicated server and every client. Mixing a jar from one release with scripts from another can leave achievements missing or impossible to complete.

## Upgrade

Back up the world and `config/triumph`, remove the old `sf4angel-*.jar`, then extract the new archive over the instance. Do not delete player advancement data. Existing progress uses stable advancement IDs and should remain intact unless a release note explicitly documents a migration.

## Remove

Stop the game, remove `mods/sf4angel-*.jar`, and remove `config/triumph/script/sf4angel`. Edit `config/triumph/Triumph.txt` only if the installed release changed page ordering that you no longer want. Removing the addon does not remove SkyFactory, Triumph, or other Triumph pages.

## Troubleshooting

If the Angel does not appear or the catalog is absent, verify that the jar exists on both sides and that `config/triumph/script/sf4angel` contains the release scripts. Search `logs/latest.log` for `sf4angel`, `Triumph`, missing parents, invalid registry names, or a missing `custom` criterion before filing a bug report.
