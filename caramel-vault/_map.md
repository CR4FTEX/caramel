# Caramel vault

How Caramel works, for a reader who was not there: how the build, datagen, mixins, CI, licensing and the GitHub repository behave, and the traps each one hides. Each trap gives the error message it produces, or says that it fails silently. Every fact names the file or the check it came from. Each topic has one owning note; other notes link to it instead of explaining it again.

## Owning notes

| Topic                                              | Note          |
| -------------------------------------------------- | ------------- |
| Gradle, Loom, source sets, what goes in the jar    | [[build]]     |
| Data generation and `src/main/generated/`          | [[datagen]]   |
| Mixin configs and how they fail                    | [[mixins]]    |
| The GitHub Actions build                           | [[ci]]        |
| Code and art licenses, and how the jars carry them | [[licensing]] |
| Repository, issue forms, labels, git               | [[github]]    |

## What lives outside the vault

The vault holds no status. Status has named homes, which an agent corrects as the work moves:

- **Session state and next steps:** `docs/SETUP_REPORT.md`, the passdown.
- **Ideas and their state:** the idea log in `docs/DESIGN.md`, one entry per idea with a Status field. The owner's vision, quoted word for word, lives there too.
- **Procedures for people:** `README.md`, `CONTRIBUTING.md`, `docs/TEXTURES.md`.
- **Rules for AI sessions:** `CLAUDE.md`.

The vault links to those files and does not restate them.

## Discipline and checkers

`.vaultrc` declares the vault's rules, and its comments give the reason for each and list the checkers a gold pass runs first. Lint with the `/gold` skill's `vault_lint.py` (`python3 <skill-dir>/vault_lint.py caramel-vault`); its last line, `exit: N`, is the result, and 0 is gold.

## What is still unknown, and what would settle it

- Whether the `basic` Gradle cache restores for this repository. Runs on `main` save it (the log lines are in [[ci]]). The "setup gradle" step of the next run on `main` settles it: a restore no longer prints "did not find an entry to restore". A Markdown-only push starts no run, so only a push that changes another file can settle it.
- Whether the client mixin config fails the same way as the common one ([[mixins]] reproduced only the common config). The same broken `@Inject`, listed in the `client` array of `caramel.client.mixins.json` against a client-only class that loads during `./gradlew runDatagen`, settles it.
