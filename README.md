# Caramel

Caramel is a Minecraft: Java Edition mod for Fabric with two goals. First, it
streamlines and improves concepts and features the game already has (the Deep
Dark, crafting and smithing, recipes, blocks and items, world generation and
more) without steering too far from the game's core identity and gameplay loop.
Second, it brings in content from the Minecraft spin-off games (Minecraft
Dungeons, Minecraft Legends, Minecraft Earth and Minecraft: Story Mode) to
improve barren areas and neglected established concepts of the original game,
giving each dimension a fresh coat of paint and more incentives to explore and
risk to be rewarded.

**Status:** early development. Nothing is playable yet.

## Requirements

- Minecraft: Java Edition 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API
- Java 25

## Building from source

You need JDK 25. Gradle is provided by the wrapper, so you do not need to
install it.

The repository is private, so cloning it needs a collaborator invite and git
signed in to your GitHub account. See
[Getting access](CONTRIBUTING.md#getting-access) in CONTRIBUTING.md.

```sh
git clone https://github.com/CR4FTEX/caramel.git
cd caramel
./gradlew build
```

The mod jar is written to `build/libs/` (for example
`caramel-0.1.0+26.3.jar`). On Windows, use `gradlew.bat` instead of
`./gradlew`.

## Running a development client

```sh
./gradlew runClient
```

This starts Minecraft with Caramel and Fabric API loaded. `./gradlew runServer`
starts a development server instead.

## Contributing

The repository is private. Contributors are invited as collaborators by the
owner, CR4FTEX. Help with both code and textures is welcome.

- [CONTRIBUTING.md](CONTRIBUTING.md): how to set up, code style, and how to
  submit changes.
- [docs/DESIGN.md](docs/DESIGN.md): the log of every idea the owner pitches
  or takes on. A contributor's idea is only a suggestion until the owner takes
  it on.
- [docs/TEXTURES.md](docs/TEXTURES.md): guidelines for textures and other art.

All art must be original. Do not copy textures, models or sounds from any
other game or mod, including the Minecraft spin-offs.

## License

- Code is licensed under the MIT License. See [LICENSE](LICENSE).
- Art assets (textures, models, animations, sounds and the icon) are licensed
  under CC BY-NC-SA 4.0. See [LICENSE-ASSETS](LICENSE-ASSETS).
- Entity model and animation code is kept in its own folder,
  `src/client/java/io/github/cr4ftex/caramel/client/model/`, and is covered by
  the art license (CC BY-NC-SA 4.0), not the MIT License.

Contributors keep the copyright to their work and license it under these same
terms by submitting it.

## Disclaimer

Caramel is not an official Minecraft product. It is not approved by or
associated with Mojang or Microsoft.
