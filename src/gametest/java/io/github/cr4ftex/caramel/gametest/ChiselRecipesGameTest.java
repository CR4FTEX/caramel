package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.accepted;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craft;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.craftingRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.placements;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaUnlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.ChiselGameTest.Tier;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// C-009's recipes: each crafted Chisel is its tier's material above any planks, the Netherite Chisel is the Diamond
// Chisel upgraded on a smithing table, and no cracked, cut, chiseled or pillar block is crafted or smelted any more.
// Which materials a tier accepts, which recipes made those blocks and which stonecutter recipes still do are all read
// from the vanilla data pack.
public class ChiselRecipesGameTest {
	private static final String IDEA = "C-009";
	// C-009's scope in Minecraft 26.3, from docs/DESIGN.md: 5 cracked blocks made by smelting, 10 cut blocks crafted 4
	// from 4, and the quartz pillar. C-002 had removed the 20 chiseled blocks' recipes and the purpur pillar's.
	static final Map<String, Integer> REMOVED = Map.of("cracked", 5, "cut", 10, "pillar", 1);
	// The words that make a full block a carved one.
	private static final List<String> CARVED = List.of("cracked", "cut", "chiseled", "pillar");
	private static final Set<String> CRAFTED_OR_COOKED = Set.of("minecraft:crafting_shaped", "minecraft:crafting_shapeless", "minecraft:smelting", "minecraft:blasting",
			"minecraft:smoking", "minecraft:campfire_cooking");

	// The crafted Chisels: each head material the tier's vanilla pickaxe accepts (a flint for the flint tier) above
	// each kind of planks makes 1 Chisel, at both columns of the inventory's 2x2 grid and all 6 places of the 3x3
	// grid, with no other recipe matching. Upside down, or with a stick for the planks, it makes no Chisel.
	@GameTest
	public void chiselsAreCraftedInTheInventoryGrid(GameTestHelper helper) {
		Map<Identifier, Recipe<?>> vanilla = vanillaRecipes(helper);
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Item> planks = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.PLANKS).forEach(holder -> planks.add(holder.value()));
		Set<Item> chisels = new HashSet<>(ChiselGameTest.TIERS.stream().map(Tier::chisel).toList());
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Tier tier : ChiselGameTest.TIERS) {
			if (tier.name().equals("netherite")) {
				continue;
			}
			List<Item> heads = tier.name().equals("flint") ? List.of(Items.FLINT) : pickaxeHeads(vanilla, tier);
			if (heads.isEmpty() || planks.isEmpty()) {
				failures.add("Found " + heads.size() + " head materials for the " + name(tier.pickaxe()) + " and " + planks.size() + " planks");
			}
			for (Item head : heads) {
				for (Item plank : planks) {
					for (Grid grid : placements(1, 2, List.of(new ItemStack(head), new ItemStack(plank)))) {
						checked++;
						Crafted crafted = craft(helper, crafting, grid.input());
						if (crafted.recipes().size() != 1 || !crafted.result().is(tier.chisel()) || crafted.result().getCount() != 1
								|| !crafted.recipes().getFirst().id().identifier().equals(BuiltInRegistries.ITEM.getKey(tier.chisel()))) {
							failures.add(name(head) + " above " + name(plank) + " " + grid.label() + " makes " + crafted + ", expected 1 " + name(tier.chisel()));
						}
					}
				}
				for (List<ItemStack> wrong : List.of(List.of(new ItemStack(planks.getFirst()), new ItemStack(head)), List.of(new ItemStack(head), new ItemStack(Items.STICK)))) {
					for (Grid grid : placements(1, 2, wrong)) {
						checked++;
						Crafted crafted = craft(helper, crafting, grid.input());
						if (chisels.contains(crafted.result().getItem())) {
							failures.add(name(wrong.getFirst().getItem()) + " above " + name(wrong.getLast().getItem()) + " " + grid.label() + " makes " + crafted + ", expected no Chisel");
						}
					}
				}
			}
		}
		report(helper, IDEA, "Chisel crafting grids", checked, failures);
	}

	// The Netherite Chisel: the smithing table turns a Diamond Chisel into one with the template and addition vanilla's
	// netherite pickaxe takes, keeps its wear and enchantments, takes no other Chisel, and nothing crafts it.
	@GameTest
	public void theNetheriteChiselIsADiamondOneUpgraded(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Item diamond = chisel("diamond");
		Item netherite = chisel("netherite");
		List<String> failures = new ArrayList<>();
		int checked = 0;
		SmithingRecipe pickaxe = vanillaRecipes(helper).get(Identifier.withDefaultNamespace("netherite_pickaxe_smithing")) instanceof SmithingRecipe recipe ? recipe : null;
		if (pickaxe == null || pickaxe.templateIngredient().isEmpty() || pickaxe.additionIngredient().isEmpty() || !pickaxe.baseIngredient().acceptsItem(Items.DIAMOND_PICKAXE.builtInRegistryHolder())) {
			helper.fail("Vanilla's netherite pickaxe is not a smithing upgrade of the diamond pickaxe with a template and an addition");
			return;
		}
		Holder<Enchantment> efficiency = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
		List<SmithingRecipe> smithing = level.recipeAccess().getRecipes().stream().map(RecipeHolder::value).filter(SmithingRecipe.class::isInstance).map(SmithingRecipe.class::cast).toList();
		for (Item template : accepted(pickaxe.templateIngredient().get())) {
			for (Item addition : accepted(pickaxe.additionIngredient().get())) {
				for (Tier tier : ChiselGameTest.TIERS) {
					checked++;
					ItemStack base = new ItemStack(tier.chisel());
					base.setDamageValue(7);
					base.enchant(efficiency, 3);
					SmithingRecipeInput input = new SmithingRecipeInput(new ItemStack(template), base, new ItemStack(addition));
					List<ItemStack> results = smithing.stream().filter(recipe -> recipe.matches(input, level)).map(recipe -> recipe.assemble(input)).toList();
					if (tier.chisel() == diamond) {
						if (results.size() != 1 || !results.getFirst().is(netherite) || results.getFirst().getDamageValue() != 7
								|| !results.getFirst().getEnchantments().equals(base.getEnchantments())) {
							failures.add(name(template) + ", a worn and enchanted " + name(diamond) + " and " + name(addition) + " make " + results + ", expected 1 " + name(netherite) + " with the same wear and enchantments");
						}
					} else if (!results.isEmpty()) {
						failures.add(name(template) + ", a " + name(tier.chisel()) + " and " + name(addition) + " make " + results + " on the smithing table, expected nothing");
					}
				}
			}
		}
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
		for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
			if (!(holder.value() instanceof SmithingRecipe) && result(Recipe.DIRECT_CODEC.encodeStart(ops, holder.value()).getOrThrow()).filter(id -> id.equals(BuiltInRegistries.ITEM.getKey(netherite))).isPresent()) {
				failures.add("Recipe " + holder.id().identifier() + " makes a " + name(netherite) + " off the smithing table");
			}
		}
		report(helper, IDEA, "smithing table checks", checked, failures);
	}

	// What shows a Chisel's recipe in the recipe book, and where. Each crafted Chisel unlocks on what its tier's vanilla
	// pickaxe unlocks on and sits in that pickaxe's recipe book category; the Flint Chisel unlocks on having a flint,
	// where the wooden pickaxe unlocks on a stick; and the upgrade unlocks like the netherite pickaxe's.
	@GameTest
	public void chiselRecipesUnlockAndListLikeTheirPickaxes(GameTestHelper helper) {
		Map<Identifier, JsonObject> vanillaAdvancements = packFiles(helper, "vanilla", Registries.ADVANCEMENT);
		Map<Identifier, JsonObject> vanillaFiles = packFiles(helper, "vanilla", Registries.RECIPE);
		Map<Identifier, JsonObject> advancements = packFiles(helper, Caramel.MOD_ID, Caramel.MOD_ID, Registries.ADVANCEMENT);
		Map<Identifier, JsonObject> recipes = packFiles(helper, Caramel.MOD_ID, Caramel.MOD_ID, Registries.RECIPE);
		List<String> failures = new ArrayList<>();
		for (Tier tier : ChiselGameTest.TIERS) {
			String smithing = tier.name().equals("netherite") ? "_smithing" : "";
			Identifier pickaxe = BuiltInRegistries.ITEM.getKey(tier.pickaxe()).withSuffix(smithing);
			Identifier chisel = BuiltInRegistries.ITEM.getKey(tier.chisel()).withSuffix(smithing);
			JsonObject vanilla = vanillaAdvancements.get(pickaxe.withPrefix("recipes/tools/"));
			JsonObject actual = advancements.get(chisel.withPrefix("recipes/tools/"));
			if (vanilla == null || actual == null || !vanillaFiles.containsKey(pickaxe) || !recipes.containsKey(chisel)) {
				failures.add("Missing a recipe or an unlock advancement for " + pickaxe + " in the vanilla pack or " + chisel + " in Caramel's");
				continue;
			}
			// The same criteria, but for the recipe each names in has_the_recipe.
			String expected = unlocksOn(vanilla).toString();
			if (tier.name().equals("flint")) {
				expected = expected.replace("has_stick", "has_flint").replace(BuiltInRegistries.ITEM.getKey(Items.STICK).toString(), BuiltInRegistries.ITEM.getKey(Items.FLINT).toString());
			}
			if (!unlocksOn(actual).equals(JsonParser.parseString(expected))) {
				failures.add("Recipe " + chisel + " unlocks on " + unlocksOn(actual) + ", expected " + expected);
			}
			if (!Objects.equals(recipes.get(chisel).get("category"), vanillaFiles.get(pickaxe).get("category"))) {
				failures.add("Recipe " + chisel + " is in recipe book category " + recipes.get(chisel).get("category") + ", expected " + pickaxe + "'s " + vanillaFiles.get(pickaxe).get("category"));
			}
		}
		// The check needs vanilla's criteria to differ between tiers, or it could not tell a wrong one.
		if (unlocksOn(vanillaAdvancements.get(Identifier.withDefaultNamespace("recipes/tools/iron_pickaxe"))).equals(unlocksOn(vanillaAdvancements.get(Identifier.withDefaultNamespace("recipes/tools/golden_pickaxe"))))) {
			failures.add("Vanilla's iron and golden pickaxes unlock on the same criteria");
		}
		report(helper, IDEA, "Chisel recipe unlocks and categories", ChiselGameTest.TIERS.size(), failures);
	}

	// No recipe in the vanilla data pack that crafted or cooked a carved block is loaded, nor its unlock advancement.
	// Of everything that is loaded, a carved block is only crafted by Caramel's own recipes that turn its slabs and
	// stairs back into it. Vanilla's honeycomb waxing and the chiseled bookshelf are left as they were.
	@GameTest
	public void carvedBlocksAreNoLongerCraftedOrSmelted(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Identifier, Block> carved = vanillaCarvedRecipes(helper);
		List<String> failures = new ArrayList<>(scopeFailures(helper, carved));
		Map<Identifier, List<Identifier>> unlocks = vanillaUnlocks(helper);
		ServerAdvancementManager advancements = level.getServer().getAdvancements();
		int checked = 0;
		for (Identifier id : carved.keySet()) {
			checked++;
			if (level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
				failures.add("Recipe " + id + " is still loaded");
			}
			for (Identifier unlock : unlocks.getOrDefault(id, List.of())) {
				if (advancements.get(unlock) != null) {
					failures.add("Advancement " + unlock + " is still loaded");
				}
			}
		}
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
		int waxing = 0;
		for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
			if (!(holder.value() instanceof CraftingRecipe) && !(holder.value() instanceof AbstractCookingRecipe)) {
				continue;
			}
			checked++;
			JsonObject json = Recipe.DIRECT_CODEC.encodeStart(ops, holder.value()).getOrThrow().getAsJsonObject();
			Optional<Block> made = result(json).flatMap(BuiltInRegistries.BLOCK::getOptional).filter(ChiselRecipesGameTest::isCarved);
			if (made.isEmpty() || made.get() == Blocks.CHISELED_BOOKSHELF) {
				continue;
			}
			if (isWaxing(json)) {
				waxing++;
			} else if (!holder.id().identifier().getNamespace().equals(Caramel.MOD_ID) || !recyclesSlabsOrStairs(holder.value())) {
				failures.add("Recipe " + holder.id().identifier() + " still crafts or cooks " + BuiltInRegistries.BLOCK.getKey(made.get()));
			}
		}
		// What stays: the chiseled bookshelf's only recipe, and honeycomb on each of the 4 cut and 4 chiseled coppers.
		if (level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("chiseled_bookshelf"))).isEmpty() || waxing != 8) {
			failures.add("Expected the chiseled bookshelf's recipe and 8 honeycomb recipes that wax a cut or chiseled copper, found " + waxing + " of the latter");
		}
		report(helper, IDEA, "carved block recipe checks", checked, failures);
	}

	// Every carved block the vanilla data pack had a recipe for, and bamboo mosaic, can still be made: by a loaded
	// recipe, such as the stonecutter's, or by a Chisel's right click. And every vanilla stonecutter recipe that cuts
	// a carved block is loaded as vanilla has it.
	@GameTest
	public void everyCarvedBlockCanStillBeMade(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
		Set<Block> needed = new HashSet<>(List.of(Blocks.BAMBOO_MOSAIC));
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Map.Entry<Identifier, JsonObject> file : packFiles(helper, "vanilla", Registries.RECIPE).entrySet()) {
			Optional<Block> made = result(file.getValue()).flatMap(BuiltInRegistries.BLOCK::getOptional).filter(ChiselRecipesGameTest::isCarved);
			if (made.isEmpty()) {
				continue;
			}
			needed.add(made.get());
			if (file.getValue().get("type").getAsString().equals("minecraft:stonecutting")) {
				checked++;
				JsonElement expected = Recipe.DIRECT_CODEC.encodeStart(ops, Recipe.DIRECT_CODEC.parse(ops, file.getValue()).getOrThrow()).getOrThrow();
				Optional<RecipeHolder<?>> loaded = level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, file.getKey()));
				if (loaded.isEmpty() || !(loaded.get().value() instanceof StonecutterRecipe) || !Recipe.DIRECT_CODEC.encodeStart(ops, loaded.get().value()).getOrThrow().equals(expected)) {
					failures.add("Vanilla's stonecutter recipe " + file.getKey() + " is not loaded as vanilla has it");
				}
			}
		}
		Set<Block> made = new HashSet<>();
		level.recipeAccess().getRecipes().forEach(holder -> result(Recipe.DIRECT_CODEC.encodeStart(ops, holder.value()).getOrThrow()).flatMap(BuiltInRegistries.BLOCK::getOptional).ifPresent(made::add));
		// ChiselGameTest checks that a Chisel's right click follows exactly these cycles.
		ChiselGameTest.CYCLES.forEach(made::addAll);
		for (Block block : needed) {
			checked++;
			if (!made.contains(block)) {
				failures.add(BuiltInRegistries.BLOCK.getKey(block) + " can no longer be made by any recipe or by a Chisel");
			}
		}
		report(helper, IDEA, "carved block sources", checked, failures);
	}

	// Every recipe in the vanilla data pack that crafts or cooks a carved block, by id, with the block it makes. The
	// stonecutter's recipes are not among them, nor vanilla's honeycomb waxing, nor the chiseled bookshelf.
	static Map<Identifier, Block> vanillaCarvedRecipes(GameTestHelper helper) {
		Map<Identifier, Block> found = new TreeMap<>();
		packFiles(helper, "vanilla", Registries.RECIPE).forEach((id, json) -> {
			if (CRAFTED_OR_COOKED.contains(json.get("type").getAsString()) && !isWaxing(json)) {
				result(json).flatMap(BuiltInRegistries.BLOCK::getOptional).filter(ChiselRecipesGameTest::isCarved).filter(block -> block != Blocks.CHISELED_BOOKSHELF)
						.ifPresent(block -> found.put(id, block));
			}
		});
		return found;
	}

	// C-009's scope from docs/DESIGN.md: of the vanilla recipes for carved blocks that C-002 had not already removed,
	// 5 make a cracked block, 10 a cut block and 1 a pillar. A Minecraft update that shifts it fails here, so the
	// scope gets rechecked.
	static List<String> scopeFailures(GameTestHelper helper, Map<Identifier, Block> carved) {
		Set<Identifier> alreadyRemoved = SlabsRecipeGameTest.removedRecipes(helper).keySet();
		Map<String, Integer> found = new TreeMap<>();
		for (Map.Entry<Identifier, Block> recipe : carved.entrySet()) {
			if (!alreadyRemoved.contains(recipe.getKey())) {
				found.merge(CARVED.stream().filter(List.of(BuiltInRegistries.BLOCK.getKey(recipe.getValue()).getPath().split("_"))::contains).findFirst().orElseThrow(), 1, Integer::sum);
			}
		}
		Caramel.LOGGER.info("{} game test: {} vanilla recipes craft or cook a carved block, and C-002 had removed {} of them; the rest are {}", IDEA, carved.size(),
				carved.keySet().stream().filter(alreadyRemoved::contains).count(), found);
		return found.equals(new TreeMap<>(REMOVED)) ? List.of() : List.of("Expected C-009 to remove " + new TreeMap<>(REMOVED) + " vanilla recipes for carved blocks, found " + found);
	}

	// A full block whose name carries one of the carved words: not a slab, stairs or wall cut from one.
	static boolean isCarved(Block block) {
		List<String> words = List.of(BuiltInRegistries.BLOCK.getKey(block).getPath().split("_"));
		return CARVED.stream().anyMatch(words::contains) && !block.defaultBlockState().is(BlockTags.SLABS) && !block.defaultBlockState().is(BlockTags.STAIRS)
				&& !block.defaultBlockState().is(BlockTags.WALLS);
	}

	// An unlock advancement's criteria and requirements without has_the_recipe, which names the recipe itself.
	private static JsonObject unlocksOn(JsonObject advancement) {
		JsonObject unlocks = new JsonObject();
		JsonObject criteria = advancement.getAsJsonObject("criteria").deepCopy();
		criteria.remove("has_the_recipe");
		unlocks.add("criteria", criteria);
		unlocks.add("requirements", advancement.get("requirements"));
		return unlocks;
	}

	// The id of the item a recipe file makes, for the recipe types that name one.
	private static Optional<Identifier> result(JsonElement recipe) {
		return recipe.isJsonObject() && recipe.getAsJsonObject().get("result") instanceof JsonObject result && result.has("id")
				? Optional.of(Identifier.parse(result.get("id").getAsString())) : Optional.empty();
	}

	// Vanilla waxes a block by crafting it with honeycomb.
	private static boolean isWaxing(JsonObject recipe) {
		return recipe.get("type").getAsString().equals("minecraft:crafting_shapeless") && recipe.get("ingredients").toString().contains("\"minecraft:honeycomb\"");
	}

	// Caramel's recycling recipes take only slabs or only stairs.
	private static boolean recyclesSlabsOrStairs(Recipe<?> recipe) {
		List<Item> ingredients = recipe.placementInfo().ingredients().stream().flatMap(ingredient -> accepted(ingredient).stream()).toList();
		return !ingredients.isEmpty() && (ingredients.stream().allMatch(item -> Block.byItem(item).defaultBlockState().is(BlockTags.SLABS))
				|| ingredients.stream().allMatch(item -> Block.byItem(item).defaultBlockState().is(BlockTags.STAIRS)));
	}

	// The head material of a tier's vanilla pickaxe: the first ingredient of its recipe, 3 of it across the top row.
	private static List<Item> pickaxeHeads(Map<Identifier, Recipe<?>> vanilla, Tier tier) {
		return vanilla.get(BuiltInRegistries.ITEM.getKey(tier.pickaxe())) instanceof ShapedRecipe pickaxe ? accepted(pickaxe.getIngredients().getFirst().orElseThrow()) : List.of();
	}

	private static Item chisel(String tier) {
		return ChiselGameTest.TIERS.stream().filter(candidate -> candidate.name().equals(tier)).findFirst().orElseThrow().chisel();
	}
}
