package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

// Item tags. A tag file adds to the same tag in other packs, so the files under data/minecraft/tags extend vanilla's
// tags and replace nothing.
public class CaramelItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public CaramelItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, CaramelBlockTagProvider blockTags) {
		super(output, registriesFuture, blockTags);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// C-009: the Chisels, and what repairs the flint one.
		builder(Chisels.CHISELS).addAll(Chisels.ALL.stream().map(chisel -> key(chisel.item())));
		builder(Chisels.FLINT_TOOL_MATERIALS).add(key(Items.FLINT));
		// The pickaxe's enchantments: Efficiency; Fortune and Silk Touch; Unbreaking and Mending, and with them Curse of
		// Vanishing, whose tag holds the durability one. Not the axe's Sharpness, Smite or Bane of Arthropods.
		builder(ItemTags.MINING_ENCHANTABLE).addTag(Chisels.CHISELS);
		builder(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(Chisels.CHISELS);
		builder(ItemTags.DURABILITY_ENCHANTABLE).addTag(Chisels.CHISELS);
		// What vanilla's pickaxes and axes are tagged for besides enchanting: the full amethyst cluster drop, shattering
		// decorated pots on a hit, and piglins loving the golden one.
		builder(ItemTags.CLUSTER_MAX_HARVESTABLES).addTag(Chisels.CHISELS);
		builder(ItemTags.BREAKS_DECORATED_POTS).addTag(Chisels.CHISELS);
		builder(ItemTags.PIGLIN_LOVED).add(key(Chisels.GOLDEN_CHISEL.item()));
		// C-010: the items of the new stairs, slabs, walls, fence and buttons, in the item tags of the block tags they
		// are in.
		CaramelBlockTagProvider.BLOCK_AND_ITEM_TAGS.forEach(this::copy);
		// A sulfur cube swallows the full blocks, as it does smooth_stone and the stone bricks.
		builder(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_BOUNCY).addAll(CaramelBlockTagProvider.FULL_BLOCKS.stream().map(block -> key(block.asItem())));
	}

	private static ResourceKey<Item> key(Item item) {
		return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
	}

	@Override
	public String getName() {
		return "Caramel item tags";
	}
}
