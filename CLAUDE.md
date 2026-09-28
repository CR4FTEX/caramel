# CLAUDE.md

Instructions for AI coding sessions in this repository.

## Project

Caramel (mod id `caramel`) is a Fabric mod for Minecraft Java Edition, owned by CR4FTEX. It has two goals: streamline and improve the game's existing concepts and features without steering too far from its core identity and gameplay loop, and bring content from the spin-off games (Minecraft Dungeons, Legends, Earth, Story Mode) to improve barren areas and neglected concepts, giving each dimension a fresh coat of paint and more incentives to explore and risk to be rewarded. The vision, principles and idea log are in `docs/DESIGN.md`. Read it before starting work.

## Working agreement

- The owner pitches ideas one at a time. You implement them; the owner playtests.
- Before writing code for a pitch, record it in the idea log in `docs/DESIGN.md` (next free C-### ID, status Pitched, faithful summary), and list its ID under each matching heading in "Target areas". Keep the entry's status, design notes and implementation notes current as you work.
- Only the owner marks an idea Done, after playtesting it.
- Ask the owner about genuine design decisions instead of guessing. Record the answers in the entry.
- Do not steer too far from the game's core identity and gameplay loop. The owner has the final say on their own ideas.
- Never build features the owner did not ask for. Suggesting them is welcome; implementing them is not.

## Toolchain

- Minecraft 26.3, Java 25 (JDK 25 required)
- Fabric Loader 0.19.5, Fabric API 0.161.0+26.3
- Fabric Loom 1.18-SNAPSHOT (plugin id `net.fabricmc.fabric-loom`), Gradle 9.7.1 via the wrapper (`./gradlew`)
- Versions are set in `gradle.properties`.
- Minecraft 26.x ships unobfuscated. Use Mojang's official names (e.g. `net.minecraft.resources.Identifier`). There is no mappings step.

## Commands

- `./gradlew build`: compile and package. The jar lands in `build/libs/` as `caramel-<mod_version>+<minecraft_version>.jar` (currently `0.1.0+26.3`).
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

- `caramel-vault/` is the knowledge vault: how the build, datagen, mixins, CI, licensing and the GitHub repository work, and the traps each one hides. Each trap names the error it produces, or says that it fails silently. Start at `caramel-vault/_map.md`, which names the one note that owns each topic, and read that note before changing its part of the project.
- When you learn something durable (a mechanism, a trap and the error it prevents, a decision and its reason), write it into its owning note and cite the file or experiment it came from. No dates in the vault (`caramel-vault/.vaultrc`); git keeps the when.
- Status never goes in the vault. Session state and next steps go in the passdown, `docs/SETUP_REPORT.md`. The state of each idea goes in its entry in `docs/DESIGN.md`.
- The owner runs `/gold` on the vault when they choose, usually mid-session. Do not start a gold pass yourself. After writing vault notes, lint them with the skill's `vault_lint.py` if it is installed (`python3 <skill folder>/vault_lint.py caramel-vault`); its last line must read `exit: 0`.

## Art

- You may make placeholder textures so a feature can be tested. Placeholders must be original too: never recolor or edit vanilla textures or art from any other game or mod. Keep them obviously temporary and list them in the entry's implementation notes so they get replaced.
- Final art comes from the owner or contributors.
- All art must be original. Never copy or rip textures, models or sounds from Minecraft Dungeons, Legends, Earth, Story Mode or any other game or mod. Recreating a concept in new, original art is fine.
- Licensing: code is MIT (`LICENSE`); art (textures, models, animations, sounds, the icon, and their editable source files, kept in `art-source/`) is CC BY-NC-SA 4.0 (`LICENSE-ASSETS`). Entity model and animation code goes in its own package, `io.github.cr4ftex.caramel.client.model` (`src/client/java/io/github/cr4ftex/caramel/client/model/`), which is covered by the art license, not MIT. Contributors keep their copyright and license contributions under the same terms. Caramel is a fan project, not affiliated with or endorsed by Mojang Studios or Microsoft.

## Repository

- Private GitHub repo `CR4FTEX/caramel`, branch `main`. Contributors are collaborators invited by the owner.
- Collaborators work on branches and open pull requests into `main`. The owner may commit to `main` directly. Only commit when the owner asks you to, and then commit straight to `main`.
- CI: `.github/workflows/build.yml` runs `./gradlew build` on JDK 25 for every pull request and every push to `main`, skips changes that only touch Markdown (`.md`) files, and uploads `build/libs/` as the `caramel-jar` artifact, which can be downloaded from each run for 14 days.
- Commits use the owner's GitHub noreply email, set in the repo's local git config (`git config user.email`). Never replace it with another email.

## Updating Minecraft

1. Look up matching `minecraft_version`, `loader_version`, `loom_version` and `fabric_api_version` at https://fabricmc.net/develop and update `gradle.properties`. If the new Loom needs a newer Gradle, run `./gradlew wrapper --gradle-version <version>`.
2. Update `depends` in `src/main/resources/fabric.mod.json` (`minecraft`, `fabricloader`). If the required Java version changes, also update `build.gradle` (`release`, source and target compatibility), `compatibilityLevel` in both mixin configs, `java` in `fabric.mod.json`, and `java-version` in `.github/workflows/build.yml`.
3. Expect mixins to break (injection targets renamed or moved). Fix them, then run the checks in "Before calling something done" and launch `./gradlew runClient`.
