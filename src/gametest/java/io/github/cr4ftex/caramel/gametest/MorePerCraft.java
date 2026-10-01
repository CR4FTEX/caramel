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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.block.Block;

// The checks shared by the ideas that keep a vanilla recipe and make it give more: C-004's fences and C-005's fence
// gates. Which vanilla recipes are in scope, what each is crafted from and how many it gave, are read from the vanilla
// data pack's own recipe files, never from Caramel's, so a wrong generated recipe cannot also make its own expectation.
// Whether an item burned comes from the item components vanilla builds, before Caramel changes them.
final class MorePerCraft {
	private MorePerCraft() {
	}

	// One idea's scope: every vanilla crafting recipe whose result is a block in the tag (a block tag, since 26.3's
	// ItemTags has no constant for #minecraft:fences), how many there are in Minecraft 26.3, from docs/DESIGN.md, and how
	// many times vanilla's count each gives now, as the owner chose.
	record Scope(String idea, String what, TagKey<Block> blocks, int vanillaRecipes, int timesVanilla) {
		String plural() {
			return what + "s";
		}
	}

	// A vanilla recipe in a scope, its file in the vanilla data pack, and its grid, filled with the first item each of its
	// ingredients accepts, row by row. Vanilla's recipe gives vanillaResult for that grid.
	record Replaced(Identifier id, JsonObject file, ShapedRecipe recipe, List<ItemStack> cells, ItemStack vanillaResult, int timesVanilla) {
		Item item() {
			return vanillaResult.getItem();
		}

		// What the idea's recipe gives for the same grid.
		int count() {
			return timesVanilla * vanillaResult.getCount();
		}
	}

	static void allFound(GameTestHelper helper, Scope scope) {
		List<Replaced> found = vanillaRecipes(helper, scope);
		List<String> failures = scopeFailures(scope, found);
		if (!failures.isEmpty()) {
			helper.fail(String.join("\n", failures));
		}
		Caramel.LOGGER.info("{} game test: {} {} recipes, giving {} in vanilla", scope.idea(), found.size(), scope.what(),
				found.stream().map(replaced -> name(replaced.vanillaResult())).toList());
		helper.succeed();
	}

	// Vanilla's grid, at each place it fits in the crafting grids, gives the idea's count, and no other recipe matches it.
	static void gridsGiveMore(GameTestHelper helper, Scope scope) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaRecipes(helper, scope)) {
			List<Grid> grids = placements(replaced.recipe().getWidth(), replaced.recipe().getHeight(), replaced.cells());
			if (grids.isEmpty()) {
				failures.add(replaced.id() + "'s vanilla grid fits no crafting grid");
			}
			for (Grid grid : grids) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(replaced.item(), replaced.count())) {
					failures.add(replaced.id() + "'s vanilla grid " + grid.label() + " gave " + crafted + ", expected " + replaced.count() + " " + name(replaced.item()));
				}
			}
		}
		report(helper, scope.idea(), scope.what() + " grids", checked, failures);
	}

	// The recipe loaded at each vanilla id is vanilla's with the idea's count: the same shape, ingredients, group and
	// category. Both sides are compared as the recipe codec writes them: vanilla's file with its count multiplied, decoded
	// and encoded again, and the loaded recipe encoded.
	static void recipesAreVanillasWithMore(GameTestHelper helper, Scope scope) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		RecipeManager recipes = helper.getLevel().getServer().getRecipeManager();
		List<Replaced> found = vanillaRecipes(helper, scope);
		List<String> failures = new ArrayList<>();
		for (Replaced replaced : found) {
			JsonObject multiplied = replaced.file().deepCopy();
			multiplied.getAsJsonObject("result").addProperty("count", replaced.count());
			JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, multiplied).getOrThrow()).getOrThrow();
			Optional<RecipeHolder<?>> loaded = recipes.byKey(ResourceKey.create(Registries.RECIPE, replaced.id()));
			if (loaded.isEmpty()) {
				failures.add("Recipe " + replaced.id() + " is not loaded");
				continue;
			}
			JsonElement actual = Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow();
			if (!actual.equals(expected)) {
				failures.add("Recipe " + replaced.id() + " is " + actual + ", expected " + expected);
			}
		}
		report(helper, scope.idea(), scope.what() + " recipes", found.size(), failures);
	}

	// Each replaced recipe keeps vanilla's unlock advancement, the vanilla file at recipes/<folder>/<recipe path>: the
	// one that loads, Caramel's copy if its pack holds one, must equal vanilla's.
	static void keepTheVanillaUnlockAdvancement(GameTestHelper helper, Scope scope) {
		Map<Identifier, List<Identifier>> unlocks = vanillaUnlocks(helper);
		Map<Identifier, JsonObject> vanilla = packFiles(helper, "vanilla", Registries.ADVANCEMENT);
		Map<Identifier, JsonObject> caramel = packFiles(helper, Caramel.MOD_ID, Registries.ADVANCEMENT);
		ServerAdvancementManager loaded = helper.getLevel().getServer().getAdvancements();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaRecipes(helper, scope)) {
			checked++;
			List<Identifier> found = unlocks.getOrDefault(replaced.id(), List.of());
			if (found.size() != 1) {
				failures.add("The vanilla data pack has " + found.size() + " unlock advancements for recipe " + replaced.id() + " " + found + ", expected 1");
				continue;
			}
			Identifier advancement = found.getFirst();
			if (!vanilla.get(advancement).equals(caramel.getOrDefault(advancement, vanilla.get(advancement)))) {
				failures.add("Caramel's advancement " + advancement + " differs from vanilla's");
			} else if (loaded.get(advancement) == null) {
				failures.add("Advancement " + advancement + " is not loaded");
			}
		}
		report(helper, scope.idea(), "unlock advancements", checked, failures);
	}

	// A craft costs its planks, and its sticks at what vanilla's stick recipe takes for them. Each item that burned in
	// vanilla burns that cost in a furnace, divided among the items the craft gives and rounded down, and the items of one
	// craft never burn longer than the planks they cost in any furnace.
	static void craftingNeverAddsFuel(GameTestHelper helper, Scope scope) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		Fraction planksPerStick = planksPerStick(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		for (Replaced replaced : vanillaRecipes(helper, scope)) {
			ItemStack item = new ItemStack(replaced.item());
			if (!furnaces.getFirst().burns(level, withVanillaFuel(item, vanilla))) {
				continue;
			}
			burned++;
			Fraction planks = Fraction.ZERO;
			ItemStack plank = ItemStack.EMPTY;
			for (ItemStack cell : replaced.cells()) {
				if (cell.is(ItemTags.PLANKS)) {
					planks = planks.add(Fraction.ONE);
					plank = cell;
				} else if (cell.is(Items.STICK)) {
					planks = planks.add(planksPerStick);
				} else if (!cell.isEmpty()) {
					failures.add(replaced.id() + " takes " + name(cell.getItem()) + ", which is neither planks nor a stick");
				}
			}
			if (plank.isEmpty()) {
				failures.add(replaced.id() + " burned in vanilla and takes no planks");
				continue;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int itemTime = furnace.burnDuration().applyAsInt(level, item);
				Fraction planksTime = planks.multiplyBy(Fraction.getFraction(furnace.burnDuration().applyAsInt(level, plank), 1));
				int expected = planksTime.divideBy(Fraction.getFraction(replaced.count(), 1)).intValue();
				if (furnace == furnaces.getFirst() && itemTime != expected) {
					failures.add(name(replaced.item()) + " burns " + itemTime + " ticks in " + furnace.name() + ", expected " + expected + ": " + planks
							+ " " + name(plank.getItem()) + " burn " + planksTime + " and make " + replaced.count());
				} else if (Fraction.getFraction(replaced.count() * itemTime, 1).compareTo(planksTime) > 0) {
					failures.add(replaced.count() + " " + name(replaced.item()) + " burn " + replaced.count() * itemTime + " ticks in " + furnace.name() + ", the "
							+ planks + " " + name(plank.getItem()) + " they cost burn " + planksTime + ", expected no more");
				}
			}
		}
		if (burned == 0) {
			helper.fail("No " + scope.what() + " burned in a furnace in vanilla");
		}
		Caramel.LOGGER.info("{} game test: {} {} burned in vanilla, a stick costs {} planks", scope.idea(), burned, scope.plural(), planksPerStick);
		report(helper, scope.idea(), scope.what() + " burn times", checked, failures);
	}

	// Every item in scope that burned in vanilla still burns in every furnace, at vanilla's cooking speed, and every one
	// that did not still does not: the furnace takes it only if vanilla's did. With changesFuel, at least one must have a
	// different fuel from vanilla's, which proves the vanilla item components really are from before Caramel changed them.
	static void thatBurnedStillBurn(GameTestHelper helper, Scope scope, boolean changesFuel) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		int changed = 0;
		List<Replaced> found = vanillaRecipes(helper, scope);
		for (Replaced replaced : found) {
			ItemStack now = new ItemStack(replaced.item());
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
					failures.add(name(replaced.item()) + (burnedBefore ? " no longer burns" : " burns") + " in " + furnace.name() + (burnedBefore ? "" : ", but did not in vanilla"));
				}
				if (!burnedBefore && furnace.fuelSlot().test(now) != furnace.fuelSlot().test(before)) {
					failures.add(furnace.name() + (furnace.fuelSlot().test(now) ? " takes " : " no longer takes ") + name(replaced.item()) + " as fuel");
				}
				float speedBefore = furnace.speedMultiplier().apply(level, before);
				float speedNow = furnace.speedMultiplier().apply(level, now);
				if (burnedBefore && speedNow != speedBefore) {
					failures.add(name(replaced.item()) + " makes " + furnace.name() + " cook " + speedNow + " times as fast, " + speedBefore + " in vanilla");
				}
			}
		}
		if (changesFuel && changed == 0) {
			helper.fail("No " + scope.what() + " has a different fuel from vanilla's, so the vanilla item components are not vanilla's");
		}
		Caramel.LOGGER.info("{} game test: {} {} burned in vanilla, {} did not, {} have a different fuel now", scope.idea(), burned, scope.plural(),
				found.size() - burned, changed);
		report(helper, scope.idea(), scope.what() + " fuel checks", checked, failures);
	}

	// Every vanilla crafting recipe whose result is a block in the scope's tag, sorted by id, found by decoding the vanilla
	// data pack. Each must be shaped.
	static List<Replaced> vanillaRecipes(GameTestHelper helper, Scope scope) {
		ContextMap display = SlotDisplayContext.fromLevel(helper.getLevel());
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		List<Replaced> found = new ArrayList<>();
		packFiles(helper, "vanilla", Registries.RECIPE).forEach((id, file) -> {
			if (!(Recipe.DIRECT_CODEC.parse(ops, file).getOrThrow() instanceof CraftingRecipe recipe)
					|| recipe.display().stream().noneMatch(shown -> inScope(scope, shown.result().resolveForFirstStack(display)))) {
				return;
			}
			if (!(recipe instanceof ShapedRecipe shaped)) {
				throw helper.assertionException(Component.literal(id + " in the vanilla data pack makes a " + scope.what() + " and is not a shaped recipe"));
			}
			List<ItemStack> cells = filled(shaped);
			found.add(new Replaced(id, file, shaped, cells, shaped.assemble(CraftingInput.of(shaped.getWidth(), shaped.getHeight(), cells)), scope.timesVanilla()));
		});
		return found;
	}

	// The scope from docs/DESIGN.md, and what the recipes found make. A Minecraft update that shifts it fails here, so
	// the scope gets rechecked.
	static List<String> scopeFailures(Scope scope, List<Replaced> found) {
		List<String> failures = new ArrayList<>();
		if (found.size() != scope.vanillaRecipes()) {
			failures.add("Expected " + scope.vanillaRecipes() + " " + scope.what() + " recipes to replace, found " + found.size() + " "
					+ found.stream().map(Replaced::id).toList());
		}
		for (Replaced replaced : found) {
			if (!inScope(scope, replaced.vanillaResult())) {
				failures.add(replaced.id() + "'s vanilla grid makes " + name(replaced.vanillaResult()) + ", which is not a " + scope.what());
			} else if (!replaced.id().equals(BuiltInRegistries.ITEM.getKey(replaced.item()))) {
				failures.add(replaced.id() + " makes " + name(replaced.item()) + ", which has another id");
			}
		}
		return failures;
	}

	private static boolean inScope(Scope scope, ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(scope.blocks());
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

	// A shaped recipe's grid, row by row, with the first item each ingredient accepts.
	private static List<ItemStack> filled(ShapedRecipe recipe) {
		return recipe.getIngredients().stream()
				.map(ingredient -> ingredient.map(accepts -> new ItemStack(accepted(accepts).getFirst())).orElse(ItemStack.EMPTY))
				.toList();
	}
}
