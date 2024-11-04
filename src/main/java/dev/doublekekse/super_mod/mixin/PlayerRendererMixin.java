package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.registry.SuperBlocks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void getArmPose(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        ItemStack itemStack = abstractClientPlayer.getItemInHand(interactionHand);

        if (itemStack.is(SuperBlocks.COMPUTER_BLOCK.asItem())) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
