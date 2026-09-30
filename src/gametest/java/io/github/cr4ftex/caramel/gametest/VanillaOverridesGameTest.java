package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.compare;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.cut;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

// With the provider's identity recipe ids, a recipe saved without an explicit key lands on its result item's vanilla
// id and silently replaces that recipe. In the minecraft namespace, Caramel's pack may only hold the recipes the ideas
// replace or remove and their unlock advancements, each worked out from the vanilla data pack: C-001's stairs recipes
// and the stonecutter recipes that make stairs, and C-002's slab recipes and the recipes its new grids take over.
public class VanillaOverridesGameTest {
	private static final String IDEAS = "C-001 and C-002";

	@GameTest
	public void onlyExpectedVanillaRecipesAreReplacedOrRemoved(GameTestHelper helper) {
		Set<Identifier> recipes = new TreeSet<>();
		List<String> failures = new ArrayList<>();

		// C-001: every stairs recipe is replaced, and every stonecutter recipe that makes stairs is removed.
		StairsRecipeGameTest.vanillaStairs(helper).forEach(stairs -> recipes.add(BuiltInRegistries.ITEM.getKey(stairs.item())));
		vanillaRecipes(helper).forEach((id, recipe) -> {
			if (recipe instanceof StonecutterRecipe stonecutter && cut(stonecutter).is(StairsRecipeGameTest.STAIRS)) {
				recipes.add(id);
			}
		});

		// C-002: every slab recipe is replaced, and the recipes its new grids take over are removed, pressure plates included.
		List<Variant> slabs = SlabsRecipeGameTest.vanillaSlabs(helper);
		slabs.forEach(slab -> recipes.add(BuiltInRegistries.ITEM.getKey(slab.item())));
		SlabsRecipeGameTest.Removed removed = SlabsRecipeGameTest.removedRecipes(helper);
		recipes.addAll(removed.ids());
		for (Map.Entry<Identifier, ItemStack> carpet : removed.carpets().entrySet()) {
			if (!carpet.getValue().is(ItemTags.WOOL_CARPETS)) {
				failures.add(carpet.getKey() + " makes " + name(carpet.getValue()) + " from 2 blocks side by side, which is not a carpet");
			}
		}
		// C-002's scope from docs/DESIGN.md. A Minecraft update that shifts it fails here, so the scope gets rechecked.
		if (slabs.size() != SlabsRecipeGameTest.VANILLA_SLABS || removed.pressurePlates().size() != SlabsRecipeGameTest.REMOVED_PRESSURE_PLATES
				|| removed.carpets().size() != SlabsRecipeGameTest.REMOVED_CARPETS || removed.stackedSlabs().size() != SlabsRecipeGameTest.REMOVED_STACKED_SLAB_RECIPES) {
			failures.add("Expected " + SlabsRecipeGameTest.VANILLA_SLABS + " slab recipes to replace and " + SlabsRecipeGameTest.REMOVED_PRESSURE_PLATES + " pressure plate, "
					+ SlabsRecipeGameTest.REMOVED_CARPETS + " carpet and " + SlabsRecipeGameTest.REMOVED_STACKED_SLAB_RECIPES + " stacked slab recipes to remove, found "
					+ slabs.size() + ", " + removed.pressurePlates().size() + ", " + removed.carpets().size() + " and " + removed.stackedSlabs().size());
		}
		Caramel.LOGGER.info("C-002 game test: {} slab recipes replaced, {} recipes removed: {} pressure plates, {} carpets, {} from 2 stacked slabs",
				slabs.size(), removed.ids().size(), removed.pressurePlates().size(), removed.carpets().size(), removed.stackedSlabs().size());

		// Each recipe's unlock advancement is the one vanilla file at recipes/<folder>/<recipe path>.
		Map<Identifier, List<Identifier>> unlocks = new HashMap<>();
		for (Identifier id : packFiles(helper, "vanilla", Registries.ADVANCEMENT).keySet()) {
			String[] path = id.getPath().split("/", 3);
			if (path.length == 3 && path[0].equals("recipes")) {
				unlocks.computeIfAbsent(id.withPath(path[2]), recipe -> new ArrayList<>()).add(id);
			}
		}
		Set<Identifier> advancements = new TreeSet<>();
		for (Identifier id : recipes) {
			List<Identifier> found = unlocks.getOrDefault(id, List.of());
			if (found.size() == 1) {
				advancements.add(found.getFirst());
			} else {
				failures.add("The vanilla data pack has " + found.size() + " unlock advancements for recipe " + id + " " + found + ", expected 1");
			}
		}

		compare(failures, IDEAS, "recipe", recipes, packFiles(helper, Caramel.MOD_ID, Registries.RECIPE).keySet());
		compare(failures, IDEAS, "advancement", advancements, packFiles(helper, Caramel.MOD_ID, Registries.ADVANCEMENT).keySet());
		report(helper, IDEAS, "vanilla recipe and advancement overrides", recipes.size() + advancements.size(), failures);
	}
}
