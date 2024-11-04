package dev.doublekekse.super_mod.luaj.lib;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.jse.JseMathLib;

public class PiOsMathLib extends JseMathLib {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        var math = super.call(modname, env);

        math.set("sqrt", new sqrt());

        return math;
    }

    static final class sqrt extends MathLib.UnaryOp {
        protected double call(double x) {
            return Math.sqrt(x);
        }
    }
}
