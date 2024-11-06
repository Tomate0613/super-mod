package dev.doublekekse.super_mod.luaj;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.computer.file_system.VirtualFile;
import dev.doublekekse.super_mod.computer.file_system.VirtualFileSystem;
import dev.doublekekse.super_mod.computer.terminal.TerminalOutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.util.Stack;

public interface LuaComputer<Process extends LuaProcess> {
    VirtualFileSystem getVfs();

    Stack<Process> getProcessStack();

    TerminalOutputStream getTerminalOutput();

    default void openProgram(String script, @Nullable LuaValue args, boolean keepAlive) throws IOException {
        var vfs = getVfs();

        if (!vfs.fileExists(script)) {
            throw new IOException("File does not exist");
        }

        var programFile = new VirtualFile(script, vfs);
        var code = programFile.readAllToString();

        startProcess(code, args, keepAlive);
    }

    Process newProcess();

    default void startProcess(String code, @Nullable LuaValue args, boolean keepAlive) {
        var process = newProcess();
        getProcessStack().add(process);

        process.loadScript(code, args, keepAlive);
    }

    default void triggerEvent(String eventName, LuaValue... args) {
        var processStack = getProcessStack();

        if (processStack.empty()) {
            return;
        }

        ((LimitedDebugLib) processStack.peek().globals.debuglib).resetCount();


        processStack.peek().triggerEvent(eventName, args);
    }

    default void init() {
        try {
            if (getVfs().fileExists("init.lua")) {
                openProgram("init.lua", null, true);
            } else {
                openProgram("bash.lua", null, true);
            }
        } catch (IOException e) {
            SuperMod.LOGGER.error("Can't find bash.lua. The vfs might not be loaded correctly", e);
        }
    }

    BlockPos getBlockPos();

    void playSound(BlockPos blockPos, SoundEvent se, float volume, float pitch);
}
