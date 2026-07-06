package com.fossilcollector.pokedolls.registry;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.item.PokedollBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class ModBlocks {
    public static final String MOD_ID = "static_pokedolls";
    
    public static final Map<PokedollSpecies, PokedollBlock> BLOCKS = new EnumMap<>(PokedollSpecies.class);
    public static final Map<PokedollSpecies, PokedollBlockItem> NORMAL_ITEMS = new EnumMap<>(PokedollSpecies.class);
    public static final Map<PokedollSpecies, PokedollBlockItem> SHINY_ITEMS = new EnumMap<>(PokedollSpecies.class);
    
    public static Item POKEDOLL_CHISEL;
    public static ItemGroup POKEDOLLS_GROUP;

    public static void registerAll() {
        POKEDOLL_CHISEL = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pokedoll_chisel"), new Item(new Item.Settings().maxCount(1)));

        for (PokedollSpecies species : PokedollSpecies.values()) {
            Identifier blockId = Identifier.of(MOD_ID, species.getId() + "_doll");
            Identifier shinyItemId = Identifier.of(MOD_ID, species.getId() + "_shiny_doll");

            PokedollBlock block = new PokedollBlock(AbstractBlock.Settings.create().nonOpaque().strength(1.5f, 6.0f).luminance(state -> state.get(PokedollBlock.GLOWING) ? 15 : 0), species);
            PokedollBlockItem normalItem = new PokedollBlockItem(block, new Item.Settings(), species, false);
            PokedollBlockItem shinyItem = new PokedollBlockItem(block, new Item.Settings(), species, true);

            Registry.register(Registries.BLOCK, blockId, block);
            Registry.register(Registries.ITEM, blockId, normalItem);
            Registry.register(Registries.ITEM, shinyItemId, shinyItem);

            BLOCKS.put(species, block);
            NORMAL_ITEMS.put(species, normalItem);
            SHINY_ITEMS.put(species, shinyItem);
        }

        POKEDOLLS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(NORMAL_ITEMS.get(PokedollSpecies.TYRANTRUM)))
            .displayName(Text.translatable("itemGroup.static_pokedolls.general"))
            .entries((context, entries) -> {
                entries.add(POKEDOLL_CHISEL);
                NORMAL_ITEMS.values().forEach(entries::add);
                SHINY_ITEMS.values().forEach(entries::add);
            })
            .build();

        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "general"), POKEDOLLS_GROUP);
    }
}
