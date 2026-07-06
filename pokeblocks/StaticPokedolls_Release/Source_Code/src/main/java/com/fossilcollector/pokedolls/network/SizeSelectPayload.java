package com.fossilcollector.pokedolls.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SizeSelectPayload(
    BlockPos pos,
    String sizeName,
    boolean glowing,
    float scale,
    float rotationAngle,
    float offsetX,
    float offsetY,
    float offsetZ,
    boolean autoRotate,
    String pose
) implements CustomPayload {
    public static final CustomPayload.Id<SizeSelectPayload> ID = new CustomPayload.Id<>(Identifier.of("static_pokedolls", "size_select"));
    
    public static final PacketCodec<RegistryByteBuf, SizeSelectPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {
            buf.writeBlockPos(payload.pos());
            buf.writeString(payload.sizeName());
            buf.writeBoolean(payload.glowing());
            buf.writeFloat(payload.scale());
            buf.writeFloat(payload.rotationAngle());
            buf.writeFloat(payload.offsetX());
            buf.writeFloat(payload.offsetY());
            buf.writeFloat(payload.offsetZ());
            buf.writeBoolean(payload.autoRotate());
            buf.writeString(payload.pose());
        },
        buf -> new SizeSelectPayload(
            buf.readBlockPos(),
            buf.readString(),
            buf.readBoolean(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readBoolean(),
            buf.readString()
        )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
