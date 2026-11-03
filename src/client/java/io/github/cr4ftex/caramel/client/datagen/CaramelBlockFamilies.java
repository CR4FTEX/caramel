package io.github.cr4ftex.caramel.client.datagen;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import io.github.cr4ftex.caramel.block.StoneFamily;

import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// C-010's new pieces, described as vanilla's BlockFamilies describes its own, for the model and recipe providers. They
// are built with BlockFamily.Builder, not BlockFamilies.familyBuilder, which would add them to vanilla's list of
// families and change what C-001 to C-009 walk. A family here holds its base block and Caramel's pieces only: vanilla's
// own pieces keep their vanilla models and recipes.
final class CaramelBlockFamilies {
	static final BlockFamily STONE = new BlockFamily.Builder(Blocks.STONE)
			.wall(StoneFamily.STONE_WALL)
			.getFamily();
	static final BlockFamily COBBLESTONE = new BlockFamily.Builder(Blocks.COBBLESTONE)
			.button(StoneFamily.COBBLESTONE_BUTTON)
			.pressurePlate(StoneFamily.COBBLESTONE_PRESSURE_PLATE)
			.getFamily();
	static final BlockFamily MOSSY_COBBLESTONE = new BlockFamily.Builder(Blocks.MOSSY_COBBLESTONE)
			.button(StoneFamily.MOSSY_COBBLESTONE_BUTTON)
			.pressurePlate(StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE)
			.getFamily();
	// Smooth Stone's slab is vanilla's, made outside any family.
	static final BlockFamily SMOOTH_STONE = new BlockFamily.Builder(Blocks.SMOOTH_STONE)
			.stairs(StoneFamily.SMOOTH_STONE_STAIRS)
			.wall(StoneFamily.SMOOTH_STONE_WALL)
			.button(StoneFamily.SMOOTH_STONE_BUTTON)
			.pressurePlate(StoneFamily.SMOOTH_STONE_PRESSURE_PLATE)
			.getFamily();
	static final BlockFamily POLISHED_STONE = new BlockFamily.Builder(StoneFamily.POLISHED_STONE)
			.stairs(StoneFamily.POLISHED_STONE_STAIRS)
			.slab(StoneFamily.POLISHED_STONE_SLAB)
			.wall(StoneFamily.POLISHED_STONE_WALL)
			.button(StoneFamily.POLISHED_STONE_BUTTON)
			.pressurePlate(StoneFamily.POLISHED_STONE_PRESSURE_PLATE)
			.getFamily();
	// The fence is crafted like the nether brick fence, which no family walk writes (CaramelRecipeProvider.stoneFamily).
	static final BlockFamily STONE_TILES = new BlockFamily.Builder(StoneFamily.STONE_TILES)
			.stairs(StoneFamily.STONE_TILE_STAIRS)
			.slab(StoneFamily.STONE_TILE_SLAB)
			.wall(StoneFamily.STONE_TILE_WALL)
			.fence(StoneFamily.STONE_TILE_FENCE)
			.cracked(StoneFamily.CRACKED_STONE_TILES)
			.getFamily();

	static final List<BlockFamily> ALL = List.of(STONE, COBBLESTONE, MOSSY_COBBLESTONE, SMOOTH_STONE, POLISHED_STONE, STONE_TILES);

	// The slab each new pressure plate is crafted from (C-003), where it is vanilla's and so in no family here.
	static final Map<Block, Block> VANILLA_SLABS = Map.of(
			Blocks.COBBLESTONE, Blocks.COBBLESTONE_SLAB,
			Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_SLAB,
			Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB);

	// The five kinds of piece the Chisel carries across a cycle (C-010). CaramelRecipeProvider.SMELTED_PIECES lists the same five,
	// with what each smelts like.
	static final List<BlockFamily.Variant> PIECES = List.of(BlockFamily.Variant.STAIRS, BlockFamily.Variant.SLAB, BlockFamily.Variant.WALL, BlockFamily.Variant.BUTTON,
			BlockFamily.Variant.PRESSURE_PLATE);

	private CaramelBlockFamilies() {
	}

	// A block's piece of one kind, from Caramel's family and vanilla's for that base block together (a base block can be in
	// both, Stone is), or null if it has none.
	static @Nullable Block piece(Block base, BlockFamily.Variant variant) {
		return piece(base, ALL.stream().filter(family -> family.getBaseBlock() == base).findFirst().orElse(null),
				BlockFamilies.getAllFamilies().filter(family -> family.getBaseBlock() == base).findFirst().orElse(null), variant);
	}

	// A set's piece: Caramel's if it adds one, else vanilla's family's, else the slab made outside any family; null if none.
	static @Nullable Block piece(Block base, @Nullable BlockFamily caramel, @Nullable BlockFamily vanilla, BlockFamily.Variant variant) {
		Block piece = caramel == null ? null : caramel.get(variant);
		if (piece == null && vanilla != null) {
			piece = vanilla.get(variant);
		}
		if (piece == null && variant == BlockFamily.Variant.SLAB) {
			piece = VANILLA_SLABS.get(base);
		}
		return piece;
	}
}
