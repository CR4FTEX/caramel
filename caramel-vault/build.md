# Build

How `./gradlew build` turns the `main` and `client` source sets into one mod jar, and runs the game tests on the way. The commands and IDE setup for people are in `CONTRIBUTING.md`; this note is the mechanism under them.

## Toolchain

- Minecraft, Fabric Loader, Loom, Fabric API and the mod version are set in `gradle.properties`. Gradle's own version is the `distributionUrl` in `gradle/wrapper/gradle-wrapper.properties`.
- The Loom plugin id is `net.fabricmc.fabric-loom` (`build.gradle`). It is requested as a snapshot (`loom_version` in `gradle.properties`), so the Loom that runs can change with no file changing. The configure log names the one that ran on its `Fabric Loom:` line; when a build breaks with no local change, compare that line first.
- `build.gradle` declares `minecraft` with no `mappings` line: Minecraft 26.x ships unobfuscated, with Mojang's names, and Fabric Loader logs "Mappings not present!" at startup (`runGameTest` log).
- The Java version is declared in several files that must move together. `CLAUDE.md`, "Updating Minecraft", lists them.

## Source sets

- `build.gradle` calls `splitEnvironmentSourceSets()`. `src/main` is common code. Of the Minecraft jars, it compiles against Loom's `minecraft-common` only; `src/client` also gets `minecraft-clientOnly` and main's output. Fabric Loader, Fabric API and the libraries are on both (printed compile classpaths).
- The guard covers Minecraft's client classes. A class from `net.minecraft.client` used in `src/main` fails `compileJava` with "package net.minecraft.client does not exist".
- Fabric API is not split, so its client classes are on `src/main`'s classpath. Code that names no Minecraft client class compiles: `return ClientTickEvents.END_CLIENT_TICK;` builds. Code that does name one fails `compileJava` with "cannot access Minecraft" and "class file for net.minecraft.client.Minecraft not found": `ClientTickEvents.END_CLIENT_TICK.register(client -> {})`, whose lambda takes a `Minecraft`. All three were reproduced in a scratch copy.
- `loom.mods.caramel` lists main and client, and both compile into the one mod jar: `unzip -l` on it shows `io/github/cr4ftex/caramel/client/` beside the common classes. A dedicated server never runs the client code because it only starts the `main` entrypoint and skips the mixin config marked `"environment": "client"` (`fabric.mod.json`).
- `configureTests` in `build.gradle` adds a third source set, `gametest`, which compiles against main and client, runs in every build, and never reaches the jar. [[gametest]] owns it.

## Run tasks

- Loom registers `runClient`, `runServer`, `runDatagen` and `runGameTest` from `loom.runConfigs` (`client`, `server`, `datagen`, `gameTest`), plus `runClientRenderDoc`, a separate RenderDoc task (`./gradlew tasks --all`). How `./gradlew build` runs the game tests is in [[gametest]].
- `runServer`'s classpath has main's output and no client output, and its only Minecraft jar is `minecraft-common`. `runDatagen`'s classpath is the same as `runClient`'s; [[datagen]] says why.
- Game files go in `run/` for `runClient` and `runServer`, in `run/datagen` for `runDatagen`, and in `build/run/gameTest` for `runGameTest`. `.gitignore` excludes `run/` and `build/`.
- **On Linux, `runClient` logs "Error while loading the narrator" and runs on.** `com.mojang.text2speech.NarratorLinux` loads `libflite.so` from the system through JNA, and `text2speech-1.19.12.jar` bundles none (`unzip -l`), so the log shows a long `UnsatisfiedLinkError` for `libflite.so`. `Narrator.getNarrator` then returns `Narrator.EMPTY`, which does nothing (`run/logs/latest.log`, `javap`). Loom's dev log config, `.gradle/loom-cache/log4j.xml`, hides this ERROR from the console only when the system property `fabric.loom.suppressNarratorError` is set (a `SystemPropertyArbiter` around its filter).
- **`runClient` logs three ERRORs from Mojang's services to `run/logs/latest.log` and runs on:** "Failed to fetch user properties" (`InvalidCredentialsException: Status: 401`), "Failed to fetch Realms feature flags" and "Couldn't connect to realms" ("Failed to parse into SignedJWT: FabricMC"). Loom launches with no login, and Fabric Loader fills in `--accessToken FabricMC` (`MinecraftGameProvider.processArgumentMap`, Loader 0.19.5), which the services reject. The console never shows the three: `.gradle/loom-cache/log4j.xml` denies each message on its `SysOut` appender, and the terminal shows only the INFO line "Could not authorize you against Realms server".

## Resources and the jar name

- Main resources come from `src/main/resources` and `src/main/generated` (`sourceSets.main.resources.srcDirs`). Loom adds the generated directory through a `HashSet` (`FabricApiDataGeneration`, Loom 1.18.2), so the printed order follows the checkout's absolute path and means nothing. The same path in both fails the build; see [[datagen]].
- `processResources` expands `${version}` in `fabric.mod.json` to the project version, `mod_version+minecraft_version` (`build.gradle`). The jar in `build/libs/` is named `caramel-<mod_version>+<minecraft_version>.jar` from the same values.

## The jars

- `build/libs/` holds the mod jar and a `-sources` jar (`withSourcesJar()` in `build.gradle`).
- The `jar` block copies `LICENSE` and `LICENSE-ASSETS` into the mod jar under new names; [[licensing]] covers what that protects and how it fails silently. Loom excludes datagen's `.cache/**` from the `jar` task.
- The `-sources` jar carries `fabric.mod.json` and `assets/caramel/icon.png`, and no license file: the license copy and the `.cache/**` exclude apply to the `jar` task only. A local build's `-sources` jar also carries the gitignored `src/main/generated/.cache/`, which a CI checkout does not have (`unzip -l`). Only the mod jar goes in a `mods` folder.

## Scratch copies

Reproducing a trap happens in a copy of the project, never in the project itself. `gradle.properties` turns on Gradle's configuration cache, and its entries in `.gradle/configuration-cache` hold absolute paths.

- **A copy that includes `.gradle/` builds the real project.** Gradle in the copy reuses the real entries ("Reusing configuration cache."). Reproduced with two scratch copies: in the copy that kept `.gradle/`, `compileJava` rebuilt the original's `build/classes` and created no `build/` of its own.
- Copy without it, and pass `--no-configuration-cache` in the copy: `tar --exclude=./build --exclude=./.gradle --exclude=./run --exclude=./.git -C <project> -cf - . | tar -xf - -C <copy>`.
- **`./gradlew --stop` in a copy stops the real project's daemons too.** It terminates every daemon started with the same Gradle version, whichever project it serves (Gradle docs, "Gradle Daemon"). Each stopped daemon stores the stop event "stop command received" in the shared daemon registry, and for an hour the real project's next build prints "Starting a Gradle Daemon, 1 stopped Daemon could not be reused, use --status for details" (`HandleStop`, `DefaultDaemonConnector.handleStopEvents`, `DaemonStartupMessage`, Gradle 9.7.1). Leave a copy's daemons to exit on their own.
