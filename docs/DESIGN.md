# Caramel design document

This is the living design document for Caramel. It holds the vision, the principles that follow from it, and a log of every idea the owner pitches or takes on. A contributor's idea is only a suggestion until the owner takes it on. Ideas are recorded here before they are implemented, and their entries are updated as the work moves along.

## Vision

The owner (CR4FTEX) set out Caramel's vision in a presentation, "Mod Presentation", which replaced the two pillars this document first quoted. The owner's words are quoted below, each followed by a short summary. When the presentation changes, this section follows it.

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

In short: polish and change the crafting recipes, redefine the work blocks, add new blocks, items and tools, and let players build without wasting resources or time. Every block family becomes consistent, and each of its members gets a purpose. C-001 to C-008 serve this goal. The slide's examples, such as chiseled and cut blocks, are not pitches until the owner pitches them.

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

### Recipes

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks
- C-003: Pressure plates and carpets from slabs
- C-004: More fences per craft
- C-005: More fence gates per craft
- C-006: More doors and trapdoors per craft, iron and copper from blocks
- C-007: More buttons per craft
- C-008: More walls per craft

### Work Blocks

- C-001: Cheaper stairs, and stairs back into blocks
- C-008: More walls per craft

### Blocks and Items

No ideas yet.

### Tools

No ideas yet.

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
  - C-003 gives the pressure plates and wool carpets new recipes, which makes detector rails craftable again. Of this list, only bamboo mosaic, its slab and its stairs remain; the owner plans a later idea for them, the chiseled blocks and the pillars.
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
