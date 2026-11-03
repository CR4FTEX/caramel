# Contributing to Caramel

Thanks for helping. This guide is for both coders and artists. Artists do not need git, Java or IntelliJ: after reading [Getting access](#getting-access), read [docs/TEXTURES.md](docs/TEXTURES.md), which explains how to submit art through a GitHub issue or the GitHub website.

The owner, CR4FTEX, pitches ideas one at a time. [docs/DESIGN.md](docs/DESIGN.md) logs every idea the owner pitches or takes on. That file decides what gets built and how.

## Getting access

The repository, https://github.com/CR4FTEX/caramel, is public, and anyone can clone it. To send changes you need a GitHub account (a free one is enough). Fork the repository, push your branches to your fork, and open pull requests from it into `main`. CR4FTEX may invite regular contributors as collaborators, who push branches to this repository directly.

To clone, use your fork's address (or `https://github.com/CR4FTEX/caramel.git` to only build it):

```sh
git clone https://github.com/<your-username>/caramel.git
cd caramel
git remote add upstream https://github.com/CR4FTEX/caramel.git
```

`upstream` is the main repository: it is where you pull the latest `main` from, because your fork's own `main` does not move by itself. (If you cloned `CR4FTEX/caramel` directly, `upstream` and `origin` are the same repository, so the extra remote does no harm.)

To push, git has to be signed in to your GitHub account. Use an SSH key, the GitHub CLI (`gh auth login`, and choose HTTPS when asked), or Git Credential Manager (included with Git for Windows). With SSH, clone `git@github.com:<your-username>/caramel.git` instead.

## Development setup

You need:

- JDK 25. Any distribution works (CI uses the Microsoft Build of OpenJDK).
- Git.
- IntelliJ IDEA (the free version is enough) with the **Minecraft Development** plugin. Recommended, not required.

Versions are set in `gradle.properties` and the Gradle wrapper: Minecraft 26.3, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Fabric Loom 1.18-SNAPSHOT, Gradle 9.7.1. Do not install Gradle yourself; always use the wrapper. Minecraft 26.x ships unobfuscated, so the code uses Mojang's official names (for example `net.minecraft.resources.Identifier`) and there is no mappings step.

If you are new to Fabric modding, start with the official [Fabric documentation](https://docs.fabricmc.net/develop/).

In IntelliJ IDEA:

1. File > Open, select the project folder and trust the project. IntelliJ imports it as a Gradle project.
2. Set the project SDK to JDK 25 (File > Project Structure), and set Gradle JVM to JDK 25 (Settings > Build, Execution, Deployment > Build Tools > Gradle).
3. Wait for the Gradle sync to finish. The first sync downloads Minecraft and the dependencies, which takes a while.

Gradle tasks, run from the project root (on Windows, use `gradlew.bat` in place of `./gradlew`):

| Command | What it does |
| --- | --- |
| `./gradlew runClient` | Starts a dev client with Caramel and Fabric API loaded. Game files go in `run/`, which git ignores. |
| `./gradlew runServer` | Starts a dev dedicated server. The first start stops until you set `eula=true` in `run/eula.txt`. |
| `./gradlew runDatagen` | Regenerates the JSON files in `src/main/generated/`. |
| `./gradlew build` | Compiles and packages the mod, then runs the game tests. The jar lands in `build/libs/` (currently `caramel-0.1.0+26.3.jar`). |
| `./gradlew runGameTest` | Runs only the game tests: a Minecraft test server with no window loads the mod's data and checks it, then exits. |

## Where things go

```
src/main/java/io/github/cr4ftex/caramel/           Common code (runs on server and client)
    Caramel.java                                    Main entrypoint: MOD_ID, LOGGER, id()
    mixin/                                          Common mixins
src/main/resources/
    fabric.mod.json                                 Mod metadata and entrypoints
    caramel.mixins.json                             Common mixin config
    assets/caramel/                                 Textures, hand-made models, sounds, icon.png
    data/caramel/                                   Hand-written data files
src/main/generated/                                 Datagen output (do not edit by hand)
src/client/java/io/github/cr4ftex/caramel/client/  Client-only code (rendering, screens, key binds)
    CaramelClient.java                              Client entrypoint
    datagen/CaramelDataGenerator.java               Datagen entrypoint, registers the providers
    mixin/                                          Client mixins
    model/                                          Entity model and animation code (art license, see below)
src/client/resources/
    caramel.client.mixins.json                      Client mixin config
src/gametest/                                       Game tests, a separate mod (caramel-gametest) that never ships in the jar
art-source/                                         Editable source art (.bbmodel, .aseprite, layered images), never shipped
caramel-vault/                                      How the build and tooling work, and their traps (start at _map.md)
```

- `src/main` must work on a dedicated server, so it never references client classes, Fabric API's included. The compiler catches only some of them (see `caramel-vault/build.md`). Anything that draws, plays on screen or reads input goes in `src/client`.
- Put new code in subpackages of `io.github.cr4ftex.caramel` (or `io.github.cr4ftex.caramel.client`), grouped by type or feature, not all in the root package.
- Models, blockstates, recipes, loot tables, tags and translations are generated by datagen into `src/main/generated/`. To change them, edit the Java providers in `src/client/java/io/github/cr4ftex/caramel/client/datagen/` (registered in `CaramelDataGenerator`), run `./gradlew runDatagen`, and commit both the Java change and the regenerated files. Do not edit or add files in `src/main/generated/` by hand: a later run, or someone else's fresh clone, overwrites or deletes them (how: `caramel-vault/datagen.md`). CI does not run datagen, so generated files you forget to commit are missing from the jar.
- Only put hand-written files in `assets/caramel/` and `data/caramel/` when datagen cannot produce them (textures, sounds, custom Blockbench models). Never keep the same file in both `src/main/resources/` and `src/main/generated/`.

## Code conventions

- Tabs for indentation in Java, JSON and Gradle files. The `.editorconfig` file sets this up, and IntelliJ reads it automatically.
- Build every identifier in Caramel's namespace with `Caramel.id("name")`. Do not hard-code the `"caramel"` string; use `Caramel.MOD_ID` if you need the namespace itself.
- Registry names (blocks, items, entities, sounds and so on) are lowercase snake_case: `Caramel.id("example_block")`, not `ExampleBlock` or `example-block`.
- Log with `Caramel.LOGGER`.
- In order of preference: data-driven JSON generated by datagen, then Fabric API registries and events, then mixins as a last resort.

### Mixins

- Before writing one, check whether Fabric API has an event, callback or registry that does the job. If you are unsure, ask in the issue.
- Keep each mixin small and targeted: inject at the narrowest point and change as little vanilla behavior as possible.
- One mixin class per target class, named after the target (a mixin into `LivingEntity` is `LivingEntityMixin`).
- Prefer `@Inject` and the MixinExtras annotations that ship with Fabric Loader (`@WrapOperation`, `@ModifyExpressionValue`, `@ModifyReturnValue`) over `@Redirect`. Do not use `@Overwrite`.
- Mark fields and methods that a mixin adds with `@Unique` and prefix their names with `caramel$`, so they cannot clash with other mods.
- Common mixins go in package `io.github.cr4ftex.caramel.mixin` and are listed in the `mixins` array of `src/main/resources/caramel.mixins.json`. Client mixins go in `io.github.cr4ftex.caramel.client.mixin` and are listed in the `client` array of `src/client/resources/caramel.client.mixins.json`.
- Both configs set `"defaultRequire": 1`. Leave it that way: it is what catches broken mixins after a Minecraft update. What it does, and when a broken mixin surfaces, is in `caramel-vault/mixins.md`.
- Add a comment explaining why the mixin is needed, and note it in the idea's entry in `docs/DESIGN.md`.

## Picking up work

- Work is tracked in GitHub issues: https://github.com/CR4FTEX/caramel/issues. Use the issue templates (Idea / feature, Bug report, Art / texture).
- Comment on an issue before you start, so two people do not work on the same thing.
- Read the matching entry in [docs/DESIGN.md](docs/DESIGN.md) first and build what it describes. Only pick up an idea whose status is **Planned**; a **Pitched** idea is recorded but not yet agreed on. If something is unclear, or you think a different approach would be better, ask in the issue instead of deciding alone.
- To suggest an idea of your own, open an Idea / feature issue. It is only a suggestion until the owner takes it on and records it in `docs/DESIGN.md`, and it can be built once its status is **Planned**.

## Git workflow

1. Start from an up-to-date `main`: `git switch main`, then `git pull upstream main`.
2. Create one branch per feature or fix: `git switch -c feature/short-name` (or `fix/...`, `art/...`).
3. Make your changes. Test them in `./gradlew runClient`, and in `./gradlew runServer` if you changed common code. Run `./gradlew build` before pushing.
4. Push the branch to your fork (or to this repository, if you are a collaborator): `git push -u origin feature/short-name`.
5. Open a pull request into `main` and fill in the template: what changed, how you tested it, screenshots for anything visual, and the related issue (`Closes #12`).
6. The `build` workflow (`.github/workflows/build.yml`) runs `./gradlew build` on every pull request and on every push to `main`, and it must pass. A push to `main` that only touches Markdown (`.md`) files does not start it; a pull request always does. Each run uploads the built jars and keeps them for 14 days: open the run in the repository's Actions tab and download `caramel-jar` under Artifacts. It is a `.zip` with two jars. Install the mod jar (currently `caramel-0.1.0+26.3.jar`), not the one ending in `-sources.jar`.
7. CR4FTEX reviews, playtests and merges. Collaborators always go through a branch and a pull request: do not push to `main` directly and do not merge your own pull request. Only the owner may commit to `main` directly.

If `main` moves on while your pull request is open, bring your branch up to date with `git pull --no-rebase --no-edit upstream main`, then `git push`.

## Commit messages

- A short summary line in the imperative mood, about 50 characters and at most 72, with no period at the end. For example: `Add block model provider to datagen`, `Fix client class loaded on server`.
- If the change needs explaining, add a blank line and then a short body that says why.
- One logical change per commit.

## Licensing of contributions

- Code is licensed under the MIT License, Copyright (c) 2026 CR4FTEX. See [LICENSE](LICENSE).
- Art assets (textures, models, animations, sounds and the icon under `src/main/resources/assets/caramel/`, plus their editable source files in `art-source/`) are licensed under CC BY-NC-SA 4.0. See [LICENSE-ASSETS](LICENSE-ASSETS).
- Entity model and animation code is kept in its own folder, `src/client/java/io/github/cr4ftex/caramel/client/model/`, and is covered by the art license (CC BY-NC-SA 4.0), not the MIT License.
- You keep the copyright to what you contribute. By submitting a contribution (a pull request, a commit, or a file attached to an issue), you agree to license it under these terms, and you confirm that it is your own work, or bought or commissioned art that meets the license rule in [docs/TEXTURES.md](docs/TEXTURES.md), and that you have the right to submit it.
- All art must be original. Never copy or rip textures, models or sounds from another game or mod, including the Minecraft spin-offs. Never use AI to make art: Caramel ships no AI-generated assets. Bought or commissioned art ships only when it can ship under CC BY-NC-SA 4.0. See [docs/TEXTURES.md](docs/TEXTURES.md) for the full art rules.
