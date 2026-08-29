# Releasing

GitHub Releases is the canonical distribution channel. Each release contains one instance-overlay archive with the mod jar and its matching Triumph configuration.

## Prepare A Version

Choose a semantic version such as `1.1.0`. Update `version` in `build.gradle` and `VERSION` in `src/main/java/com/godh00d/sf4angel/Reference.java` to the same value. Describe player-visible changes in the release commit or pull request so GitHub can generate useful notes.

Run the release checks locally:

```powershell
./gradlew.bat clean build
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ./instance-config/generate_triumph.ps1 -Check
./scripts/package-release.ps1 -Version 1.1.0
```

Test the archive in a clean copy of SkyFactory 4.2.4, including a new player and an existing world. Confirm client/server login, Angel interaction, observatory entry and return, and at least one Java-backed achievement.

## Publish On GitHub

Merge the version commit into `master`, then create and push a matching annotated tag:

```powershell
git tag -a v1.1.0 -m "SF4 Angel Guide 1.1.0"
git push origin master
git push origin v1.1.0
```

The release workflow rejects a tag that does not match both source version declarations. A valid tag builds the project, validates all Triumph scripts, packages the overlay, and creates a GitHub Release with generated notes.

## Other Platforms

CurseForge and Modrinth mod uploads normally install a jar but do not merge an external Triumph script tree. Publishing only the jar there would produce an incomplete installation. Until the mod can safely provision and update its own scripts, publish the GitHub archive as a manually installed SkyFactory addon and link to the installation page.

If automatic script provisioning is implemented later, publish the jar as a Minecraft 1.12.2 Forge mod, declare the exact SkyFactory and Triumph compatibility in the project description, attach the complete overlay as an additional file, and keep GitHub Releases as the source of record. Do not bundle SkyFactory or third-party mod jars.
