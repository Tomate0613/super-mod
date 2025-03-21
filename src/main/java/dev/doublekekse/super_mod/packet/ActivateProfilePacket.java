package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ActivateProfilePacket(ResourceLocation areaId, UUID player,
                                    @Nullable ResourceLocation computerDim,
                                    @Nullable BlockPos computerPos) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateProfilePacket> STREAM_CODEC = CustomPacketPayload.codec(ActivateProfilePacket::write, ActivateProfilePacket::new);
    public static final CustomPacketPayload.Type<ActivateProfilePacket> TYPE = new CustomPacketPayload.Type<>(SuperMod.id("activate_profile"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ActivateProfilePacket(FriendlyByteBuf buf) {
        this(
            buf.readNullable(FriendlyByteBuf::readResourceLocation),
            buf.readNullable((b) -> b.readUUID()),
            buf.readNullable(FriendlyByteBuf::readResourceLocation),
            buf.readNullable((b) -> b.readBlockPos())
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(areaId, FriendlyByteBuf::writeResourceLocation);
        buf.writeNullable(player, (b, a) -> b.writeUUID(a));
        buf.writeNullable(computerDim, FriendlyByteBuf::writeResourceLocation);
        buf.writeNullable(computerPos, (b, a) -> b.writeBlockPos(a));
    }
}
