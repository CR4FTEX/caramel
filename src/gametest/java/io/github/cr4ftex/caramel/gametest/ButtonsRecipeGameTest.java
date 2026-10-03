package io.github.cr4ftex.caramel.gametest;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;

// C-007: every button recipe gives 20 instead of 1. The checks are MorePerCraft's.
public class ButtonsRecipeGameTest {
	// C-007's scope in Minecraft 26.3, from docs/DESIGN.md: the 13 wooden buttons, the stone button and the polished
	// blackstone button, each 1 block for 1 button. The owner's rule: 20 of every button.
	static final MorePerCraft.Scope SCOPE = new MorePerCraft.Scope("C-007", "button", "buttons", List.of(BlockTags.BUTTONS), 15, count -> 20);

	@GameTest
	public void vanillaButtonsAreAllFound(GameTestHelper helper) {
		MorePerCraft.allFound(helper, SCOPE);
	}

	@GameTest
	public void buttonRecipesGiveTwenty(GameTestHelper helper) {
		MorePerCraft.gridsGiveMore(helper, SCOPE);
	}

	@GameTest
	public void buttonRecipesAreVanillasWithTwenty(GameTestHelper helper) {
		MorePerCraft.recipesAreVanillasWithMore(helper, SCOPE);
	}

	@GameTest
	public void buttonRecipesKeepTheVanillaUnlockAdvancement(GameTestHelper helper) {
		MorePerCraft.keepTheVanillaUnlockAdvancement(helper, SCOPE);
	}

	// 20 buttons cost 1 plank, so a wooden button burns 1/20 of a plank.
	@GameTest
	public void craftingButtonsNeverAddsFuel(GameTestHelper helper) {
		MorePerCraft.craftingNeverAddsFuel(helper, SCOPE);
	}

	// C-007 gives the wooden buttons that burned a new burn time.
	@GameTest
	public void buttonsThatBurnedStillBurn(GameTestHelper helper) {
		MorePerCraft.thatBurnedStillBurn(helper, SCOPE, true);
	}
}
