# Caramel design document

This is the living design document for Caramel. It holds the vision, the principles that follow from it, and a log of every idea the owner pitches or takes on. A contributor's idea is only a suggestion until the owner takes it on. Ideas are recorded here before they are implemented, and their entries are updated as the work moves along.

## Vision

Caramel rests on two pillars, set by the owner (CR4FTEX).

### Pillar 1: Streamline and improve what exists

> "Streamline and improve every concepts and already implemented features (The Deep Dark, Crafting and Smithing, Recipes, Blocks and Items, World Generation, etc.), without steering too far to the core identity and gameplay loop of the game."

In short: streamline and improve every existing concept and feature in the game without steering too far from its core identity and gameplay loop.

### Pillar 2: Bring in content from the spin-offs

> "Add content from the Minecraft spin-offs in order to improve barren areas and neglected established concepts of the original game, giving each dimension a fresh coat of paint and more incentives to explore and risk to be rewarded."

In short: use content from the Minecraft spin-off games to improve barren areas and neglected established concepts of the original game, giving each dimension a fresh coat of paint and more incentives to explore and risk to be rewarded.

## Guiding principles

These come only from the owner's words above. Check every idea against them.

- **Keep the core identity and gameplay loop.** Caramel changes Minecraft without steering too far from what the game is and how it is played.
- **Streamline and improve what exists.** Caramel streamlines and improves every concept and feature the game already has.
- **Risk should be rewarded.** Caramel gives more incentives to explore, and risk is rewarded.
- **Every dimension gets attention.** The Overworld, the Nether and the End each get a fresh coat of paint.

## Priorities

Set by the owner while playtesting C-001:

> "the topic of compatibility with the other mods is not really a priority right now, as I would like to focus our efforts into the Vanilla game first before covering cross-compatibility with other mods."

In short: Caramel targets the base game first. Compatibility with other mods comes later.

## Target areas

These are the areas the owner has named as targets. The owner's list ended with "etc.", so more areas will be added as ideas come in. Under each heading, list the IDs of the idea log entries that affect it.

### The Deep Dark

No ideas yet.

### Crafting and Smithing

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks

### Recipes

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks

### Blocks and Items

No ideas yet.

### World Generation

No ideas yet.

### Dimensions

#### Overworld

No ideas yet.

#### Nether

No ideas yet.

#### End

No ideas yet.

## Spin-off sources in scope

- Minecraft Dungeons
- Minecraft Legends
- Minecraft Earth
- Minecraft Story Mode

The owner may add others.

Per the owner, spin-off content is brought in to improve barren areas and neglected established concepts of the original game.

All art must be original. Never copy or rip textures, models or sounds from any of these games, or from any other game or mod. Recreating a concept in new, original art is fine. Caramel is a fan project and is not affiliated with or endorsed by Mojang Studios or Microsoft.

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
- **Spin-off source:** <game, or None>

**Pitch**

<The owner's pitch, summarized faithfully.>

**Design notes and decisions**

- <Questions asked, answers given, choices made and why.>

**Implementation notes**

- <Key classes and files, datagen providers, mixins (and why a mixin was needed), placeholder textures still to be replaced.>
```

### Entries

### C-001: Cheaper stairs, and stairs back into blocks

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Spin-off source:** None

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
- No duplication: crafting and recycling trade 3 blocks for 4 stairs both ways. The stonecutter's best case is 4 cut copper stairs from one copper block. Those recycle into 3 cut copper, fewer than the 4 cut copper the same block gives directly.
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
- Fuel: `CaramelBurnTimeProvider` (datagen) writes `caramel:cooking/time_wood_stairs` (225) and `caramel:cooking/time_wool_stairs` (75), built like vanilla's burn times, so a blast furnace or smoker halves them (112 and 37). `io.github.cr4ftex.caramel.stairs.StairsFuel`, called from `Caramel.onInitialize`, points the 28 stairs that burned in the base game at them (12 wooden, bamboo and bamboo mosaic included, and 16 wool) through Fabric's `DefaultItemComponentEvents.MODIFY`.
- Game test: `src/gametest/java/io/github/cr4ftex/caramel/gametest/StairsRecipeGameTest.java`, a separate mod `caramel-gametest`, wired by `configureTests` in `build.gradle`. `./gradlew build` and CI run its 10 checks. They read the expected values from the vanilla data pack and check every L rotation and placement, the old staircase, recycling, mixed stairs, the stonecutter, recipe groups and categories, and every item's fuel. Which vanilla ids Caramel's pack touches is checked by `VanillaOverridesGameTest`, shared with C-002. `OtherModStairs` registers a test-only stairs in the game-test mod to prove other mods' stairs keep their fuel; it is not on `runClient`'s classpath and not in the mod jar.
- Generated files: 790 in `src/main/generated/data`. They must be committed, because CI does not run datagen.
- No placeholder textures.

### C-002: Cheaper slabs, and slabs back into blocks

- **Status:** Done
- **Area:** Recipes; Crafting and Smithing
- **Spin-off source:** None

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
- Pressure plates (asked, owner's answer): all 17 pressure plate recipes are removed, the two weighted plates (2 gold or 2 iron ingots) included, although ingots have no slabs. No pressure plate can be crafted until the owner's next idea gives them a new recipe.
- Carpets (asked, owner's answer): the 16 wool carpet recipes are removed for now, and wool slabs get the new recipe as pitched. No carpet can be crafted from wool until a later idea gives them a new recipe. Moss and pale moss carpets keep theirs: those blocks have no slabs. The recipes that dye a carpet another colour stay. Shepherds still sell carpets, but each one offers only 2 trades at level 2, drawn from 37 (16 of them carpets), so one shepherd sells at most 2 colours.
- Bamboo mosaic (asked, owner's answer): its recipe is removed, so bamboo slabs recycle like every other slab. Bamboo mosaic, and with it its slab and stairs, cannot be crafted until a later idea gives it a recipe.
- Purpur pillar (asked, owner's answer): removed like the chiseled recipes, so purpur slabs recycle into purpur blocks. The stonecutter still makes the pillar.
- What can no longer be made (found in review, from the 26.3 recipes, loot tables, villager trades and structure files):
  - None of the 17 pressure plates has a recipe. Only a few can still be found: the oak, spruce and acacia plates in villages (oak also in trial chambers), the stone plate in ancient cities and desert pyramids, and the light weighted plate in ruined portal chests. The other 12 cannot be obtained in survival.
  - Detector rails need a stone pressure plate, so each batch of 6 needs a stone plate found in the world.
  - Bamboo mosaic, its slab and its stairs cannot be obtained in survival at all: no structure, chest or trade has them. Their recipes stay, but each needs bamboo mosaic to start.
  - Every chiseled block and the purpur pillar can still be cut on the stonecutter, and the waxed chiseled coppers still come from honeycomb.
- Playtest (owner): all 12 in-game checks passed as intended: slabs from 2 blocks in the 2x2 and 3x3 grids, the old row of 3 gone, several families, alternative blocks, recycling in both columns and in the crafting table, stacked slabs giving the base block instead of the chiseled block, mixed slabs refused, pressure plates and carpets no longer crafted, the stonecutter unchanged, the recipe book, and the detector rail.

**Implementation notes**

- No mixin was needed. No fuel change, and no placeholder textures.
- Recipes: `CaramelRecipeProvider.replaceSlabs()` (datagen). It writes 100 recipes at `minecraft:<slab>`, for the 99 block families with slabs and the smooth stone slab, which vanilla crafts outside any family, and 100 at `caramel:<base>_from_<slab>`. It removes 55 recipes and their unlock advancements with Fabric's always-false load condition. The families' variants give 15 pressure plates, the 16 carpets and 16 chiseled blocks. The two weighted plates, the four waxed chiseled coppers, the purpur pillar and bamboo mosaic are removed by name. Datagen stops if the number of families with slabs (99), pressure plates (15), carpets (16) or chiseled blocks (16) changes.
- The removed files carry vanilla's recipe bodies. Four removed unlock advancements (chiseled stone bricks, chiseled quartz block, chiseled sandstone, chiseled red sandstone) carry simpler criteria than vanilla's; Fabric drops them before reading.
- Game tests, in `src/gametest/java/io/github/cr4ftex/caramel/gametest/`:
  - `SlabsRecipeGameTest` has 9 checks, with expected values read from the vanilla data pack: every slab found, the new recipe in every placement of the 2x2 and 3x3 grids, the vanilla group and category, the old row of 3 gone, recycling in every placement, mixed pairs, the removed recipes gone, the chiseled blocks still cut, and fuel.
  - `VanillaOverridesGameTest` checks which vanilla ids Caramel's pack replaces or removes, for C-001 and C-002 together. It works the list out from the vanilla pack: pressure plates by the `#minecraft:pressure_plates` block tag, and every other recipe that the new grids would also match.
  - `UnlockAdvancementsGameTest` checks that every recipe in the `caramel` namespace has exactly one unlock advancement.
  - `RecipeTestSupport` holds the helpers these tests and C-001's share.
  - Five deliberate breaks to the generated files each failed the build, then were undone: a carpet removal deleted, a recycling recipe deleted, the light weighted plate's removal deleted, a slab count changed, the chiseled stone bricks removal deleted.
- Generated files: 1300 in `src/main/generated/data`, 510 of them new with C-002. They must be committed, because CI does not run datagen.
