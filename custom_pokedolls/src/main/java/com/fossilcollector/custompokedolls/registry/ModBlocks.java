package com.fossilcollector.custompokedolls.registry;

import com.fossilcollector.custompokedolls.block.PokedollBlock;
import com.fossilcollector.custompokedolls.item.PokedollBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModBlocks {
    public static final String MOD_ID = "custom_pokedolls";
    public static boolean ALLOW_RUNTIME_REGISTRATION = false;
    
    public static final Map<PokedollSpecies, PokedollBlock> BLOCKS = new LinkedHashMap<>();
    public static final Map<PokedollSpecies, PokedollBlockItem> NORMAL_ITEMS = new LinkedHashMap<>();
    public static final Map<PokedollSpecies, PokedollBlockItem> SHINY_ITEMS = new LinkedHashMap<>();
    
    public static Item POKEDOLL_CHISEL;
    public static ItemGroup POKEDOLLS_GROUP;

    public static void registerAll() {
        File baseDir = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().toFile(), "custom_pokedolls");
        PokedollSpecies.scanAndRegisterUserModels(baseDir);

        POKEDOLL_CHISEL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pokedoll_chisel"), new Item(new Item.Settings().maxCount(1)));

        for (PokedollSpecies species : PokedollSpecies.values()) {
            Identifier blockId = Identifier.of(MOD_ID, species.getId() + "_doll");

            PokedollBlock block = new PokedollBlock(AbstractBlock.Settings.create().nonOpaque().strength(1.5f, 6.0f).luminance(state -> state.get(PokedollBlock.GLOWING) ? 15 : 0), species);
            PokedollBlockItem normalItem = new PokedollBlockItem(block, new Item.Settings(), species, false);

            Registry.register(Registries.BLOCK, blockId, block);
            Registry.register(Registries.ITEM, blockId, normalItem);

            BLOCKS.put(species, block);
            NORMAL_ITEMS.put(species, normalItem);

            if (species.hasShinyTexture()) {
                Identifier shinyItemId = Identifier.of(MOD_ID, species.getId() + "_shiny_doll");
                PokedollBlockItem shinyItem = new PokedollBlockItem(block, new Item.Settings(), species, true);
                Registry.register(Registries.ITEM, shinyItemId, shinyItem);
                SHINY_ITEMS.put(species, shinyItem);
            }
        }

        POKEDOLLS_GROUP = FabricItemGroup.builder()
            .icon(() -> NORMAL_ITEMS.isEmpty() ? new ItemStack(POKEDOLL_CHISEL) : new ItemStack(NORMAL_ITEMS.values().iterator().next()))
            .displayName(Text.translatable("itemGroup.custom_pokedolls.general"))
            .entries((context, entries) -> {
                entries.add(POKEDOLL_CHISEL);
                NORMAL_ITEMS.values().forEach(entries::add);
                SHINY_ITEMS.values().forEach(item -> {
                    if (item.getSpecies().hasShinyTexture()) {
                        entries.add(item);
                    }
                });
            })
            .build();

        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "general"), POKEDOLLS_GROUP);
    }

    public static int reloadAndRegisterNewSpecies() {
        ALLOW_RUNTIME_REGISTRATION = true;
        File baseDir = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().toFile(), "custom_pokedolls");
        PokedollSpecies.scanAndRegisterUserModels(baseDir);

        int newlyAdded = 0;
        List<PokedollBlock> newBlocks = new ArrayList<>();
        for (PokedollSpecies species : PokedollSpecies.values()) {
            if (BLOCKS.containsKey(species)) {
                PokedollBlock b = BLOCKS.remove(species);
                BLOCKS.put(species, b);
                PokedollBlockItem ni = NORMAL_ITEMS.remove(species);
                NORMAL_ITEMS.put(species, ni);
                if (SHINY_ITEMS.containsKey(species)) {
                    PokedollBlockItem si = SHINY_ITEMS.remove(species);
                    if (species.hasShinyTexture()) {
                        SHINY_ITEMS.put(species, si);
                    }
                } else if (species.hasShinyTexture()) {
                    Identifier shinyItemId = Identifier.of(MOD_ID, species.getId() + "_shiny_doll");
                    PokedollBlockItem shinyItem = new PokedollBlockItem(b, new Item.Settings(), species, true);
                    Registry.register(Registries.ITEM, shinyItemId, shinyItem);
                    SHINY_ITEMS.put(species, shinyItem);
                }
            } else {
                Identifier blockId = Identifier.of(MOD_ID, species.getId() + "_doll");

                PokedollBlock block = new PokedollBlock(AbstractBlock.Settings.create().nonOpaque().strength(1.5f, 6.0f).luminance(state -> state.get(PokedollBlock.GLOWING) ? 15 : 0), species);
                PokedollBlockItem normalItem = new PokedollBlockItem(block, new Item.Settings(), species, false);

                Registry.register(Registries.BLOCK, blockId, block);
                Registry.register(Registries.ITEM, blockId, normalItem);

                BLOCKS.put(species, block);
                NORMAL_ITEMS.put(species, normalItem);

                if (species.hasShinyTexture()) {
                    Identifier shinyItemId = Identifier.of(MOD_ID, species.getId() + "_shiny_doll");
                    PokedollBlockItem shinyItem = new PokedollBlockItem(block, new Item.Settings(), species, true);
                    Registry.register(Registries.ITEM, shinyItemId, shinyItem);
                    SHINY_ITEMS.put(species, shinyItem);
                }
                newBlocks.add(block);
                newlyAdded++;
            }
        }
        if (!newBlocks.isEmpty()) {
            ModBlockEntities.addSupportedBlocks(newBlocks);
        }
        ALLOW_RUNTIME_REGISTRATION = false;
        return newlyAdded;
    }
}
