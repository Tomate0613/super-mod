package dev.doublekekse.super_mod.luaj;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

public class LimitedDebugLib extends DebugLib {
    final int maxInstructions;
    int instructionsLeft;

    public LimitedDebugLib(int count) {
        super();

        maxInstructions = count;

        resetCount();
    }

    public void resetCount() {
        instructionsLeft = maxInstructions;
    }

    @Override
    public void onInstruction(int i, Varargs varargs, int i1) {
        instructionsLeft--;

        if (instructionsLeft < 0) {
            throw new RuntimeException("Too many instructions");
        }

        super.onInstruction(i, varargs, i1);
    }
}
