[CmdletBinding()]
param(
    [string]$InstancePath
)

$ErrorActionPreference = 'Stop'
$packageRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$standardInstance = Join-Path $env:USERPROFILE 'curseforge\minecraft\Instances\SkyFactory 4'

if ([string]::IsNullOrWhiteSpace($InstancePath)) {
    if (Test-Path -LiteralPath $standardInstance -PathType Container) {
        $InstancePath = $standardInstance
    } else {
        $shell = New-Object -ComObject Shell.Application
        $selection = $shell.BrowseForFolder(0, 'Select the SkyFactory 4 instance folder', 0)
        if ($null -eq $selection) {
            throw 'Installation cancelled because no instance folder was selected.'
        }
        $InstancePath = $selection.Self.Path
    }
}

$InstancePath = [System.IO.Path]::GetFullPath($InstancePath)
$instanceMods = Join-Path $InstancePath 'mods'
$instanceConfig = Join-Path $InstancePath 'config'
if (-not (Test-Path -LiteralPath $instanceMods -PathType Container) -or
    -not (Test-Path -LiteralPath $instanceConfig -PathType Container)) {
    throw "The selected folder is not a usable Minecraft instance: $InstancePath"
}

$runningJava = @(Get-Process -Name java, javaw -ErrorAction SilentlyContinue)
if ($runningJava.Count -gt 0) {
    throw 'Close Minecraft and any dedicated server before installing SF4 Angel Guide.'
}

$packageJars = @(Get-ChildItem -LiteralPath (Join-Path $packageRoot 'mods') -Filter 'sf4angel-*.jar' -File)
if ($packageJars.Count -ne 1) {
    throw 'The release package must contain exactly one mods\sf4angel-*.jar file.'
}
$packageTriumph = Join-Path $packageRoot 'config\triumph'
$packageScripts = Join-Path $packageTriumph 'script\sf4angel'
if (-not (Test-Path -LiteralPath (Join-Path $packageTriumph 'Triumph.txt') -PathType Leaf) -or
    -not (Test-Path -LiteralPath $packageScripts -PathType Container)) {
    throw 'The release package is missing its Triumph configuration.'
}

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$backupRoot = Join-Path $InstancePath "sf4angel-backups\install-$timestamp"
$backupMods = New-Item -ItemType Directory -Path (Join-Path $backupRoot 'mods') -Force
$backupTriumph = New-Item -ItemType Directory -Path (Join-Path $backupRoot 'config\triumph') -Force
$existingJars = @(Get-ChildItem -LiteralPath $instanceMods -Filter 'sf4angel-*.jar' -File)
foreach ($existingJar in $existingJars) {
    Copy-Item -LiteralPath $existingJar.FullName -Destination $backupMods.FullName
}

$targetTriumph = Join-Path $instanceConfig 'triumph'
$targetTriumphFile = Join-Path $targetTriumph 'Triumph.txt'
$targetScripts = Join-Path $targetTriumph 'script\sf4angel'
if (Test-Path -LiteralPath $targetTriumphFile -PathType Leaf) {
    Copy-Item -LiteralPath $targetTriumphFile -Destination $backupTriumph.FullName
}
if (Test-Path -LiteralPath $targetScripts -PathType Container) {
    $backupScriptParent = New-Item -ItemType Directory -Path (Join-Path $backupTriumph 'script') -Force
    Copy-Item -LiteralPath $targetScripts -Destination $backupScriptParent.FullName -Recurse
}

foreach ($existingJar in $existingJars) {
    Remove-Item -LiteralPath $existingJar.FullName -Force
}
Copy-Item -LiteralPath $packageJars[0].FullName -Destination $instanceMods
New-Item -ItemType Directory -Path (Join-Path $targetTriumph 'script') -Force | Out-Null
Copy-Item -LiteralPath (Join-Path $packageTriumph 'Triumph.txt') -Destination $targetTriumphFile -Force
Remove-Item -LiteralPath $targetScripts -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item -LiteralPath $packageScripts -Destination (Join-Path $targetTriumph 'script') -Recurse

"Installed $($packageJars[0].Name) into $InstancePath"
"Backup saved to $backupRoot"
