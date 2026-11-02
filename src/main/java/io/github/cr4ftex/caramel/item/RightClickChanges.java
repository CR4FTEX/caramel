package io.github.cr4ftex.caramel.item;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

// What a held item's right click would change, worked out before the game handles the click: Fabric's block-click
// event, which Caramel listens to, runs first, and the game offers no event after a change.
public final class RightClickChanges {
	private RightClickChanges() {
	}

	// The entry of the held item's block transformer that the click would change a block by, by the checks
	// ItemStack.useOn and BlockTransformer.transformBlock make before they change a block, and by the game running a
	// block's own right click first. The caller checks the item's cooldown. A Chisel does not change a pressed button or
	// plate (see refused).
	public static Optional<BlockTransformer.BlockTransformData> change(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return entry(player, level, hand, hit).filter(entry -> !awaitsRelease(player, level, hand, hit));
	}

	// Whether a Chisel's click would change a pressed button or plate if the game let it. A block's state is copied whole,
	// the pressed state too, but the tick that releases it is scheduled for the block it was in: the game runs a scheduled
	// tick only while the position still holds that block (ServerLevel.tickBlock), so the new block would stay pressed
	// for good. The game gives a transformer no way to say no, so the caller ends the click.
	public static boolean refused(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return entry(player, level, hand, hit).isPresent() && awaitsRelease(player, level, hand, hit);
	}

	// Only a Chisel's: an axe scraping a powered copper door or bulb stays the game's.
	private static boolean awaitsRelease(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		Holder<BlockTransformer> transformer = player.getItemInHand(hand).get(DataComponents.BLOCK_TRANSFORMER);
		return transformer != null && transformer.is(Chisels.TRANSFORMER) && level.getBlockState(hit.getBlockPos()).getOptionalValue(BlockStateProperties.POWERED).orElse(false);
	}

	// The game runs a block's own right click before the held item's, unless the player sneaks with something in hand
	// (ServerPlayerGameMode.useItemOn, MultiPlayerGameMode.performUseItemOn); when it consumes the click, the item is never
	// used. Only buttons have one among the blocks a Chisel changes (ChiselPiecesGameTest checks it). It is run for the main
	// hand, and a click that reaches the off hand has had it first.
	private static boolean runsOwnRightClick(Player player, BlockState state) {
		boolean suppressed = player.isSecondaryUseActive() && (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty());
		return !suppressed && state.getBlock() instanceof ButtonBlock;
	}

	private static Optional<BlockTransformer.BlockTransformData> entry(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		Holder<BlockTransformer> transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
		BlockPos pos = hit.getBlockPos();
		if (transformer == null || player.isSpectator() || runsOwnRightClick(player, level.getBlockState(pos))) {
			return Optional.empty();
		}
		if (!player.getAbilities().mayBuild && !stack.canPlaceOnBlockInAdventureMode(new BlockInWorld(level, pos, false))) {
			return Optional.empty();
		}
		// A shield in the off hand takes a main-hand click unless the player sneaks.
		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !player.isSecondaryUseActive()) {
			return Optional.empty();
		}
		return transformer.value().transforms().stream()
				.filter(transform -> !transform.disallowedFaces().contains(hit.getDirection())
						&& transform.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos) != null)
				.findFirst();
	}
}
