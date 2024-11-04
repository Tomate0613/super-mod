package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.duck.ItemEntityDuck;
import dev.doublekekse.super_mod.duck.PlayerDuck;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    void drop(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
        if (!SuperMod.isHot((Player) (Object) this)) {
            return;
        }

        var itemEntity = cir.getReturnValue();

        if (itemEntity == null) {
            return;
        }

        var deltaMovement = itemEntity.getDeltaMovement().scale(3);
        itemEntity.setDeltaMovement(deltaMovement.x, deltaMovement.y - .3, deltaMovement.z);
        ((ItemEntityDuck) itemEntity).super_mod$setDamaging(ItemEntityDuck.DamageTarget.MOB);
    }

    @Override
    public void super_mod$openComputer(ComputerBlockEntity cbe, boolean isScreen) {

    }
}
