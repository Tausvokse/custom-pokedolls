package com.fossilcollector.pokedolls.registry;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<PokedollBlockEntity> POKEDOLL_BLOCK_ENTITY;

    public static void registerAll() {
        POKEDOLL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ModBlocks.MOD_ID, "pokedoll_block_entity"),
            FabricBlockEntityTypeBuilder.create(
                PokedollBlockEntity::new,
                ModBlocks.BLOCKS.values().toArray(new PokedollBlock[0])
            ).build()
        );
    }
}
