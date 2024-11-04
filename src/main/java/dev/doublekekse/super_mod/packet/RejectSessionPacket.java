package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RejectSessionPacket(ResourceLocation computerDim, BlockPos computerPos,
                                  String reason) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, RejectSessionPacket> STREAM_CODEC = CustomPacketPayload.codec(RejectSessionPacket::write, RejectSessionPacket::new);
    public static final CustomPacketPayload.Type<RejectSessionPacket> TYPE = new CustomPacketPayload.Type<>(SuperMod.id("reject_session"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public RejectSessionPacket(FriendlyByteBuf buf) {
        this(
            buf.readResourceLocation(),
            buf.readBlockPos(),
            buf.readUtf()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(computerDim);
        buf.writeBlockPos(computerPos);
        buf.writeUtf(reason);
    }
}
