package io.github.cr4ftex.caramel.client.datagen;

import java.util.concurrent.CompletableFuture;

import io.github.cr4ftex.caramel.item.Chisels;

import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;

// What each of Caramel's sound events plays, in assets/caramel/sounds.json.
public class CaramelSoundsProvider extends FabricSoundsProvider {
	// C-009, the owner's rules: the anvil's sound at 1.5 times its pitch and half its volume, with a subtitle of its own,
	// until the owner's commissioned sound replaces it.
	static final float CARVE_STONE_PITCH = 1.5F;
	static final float CARVE_STONE_VOLUME = 0.5F;
	static final String CARVE_STONE_SUBTITLE = "subtitles.caramel.item.chisel.carve_stone";

	public CaramelSoundsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, SoundExporter exporter) {
		// Borrowed by event, not by file, so it plays whatever a resource pack makes the anvil play. The client multiplies
		// this pitch and volume by the anvil's own. CaramelLanguageProvider writes the subtitle's text.
		exporter.add(Chisels.CARVE_STONE, SoundTypeBuilder.of()
				.subtitle(CARVE_STONE_SUBTITLE)
				.sound(SoundTypeBuilder.RegistrationBuilder.ofEvent(SoundEvents.ANVIL_USE).pitch(CARVE_STONE_PITCH).volume(CARVE_STONE_VOLUME)));
	}

	@Override
	public String getName() {
		return "Caramel sounds";
	}
}
