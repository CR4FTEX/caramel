package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import io.github.cr4ftex.caramel.fuel.CraftedFuel;

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
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_STAIRS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 225));
		provider.accept(CraftedFuel.COOKING_TIME_WOOL_STAIRS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 75));
		// C-003: 1/16 of a plank's 300 ticks and of wool's 100, rounded down so 16 never burn longer than the block.
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_PRESSURE_PLATES.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 18));
		provider.accept(CraftedFuel.COOKING_TIME_WOOL_CARPETS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 6));
		// C-004: 12 fences cost 4 planks and 2 sticks, 5 planks, so each burns 5/12 of a plank's 300 ticks.
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_FENCES.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 125));
		// C-006: 6 planks make 16 doors or 16 trapdoors, so each burns 6/16 of a plank's 300 ticks, rounded down.
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_DOORS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 112));
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_TRAPDOORS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 112));
		// C-007: 1 plank makes 20 buttons, so each burns 1/20 of a plank's 300 ticks.
		provider.accept(CraftedFuel.COOKING_TIME_WOOD_BUTTONS.identifier(), ContextIntProviders.cooking(predicates, normal, fast, 15));
	}

	@Override
	public String getName() {
		return "Caramel burn times";
	}
}
