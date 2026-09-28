# Build

How `./gradlew build` turns two source sets into one mod jar. The commands and IDE setup for people are in `CONTRIBUTING.md`; this note is the mechanism under them.

## Toolchain

- Minecraft, Fabric Loader, Loom, Fabric API and the mod version are set in `gradle.properties`. Gradle's own version is the `distributionUrl` in `gradle/wrapper/gradle-wrapper.properties`.
- The Loom plugin id is `net.fabricmc.fabric-loom` (`build.gradle`). It is requested as a snapshot (`loom_version` in `gradle.properties`), so the Loom that runs can change with no file changing. The configure log names the one that ran on its `Fabric Loom:` line; when a build breaks with no local change, compare that line first.
- `build.gradle` declares `minecraft` with no `mappings` line. Why is in `CLAUDE.md`, "Toolchain".
- The Java version is declared in several files that must move together. `CLAUDE.md`, "Updating Minecraft", lists them.

## Source sets

- `build.gradle` calls `splitEnvironmentSourceSets()`. `src/main` is common code. Of the Minecraft jars, it compiles against Loom's `minecraft-common` only; `src/client` also gets `minecraft-clientOnly` and main's output. Fabric Loader, Fabric API and the libraries are on both. All of this was read from the printed compile classpaths.
- The guard covers Minecraft's client classes only. A class from `net.minecraft.client` used in `src/main` fails `compileJava` with "package net.minecraft.client does not exist". Fabric API is not split: its client classes, such as `ClientTickEvents`, compile in `src/main`, and the compiler does not catch them. Both were reproduced in a scratch copy. Client code can use everything in main.
- `loom.mods.caramel` lists both source sets, and both compile into the one mod jar: `unzip -l` on it shows `io/github/cr4ftex/caramel/client/` beside the common classes. A dedicated server never runs the client code because it only starts the `main` entrypoint and skips the mixin config marked `"environment": "client"` (`fabric.mod.json`).

## Run tasks

Loom registers `runClient`, `runServer` and `runDatagen` from `loom.runConfigs`, plus `runClientRenderDoc`, a separate RenderDoc task (`./gradlew tasks --all`). `runServer`'s classpath has main's output and no client output, and its only Minecraft jar is `minecraft-common`. `runDatagen`'s classpath is the same as `runClient`'s; [[datagen]] says why. Game files from these runs go in `run/` (`runDatagen` uses `run/datagen`), which `.gitignore` excludes.

## Resources and the jar name

- Main resources come from `src/main/generated` and `src/main/resources`, in that order, as printed from `sourceSets.main.resources.srcDirs`. The same path in both fails the build; see [[datagen]].
- `processResources` expands `${version}` in `fabric.mod.json` to the project version, `mod_version+minecraft_version` (`build.gradle`). The jar in `build/libs/` is named `caramel-<mod_version>+<minecraft_version>.jar` from the same values.

## The jars

- `build/libs/` holds the mod jar and a `-sources` jar (`withSourcesJar()` in `build.gradle`).
- The `jar` block copies `LICENSE` and `LICENSE-ASSETS` into the mod jar under new names. What that protects, and how it fails silently, is in [[licensing]].
- A jar in `build/libs/` is only as fresh as the last build: one built before a file existed lacks that file, and nothing flags it. Rebuild before checking a jar's contents.

## Scratch copies

Reproducing a trap happens in a copy of the project, never in the project itself. `gradle.properties` turns on Gradle's configuration cache, and its entries in `.gradle/configuration-cache` hold absolute paths.

- **A copy that includes `.gradle/` builds the real project.** Gradle in the copy reuses the real entries ("Reusing configuration cache."), so it compiles the real sources into the real `build/` and runs datagen into the real `src/main/generated`. Seen once; the output happened to be byte-identical.
- Copy without it, and pass `--no-configuration-cache` in the copy: `tar --exclude=./build --exclude=./.gradle --exclude=./run --exclude=./.git -C <project> -cf - . | tar -xf - -C <copy>`.
