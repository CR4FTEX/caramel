package io.github.cr4ftex.caramel.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;

// C-004: every fence recipe gives 4 times vanilla's count. The checks are MorePerCraft's.
public class FencesRecipeGameTest {
	// C-004's scope in Minecraft 26.3, from docs/DESIGN.md: the 13 wooden fences and the nether brick fence. The owner's
	// rule: 12 wooden fences instead of 3, and 24 nether brick fences instead of 6.
	static final MorePerCraft.Scope SCOPE = new MorePerCraft.Scope("C-004", "fence", BlockTags.FENCES, 14, 4);

	@GameTest
	public void vanillaFencesAreAllFound(GameTestHelper helper) {
		MorePerCraft.allFound(helper, SCOPE);
	}

	@GameTest
	public void fenceRecipesGiveFourTimesAsMany(GameTestHelper helper) {
		MorePerCraft.gridsGiveMore(helper, SCOPE);
	}

	@GameTest
	public void fenceRecipesAreVanillasWithFourTimesTheCount(GameTestHelper helper) {
		MorePerCraft.recipesAreVanillasWithMore(helper, SCOPE);
	}

	@GameTest
	public void fenceRecipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, SCOPE);
	}

	// 12 fences cost 4 planks and 2 sticks, 5 planks, so a wooden fence burns 5/12 of a plank.
	@GameTest
	public void craftingFencesNeverAddsFuel(GameTestHelper helper) {
		MorePerCraft.craftingNeverAddsFuel(helper, SCOPE);
	}

	// C-004 gives the wooden fences that burned a new burn time.
	@GameTest
	public void fencesThatBurnedStillBurn(GameTestHelper helper) {
		MorePerCraft.thatBurnedStillBurn(helper, SCOPE, true);
	}
}
