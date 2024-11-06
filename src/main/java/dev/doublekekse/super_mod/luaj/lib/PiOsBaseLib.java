package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.computer.file_system.VirtualFile;
import dev.doublekekse.super_mod.luaj.LuaComputer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.BaseLib;

import java.io.InputStream;

public class PiOsBaseLib extends BaseLib {
    final LuaComputer<?> lc;

    public PiOsBaseLib(LuaComputer<?> lc) {
        this.lc = lc;
    }

    public LuaValue call(LuaValue var1, LuaValue var2) {
        super.call(var1, var2);
        var2.checkglobals().STDIN = System.in;
        return var2;
    }

    public InputStream findResource(String filename) {
        if (!lc.getVfs().fileExists(filename)) {
            return super.findResource(filename);
        } else {
            var file = new VirtualFile(filename, lc.getVfs());
            return file.getInputStream();
        }
    }
}
