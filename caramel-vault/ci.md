# CI

`.github/workflows/build.yml` runs `./gradlew build` on GitHub Actions and keeps the jars as a downloadable artifact. The repository is private, so Actions minutes are limited, and the workflow is shaped to spend few of them.

## When it runs

- On a push to `main`, and on every pull request (`on.push.branches` and `on.pull_request`). A pull request branch is built through `pull_request` only; its pushes do not start a second run.
- Both triggers set `paths-ignore: '**.md'`. GitHub checks the files changed by the whole push, or by the whole pull request against its base, not commit by commit (GitHub docs, "Workflow syntax for GitHub Actions"). A push to `main` that changes only Markdown starts no run. A pull request that changes any other file runs the full build on every push to it, including a push that changes only Markdown.
- `concurrency` with `cancel-in-progress: true` cancels the older run when a newer run starts for the same branch or pull request. A Markdown-only push starts no run, so it cancels nothing.

## What it does

- Validates the Gradle wrapper, installs Microsoft's JDK 25, and runs `./gradlew build`, with a 30-minute job timeout.
- `gradle/actions/setup-gradle` with `cache-provider: basic` restores `~/.gradle/caches`, which holds Loom's shared cache, and `~/.gradle/wrapper`. Those paths are fixed (gradle/actions `docs/setup-gradle.md`, "Basic Caching"), so Loom's per-project `.gradle/loom-cache` is rebuilt on every run. Only runs on `main` write the cache; pull request runs only read it.
- Uploads `build/libs/` as the artifact `caramel-jar`, kept 14 days, and fails the run if that folder is empty. The artifact holds both jars. The `-sources` jar carries a `fabric.mod.json` too (`unzip -l`), so only the mod jar goes in a `mods` folder.
- The job's token can only read the repository (`permissions: contents: read`).

## Traps

- CI builds only what is committed and does not run datagen. See [[datagen]] for what that misses.
