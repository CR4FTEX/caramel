# Villager trades

How a villager's trades are defined in Minecraft 26.3, and how Caramel changes one. The code is the datagen provider `CaramelTradeProvider` in `src/client/java/io/github/cr4ftex/caramel/client/datagen/`; the decision behind it is in idea C-003 in `docs/DESIGN.md`. Checked in bytecode and in game tests.

## How trades work

- Each trade is an entry of the data-pack registry `minecraft:villager_trade`, a file `data/<namespace>/villager_trade/<path>.json` holding a `VillagerTrade`: `wants` (a `TradeCost`) and `gives` (an `ItemStackTemplate`); `max_uses`, `xp` and `reputation_discount`, which default to 4, 1 and 0 when left out; and optional `additional_wants`, `merchant_predicate`, `given_item_modifier` and `double_trade_price_enchantments` (`javap -c` on `VillagerTrade`).
- Which trades a villager draws at each level is a `trade_set` file: `data/minecraft/trade_set/shepherd/level_2.json` draws `amount` 2 trades, through its own `random_sequence`, from the tag `#minecraft:shepherd/level_2`, which lists 37 (5 dye, 16 wool, 16 carpet).
- Vanilla builds its trades in `VillagerTrades.bootstrap`. The shepherds' carpet trades come from `registerCarpetPurchases`, at the keys in `VillagerTrades.SHEPHERD_2_EMERALD_CARPETS`, each `VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(carpet, 4), 16, 5, 0.05F)`.
- A villager saves the offers it already has (`AbstractVillager.addAdditionalSaveData` writes `Offers`) and draws new ones only through `Villager.updateTrades`, which reads `VillagerProfession.getTrades(level)`. Two methods call it: `AbstractVillager.getOffers`, while the villager holds no offers (`Villager.setVillagerData` clears them when the profession changes), and `Villager.increaseMerchantCareer`, on a level up. A changed trade reaches only offers drawn after the change.

## How Caramel changes them

- A file at the vanilla id replaces vanilla's trade, as a recipe file does (see [[recipes]], "How recipes load"). `CaramelTradeProvider` (`FabricCodecDataProvider` with `VillagerTrade.CODEC`) writes each one with vanilla's own builder call and a new count, so the rest of the trade stays vanilla's byte for byte.
- `PressurePlatesAndCarpetsGameTest.shepherdsSellTwentyCarpets` checks every vanilla carpet trade as loaded, and `VanillaOverridesGameTest` fails if Caramel's pack holds any other `villager_trade` file in the `minecraft` namespace (see [[gametest]]).
