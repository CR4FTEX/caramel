package io.github.cr4ftex.caramel.gametest;

import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.name;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.packFiles;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.report;
import static io.github.cr4ftex.caramel.gametest.RecipeTestSupport.vanillaComponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import io.github.cr4ftex.caramel.Caramel;
import io.github.cr4ftex.caramel.gametest.FencesRecipeGameTest.Fence;
import io.github.cr4ftex.caramel.gametest.PressurePlatesAndCarpetsGameTest.Replaced;
import io.github.cr4ftex.caramel.gametest.PressurePlatesAndCarpetsGameTest.Scope;
import io.github.cr4ftex.caramel.gametest.RecipeTestSupport.Variant;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.StairBlock;

// C-001, C-003 and C-004 craft stairs, pressure plates, carpets and fences from fewer blocks, so those that burned burn
// for what they cost now. Which items burned comes from the item components vanilla builds, before Caramel changes
// them, and which items the ideas craft from the vanilla data pack, through each idea's test class.
public class CraftedFuelGameTest {
	private static final String IDEAS = "C-001, C-003 and C-004";

	// Of every item, only the vanilla stairs, pressure plates, wool carpets and fences that burned have a new fuel: not
	// the blocks they are made from, which the burn time checks measure them against, and not other mods' items, whose
	// recipes the ideas leave alone. Caramel's data pack replaces none of vanilla's burn times, cooking speeds or the
	// predicates they use either.
	@GameTest
	public void onlyVanillaStairsPressurePlatesCarpetsAndFencesThatBurnedHaveNewFuel(GameTestHelper helper) {
		Map<Item, DataComponentMap> vanilla = vanillaComponents(helper);
		Predicate<Item> burned = item -> vanilla.containsKey(item) && vanilla.get(item).has(DataComponents.COOKING_FUEL);
		Scope scope = PressurePlatesAndCarpetsGameTest.vanillaScope(helper);
		List<Item> stairs = StairsRecipeGameTest.vanillaStairs(helper).stream().map(Variant::item).filter(burned).toList();
		List<Item> pressurePlates = scope.pressurePlates().stream().map(Replaced::result).filter(burned).toList();
		List<Item> woolCarpets = scope.woolCarpets().stream().map(Replaced::result).filter(burned).toList();
		List<Item> fences = FencesRecipeGameTest.vanillaFences(helper).stream().map(Fence::item).filter(burned).toList();
		Set<Item> expected = new HashSet<>(stairs);
		expected.addAll(pressurePlates);
		expected.addAll(woolCarpets);
		expected.addAll(fences);
		// Without other mods' stairs that burn, nothing here would tell vanilla's items from any others.
		Item otherModStairs = BuiltInRegistries.ITEM.getValue(OtherModStairs.ITEM);
		if (!(otherModStairs instanceof BlockItem blockItem && blockItem.getBlock() instanceof StairBlock) || !burned.test(otherModStairs)) {
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
		Caramel.LOGGER.info("C-001, C-003 and C-004 game test: {} items have a new fuel: {} stairs, {} pressure plates, {} wool carpets, {} fences",
				expected.size(), stairs.size(), pressurePlates.size(), woolCarpets.size(), fences.size());
		report(helper, IDEAS, "item fuel checks", checked, failures);
	}
}
