# CI

`.github/workflows/build.yml` runs `./gradlew build` on GitHub Actions and keeps the jars as a downloadable artifact. The repository is private, so its runs and artifacts count against the owner's quota (see [[github]]), and the workflow is shaped to spend little of it.

## When it runs

- On a push to `main`, and on every pull request (`on.push.branches` and `on.pull_request`). A pull request branch is built through `pull_request` only; its pushes do not start a second run.
- Both triggers set `paths-ignore: '**.md'`. GitHub checks the files changed by the whole push, or by the whole pull request against its base, not commit by commit (GitHub docs, "Workflow syntax for GitHub Actions"). A push to `main` that changes only Markdown starts no run. A pull request that changes any other file runs the full build on every push to it, including a push that changes only Markdown.
- `concurrency` with `cancel-in-progress: true` cancels the older run when a newer run starts for the same branch or pull request. A Markdown-only push to `main` starts no run, so it cancels nothing; a Markdown-only push to a pull request that changes other files starts a run, which cancels the older one.

## What it does

- Validates the Gradle wrapper, installs Microsoft's JDK 25, and runs `./gradlew build`, with a 30-minute job timeout. `build` includes the server game tests (`check` depends on `runGameTest`; see [[gametest]]), and the build step's log reports "All N required tests passed".
- `gradle/actions/setup-gradle` with `cache-provider: basic` saves and restores `~/.gradle/caches`, which holds Loom's shared cache, and `~/.gradle/wrapper`. Those paths are fixed, so Loom's per-project `.gradle/loom-cache` is rebuilt on every run. Only runs on `main` write the cache; pull request runs only read it. Basic caching removes no stale entries (gradle/actions `docs/setup-gradle.md`, "Basic Caching" and "Limitations").
- The cache key is `setup-java-Linux-x64-gradle-<hash>`, hashed over `**/*.gradle*`, `**/gradle-wrapper.properties` and the other files that `docs/setup-gradle.md`, "Cache key strategy", lists. Basic caching has no restore keys, so a run restores only an exact key match. After a `build.gradle` change, the next run on `main` restored nothing and saved a new key; a later run with unchanged build files restored that key (run logs).
- The run log shows what the cache did. A miss prints "Basic caching did not find an entry to restore. Will start with empty state." in the "setup gradle" step. A save prints "Basic caching saved entry with key: setup-java-Linux-x64-gradle-..." in "Post setup gradle", whose summary reads "Entries: 0 restored, 1 saved". A restore prints "Cache hit for: <key>" and "Basic caching restored from cache key: <key>" in "setup gradle", and "Post setup gradle" then skips the save ("Save was skipped", "Entries: 1 restored, 0 saved"). `gh api repos/CR4FTEX/caramel/actions/caches` lists the saved entries and their branch.
- Uploads `build/libs/` as the artifact `caramel-jar`, kept 14 days, and fails the run if that folder is empty. The artifact holds both jars; only the mod jar goes in a `mods` folder ([[build]] describes the `-sources` jar).
- A run whose build fails uploads no jar. The upload step has no `if:`, so it takes GitHub's default `success()` and runs only when every step before it passed (GitHub docs, "Expressions", "Status check functions").
- The job's token can only read the repository (`permissions: contents: read`).

## Traps

- CI builds only what is committed and does not run datagen; its game tests check only the data they cover (see [[gametest]]). See [[datagen]] for what that misses.
