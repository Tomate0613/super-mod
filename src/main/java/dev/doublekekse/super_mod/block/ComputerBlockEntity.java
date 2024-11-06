package dev.doublekekse.super_mod.block;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.computer.file_system.VirtualFileSystem;
import dev.doublekekse.super_mod.computer.terminal.TerminalOutputStream;
import dev.doublekekse.super_mod.luaj.LuaComputer;
import dev.doublekekse.super_mod.packet.UploadComputerPacket;
import dev.doublekekse.super_mod.registry.SuperBlockEntities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class ComputerBlockEntity extends BlockEntity implements LuaComputer<ClientComputerLuaProcess> {
    public VirtualFileSystem vfs = new VirtualFileSystem();

    private boolean isLoaded = false;
    protected boolean hasSynced = false;

    public final TerminalOutputStream terminalOutput = new TerminalOutputStream();
    public final Stack<ClientComputerLuaProcess> processStack = new Stack<>();

    public ComputerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(SuperBlockEntities.COMPUTER_BLOCK_ENTITY, blockPos, blockState);
    }

    protected ComputerBlockEntity(BlockEntityType type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);

        SuperMod.defaultFiles.forEach(vfs::createFile);
        setChanged();
    }

    @Override
    public void init() {
        isLoaded = true;

        LuaComputer.super.init();
    }

    @Override
    public void playSound(BlockPos blockPos, SoundEvent se, float volume, float pitch) {
        if (level == null) {
            return;
        }

        level.playLocalSound(blockPos, se, SoundSource.BLOCKS, volume, pitch, true);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        saveAdditional(compoundTag, provider);

        return compoundTag;
    }

    @Override
    public VirtualFileSystem getVfs() {
        return vfs;
    }

    @Override
    public Stack<ClientComputerLuaProcess> getProcessStack() {
        return processStack;
    }

    @Override
    public TerminalOutputStream getTerminalOutput() {
        return terminalOutput;
    }

    @Override
    public ClientComputerLuaProcess newProcess() {
        return new ClientComputerLuaProcess(this);
    }


    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        vfs.writeNbt(compoundTag);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        vfs = new VirtualFileSystem();
        vfs.readNbt(compoundTag);

        hasSynced = true;
    }

    public void upload() {
        ClientPlayNetworking.send(new UploadComputerPacket(getLevel().dimension(), getBlockPos(), vfs));
    }

    public void load(VirtualFileSystem vfs) {
        SuperMod.LOGGER.info("Files have been uploaded");
        this.vfs = vfs;

        setChanged();
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
