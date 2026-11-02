package io.github.cr4ftex.caramel.client.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.cr4ftex.caramel.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

// Block tags. Like the item tags, the files under data/minecraft/tags extend vanilla's tags and replace nothing.
public class CaramelBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	// C-010: the tags each kind of new block joins, as vanilla's stone pieces of the same kind are in them. Each has an
	// item tag of the same name, which CaramelItemTagProvider copies.
	static final List<BlockItemTagId> BLOCK_AND_ITEM_TAGS = List.of(BlockItemTags.STAIRS, BlockItemTags.SLABS, BlockItemTags.WALLS, BlockItemTags.FENCES,
			BlockItemTags.STONE_BUTTONS);
	// The full blocks, which vanilla's full stone blocks list one by one where its pieces are tagged by kind.
	static final List<Block> FULL_BLOCKS = List.of(StoneFamily.POLISHED_STONE, StoneFamily.CHISELED_STONE_PILLAR, StoneFamily.STONE_TILES, StoneFamily.CRACKED_STONE_TILES);

	public CaramelBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// C-010: all mined with a pickaxe, and needing one to drop, as the stone blocks they are made from.
		builder(BlockTags.MINEABLE_WITH_PICKAXE).addAll(StoneFamily.BLOCKS.stream().map(CaramelBlockTagProvider::key));
		// Solid to rain, snow, lightning and the heightmaps, suffocating, and the rest that hangs off this tag, as
		// smooth_stone and the stone bricks are. Vanilla's tag takes the stairs, slabs, walls, fences and pressure
		// plates in by their own tags.
		builder(BlockTags.BLOCKS_MOTION_NO_LEAVES).addAll(FULL_BLOCKS.stream().map(CaramelBlockTagProvider::key));
		builder(BlockItemTags.STAIRS.block()).add(key(StoneFamily.SMOOTH_STONE_STAIRS), key(StoneFamily.POLISHED_STONE_STAIRS), key(StoneFamily.STONE_TILE_STAIRS));
		builder(BlockItemTags.SLABS.block()).add(key(StoneFamily.POLISHED_STONE_SLAB), key(StoneFamily.STONE_TILE_SLAB));
		builder(BlockItemTags.WALLS.block()).add(key(StoneFamily.STONE_WALL), key(StoneFamily.SMOOTH_STONE_WALL), key(StoneFamily.POLISHED_STONE_WALL), key(StoneFamily.STONE_TILE_WALL));
		// Like the nether brick fence, which is in #minecraft:fences and not #minecraft:wooden_fences, so it joins up
		// with other stone fences and not with wooden ones.
		builder(BlockItemTags.FENCES.block()).add(key(StoneFamily.STONE_TILE_FENCE));
		// #minecraft:buttons and #minecraft:pressure_plates hold these two tags.
		builder(BlockItemTags.STONE_BUTTONS.block()).add(key(StoneFamily.COBBLESTONE_BUTTON), key(StoneFamily.MOSSY_COBBLESTONE_BUTTON), key(StoneFamily.SMOOTH_STONE_BUTTON),
				key(StoneFamily.POLISHED_STONE_BUTTON));
		builder(BlockTags.STONE_PRESSURE_PLATES).add(key(StoneFamily.COBBLESTONE_PRESSURE_PLATE), key(StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE),
				key(StoneFamily.SMOOTH_STONE_PRESSURE_PLATE), key(StoneFamily.POLISHED_STONE_PRESSURE_PLATE));
	}

	private static ResourceKey<Block> key(Block block) {
		return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
	}

	@Override
	public String getName() {
		return "Caramel block tags";
	}
}
