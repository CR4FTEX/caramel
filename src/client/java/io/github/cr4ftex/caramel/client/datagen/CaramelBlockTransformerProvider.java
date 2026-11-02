package io.github.cr4ftex.caramel.client.datagen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

// What a right click with a tool turns a block into, in the block_transformer registry the game reads its axe, hoe and
// shovel from, encoded with the codec the game loads them with.
public class CaramelBlockTransformerProvider extends FabricCodecDataProvider<BlockTransformer> {
	// The Chisel's cycles, one family per line, in the owner's order (C-010): polished, bricks, cracked bricks, chiseled
	// bricks, chiseled pillar, tiles, cracked tiles. A right click turns a block into the next one of its line, and the
	// last into the first. Nether bricks have no polished block.
	static final List<List<Block>> STONE_CYCLES = List.of(
			List.of(StoneFamily.POLISHED_STONE, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS, StoneFamily.CHISELED_STONE_PILLAR,
					StoneFamily.STONE_TILES, StoneFamily.CRACKED_STONE_TILES),
			List.of(Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CHISELED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES),
			List.of(Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE),
			List.of(Blocks.POLISHED_TUFF, Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.CHISELED_TUFF),
			List.of(Blocks.POLISHED_SULFUR, Blocks.SULFUR_BRICKS, Blocks.CHISELED_SULFUR),
			List.of(Blocks.POLISHED_CINNABAR, Blocks.CINNABAR_BRICKS, Blocks.CHISELED_CINNABAR),
			List.of(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS));
	// C-010: a family's Smooth block comes before its Polished one in the owner's order, and a cycle never goes back to
	// it, so a right click turns it into the Polished block once.
	static final List<Step> STONE_WAYS_IN = List.of(new Step(Blocks.SMOOTH_STONE, StoneFamily.POLISHED_STONE));
	// C-010: scraping moss, one way: a mossy block into the same block without moss, and the Cobbled Mossy Stone set into
	// Cobbled Stone's. Stone Bricks are in a cycle, so a second click carves on from there.
	static final List<Step> MOSS_SCRAPING = List.of(new Step(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE), new Step(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS));
	// Blocks whose state a cycle resets to the default, by the owner's answer: a pillar the Chisel makes stands upright,
	// however the block before it was placed.
	static final List<Block> RESET_TO_DEFAULT = List.of(StoneFamily.CHISELED_STONE_PILLAR);
	// Bamboo planks and bamboo mosaic turn into each other.
	static final List<List<Block>> WOOD_CYCLES = List.of(
			List.of(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC));

	// One rule: the Chisel turns a block into another.
	record Step(Block from, Block to) {
	}

	// The pieces of the cycles (C-010, the owner's addition), derived from the families so that a later cycle or family
	// needs no new line. For each kind of piece, the cycle's members that have one make a loop in the cycle's order, and
	// those without are skipped; a kind with one member or none makes nothing. Each piece keeps its state.
	static final List<Step> STONE_PIECES = pieceLoops(STONE_CYCLES);
	static final List<Step> WOOD_PIECES = pieceLoops(WOOD_CYCLES);
	// A way in leads a piece of the block it starts from into the same kind of piece of the cycle, from the way in's
	// target on: the first member that has one.
	static final List<Step> STONE_PIECES_IN = pieceWaysIn(STONE_CYCLES, STONE_WAYS_IN);
	static final List<Step> MOSS_PIECES = pieceMoss(MOSS_SCRAPING);

	public CaramelBlockTransformerProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, Registries.BLOCK_TRANSFORMER, BlockTransformer.DIRECT_CODEC);
	}

	@Override
	protected void configure(BiConsumer<Identifier, BlockTransformer> provider, HolderLookup.Provider registries) {
		// An item holds one transformer, and vanilla's axe transformer bundles stripping with scraping and removing wax
		// from copper, which the Chisel does not do. So the Chisel's own starts with a copy of the axe's stripping
		// entry, taken from vanilla's data as it is at datagen.
		BlockTransformer axe = registries.lookupOrThrow(Registries.BLOCK_TRANSFORMER).getOrThrow(BlockTransformers.AXE).value();
		List<BlockTransformer.BlockTransformData> stripping = axe.transforms().stream()
				.filter(transform -> transform.sound().value() == SoundEvents.AXE_STRIP.value())
				.toList();
		if (stripping.size() != 1) {
			throw new IllegalStateException("Expected 1 stripping entry in vanilla's axe transformer, found " + stripping.size());
		}
		checkSteps();
		provider.accept(Chisels.TRANSFORMER.identifier(), new BlockTransformer(List.of(
				stripping.getFirst(),
				// The owner's sounds: Caramel's carving sound on stone, the axe's stripping on wood and the game's moss
				// break on moss, which Chisels plays the carving sound beside.
				entry(Stream.of(steps(STONE_CYCLES), STONE_WAYS_IN).flatMap(List::stream).toList(), Stream.of(STONE_PIECES, STONE_PIECES_IN).flatMap(List::stream).toList(), Chisels.CARVE_STONE),
				entry(steps(WOOD_CYCLES), WOOD_PIECES, SoundEvents.AXE_STRIP),
				entry(MOSS_SCRAPING, MOSS_PIECES, Chisels.SCRAPE_MOSS))));
	}

	// One entry whose rules each turn one block into another: a block into the next one's default state, a piece into the
	// next one's with the state it has. A click runs the first rule that matches the clicked block, so it advances one
	// step. Each use costs 1 durability, the builder's default, like stripping.
	private static BlockTransformer.BlockTransformData entry(List<Step> blocks, List<Step> pieces, Holder<SoundEvent> sound) {
		RuleBasedStateProvider.Builder rules = RuleBasedStateProvider.builder();
		blocks.forEach(step -> rules.ifTrueThenProvide(BlockPredicate.matchesBlocks(step.from()), step.to()));
		// The way vanilla's stripping keeps a log's axis: the new block's default state, with the properties both have
		// taken from the block that is there.
		pieces.forEach(step -> rules.ifTrueThenProvide(BlockPredicate.matchesBlocks(step.from()), new CopyPropertiesProvider(step.to())));
		return BlockTransformer.BlockTransformData.builder(rules.build()).sound(sound).build();
	}

	// Each block of a cycle into the next one, and the last into the first.
	private static List<Step> steps(List<List<Block>> cycles) {
		List<Step> steps = new ArrayList<>();
		cycles.forEach(cycle -> loop(steps, cycle));
		return steps;
	}

	private static void loop(List<Step> steps, List<Block> loop) {
		for (int i = 0; i < loop.size(); i++) {
			steps.add(new Step(loop.get(i), loop.get((i + 1) % loop.size())));
		}
	}

	private static List<Step> pieceLoops(List<List<Block>> cycles) {
		List<Step> steps = new ArrayList<>();
		for (List<Block> cycle : cycles) {
			for (BlockFamily.Variant kind : CaramelBlockFamilies.PIECES) {
				List<Block> pieces = cycle.stream().map(block -> CaramelBlockFamilies.piece(block, kind)).filter(Objects::nonNull).toList();
				if (pieces.size() >= 2) {
					loop(steps, pieces);
				}
			}
		}
		return steps;
	}

	private static List<Step> pieceWaysIn(List<List<Block>> cycles, List<Step> waysIn) {
		List<Step> steps = new ArrayList<>();
		for (Step wayIn : waysIn) {
			List<Block> cycle = cycles.stream().filter(candidate -> candidate.contains(wayIn.to())).findFirst().orElseThrow(() -> new IllegalStateException(wayIn.to() + " is in no Chisel cycle"));
			for (BlockFamily.Variant kind : CaramelBlockFamilies.PIECES) {
				Block from = CaramelBlockFamilies.piece(wayIn.from(), kind);
				if (from == null) {
					continue;
				}
				int start = cycle.indexOf(wayIn.to());
				Block to = IntStream.range(0, cycle.size()).mapToObj(i -> CaramelBlockFamilies.piece(cycle.get((start + i) % cycle.size()), kind)).filter(Objects::nonNull).findFirst()
						.orElseThrow(() -> new IllegalStateException(from + " has no " + kind + " to lead into in the cycle of " + wayIn.to()));
				steps.add(new Step(from, to));
			}
		}
		return steps;
	}

	private static List<Step> pieceMoss(List<Step> moss) {
		List<Step> steps = new ArrayList<>();
		for (Step scraping : moss) {
			for (BlockFamily.Variant kind : CaramelBlockFamilies.PIECES) {
				Block from = CaramelBlockFamilies.piece(scraping.from(), kind);
				if (from == null) {
					continue;
				}
				Block to = CaramelBlockFamilies.piece(scraping.to(), kind);
				if (to == null) {
					throw new IllegalStateException(from + " has no " + kind + " of " + scraping.to() + " to be scraped into");
				}
				steps.add(new Step(from, to));
			}
		}
		return steps;
	}

	// A rule matches by the block it starts from, so a block that starts two of them would only ever follow the first.
	// A rule gives a block the next one's default state, which is only right for blocks that have no state to carry over,
	// or that the owner chose to reset; a piece keeps its state, so its next one must have the very same properties. A
	// way in must lead into a cycle from outside every cycle, and a cycle needs 2 blocks. Datagen stops on any of these,
	// so the table gets rechecked.
	private static void checkSteps() {
		for (List<List<Block>> cycles : List.of(STONE_CYCLES, WOOD_CYCLES)) {
			cycles.forEach(cycle -> {
				if (cycle.size() < 2) {
					throw new IllegalStateException("A Chisel cycle needs at least 2 blocks: " + cycle);
				}
			});
		}
		Set<Block> starts = new HashSet<>();
		List<Step> blockSteps = Stream.of(steps(STONE_CYCLES), STONE_WAYS_IN, steps(WOOD_CYCLES), MOSS_SCRAPING).flatMap(List::stream).toList();
		List<Step> pieceSteps = Stream.of(STONE_PIECES, STONE_PIECES_IN, WOOD_PIECES, MOSS_PIECES).flatMap(List::stream).toList();
		Stream.concat(blockSteps.stream(), pieceSteps.stream()).forEach(step -> {
			if (!starts.add(step.from())) {
				throw new IllegalStateException(step.from() + " is in two Chisel cycles or rules");
			}
		});
		blockSteps.forEach(step -> checkNoState(step.from()));
		pieceSteps.forEach(step -> {
			if (!Set.copyOf(step.from().getStateDefinition().getProperties()).equals(Set.copyOf(step.to().getStateDefinition().getProperties()))) {
				throw new IllegalStateException(step.from() + " and " + step.to() + " have different block state properties, which a Chisel change would reset");
			}
		});
		Set<Block> cycleMembers = new HashSet<>();
		Stream.of(STONE_CYCLES, WOOD_CYCLES).flatMap(List::stream).forEach(cycleMembers::addAll);
		// The pieces of a cycle's blocks count as in it, whether or not they make a loop.
		new ArrayList<>(cycleMembers).forEach(block -> CaramelBlockFamilies.PIECES.stream().map(kind -> CaramelBlockFamilies.piece(block, kind)).filter(Objects::nonNull).forEach(cycleMembers::add));
		Stream.concat(STONE_WAYS_IN.stream(), STONE_PIECES_IN.stream()).forEach(wayIn -> {
			if (cycleMembers.contains(wayIn.from()) || !cycleMembers.contains(wayIn.to())) {
				throw new IllegalStateException("A Chisel way in must lead from outside the cycles into one: " + wayIn.from() + " to " + wayIn.to());
			}
		});
	}

	private static void checkNoState(Block block) {
		if (!block.getStateDefinition().getProperties().isEmpty() && !RESET_TO_DEFAULT.contains(block)) {
			throw new IllegalStateException(block + " has block state properties, which a Chisel cycle would reset");
		}
	}

	@Override
	public String getName() {
		return "Caramel block transformers";
	}
}
