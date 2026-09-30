package io.github.cr4ftex.caramel.gametest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import io.github.cr4ftex.caramel.Caramel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;

// What the recipe game tests share: the vanilla data pack files and item components they read their expected values
// from, the crafting grids they fill, the furnaces they ask, and how they report.
final class RecipeTestSupport {
	private static final int LISTED_FAILURES = 20;

	private RecipeTestSupport() {
	}

	// A stairs or slab with the blocks its vanilla recipe accepts and the base block it recycles into. The group and
	// category are the recipe book's, from the vanilla recipe; an empty group means none.
	record Variant(Item item, List<Item> accepted, Item base, String group, CraftingBookCategory category) {
	}

	record Grid(String label, CraftingInput input) {
	}

	// What a furnace does with a stack: whether its fuel slot takes it, how many ticks it burns for, and how much faster
	// the furnace cooks while it burns.
	record Furnace(Block block, Predicate<ItemStack> fuelSlot, ToIntBiFunction<ServerLevel, ItemStack> burnDuration,
			BiFunction<ServerLevel, ItemStack, Float> speedMultiplier) {
		boolean burns(ServerLevel level, ItemStack stack) {
			return fuelSlot.test(stack) && burnDuration.applyAsInt(level, stack) > 0;
		}

		String name() {
			return BuiltInRegistries.BLOCK.getKey(block).toString();
		}
	}

	// Every crafting recipe that matches a grid, and what the first one makes.
	record Crafted(List<RecipeHolder<?>> recipes, ItemStack result) {
		boolean gives(Item item, int count) {
			return recipes.size() == 1 && result.is(item) && result.getCount() == count;
		}

		@Override
		public String toString() {
			List<Identifier> ids = recipes.stream().map(holder -> holder.id().identifier()).toList();
			if (ids.size() > 1) {
				return ids.size() + " matching recipes " + ids;
			}
			return ids.isEmpty() ? "nothing" : name(result) + " from " + ids.getFirst();
		}
	}

	// Each item in the tag that has a vanilla recipe at its own id, sorted by id, with the blocks that recipe accepts.
	// Recycling gives the one block it accepts, or, for a recipe that accepts more, the family's base block from
	// baseOfAlternatives. When every item needs a recipe, one without fails the test; otherwise it is left out.
	static List<Variant> vanillaVariants(GameTestHelper helper, TagKey<Item> tag, Map<Item, Item> baseOfAlternatives, boolean everyItemHasARecipe) {
		MinecraftServer server = helper.getLevel().getServer();
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
		List<Variant> variants = new ArrayList<>();
		try (Stream<PackResources> packs = server.getPackRepository().getPack("vanilla").open()) {
			try (PackResources vanilla = packs.findFirst().orElseThrow()) {
				for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
					Item item = holder.value();
					Identifier recipeFile = BuiltInRegistries.ITEM.getKey(item).withPath(path -> "recipe/" + path + ".json");
					IoSupplier<InputStream> resource = vanilla.getResource(PackType.SERVER_DATA, recipeFile);
					if (resource == null) {
						if (everyItemHasARecipe) {
							throw helper.assertionException(Component.literal("The vanilla data pack has no " + recipeFile));
						}
						continue;
					}
					JsonObject json = read(helper, resource);
					if (!(Recipe.DIRECT_CODEC.parse(ops, json).getOrThrow() instanceof CraftingRecipe recipe)) {
						throw helper.assertionException(Component.literal(recipeFile + " in the vanilla data pack is not a crafting recipe"));
					}
					Ingredient ingredient = Ingredient.CODEC.parse(ops, json.getAsJsonObject("key").get("#")).getOrThrow();
					List<Item> accepted = BuiltInRegistries.ITEM.listElements().filter(ingredient::acceptsItem).map(Holder::value).toList();
					Item base = accepted.size() == 1 ? accepted.getFirst() : baseOfAlternatives.get(item);
					if (base == null || !accepted.contains(base)) {
						throw helper.assertionException(Component.literal(name(item) + " accepts " + accepted + " and has no known base block"));
					}
					variants.add(new Variant(item, accepted, base, recipe.group(), recipe.category()));
				}
			}
		}
		variants.sort(Comparator.comparing(v -> BuiltInRegistries.ITEM.getKey(v.item())));
		return variants;
	}

	// Every recipe in the vanilla data pack, by id, decoded as the game decodes it.
	static Map<Identifier, Recipe<?>> vanillaRecipes(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		Map<Identifier, Recipe<?>> recipes = new TreeMap<>();
		packFiles(helper, "vanilla", Registries.RECIPE).forEach((id, json) -> recipes.put(id, Recipe.DIRECT_CODEC.parse(ops, json).getOrThrow()));
		return recipes;
	}

	// Every file one data pack holds for a registry in the minecraft namespace, by entry id, read from that pack
	// alone rather than from the merged data.
	static Map<Identifier, JsonObject> packFiles(GameTestHelper helper, String packId, ResourceKey<? extends Registry<?>> registry) {
		return packFiles(helper, packId, Identifier.DEFAULT_NAMESPACE, registry);
	}

	static Map<Identifier, JsonObject> packFiles(GameTestHelper helper, String packId, String namespace, ResourceKey<? extends Registry<?>> registry) {
		Pack pack = helper.getLevel().getServer().getPackRepository().getPack(packId);
		if (pack == null) {
			throw helper.assertionException(Component.literal("There is no data pack " + packId));
		}
		FileToIdConverter converter = FileToIdConverter.registry(registry);
		Map<Identifier, JsonObject> files = new TreeMap<>();
		try (Stream<PackResources> parts = pack.open()) {
			for (PackResources part : parts.toList()) {
				try (part) {
					part.listResources(PackType.SERVER_DATA, namespace, converter.prefix(), (file, resource) -> {
						if (converter.extensionMatches(file)) {
							files.put(converter.fileToId(file), read(helper, resource));
						}
					});
				}
			}
		}
		return files;
	}

	// Every item's default components as the game builds them before applying them. Fabric's modify event, where
	// Caramel changes stairs fuel, only runs when they are applied, so these are vanilla's.
	static Map<Item, DataComponentMap> vanillaComponents(GameTestHelper helper) {
		Map<Item, DataComponentMap> components = new HashMap<>();
		HolderLookup.Provider registries = helper.getLevel().getServer().reloadableRegistries().lookup();
		for (DataComponentInitializers.PendingComponents<?> pending : BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)) {
			if (pending.key().equals(Registries.ITEM)) {
				pending.forEach((holder, map) -> {
					if (holder.value() instanceof Item item) {
						components.put(item, map);
					}
				});
			}
		}
		return components;
	}

	// A furnace, a blast furnace and a smoker, the plain furnace first. Each is asked about a stack as it asks itself:
	// canPlaceItem for its fuel slot, then getBurnDuration and getSpeedMultiplier when it lights, whose loot context
	// holds the furnace's block (minecraft:block/fast_cooking matches the blast furnace and the smoker). Those two and
	// SLOT_FUEL are protected, so each furnace is subclassed to reach them.
	static List<Furnace> furnaces() {
		return List.of(
				new FurnaceBlockEntity(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState()) {
					Furnace asked() {
						return new Furnace(getBlockState().getBlock(), stack -> canPlaceItem(SLOT_FUEL, stack), this::getBurnDuration, this::getSpeedMultiplier);
					}
				}.asked(),
				new BlastFurnaceBlockEntity(BlockPos.ZERO, Blocks.BLAST_FURNACE.defaultBlockState()) {
					Furnace asked() {
						return new Furnace(getBlockState().getBlock(), stack -> canPlaceItem(SLOT_FUEL, stack), this::getBurnDuration, this::getSpeedMultiplier);
					}
				}.asked(),
				new SmokerBlockEntity(BlockPos.ZERO, Blocks.SMOKER.defaultBlockState()) {
					Furnace asked() {
						return new Furnace(getBlockState().getBlock(), stack -> canPlaceItem(SLOT_FUEL, stack), this::getBurnDuration, this::getSpeedMultiplier);
					}
				}.asked());
	}

	private static JsonObject read(GameTestHelper helper, IoSupplier<InputStream> resource) {
		try (Reader reader = new InputStreamReader(resource.get(), StandardCharsets.UTF_8)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException exception) {
			throw helper.assertionException(Component.literal("Could not read a data pack file: " + exception));
		}
	}

	static void compare(List<String> failures, String ideas, String kind, Set<Identifier> expected, Set<Identifier> held) {
		held.stream().filter(id -> !expected.contains(id)).forEach(id -> failures.add("Caramel's data pack has " + kind + " " + id + ", which " + ideas + " do not touch"));
		expected.stream().filter(id -> !held.contains(id)).forEach(id -> failures.add("Caramel's data pack has no " + kind + " " + id));
	}

	// A shape of width by height cells, listed row by row, at each position it can take in the 2x2 grid, then in the
	// 3x3 grid. A shape that fills a grid has one position there.
	static List<Grid> placements(int width, int height, List<ItemStack> shape) {
		List<Grid> placements = new ArrayList<>();
		for (int size = 2; size <= 3; size++) {
			for (int dy = 0; dy + height <= size; dy++) {
				for (int dx = 0; dx + width <= size; dx++) {
					List<ItemStack> grid = new ArrayList<>(Collections.nCopies(size * size, ItemStack.EMPTY));
					for (int cell = 0; cell < shape.size(); cell++) {
						grid.set((cell / width + dy) * size + cell % width + dx, shape.get(cell));
					}
					String where = width == size && height == size ? "in a " : "at column " + dx + ", row " + dy + " of a ";
					placements.add(new Grid(where + size + "x" + size + " grid", CraftingInput.of(size, size, grid)));
				}
			}
		}
		return placements;
	}

	// SingleItemRecipe.result() is protected, so the tests cut this item to see what a recipe makes.
	static Item firstInput(StonecutterRecipe recipe) {
		return BuiltInRegistries.ITEM.listElements().filter(recipe.input()::acceptsItem).findFirst().orElseThrow().value();
	}

	// What a stonecutter recipe makes from its first input.
	static ItemStack cut(StonecutterRecipe recipe) {
		return recipe.assemble(new SingleRecipeInput(new ItemStack(firstInput(recipe))));
	}

	static List<RecipeHolder<?>> craftingRecipes(GameTestHelper helper) {
		return helper.getLevel().recipeAccess().getRecipes().stream().filter(holder -> holder.value() instanceof CraftingRecipe).toList();
	}

	// A crafting table uses the first match, so more than one matching recipe is a failure too.
	static Crafted craft(GameTestHelper helper, List<RecipeHolder<?>> crafting, CraftingInput input) {
		ServerLevel level = helper.getLevel();
		List<RecipeHolder<?>> matched = new ArrayList<>();
		ItemStack result = ItemStack.EMPTY;
		for (RecipeHolder<?> holder : crafting) {
			if (holder.value() instanceof CraftingRecipe recipe && recipe.matches(input, level)) {
				if (matched.isEmpty()) {
					result = recipe.assemble(input);
				}
				matched.add(holder);
			}
		}
		return new Crafted(matched, result);
	}

	// Fails with the first failures listed, or logs how many checks of the idea passed.
	static void report(GameTestHelper helper, String idea, String what, int checked, List<String> failures) {
		if (!failures.isEmpty()) {
			helper.fail(failures.size() + " of " + checked + " " + what + " failed:\n" + String.join("\n", failures.subList(0, Math.min(failures.size(), LISTED_FAILURES))));
		}
		Caramel.LOGGER.info("{} game test: {} {} passed", idea, checked, what);
		helper.succeed();
	}

	static String name(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	static String name(ItemStack stack) {
		return stack.getCount() + " " + name(stack.getItem());
	}
}
