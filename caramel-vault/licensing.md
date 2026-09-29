# Licensing

Caramel has two licenses: code is MIT (`LICENSE`), and art is CC BY-NC-SA 4.0 (`LICENSE-ASSETS`). Contributors keep their copyright and license their work under the same terms (`CONTRIBUTING.md`, "Licensing of contributions").

## What each license covers

- `LICENSE` is the plain MIT text, with no scope statement of its own.
- `LICENSE-ASSETS` opens with a preamble the project wrote, above the Creative Commons legal code. The preamble sets the scope: every art file under `src/main/resources/assets/caramel/`, the entity model and animation code under `src/client/java/io/github/cr4ftex/caramel/client/model/`, and the source art in `art-source/`. Apart from that model code, it leaves all source code to MIT.
- The preamble does not name `src/main/generated/assets/caramel/`, where datagen writes block and item models and blockstates (`docs/TEXTURES.md`, "Where files go").
- The legal code below the preamble is the official text, copied exactly: it matches https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt byte for byte. A change of scope is a change to the preamble, never to the legal code.
- Entity model and animation code counts as art. Minecraft entity models and animations are Java code, and a coder turns the artist's Blockbench model into that code (`docs/TEXTURES.md`, "Where files go", the Models paragraph). Without this rule that code would fall under the MIT `LICENSE`, which permits selling copies (`LICENSE`).
- Editable source art (`.bbmodel`, `.aseprite`, layered images) lives in `art-source/` at the repository root (`art-source/README.md`). The mod jar takes its files from `src/`, plus the two license files at the root that the `jar` block adds; the `-sources` jar takes its files from `src/` only (see [[build]]). Nothing in `art-source/` ships (`unzip -l`).

## How the jars carry them

- `fabric.mod.json` declares `"license": ["MIT", "CC-BY-NC-SA-4.0"]`.
- The `jar` block in `build.gradle` copies both files into the mod jar as `LICENSE_caramel` and `LICENSE-ASSETS_caramel`.
- The `-sources` jar carries no license file (see [[build]]). CI uploads it next to the mod jar (see [[ci]]).

## Traps

- **A missing license file drops out of the jar without an error.** `from("LICENSE-ASSETS")` adds nothing when the file does not exist, and the build still passes: a scratch copy built without `LICENSE-ASSETS` produced a jar holding only `LICENSE_caramel`. After a change to the license files or the `jar` block, `unzip -l` on a freshly built mod jar must list both (see [[build]]).
