package io.github.cr4ftex.caramel.stairs;

import java.util.Map;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

// C-001: crafting makes 4 stairs from 3 blocks, so stairs that burn as long as their block would make crafting add
// furnace fuel. They burn 3/4 of their block instead, as slabs burn 1/2. The burn times themselves are data, generated
// into data/caramel/context_int_provider/cooking/.
public final class StairsFuel {
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_STAIRS = key("cooking/time_wood_stairs");
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL_STAIRS = key("cooking/time_wool_stairs");

	// Vanilla gives wooden and wool stairs their block's burn time; each maps to the stairs burn time that replaces it.
	private static final Map<ResourceKey<ContextIntProvider>, ResourceKey<ContextIntProvider>> STAIRS_BURN_TIMES = Map.of(
			ContextIntProviders.COOKING_TIME_WOOD_BLOCKS, COOKING_TIME_WOOD_STAIRS,
			ContextIntProviders.COOKING_TIME_WOOL, COOKING_TIME_WOOL_STAIRS);

	private StairsFuel() {
	}

	public static void register() {
		// Only vanilla's stairs: C-001 leaves other mods' stairs recipes alone, so their fuel stays as those mods set it.
		// A tag would not do, since mods add their stairs to #minecraft:stairs.
		DefaultItemComponentEvents.MODIFY.register(context -> context.modify(
				item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof StairBlock
						&& BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Identifier.DEFAULT_NAMESPACE),
				(components, registries, item) -> {
					if (components.get(DataComponents.COOKING_FUEL) instanceof CookingFuel(ResolvableInt.Reference(var burnTime), var speedMultiplier)
							&& STAIRS_BURN_TIMES.containsKey(burnTime)) {
						components.set(DataComponents.COOKING_FUEL, new CookingFuel(ResolvableInt.fromKey(STAIRS_BURN_TIMES.get(burnTime)), speedMultiplier));
					}
				}));
	}

	private static ResourceKey<ContextIntProvider> key(String path) {
		return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Caramel.id(path));
	}
}
