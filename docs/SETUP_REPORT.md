# Caramel passdown

The passdown between sessions: where the project stands, what the owner decided, and what comes next. It is rewritten as the work moves along. The state of each idea is in its entry in `docs/DESIGN.md`; how things work and the traps they hide are in `caramel-vault/`.

Timeline: the project was set up on 2026-09-27. C-001 to C-008 were built, playtested and committed between 2026-09-28 and 2026-10-05, with a gold pass on the vault along the way. On 2026-10-09 the vision replaced the two pillars in the docs and the placeholder icon was removed.

## Decisions

| Topic | Decision |
|---|---|
| Name | Caramel (mod id `caramel`) |
| Mod loader | Fabric |
| Minecraft version | 26.3 |
| Java | 25 |
| GitHub repository | `CR4FTEX/caramel`, public |
| Code license | MIT (`LICENSE`) |
| Art license | CC BY-NC-SA 4.0 (`LICENSE-ASSETS`) |
| Entity model code | Counts as art: the entity model and animation code in `client/model/` is CC BY-NC-SA 4.0, set in the `LICENSE-ASSETS` preamble |
| Editable source art | `art-source/` at the repository root, never under `src/`, so it never ships in the jar |
| Vision | Quoted word for word in `docs/DESIGN.md`: the tagline, "What is Caramel?", the Mod, Expansion and Infrastructure definitions, the five goals and how Caramel is made, with the guiding principles drawn only from them. The other docs summarize it and keep "without steering too far from" |
| AI art | None, placeholders included: Caramel never generates or ships AI-generated assets. A block or item without art shows the game's missing-texture checkerboard until the owner or an artist provides it; a mob needs its artist's model first |
| Bought or commissioned art | Ships only when its license, or the artist's written agreement, lets it ship under CC BY-NC-SA 4.0 like all other art |
| Player choice and cross-compatibility | Long-term goals. Vanilla first stays the priority; a system to switch features off is built only when the owner pitches it |
| AI commits | Only when the owner asks, and then straight to `main` |
| Knowledge vault | `caramel-vault/` at the repository root, committed with the code. Not `docs/` |
| Vault dates | None. A note says how Caramel works; git keeps the when |
| Gold passes | The owner calls `/gold` on the vault when they choose, usually mid-session |
| Other mods | Vanilla first. Compatibility with other mods is not a priority yet (`docs/DESIGN.md`, "Priorities") |
| Game tests | Run in every build and in CI, through Fabric's game-test mode; `eula.txt` stays `eula=false` |

Choices made by the AI that the owner can change:

- **Java package:** `io.github.cr4ftex.caramel`, following the usual convention of basing it on the GitHub name.
- **Commit identity:** commits are signed as `CR4FTEX` with the owner's GitHub noreply address, set in the clone's local git config only.
- **Version format:** `0.1.0+26.3`, meaning mod version 0.1.0 for Minecraft 26.3.
- **`.obsidian/` is not committed.** It is per-machine app state. The vault's notes and its `.vaultrc` are committed.

## Done

- **Project created** from Fabric's official 26.3 template, renamed for Caramel: Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Loom 1.18, Gradle 9.7.1, with the code split into `src/main` (runs everywhere) and `src/client` (client only).
- **Data generation set up.** Recipes, models, loot tables, tags and translations are generated from Java code instead of written by hand.
- **Build passes.** `./gradlew build` produces `build/libs/caramel-0.1.0+26.3.jar`, with both license files inside.
- **Mod icon: none.** It was removed under the no-AI-art rule, so the mod list shows its default icon. When the owner or an artist makes one, it goes in `src/main/resources/assets/caramel/icon.png`, with an `"icon": "assets/caramel/icon.png"` line back in `fabric.mod.json`.
- **Docs:** `README.md`, `CONTRIBUTING.md`, `docs/DESIGN.md` (the vision and the idea log), `docs/TEXTURES.md` (art guide), `CLAUDE.md` (rules for AI sessions), `art-source/README.md`, the two license files, and `.github/` (issue forms, pull request checklist, build workflow). The first draft of the docs was reviewed for accuracy, fidelity to the owner's words and newcomer clarity, and every finding was fixed.
- **Knowledge vault built:** `caramel-vault/`, open in Obsidian. Start at `_map.md`, which names one note per topic. Every trap in it was reproduced in a scratch copy of the project or checked against a primary source.
- **CI works:** `.github/workflows/build.yml` builds on every pull request and push to `main`, runs the game tests, and uploads the jars as an artifact. `caramel-vault/ci.md` has the details.

## Found while checking

- **A stale jar can lack the license files.** Gradle drops a missing license file from the jar without any error; `caramel-vault/licensing.md` has the check.
- **Hand edits to generated files are not kept for good.** Datagen keeps a hand edit until the provider's output for that file changes, and a fresh clone loses it. It also deletes any file in `src/main/generated/` that no provider made. CONTRIBUTING says so.
- **`blank_issues_enabled: false` restricts only people without write access.** Every collaborator has write access on a repository owned by a personal account, so they keep the "Blank issue" option. The forms are the default, not a gate.

## Ideas

- **C-001, cheaper stairs and recycling: Done.** All 12 checks passed. The entry in `docs/DESIGN.md` has every decision and the implementation notes.
- **C-002, cheaper slabs and recycling: Done.** All 12 checks passed. The owner's four answers (all 17 pressure plates, carpets removed for now, bamboo mosaic removed, purpur pillar removed) and what can no longer be made are in the entry. C-003 brings back the pressure plates, wool carpets and detector rails, and a later idea brings back bamboo mosaic.
- **C-003, pressure plates and carpets from slabs: Done.** All 12 checks passed, and shepherds sell 20 carpets. Fuel and moss carpet compost match their cost. Dyeing wool and items is a future idea of the owner's.
- **C-004, more fences per craft: Done.** All 8 checks passed. Every fence recipe gives 4 times as many: 12 wooden fences, 24 nether brick fences. Wooden fences burn 125 ticks, matching their cost.
- **C-005, more fence gates per craft: Done.** All 7 checks passed. Every fence gate recipe gives 4 instead of 1. Fence gates keep their burn time, since 4 cost exactly 4 planks.
- **C-006, more doors and trapdoors per craft: Done.** All 10 checks passed. Every door and trapdoor recipe gives 16. The iron and copper ones take 6 Blocks of Iron or Copper in the wooden shapes; only the plain Block of Copper works. Wooden doors and trapdoors burn 112 ticks. Waxing stays 1 for 1.
- **C-007, more buttons per craft: Done.** All 8 checks passed. Every button recipe, all 15, gives 20 instead of 1. Wooden buttons burn 15 ticks.
- **C-008, more walls per craft: Done.** All 9 checks passed. All 32 wall recipes give 12 instead of 6, and every stonecutter recipe that makes a wall gives 2 per block. The balance audit found no work block giving far more than another and nothing that multiplies; its findings are in the C-008 entry.

## Open suggestions

None of these is started; each is the owner's call.

- **Picking up bugs and art requests.** CONTRIBUTING says to pick up only ideas whose status is Planned. Bug reports and art requests have no status: should they wait for the owner's go-ahead too?
- **Merge conflicts.** When `git pull --no-rebase --no-edit origin main` hits a conflict, `git commit --no-edit` finishes it without opening an editor. CONTRIBUTING could say so.
- **CI action versions.** `actions/checkout@v7` and `actions/setup-java@v6` exist. `build.yml` uses `@v6` and `@v5`, which still work.
- **CI runs.** Only Markdown-only changes skip the build. A change under `art-source/`, or to `caramel-vault/.vaultrc`, still starts one. Both could go in `paths-ignore`.
- **The `-sources` jar.** It carries no license files, and a local build's copy carries `src/main/generated/.cache/`. CI uploads it next to the mod jar. It could be left out of the artifact, or given the license files.
- **A scope line in `LICENSE`.** `LICENSE` is the plain MIT text; only `LICENSE-ASSETS` says the entity model code is not MIT. One line above the MIT text would make both files say the same thing.
- **A contact route.** The Infrastructure text says "Get in touch with us"; with a public repository, GitHub issues are the route, and the docs could say so.

## Things to know

- **Automated in-game tests** run in every `./gradlew build` and in CI: a Minecraft test server with no window loads the mod's data and checks it before a playtest. Fabric's game-test mode skips the EULA check by design; `eula.txt` stays `eula=false`, and nothing accepts the EULA. `caramel-vault/gametest.md` has the details.
- **Updating Minecraft:** the plan is to stay on 26.3 while building and move to newer versions at milestones. `CLAUDE.md` has the step-by-step update procedure.
- **Vault and passdown:** durable knowledge (how things work, traps, decisions with their reasons) goes in `caramel-vault/`. Status goes here and in the idea log.

## Next step

Send the next idea. The owner has said the next few ideas are small ones about how much a recipe gives, and that dyeing wool and items, and bamboo mosaic, the chiseled blocks and the pillars, will be future ideas.
