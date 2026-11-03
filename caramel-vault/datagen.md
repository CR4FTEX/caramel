# Data generation

Caramel's JSON data is generated from Java into `src/main/generated/` by the providers registered in `CaramelDataGenerator`, and that output is committed. They write recipes and their unlock advancements ([[recipes]]), burn times and compost chances ([[fuel]]), villager trades ([[trades]]), for items, tags, block transformers, item definitions, models, names and sounds ([[tools]]), and for blocks, blockstates, models, loot tables, tags and names ([[blocks]]).

`CLAUDE.md` requires every other generated JSON, such as blockstates and loot tables, to come from datagen too. The rule for people is in `CONTRIBUTING.md`, "Where things go"; this note is how it works and what breaks. The behaviour below was reproduced in scratch copies on Loom 1.18.2 and Fabric API 0.161.0+26.3.

## How it runs

- `fabric.mod.json` registers `io.github.cr4ftex.caramel.client.datagen.CaramelDataGenerator` as the `fabric-datagen` entrypoint, and providers are added to the pack in its `onInitializeDataGenerator`. In this Fabric API version, registry-dependent providers such as `FabricLanguageProvider` and `FabricRecipeProvider` take a `FabricPackOutput` and a `CompletableFuture<HolderLookup.Provider>`, `FabricModelProvider` takes only a `FabricPackOutput`, and `Pack.addProvider` accepts both shapes (`javap` on the `fabric-data-generation-api-v1` jar).
- `configureDataGeneration { client = true }` in `build.gradle` makes the `datagen` run inherit Loom's `client` run config: client environment, `KnotClient`, and the client source set's classpath. That is what lets the generator live in `src/client` and use client-only classes. It creates no separate datagen source set. Sources: `FabricApiDataGeneration` in Loom 1.18.4 (`javap`), and the run configs printed in a scratch copy with `client` set each way.
- `./gradlew runDatagen` logs no window creation, works in `run/datagen`, and exits when the providers finish: Fabric API's datagen `MinecraftMixin` calls `System.exit(0)` inside Minecraft's constructor. The main entrypoint runs first; the log shows "Caramel is melting in." before "Running data generator for caramel", and a crash in `Caramel.onInitialize` fails `runDatagen` (reproduced with a thrown exception). Which broken mixins the run catches is in [[mixins]].
- `src/main/generated` is a main resource directory beside `src/main/resources` (see [[build]]), so generated files reach the mod jar through normal resource processing.

## What the item providers write

- `CaramelItemTagProvider` (a `FabricTagsProvider.ItemTagsProvider`) writes `data/caramel/tags/item/` and, for the vanilla tags it adds to, `data/minecraft/tags/item/`. `builder(tag).addTag(other)` works for a tag the same provider defines.
- `CaramelBlockTransformerProvider` is a `FabricCodecDataProvider` over `Registries.BLOCK_TRANSFORMER` with `BlockTransformer.DIRECT_CODEC`, like the burn time and compost providers over theirs. A provider's registry lookup holds vanilla's own entries of that registry; [[tools]], "Right-click block changes", says what this one takes from them.
- `CaramelModelProvider` is a `FabricModelProvider`, a client class, and `CaramelLanguageProvider` a `FabricLanguageProvider`. [[tools]], "Assets", lists their files and says where textures come from.
- `CaramelSoundsProvider` is a `FabricSoundsProvider`, also a client class, and writes `assets/caramel/sounds.json`; [[tools]], "Sounds", says what it holds.

## What a run does to src/main/generated

- `.cache/` holds one file per provider, whose first line ends with the mod name and the provider's `getName()`, such as `Caramel/Caramel burn times`, listing the hash of each file that provider wrote. `.gitignore` excludes it, so a fresh clone has none. Two providers can write into one registry folder, as the burn-time and compost providers both write `context_int_provider/`, when their names differ; each keeps its own cache file (`src/main/generated/.cache/`).
- A file is rewritten only when the provider's new output hash differs from the hash in `.cache`, or when the file is missing. The content on disk is not compared.
- Every file in `src/main/generated` that the current run did not produce is deleted: hand-placed files, and the output of providers no longer registered. Empty directories stay, and the jar packages them as empty directory entries.
- The sweep skips only the `.cache` files of the providers that ran, so a provider taken out of `CaramelDataGenerator` loses its cache file too, and the log's `removed stale` counts it: unregistering `CaramelSoundsProvider` logged `removed stale: 2`, for `sounds.json` and that file (`HashCache.purgeStaleAndWrite`, reproduced).

## Traps

- **A hand edit to a generated file survives on the machine that made it and vanishes elsewhere.** With `.cache` present and the provider unchanged, the edited file is left alone and the log reports `written: 0`. A clone without `.cache` rewrites it, and so does a provider change that alters that file's output. A change that alters only other files leaves it alone. Change the provider instead.
- **A hand-written file placed in `src/main/generated` is deleted by the next run, silently.** The log's `Caching:` line counts it under `removed stale: N`. Hand-written files belong in `src/main/resources`.
- **A file in both `src/main/generated/` and `src/main/resources/` fails the build.** `processResources` stops with "Entry caramel.mixins.json is a duplicate but no duplicate handling strategy has been set", reproduced by copying `caramel.mixins.json` into `src/main/generated/`.
- **A provider change committed without its regenerated files fails CI.** `.github/workflows/build.yml` runs `./gradlew runDatagen` after the build and fails on any difference in `src/main/generated` ([[ci]]). Locally, after `./gradlew runDatagen`, `git status src/main/generated` lists what must be committed.
