package com.fossilcollector.pokedolls;

import com.fossilcollector.pokedolls.block.PokedollBlock;
import com.fossilcollector.pokedolls.block.PokedollSize;
import com.fossilcollector.pokedolls.entity.PokedollBlockEntity;
import com.fossilcollector.pokedolls.network.SizeSelectPayload;
import com.fossilcollector.pokedolls.registry.ModBlockEntities;
import com.fossilcollector.pokedolls.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticPokedolls implements ModInitializer {
    public static final String MOD_ID = "static_pokedolls";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Static Pokedolls for Cobblemon (Fabric 1.21.1)...");
        ModBlocks.registerAll();
        ModBlockEntities.registerAll();

        PayloadTypeRegistry.playC2S().register(SizeSelectPayload.ID, SizeSelectPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SizeSelectPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                BlockPos pos = payload.pos();
                String sizeName = payload.sizeName();
                if (context.player().squaredDistanceTo(pos.toCenterPos()) < 256.0) {
                    World world = context.player().getWorld();
                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof PokedollBlock) {
                        for (PokedollSize size : PokedollSize.values()) {
                            if (size.asString().equals(sizeName)) {
                                BlockState newState = state.with(PokedollBlock.SIZE, size).with(PokedollBlock.GLOWING, payload.glowing());
                                if (state != newState) {
                                    world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                                }
                                break;
                            }
                        }
                        if (world.getBlockEntity(pos) instanceof PokedollBlockEntity be) {
                            be.setCustomScale(payload.scale());
                            be.setCustomRotation(payload.rotationAngle());
                            be.setOffsetX(payload.offsetX());
                            be.setOffsetY(payload.offsetY());
                            be.setOffsetZ(payload.offsetZ());
                            be.setAutoRotate(payload.autoRotate());
                            be.setPose(payload.pose());
                            be.markDirty();
                            world.updateListeners(pos, state, world.getBlockState(pos), Block.NOTIFY_ALL);
                        }
                    }
                }
            });
        });

        LOGGER.info("Successfully registered 27 Pokedoll species (54 variants), BlockEntities, and Size Networking!");
    }
}
