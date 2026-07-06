# Custom Pokedolls Mod

A Minecraft Fabric (1.21.1) mod that enables dynamic loading of custom 3D decorative statues (Pokedolls) directly from your file system using GeckoLib models and textures.

## Features

* **Dynamic External Asset Loading:** Load custom 3D `.geo.json` models and `.png` textures from an external folder without modifying the mod JAR or restarting the game.
* **Automatic Item Naming:** In-game item and block display names are automatically derived and formatted from your model file names. Underscores are converted to spaces and every word is capitalized automatically.
* **Optional Shiny Variants:** Automatically creates Shiny item variants in the creative tab when a corresponding shiny texture is present.
* **Live In-Game Reloading:** Use in-game commands to scan the folder and register new statues instantly.
* **Interactive Customization GUI:** Use the built-in **Pokedoll Chisel** tool to adjust scale, rotation, offsets, poses, and glowing properties of placed statues.
* **Crash Protection:** Includes a built-in fallback rendering mechanism that prevents game crashes if a model or texture file is removed while a block is placed in the world.

---

## Installation & Setup

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.1 and the [Fabric API](https://modrinth.com/mod/fabric-api).
2. Install [GeckoLib](https://modrinth.com/mod/geckolib) for Minecraft 1.21.1.
3. Place the `custom_pokedolls` JAR file into your `.minecraft/mods/` directory.
4. Launch the game once. The mod will automatically generate a folder named `custom_pokedolls/` in your root game directory (`.minecraft/custom_pokedolls/`).

---

## Adding Custom Statues

To add custom Pokedolls to the game, place your GeckoLib assets into the `custom_pokedolls/` directory inside your game folder. You can organize them using subdirectories or place them directly in the root folder.

### Recommended Directory Structure:
```text
.minecraft/
└── custom_pokedolls/
    ├── geo/
    │   ├── my_statue.geo.json
    │   └── charizard_doll.geo.json
    └── textures/
        └──my_statue.png
        └── my_statue_shiny.png
        └──charizard_doll.png
```

### Naming Conventions & Display Names
* **Base Models:** Place your `.geo.json` model file in `custom_pokedolls/geo/` (or the root `custom_pokedolls/` folder) and the matching `.png` texture in `custom_pokedolls/textures/block/`.
* **Automatic Formatting:** The mod formats the file ID into an clean display name:
  * `my_statue.geo.json` ➔ **"My Statue"**
  * `charizard_doll.geo.json` ➔ **"Charizard Doll"**
  * `ancient_fossil_statue.geo.json` ➔ **"Ancient Fossil Statue"**
* **Shiny Variants:** To add a Shiny variant, include a second texture file with the `_shiny.png` suffix matching the base model name (e.g., `my_statue_shiny.png`).
  * When detected, the mod registers a Shiny variant item in the creative tab with the **"Shiny "** prefix (e.g., **"Shiny My Statue"**).
  * If no `_shiny.png` texture is provided, only the regular variant is created.

---

## In-Game Commands

Requires Operator (OP) permission level 2 or singleplayer cheat permissions:

* `/pokedolls reload` (or `/custom_pokedolls reload`)
  * Scans the external `custom_pokedolls/` directory for newly added `.geo.json` and `.png` files.
  * Registers new block and item entries dynamically.
  * Updates existing model properties and synchronizes the virtual resource pack across server and client without requiring a game restart.

---

## Using the Pokedoll Chisel

The **Pokedoll Chisel** is an in-game tool available in the **Custom Pokedolls** creative tab.

1. Place any custom Pokedoll block in the world.
2. Hold the **Pokedoll Chisel** in your main hand and **Right-Click** the placed statue.
3. The **Pokedoll Customization Screen** GUI will open, offering the following controls:
   * **Position Offsets:** Adjust the X, Y, and Z rendering offsets of the statue.
   * **Rotation Slider:** Smoothly rotate the statue from 0° to 360°.
   * **Glowing Effect:** Toggle block luminescence (emits light level 15 when enabled).
   * **Auto-Rotate (Showcase):** Enable a continuous spinning animation for display purposes.
   * **Pose Selection:** Cycle through available animation poses defined in your model.
   * **Reset Controls:** Reset scale, rotation, and offsets back to default values.

---

## Technical Details & Compatibility

* **Virtual Resource Pack:** The mod operates a built-in runtime resource pack (`Custom Pokedolls Virtual Pack`) that maps the external directory into Minecraft's resource system under the `custom_pokedolls` namespace.
* **Scale Normalization:** The mod parses bounding box dimensions from loaded `.geo.json` files and computes a normalized render scale so that models of differing dimensions display proportionally in inventory and in the world.
* **Multiplayer Support:** When installed on a dedicated server, executing `/pokedolls reload` automatically sends network synchronization packets (`ReloadResourcesPayload`) to all connected clients to reload their local geometry and texture caches.
