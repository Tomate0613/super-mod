package dev.doublekekse.super_mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.MinecraftDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin<T extends Entity> {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    void render(EntityRenderer<T> instance, T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (SuperMod.isAffected(entity)) {
            var q = ((MinecraftDuck) Minecraft.getInstance()).superBlock$getAffectedTimer();

            instance.render(entity, f, q.getGameTimeDeltaPartialTick(true), poseStack, multiBufferSource, i);
        } else {
            instance.render(entity, f, g, poseStack, multiBufferSource, i);
        }
    }
}
