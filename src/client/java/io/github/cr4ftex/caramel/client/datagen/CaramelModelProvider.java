package io.github.cr4ftex.caramel.client.datagen;

import java.util.List;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// Blockstates, block and item models, and item definitions. They only name a vanilla parent and a texture: the textures
// themselves come from the owner or an artist, and a block or item whose texture file does not exist yet shows the
// game's missing-texture checkerboard.
public class CaramelModelProvider extends FabricModelProvider {
	// C-010: the new texture of the base game's Smooth Stone, by the owner's choice.
	private static final Material SMOOTH_STONE = new Material(Caramel.id("block/smooth_stone"));

	public CaramelModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockModels) {
		// C-010. A new piece of a vanilla block takes that block's vanilla texture, such as minecraft:block/stone for the
		// stone wall. The provider starts from the texture alone: family(block) would also write a blockstate and a model
		// for the vanilla block itself, into the minecraft namespace, over vanilla's own.
		for (BlockFamily family : List.of(CaramelBlockFamilies.STONE, CaramelBlockFamilies.COBBLESTONE, CaramelBlockFamilies.MOSSY_COBBLESTONE)) {
			blockModels.new BlockFamilyProvider(TextureMapping.cube(family.getBaseBlock())).generateFor(family);
		}
		// New blocks, textures/block/<id>.png: stone_tiles and cracked_stone_tiles.
		blockModels.family(StoneFamily.STONE_TILES).generateFor(CaramelBlockFamilies.STONE_TILES);
		// The base game's Smooth Stone takes Caramel's new texture, textures/block/smooth_stone.png, through Caramel's
		// copy of its four models at their vanilla ids, the slab's side and double slab included. Vanilla's texture files
		// stay as they are, so Polished Stone, below, keeps the old look by naming them. Smooth Stone's new stairs, wall,
		// button and pressure plate take the new texture too.
		TextureMapping newLook = TextureMapping.cube(SMOOTH_STONE);
		ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, newLook, blockModels.modelOutput);
		// Only the models: vanilla's own blockstate already names them.
		smoothStoneSlabModels(blockModels, Blocks.SMOOTH_STONE_SLAB, TextureMapping.column(SMOOTH_STONE, SMOOTH_STONE));
		blockModels.new BlockFamilyProvider(newLook).generateFor(CaramelBlockFamilies.SMOOTH_STONE);
		// The new Polished Stone, in vanilla's Smooth Stone textures. Its slab and, by the owner's playtest suggestion, its
		// stairs wear the lined side and plain top of vanilla's smooth stone slab (BlockModelGenerators.createSmoothStoneSlab).
		TextureMapping oldLook = TextureMapping.cube(Blocks.SMOOTH_STONE);
		TextureMapping slabLook = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), oldLook.get(TextureSlot.TOP));
		blockModels.new BlockFamilyProvider(oldLook).fullBlock(StoneFamily.POLISHED_STONE, ModelTemplates.CUBE_ALL)
				.wall(StoneFamily.POLISHED_STONE_WALL)
				.button(StoneFamily.POLISHED_STONE_BUTTON)
				.pressurePlate(StoneFamily.POLISHED_STONE_PRESSURE_PLATE);
		blockModels.new BlockFamilyProvider(slabLook).stairs(StoneFamily.POLISHED_STONE_STAIRS);
		blockModels.blockStateOutput.accept(smoothStoneSlabModels(blockModels, StoneFamily.POLISHED_STONE_SLAB, slabLook));
		// A pillar like a log: chiseled_stone_pillar_side.png and chiseled_stone_pillar_top.png, with its own model
		// when lying on its side.
		blockModels.createRotatedPillarWithHorizontalVariant(StoneFamily.CHISELED_STONE_PILLAR, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
	}

	// A slab's bottom, top and double models made as vanilla makes the smooth stone slab's; the blockstate that names them.
	private static BlockModelDefinitionGenerator smoothStoneSlabModels(BlockModelGenerators blockModels, Block slab, TextureMapping textures) {
		Identifier bottom = ModelTemplates.SLAB_BOTTOM.create(slab, textures, blockModels.modelOutput);
		Identifier top = ModelTemplates.SLAB_TOP.create(slab, textures, blockModels.modelOutput);
		Identifier doubleSlab = ModelTemplates.CUBE_COLUMN.createWithOverride(slab, "_double", textures, blockModels.modelOutput);
		if (slab != Blocks.SMOOTH_STONE_SLAB) {
			blockModels.registerSimpleItemModel(slab, bottom);
		}
		return BlockModelGenerators.createSlab(slab, BlockModelGenerators.plainVariant(bottom), BlockModelGenerators.plainVariant(top),
				BlockModelGenerators.plainVariant(doubleSlab));
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModels) {
		// C-009: held like vanilla's tools, textures/item/<tier>_chisel.png.
		Chisels.ALL.forEach(chisel -> itemModels.generateFlatItem(chisel.item(), ModelTemplates.FLAT_HANDHELD_ITEM));
		// C-010: textures/item/stone_brick.png, flat like vanilla's brick.
		itemModels.generateFlatItem(StoneFamily.STONE_BRICK, ModelTemplates.FLAT_ITEM);
	}

	@Override
	public String getName() {
		return "Caramel models";
	}
}
