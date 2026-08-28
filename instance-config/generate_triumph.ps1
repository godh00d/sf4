param(
    [switch]$Check
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$PlanPath = Join-Path $ProjectRoot "ACHIEVEMENT_PLAN.md"
$TreePath = Join-Path $ProjectRoot "ACHIEVEMENT_TREE.md"
$CoreCatalogPath = Join-Path $ProjectRoot "src\main\java\com\godh00d\sf4angel\handler\CoreAdvancementCatalog.java"
$ReactionCatalogPath = Join-Path $ProjectRoot "src\main\java\com\godh00d\sf4angel\personality\AchievementReactions.java"
$OutputRoot = Join-Path $PSScriptRoot "triumph"
$ScriptRoot = Join-Path $OutputRoot "script\sf4angel"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

$ItemOverrides = @{
    "sf4angel:core/starting_from_the_bottom" = @("<minecraft:log, meta:0~15>")
    "sf4angel:core/ultimate_capacity" = @("<mekanism:energycube, meta:0, nbt:{tier:3}>")
    "sf4angel:core/factory_settings" = @("<mekanism:machineblock, meta:5~7>")
    "sf4angel:core/pressing_engagement" = @(
        "<appliedenergistics2:material:13>", "<appliedenergistics2:material:14>",
        "<appliedenergistics2:material:15>", "<appliedenergistics2:material:19>"
    )
    "sf4angel:core/cookie_bacon_donut_collapse" = @(
        "<extendedcrafting:singularity_custom:1>",
        "<extendedcrafting:singularity_custom:2>",
        "<extendedcrafting:singularity_custom:3>"
    )
}

$AnyItems = @{
    "sf4angel:core/first_spark" = @(
        "<simplegenerators:combustion_simple>", "<simplegenerators:culinary_simple>",
        "<simplegenerators:ender_simple>", "<simplegenerators:nether_simple>",
        "<simplegenerators:soul_simple>", "<simplegenerators:geothermal_simple>",
        "<simplegenerators:fluid_combustion_simple>", "<simplegenerators:turbine_simple>"
    )
    "sf4angel:core/metals_into_points" = @(
        0, 1, 2, 3, 4, 5, 6, 7, 17, 18, 19, 22, 23, 24, 25, 27, 28,
        32, 34, 48, 49, 50, 64, 65, 66
    ) | ForEach-Object { "<extendedcrafting:singularity:$($_)>" }
}

$Dimensions = @{
    "sf4angel:core/nether_say_never" = -1
    "sf4angel:core/the_hunting_trip" = 28885
    "sf4angel:core/lost_and_found" = 111
    "sf4angel:core/the_void_blinks_back" = 1
    "sf4angel:core/into_the_twilight" = 7
}

$IconOverrides = @{
    "sf4angel:core/barnyard_beginnings" = "<minecraft:wheat>"
    "sf4angel:core/truffle_shuffle" = "<resourcehogs:mud_bucket>"
    "sf4angel:core/smeltery_authority" = "<minecraft:lava_bucket>"
    "sf4angel:core/modifier_motive" = "<tconstruct:toolforge>"
    "sf4angel:core/level_headed_tool" = "<tconstruct:toolforge>"
    "sf4angel:core/model_citizen" = "<deepmoblearning:data_model_zombie>"
    "sf4angel:core/wither_or_not" = "<minecraft:nether_star>"
    "sf4angel:core/dragon_eviction_notice" = "<minecraft:dragon_egg>"
    "sf4angel:core/naga_have_i_ever" = "<minecraft:skull>"
    "sf4angel:core/lich_please" = "<minecraft:skull>"
    "sf4angel:core/hydra_expectations" = "<minecraft:skull>"
    "sf4angel:core/ice_queen_cometh" = "<minecraft:skull>"
    "sf4angel:core/positive_fission" = "<minecraft:nether_star>"
    "sf4angel:core/the_sky_finally_claps" = "<minecraft:nether_star>"
    "sf4angel:optional/sticky_keys" = "<sky_orchards:sapling_dirt>"
    "sf4angel:optional/nap_time" = "<minecraft:bed>"
    "sf4angel:optional/a_balanced_sky_diet" = "<minecraft:apple>"
    "sf4angel:optional/mob_factory_floor" = "<minecraft:rotten_flesh>"
    "sf4angel:optional/armored_to_the_teeth" = "<conarm:armorforge>"
    "sf4angel:optional/unbreakable_resolve" = "<tconstruct:toolforge>"
    "sf4angel:optional/undo_the_apocalypse" = "<minecraft:brick_block>"
    "sf4angel:optional/pixel_perfect_masonry" = "<minecraft:stone>"
    "sf4angel:optional/around_the_void_in_eighty_throws" = "<tconstruct:toolforge>"
    "sf4angel:optional/android_dreams" = "<matteroverdrive:matter_analyzer>"
    "sf4angel:optional/robot_did_it" = "<minecraft:redstone>"
    "sf4angel:prestige/prestige_worldwide" = "<parabox:parabox>"
    "sf4angel:prestige/written_in_another_age" = "<minecraft:writable_book>"
    "sf4angel:prestige/empowered_recursion" = "<parabox:parabox>"
}

$PrestigeStages = @{
    "sf4angel:prestige/equivalent_ambition_unlocked" = "project_e"
    "sf4angel:prestige/aperture_unlocked" = "portal_gun"
    "sf4angel:prestige/written_in_another_age" = "mystcraft"
    "sf4angel:prestige/empowered_recursion" = "parabox_two"
}

$AchievementStages = @{
    "sf4angel:core/into_the_twilight" = "twilight_forest"
    "sf4angel:optional/android_dreams" = "android"
    "sf4angel:optional/robot_did_it" = "open_computers"
}

function Write-AsciiFile([string]$Path, [string[]]$Lines) {
    $Parent = Split-Path -Parent $Path
    [System.IO.Directory]::CreateDirectory($Parent) | Out-Null
    [System.IO.File]::WriteAllText($Path, (($Lines -join "`n") + "`n"), $Utf8NoBom)
}

function Get-Catalog {
    $Pattern = '^\| `(?<id>sf4angel:(?:core|optional|prestige)/[^`]+)` \| (?<title>.*?) \| (?<trigger>.*?) \| (?<parents>.*?) \| `(?<type>T-ITEM|T-ANY|T-ADV|T-LOCATION|J-EVENT|J-INTEGRATION|J-COMPOSITE)` \|$'
    $Rows = New-Object System.Collections.Generic.List[object]
    foreach ($Line in [System.IO.File]::ReadAllLines($PlanPath)) {
        if ($Line -notmatch $Pattern) { continue }
        $Parents = @()
        if ($Matches.parents -ne '`[]`') {
            $Parents = @([regex]::Matches($Matches.parents, '`(sf4angel:(?:core|optional|prestige)/[^`]+)`') | ForEach-Object { $_.Groups[1].Value })
        }
        $Rows.Add([pscustomobject]@{
            Id = $Matches.id
            Title = $Matches.title
            Trigger = $Matches.trigger
            Parents = $Parents
            Type = $Matches.type
        })
    }
    $Counts = @{
        core = @($Rows | Where-Object { $_.Id -like 'sf4angel:core/*' }).Count
        optional = @($Rows | Where-Object { $_.Id -like 'sf4angel:optional/*' }).Count
        prestige = @($Rows | Where-Object { $_.Id -like 'sf4angel:prestige/*' }).Count
    }
    if ($Rows.Count -ne 129 -or $Counts.core -ne 110 -or $Counts.optional -ne 13 -or $Counts.prestige -ne 6) {
        throw "Authority catalog count mismatch: $($Rows.Count) total; $($Counts.core)/$($Counts.optional)/$($Counts.prestige)"
    }
    if (@($Rows.Id | Sort-Object -Unique).Count -ne $Rows.Count) { throw "Duplicate authority ID" }
    Test-Prerequisites @($Rows | ForEach-Object { $_ })
    Test-TreeParents @($Rows | ForEach-Object { $_ })
    Test-CoreCatalog @($Rows | ForEach-Object { $_ })
    Test-AchievementReactions @($Rows | ForEach-Object { $_ })
    return @($Rows | ForEach-Object { $_ })
}

function Test-Prerequisites($Rows) {
    $ById = @{}
    foreach ($Row in $Rows) { $ById[$Row.Id] = $Row }
    foreach ($Row in $Rows) {
        foreach ($Parent in $Row.Parents) {
            if (-not $ById.ContainsKey($Parent)) { throw "Unknown achievement prerequisite $Parent for $($Row.Id)" }
            if ($Row.Id.StartsWith('sf4angel:core/') -and $Parent.StartsWith('sf4angel:optional/')) {
                throw "Optional achievement cannot parent core achievement $($Row.Id)"
            }
        }
    }
    $Visiting = @{}
    $Visited = @{}
    function Visit([string]$Id) {
        if ($Visiting.ContainsKey($Id)) { throw "Achievement prerequisite cycle at $Id" }
        if ($Visited.ContainsKey($Id)) { return }
        $Visiting[$Id] = $true
        foreach ($Parent in $ById[$Id].Parents) { Visit $Parent }
        $Visiting.Remove($Id)
        $Visited[$Id] = $true
    }
    foreach ($Row in $Rows) { Visit $Row.Id }
}

function Test-TreeParents($Rows) {
    $TreeLines = [System.IO.File]::ReadAllLines($TreePath)
    $Aliases = @{}
    foreach ($Line in $TreeLines) {
        foreach ($Match in [regex]::Matches($Line, '([COP][0-9]+)\["((?:core|optional|prestige)/[a-z0-9_]+)')) {
            $Aliases[$Match.Groups[1].Value] = "sf4angel:$($Match.Groups[2].Value)"
        }
    }
    $TreeEdges = @{}
    foreach ($Line in $TreeLines) {
        if (-not $Line.Contains('-->')) { continue }
        $Nodes = @([regex]::Matches($Line, '\b[COP][0-9]+\b') | ForEach-Object { $_.Value })
        for ($Index = 0; $Index -lt ($Nodes.Count - 1); $Index++) {
            if ($Aliases.ContainsKey($Nodes[$Index]) -and $Aliases.ContainsKey($Nodes[$Index + 1])) {
                $TreeEdges["$($Aliases[$Nodes[$Index]])>$($Aliases[$Nodes[$Index + 1]])"] = $true
            }
        }
    }
    $PlanEdges = @{}
    foreach ($Row in $Rows) {
        foreach ($Parent in $Row.Parents) { $PlanEdges["$Parent>$($Row.Id)"] = $true }
    }
    $Missing = @($PlanEdges.Keys | Where-Object { -not $TreeEdges.ContainsKey($_) } | Sort-Object)
    $Extra = @($TreeEdges.Keys | Where-Object { -not $PlanEdges.ContainsKey($_) } | Sort-Object)
    if ($Missing.Count -gt 0 -or $Extra.Count -gt 0) {
        throw "Achievement tree differs from plan. Missing edges: $($Missing -join ', '); extra edges: $($Extra -join ', ')"
    }
}

function Test-CoreCatalog($Rows) {
    $Content = [System.IO.File]::ReadAllText($CoreCatalogPath)
    $Actual = @{}
    foreach ($Match in [regex]::Matches($Content, 'add\("(?<path>[^"]+)"(?<parents>(?:,\s*"[^"]+")*)\);')) {
        $Parents = @([regex]::Matches($Match.Groups['parents'].Value, '"([^"]+)"') | ForEach-Object {
            "sf4angel:core/$($_.Groups[1].Value)"
        })
        $Actual["sf4angel:core/$($Match.Groups['path'].Value)"] = $Parents
    }
    $CoreRows = @($Rows | Where-Object { $_.Id.StartsWith('sf4angel:core/') })
    if ($Actual.Count -ne $CoreRows.Count) { throw "Core Java catalog count mismatch: $($Actual.Count)" }
    foreach ($Row in $CoreRows) {
        if (-not $Actual.ContainsKey($Row.Id)) { throw "Core Java catalog is missing $($Row.Id)" }
        if (($Actual[$Row.Id] -join '|') -ne ($Row.Parents -join '|')) {
            throw "Core Java prerequisites differ for $($Row.Id)"
        }
    }
}

function Test-AchievementReactions($Rows) {
    $Content = [System.IO.File]::ReadAllText($ReactionCatalogPath)
    $Matches = @([regex]::Matches($Content, 'reactions\.put\("(?<id>[^"]*)", "(?<text>[^"]*)"\);'))
    if ($Matches.Count -ne 129) { throw "Achievement reaction count mismatch: $($Matches.Count)" }

    $Expected = @{}
    foreach ($Row in $Rows) { $Expected[$Row.Id] = $true }
    $Actual = @{}
    $Texts = @{}
    foreach ($Match in $Matches) {
        $Id = $Match.Groups['id'].Value
        $Text = $Match.Groups['text'].Value
        if ($Actual.ContainsKey($Id)) { throw "Duplicate achievement reaction ID: $Id" }
        if ([string]::IsNullOrWhiteSpace($Text)) { throw "Blank achievement reaction: $Id" }
        if ($Text.Length -gt 80) { throw "Achievement reaction exceeds 80 characters: $Id" }
        if ($Text.ToCharArray() | Where-Object { [int]$_ -gt 127 }) { throw "Non-ASCII achievement reaction: $Id" }
        if ($Texts.ContainsKey($Text)) { throw "Duplicate achievement reaction text: $Id and $($Texts[$Text])" }
        $Actual[$Id] = $true
        $Texts[$Text] = $Id
    }
    $Missing = @($Expected.Keys | Where-Object { -not $Actual.ContainsKey($_) } | Sort-Object)
    $Extra = @($Actual.Keys | Where-Object { -not $Expected.ContainsKey($_) } | Sort-Object)
    if ($Missing.Count -gt 0 -or $Extra.Count -gt 0) {
        throw "Achievement reactions differ from plan. Missing: $($Missing -join ', '); extra: $($Extra -join ', ')"
    }
}

function Get-ItemExpressions($Row) {
    if ($ItemOverrides.ContainsKey($Row.Id)) { return @($ItemOverrides[$Row.Id]) }
    $Resource = [regex]::Match($Row.Trigger, '`([a-z0-9_]+:[a-z0-9_.*]+)`')
    if (-not $Resource.Success) { throw "No item target for $($Row.Id)" }
    $Item = $Resource.Groups[1].Value
    $Metadata = [regex]::Match($Row.Trigger, 'metadata `([0-9]+)`')
    if ($Metadata.Success -and $Metadata.Groups[1].Value -ne '0') {
        return @("<$($Item):$($Metadata.Groups[1].Value)>")
    }
    return @("<$Item>")
}

function Get-Icon($Row) {
    if ($IconOverrides.ContainsKey($Row.Id)) { return $IconOverrides[$Row.Id] }
    if ($Row.Type -eq 'T-ITEM' -or $Row.Type -eq 'T-ANY') {
        if ($Row.Type -eq 'T-ANY') { $Items = @($AnyItems[$Row.Id]) } else { $Items = @(Get-ItemExpressions $Row) }
        $RegistryName = [regex]::Match($Items[0], '^<([a-z0-9_]+:[a-z0-9_]+)')
        if (-not $RegistryName.Success) { throw "Invalid icon item expression for $($Row.Id)" }
        return "<$($RegistryName.Groups[1].Value)>"
    }
    if ($Row.Type -eq 'T-LOCATION') { return '<minecraft:compass>' }
    $Resource = [regex]::Match($Row.Trigger, '`([a-z0-9_]+:[a-z0-9_.*]+)`')
    if ($Resource.Success -and -not $Resource.Groups[1].Value.EndsWith('_*')) {
        return "<$($Resource.Groups[1].Value)>"
    }
    return '<minecraft:redstone>'
}

function Get-CriterionLines($Row) {
    if ($Row.Type.StartsWith('J-')) { return @('criteria = addCriteria("custom", "minecraft:impossible")') }
    if ($Row.Type -eq 'T-LOCATION') {
        if (-not $Dimensions.ContainsKey($Row.Id)) { throw "Missing dimension for $($Row.Id)" }
        return @('criteria = addCriteria("location", "minecraft:location")', "criteria.setDimID($($Dimensions[$Row.Id]))")
    }
    if ($Row.Type -eq 'T-ADV') {
        if ($Row.Parents.Count -eq 0) { throw "Parent-only goal has no parents: $($Row.Id)" }
        return @('criteria = addCriteria("parents", "triumph:completed_advancement")', "criteria.setAdvancement(`"$($Row.Parents[-1])`")")
    }
    if ($Row.Type -eq 'T-ANY') {
        if (-not $AnyItems.ContainsKey($Row.Id)) { throw "Missing T-ANY alternatives for $($Row.Id)" }
        $Lines = New-Object System.Collections.Generic.List[string]
        $Lines.Add('setRequirements("any")')
        $Index = 1
        foreach ($Item in $AnyItems[$Row.Id]) {
            $Lines.Add("criteria$Index = addCriteria(`"item_$Index`", `"minecraft:inventory_changed`")")
            $Lines.Add("criteria$Index.addItem($Item)")
            $Index++
        }
        return @($Lines | ForEach-Object { $_ })
    }
    if ($Row.Type -eq 'T-ITEM') {
        $Lines = New-Object System.Collections.Generic.List[string]
        $Lines.Add('criteria = addCriteria("item", "minecraft:inventory_changed")')
        foreach ($Item in (Get-ItemExpressions $Row)) { $Lines.Add("criteria.addItem($Item)") }
        return @($Lines | ForEach-Object { $_ })
    }
    throw "Unsupported type $($Row.Type)"
}

function Get-RequiredStages($Row) {
    $Stages = New-Object System.Collections.Generic.List[string]
    if ($AchievementStages.ContainsKey($Row.Id)) { $Stages.Add($AchievementStages[$Row.Id]) }
    if ($Row.Id.StartsWith('sf4angel:prestige/')) {
        $Stages.Add('parabox')
        if ($PrestigeStages.ContainsKey($Row.Id)) { $Stages.Add($PrestigeStages[$Row.Id]) }
    }
    return @($Stages | ForEach-Object { $_ })
}

function Get-AchievementLines($Row, [int]$Index) {
    $Page = $Row.Id.Split(':')[1].Split('/')[0]
    $Columns = @{ core = 10; optional = 7; prestige = 3 }[$Page]
    $X = ($Index % $Columns) * 54
    $Y = [math]::Floor($Index / $Columns) * 48
    $Title = $Row.Title.Replace('\', '\\').Replace('"', '\"')
    $Description = $Row.Trigger.Replace('`', '').Replace('\', '\\').Replace('"', '\"')
    $Lines = New-Object System.Collections.Generic.List[string]
    $Lines.Add("setTitle(`"$Title`")")
    $Lines.Add("setDescription(`"$Description`")")
    $Lines.Add("setIcon($(Get-Icon $Row))")
    $Lines.Add("setPos($X,$Y)")
    if ($Row.Parents.Count -eq 0) {
        $Lines.Add("addParent(`"sf4angel:$Page/root`")")
    } else {
        foreach ($Parent in $Row.Parents) { $Lines.Add("addParent(`"$Parent`")") }
        $Lines.Add('setRequiresParents()')
    }
    if ($AchievementStages.ContainsKey($Row.Id)) { $Lines.Add("hiddenUnless(`"$($AchievementStages[$Row.Id])`")") }
    if ($Page -eq 'prestige') {
        $Lines.Add('hiddenUnless("parabox")')
        if ($PrestigeStages.ContainsKey($Row.Id)) { $Lines.Add("hiddenUnless(`"$($PrestigeStages[$Row.Id])`")") }
    }
    $StageIndex = 1
    foreach ($Stage in (Get-RequiredStages $Row)) {
        $Lines.Add("criteriaStage$StageIndex = addCriteria(`"stage_$StageIndex`", `"triumph:gamestage`")")
        $Lines.Add("criteriaStage$StageIndex.setStage(`"$Stage`")")
        $StageIndex++
    }
    foreach ($Line in (Get-CriterionLines $Row)) { $Lines.Add($Line) }
    return @($Lines | ForEach-Object { $_ })
}

function Write-Configuration($Rows) {
    if ([System.IO.Directory]::Exists($ScriptRoot)) { [System.IO.Directory]::Delete($ScriptRoot, $true) }
    $Roots = @{
        core = @('Core Progression', 'The complete SkyFactory 4 progression catalog.', '<sky_orchards:sapling_dirt>', 'test:textures/colors/sky_blue.png')
        optional = @('Optional Challenges', 'Optional measurable challenges outside core progression.', '<minecraft:diamond>', 'test:textures/colors/purple.png')
        prestige = @('Prestige', 'Goals available only while Prestige mode is enabled.', '<parabox:parabox>', 'test:textures/colors/black.png')
    }
    foreach ($Page in @('core', 'optional', 'prestige')) {
        $Data = $Roots[$Page]
        $Lines = @(
            "setTitle(`"$($Data[0])`")", "setDescription(`"$($Data[1])`")", "setIcon($($Data[2]))",
            "setBackground(`"$($Data[3])`")", 'setShowToast(false)', 'setAnnounceToChat(false)', 'setPos(-54,0)'
        )
        if ($Page -eq 'prestige') {
            $Lines += @('criteria = addCriteria("stage", "triumph:gamestage")', 'criteria.setStage("parabox")', 'pageHiddenUnless("parabox")', 'pageAlwaysVisible()')
        } else {
            $Lines += @('criteria = addCriteria("location", "minecraft:location")', 'pageRequiresRoot()', 'pageAlwaysVisible()')
        }
        Write-AsciiFile (Join-Path $ScriptRoot "$Page\root.txt") $Lines
    }
    $Indexes = @{ core = 0; optional = 0; prestige = 0 }
    foreach ($Row in $Rows) {
        $Relative = $Row.Id.Split(':')[1]
        $Page = $Relative.Split('/')[0]
        $Name = $Relative.Substring($Page.Length + 1)
        Write-AsciiFile (Join-Path $ScriptRoot "$Page\$Name.txt") (Get-AchievementLines $Row $Indexes[$Page])
        $Indexes[$Page]++
    }
    Write-AsciiFile (Join-Path $OutputRoot 'Triumph.txt') @(
        'printDefaultConfigs(false)', 'printDocumentation(false)', 'removeVanillaAdvancements(true)',
        'removeModAdvancements(false)', 'keepAdvancements(["twilightforest"])',
        'removeAdvancements(["twilightforest:progress_castle", "twilightforest:progress_thorns"])',
        'useNewParentCompletionCriteriaNames()',
        'pageOrder(["sf4angel:core/root", "sf4angel:optional/root", "sf4angel:prestige/root"])'
    )
}

function Test-Configuration($Rows) {
    $Errors = New-Object System.Collections.Generic.List[string]
    $Expected = @{}
    foreach ($Row in $Rows) { $Expected[$Row.Id] = $Row }
    $Files = @(Get-ChildItem -LiteralPath $ScriptRoot -Recurse -Filter '*.txt' | Where-Object { $_.Name -ne 'root.txt' })
    if ($Files.Count -ne 129) { $Errors.Add("Expected 129 achievement scripts, found $($Files.Count)") }
    $ActualIds = @{}
    foreach ($File in $Files) {
        $Relative = $File.FullName.Substring($ScriptRoot.Length + 1).Replace('\', '/')
        $ActualIds["sf4angel:$($Relative.Substring(0, $Relative.Length - 4))"] = $File.FullName
    }
    foreach ($Id in $Expected.Keys) { if (-not $ActualIds.ContainsKey($Id)) { $Errors.Add("Missing script $Id") } }
    foreach ($Id in $ActualIds.Keys) { if (-not $Expected.ContainsKey($Id)) { $Errors.Add("Unexpected script $Id") } }
    $Positions = @{ core = @{}; optional = @{}; prestige = @{} }
    foreach ($Id in $Expected.Keys) {
        if (-not $ActualIds.ContainsKey($Id)) { continue }
        $Row = $Expected[$Id]
        $Content = [System.IO.File]::ReadAllText($ActualIds[$Id])
        $Bytes = [System.IO.File]::ReadAllBytes($ActualIds[$Id])
        if (@($Bytes | Where-Object { $_ -gt 127 }).Count -gt 0) { $Errors.Add("Non-ASCII output in $Id") }
        if (-not $Content.EndsWith("`n") -or $Content.Contains("`r") -or $Content.Contains("`t") -or $Content -match '(?m) +$' -or $Content.Contains("`n`n")) {
            $Errors.Add("Invalid whitespace in $Id")
        }
        foreach ($Call in @('setTitle', 'setDescription', 'setIcon', 'setPos')) {
            if ([regex]::Matches($Content, "(?m)^$Call\(").Count -ne 1) { $Errors.Add("$Id must contain one $Call") }
        }
        if (-not [regex]::IsMatch($Content, '(?m)^setIcon\(<[a-z0-9_]+:[a-z0-9_]+>\)$')) { $Errors.Add("Malformed icon for $Id") }
        $Parents = @([regex]::Matches($Content, '(?m)^addParent\("([^"]+)"\)$') | ForEach-Object { $_.Groups[1].Value })
        $Page = $Id.Split(':')[1].Split('/')[0]
        $ExpectedParents = if ($Row.Parents.Count -eq 0) { @("sf4angel:$Page/root") } else { @($Row.Parents) }
        if (($Parents -join '|') -ne ($ExpectedParents -join '|')) { $Errors.Add("Display parent mismatch for $Id") }
        foreach ($Parent in $Parents) {
            if (-not $Expected.ContainsKey($Parent) -and $Parent -ne "sf4angel:$Page/root") { $Errors.Add("Unknown parent $Parent for $Id") }
        }
        $Requires = [regex]::Matches($Content, '(?m)^setRequiresParents\(\)$').Count
        if ($Requires -ne [int]($Row.Parents.Count -gt 0)) { $Errors.Add("Achievement prerequisite mismatch for $Id") }
        $Position = [regex]::Match($Content, '(?m)^setPos\((-?[0-9]+),(-?[0-9]+)\)$')
        if ($Position.Success) {
            $Page = $Id.Split(':')[1].Split('/')[0]
            $Key = "$($Position.Groups[1].Value),$($Position.Groups[2].Value)"
            if ($Positions[$Page].ContainsKey($Key)) { $Errors.Add("Duplicate $Page position $Key") } else { $Positions[$Page][$Key] = $true }
        }
        $Criteria = @([regex]::Matches($Content, 'addCriteria\("([^"]+)", "([^"]+)"\)'))
        $FunctionalCriteria = @($Criteria | Where-Object { $_.Groups[2].Value -ne 'triumph:gamestage' })
        $GameStageCriteria = @([regex]::Matches($Content, '(?m)^criteriaStage([0-9]+) = addCriteria\("stage_([0-9]+)", "triumph:gamestage"\)$'))
        $ActualStages = @([regex]::Matches($Content, '(?m)^criteriaStage[0-9]+\.setStage\("([^"]+)"\)$') | ForEach-Object { $_.Groups[1].Value })
        $ExpectedStages = @(Get-RequiredStages $Row)
        if (($ActualStages -join '|') -ne ($ExpectedStages -join '|')) { $Errors.Add("Eligibility stage mismatch for $Id") }
        if ($GameStageCriteria.Count -ne $ExpectedStages.Count) { $Errors.Add("Eligibility criterion count mismatch for $Id") }
        for ($StageOffset = 0; $StageOffset -lt $ExpectedStages.Count; $StageOffset++) {
            $StageIndex = $StageOffset + 1
            if (-not $Content.Contains("criteriaStage$StageIndex = addCriteria(`"stage_$StageIndex`", `"triumph:gamestage`")") -or
                -not $Content.Contains("criteriaStage$StageIndex.setStage(`"$($ExpectedStages[$StageOffset])`")")) {
                $Errors.Add("Invalid eligibility criterion $StageIndex for $Id")
            }
        }
        $HiddenStages = @([regex]::Matches($Content, '(?m)^hiddenUnless\("([^"]+)"\)$') | ForEach-Object { $_.Groups[1].Value })
        if (($HiddenStages -join '|') -ne ($ExpectedStages -join '|')) { $Errors.Add("Eligibility visibility mismatch for $Id") }
        if ($ExpectedStages.Count -gt 0 -and $Content.Contains('setHidden(true)')) {
            $Errors.Add("Stage-gated achievement must not use setHidden(true): $Id")
        }
        if ($Row.Type.StartsWith('J-')) {
            if ($FunctionalCriteria.Count -ne 1 -or $FunctionalCriteria[0].Groups[1].Value -ne 'custom' -or $FunctionalCriteria[0].Groups[2].Value -ne 'minecraft:impossible') { $Errors.Add("Invalid Java criterion for $Id") }
        } elseif ($Row.Type -eq 'T-ANY') {
            if ($FunctionalCriteria.Count -ne $AnyItems[$Id].Count -or -not $Content.Contains('setRequirements("any")')) { $Errors.Add("Invalid T-ANY criteria for $Id") }
            $ActualItems = @([regex]::Matches($Content, '(?m)\.addItem\((<.*>)\)$') | ForEach-Object { $_.Groups[1].Value })
            if (($ActualItems -join '|') -ne ($AnyItems[$Id] -join '|')) { $Errors.Add("T-ANY alternatives mismatch for $Id") }
        } elseif ($Row.Type -eq 'T-ITEM') {
            if ($FunctionalCriteria.Count -ne 1 -or $FunctionalCriteria[0].Groups[2].Value -ne 'minecraft:inventory_changed') { $Errors.Add("Invalid item criterion for $Id") }
            $ActualItems = @([regex]::Matches($Content, '(?m)^criteria\.addItem\((<.*>)\)$') | ForEach-Object { $_.Groups[1].Value })
            if (($ActualItems -join '|') -ne ((Get-ItemExpressions $Row) -join '|')) { $Errors.Add("Item predicate mismatch for $Id") }
        } elseif ($Row.Type -eq 'T-LOCATION') {
            if ($FunctionalCriteria.Count -ne 1 -or -not $Content.Contains("criteria.setDimID($($Dimensions[$Id]))")) { $Errors.Add("Invalid location criterion for $Id") }
        } elseif ($Row.Type -eq 'T-ADV') {
            $AdvancementTargets = @([regex]::Matches($Content, '(?m)^criteria\.setAdvancement\("([^"]+)"\)$') | ForEach-Object { $_.Groups[1].Value })
            if ($FunctionalCriteria.Count -ne 1 -or $FunctionalCriteria[0].Groups[1].Value -ne 'parents' -or
                $FunctionalCriteria[0].Groups[2].Value -ne 'triumph:completed_advancement' -or
                $AdvancementTargets.Count -ne 1 -or $AdvancementTargets[0] -ne $Row.Parents[-1]) {
                $Errors.Add("Invalid parent criterion target for $Id")
            }
        }
        if ($Id.StartsWith('sf4angel:prestige/') -and -not $Content.Contains('hiddenUnless("parabox")')) {
            $Errors.Add("Missing Prestige gate for $Id")
        }
        if ($PrestigeStages.ContainsKey($Id) -and -not $Content.Contains("hiddenUnless(`"$($PrestigeStages[$Id])`")")) {
            $Errors.Add("Missing unlock stage $($PrestigeStages[$Id]) for $Id")
        }
        if ($AchievementStages.ContainsKey($Id) -and -not $Content.Contains("hiddenUnless(`"$($AchievementStages[$Id])`")")) {
            $Errors.Add("Missing stage $($AchievementStages[$Id]) for $Id")
        }
    }
    foreach ($Page in @('core', 'optional', 'prestige')) {
        $RootPath = Join-Path $ScriptRoot "$Page\root.txt"
        if (-not [System.IO.File]::Exists($RootPath)) {
            $Errors.Add("Missing $Page root")
            continue
        }
        $RootContent = [System.IO.File]::ReadAllText($RootPath)
        if (-not $RootContent.EndsWith("`n") -or $RootContent.Contains("`r") -or $RootContent.Contains("`t") -or $RootContent -match '(?m) +$' -or $RootContent.Contains("`n`n")) {
            $Errors.Add("Invalid whitespace in $Page root")
        }
        foreach ($Call in @('setTitle', 'setDescription', 'setIcon', 'setPos')) {
            if ([regex]::Matches($RootContent, "(?m)^$Call\(").Count -ne 1) { $Errors.Add("$Page root must contain one $Call") }
        }
        if ($Page -eq 'prestige' -and (-not $RootContent.Contains('criteria.setStage("parabox")') -or -not $RootContent.Contains('pageHiddenUnless("parabox")'))) {
            $Errors.Add('Prestige root is not gated by parabox')
        }
    }
    $Order = 'pageOrder(["sf4angel:core/root", "sf4angel:optional/root", "sf4angel:prestige/root"])'
    $TriumphContent = [System.IO.File]::ReadAllText((Join-Path $OutputRoot 'Triumph.txt'))
    if (-not $TriumphContent.Contains($Order)) { $Errors.Add('Page order mismatch') }
    if (-not $TriumphContent.EndsWith("`n") -or $TriumphContent.Contains("`r") -or $TriumphContent -match '(?m) +$') { $Errors.Add('Invalid Triumph.txt whitespace') }
    if ($Errors.Count -gt 0) { throw "Validation failed:`n- $($Errors -join "`n- ")" }
    Write-Output 'Validated 129 achievements and unique reactions, 3 roots, Java/plan/tree parity, prerequisite/display parents, criteria, positions, stage gates, and whitespace.'
}

$Catalog = Get-Catalog
if (-not $Check) { Write-Configuration $Catalog }
Test-Configuration $Catalog
