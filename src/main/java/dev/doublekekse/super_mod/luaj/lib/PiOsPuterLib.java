package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.luaj.LuaProcess;
import dev.doublekekse.super_mod.luaj.TableUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class PiOsPuterLib extends TwoArgFunction {
    private final HashMap<String, List<LuaFunction>> eventListeners = new HashMap<>();
    private final LuaProcess process;

    public PiOsPuterLib(LuaProcess process) {
        this.process = process;
    }

    public boolean hasListeners() {
        return !eventListeners.isEmpty();
    }

    @Override
    public LuaValue call(LuaValue script, LuaValue env) {
        LuaTable puter = new LuaTable();

        puter.set("on", new on());
        puter.set("stop", new stop());
        puter.set("run", new run());
        puter.set("list_files", new list_files());
        puter.set("get_screen_size", new list_files());
        puter.set("upload", new upload());
        puter.set("get_screen_size", new get_screen_size());
        puter.set("make_sound", new make_sound());
        puter.set("get_pos", new get_pos());

        env.set("puter", puter);
        return puter;
    }

    class on extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue eventName, LuaValue listener) {
            if (!eventName.isstring()) {
                throw new LuaError("Event name must be a string");
            }
            if (!listener.isfunction()) {
                throw new LuaError("Listener must be a function");
            }

            String event = eventName.tojstring();
            LuaFunction luaListener = listener.checkfunction();

            // Add the listener to the eventListeners map
            eventListeners.putIfAbsent(event, new ArrayList<>());
            eventListeners.get(event).add(luaListener);

            return NONE;
        }
    }

    class run extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue script, LuaValue args) {
            if (!script.isstring()) {
                throw new LuaError("Script must be a string");
            }

            String scriptName = script.tojstring();
            try {
                process.cbe.openProgram(scriptName, args);
            } catch (IOException e) {
                throw new LuaError(e);
            }

            return NONE;
        }
    }

    class stop extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            process.stop();

            return NONE;
        }
    }

    class get_pos extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return TableUtils.positionTable(process.cbe.getBlockPos());
        }
    }

    class get_screen_size extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            LuaTable table = new LuaTable();

            table.set("x", process.cbe.terminalOutput.screenSizeX);
            table.set("y", process.cbe.terminalOutput.screenSizeY);

            return table;
        }
    }

    class make_sound extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue sound, LuaValue volume, LuaValue pitch) {
            if (!sound.isstring()) {
                throw new LuaError("Sound must be a string");
            }

            var location = ResourceLocation.tryParse(sound.tojstring());

            if (location == null) {
                throw new LuaError("Sound must be a resource location");
            }

            var se = SoundEvent.createVariableRangeEvent(location);

            var cbe = process.cbe;
            cbe.getLevel().playLocalSound(cbe.getBlockPos(), se, SoundSource.BLOCKS, volume.tofloat(), pitch.tofloat(), true);

            return NONE;
        }
    }

    class list_files extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var filenames = process.cbe.vfs.listFiles();

            var valueList = new LuaValue[filenames.size()];

            for (int i = 0; i < filenames.size(); i++) {
                valueList[i] = LuaValue.valueOf(filenames.get(i));
            }

            return LuaTable.listOf(valueList);
        }
    }

    class upload extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            process.cbe.upload();
            return NONE;
        }
    }

    public void triggerEvent(String eventName, LuaValue... args) {
        if (eventListeners.containsKey(eventName)) {
            var listeners = eventListeners.get(eventName);

            for (var listener : listeners) {
                listener.invoke(LuaValue.varargsOf(args));
            }
        }
    }
}
