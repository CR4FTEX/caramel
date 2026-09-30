package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.firstInput;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.furnaces;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaComponents;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaVariants;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.withVanillaFuel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.IntStream;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Furnace;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;

// C-001. What each stairs should cost and give back is read from the vanilla data pack's own recipe files,
// never from Caramel's, so a wrong generated recipe cannot also make its own expectation. Whether a stairs burned
// comes from the item components vanilla builds, before Caramel changes them.
public class StairsRecipeGameTest {
	private static final String IDEA = "C-001";
	// 26.3 has no ItemTags constant for it.
	static final TagKey<Item> STAIRS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs"));
	private static final int VANILLA_STAIRS = 97;
	// Vanilla crafts these from more than one block; recycling gives the family's base block.
	private static final Map<Item, Item> BASE_OF_ALTERNATIVES = Map.of(
			Items.QUARTZ_STAIRS, Items.QUARTZ_BLOCK,
			Items.PURPUR_STAIRS, Items.PURPUR_BLOCK,
			Items.SANDSTONE_STAIRS, Items.SANDSTONE,
			Items.RED_SANDSTONE_STAIRS, Items.RED_SANDSTONE);

	@GameTest
	public void vanillaStairsAreAllFound(GameTestHelper helper) {
		List<Variant> stairs = vanillaStairs(helper);
		if (stairs.size() != VANILLA_STAIRS) {
			helper.fail("Expected " + VANILLA_STAIRS + " stairs with a vanilla recipe in #minecraft:stairs, found " + stairs.size());
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs, {} accepted blocks", stairs.size(), stairs.stream().mapToInt(s -> s.accepted().size()).sum());
		helper.succeed();
	}

	@GameTest
	public void threeBlocksInAnLMakeFourStairs(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			for (Item block : stairs.accepted()) {
				for (int empty = 0; empty < 4; empty++) {
					for (Grid grid : placements(2, 2, lShape(block, empty))) {
						checked++;
						Crafted crafted = craft(helper, crafting, grid.input());
						if (!crafted.gives(stairs.item(), 4)) {
							failures.add(name(block) + " with empty cell " + empty + " " + grid.label() + " gave " + crafted + ", expected 4 " + name(stairs.item()));
						}
					}
				}
			}
		}
		report(helper, IDEA, "L grids", checked, failures);
	}

	@GameTest
	public void bothLRecipesKeepTheVanillaGroupAndCategory(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			// Stairs with no vanilla group get their own id as the group, so both L recipes share one recipe book button.
			String group = stairs.group().isEmpty() ? BuiltInRegistries.ITEM.getKey(stairs.item()).getPath() : stairs.group();
			Map<Identifier, CraftingRecipe> recipes = new TreeMap<>();
			for (int empty = 0; empty < 4; empty++) {
				Crafted crafted = craft(helper, crafting, CraftingInput.of(2, 2, lShape(stairs.accepted().getFirst(), empty)));
				if (crafted.gives(stairs.item(), 4)) {
					RecipeHolder<?> holder = crafted.recipes().getFirst();
					recipes.put(holder.id().identifier(), (CraftingRecipe) holder.value());
				}
			}
			for (Map.Entry<Identifier, CraftingRecipe> entry : recipes.entrySet()) {
				checked++;
				CraftingRecipe recipe = entry.getValue();
				if (!recipe.group().equals(group) || recipe.category() != stairs.category()) {
					failures.add(entry.getKey() + " has group \"" + recipe.group() + "\" and category " + recipe.category().getSerializedName()
							+ ", expected \"" + group + "\" and " + stairs.category().getSerializedName());
				}
			}
		}
		report(helper, IDEA, "L recipes", checked, failures);
	}

	@GameTest
	public void oldSixBlockStaircaseGivesNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			for (Item block : stairs.accepted()) {
				ItemStack b = new ItemStack(block);
				ItemStack e = ItemStack.EMPTY;
				for (Grid grid : List.of(
						new Grid("in a staircase", CraftingInput.of(3, 3, List.of(b, e, e, b, b, e, b, b, b))),
						new Grid("in a mirrored staircase", CraftingInput.of(3, 3, List.of(e, e, b, e, b, b, b, b, b))))) {
					checked++;
					Crafted crafted = craft(helper, crafting, grid.input());
					if (!crafted.recipes().isEmpty()) {
						failures.add("6 " + name(block) + " " + grid.label() + " gave " + crafted);
					}
				}
			}
		}
		report(helper, IDEA, "staircase grids", checked, failures);
	}

	@GameTest
	public void fourStairsMakeThreeBaseBlocks(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			ItemStack s = new ItemStack(stairs.item());
			ItemStack e = ItemStack.EMPTY;
			for (Grid grid : List.of(
					new Grid("in a 2x2 grid", CraftingInput.of(2, 2, List.of(s, s, s, s))),
					new Grid("in the bottom right of a 3x3 grid", CraftingInput.of(3, 3, List.of(e, e, e, e, s, s, e, s, s))))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(stairs.base(), 3)) {
					failures.add("4 " + name(stairs.item()) + " " + grid.label() + " gave " + crafted + ", expected 3 " + name(stairs.base()));
				}
			}
		}
		report(helper, IDEA, "recycling grids", checked, failures);
	}

	@GameTest
	public void mixedStairsGiveNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Variant> stairs = vanillaStairs(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (int i = 0; i < stairs.size(); i++) {
			ItemStack a = new ItemStack(stairs.get(i).item());
			ItemStack b = new ItemStack(stairs.get((i + 1) % stairs.size()).item());
			for (Grid grid : List.of(
					new Grid("3 " + name(a.getItem()) + " and 1 " + name(b.getItem()), CraftingInput.of(2, 2, List.of(a, a, a, b))),
					new Grid("2 " + name(a.getItem()) + " and 2 " + name(b.getItem()) + " in diagonal pairs", CraftingInput.of(2, 2, List.of(a, b, b, a))))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.recipes().isEmpty()) {
					failures.add(grid.label() + " gave " + crafted);
				}
			}
		}
		report(helper, IDEA, "mixed grids", checked, failures);
	}

	@GameTest
	public void noStonecuttingRecipeMakesStairs(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (RecipeHolder<?> holder : helper.getLevel().recipeAccess().getRecipes()) {
			if (holder.value() instanceof StonecutterRecipe recipe) {
				checked++;
				Item input = firstInput(recipe);
				ItemStack result = recipe.assemble(new SingleRecipeInput(new ItemStack(input)));
				if (result.is(STAIRS)) {
					failures.add(holder.id().identifier() + " cuts " + name(input) + " into " + name(result));
				}
			}
		}
		if (checked == 0) {
			helper.fail("No stonecutter recipes loaded at all");
		}
		report(helper, IDEA, "stonecutter recipes", checked, failures);
	}

	// 3 blocks craft into 4 stairs, so 4 stairs burn exactly as long as 3 of their base block in a furnace, and never
	// longer in any furnace. Stairs of a block that does not burn cannot burn either.
	@GameTest
	public void craftingStairsNeverAddsFuel(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int ofBlocksThatBurn = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			ItemStack item = new ItemStack(stairs.item());
			ItemStack base = new ItemStack(stairs.base());
			if (furnaces.getFirst().burns(level, base)) {
				ofBlocksThatBurn++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int stairsTime = furnace.burnDuration().applyAsInt(level, item);
				int baseTime = furnace.burnDuration().applyAsInt(level, base);
				boolean exact = furnace == furnaces.getFirst() && baseTime > 0;
				if (exact ? 4 * stairsTime != 3 * baseTime : 4 * stairsTime > 3 * baseTime) {
					failures.add("4 " + name(stairs.item()) + " burn " + 4 * stairsTime + " ticks in " + furnace.name() + ", 3 " + name(stairs.base())
							+ " burn " + 3 * baseTime + ", expected " + (exact ? "the same" : "no more"));
				}
			}
		}
		if (ofBlocksThatBurn == 0) {
			helper.fail("No stairs in #minecraft:stairs has a base block that burns in a furnace");
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs have a base block that burns", ofBlocksThatBurn);
		report(helper, IDEA, "stairs burn times", checked, failures);
	}

	// Every stairs that burned in vanilla still burns in every furnace, at vanilla's cooking speed, and every stairs that
	// did not still does not.
	@GameTest
	public void stairsThatBurnedStillBurn(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		int changed = 0;
		for (Variant stairs : vanillaStairs(helper)) {
			ItemStack now = new ItemStack(stairs.item());
			ItemStack before = withVanillaFuel(now, vanilla);
			if (!Objects.equals(before.get(DataComponents.COOKING_FUEL), now.get(DataComponents.COOKING_FUEL))) {
				changed++;
			}
			if (furnaces.getFirst().burns(level, before)) {
				burned++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				boolean burnedBefore = furnace.burns(level, before);
				if (furnace.burns(level, now) != burnedBefore) {
					failures.add(name(stairs.item()) + (burnedBefore ? " no longer burns" : " burns") + " in " + furnace.name() + (burnedBefore ? "" : ", but did not in vanilla"));
				}
				float speedBefore = furnace.speedMultiplier().apply(level, before);
				float speedNow = furnace.speedMultiplier().apply(level, now);
				if (burnedBefore && speedNow != speedBefore) {
					failures.add(name(stairs.item()) + " makes " + furnace.name() + " cook " + speedNow + " times as fast, " + speedBefore + " in vanilla");
				}
			}
		}
		// The wooden stairs that never burned: the furnace must not even take them.
		for (Item item : List.of(Items.CRIMSON_STAIRS, Items.WARPED_STAIRS)) {
			ItemStack stack = new ItemStack(item);
			for (Furnace furnace : furnaces) {
				checked++;
				if (furnace.fuelSlot().test(stack) || furnace.burnDuration().applyAsInt(level, stack) != 0) {
					failures.add(furnace.name() + " takes " + name(item) + " as fuel");
				}
			}
		}
		// Proves the vanilla components really are from before Caramel changed them.
		if (changed == 0) {
			helper.fail("No stairs has a different fuel from vanilla's, so the vanilla item components are not vanilla's");
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs burned in vanilla, {} have a different fuel now", burned, changed);
		report(helper, IDEA, "stairs fuel checks", checked, failures);
	}

	// Each stairs in #minecraft:stairs, sorted by id, with the blocks its vanilla recipe accepts.
	static List<Variant> vanillaStairs(GameTestHelper helper) {
		return vanillaVariants(helper, STAIRS, BASE_OF_ALTERNATIVES, true);
	}

	// Three of a block in a row-major 2x2 square, with the empty cell 0 top left, 1 top right, 2 bottom left or 3 bottom right.
	private static List<ItemStack> lShape(Item block, int empty) {
		return IntStream.range(0, 4).mapToObj(cell -> cell == empty ? ItemStack.EMPTY : new ItemStack(block)).toList();
	}
}
