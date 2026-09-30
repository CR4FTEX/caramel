package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import io.github.cr4ftex.caramel.compost.CraftedCompost;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

// Composter chances, in the context_int_provider registry beside the burn times, encoded with the codec the game loads
// them with.
public class CaramelCompostProvider extends FabricCodecDataProvider<ContextIntProvider> {
	public CaramelCompostProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, Registries.CONTEXT_INT_PROVIDER, ContextIntProviders.DIRECT_CODEC);
	}

	@Override
	protected void configure(BiConsumer<Identifier, ContextIntProvider> provider, HolderLookup.Provider registries) {
		// C-003: a 4% chance per moss carpet, built the way vanilla builds its 30% one, so an empty composter still
		// always takes one layer.
		provider.accept(CraftedCompost.COMPOSTABLE_MOSS_CARPETS.identifier(), ContextIntProviders.compostable(registries.lookupOrThrow(Registries.BLOCK), 4));
	}

	@Override
	public String getName() {
		return "Caramel compost chances";
	}
}
