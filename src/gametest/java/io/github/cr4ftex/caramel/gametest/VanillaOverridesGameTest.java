package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.compare;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.cut;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaUnlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.PressurePlatesAndCarpetsGameTest.Scope;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

// With the provider's identity recipe ids, a recipe saved without an explicit key lands on its result item's vanilla
// id and silently replaces that recipe. In the minecraft namespace, Caramel's pack may only hold the recipes the ideas
// replace or remove and their unlock advancements, each worked out from the vanilla data pack: C-001's stairs recipes
// and the stonecutter recipes that make stairs, C-002's slab recipes and the recipes 2 stacked slabs take over, and
// C-003's pressure plate and carpet recipes. Of vanilla's villager trades, it may only hold the shepherds' carpet trades.
public class VanillaOverridesGameTest {
	private static final String IDEAS = "C-001, C-002 and C-003";

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

		// C-002: every slab recipe is replaced, and the recipes 2 stacked slabs would also match are removed.
		List<Variant> slabs = SlabsRecipeGameTest.vanillaSlabs(helper);
		slabs.forEach(slab -> recipes.add(BuiltInRegistries.ITEM.getKey(slab.item())));
		Map<Identifier, ItemStack> removed = SlabsRecipeGameTest.removedRecipes(helper);
		recipes.addAll(removed.keySet());
		// C-002's scope from docs/DESIGN.md. A Minecraft update that shifts it fails here, so the scope gets rechecked.
		if (slabs.size() != SlabsRecipeGameTest.VANILLA_SLABS || removed.size() != SlabsRecipeGameTest.REMOVED_STACKED_SLAB_RECIPES) {
			failures.add("Expected " + SlabsRecipeGameTest.VANILLA_SLABS + " slab recipes to replace and " + SlabsRecipeGameTest.REMOVED_STACKED_SLAB_RECIPES
					+ " stacked slab recipes to remove, found " + slabs.size() + " and " + removed.size());
		}
		Caramel.LOGGER.info("C-002 game test: {} slab recipes replaced, {} recipes that 2 stacked slabs matched removed", slabs.size(), removed.size());

		// C-003: every pressure plate recipe, the other recipes that 2 of a slab's block side by side matched (the wool
		// carpets, whose 2 wool side by side now make slabs) and the two moss carpet recipes are replaced.
		Scope scope = PressurePlatesAndCarpetsGameTest.vanillaScope(helper);
		scope.all().forEach(replaced -> recipes.add(replaced.id()));
		failures.addAll(PressurePlatesAndCarpetsGameTest.scopeFailures(scope));
		Caramel.LOGGER.info("C-003 game test: {} recipes replaced: {} pressure plates, {} wool carpets, {} moss carpets",
				scope.all().size(), scope.pressurePlates().size(), scope.woolCarpets().size(), scope.mossCarpets().size());

		// Each recipe's unlock advancement is the one vanilla file at recipes/<folder>/<recipe path>.
		Map<Identifier, List<Identifier>> unlocks = vanillaUnlocks(helper);
		Set<Identifier> advancements = new TreeSet<>();
		for (Identifier id : recipes) {
			List<Identifier> found = unlocks.getOrDefault(id, List.of());
			if (found.size() == 1) {
				advancements.add(found.getFirst());
			} else {
				failures.add("The vanilla data pack has " + found.size() + " unlock advancements for recipe " + id + " " + found + ", expected 1");
			}
		}

		// C-003 also replaces the shepherds' carpet trades, and no other trade.
		Set<Identifier> trades = PressurePlatesAndCarpetsGameTest.vanillaCarpetTrades(helper).keySet();

		compare(failures, IDEAS, "recipe", recipes, packFiles(helper, Caramel.MOD_ID, Registries.RECIPE).keySet());
		compare(failures, IDEAS, "advancement", advancements, packFiles(helper, Caramel.MOD_ID, Registries.ADVANCEMENT).keySet());
		compare(failures, IDEAS, "villager trade", trades, packFiles(helper, Caramel.MOD_ID, Registries.VILLAGER_TRADE).keySet());
		report(helper, IDEAS, "vanilla recipe, advancement and trade overrides", recipes.size() + advancements.size() + trades.size(), failures);
	}
}
