package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.accepted;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Block;

// C-006: 6 planks make 16 wooden doors or trapdoors, checked by MorePerCraft. The iron and copper doors and trapdoors
// take the wooden shapes, made of 6 blocks. Their expected recipes are read from the vanilla data pack too: each is
// vanilla's recipe with the wooden counterpart's pattern, the block that 9 of vanilla's ingot craft into, and a count of
// 16, so a wrong generated recipe cannot also make its own expectation.
public class DoorsAndTrapdoorsGameTest {
	private static final String IDEA = "C-006";
	// C-006's scope in Minecraft 26.3, from docs/DESIGN.md: 13 wooden doors and 13 wooden trapdoors, the iron and copper
	// door and trapdoor, and the 8 honeycomb recipes that wax a copper door or trapdoor, which stay 1 for 1.
	static final MorePerCraft.Scope WOODEN = new MorePerCraft.Scope(IDEA, "wooden door or trapdoor", "wooden doors and trapdoors",
			List.of(BlockTags.WOODEN_DOORS, BlockTags.WOODEN_TRAPDOORS), 26, count -> 16);
	static final int VANILLA_FROM_INGOTS = 4;
	static final int VANILLA_WAXING = 8;
	private static final int COUNT = 16;

	// A vanilla recipe that makes an iron or copper door or trapdoor from one ingot, and what C-006 makes it from: the
	// wooden counterpart's pattern and key, with every key filled by the block that 9 of the ingot craft into.
	record FromBlocks(Identifier id, JsonObject file, ShapedRecipe vanilla, Item result, Item ingot, Item block, JsonObject wooden) {
		int width() {
			return wooden.getAsJsonArray("pattern").get(0).getAsString().length();
		}

		int height() {
			return wooden.getAsJsonArray("pattern").size();
		}

		// The new grid, row by row: a block wherever the wooden pattern has a key.
		List<ItemStack> cells() {
			List<ItemStack> cells = new ArrayList<>();
			wooden.getAsJsonArray("pattern").forEach(row -> row.getAsString().chars()
					.forEach(cell -> cells.add(cell == ' ' ? ItemStack.EMPTY : new ItemStack(block))));
			return cells;
		}
	}

	@GameTest
	public void vanillaDoorsAndTrapdoorsAreAllFound(GameTestHelper helper) {
		List<String> failures = new ArrayList<>(MorePerCraft.scopeFailures(WOODEN, MorePerCraft.vanillaRecipes(helper, WOODEN)));
		failures.addAll(scopeFailures(vanillaFromIngots(helper), vanillaWaxing(helper)));
		if (!failures.isEmpty()) {
			helper.fail(String.join("\n", failures));
		}
		MorePerCraft.allFound(helper, WOODEN);
	}

	@GameTest
	public void woodenRecipesGiveSixteen(GameTestHelper helper) {
		MorePerCraft.gridsGiveMore(helper, WOODEN);
	}

	@GameTest
	public void woodenRecipesAreVanillasWithSixteen(GameTestHelper helper) {
		MorePerCraft.recipesAreVanillasWithMore(helper, WOODEN);
	}

	@GameTest
	public void recipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		List<Identifier> ids = new ArrayList<>(MorePerCraft.vanillaRecipes(helper, WOODEN).stream().map(MorePerCraft.Replaced::id).toList());
		vanillaFromIngots(helper).forEach(remade -> ids.add(remade.id()));
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, IDEA, ids);
	}

	// 6 planks make 16, so a wooden door or trapdoor burns 6/16 of a plank, rounded down.
	@GameTest
	public void craftingWoodenDoorsAndTrapdoorsNeverAddsFuel(GameTestHelper helper) {
		MorePerCraft.craftingNeverAddsFuel(helper, WOODEN);
	}

	@GameTest
	public void woodenDoorsAndTrapdoorsThatBurnedStillBurn(GameTestHelper helper) {
		MorePerCraft.thatBurnedStillBurn(helper, WOODEN, true);
	}

	// The recipe loaded at each vanilla id is vanilla's with the wooden counterpart's pattern and key, the block in every
	// key and a count of 16: the same group and category. Both sides are compared as the recipe codec writes them.
	@GameTest
	public void ironAndCopperDoorsAndTrapdoorsAreRemadeFromBlocks(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		List<FromBlocks> remade = vanillaFromIngots(helper);
		List<String> failures = new ArrayList<>();
		for (FromBlocks recipe : remade) {
			JsonObject file = recipe.file().deepCopy();
			file.add("pattern", recipe.wooden().getAsJsonArray("pattern").deepCopy());
			JsonObject key = new JsonObject();
			recipe.wooden().getAsJsonObject("key").keySet().forEach(symbol -> key.addProperty(symbol, BuiltInRegistries.ITEM.getKey(recipe.block()).toString()));
			file.add("key", key);
			file.getAsJsonObject("result").addProperty("count", COUNT);
			JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, file).getOrThrow()).getOrThrow();
			Optional<RecipeHolder<?>> loaded = helper.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, recipe.id()));
			if (loaded.isEmpty()) {
				failures.add("Recipe " + recipe.id() + " is not loaded");
				continue;
			}
			JsonElement actual = Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow();
			if (!actual.equals(expected)) {
				failures.add("Recipe " + recipe.id() + " is " + actual + ", expected " + expected);
			}
		}
		Caramel.LOGGER.info("C-006 game test: {} remade from blocks: {}", remade.size(),
				remade.stream().map(recipe -> name(recipe.result()) + " from " + name(recipe.block())).toList());
		report(helper, IDEA, "recipes remade from blocks", remade.size(), failures);
	}

	// The new grid, at each place it fits in the crafting grids, gives 16, and no other recipe matches it.
	@GameTest
	public void sixBlocksMakeSixteenIronAndCopperDoorsAndTrapdoors(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (FromBlocks recipe : vanillaFromIngots(helper)) {
			for (Grid grid : placements(recipe.width(), recipe.height(), recipe.cells())) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(recipe.result(), COUNT)) {
					failures.add(recipe.cells().size() + " " + name(recipe.block()) + " " + grid.label() + " gave " + crafted + ", expected " + COUNT + " " + name(recipe.result()));
				}
			}
		}
		report(helper, IDEA, "block grids", checked, failures);
	}

	// Vanilla's ingot grids, at each place they fit, give nothing now.
	@GameTest
	public void oldIngotGridsGiveNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (FromBlocks recipe : vanillaFromIngots(helper)) {
			List<ItemStack> cells = recipe.vanilla().getIngredients().stream()
					.map(ingredient -> ingredient.map(accepts -> new ItemStack(recipe.ingot())).orElse(ItemStack.EMPTY))
					.toList();
			for (Grid grid : placements(recipe.vanilla().getWidth(), recipe.vanilla().getHeight(), cells)) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.recipes().isEmpty()) {
					failures.add(recipe.id() + "'s vanilla grid of " + name(recipe.ingot()) + " " + grid.label() + " gave " + crafted + ", expected nothing");
				}
			}
		}
		report(helper, IDEA, "old ingot grids", checked, failures);
	}

	// Waxing a copper door or trapdoor with honeycomb still turns 1 into 1: the recipe loaded at each vanilla id is
	// vanilla's, and Caramel's data pack holds no file there.
	@GameTest
	public void waxingStaysOneForOne(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		Map<Identifier, JsonObject> waxing = vanillaWaxing(helper);
		Map<Identifier, JsonObject> caramel = packFiles(helper, Caramel.MOD_ID, Registries.RECIPE);
		List<String> failures = new ArrayList<>();
		waxing.forEach((id, file) -> {
			if (caramel.containsKey(id)) {
				failures.add("Caramel's data pack has recipe " + id);
			}
			JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, file).getOrThrow()).getOrThrow();
			Optional<RecipeHolder<?>> loaded = helper.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id));
			if (loaded.isEmpty()) {
				failures.add("Recipe " + id + " is not loaded");
			} else if (!Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow().equals(expected)) {
				failures.add("Recipe " + id + " differs from vanilla's");
			}
		});
		report(helper, IDEA, "waxing recipes", waxing.size(), failures);
	}

	// Every vanilla crafting recipe that makes a door or trapdoor outside the wooden tags and takes none, sorted by id. Each
	// must be shaped, take one ingot that 9 of craft into one block, and have a wooden counterpart whose recipes share one
	// pattern.
	static List<FromBlocks> vanillaFromIngots(GameTestHelper helper) {
		Map<Identifier, Recipe<?>> vanilla = RecipeTestSupport.vanillaRecipes(helper);
		Map<Identifier, JsonObject> files = packFiles(helper, "vanilla", Registries.RECIPE);
		List<FromBlocks> found = new ArrayList<>();
		vanillaMaking(helper, false).forEach((id, recipe) -> {
			if (madeFrom(helper, recipe).stream().anyMatch(DoorsAndTrapdoorsGameTest::isDoorOrTrapdoor)) {
				return;
			}
			if (!(recipe instanceof ShapedRecipe shaped)) {
				throw helper.assertionException(Component.literal(id + " in the vanilla data pack is not a shaped recipe"));
			}
			List<Item> ingots = madeFrom(helper, recipe);
			if (ingots.size() != 1) {
				throw helper.assertionException(Component.literal(id + " in the vanilla data pack takes " + ingots + ", expected one item"));
			}
			Item ingot = ingots.getFirst();
			Item result = result(helper, recipe).getItem();
			TagKey<Block> wooden = isIn(result, BlockTags.DOORS) ? BlockTags.WOODEN_DOORS : BlockTags.WOODEN_TRAPDOORS;
			found.add(new FromBlocks(id, files.get(id), shaped, result, ingot, storageBlock(helper, vanilla, ingot), woodenCounterpart(helper, files, wooden)));
		});
		return found;
	}

	// Every vanilla crafting recipe that makes a door or trapdoor and takes one: honeycomb waxing.
	static Map<Identifier, JsonObject> vanillaWaxing(GameTestHelper helper) {
		Map<Identifier, JsonObject> files = packFiles(helper, "vanilla", Registries.RECIPE);
		Map<Identifier, JsonObject> waxing = new TreeMap<>();
		vanillaMaking(helper, false).forEach((id, recipe) -> {
			if (madeFrom(helper, recipe).stream().anyMatch(DoorsAndTrapdoorsGameTest::isDoorOrTrapdoor)) {
				waxing.put(id, files.get(id));
			}
		});
		return waxing;
	}

	// C-006's scope from docs/DESIGN.md beyond the wooden recipes. A Minecraft update that shifts it fails here.
	static List<String> scopeFailures(List<FromBlocks> fromIngots, Map<Identifier, JsonObject> waxing) {
		List<String> failures = new ArrayList<>();
		if (fromIngots.size() != VANILLA_FROM_INGOTS || waxing.size() != VANILLA_WAXING) {
			failures.add("Expected " + VANILLA_FROM_INGOTS + " door and trapdoor recipes from ingots and " + VANILLA_WAXING + " waxing recipes, found "
					+ fromIngots.size() + " " + fromIngots.stream().map(FromBlocks::id).toList() + " and " + waxing.size() + " " + waxing.keySet());
		}
		for (FromBlocks recipe : fromIngots) {
			if (!recipe.id().equals(BuiltInRegistries.ITEM.getKey(recipe.result()))) {
				failures.add(recipe.id() + " makes " + name(recipe.result()) + ", which has another id");
			}
		}
		return failures;
	}

	// The vanilla crafting recipes that make a door or trapdoor, sorted by id: those in the wooden tags, or with wooden
	// false, those outside them.
	private static Map<Identifier, Recipe<?>> vanillaMaking(GameTestHelper helper, boolean wooden) {
		Map<Identifier, Recipe<?>> making = new TreeMap<>();
		RecipeTestSupport.vanillaRecipes(helper).forEach((id, recipe) -> {
			if (recipe instanceof CraftingRecipe && isDoorOrTrapdoor(result(helper, recipe).getItem())) {
				Item result = result(helper, recipe).getItem();
				if (wooden == (isIn(result, BlockTags.WOODEN_DOORS) || isIn(result, BlockTags.WOODEN_TRAPDOORS))) {
					making.put(id, recipe);
				}
			}
		});
		return making;
	}

	// The pattern and key every vanilla recipe for the wooden tag shares, from its file.
	private static JsonObject woodenCounterpart(GameTestHelper helper, Map<Identifier, JsonObject> files, TagKey<Block> wooden) {
		List<JsonObject> shapes = vanillaMaking(helper, true).entrySet().stream()
				.filter(entry -> isIn(result(helper, entry.getValue()).getItem(), wooden))
				.map(entry -> {
					JsonObject shape = new JsonObject();
					shape.add("pattern", files.get(entry.getKey()).get("pattern"));
					JsonObject symbols = new JsonObject();
					files.get(entry.getKey()).getAsJsonObject("key").keySet().forEach(symbol -> symbols.addProperty(symbol, ""));
					shape.add("key", symbols);
					return shape;
				})
				.distinct()
				.toList();
		if (shapes.size() != 1 || !(shapes.getFirst().get("pattern") instanceof JsonArray)) {
			throw helper.assertionException(Component.literal("The vanilla recipes for " + wooden.location() + " have " + shapes.size() + " patterns, expected 1: " + shapes));
		}
		return shapes.getFirst();
	}

	// The one vanilla recipe that crafts 9 of the ingot, filling a 3x3 grid, into 1 block.
	private static Item storageBlock(GameTestHelper helper, Map<Identifier, Recipe<?>> vanilla, Item ingot) {
		CraftingInput nine = CraftingInput.of(3, 3, Collections.nCopies(9, new ItemStack(ingot)));
		List<Item> blocks = vanilla.values().stream()
				.filter(recipe -> recipe instanceof ShapedRecipe shaped && shaped.getWidth() == 3 && shaped.getHeight() == 3 && shaped.matches(nine, helper.getLevel()))
				.map(recipe -> ((ShapedRecipe) recipe).assemble(nine))
				.filter(stack -> stack.getCount() == 1 && stack.getItem() instanceof BlockItem)
				.map(ItemStack::getItem)
				.toList();
		if (blocks.size() != 1) {
			throw helper.assertionException(Component.literal("9 " + name(ingot) + " craft into " + blocks + ", expected one block"));
		}
		return blocks.getFirst();
	}

	// Every distinct item a recipe's ingredients accept first, in its own order.
	private static List<Item> madeFrom(GameTestHelper helper, Recipe<?> recipe) {
		if (!(recipe instanceof CraftingRecipe crafting)) {
			return List.of();
		}
		return crafting.placementInfo().ingredients().stream().map(ingredient -> accepted(ingredient).getFirst()).distinct().toList();
	}

	private static ItemStack result(GameTestHelper helper, Recipe<?> recipe) {
		ServerLevel level = helper.getLevel();
		ContextMap display = SlotDisplayContext.fromLevel(level);
		return recipe.display().stream().map(shown -> shown.result().resolveForFirstStack(display)).findFirst().orElse(ItemStack.EMPTY);
	}

	private static boolean isDoorOrTrapdoor(Item item) {
		return isIn(item, BlockTags.DOORS) || isIn(item, BlockTags.TRAPDOORS);
	}

	private static boolean isIn(Item item, TagKey<Block> tag) {
		return item instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(tag);
	}
}
