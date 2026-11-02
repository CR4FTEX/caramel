package io.github.cr4ftex.caramel.item;

import java.util.Optional;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

// C-009, the owner's suggestion: a right click that chisels or strips a block throws a quarter of the particles that
// breaking the block would, from a Chisel's every change and from an axe's stripping. The axe's scraping and wax
// removal keep their own particles. Scraping moss throws the Moss Block's, by the owner's request (C-010).
public final class CarvingParticles {
	// The owner's rule: a quarter of the particles that breaking the block would throw. A full block breaks into 4 by 4
	// by 4, so 16; a stair, a slab or a wall post breaks into fewer, from its shape (see particles).
	private static final int BREAKING_PER_CARVING = 4;
	private static final double EDGE = 0.1;

	private CarvingParticles() {
	}

	public static void register() {
		UseBlockCallback.EVENT.register(CarvingParticles::onUseBlock);
	}

	// The game offers no event after a change, so the particles go out just before it, in the same tick, from the
	// server to every player in range, the clicking one included.
	private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		if (level instanceof ServerLevel serverLevel) {
			burst(player, level, hand, hit).ifPresent(burst -> serverLevel.sendParticles(burst.particle(), burst.corner().x(), burst.corner().y(), burst.corner().z(), burst.count(),
					burst.spread().x(), burst.spread().y(), burst.spread().z(), 0.0, ClientboundLevelParticlesPacket.RandomizationType.ALTERNATIVE));
		}
		return InteractionResult.PASS;
	}

	// What the click throws: particles of the block as it is, or of moss, spread evenly over the clicked side of the
	// block's shape, 0.1 out from it and 0.1 in from its edges, where the game puts the cracks of a block being mined
	// (ClientLevel.addBreakingParticles). The block stays solid, so particles inside it would stay hidden. Each one bursts
	// out on its own.
	public record Burst(BlockParticleOption particle, int count, Vec3 corner, Vec3 spread) {
	}

	public static Optional<Burst> burst(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return carving(player, level, hand, hit).map(change -> {
			BlockPos pos = hit.getBlockPos();
			Direction face = hit.getDirection();
			BlockState state = level.getBlockState(pos);
			VoxelShape shape = state.getShape(level, pos);
			AABB box = shape.bounds().move(pos);
			Vec3 corner = Vec3.ZERO;
			Vec3 spread = Vec3.ZERO;
			for (Direction.Axis axis : Direction.Axis.values()) {
				if (axis == face.getAxis()) {
					corner = corner.with(axis, face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? box.max(axis) + EDGE : box.min(axis) - EDGE);
				} else {
					double inset = Math.min(EDGE, (box.max(axis) - box.min(axis)) / 2);
					corner = corner.with(axis, box.min(axis) + inset);
					spread = spread.with(axis, box.max(axis) - box.min(axis) - 2 * inset);
				}
			}
			BlockState shown = Chisels.scrapesMoss(change) ? Blocks.MOSS_BLOCK.defaultBlockState() : state;
			return new Burst(new BlockParticleOption(ParticleTypes.BLOCK, shown), particles(shape), corner, spread);
		});
	}

	// A quarter of what breaking a block of this shape throws: for each box of the shape, at least 2 along each side and
	// one per quarter block (ClientLevel.addDestroyBlockEffect).
	private static int particles(VoxelShape shape) {
		int[] breaking = {0};
		shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> breaking[0] += along(x2 - x1) * along(y2 - y1) * along(z2 - z1));
		return breaking[0] / BREAKING_PER_CARVING;
	}

	private static int along(double width) {
		return Math.max(2, (int) Math.ceil(Math.min(1.0, width) / 0.25));
	}

	// Whether the click is about to make a Chisel's change or an axe's stripping.
	public static boolean throwsParticles(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return carving(player, level, hand, hit).isPresent();
	}

	private static Optional<BlockTransformer.BlockTransformData> carving(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		Holder<BlockTransformer> transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
		if (transformer == null || player.getCooldowns().isOnCooldown(stack)) {
			return Optional.empty();
		}
		return RightClickChanges.change(player, level, hand, hit)
				.filter(change -> transformer.is(Chisels.TRANSFORMER) || transformer.is(BlockTransformers.AXE) && change.sound().value() == SoundEvents.AXE_STRIP.value());
	}
}
