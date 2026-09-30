package io.github.cr4ftex.caramel.compost;

import java.util.List;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

// C-003 makes 32 moss or pale moss carpets from 2 blocks, 16 per block, so composting the carpets must not give more
// than composting the block. Each carpet adds a layer with a 4% chance, 1/16 of its block's 65% rounded down, instead
// of vanilla's 30%. The first item into an empty composter still always adds one, as vanilla builds every chance. The
// chance itself is data, generated into data/caramel/context_int_provider/compostable/.
public final class CraftedCompost {
	public static final ResourceKey<ContextIntProvider> COMPOSTABLE_MOSS_CARPETS = ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Caramel.id("compostable/moss_carpets"));

	private CraftedCompost() {
	}

	public static void register() {
		// Only vanilla's two moss carpets, which the owner named; other mods' carpets keep their chance.
		DefaultItemComponentEvents.MODIFY.register(context -> context.modify(List.of(Items.MOSS_CARPET, Items.PALE_MOSS_CARPET), (components, registries, item) -> {
			if (components.get(DataComponents.COMPOSTABLE) instanceof Compostable(ResolvableInt.Reference(var layers))
					&& layers.equals(ContextIntProviders.COMPOSTABLE_LOW)) {
				components.set(DataComponents.COMPOSTABLE, new Compostable(COMPOSTABLE_MOSS_CARPETS));
			}
		}));
	}
}
