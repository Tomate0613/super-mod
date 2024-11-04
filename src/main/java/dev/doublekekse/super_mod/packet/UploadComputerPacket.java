package dev.doublekekse.super_mod.packet;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.computer.file_system.VirtualFileSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record UploadComputerPacket(ResourceKey<Level> dimension, BlockPos pos, VirtualFileSystem vfs) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, UploadComputerPacket> STREAM_CODEC = CustomPacketPayload.codec(UploadComputerPacket::write, UploadComputerPacket::load);
    public static final Type<UploadComputerPacket> TYPE = new Type<>(SuperMod.id("upload_computer"));

    static UploadComputerPacket load(FriendlyByteBuf byteBuf) {
        var vfs = new VirtualFileSystem();

        var dimension = byteBuf.readResourceKey(Registries.DIMENSION);
        var pos = byteBuf.readBlockPos();

        var size = byteBuf.readInt();

        for (int i = 0; i < size; i++) {
            vfs.createFile(byteBuf.readUtf(), byteBuf.readByteArray());
        }

        return new UploadComputerPacket(dimension, pos, vfs);
    }

    void write(FriendlyByteBuf byteBuf) {
        var files = vfs.getCustomFiles();

        byteBuf.writeResourceKey(dimension);
        byteBuf.writeBlockPos(pos);
        byteBuf.writeInt(files.size());

        files.forEach((filename, data) -> {
            byteBuf.writeUtf(filename);
            byteBuf.writeByteArray(data);
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
