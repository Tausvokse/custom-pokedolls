package com.fossilcollector.custompokedolls;

import com.fossilcollector.custompokedolls.block.PokedollBlock;
import com.fossilcollector.custompokedolls.block.PokedollSize;
import com.fossilcollector.custompokedolls.entity.PokedollBlockEntity;
import com.fossilcollector.custompokedolls.network.ReloadResourcesPayload;
import com.fossilcollector.custompokedolls.network.SizeSelectPayload;
import com.fossilcollector.custompokedolls.registry.ModBlockEntities;
import com.fossilcollector.custompokedolls.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class StaticPokedolls implements ModInitializer {
    public static final String MOD_ID = "custom_pokedolls";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Custom Pokedolls for Cobblemon (Fabric 1.21.1)...");
        ModBlocks.registerAll();
        ModBlockEntities.registerAll();

        PayloadTypeRegistry.playC2S().register(SizeSelectPayload.ID, SizeSelectPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ReloadResourcesPayload.ID, ReloadResourcesPayload.CODEC);

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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var cmd = literal("pokedolls")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("reload").executes(context -> {
                    try {
                        int added = ModBlocks.reloadAndRegisterNewSpecies();
                        context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> {
                            try {
                                if (ServerPlayNetworking.canSend(player, ReloadResourcesPayload.ID)) {
                                    ServerPlayNetworking.send(player, new ReloadResourcesPayload());
                                }
                            } catch (Exception e) {
                                LOGGER.error("Failed to send reload packet to " + player.getName().getString(), e);
                            }
                        });
                        context.getSource().sendFeedback(() -> Text.literal("[CustomPokedolls] Successfully reloaded! New species added: " + added + ". Total species: " + com.fossilcollector.custompokedolls.registry.PokedollSpecies.values().size()).formatted(Formatting.GREEN), true);
                        return 1;
                    } catch (Exception e) {
                        LOGGER.error("Error executing /pokedolls reload", e);
                        e.printStackTrace();
                        context.getSource().sendError(Text.literal("Reload error: " + e.getMessage()));
                        return 0;
                    }
                }));
            dispatcher.register(cmd);

            var cmd2 = literal("custom_pokedolls")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("reload").executes(context -> {
                    try {
                        int added = ModBlocks.reloadAndRegisterNewSpecies();
                        context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> {
                            try {
                                if (ServerPlayNetworking.canSend(player, ReloadResourcesPayload.ID)) {
                                    ServerPlayNetworking.send(player, new ReloadResourcesPayload());
                                }
                            } catch (Exception e) {
                                LOGGER.error("Failed to send reload packet to " + player.getName().getString(), e);
                            }
                        });
                        context.getSource().sendFeedback(() -> Text.literal("[CustomPokedolls] Successfully reloaded! New species added: " + added + ". Total species: " + com.fossilcollector.custompokedolls.registry.PokedollSpecies.values().size()).formatted(Formatting.GREEN), true);
                        return 1;
                    } catch (Exception e) {
                        LOGGER.error("Error executing /custom_pokedolls reload", e);
                        e.printStackTrace();
                        context.getSource().sendError(Text.literal("Reload error: " + e.getMessage()));
                        return 0;
                    }
                }));
            dispatcher.register(cmd2);
        });

        LOGGER.info("Successfully registered custom pokedolls dynamically from external folder!");
    }
}
