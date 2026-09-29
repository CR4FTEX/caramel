# Mixins

Caramel has two mixin configs. The conventions for writing a mixin, and where each one goes, are in `CONTRIBUTING.md`, "Mixins"; this note covers what the configs make happen. The failures below were reproduced with an `@Inject` into `net.minecraft.server.Bootstrap`, listed in the common config and booted by `./gradlew runDatagen` (Mixin 0.8.7, Minecraft 26.3).

`caramel.mixins.json` loads everywhere; `caramel.client.mixins.json` loads on the client only (`"environment": "client"` in `fabric.mod.json`). Both configs also set `"overwrites": { "requireAnnotations": true }` and `"compatibilityLevel": "JAVA_25"`, one of the places the Java version lives (see [[build]]).

## What defaultRequire 1 does

Both configs set `"injectors": { "defaultRequire": 1 }`. That setting turns a dead injector into a crash when its target class is loaded and transformed; for `Bootstrap` that is startup. Fabric Loader 0.19.5 never runs Mixin's audit (no class in the loader jar calls it), so a mixin applies only when its target class loads. The compiler catches none of these.

| Broken injector | With `defaultRequire: 1` | With 0 |
| --- | --- | --- |
| Method exists, injection point matches nothing | crash: `InjectionError: Critical injection failure: ... failed injection check, (0/1) succeeded` | no crash, no log line |
| Method name does not exist | crash: `InvalidInjectionException: Critical injection failure: @Inject annotation on ... could not find any targets matching '...'` | no crash, no log line |

After a Minecraft update, these crashes are the signal that a target moved or was renamed. The first message also says "Scanned 0 target(s)": in Mixin 0.8.7 that count is always 0, so it says nothing about whether the target method exists.

## Where a broken mixin surfaces

- **Every `./gradlew build`, locally and in CI**, boots the game test server (`runGameTest`, see [[gametest]]). It crashes on a broken common mixin whose target loads on it: with a dead injector into `MinecraftServer.tickServer` or `ServerLevel.tick`, `:runGameTest` failed with "InjectionError: Critical injection failure" (scratch copy). It is a server environment, so it never applies `caramel.client.mixins.json`.
- **`./gradlew runDatagen`** crashes on a broken mixin whose target loads during the run: `Bootstrap`, and server classes such as `ServerLevel` and `MinecraftServer` (reproduced). A target that first loads later, such as on a screen, is not checked, because the run ends inside Minecraft's constructor (see [[datagen]]). The datagen run is a client environment.

## Traps

- **A mixin class that its config's array does not list is ignored completely**, even when broken: no crash and no log line, whatever `defaultRequire` says. The debug log (`logs/debug.log` in the run directory) prints "Preparing caramel.mixins.json (N)", where N is the number of class names the config lists in its `mixins`, `client` and `server` arrays (`MixinProcessor.prepareConfigs` in Mixin 0.8.7).

## What is still unknown, and what would settle it

- Whether the client mixin config fails the same way as the common one: every reproduction above used the common config. The same broken `@Inject`, listed in the `client` array of `caramel.client.mixins.json` against a client-only class that loads during `./gradlew runDatagen`, settles it.
- Whether the MixinExtras injectors that `CONTRIBUTING.md` prefers (`@WrapOperation`, `@ModifyExpressionValue`, `@ModifyReturnValue`) crash under `defaultRequire: 1` like `@Inject`. A `@WrapOperation` into `Bootstrap` whose target matches nothing, booted by `./gradlew runDatagen` in a scratch copy, settles it.
