# Art guide

How to make textures, models, animations and sounds for Caramel. For access, the git workflow and licensing, see [CONTRIBUTING.md](../CONTRIBUTING.md).

## Originality rule

All art in Caramel must be original work: made for this project, or bought from or commissioned from an artist under the license rule below. This rule is strict.

- Never copy, rip, trace or edit textures, models or sounds from Minecraft Dungeons, Minecraft Legends, Minecraft Earth, Minecraft Story Mode, or any other game, mod or resource pack.
- That includes vanilla Minecraft: match its style, but draw your own pixels instead of editing Mojang's files.
- Recreating a concept from a spin-off in new, original art is fine. You may look at the original to understand the concept, but never reuse its pixels, geometry or audio.
- No AI-generated art, placeholders included: Caramel never ships AI-generated assets. Never use AI tools to make art for Caramel.
- Art bought from or commissioned from an artist ships only when its license, or the artist's written agreement, lets it ship under CC BY-NC-SA 4.0 like the rest of Caramel's art. Many stock asset licenses do not allow that, because they forbid redistributing purchased files without the seller's permission and do not allow sublicensing, so check before buying.
- If you are unsure whether something counts, ask in the issue before you start.

Why: art from other games and mods belongs to the people and studios who made it. Using it would put the project at risk of being taken down, and it would disrespect their work. Copied art is rejected, and removed if it is found later.

## Style target: vanilla Minecraft

Caramel art should look like it belongs in the base game. This is the default, because Caramel does not steer too far from the game's core identity and gameplay loop. The owner can decide otherwise for any idea, and that decision is recorded in the idea's entry in [DESIGN.md](DESIGN.md).

- **Size.** Block and item textures are 16x16 pixels unless there is a clear reason to differ. No high-resolution textures.
- **Entities.** Use the same pixel density as vanilla: one texture pixel per model pixel. Blockbench sets the texture size and UV layout for you.
- **Palette.** Keep it limited: a handful of shades per material. Reuse the same color ramps across related textures so they sit well together.
- **Silhouette.** The shape must read clearly at actual size, in the hotbar and inventory, and from a distance in the world. Check at 1x zoom, not only zoomed in.
- **Lighting.** Light comes from the top-left, as in vanilla: highlights on top and left edges, shadows on bottom and right. On block textures, keep the overall brightness even (the game already darkens the sides and bottom of blocks) and apply the top-left lighting to details such as cracks, bevels and bumps.
- **Hand-placed pixels.** No smooth gradients, no anti-aliasing, no soft brushes, blur or noise filters, no photo textures. Shade in distinct steps of color.
- **Transparency.** Pixels are either fully opaque or fully transparent, except on textures that are meant to be see-through, like glass.
- **Tiling.** Full-block textures must tile seamlessly, without visible seams or a standout detail that repeats obviously across a wall or floor.
- **Compare in game.** Place your block or item next to vanilla ones. If it looks like a different style, adjust it.

## Tools

- [Blockbench](https://www.blockbench.net/) (free) for models, and for painting textures directly on a model.
- A pixel art editor for textures: Aseprite, LibreSprite, Pixelorama or similar.
- A general image editor (GIMP, Krita, Photoshop) also works if you use the pencil tool with anti-aliasing turned off.

## File format and naming

- Save textures as PNG with an alpha channel (RGBA, 8 bits per channel).
- File names are lowercase_snake_case and match the registry id. A block registered as `caramel:example_block` uses `textures/block/example_block.png`; an item `caramel:example_item` uses `textures/item/example_item.png`.
- When one block needs several textures, use vanilla-style suffixes: `_top`, `_bottom`, `_side`, `_front` (for example `example_block_top.png`).
- No capital letters, spaces or special characters. Minecraft cannot load resource files named that way.

## Where files go

All paths are under `src/main/resources/assets/caramel/`.

| What | Folder |
| --- | --- |
| Block textures | `textures/block/` |
| Item textures | `textures/item/` |
| Entity textures | `textures/entity/` (a subfolder per entity is fine) |
| Custom block and item models | `models/block/`, `models/item/` |
| Sounds | `sounds/` |
| Mod icon | `icon.png` |

**Editable source files** (`.bbmodel`, `.aseprite`, layered images) are the exception: they go in `art-source/` at the top of the repository, not under `src/`. Everything under `src/main/resources/` is packed into the mod jar, and `art-source/` is not.

**Models.** Most block and item models (plain cubes, flat items) and blockstates are generated by datagen into `src/main/generated/assets/caramel/`. For those you only provide the texture; a coder adds it to the datagen providers. For a custom shape, build the model in Blockbench as a Java Block/Item model, export it, and save it in `models/block/` or `models/item/` under the registry id. Textures in the model are referenced as `caramel:block/<name>` or `caramel:item/<name>`. Entity models and animations are Java code in Minecraft, so make them in Blockbench's Modded Entity format and include the `.bbmodel` file with your submission; a coder turns it into code. That code is kept in its own folder, `src/client/java/io/github/cr4ftex/caramel/client/model/`, which is covered by the art license (CC BY-NC-SA 4.0), not the MIT License.

**Sounds.** Use Ogg Vorbis (`.ogg`) files with lowercase_snake_case names. Make them mono if they play at a position in the world, because stereo sounds do not fade with distance. A coder registers them in `sounds.json` and in code.

## Animated textures

Stack the frames vertically in one PNG. Each frame is square and as wide as the image, so a 16x16 texture with 4 frames is a 16x64 image. Next to it, add a file with the same name plus `.mcmeta` (`example_block.png` gets `example_block.png.mcmeta`):

```json
{
	"animation": {
		"frametime": 2
	}
}
```

- `frametime` is how many game ticks each frame is shown (20 ticks = 1 second). The default is 1.
- An optional `"frames"` list, such as `[0, 1, 2, 1]`, sets a custom frame order.
- `"interpolate": true` blends between frames. Use it sparingly, since it produces smooth in-between colors.

## Submitting art

- **With git:** follow the workflow in [CONTRIBUTING.md](../CONTRIBUTING.md). Branch off `main`, add your files to the folders above, and open a pull request. Include an in-game screenshot if you can: run `./gradlew runClient` and press F2; screenshots are saved in `run/screenshots/`.
- **Without git, through an issue:** put all your files, including the editable source files, in one `.zip` and attach it to the related GitHub issue (or open an Art / texture issue). GitHub does not accept `.ogg` or `.mcmeta` files as issue attachments, but it accepts a `.zip`. A coder will add your files to the project and credit you.
- **Without git, on the GitHub website:** upload your files and open a pull request from your browser.
  1. Open the repository on GitHub and go to `src/main/resources/assets/caramel/`, then into the folder your files belong in (see "Where files go"). If that folder does not exist yet, stop at the deepest folder that does. Upload only textures, models and sounds here; editable source files come in step 8.
  2. Click Add file > Upload files.
  3. Drag your files onto the page. If some folders do not exist yet, create them on your computer with your files inside, then drag the top new folder onto the page. For example, if you stopped at `src/main/resources/` because there is no `assets` folder yet, make `assets/caramel/textures/block/` on your computer, put `example_block.png` in `block`, and drag `assets` onto the page.
  4. Below the commit message, choose to create a new branch for this commit and start a pull request, and name the branch `art/short-name`. Never commit directly to the `main` branch.
  5. Click Propose changes. On the next page, fill in the pull request template and click Create pull request.
  6. In the pull request, open the Files changed tab and check that each file is in the right folder. If one is not, say so in the pull request.
  7. To change a file after the pull request is open, pick your `art/short-name` branch in the branch menu above the file list, go to the file's folder, upload the new version with the same file name, and choose to commit directly to your `art/short-name` branch. The pull request updates by itself.
  8. Add your editable source files the same way: on your `art/short-name` branch, go to `art-source/`, click Add file > Upload files, and commit directly to your `art/short-name` branch.
- Include your editable source files (`.bbmodel`, `.aseprite`, layered images), so the art can be adjusted later. They go in `art-source/` (see "Where files go"). On the issue route, put them in your `.zip` with the rest.
- Placeholder art is welcome, because it lets features be tested before final art exists. Like all Caramel art, it must be made by a person, not by AI. Mark it as placeholder in the pull request title or description so it gets replaced later.
- By submitting art, you license it under CC BY-NC-SA 4.0 (see [LICENSE-ASSETS](../LICENSE-ASSETS)) and confirm that it is your own original work. You keep the copyright. For bought or commissioned art, name the artist and include the license or written agreement that lets it ship under CC BY-NC-SA 4.0 instead; the copyright stays with the artist unless they transferred it.
