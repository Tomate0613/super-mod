package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

/**
 * This mixin was adapted from <a href="https://github.com/HamaIndustries/Rochefort-Mills/blob/main/src/client/java/symbolics/division/flopster/mixin/client/MouseMixin.java">Flopster</a>
 * <p>
 * Licensed under the MIT License
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2024 hama Industries
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onDrop", at = @At("HEAD"), cancellable = true)
    void onDrop(long l, List<Path> list, int i, CallbackInfo ci) {
        var client = Minecraft.getInstance();
        if (SuperMod.putFile(client, list, i)) ci.cancel();
    }
}
