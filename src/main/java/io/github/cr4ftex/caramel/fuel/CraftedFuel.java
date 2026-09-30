package io.github.cr4ftex.caramel.fuel;

import java.util.List;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

// Items that Caramel's recipes make cheaper burn for what they cost, so crafting never adds furnace fuel. C-001 makes
// 4 stairs from 3 blocks, so stairs burn 3/4 of their block, as slabs burn 1/2. C-003 makes 16 pressure plates or
// carpets from 2 slabs, one block, so they burn 1/16 of it, rounded down. The burn times themselves are data, generated
// into data/caramel/context_int_provider/cooking/.
public final class CraftedFuel {
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_STAIRS = key("cooking/time_wood_stairs");
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL_STAIRS = key("cooking/time_wool_stairs");
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_PRESSURE_PLATES = key("cooking/time_wood_pressure_plates");
	public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL_CARPETS = key("cooking/time_wool_carpets");

	// Vanilla's burn time for a kind of block, and the one that replaces it. Vanilla gives wooden and wool stairs their
	// block's burn time, wooden pressure plates a plank's, and wool carpets their own 67 ticks.
	private record Replacement(Class<? extends Block> block, ResourceKey<ContextIntProvider> vanilla, ResourceKey<ContextIntProvider> crafted) {
	}

	private static final List<Replacement> REPLACEMENTS = List.of(
			new Replacement(StairBlock.class, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS, COOKING_TIME_WOOD_STAIRS),
			new Replacement(StairBlock.class, ContextIntProviders.COOKING_TIME_WOOL, COOKING_TIME_WOOL_STAIRS),
			new Replacement(PressurePlateBlock.class, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS, COOKING_TIME_WOOD_PRESSURE_PLATES),
			new Replacement(WoolCarpetBlock.class, ContextIntProviders.COOKING_TIME_WOOL_CARPETS, CraftedFuel.COOKING_TIME_WOOL_CARPETS));

	private CraftedFuel() {
	}

	public static void register() {
		// Only vanilla's items: the ideas leave other mods' recipes alone, so their fuel stays as those mods set it.
		// A tag would not do, since mods add their stairs to #minecraft:stairs.
		DefaultItemComponentEvents.MODIFY.register(context -> context.modify(
				item -> item instanceof BlockItem blockItem && REPLACEMENTS.stream().anyMatch(replacement -> replacement.block().isInstance(blockItem.getBlock()))
						&& BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Identifier.DEFAULT_NAMESPACE),
				(components, registries, item) -> {
					if (components.get(DataComponents.COOKING_FUEL) instanceof CookingFuel(ResolvableInt.Reference(var burnTime), var speedMultiplier)) {
						Block block = ((BlockItem) item).getBlock();
						for (Replacement replacement : REPLACEMENTS) {
							if (replacement.block().isInstance(block) && replacement.vanilla().equals(burnTime)) {
								components.set(DataComponents.COOKING_FUEL, new CookingFuel(ResolvableInt.fromKey(replacement.crafted()), speedMultiplier));
							}
						}
					}
				}));
	}

	private static ResourceKey<ContextIntProvider> key(String path) {
		return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Caramel.id(path));
	}
}
