package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.CarvingParticles;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

// C-009: the Chisel, one per tier, is an axe and a pickaxe in one that takes twice as long. Each expected value is
// worked out from vanilla's own tools of the same tier, as the game builds them, and from the owner's rules in
// docs/DESIGN.md: never from Caramel's data.
public class ChiselGameTest {
	private static final String IDEA = "C-009";
	// The owner's rule: the tier's hoe attack speed plus 0.5.
	private static final double FASTER_THAN_THE_HOE = 0.5;
	private static final BlockPos POS = new BlockPos(1, 1, 1);
	static final ResourceKey<BlockTransformer> CHISEL_TRANSFORMER = ResourceKey.create(Registries.BLOCK_TRANSFORMER, Caramel.id("chisel"));
	// The sound of carving stone, and the owner's rules for it: the anvil's sound at 1.5 times its pitch and half its
	// volume, with the subtitle "Block chiselled".
	private static final Identifier CARVE_STONE = Caramel.id("item.chisel.carve_stone");
	private static final double ANVIL_PITCH_TIMES = 1.5;
	private static final double ANVIL_VOLUME_TIMES = 0.5;
	private static final String CARVE_STONE_SUBTITLE = "Block chiselled";
	// The owner's rule: a Chisel changes at most one block a second, 20 game ticks, and every Chisel shares the wait.
	private static final int TICKS_BETWEEN_CHANGES = 20;
	private static final Identifier COOLDOWN_GROUP = Caramel.id("chisel");
	// The owner's rule: chiselling or stripping throws a quarter of the particles that breaking the block would.
	private static final int BREAK_PARTICLES_PER_CARVING = 4;

	// A Chisel's tier, with vanilla's tools of that tier. The flint tier takes the wood tier's place.
	record Tier(String name, Item pickaxe, Item axe, Item hoe) {
		Item chisel() {
			return BuiltInRegistries.ITEM.getValue(Caramel.id(name + "_chisel"));
		}
	}

	static final List<Tier> TIERS = List.of(
			new Tier("flint", Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE),
			new Tier("stone", Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_HOE),
			new Tier("copper", Items.COPPER_PICKAXE, Items.COPPER_AXE, Items.COPPER_HOE),
			new Tier("iron", Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE),
			new Tier("golden", Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_HOE),
			new Tier("diamond", Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_HOE),
			new Tier("netherite", Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE));

	// The owner's cycles from docs/DESIGN.md, one family per line: a right click turns a block into the next of its
	// line, and the last into the first. C-010 brought the owner's new order and stone's new blocks.
	static final List<List<Block>> CYCLES = List.of(
			List.of(StoneFamily.POLISHED_STONE, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS, StoneFamily.CHISELED_STONE_PILLAR,
					StoneFamily.STONE_TILES, StoneFamily.CRACKED_STONE_TILES),
			List.of(Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CHISELED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES),
			List.of(Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE),
			List.of(Blocks.POLISHED_TUFF, Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.CHISELED_TUFF),
			List.of(Blocks.POLISHED_SULFUR, Blocks.SULFUR_BRICKS, Blocks.CHISELED_SULFUR),
			List.of(Blocks.POLISHED_CINNABAR, Blocks.CINNABAR_BRICKS, Blocks.CHISELED_CINNABAR),
			List.of(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS),
			List.of(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC));

	// C-010: a right click turns a family's Smooth block into its Polished one, once; no cycle goes back to it.
	static final Map<Block, Block> WAYS_IN = Map.of(Blocks.SMOOTH_STONE, StoneFamily.POLISHED_STONE);

	// The one cycle that is wood: it plays the axe's stripping sound, and the others Caramel's carving sound.
	private static final List<Block> WOOD_CYCLE = CYCLES.getLast();

	// Blocks a Chisel's right click must leave alone: the polished blocks with no other full block, the plain stones,
	// the families outside the cycles, and copper an axe would scrape, unwax or leave.
	private static final List<Block> UNTOUCHED = Stream.of("polished_granite", "polished_diorite", "polished_andesite", "polished_basalt", "stone", "cobblestone", "deepslate",
			"quartz_block", "sandstone", "purpur_block", "exposed_copper", "waxed_copper_block", "cut_copper", "waxed_oxidized_cut_copper")
			.map(path -> BuiltInRegistries.BLOCK.getOptional(Identifier.withDefaultNamespace(path)).orElseThrow(() -> new IllegalStateException("There is no minecraft:" + path)))
			.toList();

	// Each Chisel is a tool of its tier: the durability, enchantability and repair material of that tier's pickaxe and
	// axe, with flint repairing the flint one. It wears like a pickaxe, is not fuel, and only the netherite one
	// resists fire.
	@GameTest
	public void chiselsAreToolsOfTheirTier(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			ItemStack chisel = new ItemStack(tier.chisel());
			ItemStack pickaxe = new ItemStack(tier.pickaxe());
			ItemStack axe = new ItemStack(tier.axe());
			String name = name(tier.chisel());
			checked++;
			if (chisel.isEmpty()) {
				failures.add("There is no caramel:" + tier.name() + "_chisel");
				continue;
			}
			if (chisel.getMaxDamage() != pickaxe.getMaxDamage() || chisel.getMaxDamage() != axe.getMaxDamage() || chisel.getMaxStackSize() != 1) {
				failures.add(name + " has durability " + chisel.getMaxDamage() + " in stacks of " + chisel.getMaxStackSize() + ", expected the " + name(tier.pickaxe()) + "'s "
						+ pickaxe.getMaxDamage() + " in stacks of 1");
			}
			if (!Objects.equals(chisel.get(DataComponents.ENCHANTABLE), pickaxe.get(DataComponents.ENCHANTABLE))) {
				failures.add(name + " has enchantability " + chisel.get(DataComponents.ENCHANTABLE) + ", expected " + pickaxe.get(DataComponents.ENCHANTABLE));
			}
			for (Item item : BuiltInRegistries.ITEM) {
				ItemStack material = new ItemStack(item);
				boolean expected = tier.name().equals("flint") ? item == Items.FLINT : pickaxe.isValidRepairItem(material);
				if (chisel.isValidRepairItem(material) != expected) {
					failures.add(name + (expected ? " is not" : " is") + " repaired with " + name(item));
				}
			}
			Tool tool = chisel.get(DataComponents.TOOL);
			Tool pickaxeTool = pickaxe.get(DataComponents.TOOL);
			if (tool == null || tool.damagePerBlock() != pickaxeTool.damagePerBlock() || tool.defaultMiningSpeed() != pickaxeTool.defaultMiningSpeed()
					|| tool.canDestroyBlocksInCreative() != pickaxeTool.canDestroyBlocksInCreative()) {
				failures.add(name + " has tool " + tool + ", expected the " + name(tier.pickaxe()) + "'s wear per block, speed on other blocks and creative breaking");
			}
			// Vanilla's tools take 2 durability per attack, and only axes disable a shield.
			Weapon weapon = chisel.get(DataComponents.WEAPON);
			if (!Objects.equals(weapon, pickaxe.get(DataComponents.WEAPON)) || Objects.equals(weapon, axe.get(DataComponents.WEAPON))) {
				failures.add(name + " has weapon " + weapon + ", expected the " + name(tier.pickaxe()) + "'s " + pickaxe.get(DataComponents.WEAPON));
			}
			if (!Objects.equals(chisel.get(DataComponents.DAMAGE_RESISTANT), pickaxe.get(DataComponents.DAMAGE_RESISTANT))) {
				failures.add(name + " resists " + chisel.get(DataComponents.DAMAGE_RESISTANT) + ", expected " + pickaxe.get(DataComponents.DAMAGE_RESISTANT));
			}
			if (chisel.has(DataComponents.COOKING_FUEL)) {
				failures.add(name + " is furnace fuel");
			}
			// Chisels leaves the component out when the transformer is missing, as on a server without Caramel. Here
			// it has to be there, and a pickaxe has none.
			Holder<BlockTransformer> rightClick = chisel.get(DataComponents.BLOCK_TRANSFORMER);
			if (rightClick == null || !rightClick.is(CHISEL_TRANSFORMER) || pickaxe.has(DataComponents.BLOCK_TRANSFORMER)) {
				failures.add(name + " has the right-click transformer " + rightClick + ", expected " + CHISEL_TRANSFORMER.identifier());
			}
		}
		List<Item> tagged = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(TagKey.create(Registries.ITEM, Caramel.id("chisels"))).forEach(holder -> tagged.add(holder.value()));
		List<Item> chisels = TIERS.stream().map(Tier::chisel).toList();
		checked++;
		if (!tagged.containsAll(chisels) || tagged.size() != chisels.size()) {
			failures.add("#caramel:chisels holds " + tagged.stream().map(RecipeTestSupport::name).toList() + ", expected the " + chisels.size() + " Chisels");
		}
		report(helper, IDEA, "Chisel tier checks", checked, failures);
	}

	// For every block in the game: a Chisel drops what its tier's pickaxe or axe drops, mines what either mines at half
	// that speed, and breaks anything else as a bare hand does.
	@GameTest
	public void chiselsMineWhatAxesAndPickaxesMineAtHalfSpeed(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			ItemStack chisel = new ItemStack(tier.chisel());
			ItemStack pickaxe = new ItemStack(tier.pickaxe());
			ItemStack axe = new ItemStack(tier.axe());
			int mined = 0;
			for (Block block : BuiltInRegistries.BLOCK) {
				BlockState state = block.defaultBlockState();
				checked++;
				boolean drops = pickaxe.isCorrectToolForDrops(state) || axe.isCorrectToolForDrops(state);
				float vanilla = Math.max(pickaxe.getDestroySpeed(state), axe.getDestroySpeed(state));
				float expected = vanilla > 1.0F ? vanilla / 2.0F : 1.0F;
				mined += vanilla > 1.0F ? 1 : 0;
				if (chisel.isCorrectToolForDrops(state) != drops || Math.abs(chisel.getDestroySpeed(state) - expected) > 1.0E-5F) {
					failures.add(name(tier.chisel()) + " mines " + BuiltInRegistries.BLOCK.getKey(block) + " at speed " + chisel.getDestroySpeed(state)
							+ (chisel.isCorrectToolForDrops(state) ? " with" : " without") + " drops, expected " + expected + (drops ? " with" : " without") + " drops");
				}
			}
			// Without blocks that the vanilla tools mine, the loop above would prove nothing.
			if (pickaxe.getDestroySpeed(Blocks.STONE.defaultBlockState()) <= 1.0F || axe.getDestroySpeed(Blocks.OAK_LOG.defaultBlockState()) <= 1.0F || mined < 2) {
				failures.add("Vanilla's " + name(tier.pickaxe()) + " and " + name(tier.axe()) + " mine " + mined + " blocks, expected stone and oak logs among them");
			}
		}
		report(helper, IDEA, "Chisel mining checks", checked, failures);
	}

	// The owner's answer: always twice as long. As a player's mining speed, with the enchantment's own modifiers
	// applied, a Chisel is at half the speed of its tier's pickaxe or axe at every Efficiency level, and at a bare
	// hand's speed on blocks neither mines.
	@GameTest
	public void chiselsTakeTwiceAsLongAtEveryEfficiencyLevel(GameTestHelper helper) {
		Holder<Enchantment> efficiency = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		List<Block> mined = List.of(Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.CRAFTING_TABLE, Blocks.STONE, Blocks.COBBLESTONE, Blocks.DEEPSLATE, Blocks.IRON_ORE, Blocks.OBSIDIAN);
		List<Block> others = List.of(Blocks.DIRT, Blocks.OAK_LEAVES, Blocks.SAND, Blocks.GLASS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			for (int level = 0; level <= efficiency.value().getMaxLevel(); level++) {
				ItemStack chisel = enchanted(tier.chisel(), efficiency, level);
				for (Block block : mined) {
					BlockState state = block.defaultBlockState();
					checked++;
					float vanilla = Math.max(miningSpeed(player, enchanted(tier.pickaxe(), efficiency, level), state), miningSpeed(player, enchanted(tier.axe(), efficiency, level), state));
					float actual = miningSpeed(player, chisel, state);
					if (Math.abs(actual - vanilla / 2.0F) > vanilla * 1.0E-5F) {
						failures.add(name(tier.chisel()) + " with Efficiency " + level + " mines " + BuiltInRegistries.BLOCK.getKey(block) + " at " + actual
								+ ", expected half of the vanilla tool's " + vanilla);
					}
				}
				for (Block block : others) {
					BlockState state = block.defaultBlockState();
					checked++;
					float hand = miningSpeed(player, ItemStack.EMPTY, state);
					float actual = miningSpeed(player, chisel, state);
					if (actual != hand) {
						failures.add(name(tier.chisel()) + " with Efficiency " + level + " breaks " + BuiltInRegistries.BLOCK.getKey(block) + " at " + actual + ", expected a bare hand's " + hand);
					}
				}
			}
		}
		// The check needs Efficiency to change the vanilla tools' speed.
		BlockState stone = Blocks.STONE.defaultBlockState();
		if (miningSpeed(player, enchanted(Items.IRON_PICKAXE, efficiency, 5), stone) <= miningSpeed(player, new ItemStack(Items.IRON_PICKAXE), stone)) {
			failures.add("Efficiency V does not speed up a vanilla iron pickaxe held by the mock player");
		}
		report(helper, IDEA, "Chisel mining speed checks", checked, failures);
	}

	// Half a heart like every hoe, and the tier's hoe attack speed plus 0.5.
	@GameTest
	public void chiselsAttackLikeSlightlyFasterHoes(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		for (Tier tier : TIERS) {
			ItemStack chisel = new ItemStack(tier.chisel());
			ItemStack hoe = new ItemStack(tier.hoe());
			double damage = attribute(chisel, true);
			double speed = attribute(chisel, false);
			if (damage != attribute(hoe, true) || damage != 1.0 || Math.abs(speed - (attribute(hoe, false) + FASTER_THAN_THE_HOE)) > 1.0E-9) {
				failures.add(name(tier.chisel()) + " deals " + damage + " at attack speed " + speed + ", expected the " + name(tier.hoe()) + "'s " + attribute(hoe, true)
						+ " at " + (attribute(hoe, false) + FASTER_THAN_THE_HOE));
			}
		}
		report(helper, IDEA, "Chisel attack checks", TIERS.size(), failures);
	}

	// A Chisel takes exactly the enchantments its tier's pickaxe takes, so none of the axe's weapon ones, and has the
	// pickaxe's other traits: the full amethyst cluster drop, shattering decorated pots, and piglins loving the golden
	// one.
	@GameTest
	public void chiselsTakeThePickaxesEnchantmentsAndTraits(GameTestHelper helper) {
		Registry<Enchantment> enchantments = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			ItemStack chisel = new ItemStack(tier.chisel());
			ItemStack pickaxe = new ItemStack(tier.pickaxe());
			for (Holder.Reference<Enchantment> enchantment : enchantments.listElements().toList()) {
				checked++;
				if (enchantment.value().canEnchant(chisel) != enchantment.value().canEnchant(pickaxe) || enchantment.value().isPrimaryItem(chisel) != enchantment.value().isPrimaryItem(pickaxe)) {
					failures.add(name(tier.chisel()) + (enchantment.value().canEnchant(chisel) ? " takes " : " does not take ") + enchantment.key().identifier() + ", unlike the " + name(tier.pickaxe()));
				}
			}
			for (TagKey<Item> trait : List.of(ItemTags.CLUSTER_MAX_HARVESTABLES, ItemTags.BREAKS_DECORATED_POTS, ItemTags.PIGLIN_LOVED)) {
				checked++;
				if (chisel.is(trait) != pickaxe.is(trait)) {
					failures.add(name(tier.chisel()) + (chisel.is(trait) ? " is" : " is not") + " in #" + trait.location() + ", unlike the " + name(tier.pickaxe()));
				}
			}
		}
		// The loop needs enchantments that tell the pickaxe from the axe, or it could not show the weapon ones are left out.
		Enchantment sharpness = enchantments.getOrThrow(Enchantments.SHARPNESS).value();
		if (!sharpness.canEnchant(new ItemStack(Items.IRON_AXE)) || sharpness.canEnchant(new ItemStack(Items.IRON_PICKAXE))
				|| !enchantments.getOrThrow(Enchantments.EFFICIENCY).value().canEnchant(new ItemStack(Items.IRON_PICKAXE))) {
			failures.add("Vanilla's iron axe should take Sharpness and its iron pickaxe Efficiency but not Sharpness");
		}
		report(helper, IDEA, "Chisel enchantment and trait checks", checked, failures);
	}

	// For every block in the game, with its default state and, for a log, lying on its side: a Chisel's right click
	// gives what the stripping entry of vanilla's axe gives, or the next block of the owner's cycles, or nothing. So
	// it strips whatever an axe strips and keeps the log's direction, and it never scrapes or unwaxes copper.
	@GameTest
	public void chiselsStripLikeAxesAndCycleTheOwnersBlocks(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Registry<BlockTransformer> transformers = level.registryAccess().lookupOrThrow(Registries.BLOCK_TRANSFORMER);
		BlockTransformer axe = transformers.getOrThrow(BlockTransformers.AXE).value();
		BlockTransformer chisel = transformers.getOrThrow(CHISEL_TRANSFORMER).value();
		Predicate<BlockTransformer.BlockTransformData> stripping = transform -> transform.sound().value() == SoundEvents.AXE_STRIP.value();
		Map<Block, Block> next = new HashMap<>(WAYS_IN);
		next.putAll(ChiselPiecesGameTest.MOSS_SCRAPING_BLOCKS);
		for (List<Block> cycle : CYCLES) {
			for (int i = 0; i < cycle.size(); i++) {
				next.put(cycle.get(i), cycle.get((i + 1) % cycle.size()));
			}
		}
		Map<Block, Block> pieces = ChiselPiecesGameTest.nextPiece();
		BlockPos pos = helper.absolutePos(POS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int stripped = 0;
		int scrapedOrUnwaxed = 0;
		for (Block block : BuiltInRegistries.BLOCK) {
			List<BlockState> states = new ArrayList<>(List.of(block.defaultBlockState()));
			if (block.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS)) {
				states.add(block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
			}
			for (BlockState state : states) {
				checked++;
				level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
				BlockState strippedTo = transformed(axe, stripping, level, pos);
				// A piece keeps its state, a block gets its next block's default state.
				BlockState expected = strippedTo != null ? strippedTo : pieces.containsKey(block) ? pieces.get(block).defaultBlockState().withPropertiesOf(state)
						: next.containsKey(block) ? next.get(block).defaultBlockState() : null;
				BlockState actual = transformed(chisel, transform -> true, level, pos);
				stripped += strippedTo != null ? 1 : 0;
				scrapedOrUnwaxed += strippedTo == null && transformed(axe, transform -> true, level, pos) != null ? 1 : 0;
				if (!Objects.equals(actual, expected)) {
					failures.add("A Chisel's right click turns " + state + " into " + actual + ", expected " + expected);
				} else if (actual != null) {
					// The owner's sounds: Caramel's carving sound on stone, the axe's stripping sound on wood and the game's moss
					// break on moss, with no particles. Like stripping, a change costs 1 durability, works on every face and
					// drops nothing.
					BlockTransformer.BlockTransformData entry = chisel.transforms().stream()
							.filter(transform -> transform.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos) != null).findFirst().orElseThrow();
					SoundEvent sound = ChiselPiecesGameTest.MOSS_SCRAPING.containsKey(block) ? SoundEvents.MOSS_BREAK
							: strippedTo != null || WOOD_CYCLE.contains(block) || ChiselPiecesGameTest.WOOD_PIECES.contains(block) ? SoundEvents.AXE_STRIP.value()
							: BuiltInRegistries.SOUND_EVENT.getValue(CARVE_STONE);
					if (sound == null || entry.sound().value() != sound || entry.particle() != BlockTransformer.TransformParticle.NONE || entry.itemDamagePerUse() != 1
							|| !entry.disallowedFaces().isEmpty() || entry.loot().isPresent() || entry.transformType() != BlockTransformer.TransformType.SINGLE_BLOCK) {
						failures.add("A Chisel's right click on " + state + " runs " + entry + ", expected the sound " + (sound == null ? CARVE_STONE : BuiltInRegistries.SOUND_EVENT.getKey(sound))
								+ ", no particles, 1 durability, every face and no loot");
					}
				}
			}
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		// Without logs an axe strips and copper it scrapes or unwaxes, the loop above would prove nothing about either.
		if (stripped == 0 || scrapedOrUnwaxed == 0) {
			failures.add("Vanilla's axe strips " + stripped + " block states and scrapes or unwaxes " + scrapedOrUnwaxed + ", expected some of each");
		}
		Caramel.LOGGER.info("{} game test: an axe strips {} block states, which a Chisel strips too, and scrapes or unwaxes {}, which a Chisel leaves alone", IDEA, stripped, scrapedOrUnwaxed);
		report(helper, IDEA, "Chisel right-click checks", checked, failures);
	}

	// Through a player's own right click: each of the owner's cycles goes all the way round, one block and 1 durability
	// a click; a log is stripped for 1 durability and keeps its direction; and a block outside the cycles is left alone
	// at no cost, as is every block of the cycles under a vanilla pickaxe or axe.
	@GameTest
	public void aPlayersChiselCarvesOneStepAClick(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			ItemStack chisel = new ItemStack(tier.chisel());
			player.setItemInHand(InteractionHand.MAIN_HAND, chisel);
			for (List<Block> cycle : CYCLES) {
				// Each cycle's wear is counted from 0.
				chisel.setDamageValue(0);
				helper.setBlock(POS, cycle.getFirst());
				for (int click = 1; click <= cycle.size(); click++) {
					checked++;
					helper.useBlock(POS, player);
					Block expected = cycle.get(click % cycle.size());
					if (!helper.getBlockState(POS).is(expected) || chisel.getDamageValue() != click) {
						failures.add("Click " + click + " of a " + name(tier.chisel()) + " from " + BuiltInRegistries.BLOCK.getKey(cycle.getFirst()) + " gives " + helper.getBlockState(POS)
								+ " at " + chisel.getDamageValue() + " wear, expected " + BuiltInRegistries.BLOCK.getKey(expected) + " at " + click);
						break;
					}
				}
			}
			for (Map.Entry<Block, Block> wayIn : WAYS_IN.entrySet()) {
				chisel.setDamageValue(0);
				checked++;
				helper.setBlock(POS, wayIn.getKey());
				helper.useBlock(POS, player);
				if (!helper.getBlockState(POS).is(wayIn.getValue()) || chisel.getDamageValue() != 1) {
					failures.add("A " + name(tier.chisel()) + " turns " + BuiltInRegistries.BLOCK.getKey(wayIn.getKey()) + " into " + helper.getBlockState(POS) + " at "
							+ chisel.getDamageValue() + " wear, expected " + BuiltInRegistries.BLOCK.getKey(wayIn.getValue()) + " at 1");
				}
			}
			chisel.setDamageValue(0);
			checked++;
			helper.setBlock(POS, Blocks.OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
			helper.useBlock(POS, player);
			if (!helper.getBlockState(POS).equals(Blocks.STRIPPED_OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X)) || chisel.getDamageValue() != 1) {
				failures.add("A " + name(tier.chisel()) + " turns an oak log on its side into " + helper.getBlockState(POS) + " at " + chisel.getDamageValue() + " wear, expected a stripped one at 1");
			}
			chisel.setDamageValue(0);
			for (Block untouched : UNTOUCHED) {
				checked++;
				helper.setBlock(POS, untouched);
				helper.useBlock(POS, player);
				if (!helper.getBlockState(POS).is(untouched) || chisel.getDamageValue() != 0) {
					failures.add("A " + name(tier.chisel()) + " turns " + BuiltInRegistries.BLOCK.getKey(untouched) + " into " + helper.getBlockState(POS) + " at " + chisel.getDamageValue() + " wear");
				}
			}
		}
		for (Item vanilla : List.of(Items.IRON_PICKAXE, Items.IRON_AXE)) {
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(vanilla));
			for (Block block : Stream.concat(CYCLES.stream().flatMap(List::stream), WAYS_IN.keySet().stream()).toList()) {
				checked++;
				helper.setBlock(POS, block);
				helper.useBlock(POS, player);
				if (!helper.getBlockState(POS).is(block)) {
					failures.add("A vanilla " + name(vanilla) + " turns " + BuiltInRegistries.BLOCK.getKey(block) + " into " + helper.getBlockState(POS));
				}
			}
		}
		helper.setBlock(POS, Blocks.AIR);
		report(helper, IDEA, "Chisel clicks", checked, failures);
	}

	// The owner's playtest suggestions: carving stone plays the anvil's sound at 1.5 times its pitch and half its volume,
	// with the subtitle "Block chiselled", until the owner's commissioned sound replaces it. A transformer plays every sound at pitch 1.0, so the pitch is
	// in Caramel's definition of its own sound, read here from the mod's sounds.json, which a server never loads. The
	// client finds a definition by the event's location, and plays nothing for an event without one.
	@GameTest
	public void carvingStoneSoundsLikeAHigherPitchedAnvil(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		SoundEvent carveStone = BuiltInRegistries.SOUND_EVENT.getValue(CARVE_STONE);
		checked++;
		// Heard from as far as the anvil's, which carries no fixed range.
		if (carveStone == null || !carveStone.location().equals(CARVE_STONE) || !carveStone.fixedRange().equals(SoundEvents.ANVIL_USE.fixedRange())) {
			failures.add("The sound " + CARVE_STONE + " is " + carveStone + ", expected one at that location, heard as far as " + SoundEvents.ANVIL_USE);
		}
		List<String> registered = new ArrayList<>();
		List<String> moved = new ArrayList<>();
		for (Map.Entry<ResourceKey<SoundEvent>, SoundEvent> entry : BuiltInRegistries.SOUND_EVENT.entrySet()) {
			if (entry.getKey().identifier().getNamespace().equals(Caramel.MOD_ID)) {
				registered.add(entry.getValue().location().getPath());
				if (!entry.getValue().location().equals(entry.getKey().identifier())) {
					moved.add(entry.getKey().identifier() + " at " + entry.getValue().location());
				}
			}
		}
		checked++;
		if (!moved.isEmpty()) {
			failures.add("Caramel's sounds " + moved + " are registered under another id than their location, so the client looks their definitions up under the wrong name");
		}
		JsonObject sounds = soundDefinitions();
		checked++;
		if (sounds == null || !sounds.keySet().stream().sorted().toList().equals(registered.stream().sorted().toList())) {
			failures.add("assets/caramel/sounds.json defines " + (sounds == null ? "nothing" : sounds.keySet()) + ", expected exactly Caramel's registered sounds " + registered);
		}
		JsonObject definition = sounds == null ? null : sounds.getAsJsonObject(CARVE_STONE.getPath());
		checked++;
		// One entry: the anvil's sound event by name, at the owner's pitch and volume and nothing else changed.
		JsonObject anvil = new JsonObject();
		anvil.addProperty("type", "event");
		anvil.addProperty("name", SoundEvents.ANVIL_USE.location().toString());
		anvil.addProperty("pitch", ANVIL_PITCH_TIMES);
		anvil.addProperty("volume", ANVIL_VOLUME_TIMES);
		JsonArray expected = new JsonArray();
		expected.add(anvil);
		if (definition == null || !expected.equals(definition.get("sounds")) || definition.has("replace")) {
			failures.add("The definition of " + CARVE_STONE + " is " + definition + ", expected the sounds " + expected);
		}
		checked++;
		String subtitle = definition == null || !definition.has("subtitle") ? null : definition.get("subtitle").getAsString();
		if (subtitle == null || !Language.getInstance().has(subtitle) || !CARVE_STONE_SUBTITLE.equals(Language.getInstance().getOrDefault(subtitle))) {
			failures.add("The subtitle of " + CARVE_STONE + " is " + subtitle + (subtitle == null ? "" : ", which reads \"" + Language.getInstance().getOrDefault(subtitle) + "\"")
					+ ", expected \"" + CARVE_STONE_SUBTITLE + "\"");
		}
		report(helper, IDEA, "carving sound checks", checked, failures);
	}

	// The owner's playtest suggestion: a Chisel changes at most one block a second, so a held right click no longer piles
	// up its sounds. Each change, on stone, bamboo or a log, starts a wait of 20 ticks that every Chisel shares and no
	// vanilla tool does, and a click that changes nothing starts none. While an item waits, the game refuses its right
	// click (ServerPlayerGameMode.useItemOn); helper.useBlock skips that check, so this test reads the wait itself.
	@GameTest
	public void aChiselWaitsASecondAfterEachChange(GameTestHelper helper) {
		List<ItemStack> vanillaTools = TIERS.stream().flatMap(tier -> Stream.of(tier.pickaxe(), tier.axe(), tier.hoe())).map(ItemStack::new).toList();
		List<ItemStack> chisels = TIERS.stream().map(tier -> new ItemStack(tier.chisel())).toList();
		Map<Block, Block> changes = Map.of(Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_MOSAIC, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			String name = name(tier.chisel());
			checked++;
			UseCooldown cooldown = new ItemStack(tier.chisel()).get(DataComponents.USE_COOLDOWN);
			if (cooldown == null || cooldown.ticks() != TICKS_BETWEEN_CHANGES || !cooldown.cooldownGroup().equals(Optional.of(COOLDOWN_GROUP))) {
				failures.add(name + " has the use cooldown " + cooldown + ", expected " + TICKS_BETWEEN_CHANGES + " ticks in " + COOLDOWN_GROUP);
			}
			checked++;
			Player idle = helper.makeMockPlayer(GameType.SURVIVAL);
			idle.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tier.chisel()));
			helper.setBlock(POS, Blocks.COBBLESTONE);
			helper.useBlock(POS, idle);
			if (chisels.stream().anyMatch(idle.getCooldowns()::isOnCooldown)) {
				failures.add("A " + name + " that changes nothing still makes the Chisels wait");
			}
			for (Map.Entry<Block, Block> change : changes.entrySet()) {
				checked++;
				Player player = helper.makeMockPlayer(GameType.SURVIVAL);
				ItemStack chisel = new ItemStack(tier.chisel());
				player.setItemInHand(InteractionHand.MAIN_HAND, chisel);
				helper.setBlock(POS, change.getKey());
				helper.useBlock(POS, player);
				List<String> waiting = Stream.concat(chisels.stream(), vanillaTools.stream()).filter(player.getCooldowns()::isOnCooldown).map(stack -> name(stack.getItem())).toList();
				if (!helper.getBlockState(POS).is(change.getValue()) || !waiting.equals(chisels.stream().map(stack -> name(stack.getItem())).toList())) {
					failures.add("A " + name + " turns " + BuiltInRegistries.BLOCK.getKey(change.getKey()) + " into " + helper.getBlockState(POS) + " and makes " + waiting
							+ " wait, expected " + BuiltInRegistries.BLOCK.getKey(change.getValue()) + " and every Chisel waiting, no vanilla tool");
				}
				if (change.getKey() == Blocks.POLISHED_DEEPSLATE) {
					// The wait lasts exactly 20 ticks.
					for (int tick = 1; tick <= TICKS_BETWEEN_CHANGES; tick++) {
						checked++;
						player.getCooldowns().tick();
						if (player.getCooldowns().isOnCooldown(chisel) != tick < TICKS_BETWEEN_CHANGES) {
							failures.add("A " + name + (player.getCooldowns().isOnCooldown(chisel) ? " still waits" : " no longer waits") + " " + tick + " ticks after a change");
						}
					}
				}
			}
		}
		helper.setBlock(POS, Blocks.AIR);
		report(helper, IDEA, "Chisel cooldown checks", checked, failures);
	}

	// The owner's answer to the review: while a Chisel waits, a click on a block its right click would change ends at
	// the Chisel (FAIL), so the client sends nothing and tries no other hand; on any other block the click goes on as
	// usual (PASS). Which blocks it would change is read from a Chisel that is not waiting, clicked through
	// helper.useBlock. An off-hand shield still takes the click unless the player sneaks, a waiting Chisel in the off
	// hand is held too, and a waiting vanilla axe is left to the game.
	@GameTest
	public void aWaitingChiselHoldsTheClickOnBlocksItWouldChange(GameTestHelper helper) {
		List<Block> blocks = Stream.of(CYCLES.stream().flatMap(List::stream), WAYS_IN.keySet().stream(), UNTOUCHED.stream(), Stream.of(Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.DIRT))
				.flatMap(stream -> stream).toList();
		BlockPos pos = helper.absolutePos(POS);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, true);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int changing = 0;
		for (Tier tier : TIERS) {
			String name = name(tier.chisel());
			for (Block block : blocks) {
				checked++;
				Player ready = helper.makeMockPlayer(GameType.SURVIVAL);
				ready.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tier.chisel()));
				helper.setBlock(POS, block);
				helper.useBlock(POS, ready);
				boolean changes = !helper.getBlockState(POS).is(block);
				changing += changes ? 1 : 0;
				helper.setBlock(POS, block);
				InteractionResult result = holdsClick(helper, waiting(helper, tier.chisel(), InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, hit);
				if (result != (changes ? InteractionResult.FAIL : InteractionResult.PASS)) {
					failures.add("A waiting " + name + " clicking " + BuiltInRegistries.BLOCK.getKey(block) + ", which it " + (changes ? "changes" : "leaves alone") + ", gives " + result);
				}
			}
			helper.setBlock(POS, Blocks.POLISHED_DEEPSLATE);
			for (boolean sneaking : List.of(false, true)) {
				checked++;
				Player player = waiting(helper, tier.chisel(), InteractionHand.MAIN_HAND);
				player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
				player.setShiftKeyDown(sneaking);
				InteractionResult result = holdsClick(helper, player, InteractionHand.MAIN_HAND, hit);
				if (result != (sneaking ? InteractionResult.FAIL : InteractionResult.PASS)) {
					failures.add("A waiting " + name + " with a shield in the off hand, " + (sneaking ? "" : "not ") + "sneaking, gives " + result);
				}
			}
			checked++;
			InteractionResult offHand = holdsClick(helper, waiting(helper, tier.chisel(), InteractionHand.OFF_HAND), InteractionHand.OFF_HAND, hit);
			if (offHand != InteractionResult.FAIL) {
				failures.add("A waiting " + name + " in the off hand gives " + offHand + " on polished deepslate, expected FAIL");
			}
			checked++;
			Player ready = helper.makeMockPlayer(GameType.SURVIVAL);
			ready.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tier.chisel()));
			InteractionResult notWaiting = holdsClick(helper, ready, InteractionHand.MAIN_HAND, hit);
			if (notWaiting != InteractionResult.PASS) {
				failures.add("A " + name + " that is not waiting gives " + notWaiting + " on polished deepslate, expected PASS, leaving the change to the game");
			}
		}
		checked++;
		helper.setBlock(POS, Blocks.OAK_LOG);
		Player axe = helper.makeMockPlayer(GameType.SURVIVAL);
		axe.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
		axe.getCooldowns().addCooldown(axe.getMainHandItem(), TICKS_BETWEEN_CHANGES);
		InteractionResult vanillaAxe = holdsClick(helper, axe, InteractionHand.MAIN_HAND, hit);
		if (vanillaAxe != InteractionResult.PASS) {
			failures.add("A waiting iron axe on an oak log gives " + vanillaAxe + ", expected PASS");
		}
		// Without blocks of both kinds the loop could not show the click is held only where it should be.
		if (changing == 0 || changing == TIERS.size() * blocks.size()) {
			failures.add(changing + " of the " + TIERS.size() * blocks.size() + " clicks change their block, expected some but not all");
		}
		helper.setBlock(POS, Blocks.AIR);
		report(helper, IDEA, "waiting Chisel click checks", checked, failures);
	}

	// The owner's suggestion: a Chisel's every change and an axe's stripping throw a quarter of the particles that
	// breaking the block would. CarvingParticles works the click out before the game makes it, so for every block in the game,
	// also on its side for each block with an axis, it must say so exactly when the block's next state comes from the
	// Chisel's transformer, or from the axe's stripping entry and never its scraping or wax removal; and the game's own
	// click must then make that change. The count is a quarter of what breaking the block makes, worked out from its shape
	// as ClientLevel.addDestroyBlockEffect does, and the particles are of the block before the change. They sit 0.1 out
	// from the clicked face and 0.1 in from its edges, where the game puts a mined block's cracks
	// (ClientLevel.addBreakingParticles), for each of the six faces. A waiting Chisel, and a click a shield takes, throw
	// none. This reads what CarvingParticles would send, not the packet, and helper.useBlock skips the game's
	// ServerPlayerGameMode.useItemOn; ChiselPiecesGameTest.aClickSendsItsSoundsAndParticlesToPlayers runs the listener itself.
	@GameTest
	public void chiselsAndAxesThrowParticlesExactlyWhenTheyChiselOrStrip(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Registry<BlockTransformer> transformers = level.registryAccess().lookupOrThrow(Registries.BLOCK_TRANSFORMER);
		BlockTransformer axe = transformers.getOrThrow(BlockTransformers.AXE).value();
		BlockTransformer chisel = transformers.getOrThrow(CHISEL_TRANSFORMER).value();
		Predicate<BlockTransformer.BlockTransformData> stripping = transform -> transform.sound().value() == SoundEvents.AXE_STRIP.value();
		Item ironChisel = TIERS.get(3).chisel();
		BlockPos pos = helper.absolutePos(POS);
		BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, true);
		// A pressure plate the Chisel changes needs the ground under it, or the game removes it.
		level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int throwing = 0;
		Map<Item, Integer> throwingByTool = new HashMap<>();
		for (Block block : BuiltInRegistries.BLOCK) {
			List<BlockState> states = new ArrayList<>(List.of(block.defaultBlockState()));
			if (block.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS)) {
				states.add(block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
			}
			for (BlockState state : states) {
				for (Item tool : List.of(ironChisel, Items.IRON_AXE)) {
					checked++;
					level.setBlock(pos, state, Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
					BlockState next = tool == ironChisel ? transformed(chisel, transform -> true, level, pos) : transformed(axe, stripping, level, pos);
					Player player = helper.makeMockPlayer(GameType.SURVIVAL);
					player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool));
					Optional<CarvingParticles.Burst> burst = CarvingParticles.burst(player, level, InteractionHand.MAIN_HAND, hit);
					boolean throwsParticles = burst.isPresent();
					// A button presses on a plain click, whatever the Chisel would do (ChiselPiecesGameTest has the sneaking one).
					boolean pressed = tool == ironChisel && block instanceof ButtonBlock;
					if (throwsParticles != (next != null && !pressed)) {
						failures.add("A " + name(tool) + " on " + state + (throwsParticles ? " throws" : " throws no") + " particles, though its click gives " + (pressed ? "a press" : next));
						continue;
					}
					if (next == null || pressed) {
						continue;
					}
					throwing++;
					throwingByTool.merge(tool, 1, Integer::sum);
					checked++;
					BlockState shown = ChiselPiecesGameTest.MOSS_SCRAPING.containsKey(block) ? Blocks.MOSS_BLOCK.defaultBlockState() : state;
					if (!burst.get().particle().getState().equals(shown)) {
						failures.add("A " + name(tool) + " on " + state + " throws particles of " + burst.get().particle().getState() + ", expected " + shown);
					}
					checked++;
					int breaking = breakParticles(state.getShape(level, pos));
					if (burst.get().count() != breaking / BREAK_PARTICLES_PER_CARVING) {
						failures.add("A " + name(tool) + " on " + state + " throws " + burst.get().count() + " particles, expected a quarter of breaking's " + breaking);
					}
					checked++;
					helper.useBlock(POS, player);
					if (!level.getBlockState(pos).equals(next)) {
						failures.add("A " + name(tool) + "'s click on " + state + " gives " + level.getBlockState(pos) + ", though particles were thrown for " + next);
					}
				}
			}
		}
		helper.setBlock(POS, Blocks.POLISHED_DEEPSLATE);
		for (Direction face : Direction.values()) {
			checked++;
			Player player = helper.makeMockPlayer(GameType.SURVIVAL);
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ironChisel));
			Optional<CarvingParticles.Burst> burst = CarvingParticles.burst(player, level, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), face, pos, true));
			List<Double> expectedCorner = new ArrayList<>();
			List<Double> expectedSpread = new ArrayList<>();
			for (Direction.Axis axis : Direction.Axis.values()) {
				boolean normal = axis == face.getAxis();
				double outside = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.1 : -0.1;
				expectedCorner.add(pos.get(axis) + (normal ? outside : 0.1));
				expectedSpread.add(normal ? 0.0 : 0.8);
			}
			List<Double> corner = burst.map(b -> List.of(b.corner().x(), b.corner().y(), b.corner().z())).orElse(List.of());
			List<Double> spread = burst.map(b -> List.of(b.spread().x(), b.spread().y(), b.spread().z())).orElse(List.of());
			if (!close(corner, expectedCorner) || !close(spread, expectedSpread)) {
				failures.add("Clicking the " + face + " face throws particles from " + corner + " over " + spread + ", expected from " + expectedCorner + " over " + expectedSpread);
			}
		}
		// On a thinner block they stay on its shape: 0.1 above a bottom slab's top, half a block up, and above a plate, which
		// is 1/16 tall and stops 1/16 short of each side.
		double plateEdge = 1.0 / 16 + 0.1;
		Map<BlockState, List<List<Double>>> thin = Map.of(
				StoneFamily.POLISHED_STONE_SLAB.defaultBlockState(), List.of(List.of(pos.getX() + 0.1, pos.getY() + 0.6, pos.getZ() + 0.1), List.of(0.8, 0.0, 0.8)),
				StoneFamily.SMOOTH_STONE_PRESSURE_PLATE.defaultBlockState(),
				List.of(List.of(pos.getX() + plateEdge, pos.getY() + 1.0 / 16 + 0.1, pos.getZ() + plateEdge), List.of(1.0 - 2 * plateEdge, 0.0, 1.0 - 2 * plateEdge)));
		helper.setBlock(POS.below(), Blocks.STONE);
		for (Map.Entry<BlockState, List<List<Double>>> block : thin.entrySet()) {
			checked++;
			level.setBlock(pos, block.getKey(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
			Player player = helper.makeMockPlayer(GameType.SURVIVAL);
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ironChisel));
			Optional<CarvingParticles.Burst> burst = CarvingParticles.burst(player, level, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, true));
			List<Double> corner = burst.map(b -> List.of(b.corner().x(), b.corner().y(), b.corner().z())).orElse(List.of());
			List<Double> spread = burst.map(b -> List.of(b.spread().x(), b.spread().y(), b.spread().z())).orElse(List.of());
			if (!close(corner, block.getValue().get(0)) || !close(spread, block.getValue().get(1))) {
				failures.add("Clicking the top of " + block.getKey() + " throws particles from " + corner + " over " + spread + ", expected " + block.getValue());
			}
		}
		helper.setBlock(POS, Blocks.POLISHED_DEEPSLATE);
		checked++;
		Player waiting = waiting(helper, ironChisel, InteractionHand.MAIN_HAND);
		if (CarvingParticles.burst(waiting, level, InteractionHand.MAIN_HAND, hit).isPresent()) {
			failures.add("A waiting " + name(ironChisel) + " throws particles");
		}
		helper.setBlock(POS, Blocks.OAK_LOG);
		for (Item tool : List.of(ironChisel, Items.IRON_AXE)) {
			checked++;
			Player shielded = helper.makeMockPlayer(GameType.SURVIVAL);
			shielded.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool));
			shielded.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
			if (CarvingParticles.burst(shielded, level, InteractionHand.MAIN_HAND, hit).isPresent()) {
				failures.add("A " + name(tool) + " whose click a shield takes throws particles");
			}
		}
		// Without changes of both tools the loop above would prove nothing about either.
		checked++;
		if (!throwingByTool.containsKey(ironChisel) || !throwingByTool.containsKey(Items.IRON_AXE)) {
			failures.add("Particles were thrown by " + throwingByTool + ", expected by both the Iron Chisel and the iron axe");
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
		Caramel.LOGGER.info("{} game test: {} Chisel changes and {} axe strippings throw particles", IDEA, throwingByTool.get(ironChisel), throwingByTool.get(Items.IRON_AXE));
		report(helper, IDEA, "carving particle checks", checked, failures);
	}

	// How many particles breaking a block of this shape makes: per box, at least 2 a side, one per quarter block
	// (ClientLevel.addDestroyBlockEffect).
	private static int breakParticles(VoxelShape shape) {
		int[] count = {0};
		shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> count[0] += side(x2 - x1) * side(y2 - y1) * side(z2 - z1));
		return count[0];
	}

	private static boolean close(List<Double> actual, List<Double> expected) {
		if (actual.size() != expected.size()) {
			return false;
		}
		for (int i = 0; i < actual.size(); i++) {
			if (Math.abs(actual.get(i) - expected.get(i)) > 1.0E-9) {
				return false;
			}
		}
		return true;
	}

	private static int side(double width) {
		return Math.max(2, (int) Math.ceil(Math.min(1.0, width) / 0.25));
	}

	// The playtest's anvil, in survival: a book goes on a Chisel exactly when it goes on its tier's pickaxe. In creative
	// the anvil skips that check for every item, so a Sharpness book goes on both.
	@GameTest
	public void anAnvilPutsOnlyThePickaxesBooksOnAChiselInSurvival(GameTestHelper helper) {
		Registry<Enchantment> enchantments = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		Holder<Enchantment> sharpness = enchantments.getOrThrow(Enchantments.SHARPNESS);
		Player survival = helper.makeMockPlayer(GameType.SURVIVAL);
		Player creative = helper.makeMockPlayer(GameType.CREATIVE);
		GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : TIERS) {
			for (Holder.Reference<Enchantment> enchantment : enchantments.listElements().toList()) {
				checked++;
				boolean chisel = anvilEnchants(survival, tier.chisel(), enchantment);
				if (chisel != anvilEnchants(survival, tier.pickaxe(), enchantment)) {
					failures.add("In survival, an anvil " + (chisel ? "puts " : "does not put ") + enchantment.key().identifier() + " on a " + name(tier.chisel()) + ", unlike on a "
							+ name(tier.pickaxe()));
				}
			}
			checked++;
			if (!anvilEnchants(creative, tier.chisel(), sharpness) || !anvilEnchants(creative, tier.pickaxe(), sharpness)) {
				failures.add("In creative, an anvil should put Sharpness on both a " + name(tier.chisel()) + " and a " + name(tier.pickaxe()));
			}
		}
		// The loop needs a book the survival anvil refuses a pickaxe and gives an axe, or it could not show a refusal.
		checked++;
		if (anvilEnchants(survival, Items.IRON_PICKAXE, sharpness) || !anvilEnchants(survival, Items.IRON_AXE, sharpness)) {
			failures.add("In survival, an anvil should put Sharpness on an iron axe and not on an iron pickaxe");
		}
		report(helper, IDEA, "Chisel anvil checks", checked, failures);
	}

	// Each Chisel sits right after its tier's hoe in the creative inventory's tools tab.
	@GameTest
	public void chiselsFollowTheirHoesInTheCreativeInventory(GameTestHelper helper) {
		CreativeModeTabs.tryRebuildTabContents(helper.getLevel().enabledFeatures(), false, helper.getLevel().registryAccess());
		List<Item> tools = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.TOOLS_AND_UTILITIES).getDisplayItems().stream().map(ItemStack::getItem).toList();
		List<String> failures = new ArrayList<>();
		for (Tier tier : TIERS) {
			int hoe = tools.indexOf(tier.hoe());
			if (hoe < 0 || hoe + 1 >= tools.size() || tools.get(hoe + 1) != tier.chisel() || tools.indexOf(tier.chisel()) != tools.lastIndexOf(tier.chisel())) {
				failures.add(name(tier.chisel()) + " is at " + tools.indexOf(tier.chisel()) + " of the tools tab, expected once, right after the " + name(tier.hoe()) + " at " + hoe);
			}
		}
		report(helper, IDEA, "creative tab placements", TIERS.size(), failures);
	}

	// What a transformer's chosen entries turn the block at a position into: the first entry that gives a block, as
	// the game runs them, or null.
	static BlockState transformed(BlockTransformer transformer, Predicate<BlockTransformer.BlockTransformData> entries, ServerLevel level, BlockPos pos) {
		for (BlockTransformer.BlockTransformData transform : transformer.transforms()) {
			if (entries.test(transform)) {
				BlockState state = transform.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos);
				if (state != null) {
					return state;
				}
			}
		}
		return null;
	}

	// A player holding a new stack of the Chisel in one hand, waiting as after a change.
	private static Player waiting(GameTestHelper helper, Item chisel, InteractionHand hand) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		ItemStack stack = new ItemStack(chisel);
		player.setItemInHand(hand, stack);
		player.getCooldowns().addCooldown(stack, TICKS_BETWEEN_CHANGES);
		return player;
	}

	// What Fabric's block-click event answers, as the game asks it before its own handling of a click.
	private static InteractionResult holdsClick(GameTestHelper helper, Player player, InteractionHand hand, BlockHitResult hit) {
		return UseBlockCallback.EVENT.invoker().interact(player, helper.getLevel(), hand, hit);
	}

	// Whether an anvil used by the player puts a level 1 book of the enchantment on a new stack of the item.
	private static boolean anvilEnchants(Player player, Item item, Holder<Enchantment> enchantment) {
		AnvilMenu anvil = new AnvilMenu(0, player.getInventory());
		anvil.getSlot(AnvilMenu.INPUT_SLOT).set(new ItemStack(item));
		anvil.getSlot(AnvilMenu.ADDITIONAL_SLOT).set(EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, 1)));
		anvil.createResult();
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, anvil.getSlot(AnvilMenu.RESULT_SLOT).getItem()) > 0;
	}

	// Caramel's assets/caramel/sounds.json as the mod ships it, or null if it has none.
	private static JsonObject soundDefinitions() {
		Optional<Path> file = FabricLoader.getInstance().getModContainer(Caramel.MOD_ID).flatMap(mod -> mod.findPath("assets/" + Caramel.MOD_ID + "/sounds.json"));
		if (file.isEmpty()) {
			return null;
		}
		try {
			JsonElement json = JsonParser.parseString(Files.readString(file.get()));
			return json.isJsonObject() ? json.getAsJsonObject() : null;
		} catch (IOException e) {
			throw new IllegalStateException("Could not read " + file.get(), e);
		}
	}

	private static ItemStack enchanted(Item item, Holder<Enchantment> enchantment, int level) {
		ItemStack stack = new ItemStack(item);
		if (level > 0) {
			stack.enchant(enchantment, level);
		}
		return stack;
	}

	// The player's speed on a block while holding a stack. A mock player is never ticked, so the stack's main-hand
	// modifiers, Efficiency's among them, are applied the way the game applies them when equipment changes.
	private static float miningSpeed(Player player, ItemStack stack, BlockState state) {
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> player.getAttribute(attribute).addTransientModifier(modifier));
		float speed = player.getDestroySpeed(state);
		stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> player.getAttribute(attribute).removeModifier(modifier));
		return speed;
	}

	// A stack's attack damage or attack speed in the main hand, on the player's base 1 damage and 4 attacks a second.
	private static double attribute(ItemStack stack, boolean damage) {
		return stack.get(DataComponents.ATTRIBUTE_MODIFIERS).compute(damage ? Attributes.ATTACK_DAMAGE : Attributes.ATTACK_SPEED, damage ? 1.0 : 4.0, EquipmentSlot.MAINHAND);
	}
}
