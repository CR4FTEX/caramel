package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.cr4ftex.caramel.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

// What blocks drop. A table at a vanilla block's id, such as data/minecraft/loot_table/blocks/stone_stairs.json,
// replaces vanilla's.
public class CaramelBlockLootProvider extends FabricBlockLootSubProvider {
	public CaramelBlockLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generate() {
		// C-010: each new block drops itself, and a double slab two, as vanilla's own do; the stone wall is the one
		// exception, below.
		for (Block block : StoneFamily.BLOCKS) {
			if (block instanceof SlabBlock) {
				add(block, createSlabItemTable(block));
			} else if (block != StoneFamily.STONE_WALL) {
				dropSelf(block);
			}
		}
		// C-010: Stone and every block of its set drop their Cobbled Stone counterpart, and themselves with Silk Touch,
		// as Stone already drops Cobblestone.
		add(StoneFamily.STONE_WALL, createSingleItemTableWithSilkTouch(StoneFamily.STONE_WALL, Blocks.COBBLESTONE_WALL));
		add(Blocks.STONE_STAIRS, createSingleItemTableWithSilkTouch(Blocks.STONE_STAIRS, Blocks.COBBLESTONE_STAIRS));
		add(Blocks.STONE_BUTTON, createSingleItemTableWithSilkTouch(Blocks.STONE_BUTTON, StoneFamily.COBBLESTONE_BUTTON));
		add(Blocks.STONE_PRESSURE_PLATE, createSingleItemTableWithSilkTouch(Blocks.STONE_PRESSURE_PLATE, StoneFamily.COBBLESTONE_PRESSURE_PLATE));
		add(Blocks.STONE_SLAB, slabWithSilkTouch(Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB));
	}

	// Every table with vanilla's random sequence, named after the table, which vanilla's own block loot sets and
	// Fabric's provider does not.
	@Override
	public void add(Block block, LootTable.Builder builder) {
		super.add(block, builder.setRandomSequence(block.getLootTable().orElseThrow().identifier()));
	}

	// createSlabItemTable with createSilkTouchDispatchTable's choice: a double slab gives two either way, the slab
	// itself with Silk Touch and the other slab without.
	private LootTable.Builder slabWithSilkTouch(Block slab, Block otherwise) {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ContextIntProviders.exactly(1))
						.add(applyExplosionDecay(slab, LootItem.lootTableItem(slab).apply(twoWhenDouble(slab)))
								.when(hasSilkTouch())
								.otherwise(applyExplosionDecay(slab, LootItem.lootTableItem(otherwise).apply(twoWhenDouble(slab))))));
	}

	private LootItemFunction.Builder twoWhenDouble(Block slab) {
		return SetItemCountFunction.setCount(ContextIntProviders.exactly(2))
				.when(MatchBlock.blockMatches(blocks, slab, StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)));
	}

	@Override
	public String getName() {
		return "Caramel block loot tables";
	}
}
