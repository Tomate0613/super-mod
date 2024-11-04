package dev.doublekekse.super_mod.luaj;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.luaj.lib.*;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;

import java.io.IOException;
import java.io.PrintStream;

public class LuaProcess {
    public final Globals globals;
    public final PiOsPuterLib puterLib;
    public final SuperModLib superModLib;
    public final ComputerBlockEntity cbe;

    boolean markClosed = false;

    public LuaProcess(ComputerBlockEntity cbe) {
        this.cbe = cbe;

        var printStream = new PrintStream(cbe.terminalOutput);

        globals = new Globals();
        globals.STDOUT = printStream;
        globals.STDERR = printStream;

        globals.load(new PiOsBaseLib(cbe));
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new PiOsMathLib());
        globals.load(new PiOsIoLib(cbe));
        globals.load(new MinecraftLib(this));

        superModLib = new SuperModLib(this);
        globals.load(superModLib);

        puterLib = new PiOsPuterLib(this);
        globals.load(puterLib);

        LoadState.install(globals);
        LuaC.install(globals);

        setMaxInstructions(this.globals, 1000);
    }

    public void loadScript(String code, @Nullable LuaValue args) {
        wrapError(() -> {
            var chunk = globals.load(code);

            if (args != null) {
                globals.set("arg", args);
            }

            chunk.call();

            if (!puterLib.hasListeners() && superModLib.sessionCallback == null) {
                stop();
            }
        });
    }

    public void stop() {
        var open = cbe.processStack.peek();

        if (open == this) {
            cbe.processStack.pop();

            if (!cbe.processStack.isEmpty() && cbe.processStack.peek().markClosed) {
                cbe.processStack.peek().stop();
            }
        } else {
            this.markClosed = true;
        }

        if (cbe.processStack.isEmpty()) {
            wrapError(() -> {
                try {
                    cbe.terminalOutput.reset();
                    cbe.openProgram("bash.lua");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    void setMaxInstructions(Globals globals, int maxInstructions) {
        globals.load(new LimitedDebugLib(maxInstructions));
    }


    public void wrapError(Runnable runnable) {
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
}
