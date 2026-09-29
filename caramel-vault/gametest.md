# Game tests

Caramel's server game tests start a headless Minecraft test server with every data pack loaded, run the tests, and exit. The tests live in `src/gametest/java/io/github/cr4ftex/caramel/gametest/`, as a separate mod `caramel-gametest` (`src/gametest/resources/fabric.mod.json`). Checked with Loom 1.18.2 and Fabric API 0.161.

## How they run

- `configureTests` in `build.gradle` creates the `gametest` source set, the `gameTest` run config and the `runGameTest` task, and makes `check` depend on it. So `./gradlew build`, and CI, run every game test; `./gradlew runGameTest` runs them alone.
- `runGameTest` never counts as up to date: a second `./gradlew build` of an unchanged tree starts the test server again and runs every test ("2 executed, 10 up-to-date", with `:runGameTest` among the executed; scratch copy).
- The run inherits the `server` run config, adds `-Dfabric-api.gametest`, and works in `build/run/gameTest`, which is not cleared between runs.
- Tests are found through the `fabric-gametest` entrypoint: public, non-static, `void` methods annotated `@net.fabricmc.fabric.api.gametest.v1.GameTest` taking one `GameTestHelper`.
- Fabric names a test `<mod id>:` followed by its class's simple name and method name joined by `_`. It inserts `_` only between a lowercase letter and a capital, then lowercases the result (`TestAnnotationLocator$TestMethod.camelToSnake`): `threeBlocksInAnLMakeFourStairs` becomes `caramel-gametest:stairs_recipe_game_test_three_blocks_in_an_lmake_four_stairs`.
- The exit code is 0 when all pass, the number of failed required tests otherwise (`MultipleTestTracker.getFailedRequiredCount`), 255 when the data packs fail to load (WARN "Failed to load vanilla datapack, bit oops") or a filter matches nothing, and 1 when the server crashes (`GameTestServer.onServerCrash`). Any non-zero exit fails the build.
- Fabric's runner reads three more system properties (`FabricGameTestRunner.runHeadlessServer`, fabric-gametest-api-v1 4.0.32). `fabric-api.gametest.filter` runs only the tests whose ids match a pattern, `fabric-api.gametest.report-file` writes a JUnit-style XML report to that path, and `fabric-api.gametest.verify` runs each matching test several times. They go on the run in `build.gradle`: `loom { runs { named("gameTest") { property("fabric-api.gametest.filter", "caramel-gametest:*fuel*") } } }` logged "Will run tests matching caramel-gametest:*fuel* (2 tests)" (scratch copy).
- The test classes stay out of the mod jar (`unzip -l` on it lists none).
- The test mod can register test-only content through its own `main` entrypoint: `OtherModStairs` registers a stand-in modded stairs. It exists only in game tests: `runClient`'s classpath holds main and client and not `gametest` (printed from the `runClient` task).

## The EULA

`net.minecraft.server.Main` still reads `eula.txt` and writes `eula=false` into `build/run/gameTest/eula.txt`. Fabric's gametest `MainMixin` makes the check pass while `-Dfabric-api.gametest` is set; nothing accepts the EULA. The owner chose this path, as recorded in C-001 in `docs/DESIGN.md`.

`configureTests` keeps `eula = false` and `enableClientGameTests = false`. With client game tests on, Loom's `eula = true` registers `acceptGameTestEula`, which writes `eula=true` into `build/run/clientGameTest/eula.txt` (`FabricApiTesting.configureTests`, Loom 1.18.2).

## Expected values

A test that takes its expected values from the merged data passes silently, because it checks Caramel against itself. Vanilla's own values have two sources:

- Data: `StairsRecipeGameTest` opens `server.getPackRepository().getPack("vanilla")` and parses the vanilla recipe files.
- Item components: `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(...)` returns vanilla's defaults, which Fabric's modify event never touches (see [[fuel]]).

## Traps

- **The `gametest` source set compiles and runs against the client classes, silently.** Loom hard-codes the client dependency (`FabricApiAbstractSourceSet.configureSourceSet`), and `runGameTest`'s classpath holds `minecraft-clientOnly` and the client output. A server test that calls `net.minecraft.client.Minecraft.getInstance()` compiles, gets `null` and passes (scratch copy). The split-source-set guard in [[build]] does not cover it.
- **A test filter with no namespace matches nothing and exits 255.** The filter is a `/test`-style selector, and one with no colon gets `minecraft:` prepended (`ResourceSelectorArgument`), so `*fuel*` matches no Caramel test. The server logs WARN "Test selection matcher (*fuel*) found no tests" and exits 255, the same code as a data-pack load failure (scratch copy). Write `caramel-gametest:*fuel*`.
- **A fresh run directory logs an ERROR** "Failed to load properties from file: server.properties" with a `NoSuchFileException`, and a WARN "Failed to load eula.txt". Both are first-run noise. A CI checkout has no `build/run/gameTest`, so CI runs show them (CI run log).
- **`GameTestHelper.assertionException(String, Object...)` garbles its message silently.** It passes the string to `Component.translatableEscape` as a translation key: arguments fill only `%s` placeholders, and a stray `%` drops them all. "PROBE2 {} and %s" with "A", "B" printed "PROBE2 {} and A" (scratch copy). Build messages with `Component.literal`.
