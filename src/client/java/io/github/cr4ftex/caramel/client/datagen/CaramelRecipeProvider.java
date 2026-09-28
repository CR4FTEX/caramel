package io.github.cr4ftex.caramel.client.datagen;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
			// Vanilla crafts these stairs from more than one block, listed in vanilla's order.
			private final Map<Block, List<Block>> alternatives = Map.of(
					Blocks.PURPUR_STAIRS, List.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR),
					Blocks.QUARTZ_STAIRS, List.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR),
					Blocks.SANDSTONE_STAIRS, List.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE),
					Blocks.RED_SANDSTONE_STAIRS, List.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE));
			private RecipeOutput removed;
			private int removedCount;

			@Override
			public void buildRecipes() {
				List<BlockFamily> families = BlockFamilies.getAllFamilies()
						.filter(family -> family.getVariants().containsKey(BlockFamily.Variant.STAIRS))
						.sorted(Comparator.comparing(family -> BuiltInRegistries.BLOCK.getKey(family.get(BlockFamily.Variant.STAIRS))))
						.toList();
				if (families.size() != STAIRS_FAMILIES) {
					throw new IllegalStateException("Expected " + STAIRS_FAMILIES + " block families with stairs, found " + families.size());
				}
				families.forEach(this::stairs);

				removed = CaramelRecipeProvider.this.withConditions(output, ResourceConditions.alwaysFalse());
				BlockFamilies.getAllFamilies()
						.filter(BlockFamily::shouldGenerateStonecutterRecipe)
						.forEach(family -> family.getVariants().keySet().forEach(variant -> generateStonecutterRecipe(family, variant, family.getBaseBlock())));
				WeatheringCopperCollection.zipApply(Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK,
						(stairs, copper) -> stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, stairs, copper, 4));
				if (removedCount != STONECUTTER_STAIRS_RECIPES) {
					throw new IllegalStateException("Expected " + STONECUTTER_STAIRS_RECIPES + " stonecutter recipes that make stairs, found " + removedCount);
				}
			}

			private void stairs(BlockFamily family) {
				Block stairs = family.get(BlockFamily.Variant.STAIRS);
				Block base = family.getBaseBlock();
				List<Block> accepted = alternatives.getOrDefault(stairs, List.of(base));
				Ingredient ingredient = Ingredient.of(accepted.toArray(ItemLike[]::new));
				// Stairs with no vanilla group get their own, so both L recipes share one recipe book button.
				String group = family.getRecipeGroupPrefix().map(BlockFamily.Variant.STAIRS::getPrefixedRecipeGroup).orElse(getItemName(stairs));

				// Empty cell on top; shaped recipes also match their mirror image.
				unlockLikeVanilla(shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4), family, stairs, base, accepted)
						.define('#', ingredient)
						.pattern("# ")
						.pattern("##")
						.group(group)
						.save(output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(stairs.asItem())));
				// Empty cell at the bottom.
				unlockLikeVanilla(shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4), family, stairs, base, accepted)
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
			private ShapedRecipeBuilder unlockLikeVanilla(ShapedRecipeBuilder builder, BlockFamily family, Block stairs, Block base, List<Block> accepted) {
				if (stairs == Blocks.STONE_BRICK_STAIRS) {
					return builder.unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS));
				}
				if (stairs == Blocks.QUARTZ_STAIRS || stairs == Blocks.SANDSTONE_STAIRS || stairs == Blocks.RED_SANDSTONE_STAIRS) {
					accepted.forEach(block -> builder.unlockedBy(getHasName(block), has(block)));
					return builder;
				}
				return builder.unlockedBy(getCraftingCriterionName(family, BlockFamily.Variant.STAIRS, base), has(base));
			}

			// Vanilla's stonecutter walk calls this for every variant; only stairs are written, at their vanilla ids.
			// The removed output disables the unlock advancement too: an advancement naming a missing recipe makes
			// every world fail to load with "Unbound values in registry".
			@Override
			public void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike base, int count) {
				if (result.asItem() instanceof BlockItem item && item.getBlock() instanceof StairBlock) {
					SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
							.unlockedBy(getHasName(base), has(base))
							.save(removed, ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(getConversionRecipeName(result, base) + "_stonecutting")));
					removedCount++;
				}
			}
		};
	}
}
