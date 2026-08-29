[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^\d+\.\d+\.\d+(?:[-+][0-9A-Za-z.-]+)?$')]
    [string]$Version
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jar = Join-Path $projectRoot "build\libs\sf4angel-$Version.jar"
$packageName = "SF4-Angel-Guide-$Version"
$stagingRoot = Join-Path $projectRoot "build\release\$packageName"
$outputDirectory = Join-Path $projectRoot 'dist'
$outputArchive = Join-Path $outputDirectory "$packageName.zip"

if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) {
    throw "Missing $jar. Build this version with .\gradlew.bat clean build first."
}

Remove-Item -LiteralPath $stagingRoot -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -LiteralPath $outputArchive -Force -ErrorAction SilentlyContinue

$modsDirectory = New-Item -ItemType Directory -Path (Join-Path $stagingRoot 'mods') -Force
$triumphDirectory = New-Item -ItemType Directory -Path (Join-Path $stagingRoot 'config\triumph') -Force
$scriptDirectory = New-Item -ItemType Directory -Path (Join-Path $triumphDirectory 'script') -Force
New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null

Copy-Item -LiteralPath $jar -Destination $modsDirectory.FullName
Copy-Item -LiteralPath (Join-Path $projectRoot 'instance-config\triumph\Triumph.txt') `
    -Destination $triumphDirectory.FullName
Copy-Item -LiteralPath (Join-Path $projectRoot 'instance-config\triumph\script\sf4angel') `
    -Destination $scriptDirectory.FullName -Recurse
Copy-Item -LiteralPath (Join-Path $projectRoot 'docs\INSTALLATION.md') -Destination $stagingRoot
Copy-Item -LiteralPath (Join-Path $projectRoot 'LICENSE') -Destination $stagingRoot

Compress-Archive -Path (Join-Path $stagingRoot '*') -DestinationPath $outputArchive
$hash = Get-FileHash -LiteralPath $outputArchive -Algorithm SHA256
"Created $outputArchive"
"SHA-256 $($hash.Hash)"
