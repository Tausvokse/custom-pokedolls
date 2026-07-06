package com.fossilcollector.pokedolls.client;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.client.gui.PokedollSizeScreen;
import com.fossilcollector.pokedolls.client.render.PokedollBlockRenderer;
import com.fossilcollector.pokedolls.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

public class StaticPokedollsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.POKEDOLL_BLOCK_ENTITY, PokedollBlockRenderer::new);

        PokedollBlock.SCREEN_OPENER = pos -> {
            MinecraftClient.getInstance().setScreen(new PokedollSizeScreen(pos));
        };
    }
}
