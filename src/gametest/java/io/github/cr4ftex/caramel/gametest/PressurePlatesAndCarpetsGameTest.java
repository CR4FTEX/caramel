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
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaRecipes;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.withVanillaFuel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import org.apache.commons.lang3.math.Fraction;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Crafted;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Furnace;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Grid;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.NumberDispatcher;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.level.storage.loot.providers.number.ints.WeightedListValue;
import net.minecraft.world.phys.Vec3;

// C-003. Which vanilla recipes become pressure plates and carpets from slabs, what each was crafted from and what it
// should be crafted from now, are read from the vanilla data pack's own recipe files, never from Caramel's, so a wrong
// generated recipe cannot also make its own expectation. Whether an item burned or how it composted comes from the item
// components vanilla builds, before Caramel changes them.
public class PressurePlatesAndCarpetsGameTest {
	private static final String IDEA = "C-003";
	// C-003's scope in Minecraft 26.3, from docs/DESIGN.md.
	static final int VANILLA_PRESSURE_PLATES = 17;
	static final int VANILLA_WOOL_CARPETS = 16;
	static final int VANILLA_MOSS_CARPETS = 2;
	static final int VANILLA_CARPET_TRADES = 16;
	// The owner's price: a wool makes 16 carpets, so a shepherd sells 20 for an emerald instead of 4.
	private static final int CARPETS_PER_TRADE = 20;
	// The owner named these. Their blocks have no slabs, so no slab recipe finds them.
	private static final List<Item> MOSS_CARPETS = List.of(Items.MOSS_CARPET, Items.PALE_MOSS_CARPET);

	// A vanilla crafting recipe C-003 replaces at its own id: what it makes, the one item vanilla crafts it from, 2 side
	// by side, and its recipe book group and category. C-003 crafts it from 2 of the slab whose vanilla recipe accepts
	// that item instead, or from 2 of the item itself when no slab's does (the ingots and the moss blocks).
	record Replaced(Identifier id, Item result, Item block, Item ingredient, String group, CraftingBookCategory category) {
	}

	// C-003's vanilla recipes, each list sorted by id: every pressure plate, the other recipes that 2 of a block a slab
	// recipe accepts side by side also match (the wool carpets), and the two moss carpets.
	record Scope(List<Replaced> pressurePlates, List<Replaced> woolCarpets, List<Replaced> mossCarpets) {
		// The recipes that give 16.
		List<Replaced> sixteen() {
			return Stream.concat(pressurePlates.stream(), woolCarpets.stream()).toList();
		}

		List<Replaced> all() {
			return Stream.concat(sixteen().stream(), mossCarpets.stream()).toList();
		}
	}

	@GameTest
	public void vanillaPressurePlatesAndCarpetsAreAllFound(GameTestHelper helper) {
		Scope scope = vanillaScope(helper);
		List<String> failures = scopeFailures(scope);
		if (!failures.isEmpty()) {
			helper.fail(String.join("\n", failures));
		}
		Caramel.LOGGER.info("C-003 game test: {} pressure plates, {} wool carpets, {} moss carpets, {} of them from slabs", scope.pressurePlates().size(),
				scope.woolCarpets().size(), scope.mossCarpets().size(), scope.all().stream().filter(replaced -> replaced.ingredient() != replaced.block()).count());
		helper.succeed();
	}

	@GameTest
	public void twoSlabsSideBySideMakeSixteen(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaScope(helper).sixteen()) {
			for (Grid grid : placements(2, 1, List.of(new ItemStack(replaced.ingredient()), new ItemStack(replaced.ingredient())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(replaced.result(), 16)) {
					failures.add("2 " + name(replaced.ingredient()) + " side by side " + grid.label() + " gave " + crafted + ", expected 16 " + name(replaced.result()));
				}
			}
		}
		report(helper, IDEA, "side by side grids", checked, failures);
	}

	@GameTest
	public void twoMossBlocksSideBySideMakeThirtyTwoCarpets(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Variant> slabs = SlabsRecipeGameTest.vanillaSlabs(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaScope(helper).mossCarpets()) {
			// With no slab to craft them from, 2 blocks give 32, 16 per block like the others.
			checked++;
			slabs.stream().filter(slab -> slab.accepted().contains(replaced.block())).forEach(slab ->
					failures.add(name(slab.item()) + " is crafted from " + name(replaced.block()) + ", so " + name(replaced.result()) + " should come from slabs"));
			for (Grid grid : placements(2, 1, List.of(new ItemStack(replaced.ingredient()), new ItemStack(replaced.ingredient())))) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.gives(replaced.result(), 32)) {
					failures.add("2 " + name(replaced.ingredient()) + " side by side " + grid.label() + " gave " + crafted + ", expected 32 " + name(replaced.result()));
				}
			}
		}
		report(helper, IDEA, "moss carpet grids", checked, failures);
	}

	// Each new recipe is the vanilla recipe's id, so it replaces it, with vanilla's recipe book group and category.
	@GameTest
	public void recipesKeepTheVanillaIdGroupAndCategory(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaScope(helper).all()) {
			checked++;
			ItemStack ingredient = new ItemStack(replaced.ingredient());
			Crafted crafted = craft(helper, crafting, CraftingInput.of(2, 1, List.of(ingredient, ingredient.copy())));
			if (crafted.recipes().size() != 1 || !crafted.result().is(replaced.result())) {
				failures.add("2 " + name(replaced.ingredient()) + " side by side gave " + crafted + ", expected " + name(replaced.result()));
				continue;
			}
			RecipeHolder<?> holder = crafted.recipes().getFirst();
			CraftingRecipe recipe = (CraftingRecipe) holder.value();
			if (!holder.id().identifier().equals(replaced.id())) {
				failures.add(name(replaced.result()) + " is crafted by " + holder.id().identifier() + ", expected vanilla's id " + replaced.id());
			} else if (!recipe.group().equals(replaced.group()) || recipe.category() != replaced.category()) {
				failures.add(replaced.id() + " has group \"" + recipe.group() + "\" and category " + recipe.category().getSerializedName()
						+ ", expected \"" + replaced.group() + "\" and " + replaced.category().getSerializedName());
			}
		}
		report(helper, IDEA, "replaced recipes", checked, failures);
	}

	// Each replaced recipe keeps vanilla's unlock advancement (MorePerCraft says how).
	@GameTest
	public void recipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, IDEA, vanillaScope(helper).all().stream().map(Replaced::id).toList());
	}

	@GameTest
	public void mixedPairsGiveNothing(GameTestHelper helper) {
		List<RecipeHolder<?>> crafting = craftingRecipes(helper);
		List<Replaced> sixteen = vanillaScope(helper).sixteen();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (int i = 0; i < sixteen.size(); i++) {
			Replaced a = sixteen.get(i);
			Replaced b = sixteen.get((i + 1) % sixteen.size());
			List<Grid> grids = new ArrayList<>();
			grids.add(new Grid(name(a.ingredient()) + " and " + name(b.ingredient()) + " side by side",
					CraftingInput.of(2, 1, List.of(new ItemStack(a.ingredient()), new ItemStack(b.ingredient())))));
			if (a.ingredient() != a.block()) {
				grids.add(new Grid(name(a.ingredient()) + " and " + name(a.block()) + " side by side",
						CraftingInput.of(2, 1, List.of(new ItemStack(a.ingredient()), new ItemStack(a.block())))));
			}
			for (Grid grid : grids) {
				checked++;
				Crafted crafted = craft(helper, crafting, grid.input());
				if (!crafted.recipes().isEmpty()) {
					failures.add(grid.label() + " gave " + crafted);
				}
			}
		}
		report(helper, IDEA, "mixed grids", checked, failures);
	}

	// C-002 left the detector rail without a stone pressure plate to craft it from. Vanilla's recipe, filled with the
	// first item each of its ingredients accepts, crafts vanilla's result again, and every pressure plate it takes is one
	// C-003 crafts.
	@GameTest
	public void detectorRailCraftsAgain(GameTestHelper helper) {
		Identifier id = BuiltInRegistries.ITEM.getKey(Items.DETECTOR_RAIL);
		if (!(vanillaRecipes(helper).get(id) instanceof ShapedRecipe vanilla)) {
			throw helper.assertionException(Component.literal("The vanilla data pack has no shaped recipe " + id));
		}
		List<ItemStack> cells = vanilla.getIngredients().stream()
				.map(ingredient -> ingredient.map(accepts -> new ItemStack(accepted(accepts).getFirst())).orElse(ItemStack.EMPTY))
				.toList();
		CraftingInput input = CraftingInput.of(vanilla.getWidth(), vanilla.getHeight(), cells);
		ItemStack expected = vanilla.assemble(input);
		Set<Item> crafted = new HashSet<>();
		vanillaScope(helper).all().forEach(replaced -> crafted.add(replaced.result()));
		List<String> failures = new ArrayList<>();
		int checked = 1;
		Crafted result = craft(helper, craftingRecipes(helper), input);
		if (!result.gives(expected.getItem(), expected.getCount())) {
			failures.add(id + "'s vanilla grid gave " + result + ", expected " + name(expected));
		}
		int plates = 0;
		for (ItemStack cell : cells) {
			if (cell.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTags.PRESSURE_PLATES)) {
				checked++;
				plates++;
				if (!crafted.contains(cell.getItem())) {
					failures.add(id + " takes " + name(cell.getItem()) + ", which C-003 does not craft");
				}
			}
		}
		if (plates == 0) {
			failures.add(id + "'s vanilla recipe takes no pressure plate");
		}
		report(helper, IDEA, "detector rail checks", checked, failures);
	}

	// 2 slabs, one block, craft into 16, so each pressure plate or carpet that burned in vanilla burns 1/16 of its block,
	// rounded down, in a furnace, and 16 of them never burn longer than the block in any furnace. The others burn no
	// longer than 1/16 of their block, which does not burn either.
	@GameTest
	public void craftingPressurePlatesAndCarpetsNeverAddsFuel(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		for (Replaced replaced : vanillaScope(helper).all()) {
			ItemStack item = new ItemStack(replaced.result());
			ItemStack block = new ItemStack(replaced.block());
			boolean burnedInVanilla = furnaces.getFirst().burns(level, withVanillaFuel(item, vanilla));
			if (burnedInVanilla) {
				burned++;
			}
			for (Furnace furnace : furnaces) {
				checked++;
				int itemTime = furnace.burnDuration().applyAsInt(level, item);
				int blockTime = furnace.burnDuration().applyAsInt(level, block);
				if (furnace == furnaces.getFirst() && burnedInVanilla && itemTime != blockTime / 16) {
					failures.add(name(replaced.result()) + " burns " + itemTime + " ticks in " + furnace.name() + ", " + name(replaced.block())
							+ " burns " + blockTime + ", expected " + blockTime / 16);
				} else if (16 * itemTime > blockTime) {
					failures.add("16 " + name(replaced.result()) + " burn " + 16 * itemTime + " ticks in " + furnace.name() + ", 1 " + name(replaced.block())
							+ " burns " + blockTime + ", expected no more");
				}
			}
		}
		if (burned == 0) {
			helper.fail("No pressure plate or carpet burned in a furnace in vanilla");
		}
		Caramel.LOGGER.info("C-003 game test: {} pressure plates and carpets burned in vanilla", burned);
		report(helper, IDEA, "pressure plate and carpet burn times", checked, failures);
	}

	// Every pressure plate or carpet that burned in vanilla still burns in every furnace, at vanilla's cooking speed, and
	// every one that did not still does not: the furnace takes it only if vanilla's did.
	@GameTest
	public void pressurePlatesAndCarpetsThatBurnedStillBurn(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		List<Furnace> furnaces = furnaces();
		List<String> failures = new ArrayList<>();
		int checked = 0;
		int burned = 0;
		int changed = 0;
		List<Replaced> all = vanillaScope(helper).all();
		for (Replaced replaced : all) {
			ItemStack now = new ItemStack(replaced.result());
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
					failures.add(name(replaced.result()) + (burnedBefore ? " no longer burns" : " burns") + " in " + furnace.name() + (burnedBefore ? "" : ", but did not in vanilla"));
				}
				if (!burnedBefore && furnace.fuelSlot().test(now) != furnace.fuelSlot().test(before)) {
					failures.add(furnace.name() + (furnace.fuelSlot().test(now) ? " takes " : " no longer takes ") + name(replaced.result()) + " as fuel");
				}
				float speedBefore = furnace.speedMultiplier().apply(level, before);
				float speedNow = furnace.speedMultiplier().apply(level, now);
				if (burnedBefore && speedNow != speedBefore) {
					failures.add(name(replaced.result()) + " makes " + furnace.name() + " cook " + speedNow + " times as fast, " + speedBefore + " in vanilla");
				}
			}
		}
		// Proves the vanilla components really are from before Caramel changed them.
		if (changed == 0) {
			helper.fail("No pressure plate or carpet has a different fuel from vanilla's, so the vanilla item components are not vanilla's");
		}
		Caramel.LOGGER.info("C-003 game test: {} pressure plates and carpets burned in vanilla, {} did not, {} have a different fuel now",
				burned, all.size() - burned, changed);
		report(helper, IDEA, "pressure plate and carpet fuel checks", checked, failures);
	}

	// Of every item, only the moss carpets compost differently from vanilla: C-003 makes 16 of them from one block, and
	// no other item it makes composts.
	@GameTest
	public void onlyMossCarpetsHaveANewCompostChance(GameTestHelper helper) {
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		Set<Item> expected = new HashSet<>();
		vanillaScope(helper).mossCarpets().forEach(replaced -> expected.add(replaced.result()));
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Item item : BuiltInRegistries.ITEM) {
			checked++;
			if (!vanilla.containsKey(item)) {
				failures.add(name(item) + " has no components as vanilla builds them");
				continue;
			}
			Compostable vanillaCompostable = vanilla.get(item).get(DataComponents.COMPOSTABLE);
			Compostable compostable = item.components().get(DataComponents.COMPOSTABLE);
			boolean changed = !Objects.equals(vanillaCompostable, compostable);
			if (changed != expected.contains(item)) {
				failures.add(name(item) + (changed ? " composts as " + compostable + ", vanilla's is " + vanillaCompostable : " still composts as vanilla's " + vanillaCompostable));
			}
		}
		Caramel.LOGGER.info("C-003 game test: {} items have a new compost chance", expected.size());
		report(helper, IDEA, "item compost checks", checked, failures);
	}

	// 2 moss blocks craft into 32 moss carpets, so 16 carpets never add more composter layers on average than their
	// block, at any level of the composter, and each adds 1/16 of the block's chance rounded down to a whole percent, as
	// the owner chose. The block's chance is vanilla's. The first item into an empty composter still always adds a layer.
	@GameTest
	public void mossCarpetsCompostNoMoreThanTheirBlock(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		BlockPos composter = new BlockPos(1, 1, 1);
		Fraction sixteen = Fraction.getFraction(16, 1);
		List<String> failures = new ArrayList<>();
		int checked = 0;
		for (Replaced replaced : vanillaScope(helper).mossCarpets()) {
			Compostable carpet = new ItemStack(replaced.result()).get(DataComponents.COMPOSTABLE);
			Compostable block = vanilla.get(replaced.block()).get(DataComponents.COMPOSTABLE);
			checked++;
			if (carpet == null || block == null) {
				failures.add(name(replaced.result()) + " composts as " + carpet + " and vanilla's " + name(replaced.block()) + " as " + block + ", expected both to compost");
				continue;
			}
			for (int layers = ComposterBlock.MIN_LEVEL + 1; layers < ComposterBlock.MAX_LEVEL; layers++) {
				checked++;
				LootContext context = composterContext(level, Blocks.COMPOSTER.defaultBlockState().setValue(ComposterBlock.LEVEL, layers), helper.absolutePos(composter));
				Fraction carpetChance = layers(carpet.layers(), context);
				Fraction blockChance = layers(block.layers(), context);
				Fraction chosen = Fraction.getFraction(blockChance.multiplyBy(Fraction.getFraction(100, 1)).divideBy(sixteen).intValue(), 100);
				// A block that never adds a layer would let any carpet pass.
				if (blockChance.compareTo(Fraction.ZERO) <= 0) {
					failures.add("Vanilla's " + name(replaced.block()) + " adds no layer at level " + layers);
				} else if (carpetChance.multiplyBy(sixteen).compareTo(blockChance) > 0 || carpetChance.compareTo(chosen) != 0) {
					failures.add(name(replaced.result()) + " adds a layer with a " + carpetChance + " chance at level " + layers + ", " + name(replaced.block())
							+ " with " + blockChance + ", expected " + chosen);
				}
			}
			checked++;
			helper.setBlock(composter, Blocks.COMPOSTER.defaultBlockState());
			BlockState after = ComposterBlock.insertItem(null, helper.getBlockState(composter), level, new ItemStack(replaced.result()), helper.absolutePos(composter));
			if (!after.is(Blocks.COMPOSTER) || after.getValue(ComposterBlock.LEVEL) != ComposterBlock.MIN_LEVEL + 1) {
				failures.add(name(replaced.result()) + " put into an empty composter left it at " + after + ", expected level " + (ComposterBlock.MIN_LEVEL + 1));
			}
		}
		report(helper, IDEA, "moss carpet compost checks", checked, failures);
	}

	// Every vanilla trade that sells wool carpets is loaded exactly as vanilla wrote it, but gives 20 carpets. Both sides
	// are compared as the trade codec writes them: vanilla's file with its count set to 20, decoded and encoded again,
	// and the loaded trade encoded. The round trip matters: the file's 0.05 parses as a double, the trade's as a float.
	@GameTest
	public void shepherdsSellTwentyCarpets(GameTestHelper helper) {
		RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().getServer().registryAccess());
		Registry<VillagerTrade> trades = helper.getLevel().getServer().registryAccess().lookupOrThrow(Registries.VILLAGER_TRADE);
		Map<Identifier, JsonObject> vanilla = vanillaCarpetTrades(helper);
		List<String> failures = new ArrayList<>();
		if (vanilla.size() != VANILLA_CARPET_TRADES) {
			failures.add("Expected " + VANILLA_CARPET_TRADES + " vanilla trades that sell wool carpets, found " + vanilla.size() + " " + vanilla.keySet());
		}
		vanilla.forEach((id, file) -> {
			JsonObject twenty = file.deepCopy();
			twenty.getAsJsonObject("gives").addProperty("count", CARPETS_PER_TRADE);
			JsonElement expected = VillagerTrade.CODEC.encodeStart(ops, VillagerTrade.CODEC.parse(ops, twenty).getOrThrow()).getOrThrow();
			Optional<VillagerTrade> loaded = trades.getOptional(id);
			if (loaded.isEmpty()) {
				failures.add("Trade " + id + " is not loaded");
				return;
			}
			JsonElement actual = VillagerTrade.CODEC.encodeStart(ops, loaded.get()).getOrThrow();
			if (!actual.equals(expected)) {
				failures.add("Trade " + id + " is " + actual + ", expected " + expected);
			}
		});
		report(helper, IDEA, "carpet trades", vanilla.size(), failures);
	}

	// Every trade in the vanilla data pack whose gives is a wool carpet, by id.
	static Map<Identifier, JsonObject> vanillaCarpetTrades(GameTestHelper helper) {
		Map<Identifier, JsonObject> carpetTrades = new TreeMap<>();
		packFiles(helper, "vanilla", Registries.VILLAGER_TRADE).forEach((id, file) -> {
			Identifier gives = Identifier.parse(file.getAsJsonObject("gives").get("id").getAsString());
			if (BuiltInRegistries.ITEM.getValue(gives).builtInRegistryHolder().is(ItemTags.WOOL_CARPETS)) {
				carpetTrades.put(id, file);
			}
		});
		return carpetTrades;
	}

	// Found by crafting with every vanilla crafting recipe, decoded from the vanilla data pack. A pressure plate is a
	// recipe whose result is in #minecraft:pressure_plates, a block tag: 26.3 has no item tag of that name.
	static Scope vanillaScope(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ContextMap display = SlotDisplayContext.fromLevel(level);
		List<Variant> slabs = SlabsRecipeGameTest.vanillaSlabs(helper);
		Map<Identifier, Recipe<?>> vanilla = vanillaRecipes(helper);
		List<Replaced> pressurePlates = new ArrayList<>();
		List<Replaced> woolCarpets = new ArrayList<>();
		List<Replaced> mossCarpets = new ArrayList<>();
		vanilla.forEach((id, value) -> {
			if (!(value instanceof CraftingRecipe recipe)) {
				return;
			}
			for (ItemStack result : recipe.display().stream().map(shown -> shown.result().resolveForFirstStack(display)).toList()) {
				if (result.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTags.PRESSURE_PLATES)) {
					pressurePlates.add(replaced(helper, id, recipe, slabs));
					return;
				}
			}
			if (slabs.stream().flatMap(slab -> slab.accepted().stream())
					.anyMatch(block -> recipe.matches(CraftingInput.of(2, 1, List.of(new ItemStack(block), new ItemStack(block))), level))) {
				woolCarpets.add(replaced(helper, id, recipe, slabs));
			}
		});
		for (Item carpet : MOSS_CARPETS) {
			Identifier id = BuiltInRegistries.ITEM.getKey(carpet);
			if (!(vanilla.get(id) instanceof CraftingRecipe recipe)) {
				throw helper.assertionException(Component.literal("The vanilla data pack has no crafting recipe " + id));
			}
			mossCarpets.add(replaced(helper, id, recipe, slabs));
		}
		return new Scope(pressurePlates, woolCarpets, mossCarpets);
	}

	// C-003's scope from docs/DESIGN.md, and what the recipes it found make. A Minecraft update that shifts it fails
	// here, so the scope gets rechecked.
	static List<String> scopeFailures(Scope scope) {
		List<String> failures = new ArrayList<>();
		if (scope.pressurePlates().size() != VANILLA_PRESSURE_PLATES || scope.woolCarpets().size() != VANILLA_WOOL_CARPETS) {
			failures.add("Expected " + VANILLA_PRESSURE_PLATES + " pressure plate, " + VANILLA_WOOL_CARPETS + " wool carpet and " + VANILLA_MOSS_CARPETS
					+ " moss carpet recipes to replace, found " + scope.pressurePlates().size() + ", " + scope.woolCarpets().size() + " and " + scope.mossCarpets().size());
		}
		for (Replaced carpet : scope.woolCarpets()) {
			if (!new ItemStack(carpet.result()).is(ItemTags.WOOL_CARPETS)) {
				failures.add(carpet.id() + " makes " + name(carpet.result()) + " from 2 " + name(carpet.block()) + " side by side, which is not a wool carpet");
			}
		}
		for (Replaced replaced : scope.all()) {
			if (!replaced.id().equals(BuiltInRegistries.ITEM.getKey(replaced.result()))) {
				failures.add(replaced.id() + " makes " + name(replaced.result()) + ", which has another id");
			}
		}
		return failures;
	}

	// Vanilla's recipe must take 2 of one item side by side. C-003 crafts its result from that item's slab, the slab whose
	// vanilla recipe accepts the item, or from the item itself when none does.
	private static Replaced replaced(GameTestHelper helper, Identifier id, CraftingRecipe recipe, List<Variant> slabs) {
		if (!(recipe instanceof ShapedRecipe shaped) || shaped.getWidth() != 2 || shaped.getHeight() != 1
				|| !shaped.getIngredients().stream().allMatch(Optional::isPresent)) {
			throw helper.assertionException(Component.literal(id + " in the vanilla data pack is not 2 items side by side"));
		}
		List<List<Item>> cells = shaped.getIngredients().stream().map(Optional::orElseThrow).map(RecipeTestSupport::accepted).toList();
		if (cells.getFirst().size() != 1 || !cells.getFirst().equals(cells.getLast())) {
			throw helper.assertionException(Component.literal(id + " in the vanilla data pack accepts " + cells + ", expected 2 of one item"));
		}
		Item block = cells.getFirst().getFirst();
		ItemStack result = recipe.assemble(CraftingInput.of(2, 1, List.of(new ItemStack(block), new ItemStack(block))));
		List<Item> ofSlabs = slabs.stream().filter(slab -> slab.accepted().contains(block)).map(Variant::item).toList();
		if (ofSlabs.size() > 1) {
			throw helper.assertionException(Component.literal(name(block) + " makes " + ofSlabs.size() + " slabs " + ofSlabs + ", expected at most 1"));
		}
		return new Replaced(id, result.getItem(), block, ofSlabs.isEmpty() ? block : ofSlabs.getFirst(), recipe.group(), recipe.category());
	}

	// The loot context a composter asks for its chance in (ComposterBlock.addLayer). Its minecraft:match_block cases
	// read the composter's level from the block state given here.
	private static LootContext composterContext(ServerLevel level, BlockState composter, BlockPos pos) {
		return new LootContext.Builder(new LootParams.Builder(level)
				.withParameter(LootContextParams.BLOCK_STATE, composter)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
				.create(LootContextParamSets.BLOCK_INTERACT)).create(Optional.empty());
	}

	// How many layers an item adds on average, worked out exactly rather than by drawing: the key is looked up as the
	// game looks it up, and a missing one adds nothing, as in ComposterBlock.addLayer.
	private static Fraction layers(ResolvableInt layers, LootContext context) {
		if (layers instanceof ResolvableInt.Reference(var key)) {
			return context.getResolver().lookupOrThrow(Registries.CONTEXT_INT_PROVIDER).get(key)
					.map(provider -> layers(provider.value(), context))
					.orElse(Fraction.ZERO);
		}
		return Fraction.getFraction(layers.get(context, 0), 1);
	}

	// A dispatcher picks its case as the game does, a weighted list averages its entries by weight, and any other
	// provider is evaluated.
	private static Fraction layers(ContextIntProvider provider, LootContext context) {
		if (provider instanceof NumberDispatcher dispatcher) {
			return layers(dispatcher.selectValue(context).value(), context);
		}
		if (provider instanceof WeightedListValue weightedList) {
			Fraction sum = Fraction.ZERO;
			int total = 0;
			for (Weighted<Holder<ContextIntProvider>> entry : weightedList.distribution().unwrap()) {
				sum = sum.add(layers(entry.value().value(), context).multiplyBy(Fraction.getFraction(entry.weight(), 1)));
				total += entry.weight();
			}
			return sum.divideBy(Fraction.getFraction(total, 1));
		}
		return Fraction.getFraction(provider.getInt(context), 1);
	}
}
