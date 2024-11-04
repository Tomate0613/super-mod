package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestSessionPacket(ResourceLocation computerDim, BlockPos computerPos,
                                   ResourceLocation profileId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSessionPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestSessionPacket::write, RequestSessionPacket::new);
    public static final CustomPacketPayload.Type<RequestSessionPacket> TYPE = new CustomPacketPayload.Type<>(SuperMod.id("request_session"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public RequestSessionPacket(FriendlyByteBuf buf) {
        this(
            buf.readResourceLocation(),
            buf.readBlockPos(),
            buf.readResourceLocation()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(computerDim);
        buf.writeBlockPos(computerPos);
        buf.writeResourceLocation(profileId);
    }
}
