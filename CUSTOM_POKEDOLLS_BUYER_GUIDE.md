# 💎 CUSTOM POKEDOLLS — ULTIMATE DYNAMIC 3D STATUES MOD 💎
*An interactive, customizable 3D statue mod designed for Minecraft Fabric 1.21.1!*

---

### ✨ **WHY YOUR SERVER NEEDS THIS**
Looking to add unique collectibles, decorative items, and high-value trophies to your server? 
**Custom Pokedolls** allows server administrators to load dynamic 3D statues directly from the game directory using GeckoLib models and textures without ever having to rebuild the mod JAR or restart the server!

Players can right-click any placed statue with the **Pokedoll Chisel** to customize its scale, offsets, rotation angle, luminescence (glowing state), and GeckoLib animated poses. This is the perfect tool for boosting player VIP store purchases, server economies, and chat visual variety!

---

### 🔥 **VERIFIED CORE FEATURES**
- 📂 **Dynamic Asset Loading:** Place your custom 3D `.geo.json` models and `.png` textures directly into `.minecraft/custom_pokedolls/`. No restarts required!
- 🏷️ **Automatic Item Naming:** Display names are automatically derived and formatted from your model file names (e.g., `charizard_doll` ➔ **"Charizard Doll"**).
- 🌟 **Optional Shiny Variants:** Automatically creates a Shiny variant item in your creative tab when a corresponding `_shiny.png` texture is detected.
- 🔄 **Live In-Game Reloading:** Run `/pokedolls reload` to scan the folders, register new statues, and synchronize virtual resource packs with all clients on the fly.
- 🛠️ **Interactive GUI Chisel:** Shift-right click or right-click any placed statue with the Chisel to adjust X/Y/Z offsets, 360° rotation, glowing effects, and animations.
- 🛡️ **Crash Protection:** Built-in fallback rendering prevents game crashes or chunk corruption if placed model files are accidentally renamed or deleted.

---

### 💬 **COMMAND REFERENCE (`/pokedolls`)**
#### 👤 **Admin Commands (OP Level 2+ / Permission Node `custom_pokedolls.command.reload`)**
- `/pokedolls reload` (or `/custom_pokedolls reload`) — Scans the external directory for new geometry/textures, dynamically registers blocks, and syncs asset bundles to all connected players.

---

### ⚙️ **DIRECTORY CONFIGURATION (custom_pokedolls/)**
Custom Pokedolls dynamically registers statues by parsing your custom models. Place your assets in the game root under the following structure:

```text
.minecraft/
└── custom_pokedolls/
    ├── geo/
    │   ├── tyrunt_pokedoll.geo.json      # GeckoLib 3D Geometry model
    │   └── charizard_doll.geo.json
    └── textures/
        └── block/
            ├── tyrunt_pokedoll.png       # Base texture
            ├── tyrunt_pokedoll_shiny.png # Optional Shiny variant texture
            └── charizard_doll.png
```

---

### 🚀 **ROADMAP & TODO (COMING SOON!)**
We are continuously developing and improving Custom Pokedolls! Here is what is planned for future updates:
- ⚡ **Interactive Animations:** Trigger animation states based on player clicks, proximity, or redstone signals.
- 📦 **Collision Bounds:** Custom bounding box definitions parsed directly from `.geo.json` configurations.
- 🎨 **Multi-Texture Variants:** Select between multiple texture sets for the same model inside the Chisel GUI.
- 💻 **Interactive Click Commands:** Execute custom console or player commands automatically when players right-click or interact with a placed 3D statue.


---

### 🛒 **PRICING & HOW TO BUY**
> 💰 **Price:** **$25.00 USD** *(One-Time Purchase / Server License)*  
> 📩 **How to buy:** **DM ME directly on Discord to purchase your license and get instant access to the files!**
