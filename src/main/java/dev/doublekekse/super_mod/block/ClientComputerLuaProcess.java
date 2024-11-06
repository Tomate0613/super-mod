package dev.doublekekse.super_mod.block;

import dev.doublekekse.super_mod.luaj.LuaProcess;
import dev.doublekekse.super_mod.luaj.lib.MinecraftLib;
import dev.doublekekse.super_mod.luaj.lib.PiOsClientPuterLib;
import dev.doublekekse.super_mod.luaj.lib.SuperModLib;

public class ClientComputerLuaProcess extends LuaProcess {
    public SuperModLib superModLib;

    public ClientComputerLuaProcess(ComputerBlockEntity cbe) {
        super(cbe);
    }

    @Override
    protected void loadLibraries() {
        super.loadLibraries();

        globals.load(new MinecraftLib(this));

        superModLib = new SuperModLib(this);
        globals.load(superModLib);

        puterLib = new PiOsClientPuterLib(this);
        globals.load(puterLib);
    }

    @Override
    public boolean shouldStopInstantly() {
        return super.shouldStopInstantly() && superModLib.sessionCallback == null;
    }

    @Override
    public ComputerBlockEntity getComputer() {
        return (ComputerBlockEntity) lc;
    }
}
