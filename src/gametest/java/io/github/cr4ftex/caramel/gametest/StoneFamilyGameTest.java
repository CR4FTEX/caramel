package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.cut;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;

// C-010: the Stone family. Every expected value is vanilla's own block, recipe or data pack file, or the owner's rule
// from docs/DESIGN.md written out here: never Caramel's data.
public class StoneFamilyGameTest {
	private static final String IDEA = "C-010";
	private static final BlockPos POS = new BlockPos(1, 1, 1);

	// The 9 vanilla blocks the owner renamed, by translation key. The ids stay vanilla's.
	private static final Map<String, String> RENAMED = new LinkedHashMap<>();
	// The English name of each new block, by id path, and of the Stone Brick item.
	private static final Map<String, String> NEW_BLOCK_NAMES = new LinkedHashMap<>();

	static {
		RENAMED.put("block.minecraft.cobblestone", "Cobbled Stone");
		RENAMED.put("block.minecraft.cobblestone_stairs", "Cobbled Stone Stairs");
		RENAMED.put("block.minecraft.cobblestone_slab", "Cobbled Stone Slab");
		RENAMED.put("block.minecraft.cobblestone_wall", "Cobbled Stone Wall");
		RENAMED.put("block.minecraft.infested_cobblestone", "Infested Cobbled Stone");
		RENAMED.put("block.minecraft.mossy_cobblestone", "Cobbled Mossy Stone");
		RENAMED.put("block.minecraft.mossy_cobblestone_stairs", "Cobbled Mossy Stone Stairs");
		RENAMED.put("block.minecraft.mossy_cobblestone_slab", "Cobbled Mossy Stone Slab");
		RENAMED.put("block.minecraft.mossy_cobblestone_wall", "Cobbled Mossy Stone Wall");

		NEW_BLOCK_NAMES.put("stone_wall", "Stone Wall");
		NEW_BLOCK_NAMES.put("cobblestone_button", "Cobbled Stone Button");
		NEW_BLOCK_NAMES.put("cobblestone_pressure_plate", "Cobbled Stone Pressure Plate");
		NEW_BLOCK_NAMES.put("mossy_cobblestone_button", "Cobbled Mossy Stone Button");
		NEW_BLOCK_NAMES.put("mossy_cobblestone_pressure_plate", "Cobbled Mossy Stone Pressure Plate");
		NEW_BLOCK_NAMES.put("smooth_stone_stairs", "Smooth Stone Stairs");
		NEW_BLOCK_NAMES.put("smooth_stone_wall", "Smooth Stone Wall");
		NEW_BLOCK_NAMES.put("smooth_stone_button", "Smooth Stone Button");
		NEW_BLOCK_NAMES.put("smooth_stone_pressure_plate", "Smooth Stone Pressure Plate");
		NEW_BLOCK_NAMES.put("polished_stone", "Polished Stone");
		NEW_BLOCK_NAMES.put("polished_stone_stairs", "Polished Stone Stairs");
		NEW_BLOCK_NAMES.put("polished_stone_slab", "Polished Stone Slab");
		NEW_BLOCK_NAMES.put("polished_stone_wall", "Polished Stone Wall");
		NEW_BLOCK_NAMES.put("polished_stone_button", "Polished Stone Button");
		NEW_BLOCK_NAMES.put("polished_stone_pressure_plate", "Polished Stone Pressure Plate");
		NEW_BLOCK_NAMES.put("chiseled_stone_pillar", "Chiseled Stone Pillar");
		NEW_BLOCK_NAMES.put("stone_tiles", "Stone Tiles");
		NEW_BLOCK_NAMES.put("cracked_stone_tiles", "Cracked Stone Tiles");
		NEW_BLOCK_NAMES.put("stone_tile_stairs", "Stone Tile Stairs");
		NEW_BLOCK_NAMES.put("stone_tile_slab", "Stone Tile Slab");
		NEW_BLOCK_NAMES.put("stone_tile_wall", "Stone Tile Wall");
		NEW_BLOCK_NAMES.put("stone_tile_fence", "Stone Tile Fence");
	}

	// A new block, the vanilla block it is made like, the class of its kind, and the map colour it takes when that is not
	// the vanilla block's.
	record Piece(Block block, Block like, Class<? extends Block> kind, MapColor mapColor) {
		Piece(Block block, Block like, Class<? extends Block> kind) {
			this(block, like, kind, like.defaultMapColor());
		}

		String id() {
			return BuiltInRegistries.BLOCK.getKey(block).getPath();
		}
	}

	// The pairs from the owner's defaults: made as the game makes the same piece of a family, with the hardness, blast
	// resistance and sound of the closest game block. The pressure plates take their base block's map colour.
	static final List<Piece> PIECES = List.of(
			new Piece(StoneFamily.STONE_WALL, Blocks.STONE, WallBlock.class),
			new Piece(StoneFamily.SMOOTH_STONE_STAIRS, Blocks.SMOOTH_STONE, StairBlock.class),
			new Piece(StoneFamily.SMOOTH_STONE_WALL, Blocks.SMOOTH_STONE, WallBlock.class),
			new Piece(StoneFamily.POLISHED_STONE, Blocks.SMOOTH_STONE, Block.class),
			new Piece(StoneFamily.POLISHED_STONE_STAIRS, Blocks.SMOOTH_STONE, StairBlock.class),
			new Piece(StoneFamily.POLISHED_STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, SlabBlock.class),
			new Piece(StoneFamily.POLISHED_STONE_WALL, Blocks.SMOOTH_STONE, WallBlock.class),
			new Piece(StoneFamily.CHISELED_STONE_PILLAR, Blocks.CHISELED_STONE_BRICKS, RotatedPillarBlock.class),
			new Piece(StoneFamily.STONE_TILES, Blocks.STONE_BRICKS, Block.class),
			new Piece(StoneFamily.CRACKED_STONE_TILES, Blocks.CRACKED_STONE_BRICKS, Block.class),
			new Piece(StoneFamily.STONE_TILE_STAIRS, Blocks.STONE_BRICK_STAIRS, StairBlock.class),
			new Piece(StoneFamily.STONE_TILE_SLAB, Blocks.STONE_BRICK_SLAB, SlabBlock.class),
			new Piece(StoneFamily.STONE_TILE_WALL, Blocks.STONE_BRICK_WALL, WallBlock.class),
			new Piece(StoneFamily.STONE_TILE_FENCE, Blocks.STONE_BRICKS, FenceBlock.class),
			new Piece(StoneFamily.COBBLESTONE_BUTTON, Blocks.STONE_BUTTON, ButtonBlock.class),
			new Piece(StoneFamily.MOSSY_COBBLESTONE_BUTTON, Blocks.STONE_BUTTON, ButtonBlock.class),
			new Piece(StoneFamily.SMOOTH_STONE_BUTTON, Blocks.STONE_BUTTON, ButtonBlock.class),
			new Piece(StoneFamily.POLISHED_STONE_BUTTON, Blocks.STONE_BUTTON, ButtonBlock.class),
			new Piece(StoneFamily.COBBLESTONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, PressurePlateBlock.class, Blocks.COBBLESTONE.defaultMapColor()),
			new Piece(StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, PressurePlateBlock.class, Blocks.MOSSY_COBBLESTONE.defaultMapColor()),
			new Piece(StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, PressurePlateBlock.class, Blocks.SMOOTH_STONE.defaultMapColor()),
			new Piece(StoneFamily.POLISHED_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, PressurePlateBlock.class, Blocks.SMOOTH_STONE.defaultMapColor()));

	// The vanilla stone piece of each kind, whose tags the new pieces of that kind join.
	private static final Map<Class<? extends Block>, Block> TAG_PIECES = Map.of(
			StairBlock.class, Blocks.STONE_BRICK_STAIRS,
			SlabBlock.class, Blocks.STONE_BRICK_SLAB,
			WallBlock.class, Blocks.STONE_BRICK_WALL,
			FenceBlock.class, Blocks.NETHER_BRICK_FENCE,
			ButtonBlock.class, Blocks.STONE_BUTTON,
			PressurePlateBlock.class, Blocks.STONE_PRESSURE_PLATE);

	// Tags that name only the vanilla piece's own material, not its kind, so a block of another material is not in them:
	// #minecraft:cannot_place_basalt_pillar_on, which names nether bricks and the nether brick fence. Fabric's conventional
	// tags (the c namespace, such as #c:fences, which lists fences by material, and #c:fences/nether_brick) are left out
	// too: compatibility with other mods is not a priority yet (docs/DESIGN.md, "Priorities"), and only vanilla's tags are
	// compared.
	// #minecraft:stone_bricks, block and item, names the stone bricks family alone, so the tiles made like them stay out.
	private static final Set<Identifier> TAGS_OF_THE_VANILLA_PIECE_ONLY = Set.of(Identifier.withDefaultNamespace("cannot_place_basalt_pillar_on"),
			Identifier.withDefaultNamespace("stone_bricks"));

	// The replaced vanilla recipe, in the minecraft namespace: Stone Bricks from 4 Stone in a 2x2 square. The owner's chain
	// takes its place (4 Stone make Polished Stone, 4 Polished Stone make Stone Bricks), and VanillaOverridesGameTest
	// expects it to be the only one.
	static final Set<Identifier> REPLACED_RECIPES = Set.of(Identifier.withDefaultNamespace("stone_bricks"));

	// Vanilla's models of Smooth Stone and its slab, by name, which Caramel's own copies give the new texture, and which
	// Polished Stone's models of the same names repeat.
	private static final List<String> SMOOTH_STONE_MODELS = List.of("smooth_stone", "smooth_stone_slab", "smooth_stone_slab_top", "smooth_stone_slab_double");

	// The owner's rules: Stone Bricks are cut into Stone Brick items 4 to a block, 3 to a stairs, 2 to a slab and 2 to a wall.
	private static final Map<Item, Integer> BRICKS_CUT_FROM = Map.of(
			Items.STONE_BRICKS, 4,
			Items.STONE_BRICK_STAIRS, 3,
			Items.STONE_BRICK_SLAB, 2,
			Items.STONE_BRICK_WALL, 2);

	// A stonecutter option: what it makes and how many.
	record Cut(Item result, int count) {
		Cut(Block block, int count) {
			this(block.asItem(), count);
		}
	}

	// What the stonecutter must offer for each block, in the owner's words: each block cuts into the blocks after it in the
	// Chisel's chain, with their slabs and walls, 1 of a block and 2 of a slab or a wall. Vanilla's own cuts along the chain
	// (stone bricks and their slab and wall, the chiseled bricks) are included, since the entry lists them. Smooth Stone
	// cuts only into its own slab and wall.
	private static final Map<Item, List<Cut>> CUTS = Map.of(
			Items.STONE, List.of(new Cut(StoneFamily.STONE_WALL, 2),
					new Cut(StoneFamily.POLISHED_STONE, 1), new Cut(StoneFamily.POLISHED_STONE_SLAB, 2), new Cut(StoneFamily.POLISHED_STONE_WALL, 2),
					new Cut(Blocks.STONE_BRICKS, 1), new Cut(Blocks.STONE_BRICK_SLAB, 2), new Cut(Blocks.STONE_BRICK_WALL, 2),
					new Cut(Blocks.CHISELED_STONE_BRICKS, 1), new Cut(StoneFamily.CHISELED_STONE_PILLAR, 1),
					new Cut(StoneFamily.STONE_TILES, 1), new Cut(StoneFamily.STONE_TILE_SLAB, 2), new Cut(StoneFamily.STONE_TILE_WALL, 2)),
			Items.SMOOTH_STONE, List.of(new Cut(Blocks.SMOOTH_STONE_SLAB, 2), new Cut(StoneFamily.SMOOTH_STONE_WALL, 2)),
			StoneFamily.POLISHED_STONE.asItem(), List.of(new Cut(StoneFamily.POLISHED_STONE_SLAB, 2), new Cut(StoneFamily.POLISHED_STONE_WALL, 2),
					new Cut(Blocks.STONE_BRICKS, 1), new Cut(Blocks.STONE_BRICK_SLAB, 2), new Cut(Blocks.STONE_BRICK_WALL, 2),
					new Cut(Blocks.CHISELED_STONE_BRICKS, 1), new Cut(StoneFamily.CHISELED_STONE_PILLAR, 1),
					new Cut(StoneFamily.STONE_TILES, 1), new Cut(StoneFamily.STONE_TILE_SLAB, 2), new Cut(StoneFamily.STONE_TILE_WALL, 2)),
			Items.STONE_BRICKS, List.of(new Cut(Blocks.CHISELED_STONE_BRICKS, 1), new Cut(StoneFamily.CHISELED_STONE_PILLAR, 1),
					new Cut(StoneFamily.STONE_TILES, 1), new Cut(StoneFamily.STONE_TILE_SLAB, 2), new Cut(StoneFamily.STONE_TILE_WALL, 2),
					new Cut(StoneFamily.STONE_BRICK, 4)),
			StoneFamily.STONE_TILES.asItem(), List.of(new Cut(StoneFamily.STONE_TILE_SLAB, 2), new Cut(StoneFamily.STONE_TILE_WALL, 2)),
			Items.STONE_BRICK_STAIRS, List.of(new Cut(StoneFamily.STONE_BRICK, 3)),
			Items.STONE_BRICK_SLAB, List.of(new Cut(StoneFamily.STONE_BRICK, 2)),
			Items.STONE_BRICK_WALL, List.of(new Cut(StoneFamily.STONE_BRICK, 2)));

	// The blocks the base game's Stone family keeps the ids of, and the Cobbled Stone counterpart each drops without Silk
	// Touch: the owner's rule is that Stone and every block of its set drop their cobbled version, as Stone drops
	// Cobblestone today.
	private static final Map<Block, Item> COBBLED_COUNTERPARTS = new LinkedHashMap<>();

	static {
		COBBLED_COUNTERPARTS.put(Blocks.STONE, Items.COBBLESTONE);
		COBBLED_COUNTERPARTS.put(Blocks.STONE_STAIRS, Items.COBBLESTONE_STAIRS);
		COBBLED_COUNTERPARTS.put(Blocks.STONE_SLAB, Items.COBBLESTONE_SLAB);
		COBBLED_COUNTERPARTS.put(Blocks.STONE_BUTTON, StoneFamily.COBBLESTONE_BUTTON.asItem());
		COBBLED_COUNTERPARTS.put(Blocks.STONE_PRESSURE_PLATE, StoneFamily.COBBLESTONE_PRESSURE_PLATE.asItem());
		COBBLED_COUNTERPARTS.put(StoneFamily.STONE_WALL, Items.COBBLESTONE_WALL);
	}

	// The owner's names, read as the server reads them. Vanilla's 11 renamed keys (assets/minecraft/lang/en_us.json is
	// Caramel's, read after vanilla's), each new block and the Stone Brick item.
	@GameTest
	public void namesAreTheOwnersNames(GameTestHelper helper) {
		Language language = Language.getInstance();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Map.Entry<String, String> renamed : RENAMED.entrySet()) {
			checked++;
			String actual = language.has(renamed.getKey()) ? language.getOrDefault(renamed.getKey()) : null;
			if (!renamed.getValue().equals(actual)) {
				failures.add(renamed.getKey() + " is " + actual + ", expected " + renamed.getValue());
			}
		}
		for (Map.Entry<String, String> named : NEW_BLOCK_NAMES.entrySet()) {
			checked++;
			String key = "block.caramel." + named.getKey();
			String actual = language.has(key) ? language.getOrDefault(key) : null;
			if (!named.getValue().equals(actual)) {
				failures.add(key + " is " + actual + ", expected " + named.getValue());
			}
		}
		checked++;
		String brick = language.has("item.caramel.stone_brick") ? language.getOrDefault("item.caramel.stone_brick") : null;
		if (!"Stone Brick".equals(brick)) {
			failures.add("item.caramel.stone_brick is " + brick + ", expected Stone Brick");
		}
		// The 22 new blocks are the ones named: none is left without a name, and none is named twice.
		checked++;
		Set<String> registered = new TreeSet<>();
		StoneFamily.BLOCKS.forEach(block -> registered.add(BuiltInRegistries.BLOCK.getKey(block).getPath()));
		if (!registered.equals(new TreeSet<>(NEW_BLOCK_NAMES.keySet())) || StoneFamily.BLOCKS.size() != 22) {
			failures.add("StoneFamily.BLOCKS holds " + registered + " (" + StoneFamily.BLOCKS.size() + "), expected the 22 named blocks " + NEW_BLOCK_NAMES.keySet());
		}
		report(helper, IDEA, "name checks", checked, failures);
	}

	// Each new block is of the class of its kind and has the destroy time, explosion resistance, sound, map colour, tool
	// requirement, note block instrument and push reaction of the vanilla block it is made like. As a guard, vanilla's
	// nether brick fence shows the comparison holds for a fence and its block.
	@GameTest
	public void newBlocksAreMadeLikeTheirClosestVanillaBlocks(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		List<Piece> pieces = new ArrayList<>(PIECES);
		pieces.add(new Piece(Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICKS, FenceBlock.class));
		for (Piece piece : pieces) {
			Block block = piece.block();
			Block like = piece.like();
			String label = BuiltInRegistries.BLOCK.getKey(block) + " (like " + BuiltInRegistries.BLOCK.getKey(like) + ")";
			BlockState state = block.defaultBlockState();
			BlockState likeState = like.defaultBlockState();
			checked += 8;
			if (block.getClass() != piece.kind()) {
				failures.add(label + " is a " + block.getClass().getSimpleName() + ", expected a " + piece.kind().getSimpleName());
			}
			if (block.defaultDestroyTime() != like.defaultDestroyTime()) {
				failures.add(label + " has destroy time " + block.defaultDestroyTime() + ", expected " + like.defaultDestroyTime());
			}
			if (block.getExplosionResistance() != like.getExplosionResistance()) {
				failures.add(label + " has explosion resistance " + block.getExplosionResistance() + ", expected " + like.getExplosionResistance());
			}
			if (state.getSoundType() != likeState.getSoundType()) {
				failures.add(label + " has a different sound type");
			}
			if (block.defaultMapColor() != piece.mapColor()) {
				failures.add(label + " has map colour " + block.defaultMapColor().id + ", expected " + piece.mapColor().id);
			}
			if (state.requiresCorrectToolForDrops() != likeState.requiresCorrectToolForDrops()) {
				failures.add(label + " has requiresCorrectToolForDrops " + state.requiresCorrectToolForDrops() + ", expected " + likeState.requiresCorrectToolForDrops());
			}
			if (state.instrument() != likeState.instrument()) {
				failures.add(label + " has note block instrument " + state.instrument() + ", expected " + likeState.instrument());
			}
			if (state.getPistonPushReaction() != likeState.getPistonPushReaction()) {
				failures.add(label + " has push reaction " + state.getPistonPushReaction() + ", expected " + likeState.getPistonPushReaction());
			}
		}
		// The pillar turns with how it is placed, like the quartz pillar (the owner's answer).
		checked++;
		if (!StoneFamily.CHISELED_STONE_PILLAR.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS)) {
			failures.add("The chiseled stone pillar has no axis property");
		}
		report(helper, IDEA, "block property checks", checked, failures);
	}

	// The buttons are vanilla's stone button: stone's block set type and 20 ticks pressed. The pressure plates are stone's
	// block set type too.
	@GameTest
	public void buttonsAndPlatesAreStoneOnes(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		try {
			Field buttonType = ButtonBlock.class.getDeclaredField("type");
			Field buttonTicks = ButtonBlock.class.getDeclaredField("ticksToStayPressed");
			Field plateType = BasePressurePlateBlock.class.getDeclaredField("type");
			buttonType.setAccessible(true);
			buttonTicks.setAccessible(true);
			plateType.setAccessible(true);
			// Vanilla's stone button shows what the two fields hold.
			int vanillaTicks = (int) buttonTicks.get(Blocks.STONE_BUTTON);
			checked++;
			if (buttonType.get(Blocks.STONE_BUTTON) != BlockSetType.STONE || vanillaTicks != 20) {
				failures.add("Vanilla's stone button is " + buttonType.get(Blocks.STONE_BUTTON) + " for " + vanillaTicks + " ticks, expected the stone type and 20");
			}
			for (Piece piece : PIECES) {
				if (piece.block() instanceof ButtonBlock) {
					checked += 2;
					if (buttonType.get(piece.block()) != BlockSetType.STONE) {
						failures.add(piece.id() + " has block set type " + buttonType.get(piece.block()) + ", expected the stone type");
					}
					if ((int) buttonTicks.get(piece.block()) != 20) {
						failures.add(piece.id() + " stays pressed for " + buttonTicks.get(piece.block()) + " ticks, expected 20");
					}
				} else if (piece.block() instanceof PressurePlateBlock) {
					checked++;
					if (plateType.get(piece.block()) != BlockSetType.STONE) {
						failures.add(piece.id() + " has block set type " + plateType.get(piece.block()) + ", expected the stone type");
					}
				}
			}
		} catch (ReflectiveOperationException exception) {
			failures.add("Could not read a button or pressure plate field: " + exception);
		}
		report(helper, IDEA, "button and pressure plate checks", checked, failures);
	}

	// Each new stairs, slab, wall, fence, button and pressure plate is in the block tags of vanilla's stone piece of its
	// kind, and so is its item in the item tags of that piece's item; each full block and the pillar is in those of the
	// vanilla block it is made like. Which tags those are comes from the vanilla block itself, not from Caramel's files.
	// So a full block blocks rain, snow and the heightmaps (#minecraft:blocks_motion_no_leaves), and a sulfur cube
	// swallows it, as the stone bricks: a review found both missing, while this test checked them for the pickaxe only.
	@GameTest
	public void newBlocksJoinTheTagsOfTheirKind(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Piece piece : PIECES) {
			Block vanilla = TAG_PIECES.getOrDefault(piece.kind(), piece.like());
			String label = BuiltInRegistries.BLOCK.getKey(piece.block()).toString();
			List<TagKey<Block>> blockTags = vanilla.builtInRegistryHolder().tags().filter(StoneFamilyGameTest::isVanillaKindTag).toList();
			List<TagKey<Item>> itemTags = vanilla.asItem().builtInRegistryHolder().tags().filter(StoneFamilyGameTest::isVanillaKindTag).toList();
			// A guard that the vanilla piece is in tags at all, so the loops below check something.
			checked++;
			if (blockTags.isEmpty()) {
				failures.add(BuiltInRegistries.BLOCK.getKey(vanilla) + " is in no block tags");
			}
			for (TagKey<Block> tag : blockTags) {
				checked++;
				if (!piece.block().builtInRegistryHolder().is(tag)) {
					failures.add(label + " is not in block tag #" + tag.location() + ", which " + BuiltInRegistries.BLOCK.getKey(vanilla) + " is in");
				}
			}
			for (TagKey<Item> tag : itemTags) {
				checked++;
				if (!piece.block().asItem().builtInRegistryHolder().is(tag)) {
					failures.add(label + " (item) is not in item tag #" + tag.location() + ", which " + BuiltInRegistries.ITEM.getKey(vanilla.asItem()) + " is in");
				}
			}
		}
		report(helper, IDEA, "tag checks", checked, failures);
	}

	// The new items sit next to their families in the creative inventory, in the order of the Chisel's chain: Smooth
	// Stone's set before Polished Stone's, the pillar and the tiles after the chiseled bricks. Each run is written out, and each
	// new item is in its tab once.
	@GameTest
	public void newItemsSitNextToTheirFamiliesInTheCreativeTabs(GameTestHelper helper) {
		CreativeModeTabs.tryRebuildTabContents(helper.getLevel().enabledFeatures(), false, helper.getLevel().registryAccess());
		List<String> failures = new ArrayList<>();
		int checked = 0;

		List<Item> building = tab(CreativeModeTabs.BUILDING_BLOCKS);
		List<List<Item>> buildingRuns = List.of(
				List.of(Items.STONE_SLAB, StoneFamily.STONE_WALL.asItem()),
				List.of(Items.COBBLESTONE_WALL, StoneFamily.COBBLESTONE_PRESSURE_PLATE.asItem(), StoneFamily.COBBLESTONE_BUTTON.asItem()),
				List.of(Items.MOSSY_COBBLESTONE_WALL, StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE.asItem(), StoneFamily.MOSSY_COBBLESTONE_BUTTON.asItem()),
				List.of(Items.SMOOTH_STONE, StoneFamily.SMOOTH_STONE_STAIRS.asItem()),
				List.of(Items.SMOOTH_STONE_SLAB, StoneFamily.SMOOTH_STONE_WALL.asItem(), StoneFamily.SMOOTH_STONE_PRESSURE_PLATE.asItem(),
						StoneFamily.SMOOTH_STONE_BUTTON.asItem(), StoneFamily.POLISHED_STONE.asItem(), StoneFamily.POLISHED_STONE_STAIRS.asItem(),
						StoneFamily.POLISHED_STONE_SLAB.asItem(), StoneFamily.POLISHED_STONE_WALL.asItem(), StoneFamily.POLISHED_STONE_PRESSURE_PLATE.asItem(),
						StoneFamily.POLISHED_STONE_BUTTON.asItem()),
				List.of(Items.CHISELED_STONE_BRICKS, StoneFamily.CHISELED_STONE_PILLAR.asItem(), StoneFamily.STONE_TILES.asItem(), StoneFamily.CRACKED_STONE_TILES.asItem(),
						StoneFamily.STONE_TILE_STAIRS.asItem(), StoneFamily.STONE_TILE_SLAB.asItem(), StoneFamily.STONE_TILE_WALL.asItem(), StoneFamily.STONE_TILE_FENCE.asItem()));
		for (List<Item> run : buildingRuns) {
			checked++;
			checkRun(failures, "Building Blocks", building, run);
		}
		// The chain itself, in order: Smooth, Polished, Bricks, Cracked Bricks, Chiseled Bricks, the pillar, Tiles, Cracked Tiles.
		List<Item> chain = List.of(Items.SMOOTH_STONE, StoneFamily.POLISHED_STONE.asItem(), Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS, Items.CHISELED_STONE_BRICKS,
				StoneFamily.CHISELED_STONE_PILLAR.asItem(), StoneFamily.STONE_TILES.asItem(), StoneFamily.CRACKED_STONE_TILES.asItem());
		checked++;
		List<Integer> chainPlaces = chain.stream().map(building::indexOf).toList();
		if (chainPlaces.stream().anyMatch(place -> place < 0) || !chainPlaces.stream().sorted().toList().equals(chainPlaces)) {
			failures.add("Building Blocks lists the chain " + chain.stream().map(RecipeTestSupport::name).toList() + " at " + chainPlaces + ", expected in that order");
		}
		for (Block block : StoneFamily.BLOCKS) {
			checked++;
			if (Collections.frequency(building, block.asItem()) != 1) {
				failures.add(name(block.asItem()) + " is in Building Blocks " + Collections.frequency(building, block.asItem()) + " times, expected once");
			}
		}

		// Vanilla lists its stone button and pressure plate among the redstone blocks too.
		List<Item> redstone = tab(CreativeModeTabs.REDSTONE_BLOCKS);
		for (List<Item> run : List.of(
				List.of(Items.STONE_BUTTON, StoneFamily.COBBLESTONE_BUTTON.asItem(), StoneFamily.MOSSY_COBBLESTONE_BUTTON.asItem(), StoneFamily.SMOOTH_STONE_BUTTON.asItem(),
						StoneFamily.POLISHED_STONE_BUTTON.asItem()),
				List.of(Items.STONE_PRESSURE_PLATE, StoneFamily.COBBLESTONE_PRESSURE_PLATE.asItem(), StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE.asItem(),
						StoneFamily.SMOOTH_STONE_PRESSURE_PLATE.asItem(), StoneFamily.POLISHED_STONE_PRESSURE_PLATE.asItem()))) {
			checked++;
			checkRun(failures, "Redstone Blocks", redstone, run);
		}
		for (Block block : List.of(StoneFamily.COBBLESTONE_BUTTON, StoneFamily.MOSSY_COBBLESTONE_BUTTON, StoneFamily.SMOOTH_STONE_BUTTON, StoneFamily.POLISHED_STONE_BUTTON,
				StoneFamily.COBBLESTONE_PRESSURE_PLATE, StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE, StoneFamily.SMOOTH_STONE_PRESSURE_PLATE, StoneFamily.POLISHED_STONE_PRESSURE_PLATE)) {
			checked++;
			if (Collections.frequency(redstone, block.asItem()) != 1) {
				failures.add(name(block.asItem()) + " is in Redstone Blocks " + Collections.frequency(redstone, block.asItem()) + " times, expected once");
			}
		}

		// The Stone Brick item sits after the Brick among the ingredients.
		List<Item> ingredients = tab(CreativeModeTabs.INGREDIENTS);
		checked += 2;
		checkRun(failures, "Ingredients", ingredients, List.of(Items.BRICK, StoneFamily.STONE_BRICK));
		if (Collections.frequency(ingredients, StoneFamily.STONE_BRICK) != 1) {
			failures.add("The Stone Brick is in Ingredients " + Collections.frequency(ingredients, StoneFamily.STONE_BRICK) + " times, expected once");
		}
		report(helper, IDEA, "creative tab placements", checked, failures);
	}

	// The drops of every new block, and of Stone and its set, with an iron pickaxe and with an iron pickaxe enchanted with
	// Silk Touch. A double slab drops 2 either way.
	@GameTest
	public void blocksDropAsTheOwnerSays(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ItemStack plain = new ItemStack(Items.IRON_PICKAXE);
		ItemStack silk = new ItemStack(Items.IRON_PICKAXE);
		silk.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), 1);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		record Tool(String name, ItemStack stack) {
		}
		// Every new block drops itself; the stone wall is the exception, in COBBLED_COUNTERPARTS.
		Map<Block, Item> withoutSilk = new LinkedHashMap<>();
		Map<Block, Item> withSilk = new LinkedHashMap<>();
		for (Block block : StoneFamily.BLOCKS) {
			if (block != StoneFamily.STONE_WALL) {
				withoutSilk.put(block, block.asItem());
				withSilk.put(block, block.asItem());
			}
		}
		COBBLED_COUNTERPARTS.forEach((block, cobbled) -> {
			withoutSilk.put(block, cobbled);
			withSilk.put(block, block.asItem());
		});
		for (Block block : withoutSilk.keySet()) {
			List<BlockState> states = new ArrayList<>(List.of(block.defaultBlockState()));
			if (block instanceof SlabBlock) {
				states.add(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE));
			}
			for (BlockState state : states) {
				int count = state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 2 : 1;
				for (Tool tool : List.of(new Tool("an iron pickaxe", plain), new Tool("a Silk Touch iron pickaxe", silk))) {
					checked++;
					Item expected = (tool.stack() == silk ? withSilk : withoutSilk).get(block);
					Map<Item, Integer> dropped = new TreeMap<>((a, b) -> name(a).compareTo(name(b)));
					Block.getDrops(state, level, POS, null, null, tool.stack()).forEach(stack -> dropped.merge(stack.getItem(), stack.getCount(), Integer::sum));
					if (dropped.size() != 1 || dropped.getOrDefault(expected, 0) != count) {
						failures.add(BuiltInRegistries.BLOCK.getKey(block) + (count == 2 ? " (double)" : "") + " with " + tool.name() + " drops "
								+ dropped.entrySet().stream().map(drop -> drop.getValue() + " " + name(drop.getKey())).toList() + ", expected " + count + " " + name(expected));
					}
				}
			}
		}
		report(helper, IDEA, "drop checks", checked, failures);
	}

	// The owner's chain, each step a 2x2 square of 4 for 4: Stone to Polished Stone, to Stone Bricks, to Stone Tiles.
	// 4 Stone Brick items make 1 Stone Bricks. The Stone Tile Fence is the nether brick fence's recipe, 24 per craft
	// (C-004): 4 Stone Tiles and 2 Stone Brick items.
	@GameTest
	public void theChainIsCraftedFourForFour(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		record Step(Item ingredient, Item result, int count) {
		}
		for (Step step : List.of(new Step(Items.STONE, StoneFamily.POLISHED_STONE.asItem(), 4), new Step(StoneFamily.POLISHED_STONE.asItem(), Items.STONE_BRICKS, 4),
				new Step(Items.STONE_BRICKS, StoneFamily.STONE_TILES.asItem(), 4), new Step(StoneFamily.STONE_BRICK, Items.STONE_BRICKS, 1))) {
			for (Grid grid : placements(2, 2, Collections.nCopies(4, new ItemStack(step.ingredient())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(step.result(), step.count())) {
					failures.add("4 " + name(step.ingredient()) + " " + grid.label() + " gave " + crafted + ", expected " + step.count() + " " + name(step.result()));
				}
			}
		}
		ItemStack tiles = new ItemStack(StoneFamily.STONE_TILES);
		ItemStack brick = new ItemStack(StoneFamily.STONE_BRICK);
		for (Grid grid : placements(3, 2, List.of(tiles, brick, tiles, tiles, brick, tiles))) {
			checked++;
			Crafted crafted = craft(helper, crafting, grid.input());
			if (!crafted.gives(StoneFamily.STONE_TILE_FENCE.asItem(), 24)) {
				failures.add("Stone Tiles and Stone Brick items in the fence shape " + grid.label() + " gave " + crafted + ", expected 24 " + name(StoneFamily.STONE_TILE_FENCE.asItem()));
			}
		}
		report(helper, IDEA, "chain grids", checked, failures);
	}

	// Caramel's new stairs, slabs, walls, buttons and pressure plates follow C-001 to C-008: stairs from 3 of the base in
	// either L for 4, and 4 stairs back into 3 base; slabs from 2 in a row for 4, and 2 stacked back into 1 base; walls
	// from 6 for 12; buttons from 1 for 20; pressure plates from 2 of the base's slab side by side for 16.
	@GameTest
	public void newPiecesFollowTheMorePerCraftRules(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		record Pair(Item base, Item piece) {
		}
		// Stairs, with the base each is crafted from.
		for (Pair stairs : List.of(new Pair(Items.SMOOTH_STONE, StoneFamily.SMOOTH_STONE_STAIRS.asItem()), new Pair(StoneFamily.POLISHED_STONE.asItem(), StoneFamily.POLISHED_STONE_STAIRS.asItem()),
				new Pair(StoneFamily.STONE_TILES.asItem(), StoneFamily.STONE_TILE_STAIRS.asItem()))) {
			for (int empty = 0; empty < 4; empty++) {
				List<ItemStack> l = new ArrayList<>();
				for (int cell = 0; cell < 4; cell++) {
					l.add(cell == empty ? ItemStack.EMPTY : new ItemStack(stairs.base()));
				}
				for (Grid grid : placements(2, 2, l)) {
					checked++;
					Crafted crafted = craft(helper, crafting, grid.input());
					if (!crafted.gives(stairs.piece(), 4)) {
						failures.add("3 " + name(stairs.base()) + " with empty cell " + empty + " " + grid.label() + " gave " + crafted + ", expected 4 " + name(stairs.piece()));
					}
				}
			}
			for (Grid grid : placements(2, 2, Collections.nCopies(4, new ItemStack(stairs.piece())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(stairs.base(), 3)) {
					failures.add("4 " + name(stairs.piece()) + " " + grid.label() + " gave " + crafted + ", expected 3 " + name(stairs.base()));
				}
			}
		}
		// Slabs.
		for (Pair slab : List.of(new Pair(StoneFamily.POLISHED_STONE.asItem(), StoneFamily.POLISHED_STONE_SLAB.asItem()), new Pair(StoneFamily.STONE_TILES.asItem(), StoneFamily.STONE_TILE_SLAB.asItem()))) {
			for (Grid grid : placements(2, 1, Collections.nCopies(2, new ItemStack(slab.base())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(slab.piece(), 4)) {
					failures.add("2 " + name(slab.base()) + " in a row " + grid.label() + " gave " + crafted + ", expected 4 " + name(slab.piece()));
				}
			}
			for (Grid grid : placements(1, 2, Collections.nCopies(2, new ItemStack(slab.piece())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(slab.base(), 1)) {
					failures.add("2 " + name(slab.piece()) + " stacked " + grid.label() + " gave " + crafted + ", expected 1 " + name(slab.base()));
				}
			}
		}
		// Walls.
		for (Pair wall : List.of(new Pair(Items.STONE, StoneFamily.STONE_WALL.asItem()), new Pair(StoneFamily.POLISHED_STONE.asItem(), StoneFamily.POLISHED_STONE_WALL.asItem()),
				new Pair(Items.SMOOTH_STONE, StoneFamily.SMOOTH_STONE_WALL.asItem()), new Pair(StoneFamily.STONE_TILES.asItem(), StoneFamily.STONE_TILE_WALL.asItem()))) {
			for (Grid grid : placements(3, 2, Collections.nCopies(6, new ItemStack(wall.base())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(wall.piece(), 12)) {
					failures.add("6 " + name(wall.base()) + " " + grid.label() + " gave " + crafted + ", expected 12 " + name(wall.piece()));
				}
			}
		}
		// Buttons.
		for (Pair button : List.of(new Pair(Items.COBBLESTONE, StoneFamily.COBBLESTONE_BUTTON.asItem()), new Pair(Items.MOSSY_COBBLESTONE, StoneFamily.MOSSY_COBBLESTONE_BUTTON.asItem()),
				new Pair(Items.SMOOTH_STONE, StoneFamily.SMOOTH_STONE_BUTTON.asItem()), new Pair(StoneFamily.POLISHED_STONE.asItem(), StoneFamily.POLISHED_STONE_BUTTON.asItem()))) {
			for (Grid grid : placements(1, 1, List.of(new ItemStack(button.base())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(button.piece(), 20)) {
					failures.add("1 " + name(button.base()) + " " + grid.label() + " gave " + crafted + ", expected 20 " + name(button.piece()));
				}
			}
		}
		// Pressure plates, from 2 of the slab of the base block side by side.
		for (Pair plate : List.of(new Pair(Items.COBBLESTONE_SLAB, StoneFamily.COBBLESTONE_PRESSURE_PLATE.asItem()), new Pair(Items.MOSSY_COBBLESTONE_SLAB, StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE.asItem()),
				new Pair(Items.SMOOTH_STONE_SLAB, StoneFamily.SMOOTH_STONE_PRESSURE_PLATE.asItem()), new Pair(StoneFamily.POLISHED_STONE_SLAB.asItem(), StoneFamily.POLISHED_STONE_PRESSURE_PLATE.asItem()))) {
			for (Grid grid : placements(2, 1, Collections.nCopies(2, new ItemStack(plate.base())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(plate.piece(), 16)) {
					failures.add("2 " + name(plate.base()) + " side by side " + grid.label() + " gave " + crafted + ", expected 16 " + name(plate.piece()));
				}
			}
		}
		report(helper, IDEA, "new piece grids", checked, failures);
	}

	// The owner's rule: every piece of Stone's, Cobbled Stone's and Cobbled Mossy Stone's sets smelts as its block does, the
	// block of vanilla's smelting of cobblestone into stone. Cobbled Stone's and Cobbled Mossy Stone's stairs, slab, wall,
	// button and pressure plate smelt into Stone's, and Stone's into Smooth Stone's, 15 recipes and the 3 of the blocks.
	// Each takes vanilla's time and experience times what the piece is worth in blocks (a stairs 3/4, a slab and a wall
	// 1/2, a button 1/20, a pressure plate 1/16), the time rounded up. Only the furnace smelts them, and nothing else in
	// the game smelts from or into the family: Polished Stone, crafted, is smelted from nothing and into nothing.
	@GameTest
	public void stoneFamilyPiecesSmeltLikeTheirBlocks(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		Recipe<?> vanillaStone = vanillaRecipes(helper).get(Identifier.withDefaultNamespace("stone"));
		checked++;
		if (!(vanillaStone instanceof SmeltingRecipe vanillaSmelting) || !vanillaSmelting.input().test(new ItemStack(Items.COBBLESTONE))
				|| !vanillaSmelting.assemble(new SingleRecipeInput(new ItemStack(Items.COBBLESTONE))).is(Items.STONE)) {
			failures.add("Vanilla's minecraft:stone is " + vanillaStone + ", expected the smelting of cobblestone into stone");
			report(helper, IDEA, "smelting checks", checked, failures);
			return;
		}
		float experience = vanillaSmelting.experience();
		int cookingTime = vanillaSmelting.cookingTime();

		// What each piece is worth, in the order of the lists below: stairs, slab, wall, button, pressure plate.
		record Worth(int parts, int ofBlock) {
		}
		List<Worth> worths = List.of(new Worth(3, 4), new Worth(1, 2), new Worth(1, 2), new Worth(1, 20), new Worth(1, 16));
		List<Item> cobbled = List.of(Items.COBBLESTONE_STAIRS, Items.COBBLESTONE_SLAB, Items.COBBLESTONE_WALL, StoneFamily.COBBLESTONE_BUTTON.asItem(),
				StoneFamily.COBBLESTONE_PRESSURE_PLATE.asItem());
		List<Item> mossy = List.of(Items.MOSSY_COBBLESTONE_STAIRS, Items.MOSSY_COBBLESTONE_SLAB, Items.MOSSY_COBBLESTONE_WALL, StoneFamily.MOSSY_COBBLESTONE_BUTTON.asItem(),
				StoneFamily.MOSSY_COBBLESTONE_PRESSURE_PLATE.asItem());
		List<Item> stone = List.of(Items.STONE_STAIRS, Items.STONE_SLAB, StoneFamily.STONE_WALL.asItem(), Items.STONE_BUTTON, Items.STONE_PRESSURE_PLATE);
		List<Item> smooth = List.of(StoneFamily.SMOOTH_STONE_STAIRS.asItem(), Items.SMOOTH_STONE_SLAB, StoneFamily.SMOOTH_STONE_WALL.asItem(),
				StoneFamily.SMOOTH_STONE_BUTTON.asItem(), StoneFamily.SMOOTH_STONE_PRESSURE_PLATE.asItem());

		// What smelts into what, with the block's worth: the three blocks, then the 15 pieces. Keyed "ingredient -> result".
		record Smelt(Item ingredient, Item result, Worth worth) {
			String key() {
				return name(ingredient) + " -> " + name(result);
			}
		}
		List<Smelt> expected = new ArrayList<>();
		Worth block = new Worth(1, 1);
		expected.add(new Smelt(Items.COBBLESTONE, Items.STONE, block));
		expected.add(new Smelt(Items.MOSSY_COBBLESTONE, Items.STONE, block));
		expected.add(new Smelt(Items.STONE, Items.SMOOTH_STONE, block));
		for (int piece = 0; piece < worths.size(); piece++) {
			expected.add(new Smelt(cobbled.get(piece), stone.get(piece), worths.get(piece)));
			expected.add(new Smelt(mossy.get(piece), stone.get(piece), worths.get(piece)));
			expected.add(new Smelt(stone.get(piece), smooth.get(piece), worths.get(piece)));
		}
		checked++;
		if (expected.size() != 18 || expected.stream().map(Smelt::key).distinct().count() != 18) {
			failures.add("The test expects " + expected.size() + " distinct smelting recipes, expected the 3 of the blocks and the 15 of the pieces");
		}
		// Each smelts, in a furnace alone, into one of its result, for vanilla's time and experience by its worth.
		for (Smelt smelt : expected) {
			ItemStack input = new ItemStack(smelt.ingredient());
			List<SmeltingRecipe> matching = level.recipeAccess().getRecipes().stream()
					.filter(holder -> holder.value() instanceof SmeltingRecipe recipe && recipe.matches(new SingleRecipeInput(input), level))
					.map(holder -> (SmeltingRecipe) holder.value()).toList();
			checked++;
			if (matching.size() != 1) {
				failures.add(matching.size() + " smelting recipes take " + name(smelt.ingredient()) + ", expected 1");
				continue;
			}
			SmeltingRecipe recipe = matching.getFirst();
			ItemStack result = recipe.assemble(new SingleRecipeInput(input));
			checked += 2;
			if (!result.is(smelt.result()) || result.getCount() != 1) {
				failures.add(name(smelt.ingredient()) + " smelts into " + name(result) + ", expected 1 " + name(smelt.result()));
			}
			int expectedTime = (int) Math.ceil((double) cookingTime * smelt.worth().parts() / smelt.worth().ofBlock());
			double expectedExperience = (double) experience * smelt.worth().parts() / smelt.worth().ofBlock();
			if (recipe.cookingTime() != expectedTime || Math.abs(recipe.experience() - expectedExperience) > 1e-6) {
				failures.add(smelt.key() + " takes " + recipe.cookingTime() + " ticks for " + recipe.experience() + " experience, expected " + expectedTime + " and "
						+ expectedExperience);
			}
		}
		// Nothing else: every cooking recipe of any kind (furnace, blast furnace, smoker, campfire) that takes a piece of the
		// family or makes one, found through the items it accepts, is one of the 18, and a furnace one. Polished Stone is in
		// the family here, so nothing smelts into it or out of it.
		Set<Item> family = new HashSet<>();
		for (List<Item> pieces : List.of(cobbled, mossy, stone, smooth)) {
			family.addAll(pieces);
		}
		family.addAll(List.of(Items.COBBLESTONE, Items.MOSSY_COBBLESTONE, Items.STONE, Items.SMOOTH_STONE, StoneFamily.POLISHED_STONE.asItem(),
				StoneFamily.POLISHED_STONE_STAIRS.asItem(), StoneFamily.POLISHED_STONE_SLAB.asItem(), StoneFamily.POLISHED_STONE_WALL.asItem(),
				StoneFamily.POLISHED_STONE_BUTTON.asItem(), StoneFamily.POLISHED_STONE_PRESSURE_PLATE.asItem()));
		Map<String, List<String>> found = new TreeMap<>();
		for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
			if (!(holder.value() instanceof AbstractCookingRecipe cooking)) {
				continue;
			}
			String type = BuiltInRegistries.RECIPE_TYPE.getKey(cooking.getType()).toString();
			for (Item item : BuiltInRegistries.ITEM) {
				SingleRecipeInput input = new SingleRecipeInput(new ItemStack(item));
				if (cooking.matches(input, level)) {
					Item result = cooking.assemble(input).getItem();
					if (family.contains(item) || family.contains(result)) {
						found.computeIfAbsent(name(item) + " -> " + name(result), key -> new ArrayList<>()).add(type);
					}
				}
			}
		}
		checked++;
		Map<String, List<String>> furnaceOnly = new TreeMap<>();
		expected.forEach(smelt -> furnaceOnly.put(smelt.key(), List.of("minecraft:smelting")));
		if (!found.equals(furnaceOnly)) {
			Set<String> missing = new TreeSet<>(furnaceOnly.keySet());
			missing.removeAll(found.keySet());
			Set<String> stray = new TreeSet<>(found.keySet());
			stray.removeAll(furnaceOnly.keySet());
			failures.add("The cooking recipes that take or make a piece of the Stone family differ from the 18 furnace ones: missing " + missing + ", stray " + stray
					+ ", other types " + found.entrySet().stream().filter(entry -> furnaceOnly.containsKey(entry.getKey()) && !entry.getValue().equals(furnaceOnly.get(entry.getKey()))).toList());
		}
		report(helper, IDEA, "smelting checks", checked, failures);
	}

	// The stonecutter offers every cut the owner's chain lists for a block, offers no result twice, and offers no stairs
	// (C-001). Read as the stonecutter menu reads them. And it offers nothing else: what it offers is vanilla's own cuts
	// of that block, as C-001 and C-008 left them (no stairs, 2 walls a block), and the chain's.
	@GameTest
	public void stonecutterOffersTheChainsCuts(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		Map<Identifier, Recipe<?>> vanilla = vanillaRecipes(helper);
		for (Map.Entry<Item, List<Cut>> blockCuts : CUTS.entrySet()) {
			Item input = blockCuts.getKey();
			ItemStack stack = new ItemStack(input);
			List<ItemStack> options = new ArrayList<>();
			for (SelectableRecipe.SingleInputEntry<StonecutterRecipe> option : helper.getLevel().getServer().getRecipeManager().stonecutterRecipes().selectByInput(stack).entries()) {
				option.recipe().recipe().map(holder -> holder.value().assemble(new SingleRecipeInput(stack))).ifPresent(options::add);
			}
			String offered = options.stream().map(RecipeTestSupport::name).toList().toString();
			for (Cut cut : blockCuts.getValue()) {
				checked++;
				if (options.stream().noneMatch(option -> option.is(cut.result()) && option.getCount() == cut.count())) {
					failures.add("The stonecutter offers " + offered + " for " + name(input) + ", expected " + cut.count() + " " + name(cut.result()));
				}
			}
			checked++;
			Set<Item> results = new HashSet<>();
			Set<Item> twice = new TreeSet<>((a, b) -> name(a).compareTo(name(b)));
			options.forEach(option -> {
				if (!results.add(option.getItem())) {
					twice.add(option.getItem());
				}
			});
			if (!twice.isEmpty()) {
				failures.add("The stonecutter offers " + twice.stream().map(RecipeTestSupport::name).toList() + " twice for " + name(input) + ": " + offered);
			}
			checked++;
			List<String> stairs = options.stream().filter(option -> option.getItem() instanceof BlockItem block && block.getBlock() instanceof StairBlock).map(RecipeTestSupport::name).toList();
			if (!stairs.isEmpty()) {
				failures.add("The stonecutter offers stairs " + stairs + " for " + name(input));
			}
			checked++;
			Set<String> expected = new TreeSet<>();
			blockCuts.getValue().forEach(cut -> expected.add(cut.count() + " " + name(cut.result())));
			vanilla.values().forEach(recipe -> {
				if (recipe instanceof StonecutterRecipe stonecutter && stonecutter.input().acceptsItem(input.builtInRegistryHolder())) {
					ItemStack result = RecipeTestSupport.cut(stonecutter);
					Block block = result.getItem() instanceof BlockItem item ? item.getBlock() : null;
					if (!(block instanceof StairBlock)) {
						expected.add((block instanceof WallBlock ? 2 * result.getCount() : result.getCount()) + " " + name(result.getItem()));
					}
				}
			});
			Set<String> actual = new TreeSet<>();
			options.forEach(option -> actual.add(option.getCount() + " " + name(option.getItem())));
			if (!actual.equals(expected)) {
				failures.add("The stonecutter offers " + actual + " for " + name(input) + ", expected " + expected);
			}
		}
		report(helper, IDEA, "stonecutter checks", checked, failures);
	}

	// Stone Brick items come from Stone Bricks (4), its stairs (3), its slab (2) and its wall (2), and from no other input
	// in the game: the mossy and cracked bricks, the chiseled bricks, the pillar and the tiles give none.
	@GameTest
	public void onlyStoneBricksAndTheirPiecesCutIntoStoneBrickItems(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		Map<Item, Integer> found = new TreeMap<>((a, b) -> name(a).compareTo(name(b)));
		int checked = 0;
		for (RecipeHolder<?> holder : helper.getLevel().recipeAccess().getRecipes()) {
			if (holder.value() instanceof StonecutterRecipe recipe) {
				checked++;
				ItemStack result = cut(recipe);
				if (result.is(StoneFamily.STONE_BRICK)) {
					for (Item input : RecipeTestSupport.accepted(recipe.input())) {
						found.merge(input, result.getCount(), Integer::sum);
					}
				}
			}
		}
		checked++;
		if (!found.equals(BRICKS_CUT_FROM)) {
			failures.add("The stonecutter cuts Stone Brick items from " + found.entrySet().stream().map(cut -> cut.getValue() + " from " + name(cut.getKey())).toList()
					+ ", expected " + BRICKS_CUT_FROM.entrySet().stream().map(cut -> cut.getValue() + " from " + name(cut.getKey())).sorted().toList());
		}
		report(helper, IDEA, "stonecutter recipes", checked, failures);
	}

	// The client draws a block from its blockstate and an item from its item definition; without either it draws the
	// missing model. Under assets/minecraft Caramel changes only the names in lang/en_us.json and Smooth Stone's four
	// models, which take its new texture on every face, each with vanilla's parent; Polished Stone's models wear vanilla's
	// own Smooth Stone textures, as vanilla's models of the same shape do.
	@GameTest
	public void everyNewBlockAndItemShipsItsAssetDefinitions(GameTestHelper helper) {
		ModContainer mod = FabricLoader.getInstance().getModContainer(Caramel.MOD_ID).orElseThrow();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		List<String> items = new ArrayList<>(NEW_BLOCK_NAMES.keySet());
		items.add("stone_brick");
		for (String path : NEW_BLOCK_NAMES.keySet()) {
			checked++;
			if (mod.findPath("assets/caramel/blockstates/" + path + ".json").isEmpty()) {
				failures.add("Caramel ships no assets/caramel/blockstates/" + path + ".json");
			}
		}
		for (String path : items) {
			checked++;
			if (mod.findPath("assets/caramel/items/" + path + ".json").isEmpty()) {
				failures.add("Caramel ships no assets/caramel/items/" + path + ".json");
			}
		}
		Set<String> minecraftFiles = new TreeSet<>();
		for (Path root : mod.getRootPaths()) {
			Path minecraft = root.resolve("assets/minecraft");
			if (Files.isDirectory(minecraft)) {
				try (Stream<Path> files = Files.walk(minecraft)) {
					files.filter(Files::isRegularFile).forEach(file -> minecraftFiles.add(minecraft.relativize(file).toString().replace('\\', '/')));
				} catch (IOException exception) {
					failures.add("Could not list assets/minecraft: " + exception);
				}
			}
		}
		Set<String> expected = new TreeSet<>(Set.of("lang/en_us.json"));
		SMOOTH_STONE_MODELS.forEach(model -> expected.add("models/block/" + model + ".json"));
		checked++;
		if (!minecraftFiles.equals(expected)) {
			failures.add("Caramel ships " + minecraftFiles + " under assets/minecraft, expected " + expected);
		}
		for (String model : SMOOTH_STONE_MODELS) {
			JsonObject vanilla = vanillaModel(model);
			JsonObject caramel = model(mod, "minecraft", model);
			checked++;
			if (vanilla == null || caramel == null || !vanilla.get("parent").equals(caramel.get("parent"))
					|| !caramel.getAsJsonObject("textures").entrySet().stream().allMatch(texture -> texture.getValue().getAsString().equals("caramel:block/smooth_stone"))) {
				failures.add("Caramel's minecraft:block/" + model + " is " + caramel + ", expected vanilla's parent " + (vanilla == null ? null : vanilla.get("parent"))
						+ " and caramel:block/smooth_stone on every face");
			}
			JsonObject polished = model(mod, Caramel.MOD_ID, model.replace("smooth_stone", "polished_stone"));
			checked++;
			if (vanilla == null || polished == null || !vanilla.equals(polished)) {
				failures.add("Caramel's polished stone model for " + model + " is " + polished + ", expected vanilla's " + vanilla);
			}
		}
		// The owner's playtest suggestion: Polished Stone Stairs wear vanilla's smooth stone slab's textures, the lined side
		// and the plain top and bottom.
		JsonObject slabModel = vanillaModel("smooth_stone_slab");
		for (String stairs : List.of("polished_stone_stairs", "polished_stone_stairs_inner", "polished_stone_stairs_outer")) {
			JsonObject model = model(mod, Caramel.MOD_ID, stairs);
			checked++;
			if (slabModel == null || model == null || !slabModel.get("textures").equals(model.get("textures"))) {
				failures.add("Caramel's " + stairs + " model is " + model + ", expected the textures of vanilla's smooth stone slab " + (slabModel == null ? null : slabModel.get("textures")));
			}
		}
		// Smooth Stone's button and pressure plate wear its new texture, which the artist delivered, like its stairs and wall,
		// each model with the parent of vanilla's stone button's or stone pressure plate's model of the same name.
		checked++;
		if (mod.findPath("assets/caramel/textures/block/smooth_stone.png").isEmpty()) {
			failures.add("Caramel ships no assets/caramel/textures/block/smooth_stone.png");
		}
		for (String smoothModel : List.of("smooth_stone_button", "smooth_stone_button_pressed", "smooth_stone_button_inventory", "smooth_stone_pressure_plate",
				"smooth_stone_pressure_plate_down")) {
			JsonObject model = model(mod, Caramel.MOD_ID, smoothModel);
			JsonObject vanilla = vanillaModel(smoothModel.replace("smooth_stone", "stone"));
			checked++;
			if (model == null || vanilla == null || !vanilla.get("parent").equals(model.get("parent")) || !model.has("textures")
					|| model.getAsJsonObject("textures").entrySet().isEmpty()
					|| !model.getAsJsonObject("textures").entrySet().stream().allMatch(texture -> texture.getValue().getAsString().equals("caramel:block/smooth_stone"))) {
				failures.add("Caramel's " + smoothModel + " model is " + model + ", expected vanilla's parent " + (vanilla == null ? null : vanilla.get("parent"))
						+ " and caramel:block/smooth_stone on every texture");
			}
		}
		report(helper, IDEA, "asset checks", checked, failures);
	}

	// The one vanilla recipe the chain replaces is the one the owner names: Stone Bricks crafted from 4 Stone in a 2x2
	// square.
	@GameTest
	public void theReplacedRecipeIsVanillasFourStone(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Identifier, Recipe<?>> vanilla = vanillaRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		Recipe<?> bricks = vanilla.get(Identifier.withDefaultNamespace("stone_bricks"));
		checked++;
		CraftingInput fourStone = CraftingInput.of(2, 2, Collections.nCopies(4, new ItemStack(Items.STONE)));
		if (!(bricks instanceof CraftingRecipe crafting) || !crafting.matches(fourStone, level) || !crafting.assemble(fourStone).is(Items.STONE_BRICKS)
				|| crafting.assemble(fourStone).getCount() != 4) {
			failures.add("Vanilla's minecraft:stone_bricks is " + bricks + ", expected 4 stone bricks from 4 stone in a 2x2 square");
		}
		checked++;
		if (!REPLACED_RECIPES.equals(Set.of(Identifier.withDefaultNamespace("stone_bricks")))) {
			failures.add("REPLACED_RECIPES holds " + REPLACED_RECIPES);
		}
		report(helper, IDEA, "replaced recipe checks", checked, failures);
	}

	// The loot tables in the minecraft namespace that C-010 replaces: those of vanilla's Stone family's variants. Stone
	// itself already drops cobblestone in vanilla.
	static Set<Identifier> vanillaStoneLootTables() {
		Set<Identifier> tables = new TreeSet<>();
		for (BlockFamily.Variant variant : List.of(BlockFamily.Variant.STAIRS, BlockFamily.Variant.SLAB, BlockFamily.Variant.BUTTON, BlockFamily.Variant.PRESSURE_PLATE)) {
			Block block = BlockFamilies.STONE.get(variant);
			if (block == null) {
				throw new IllegalStateException("Vanilla's stone family has no " + variant);
			}
			tables.add(block.getLootTable().orElseThrow().identifier());
		}
		return tables;
	}

	// A vanilla block model, read from the client jar on the test classpath: the server's minecraft mod container holds
	// no assets, and the class loader also finds Caramel's own copy at the same path.
	private static @Nullable JsonObject vanillaModel(String path) {
		try {
			Enumeration<URL> urls = StoneFamilyGameTest.class.getClassLoader().getResources("assets/minecraft/models/block/" + path + ".json");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url.toString().contains("minecraft-clientOnly")) {
					try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
						return JsonParser.parseReader(reader).getAsJsonObject();
					}
				}
			}
		} catch (IOException exception) {
			return null;
		}
		return null;
	}

	// A block model as a mod ships it, or null.
	private static @Nullable JsonObject model(ModContainer mod, String namespace, String path) {
		return mod.findPath("assets/" + namespace + "/models/block/" + path + ".json").map(file -> {
			try (Reader reader = Files.newBufferedReader(file)) {
				return JsonParser.parseReader(reader).getAsJsonObject();
			} catch (IOException exception) {
				return null;
			}
		}).orElse(null);
	}

	private static boolean isVanillaKindTag(TagKey<?> tag) {
		return tag.location().getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && !TAGS_OF_THE_VANILLA_PIECE_ONLY.contains(tag.location());
	}

	// A creative tab's items in order, as a server lists them.
	private static List<Item> tab(ResourceKey<CreativeModeTab> tab) {
		return BuiltInRegistries.CREATIVE_MODE_TAB.getValue(tab).getDisplayItems().stream().map(ItemStack::getItem).toList();
	}

	// Fails unless the run's items are in the tab one right after the other, in order.
	private static void checkRun(List<String> failures, String tabName, List<Item> tab, List<Item> run) {
		int start = tab.indexOf(run.getFirst());
		boolean inPlace = start >= 0 && start + run.size() <= tab.size() && tab.subList(start, start + run.size()).equals(run);
		if (!inPlace) {
			int end = Math.min(tab.size(), Math.max(start, 0) + run.size() + 1);
			failures.add(tabName + " lists " + (start < 0 ? "no " + name(run.getFirst()) : tab.subList(start, end).stream().map(RecipeTestSupport::name).toList())
					+ ", expected the run " + run.stream().map(RecipeTestSupport::name).toList());
		}
	}
}
