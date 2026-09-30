package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;

// A recipe with no unlock advancement still crafts, so nothing else notices when one goes missing: the recipe book just
// never shows it. Every recipe in the caramel namespace (C-001's second L and stairs recycling, C-002's slab recycling)
// has exactly one loaded advancement, caramel:recipes/<folder>/<recipe path>, that unlocks it and nothing else.
public class UnlockAdvancementsGameTest {
	private static final String IDEAS = "C-001 and C-002";

	@GameTest
	public void everyCaramelRecipeHasOneUnlockAdvancement(GameTestHelper helper) {
		Set<Identifier> recipes = packFiles(helper, Caramel.MOD_ID, Caramel.MOD_ID, Registries.RECIPE).keySet();
		ServerAdvancementManager loaded = helper.getLevel().getServer().getAdvancements();
		Map<Identifier, List<Identifier>> unlocks = new HashMap<>();
		List<String> failures = new ArrayList<>();
		packFiles(helper, Caramel.MOD_ID, Caramel.MOD_ID, Registries.ADVANCEMENT).forEach((id, json) -> {
			String[] path = id.getPath().split("/", 3);
			Identifier recipe = id.withPath(path[path.length - 1]);
			if (path.length != 3 || !path[0].equals("recipes") || !recipes.contains(recipe)) {
				failures.add("Advancement " + id + " is not at recipes/<folder>/<path> of a Caramel recipe");
			} else if (!unlocksOnly(json, recipe)) {
				failures.add("Advancement " + id + " does not unlock exactly " + recipe);
			} else if (loaded.get(id) == null) {
				failures.add("Advancement " + id + " is not loaded");
			}
			unlocks.computeIfAbsent(recipe, key -> new ArrayList<>()).add(id);
		});
		for (Identifier recipe : recipes) {
			List<Identifier> found = unlocks.getOrDefault(recipe, List.of());
			if (found.size() != 1) {
				failures.add("Recipe " + recipe + " has " + found.size() + " unlock advancements " + found + ", expected 1");
			}
		}
		if (recipes.isEmpty()) {
			helper.fail("Caramel's data pack has no recipes in the caramel namespace");
		}
		report(helper, IDEAS, "Caramel recipe unlock advancements", recipes.size(), failures);
	}

	// Its rewards name only this recipe, and a recipe_unlocked criterion names it too, as datagen writes them.
	private static boolean unlocksOnly(JsonObject advancement, Identifier recipe) {
		JsonObject rewards = advancement.getAsJsonObject("rewards");
		if (rewards == null || !rewards.getAsJsonArray("recipes").equals(single(recipe))) {
			return false;
		}
		for (Map.Entry<String, JsonElement> criterion : advancement.getAsJsonObject("criteria").entrySet()) {
			JsonObject value = criterion.getValue().getAsJsonObject();
			if (value.get("trigger").getAsString().equals("minecraft:recipe_unlocked")
					&& value.getAsJsonObject("conditions").get("recipes").getAsString().equals(recipe.toString())) {
				return true;
			}
		}
		return false;
	}

	private static JsonArray single(Identifier recipe) {
		JsonArray array = new JsonArray();
		array.add(recipe.toString());
		return array;
	}
}
