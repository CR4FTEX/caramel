package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.item.trading.VillagerTrades;
import net.minecraft.world.level.block.ColorCollection;

// Villager trades, in the villager_trade registry, encoded with the codec the game loads them with. Each is written
// at its vanilla id, so it replaces vanilla's.
public class CaramelTradeProvider extends FabricCodecDataProvider<VillagerTrade> {
	public CaramelTradeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, Registries.VILLAGER_TRADE, VillagerTrade.CODEC);
	}

	@Override
	protected void configure(BiConsumer<Identifier, VillagerTrade> provider, HolderLookup.Provider registries) {
		// C-003: a wool makes 16 carpets, so an apprentice shepherd's carpets are worth buying at 20 an emerald
		// instead of 4. Everything else is built as VillagerTrades.registerCarpetPurchases builds it.
		ColorCollection.zipApply(VillagerTrades.SHEPHERD_2_EMERALD_CARPETS, Items.CARPET, (trade, carpet) ->
				provider.accept(trade.identifier(), VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(carpet, 20), 16, 5, 0.05F).build()));
	}

	@Override
	public String getName() {
		return "Caramel villager trades";
	}
}
