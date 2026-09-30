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
- C-003: Pressure plates and carpets from slabs

### Recipes

- C-001: Cheaper stairs, and stairs back into blocks
- C-002: Cheaper slabs, and slabs back into blocks
- C-003: Pressure plates and carpets from slabs

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
- Fuel: `CaramelBurnTimeProvider` (datagen) writes `caramel:cooking/time_wood_stairs` (225) and `caramel:cooking/time_wool_stairs` (75), built like vanilla's burn times, so a blast furnace or smoker halves them (112 and 37). `io.github.cr4ftex.caramel.fuel.CraftedFuel` (named `StairsFuel` until C-003 widened it), called from `Caramel.onInitialize`, points the 28 stairs that burned in the base game at them (12 wooden, bamboo and bamboo mosaic included, and 16 wool) through Fabric's `DefaultItemComponentEvents.MODIFY`.
- Game test: `src/gametest/java/io/github/cr4ftex/caramel/gametest/StairsRecipeGameTest.java`, a separate mod `caramel-gametest`, wired by `configureTests` in `build.gradle`. `./gradlew build` and CI run its 9 checks. They read the expected values from the vanilla data pack and check every L rotation and placement, the old staircase, recycling, mixed stairs, the stonecutter, recipe groups and categories, and stairs fuel. Which vanilla ids Caramel's pack touches is checked by `VanillaOverridesGameTest`, and which items have new fuel by `CraftedFuelGameTest`, both shared with the later ideas. `OtherModStairs` registers a test-only stairs in the game-test mod to prove other mods' stairs keep their fuel; it is not on `runClient`'s classpath and not in the mod jar.
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
- **Spin-off source:** None

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
