package io.github.cr4ftex.caramel.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CaramelDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(CaramelRecipeProvider::new);
		pack.addProvider(CaramelBurnTimeProvider::new);
		pack.addProvider(CaramelCompostProvider::new);
		pack.addProvider(CaramelTradeProvider::new);
		CaramelBlockTagProvider blockTags = pack.addProvider(CaramelBlockTagProvider::new);
		pack.addProvider((output, registries) -> new CaramelItemTagProvider(output, registries, blockTags));
		pack.addProvider(CaramelBlockLootProvider::new);
		pack.addProvider(CaramelBlockTransformerProvider::new);
		pack.addProvider(CaramelModelProvider::new);
		pack.addProvider(CaramelLanguageProvider::new);
		pack.addProvider(CaramelVanillaNamesProvider::new);
		pack.addProvider(CaramelSoundsProvider::new);
	}
}
