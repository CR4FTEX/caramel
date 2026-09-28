# Game tests

Caramel's server game tests start a headless Minecraft test server with every data pack loaded, run the tests, and exit. The tests live in `src/gametest/java/io/github/cr4ftex/caramel/gametest/`, as a separate mod `caramel-gametest` (`src/gametest/resources/fabric.mod.json`). Checked with Loom 1.18.2 and Fabric API 0.161.

## How they run

- `configureTests` in `build.gradle` creates the `gametest` source set, the `gameTest` run config and the `runGameTest` task, and makes `check` depend on it. So `./gradlew build`, and CI, run every game test; `./gradlew runGameTest` runs them alone.
- The run inherits the `server` run config, adds `-Dfabric-api.gametest`, and works in `build/run/gameTest`, which is not cleared between runs.
- Tests are found through the `fabric-gametest` entrypoint: public, non-static, `void` methods annotated `@net.fabricmc.fabric.api.gametest.v1.GameTest` taking one `GameTestHelper`.
- The exit code is 0 when all pass, the number of failed tests otherwise, and 255 when the data packs fail to load ("Failed to load vanilla datapack, bit oops"); all three were observed in `runGameTest` runs. A data-load failure fails the build too.
- The test classes stay out of the mod jar (`unzip -l` on it lists none).
- The test mod can register test-only content through its own `main` entrypoint: `OtherModStairs` registers a stand-in modded stairs. It exists only in game tests: `runClient`'s classpath holds the main and client source sets and not `gametest` (printed from the `runClient` task).
- Fabric derives a test's id from its class and method names, splitting on capitals: `threeBlocksInAnLMakeFourStairs` becomes `stairs_recipe_game_test_three_blocks_in_an_lmake_four_stairs`.

## The EULA

`net.minecraft.server.Main` still reads `eula.txt` and writes `eula=false` into `build/run/gameTest/eula.txt`. Fabric's gametest `MainMixin` makes the check pass while `-Dfabric-api.gametest` is set; nothing accepts the EULA. The owner chose this path for C-001. Loom's `eula = true` setting and client game tests with a dedicated server would write `eula=true`, so `configureTests` keeps `eula = false` and `enableClientGameTests = false`.

## Traps

- **The `gametest` source set compiles against the client classes.** Loom hard-codes the client dependency, so a server test that uses a client class compiles and fails only at run time. The split-source-set guard in [[build]] does not cover it.
- **A test must read its expected values from the `vanilla` pack, not from the merged data**, or it checks Caramel against itself. `StairsRecipeGameTest` opens `server.getPackRepository().getPack("vanilla")` and parses the vanilla recipe files.
- **A fresh run directory logs an ERROR** "Failed to load properties from file: server.properties" with a `NoSuchFileException`, and a WARN "Failed to load eula.txt". Both are first-run noise; CI always shows them.
- **`GameTestHelper.assertionException(String, Object...)` treats its string as a translation key**, not a format string. Build messages with `Component.literal`.
