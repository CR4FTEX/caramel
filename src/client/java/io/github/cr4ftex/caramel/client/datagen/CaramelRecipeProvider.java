package io.github.cr4ftex.caramel.client.datagen;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.jspecify.annotations.Nullable;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.block.StoneFamily;
import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public class CaramelRecipeProvider extends FabricRecipeProvider {
	// C-001's scope in Minecraft 26.3. If an update changes either count, datagen stops so the scope gets rechecked.
	private static final int STAIRS_FAMILIES = 97;
	private static final int STONECUTTER_STAIRS_RECIPES = 103;
	// C-002's scope, checked the same way. The smooth stone slab, crafted outside any family, is the 100th slab. The
	// waxed chiseled coppers, the purpur pillar and bamboo mosaic are removed by name.
	private static final int SLAB_FAMILIES = 99;
	private static final int CHISELED_FAMILIES = 16;
	// C-003's: the slab families with a pressure plate or a carpet. The weighted pressure plates and the moss carpets are
	// made by name.
	private static final int PRESSURE_PLATE_FAMILIES = 15;
	private static final int CARPET_FAMILIES = 16;
	// C-004's: the families with a fence, the 13 wooden ones (bamboo's is a custom fence) and nether bricks.
	private static final int FENCE_FAMILIES = 14;
	// C-005's: the families with a fence gate, the 13 wooden ones (bamboo's is a custom fence gate).
	private static final int FENCE_GATE_FAMILIES = 13;
	// C-006's: the families with a door and a trapdoor, the 13 wooden ones. The iron and copper doors and trapdoors are
	// made by name.
	private static final int DOOR_FAMILIES = 13;
	private static final int TRAPDOOR_FAMILIES = 13;
	// C-007's: the families with a button, the 13 wooden ones, stone and polished blackstone.
	private static final int BUTTON_FAMILIES = 15;
	// C-008's: the families with a wall, and the stonecutter recipes that cut a wall, 32 from the wall's own block and 25
	// from another.
	private static final int WALL_FAMILIES = 32;
	private static final int STONECUTTER_WALL_RECIPES = 57;
	// C-009's: the families with a cracked block, made by smelting, and with a cut block, 4 crafted from 4: sandstone,
	// red sandstone and copper in its 8 states. The quartz pillar is removed by name.
	private static final int CRACKED_FAMILIES = 5;
	private static final int CUT_FAMILIES = 10;
	// C-010's: the owner's yield at every step up the Stone family's chain, and the Stone Brick items the stonecutter
	// cuts from a block of Stone Bricks, its stairs, its slab and its wall.
	private static final int CHAIN_YIELD = 4;
	private static final int BRICKS_PER_BLOCK = 4;
	private static final int BRICKS_PER_STAIRS = 3;
	private static final int BRICKS_PER_SLAB = 2;
	private static final int BRICKS_PER_WALL = 2;
	// What a piece smelts in, by what it is worth in blocks: of vanilla's cobblestone into stone, 200 ticks and 0.1
	// experience a block, a stair is worth 3/4 (C-001), a slab and a wall 1/2 (C-002, C-008), a button 1/20 (C-007) and
	// a pressure plate 1/16 (C-003). The recipe category is the one the piece's crafting recipe uses.
	private static final int SMELTING_TICKS = 200;
	private static final float SMELTING_EXPERIENCE = 0.1F;
	private static final List<SmeltedPiece> SMELTED_PIECES = List.of(
			new SmeltedPiece(BlockFamily.Variant.STAIRS, RecipeCategory.BUILDING_BLOCKS, 3, 4),
			new SmeltedPiece(BlockFamily.Variant.SLAB, RecipeCategory.BUILDING_BLOCKS, 1, 2),
			new SmeltedPiece(BlockFamily.Variant.WALL, RecipeCategory.DECORATIONS, 1, 2),
			new SmeltedPiece(BlockFamily.Variant.BUTTON, RecipeCategory.REDSTONE, 1, 20),
			new SmeltedPiece(BlockFamily.Variant.PRESSURE_PLATE, RecipeCategory.REDSTONE, 1, 16));

	private record SmeltedPiece(BlockFamily.Variant variant, RecipeCategory category, int parts, int ofBlock) {
	}

	public CaramelRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	// The default moves every id into the caramel namespace, so nothing would replace vanilla. With the ids kept,
	// a save without a key lands on the result item's vanilla id: every save below passes an explicit key.
	@Override
	protected Identifier getRecipeIdentifier(Identifier identifier) {
		return identifier;
	}

	@Override
	public String getName() {
		return "Caramel recipes";
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
		return new RecipeProvider(recipes, advancements) {
			// Vanilla crafts these stairs and slabs from more than one block, listed in vanilla's order.
			private final Map<Block, List<Block>> alternatives = Map.of(
					Blocks.PURPUR_STAIRS, List.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR),
					Blocks.QUARTZ_STAIRS, List.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR),
					Blocks.SANDSTONE_STAIRS, List.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE),
					Blocks.RED_SANDSTONE_STAIRS, List.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE),
					Blocks.PURPUR_SLAB, List.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR),
					Blocks.QUARTZ_SLAB, List.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR),
					Blocks.SANDSTONE_SLAB, List.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE),
					Blocks.RED_SANDSTONE_SLAB, List.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE));
			// Written at their vanilla ids with Fabric's always-false condition, which removes the recipe and its unlock
			// advancement: an advancement naming a missing recipe makes every world fail to load with "Unbound values in registry".
			private RecipeOutput removed;
			private int removedStonecutterStairs;
			private int stonecutterWalls;

			@Override
			public void buildRecipes() {
				removed = CaramelRecipeProvider.this.withConditions(output, ResourceConditions.alwaysFalse());
				replaceStairs();
				List<BlockFamily> slabFamilies = replaceSlabs();
				pressurePlatesAndCarpets(slabFamilies);
				fences();
				fenceGates();
				doorsAndTrapdoors();
				buttons();
				walls();
				stonecutter();
				chisels();
				carvedBlocks();
				stoneFamily();
			}

			// C-001: 3 blocks in an L make 4 stairs and 4 stairs make 3 blocks. stonecutter() removes the stonecutter's stairs.
			private void replaceStairs() {
				List<BlockFamily> families = BlockFamilies.getAllFamilies()
						.filter(family -> family.getVariants().containsKey(BlockFamily.Variant.STAIRS))
						.sorted(Comparator.comparing(family -> BuiltInRegistries.BLOCK.getKey(family.get(BlockFamily.Variant.STAIRS))))
						.toList();
				if (families.size() != STAIRS_FAMILIES) {
					throw new IllegalStateException("Expected " + STAIRS_FAMILIES + " block families with stairs, found " + families.size());
				}
				families.forEach(this::stairs);
			}

			// C-002: 2 blocks in a row make 4 slabs and 2 stacked slabs make 1 block. The vanilla recipes 2 stacked slabs
			// would also match are removed: the chiseled blocks, purpur pillar and bamboo mosaic. The pressure plates and
			// carpets that 2 blocks in a row made get their C-003 recipes instead.
			private List<BlockFamily> replaceSlabs() {
				List<BlockFamily> families = BlockFamilies.getAllFamilies()
						.filter(family -> family.getVariants().containsKey(BlockFamily.Variant.SLAB))
						.sorted(Comparator.comparing(family -> BuiltInRegistries.BLOCK.getKey(family.get(BlockFamily.Variant.SLAB))))
						.toList();
				if (families.size() != SLAB_FAMILIES) {
					throw new IllegalStateException("Expected " + SLAB_FAMILIES + " block families with slabs, found " + families.size());
				}
				families.forEach(family -> slabRecipes(family, family.get(BlockFamily.Variant.SLAB), family.getBaseBlock()));
				slabRecipes(null, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);

				int chiseledBlocks = 0;
				for (BlockFamily family : families) {
					Block slab = family.get(BlockFamily.Variant.SLAB);
					if (family.get(BlockFamily.Variant.CHISELED) instanceof Block chiseled) {
						remove(chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, chiseled, Ingredient.of(slab))
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.CHISELED, slab), has(slab)), chiseled);
						chiseledBlocks++;
					}
				}
				if (chiseledBlocks != CHISELED_FAMILIES) {
					throw new IllegalStateException("Expected " + CHISELED_FAMILIES + " slab families with chiseled blocks, found " + chiseledBlocks);
				}
				// The block families hold the unwaxed chiseled copper only. Vanilla writes the waxed ones in a 3-wide pattern.
				WeatheringCopperCollection.zipApply(Blocks.CHISELED_COPPER.waxed(), Blocks.CUT_COPPER_SLAB.waxed(), (chiseled, slab) ->
						remove(shaped(RecipeCategory.BUILDING_BLOCKS, chiseled)
								.define('M', slab)
								.pattern(" M ")
								.pattern(" M ")
								.group(getItemName(chiseled))
								.unlockedBy(getHasName(slab), has(slab)), chiseled));
				remove(chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR, Ingredient.of(Blocks.PURPUR_SLAB))
						.unlockedBy(getHasName(Blocks.PURPUR_BLOCK), has(Blocks.PURPUR_BLOCK)), Blocks.PURPUR_PILLAR);
				remove(chiseledBuilder(RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Ingredient.of(Blocks.BAMBOO_SLAB))
						.unlockedBy(getHasName(Blocks.BAMBOO_SLAB), has(Blocks.BAMBOO_SLAB)), Blocks.BAMBOO_MOSAIC);
				return families;
			}

			// C-003: 2 slabs side by side make 16 pressure plates or carpets, 2 ingots side by side 16 weighted pressure
			// plates, and 2 moss or pale moss blocks 32 moss carpets. Each replaces the vanilla recipe at its id (C-002
			// would otherwise remove all but the moss carpets) and keeps vanilla's group and unlock criteria.
			private void pressurePlatesAndCarpets(List<BlockFamily> slabFamilies) {
				int plates = 0;
				int carpets = 0;
				for (BlockFamily family : slabFamilies) {
					Block base = family.getBaseBlock();
					Block slab = family.get(BlockFamily.Variant.SLAB);
					if (family.get(BlockFamily.Variant.PRESSURE_PLATE) != null) {
						pressurePlate(family, slab);
						plates++;
					}
					if (family.get(BlockFamily.Variant.CARPET) instanceof Block carpet) {
						sixteenFrom(RecipeCategory.DECORATIONS, carpet, slab, family, BlockFamily.Variant.CARPET)
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.CARPET, base), has(base))
								.save(output, vanillaKey(carpet));
						carpets++;
					}
				}
				if (plates != PRESSURE_PLATE_FAMILIES || carpets != CARPET_FAMILIES) {
					throw new IllegalStateException("Expected " + PRESSURE_PLATE_FAMILIES + " slab families with pressure plates and " + CARPET_FAMILIES
							+ " with carpets, found " + plates + " and " + carpets);
				}
				Map.of(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT).forEach((plate, ingot) ->
						sixteenFrom(RecipeCategory.REDSTONE, plate, ingot, null, BlockFamily.Variant.PRESSURE_PLATE)
								.unlockedBy(getHasName(ingot), has(ingot))
								.save(output, vanillaKey(plate)));
				// The moss blocks have no slabs, so their carpets keep 2 blocks side by side and give 32, 16 per block.
				Map.of(Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK, Blocks.PALE_MOSS_CARPET, Blocks.PALE_MOSS_BLOCK).forEach((carpet, moss) ->
						shaped(RecipeCategory.DECORATIONS, carpet, 32)
								.define('#', moss)
								.pattern("##")
								.group("carpet")
								.unlockedBy(getHasName(moss), has(moss))
								.save(output, vanillaKey(carpet)));
			}

			// C-004: every fence recipe gives 4 times vanilla's count, 12 wooden fences or 24 nether brick fences. The recipe
			// is vanilla's otherwise, built as vanilla's fenceBuilder builds it, and replaces it at its id.
			private void fences() {
				eachFamilyWith(List.of(BlockFamily.Variant.FENCE, BlockFamily.Variant.CUSTOM_FENCE), FENCE_FAMILIES, "fences", (family, variant) -> {
					Block fence = family.get(variant);
					Block base = family.getBaseBlock();
					boolean netherBrick = fence == Blocks.NETHER_BRICK_FENCE;
					inFamilyGroup(shaped(RecipeCategory.DECORATIONS, fence, netherBrick ? 24 : 12)
							.define('W', base)
							.define('#', netherBrick ? Items.NETHER_BRICK : Items.STICK)
							.pattern("W#W")
							.pattern("W#W"), family, variant)
							.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
							.save(output, vanillaKey(fence));
				});
			}

			// C-005: every fence gate recipe gives 4 instead of 1. The recipe is vanilla's otherwise, built as vanilla's
			// fenceGateBuilder builds it, and replaces it at its id.
			private void fenceGates() {
				eachFamilyWith(List.of(BlockFamily.Variant.FENCE_GATE, BlockFamily.Variant.CUSTOM_FENCE_GATE), FENCE_GATE_FAMILIES, "fence gates", (family, variant) -> {
					Block gate = family.get(variant);
					Block base = family.getBaseBlock();
					inFamilyGroup(shaped(RecipeCategory.REDSTONE, gate, 4)
							.define('#', Items.STICK)
							.define('W', base)
							.pattern("#W#")
							.pattern("#W#"), family, variant)
							.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
							.save(output, vanillaKey(gate));
				});
			}

			// C-006: 6 planks make 16 doors in 2 columns, or 16 trapdoors in 2 rows, at the vanilla ids. The iron and copper
			// doors and trapdoors take the same shapes, made of 6 Blocks of Iron or Copper instead of ingots, and keep vanilla's
			// unlock on the ingot. Only the plain Block of Copper makes them; the waxing recipes stay 1 for 1.
			private void doorsAndTrapdoors() {
				eachFamilyWith(List.of(BlockFamily.Variant.DOOR), DOOR_FAMILIES, "doors", (family, variant) ->
						door(family.get(variant), family.getBaseBlock(), family, variant)
								.unlockedBy(getCraftingCriterionName(family, variant, family.getBaseBlock()), has(family.getBaseBlock()))
								.save(output, vanillaKey(family.get(variant))));
				eachFamilyWith(List.of(BlockFamily.Variant.TRAPDOOR), TRAPDOOR_FAMILIES, "trapdoors", (family, variant) ->
						trapdoor(family.get(variant), family.getBaseBlock(), family, variant)
								.unlockedBy(getCraftingCriterionName(family, variant, family.getBaseBlock()), has(family.getBaseBlock()))
								.save(output, vanillaKey(family.get(variant))));
				fromBlocks(Items.IRON_INGOT, Blocks.IRON_BLOCK, Blocks.IRON_DOOR, Blocks.IRON_TRAPDOOR);
				fromBlocks(Items.COPPER_INGOT, Blocks.COPPER_BLOCK.weathering().unaffected(), Blocks.COPPER_DOOR.weathering().unaffected(),
						Blocks.COPPER_TRAPDOOR.weathering().unaffected());
			}

			// A metal door and trapdoor in the wooden shapes, from 6 blocks, unlocking on the ingot as vanilla's do.
			private void fromBlocks(Item ingot, Block block, Block door, Block trapdoor) {
				door(door, block, null, BlockFamily.Variant.DOOR).unlockedBy(getHasName(ingot), has(ingot)).save(output, vanillaKey(door));
				trapdoor(trapdoor, block, null, BlockFamily.Variant.TRAPDOOR).unlockedBy(getHasName(ingot), has(ingot)).save(output, vanillaKey(trapdoor));
			}

			// C-007: every button recipe gives 20 instead of 1. The recipe is vanilla's otherwise, built as vanilla's
			// buttonBuilder builds it, and replaces it at its id.
			private void buttons() {
				eachFamilyWith(List.of(BlockFamily.Variant.BUTTON), BUTTON_FAMILIES, "buttons", this::button);
			}

			private void button(BlockFamily family, BlockFamily.Variant variant) {
				Block button = family.get(variant);
				Block base = family.getBaseBlock();
				inFamilyGroup(shapeless(RecipeCategory.REDSTONE, button, 20).requires(base), family, variant)
						.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
						.save(output, vanillaKey(button));
			}

			// C-008: every wall recipe gives 12 instead of 6. The recipe is vanilla's otherwise, built as vanilla's wallBuilder
			// builds it, and replaces it at its id. stonecutter() makes the stonecutter's walls give 2 per block.
			private void walls() {
				eachFamilyWith(List.of(BlockFamily.Variant.WALL), WALL_FAMILIES, "walls", this::wall);
			}

			private void wall(BlockFamily family, BlockFamily.Variant variant) {
				Block wall = family.get(variant);
				Block base = family.getBaseBlock();
				inFamilyGroup(shaped(RecipeCategory.DECORATIONS, wall, 12)
						.define('#', base)
						.pattern("###")
						.pattern("###"), family, variant)
						.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
						.save(output, vanillaKey(wall));
			}

			// Vanilla's stonecutter walk, which reaches the recipes that cut a variant from another family's block too, such
			// as stone into a stone brick wall. stonecutterResultFromBase below writes only the stairs (C-001) and walls
			// (C-008), and datagen stops unless both counts are the ones in docs/DESIGN.md.
			private void stonecutter() {
				BlockFamilies.getAllFamilies()
						.filter(BlockFamily::shouldGenerateStonecutterRecipe)
						.forEach(family -> family.getVariants().keySet().forEach(variant -> generateStonecutterRecipe(family, variant, family.getBaseBlock())));
				WeatheringCopperCollection.zipApply(Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK,
						(stairs, copper) -> stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, stairs, copper, 4));
				if (removedStonecutterStairs != STONECUTTER_STAIRS_RECIPES || stonecutterWalls != STONECUTTER_WALL_RECIPES) {
					throw new IllegalStateException("Expected " + STONECUTTER_STAIRS_RECIPES + " stonecutter recipes that make stairs and " + STONECUTTER_WALL_RECIPES
							+ " that make walls, found " + removedStonecutterStairs + " and " + stonecutterWalls);
				}
			}

			// C-009: a Chisel is its tier's material above any planks, which fits the inventory's 2x2 grid; the planks
			// take the stick's place. The Flint Chisel unlocks on having a flint, and the others on what their tier's
			// vanilla pickaxe unlocks on. The Netherite Chisel is the Diamond Chisel upgraded on a smithing table, like
			// vanilla's netherite tools.
			private void chisels() {
				chisel(Chisels.FLINT_CHISEL, Ingredient.of(Items.FLINT), getHasName(Items.FLINT), has(Items.FLINT));
				chisel(Chisels.STONE_CHISEL, tag(ItemTags.STONE_TOOL_MATERIALS), "has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS));
				chisel(Chisels.COPPER_CHISEL, tag(ItemTags.COPPER_TOOL_MATERIALS), "has_copper_ingot", has(ItemTags.COPPER_TOOL_MATERIALS));
				chisel(Chisels.IRON_CHISEL, tag(ItemTags.IRON_TOOL_MATERIALS), "has_iron_ingot", has(ItemTags.IRON_TOOL_MATERIALS));
				chisel(Chisels.GOLDEN_CHISEL, tag(ItemTags.GOLD_TOOL_MATERIALS), "has_gold_ingot", has(ItemTags.GOLD_TOOL_MATERIALS));
				chisel(Chisels.DIAMOND_CHISEL, tag(ItemTags.DIAMOND_TOOL_MATERIALS), "has_diamond", has(ItemTags.DIAMOND_TOOL_MATERIALS));
				SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(Chisels.DIAMOND_CHISEL.item()),
								tag(ItemTags.NETHERITE_TOOL_MATERIALS), RecipeCategory.TOOLS, Chisels.NETHERITE_CHISEL.item())
						.unlocks("has_netherite_ingot", has(ItemTags.NETHERITE_TOOL_MATERIALS))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getItemName(Chisels.NETHERITE_CHISEL.item()) + "_smithing")));
			}

			private void chisel(Chisels.Chisel chisel, Ingredient head, String criterionName, Criterion<?> criterion) {
				shaped(RecipeCategory.TOOLS, chisel.item())
						.define('X', head)
						.define('#', ItemTags.PLANKS)
						.pattern("X")
						.pattern("#")
						.unlockedBy(criterionName, criterion)
						.save(output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(chisel.item())));
			}

			// C-009: the Chisel's right click makes the cracked blocks now, so their smelting recipes go. The crafting
			// recipes that cut 4 blocks into 4 cut blocks, and 2 quartz blocks into 2 pillars, go as well: the
			// stonecutter still makes those. C-002 already removed the chiseled blocks' and the purpur pillar's.
			private void carvedBlocks() {
				eachFamilyWith(List.of(BlockFamily.Variant.CRACKED), CRACKED_FAMILIES, "cracked blocks", (family, variant) -> {
					Block base = family.getBaseBlock();
					remove(SimpleCookingRecipeBuilder.smelting(Ingredient.of(base), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, family.get(variant), 0.1F, 200)
							.unlockedBy(getHasName(base), has(base)), family.get(variant));
				});
				eachFamilyWith(List.of(BlockFamily.Variant.CUT), CUT_FAMILIES, "cut blocks", (family, variant) -> {
					Block base = family.getBaseBlock();
					remove(cutBuilder(RecipeCategory.BUILDING_BLOCKS, family.get(variant), Ingredient.of(base))
							.unlockedBy(getHasName(base), has(base)), family.get(variant));
				});
				remove(shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, 2)
						.define('#', Blocks.QUARTZ_BLOCK)
						.pattern("#")
						.pattern("#")
						.unlockedBy(getHasName(Blocks.QUARTZ_BLOCK), has(Blocks.QUARTZ_BLOCK)), Blocks.QUARTZ_PILLAR);
			}

			// C-010: the Stone family. Caramel's new stairs, slabs, walls, buttons and pressure plates get the recipes C-001
			// to C-008 give vanilla's. Up the owner's chain, each step is 4 blocks in a 2x2 square for 4: Stone to the new
			// Polished Stone, to Stone Bricks, to Stone Tiles. Smooth Stone stays vanilla's, smelted from Stone.
			private void stoneFamily() {
				for (BlockFamily family : CaramelBlockFamilies.ALL) {
					Block base = family.getBaseBlock();
					if (family.get(BlockFamily.Variant.STAIRS) != null) {
						stairs(family);
					}
					if (family.get(BlockFamily.Variant.SLAB) instanceof Block slab) {
						slabRecipes(family, slab, base);
					}
					if (family.get(BlockFamily.Variant.WALL) != null) {
						wall(family, BlockFamily.Variant.WALL);
					}
					if (family.get(BlockFamily.Variant.BUTTON) != null) {
						button(family, BlockFamily.Variant.BUTTON);
					}
					if (family.get(BlockFamily.Variant.PRESSURE_PLATE) != null) {
						Block slab = family.get(BlockFamily.Variant.SLAB);
						pressurePlate(family, slab != null ? slab : CaramelBlockFamilies.VANILLA_SLABS.get(base));
					}
				}
				twoByTwo(StoneFamily.POLISHED_STONE, Blocks.STONE, CHAIN_YIELD)
						.unlockedBy(getHasName(Blocks.STONE), has(Blocks.STONE))
						.save(output, vanillaKey(StoneFamily.POLISHED_STONE));
				// At vanilla's id, replacing its 4 Stone into Stone Bricks, and keeping its unlock on Stone.
				twoByTwo(Blocks.STONE_BRICKS, StoneFamily.POLISHED_STONE, CHAIN_YIELD)
						.unlockedBy(getHasName(Blocks.STONE), has(Blocks.STONE))
						.save(output, vanillaKey(Blocks.STONE_BRICKS));
				twoByTwo(StoneFamily.STONE_TILES, Blocks.STONE_BRICKS, CHAIN_YIELD)
						.unlockedBy(getHasName(Blocks.STONE_BRICKS), has(Blocks.STONE_BRICKS))
						.save(output, vanillaKey(StoneFamily.STONE_TILES));
				twoByTwo(Blocks.STONE_BRICKS, StoneFamily.STONE_BRICK, 1)
						.unlockedBy(getHasName(StoneFamily.STONE_BRICK), has(StoneFamily.STONE_BRICK))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getConversionRecipeName(Blocks.STONE_BRICKS, StoneFamily.STONE_BRICK))));
				// The nether brick fence's recipe, with C-004's count for it.
				shaped(RecipeCategory.DECORATIONS, StoneFamily.STONE_TILE_FENCE, 24)
						.define('W', StoneFamily.STONE_TILES)
						.define('#', StoneFamily.STONE_BRICK)
						.pattern("W#W")
						.pattern("W#W")
						.unlockedBy(getHasName(StoneFamily.STONE_TILES), has(StoneFamily.STONE_TILES))
						.save(output, vanillaKey(StoneFamily.STONE_TILE_FENCE));
				// As vanilla smelts Cobblestone into Stone, and each piece as its block: its stairs, slab, wall, button and
				// pressure plate. Vanilla's smelting of Smooth Stone from Stone is its own.
				smeltingLikeStone(Blocks.STONE, Blocks.MOSSY_COBBLESTONE, RecipeCategory.BUILDING_BLOCKS, 1, 1);
				smeltPieces(CaramelBlockFamilies.COBBLESTONE, BlockFamilies.COBBLESTONE, CaramelBlockFamilies.STONE, BlockFamilies.STONE);
				smeltPieces(CaramelBlockFamilies.MOSSY_COBBLESTONE, BlockFamilies.MOSSY_COBBLESTONE, CaramelBlockFamilies.STONE, BlockFamilies.STONE);
				smeltPieces(CaramelBlockFamilies.STONE, BlockFamilies.STONE, CaramelBlockFamilies.SMOOTH_STONE, null);
				// Each block cuts into the blocks after it in the Chisel's chain, with their slabs and walls, as vanilla's
				// stonecutter cuts deepslate. Vanilla's own such recipes stay as they are and are not written again.
				cutEach(Blocks.STONE, List.of(StoneFamily.STONE_WALL, StoneFamily.POLISHED_STONE, StoneFamily.POLISHED_STONE_SLAB, StoneFamily.POLISHED_STONE_WALL,
						StoneFamily.CHISELED_STONE_PILLAR, StoneFamily.STONE_TILES, StoneFamily.STONE_TILE_SLAB, StoneFamily.STONE_TILE_WALL));
				cutEach(StoneFamily.POLISHED_STONE, List.of(StoneFamily.POLISHED_STONE_SLAB, StoneFamily.POLISHED_STONE_WALL, Blocks.STONE_BRICKS, Blocks.STONE_BRICK_SLAB,
						Blocks.STONE_BRICK_WALL, Blocks.CHISELED_STONE_BRICKS, StoneFamily.CHISELED_STONE_PILLAR, StoneFamily.STONE_TILES, StoneFamily.STONE_TILE_SLAB,
						StoneFamily.STONE_TILE_WALL));
				// Smooth Stone is smelted, not cut, and vanilla cuts it into its slab.
				cut(Blocks.SMOOTH_STONE, StoneFamily.SMOOTH_STONE_WALL);
				cutEach(Blocks.STONE_BRICKS, List.of(StoneFamily.CHISELED_STONE_PILLAR, StoneFamily.STONE_TILES, StoneFamily.STONE_TILE_SLAB, StoneFamily.STONE_TILE_WALL));
				cutEach(StoneFamily.STONE_TILES, List.of(StoneFamily.STONE_TILE_SLAB, StoneFamily.STONE_TILE_WALL));
				// Stone Brick items, as many as each piece is worth in blocks, 4 to a block: a stair is 3/4 of a block
				// (C-001), and a slab and a wall 1/2 (C-002, C-008).
				cutBricks(Blocks.STONE_BRICKS, BRICKS_PER_BLOCK);
				cutBricks(Blocks.STONE_BRICK_STAIRS, BRICKS_PER_STAIRS);
				cutBricks(Blocks.STONE_BRICK_SLAB, BRICKS_PER_SLAB);
				cutBricks(Blocks.STONE_BRICK_WALL, BRICKS_PER_WALL);
			}

			private ShapedRecipeBuilder twoByTwo(ItemLike result, ItemLike ingredient, int count) {
				return shaped(RecipeCategory.BUILDING_BLOCKS, result, count)
						.define('#', ingredient)
						.pattern("##")
						.pattern("##");
			}

			// Each of the five pieces of one block's set into the same piece of another's, as the blocks smelt. A set missing a
			// piece stops datagen in piece().
			private void smeltPieces(BlockFamily from, @Nullable BlockFamily vanillaFrom, BlockFamily to, @Nullable BlockFamily vanillaTo) {
				for (SmeltedPiece piece : SMELTED_PIECES) {
					smeltingLikeStone(piece(to, vanillaTo, piece.variant()), piece(from, vanillaFrom, piece.variant()), piece.category(), piece.parts(), piece.ofBlock());
				}
			}

			// A set's piece, as the Chisel finds it too; a set missing one stops datagen.
			private Block piece(BlockFamily caramel, @Nullable BlockFamily vanilla, BlockFamily.Variant variant) {
				Block piece = CaramelBlockFamilies.piece(caramel.getBaseBlock(), caramel, vanilla, variant);
				if (piece == null) {
					throw new IllegalStateException(caramel.getBaseBlock() + " has no " + variant);
				}
				return piece;
			}

			// Smelting that takes the share of a block that the piece is worth of vanilla's time and experience, the time
			// rounded up so that a block's worth of pieces never smelts faster than the block.
			private void smeltingLikeStone(ItemLike result, ItemLike ingredient, RecipeCategory category, int parts, int ofBlock) {
				SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), category, CookingBookCategory.BLOCKS, result,
								(float) ((double) SMELTING_EXPERIENCE * parts / ofBlock), (int) Math.ceil((double) SMELTING_TICKS * parts / ofBlock))
						.unlockedBy(getHasName(ingredient), has(ingredient))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getSmeltingRecipeName(result) + "_" + getItemName(ingredient))));
			}

			private void cutEach(ItemLike input, List<ItemLike> results) {
				results.forEach(result -> cut(input, result));
			}

			// 1 of a block, or 2 of a slab or a wall, as vanilla's stonecutter and C-008 cut them.
			private void cut(ItemLike input, ItemLike result) {
				Block block = ((BlockItem) result.asItem()).getBlock();
				boolean wall = block instanceof WallBlock;
				SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), wall ? RecipeCategory.DECORATIONS : RecipeCategory.BUILDING_BLOCKS, result,
								wall || block instanceof SlabBlock ? 2 : 1)
						.unlockedBy(getHasName(input), has(input))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getConversionRecipeName(result, input) + "_stonecutting")));
			}

			private void cutBricks(ItemLike input, int count) {
				SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.MISC, StoneFamily.STONE_BRICK, count)
						.unlockedBy(getHasName(input), has(input))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getConversionRecipeName(StoneFamily.STONE_BRICK, input) + "_stonecutting")));
			}

			// C-003: 2 slabs side by side make 16 pressure plates.
			private void pressurePlate(BlockFamily family, Block slab) {
				Block plate = family.get(BlockFamily.Variant.PRESSURE_PLATE);
				Block base = family.getBaseBlock();
				sixteenFrom(RecipeCategory.REDSTONE, plate, slab, family, BlockFamily.Variant.PRESSURE_PLATE)
						.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.PRESSURE_PLATE, base), has(base))
						.save(output, vanillaKey(plate));
			}

			private ShapedRecipeBuilder door(ItemLike door, ItemLike material, @Nullable BlockFamily family, BlockFamily.Variant variant) {
				return inFamilyGroup(shaped(RecipeCategory.REDSTONE, door, 16)
						.define('#', material)
						.pattern("##")
						.pattern("##")
						.pattern("##"), family, variant);
			}

			private ShapedRecipeBuilder trapdoor(ItemLike trapdoor, ItemLike material, @Nullable BlockFamily family, BlockFamily.Variant variant) {
				return inFamilyGroup(shaped(RecipeCategory.REDSTONE, trapdoor, 16)
						.define('#', material)
						.pattern("###")
						.pattern("###"), family, variant);
			}

			// Writes a recipe for each family that has one of the variants, and stops datagen unless that is the expected
			// number of families. Bamboo's fence and fence gate are its family's custom variants, so a walk over FENCE or
			// FENCE_GATE alone misses them.
			private void eachFamilyWith(List<BlockFamily.Variant> variants, int expected, String blocks, BiConsumer<BlockFamily, BlockFamily.Variant> write) {
				int found = 0;
				for (BlockFamily family : BlockFamilies.getAllFamilies().toList()) {
					for (BlockFamily.Variant variant : variants) {
						if (family.get(variant) != null) {
							write.accept(family, variant);
							found++;
						}
					}
				}
				if (found != expected) {
					throw new IllegalStateException("Expected " + expected + " block families with " + blocks + ", found " + found);
				}
			}

			private ShapedRecipeBuilder sixteenFrom(RecipeCategory category, ItemLike result, ItemLike ingredient, @Nullable BlockFamily family, BlockFamily.Variant variant) {
				return inFamilyGroup(shaped(category, result, 16)
						.define('#', ingredient)
						.pattern("##"), family, variant);
			}

			private void slabRecipes(@Nullable BlockFamily family, Block slab, Block base) {
				List<Block> accepted = alternatives.getOrDefault(slab, List.of(base));
				ShapedRecipeBuilder recipe = shaped(RecipeCategory.BUILDING_BLOCKS, slab, 4)
						.define('#', Ingredient.of(accepted.toArray(ItemLike[]::new)))
						.pattern("##");
				unlockLikeVanilla(inFamilyGroup(recipe, family, BlockFamily.Variant.SLAB), family, BlockFamily.Variant.SLAB, base, accepted)
						.save(output, vanillaKey(slab));
				shaped(RecipeCategory.BUILDING_BLOCKS, base)
						.define('#', slab)
						.pattern("#")
						.pattern("#")
						.unlockedBy(getHasName(slab), has(slab))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getConversionRecipeName(base, slab))));
			}

			// Vanilla's recipe book group for a family's variant, such as wooden_slab; the smooth stone slab and the weighted
			// pressure plates have none.
			private <T extends RecipeBuilder> T inFamilyGroup(T builder, @Nullable BlockFamily family, BlockFamily.Variant variant) {
				if (family != null) {
					family.getRecipeGroupPrefix().map(variant::getPrefixedRecipeGroup).ifPresent(builder::group);
				}
				return builder;
			}

			private void remove(RecipeBuilder builder, ItemLike result) {
				builder.save(removed, vanillaKey(result));
			}

			private ResourceKey<Recipe<?>> vanillaKey(ItemLike result) {
				return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(result.asItem()));
			}

			private void stairs(BlockFamily family) {
				Block stairs = family.get(BlockFamily.Variant.STAIRS);
				Block base = family.getBaseBlock();
				List<Block> accepted = alternatives.getOrDefault(stairs, List.of(base));
				Ingredient ingredient = Ingredient.of(accepted.toArray(ItemLike[]::new));
				// Stairs with no vanilla group get their own, so both L recipes share one recipe book button.
				String group = family.getRecipeGroupPrefix().map(BlockFamily.Variant.STAIRS::getPrefixedRecipeGroup).orElse(getItemName(stairs));

				// Empty cell on top; shaped recipes also match their mirror image.
				unlockLikeVanilla(shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4), family, BlockFamily.Variant.STAIRS, base, accepted)
						.define('#', ingredient)
						.pattern("# ")
						.pattern("##")
						.group(group)
						.save(output, vanillaKey(stairs));
				// Empty cell at the bottom.
				unlockLikeVanilla(shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4), family, BlockFamily.Variant.STAIRS, base, accepted)
						.define('#', ingredient)
						.pattern("##")
						.pattern("# ")
						.group(group)
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getItemName(stairs) + "_flipped")));
				shaped(RecipeCategory.BUILDING_BLOCKS, base, 3)
						.define('#', stairs)
						.pattern("##")
						.pattern("##")
						.unlockedBy(getHasName(stairs), has(stairs))
						.save(output, ResourceKey.create(Registries.RECIPE, Caramel.id(getConversionRecipeName(base, stairs))));
			}

			// The override at the vanilla id also replaces vanilla's unlock advancement, so it keeps vanilla's criteria.
			// A null family is the smooth stone slab's.
			private ShapedRecipeBuilder unlockLikeVanilla(ShapedRecipeBuilder builder, @Nullable BlockFamily family, BlockFamily.Variant variant, Block base, List<Block> accepted) {
				if (base == Blocks.STONE_BRICKS) {
					return builder.unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS));
				}
				if (base == Blocks.QUARTZ_BLOCK || base == Blocks.SANDSTONE || base == Blocks.RED_SANDSTONE) {
					accepted.forEach(block -> builder.unlockedBy(getHasName(block), has(block)));
					return builder;
				}
				return builder.unlockedBy(family == null ? getHasName(base) : getCraftingCriterionName(family, variant, base), has(base));
			}

			// Vanilla's stonecutter walk calls this for every variant; only stairs and walls are written, at their vanilla ids.
			// C-001 removes the stairs; C-008's walls give twice vanilla's count, 2 per block.
			@Override
			public void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike base, int count) {
				ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(getConversionRecipeName(result, base) + "_stonecutting"));
				if (result.asItem() instanceof BlockItem item && item.getBlock() instanceof StairBlock) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
							.unlockedBy(getHasName(base), has(base))
							.save(removed, key);
					removedStonecutterStairs++;
				} else if (result.asItem() instanceof BlockItem item && item.getBlock() instanceof WallBlock) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, 2 * count)
							.unlockedBy(getHasName(base), has(base))
							.save(output, key);
					stonecutterWalls++;
				}
			}
		};
	}
}
