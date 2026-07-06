package com.fossilcollector.custompokedolls.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ReloadResourcesPayload() implements CustomPayload {
    public static final CustomPayload.Id<ReloadResourcesPayload> ID = new CustomPayload.Id<>(Identifier.of("custom_pokedolls", "reload_resources"));
    
    public static final PacketCodec<RegistryByteBuf, ReloadResourcesPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {},
        buf -> new ReloadResourcesPayload()
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
