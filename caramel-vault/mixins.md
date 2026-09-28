# Mixins

Caramel has two mixin configs. The conventions for writing a mixin are in `CONTRIBUTING.md`, "Mixins"; this note covers what the configs make happen. The failures below were reproduced with an `@Inject` into `net.minecraft.server.Bootstrap`, listed in the common config and booted by `./gradlew runDatagen` (Mixin 0.8.7, Minecraft 26.3).

`caramel.mixins.json` loads everywhere; `caramel.client.mixins.json` loads on the client only (`"environment": "client"` in `fabric.mod.json`). Where each mixin goes and which array lists it is in `CONTRIBUTING.md`, "Mixins". Both configs also set `"overwrites": { "requireAnnotations": true }` and `"compatibilityLevel": "JAVA_25"`, one of the places the Java version lives (see [[build]]).

## What defaultRequire 1 does

Both configs set `"injectors": { "defaultRequire": 1 }`. That setting turns a dead injector into a crash when its target class is loaded and transformed. For `Bootstrap` that is startup; a class first loaded when a world opens or a screen is shown crashes only then. The compiler catches none of these, so a run is where they surface.

| Broken injector | With `defaultRequire: 1` | With 0 |
| --- | --- | --- |
| Method exists, injection point matches nothing | crash: `InjectionError: Critical injection failure: ... failed injection check, (0/1) succeeded` | silent: the handler never runs |
| Method name does not exist | crash: `InvalidInjectionException: Critical injection failure: @Inject annotation on ... could not find any targets matching '...'` | silent |

After a Minecraft update, these crashes are the signal that a target moved or was renamed. The first message also says "Scanned 0 target(s)": in Mixin 0.8.7 that count is always 0, so it says nothing about whether the target method exists.

## Traps

- **A mixin class that its config's array does not list is ignored completely**, even when broken: no crash and no log line, whatever `defaultRequire` says. The debug log (`logs/debug.log` in the run directory) prints "Preparing caramel.mixins.json (N)", where N is the number of class names the config lists in its `mixins`, `client` and `server` arrays (`MixinProcessor.prepareConfigs` in Mixin 0.8.7).
- **`runDatagen` checks only the mixins whose target loads during the run.** A broken mixin into a class loaded at startup, such as `Bootstrap`, crashes it without a world or the server EULA. A mixin whose target first loads in a world or on a screen is not checked: the datagen run ends inside Minecraft's constructor. The datagen run is a client environment (see [[datagen]]).
