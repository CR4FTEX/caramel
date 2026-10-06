package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.cut;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.firstInput;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;

// C-008: every wall recipe gives 12 instead of 6, checked by MorePerCraft, and every stonecutter recipe that cuts a wall
// gives 2 instead of 1, as the owner chose. Which stonecutter recipes are in scope, what each cuts and how many it gave,
// are read from the vanilla data pack, never from Caramel's.
public class WallsRecipeGameTest {
	private static final String IDEA = "C-008";
	// C-008's scope in Minecraft 26.3, from docs/DESIGN.md: 32 walls, each 6 blocks for 6 walls, and 57 stonecutter recipes
	// that cut a wall, each 1 block for 1 wall, 32 from the wall's own block and 25 from another. The owner's rule: twice
	// vanilla's count, 12 walls from the crafting grid and 2 from the stonecutter.
	static final MorePerCraft.Scope SCOPE = new MorePerCraft.Scope(IDEA, "wall", "walls", List.of(BlockTags.WALLS), 32, count -> 2 * count);
	static final int VANILLA_STONECUTTER_WALLS = 57;
	static final int VANILLA_STONECUTTER_WALLS_FROM_ANOTHER_BLOCK = 25;

	// A vanilla stonecutter recipe that cuts a wall, its file in the vanilla data pack, the first item it accepts, and what
	// vanilla's recipe cuts from it. C-008's cuts twice as many.
	record Cut(Identifier id, JsonObject file, Item input, ItemStack vanillaResult) {
		int count() {
			return 2 * vanillaResult.getCount();
		}
	}

	@GameTest
	public void vanillaWallsAreAllFound(GameTestHelper helper) {
		List<String> failures = scopeFailures(helper, vanillaStonecutterWalls(helper));
		if (!failures.isEmpty()) {
			helper.fail(String.join("\n", failures));
		}
		MorePerCraft.allFound(helper, SCOPE);
	}

	@GameTest
	public void wallRecipesGiveTwelve(GameTestHelper helper) {
		MorePerCraft.gridsGiveMore(helper, SCOPE);
	}

	@GameTest
	public void wallRecipesAreVanillasWithTwelve(GameTestHelper helper) {
		MorePerCraft.recipesAreVanillasWithMore(helper, SCOPE);
	}

	@GameTest
	public void recipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		List<Identifier> ids = new ArrayList<>(MorePerCraft.vanillaRecipes(helper, SCOPE).stream().map(MorePerCraft.Replaced::id).toList());
		vanillaStonecutterWalls(helper).forEach(cut -> ids.add(cut.id()));
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, IDEA, ids);
	}

	// No vanilla wall burns, and none does now.
	@GameTest
	public void wallsStillDoNotBurn(GameTestHelper helper) {
		MorePerCraft.thatBurnedStillBurn(helper, SCOPE, false);
	}

	// The recipe loaded at each vanilla id is vanilla's with twice the count: the same input and result. Both sides are
	// compared as the recipe codec writes them.
	@GameTest
	public void stonecutterRecipesAreVanillasWithTwo(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		List<Cut> cuts = vanillaStonecutterWalls(helper);
		List<String> failures = new ArrayList<>();
		for (Cut cut : cuts) {
			JsonObject doubled = cut.file().deepCopy();
			doubled.getAsJsonObject("result").addProperty("count", cut.count());
			JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, doubled).getOrThrow()).getOrThrow();
			Optional<RecipeHolder<?>> loaded = helper.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, cut.id()));
			if (loaded.isEmpty()) {
				failures.add("Recipe " + cut.id() + " is not loaded");
				continue;
			}
			JsonElement actual = Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow();
			if (!actual.equals(expected)) {
				failures.add("Recipe " + cut.id() + " is " + actual + ", expected " + expected);
			}
		}
		report(helper, IDEA, "stonecutter wall recipes", cuts.size(), failures);
	}

	// What the stonecutter offers for each block, as it lists its options for a player: exactly one option makes the wall,
	// and it cuts 2.
	@GameTest
	public void stonecutterCutsTwoWallsPerBlock(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		List<Cut> cuts = vanillaStonecutterWalls(helper);
		for (Cut cut : cuts) {
			ItemStack block = new ItemStack(cut.input());
			List<ItemStack> walls = new ArrayList<>();
			for (SelectableRecipe.SingleInputEntry<StonecutterRecipe> option : helper.getLevel().getServer().getRecipeManager().stonecutterRecipes().selectByInput(block).entries()) {
				option.recipe().recipe().map(holder -> holder.value().assemble(new SingleRecipeInput(block)))
						.filter(result -> result.is(cut.vanillaResult().getItem()))
						.ifPresent(walls::add);
			}
			if (walls.size() != 1 || walls.getFirst().getCount() != cut.count()) {
				failures.add("The stonecutter offers " + walls.stream().map(RecipeTestSupport::name).toList() + " for " + name(cut.input()) + ", expected one option of "
						+ cut.count() + " " + name(cut.vanillaResult().getItem()));
			}
		}
		report(helper, IDEA, "stonecutter wall options", cuts.size(), failures);
	}

	// Every vanilla stonecutter recipe that cuts a wall, sorted by id.
	static List<Cut> vanillaStonecutterWalls(GameTestHelper helper) {
		Map<Identifier, JsonObject> files = packFiles(helper, "vanilla", Registries.RECIPE);
		List<Cut> found = new ArrayList<>();
		RecipeTestSupport.vanillaRecipes(helper).forEach((id, recipe) -> {
			if (recipe instanceof StonecutterRecipe stonecutter && isWall(cut(stonecutter))) {
				found.add(new Cut(id, files.get(id), firstInput(stonecutter), cut(stonecutter)));
			}
		});
		return found;
	}

	// C-008's stonecutter scope from docs/DESIGN.md: how many recipes cut a wall, how many of them from a block other than
	// the one vanilla crafts that wall from, and that each cut 1. A Minecraft update that shifts it fails here, so the scope
	// gets rechecked.
	static List<String> scopeFailures(GameTestHelper helper, List<Cut> cuts) {
		Map<Item, Item> craftedFrom = new HashMap<>();
		MorePerCraft.vanillaRecipes(helper, SCOPE).forEach(wall -> craftedFrom.put(wall.item(), wall.cells().getFirst().getItem()));
		long fromAnotherBlock = cuts.stream().filter(cut -> craftedFrom.get(cut.vanillaResult().getItem()) != cut.input()).count();
		List<String> failures = new ArrayList<>();
		if (cuts.size() != VANILLA_STONECUTTER_WALLS || fromAnotherBlock != VANILLA_STONECUTTER_WALLS_FROM_ANOTHER_BLOCK) {
			failures.add("Expected " + VANILLA_STONECUTTER_WALLS + " stonecutter recipes that cut a wall, " + VANILLA_STONECUTTER_WALLS_FROM_ANOTHER_BLOCK
					+ " of them from another block, found " + cuts.size() + " and " + fromAnotherBlock + " " + cuts.stream().map(Cut::id).toList());
		}
		for (Cut cut : cuts) {
			if (cut.vanillaResult().getCount() != 1) {
				failures.add(cut.id() + " cuts " + name(cut.vanillaResult()) + " in vanilla, expected 1");
			}
		}
		Caramel.LOGGER.info("{} game test: {} stonecutter recipes cut a wall, {} of them from another block", IDEA, cuts.size(), fromAnotherBlock);
		return failures;
	}

	private static boolean isWall(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTags.WALLS);
	}
}
