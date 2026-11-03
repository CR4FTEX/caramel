# Caramel design document

This is the living design document for Caramel. It holds the vision, the principles that follow from it, and a log of every idea the owner pitches or takes on. A contributor's idea is only a suggestion until the owner takes it on. Ideas are recorded here before they are implemented, and their entries are updated as the work moves along.

## Vision

The owner (CR4FTEX) set out Caramel's vision in a presentation, which replaced the two pillars this document first quoted. The owner's words are quoted below, each followed by a short summary. When the presentation changes, this section follows it.

> "The Minecraft Mod that sticks everything and everyone together, to build atop Vanilla, and form a polished, cross-compatible, feature-complete experience."

In short: Caramel builds atop vanilla and brings the game together into a polished, feature-complete experience. Compatibility with other mods is a long-term goal (see "Priorities").

### What Caramel is

> "Caramel is a passion project made by devoted individuals to bring Minecraft to a higher level of polish, give proper attention to certain neglected, already-implemented features, expand parity beyond Mojang's usual simple tweaks from Bedrock and much more!"
>
> "We also make sure we don't steer too far from the core identity and gameplay loop of Minecraft."
>
> "It's a Mod first. It's an Expansion second. It's an Infrastructure third."

In short: Caramel brings the game to a higher level of polish, gives neglected features the attention they deserve, and takes parity with Bedrock Edition further than Mojang's own tweaks, without steering too far from the game's core identity and gameplay loop. It is a mod first, an expansion second and an infrastructure third, each defined below.

#### A mod

> "Caramel is a Mod for the Fabric Mod Loader, and aims to be updated to the highest version of the game possible."
>
> "While we don't plan on porting Caramel back to previous versions, we hope we can provide reasons to stay on the latest one."

In short: Caramel follows the newest version of the game it can, and is not ported back to older ones. How it moves to a new version is in `CLAUDE.md`, "Updating Minecraft".

#### An expansion

> "Caramel isn't just one idea: it's a collection of various improvements, additions and even some major gameplay alterations that will allow you and your friends to get the most out of what Minecraft can become."
>
> "It improves and expands on everything, including but not limited to the Blocks, the Mobs, the Biomes, the Dimensions, you name it!"

In short: Caramel is many improvements and additions across the whole game, some of them major changes to how it plays.

#### An infrastructure

> "Caramel doesn't want to force you to accept unwanted features, or give you trouble trying to troubleshoot compatibility issues. It wants to establish itself as a base for all other Mods you wish to integrate, even those that contradict Caramel."
>
> "Don't like something? Disable it!"
>
> "Another important Mod isn't compatible with Caramel? Get in touch with us and we'll sort this out!"

In short: players can switch off what they don't want, and Caramel aims to be a base that other mods work with, even mods that contradict it. "Priorities" says when this gets built.

### Goal 1: Optimize your workflow and craft your dreams

> "Caramel wants to help Players build their dreams. Minecraft is a Sandbox Game, after all."
>
> "Not only do we want to polish and alter the existing Crafting Recipes and redefine the various Work Blocks, we also want to expand your palette of creativity by adding new diverse and accessible Blocks, Items and Tools, and optimize your building efforts without sacrificing precious resources or time."
>
> "Block Sets, Chiseled Blocks, Cut Blocks, Blocks to Slabs to Blocks, anything you can name will be streamlined or added and give their respective Families of Blocks and each of their Members fresh new purposes. This allows for a consistent and more versatile set of Blocks across the board."

In short: polish and change the crafting recipes, redefine the work blocks, add new blocks, items and tools, and let players build without wasting resources or time. Every block family becomes consistent, and each of its members gets a purpose. C-001 to C-009 serve this goal. The slide's examples, such as chiseled and cut blocks, are not pitches until the owner pitches them.

### Goal 2: Connect the world to create your stories

> "Minecraft is about punching your first Wood, adopting your first Pet, discovering ancient Structures and making your own Goal(s)."
>
> "However, the imposing Structures, mysterious Mobs and various lore pieces usually don't benefit the Players' progress, or offer any lasting Goal that would leave a memorable mark on their journey."
>
> "Caramel aims to streamline all of these concepts by connecting them with each other in versatile ways, improve them by adding or modifying content to justify their existence, and allow those to naturally help Players carve a path of their choosing."

In short: connect structures, mobs and lore with each other and with the player's progress, add or change content until each one is worth seeking out, and let players carve their own path. The reward should justify "travelling so far away from home".

### Goal 3: Fill up the blanks and even out the world

> "Caramel wants to fill up those missing gaps that are everywhere in all Dimensions: from the rich but sometimes blank Biomes of the Overworld, the repetitive landscapes of the Nether and especially the barren wastelands of the End. We want to give every Biome of every Dimension the same share of quality and their own substance so that they all feel memorable in their own way, without having one feel left out."

In short: every biome of every dimension, the End's above all, gets the same share of quality and its own substance, so that none feels left out. The owner also asks whether some of the Overworld's woods and the Underground's deepest caves feel a bit barren and repetitive.

### Goal 4: Creating a living world

> "No more bland Biomes, no more special treatment for the Spin-offs; let's bridge the gaps of visual quality and fidelity to form a living world!"
>
> "Many of the options that lost the yearly votes, and many exclusive features from the Spin-offs, get pushed aside without ever getting a chance to shine in the main game."
>
> "From the exclusive Mobs from Earth, the imposing Structures from Dungeons, and even the first ever Mob Vote, Caramel aims to bring those missing elements to fill up those previously mentioned blank areas and create a much more diverse and unique experience for every adventure."

In short: bring the main game's world up to the world-building, audio and visual design of the spin-offs, and to the biomes of each update's concept art, almost all of which were left out of the game. Bring in what else was left out, the spin-offs' exclusives and the options that lost the yearly votes back to the first Mob Vote, to fill the blank areas of Goal 3.

### Goal 5: Innovation and experimentation

> "Caramel wants to bring its own spice, going as far as to expand on already introduced elements and ideas, bringing back scrapped content and re-adapting it, and even adding layers to give Players a much more interesting experience."
>
> "You don't like something? Don't fret! Caramel is also all about choices and being the Infrastructure for all of your needs. We want to give you a personalized feature-complete experience that helps raise Minecraft to a higher standard, or to your own."

In short: Caramel adds ideas of its own. It expands what the game already has, brings back scrapped content in a new form, and adds layers. Players choose which parts they want.

### How Caramel is made

> "The Mod's source code is mostly written and coded with AI, specifically with Claude, with contributors more than welcome to help."
>
> "Every feature is first designed by people, coded with Claude, then playtested."
>
> "We will never generate, nor integrate AI-generated assets in the Mod."
>
> "Every custom asset is either: Made by one of our artists. Commissioned from third-party artists. Bought from third-party artists who allow us to share it under our art license."

In short: people design every feature first, Claude writes most of the code, contributors are welcome to write code too, and every feature is playtested. Caramel never generates or ships AI-generated art, placeholders included. "Our art license" is CC BY-NC-SA 4.0 (`LICENSE-ASSETS`); the art rules are under "Sources in scope".

## Guiding principles

These come only from the owner's words above. Check every idea against them.

- **Keep the core identity and gameplay loop.** Caramel builds atop vanilla without steering too far from what the game is and how it is played.
- **Polish what exists.** Neglected features already in the game get proper attention and a higher level of polish, and parity with Bedrock Edition goes further than Mojang's own tweaks.
- **Help players build their dreams.** Crafting recipes are polished and changed, work blocks are redefined, new blocks, items and tools widen the building palette, every block family is made consistent, and building costs players less of their resources and time.
- **Make everything worth seeking out.** Structures, mobs and lore connect with each other and with the player's progress, and the reward justifies the journey.
- **Every biome gets its share.** Every biome of every dimension gets the same share of quality and its own substance. None is left out, the End least of all.
- **Bring in what was left out.** The spin-offs' exclusives, the options that lost the yearly votes, and the biomes of the concept art.
- **Bring Caramel's own spice.** Caramel expands existing ideas, brings back scrapped content and adds layers of its own.
- **Players choose.** Caramel is "all about choices": "Don't like something? Disable it!" This is a long-term goal (see "Priorities").

## Priorities

Set by the owner while playtesting C-001:

> "the topic of compatibility with the other mods is not really a priority right now, as I would like to focus our efforts into the Vanilla game first before covering cross-compatibility with other mods."

In short: Caramel targets the base game first. Compatibility with other mods comes later.

Decided with the owner when the presentation's vision was recorded here: switching features off and compatibility with other mods (the presentation's "cross-compatible" and its infrastructure slide) are long-term goals, in line with infrastructure coming third. Vanilla first stays the priority, and a system to switch Caramel's features off is built only when the owner pitches it as an idea.

## Target areas

These are the areas the owner has named as targets. The Deep Dark, Crafting and Smithing, Recipes, Blocks and Items, and World Generation come from the owner's first vision statement, whose list ended with "etc."; Work Blocks, Tools, Structures, Mobs, Biomes and the Underground come from the presentation; the dimensions come from both. More areas will be added as ideas come in. Under each heading, list the IDs of the idea log entries that affect it.

### The Deep Dark

No ideas yet.

### Crafting and Smithing

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks
- C-003: Pressure plates and carpets from slabs
- C-004: More fences per craft
- C-005: More fence gates per craft
- C-006: More doors and trapdoors per craft, iron and copper from blocks
- C-007: More buttons per craft
- C-008: More walls per craft
- C-009: The Chisel, the first primitive tool
- C-010: The Stone family, the first consistent block set

### Recipes

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks
- C-003: Pressure plates and carpets from slabs
- C-004: More fences per craft
- C-005: More fence gates per craft
- C-006: More doors and trapdoors per craft, iron and copper from blocks
- C-007: More buttons per craft
- C-008: More walls per craft
- C-009: The Chisel, the first primitive tool
- C-010: The Stone family, the first consistent block set

### Work Blocks

- C-001: Cheaper stairs, and stairs back into blocks
- C-008: More walls per craft
- C-009: The Chisel, the first primitive tool
- C-010: The Stone family, the first consistent block set

### Blocks and Items

- C-009: The Chisel, the first primitive tool
- C-010: The Stone family, the first consistent block set

### Tools

- C-009: The Chisel, the first primitive tool
- C-010: The Stone family, the first consistent block set

### World Generation

No ideas yet.

### Structures

No ideas yet.

### Mobs

No ideas yet.

### Biomes

No ideas yet.

### The Underground

No ideas yet.

### Dimensions

#### Overworld

No ideas yet.

#### Nether

No ideas yet.

#### End

No ideas yet.

## Sources in scope

- **The spin-offs:** Minecraft Dungeons, Minecraft Legends, Minecraft Earth and Minecraft Story Mode. The owner may add others.
- **Bedrock Edition:** parity beyond Mojang's usual simple tweaks ("What Caramel is").
- **The yearly votes:** the options that lost, back to the first Mob Vote (Goal 4).
- **Concept art:** notably the biomes in each update's concept art (Goal 4).
- **Scrapped content:** brought back and re-adapted (Goal 5).
- **Caramel's own ideas:** new content Caramel invents, such as a new block, item, mob or mechanic, expanding on already introduced elements and ideas (Goal 5). A change to how existing content works has no source: its entry says None (owner's decision, recorded with the presentation's vision).

Per the owner, the spin-offs' exclusives and the options that lost the yearly votes fill the blank areas of every dimension (Goals 3 and 4).

All art must be original. Never copy or rip textures, models or sounds from any of these sources, or from any other game or mod. Recreating a concept in new, original art is fine. Caramel never generates or ships AI-generated assets, placeholders included. Every custom asset is made by one of Caramel's artists (the owner and contributors), commissioned from third-party artists, or bought from third-party artists. Bought or commissioned art ships only when its license, or the artist's written agreement, lets it ship under CC BY-NC-SA 4.0 like all other Caramel art. A marketplace's standard license is often not enough: many forbid redistributing purchased files without the seller's permission and do not allow sublicensing, so check before buying. These are the owner's decisions, recorded with the presentation's vision. Caramel is a fan project and is not affiliated with or endorsed by Mojang Studios or Microsoft.

## Idea log

### How it works

1. The owner pitches an idea, or takes on an idea a contributor suggested.
2. Before any code is written, the idea gets an entry below with status **Pitched** and a faithful summary of the pitch. Do not add details the owner did not give.
3. Open design questions are put to the owner. The answers go under "Design notes and decisions".
4. The status is updated as work moves along, and the key classes and files are listed under "Implementation notes".
5. Dropped ideas stay in the log, with the reason they were dropped.
6. The entry's ID is added under each matching heading in "Target areas".

IDs are sequential (C-001, C-002, ...) and never reused. Entries are listed in ID order.

| Status | Meaning |
| --- | --- |
| Pitched | Recorded, not yet agreed on for implementation. |
| Planned | Design questions answered, ready to build. |
| In progress | Being implemented. |
| Done | Implemented, builds, and playtested by the owner. |
| Dropped | Not going ahead. The reason is noted in the entry. |

### Entry format

```markdown
### C-000: <Title>

- **Status:** Pitched | Planned | In progress | Done | Dropped
- **Area:** <one or more areas from "Target areas">
- **Source:** <one or more from "Sources in scope", or None for a change to how existing content works>

**Pitch**

<The owner's pitch, summarized faithfully.>

**Design notes and decisions**

- <Questions asked, answers given, choices made and why.>

**Implementation notes**

- <Key classes and files, datagen providers, mixins (and why a mixin was needed), art the idea still needs.>
```

### Entries

### C-001: Cheaper stairs, and stairs back into blocks

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing; Work Blocks
- **Source:** None

**Pitch**

- Every stairs of every block family (for example Oak, Cobblestone, Nether Bricks, Lime Wool) is crafted from 3 of its base block instead of 6, and still gives 4 stairs. The recipe keeps its L shape, now 3 blocks in a 2x2 grid, and works in all 4 rotations.
- This deletes and replaces the base-game stairs recipes.
- Why: stairs become a cheaper and more accessible recipe.
- A new recipe for every stairs of every family turns 4 stairs, in a 2x2 square, back into 3 of its base block: the same 3 it costs to craft them.
- Why: stairs already crafted can be recycled back into blocks, so crafting too many wastes nothing.

As pictured: 3 oak planks in a 2x2 L (top left, bottom left, bottom right) make 4 oak stairs, and 4 oak stairs in a 2x2 square make 3 oak planks.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 97 stairs have a shaped crafting recipe, all 6 blocks to 4 stairs in the 3x3 staircase pattern, and they are exactly the 97 stairs in the `#minecraft:stairs` tag. 26.3 has wool stairs in all 16 colours, so Lime Wool is one of the families.
- Four base-game stairs recipes accept more than one block: quartz stairs (quartz block, chiseled quartz block, quartz pillar), purpur stairs (purpur block, purpur pillar), sandstone and red sandstone stairs (plain, chiseled, cut). The new recipes keep those alternatives, since the pitch changes only the amount and the shape. Recycling gives the family's base block: quartz block, purpur block, sandstone, red sandstone.
- No clash with existing recipes: nothing in the base game uses 3 of one item in a 2x2 L, or 4 stairs in a 2x2 square.
- No duplication: crafting and recycling trade 3 blocks for 4 stairs both ways. With the stonecutter's stairs removed (below), the best case is a copper block cut into 4 cut copper on the stonecutter, crafted into 16/3 cut copper stairs, which recycle into exactly those 4 cut copper. A balance audit of C-001 to C-008 found no route that multiplies blocks.
- Stonecutter (asked, owner's answer): crafting now gives 4 stairs for 3 blocks, which makes the stonecutter's 1 block to 1 stairs the worse way. The owner chose to remove every stonecutter recipe that makes stairs, 103 in the base game, the copper ones included. Crafting becomes the only way to make stairs.
- Removing a stonecutter recipe also removes its unlock advancement: a removed recipe whose advancement stays makes every world fail to load (`caramel-vault/recipes.md`, "Traps").
- Recipe ids. The first L (empty cell on top, and its mirror) keeps the base-game id, `minecraft:<stairs>`, so it replaces the old recipe. The second L (empty cell at the bottom, and its mirror) is `caramel:<stairs>_flipped`. Recycling is `caramel:<base>_from_<stairs>`. Shaped recipes already match their mirror image, so these two recipes cover all 4 rotations.
- Recipe book: the 48 stairs with no recipe group in the base game get their own id as the group, so both L recipes show as one button. This follows the base game's waxed copper stairs, which already do this.
- Left alone: the 16 recipes that dye wool stairs and the 4 that wax copper stairs with honeycomb. They turn stairs into other stairs rather than crafting them from blocks.
- Automated test (asked, owner's answer): a Fabric game test checks all 97 stairs in a headless test server, and runs in `./gradlew build` and CI. Fabric's game-test mode skips the EULA check by design; `eula.txt` stays `eula=false`.
- Furnace fuel (found in review, asked, owner's answer). Wooden and wool stairs burn as long as a plank or a wool block, so at 4 stairs per 3 blocks crafting stairs would add a third more fuel, and wooden stairs would become the best wood fuel (1600 ticks per log, as much as charcoal). The owner chose to match the cost, as the base game does for slabs: wooden stairs burn 3/4 of a plank (225 ticks instead of 300) and wool stairs 3/4 of wool (75 instead of 100), so crafting stairs never adds fuel.
- Fuel scope (asked, owner's answer): only the base game's stairs get the new burn times, because C-001 changes only their recipes. Another mod's wooden or wool stairs keeps its recipe and its fuel. The owner answered no to changing them, and set compatibility with other mods aside for now (see "Priorities").
- Playtest (owner): all 12 in-game checks passed with no issues: the four L rotations in the 2x2 and 3x3 grids, the old staircase gone, several families, quartz alternatives, recycling, mixed stairs refused, the stonecutter offering no stairs, one recipe book button per stairs, and fuel in the furnace, blast furnace and smoker.

**Implementation notes**

- No mixin was needed.
- Recipes: `CaramelRecipeProvider` (datagen, `src/client/java/io/github/cr4ftex/caramel/client/datagen/`). It writes 97 recipes at `minecraft:<stairs>`, 97 at `caramel:<stairs>_flipped`, 97 at `caramel:<base>_from_<stairs>`, and removes the 103 stonecutter recipes and their 103 unlock advancements with Fabric's always-false load condition. Datagen stops if Minecraft's stairs count stops being 97 or the stonecutter count 103.
- Fuel: `CaramelBurnTimeProvider` (datagen) writes `caramel:cooking/time_wood_stairs` (225) and `caramel:cooking/time_wool_stairs` (75), built like vanilla's burn times, so a blast furnace or smoker halves them (112 and 37). `io.github.cr4ftex.caramel.fuel.CraftedFuel` (named `StairsFuel` until C-003 widened it), called from `Caramel.onInitialize`, points the 28 stairs that burned in the base game at them (12 wooden, bamboo and bamboo mosaic included, and 16 wool) through Fabric's `DefaultItemComponentEvents.MODIFY`.
- Game test: `src/gametest/java/io/github/cr4ftex/caramel/gametest/StairsRecipeGameTest.java`, a separate mod `caramel-gametest`, wired by `configureTests` in `build.gradle`. `./gradlew build` and CI run its 9 checks. They read the expected values from the vanilla data pack and check every L rotation and placement, the old staircase, recycling, mixed stairs, the stonecutter, recipe groups and categories, and stairs fuel. Which vanilla ids Caramel's pack touches is checked by `VanillaOverridesGameTest`, and which items have new fuel by `CraftedFuelGameTest`, both shared with the later ideas. `OtherModStairs` registers a test-only stairs in the game-test mod to prove other mods' stairs keep their fuel; it is not on `runClient`'s classpath and not in the mod jar.
- Generated files: 790 in `src/main/generated/data`. They must be committed, because CI does not run datagen.
- No placeholder textures.

### C-002: Cheaper slabs, and slabs back into blocks

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every slab of every block family (for example Spruce, Andesite, Bricks, Orange Wool) is crafted from 2 of its base block instead of 3. The recipe keeps its straight-line shape.
- This deletes and replaces the base-game slab, pressure plate and chiseled block recipes.
- The pressure plate recipes that had this shape will get a new recipe in the owner's next idea, after the slabs.
- Why: slabs become a cheaper and more accessible recipe, with no crafting table needed.
- A new recipe for every slab of every family turns 2 of the same slab, stacked on top of each other in the crafting grid, back into 1 of its base block.
- Why: slabs already crafted can be recycled back into blocks, so crafting too many wastes nothing.
- Some slabs, such as the stone brick slab, have a base-game recipe where 2 stacked slabs make the chiseled version of their block. The owner asked to remove these chiseled recipes too.

As pictured: 2 oak planks side by side in the bottom row of the 2x2 grid make 4 oak slabs, and 2 oak slabs stacked in the left column make 1 oak planks.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 100 slabs have a shaped crafting recipe, all 3 blocks in a row to 6 slabs. The `#minecraft:slabs` tag has 101; the other one, the petrified oak slab, has no recipe. 26.3 has wool slabs in all 16 colours, so Orange Wool is one of the families.
- The pitch's first highlight named the stairs recipes. The owner confirmed it meant the slab recipes, a copy-paste slip.
- The rate stays the same: 2 blocks for 4 slabs is 2 slabs per block, as in the base game (3 for 6), and recycling gives 1 block for 2 slabs. So nothing can be duplicated, and fuel does not change: slabs burn the same per block as before. The new recipe fits the inventory's 2x2 grid.
- The stonecutter also makes 2 slabs per block (8 cut copper slabs per copper block, which is 4 cut copper), as good as crafting. The pitch does not change it, so its 108 slab recipes stay.
- Four base-game slab recipes accept more than one block: quartz slab (quartz block, chiseled quartz block, quartz pillar), purpur slab (purpur block, purpur pillar), sandstone and red sandstone slabs (plain and chiseled). As in C-001, the new recipes keep those alternatives, and recycling gives the family's base block.
- Recipes the new shapes take over, found in the game data:
  - 2 base blocks in a row: 15 pressure plates (the 13 wooden ones, stone, polished blackstone) and the 16 carpets (2 wool make 3 carpets). The two weighted pressure plates use gold and iron ingots, which have no slabs.
  - 2 stacked slabs: 20 chiseled blocks (stone bricks, sandstone, red sandstone, quartz, nether bricks, polished blackstone, deepslate from cobbled deepslate slabs, tuff, tuff bricks, resin bricks, cinnabar, sulfur, and the 8 chiseled coppers, waxed ones included). The stonecutter still makes every one of them. Also the purpur pillar, which the stonecutter also makes, and bamboo mosaic, which has no other recipe.
- Pressure plates (asked, owner's answer): all 17 pressure plate recipes are removed, the two weighted plates (2 gold or 2 iron ingots) included, although ingots have no slabs. No pressure plate can be crafted until the owner's next idea gives them a new recipe. C-003 did: 2 slabs, or 2 ingots for the weighted plates, make 16.
- Carpets (asked, owner's answer): the 16 wool carpet recipes are removed for now, and wool slabs get the new recipe as pitched. No carpet can be crafted from wool until a later idea gives them a new recipe. Moss and pale moss carpets keep theirs: those blocks have no slabs. C-003 gave wool carpets a new recipe, 2 wool slabs to 16, and moss carpets 32 from their 2 blocks. The recipes that dye a carpet another colour stay. Shepherds still sell carpets, but each one offers only 2 trades at level 2, drawn from 37 (16 of them carpets), so one shepherd sells at most 2 colours.
- Bamboo mosaic (asked, owner's answer): its recipe is removed, so bamboo slabs recycle like every other slab. Bamboo mosaic, and with it its slab and stairs, cannot be crafted until a later idea gives it a recipe.
- Purpur pillar (asked, owner's answer): removed like the chiseled recipes, so purpur slabs recycle into purpur blocks. The stonecutter still makes the pillar.
- What can no longer be made (found in review, from the 26.3 recipes, loot tables, villager trades and structure files):
  - None of the 17 pressure plates has a recipe. Only a few can still be found: the oak, spruce and acacia plates in villages (oak also in trial chambers), the stone plate in ancient cities and desert pyramids, and the light weighted plate in ruined portal chests. The other 12 cannot be obtained in survival.
  - Detector rails need a stone pressure plate, so each batch of 6 needs a stone plate found in the world.
  - Bamboo mosaic, its slab and its stairs cannot be obtained in survival at all: no structure, chest or trade has them. Their recipes stay, but each needs bamboo mosaic to start.
  - C-003 gives the pressure plates and wool carpets new recipes, which makes detector rails craftable again. Of this list, only bamboo mosaic, its slab and its stairs remained, for a later idea that would also cover the chiseled blocks and the pillars.
  - C-009 is that idea: a Chisel's right click turns bamboo planks into bamboo mosaic, so its slab and stairs can be crafted again.
  - Every chiseled block and the purpur pillar can still be cut on the stonecutter, and the waxed chiseled coppers still come from honeycomb.
- Playtest (owner): all 12 in-game checks passed as intended: slabs from 2 blocks in the 2x2 and 3x3 grids, the old row of 3 gone, several families, alternative blocks, recycling in both columns and in the crafting table, stacked slabs giving the base block instead of the chiseled block, mixed slabs refused, pressure plates and carpets no longer crafted, the stonecutter unchanged, the recipe book, and the detector rail.

**Implementation notes**

- No mixin was needed. No fuel change, and no placeholder textures.
- Recipes: `CaramelRecipeProvider.replaceSlabs()` (datagen). It writes 100 recipes at `minecraft:<slab>`, for the 99 block families with slabs and the smooth stone slab, which vanilla crafts outside any family, and 100 at `caramel:<base>_from_<slab>`. It removes 22 recipes and their unlock advancements with Fabric's always-false load condition: the families' 16 chiseled blocks, and by name the four waxed chiseled coppers, the purpur pillar and bamboo mosaic. Datagen stops if the number of families with slabs (99) or chiseled blocks (16) changes. C-002 also removed the 17 pressure plates and 16 wool carpets until C-003 gave them new recipes at the same ids.
- The removed files carry vanilla's recipe bodies. Four removed unlock advancements (chiseled stone bricks, chiseled quartz block, chiseled sandstone, chiseled red sandstone) carry simpler criteria than vanilla's; Fabric drops them before reading.
- Game tests, in `src/gametest/java/io/github/cr4ftex/caramel/gametest/`:
  - `SlabsRecipeGameTest` has 9 checks, with expected values read from the vanilla data pack: every slab found, the new recipe in every placement of the 2x2 and 3x3 grids, the vanilla group and category, the old row of 3 gone, recycling in every placement, mixed pairs, the removed recipes gone, the chiseled blocks still cut, and fuel.
  - `VanillaOverridesGameTest` checks which vanilla ids Caramel's pack replaces or removes, for C-001, C-002 and C-003 together, working the list out from the vanilla pack.
  - `UnlockAdvancementsGameTest` checks that every recipe in the `caramel` namespace has exactly one unlock advancement.
  - `RecipeTestSupport` holds the helpers these tests and C-001's share.
  - While C-002 was built, five deliberate breaks to the generated files each failed the build, then were undone: a carpet removal deleted, a recycling recipe deleted, the light weighted plate's removal deleted, a slab count changed, the chiseled stone bricks removal deleted.
- Generated files: 510 new with C-002, 1300 in `src/main/generated/data` in all at that point. They must be committed, because CI does not run datagen.

### C-003: Pressure plates and carpets from slabs

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every pressure plate and carpet of every block family that has them (for example Birch, Stone, White Wool, Yellow Wool) is crafted from 2 of the family's slab instead of 2 of its block. The recipe keeps its straight-line shape and gives 16 pressure plates or 16 carpets, instead of 1 plate or 3 carpets.
- The light and heavy weighted pressure plates keep their base-game recipe, 2 gold or 2 iron ingots side by side, and now give 16 instead of 1.
- Why: the pressure plate becomes a more accessible and profitable recipe, with no crafting table needed.
- The owner added: 2 wool slabs of the same colour in a row make 16 carpets of that colour. Bamboo mosaic, the chiseled blocks and the pillars will be part of a future idea, and their removal in C-002 stands.
- The owner added later: the moss carpet and pale moss carpet recipes (2 moss or pale moss blocks side by side) give 32 instead of 3.
- After the playtest, answering the knock-ons: shepherds sell 20 carpets for an emerald instead of 4, so buying carpets is worth it again. Dyeing wool and items will be addressed in a future idea.

As pictured: 2 oak slabs side by side in the bottom row of the 2x2 grid make 16 oak pressure plates, and 2 white wool slabs make 16 white carpets.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 15 block families have a pressure plate and a slab (the 13 wooden ones, stone and polished blackstone), and the 16 wool families have a carpet and a slab. With the 2 weighted plates, that is the 33 recipes C-002 removed. C-003 gives each a new recipe at the same base-game id.
- Moss and pale moss carpets: their blocks have no slabs, so their recipe keeps its 2 blocks side by side and gives 32, the same 16 carpets per block as the others. They are not fuel. They do compost: each has a 30% chance to add a composter layer (`minecraft:compostable/low`), and a moss or pale moss block 65% (`compostable/medium`), checked in the game.
- No clash: no other recipe in the game data takes 2 of one slab side by side. The cushion takes 3 wool slabs in a row, and C-002's recycling takes 2 slabs stacked, which a shaped recipe never confuses with side by side.
- Nothing can be duplicated: no recipe, furnace or trade turns pressure plates or carpets back into anything, and villagers only sell carpets (shepherds). The detector rail, which takes 1 stone pressure plate, can be crafted from new materials again.
- Burn times, measured in the game: wooden pressure plates burn 300 ticks (crimson and warped do not burn), wool carpets 67.
- Compost (asked, owner's answer): at 32 carpets from 2 blocks, one moss block turned into carpets would compost for about 4.8 layers instead of 0.65. The owner chose to match the cost: each moss and pale moss carpet adds a layer with a 4% chance, 1/16 of the block's 65% rounded down. The first item into an empty composter still always adds one layer, as in vanilla.
- Recipe book: each replaced recipe keeps its base-game unlock advancement, so it shows once the player has held the base-game ingredient (planks, stone, polished blackstone, wool, the ingot, the moss block), not the slab.
- Fuel and compost apply to the item, not only to crafted ones: a wooden pressure plate found in a village burns 18 ticks too, and a moss carpet grown with bone meal composts at 4%.
- Knock-ons, found in review: a shepherd's trade of 4 carpets for an emerald became worse than buying 1 wool (16 carpets), which the owner answered by raising it to 20; dyeing a carpet stays 1 for 1, so dyeing the wool first is 16 times better, left for the owner's future dyeing idea; carpets, which dampen vibrations for sculk sensors, are 16 per wool; light weighted pressure plates, which distract piglins, are 16 per 2 gold ingots.
- Shepherd trades (owner's addition): each of the 16 carpet trades at shepherd level 2 gives 20 carpets instead of 4, and keeps the rest of vanilla's trade: 1 emerald, 16 uses, 5 experience. A shepherd draws 2 of its 37 level-2 trades, as before. A villager keeps the offers it already has, so only trades drawn after the change give 20.
- Playtest (owner): the owner confirmed in game that a shepherd sells 20 carpets. Before the trades were added, all 12 in-game checks passed as intended: plates and carpets from 2 slabs, other plate families, the weighted plates, wool carpets in two colours, moss carpets, mixed slabs refused, 2 blocks still making slabs, the recipe book, the detector rail, fuel, compost, and what does not burn.
- Furnace fuel (asked, owner's answer): at 16 per block, a plank would make 16 plates worth 4,800 ticks and a wool 16 carpets worth 1,072. The owner chose to match the cost, as C-001 did for stairs: a wooden pressure plate burns 18 ticks and a wool carpet 6, 1/16 of a plank or a wool rounded down, so crafting never adds fuel. Blast furnaces and smokers halve both (9 and 3).

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.pressurePlatesAndCarpets()` (datagen) writes 35 recipes at their base-game ids: the 15 slab families' pressure plates and the 16 wool carpets from 2 slabs, the two weighted plates from 2 ingots (16 each), and the moss and pale moss carpets from 2 blocks (32 each). Each keeps the base-game group, category and unlock advancement, byte for byte for the advancements. Datagen stops if the number of slab families with a pressure plate (15) or a carpet (16) changes.
- Fuel: C-001's `StairsFuel` became `io.github.cr4ftex.caramel.fuel.CraftedFuel`, which matches an item by its block's class and vanilla's burn time. `CaramelBurnTimeProvider` writes `caramel:cooking/time_wood_pressure_plates` (18) and `caramel:cooking/time_wool_carpets` (6); the 11 wooden plates that burned and the 16 wool carpets point at them.
- Compost: `io.github.cr4ftex.caramel.compost.CraftedCompost` points the two moss carpets at `caramel:compostable/moss_carpets`, a 4% chance written by the new `CaramelCompostProvider`, built like vanilla's chances.
- Trades: `CaramelTradeProvider` (datagen) writes the 16 shepherd carpet trades at their vanilla ids, built with vanilla's own call and a count of 20.
- Game tests: `PressurePlatesAndCarpetsGameTest` has 12 checks, with expected values read from the vanilla data pack and vanilla item components: all 35 recipes found, 2 slabs or ingots in every placement of the 2x2 and 3x3 grids, the moss carpets, groups and categories, unlock advancements, mixed pairs, the detector rail, fuel, fuel still burning where it burned, compost, and the carpet trades. `CraftedFuelGameTest` checks that only the vanilla stairs, pressure plates and carpets that burned have new fuel. `SlabsRecipeGameTest` and `VanillaOverridesGameTest` were updated for the recipes C-003 took over from C-002's removals. `VanillaOverridesGameTest` also fails if Caramel's pack replaces any villager trade but these 16. `./gradlew build` runs 33 Caramel game tests.
- Ten deliberate breaks each failed the build, then were undone: a plate's count, a carpet's ingredient, the light weighted plate removed again, the plate burn time, the carpet fuel mapping, the moss carpet count, the moss carpet compost chance, the pale moss carpet left out of the compost change, a carpet trade back at 4, and a carpet trade file deleted.
- Generated files: 1323 in `src/main/generated/data`, 23 new and 66 changed with C-003.

### C-004: More fences per craft

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every crafted fence, of every block family that has one (for example Oak, Acacia, Pale Oak, Nether Bricks), gives 12 fences instead of 3.
- As pictured: the recipe keeps its shape, 2 rows of plank, stick, plank, and makes 12 oak fences.
- The owner noted that this idea, and the next few, are smaller ideas that only change how much a recipe gives.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 14 recipes make a fence, the 13 wooden ones (oak, spruce, birch, jungle, acacia, dark oak, mangrove, cherry, pale oak, poplar, bamboo, crimson, warped), 3 each, and the nether brick fence (4 nether bricks and 2 nether brick items), which gives 6 in the base game, not 3. No other recipe, furnace, stonecutter or villager trade makes, uses or sells fences. Fence gates are not part of the pitch and kept their recipes; C-005 makes each fence gate recipe give 4.
- No clash: plank, stick, plank in 2 rows is still the only recipe with that shape. The fence gate puts the sticks on the outside.
- Burn time, read from the game code: the 11 wooden fences that burn (crimson and warped do not) burn 300 ticks, as long as a plank (`minecraft:cooking/time_wood_blocks`). The nether brick fence does not burn.
- Nether brick fence (asked, owner's answer): it gives 24, four times the base game's 6, the same increase the wooden fences get.
- Furnace fuel (asked, owner's answer): at 12 per craft, one craft would burn 3,600 ticks from 5 planks' worth of wood (4 planks and 2 sticks; 2 planks make 4 sticks, so 2 sticks cost a plank), 1,500 ticks. The owner chose to match the cost, as for stairs and pressure plates: a wooden fence burns 125 ticks, 5/12 of a plank, so crafting never adds fuel when sticks are counted at their cost in planks. Sticks from bamboo, leaves or elsewhere gain a little, as C-005 records: 12 fences burn 1,500 ticks against 1,400 for 4 planks and 2 sticks burned as they are. Blast furnaces and smokers halve it (62). The question put the cost at 4½ planks and 112 ticks, a miscount of the sticks, which the fuel game test caught.
- Playtest (owner): all 8 in-game checks worked as intended: 12 oak fences in both rows of the grid, other woods including bamboo and crimson, 24 nether brick fences, mixed planks refused, the fence gate unchanged, the recipe book, 1 fence no longer smelting an item in a furnace or blast furnace where 2 do, and the fences that do not burn.

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.fences()` (datagen) writes the 14 fence recipes at their base-game ids, each built as the base game builds it with 4 times the count: 12 for the wooden fences, 24 for the nether brick fence. Each keeps the base-game group, category and unlock advancement, byte for byte. Bamboo's fence is its family's custom fence variant, so the walk covers both variants; datagen stops if the number of fences found (14) changes.
- Fuel: `CraftedFuel` points the 11 wooden fences that burned at `caramel:cooking/time_wood_fences` (125 ticks), written by `CaramelBurnTimeProvider`.
- Game tests: `FencesRecipeGameTest` has 6 checks, with expected values read from the vanilla data pack and vanilla item components: all 14 recipes found; the base-game grid at both places it fits in the 3x3 grid gives 4 times the base game's count, with no other recipe matching; each loaded recipe equals the base game's file with only the count changed; unlock advancements; fuel matching the cost (sticks costed from the base game's stick recipe); and fuel still burning where it burned. `CraftedFuelGameTest` and `VanillaOverridesGameTest` cover fences too. `./gradlew build` runs 39 Caramel game tests.
- Six deliberate breaks each failed the build, then were undone: the nether brick fence at 12, the oak fence's group dropped, the oak fence unlocking on sticks, the fence burn time at 126, the fence fuel mapping broken, and the bamboo fence's files deleted.
- Generated files: 1352 in `src/main/generated/data`, 29 new with C-004 and none changed.

### C-005: More fence gates per craft

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every crafted fence gate, of every block family that has one (for example Spruce, Dark Oak, Poplar, Jungle), gives 4 fence gates instead of 1.
- As pictured: the recipe keeps its shape, 2 rows of stick, plank, stick, and makes 4 oak fence gates.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 13 recipes make a fence gate, all wooden (oak, spruce, birch, jungle, acacia, dark oak, mangrove, cherry, pale oak, poplar, bamboo, crimson, warped), 1 each. Nether bricks have no fence gate. No other recipe, furnace, stonecutter or villager trade makes, uses or sells fence gates. Bamboo's fence gate is its family's custom fence gate variant, as its fence is (C-004).
- No clash: stick, plank, stick in 2 rows is still the only recipe with that shape. The fence puts the sticks in the middle.
- Burn time, read from the game code: the 11 wooden fence gates that burn (crimson and warped do not) burn 300 ticks, as long as a plank (`minecraft:cooking/time_wood_blocks`). 4 fence gates take 2 planks and 4 sticks, and 2 planks make 4 sticks, so they cost 4 planks: each costs one plank and already burns exactly as long as one, in every furnace. Crafting fence gates adds no fuel, so their burn time stays the base game's, as the owner's "match their cost" choices for stairs, pressure plates and fences would set it. No question was needed.
- Sticks are priced in planks, as for C-004's fences: a stick in a fence gate is worth 150 ticks, half a plank, though it burns 100 on its own. Where sticks come from something other than planks, a fence gate craft gains a little fuel over burning its ingredients, found in review: bamboo burns 50 ticks a piece, 66.7 per bamboo as bamboo planks, 68.2 as C-004's bamboo fences and 70.6 as C-005's bamboo fence gates (2 bamboo make a stick). The base game's ladders already value a stick at 128.6 ticks. It follows from the pricing the owner accepted for fences, so nothing was changed.
- Playtest (owner): all 7 in-game checks worked as intended: 4 oak fence gates in both rows of the grid, other woods including bamboo and crimson, mixed planks refused, fences unchanged at 12, the recipe book, a fence gate still smelting an item in a furnace and blast furnace, and the gates that do not burn.

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.fenceGates()` (datagen) writes the 13 fence gate recipes at their base-game ids, each built as the base game builds it with a count of 4, and each keeps the base-game group, category and unlock advancement, byte for byte. `fences()` and `fenceGates()` share `eachFamilyWith`, which walks a variant and its custom twin (bamboo's) and stops datagen if the number of families found (13 fence gates) changes.
- Fuel: unchanged. Fence gates keep the base game's burn time.
- Game tests: C-004's fence checks moved into `MorePerCraft`, shared by `FencesRecipeGameTest` and the new `FenceGatesRecipeGameTest`, which runs the same 6 checks for fence gates: all 13 recipes found, the base-game grid at both places it fits giving 4 with no other recipe matching, each loaded recipe equal to the base game's file but for the count, unlock advancements, fuel matching the cost (exactly a plank), and the fuel unchanged where it burned. `VanillaOverridesGameTest` covers the fence gate recipes, and `CraftedFuelGameTest` fails if a fence gate's fuel changes. `./gradlew build` runs 45 Caramel game tests.
- Six deliberate breaks each failed the build or datagen, then were undone: a gate back to 1, the bamboo fence gate's files deleted, a gate's group dropped, a gate unlocking on sticks, the gates given the fence burn time, and the walk missing the custom fence gate ("Expected 13 block families with fence gates, found 12").
- Review: four reviewers (datagen, tests, knock-ons, docs) and a skeptic per finding; the 7 findings that held were documentation, fixed here and in `caramel-vault/`.
- Generated files: 1378 in `src/main/generated/data`, 26 new with C-005 and none changed.

### C-006: More doors and trapdoors per craft, iron and copper from blocks

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every crafted door and trapdoor, of every block family that has them (for example Iron, Copper, Bamboo, Acacia), gives 16 instead of 3 or 1.
- The iron door, copper door, iron trapdoor and copper trapdoor use the same grid shape as their wooden counterparts: 6 in 2 columns of 3 for a door, 6 in 2 rows of 3 for a trapdoor.
- Those four take Blocks of Iron and Blocks of Copper instead of iron and copper ingots.
- Why: the iron and copper doors and trapdoors cost more to craft, and players get more value out of all of them.

As pictured: 6 oak planks in 2 columns make 16 oak doors, 6 oak planks in 2 rows make 16 oak trapdoors, 6 Blocks of Iron in 2 columns make 16 iron doors, and 6 Blocks of Copper in 2 rows make 16 copper trapdoors.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 30 recipes. 13 wooden doors (oak, spruce, birch, jungle, acacia, dark oak, mangrove, cherry, pale oak, poplar, bamboo, crimson, warped) give 3 each and 13 wooden trapdoors give 2 each, from 6 planks. The iron door and copper door give 3 from 6 ingots; the iron trapdoor and copper trapdoor give 1 from 4 ingots in a 2x2. The pitch's "instead of 3 or 1" leaves out the wooden trapdoors' 2; they give 16 like the rest.
- Only the plain copper door and trapdoor are crafted. The exposed, weathered and oxidized ones come from the copper aging in the world, and the 8 waxed ones from a honeycomb recipe, 1 door or trapdoor and 1 honeycomb for 1 waxed one. Those waxing recipes stay 1 for 1: at 16, one door and one honeycomb would make 16 doors.
- No other recipe, furnace, stonecutter, villager trade or chest loot makes, uses or sells doors or trapdoors.
- No clash: no other recipe takes 6 Blocks of Iron or Copper in 2 columns or 2 rows. The old ingot grids give nothing afterwards; 6 iron or copper ingots in 2 rows of 3 are still iron or copper bars, as in the base game.
- Cost: 6 Blocks of Iron are 54 ingots, about 3.4 per door or trapdoor. An iron or copper door cost 2 ingots before and costs more; an iron or copper trapdoor cost 4 and costs a little less.
- Burn times, read from the game code: the 11 wooden doors that burn (crimson and warped do not) burn 200 ticks (`minecraft:cooking/time_wood_items_large`), and the 11 wooden trapdoors that burn 300, as long as a plank (`minecraft:cooking/time_wood_blocks`). Iron and copper doors and trapdoors do not burn.
- Furnace fuel (asked, owner's answer): at 16 from 6 planks, 1,800 ticks of wood, 16 doors would burn 3,200 ticks and 16 trapdoors 4,800. The owner chose to match the cost, as for stairs, pressure plates and fences: a wooden door and a wooden trapdoor each burn 112 ticks, 6/16 of a plank rounded down, so crafting never adds fuel. Blast furnaces and smokers halve it (56).
- Copper (asked, owner's answer): only the Block of Copper makes copper doors and trapdoors, as pitched. The exposed, weathered, oxidized and waxed blocks make none; those doors and trapdoors still come from aging and honeycomb.
- Recipe book: each replaced recipe keeps its base-game unlock advancement, as C-003's did, so the iron and copper doors and trapdoors show once the player has held an iron or copper ingot, though they now take blocks.
- Playtest (owner): all 10 in-game checks worked as intended: oak doors and trapdoors at every place in the grid, other woods, iron and copper doors and trapdoors from 6 blocks, aged and waxed copper blocks refused, the old ingot recipes gone with iron bars still crafting, waxing 1 for 1, the recipe book, fuel in a furnace and blast furnace, and what does not burn.

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.doorsAndTrapdoors()` (datagen) writes the 30 recipes at their base-game ids. The 26 wooden ones walk the families' door and trapdoor variants (13 each; datagen stops if either count changes) and give 16 with the base-game shape and group. `fromBlocks()` writes the iron and copper door and trapdoor in the wooden shapes from 6 Blocks of Iron or the plain Block of Copper (`Blocks.COPPER_BLOCK.weathering().unaffected()`), 16 each. Every unlock advancement is the base game's, byte for byte. The waxing recipes are untouched.
- Fuel: `CraftedFuel` points the 11 wooden doors that burned (vanilla's `cooking/time_wood_items_large`) at `caramel:cooking/time_wood_doors` and the 11 wooden trapdoors (vanilla's `time_wood_blocks`) at `caramel:cooking/time_wood_trapdoors`, both 112 ticks, written by `CaramelBurnTimeProvider`.
- Game tests: `DoorsAndTrapdoorsGameTest` has 10 checks. Six run `MorePerCraft` over the 26 wooden recipes (`MorePerCraft.Scope` now takes several tags and a count rule, 16 here, 4 times vanilla's for fences and fence gates). Four are its own, with expected values built from the base game's data: the iron and copper recipes equal the base game's with the wooden counterpart's pattern, the block that 9 of the ingot craft into, and 16; 6 blocks give 16 at every place they fit; the old ingot grids give nothing; the 8 waxing recipes stay the base game's. `CraftedFuelGameTest` (its check renamed `onlyVanillaItemsCraftedCheaperThatBurnedHaveNewFuel`) and `VanillaOverridesGameTest` cover C-006. `./gradlew build` runs 55 Caramel game tests.
- Eight deliberate breaks each failed the build, then were undone: an oak door back to 3, the iron trapdoor back to 4 ingots in a 2x2, the copper door from ingots, the copper trapdoor from exposed copper, a waxing recipe overridden to give 16, the door burn time at 200, the trapdoor fuel mapping removed, and the iron door unlocking on iron blocks.
- Review: four reviewers (datagen and fuel, tests, knock-ons, docs) and a skeptic per finding; the 8 findings that held were documentation, fixed here, in `caramel-vault/` and in one test comment.
- Generated files: 1440 in `src/main/generated/data`, 62 new with C-006 and none changed.

### C-007: More buttons per craft

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Source:** None

**Pitch**

- Every crafted button, of every block family that has one (for example Stone, Dark Oak, Poplar, Jungle), gives 20 buttons instead of 1.
- As pictured: the recipe keeps its shape, 1 oak plank in the grid, and makes 20 oak buttons.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 15 recipes make a button, each a shapeless recipe of 1 block for 1 button. 13 are wooden (oak, spruce, birch, jungle, acacia, dark oak, mangrove, cherry, pale oak, poplar, bamboo, crimson, warped), from 1 plank, in the `wooden_button` recipe book group. The stone button takes 1 stone and the polished blackstone button 1 polished blackstone, with no group. They are exactly the 15 buttons in `#minecraft:buttons`, and each is its block family's button.
- No clash: 1 plank, 1 stone or 1 polished blackstone matches no other recipe, in the base game or in Caramel's pack.
- No other recipe, furnace, stonecutter, villager trade or chest loot makes, uses or sells buttons.
- Burn time, read from the game code: the 11 wooden buttons that burn (crimson and warped do not) burn 100 ticks, a third of a plank (`minecraft:cooking/time_wood_items_extra_small`, which the bowl also uses). Stone and polished blackstone buttons do not burn.
- Furnace fuel (asked, owner's answer): at 20 per plank, one plank's 300 ticks would become 2,000, and the 4 planks from a log, 1,200 ticks, would become 8,000 as 80 buttons, five times a piece of coal. The owner chose to match the cost, as for stairs, pressure plates, fences, doors and trapdoors: a wooden button burns 15 ticks, 1/20 of a plank, so crafting never adds fuel. Blast furnaces and smokers halve it (7). A furnace now needs 14 wooden buttons to smelt one item, where the base game needed 2, and a blast furnace 15 (worked out from the furnace code).
- Playtest (owner): all 8 in-game checks worked as intended: 20 oak buttons at every place in both grids, other woods including poplar, bamboo and crimson, 20 stone and 20 polished blackstone buttons with cobblestone and smooth stone refused, shift-click crafting, the recipe book, 13 buttons short of an ingot and 14 enough in a furnace, 14 short and 15 enough in a blast furnace, and the buttons that do not burn, with the bowl burning as before.

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.buttons()` (datagen) writes the 15 button recipes at their base-game ids, each built as the base game's `buttonBuilder` builds it, 1 block in a shapeless recipe, with a count of 20. Each keeps the base-game group, category and unlock advancement, byte for byte. The walk covers the families' button variant; datagen stops if the number found (15) changes.
- Fuel: `CraftedFuel` points the 11 wooden buttons that burned (the base game's `cooking/time_wood_items_extra_small`) at `caramel:cooking/time_wood_buttons` (15 ticks), written by `CaramelBurnTimeProvider`. It matches the button block as well as the key, so the bowl, which shares the key, keeps its 100 ticks.
- Game tests: `ButtonsRecipeGameTest` runs `MorePerCraft`'s 6 checks over the 15 recipes, with expected values read from the base game's data pack and item components: all 15 found; the block at each of the 13 places in the 2x2 and 3x3 grids gives 20, with no other recipe matching (195 grids); each loaded recipe equals the base game's file but for the count; unlock advancements; fuel matching the cost; and fuel still burning where it burned. `MorePerCraft` took only shaped recipes and now takes shapeless ones too, their ingredients laid in one row. `CraftedFuelGameTest` and `VanillaOverridesGameTest` cover the buttons. `./gradlew build` runs 61 Caramel game tests.
- Nine deliberate breaks each failed the build or datagen, then were undone: the oak button back to 1, the stone button in the wooden group, the birch button unlocking on sticks, the button burn time at 16, the polished blackstone button's files deleted, the spruce button made a shaped recipe, a second recipe matching 1 jungle plank, the button fuel mapping removed, and the button family count at 14 ("Expected 14 block families with buttons, found 15").
- Review: four reviewers (datagen and fuel, tests, knock-ons, docs) and a skeptic per finding; the 2 findings that held were documentation, fixed here and in `caramel-vault/fuel.md`: the bowl also burns on the buttons' fuel key, and a log's burn time was misstated.
- Generated files: 1471 in `src/main/generated/data`, 31 new with C-007 and none changed.

### C-008: More walls per craft

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing; Work Blocks
- **Source:** None

**Pitch**

- Every crafted wall, of every block family that has one (for example Sulfur, Andesite, Tuff, Diorite), gives 12 walls instead of 6.
- As pictured: the recipe keeps its shape, 2 rows of 3 cobblestone, and makes 12 cobblestone walls.

**Design notes and decisions**

- Scope, checked in the Minecraft 26.3 game data: 32 crafting recipes make a wall, each 6 of its block in 2 rows of 3 for 6 walls, with no recipe book group: andesite, blackstone, brick, cinnabar, cinnabar brick, cobbled deepslate, cobblestone, deepslate brick, deepslate tile, diorite, end stone brick, granite, mossy cobblestone, mossy stone brick, mud brick, nether brick, polished blackstone, polished blackstone brick, polished cinnabar, polished deepslate, polished sulfur, polished tuff, prismarine, red nether brick, red sandstone, resin brick, sandstone, stone brick, sulfur, sulfur brick, tuff and tuff brick. They are exactly the 32 walls in `#minecraft:walls`.
- No clash: 6 of a wall's block in 2 rows of 3 matches no other recipe, in the base game or in Caramel's pack.
- Besides the stonecutter's (below), no other recipe, furnace, villager trade or chest loot makes, uses or sells walls. Walls do not burn.
- Stonecutter (asked, owner's answer): 57 stonecutter recipes make walls, 1 block for 1 wall. 32 cut a wall from its own block, and 25 from another block, such as stone into a stone brick wall or deepslate into a deepslate tile wall. At 12 walls for 6 blocks, crafting gives 2 per block. The owner chose to make every stonecutter wall recipe give 2, the ones from other blocks included, so the stonecutter matches crafting, as it already does for slabs.
- Playtest (owner): all 9 in-game checks worked as intended: 12 cobblestone walls in either pair of rows, sulfur, andesite, tuff, diorite and stone brick walls, mixed blocks refused, the recipe book, the stonecutter's 2 cobblestone walls, 2 stone brick walls from stone, 2 deepslate tile walls from deepslate and 2 tuff brick walls from tuff, its other options unchanged, and walls still not taken as fuel.

**Implementation notes**

- No mixin was needed. No placeholder textures.
- Recipes: `CaramelRecipeProvider.walls()` (datagen) writes the 32 wall recipes at their base-game ids, each built as the base game's `wallBuilder` builds it with a count of 12, keeping the base-game category and unlock advancement, byte for byte. The walk covers the families' wall variant; datagen stops if the number found (32) changes.
- Stonecutter: `CaramelRecipeProvider.stonecutter()` runs the base game's stonecutter walk, which C-001 already used to remove the stairs, and its `stonecutterResultFromBase` override now also writes every wall recipe at its base-game id with a count of 2, the cross-block ones included. Datagen stops unless it finds 103 stonecutter recipes that make stairs (95 from the walk, and 8 cut copper stairs, each cut from the copper block of the same weathering stage and waxing, from a separate copper call) and 57 that make walls, all from the walk.
- Game tests: `WallsRecipeGameTest` has 7 checks, with expected values read from the base game's data pack and item components: all 32 crafting and 57 stonecutter recipes found (25 of them from another block); the base-game grid at both places it fits in the 3x3 grid gives 12 with no other recipe matching (64 grids); each loaded crafting recipe equals the base game's file but for the count; each loaded stonecutter recipe equals the base game's file with a count of 2; the stonecutter, read as its menu lists its options, offers exactly one option of 2 for each wall each block cuts into; the 89 unlock advancements; and walls still not burning. `VanillaOverridesGameTest` covers the 89 recipes. `./gradlew build` runs 68 Caramel game tests.
- Nine deliberate breaks each failed the build or datagen, then were undone. Seven shared one build, each on a different wall, and each check's failure count matched the one worked out beforehand: the andesite wall back to 6, the brick wall given a recipe group, the cobblestone wall unlocking on stone, stone into stone brick walls back to 1 on the stonecutter, the diorite wall's stonecutter files deleted, a second stonecutter recipe for granite walls, and a second recipe matching 6 tuff. Two were datagen's counts: walls at 31 ("Expected 31 block families with walls, found 32") and stonecutter walls at 56.
- Review: four reviewers (datagen, tests, knock-ons, docs) and a skeptic per finding; the 8 findings that held were documentation and one test comment, fixed here and in `caramel-vault/recipes.md`.
- Balance audit (asked by the owner): four auditors and a skeptic per issue checked C-001 to C-008 for any work block giving far more than another, and for loops in items, fuel, compost and emeralds. None multiplies anything. The crafting table and stonecutter give the same per block for stairs' sources, slabs and walls; the one gap is the base game's copper (4 cut copper per block on the stonecutter, 1 by crafting). The only gain left is the one C-005 records, sticks not made from planks in fences and fence gates.
- Generated files: 1649 in `src/main/generated/data`, 178 new with C-008 (89 recipes, 89 advancements) and none changed.

### C-009: The Chisel, the first primitive tool

- **Status:** Done
- **Area:** Tools; Blocks and Items; Recipes; Crafting and Smithing; Work Blocks
- **Source:** Caramel's own ideas

**Pitch**

Primitive tools, the broader idea this tool opens:

- A new set of tools. They are easy to craft, and can be made in the player's inventory.
- They are weaker than the regular tools (the Chisel does not break blocks as fast), but each is two tools in one and has extra uses.
- They have no wooden version. A flint tier takes the wood tier's place: it needs a flint to craft, and has the wood tier's durability.

The Chisel:

- A new primitive tool with the combined mining efficiency and abilities of the axe and the pickaxe. Each tier has the durability of that tier's axe and pickaxe.
- Destroying a block that the axe and the pickaxe can destroy takes the Chisel twice as long as it takes them.
- As a weapon, it is slightly faster than the hoe, and deals half a heart of damage.
- A right click turns a log or wood block into its stripped log or stripped wood block, much like an axe.
- A right click turns a polished block that has variants or sub-families into a decorative block of its family. The blocks follow this order, where the family has the block: 1. Polished X, 2. X Bricks, 3. Cracked X Bricks, 4. X Tiles, 5. Chiseled X Bricks, 6. Chiseled X Pillar, then back to 1.
- This also deletes the crafting and smelting recipes of every cracked, cut, chiseled and pillar block. The stonecutter's recipes are kept.
- As pictured: in the 2x2 grid, an iron ingot above a planks block makes an iron chisel. The planks take the stick's place, and any planks work (oak, spruce, birch and so on).
- Art: the owner has made a placeholder sprite for the iron chisel and will make the others later. Until then, the missing-texture checkerboard stands in.

**Design notes and decisions**

- Checked in the Minecraft 26.3 game files before asking. Tools are built from data components, and there is no axe class: an axe's right click runs a `block_transformer`, a data registry entry that lists stripping, copper scraping and wax removal. The pickaxe has no right-click ability.
- Tiers (asked, owner's answer): all 7. The Flint, Stone, Copper, Iron, Golden and Diamond Chisels are crafted, each from its tier's material above any planks, which fits the inventory's 2x2 grid. The Stone Chisel takes cobblestone, blackstone or cobbled deepslate, like the base game's stone tools. The Netherite Chisel comes from upgrading the Diamond Chisel on a smithing table, and is fire-resistant, like the base game's netherite tools.
- The flint tier copies the wood tier: durability 59, wood's mining level and enchantability. It is repaired with flint on an anvil and is not furnace fuel.
- Mining: no block is mined by both the axe and the pickaxe (their two block tags share nothing), so the pitch is read as every block that either tool mines. The Chisel mines those at half its tier's speed and drops what that tier's pickaxe drops. Other blocks, such as dirt, break as by hand.
- Enchanted speed (asked, owner's answer): always twice as long. Efficiency adds a flat bonus on top of the tool's speed, so with only the base speed halved, an Efficiency V Iron Chisel would be about 10% slower than an iron pickaxe. The Chisel gets half of Efficiency's bonus as well, so it takes twice as long at every level. Haste speeds up both alike.
- Flint on wood (asked, owner's answer): kept as pitched. Half the wood tier's speed is the bare hand's speed, so a Flint Chisel breaks logs and planks as fast as a hand (about 3 seconds a log, against 1.5 with a wooden axe). It mines stone in about 2.25 seconds with the drop, against 7.5 by hand with none.
- Twice as long is the break itself. With the button held, the game pauses 6 ticks between blocks that do not break at once, so in blocks a minute the gap is smaller: on stone, an unenchanted Iron Chisel takes 21 ticks a block against the pickaxe's 14, and an Efficiency V Diamond Chisel 9 against 8. Where a pickaxe breaks a block at once and the Chisel does not, such as stone under Efficiency V and Haste II, the Chisel takes 8 ticks against 1. No Chisel breaks stone at once.
- Cost: a Chisel takes 1 head item where a pickaxe and an axe take 3 each, for the same durability, so it mines 3 times as many blocks per ingot as a pickaxe, at twice the time. An Iron Chisel mines slower than a stone pickaxe, and a Diamond Chisel as fast as one.
- Attack (asked, owner's answer): half a heart at every tier, as every hoe deals, and the tier's hoe attack speed plus 0.5: flint 1.5, stone 2.5, copper 2.5, iron 3.5, golden 1.5, diamond 4.5, netherite 4.5. It does not disable shields and cannot take Sharpness, Smite or Bane of Arthropods.
- Durability: 1 per block mined, 2 per attack and 1 per right-click change, as for the base game's tools.
- Enchantments: the pickaxe's set, which is Efficiency, Fortune, Silk Touch, Unbreaking, Mending and Curse of Vanishing.
- Other traits both tools share or the pickaxe has: the Chisel gets the full amethyst cluster drop and shatters decorated pots, and piglins love the Golden Chisel.
- Copper (asked, owner's answer): stripping only. Scraping oxidation and removing wax stay with the axe.
- The cycle with the blocks the game has today. The owner's block-set names map onto them: "Chiseled X Bricks" is each family's chiseled block, and "Chiseled X Pillar" is Chiseled Tuff for tuff; no other family has that block yet.
  - Stone (asked, owner's answer: Smooth Stone stands in for the Polished block): Smooth Stone, Stone Bricks, Cracked Stone Bricks, Chiseled Stone Bricks, then back.
  - Deepslate: Polished Deepslate, Deepslate Bricks, Cracked Deepslate Bricks, Deepslate Tiles, Cracked Deepslate Tiles, Chiseled Deepslate, then back. Cracked Deepslate Tiles were not in the pitched order (asked, owner's answer: add them right after Deepslate Tiles).
  - Blackstone: Polished Blackstone, Polished Blackstone Bricks, Cracked Polished Blackstone Bricks, Chiseled Polished Blackstone, then back.
  - Tuff: Polished Tuff, Tuff Bricks, Chiseled Tuff Bricks, Chiseled Tuff, then back.
  - Sulfur: Polished Sulfur, Sulfur Bricks, Chiseled Sulfur, then back.
  - Cinnabar: Polished Cinnabar, Cinnabar Bricks, Chiseled Cinnabar, then back.
  - Nether Bricks (asked, owner's answer: they have no Polished block, so they get a short cycle of their own): Nether Bricks, Cracked Nether Bricks, Chiseled Nether Bricks, then back.
  - Polished Granite, Polished Diorite, Polished Andesite and Polished Basalt have no other full block, so a right click does nothing on them.
- The cycle works on full blocks only, not stairs, slabs or walls, and only goes forward.
- Once a second (owner's playtest suggestion: a held right click carved 5 blocks a second and piled up the sounds). Asked, owner's answers: a 1-second cooldown after each change, whether the button is held or clicked again, shared by every Chisel so switching tiers does not skip it; and it covers every Chisel right click, stone, bamboo and stripping logs alike. The hotbar shows the cooldown sweep, as for an ender pearl. A click that changes nothing starts no cooldown, and mining and attacking never wait. A cycle never runs out of steps, so holding the button on a cycle block still keeps carving, about once a second: every 1 to 1.2 seconds in singleplayer, a little slower on a server with ping, because the game repeats a held click every 0.2 seconds and the client's copy of the wait ends a moment after the server's.
- Holding the click (found in review, asked, owner's answer): with a plain cooldown, a held click that landed between the server's end of the wait and the client's was carried out by the server alone, with no sound or swing for the player, and the off hand was used during the wait (blocks placed, fireworks launched, food eaten). Now, while a Chisel waits, a click on a block it would change stops at the Chisel: nothing is sent and the off hand is not used.
- A click goes to the main hand first. A Chisel there changes a cycle block, or waits on it, so a block or torch in the off hand is not placed against one. On blocks the Chisel does not change, the off hand works as usual.
- The base game's rule for every right-click tool applies: with a shield in the off hand, the Chisel's right click does nothing unless the player sneaks.
- Bamboo Mosaic (asked, owner's answer): a right click turns Bamboo Planks into Bamboo Mosaic, and back. C-002 had left Bamboo Mosaic, its slab and its stairs uncraftable until a later idea; this is that idea.
- Out of the cycle: quartz, purpur, sandstone, red sandstone, resin bricks and copper. Their cut, chiseled and pillar blocks come from the stonecutter. Copper must never cycle back to a copper block: the stonecutter cuts 1 block into 4 cut or chiseled copper, so that would turn 1 block into 4.
- Deleted recipes, 16 in all: the 5 smelting recipes for cracked blocks (Cracked Stone Bricks, Cracked Deepslate Bricks, Cracked Deepslate Tiles, Cracked Nether Bricks, Cracked Polished Blackstone Bricks), the 10 crafting recipes that turn 4 blocks into 4 cut blocks (Cut Sandstone, Cut Red Sandstone and the 8 cut coppers), and the Quartz Pillar's crafting recipe. C-002 had already removed the chiseled blocks' and the Purpur Pillar's. Every stonecutter recipe stays.
- Kept: the Chiseled Bookshelf's recipe (its only source), waxing cut and chiseled copper with honeycomb, the slabs and stairs made from cut blocks, and Caramel's own recipes that turn those slabs and stairs back into cut blocks.
- Sounds (owner's playtest suggestions, replacing the stonecutter's sound that stone first played): carving a stone block plays the anvil's sound, `block.anvil.use`, at 1.5 times its default pitch and half its volume, with the subtitle "Block chiselled" (the owner's words). The anvil itself plays it at a random pitch from 0.9 to 1.0. The borrowed anvil sound is a placeholder: the owner will commission a fitting sound later. The wood cycle and stripping play the axe's stripping sound, and show its subtitle, "Axe strips".
- Particles (a suggestion shared with the owner, after the recheck): every Chisel change, and the base game axe's stripping of logs and wood, throws 16 particles of the block as it was, a quarter of the 64 that breaking a full block throws (the owner first said a third, then a quarter). They burst from the clicked face, where the cracks of a block being mined appear: the block stays solid, so particles inside it would stay hidden (found in review). The axe's scraping and wax removal keep their own particles. Everyone nearby sees them. Unlike breaking particles, they follow the player's Particles setting, so on Decreased some and on Minimal all are left out; and the clicking player sees them a moment after the change on a server with ping.
- Cracked blocks are no longer smelted, so the 0.1 experience each gave is gone and a furnace can no longer make them unattended: each is one click on a placed block.
- Other servers (asked, owner's answer): a player with Caramel can still join a server without it. There the Chisels have no right click, and cannot be obtained.
- Left as the base game has them, and told to the owner as options: a furnace turns the base game's iron, golden and copper tools into a nugget, and not the Chisels; the advancements "Getting an Upgrade" and "Isn't It Iron Pick" ask for a pickaxe; the torch recipe unlocks on a stone pickaxe.
- Third playtest (owner): all particle checks passed, on stone, in a wall, stripping with a Chisel and with an axe, bamboo, copper left to its own sparks, the smaller burst, and none while waiting. The owner marked C-009 Done.
- Second playtest (owner): everything worked. The owner then asked for the subtitle "Block chiselled", for a Chisel to change at most one block a second, and for the carving sound at half its volume, so held clicks no longer pile up loud sounds.
- Playtest (owner): every check worked as intended but one. The anvil put a Sharpness book on a Chisel, in creative mode. There the base game's anvil skips the check of which items an enchantment fits, so a book goes on any item, a pickaxe included, unless it clashes with an enchantment the item already has. In survival it refuses, as a test now shows. The enchanting table and `/enchant` keep the item's own rules in every game mode. The checklist had not said that step needed survival.
- Art: no texture is shipped. Each Chisel shows the game's missing-texture checkerboard until the owner adds its file.

**Implementation notes**

- No mixin was needed: mining, attacking, enchanting and the right click are all data components and tags in Minecraft 26.3.
- Art: 7 item textures, 16 by 16, at `src/main/resources/assets/caramel/textures/item/` as `flint_chisel.png`, `stone_chisel.png`, `copper_chisel.png`, `iron_chisel.png`, `golden_chisel.png`, `diamond_chisel.png` and `netherite_chisel.png`, in place on 2026-11-02, commissioned by the owner on Fiverr from Lorenzo N. (seller l_n_dr), who is credited in `README.md` and `fabric.mod.json`. The owner confirmed the order is paid in full (2026-11-02), so under Fiverr's terms the owner holds the rights and can release the art under CC BY-NC-SA 4.0, as with C-010's. The owner then asked the artist about the public credit and the art's release, and the artist replied in the Fiverr chat: "No problem, I'm okay with everything" (owner's report with a screenshot, 2026-11-03). That is the artist's written agreement, on top of Fiverr's default terms; it covers C-010's art too. Checked: all seven are the right size, with fully clear or fully solid pixels. Golden, diamond and netherite are saved with a palette rather than the RGBA that `docs/TEXTURES.md` asks for. Until then each Chisel showed the missing-texture checkerboard. Adding the files needed no code change.
- Sound the idea still needs: the stone carving sound, now borrowed from the anvil, which the owner will commission. A commissioned sound ships only when its license, or the artist's written agreement, lets it ship under CC BY-NC-SA 4.0 (`CLAUDE.md`, "Art"). Its files go in `src/main/resources/assets/caramel/sounds/` (`docs/TEXTURES.md`), and `CaramelSoundsProvider` then names them in place of the anvil's sound. The subtitle "Block chiselled" stays as it is.
- Items: `io.github.cr4ftex.caramel.item.Chisels` registers the 7 Chisels from `Caramel.onInitialize`, Caramel's first registered items, and places each after its tier's hoe in the creative inventory's Tools & Utilities tab. Each is a plain item with the components the base game gives a pickaxe or an axe:
  - the tier's durability, repair material and enchantability, with the flint tier built from the wood tier's values and repaired with flint (the tag `caramel:flint_tool_materials`);
  - a tool component with the tier's rule for which blocks drop, and a rule for each of the axe's and the pickaxe's block tags at half the tier's speed;
  - attack modifiers of 0 damage, like a hoe's, and the hoe's attack speed plus 0.5, and a weapon component that takes 2 durability per attack and disables no shield;
  - a modifier that halves the `mining_efficiency` attribute, which is where Efficiency's bonus goes, so the Chisel stays at half speed when enchanted. The tooltip lists it under "When in Main Hand:" as a red "-50% Mining Efficiency", after the attack damage and speed. Efficiency's own tooltip line still shows its whole bonus.
  - the right-click component, looked up leniently: where the transformer is missing, as on a server without Caramel, the Chisel is built without it.
- The flint tier's half speed is 1.0, the bare hand's, and the game adds Efficiency only to a speed above 1.0. Its speed is the smallest value above 1.0, which keeps the hand's pace and lets Efficiency count.
- Sound: `Chisels.CARVE_STONE` registers `caramel:item.chisel.carve_stone`, Caramel's first sound, and the stone cycles' entry names it. A transformer plays its sound at pitch 1.0 and has no field to change that, so the pitch is in the sound's own definition: `CaramelSoundsProvider` writes `assets/caramel/sounds.json`, which plays the anvil's sound event, by name, at pitch 1.5 and volume 0.5, with the subtitle key `subtitles.caramel.item.chisel.carve_stone`, which `CaramelLanguageProvider` translates as "Block chiselled". The client multiplies the two pitches and the two volumes, so it is the anvil's sound pitched up, at half the anvil's loudness. Naming the event rather than its file keeps a resource pack's anvil sound here too.
- Cooldown: each Chisel carries the base game's `use_cooldown` component, 1 second in the group `caramel:chisel`. The game starts it after a right click that changed a block, and refuses the Chisel's right click until it ends, on the client and the server. Each side keeps its own copy, and the client's ends a moment later. `Chisels.holdWhileWaiting`, on Fabric's `UseBlockCallback`, answers FAIL for a waiting Chisel on a block it would change, before the game's own handling, so the client sends nothing and tries no other hand. It checks what the game checks before a change: adventure mode, a shield in the off hand without sneaking, and the transformer's rules. No mixin and no item class were needed.
- Particles: `CarvingParticles`, on the same Fabric event, sends 16 block particles from the server, spread evenly over the clicked face, 0.1 out from it and 0.1 in from its edges as `ClientLevel.addBreakingParticles` places a mined block's cracks, just before the change: the game offers no event after one. `RightClickChanges.change`, shared with the guard, works out which transformer entry the click will run by the game's own checks. A data file could not do it: a transformer entry's particle is only none, scrape, wax on or wax off. A mixin into `BlockTransformer.transformBlock` would have caught the change itself, but the event was enough. The test checks the prediction against what a click on every block then changes, and reads the burst `CarvingParticles` would send; that the listener is registered and its packet arrives was left to the playtest, until C-010's packet test.
- Right click: the data file `data/caramel/block_transformer/chisel.json`, generated by `CaramelBlockTransformerProvider`. It holds a copy of the base game's stripping entry, taken from the axe's data at datagen, then the cycles. The cycles are one list per family in that provider (`STONE_CYCLES`, `WOOD_CYCLES`), so a family or a block is added by editing its line. Datagen stops if a block is in two cycles or has block state properties, which a cycle would reset.
- The owner's next idea reworks the base stone sets and may change these cycles; the table is where that lands.
- Tags: `CaramelItemTagProvider` writes `caramel:chisels` and adds it to the base game's tags for Efficiency, for Fortune and Silk Touch, and for Unbreaking, Mending and Curse of Vanishing, and to the tags for the full amethyst cluster drop and for shattering decorated pots. The Golden Chisel joins the piglins' loved items.
- Recipes: `CaramelRecipeProvider.chisels()` writes the 6 crafting recipes and `caramel:netherite_chisel_smithing`. The Flint Chisel unlocks on having a flint; the others, and the upgrade, on what their tier's base-game pickaxe unlocks on. `carvedBlocks()` removes the 16 recipes with their unlock advancements; datagen stops unless the base game has 5 families with a cracked block and 10 with a cut block.
- Assets: `CaramelModelProvider` writes each Chisel's item definition and its model, which names the base game's handheld parent and the texture path. `CaramelLanguageProvider` writes the names, from Flint Chisel to Netherite Chisel.
- Game tests: `ChiselGameTest` (13 checks) and `ChiselRecipesGameTest` (5 checks), with expected values from the base game's own tools of each tier, its data pack, and the cycles above:
  - each Chisel has its tier's durability, enchantability and repair material, wears like a pickaxe, is not fuel, carries its right-click component, and only the netherite one resists fire;
  - for every block in the game, each Chisel drops what its tier's pickaxe or axe drops and mines at half their speed, or at a bare hand's (9009 checks);
  - as a player's mining speed, each Chisel is at half its tier's tools at Efficiency 0 to V (504 checks);
  - the attack damage and speed; every enchantment and the three trait tags against the tier's pickaxe;
  - in survival, an anvil puts a level 1 book of each enchantment on each Chisel exactly when it puts it on the tier's pickaxe, and in creative it puts Sharpness on both (309 checks);
  - the carving sound: registered at its own id and heard as far as the anvil's, defined in the mod's `sounds.json` as the anvil's sound at pitch 1.5 and volume 0.5 with a subtitle that reads "Block chiselled", and every sound Caramel registers at its own id and defined there;
  - the cooldown: each Chisel's component is 20 ticks in the shared group; a change on stone, bamboo or a log makes every Chisel wait and no base-game pickaxe, axe or hoe; a click that changes nothing starts no wait; and the wait ends after exactly 20 ticks (175 checks);
  - a waiting Chisel holds the click (FAIL) on exactly the blocks a Chisel that is not waiting changes, of the cycles, the blocks left alone, logs, wood and dirt, and passes it on every other; a shield in the off hand still takes the click unless the player sneaks; a waiting Chisel in the off hand is held too; and a waiting base-game axe is left to the game (351 checks);
  - for every block in the game, also on its side for each block with an axis, the burst an Iron Chisel or an iron axe would throw is there exactly when the Chisel's transformer, or the axe's stripping entry, gives the block a next state, and a click through `helper.useBlock` then makes that change; the particles are of the block before it, the count is a quarter of what breaking the block throws, worked out from its shape, and on each of the six faces they sit where a mined block's cracks do; a waiting Chisel and a click a shield takes throw none; and both tools throw some (3115 checks, 79 Chisel changes and 50 axe strippings throwing);
  - for every block in the game, also on its side for each block that has an axis, such as a log, the right click gives what the axe's stripping gives, or the next block of the cycles, or nothing, so no copper is scraped or unwaxed; each change has its sound, no transformer particle and a cost of 1 durability (1359 checks);
  - through a player's own right click, every cycle goes all the way round at 1 durability a click, and blocks outside the cycles are left alone at no cost, as is every cycle block under a base-game pickaxe or axe;
  - the creative tab order; every head material with every planks at all 8 places of the two grids, with no other recipe matching, and no Chisel from the pattern upside down or with a stick (960 grids); the smithing upgrade keeping wear and enchantments; each recipe's unlock criteria and recipe book category against its tier's pickaxe;
  - none of the 37 base-game recipes that crafted or cooked a cracked, cut, chiseled or pillar block is loaded, nor its advancement, and every such block, and bamboo mosaic, can still be made.
- `VanillaOverridesGameTest` now expects the 16 removed recipes and their advancements, 1084 overrides in all.
- Generated files: 1720 in `src/main/generated`, 71 new with C-009: 7 recipes and 7 advancements for the Chisels, 16 removed recipes and 16 advancements, 1 block transformer, 8 tag files, 7 item definitions, 7 models, the language file and the sounds file.
- Deliberate breaks, each failing the build and then undone. Ten shared one build, each on a different part, and failed 11 checks, each with the count worked out beforehand: the iron attack speed, the Efficiency penalty, Fortune and Silk Touch dropped, a cycle step skipped, a cycle costing 2 durability, the creative tab order, the Netherite Chisel not fire-resistant, cut copper's recipe back, the Stone Chisel taking cobblestone only, and the upgrade taking an Iron Chisel. Two more shared a second build: the flint tier at a plain half speed, and the Copper Chisel on the stone tier. Three probes gave the error messages now in `caramel-vault/tools.md` and `caramel-vault/gametest.md`: the transformer file deleted, the Chisels never registered, and a test player without its held item's modifiers. After the review, three more breaks shared a build and failed the two checks it added, 27 and 2 as worked out: the stone cycles given the stripping sound, the Iron Chisel unlocking on gold, and the Copper Chisel moved to another recipe book category.
- Review: five reviewers (code and data, tests, knock-ons and balance, what a player meets, docs) and a skeptic per finding; 32 of 33 findings held. None was a fault in what the Chisel does. One became the owner's answer on other servers, two tests were added (sounds and particles, recipe unlocks), and the rest were notes for the playtest and corrections to these notes and the vault.
- After the playtest's change, three more builds of deliberate breaks, each failing with the counts worked out beforehand, then undone. The first had the pitch at 1.4, the subtitle a key with no translation, a second Caramel sound with no definition, the wood cycle given the carving sound, and the creative test player without creative's abilities: 3 of 5 sound checks, 2 of 1359 right-click checks and 7 of 309 anvil checks failed. The second added the Chisels to the tag for Sharpness, Smite and Bane of Arthropods, and moved the sound's location off its id: 21 of 322 enchantment checks, 21 of 309 anvil checks and 3 of 5 sound checks. The third took the sounds provider out of datagen: 3 of 5. Two probes stopped datagen with the messages now in `caramel-vault/tools.md`: a pitch of 2.5, and a borrowed sound that does not exist.
- The first change's review: four reviewers (code and data, tests, what a player meets, docs and vault) and a skeptic for each one's findings; 19 of 22 held. None was a fault in what a player sees or hears. One became a check: a sound's location must match its id, because the client looks a definition up by location. The rest corrected these notes and the vault, or are notes for the recheck.
- The second change's review (the cooldown, the volume and the subtitle): four reviewers and a skeptic for each one's findings; 7 of 10 held. One was a fault a player meets: with a plain cooldown, held clicks were often carried out by the server alone, with no sound or swing for the player. It became the owner's question and the guard above. The rest corrected these notes and the vault.
- Deliberate breaks after the second change, three builds, each failing with the counts worked out beforehand, then undone. A cooldown of 0.95 seconds, the entry's volume at 0.6 and the guard without its shield check failed 14 of 175 cooldown checks, 1 of 5 sound checks and 7 of 351 waiting-click checks. No shared cooldown group and the subtitle spelled "Block chiseled" failed 28 of 175, 231 of 351 and 1 of 5. The guard never registered failed 231 of 351.
- Multiplayer: registering items makes Fabric send its item ids to each joining client, so a server with Caramel needs Caramel on the client from this idea on. The other way round still works by the owner's answer: a client builds its item components from what the server sent, and the lenient lookup keeps a missing transformer from ending the join (read in the game's code; no client was run).

### C-010: The Stone family, the first consistent block set

- **Status:** Done
- **Area:** Blocks and Items; Recipes; Crafting and Smithing; Work Blocks; Tools
- **Source:** Caramel's own ideas

**Pitch**

The owner's Block Sets idea: every family of blocks gets a consistent set, recipes that pay off and recycle back, and streamlined names. Its first part, Base Stone, covers the pure stone blocks that make up most of a structure, a biome or a dimension and have a broad or made-up name. It is built family by family, Stone first. Agreed with the owner over a review of the Block Sets tab of their Doc on 2026-10-23 and 2026-10-24:

- Names, in English. Cobblestone and its set become Cobbled Stone; Mossy Cobblestone and its set become Cobbled Mossy Stone; Every other block carrying those names follows, such as Infested Cobbled Stone. Smooth Stone keeps its name and takes a new texture (the owner's change of 2026-10-25, below).
- The Stone family's set, the base game's blocks and the new ones:
  - Stone, with its stairs, slab, button and pressure plate, and a new wall.
  - Cobbled Stone and Cobbled Mossy Stone, each with its stairs, slab and wall, and a new button and pressure plate.
  - Smooth Stone, the base game's, in a new texture, with its slab, and new stairs, wall, button and pressure plate (the owner's addition of 2026-10-31: every Smooth variant gets a button and a pressure plate, as its regular, cobbled and polished variants have).
  - A new Polished Stone, in the base game's old Smooth Stone look, with its stairs, slab, wall, button and pressure plate.
  - Stone Bricks with its stairs, slab and wall, and a new Stone Brick item; Mossy Stone Bricks with its stairs, slab and wall; Cracked Stone Bricks; Chiseled Stone Bricks.
  - A new Chiseled Stone Pillar.
  - New Stone Tiles, with stairs, slab, wall and fence, and new Cracked Stone Tiles.
- Drops: Stone and every block of its set (stairs, slab, wall, button, pressure plate) drop their Cobbled Stone counterpart, as Stone drops Cobblestone today. Silk Touch drops the block itself.
- Smelting: Cobbled Stone and Cobbled Mossy Stone smelt back into Stone. A Smooth block is smelted from its regular block: Stone smelts into Smooth Stone, as in the base game.
- Smelting the pieces (the owner's addition of 2026-10-31): the rule covers the whole set, not only the block. Each stairs, slab, wall, button and pressure plate smelts as its block does: a Cobbled Stone Pressure Plate into a Stone Pressure Plate, a Stone Button into a Smooth Stone Button.
- Crafting, each in a 2x2 square: 4 Stone make 4 Polished Stone, 4 Polished Stone make 4 Stone Bricks, and 4 Stone Bricks make 4 Stone Tiles. 4 Stone Brick items make 1 Stone Bricks. The recipes these contradict go: 4 Stone no longer make Stone Bricks.
- The stonecutter cuts Stone Brick items from Stone Bricks (4), its stairs (3), its slab (2) and its wall (2). New members get stonecutter recipes as the game's families have.
- The Chisel's order becomes: Smooth X, Polished X, X Bricks, Cracked X Bricks, Chiseled X Bricks, Chiseled X Pillar, X Tiles, Cracked X Tiles, then back to Polished X. Smooth X is only a way in: the cycle never returns to it.
- The Chisel on a set's pieces (the owner's addition of 2026-11-01, before the commit): the Chisel changes stairs, slabs and other pieces too, where the next block of the order has that piece, skipping those that do not. A Polished Stone Wall becomes a Stone Brick Wall; Polished Stone Stairs become Stone Brick Stairs, then Stone Tile Stairs, skipping Cracked and Chiseled, which have no stairs. This holds for later variants and sets.
- Scraping moss (the owner's addition of 2026-11-01): the Chisel used on any member of the Cobbled Mossy Stone or Mossy Stone Bricks sets scrapes the moss off, making the same piece of the set without moss.
- Art: the owner has an artist making the new textures and the Stone Brick item's sprite.

**Design notes and decisions**

- Review of the Doc (asked, owner's answers). The owner added End Stone, Red Netherrack, a fence for every Tiles set and a Chiseled Pillar for every family to the Doc, and fixed its rename notes; Purpur waits for later. Those families are later steps, as are their questions: the prismarine shard's recipes, Red Netherrack's first recipe, End Stone in the drop rule, and whether Polished Slate comes from Slate or Cobbled Slate.
- Smaller steps (owner's choice, from the review): the rework goes family by family, starting with Stone.
- Brick items (asked, owner's answer): they come from the stonecutter, 4 from a Bricks block, 3 from its stairs and 2 from its slab; and 2 from its wall (asked, owner's answer). 4 make exactly 1 Bricks block (asked, owner's answer), so no route gives more than it took: C-001 makes a stair worth 3/4 of a block and C-002 and C-008 a slab and a wall worth 1/2. Mossy, cracked and chiseled bricks, the pillar and the tiles give none. Stone Brick items do not make decorated pots (default, owner fine), unlike the base game's Brick.
- Yields (asked, owner's answer): 4 for 4 at every step up the chain. The earlier plan of 9 Brick items for a Tiles block was dropped.
- Drops (asked, owner's answers): Silk Touch keeps the block itself; the whole set drops its cobbled versions; Cobbled Mossy Stone smelts into Stone.
- Structures (asked, owner's answer): later. For now the new blocks come only from crafting, the stonecutter and the Chisel.
- Stonecutter (asked, owner's answer: yes; default, owner fine): each Stone block cuts into the blocks after it in the Chisel's chain, with their slabs and walls, as the game does for deepslate. Stone cuts into Polished Stone, Stone Bricks, Chiseled Stone Bricks, the pillar and Stone Tiles; Polished Stone from Stone Bricks on; Stone Bricks into the pillar and Stone Tiles; Stone Tiles into its slab and wall. Cobbled, Cobbled Mossy and Smooth Stone cut only into their own slabs and walls. Cracked blocks stay the Chisel's alone (C-009).
- Stone Tile Fence (asked, owner's answer): the nether brick fence's recipe, 4 Stone Tiles and 2 Stone Brick items, giving 24 as C-004 makes that fence give.
- Chiseled Stone Pillar (asked, owner's answer): it turns with how it is placed, like the quartz pillar, and the Chisel's cycle always makes it upright.
- Smooth Stone and Polished Stone (owner's change, 2026-10-25). First agreed on 2026-10-24: the two traded names, today's Smooth Stone showing as Polished Stone, with a new block under the id word `honed` as the new Smooth Stone. The owner then asked instead to keep the base game's Smooth Stone as Smooth Stone in the new texture, and to make Polished Stone the new block, recycling the base game's Smooth Stone textures, its slab's lined side and double slab included. So names and ids match everywhere (`smooth_stone` is Smooth Stone, `caramel:polished_stone` is Polished Stone), Smooth Stone keeps its furnace recipe (the blast furnace and the armor stand take it as before), and no structure needs a block swapped: they keep their Smooth Stone, in the new look. The cost: every Smooth Stone shows the new texture, in existing worlds too, while Caramel is installed; and since Smooth Stone's models now name Caramel's texture file, a resource pack that retextures `minecraft:block/smooth_stone` changes Polished Stone instead. The Smooth Stone Slab takes the new texture on every face (assumed from the owner's message, as no new slab art was ordered). The buttons and pressure plates go with Polished Stone, as the owner's table has them; on 2026-10-31 the owner gave Smooth Stone its own too (below).
- Chisel order (asked, owner's answer): the owner's new order, with Chiseled Bricks and the pillar before the tiles, applies to every cycle now. Deepslate's, which C-009 ships, changes with it; the other cycles have no tiles, and nether bricks' is the same loop.
- Ids (asked, owner's answers). The base game's blocks keep their ids, and only their English names change: changing an id would turn those blocks to air in any world opened without Caramel, and Fabric API's registry aliases only add a name commands accept (read in Fabric's `MappedRegistryMixin`). The creative and recipe book searches match ids only when the text holds a colon (`IdSearchTree.search`): "cobblestone" finds nothing now, and "minecraft:cobblestone" or ":cobblestone" finds Cobbled Stone. Plain text matches names alone. This was first told to the owner wrongly, as matching ids always; the owner's playtest found it. New pieces take the base game's words in their ids, such as `caramel:cobblestone_button` for the Cobbled Stone Button and `caramel:smooth_stone_stairs`. With the owner's change of 2026-10-25, Polished Stone's set is `caramel:polished_stone` and its pieces, and the `honed` word is gone.
- Defaults (owner fine): new blocks are made as the game makes the same piece of a family, with the hardness, blast resistance and sound of their closest game block; names follow the game's singular style (Stone Tile Stairs, like Deepslate Tile Stairs); English only, other languages keep the game's names; the new stairs, slabs, walls, buttons and pressure plates follow C-001 to C-008.
- The owner's additions of 2026-10-31. Smooth Stone gets a button and a pressure plate, in its new texture like its stairs and wall, crafted by C-003 and C-007's rules (2 Smooth Stone Slabs make 16 plates, 1 Smooth Stone makes 20 buttons); every Smooth variant of a later family gets them too. Every piece smelts as its block does: Cobbled Stone's and Cobbled Mossy Stone's stairs, slab, wall, button and pressure plate into Stone's (asked, owner's answer: the mossy pieces too), and Stone's into Smooth Stone's, 15 recipes. Each takes time and gives experience by what the piece is worth (asked, owner's answer), as the Stone Brick items count it: a stair 3/4 of a block, a slab and a wall 1/2 (C-001, C-002, C-008), a button 1/20 (C-007) and a pressure plate 1/16 (C-003), of the block's 200 ticks and 0.1 experience. So stairs take 150 ticks, slabs and walls 100, buttons 10, and pressure plates 13 (12.5 rounded up, so a block's worth of plates never smelts faster than the block). Smelting a block's worth of pieces costs the time and fuel of smelting the block, or 8 ticks more for plates (16 at 13 ticks, 208). As for the blocks, only the furnace smelts them (default): the base game's stone recipes have no blast furnace version.
- The Chisel on pieces (the owner's addition of 2026-11-01). Each piece follows its block's place in the Chisel's order, keeping its shape, direction and waterlogging, and skipping blocks that lack that piece: Polished Stone, Stone Brick and Stone Tile stairs, slabs and walls each make a loop. Every cycle follows the same rule (default, owner's "consistent"): deepslate, blackstone, tuff, sulfur and cinnabar's stairs, slabs and walls, and bamboo's and bamboo mosaic's stairs and slabs. A piece with no other in its line, such as Polished Blackstone's button or the Nether Brick pieces, stays as it is. Smooth Stone's stairs, slab, wall, button and pressure plate go one way into Polished Stone's, as the block does (asked, owner's answer: yes).
- Scraping moss (the owner's addition of 2026-11-01). Cobbled Mossy Stone's block, stairs, slab, wall, button and pressure plate, and Mossy Stone Bricks' block, stairs, slab and wall, become the same piece without moss, one way. It plays the carving sound and `minecraft:block.moss.break` together (asked, owner's choice). Its particles are the Moss Block's rather than the block's own (the owner's request). It drops nothing, like an axe scraping copper, and costs 1 durability and the Chisel's usual wait (defaults). Infested blocks stay out, as they already are of the Chisel's cycles (default).
- Pressed pieces (default, found while building; the owner told): a Chisel does not change a button that is pressed or a plate with something on it, until it releases. The pressed state would be copied, but the game drops the tick that releases it when the block changes, so the new piece would stay pressed and powered for good. An axe on powered copper is not touched.
- Particles on pieces (default, the owner told): C-009's rule, a quarter of what breaking the block throws, counted from the piece's shape: 16 for a full block, 12 for stairs, 8 for a bottom slab or a plate, 4 for a wall post, 2 for a button. They burst from the clicked side of the piece's shape, not of a full block.
- Buttons (asked, owner's answer): a button has its own right click, which the game runs first, so a Chisel changes a button only while the player sneaks, as the base game does for any block with its own right click. A plain click presses it. Pressure plates have no right click and change on a plain click.
- The base game's English text names "Cobblestone" only in the 9 block names renamed here (searched in `assets/minecraft/lang/en_us.json`).

**Implementation notes**

- Commit (owner's request): C-009 and C-010 go in one commit, once C-010 is playtested.
- No mixin was needed. These are Caramel's first blocks.
- Art (commissioned by the owner on Fiverr, from the seller l_n_dr, in place on 2026-10-24): the six files, each 16 by 16, are in `src/main/resources/assets/caramel/textures/`. In `block/`: `smooth_stone.png` (the base game's Smooth Stone's new texture, delivered as `honed_stone.png` and renamed, also used by its slab, stairs, wall, button and pressure plate), `stone_tiles.png` (also its stairs, slab, wall and fence), `cracked_stone_tiles.png`, and `chiseled_stone_pillar_side.png` and `chiseled_stone_pillar_top.png`. In `item/`: `stone_brick.png`. Every texture the generated models name for C-010 exists. The stone wall and the cobbled buttons and plates use the base game's textures, and Polished Stone's set the base game's Smooth Stone textures. Artist: Lorenzo N. (Fiverr seller l_n_dr), a Fiverr commission completed on 2026-10-24 (the order number stays with the owner, by their choice). Fiverr's terms give the buyer the work's intellectual property rights on full payment unless the gig description says otherwise, so the owner can release it under CC BY-NC-SA 4.0 (`CLAUDE.md`, "Art"). The owner credits everyone they work with, whoever holds the rights: the artist is in `README.md`'s Credits and in `fabric.mod.json`'s `contributors` (asked, owner's answer: both). The block textures are saved as RGB or with a palette rather than the RGBA that `docs/TEXTURES.md` asks for; the game loads both for opaque blocks. The artist's agreement to the public credit and the release is recorded in C-009's art note.
- Blocks: `io.github.cr4ftex.caramel.block.StoneFamily` registers the 22 blocks, each with its item, and the Stone Brick item, from `Caramel.onInitialize`, and places them in the creative inventory's Building Blocks, Redstone Blocks and Ingredients tabs. Each piece is made the way vanilla's `Blocks` makes the same piece, from its closest vanilla block: stairs and slabs as `registerLegacyStair` and `registerSlab` (the stone tile slab with the stone brick slab's hardness), walls as `registerWall`, buttons and pressure plates as the stone ones (a plate in its block's map colour), the fence with its block's properties as the nether brick fence has, and the pillar as a `RotatedPillarBlock`. Stairs and slabs take vanilla's view-blocking rule, a private field in `Blocks` that `StoneFamily` copies.
- Names: `CaramelLanguageProvider` names the new blocks and the item. `CaramelVanillaNamesProvider` writes the 9 renamed vanilla names into `assets/minecraft/lang/en_us.json`, where the client merges them over vanilla's file pack by pack; in Caramel's own namespace, which file won would depend on the order the client lists namespaces in (`ClientLanguage.loadFrom`).
- Models: `CaramelModelProvider`. New pieces of a vanilla block start from that block's texture alone (`BlockFamilyProvider` with `TextureMapping.cube`), because `family(block)` would also write a blockstate and a model for the vanilla block itself, over vanilla's. The pillar has an upright model and one for lying on its side, like a log. Smooth Stone's new look is Caramel's copy of vanilla's four models of it at their vanilla ids (`smooth_stone`, `smooth_stone_slab`, `smooth_stone_slab_top`, `smooth_stone_slab_double`), each with vanilla's parent and `caramel:block/smooth_stone` on every face; vanilla's blockstates already name those models. Vanilla's texture files stay untouched, and Polished Stone's models repeat vanilla's four exactly under its own ids, so it wears the old textures by name and copies no pixels.
- Families: `CaramelBlockFamilies` describes the new pieces as vanilla's `BlockFamilies` describes its own, for the model and recipe providers, built with `BlockFamily.Builder` so they stay out of vanilla's list of families that C-001 to C-009 walk.
- Drops: `CaramelBlockLootProvider` writes each new block's loot table, every one with vanilla's random sequence named after it, and replaces vanilla's for the stone stairs, slab, button and pressure plate (`data/minecraft/loot_table/blocks/`). The stone slab's keeps 2 for a double slab on both sides of the Silk Touch choice, which vanilla's helpers do not combine.
- Tags: `CaramelBlockTagProvider` adds the new blocks to `#minecraft:mineable/pickaxe` and to the stairs, slabs, walls, fences, stone buttons and stone pressure plates tags, and the four full blocks to `#minecraft:blocks_motion_no_leaves`, where vanilla lists its full stone blocks one by one: that tag is what makes a block stop rain, snow and lightning and count in the heightmaps, among others. `CaramelItemTagProvider` copies the item tags of the same names, and adds the full blocks' items to the sulfur cube's `slow_bouncy` tag, as vanilla's stone bricks are.
- Recipes: `CaramelRecipeProvider.stoneFamily()`. Caramel's stairs, slabs, walls, buttons and pressure plates get C-001 to C-008's recipes through the same helpers as vanilla's. Stone Bricks' 4 Polished Stone is written at vanilla's id, `minecraft:stone_bricks`, replacing its 4 Stone, with vanilla's unlock advancement unchanged. The rest are new `caramel:` recipes: Polished Stone from 4 Stone, the tiles, the bricks from Stone Brick items, the fence, Cobbled Mossy Stone's smelting, the 15 smeltings of the pieces and 29 stonecutter recipes, 4 of them for Stone Brick items.
- Chisel: `CaramelBlockTransformerProvider` holds the stone line in the new order, Deepslate's reordered, and `STONE_WAYS_IN`, a rule from Smooth Stone into Polished Stone that no cycle returns to. Datagen still stops on a block with state, except those in `RESET_TO_DEFAULT`, the pillar, which the cycle always makes upright.
- Tests: the shared walk of vanilla's stairs and slabs (`RecipeTestSupport.vanillaVariants`) now skips items outside the `minecraft` namespace, since Caramel's stairs and slabs are in the same tags and have no vanilla recipe. `ChiselGameTest` has the new order and the way in.
- Generated files: 1971 in `src/main/generated`, 251 new with C-010, before the owner's change below.
- Game tests: `StoneFamilyGameTest`, 13 checks, with expected values from the vanilla game and data pack and the owner's rules above:
  - the names: the 9 renamed vanilla blocks, the 22 new blocks and the Stone Brick (33 checks);
  - each new block's class, and its hardness, blast resistance, sound, map colour, tool, instrument and push reaction against its closest vanilla block, the buttons' and plates' stone behaviour, and that the pillar turns (185 and 13 checks);
  - the tags of the vanilla stone piece of each kind, and of the vanilla block each full block is made like, for blocks and items (284 checks);
  - the creative tab places (41 checks);
  - the drops with and without Silk Touch, double slabs included, and the Stone set's cobbled drops (60 checks);
  - the chain's 2x2 recipes, the fence, and C-001 to C-008's rules for every new piece, each grid matching one recipe only (22 and 199 grids);
  - the smelting: the 3 block recipes and the 15 of the pieces, each the only furnace recipe for its input, with vanilla's cobblestone into stone time and experience times what the piece is worth, the time rounded up; and every cooking recipe of any type that takes or makes a piece of Stone, Cobbled, Cobbled Mossy, Smooth or Polished Stone is one of those 18, all furnace only, so nothing smelts into or out of Polished Stone (57 checks);
  - the stonecutter's cuts for each block of the chain, exactly vanilla's own cuts of that block as C-001 and C-008 left them and the chain's, none twice and no stairs, and Stone Brick items cut only from Stone Bricks and its pieces, at 4, 3, 2 and 2 (59 and 278 checks);
  - every new block's blockstate and item definition; nothing under `assets/minecraft/` but the names and Smooth Stone's four models, each with vanilla's parent and the new texture on every face; and Polished Stone's four models equal to vanilla's, read from the client jar on the test classpath; Polished Stone Stairs in the smooth stone slab's textures; and Smooth Stone's button and plate models with the parents of vanilla's stone button's and plate's and the new texture (63 checks);
  - and that the one vanilla recipe replaced is its 4 Stone into Stone Bricks (2 checks).
  `VanillaOverridesGameTest` now also expects that recipe, and compares Caramel's `minecraft:` loot tables with the stone family's stairs, slab, button and pressure plate: 1090 overrides. The build passes with 100 tests.
- Deliberate breaks, one build, each on a different part, failing with the counts worked out beforehand, then undone: the stone stairs' cobbled drop removed (1 of 56 drop checks, and 1 override: "Caramel's data pack has no loot table minecraft:blocks/stone_stairs"); the stairs cut into 4 Stone Brick items (1 of 50 and 1 of 278); the stone tile slab without its harder break time (1 of 169); a misspelt name (1 of 33); and no way in from Smooth Stone (1 of 1380 right-click checks, and 7 of the click test's 402, one per tier; the 402 itself was not worked out beforehand, since the longer stone cycle adds checks). A probe in the same build, the stone wall's models started with `family(Blocks.STONE)`, wrote vanilla stone's blockstate and two models into Caramel's pack and failed 1 of 42 asset checks. Two more probes: a block left out of the loot provider drops nothing with no datagen error (2 of 56), and the block items registered without linking them to their blocks still passed every test, since Fabric's registry sync links them, so `StoneFamily` no longer does it itself (`caramel-vault/blocks.md`).
- Review: an independent reviewer read the change against these rules and the vanilla data, and scanned the merged recipes for conflicts and loops: none (no two recipes match one grid or stonecutter input, and no cycle of crafting, cutting, smelting, drops and the Chisel gains anything). It found the four full blocks missing from `#minecraft:blocks_motion_no_leaves`, so rain and snow would have fallen through a roof of Stone Tiles or Smooth Stone, lightning could have struck under it and the heightmaps skipped it; and their items missing from the sulfur cube's tags. The tag test had compared only the stairs, slabs, walls, fences, buttons and plates with vanilla, and the stonecutter test only that the expected cuts were there. Both tests now compare in full, and both fixes are in; the loot tables also gained vanilla's random sequence. A break then removed both tags and added a stray cut, and failed 48 of 262 tag checks (each full block outside the motion tag and the 9 tags built on it, and its item outside the sulfur cube's two) and 1 of 58 stonecutter checks, as worked out.
- Fabric's own `c:` tags list the nether brick fence under `#c:fences`; Caramel's tile fence is in vanilla's `#minecraft:fences` only, since compatibility with other mods is not a priority yet.
- The owner's change of 2026-10-25 (Smooth Stone kept, Polished Stone new): the honed blocks became Polished Stone's set, the buttons and plates moved to it, the renames went from 11 to 9, and the Chisel's way in now starts at Smooth Stone. The tests followed; their check counts above are after the change. A break then gave the Smooth Stone Slab's models the old lined side and Polished Stone Slab's the new texture, and failed 6 of 50 asset checks, 3 each way, as worked out. Generated files: 1974 in `src/main/generated`.
- Playtest (owner, 2026-10-25): everything worked as intended, but for two points. Searching "cobblestone" in the creative inventory found nothing, against what the owner had been told: the search reads ids only after a colon (above), so it is how the base game's search works, not a fault. And, as a suggestion, Polished Stone Stairs now wear vanilla's smooth stone slab's textures, the lined side and the plain top and bottom; the asset test checks it (53 checks), and a break that gave the stairs the plain texture again failed 3 of them, as worked out. The owner will add the Chisels' sprites from the same artist before the commit.
- The owner's additions of 2026-10-31 (design notes above). `StoneFamily` registers the Smooth Stone Button and Smooth Stone Pressure Plate as it does Polished Stone's, and `CaramelBlockFamilies.SMOOTH_STONE` lists them, so the families' model, loot and recipe walks make them in the new texture, dropping themselves, at 20 and 16 a craft; the plate takes the base game's smooth stone slab through `VANILLA_SLABS`. `CaramelRecipeProvider` keeps the worth of each piece next to the Stone Brick counts (`SMELTED_PIECES`) and walks the five pieces of Cobbled Stone, Cobbled Mossy Stone and Stone into the same piece of the next set, 15 furnace recipes named like the mossy block's, `caramel:<result>_from_smelting_<ingredient>`; datagen stops if a set lacks one of the five. Generated files: 2019 in `src/main/generated`, 45 new and 5 changed. The build passes with 100 tests; the check counts above are after the additions.
- Review of the additions: an independent reviewer read them against these notes and the vanilla data. No loop gains anything (each smelt swaps one piece for the same piece, one way, and a block's worth of pieces earns the block's 0.1 experience), no furnace input matches two recipes, and the base game has no cooking recipe for any stone piece to clash with. It found a datagen count that could not fail, now removed, and the asset test checking the new button and plate models' textures but not their parents, now checked against vanilla's stone button and plate.
- Deliberate breaks of the additions, one build, worked out beforehand, then undone with the generated files back byte for byte: the button worth 1/10 instead of 1/20 (3 of 57 smelting checks, one per button recipe), a misspelt "Smooth Stone Buton" (1 of 33 names), and a probe of the parent check, the test reading vanilla's pressed stone button for the Smooth Stone Button (1 of 63 asset checks): 3 of 100 tests, as worked out.
- Playtest of the additions (owner, 2026-10-31): everything worked as intended.
- The owner's additions of 2026-11-01 (design notes above). `CaramelBlockTransformerProvider` derives the piece rules from the families, through `CaramelBlockFamilies.piece`, which the recipe provider's smelting shares: 47 piece rules in the stone entry beside its 31 block rules, 4 beside bamboo's 2, and a new moss entry of 2 and 8, 119 changeable blocks with stripping's. Pieces use `copy_properties`, as vanilla's stripping does. `RightClickChanges` now answers for a button's own right click, so the particles, the waiting Chisel and the moss sound all follow one rule. `Chisels.carveWhileScraping` plays the carving sound beside the moss entry's own, since an entry names one sound and an event picks one file; `Chisels.refuseWhilePowered` ends a Chisel's click on a pressed piece. `CarvingParticles` counts and places its burst from the block's shape, and shows the Moss Block when scraping. Only `chisel.json` changed under `src/main/generated`. No mixin. A fence is outside the derived kinds: no line holds two today, and the Doc's fences for each Tiles set keep it so.
- Game tests: `ChiselPiecesGameTest`, 11 tests, with its expected blocks written out from the owner's rules: the owner's examples (5 checks), every loop and step with the state kept (147), pieces that do not change and the one-way rules (52), button and plate clicks (8), a waiting Chisel (118), the moss sounds (105), that no changeable block but a button has its own right click (119), a raw transformer leaving a pressed piece stuck (6), the refusal of pressed pieces and their change once released (8), what a real click sends to the clicking player and another one (32), and an axe on powered copper (3). `ChiselGameTest`'s exact rule walk includes pieces and moss, and its particle walk (3355 checks) the Moss Block and particles on thin blocks. The build passes with 111 tests.
- Review of these additions: an independent reviewer enumerated the rules against these notes, ran the click paths through the server's game mode in a scratch copy (off hand, adventure, shield, each with and without sneaking), and searched crafting, cutting, cooking, drops and the Chisel's rules together for a gain: none in 1818 edges. Stone Brick items stay at what each piece is worth. It found the refusal of pressed pieces also stopping axes on powered copper, the moss particles not yet in, particles floating off thin pieces, and that no test saw the listeners: with both unregistered every test still passed. All four are fixed, the last by the packet test. Route gains have no test of their own, and multi-ingredient recipes and villager trades were outside the search. Client-side sound playing was reasoned from the code and left to the playtest, which passed.
- Deliberate breaks, one build, worked out beforehand, then undone with the generated files back byte for byte: moss showing the block's own particles (9 in the particle walk, 6 in the packet test), the extra-sound listener removed (3 in the packet test), the refusal not limited to the Chisel (3 of 3 copper checks), and particles placed as on a full block (2 in the particle walk): 11 of 3355, 9 of 32 and 3 of 3, 3 of 111 tests, as worked out. An earlier break by the builder, with the button rule and the refusal both off, failed 4 tests as it predicted, and its pressed-piece test proved the stuck piece before the fix.
- Playtest of these additions (owner, 2026-11-01): the 11-step checklist all worked as intended, the moss sounds, which the tests could not hear, included.
- Committed with C-009 as `43bd6bd` on 2026-11-02; CI passed with 111 tests. The owner marked C-010 Done the same day.
