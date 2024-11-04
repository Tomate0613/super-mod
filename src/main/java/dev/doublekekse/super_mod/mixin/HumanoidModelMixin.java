package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.registry.SuperBlocks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    void poseLeftArm(T livingEntity, float a, float b, float c, float d, float e, CallbackInfo ci) {
        if (livingEntity instanceof Player player) {
            var main = player.getMainHandItem();
            var offhand = player.getOffhandItem();

            var computer = SuperBlocks.COMPUTER_BLOCK.asItem();

            if (!main.is(computer) && !offhand.is(computer)) {
                return;
            }

            leftArm.xRot = leftArm.xRot * 0.5f - 1;
            leftArm.yRot = 0;

            rightArm.xRot = rightArm.xRot * .5f - 1;
            rightArm.yRot = 0;
        }
    }
}
