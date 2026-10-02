# Caramel setup report

The passdown between sessions: where the project stands, what you decided, and what comes next. Part 1 is the current state and is rewritten each session. Part 2 is the record of the first review of the docs and how each finding was handled.

Session 1 (2026-09-27) set up the project. Session 2 (2026-09-27) applied the review fixes, built the knowledge vault, installed the `/gold` skill and made the first commit. The first idea, C-001, was built, playtested and committed on 2026-09-28. The second, C-002, was built, playtested and committed on 2026-09-29. The third, C-003, was built, playtested and committed on 2026-09-30. The fourth, C-004, was built and playtested on 2026-09-30. The fifth, C-005, was built, playtested and committed on 2026-10-01. The sixth, C-006, was built, playtested and committed on 2026-10-01.

---

## Part 1: Current state

### Your decisions

| Topic | Decision |
|---|---|
| Name | Caramel (mod id `caramel`) |
| Mod loader | Fabric |
| Minecraft version | 26.3 (newest release, out 2026-09-15) |
| Java | 25 |
| Project folder | `~/Projects/caramel` |
| GitHub repository | `CR4FTEX/caramel`, **private** |
| Code license | MIT (`LICENSE`) |
| Art license | CC BY-NC-SA 4.0 (`LICENSE-ASSETS`) |
| Entity model code | Counts as art: the entity model and animation code in `client/model/` is CC BY-NC-SA 4.0, set in the `LICENSE-ASSETS` preamble |
| Editable source art | `art-source/` at the repository root, never under `src/`, so it never ships in the jar |
| Your pillar wording | Your exact words in every doc: "without steering too far from", "more incentives to explore and risk to be rewarded" |
| AI commits | Only when you ask, and then straight to `main` |
| Knowledge vault | `caramel-vault/` at the repository root, committed with the code. Not `docs/` |
| Vault dates | None. A note says how Caramel works; git keeps the when |
| Gold passes | You call `/gold` on the vault when you choose, usually mid-session |
| Other mods | Vanilla first. Compatibility with other mods is not a priority yet (`docs/DESIGN.md`, "Priorities") |
| Game tests | Run in every build and in CI, through Fabric's game-test mode; `eula.txt` stays `eula=false` |

Choices I made that you can change:

- **Java package:** `io.github.cr4ftex.caramel`. This follows the usual convention of basing it on your GitHub name.
- **Commit identity:** commits are signed as `CR4FTEX` with your private GitHub address (`82980321+CR4FTEX@users.noreply.github.com`). This is set only for this project.
- **Version format:** `0.1.0+26.3`, meaning mod version 0.1.0 for Minecraft 26.3.
- **`.obsidian/` is not committed.** It is per-machine app state. The vault's notes and its `.vaultrc` are committed.

### Done

- **Project created** from Fabric's official 26.3 template, renamed for Caramel:
  - Toolchain: Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Loom 1.18, Gradle 9.7.1.
  - Code is split into `src/main` (runs everywhere) and `src/client` (client only).
- **Data generation set up.** Recipes, models, loot tables, tags and translations will be generated from Java code instead of written by hand.
- **Build passes.** `./gradlew build` produces `build/libs/caramel-0.1.0+26.3.jar`, with both license files inside.
- **The mod loads in Minecraft.** The data generator starts Minecraft 26.3 with Caramel and Fabric API, and Caramel prints its startup message ("Caramel is melting in.").
- **Placeholder icon:** a 32x32 pixel-art caramel cube with drips. Replace it whenever you like.
- **Docs:** `README.md`, `CONTRIBUTING.md`, `docs/DESIGN.md` (your vision and the idea log), `docs/TEXTURES.md` (art guide), `CLAUDE.md` (rules for AI sessions), `art-source/README.md`, the two license files, and `.github/` (issue forms, pull request checklist, build workflow).
- **All 28 review fixes applied** (Part 2). A second review of the fixed docs, through four lenses, found 21 more problems, all fixed; a skeptic rejected 9 others. The GitHub, git and Actions behaviour the fixes rely on was checked against official docs.
- **Knowledge vault built:** `caramel-vault/`, open in Obsidian. Start at `_map.md`, which names one note per topic: build, datagen, recipes, game tests, fuel, trades, mixins, CI, licensing, GitHub. Every trap in it was reproduced in a scratch copy of the project or checked against a primary source.
- **`/gold` installed** at `~/.claude/skills/gold/`, outside the repository: `SKILL.md`, `vault_lint.py` and `vault-rot-patterns.md`, with nothing tied to one machine. The vault lints clean (`exit: 0`). An older `bindings.md` is still in that folder; the skill does not need it, its Caramel entry now points at the vault's `.vaultrc`, and you can delete it.
- **First commit pushed:** `88b1a56` on `main` at https://github.com/CR4FTEX/caramel, signed with your noreply address. The `idea` and `art` labels exist on GitHub.
- **First CI run passed** (run 36375456105, 1m21s). The `caramel-jar` artifact is downloadable from the run until 2026-10-12. The run saved a 460 MB Gradle cache on `main`. CI caching works: a run whose Gradle build files are unchanged restores it (the gold-pass run did, and took 1m30s against 2m05s cold); a change to `build.gradle` starts cold and saves a new entry.

### Found while checking

- **The session 1 jar was stale.** It was built before the license files existed and held neither. A rebuild fixed it. Gradle drops a missing license file from the jar without any error; `caramel-vault/licensing.md` has the check.
- **Hand edits to generated files are not lost on the next run,** as CONTRIBUTING said. Datagen keeps a hand edit until the provider's output for that file changes, and a fresh clone loses it. It also deletes any file in `src/main/generated/` that no provider made. CONTRIBUTING now says so.
- **`blank_issues_enabled: false` restricts nobody here.** On a private repository owned by a personal account, every collaborator has write access, and write access keeps the "Blank issue" option. The forms are the default, not a gate.

### Ideas

- **C-001, cheaper stairs and recycling: Done.** You playtested all 12 checks with no issues. Committed and pushed to `main`. The entry in `docs/DESIGN.md` has every decision and the implementation notes.
- **C-002, cheaper slabs and recycling: Done.** You playtested all 12 checks and they all worked as intended. Committed and pushed to `main`. Your four answers (all 17 pressure plates, carpets removed for now, bamboo mosaic removed, purpur pillar removed) and what can no longer be made are in the entry in `docs/DESIGN.md`. Bamboo mosaic cannot be obtained in survival until a later idea; C-003 brings back the pressure plates, wool carpets and detector rails.
- **C-003, pressure plates and carpets from slabs: Done.** You playtested all 12 checks and they passed, then confirmed in game that shepherds sell 20 carpets. Committed and pushed to `main`. Your answers (fuel and moss carpet compost both match their cost, shepherd carpets at 20) are in the entry in `docs/DESIGN.md`. Dyeing wool and items is a future idea of yours.
- **C-004, more fences per craft: Done.** You playtested all 8 checks and they all worked as intended. Every fence recipe gives 4 times as many: 12 wooden fences, 24 nether brick fences (your answer). Wooden fences burn 125 ticks, matching their cost (your answer). Committed and pushed to `main`. The entry in `docs/DESIGN.md` has the decisions.
- **C-005, more fence gates per craft: Done.** You playtested all 7 checks and they all worked as intended. Every fence gate recipe gives 4 instead of 1. Fence gates keep their burn time, since 4 cost exactly 4 planks. Committed and pushed to `main`, with the gold pass on `caramel-vault/`.
- **C-006, more doors and trapdoors per craft: Done.** You playtested all 10 checks and they all worked as intended. Every door and trapdoor recipe gives 16. The iron and copper ones take 6 Blocks of Iron or Copper in the wooden shapes (your pitch); only the plain Block of Copper works (your answer). Wooden doors and trapdoors burn 112 ticks, matching their cost (your answer). Waxing stays 1 for 1. Committed and pushed to `main`, with the second gold pass on `caramel-vault/`.

### Open suggestions (your call)

None of these is started. Say the word for any of them.

- **Picking up bugs and art requests.** CONTRIBUTING says to pick up only ideas whose status is Planned. Bug reports and art requests have no status: should they wait for your go-ahead too?
- **Merge conflicts.** When `git pull --no-rebase --no-edit origin main` hits a conflict, `git commit --no-edit` finishes it without opening an editor. CONTRIBUTING could say so.
- **CI action versions.** `actions/checkout@v7` and `actions/setup-java@v6` exist. `build.yml` uses `@v6` and `@v5`, which still work.
- **CI minutes.** Only Markdown-only changes skip the build. A change under `art-source/`, or to `caramel-vault/.vaultrc`, still starts one. Both could go in `paths-ignore`.
- **The `-sources` jar.** It carries no license files, and a local build's copy carries `src/main/generated/.cache/`. CI uploads it next to the mod jar. It could be left out of the artifact, or given the license files.
- **Generated art and the art license.** The `LICENSE-ASSETS` preamble covers art under `src/main/resources/assets/caramel/`, but datagen writes block and item models and blockstates into `src/main/generated/assets/caramel/`, which it does not name. Nothing is generated there yet. Should the preamble name that folder too, before the first generated model?
- **A scope line in `LICENSE`.** `LICENSE` is the plain MIT text; only `LICENSE-ASSETS` says the entity model code is not MIT. One line above the MIT text would make both files say the same thing.

### Things you should know

- **Private repo limits:** on a free GitHub account, private repositories get 2,000 build minutes a month and 500 MB of artifact storage. CI builds take about 1m10s to 2m35s (`gh run list`). The build workflow skips Markdown-only changes and cancels outdated runs to save minutes (`caramel-vault/ci.md`).
- **Inviting contributors:** because the repo is private, people can only help once you invite them. On the GitHub website, open the repo, then Settings > Collaborators > Add people. Artists don't need git or Java: the art guide explains how to submit through GitHub issues or the website.
- **Automated in-game tests** run in every `./gradlew build` and in CI, since C-001: a Minecraft test server with no window loads the mod's data and checks it before you playtest. You chose Fabric's game-test mode, which skips the EULA check by design; `eula.txt` stays `eula=false`, and nothing accepts the EULA for you. `caramel-vault/gametest.md` has the details.
- **Updating Minecraft:** Mojang releases a new version every few months. The plan is to stay on 26.3 while building and move to newer versions at milestones. `CLAUDE.md` has the step-by-step update procedure.
- **Vault and passdown:** durable knowledge (how things work, traps, decisions with their reasons) goes in `caramel-vault/`. Status goes here and in the idea log. Around the middle of a session, you can call `/gold` on the vault.

### Next step

Send me the next idea. You have said the next few ideas are small ones about how much a recipe gives, and that dyeing wool and items, and bamboo mosaic, the chiseled blocks and the pillars, will be future ideas.

---

## Part 2: Review findings

Three reviewers read the first draft of the docs, each looking for a different kind of problem:

- **Accuracy:** do commands, paths, versions and file formats match reality?
- **Fidelity:** do the docs say only what you actually said and decided?
- **Newcomer:** could a new coder, or an artist who has never used git, follow them?

Severity: **High** = something is broken, **Medium** = it misleads people or blocks them, **Low** = unclear, inconsistent or wordy.

### Accuracy

| # | Severity | File | Problem | Fix |
|---|---|---|---|---|
| A1 | High | Idea issue form | "None" is a reserved word in GitHub dropdowns, so the whole Idea form would silently fail to appear, and blank issues are turned off. Nobody could file an idea. | Renamed the option to "No spin-off (vanilla change)". |
| A2 | Medium | CONTRIBUTING | The `git pull origin main` instruction fails with an error on current versions of git when both branches have new commits. | Use `git pull --no-rebase --no-edit origin main`, then `git push`. |
| A3 | Medium | CONTRIBUTING | Said the build runs on every push. It actually runs on pull requests and on pushes to `main`, and skips docs-only changes. | Corrected the wording, and mentioned the jar can be downloaded from each build run. |
| A4 | Low | CLAUDE.md | Same wrong claim about when the build runs. | Corrected. |
| A5 | Low | Idea and Art forms | The `idea` and `art` labels don't exist on GitHub by default, so issues would arrive without labels. | Create both labels when pushing (Part 1, step 2). |

### Fidelity to your decisions

| # | Severity | File | Problem | Fix |
|---|---|---|---|---|
| F1 | Medium | Licenses, art guide | Entity models are made in Blockbench but end up as Java code, and all Java code is MIT. An artist's model would quietly become MIT, which allows selling it. That goes against your CC BY-NC-SA choice for art. | Entity model and animation code goes in its own folder (`client/model`), and that folder is covered by the art license (CC BY-NC-SA). |
| F2 | Medium | DESIGN.md | One guiding principle ("streamline instead of bloat, don't pile more on top") was invented. You never said it, and it could be used to reject your own spin-off content. | Replaced with your actual wording: streamline and improve what exists. |
| F3 | Medium | Idea form, CONTRIBUTING, README, DESIGN | The files disagreed on who pitches ideas and when they get logged. | One rule everywhere: the design doc logs ideas you pitch or take on. A contributor's idea is only a suggestion until you take it on. |
| F4 | Low | CONTRIBUTING, CLAUDE.md | Same build-trigger mistake as A3. | Corrected. |
| F5 | Low | Idea form | Area names didn't match the design doc ("Deep Dark" vs "The Deep Dark"). A required question also assumed every idea adds risk and reward, which a simple recipe fix might not. | Names now match exactly, "Other / new area" was added, and the question was reworded. |
| F6 | Low | Art guide | Presented vanilla-style 16x16 art as settled, but you never said it. | Kept it as the default because it follows from staying close to the game's identity, with a note that you can decide otherwise for any idea. |
| F7 | Low | CLAUDE.md | Didn't say that placeholder textures also have to be original. An AI could have recolored vanilla textures. | Added: placeholders must be original too. |
| F8 | Low | CLAUDE.md | Left out two rules: only you can mark an idea Done (after playtesting), and each idea is listed under its area. | Added both. |
| F9 | Low | CLAUDE.md | Was stricter than your words ("do not stray" vs your "without steering too far"), and left out "incentives to explore and risk to be rewarded". | Reworded to match your words. You have the final say on your own ideas. |
| F10 | Low | CONTRIBUTING, CLAUDE.md | Unclear who may commit straight to `main`. | Collaborators use branches and pull requests. You may commit to `main` directly. AI sessions only commit when asked. |
| F11 | Low | Art form | Contradicted itself on what reference images may be used for. | References are for inspiration only; all art must be original. |
| F12 | Low | `fabric.mod.json` | The in-game description said "revamps", which is stronger than your wording, and left out barren areas. | Now: "Streamlines and improves Minecraft's existing features, and brings content from the spin-off games to barren areas and neglected concepts in every dimension." |

### Newcomer experience

| # | Severity | File | Problem | Fix |
|---|---|---|---|---|
| N1 | Medium | CONTRIBUTING | Same `git pull` failure as A2. | Fixed with A2. |
| N2 | Medium | README | The first command is cloning a private repo, but nothing before it says you need access and a signed-in git. | Added a note before the clone command. |
| N3 | Medium | CONTRIBUTING | A Java developer new to modding gets no pointer to learning material. | Added a link to the official Fabric documentation. |
| N4 | Low | CONTRIBUTING | Artists had to read through git and Java setup before learning they can skip it. | The opening now tells artists they don't need git, Java or IntelliJ. |
| N5 | Low | CONTRIBUTING | Same build-trigger mistake as A3. | Corrected. |
| N6 | Low | CONTRIBUTING | Said work can start once an idea is logged, but logged ideas start as "Pitched" (not yet agreed). | Only pick up work whose status is "Planned". |
| N7 | Low | Art guide | The "upload on the GitHub website" route didn't explain how to create folders or avoid committing straight to `main`. | Step-by-step instructions added. |
| N8 | Low | Art guide | GitHub issues refuse `.ogg` sound and `.mcmeta` animation files as attachments. | Put everything in one `.zip`. |
| N9 | Low | Pull request checklist | Artists submitting from the website can't run the build checks, so their pull request looks incomplete. | Added a note: the GitHub build covers it; say so under "How I tested it". |
| N10 | Low | Art form | Only covered requesting art, not submitting it. | Now covers both, with a field for attaching a `.zip`. |
| N11 | Low | CONTRIBUTING, art guide | Repeated the README's summary and disclaimer, so readers saw them two or three times. | Cut the repeats. |

**Status:** all fixes applied in session 2. A5 (the labels) is a GitHub step, done with the first push. Two fixes needed more than the table says:

- **F1** took effect only once the `LICENSE-ASSETS` preamble named the `client/model/` folder. You approved that edit. The Creative Commons legal text below the preamble is unchanged, checked by hash.
- **N7** raised where editable source files go. You chose `art-source/`.

**Correction to A1 (session 2):** the row overstates the failure. GitHub's docs say a form with a "None" option fails validation, not that it disappears. People with write access, which on this repository is every collaborator, keep a blank issue anyway.
