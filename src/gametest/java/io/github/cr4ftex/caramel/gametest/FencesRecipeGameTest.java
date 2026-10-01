package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.accepted;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.furnaces;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaComponents;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaUnlocks;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.withVanillaFuel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import org.apache.commons.lang3.math.Fraction;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Furnace;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

// C-004. Which vanilla recipes make fences, what each is crafted from and how many it gave, are read from the vanilla
// data pack's own recipe files, never from Caramel's, so a wrong generated recipe cannot also make its own expectation.
// Whether a fence burned comes from the item components vanilla builds, before Caramel changes them.
public class FencesRecipeGameTest {
	private static final String IDEA = "C-004";
	// C-004's scope in Minecraft 26.3, from docs/DESIGN.md: the 13 wooden fences and the nether brick fence.
	static final int VANILLA_FENCES = 14;
	// The owner's rule: every fence recipe gives 4 times vanilla's count, 12 wooden fences instead of 3 and 24 nether
	// brick fences instead of 6.
	private static final int TIMES_VANILLA = 4;

	// A vanilla recipe that makes a fence, its file in the vanilla data pack, and its grid, filled with the first item each
	// of its ingredients accepts, row by row. Vanilla's recipe gives vanillaResult for that grid.
	record Fence(Identifier id, JsonObject file, ShapedRecipe recipe, List<ItemStack> cells, ItemStack vanillaResult) {
		Item item() {
			return vanillaResult.getItem();
		}

		// What C-004's recipe gives for the same grid.
		int count() {
			return TIMES_VANILLA * vanillaResult.getCount();
		}
	}

	@GameTest
	public void vanillaFencesAreAllFound(GameTestHelper helper) {
		List<Fence> fences = vanillaFences(helper);
		List<String> failures = scopeFailures(fences);
		if (!failures.isEmpty()) {
			helper.fail(String.join("\n", failures));
		}
		Caramel.LOGGER.info("C-004 game test: {} fence recipes, giving {} in vanilla", fences.size(),
				fences.stream().map(fence -> name(fence.vanillaResult())).toList());
		helper.succeed();
	}

	// Vanilla's grid, at each place it fits in the 3x3 grid, gives 4 times vanilla's count, and no other recipe matches it.
	@GameTest
	public void fenceRecipesGiveFourTimesAsMany(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Fence fence : vanillaFences(helper)) {
			List<Grid> grids = placements(fence.recipe().getWidth(), fence.recipe().getHeight(), fence.cells());
			if (grids.isEmpty()) {
				failures.add(fence.id() + "'s vanilla grid fits no crafting grid");
			}
			for (Grid grid : grids) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(fence.item(), fence.count())) {
					failures.add(fence.id() + "'s vanilla grid " + grid.label() + " gave " + crafted + ", expected " + fence.count() + " " + name(fence.item()));
				}
			}
		}
		report(helper, IDEA, "fence grids", checked, failures);
	}

	// The recipe loaded at each vanilla id is vanilla's with 4 times the count: the same shape, ingredients, group and
	// category. Both sides are compared as the recipe codec writes them: vanilla's file with its count multiplied, decoded
	// and encoded again, and the loaded recipe encoded.
	@GameTest
	public void fenceRecipesAreVanillasWithFourTimesTheCount(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		RecipeManager recipes = helper.getLevel().getServer().getRecipeManager();
		List<Fence> fences = vanillaFences(helper);
		List<String> failures = new ArrayList<>();
		for (Fence fence : fences) {
			JsonObject multiplied = fence.file().deepCopy();
			multiplied.getAsJsonObject("result").addProperty("count", fence.count());
			JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, multiplied).getOrThrow()).getOrThrow();
			Optional<RecipeHolder<?>> loaded = recipes.byKey(ResourceKey.create(Registries.RECIPE, fence.id()));
			if (loaded.isEmpty()) {
				failures.add("Recipe " + fence.id() + " is not loaded");
				continue;
			}
			JsonElement actual = Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow();
			if (!actual.equals(expected)) {
				failures.add("Recipe " + fence.id() + " is " + actual + ", expected " + expected);
			}
		}
		report(helper, IDEA, "fence recipes", fences.size(), failures);
	}

	// Each replaced recipe keeps vanilla's unlock advancement, the vanilla file at recipes/<folder>/<recipe path>: the
	// one that loads, Caramel's copy if its pack holds one, must equal vanilla's.
	@GameTest
	public void fenceRecipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		Map<Identifier, List<Identifier>> unlocks = vanillaUnlocks(helper);
		Map<Identifier, JsonObject> vanilla = packFiles(helper, "vanilla", Registries.ADVANCEMENT);
		Map<Identifier, JsonObject> caramel = packFiles(helper, Caramel.MOD_ID, Registries.ADVANCEMENT);
		ServerAdvancementManager loaded = helper.getLevel().getServer().getAdvancements();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Fence fence : vanillaFences(helper)) {
			checked++;
			List<Identifier> found = unlocks.getOrDefault(fence.id(), List.of());
			if (found.size() != 1) {
				failures.add("The vanilla data pack has " + found.size() + " unlock advancements for recipe " + fence.id() + " " + found + ", expected 1");
				continue;
			}
			Identifier advancement = found.getFirst();
			if (!vanilla.get(advancement).equals(caramel.getOrDefault(advancement, vanilla.get(advancement)))) {
				failures.add("Caramel's advancement " + advancement + " differs from vanilla's");
			} else if (loaded.get(advancement) == null) {
				failures.add("Advancement " + advancement + " is not loaded");
			}
		}
		report(helper, IDEA, "unlock advancements", checked, failures);
	}

	// A craft of fences costs its planks, and its sticks at what vanilla's stick recipe takes for them. Each fence that
	// burned in vanilla burns that cost in a furnace, divided among the fences and rounded down, and the fences of one
	// craft never burn longer than the planks they cost in any furnace.
	@GameTest
	public void craftingFencesNeverAddsFuel(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		Fraction planksPerStick = planksPerStick(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		for (Fence fence : vanillaFences(helper)) {
			ItemStack item = new ItemStack(fence.item());
			if (!furnaces.getFirst().burns(level, withVanillaFuel(item, vanilla))) {
				continue;
			}
			burned++;
			Fraction planks = Fraction.ZERO;
			ItemStack plank = ItemStack.EMPTY;
			for (ItemStack cell : fence.cells()) {
				if (cell.is(ItemTags.PLANKS)) {
					planks = planks.add(Fraction.ONE);
					plank = cell;
				} else if (cell.is(Items.STICK)) {
					planks = planks.add(planksPerStick);
				} else if (!cell.isEmpty()) {
					failures.add(fence.id() + " takes " + name(cell.getItem()) + ", which is neither planks nor a stick");
				}
			}
			if (plank.isEmpty()) {
				failures.add(fence.id() + " burned in vanilla and takes no planks");
				continue;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int fenceTime = furnace.burnDuration().applyAsInt(level, item);
				Fraction planksTime = planks.multiplyBy(Fraction.getFraction(furnace.burnDuration().applyAsInt(level, plank), 1));
				int expected = planksTime.divideBy(Fraction.getFraction(fence.count(), 1)).intValue();
				if (furnace == furnaces.getFirst() && fenceTime != expected) {
					failures.add(name(fence.item()) + " burns " + fenceTime + " ticks in " + furnace.name() + ", expected " + expected + ": " + planks
							+ " " + name(plank.getItem()) + " burn " + planksTime + " and make " + fence.count());
				} else if (Fraction.getFraction(fence.count() * fenceTime, 1).compareTo(planksTime) > 0) {
					failures.add(fence.count() + " " + name(fence.item()) + " burn " + fence.count() * fenceTime + " ticks in " + furnace.name() + ", the "
							+ planks + " " + name(plank.getItem()) + " they cost burn " + planksTime + ", expected no more");
				}
			}
		}
		if (burned == 0) {
			helper.fail("No fence burned in a furnace in vanilla");
		}
		Caramel.LOGGER.info("C-004 game test: {} fences burned in vanilla, a stick costs {} planks", burned, planksPerStick);
		report(helper, IDEA, "fence burn times", checked, failures);
	}

	// Every fence that burned in vanilla still burns in every furnace, at vanilla's cooking speed, and every one that did
	// not still does not: the furnace takes it only if vanilla's did.
	@GameTest
	public void fencesThatBurnedStillBurn(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		int changed = 0;
		List<Fence> fences = vanillaFences(helper);
		for (Fence fence : fences) {
			ItemStack now = new ItemStack(fence.item());
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
					failures.add(name(fence.item()) + (burnedBefore ? " no longer burns" : " burns") + " in " + furnace.name() + (burnedBefore ? "" : ", but did not in vanilla"));
				}
				if (!burnedBefore && furnace.fuelSlot().test(now) != furnace.fuelSlot().test(before)) {
					failures.add(furnace.name() + (furnace.fuelSlot().test(now) ? " takes " : " no longer takes ") + name(fence.item()) + " as fuel");
				}
				float speedBefore = furnace.speedMultiplier().apply(level, before);
				float speedNow = furnace.speedMultiplier().apply(level, now);
				if (burnedBefore && speedNow != speedBefore) {
					failures.add(name(fence.item()) + " makes " + furnace.name() + " cook " + speedNow + " times as fast, " + speedBefore + " in vanilla");
				}
			}
		}
		// Proves the vanilla components really are from before Caramel changed them.
		if (changed == 0) {
			helper.fail("No fence has a different fuel from vanilla's, so the vanilla item components are not vanilla's");
		}
		Caramel.LOGGER.info("C-004 game test: {} fences burned in vanilla, {} did not, {} have a different fuel now", burned, fences.size() - burned, changed);
		report(helper, IDEA, "fence fuel checks", checked, failures);
	}

	// Every vanilla crafting recipe that makes a fence, sorted by id, found by decoding the vanilla data pack. Each must be
	// shaped.
	static List<Fence> vanillaFences(GameTestHelper helper) {
		ContextMap display = SlotDisplayContext.fromLevel(helper.getLevel());
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		List<Fence> fences = new ArrayList<>();
		packFiles(helper, "vanilla", Registries.RECIPE).forEach((id, file) -> {
			if (!(Recipe.DIRECT_CODEC.parse(ops, file).getOrThrow() instanceof CraftingRecipe recipe)
					|| recipe.display().stream().noneMatch(shown -> isFence(shown.result().resolveForFirstStack(display)))) {
				return;
			}
			if (!(recipe instanceof ShapedRecipe shaped)) {
				throw helper.assertionException(Component.literal(id + " in the vanilla data pack makes a fence and is not a shaped recipe"));
			}
			List<ItemStack> cells = filled(shaped);
			fences.add(new Fence(id, file, shaped, cells, shaped.assemble(CraftingInput.of(shaped.getWidth(), shaped.getHeight(), cells))));
		});
		return fences;
	}

	// C-004's scope from docs/DESIGN.md, and what the recipes it found make. A Minecraft update that shifts it fails here,
	// so the scope gets rechecked.
	static List<String> scopeFailures(List<Fence> fences) {
		List<String> failures = new ArrayList<>();
		if (fences.size() != VANILLA_FENCES) {
			failures.add("Expected " + VANILLA_FENCES + " fence recipes to replace, found " + fences.size() + " " + fences.stream().map(Fence::id).toList());
		}
		for (Fence fence : fences) {
			if (!isFence(fence.vanillaResult())) {
				failures.add(fence.id() + "'s vanilla grid makes " + name(fence.vanillaResult()) + ", which is not a fence");
			} else if (!fence.id().equals(BuiltInRegistries.ITEM.getKey(fence.item()))) {
				failures.add(fence.id() + " makes " + name(fence.item()) + ", which has another id");
			}
		}
		return failures;
	}

	// What vanilla's minecraft:stick recipe takes per stick, in planks: filled with the first item each ingredient accepts,
	// it must take nothing but planks.
	private static Fraction planksPerStick(GameTestHelper helper) {
		Identifier id = BuiltInRegistries.ITEM.getKey(Items.STICK);
		if (!(RecipeTestSupport.vanillaRecipes(helper).get(id) instanceof ShapedRecipe recipe)) {
			throw helper.assertionException(Component.literal("The vanilla data pack has no shaped recipe " + id));
		}
		List<ItemStack> cells = filled(recipe);
		ItemStack sticks = recipe.assemble(CraftingInput.of(recipe.getWidth(), recipe.getHeight(), cells));
		List<ItemStack> taken = cells.stream().filter(cell -> !cell.isEmpty()).toList();
		if (!sticks.is(Items.STICK) || taken.isEmpty() || !taken.stream().allMatch(cell -> cell.is(ItemTags.PLANKS))) {
			throw helper.assertionException(Component.literal(id + " in the vanilla data pack makes " + name(sticks) + " from " + taken + ", expected sticks from planks"));
		}
		return Fraction.getFraction(taken.size(), sticks.getCount()).reduce();
	}

	// A fence is an item whose block is in #minecraft:fences, a block tag: 26.3's ItemTags has no constant for the item tag.
	private static boolean isFence(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTags.FENCES);
	}

	// A shaped recipe's grid, row by row, with the first item each ingredient accepts.
	private static List<ItemStack> filled(ShapedRecipe recipe) {
		return recipe.getIngredients().stream()
				.map(ingredient -> ingredient.map(accepts -> new ItemStack(accepted(accepts).getFirst())).orElse(ItemStack.EMPTY))
				.toList();
	}
}
