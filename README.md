# Caramel

Caramel is a Minecraft: Java Edition mod for Fabric that builds atop vanilla. It
brings the game to a higher level of polish, gives neglected features the
attention they deserve, and takes parity with Bedrock Edition further than
Mojang's own tweaks, without steering too far from the game's core identity and
gameplay loop. It is a mod first, an expansion second and an infrastructure
third: it follows the newest version of the game it can, it adds and improves
across the whole game, and in the long run it aims to let you switch off what
you don't want and to work alongside other mods.

It has five goals:

1. **Optimize your workflow and craft your dreams.** Better crafting recipes
   and work blocks, new blocks, items and tools, and consistent block families,
   so building costs less of your resources and time.
2. **Connect the world to create your stories.** Structures, mobs and lore
   connect with each other and with your progress, so each one is worth the
   journey.
3. **Fill up the blanks and even out the world.** Every biome of every
   dimension gets the same share of quality and its own substance, the End
   above all.
4. **Creating a living world.** The world catches up with the world-building,
   audio and visual design of the spin-offs, and content that was left out
   comes in: from the spin-offs (Minecraft Dungeons, Minecraft Legends,
   Minecraft Earth and Minecraft: Story Mode), the options that lost the yearly
   votes, and the biomes of Minecraft's concept art.
5. **Innovation and experimentation.** Caramel expands what the game has,
   brings back scrapped content, and adds ideas of its own.

**Status:** early development.

## How Caramel is made

- Every feature is designed by people first: the owner, CR4FTEX, pitches every
  idea or takes it on from a contributor, and decides how it is built. Claude,
  an AI, writes most of the code, contributors are welcome to write code too,
  and every feature is playtested.
- Caramel never generates or ships AI-generated art. Every texture, model,
  animation and sound is made by one of Caramel's artists, commissioned from a
  third-party artist, or bought from a third-party artist who allows it to be
  shared under Caramel's art license (CC BY-NC-SA 4.0).

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
other game or mod, including the Minecraft spin-offs, and do not use AI to make
art.

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
