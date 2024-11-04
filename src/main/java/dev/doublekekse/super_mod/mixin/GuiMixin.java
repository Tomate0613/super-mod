package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow public abstract Font getFont();

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        guiGraphics.drawString(getFont(), Component.literal("SPEED: ").append("" + SuperMod.speed), 0, 0, 0xffffff);
    }
}
