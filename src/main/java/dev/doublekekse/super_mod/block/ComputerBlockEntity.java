package dev.doublekekse.super_mod.block;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.computer.file_system.VirtualFile;
import dev.doublekekse.super_mod.computer.file_system.VirtualFileSystem;
import dev.doublekekse.super_mod.computer.terminal.TerminalOutputStream;
import dev.doublekekse.super_mod.luaj.LimitedDebugLib;
import dev.doublekekse.super_mod.luaj.LuaProcess;
import dev.doublekekse.super_mod.packet.UploadComputerPacket;
import dev.doublekekse.super_mod.registry.SuperBlockEntities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;

import java.io.IOException;
import java.util.Stack;

public class ComputerBlockEntity extends BlockEntity {
    public VirtualFileSystem vfs = new VirtualFileSystem();

    private boolean isLoaded = false;
    protected boolean hasSynced = false;

    public TerminalOutputStream terminalOutput = new TerminalOutputStream();
    public Stack<LuaProcess> processStack = new Stack<>();

    public ComputerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(SuperBlockEntities.COMPUTER_BLOCK_ENTITY, blockPos, blockState);
    }

    protected ComputerBlockEntity(BlockEntityType type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);

        SuperMod.defaultFiles.forEach(vfs::createFile);
        setChanged();
    }

    public void init() {
        isLoaded = true;

        try {
            if (vfs.fileExists("init.lua")) {
                openProgram("init.lua");
            } else {
                openProgram("bash.lua");
            }
        } catch (IOException e) {
            SuperMod.LOGGER.error("Can't find bash.lua. The vfs might not be loaded correctly", e);
        }
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

    public void openProgram(String script) throws IOException {
        openProgram(script, null);
    }

    public void openProgram(String script, @Nullable LuaValue args) throws IOException {
        if (!vfs.fileExists(script)) {
            throw new IOException("File does not exist");
        }

        var programFile = new VirtualFile(script, vfs);
        var code = programFile.readAllToString();

        startProcess(code, args);
    }

    public void startProcess(String code, @Nullable LuaValue args) {
        var process = new LuaProcess(this);
        processStack.add(process);

        process.loadScript(code, args);
    }

    public void triggerEvent(String eventName, LuaValue... args) {
        if (processStack.empty()) {
            return;
        }

        ((LimitedDebugLib) processStack.peek().globals.debuglib).resetCount();


        processStack.peek().triggerEvent(eventName, args);
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
