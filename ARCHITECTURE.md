# Veneficium project structure and architecture

This document is meant as rough guide to understanding the code, designs, and similar around, and supporting veneficium

### Basic package layout
##### not everything is documented here, just stuff I didn't think was immediately obvious
- net/vene
    - Contains initializers and config
    - /compat
      - Stuff related to `veneficium -> mod` compat
      - /api
        - Stuff related to `mod -> veneficum` compat
    - /data
        - Stuff related to data generation, that ***doesn't*** need to be referenced later (lang files, dispenser behaviors, ect.)
        - If the data is referenced later its most likely on a dedicated class or `VeneMain.kt`
    - /recipe
        - Classes relating to the Weaving system (called `SCCS` internally, stands for **S**pell **C**omponent **C**rafting **S**ystem)
    - /magic
        - The largest and most important package, covers spells, and the things that handle them
        - /spell_components
            - Where spell components are stored, as well as their types
            - Each type gets a `<Type>Component` class, and a `<Type>ComponentCollection` object
        - /handling
            - Things that handle components 
            - Things components use to manage state
        - /event
            - Spell events for reacting to world/executor state changes
    - /common
        - Things that need to be shared between sides
        - /block
            - Blocks and their additional classes (`BlockEntity`s, ect.)
        - /item
            - Complex items as well as the `SpellProvider` interface (more on that in "The magic system internally")


### The magic system internally

The magic system is a mix of FP and OOP, mainly powered by `MagicEffect`s

`MagicEffect`s are essentially a wrapper around a `SpellRunnable` (typealias for a specific lambda signature).
They provide a single method that invokes the `SpellRunnable` as well as metadata and debug information.
These classes are [flyweights](https://en.wikipedia.org/wiki/Flyweight_pattern), and therefore it can be safely assumed that only 1 `MagicEffect` exists for any 1 `SpellRunnable` ***and/or*** spell name.
However, in most cases it will be more useful to think of them as their core, the `SpellRunnable` they are given to execute.
As such `MagicEffect` and `SpellRunnable` will be referenced as `MagicEffect` or simply "magic effect" for the rest of this document, unless specified specifically.

##### Understanding the SpellQueue

The spell queue is somewhat similar to a tiny VM.
When `SpellQueue#run` is invoked, the spell queue executes magic effects in order, and performs an action based on the returned `HandlerOperation`.
The `HandlerOperation` is then turned into a (silent) action, and an `OpResult` that instructs the spell queue to either stop or continue evaluating effects, as well as if it should evaluate the next effect or the same one.

For example, if we have a `SpellRunnable` that returns `HandlerOperation.REMOVE_CONTINUE`, the queue will
1. Remove that effect from the effect list
2. Evaluate the next effect (or, technically, the same index as we removed the existing effect immediately)

The `HandlerOperation.FREEZE` result stops the queue, but ***causes the queue to begin evaluation from that position instead of the start*** on next run

##### What is a SpellExecutor?

A spell executor is the physics and visibility part of the spell. It can be thought of as the glue pulling all the systems together.
It holds a spell queue, as well as a `SpellContext`, a common class for magic effects to save and read data to, including targets.
All existing spell executors are ticked at the end of every server tick.

##### Pulling it all together: an example spell casting routine

So, now that we understand the big classes and functions, lets run through an example.

We have a wand with the spells
1. Target ground
2. Dirt
3. Large explode

First, the spells are converted from their text/nbt version into `MagicEffect`s through lookup into the `VeneMain.ALL_COMPONENTS` list.
All these `MagicEffect`s are then pushed onto a `SpellQueue`, which is passed into a new `SpellExecutor`.

At the end of every tick, the executor goes to move, and every few physics steps calls `SpellQueue#run`. The effects dictate the logic, with "Target ground" pausing the queue every run until the executor hits the gound, before allowing it to pass through to other effects.
Once it passes to other effects, however, they have no pause logic, causing an large explosion of dirt at the impact point, meaning the executor is removed due to running out of effects.

##### A side note: The hell that is writing SpellRunnables

Spell runnables are lambdas, and wrapped in flyweights, meaning they cant save any state to themselves.
So in order to allow for data between steps, `SpellContext.data` holds a `ContextDataHandler`.
This class acts as a pseudo-map, allowing mapping between strings and any (based on a generic).
However, there is ***NO*** type safety guaranteed with a `ContextDataHandler` due to type erasure.
As such, all keys must make an attempt to be unique to avoid overwriting others with unexpected data.

However, as there is only a single location where data can be stored, it must be properly reset when a spell removes itself from the queue "manually".
This also has the consequence that a spell that is non-blocking ***AND*** stores state for itself causes problems *without fail*.
It will see the old version of its data, and there is no reasonable way to differentiate.
