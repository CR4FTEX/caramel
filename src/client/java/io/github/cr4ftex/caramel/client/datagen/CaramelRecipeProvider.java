package io.github.cr4ftex.caramel.client.datagen;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.jspecify.annotations.Nullable;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import net.minecraft.advancements.Advancement;
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
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
					if (family.get(BlockFamily.Variant.PRESSURE_PLATE) instanceof Block plate) {
						sixteenFrom(RecipeCategory.REDSTONE, plate, slab, family, BlockFamily.Variant.PRESSURE_PLATE)
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.PRESSURE_PLATE, base), has(base))
								.save(output, vanillaKey(plate));
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
				eachFamilyWith(List.of(BlockFamily.Variant.BUTTON), BUTTON_FAMILIES, "buttons", (family, variant) -> {
					Block button = family.get(variant);
					Block base = family.getBaseBlock();
					inFamilyGroup(shapeless(RecipeCategory.REDSTONE, button, 20).requires(base), family, variant)
							.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
							.save(output, vanillaKey(button));
				});
			}

			// C-008: every wall recipe gives 12 instead of 6. The recipe is vanilla's otherwise, built as vanilla's wallBuilder
			// builds it, and replaces it at its id. stonecutter() makes the stonecutter's walls give 2 per block.
			private void walls() {
				eachFamilyWith(List.of(BlockFamily.Variant.WALL), WALL_FAMILIES, "walls", (family, variant) -> {
					Block wall = family.get(variant);
					Block base = family.getBaseBlock();
					inFamilyGroup(shaped(RecipeCategory.DECORATIONS, wall, 12)
							.define('#', base)
							.pattern("###")
							.pattern("###"), family, variant)
							.unlockedBy(getCraftingCriterionName(family, variant, base), has(base))
							.save(output, vanillaKey(wall));
				});
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
