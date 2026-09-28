# Recipes

How Caramel replaces, adds and removes recipes, and the traps each one hides. The code is `CaramelRecipeProvider` in `src/client/java/io/github/cr4ftex/caramel/client/datagen/`; what it generates, and why, is idea C-001 in `docs/DESIGN.md`. Everything below was checked in Minecraft 26.3 with Fabric API 0.161, in bytecode and by loading the real data packs in a scratch copy.

## How recipes load

- Recipes and advancements are reloadable registries, loaded in the same batch by `RegistryDataLoader` (`RELOADABLE_REGISTRIES`). `RecipeManager` only wraps the frozen `minecraft:recipe` registry; it reads no files itself.
- A recipe is `data/<namespace>/recipe/<path>.json`, with id `<namespace>:<path>`.
- Fabric registers Caramel as a required, hidden data pack and always inserts it directly above `vanilla`, in new worlds, existing worlds, and safe mode alike (`ModPackResourcesUtil.refreshAutoEnabledPacks`). For each id only the top pack's file is read (`listMatchingResources`), so Caramel's `data/minecraft/recipe/<id>.json` replaces vanilla's.
- Removing a recipe: the file at the vanilla id holds `{"fabric:load_conditions":[{"condition":"fabric:false"}]}`. Fabric drops the entry before parsing (`RegistryLoadTaskPendingRegistrationMixin`), and vanilla's lower file is never read. `FabricRecipeProvider.withConditions(output, ResourceConditions.alwaysFalse())` writes it, on the recipe and on its advancement.

## Datagen

- `FabricRecipeProvider.getRecipeIdentifier` rewrites every recipe id to the mod's namespace by default, `minecraft:oak_stairs` included. Caramel overrides it to return the id unchanged, so `minecraft:` ids can replace vanilla recipes.
- Every builder saves an unlock advancement, `<namespace>:recipes/<category folder>/<path>`, and throws "No way of obtaining recipe" without an `unlockedBy`.
- `save(output, String)` throws when the string equals the default id; `save(output, ResourceKey)` does not.
- Shaped recipes match their left-right mirror image and nothing else (`ShapedRecipePattern.matches`, `Util.isSymmetrical`). A 2x2 pattern matches anywhere in the 3x3 grid, and in the inventory's 2x2 grid.
- The recipe book shows one button per group string: recipes sharing a group collapse into one.

## Traps

- **A removed recipe whose unlock advancement stays stops every world from loading.** The advancement's `recipe_unlocked` criterion names the recipe as a registry entry, so the registry freeze fails with "Unbound values in registry ResourceKey[minecraft:root / minecraft:recipe]: [...]" and "Failed to load registries due to errors". Safe mode does not help, because the Caramel pack is required. This holds for Caramel's own recipes too: deleting a generated recipe file without its advancement fails the same way. Remove both, always.
- **With the identity `getRecipeIdentifier`, a save without an explicit key replaces a vanilla recipe without any error.** It lands on the result item's `minecraft:` id: a recycling recipe saved that way replaced `minecraft:oak_planks` (logs to planks), 97 vanilla recipes in one test run. Every new recipe passes `ResourceKey.create(Registries.RECIPE, Caramel.id(...))`. The game test `onlyStairsRecipesAreReplacedOrRemoved` fails if Caramel's pack touches any other vanilla id (see [[gametest]]).
- **An unknown condition id counts as passing.** Fabric logs "Failed to parse resource conditions ... skipping", then the condition-only body fails with "No key type" and the whole load fails. Use `ResourceConditions` constants, never a hand-typed id.
- **Without the identity override, nothing fails either.** Every id moves to `caramel:`, vanilla's recipes stay active beside the new ones, and `data/minecraft/recipe` stays empty.
