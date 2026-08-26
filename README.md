# SF4 Angel

Floating angel guide for Sky Factory 4. Replaces the advancement book.

## Features

- Appears on login/death/advancement completion
- Types messages in the action bar character-by-character
- Shows the **next goal** after each advancement
- White cube with gold halo, spinning beams, particles
- Invulnerable, no gravity, 9999 HP

## Removed

- Advancement Book (disabled)
- Old Triumph scripts (all age dirs cleared)
- Prayer/chat commands, item sacrifice
- Starting items (spawn with nothing)

## Install

Drop `sf4angel-1.0.0.jar` into `mods/`. Requires Forge 1.12.2-14.23.5.2859.

## Config Changes

| File | Change |
|------|--------|
| `advancementbook.cfg` | `giveOnFirstJoin=false` |
| `triumph/Triumph.txt` | sf4angel page order only |
| `triumph/script/sf4angel/` | ~300 Triumph scripts across 8 age dirs |
| `startingitems.txt` | Cleared |
| `sky_islands_start.txt` | Cleared |

## Build

```
set JAVA_HOME=C:\path\to\jdk8
gradlew.bat clean build
```

Output: `build/libs/sf4angel-1.0.0.jar`

## GitHub

https://github.com/godh00d/sf4
