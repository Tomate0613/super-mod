package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record CallSuperFunctionPacket(String function) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, CallSuperFunctionPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, CallSuperFunctionPacket::function,
        CallSuperFunctionPacket::new
    );
    public static final CustomPacketPayload.Type<CallSuperFunctionPacket> TYPE = new CustomPacketPayload.Type<>(SuperMod.id("call_super_function"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
