package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

public class CaramelLanguageProvider extends FabricLanguageProvider {
	public CaramelLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, "en_us", registriesFuture);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder translations) {
		// C-009: named like vanilla's tools, "Golden" included.
		translations.add(Chisels.FLINT_CHISEL.item(), "Flint Chisel");
		translations.add(Chisels.STONE_CHISEL.item(), "Stone Chisel");
		translations.add(Chisels.COPPER_CHISEL.item(), "Copper Chisel");
		translations.add(Chisels.IRON_CHISEL.item(), "Iron Chisel");
		translations.add(Chisels.GOLDEN_CHISEL.item(), "Golden Chisel");
		translations.add(Chisels.DIAMOND_CHISEL.item(), "Diamond Chisel");
		translations.add(Chisels.NETHERITE_CHISEL.item(), "Netherite Chisel");
		translations.add(Chisels.CHISELS, "Chisels");
		translations.add(Chisels.FLINT_TOOL_MATERIALS, "Flint Tool Materials");
		// The owner's words, in British spelling.
		translations.add(CaramelSoundsProvider.CARVE_STONE_SUBTITLE, "Block chiselled");
		// C-010, by the owner's names. Pieces of the base game's blocks take those blocks' names, cobblestone's its new one
		// (CaramelVanillaNamesProvider): Cobbled Stone.
		translations.add(StoneFamily.STONE_WALL, "Stone Wall");
		translations.add(StoneFamily.COBBLESTONE_BUTTON, "Cobbled Stone Button");
		translations.add(StoneFamily.COBBLESTONE_PRESSURE_PLATE, "Cobbled Stone Pressure Plate");
		translations.add(StoneFamily.MOSSY_COBBLESTONE_BUTTON, "Cobbled Mossy Stone Button");
		translations.add(StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE, "Cobbled Mossy Stone Pressure Plate");
		translations.add(StoneFamily.SMOOTH_STONE_STAIRS, "Smooth Stone Stairs");
		translations.add(StoneFamily.SMOOTH_STONE_WALL, "Smooth Stone Wall");
		translations.add(StoneFamily.SMOOTH_STONE_BUTTON, "Smooth Stone Button");
		translations.add(StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, "Smooth Stone Pressure Plate");
		translations.add(StoneFamily.POLISHED_STONE, "Polished Stone");
		translations.add(StoneFamily.POLISHED_STONE_STAIRS, "Polished Stone Stairs");
		translations.add(StoneFamily.POLISHED_STONE_SLAB, "Polished Stone Slab");
		translations.add(StoneFamily.POLISHED_STONE_WALL, "Polished Stone Wall");
		translations.add(StoneFamily.POLISHED_STONE_BUTTON, "Polished Stone Button");
		translations.add(StoneFamily.POLISHED_STONE_PRESSURE_PLATE, "Polished Stone Pressure Plate");
		translations.add(StoneFamily.CHISELED_STONE_PILLAR, "Chiseled Stone Pillar");
		translations.add(StoneFamily.STONE_TILES, "Stone Tiles");
		translations.add(StoneFamily.CRACKED_STONE_TILES, "Cracked Stone Tiles");
		translations.add(StoneFamily.STONE_TILE_STAIRS, "Stone Tile Stairs");
		translations.add(StoneFamily.STONE_TILE_SLAB, "Stone Tile Slab");
		translations.add(StoneFamily.STONE_TILE_WALL, "Stone Tile Wall");
		translations.add(StoneFamily.STONE_TILE_FENCE, "Stone Tile Fence");
		translations.add(StoneFamily.STONE_BRICK, "Stone Brick");
	}
}
