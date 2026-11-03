# CLAUDE.md

Instructions for AI coding sessions in this repository.

## Project

Caramel (mod id `caramel`) is a Fabric mod for Minecraft Java Edition, owned by CR4FTEX: in the owner's words, "The Minecraft Mod that sticks everything and everyone together, to build atop Vanilla". It polishes neglected features, takes Bedrock parity further than Mojang's own tweaks, and has five goals: better crafting recipes, work blocks and block families, and new blocks, items and tools, so building costs less of the player's resources and time; connect structures, mobs and lore to the player's progress so each is worth seeking out; give every biome of every dimension the same share of quality; bring the world up to the spin-offs' world-building, audio and visual design, and bring in what was left out (the spin-offs Minecraft Dungeons, Legends, Earth and Story Mode, the options that lost the yearly votes, the biomes of the concept art); and add ideas of its own, scrapped content brought back included. All of it without steering too far from the game's core identity and gameplay loop. It is "a Mod first", "an Expansion second" and "an Infrastructure third"; the infrastructure (switching features off, compatibility with other mods) is a long-term goal. The vision, principles and idea log are in `docs/DESIGN.md`. Read it before starting work.

## Working agreement

- The owner pitches ideas one at a time. You implement them; the owner playtests.
- Before writing code for a pitch, record it in the idea log in `docs/DESIGN.md` (next free C-### ID, status Pitched, faithful summary), and list its ID under each matching heading in "Target areas". Keep the entry's status, design notes and implementation notes current as you work.
- Only the owner marks an idea Done, after playtesting it.
- Ask the owner about genuine design decisions instead of guessing. Record the answers in the entry.
- Do not steer too far from the game's core identity and gameplay loop. The owner has the final say on their own ideas.
- Never build features the owner did not ask for. Suggesting them is welcome; implementing them is not.
- Vanilla first: compatibility with other mods is not a priority yet (`docs/DESIGN.md`, "Priorities"). Scope changes to the base game's content, and don't build for other mods unless the owner asks.

## If you are not the owner

This file is written for the owner's sessions. If the person you are working for is not CR4FTEX (they work from a fork, or `git remote -v` and the commit identity are not the owner's; when unsure, ask), they are a contributor, and this holds for you:

- Their idea is a suggestion, not a pitch. Do not add a C-### entry to `docs/DESIGN.md`, list it under "Target areas" or mark anything Done: the owner takes an idea on first (`CONTRIBUTING.md`, "How ideas work"). Build only what an idea with status **Planned** describes, or what a bug fix needs.
- Work on a branch of their fork and open a pull request into `main`. Never commit to `main`, and commit only when they ask you to.
- In place of updating an idea's entry, put what changed and what to playtest in the pull request description.
- Everything else here still applies: the art rules, the conventions, and the checks in "Before calling something done".

## Toolchain

- Minecraft 26.3, Java 25 (JDK 25 required)
- Fabric Loader 0.19.5, Fabric API 0.161.0+26.3
- Fabric Loom 1.18-SNAPSHOT (plugin id `net.fabricmc.fabric-loom`), Gradle 9.7.1 via the wrapper (`./gradlew`)
- Versions are set in `gradle.properties`.
- Minecraft 26.x ships unobfuscated. Use Mojang's official names (e.g. `net.minecraft.resources.Identifier`). There is no mappings step.

## Commands

- `./gradlew build`: compile, package, and run the server game tests. The jar lands in `build/libs/` as `caramel-<mod_version>+<minecraft_version>.jar` (currently `0.1.0+26.3`).
- `./gradlew runGameTest`: run only the server game tests in `src/gametest` (a separate mod, `caramel-gametest`, that never ships in the jar). Read the expected values from the vanilla data pack, never from Caramel's own output. Never set `eula = true` or turn on client game tests in `build.gradle`: that is how Loom would accept the Minecraft EULA on the owner's behalf.
- `./gradlew runDatagen`: regenerate JSON into `src/main/generated/`.
- `./gradlew runClient`: start a dev client.
- `./gradlew runServer`: start a dev server.

## Layout and conventions

- Split source sets. `src/main` is common code (runs on server and client). `src/client` is client-only: rendering, screens, key binds, and the data generator. Common code must never reference client classes.
- Java package `io.github.cr4ftex.caramel`. Entrypoints: `io.github.cr4ftex.caramel.Caramel` (main), `io.github.cr4ftex.caramel.client.CaramelClient` (client), `io.github.cr4ftex.caramel.client.datagen.CaramelDataGenerator` (datagen).
- Build identifiers with `Caramel.id("path")` (never hard-code the namespace; use `Caramel.MOD_ID` if you need it). Log with `Caramel.LOGGER`.
- Assets go in `src/main/resources/assets/caramel/`, data in `src/main/resources/data/caramel/`.
- Generate all JSON (models, blockstates, recipes, loot tables, tags, translations) with datagen providers registered in `CaramelDataGenerator`. The exception is JSON that datagen cannot produce, such as custom Blockbench models: those are hand-made and go in `src/main/resources/assets/caramel/`. The output in `src/main/generated/` is committed. Never hand-edit it: change the provider and rerun `./gradlew runDatagen`.
- Prefer Fabric API events and registries and data-driven JSON over mixins. Use a mixin only when there is no other way, and keep it small and targeted. Common mixins go in package `io.github.cr4ftex.caramel.mixin` and are listed in `src/main/resources/caramel.mixins.json`; client mixins go in `io.github.cr4ftex.caramel.client.mixin` and are listed in the `client` array of `src/client/resources/caramel.client.mixins.json`. Note why the mixin was needed in the idea's entry.
- Tabs for indentation in Java, JSON and Gradle files.

## Before calling something done

1. `./gradlew build` passes.
2. Run `./gradlew runDatagen`, then check `git diff` and `git status`. The only changes under `src/main/generated/` should be ones you expect.
3. Update the idea's entry in `docs/DESIGN.md` and tell the owner what to playtest.

## Vault and status

- `caramel-vault/` is the knowledge vault: how Caramel's build and tooling work, and the traps each part hides. Each trap names the error it produces, or says that it fails silently. Start at `caramel-vault/_map.md`, which names the one note that owns each topic, and read that note before changing its part of the project.
- When you learn something durable (a mechanism, a trap and the error it prevents, a decision and its reason), write it into its owning note and cite the file or experiment it came from. No dates in the vault (`caramel-vault/.vaultrc`); git keeps the when.
- Status never goes in the vault. Session state and next steps go in the passdown, `docs/SETUP_REPORT.md`. The state of each idea goes in its entry in `docs/DESIGN.md`.
- The owner runs `/gold` on the vault when they choose, usually mid-session. Do not start a gold pass yourself. After writing vault notes, lint them with the skill's `vault_lint.py` if it is installed (`python3 <skill folder>/vault_lint.py caramel-vault`); its last line must read `exit: 0`.

## Art

- Never make art: no textures, custom (Blockbench) models, animations, sounds or icons, placeholders included. The owner's rule is that Caramel never generates or ships AI-generated assets. When a block or item needs art that does not exist yet, leave the art out: the game draws its missing-texture checkerboard instead, which is enough to playtest. An entity has no such fallback: the client looks up its renderer with no default (`EntityRenderDispatcher.getRenderer`), so ask the owner before building a mob whose model does not exist yet. List the art the feature still needs in the entry's implementation notes. Writing datagen providers for standard block and item models and blockstates (a vanilla parent model and a texture reference, `docs/TEXTURES.md`, "Where files go") is not making art, and neither is turning an artist's Blockbench model or animation into model or animation code.
- Art comes from the owner or contributors, or is bought from or commissioned from artists. Bought or commissioned art ships only when its license, or the artist's written agreement, lets it ship under CC BY-NC-SA 4.0 like all other Caramel art.
- All art must be original. Never copy or rip textures, models or sounds from Minecraft Dungeons, Legends, Earth, Story Mode or any other game or mod. Recreating a concept in new, original art is fine.
- Licensing: code is MIT (`LICENSE`); art (textures, models, animations, sounds, the icon, and their editable source files, kept in `art-source/`) is CC BY-NC-SA 4.0 (`LICENSE-ASSETS`). Entity model and animation code goes in its own package, `io.github.cr4ftex.caramel.client.model` (`src/client/java/io/github/cr4ftex/caramel/client/model/`), which is covered by the art license, not MIT. Contributors keep their copyright and license contributions under the same terms. Caramel is a fan project, not affiliated with or endorsed by Mojang Studios or Microsoft.

## Repository

- Public GitHub repo `CR4FTEX/caramel`, branch `main`. Contributors open pull requests from forks; the owner may invite regular ones as collaborators.
- Contributors work on branches and open pull requests into `main`. The owner may commit to `main` directly. Only commit when the owner asks you to, and then commit straight to `main`.
- CI: `.github/workflows/build.yml` must pass on every pull request and push to `main`. When it runs, what it caches and what it uploads is in `caramel-vault/ci.md`.
- Commits use the owner's GitHub noreply email, set in the repo's local git config (`git config user.email`). Never replace it with another email.

## Updating Minecraft

1. Look up matching `minecraft_version`, `loader_version`, `loom_version` and `fabric_api_version` at https://fabricmc.net/develop and update `gradle.properties`. If the new Loom needs a newer Gradle, run `./gradlew wrapper --gradle-version <version>`.
2. Update `depends` in `src/main/resources/fabric.mod.json` (`minecraft`, `fabricloader`). If the required Java version changes, also update `build.gradle` (`release`, source and target compatibility), `compatibilityLevel` in both mixin configs, `java` in `fabric.mod.json`, and `java-version` in `.github/workflows/build.yml`.
3. Expect mixins to break (injection targets renamed or moved). Fix them, then run the checks in "Before calling something done" and launch `./gradlew runClient`.
