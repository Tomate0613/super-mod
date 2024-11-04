package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.computer.file_system.VirtualFile;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.BaseLib;

import java.io.InputStream;

public class PiOsBaseLib extends BaseLib {
    ComputerBlockEntity cbe;

    public PiOsBaseLib(ComputerBlockEntity cbe) {
        this.cbe = cbe;
    }

    public LuaValue call(LuaValue var1, LuaValue var2) {
        super.call(var1, var2);
        var2.checkglobals().STDIN = System.in;
        return var2;
    }

    public InputStream findResource(String filename) {
        if (!cbe.vfs.fileExists(filename)) {
            return super.findResource(filename);
        } else {
            var file = new VirtualFile(filename, cbe.vfs);
            return file.getInputStream();
        }
    }
}
