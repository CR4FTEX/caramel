package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import io.github.cr4ftex.caramel.stairs.StairsFuel;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

// Furnace burn times, in the context_int_provider registry, encoded with the codec the game loads them with.
public class CaramelBurnTimeProvider extends FabricCodecDataProvider<ContextIntProvider> {
	public CaramelBurnTimeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, Registries.CONTEXT_INT_PROVIDER, ContextIntProviders.DIRECT_CODEC);
	}

	@Override
	protected void configure(BiConsumer<Identifier, ContextIntProvider> provider, HolderLookup.Provider registries) {
		HolderGetter<LootItemCondition> predicates = registries.lookupOrThrow(Registries.PREDICATE);
		HolderGetter<ContextIntProvider> burnTimes = registries.lookupOrThrow(Registries.CONTEXT_INT_PROVIDER);
		Holder.Reference<ContextIntProvider> normal = burnTimes.getOrThrow(ContextIntProviders.COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR);
		Holder.Reference<ContextIntProvider> fast = burnTimes.getOrThrow(ContextIntProviders.COOKING_FAST_BURN_TIME_REDUCTION_FACTOR);
		// C-001: 3/4 of a plank's 300 ticks and of wool's 100, built the way vanilla builds every burn time, so a blast
		// furnace or smoker halves them too.
		provider.accept(StairsFuel.COOKING_TIME_WOOD_STAIRS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 225));
		provider.accept(StairsFuel.COOKING_TIME_WOOL_STAIRS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 75));
	}

	@Override
	public String getName() {
		return "Caramel burn times";
	}
}
