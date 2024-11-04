package dev.doublekekse.super_mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final
    Minecraft minecraft;

    /*
    @Redirect(method ="renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaPartialTick(Z)F"))
    float getGameTimeDeltaPartialTick(DeltaTracker instance, boolean b) {
        if(minecraft.getCameraEntity() != null && !(minecraft.getCameraEntity() instanceof Player)) {
            return instance.getGameTimeDeltaPartialTick(b);
        }


        //return ((MinecraftDuck) minecraft).superBlock$getPlayerTimer().getGameTimeDeltaPartialTick(b);
    }
     */
}
