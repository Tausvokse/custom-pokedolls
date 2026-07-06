package com.fossilcollector.custompokedolls.registry;

import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DirectoryVirtualResourcePack extends DirectoryResourcePack {
    private static final String DUMMY_GEO_JSON = "{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.dummy\",\"texture_width\":16,\"texture_height\":16,\"visible_bounds_width\":1,\"visible_bounds_height\":1,\"visible_bounds_offset\":[0,0.5,0]},\"bones\":[{\"name\":\"root\",\"pivot\":[0,0,0],\"cubes\":[{\"origin\":[-4,0,-4],\"size\":[8,8,8],\"uv\":[0,0]}]}]}]}";
    private static final byte[] DUMMY_PNG = new byte[] {
        (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4,
        (byte)0x89, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x44, 0x41, 0x54, 0x78, (byte)0xDA, 0x63, 0x64, 0x60, 0x60, 0x60,
        0x00, 0x00, 0x00, 0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte)0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45,
        0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
    };

    private final File baseDir;

    public DirectoryVirtualResourcePack(ResourcePackInfo info, File baseDir) {
        super(info, baseDir.toPath());
        this.baseDir = baseDir;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        Set<String> namespaces = new HashSet<>(super.getNamespaces(type));
        namespaces.add("custom_pokedolls");
        return namespaces;
    }

    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        if (segments.length > 0 && "pack.mcmeta".equals(segments[0])) {
            String json = "{\"pack\":{\"pack_format\":34,\"description\":\"Custom Pokedolls Virtual Pack\"}}";
            return () -> new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        }
        return super.openRoot(segments);
    }

    @Override
    public <T> T parseMetadata(net.minecraft.resource.metadata.ResourceMetadataReader<T> metaReader) throws IOException {
        InputSupplier<InputStream> supplier = this.openRoot("pack.mcmeta");
        if (supplier != null) {
            try (InputStream stream = supplier.get()) {
                return AbstractFileResourcePack.parseMetadata(metaReader, stream);
            }
        }
        return super.parseMetadata(metaReader);
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if ("custom_pokedolls".equals(id.getNamespace())) {
            String path = id.getPath();
            // 1. Geo files: e.g. "geo/my_statue.geo.json"
            if (path.startsWith("geo/")) {
                String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path.substring("geo/".length());
                File file = new File(this.baseDir, "geo/" + fileName);
                if (!file.exists()) file = new File(this.baseDir, fileName);
                if (file.exists()) {
                    File finalFile = file;
                    return () -> new FileInputStream(finalFile);
                }
                return () -> new ByteArrayInputStream(DUMMY_GEO_JSON.getBytes(StandardCharsets.UTF_8));
            }
            // 2. Textures: e.g. "textures/block/my_statue.png" or "textures/my_statue.png"
            if (path.startsWith("textures/")) {
                String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path.substring("textures/".length());
                File file = new File(this.baseDir, "textures/" + fileName);
                if (!file.exists()) file = new File(this.baseDir, fileName);
                if (file.exists()) {
                    File finalFile = file;
                    return () -> new FileInputStream(finalFile);
                }
                return () -> new ByteArrayInputStream(DUMMY_PNG);
            }
            // 3. Animations: e.g. "animations/my_statue.animation.json"
            if (path.startsWith("animations/")) {
                String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path.substring("animations/".length());
                File file = new File(this.baseDir, "animations/" + fileName);
                if (file.exists()) {
                    return () -> new FileInputStream(file);
                }
            }
            // 4. Virtual Item models
            if (path.startsWith("models/item/") && path.endsWith(".json")) {
                return () -> new ByteArrayInputStream("{\"parent\":\"minecraft:builtin/entity\"}".getBytes(StandardCharsets.UTF_8));
            }
            // 5. Virtual Blockstates
            if (path.startsWith("blockstates/") && path.endsWith(".json")) {
                return () -> new ByteArrayInputStream("{\"variants\":{\"\":{\"model\":\"custom_pokedolls:block/dummy\"}}}".getBytes(StandardCharsets.UTF_8));
            }
            // 6. Virtual Dummy block model
            if (path.equals("models/block/dummy.json")) {
                return () -> new ByteArrayInputStream("{\"parent\":\"minecraft:block/air\"}".getBytes(StandardCharsets.UTF_8));
            }
        }
        return super.open(type, id);
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        super.findResources(type, namespace, prefix, consumer);
        if ("custom_pokedolls".equals(namespace)) {
            for (PokedollSpecies species : PokedollSpecies.values()) {
                // 1. Geo
                Identifier geoId = species.getGeoModelPath();
                if (geoId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    File f = new File(this.baseDir, "geo/" + species.getId() + ".geo.json");
                    if (!f.exists()) f = new File(this.baseDir, species.getId() + ".geo.json");
                    File finalF = f;
                    consumer.accept(geoId, () -> {
                        try {
                            if (finalF.exists()) return new FileInputStream(finalF);
                        } catch (FileNotFoundException ignored) {}
                        return new ByteArrayInputStream(DUMMY_GEO_JSON.getBytes(StandardCharsets.UTF_8));
                    });
                }
                // 2. Normal Texture
                Identifier texId = species.getTexturePath(false);
                if (texId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    File f = new File(this.baseDir, "textures/" + species.getId() + ".png");
                    if (!f.exists()) f = new File(this.baseDir, species.getId() + ".png");
                    File finalF = f;
                    consumer.accept(texId, () -> {
                        try {
                            if (finalF.exists()) return new FileInputStream(finalF);
                        } catch (FileNotFoundException ignored) {}
                        return new ByteArrayInputStream(DUMMY_PNG);
                    });
                }
                // 3. Shiny Texture
                if (species.hasShinyTexture()) {
                    Identifier shinyTexId = species.getTexturePath(true);
                    if (shinyTexId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                        File f = new File(this.baseDir, "textures/" + species.getId() + "_shiny.png");
                        if (!f.exists()) f = new File(this.baseDir, species.getId() + "_shiny.png");
                        File finalF = f;
                        consumer.accept(shinyTexId, () -> {
                            try {
                                if (finalF.exists()) return new FileInputStream(finalF);
                            } catch (FileNotFoundException ignored) {}
                            return new ByteArrayInputStream(DUMMY_PNG);
                        });
                    }
                }
                // 4. Animation
                Identifier animId = Identifier.of("custom_pokedolls", "animations/" + species.getId() + ".animation.json");
                if (animId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    File f = new File(this.baseDir, "animations/" + species.getId() + ".animation.json");
                    if (f.exists()) {
                        consumer.accept(animId, () -> {
                            try {
                                return new FileInputStream(f);
                            } catch (FileNotFoundException e) {
                                return new ByteArrayInputStream(new byte[0]);
                            }
                        });
                    }
                }
                // 5. Item models
                Identifier normalItemId = Identifier.of("custom_pokedolls", "models/item/" + species.getId() + "_doll.json");
                if (normalItemId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    consumer.accept(normalItemId, () -> new ByteArrayInputStream("{\"parent\":\"minecraft:builtin/entity\"}".getBytes(StandardCharsets.UTF_8)));
                }
                Identifier shinyItemId = Identifier.of("custom_pokedolls", "models/item/" + species.getId() + "_shiny_doll.json");
                if (shinyItemId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    consumer.accept(shinyItemId, () -> new ByteArrayInputStream("{\"parent\":\"minecraft:builtin/entity\"}".getBytes(StandardCharsets.UTF_8)));
                }
                // 6. Blockstates
                Identifier bsId = Identifier.of("custom_pokedolls", "blockstates/" + species.getId() + "_doll.json");
                if (bsId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                    consumer.accept(bsId, () -> new ByteArrayInputStream("{\"variants\":{\"\":{\"model\":\"custom_pokedolls:block/dummy\"}}}".getBytes(StandardCharsets.UTF_8)));
                }
            }
            // 7. Dummy block model
            Identifier dummyId = Identifier.of("custom_pokedolls", "models/block/dummy.json");
            if (dummyId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                consumer.accept(dummyId, () -> new ByteArrayInputStream("{\"parent\":\"minecraft:block/air\"}".getBytes(StandardCharsets.UTF_8)));
            }
            // 8. Dummy geo and texture fallbacks
            Identifier dummyGeoId = Identifier.of("custom_pokedolls", "geo/dummy.geo.json");
            if (dummyGeoId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                consumer.accept(dummyGeoId, () -> new ByteArrayInputStream(DUMMY_GEO_JSON.getBytes(StandardCharsets.UTF_8)));
            }
            Identifier dummyTexId = Identifier.of("custom_pokedolls", "textures/dummy.png");
            if (dummyTexId.getPath().startsWith(prefix) || prefix.isEmpty()) {
                consumer.accept(dummyTexId, () -> new ByteArrayInputStream(DUMMY_PNG));
            }
        }
    }

    public static void registerProfile(java.util.function.Consumer<ResourcePackProfile> profileAdder) {
        File customDir = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().toFile(), "custom_pokedolls");
        if (!customDir.exists()) customDir.mkdirs();
        File mcmetaFile = new File(customDir, "pack.mcmeta");
        if (!mcmetaFile.exists()) {
            try (FileWriter writer = new FileWriter(mcmetaFile)) {
                writer.write("{\"pack\":{\"pack_format\":34,\"description\":\"Custom Pokedolls Virtual Pack\"}}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ResourcePackInfo info = new ResourcePackInfo("custom_pokedolls_virtual", Text.literal("Custom Pokedolls Virtual Pack"), ResourcePackSource.BUILTIN, Optional.empty());
        ResourcePackPosition position = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false);
        ResourcePackProfile.PackFactory factory = new ResourcePackProfile.PackFactory() {
            @Override
            public ResourcePack open(ResourcePackInfo info) {
                return new DirectoryVirtualResourcePack(info, customDir);
            }
            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                return new DirectoryVirtualResourcePack(info, customDir);
            }
        };
        ResourcePackProfile profile = ResourcePackProfile.create(info, factory, ResourceType.CLIENT_RESOURCES, position);
        if (profile != null) {
            profileAdder.accept(profile);
        }
    }
}
