package com.fossilcollector.pokedolls.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public enum PokedollSpecies implements StringIdentifiable {
    AERODACTYL("aerodactyl", 1.8F, false),
    AMAURA("amaura", 1.3F, false),
    ANORITH("anorith", 0.8F, false),
    ARCHEN("archen", 0.9F, false),
    ARCHEOPS("archeops", 1.4F, false),
    ARCTOVISH("arctovish", 2.0F, false),
    ARCTOZOLT("arctozolt", 2.3F, false),
    ARMALDO("armaldo", 1.5F, false),
    AURORUS("aurorus", 2.7F, false),
    BASTIODON("bastiodon", 1.3F, false),
    CARRACOSTA("carracosta", 1.2F, false),
    CRADILY("cradily", 1.5F, false),
    CRANIDOS("cranidos", 0.9F, false),
    DRACOVISH("dracovish", 2.3F, false),
    DRACOZOLT("dracozolt", 1.8F, false),
    KABUTO("kabuto", 0.5F, false),
    KABUTOPS("kabutops", 1.3F, false),
    LILEEP("lileep", 1.0F, false),
    MEW("mew", 0.4F, false),
    MEWTWO("mewtwo", 2.0F, false),
    OMANYTE("omanyte", 0.4F, false),
    OMASTAR("omastar", 1.0F, false),
    RAMPARDOS("rampardos", 1.6F, false),
    SHIELDON("shieldon", 0.5F, false),
    TIRTOUGA("tirtouga", 0.7F, false),
    TYRANTRUM("tyrantrum", 2.5F, false),
    TYRUNT_POKEDOLL("tyrunt_pokedoll", 0.8F, true);

    private final String id;
    private final float legacyScaleModifier;
    private final boolean prefixShinyName;
    private float cachedNormalizedScale = -1.0F;

    PokedollSpecies(String id, float legacyScaleModifier, boolean prefixShinyName) {
        this.id = id;
        this.legacyScaleModifier = legacyScaleModifier;
        this.prefixShinyName = prefixShinyName;
    }

    public String getId() { return id; }

    public float getScaleModifier() {
        if (this.cachedNormalizedScale < 0) {
            this.cachedNormalizedScale = calculateNormalizedScale();
        }
        return this.cachedNormalizedScale;
    }

    private float calculateNormalizedScale() {
        try (java.io.InputStream is = PokedollSpecies.class.getResourceAsStream("/assets/static_pokedolls/geo/" + this.id + ".geo.json")) {
            if (is != null) {
                com.google.gson.JsonObject data = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                com.google.gson.JsonArray bones = data.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones");
                double minY = 99999.0, maxY = -99999.0;
                double minX = 99999.0, maxX = -99999.0;
                double minZ = 99999.0, maxZ = -99999.0;
                if (bones != null) {
                    for (com.google.gson.JsonElement boneElem : bones) {
                        com.google.gson.JsonArray cubes = boneElem.getAsJsonObject().getAsJsonArray("cubes");
                        if (cubes != null) {
                            for (com.google.gson.JsonElement cubeElem : cubes) {
                                com.google.gson.JsonObject cube = cubeElem.getAsJsonObject();
                                com.google.gson.JsonArray origin = cube.getAsJsonArray("origin");
                                com.google.gson.JsonArray size = cube.getAsJsonArray("size");
                                double ox = origin.get(0).getAsDouble();
                                double oy = origin.get(1).getAsDouble();
                                double oz = origin.get(2).getAsDouble();
                                double sx = size.get(0).getAsDouble();
                                double sy = size.get(1).getAsDouble();
                                double sz = size.get(2).getAsDouble();
                                if (sx <= 0 || sy <= 0 || sz <= 0) continue;
                                minX = Math.min(minX, ox); maxX = Math.max(maxX, ox + sx);
                                minY = Math.min(minY, oy); maxY = Math.max(maxY, oy + sy);
                                minZ = Math.min(minZ, oz); maxZ = Math.max(maxZ, oz + sz);
                            }
                        }
                    }
                }
                double height = Math.max(0.1, maxY - minY);
                double maxDim = Math.max(height, Math.max((maxX - minX) * 0.7, (maxZ - minZ) * 0.7));
                return (float) (16.0 / maxDim);
            }
        } catch (Exception e) {
            System.err.println("Failed to compute normalized scale for " + this.id + ": " + e.getMessage());
        }
        return 1.0F;
    }

    @Override
    public String asString() { return id; }

    public Identifier getGeoModelPath() {
        return Identifier.of("static_pokedolls", "geo/" + this.id + ".geo.json");
    }

    public Identifier getTexturePath(boolean isShiny) {
        if (!isShiny) {
            return Identifier.of("static_pokedolls", "textures/block/" + this.id + ".png");
        }
        String fileName = this.prefixShinyName ? ("shiny_" + this.id) : (this.id + "_shiny");
        return Identifier.of("static_pokedolls", "textures/block/" + fileName + ".png");
    }
}
