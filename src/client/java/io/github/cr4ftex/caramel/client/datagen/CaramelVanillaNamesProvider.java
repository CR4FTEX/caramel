package io.github.cr4ftex.caramel.client.datagen;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// New English names for vanilla's own blocks, in assets/minecraft/lang/en_us.json. The client merges each language
// file of a namespace pack by pack, from vanilla up, so a key here wins over vanilla's. In Caramel's own namespace the
// order would depend on how the client lists namespaces (ClientLanguage.loadFrom). The server reads every lang/en_us.json
// a mod ships, after vanilla's (Fabric's server LanguageMixin). Other languages keep vanilla's names.
public class CaramelVanillaNamesProvider extends FabricCodecDataProvider<Map<String, String>> {
	// C-010, the owner's names. The ids stay vanilla's.
	static final Map<Block, String> NAMES = Map.ofEntries(
			Map.entry(Blocks.COBBLESTONE, "Cobbled Stone"),
			Map.entry(Blocks.COBBLESTONE_STAIRS, "Cobbled Stone Stairs"),
			Map.entry(Blocks.COBBLESTONE_SLAB, "Cobbled Stone Slab"),
			Map.entry(Blocks.COBBLESTONE_WALL, "Cobbled Stone Wall"),
			Map.entry(Blocks.INFESTED_COBBLESTONE, "Infested Cobbled Stone"),
			Map.entry(Blocks.MOSSY_COBBLESTONE, "Cobbled Mossy Stone"),
			Map.entry(Blocks.MOSSY_COBBLESTONE_STAIRS, "Cobbled Mossy Stone Stairs"),
			Map.entry(Blocks.MOSSY_COBBLESTONE_SLAB, "Cobbled Mossy Stone Slab"),
			Map.entry(Blocks.MOSSY_COBBLESTONE_WALL, "Cobbled Mossy Stone Wall"));

	public CaramelVanillaNamesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, PackOutput.Target.RESOURCE_PACK, "lang", Codec.unboundedMap(Codec.STRING, Codec.STRING));
	}

	@Override
	protected void configure(BiConsumer<Identifier, Map<String, String>> provider, HolderLookup.Provider registries) {
		Map<String, String> translations = new TreeMap<>();
		NAMES.forEach((block, name) -> translations.put(block.getDescriptionId(), name));
		provider.accept(Identifier.withDefaultNamespace("en_us"), translations);
	}

	@Override
	public String getName() {
		return "Caramel names for vanilla blocks";
	}
}
