package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.block.ClientComputerLuaProcess;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class PiOsClientPuterLib extends PiOsBasePuterLib {
    public PiOsClientPuterLib(ClientComputerLuaProcess process) {
        super(process);
    }

    @Override
    void setPuter(LuaTable puter) {
        super.setPuter(puter);

        puter.set("upload", new upload());
    }

    class upload extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            ((ClientComputerLuaProcess) process).getComputer().upload();
            return NONE;
        }
    }
}
