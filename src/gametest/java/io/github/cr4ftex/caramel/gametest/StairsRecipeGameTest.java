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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CookingFuel;
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
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;

// C-001. What each stairs should cost and give back is read from the vanilla data pack's own recipe files,
// never from Caramel's, so a wrong generated recipe cannot also make its own expectation. Whether a stairs burned
// comes from the item components vanilla builds, before Caramel changes them.
public class StairsRecipeGameTest {
	// 26.3 has no ItemTags constant for it.
	private static final TagKey<Item> STAIRS = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs"));
	private static final int VANILLA_STAIRS = 97;
	// Vanilla crafts these from more than one block; recycling gives the family's base block.
	private static final Map<Item, Item> BASE_OF_ALTERNATIVES = Map.of(
			Items.QUARTZ_STAIRS, Items.QUARTZ_BLOCK,
			Items.PURPUR_STAIRS, Items.PURPUR_BLOCK,
			Items.SANDSTONE_STAIRS, Items.SANDSTONE,
			Items.RED_SANDSTONE_STAIRS, Items.RED_SANDSTONE);
	private static final int LISTED_FAILURES = 20;

	// The group and category are the recipe book's, from the vanilla recipe; an empty group means none.
	private record Stairs(Item item, List<Item> accepted, Item base, String group, CraftingBookCategory category) {
	}

	private record Grid(String label, CraftingInput input) {
	}

	// What a furnace does with a stack: whether its fuel slot takes it, how many ticks it burns for, and how much faster
	// the furnace cooks while it burns.
	private record Furnace(Block block, Predicate<ItemStack> fuelSlot, ToIntBiFunction<ServerLevel, ItemStack> burnDuration,
			BiFunction<ServerLevel, ItemStack, Float> speedMultiplier) {
		boolean burns(ServerLevel level, ItemStack stack) {
			return fuelSlot.test(stack) && burnDuration.applyAsInt(level, stack) > 0;
		}

		String name() {
			return BuiltInRegistries.BLOCK.getKey(block).toString();
		}
	}

	// Every crafting recipe that matches a grid, and what the first one makes.
	private record Crafted(List<RecipeHolder<?>> recipes, ItemStack result) {
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

	@GameTest
	public void vanillaStairsAreAllFound(GameTestHelper helper) {
		List<Stairs> stairs = vanillaStairs(helper);
		if (stairs.size() != VANILLA_STAIRS) {
			helper.fail("Expected " + VANILLA_STAIRS + " stairs with a vanilla recipe in #minecraft:stairs, found " + stairs.size());
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs, {} accepted blocks", stairs.size(), stairs.stream().mapToInt(s -> s.accepted().size()).sum());
		helper.succeed();
	}

	@GameTest
	public void threeBlocksInAnLMakeFourStairs(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			for (Item block : stairs.accepted()) {
				for (int empty = 0; empty < 4; empty++) {
					for (Grid grid : placements(lShape(block, empty))) {
						checked++;
						Crafted crafted = craft(helper, crafting, grid.input());
						if (!crafted.gives(stairs.item(), 4)) {
							failures.add(name(block) + " with empty cell " + empty + " " + grid.label() + " gave " + crafted + ", expected 4 " + name(stairs.item()));
						}
					}
				}
			}
		}
		report(helper, "L grids", checked, failures);
	}

	@GameTest
	public void bothLRecipesKeepTheVanillaGroupAndCategory(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			// Stairs with no vanilla group get their own id as the group, so both L recipes share one recipe book button.
			String group = stairs.group().isEmpty() ? BuiltInRegistries.ITEM.getKey(stairs.item()).getPath() : stairs.group();
			Map<Identifier, CraftingRecipe> recipes = new TreeMap<>();
			for (int empty = 0; empty < 4; empty++) {
				Crafted crafted = craft(helper, crafting, CraftingInput.of(2, 2, lShape(stairs.accepted().getFirst(), empty)));
				if (crafted.gives(stairs.item(), 4)) {
					RecipeHolder<?> holder = crafted.recipes().getFirst();
					recipes.put(holder.id().identifier(), (CraftingRecipe) holder.value());
				}
			}
			for (Map.Entry<Identifier, CraftingRecipe> entry : recipes.entrySet()) {
				checked++;
				CraftingRecipe recipe = entry.getValue();
				if (!recipe.group().equals(group) || recipe.category() != stairs.category()) {
					failures.add(entry.getKey() + " has group \"" + recipe.group() + "\" and category " + recipe.category().getSerializedName()
							+ ", expected \"" + group + "\" and " + stairs.category().getSerializedName());
				}
			}
		}
		report(helper, "L recipes", checked, failures);
	}

	@GameTest
	public void oldSixBlockStaircaseGivesNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			for (Item block : stairs.accepted()) {
				ItemStack b = new ItemStack(block);
				ItemStack e = ItemStack.EMPTY;
				for (Grid grid : List.of(
						new Grid("in a staircase", CraftingInput.of(3, 3, List.of(b, e, e, b, b, e, b, b, b))),
						new Grid("in a mirrored staircase", CraftingInput.of(3, 3, List.of(e, e, b, e, b, b, b, b, b))))) {
					checked++;
					Crafted crafted = craft(helper, crafting, grid.input());
					if (!crafted.recipes().isEmpty()) {
						failures.add("6 " + name(block) + " " + grid.label() + " gave " + crafted);
					}
				}
			}
		}
		report(helper, "staircase grids", checked, failures);
	}

	@GameTest
	public void fourStairsMakeThreeBaseBlocks(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			ItemStack s = new ItemStack(stairs.item());
			ItemStack e = ItemStack.EMPTY;
			for (Grid grid : List.of(
					new Grid("in a 2x2 grid", CraftingInput.of(2, 2, List.of(s, s, s, s))),
					new Grid("in the bottom right of a 3x3 grid", CraftingInput.of(3, 3, List.of(e, e, e, e, s, s, e, s, s))))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(stairs.base(), 3)) {
					failures.add("4 " + name(stairs.item()) + " " + grid.label() + " gave " + crafted + ", expected 3 " + name(stairs.base()));
				}
			}
		}
		report(helper, "recycling grids", checked, failures);
	}

	@GameTest
	public void mixedStairsGiveNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Stairs> stairs = vanillaStairs(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (int i = 0; i < stairs.size(); i++) {
			ItemStack a = new ItemStack(stairs.get(i).item());
			ItemStack b = new ItemStack(stairs.get((i + 1) % stairs.size()).item());
			for (Grid grid : List.of(
					new Grid("3 " + name(a.getItem()) + " and 1 " + name(b.getItem()), CraftingInput.of(2, 2, List.of(a, a, a, b))),
					new Grid("2 " + name(a.getItem()) + " and 2 " + name(b.getItem()) + " in diagonal pairs", CraftingInput.of(2, 2, List.of(a, b, b, a))))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.recipes().isEmpty()) {
					failures.add(grid.label() + " gave " + crafted);
				}
			}
		}
		report(helper, "mixed grids", checked, failures);
	}

	@GameTest
	public void noStonecuttingRecipeMakesStairs(GameTestHelper helper) {
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (RecipeHolder<?> holder : helper.getLevel().recipeAccess().getRecipes()) {
			if (holder.value() instanceof StonecutterRecipe recipe) {
				checked++;
				Item input = firstInput(recipe);
				ItemStack result = recipe.assemble(new SingleRecipeInput(new ItemStack(input)));
				if (result.is(STAIRS)) {
					failures.add(holder.id().identifier() + " cuts " + name(input) + " into " + name(result));
				}
			}
		}
		if (checked == 0) {
			helper.fail("No stonecutter recipes loaded at all");
		}
		report(helper, "stonecutter recipes", checked, failures);
	}

	// With the provider's identity recipe ids, a recipe saved without an explicit key lands on its result item's
	// vanilla id and silently replaces that recipe. In the minecraft namespace, Caramel's pack may only hold the
	// recipes C-001 replaces or removes and their unlock advancements.
	@GameTest
	public void onlyStairsRecipesAreReplacedOrRemoved(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		Set<Identifier> recipes = new TreeSet<>();
		vanillaStairs(helper).forEach(stairs -> recipes.add(BuiltInRegistries.ITEM.getKey(stairs.item())));
		packFiles(helper, "vanilla", Registries.RECIPE).forEach((id, json) -> {
			if (Recipe.DIRECT_CODEC.parse(ops, json).getOrThrow() instanceof StonecutterRecipe recipe
					&& recipe.assemble(new SingleRecipeInput(new ItemStack(firstInput(recipe)))).is(STAIRS)) {
				recipes.add(id);
			}
		});
		Set<Identifier> advancements = new TreeSet<>();
		recipes.forEach(id -> advancements.add(id.withPrefix("recipes/building_blocks/")));
		List<String> failures = new ArrayList<>();
		compare(failures, "recipe", recipes, packFiles(helper, Caramel.MOD_ID, Registries.RECIPE).keySet());
		compare(failures, "advancement", advancements, packFiles(helper, Caramel.MOD_ID, Registries.ADVANCEMENT).keySet());
		report(helper, "vanilla recipe and advancement overrides", recipes.size() + advancements.size(), failures);
	}

	// 3 blocks craft into 4 stairs, so 4 stairs burn exactly as long as 3 of their base block in a furnace, and never
	// longer in any furnace. Stairs of a block that does not burn cannot burn either.
	@GameTest
	public void craftingStairsNeverAddsFuel(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int ofBlocksThatBurn = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			ItemStack item = new ItemStack(stairs.item());
			ItemStack base = new ItemStack(stairs.base());
			if (furnaces.getFirst().burns(level, base)) {
				ofBlocksThatBurn++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int stairsTime = furnace.burnDuration().applyAsInt(level, item);
				int baseTime = furnace.burnDuration().applyAsInt(level, base);
				boolean exact = furnace == furnaces.getFirst() && baseTime > 0;
				if (exact ? 4 * stairsTime != 3 * baseTime : 4 * stairsTime > 3 * baseTime) {
					failures.add("4 " + name(stairs.item()) + " burn " + 4 * stairsTime + " ticks in " + furnace.name() + ", 3 " + name(stairs.base())
							+ " burn " + 3 * baseTime + ", expected " + (exact ? "the same" : "no more"));
				}
			}
		}
		if (ofBlocksThatBurn == 0) {
			helper.fail("No stairs in #minecraft:stairs has a base block that burns in a furnace");
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs have a base block that burns", ofBlocksThatBurn);
		report(helper, "stairs burn times", checked, failures);
	}

	// Every stairs that burned in vanilla still burns in every furnace, at vanilla's cooking speed, and every stairs that
	// did not still does not.
	@GameTest
	public void stairsThatBurnedStillBurn(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		int changed = 0;
		for (Stairs stairs : vanillaStairs(helper)) {
			ItemStack now = new ItemStack(stairs.item());
			ItemStack before = now.copy();
			CookingFuel vanillaFuel = vanilla.get(stairs.item()).get(DataComponents.COOKING_FUEL);
			if (vanillaFuel == null) {
				before.remove(DataComponents.COOKING_FUEL);
			} else {
				before.set(DataComponents.COOKING_FUEL, vanillaFuel);
			}
			if (!Objects.equals(vanillaFuel, now.get(DataComponents.COOKING_FUEL))) {
				changed++;
			}
			if (furnaces.getFirst().burns(level, before)) {
				burned++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				boolean burnedBefore = furnace.burns(level, before);
				if (furnace.burns(level, now) != burnedBefore) {
					failures.add(name(stairs.item()) + (burnedBefore ? " no longer burns" : " burns") + " in " + furnace.name() + (burnedBefore ? "" : ", but did not in vanilla"));
				}
				float speedBefore = furnace.speedMultiplier().apply(level, before);
				float speedNow = furnace.speedMultiplier().apply(level, now);
				if (burnedBefore && speedNow != speedBefore) {
					failures.add(name(stairs.item()) + " makes " + furnace.name() + " cook " + speedNow + " times as fast, " + speedBefore + " in vanilla");
				}
			}
		}
		// The wooden stairs that never burned: the furnace must not even take them.
		for (Item item : List.of(Items.CRIMSON_STAIRS, Items.WARPED_STAIRS)) {
			ItemStack stack = new ItemStack(item);
			for (Furnace furnace : furnaces) {
				checked++;
				if (furnace.fuelSlot().test(stack) || furnace.burnDuration().applyAsInt(level, stack) != 0) {
					failures.add(furnace.name() + " takes " + name(item) + " as fuel");
				}
			}
		}
		// Proves the vanilla components really are from before Caramel changed them.
		if (changed == 0) {
			helper.fail("No stairs has a different fuel from vanilla's, so the vanilla item components are not vanilla's");
		}
		Caramel.LOGGER.info("C-001 game test: {} stairs burned in vanilla, {} have a different fuel now", burned, changed);
		report(helper, "stairs fuel checks", checked, failures);
	}

	// Of every item, only the vanilla stairs that burned have a new fuel: not the blocks stairs are made from, which the
	// burn time checks above measure stairs against, and not other mods' stairs, whose recipes C-001 leaves alone.
	// Caramel's data pack replaces none of vanilla's burn times, cooking speeds or the predicates they use either.
	@GameTest
	public void onlyVanillaStairsThatBurnedHaveNewFuel(GameTestHelper helper) {
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		Set<Item> expected = new HashSet<>();
		for (Stairs stairs : vanillaStairs(helper)) {
			if (vanilla.containsKey(stairs.item()) && vanilla.get(stairs.item()).has(DataComponents.COOKING_FUEL)) {
				expected.add(stairs.item());
			}
		}
		// Without other mods' stairs that burn, nothing here would tell vanilla's stairs from any others.
		Item otherModStairs = BuiltInRegistries.ITEM.getValue(OtherModStairs.ITEM);
		if (!(otherModStairs instanceof BlockItem blockItem && blockItem.getBlock() instanceof StairBlock)
				|| !vanilla.containsKey(otherModStairs) || !vanilla.get(otherModStairs).has(DataComponents.COOKING_FUEL)) {
			helper.fail("The game test mod's stand-in " + OtherModStairs.ITEM.identifier() + " is missing, or is not stairs that burn");
		}
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Item item : BuiltInRegistries.ITEM) {
			checked++;
			if (!vanilla.containsKey(item)) {
				failures.add(name(item) + " has no components as vanilla builds them");
				continue;
			}
			CookingFuel vanillaFuel = vanilla.get(item).get(DataComponents.COOKING_FUEL);
			CookingFuel fuel = item.components().get(DataComponents.COOKING_FUEL);
			boolean changed = !Objects.equals(vanillaFuel, fuel);
			if (changed != expected.contains(item)) {
				failures.add(name(item) + (changed ? " has fuel " + fuel + ", vanilla's is " + vanillaFuel : " still has vanilla's fuel " + vanillaFuel));
			}
		}
		for (ResourceKey<? extends Registry<?>> registry : List.of(Registries.CONTEXT_INT_PROVIDER, Registries.CONTEXT_FLOAT_PROVIDER, Registries.PREDICATE)) {
			checked++;
			packFiles(helper, Caramel.MOD_ID, registry).keySet().forEach(id -> failures.add("Caramel's data pack replaces " + registry.identifier().getPath() + " " + id));
		}
		Caramel.LOGGER.info("C-001 game test: {} items have a new fuel", expected.size());
		report(helper, "item fuel checks", checked, failures);
	}

	// Each stairs in #minecraft:stairs, sorted by id, with the blocks its vanilla recipe accepts.
	private static List<Stairs> vanillaStairs(GameTestHelper helper) {
		MinecraftServer server = helper.getLevel().getServer();
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
		List<Stairs> stairs = new ArrayList<>();
		try (Stream<PackResources> packs = server.getPackRepository().getPack("vanilla").open()) {
			try (PackResources vanilla = packs.findFirst().orElseThrow()) {
				for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(STAIRS)) {
					Item item = holder.value();
					Identifier recipeFile = BuiltInRegistries.ITEM.getKey(item).withPath(path -> "recipe/" + path + ".json");
					IoSupplier<InputStream> resource = vanilla.getResource(PackType.SERVER_DATA, recipeFile);
					if (resource == null) {
						throw helper.assertionException(Component.literal("The vanilla data pack has no " + recipeFile));
					}
					JsonObject json = read(helper, resource);
					if (!(Recipe.DIRECT_CODEC.parse(ops, json).getOrThrow() instanceof CraftingRecipe recipe)) {
						throw helper.assertionException(Component.literal(recipeFile + " in the vanilla data pack is not a crafting recipe"));
					}
					Ingredient ingredient = Ingredient.CODEC.parse(ops, json.getAsJsonObject("key").get("#")).getOrThrow();
					List<Item> accepted = BuiltInRegistries.ITEM.listElements().filter(ingredient::acceptsItem).map(Holder::value).toList();
					Item base = accepted.size() == 1 ? accepted.getFirst() : BASE_OF_ALTERNATIVES.get(item);
					if (base == null || !accepted.contains(base)) {
						throw helper.assertionException(Component.literal(name(item) + " accepts " + accepted + " and has no known base block"));
					}
					stairs.add(new Stairs(item, accepted, base, recipe.group(), recipe.category()));
				}
			}
		}
		stairs.sort(Comparator.comparing(s -> BuiltInRegistries.ITEM.getKey(s.item())));
		return stairs;
	}

	// Every file one data pack holds for a registry in the minecraft namespace, by entry id, read from that pack
	// alone rather than from the merged data.
	private static Map<Identifier, JsonObject> packFiles(GameTestHelper helper, String packId, ResourceKey<? extends Registry<?>> registry) {
		Pack pack = helper.getLevel().getServer().getPackRepository().getPack(packId);
		if (pack == null) {
			throw helper.assertionException(Component.literal("There is no data pack " + packId));
		}
		FileToIdConverter converter = FileToIdConverter.registry(registry);
		Map<Identifier, JsonObject> files = new TreeMap<>();
		try (Stream<PackResources> parts = pack.open()) {
			for (PackResources part : parts.toList()) {
				try (part) {
					part.listResources(PackType.SERVER_DATA, Identifier.DEFAULT_NAMESPACE, converter.prefix(), (file, resource) -> {
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
	private static Map<Item, DataComponentMap> vanillaComponents(GameTestHelper helper) {
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
	private static List<Furnace> furnaces() {
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

	private static void compare(List<String> failures, String kind, Set<Identifier> expected, Set<Identifier> held) {
		held.stream().filter(id -> !expected.contains(id)).forEach(id -> failures.add("Caramel's data pack has " + kind + " " + id + ", which C-001 does not touch"));
		expected.stream().filter(id -> !held.contains(id)).forEach(id -> failures.add("Caramel's data pack has no " + kind + " " + id));
	}

	// Three of a block in a row-major 2x2 square, with the empty cell 0 top left, 1 top right, 2 bottom left or 3 bottom right.
	private static List<ItemStack> lShape(Item block, int empty) {
		return IntStream.range(0, 4).mapToObj(cell -> cell == empty ? ItemStack.EMPTY : new ItemStack(block)).toList();
	}

	// The 2x2 square on its own, then at each of the four positions it can take in a 3x3 grid.
	private static List<Grid> placements(List<ItemStack> square) {
		List<Grid> placements = new ArrayList<>();
		placements.add(new Grid("in a 2x2 grid", CraftingInput.of(2, 2, square)));
		for (int dy = 0; dy < 2; dy++) {
			for (int dx = 0; dx < 2; dx++) {
				List<ItemStack> grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
				for (int cell = 0; cell < 4; cell++) {
					grid.set((cell / 2 + dy) * 3 + cell % 2 + dx, square.get(cell));
				}
				placements.add(new Grid("at column " + dx + ", row " + dy + " of a 3x3 grid", CraftingInput.of(3, 3, grid)));
			}
		}
		return placements;
	}

	// SingleItemRecipe.result() is protected, so the tests cut this item to see what a recipe makes.
	private static Item firstInput(StonecutterRecipe recipe) {
		return BuiltInRegistries.ITEM.listElements().filter(recipe.input()::acceptsItem).findFirst().orElseThrow().value();
	}

	private static List<RecipeHolder<?>> craftingRecipes(GameTestHelper helper) {
		return helper.getLevel().recipeAccess().getRecipes().stream().filter(holder -> holder.value() instanceof CraftingRecipe).toList();
	}

	// A crafting table uses the first match, so more than one matching recipe is a failure too.
	private static Crafted craft(GameTestHelper helper, List<RecipeHolder<?>> crafting, CraftingInput input) {
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

	private static void report(GameTestHelper helper, String what, int checked, List<String> failures) {
		if (!failures.isEmpty()) {
			helper.fail(failures.size() + " of " + checked + " " + what + " failed:\n" + String.join("\n", failures.subList(0, Math.min(failures.size(), LISTED_FAILURES))));
		}
		Caramel.LOGGER.info("C-001 game test: {} {} passed", checked, what);
		helper.succeed();
	}

	private static String name(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	private static String name(ItemStack stack) {
		return stack.getCount() + " " + name(stack.getItem());
	}
}
