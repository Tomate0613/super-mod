package dev.doublekekse.super_mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.MinecraftDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow protected abstract void renderEntity(Entity entity, double d, double e, double f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource);

    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    void renderEntity(LevelRenderer instance, Entity entity, double d, double e, double f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        if(SuperMod.isAffected(entity)) {
            var q = ((MinecraftDuck)minecraft).superBlock$getAffectedTimer();
            renderEntity(entity, d, e, f, q.getGameTimeDeltaPartialTick(true), poseStack, multiBufferSource);
            return;
        }

        renderEntity(entity, d, e, f, g, poseStack, multiBufferSource);
    }
}
