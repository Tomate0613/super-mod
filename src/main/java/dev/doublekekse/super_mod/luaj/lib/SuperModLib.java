package dev.doublekekse.super_mod.luaj.lib;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ClientComputerLuaProcess;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.packet.CallSuperFunctionPacket;
import dev.doublekekse.super_mod.packet.RequestSessionPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class SuperModLib extends TwoArgFunction {
    public LuaFunction sessionCallback;
    private final ClientComputerLuaProcess process;

    public SuperModLib(ClientComputerLuaProcess process) {
        this.process = process;
    }

    @Override
    public LuaValue call(LuaValue script, LuaValue env) {
        LuaTable supermod = new LuaTable();

        supermod.set("get_speed", new get_speed());
        supermod.set("request_session", new request_session());
        supermod.set("mc_fn", new mc_fn());

        env.set("supermod", supermod);
        return supermod;
    }

    static class get_speed extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(SuperMod.speed);
        }
    }

    class request_session extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue area, LuaValue callback) {
            if (!area.isstring()) {
                throw new LuaError("Area id must be a string");
            }
            if (!callback.isfunction()) {
                throw new LuaError("Callback must be a function");
            }

            var areaIdString = area.tojstring();
            var location = ResourceLocation.tryParse(areaIdString);

            if (location == null) {
                throw new LuaError("Area id must be a valid resource location");
            }

            sessionCallback = callback.checkfunction();

            var cbe = (ComputerBlockEntity) process.lc;

            ClientPlayNetworking.send(new RequestSessionPacket(cbe.getLevel().dimension().location(), cbe.getBlockPos(), location));

            return NONE;
        }
    }

    static class mc_fn extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue name) {
            if (!name.isstring()) {
                throw new LuaError("Function name must be a string");
            }

            ClientPlayNetworking.send(new CallSuperFunctionPacket(name.tojstring()));

            return NONE;
        }
    }
}
