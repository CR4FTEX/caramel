package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.CarvingParticles;
import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// C-010, the owner's additions of 2026-11-01: the Chisel changes the pieces of a set (stairs, slabs, walls, buttons and
// pressure plates) as it changes the blocks, and scrapes moss off. The expected blocks are written out here from the
// owner's rules in docs/DESIGN.md and from the base game's blocks, never from Caramel's data. A click is made through
// the game's own ServerPlayerGameMode.useItemOn, with a player that has a (disconnected) connection, so that the rule
// that a block's own right click comes first, and Fabric's block-click event, are the game's.
public class ChiselPiecesGameTest {
	private static final String IDEA = "C-010";
	private static final BlockPos POS = new BlockPos(1, 1, 1);
	// A button stays pressed for 20 ticks if it is made of stone (ButtonBlock), a plate waits 20 (BasePressurePlateBlock).
	private static final int PRESSED_TICKS = 20;

	// Each line is a loop: a click turns a piece into the next of its line, and the last into the first. The first three
	// of each family are the Polished, Brick and Tile pieces (the Tiles only in stone), skipping Cracked and Chiseled,
	// which have no pieces. Nether Bricks, and any button or plate, are in no loop: only one block of their line has them.
	static final List<List<Block>> PIECE_LOOPS = List.of(
			List.of(StoneFamily.POLISHED_STONE_STAIRS, Blocks.STONE_BRICK_STAIRS, StoneFamily.STONE_TILE_STAIRS),
			List.of(StoneFamily.POLISHED_STONE_SLAB, Blocks.STONE_BRICK_SLAB, StoneFamily.STONE_TILE_SLAB),
			List.of(StoneFamily.POLISHED_STONE_WALL, Blocks.STONE_BRICK_WALL, StoneFamily.STONE_TILE_WALL),
			List.of(Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_TILE_STAIRS),
			List.of(Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_TILE_SLAB),
			List.of(Blocks.POLISHED_DEEPSLATE_WALL, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_TILE_WALL),
			List.of(Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS),
			List.of(Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB),
			List.of(Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL),
			List.of(Blocks.POLISHED_TUFF_STAIRS, Blocks.TUFF_BRICK_STAIRS),
			List.of(Blocks.POLISHED_TUFF_SLAB, Blocks.TUFF_BRICK_SLAB),
			List.of(Blocks.POLISHED_TUFF_WALL, Blocks.TUFF_BRICK_WALL),
			List.of(Blocks.POLISHED_SULFUR_STAIRS, Blocks.SULFUR_BRICK_STAIRS),
			List.of(Blocks.POLISHED_SULFUR_SLAB, Blocks.SULFUR_BRICK_SLAB),
			List.of(Blocks.POLISHED_SULFUR_WALL, Blocks.SULFUR_BRICK_WALL),
			List.of(Blocks.POLISHED_CINNABAR_STAIRS, Blocks.CINNABAR_BRICK_STAIRS),
			List.of(Blocks.POLISHED_CINNABAR_SLAB, Blocks.CINNABAR_BRICK_SLAB),
			List.of(Blocks.POLISHED_CINNABAR_WALL, Blocks.CINNABAR_BRICK_WALL));
	// Bamboo is wood: it plays the axe's stripping sound.
	static final List<List<Block>> WOOD_PIECE_LOOPS = List.of(
			List.of(Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_MOSAIC_STAIRS),
			List.of(Blocks.BAMBOO_SLAB, Blocks.BAMBOO_MOSAIC_SLAB));
	static final List<Block> WOOD_PIECES = WOOD_PIECE_LOOPS.stream().flatMap(List::stream).toList();
	// Smooth Stone's five pieces go one way into Polished Stone's.
	static final Map<Block, Block> PIECE_WAYS_IN = Map.of(
			StoneFamily.SMOOTH_STONE_STAIRS, StoneFamily.POLISHED_STONE_STAIRS,
			Blocks.SMOOTH_STONE_SLAB, StoneFamily.POLISHED_STONE_SLAB,
			StoneFamily.SMOOTH_STONE_WALL, StoneFamily.POLISHED_STONE_WALL,
			StoneFamily.SMOOTH_STONE_BUTTON, StoneFamily.POLISHED_STONE_BUTTON,
			StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, StoneFamily.POLISHED_STONE_PRESSURE_PLATE);
	// Scraping moss, one way: ten rules, two of them for blocks.
	static final Map<Block, Block> MOSS_SCRAPING_BLOCKS = Map.of(
			Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE,
			Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS);
	static final Map<Block, Block> MOSS_SCRAPING_PIECES = Map.of(
			Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS,
			Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB,
			Blocks.MOSSY_COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL,
			StoneFamily.MOSSY_COBBLESTONE_BUTTON, StoneFamily.COBBLESTONE_BUTTON,
			StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE, StoneFamily.COBBLESTONE_PRESSURE_PLATE,
			Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS,
			Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB,
			Blocks.MOSSY_STONE_BRICK_WALL, Blocks.STONE_BRICK_WALL);
	static final Map<Block, Block> MOSS_SCRAPING = joined(MOSS_SCRAPING_BLOCKS, MOSS_SCRAPING_PIECES);
	// Pieces with no partner in their line: a Chisel leaves them alone.
	static final List<Block> NO_PARTNER = List.of(
			Blocks.POLISHED_BLACKSTONE_BUTTON, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICK_WALL,
			Blocks.NETHER_BRICK_FENCE, StoneFamily.POLISHED_STONE_BUTTON, StoneFamily.POLISHED_STONE_PRESSURE_PLATE, StoneFamily.STONE_TILE_FENCE, Blocks.BAMBOO_BUTTON,
			Blocks.BAMBOO_PRESSURE_PLATE, Blocks.BAMBOO_FENCE, Blocks.STONE_STAIRS, Blocks.STONE_SLAB, StoneFamily.STONE_WALL, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE,
			Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE_WALL, StoneFamily.COBBLESTONE_BUTTON, StoneFamily.COBBLESTONE_PRESSURE_PLATE);

	// Looked up when a test runs: whether the Chisels are registered when this class loads depends on the mod order.
	private static Item ironChisel() {
		return ChiselGameTest.TIERS.get(3).chisel();
	}

	record Step(Block from, Block to) {
	}

	private static Map<Block, Block> joined(Map<Block, Block> first, Map<Block, Block> second) {
		Map<Block, Block> all = new HashMap<>(first);
		all.putAll(second);
		return Map.copyOf(all);
	}

	// Every piece step: the loops, the way in and the moss, in a fixed order.
	static Map<Block, Block> nextPiece() {
		Map<Block, Block> next = new LinkedHashMap<>();
		for (List<Block> loop : Stream.concat(PIECE_LOOPS.stream(), WOOD_PIECE_LOOPS.stream()).toList()) {
			for (int i = 0; i < loop.size(); i++) {
				next.put(loop.get(i), loop.get((i + 1) % loop.size()));
			}
		}
		next.putAll(PIECE_WAYS_IN);
		next.putAll(MOSS_SCRAPING_PIECES);
		return next;
	}

	private static List<Step> everyStep() {
		List<Step> steps = new ArrayList<>(nextPiece().entrySet().stream().map(entry -> new Step(entry.getKey(), entry.getValue())).toList());
		MOSS_SCRAPING_BLOCKS.forEach((from, to) -> steps.add(new Step(from, to)));
		return steps;
	}

	// The owner's examples: a Polished Stone Wall becomes a Stone Brick Wall; Polished Stone Stairs become Stone Brick
	// Stairs, then Stone Tile Stairs (skipping Cracked and Chiseled, which have no stairs), and back to Polished.
	@GameTest
	public void theOwnersExamplesOfPieces(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		List<Block> expectedStairs = List.of(Blocks.STONE_BRICK_STAIRS, StoneFamily.STONE_TILE_STAIRS, StoneFamily.POLISHED_STONE_STAIRS, Blocks.STONE_BRICK_STAIRS);
		helper.setBlock(POS, StoneFamily.POLISHED_STONE_WALL);
		checked++;
		click(helper, player(helper, ironChisel(), false));
		if (!helper.getBlockState(POS).is(Blocks.STONE_BRICK_WALL)) {
			failures.add("A Chisel turns a Polished Stone Wall into " + helper.getBlockState(POS) + ", expected a Stone Brick Wall");
		}
		helper.setBlock(POS, StoneFamily.POLISHED_STONE_STAIRS);
		for (Block expected : expectedStairs) {
			checked++;
			click(helper, player(helper, ironChisel(), false));
			if (!helper.getBlockState(POS).is(expected)) {
				failures.add("A Chisel turns the stairs into " + helper.getBlockState(POS) + ", expected " + BuiltInRegistries.BLOCK.getKey(expected));
				break;
			}
		}
		helper.setBlock(POS, Blocks.AIR);
		report(helper, IDEA, "owner's piece example checks", checked, failures);
	}

	// Every loop, every step, in order, from each piece to the next: a piece keeps its state (a stairs' facing, half,
	// shape and waterlogging, a slab's type, a wall's sides and waterlogging), costs 1 durability and starts the wait; a
	// loop comes back to where it began. Every way in and every moss rule does the same once. Sneaking, so that a
	// button's own right click is out of the way.
	@GameTest
	public void piecesChangeOneStepAClickKeepingTheirState(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Step step : everyStep()) {
			for (BlockState state : prepare(level, pos, step.from(), failures)) {
				checked++;
				level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				ServerPlayer player = player(helper, ironChisel(), true);
				click(helper, player);
				BlockState after = level.getBlockState(pos);
				String what = "A Chisel on " + state + " (" + name(step.from().asItem()) + ")";
				if (!after.is(step.to())) {
					failures.add(what + " gives " + after + ", expected " + BuiltInRegistries.BLOCK.getKey(step.to()));
				} else if (!keepsState(state, after)) {
					failures.add(what + " gives " + after + ", which lost some of its state");
				} else if (player.getMainHandItem().getDamageValue() != 1 || !player.getCooldowns().isOnCooldown(player.getMainHandItem())) {
					failures.add(what + " costs " + player.getMainHandItem().getDamageValue() + " durability and "
							+ (player.getCooldowns().isOnCooldown(player.getMainHandItem()) ? "starts" : "does not start") + " the wait, expected 1 and the wait");
				}
			}
		}
		// Every loop all the way round.
		for (List<Block> loop : Stream.concat(PIECE_LOOPS.stream(), WOOD_PIECE_LOOPS.stream()).toList()) {
			checked++;
			List<BlockState> states = prepare(level, pos, loop.getFirst(), failures);
			level.setBlock(pos, states.getFirst(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			for (int click = 1; click <= loop.size(); click++) {
				click(helper, player(helper, ironChisel(), true));
			}
			if (!level.getBlockState(pos).equals(states.getFirst())) {
				failures.add(loop.size() + " clicks on " + states.getFirst() + " give " + level.getBlockState(pos) + ", expected it back");
			}
		}
		// Every tier of Chisel.
		for (ChiselGameTest.Tier tier : ChiselGameTest.TIERS) {
			checked++;
			helper.setBlock(POS, StoneFamily.POLISHED_STONE_WALL);
			click(helper, player(helper, tier.chisel(), false));
			if (!helper.getBlockState(POS).is(Blocks.STONE_BRICK_WALL)) {
				failures.add("A " + name(tier.chisel()) + " turns a Polished Stone Wall into " + helper.getBlockState(POS));
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "piece step checks", checked, failures);
	}

	// Pieces with no partner stay as they are, at no cost; the Smooth pieces and the mossy ones are never a result of
	// any step, so a Chisel never goes back to them; and Cobbled Stone and its pieces do not change further.
	@GameTest
	public void piecesWithNoPartnerAndTheOneWaysStayPut(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Block block : NO_PARTNER) {
			for (BlockState state : prepare(level, pos, block, failures)) {
				checked++;
				level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				ServerPlayer player = player(helper, ironChisel(), true);
				click(helper, player);
				if (!level.getBlockState(pos).equals(state) || player.getMainHandItem().getDamageValue() != 0) {
					failures.add("A Chisel turns " + state + " into " + level.getBlockState(pos) + " at " + player.getMainHandItem().getDamageValue() + " wear, expected no change");
				}
			}
		}
		Map<Block, Block> next = nextPiece();
		next.putAll(MOSS_SCRAPING_BLOCKS);
		for (Block oneWay : Stream.concat(PIECE_WAYS_IN.keySet().stream(), MOSS_SCRAPING.keySet().stream()).toList()) {
			checked++;
			if (next.containsValue(oneWay)) {
				failures.add(name(oneWay.asItem()) + " is the result of a step, but a Chisel never goes back to it");
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "piece standstill checks", checked, failures);
	}

	// The game runs a button's own right click before the item's: a plain click presses it, with no change, no particles,
	// no sound of the Chisel's, no wear and no wait, and a sneaking one changes it. A plate has no right click and a plain
	// click changes it. Through the game's own useItemOn, for the buttons and plates a Chisel changes.
	@GameTest
	public void aButtonPressesOnAPlainClickAndChangesWhenSneaking(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		BlockHitResult hit = hit(pos);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		Map<Block, Block> buttonsAndPlates = new LinkedHashMap<>();
		nextPiece().forEach((from, to) -> {
			if (from instanceof ButtonBlock || from.defaultBlockState().hasProperty(BlockStateProperties.POWERED)) {
				buttonsAndPlates.put(from, to);
			}
		});
		for (Map.Entry<Block, Block> change : buttonsAndPlates.entrySet()) {
			boolean button = change.getKey() instanceof ButtonBlock;
			boolean moss = MOSS_SCRAPING.containsKey(change.getKey());
			String what = name(change.getKey().asItem());
			for (boolean sneaking : List.of(false, true)) {
				checked++;
				BlockState state = prepare(level, pos, change.getKey(), failures).getFirst();
				level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				ServerPlayer player = player(helper, ironChisel(), sneaking);
				boolean changes = sneaking || !button;
				Optional<CarvingParticles.Burst> burst = CarvingParticles.burst(player, level, InteractionHand.MAIN_HAND, hit);
				Optional<Holder<SoundEvent>> extra = Chisels.extraSound(player, level, InteractionHand.MAIN_HAND, hit);
				click(helper, player);
				BlockState after = level.getBlockState(pos);
				ItemStack chisel = player.getMainHandItem();
				if (burst.isPresent() != changes || extra.isPresent() != (changes && moss)) {
					failures.add("A " + (sneaking ? "sneaking" : "plain") + " click on " + what + " is told to throw " + (burst.isPresent() ? "" : "no ") + "particles and play "
							+ (extra.isPresent() ? "an extra" : "no extra") + " sound, expected " + (changes ? "particles" : "none") + " and " + (changes && moss ? "the extra sound" : "none"));
				}
				if (changes) {
					if (!after.is(change.getValue()) || chisel.getDamageValue() != 1 || after.getValue(BlockStateProperties.POWERED)) {
						failures.add("A " + (sneaking ? "sneaking" : "plain") + " click on " + what + " gives " + after + " at " + chisel.getDamageValue() + " wear, expected the change at 1");
					}
				} else if (!after.is(change.getKey()) || !after.getValue(BlockStateProperties.POWERED) || chisel.getDamageValue() != 0 || player.getCooldowns().isOnCooldown(chisel)) {
					failures.add("A plain click on " + what + " gives " + after + " at " + chisel.getDamageValue() + " wear" + (player.getCooldowns().isOnCooldown(chisel) ? " and a wait" : "")
							+ ", expected it pressed, with no change, no wear and no wait");
				}
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "button and plate click checks", checked, failures);
	}

	// A waiting Chisel holds a click on a piece it would change (FAIL), and leaves a plain click on a button to the game
	// (PASS), which presses it.
	@GameTest
	public void aWaitingChiselHoldsTheClickOnPiecesItWouldChange(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		BlockHitResult hit = hit(pos);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Map.Entry<Block, Block> change : nextPiece().entrySet()) {
			boolean button = change.getKey() instanceof ButtonBlock;
			for (boolean sneaking : List.of(false, true)) {
				checked++;
				level.setBlock(pos, prepare(level, pos, change.getKey(), failures).getFirst(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				Player player = player(helper, ironChisel(), sneaking);
				player.getCooldowns().addCooldown(player.getMainHandItem(), 20);
				InteractionResult result = UseBlockCallback.EVENT.invoker().interact(player, level, InteractionHand.MAIN_HAND, hit);
				InteractionResult expected = button && !sneaking ? InteractionResult.PASS : InteractionResult.FAIL;
				if (result != expected || Chisels.extraSound(player, level, InteractionHand.MAIN_HAND, hit).isPresent() || CarvingParticles.throwsParticles(player, level, InteractionHand.MAIN_HAND, hit)) {
					failures.add("A waiting Chisel " + (sneaking ? "sneaking " : "") + "on " + name(change.getKey().asItem()) + " gives " + result + " (or particles or a sound), expected " + expected + " with neither");
				}
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "waiting Chisel piece checks", checked, failures);
	}

	// Moss plays both sounds: its entry names the game's moss break, and Chisels adds the carving sound, once for the
	// click (a transformer plays one sound, and an event picks one file of its own). Any other change plays its entry's
	// sound alone. Neither plays while the Chisel waits, on a refused click, or when a plain click presses a button.
	@GameTest
	public void scrapingMossSoundsLikeMossAndStone(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		BlockHitResult hit = hit(pos);
		SoundEvent carve = BuiltInRegistries.SOUND_EVENT.getValue(Caramel.id("item.chisel.carve_stone"));
		List<String> failures = new ArrayList<>();
		int checked = 0;
		List<Block> everyChange = new ArrayList<>(nextPiece().keySet());
		everyChange.addAll(MOSS_SCRAPING_BLOCKS.keySet());
		everyChange.addAll(ChiselGameTest.CYCLES.stream().flatMap(List::stream).toList());
		everyChange.addAll(ChiselGameTest.WAYS_IN.keySet());
		everyChange.add(Blocks.OAK_LOG);
		for (Block block : everyChange) {
			checked++;
			level.setBlock(pos, prepare(level, pos, block, failures).getFirst(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			Player player = player(helper, ironChisel(), true);
			Optional<BlockTransformer.BlockTransformData> entry = player.getMainHandItem().get(net.minecraft.core.component.DataComponents.BLOCK_TRANSFORMER).value().transforms().stream()
					.filter(transform -> transform.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos) != null).findFirst();
			Optional<Holder<SoundEvent>> extra = Chisels.extraSound(player, level, InteractionHand.MAIN_HAND, hit);
			boolean moss = MOSS_SCRAPING.containsKey(block);
			if (entry.isEmpty() || (entry.get().sound().value() == SoundEvents.MOSS_BREAK) != moss) {
				failures.add("A Chisel on " + name(block.asItem()) + " plays " + entry.map(e -> e.sound().value().location().toString()).orElse("nothing") + (moss ? ", expected minecraft:block.moss.break" : ", not the moss one"));
			}
			if (extra.isPresent() != moss || moss && extra.get().value() != carve) {
				failures.add("A Chisel on " + name(block.asItem()) + " plays the extra sound " + extra.map(h -> h.value().location().toString()).orElse("none") + ", expected "
						+ (moss ? "caramel:item.chisel.carve_stone" : "none"));
			}
			if (moss) {
				// Not while it waits.
				checked++;
				player.getCooldowns().addCooldown(player.getMainHandItem(), 20);
				if (Chisels.extraSound(player, level, InteractionHand.MAIN_HAND, hit).isPresent()) {
					failures.add("A waiting Chisel plays the extra sound on " + name(block.asItem()));
				}
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "moss sound checks", checked, failures);
	}

	// What the players are sent for a real click, so through the listeners themselves. Another player in range hears the
	// click's sounds once each. The clicking one is sent none, since its client plays them itself (ServerLevel.playSeededSound
	// leaves it out; ClientLevel plays only for it). Both get one burst of particles, of moss for moss, and nothing at all
	// for a press or a waiting Chisel.
	@GameTest
	public void aClickSendsItsSoundsAndParticlesToPlayers(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		SoundEvent carve = BuiltInRegistries.SOUND_EVENT.getValue(Caramel.id("item.chisel.carve_stone"));
		BlockState moss = Blocks.MOSS_BLOCK.defaultBlockState();
		record Click(Block block, boolean sneaking, boolean waiting, List<SoundEvent> sounds, BlockState particles, int count) {
		}
		List<Click> clicks = List.of(
				new Click(Blocks.MOSSY_COBBLESTONE, false, false, List.of(carve, SoundEvents.MOSS_BREAK), moss, 16),
				new Click(Blocks.MOSSY_STONE_BRICK_SLAB, false, false, List.of(carve, SoundEvents.MOSS_BREAK), moss, 8),
				new Click(StoneFamily.MOSSY_COBBLESTONE_BUTTON, true, false, List.of(carve, SoundEvents.MOSS_BREAK), moss, 2),
				new Click(StoneFamily.POLISHED_STONE, false, false, List.of(carve), StoneFamily.POLISHED_STONE.defaultBlockState(), 16),
				new Click(StoneFamily.POLISHED_STONE_WALL, false, false, List.of(carve), null, 0),
				new Click(Blocks.OAK_LOG, false, false, List.of(SoundEvents.AXE_STRIP.value()), Blocks.OAK_LOG.defaultBlockState(), 16),
				new Click(StoneFamily.MOSSY_COBBLESTONE_BUTTON, false, false, List.of(), null, 0),
				new Click(Blocks.MOSSY_COBBLESTONE, false, true, List.of(), null, 0));
		List<String> failures = new ArrayList<>();
		int checked = 0;
		List<Packet<?>> toClicker = new ArrayList<>();
		List<Packet<?>> toOther = new ArrayList<>();
		ServerPlayer clicker = playerInLevel(helper, pos, toClicker);
		ServerPlayer other = playerInLevel(helper, pos, toOther);
		try {
			for (Click click : clicks) {
				BlockState state = prepare(level, pos, click.block(), failures).getFirst();
				level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				clicker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ironChisel()));
				clicker.setShiftKeyDown(click.sneaking());
				clicker.getCooldowns().removeCooldown(Chisels.COOLDOWN_GROUP);
				if (click.waiting()) {
					clicker.getCooldowns().addCooldown(clicker.getMainHandItem(), 20);
				}
				toClicker.clear();
				toOther.clear();
				click(helper, clicker);
				String named = "A " + (click.waiting() ? "waiting " : "") + (click.sneaking() ? "sneaking " : "") + "click on " + name(click.block().asItem());
				boolean changes = !click.sounds().isEmpty();
				List<SoundEvent> others = toOther.stream().filter(ClientboundSoundPacket.class::isInstance).map(packet -> ((ClientboundSoundPacket) packet).getSound().value())
						.filter(sound -> sound == carve || sound == SoundEvents.MOSS_BREAK || sound == SoundEvents.AXE_STRIP.value()).toList();
				checked++;
				if (others.size() != click.sounds().size() || !others.containsAll(click.sounds())) {
					failures.add(named + " sends another player the sounds " + others + ", expected " + click.sounds());
				}
				checked++;
				List<SoundEvent> own = toClicker.stream().filter(ClientboundSoundPacket.class::isInstance).map(packet -> ((ClientboundSoundPacket) packet).getSound().value())
						.filter(sound -> sound == carve || sound == SoundEvents.MOSS_BREAK || sound == SoundEvents.AXE_STRIP.value()).toList();
				if (!own.isEmpty()) {
					failures.add(named + " sends the clicking player the sounds " + own + ", which its client plays itself");
				}
				for (List<Packet<?>> packets : List.of(toClicker, toOther)) {
					List<ClientboundLevelParticlesPacket> bursts = packets.stream().filter(ClientboundLevelParticlesPacket.class::isInstance).map(ClientboundLevelParticlesPacket.class::cast).toList();
					checked++;
					if (bursts.size() != (changes ? 1 : 0)) {
						failures.add(named + " sends " + bursts.size() + " bursts of particles to a player, expected " + (changes ? 1 : 0));
					} else if (changes && click.particles() != null) {
						BlockParticleOption particle = (BlockParticleOption) bursts.getFirst().particle();
						if (!particle.getState().equals(click.particles()) || bursts.getFirst().count() != click.count()) {
							failures.add(named + " sends " + bursts.getFirst().count() + " particles of " + particle.getState() + ", expected " + click.count() + " of " + click.particles());
						}
					}
				}
			}
		} finally {
			level.getServer().getPlayerList().remove(clicker);
			level.getServer().getPlayerList().remove(other);
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "sent sound and particle checks", checked, failures);
	}

	// A player in the level's player list, beside the block, whose packets are kept: only those are sent sounds and
	// particles. The test takes it out again in the same tick, so nothing else meets it.
	@SuppressWarnings("removal")
	private static ServerPlayer playerInLevel(GameTestHelper helper, BlockPos pos, List<Packet<?>> packets) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();
		player.setGameMode(GameType.SURVIVAL);
		player.snapTo(pos.getX() + 0.5, pos.getY() + 3.0, pos.getZ() + 0.5);
		new ServerGamePacketListenerImpl(helper.getLevel().getServer(), new Connection(PacketFlow.SERVERBOUND), player, CommonListenerCookie.createInitial(player.getGameProfile(), false)) {
			@Override
			public void send(Packet<?> packet) {
				packets.add(packet);
			}
		};
		return player;
	}

	// An axe keeps the game's behaviour on powered copper, which the Chisel's refusal of pressed pieces must not touch: it
	// scrapes a powered bulb and lightning rod, and a plain click opens a powered copper door, whichever the game's own
	// click would do. The expected blocks are the ones vanilla's axe.json names.
	@GameTest
	public void anAxeStillScrapesPoweredCopper(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		Map<Block, Block> scraped = Map.of(vanillaBlock("weathered_copper_bulb"), vanillaBlock("exposed_copper_bulb"), vanillaBlock("weathered_lightning_rod"), vanillaBlock("exposed_lightning_rod"));
		for (Map.Entry<Block, Block> copper : scraped.entrySet()) {
			checked++;
			prepare(level, pos, Blocks.STONE, failures);
			BlockState powered = copper.getKey().defaultBlockState().setValue(BlockStateProperties.POWERED, true);
			level.setBlock(pos, powered, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			click(helper, player(helper, Items.IRON_AXE, false));
			if (!level.getBlockState(pos).is(copper.getValue())) {
				failures.add("An axe on a powered " + name(copper.getKey().asItem()) + " gives " + level.getBlockState(pos) + ", expected " + name(copper.getValue().asItem()));
			}
		}
		checked++;
		prepare(level, pos, Blocks.STONE, failures);
		level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		BlockState door = vanillaBlock("weathered_copper_door").defaultBlockState().setValue(BlockStateProperties.POWERED, true);
		level.setBlock(pos, door, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		level.setBlock(pos.above(), door.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		click(helper, player(helper, Items.IRON_AXE, false));
		if (!level.getBlockState(pos).getValue(BlockStateProperties.OPEN)) {
			failures.add("A plain click with an axe on a powered copper door leaves it " + level.getBlockState(pos) + ", expected it to open");
		}
		prepare(level, pos, Blocks.STONE, failures);
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		report(helper, IDEA, "powered copper axe checks", checked, failures);
	}

	private static Block vanillaBlock(String name) {
		return BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(name));
	}

	// No block a Chisel can change has a right click of its own, which the game would run first, except buttons: the
	// block-click listeners treat those as pressed (RightClickChanges). A later kind of piece with one would otherwise
	// get particles and a sound for a change that never happens, silently. A block has one when its class declares
	// useWithoutItem or useItemOn below BlockBehaviour, where the game's default is a pass.
	@GameTest
	public void noBlockAChiselChangesHasARightClickOfItsOwnButButtons(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(POS);
		BlockTransformer chisel = level.registryAccess().lookupOrThrow(Registries.BLOCK_TRANSFORMER).getOrThrow(ChiselGameTest.CHISEL_TRANSFORMER).value();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int buttons = 0;
		for (Block block : BuiltInRegistries.BLOCK) {
			level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			if (ChiselGameTest.transformed(chisel, entry -> true, level, pos) == null) {
				continue;
			}
			checked++;
			boolean own = declaresRightClick(block.getClass());
			if (block instanceof ButtonBlock) {
				buttons++;
				if (!own) {
					failures.add(name(block.asItem()) + " is a button but declares no right click, so the check cannot see a right click");
				}
			} else if (own) {
				failures.add(name(block.asItem()) + " can be changed by a Chisel and has a right click of its own (" + block.getClass().getName() + ")");
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		if (buttons == 0) {
			failures.add("No button is changeable by a Chisel, so the check proves nothing about them");
		}
		report(helper, IDEA, "changeable blocks' right-click checks", checked, failures);
	}

	private static boolean declaresRightClick(Class<?> type) {
		for (Class<?> c = type; c != null && c != BlockBehaviour.class; c = c.getSuperclass()) {
			for (Method method : c.getDeclaredMethods()) {
				if (method.getName().equals("useWithoutItem") || method.getName().equals("useItemOn")) {
					return true;
				}
			}
		}
		return false;
	}

	// The Chisel copies a pressed button's or plate's state whole, but the tick that releases it is scheduled for the block
	// it was, and the game runs a scheduled tick only while the position still holds that block. So what the transformer
	// alone changes while pressed stays pressed for good: this reproduces it, with an unchanged pressed one as the control
	// that is released. It is why the Chisel refuses to change a pressed piece (the next test).
	@GameTest(maxTicks = 60)
	public void aPressedPieceChangedByTheTransformerAloneStaysPressed(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<String> failures = new ArrayList<>();
		BlockPos[] positions = {new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), new BlockPos(1, 1, 3), new BlockPos(3, 1, 3)};
		Block[] blocks = {StoneFamily.SMOOTH_STONE_BUTTON, StoneFamily.POLISHED_STONE_BUTTON, StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, StoneFamily.POLISHED_STONE_PRESSURE_PLATE};
		for (int i = 0; i < 4; i++) {
			press(level, helper.absolutePos(positions[i]), blocks[i]);
		}
		for (int changed : new int[] {0, 2}) {
			BlockPos pos = helper.absolutePos(positions[changed]);
			ServerPlayer player = player(helper, ironChisel(), true);
			// The transformer itself, with none of the block-click listeners in front of it.
			player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit(pos)));
			if (!level.getBlockState(pos).is(blocks[changed] == StoneFamily.SMOOTH_STONE_BUTTON ? StoneFamily.POLISHED_STONE_BUTTON : StoneFamily.POLISHED_STONE_PRESSURE_PLATE)
					|| !level.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
				failures.add("The transformer alone turns the pressed " + name(blocks[changed].asItem()) + " into " + level.getBlockState(pos) + ", expected the pressed Polished one");
			}
		}
		helper.runAfterDelay(PRESSED_TICKS + 5, () -> {
			for (int i = 0; i < 4; i++) {
				boolean pressed = level.getBlockState(helper.absolutePos(positions[i])).getValue(BlockStateProperties.POWERED);
				boolean changed = i == 0 || i == 2;
				if (pressed != changed) {
					failures.add("The " + (changed ? "changed" : "unchanged") + " " + name(blocks[i].asItem()) + " is " + (pressed ? "still pressed" : "released") + " after " + (PRESSED_TICKS + 5) + " ticks, expected "
							+ (changed ? "stuck pressed, the bug the refusal is for" : "released"));
				}
				level.setBlock(helper.absolutePos(positions[i]), Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			}
			report(helper, IDEA, "stuck pressed piece checks", 6, failures);
		});
	}

	// What the Chisel does about it: a click that would change a pressed button or plate is refused (FAIL on both sides),
	// with no change, wear, wait, particles or sound; once it has released, the click changes it, to an unpressed piece.
	// Through the game's own useItemOn, for the Smooth and mossy buttons and plates.
	@GameTest(maxTicks = 60)
	public void aPressedPieceIsNotChangedUntilItReleases(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<String> failures = new ArrayList<>();
		List<Block> pressed = List.of(StoneFamily.SMOOTH_STONE_BUTTON, StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, StoneFamily.MOSSY_COBBLESTONE_BUTTON, StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE);
		BlockPos[] positions = {new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), new BlockPos(1, 1, 3), new BlockPos(3, 1, 3)};
		for (int i = 0; i < pressed.size(); i++) {
			BlockPos pos = helper.absolutePos(positions[i]);
			press(level, pos, pressed.get(i));
			ServerPlayer player = player(helper, ironChisel(), true);
			BlockHitResult hit = hit(pos);
			boolean particles = CarvingParticles.throwsParticles(player, level, InteractionHand.MAIN_HAND, hit);
			boolean extra = Chisels.extraSound(player, level, InteractionHand.MAIN_HAND, hit).isPresent();
			InteractionResult result = player.gameMode.useItemOn(player, level, player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
			if (result != InteractionResult.FAIL || !level.getBlockState(pos).is(pressed.get(i)) || !level.getBlockState(pos).getValue(BlockStateProperties.POWERED)
					|| player.getMainHandItem().getDamageValue() != 0 || player.getCooldowns().isOnCooldown(player.getMainHandItem()) || particles || extra) {
				failures.add("A sneaking click on the pressed " + name(pressed.get(i).asItem()) + " gives " + result + " and " + level.getBlockState(pos) + ", wear " + player.getMainHandItem().getDamageValue()
						+ (particles ? ", particles" : "") + (extra ? ", the extra sound" : "") + ", expected FAIL and no change at all");
			}
		}
		helper.runAfterDelay(PRESSED_TICKS + 5, () -> {
			for (int i = 0; i < pressed.size(); i++) {
				BlockPos pos = helper.absolutePos(positions[i]);
				ServerPlayer player = player(helper, ironChisel(), true);
				if (level.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
					failures.add("The unchanged " + name(pressed.get(i).asItem()) + " is still pressed after " + (PRESSED_TICKS + 5) + " ticks");
					continue;
				}
				click(helper, player, pos);
				Block expected = nextPiece().get(pressed.get(i));
				if (!level.getBlockState(pos).is(expected) || level.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
					failures.add("A click on the released " + name(pressed.get(i).asItem()) + " gives " + level.getBlockState(pos) + ", expected an unpressed " + BuiltInRegistries.BLOCK.getKey(expected));
				}
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			}
			report(helper, IDEA, "pressed piece checks", 8, failures);
		});
	}

	// A pressed button, or a plate with its release scheduled as an entity leaves it: pressed, with its tick.
	private static void press(ServerLevel level, BlockPos pos, Block block) {
		level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		level.setBlock(pos.west(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		BlockState state = block.defaultBlockState();
		if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
			state = state.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
		}
		level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		if (block instanceof ButtonBlock button) {
			button.press(state, level, pos, null);
		} else {
			level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			level.scheduleTick(pos, block, PRESSED_TICKS);
		}
	}

	// A survival player with a connection that goes nowhere, holding a new Chisel: ServerPlayer.connection is read by the
	// cooldowns it starts.
	private static ServerPlayer player(GameTestHelper helper, Item chisel, boolean sneaking) {
		ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
		new ServerGamePacketListenerImpl(helper.getLevel().getServer(), new Connection(PacketFlow.SERVERBOUND), player, CommonListenerCookie.createInitial(player.getGameProfile(), false));
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(chisel));
		player.setShiftKeyDown(sneaking);
		return player;
	}

	private static BlockHitResult hit(BlockPos pos) {
		return new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, true);
	}

	private static void click(GameTestHelper helper, ServerPlayer player) {
		click(helper, player, helper.absolutePos(POS));
	}

	// The game's own handling of a right click on a block with the player's main hand.
	private static void click(GameTestHelper helper, ServerPlayer player, BlockPos pos) {
		player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit(pos));
	}

	// Whether the new state has every property of the old one at the same value.
	private static boolean keepsState(BlockState before, BlockState after) {
		for (Property<?> property : before.getProperties()) {
			if (!after.hasProperty(property) || !after.getValue(property).equals(before.getValue(property))) {
				return false;
			}
		}
		return true;
	}

	// Clears the space round the position, puts what a state of the block needs there and returns the states to try, as
	// the game would settle them: stairs with a corner neighbour in front (an outer corner) and without, slabs of each
	// type, a wall joined to stone on two sides and under another stone, a button on a wall and one on a ceiling, a plate
	// on stone. A block with no state to carry gives its default.
	private static List<BlockState> prepare(ServerLevel level, BlockPos pos, Block block, List<String> failures) {
		for (BlockPos around : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 2, 1))) {
			level.setBlock(around, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		}
		BlockState base = block.defaultBlockState();
		List<BlockState> states = new ArrayList<>();
		if (block instanceof StairBlock) {
			level.setBlock(pos.north(), Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			states.add(base.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.WATERLOGGED, true));
			states.add(base.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP));
		} else if (block instanceof SlabBlock) {
			states.add(base.setValue(SlabBlock.TYPE, SlabType.BOTTOM));
			states.add(base.setValue(SlabBlock.TYPE, SlabType.TOP).setValue(SlabBlock.WATERLOGGED, true));
			states.add(base.setValue(SlabBlock.TYPE, SlabType.DOUBLE).setValue(SlabBlock.WATERLOGGED, false));
		} else if (block instanceof WallBlock) {
			level.setBlock(pos.north(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			level.setBlock(pos.east(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			states.add(base.setValue(WallBlock.WATERLOGGED, true));
		} else if (block instanceof ButtonBlock) {
			level.setBlock(pos.west(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			level.setBlock(pos.above(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			states.add(base.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
			states.add(base.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.CEILING).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
		} else if (base.hasProperty(BlockStateProperties.POWERED)) {
			level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			states.add(base);
		} else {
			states.add(base);
		}
		// The state the neighbours settle on, which is the one the game keeps; a sample that settles into air, or that
		// shows nothing of what it is meant to test, would test nothing.
		List<BlockState> settled = new ArrayList<>();
		for (BlockState state : states) {
			BlockState natural = Block.updateFromNeighbourShapes(state, level, pos);
			if (natural.isAir()) {
				failures.add(state + " does not survive where it is tested");
			}
			settled.add(natural);
		}
		if (block instanceof StairBlock && settled.getFirst().getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT) {
			failures.add("The stairs " + settled.getFirst() + " should be a corner, to test that the shape is kept");
		}
		if (block instanceof WallBlock && settled.getFirst().getValue(WallBlock.NORTH) == WallSide.NONE) {
			failures.add("The wall " + settled.getFirst() + " should be joined to its neighbour, to test that the sides are kept");
		}
		return settled;
	}
}
