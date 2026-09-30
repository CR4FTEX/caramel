package io.github.cr4ftex.caramel.client.datagen;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public class CaramelRecipeProvider extends FabricRecipeProvider {
	// C-001's scope in Minecraft 26.3. If an update changes either count, datagen stops so the scope gets rechecked.
	private static final int STAIRS_FAMILIES = 97;
	private static final int STONECUTTER_STAIRS_RECIPES = 103;
	// C-002's scope, checked the same way. The smooth stone slab, crafted outside any family, is the 100th slab. The
	// weighted pressure plates, the waxed chiseled coppers, the purpur pillar and bamboo mosaic are removed by name.
	private static final int SLAB_FAMILIES = 99;
	private static final int PRESSURE_PLATE_FAMILIES = 15;
	private static final int CARPET_FAMILIES = 16;
	private static final int CHISELED_FAMILIES = 16;

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

			@Override
			public void buildRecipes() {
				removed = CaramelRecipeProvider.this.withConditions(output, ResourceConditions.alwaysFalse());
				replaceStairs();
				replaceSlabs();
			}

			// C-001: 3 blocks in an L make 4 stairs, 4 stairs make 3 blocks, and the stonecutter makes no stairs.
			private void replaceStairs() {
				List<BlockFamily> families = BlockFamilies.getAllFamilies()
						.filter(family -> family.getVariants().containsKey(BlockFamily.Variant.STAIRS))
						.sorted(Comparator.comparing(family -> BuiltInRegistries.BLOCK.getKey(family.get(BlockFamily.Variant.STAIRS))))
						.toList();
				if (families.size() != STAIRS_FAMILIES) {
					throw new IllegalStateException("Expected " + STAIRS_FAMILIES + " block families with stairs, found " + families.size());
				}
				families.forEach(this::stairs);

				BlockFamilies.getAllFamilies()
						.filter(BlockFamily::shouldGenerateStonecutterRecipe)
						.forEach(family -> family.getVariants().keySet().forEach(variant -> generateStonecutterRecipe(family, variant, family.getBaseBlock())));
				WeatheringCopperCollection.zipApply(Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK,
						(stairs, copper) -> stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, stairs, copper, 4));
				if (removedStonecutterStairs != STONECUTTER_STAIRS_RECIPES) {
					throw new IllegalStateException("Expected " + STONECUTTER_STAIRS_RECIPES + " stonecutter recipes that make stairs, found " + removedStonecutterStairs);
				}
			}

			// C-002: 2 blocks in a row make 4 slabs and 2 stacked slabs make 1 block. The vanilla recipes those grids
			// would also match are removed: pressure plates and carpets (2 blocks in a row) and the chiseled blocks, purpur
			// pillar and bamboo mosaic (2 stacked slabs). The owner chose to remove the weighted pressure plates too.
			private void replaceSlabs() {
				List<BlockFamily> families = BlockFamilies.getAllFamilies()
						.filter(family -> family.getVariants().containsKey(BlockFamily.Variant.SLAB))
						.sorted(Comparator.comparing(family -> BuiltInRegistries.BLOCK.getKey(family.get(BlockFamily.Variant.SLAB))))
						.toList();
				if (families.size() != SLAB_FAMILIES) {
					throw new IllegalStateException("Expected " + SLAB_FAMILIES + " block families with slabs, found " + families.size());
				}
				families.forEach(family -> slabRecipes(family, family.get(BlockFamily.Variant.SLAB), family.getBaseBlock()));
				slabRecipes(null, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);

				int plates = 0;
				int carpets = 0;
				int chiseledBlocks = 0;
				for (BlockFamily family : families) {
					Block base = family.getBaseBlock();
					Block slab = family.get(BlockFamily.Variant.SLAB);
					if (family.get(BlockFamily.Variant.PRESSURE_PLATE) instanceof Block plate) {
						remove(inFamilyGroup(pressurePlateBuilder(RecipeCategory.REDSTONE, plate, Ingredient.of(base)), family, BlockFamily.Variant.PRESSURE_PLATE)
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.PRESSURE_PLATE, base), has(base)), plate);
						plates++;
					}
					if (family.get(BlockFamily.Variant.CARPET) instanceof Block carpet) {
						remove(inFamilyGroup(carpetBuilder(RecipeCategory.DECORATIONS, carpet, Ingredient.of(base)), family, BlockFamily.Variant.CARPET)
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.CARPET, base), has(base)), carpet);
						carpets++;
					}
					if (family.get(BlockFamily.Variant.CHISELED) instanceof Block chiseled) {
						remove(chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, chiseled, Ingredient.of(slab))
								.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.CHISELED, slab), has(slab)), chiseled);
						chiseledBlocks++;
					}
				}
				if (plates != PRESSURE_PLATE_FAMILIES || carpets != CARPET_FAMILIES || chiseledBlocks != CHISELED_FAMILIES) {
					throw new IllegalStateException("Expected " + PRESSURE_PLATE_FAMILIES + " slab families with pressure plates, " + CARPET_FAMILIES + " with carpets and "
							+ CHISELED_FAMILIES + " with chiseled blocks, found " + plates + ", " + carpets + " and " + chiseledBlocks);
				}
				Map.of(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT).forEach((plate, ingot) ->
						remove(pressurePlateBuilder(RecipeCategory.REDSTONE, plate, Ingredient.of(ingot)).unlockedBy(getHasName(ingot), has(ingot)), plate));
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

			// Vanilla's recipe book group for a family's variant, such as wooden_slab; the smooth stone slab has none.
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

			// Vanilla's stonecutter walk calls this for every variant; only stairs are written, at their vanilla ids.
			@Override
			public void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike base, int count) {
				if (result.asItem() instanceof BlockItem item && item.getBlock() instanceof StairBlock) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
							.unlockedBy(getHasName(base), has(base))
							.save(removed, ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(getConversionRecipeName(result, base) + "_stonecutting")));
					removedStonecutterStairs++;
				}
			}
		};
	}
}
