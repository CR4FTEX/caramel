package io.github.cr4ftex.caramel.gametest;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

// Wooden stairs as another mod would add them: they burn as long as a plank, as vanilla's wooden stairs do, but C-001
// gives them no recipe, so their fuel must not change either. They are left out of #minecraft:stairs, which lists the
// stairs the game tests expect vanilla recipes for. Only the game test mod registers them, so they exist in game tests
// alone.
public class OtherModStairs implements ModInitializer {
	public static final String MOD_ID = "caramel-gametest";
	public static final ResourceKey<Item> ITEM = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "wooden_stairs"));

	@Override
	public void onInitialize() {
		Block block = Blocks.register(ResourceKey.create(Registries.BLOCK, ITEM.identifier()),
				properties -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), properties), BlockBehaviour.Properties.of());
		Registry.register(BuiltInRegistries.ITEM, ITEM, new BlockItem(block, new Item.Properties().setId(ITEM).cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)));
	}
}
