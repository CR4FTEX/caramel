# Furnace fuel

How an item's burn time is set in Minecraft 26.3, and how Caramel changes it. The code is `io.github.cr4ftex.caramel.stairs.StairsFuel` and the datagen provider `CaramelBurnTimeProvider`; the decision behind them is in idea C-001 in `docs/DESIGN.md`. Checked in bytecode and in game tests.

## How fuel works

- Fuel is the item data component `minecraft:cooking_fuel` (`DataComponents.COOKING_FUEL`), a `CookingFuel(ResolvableInt burnTime, ResolvableFloat speedMultiplier)`. Items get it through `Item.Properties.cookingFuel(key)`.
- The burn time is a reference into the reloadable registry `context_int_provider`: for example `minecraft:cooking/time_wood_blocks` (planks, wooden stairs) and `minecraft:cooking/time_wool` (wool, wool stairs). Slabs have their own entries, at half their block's value.
- Each `cooking/time_*` entry is `div(n, ...)`. The divisor is `cooking/fast_burn_time_reduction_factor` (2) where `minecraft:block/fast_cooking` matches (the blast furnace and the smoker), and `cooking/normal_burn_time_reduction_factor` (1) elsewhere. The division is integer division (`Quotient`, `idiv`). `ContextIntProviders.cooking(predicates, normal, fast, n)` builds one exactly like vanilla does.
- A furnace reads the burn time through `AbstractFurnaceBlockEntity.getBurnDuration(ServerLevel, ItemStack)`, in its own loot context. Its fuel slot accepts any item that has the component (`canPlaceItem`), and it lights and consumes the item only for a burn time above 0 (`serverTick`). The client never computes a burn time: no class in the client-only jar references `CookingFuel` or `ResolvableInt`.

## How Caramel changes it

- New burn times are data, generated into `data/caramel/context_int_provider/cooking/` by `CaramelBurnTimeProvider` (`FabricCodecDataProvider` with `ContextIntProviders.DIRECT_CODEC`).
- Items are pointed at them with Fabric API's `DefaultItemComponentEvents.MODIFY` from common init, not a mixin. The event runs every time the item components are bound, on the server and on a connected client alike, after vanilla binds its defaults, so a change never stacks.
- Only vanilla items are changed: the predicate checks the `minecraft` namespace rather than `#minecraft:stairs`, because any mod can add its stairs to that tag. The scope is the owner's answer under "Fuel scope" in C-001, `docs/DESIGN.md`.
- Vanilla's own fuel for an item comes from `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(...)`, which Fabric's modify event never touches; the game tests read it there (see [[gametest]]).

## Traps

- **A missing burn-time file makes the item useless as fuel, silently.** A `CookingFuel` whose key does not resolve falls back to 0 (`ResolvableInt$Reference.get`). The fuel slot still takes the item, but the furnace never lights and never consumes it, and no log line names the missing file. The game tests `craftingStairsNeverAddsFuel` and `stairsThatBurnedStillBurn` catch it (see [[gametest]]); generated files left uncommitted are one way it goes missing (see [[datagen]]).
