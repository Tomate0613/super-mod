package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.luaj.LuaProcess;
import dev.doublekekse.super_mod.luaj.TableUtils;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class MinecraftLib extends TwoArgFunction {
    private LuaProcess process;

    public MinecraftLib(LuaProcess process) {
        this.process = process;
    }

    @Override
    public LuaValue call(LuaValue script, LuaValue env) {
        LuaTable minecraft = new LuaTable();

        minecraft.set("get_local_player", new get_local_player());
        minecraft.set("get_scoreboard", new get_scoreboard());

        env.set("minecraft", minecraft);
        return minecraft;
    }

    static class get_local_player extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            assert Minecraft.getInstance().player != null;
            return TableUtils.playerTable(Minecraft.getInstance().player);
        }
    }

    class get_scoreboard extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue objectiveName) {
            var scoreboard = process.cbe.getLevel().getScoreboard();

            if (!objectiveName.isstring()) {
                throw new LuaError("Objective must be a string");
            }

            var objective = scoreboard.getObjective(objectiveName.tojstring());

            if(objective == null) {
                throw new LuaError("Objective does not exist or is not synced with client");
            }

            return TableUtils.objectiveTable(objective);
        }
    }
}
