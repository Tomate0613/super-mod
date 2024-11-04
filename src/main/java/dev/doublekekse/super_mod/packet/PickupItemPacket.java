package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record PickupItemPacket(int id) implements CustomPacketPayload{
    public static final StreamCodec<RegistryFriendlyByteBuf, PickupItemPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, PickupItemPacket::id,
        PickupItemPacket::new
    );
    public static final CustomPacketPayload.Type<PickupItemPacket> TYPE = new CustomPacketPayload.Type<>(SuperMod.id("pickup_item"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
