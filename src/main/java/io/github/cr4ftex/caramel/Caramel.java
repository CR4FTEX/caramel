package io.github.cr4ftex.caramel;

import io.github.cr4ftex.caramel.compost.CraftedCompost;
import io.github.cr4ftex.caramel.fuel.CraftedFuel;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Caramel implements ModInitializer {
	public static final String MOD_ID = "caramel";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Caramel is melting in.");
		CraftedFuel.register();
		CraftedCompost.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
