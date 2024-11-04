package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Inject(method = "onEdited", at = @At("HEAD"), cancellable = true)
    void onEdited(String string, CallbackInfo ci) {
        if (!SuperMod.isHot()) {
            return;
        }

        var value = input.getValue();

        if (value.isEmpty()) {
            return;
        }

        if (value.charAt(0) == '!' || value.charAt(0) == '/') {
            return;
        }

        var sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            var sm = "SUPER MOD ";
            sb.append(sm.charAt(i % sm.length()));
        }

        if (sb.toString().equals(value)) {
            return;
        }

        input.setValue(sb.toString());
        ci.cancel();
    }
}
