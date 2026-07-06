package com.fossilcollector.custompokedolls.client;

import com.fossilcollector.custompokedolls.block.PokedollBlock;
import com.fossilcollector.custompokedolls.client.gui.PokedollSizeScreen;
import com.fossilcollector.custompokedolls.client.render.PokedollBlockRenderer;
import com.fossilcollector.custompokedolls.mixin.ResourcePackManagerAccessor;
import com.fossilcollector.custompokedolls.network.ReloadResourcesPayload;
import com.fossilcollector.custompokedolls.registry.DirectoryVirtualResourcePack;
import com.fossilcollector.custompokedolls.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import java.util.HashSet;
import java.util.Set;

public class StaticPokedollsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.POKEDOLL_BLOCK_ENTITY, PokedollBlockRenderer::new);

        PokedollBlock.SCREEN_OPENER = pos -> {
            MinecraftClient.getInstance().setScreen(new PokedollSizeScreen(pos));
        };

        try {
            ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();
            if (manager != null) {
                Set<ResourcePackProvider> providers = new HashSet<>(((ResourcePackManagerAccessor) manager).getProviders());
                providers.add(DirectoryVirtualResourcePack::registerProfile);
                ((ResourcePackManagerAccessor) manager).setProviders(com.google.common.collect.ImmutableSet.copyOf(providers));
                manager.scanPacks();
                System.out.println("[CustomPokedolls DEBUG] Successfully added virtual resource pack provider from onInitializeClient!");
            } else {
                System.out.println("[CustomPokedolls DEBUG] getResourcePackManager() was NULL in onInitializeClient!");
            }
        } catch (Exception e) {
            System.out.println("[CustomPokedolls ERROR] Failed to add virtual resource pack provider in onInitializeClient: " + e);
            e.printStackTrace();
        }

        ClientPlayNetworking.registerGlobalReceiver(ReloadResourcesPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                try {
                    ResourcePackManager manager = context.client().getResourcePackManager();
                    if (manager != null) {
                        manager.scanPacks();
                    }
                    context.client().reloadResources();
                    System.out.println("[CustomPokedolls DEBUG] Successfully reloaded resources from payload!");
                } catch (Exception e) {
                    System.out.println("[CustomPokedolls ERROR] Failed to reload resources: " + e);
                }
            });
        });
    }
}
