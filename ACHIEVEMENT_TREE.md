# SF4 Angel Achievement Tree

The numbered ranges correspond to `ACHIEVEMENT_PLAN.md`.

## Complete Overview

```mermaid
flowchart TD
    START([SkyFactory Start])
    BOOT["1-29: Dirt Tree Bootstrap"]
    TREES["30-50: Resource Tree Network"]
    FARM["51-75: Farming, Storage, and Bonsai"]
    TOOLS["76-85: Tinkers Tools and Smeltery"]
    POWER["86-93: First Power and Mechanical Automation"]

    MEK["94-118: Mekanism Processing"]
    IF["119-131: Industrial Foregoing"]
    DML["132-146: Deep Mob Learning"]

    DIM["147-163: Dimension Access"]
    METALS["164-167: Cobalt, Ardite, Manyullyn, Prosperity"]
    TWILIGHT["168-176: Twilight Boss Progression"]

    MYST["177-185: Mystical Agriculture"]
    LOGIC["186-193: Integrated Logistics"]
    AE2["194-211: Channel-Free AE2 and Extra Cells"]

    NC["212-228: NuclearCraft Fission and Fusion"]
    MO["229-234: Matter Overdrive"]
    MEKEND["235-238: Mekanism Multiblocks"]
    EXT["239-249: Storage Capstones and Singularities"]
    FINAL(["250: The Sky Finally Claps"])

    START --> BOOT --> TREES
    TREES --> FARM
    TREES --> TOOLS
    FARM --> POWER
    TOOLS --> POWER

    POWER --> MEK
    POWER --> IF
    POWER --> DML
    MEK --> DIM
    IF --> DIM
    DML --> DIM

    DIM --> METALS
    DIM --> TWILIGHT
    METALS --> MYST
    METALS --> TOOLS

    MYST --> LOGIC
    MEK --> LOGIC
    LOGIC --> AE2

    DIM --> NC
    MEK --> NC
    DIM --> MO
    MEK --> MEKEND

    AE2 --> EXT
    NC --> EXT
    MO --> EXT
    MEKEND --> EXT
    IF --> EXT
    MYST --> EXT
    TWILIGHT --> FINAL
    EXT --> FINAL
```

## Early Resource Tree

```mermaid
flowchart TD
    A1["1 Starting from the Bottom"] --> A2["2 That's Dir-tree"]
    A2 --> A3["3 Dirty Dancing"]
    A2 --> A4["4 Captain Hook"] --> A5["5 Air Fryer"] --> A6["6 A Hard Nut to Crack"] --> A7["7 Hardwood"]
    A7 --> A8["8 Stone from Trees"]
    A3 --> A9["9 Stomp the Yard"]

    A7 --> A10["10 Gravel Travel"]
    A7 --> A11["11 Beachfront Property"]
    A7 --> A12["12 Break a Leg"]
    A3 --> A13["13 Clay Aiken"]

    A8 --> A14["14 Bubble, Bubble"]
    A9 --> A15["15 Water You Waiting For?"]
    A13 --> A16["16 Slop in the Bucket"]
    A12 --> A17["17 You Cotton Be Kidding Me"] --> A18["18 You Cotton Something?"] --> A19["19 Nap Time"]

    A11 --> A20["20 Sixteen Tons"] --> A21["21 Coal in Bloom"]
    A8 --> A22["22 Hotbois"]
    A14 --> A22
    A15 --> A23["23 Study in the Dark Arts"]
    A22 --> A23
    A15 --> A24["24 Cobble Cobble"]
    A22 --> A24

    A12 --> A25["25 Porcelain Promise"]
    A13 --> A25
    A25 --> A26["26 Melting Point"] --> A27["27 Turn Up the Heat"]
    A25 --> A28["28 Table for Molten One"]
    A25 --> A29["29 Basin Expectations"]

    A10 --> A30["30 Amber is the Color"]
    A11 --> A30
    A13 --> A30
    A21 --> A30
    A30 --> A31["31 Ironwood"] --> A32["32 Oh, the Irony"] --> A33["33 Not a Helmet"]

    A20 --> A34["34 Red Tree Redemption"]
    A12 --> A35["35 Blue Man Group"]
    A7 --> A36["36 Copper Top"] --> A37["37 Copper Canopy"]
    A10 --> A38["38 Tin Man"] --> A39["39 Tin This Pack"]
    A11 --> A40["40 Lead It Go"] --> A41["41 Lead Zeppelin"]
    A39 --> A42["42 Silver Surfer"] --> A43["43 Silver Spoon"]
    A7 --> A44["44 Nickel and Dime"] --> A45["45 Nickelback to Nature"]
    A11 --> A46["46 One Shiny Boi"] --> A47["47 Money Grows on Trees"]

    A34 --> A48["48 This Tree Is Forever"]
    A35 --> A48
    A43 --> A48
    A47 --> A48
    A48 --> A49["49 Carbon Copy"] --> A50["50 Trees to Diamonds"]
```

## Farming, Storage, and Tools

```mermaid
flowchart TD
    A50["50 Trees to Diamonds"]
    A51["51 Tiny Tree, Big Plans"] --> A52["52 Drop It Like It's Hopping"]
    A53["53 Paperwork Begins"]
    A54["54 Bulk by Barrel"] --> A55["55 It Fits, We Ship"]
    A56["56 Network Attached Chest"] --> A57["57 Request Granted"]
    A56 --> A58["58 Link in the Chain"]
    A57 --> A59["59 Remote Possibilities"]
    A58 --> A59
    A60["60 Pack It Up"] --> A61["61 Amber on Demand"]

    A50 --> A51
    A32["32 Iron"] --> A52
    A18["18 String"] --> A53
    A32 --> A54
    A32 --> A56
    A34["34 Redstone"] --> A56
    A34 --> A60

    A62["62 Market Forces"] --> A63["63 Seeds of Life"] --> A64["64 The Cow Grows Here"] --> A65["65 Milk Without the Moo"]
    A63 --> A66["66 Hog Tied"] --> A67["67 Truffle Shuffle"]
    A11["11 Sand"] --> A68["68 Snad Together"] --> A69["69 Cane and Able"]
    A50 --> A70["70 Ender Agriculture"]

    A62 --> A71["71 Brownfield Development"] --> A72["72 Yellow Growth Journalism"] --> A73["73 Amber Waves of Mulch"] --> A74["74 Ruby Roots"] --> A75["75 Blue-Chip Bonsai"]

    A3["3 Dirt"] --> A76["76 Tooling Up"]
    A76 --> A77["77 Parts Department"]
    A76 --> A78["78 Pattern Recognition"]
    A28["28 Casting Table"] --> A79["79 Cast Away"]
    A76 --> A80["80 Forge Ahead"]
    A77 --> A81["81 Modifier Motive"] --> A82["82 Level-Headed Tool"]
    A26["26 Melter"] --> A83["83 Smeltery Authority"]
    A34 --> A84["84 Purple Reign"]
    A35["35 Lapis"] --> A84
    A83 --> A84
    A77 --> A85["85 Armor by Committee"]
    A80 --> A85
    A83 --> A85
```

## Technology Tree

```mermaid
flowchart TD
    BASE["Iron, Redstone, Smeltery"] --> A86["86 First Spark"]
    A86 --> A87["87 Combustion Discussion"]
    BASE --> A88["88 Geothermal Ambition"]
    A86 --> A89["89 Battery Included"]
    A86 --> A90["90 Wireless Current Events"]
    A86 --> A91["91 Clicks While Away"]
    ANIMALS["Animal Farming"] --> A92["92 Horsepower, Literally"] --> A93["93 Crushing Responsibility"]

    A86 --> A94["94 Redstone in a Box"]
    A94 --> A95["95 Infused with Purpose"]
    A94 --> A96["96 Steel Yourself"]
    A94 --> A97["97 Circuit Training"]
    A95 --> A98["98 Enriched Expectations"]
    A96 --> A98
    A97 --> A98
    A96 --> A99["99 Electric Dreams"]
    A97 --> A100["100 Crush Depth"]

    A95 --> A101["101 Separate Ways"]
    A96 --> A101
    A97 --> A101
    A100 --> A101
    A101 --> A102["102 Hydrogen Economy"] --> A103["103 Gas, Grass, or RF"]

    A98 --> A104["104 Purity of Purpose"]
    A98 --> A105["105 Triple Threat"]
    A100 --> A105
    A104 --> A105
    A104 --> A106["106 Chemical Romance"]
    A106 --> A107["107 Four of a Kind"]
    A106 --> A108["108 Dissolution Solution"] --> A109["109 Wash Cycle"] --> A110["110 Crystal Clear"]
    A107 --> A111["111 Five Times the Charm"]
    A110 --> A111

    A100 --> A112["112 Plastic Fantastic"]
    A102 --> A112
    A112 --> A113["113 HDPEasy"]
    A95 --> A114["114 Cube Route"] --> A115["115 Ultimate Capacity"]
    A98 --> A116["116 Factory Settings"] --> A117["117 Ultimate Tier Installer"]
    A115 --> A118["118 Digital Prospector"]

    TREES["Resource Trees"] --> A119["119 Latex Intentions"] --> A120["120 Rubber Meets the Void"] --> A121["121 Plastic Industry"] --> A122["122 Machine Frame of Mind"]
    A122 --> A123["123 Sow Automatic"]
    A122 --> A124["124 Reap Automatic"]
    A123 --> A125["125 The Farm Farms Back"]
    A124 --> A125
    A122 --> A126["126 Mob Rules"]
    A126 --> A127["127 Liquid Meat Market"]
    A126 --> A128["128 Pink Slime Time"]
    A122 --> A129["129 Bore from Nothing"] --> A130["130 Laser-Focused Resources"]
    A122 --> A131["131 Black Hole Inventory"]

    BASE --> A132["132 Learn Deeply"] --> A133["133 Model Citizen"]
    A133 --> A134["134 Zombie by Numbers"] --> A135["135 Data with Experience"]
    A135 --> A136["136 Simulation Theory"]
    A133 --> A137["137 Polymer Clay Day"]
    A135 --> A138["138 Matter of Record"]
    A136 --> A138
    A137 --> A138
    A138 --> A139["139 Pristine Condition"] --> A140["140 Fabricated Loot"]
    A133 --> A141["141 Skeleton Crew Data"]
    A133 --> A142["142 Enderman Analytics"]
    A133 --> A143["143 Withering Research"]
    A133 --> A144["144 Dragon Dataset"]
    A135 --> A145["145 Glitch in the System"] --> A146["146 Reality Armor"]
```

## Dimensions and Endgame

```mermaid
flowchart TD
    MID["Midgame Technology"] --> NETHER["147-150: Nether, Blaze, Wither"]
    MID --> HUNT["151-152: Hunting Dimension"]
    MID --> LOST["153-154: Lost Cities"]
    MID --> ENDDIM["155-158: End and Dragon"]
    MID --> COMPACT["159-161: Compact Machines"]
    MID --> TWACCESS["162-163: Twilight Access"]

    LOST --> ARDITE["164: Ardite Arboretum"]
    TWACCESS --> ARDITE
    NETHER --> ARDITE
    ARDITE --> COBALT["165: Cobalt Canopy"]
    COBALT --> MANY["166: Manyullyn Falcon"]
    COBALT --> PROSP["167: Prosperity in Bloom"]

    TWACCESS --> TWBOSS["168-176: Naga to Snow Queen"]
    PROSP --> MYST["177-185: Mystical Agriculture"]
    MANY --> LOGIC["186-193: Integrated Dynamics and Tunnels"]
    LOGIC --> AE2["194-211: Channel-Free AE2"]

    LOST --> NC["212-228: NuclearCraft"]
    TWACCESS --> NC
    LOST --> MO["229-234: Matter Overdrive"]
    TWACCESS --> MO
    MID --> MEKEND["235-238: Mekanism Multiblocks"]

    AE2 --> CAP["242-245: Storage Capstones"]
    NC --> EXT["239-249: Extended Crafting"]
    MO --> EXT
    MEKEND --> EXT
    MYST --> EXT
    CAP --> FINAL["250: The Sky Finally Claps"]
    EXT --> FINAL
    TWBOSS --> FINAL
```

## Optional Branches

```mermaid
flowchart LR
    EARLY["Early Resources"] --> B1["1 Sticky Keys"]
    EARLY --> B2["2 Roast of the Town"]
    EARLY --> B3["3 Slime Time Live"] --> B4["4 Green with Envy"]

    FARM["Farming"] --> B5["5 Barnyard Botanist"] --> B6["6 Seeds of Unusual Consequence"] --> B7["7 Hostile Agriculture"]
    FARM --> B8["8 This Little Pig Mined Ore"] --> B9["9 Mud, Sweat, and Ingots"] --> B10["10 Fifty Shades of Truffle"]
    FARM --> B11["11 Kitchen in the Clouds"] --> B12["12 Bread Between Worlds"] --> B13["13 The Dagwood Singularity"]
    FARM --> B14["14 A Balanced Sky Diet"]
    FARM --> B15["15 Vintage from the Void"] --> B16["16 Bee Rustic, Bee Happy"] --> B17["17 Ironberries and Ambition"]
    FARM --> B18["18 Green Thumb Drive"]
    FARM --> B19["19 Feed the Multitude"]
    FARM --> B20["20 Mob Factory Floor"] --> B21["21 Experience Plumbing"]

    BUILD["Tools and Building"] --> B22["22 Plate Expectations"] --> B23["23 Armored to the Teeth"]
    BUILD --> B24["24 Cleaver Endeavor"]
    BUILD --> B25["25 Unbreakable Resolve"]
    BUILD --> B26["26 Spin Doctor"] --> B27["27 Around the Void in Eighty Throws"]
    BUILD --> B28["28 Hammer Space"] --> B29["29 Carved from Nothing"]
    B28 --> B30["30 A Bit Off the Block"] --> B31["31 Pixel Perfect Masonry"]
    BUILD --> B32["32 Measure Twice, Gadget Once"] --> B33["33 Exchange Rate: One Wall"] --> B34["34 Undo the Apocalypse"]
    BUILD --> B35["35 Stone-Faced Collector"]
    BUILD --> B36["36 Shelf Esteem"] --> B37["37 Typeset in the Sky"]
    BUILD --> B38["38 Airship Shape"] --> B39["39 Cloud Nine, Propeller One"]

    LOGI["Logistics"] --> B40["40 Practical Data, Impractical Altitude"] --> B41["41 Read It and Reap"]
    LOGI --> B42["42 Connect the X-Dots"] --> B43["43 Channel Surfer"]
    LOGI --> B44["44 Omnidirectional Thinking"]
    LOGI --> B45["45 Ender Chest Matchmaker"] --> B46["46 Tank You Very Much"]
    LOGI --> B47["47 Handy Bag of Holding"]
    LOGI --> B48["48 The Elevator at World's End"]
    LOGI --> B49["49 Translocation, Translocation"]
    LOGI --> B50["50 Charge of the Light Brigade"]

    MAGIC["Magic and Identity"] --> B51["51 Starlight, Star Bright"] --> B52["52 Rock Crystal Method"] --> B53["53 Table for One Constellation"] --> B54["54 Attuned to the Void"] --> B55["55 Mantled in Starlight"] --> B56["56 Gateway to the Heavens"]
    MAGIC --> B57["57 Shell Game Supreme"]

    B66["66 Prestige Worldwide"] -. Prestige-mode gate .-> B58["58 Android Dreams of Electric Sheep"]
    B66 -. Prestige-mode gate .-> B59["59 Flux Capacitated"]
    B66 -. Prestige-mode gate .-> B60["60 Hello, Open World"] --> B61["61 Robot Did It"]
    B66 -. Prestige-mode gate .-> B62["62 Aperture Unlocked"]
    B66 -. Prestige-mode gate .-> B63["63 Written in Another Age"]
    B66 --> B64["64 Equivalent Ambition Unlocked"] --> B65["65 Tablet of Excess"]
    B66 --> B67["67 Time Is a Flat Parabox"] --> B68["68 Empowered Recursion"]
    B66 -. Prestige-mode gate .-> B69["69 Jetpack Joyride"]
    BUILD --> B70["70 Weird Flex, but Loaded"]
```
