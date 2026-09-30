package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.cut;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.furnaces;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaVariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Furnace;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

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
import net.minecraft.world.item.crafting.StonecutterRecipe;

// C-002. What each slab should cost and give back, and which vanilla recipes 2 stacked slabs take over, are read from
// the vanilla data pack's own recipe files, never from Caramel's, so a wrong generated recipe cannot also make its own
// expectation. The pressure plates and carpets that 2 blocks side by side made are C-003's, in
// PressurePlatesAndCarpetsGameTest.
public class SlabsRecipeGameTest {
	private static final String IDEA = "C-002";
	// 26.3 has no ItemTags constant for it.
	private static final TagKey<Item> SLABS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("slabs"));
	// C-002's scope in Minecraft 26.3, from docs/DESIGN.md.
	static final int VANILLA_SLABS = 100;
	static final int REMOVED_STACKED_SLAB_RECIPES = 22;
	// Vanilla crafts these from more than one block; recycling gives the family's base block.
	private static final Map<Item, Item> BASE_OF_ALTERNATIVES = Map.of(
			Items.QUARTZ_SLAB, Items.QUARTZ_BLOCK,
			Items.PURPUR_SLAB, Items.PURPUR_BLOCK,
			Items.SANDSTONE_SLAB, Items.SANDSTONE,
			Items.RED_SANDSTONE_SLAB, Items.RED_SANDSTONE);

	@GameTest
	public void vanillaSlabsAreAllFound(GameTestHelper helper) {
		List<Variant> slabs = vanillaSlabs(helper);
		if (slabs.size() != VANILLA_SLABS) {
			helper.fail("Expected " + VANILLA_SLABS + " slabs with a vanilla recipe in #minecraft:slabs, found " + slabs.size());
		}
		Caramel.LOGGER.info("C-002 game test: {} slabs, {} accepted blocks", slabs.size(), slabs.stream().mapToInt(s -> s.accepted().size()).sum());
		helper.succeed();
	}

	@GameTest
	public void twoBlocksSideBySideMakeFourSlabs(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant slab : vanillaSlabs(helper)) {
			for (Item block : slab.accepted()) {
				for (Grid grid : placements(2, 1, List.of(new ItemStack(block), new ItemStack(block)))) {
					checked++;
					Crafted crafted = craft(helper, crafting, grid.input());
					if (!crafted.gives(slab.item(), 4)) {
						failures.add("2 " + name(block) + " side by side " + grid.label() + " gave " + crafted + ", expected 4 " + name(slab.item()));
					}
				}
			}
		}
		report(helper, IDEA, "side by side grids", checked, failures);
	}

	@GameTest
	public void slabRecipesKeepTheVanillaGroupAndCategory(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant slab : vanillaSlabs(helper)) {
			checked++;
			ItemStack block = new ItemStack(slab.accepted().getFirst());
			Crafted crafted = craft(helper, crafting, CraftingInput.of(2, 1, List.of(block, block.copy())));
			if (!crafted.gives(slab.item(), 4)) {
				failures.add("2 " + name(block.getItem()) + " side by side gave " + crafted + ", expected 4 " + name(slab.item()));
				continue;
			}
			RecipeHolder<?> holder = crafted.recipes().getFirst();
			CraftingRecipe recipe = (CraftingRecipe) holder.value();
			if (!recipe.group().equals(slab.group()) || recipe.category() != slab.category()) {
				failures.add(holder.id().identifier() + " has group \"" + recipe.group() + "\" and category " + recipe.category().getSerializedName()
						+ ", expected \"" + slab.group() + "\" and " + slab.category().getSerializedName());
			}
		}
		report(helper, IDEA, "slab recipes", checked, failures);
	}

	@GameTest
	public void oldThreeBlockRowGivesNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant slab : vanillaSlabs(helper)) {
			for (Item block : slab.accepted()) {
				for (int row = 0; row < 3; row++) {
					List<ItemStack> grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
					for (int column = 0; column < 3; column++) {
						grid.set(row * 3 + column, new ItemStack(block));
					}
					checked++;
					Crafted crafted = craft(helper, crafting, CraftingInput.of(3, 3, grid));
					if (!crafted.recipes().isEmpty()) {
						failures.add("3 " + name(block) + " in row " + row + " of a 3x3 grid gave " + crafted);
					}
				}
			}
		}
		report(helper, IDEA, "row of 3 grids", checked, failures);
	}

	@GameTest
	public void twoStackedSlabsMakeOneBaseBlock(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Variant slab : vanillaSlabs(helper)) {
			for (Grid grid : placements(1, 2, List.of(new ItemStack(slab.item()), new ItemStack(slab.item())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(slab.base(), 1)) {
					failures.add("2 " + name(slab.item()) + " stacked " + grid.label() + " gave " + crafted + ", expected 1 " + name(slab.base()));
				}
			}
		}
		report(helper, IDEA, "recycling grids", checked, failures);
	}

	@GameTest
	public void mixedSlabsGiveNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Variant> slabs = vanillaSlabs(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (int i = 0; i < slabs.size(); i++) {
			Variant a = slabs.get(i);
			Variant b = slabs.get((i + 1) % slabs.size());
			ItemStack aSlab = new ItemStack(a.item());
			ItemStack bSlab = new ItemStack(b.item());
			for (Grid grid : List.of(
					new Grid(name(a.item()) + " stacked on " + name(b.item()), CraftingInput.of(1, 2, List.of(aSlab, bSlab))),
					new Grid(name(b.item()) + " stacked on " + name(a.item()), CraftingInput.of(1, 2, List.of(bSlab, aSlab))),
					new Grid(name(a.base()) + " and " + name(b.base()) + " side by side", CraftingInput.of(2, 1, List.of(new ItemStack(a.base()), new ItemStack(b.base())))))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.recipes().isEmpty()) {
					failures.add(grid.label() + " gave " + crafted);
				}
			}
		}
		report(helper, IDEA, "mixed grids", checked, failures);
	}

	// No removed recipe is loaded: no recipe that 2 stacked slabs matched in vanilla is left to compete with recycling.
	@GameTest
	public void removedRecipesAreNotLoaded(GameTestHelper helper) {
		Set<Identifier> loaded = new HashSet<>();
		helper.getLevel().recipeAccess().getRecipes().forEach(holder -> loaded.add(holder.id().identifier()));
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Identifier id : removedRecipes(helper).keySet()) {
			checked++;
			if (loaded.contains(id)) {
				failures.add("Recipe " + id + " is still loaded");
			}
		}
		report(helper, IDEA, "removed recipe checks", checked, failures);
	}

	// Every block that 2 stacked slabs no longer craft into is still cut by the stonecutter, if vanilla's stonecutter cut
	// it. Bamboo mosaic is the only one it never cut.
	@GameTest
	public void blocksNoLongerCraftedFromSlabsAreStillCut(GameTestHelper helper) {
		Set<Item> vanillaCuts = new HashSet<>();
		vanillaRecipes(helper).values().forEach(recipe -> {
			if (recipe instanceof StonecutterRecipe stonecutter) {
				vanillaCuts.add(cut(stonecutter).getItem());
			}
		});
		Set<Item> cuts = new HashSet<>();
		helper.getLevel().recipeAccess().getRecipes().forEach(holder -> {
			if (holder.value() instanceof StonecutterRecipe stonecutter) {
				cuts.add(cut(stonecutter).getItem());
			}
		});
		List<String> failures = new ArrayList<>();
		List<Item> neverCut = new ArrayList<>();
		int checked = 0;
		for (Map.Entry<Identifier, ItemStack> entry : removedRecipes(helper).entrySet()) {
			checked++;
			Item block = entry.getValue().getItem();
			if (!vanillaCuts.contains(block)) {
				neverCut.add(block);
			} else if (!cuts.contains(block)) {
				failures.add("No stonecutter recipe makes " + name(block) + ", whose crafting recipe " + entry.getKey() + " was removed");
			}
		}
		if (!neverCut.equals(List.of(Items.BAMBOO_MOSAIC))) {
			failures.add("Vanilla's stonecutter makes none of " + neverCut.stream().map(RecipeTestSupport::name).toList() + ", expected only minecraft:bamboo_mosaic");
		}
		report(helper, IDEA, "blocks once crafted from slabs", checked, failures);
	}

	// 2 blocks craft into 4 slabs, so 4 slabs burn exactly as long as 2 of their base block in a furnace, and never
	// longer in any furnace. Slabs of a block that does not burn cannot burn either.
	@GameTest
	public void craftingSlabsNeverAddsFuel(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int ofBlocksThatBurn = 0;
		for (Variant slab : vanillaSlabs(helper)) {
			ItemStack item = new ItemStack(slab.item());
			ItemStack base = new ItemStack(slab.base());
			if (furnaces.getFirst().burns(level, base)) {
				ofBlocksThatBurn++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int slabTime = furnace.burnDuration().applyAsInt(level, item);
				int baseTime = furnace.burnDuration().applyAsInt(level, base);
				boolean exact = furnace == furnaces.getFirst() && baseTime > 0;
				if (exact ? 4 * slabTime != 2 * baseTime : 4 * slabTime > 2 * baseTime) {
					failures.add("4 " + name(slab.item()) + " burn " + 4 * slabTime + " ticks in " + furnace.name() + ", 2 " + name(slab.base())
							+ " burn " + 2 * baseTime + ", expected " + (exact ? "the same" : "no more"));
				}
			}
		}
		if (ofBlocksThatBurn == 0) {
			helper.fail("No slab in #minecraft:slabs has a base block that burns in a furnace");
		}
		Caramel.LOGGER.info("C-002 game test: {} slabs have a base block that burns", ofBlocksThatBurn);
		report(helper, IDEA, "slab burn times", checked, failures);
	}

	// Each slab in #minecraft:slabs that has a vanilla recipe, sorted by id, with the blocks that recipe accepts. The
	// petrified oak slab has none.
	static List<Variant> vanillaSlabs(GameTestHelper helper) {
		return vanillaVariants(helper, SLABS, BASE_OF_ALTERNATIVES, false);
	}

	// The vanilla crafting recipes C-002 removes, by id, with what each makes: the recipes that 2 of the same slab
	// stacked also match. Found by crafting with every vanilla crafting recipe, decoded from the vanilla data pack.
	static Map<Identifier, ItemStack> removedRecipes(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<Variant> slabs = vanillaSlabs(helper);
		Map<Identifier, ItemStack> removed = new TreeMap<>();
		vanillaRecipes(helper).forEach((id, value) -> {
			if (!(value instanceof CraftingRecipe recipe)) {
				return;
			}
			for (Variant slab : slabs) {
				CraftingInput stacked = CraftingInput.of(1, 2, List.of(new ItemStack(slab.item()), new ItemStack(slab.item())));
				if (recipe.matches(stacked, level)) {
					removed.put(id, recipe.assemble(stacked));
				}
			}
		});
		return removed;
	}
}
