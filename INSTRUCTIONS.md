# 🔮 Custom Pokedolls Mod Instructions (v1.0.1-fix-stable)

Official documentation and user guide for the Minecraft 1.21.1 Fabric mod **Custom Pokedolls**.

---

## 🚀 Installation & Setup

1. **Install Dependencies:**
   * Install **Fabric Loader** (v0.16.5 or newer recommended) for Minecraft 1.21.1.
   * Add **Fabric API** (v0.106.0 or newer recommended) to your `mods` folder.
   * Add **GeckoLib** (v4.7.7 or newer recommended) to your `mods` folder.
2. **Install Custom Pokedolls Mod:**
   * Place `custom-pokedolls-1.0.1-fix-stable.jar` into your `.minecraft/mods/` directory.
3. **Launch the Game:**
   * Run Minecraft once to automatically generate the `custom_pokedolls/` configuration folder in your game root directory.

---

## 📁 Adding Custom Statues & Assets

Place your 3D models and textures in the `custom_pokedolls/` directory located in the root of your Minecraft installation (or dedicated server root).

### Recommended Directory Structure:
```text
.minecraft/ (or server root)
└── custom_pokedolls/
    ├── geo/
    │   ├── tyrunt_pokedoll.geo.json      # GeckoLib 3D Geometry model
    │   └── charizard_doll.geo.json
    └── textures/
        ├── tyrunt_pokedoll.png       # Base texture
        ├── tyrunt_pokedoll_shiny.png # Optional Shiny variant texture
        └── charizard_doll.png
```

### Asset Rules & Conventions:
* **Geometry Folder (`geo/`):** Model files must end in `.geo.json` (e.g., `tyrunt_pokedoll.geo.json`).
* **Textures Folder (`textures/`):** Base texture files must match the model's file prefix and end in `.png` (e.g., `tyrunt_pokedoll.png`). 
  * *Note: A model will only load if both the geometry and base texture are present.*
* **Automatic Formatting:** In-game item names are automatically converted from filenames:
  * `tyrunt_pokedoll` ➔ **"Tyrunt Pokedoll"**
  * `charizard_doll` ➔ **"Charizard Doll"**
* **Shiny Variants:** Add a texture named `<prefix>_shiny.png` (e.g., `tyrunt_pokedoll_shiny.png`). The mod will automatically register a separate item named **"Shiny Tyrunt Pokedoll"** with the shiny texture applied.

---

## ⚙️ Dedicated Server & Registry Sync (Fixes in v1.0.1)

In older versions, adding new models on the server would cause clients to get disconnected with the error:
`Connection Lost: Received X registry entries that are unknown to this client`.

In **v1.0.1-fix-stable**, this is resolved:
1. **Server Setup:** Place your assets in `custom_pokedolls/` on the server.
2. **Client Setup:** Place the same assets in `custom_pokedolls/` on the client.
3. **Smart Registry Handshake:** When a player joins a server, the mod's client-side `RegistrySyncMixin` detects any server-registered Pokedoll items or blocks missing from the client and automatically registers safe placeholders on the fly before Fabric's handshake check triggers. This ensures you never get registry mismatch disconnects!
4. **On-the-fly Sync:** Run `/pokedolls reload` on the server, and the server will automatically notify all connected players to download any missing models and reload textures without anyone needing to reconnect or restart.

---

## 💬 Command Reference

Commands require operator privileges (OP Level 2+ / command permission):

* `/pokedolls reload` (or `/custom_pokedolls reload`)
  * Scans the external directory for new model files and textures.
  * Registers newly added species and block/item variants.
  * Cleans up placed instances of deleted species from loaded chunks.
  * Broadcasts synchronization packets to all connected clients.

---

## 🔨 Using the Pokedoll Chisel

1. Place any custom pokedoll statue block in the world.
2. Hold the **Pokedoll Chisel** (found in the Custom Pokedolls creative tab) in your main hand.
3. Right-click the statue to open the **Pokedoll Customization Screen** GUI.
4. **GUI Controls:**
   * **Scale Slider:** Dynamically resize the statue.
   * **Rotation Slider:** Rotate the statue 360 degrees.
   * **Auto-Rotate:** Toggle continuous rotation.
   * **Position Offsets:** Fine-tune X, Y, and Z render offsets.
   * **Glowing Effect:** Toggle light level 15 on the statue.
   * **Pose Selection:** Select which GeckoLib animation pose should play (e.g., `sleep`, `battle_idle`, etc.).
