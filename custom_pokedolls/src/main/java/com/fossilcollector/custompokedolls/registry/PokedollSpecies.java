package com.fossilcollector.custompokedolls.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PokedollSpecies implements StringIdentifiable {
    private static final Map<String, PokedollSpecies> REGISTRY = new LinkedHashMap<>();
    public static final PokedollSpecies DUMMY = new PokedollSpecies("dummy", 1.0f, false);

    private final String id;
    private final float scaleModifier;
    private final boolean hasShinyTexture;

    public PokedollSpecies(String id, float scaleModifier, boolean hasShinyTexture) {
        this.id = id;
        this.scaleModifier = scaleModifier;
        this.hasShinyTexture = hasShinyTexture;
    }

    public static Collection<PokedollSpecies> values() {
        return REGISTRY.values();
    }

    public static PokedollSpecies get(String id) {
        return REGISTRY.getOrDefault(id, DUMMY);
    }

    public static void register(PokedollSpecies species) {
        REGISTRY.put(species.getId(), species);
    }

    public static void scanAndRegisterUserModels(File baseDir) {
        REGISTRY.clear();
        File geoDir = new File(baseDir, "geo");
        File texturesDir = new File(baseDir, "textures");
        File animationsDir = new File(baseDir, "animations");

        if (!geoDir.exists()) geoDir.mkdirs();
        if (!texturesDir.exists()) texturesDir.mkdirs();
        if (!animationsDir.exists()) animationsDir.mkdirs();

        File readme = new File(baseDir, "README.txt");
        if (!readme.exists()) {
            try {
                java.nio.file.Files.writeString(readme.toPath(),
                    "=== Custom Pokedolls Instructions ===\n" +
                    "1. Place your .geo.json model files into the 'geo' folder.\n" +
                    "2. Place your texture .png files into the 'textures' folder.\n" +
                    "3. (Optional) Place your shiny texture as <name>_shiny.png in 'textures'.\n" +
                    "4. Restart the game to load your statues!\n");
            } catch (Exception ignored) {}
        }

        System.out.println("==================================================");
        System.out.println("[CustomPokedolls DEBUG] Scanning directory for models: " + baseDir.getAbsolutePath());
        List<File> geoFilesList = new ArrayList<>();
        File[] gfDir = geoDir.listFiles((dir, name) -> name.endsWith(".geo.json"));
        if (gfDir != null) Collections.addAll(geoFilesList, gfDir);
        File[] gfBase = baseDir.listFiles((dir, name) -> name.endsWith(".geo.json"));
        if (gfBase != null) Collections.addAll(geoFilesList, gfBase);

        if (!geoFilesList.isEmpty()) {
            for (File geoFile : geoFilesList) {
                String name = geoFile.getName();
                String id = name.substring(0, name.length() - ".geo.json".length()).toLowerCase().replace(" ", "_");
                
                boolean shinyExists = new File(texturesDir, id + "_shiny.png").exists() || new File(baseDir, id + "_shiny.png").exists() || new File(baseDir, "textures/" + id + "_shiny.png").exists();
                float scale = calculateNormalizedScale(geoFile);
                
                PokedollSpecies species = new PokedollSpecies(id, scale, shinyExists);
                register(species);
                System.out.println("[CustomPokedolls DEBUG] -> Loaded species ID: '" + id + "' (scale: " + scale + ", shiny texture: " + shinyExists + ")");
            }
        }
        if (REGISTRY.isEmpty()) {
            System.out.println("[CustomPokedolls DEBUG] WARNING: No custom .geo.json files found in " + baseDir.getAbsolutePath());
        } else {
            System.out.println("[CustomPokedolls DEBUG] Successfully loaded " + REGISTRY.size() + " custom pokedoll species!");
        }
        System.out.println("==================================================");
    }

    private static float calculateNormalizedScale(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            com.google.gson.JsonObject data = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
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
        } catch (Exception e) {
            System.err.println("Failed to compute normalized scale from file " + file.getName() + ": " + e.getMessage());
        }
        return 1.0F;
    }

    public String getId() { return id; }
    public float getScaleModifier() { return scaleModifier; }
    public boolean hasShinyTexture() { return hasShinyTexture; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokedollSpecies that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String asString() { return id; }

    public Identifier getGeoModelPath() {
        return Identifier.of("custom_pokedolls", "geo/" + this.id + ".geo.json");
    }

    public Identifier getTexturePath(boolean isShiny) {
        if (isShiny && this.hasShinyTexture) {
            return Identifier.of("custom_pokedolls", "textures/block/" + this.id + "_shiny.png");
        }
        return Identifier.of("custom_pokedolls", "textures/block/" + this.id + ".png");
    }

    public String getFormattedName(boolean isShiny) {
        String[] words = this.id.split("_");
        StringBuilder sb = new StringBuilder();
        if (isShiny) {
            sb.append("Shiny ");
        }
        for (String word : words) {
            if (!word.isEmpty()) {
                if (sb.length() > (isShiny ? "Shiny ".length() : 0)) {
                    sb.append(" ");
                }
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    sb.append(word.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }
}
