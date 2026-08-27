# SF4 Angel Instance Configuration

This directory contains the source-controlled Triumph configuration for the reduced 129-achievement catalog. It is generated from `ACHIEVEMENT_PLAN.md`; registry choices are constrained by `REGISTRY_MANIFEST.md`, and parent relationships must agree with `ACHIEVEMENT_TREE.md`.

## Generate and validate

Run from the project root with Windows PowerShell 5.1 or newer:

```powershell
.\instance-config\generate_triumph.ps1
.\instance-config\generate_triumph.ps1 -Check
```

The validator requires exactly 110 core, 13 optional, and 6 Prestige achievement scripts, plus one root for each page. It compares every plan parent with the Mermaid tree, then checks IDs, parent existence, positions, native and Java criterion contracts, exact `T-ANY` alternatives, Prestige gates, ASCII output, line endings, trailing whitespace, and final newlines.

## Deploy

1. Stop Minecraft and any dedicated server using the instance.
2. Back up the target instance's `config\triumph` directory.
3. Validate this source tree with `.\instance-config\generate_triumph.ps1 -Check`.
4. Replace the target instance's `config\triumph\Triumph.txt` with `instance-config\triumph\Triumph.txt`.
5. Replace only the target instance's `config\triumph\script\sf4angel` directory with `instance-config\triumph\script\sf4angel`.
6. Install the matching SF4 Angel mod jar on both client and server. Java-backed goals expose one `minecraft:impossible` criterion named `custom`; the mod must grant that exact criterion after verifying the stated condition.
7. Start the pack and inspect `logs\latest.log` for Triumph parsing errors, invalid registry names, missing parents, or missing `custom` criteria.

The generated configuration does not include or overwrite unrelated Triumph pages. `Triumph.txt` retains the installed pack's Twilight Forest exception and orders only the three SF4 Angel pages. Prestige content uses the installed `parabox`, `project_e`, `portal_gun`, `mystcraft`, and `parabox_two` game-stage conventions and remains fail-closed through Java integration.
