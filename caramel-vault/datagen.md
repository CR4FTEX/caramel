# Data generation

Models, blockstates, recipes, loot tables, tags and translations are generated from Java into `src/main/generated/`, and that output is committed. The rule for people is in `CONTRIBUTING.md`, "Where things go"; this note is how it works and what breaks. The behaviour below was reproduced in scratch copies of the project on Loom 1.18.2 and Fabric API 0.161.0+26.3.

## How it runs

- `fabric.mod.json` registers `io.github.cr4ftex.caramel.client.datagen.CaramelDataGenerator` as the `fabric-datagen` entrypoint, and providers are added to the pack in its `onInitializeDataGenerator`. In this Fabric API version, registry-dependent providers such as `FabricLanguageProvider` and `FabricRecipeProvider` take a `FabricPackOutput` and a `CompletableFuture<HolderLookup.Provider>`, `FabricModelProvider` takes only a `FabricPackOutput`, and `Pack.addProvider` accepts both shapes (`javap` on the `fabric-data-generation-api-v1` jar).
- `configureDataGeneration { client = true }` in `build.gradle` makes the `datagen` run inherit Loom's `client` run config: client environment, `KnotClient`, and the client source set's classpath. That is what lets the generator live in `src/client` and use client-only classes. It creates no separate datagen source set. Sources: the javadoc of `DataGenerationSettings.getClient()` and `FabricApiDataGeneration` in Loom 1.18.2, and the run configs printed with `client` set each way.
- `./gradlew runDatagen` opens no window, works in `run/datagen`, and exits when the providers finish. The main entrypoint runs first: the log shows "Caramel is melting in." before "Running data generator for caramel". A crash in `Caramel.onInitialize` therefore surfaces here too, and so does a broken mixin whose target class loads during the run, such as one into `Bootstrap` (see [[mixins]]).
- `src/main/generated` is a main resource directory ahead of `src/main/resources` (see [[build]]), so generated files reach the mod jar through normal resource processing. Loom excludes `.cache/**` from the `jar` task.

## What a run does to src/main/generated

- `.cache/` holds one file per provider, listing the hash of each file that provider wrote. `.gitignore` excludes it, so a fresh clone has none.
- A file is rewritten only when the provider's new output hash differs from the hash in `.cache`, or when the file is missing. The content on disk is not compared.
- Every file in `src/main/generated` that the current run did not produce is deleted: hand-placed files, and the output of providers no longer registered. Empty directories stay, and the jar packages them as empty directory entries.

## Traps

- **A hand edit to a generated file survives on the machine that made it and vanishes elsewhere.** With `.cache` present and the provider unchanged, the edited file is left alone and the log reports `written: 0`. A clone without `.cache` rewrites it, and so does a provider change that alters that file's output. A change that alters only other files leaves it alone. Change the provider instead.
- **A hand-written file placed in `src/main/generated` is deleted by the next run.** Hand-written files belong in `src/main/resources`.
- **A file in both `src/main/generated/` and `src/main/resources/` fails the build.** `processResources` stops with "Entry assets/caramel/icon.png is a duplicate but no duplicate handling strategy has been set", reproduced by copying `icon.png` into `src/main/generated/assets/caramel/`.
- **CI does not run datagen** (`.github/workflows/build.yml` runs only `./gradlew build`). A provider change committed without its regenerated files builds green and ships a jar without that data. After `./gradlew runDatagen`, `git status src/main/generated` lists what must be committed.
- **The `-sources` jar carries `.cache/`.** Loom's exclude applies to the `jar` task only; with a provider registered, `unzip -l` on the sources jar lists `.cache/` entries.
