package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SetSpeedPacket(double speed) implements CustomPacketPayload{
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSpeedPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, SetSpeedPacket::speed,
        SetSpeedPacket::new
    );
    public static final Type<SetSpeedPacket> TYPE = new Type<>(SuperMod.id("set_speed"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
