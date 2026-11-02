package io.github.cr4ftex.caramel.block;

import java.util.List;
import java.util.function.Function;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StateArgumentPredicate;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

// C-010: the Stone family's new blocks and the Stone Brick item. The base game's blocks of the family keep their ids;
// only their English names change. Each new block is made the way vanilla's Blocks makes the same piece of a family,
// from its closest vanilla block, so it breaks, sounds and resists blasts like that block.
public final class StoneFamily {
	// The base game's private Blocks.NEAR_PLANE_INTERSECTS_OUTLINE, which its stairs and slabs are made with: such a
	// block blocks the view only where its outline meets the camera.
	private static final StateArgumentPredicate<AABB> NEAR_PLANE_INTERSECTS_OUTLINE = (state, level, pos, nearPlaneBox) -> {
		for (AABB outlineBox : state.getOcclusionShape().toAabbs()) {
			if (outlineBox.move(pos).intersects(nearPlaneBox)) {
				return true;
			}
		}
		return false;
	};

	// Stone gains a wall.
	public static final Block STONE_WALL = wall("stone_wall", Blocks.STONE);
	// Cobbled Stone and Cobbled Mossy Stone gain a button and a pressure plate.
	public static final Block COBBLESTONE_BUTTON = button("cobblestone_button");
	public static final Block COBBLESTONE_PRESSURE_PLATE = pressurePlate("cobblestone_pressure_plate", Blocks.COBBLESTONE);
	public static final Block MOSSY_COBBLESTONE_BUTTON = button("mossy_cobblestone_button");
	public static final Block MOSSY_COBBLESTONE_PRESSURE_PLATE = pressurePlate("mossy_cobblestone_pressure_plate", Blocks.MOSSY_COBBLESTONE);
	// Smooth Stone, the base game's, which Caramel gives a new texture, gains stairs, a wall, a button and a pressure plate.
	public static final Block SMOOTH_STONE_STAIRS = stairs("smooth_stone_stairs", Blocks.SMOOTH_STONE);
	public static final Block SMOOTH_STONE_WALL = wall("smooth_stone_wall", Blocks.SMOOTH_STONE);
	public static final Block SMOOTH_STONE_BUTTON = button("smooth_stone_button");
	public static final Block SMOOTH_STONE_PRESSURE_PLATE = pressurePlate("smooth_stone_pressure_plate", Blocks.SMOOTH_STONE);
	// The new Polished Stone, crafted from 4 Stone, in the base game's old Smooth Stone look, with its set.
	public static final Block POLISHED_STONE = block("polished_stone", Blocks.SMOOTH_STONE);
	public static final Block POLISHED_STONE_STAIRS = stairs("polished_stone_stairs", POLISHED_STONE);
	public static final Block POLISHED_STONE_SLAB = slab("polished_stone_slab", POLISHED_STONE);
	public static final Block POLISHED_STONE_WALL = wall("polished_stone_wall", POLISHED_STONE);
	public static final Block POLISHED_STONE_BUTTON = button("polished_stone_button");
	public static final Block POLISHED_STONE_PRESSURE_PLATE = pressurePlate("polished_stone_pressure_plate", POLISHED_STONE);
	// The pillar turns with how it is placed, like the quartz pillar (the owner's answer).
	public static final Block CHISELED_STONE_PILLAR = register("chiseled_stone_pillar", RotatedPillarBlock::new, Properties.ofLegacyCopy(Blocks.CHISELED_STONE_BRICKS));
	public static final Block STONE_TILES = block("stone_tiles", Blocks.STONE_BRICKS);
	public static final Block CRACKED_STONE_TILES = block("cracked_stone_tiles", Blocks.CRACKED_STONE_BRICKS);
	public static final Block STONE_TILE_STAIRS = stairs("stone_tile_stairs", STONE_TILES);
	// The stone brick slab is a little harder than its block, and so is this one.
	public static final Block STONE_TILE_SLAB = register("stone_tile_slab", SlabBlock::new, slabProperties(STONE_TILES).destroyTime(Blocks.STONE_BRICK_SLAB.defaultDestroyTime()));
	public static final Block STONE_TILE_WALL = wall("stone_tile_wall", STONE_TILES);
	// Made like the nether brick fence, with its block's own properties.
	public static final Block STONE_TILE_FENCE = register("stone_tile_fence", FenceBlock::new, Properties.ofLegacyCopy(STONE_TILES));

	// Cut from Stone Bricks on the stonecutter; 4 make Stone Bricks again.
	public static final Item STONE_BRICK = Registry.register(BuiltInRegistries.ITEM, itemKey("stone_brick"), new Item(new Item.Properties().setId(itemKey("stone_brick"))));

	public static final List<Block> BLOCKS = List.of(STONE_WALL, COBBLESTONE_BUTTON, COBBLESTONE_PRESSURE_PLATE, MOSSY_COBBLESTONE_BUTTON, MOSSY_COBBLESTONE_PRESSURE_PLATE,
			SMOOTH_STONE_STAIRS, SMOOTH_STONE_WALL, SMOOTH_STONE_BUTTON, SMOOTH_STONE_PRESSURE_PLATE, POLISHED_STONE, POLISHED_STONE_STAIRS, POLISHED_STONE_SLAB, POLISHED_STONE_WALL, POLISHED_STONE_BUTTON,
			POLISHED_STONE_PRESSURE_PLATE,
			CHISELED_STONE_PILLAR, STONE_TILES, CRACKED_STONE_TILES, STONE_TILE_STAIRS, STONE_TILE_SLAB, STONE_TILE_WALL, STONE_TILE_FENCE);

	private StoneFamily() {
	}

	// Loading this class registers the blocks and items, so it must be called while the mod initializes: Fabric
	// freezes the registries right after.
	public static void register() {
		// Each next to its family, in the order of the Chisel's chain: Smooth Stone's set, then Polished Stone's, and the
		// pillar and the tiles after the chiseled bricks. Pressure plates come before buttons, as vanilla's do here.
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
			output.insertAfter(Items.STONE_SLAB, STONE_WALL);
			output.insertAfter(Items.COBBLESTONE_WALL, COBBLESTONE_PRESSURE_PLATE, COBBLESTONE_BUTTON);
			output.insertAfter(Items.MOSSY_COBBLESTONE_WALL, MOSSY_COBBLESTONE_PRESSURE_PLATE, MOSSY_COBBLESTONE_BUTTON);
			output.insertAfter(Items.SMOOTH_STONE, SMOOTH_STONE_STAIRS);
			output.insertAfter(Items.SMOOTH_STONE_SLAB, SMOOTH_STONE_WALL, SMOOTH_STONE_PRESSURE_PLATE, SMOOTH_STONE_BUTTON, POLISHED_STONE, POLISHED_STONE_STAIRS, POLISHED_STONE_SLAB, POLISHED_STONE_WALL,
					POLISHED_STONE_PRESSURE_PLATE, POLISHED_STONE_BUTTON);
			output.insertAfter(Items.CHISELED_STONE_BRICKS, CHISELED_STONE_PILLAR, STONE_TILES, CRACKED_STONE_TILES, STONE_TILE_STAIRS, STONE_TILE_SLAB, STONE_TILE_WALL,
					STONE_TILE_FENCE);
		});
		// Vanilla lists its stone button and pressure plate among the redstone blocks too.
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(output -> {
			output.insertAfter(Items.STONE_BUTTON, COBBLESTONE_BUTTON, MOSSY_COBBLESTONE_BUTTON, SMOOTH_STONE_BUTTON, POLISHED_STONE_BUTTON);
			output.insertAfter(Items.STONE_PRESSURE_PLATE, COBBLESTONE_PRESSURE_PLATE, MOSSY_COBBLESTONE_PRESSURE_PLATE, SMOOTH_STONE_PRESSURE_PLATE,
					POLISHED_STONE_PRESSURE_PLATE);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> output.insertAfter(Items.BRICK, STONE_BRICK));
	}

	private static Block block(String name, Block closest) {
		return register(name, Block::new, Properties.ofLegacyCopy(closest));
	}

	// Blocks.registerLegacyStair.
	private static Block stairs(String name, Block base) {
		return register(name, properties -> new StairBlock(base.defaultBlockState(), properties), Properties.ofLegacyCopy(base).isViewBlocking(NEAR_PLANE_INTERSECTS_OUTLINE));
	}

	// Blocks.registerSlab.
	private static Block slab(String name, Block base) {
		return register(name, SlabBlock::new, slabProperties(base));
	}

	private static Properties slabProperties(Block base) {
		return Properties.ofLegacyCopy(base).isViewBlocking(NEAR_PLANE_INTERSECTS_OUTLINE);
	}

	// Blocks.registerWall.
	private static Block wall(String name, Block base) {
		return register(name, WallBlock::new, Properties.ofLegacyCopy(base).forceSolidOn());
	}

	// Vanilla's stone button.
	private static Block button(String name) {
		return register(name, properties -> new ButtonBlock(BlockSetType.STONE, 20, properties), Blocks.buttonProperties());
	}

	// Vanilla's stone pressure plate, in its block's map colour.
	private static Block pressurePlate(String name, Block base) {
		return register(name, properties -> new PressurePlateBlock(BlockSetType.STONE, properties), Properties.of()
				.mapColor(base.defaultMapColor())
				.forceSolidOn()
				.instrument(NoteBlockInstrument.BASEDRUM)
				.noCollision()
				.strength(0.5F)
				.pushReaction(PushReaction.POPPED));
	}

	// Blocks.register and Items.registerBlock: the block, then its item, which takes the block's name. Fabric links the
	// item to its block as it is registered, which is what Block.asItem() reads (BlockItemTracker in its registry sync).
	private static Block register(String name, Function<Properties, Block> factory, Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Caramel.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey(name)).useBlockDescriptionPrefix());
		Registry.register(BuiltInRegistries.ITEM, itemKey(name), item);
		return block;
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, Caramel.id(name));
	}
}
