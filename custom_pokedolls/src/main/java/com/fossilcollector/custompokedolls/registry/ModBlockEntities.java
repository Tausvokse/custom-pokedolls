package com.fossilcollector.custompokedolls.registry;

import com.fossilcollector.custompokedolls.block.PokedollBlock;
import com.fossilcollector.custompokedolls.entity.PokedollBlockEntity;
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

    @SuppressWarnings("unchecked")
    public static void addSupportedBlocks(java.util.Collection<PokedollBlock> newBlocks) {
        if (POKEDOLL_BLOCK_ENTITY == null) return;
        try {
            java.lang.reflect.Field blocksField = null;
            for (java.lang.reflect.Field f : BlockEntityType.class.getDeclaredFields()) {
                if (java.util.Set.class.isAssignableFrom(f.getType())) {
                    blocksField = f;
                    break;
                }
            }
            if (blocksField != null) {
                blocksField.setAccessible(true);
                java.util.Set<net.minecraft.block.Block> blocks = (java.util.Set<net.minecraft.block.Block>) blocksField.get(POKEDOLL_BLOCK_ENTITY);
                if (!(blocks instanceof java.util.HashSet) && !(blocks instanceof java.util.LinkedHashSet)) {
                    blocks = new java.util.HashSet<>(blocks);
                    blocksField.set(POKEDOLL_BLOCK_ENTITY, blocks);
                }
                blocks.addAll(newBlocks);
            }
        } catch (Exception e) {
            com.fossilcollector.custompokedolls.StaticPokedolls.LOGGER.error("Failed to add supported blocks to BlockEntityType", e);
        }
    }
}
