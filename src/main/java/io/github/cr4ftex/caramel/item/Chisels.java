package io.github.cr4ftex.caramel.item;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import io.github.cr4ftex.caramel.Caramel;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

// C-009: the Chisel, the first primitive tool. One per tier, each an axe and a pickaxe in one that takes twice as long.
// It is a plain item: mining, attacking and the right-click changes are all components, as on vanilla's own tools.
public final class Chisels {
	// Every Chisel, for the tags that give it the pickaxe's enchantments and traits.
	public static final TagKey<Item> CHISELS = TagKey.create(Registries.ITEM, Caramel.id("chisels"));
	// What repairs a flint tool on an anvil. No vanilla tag holds flint.
	public static final TagKey<Item> FLINT_TOOL_MATERIALS = TagKey.create(Registries.ITEM, Caramel.id("flint_tool_materials"));
	// The right-click changes: stripping, the owner's block cycles with their pieces, and scraping moss, in
	// data/caramel/block_transformer/chisel.json.
	public static final ResourceKey<BlockTransformer> TRANSFORMER = ResourceKey.create(Registries.BLOCK_TRANSFORMER, Caramel.id("chisel"));
	public static final Identifier MINING_EFFICIENCY_ID = Caramel.id("chisel_mining_efficiency");
	// The sound of a right click carving a stone block. Until the owner makes its own, its definition in
	// assets/caramel/sounds.json borrows the anvil's at a higher pitch: a transformer plays every sound at pitch 1.0.
	public static final Holder<SoundEvent> CARVE_STONE = registerSound("item.chisel.carve_stone");
	// The sound of a right click scraping moss off (C-010): the game's own, with the carving sound played beside it.
	public static final Holder<SoundEvent> SCRAPE_MOSS = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.MOSS_BREAK);
	// The owner's rule: a Chisel changes at most one block a second, held or clicked, so its sounds do not pile up.
	// Every Chisel waits in this one group, so switching to another tier does not skip the wait.
	public static final Identifier COOLDOWN_GROUP = Caramel.id("chisel");
	private static final float SECONDS_BETWEEN_CHANGES = 1.0F;

	// The flint tier takes the wood tier's place among primitive tools: wood's numbers, repaired with flint.
	public static final ToolMaterial FLINT = new ToolMaterial(ToolMaterial.WOOD.incorrectBlocksForDrops(), ToolMaterial.WOOD.durability(), ToolMaterial.WOOD.speed(),
			ToolMaterial.WOOD.attackDamageBonus(), ToolMaterial.WOOD.enchantmentValue(), FLINT_TOOL_MATERIALS);

	// The owner's rule: the Chisel attacks at its tier's hoe speed plus this.
	private static final float FASTER_THAN_THE_HOE = 0.5F;

	// A Chisel, its tier, and that tier's vanilla hoe, which it follows in the creative inventory.
	public record Chisel(Item item, ToolMaterial material, Item hoe) {
	}

	// In vanilla's tier order. Each attack speed is the tier's hoe's, from vanilla's Items.
	public static final Chisel FLINT_CHISEL = create("flint_chisel", FLINT, Items.WOODEN_HOE, -3.0F, properties -> properties);
	public static final Chisel STONE_CHISEL = create("stone_chisel", ToolMaterial.STONE, Items.STONE_HOE, -2.0F, properties -> properties);
	public static final Chisel COPPER_CHISEL = create("copper_chisel", ToolMaterial.COPPER, Items.COPPER_HOE, -2.0F, properties -> properties);
	public static final Chisel IRON_CHISEL = create("iron_chisel", ToolMaterial.IRON, Items.IRON_HOE, -1.0F, properties -> properties);
	public static final Chisel GOLDEN_CHISEL = create("golden_chisel", ToolMaterial.GOLD, Items.GOLDEN_HOE, -3.0F, properties -> properties);
	public static final Chisel DIAMOND_CHISEL = create("diamond_chisel", ToolMaterial.DIAMOND, Items.DIAMOND_HOE, 0.0F, properties -> properties);
	public static final Chisel NETHERITE_CHISEL = create("netherite_chisel", ToolMaterial.NETHERITE, Items.NETHERITE_HOE, 0.0F, Item.Properties::fireResistant);

	public static final List<Chisel> ALL = List.of(FLINT_CHISEL, STONE_CHISEL, COPPER_CHISEL, IRON_CHISEL, GOLDEN_CHISEL, DIAMOND_CHISEL, NETHERITE_CHISEL);

	private Chisels() {
	}

	// Loading this class registers the items and the sound, so it must be called while the mod initializes: Fabric
	// freezes the registries right after.
	public static void register() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> ALL.forEach(chisel -> output.insertAfter(chisel.hoe(), chisel.item())));
		UseBlockCallback.EVENT.register(Chisels::holdWhileWaiting);
		UseBlockCallback.EVENT.register(Chisels::refuseWhilePowered);
		UseBlockCallback.EVENT.register(Chisels::carveWhileScraping);
	}

	// The owner's answer: while a Chisel waits, a click on a block it would change ends at the Chisel. Fabric runs this
	// first on the client and on the server, and on the client a FAIL sends nothing and tries no other hand. Left to the
	// game, the click would go on to the off hand, and the client, whose copy of the wait ends a moment after the
	// server's, would let the server carve alone, with no sound or swing for the player.
	private static InteractionResult holdWhileWaiting(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.getCooldowns().getCooldownGroup(stack).equals(COOLDOWN_GROUP) || !player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		return RightClickChanges.change(player, level, hand, hit).isPresent() ? InteractionResult.FAIL : InteractionResult.PASS;
	}

	// A pressed button or plate stays as it is: see RightClickChanges.refused. A FAIL ends the click on both sides, with
	// no change, particles or sound.
	private static InteractionResult refuseWhilePowered(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return RightClickChanges.refused(player, level, hand, hit) ? InteractionResult.FAIL : InteractionResult.PASS;
	}

	// The sound a click plays beside its entry's own, which is all a transformer entry can name: a transformer plays one
	// sound, and an event picks one file of its own.
	public static Optional<Holder<SoundEvent>> extraSound(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		Holder<BlockTransformer> transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
		if (transformer == null || !transformer.is(TRANSFORMER) || player.getCooldowns().isOnCooldown(stack)) {
			return Optional.empty();
		}
		return RightClickChanges.change(player, level, hand, hit)
				.filter(Chisels::scrapesMoss)
				.map(change -> CARVE_STONE);
	}

	// The moss entry is the one that names this sound.
	static boolean scrapesMoss(BlockTransformer.BlockTransformData change) {
		return change.sound().value() == SCRAPE_MOSS.value();
	}

	// Scraping moss plays the carving sound with the moss one (the owner's choice), just before the change, as
	// CarvingParticles sends its particles. The game plays a transformer's sound with the player left out: the client has
	// played it for the player itself, and the server sends it to the others. This does the same, on both sides, so each
	// listener hears this one once too.
	private static InteractionResult carveWhileScraping(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		extraSound(player, level, hand, hit).ifPresent(sound -> level.playSound(player, hit.getBlockPos(), sound.value(), SoundSource.BLOCKS, 1.0F, 1.0F));
		return InteractionResult.PASS;
	}

	// The client finds a sound's definition by the event's own location, not by its registry key, so both are one id.
	private static Holder<SoundEvent> registerSound(String name) {
		Identifier id = Caramel.id(name);
		return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	private static Chisel create(String name, ToolMaterial material, Item hoe, float hoeAttackSpeed, UnaryOperator<Item.Properties> extra) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Caramel.id(name));
		Item item = new Item(extra.apply(properties(new Item.Properties(), material, hoeAttackSpeed)).setId(key));
		return new Chisel(Registry.register(BuiltInRegistries.ITEM, key, item), material, hoe);
	}

	// What vanilla's ToolMaterial.applyToolProperties sets for a pickaxe or an axe, with the owner's rules for the Chisel.
	private static Item.Properties properties(Item.Properties properties, ToolMaterial material, float hoeAttackSpeed) {
		HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
		float speed = miningSpeed(material);
		return properties.durability(material.durability())
				.repairable(material.repairItems())
				.enchantable(material.enchantmentValue())
				// Every block an axe or a pickaxe mines, with the tier's rule for which blocks drop. Any other block
				// breaks as by hand.
				.component(DataComponents.TOOL, new Tool(List.of(
						Tool.Rule.deniesDrops(blocks.getOrThrow(material.incorrectBlocksForDrops())),
						Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_AXE), speed),
						Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), speed)), 1.0F, 1, true))
				.attributes(ItemAttributeModifiers.builder()
						// Half a heart at every tier, as every hoe deals: the player's own 1 damage and nothing more.
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
						.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, hoeAttackSpeed + FASTER_THAN_THE_HOE, AttributeModifier.Operation.ADD_VALUE),
								EquipmentSlotGroup.MAINHAND)
						// Efficiency adds a flat bonus on top of the tool's speed. Halving that bonus too keeps the
						// Chisel at half the speed of the tier's axe and pickaxe at every Efficiency level.
						.add(Attributes.MINING_EFFICIENCY, new AttributeModifier(MINING_EFFICIENCY_ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND)
						.build())
				// 2 durability per attack like vanilla's tools, and no shield disabling.
				.component(DataComponents.WEAPON, new Weapon(2, 0.0F))
				// ItemStack.useOn starts the wait only after a right click changed a block, and while it runs the game
				// refuses the Chisel's right click. Mining and attacking do not wait.
				.component(DataComponents.USE_COOLDOWN, new UseCooldown(SECONDS_BETWEEN_CHANGES, Optional.of(COOLDOWN_GROUP)))
				// A client builds its item components from the registries the server sent, and a server without
				// Caramel has no such transformer. Left out there, the Chisel has no right click and the join goes on.
				.delayedComponent(DataComponents.BLOCK_TRANSFORMER, context -> context.get(TRANSFORMER).orElse(null));
	}

	// Half the tier's speed. The flint tier's half is 1.0, the bare hand's speed, and the game adds Efficiency only to
	// a speed above 1.0: the smallest step above it keeps the hand's pace and lets Efficiency count.
	private static float miningSpeed(ToolMaterial material) {
		return Math.max(material.speed() / 2.0F, Math.nextUp(1.0F));
	}
}
