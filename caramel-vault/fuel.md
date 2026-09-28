# Furnace fuel

How an item's burn time is set in Minecraft 26.3, and how Caramel changes it. The code is `io.github.cr4ftex.caramel.stairs.StairsFuel` and the datagen provider `CaramelBurnTimeProvider`; the decision behind them is in idea C-001 in `docs/DESIGN.md`. Checked in bytecode and in game tests.

## How fuel works

- Fuel is the item data component `minecraft:cooking_fuel` (`DataComponents.COOKING_FUEL`), a `CookingFuel(ResolvableInt burnTime, ResolvableFloat speedMultiplier)`. Items get it through `Item.Properties.cookingFuel(key)`.
- The burn time is a reference into the reloadable registry `context_int_provider`: for example `minecraft:cooking/time_wood_blocks` (planks, wooden stairs) and `minecraft:cooking/time_wool` (wool, wool stairs). Slabs have their own entries, at half their block's value.
- Each entry is `div(n, ...)` over a condition on `minecraft:block/fast_cooking`, which matches the blast furnace and the smoker; there the divisor is 2, with integer division. `ContextIntProviders.cooking(predicates, normal, fast, n)` builds one exactly like vanilla does.
- A furnace reads the burn time through `AbstractFurnaceBlockEntity.getBurnDuration(ServerLevel, ItemStack)`, in its own loot context. Its fuel slot accepts any item that has the component (`canPlaceItem`); the client never computes a burn time.

## How Caramel changes it

- New burn times are data, generated into `data/caramel/context_int_provider/cooking/` by `CaramelBurnTimeProvider` (`FabricCodecDataProvider` with `ContextIntProviders.DIRECT_CODEC`).
- Items are pointed at them with Fabric API's `DefaultItemComponentEvents.MODIFY` from common init, not a mixin. The event runs every time the item components are bound, on the server and on a connected client alike, after vanilla binds its defaults, so a change never stacks.
- Only vanilla items are changed (the predicate checks the `minecraft` namespace). A tag would not do, since mods add their stairs to `#minecraft:stairs`.

## Traps

- **A burn-time file that is missing burns for 0 ticks, silently.** A `CookingFuel` whose key does not resolve falls back to 0. The furnace still accepts the item and burns it for nothing, and no log line names the missing file. The game tests `craftingStairsNeverAddsFuel` and `stairsThatBurnedStillBurn` catch it (see [[gametest]]). CI does not run datagen, so the generated files must be committed (see [[datagen]]).
- **A test that reads live burn times only compares Caramel with itself.** "Was fuel before" comes from the maps vanilla builds, `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(...)`, which Fabric's modify event never touches.
