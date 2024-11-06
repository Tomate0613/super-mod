package dev.doublekekse.super_mod.luaj;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.luaj.lib.*;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;

import java.io.PrintStream;

public class LuaProcess {
    public final Globals globals;
    public PiOsBasePuterLib puterLib;
    public final LuaComputer<?> lc;

    boolean markClosed = false;

    public LuaProcess(LuaComputer<?> lc) {
        this.lc = lc;

        var printStream = new PrintStream(this.lc.getTerminalOutput());

        globals = new Globals();
        globals.STDOUT = printStream;
        globals.STDERR = printStream;

        loadLibraries();

        LoadState.install(globals);
        LuaC.install(globals);

        setMaxInstructions(this.globals, 1000);
    }

    protected void loadLibraries() {
        globals.load(new PiOsBaseLib(lc));
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new PiOsMathLib());
        globals.load(new PiOsIoLib(lc));

        puterLib = new PiOsBasePuterLib(this);
        globals.load(puterLib);
    }


    public void loadScript(String code, @Nullable LuaValue args, boolean keepAlive) {
        wrapError(() -> {
            var chunk = globals.load(code);

            if (args != null) {
                globals.set("arg", args);
            }

            chunk.call();

            if (shouldStopInstantly() && !keepAlive) {
                stop();
            }
        });
    }

    public boolean shouldStopInstantly() {
        return !puterLib.hasListeners();
    }

    public void stop() {
        var processStack = lc.getProcessStack();
        var open = (LuaProcess) processStack.peek();

        if (open == this) {
            processStack.pop();

            if (!processStack.isEmpty() && (processStack.peek()).markClosed) {
                (processStack.peek()).stop();
            }
        } else {
            this.markClosed = true;
        }

        if (processStack.isEmpty()) {
            wrapError(() -> {
                lc.getTerminalOutput().reset();
                lc.openProgram("bash.lua", null, true);
            });
        }
    }

    void setMaxInstructions(Globals globals, int maxInstructions) {
        globals.load(new LimitedDebugLib(maxInstructions));
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public void wrapError(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace(globals.STDERR);
            SuperMod.LOGGER.error("Super computer error", e);
        }
    }

    public void triggerEvent(String eventName, LuaValue... args) {
        wrapError(() -> {
            puterLib.triggerEvent(eventName, args);
        });
    }

    public LuaComputer<?> getComputer() {
        return lc;
    }
}
