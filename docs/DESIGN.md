# Caramel design document

This is the living design document for Caramel. It holds the vision, the principles that follow from it, and a log of every idea the owner pitches or takes on. A contributor's idea is only a suggestion until the owner takes it on. Ideas are recorded here before they are implemented, and their entries are updated as the work moves along.

## Vision

Caramel rests on two pillars, set by the owner (CR4FTEX).

### Pillar 1: Streamline and improve what exists

> "Streamline and improve every concepts and already implemented features (The Deep Dark, Crafting and Smithing, Recipes, Blocks and Items, World Generation, etc.), without steering too far to the core identity and gameplay loop of the game."

In short: streamline and improve every existing concept and feature in the game without steering too far from its core identity and gameplay loop.

### Pillar 2: Bring in content from the spin-offs

> "Add content from the Minecraft spin-offs in order to improve barren areas and neglected established concepts of the original game, giving each dimension a fresh coat of paint and more incentives to explore and risk to be rewarded."

In short: use content from the Minecraft spin-off games to improve barren areas and neglected established concepts of the original game, giving each dimension a fresh coat of paint and more incentives to explore and risk to be rewarded.

## Guiding principles

These come only from the owner's words above. Check every idea against them.

- **Keep the core identity and gameplay loop.** Caramel changes Minecraft without steering too far from what the game is and how it is played.
- **Streamline and improve what exists.** Caramel streamlines and improves every concept and feature the game already has.
- **Risk should be rewarded.** Caramel gives more incentives to explore, and risk is rewarded.
- **Every dimension gets attention.** The Overworld, the Nether and the End each get a fresh coat of paint.

## Target areas

These are the areas the owner has named as targets. The owner's list ended with "etc.", so more areas will be added as ideas come in. Under each heading, list the IDs of the idea log entries that affect it.

### The Deep Dark

No ideas yet.

### Crafting and Smithing

No ideas yet.

### Recipes

No ideas yet.

### Blocks and Items

No ideas yet.

### World Generation

No ideas yet.

### Dimensions

#### Overworld

No ideas yet.

#### Nether

No ideas yet.

#### End

No ideas yet.

## Spin-off sources in scope

- Minecraft Dungeons
- Minecraft Legends
- Minecraft Earth
- Minecraft Story Mode

The owner may add others.

Per the owner, spin-off content is brought in to improve barren areas and neglected established concepts of the original game.

All art must be original. Never copy or rip textures, models or sounds from any of these games, or from any other game or mod. Recreating a concept in new, original art is fine. Caramel is a fan project and is not affiliated with or endorsed by Mojang Studios or Microsoft.

## Idea log

### How it works

1. The owner pitches an idea, or takes on an idea a contributor suggested.
2. Before any code is written, the idea gets an entry below with status **Pitched** and a faithful summary of the pitch. Do not add details the owner did not give.
3. Open design questions are put to the owner. The answers go under "Design notes and decisions".
4. The status is updated as work moves along, and the key classes and files are listed under "Implementation notes".
5. Dropped ideas stay in the log, with the reason they were dropped.
6. The entry's ID is added under each matching heading in "Target areas".

IDs are sequential (C-001, C-002, ...) and never reused. Entries are listed in ID order.

| Status | Meaning |
| --- | --- |
| Pitched | Recorded, not yet agreed on for implementation. |
| Planned | Design questions answered, ready to build. |
| In progress | Being implemented. |
| Done | Implemented, builds, and playtested by the owner. |
| Dropped | Not going ahead. The reason is noted in the entry. |

### Entry format

```markdown
### C-000: <Title>

- **Status:** Pitched | Planned | In progress | Done | Dropped
- **Area:** <one or more areas from "Target areas">
- **Spin-off source:** <game, or None>

**Pitch**

<The owner's pitch, summarized faithfully.>

**Design notes and decisions**

- <Questions asked, answers given, choices made and why.>

**Implementation notes**

- <Key classes and files, datagen providers, mixins (and why a mixin was needed), placeholder textures still to be replaced.>
```

### Entries

No entries yet.
