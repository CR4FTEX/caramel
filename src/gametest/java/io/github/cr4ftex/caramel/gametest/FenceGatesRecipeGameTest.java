package io.github.cr4ftex.caramel.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;

// C-005: every fence gate recipe gives 4 times vanilla's count. The checks are MorePerCraft's.
public class FenceGatesRecipeGameTest {
	// C-005's scope in Minecraft 26.3, from docs/DESIGN.md: the 13 wooden fence gates. The owner's rule: 4 instead of 1.
	static final MorePerCraft.Scope SCOPE = new MorePerCraft.Scope("C-005", "fence gate", BlockTags.FENCE_GATES, 13, 4);

	@GameTest
	public void vanillaFenceGatesAreAllFound(GameTestHelper helper) {
		MorePerCraft.allFound(helper, SCOPE);
	}

	@GameTest
	public void fenceGateRecipesGiveFourTimesAsMany(GameTestHelper helper) {
		MorePerCraft.gridsGiveMore(helper, SCOPE);
	}

	@GameTest
	public void fenceGateRecipesAreVanillasWithFourTimesTheCount(GameTestHelper helper) {
		MorePerCraft.recipesAreVanillasWithMore(helper, SCOPE);
	}

	@GameTest
	public void fenceGateRecipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, SCOPE);
	}

	// 4 fence gates cost 2 planks and 4 sticks, 4 planks, so a wooden fence gate burns exactly a plank, as in vanilla.
	@GameTest
	public void craftingFenceGatesNeverAddsFuel(GameTestHelper helper) {
		MorePerCraft.craftingNeverAddsFuel(helper, SCOPE);
	}

	// C-005 keeps vanilla's fuel for every fence gate.
	@GameTest
	public void fenceGatesThatBurnedStillBurn(GameTestHelper helper) {
		MorePerCraft.thatBurnedStillBurn(helper, SCOPE, false);
	}
}
