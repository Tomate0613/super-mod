package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.ItemEntityDuck;
import dev.doublekekse.super_mod.registry.SuperDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getMainHandItem();

    @Shadow
    public abstract void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack);

    @Inject(method = "hurt", at = @At("RETURN"))
    void hurt(DamageSource damageSource, float r, CallbackInfoReturnable<Boolean> cir) {
        if (!SuperMod.isAffected(this)) {
            return;
        }

        if (!cir.getReturnValue()) {
            return;
        }

        var isItemAttack = damageSource.is(SuperDamageTypes.ITEM);
        if (!damageSource.is(DamageTypes.PLAYER_ATTACK) && !isItemAttack) {
            return;
        }

        if (!isItemAttack) {
            var item = damageSource.getWeaponItem();
            if (item != null && !item.isEmpty()) {
                return;
            }
        }

        var mainItem = getMainHandItem();

        if (mainItem.isEmpty()) {
            return;
        }


        var sourcePos = damageSource.getSourcePosition();

        if (sourcePos == null) {
            return;
        }

        setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        var itemStack = mainItem.copy();


        double d = getEyeY() - 0.3F;
        ItemEntity itemEntity = new ItemEntity(level(), getX(), d, getZ(), itemStack);
        itemEntity.setPickUpDelay(40);
        itemEntity.setThrower(this);
        ((ItemEntityDuck) itemEntity).super_mod$setDamaging(ItemEntityDuck.DamageTarget.PLAYER);


        var dPos = sourcePos.subtract(position()).normalize();
        itemEntity.setDeltaMovement(dPos.x * .2, .2, dPos.z * .2);

        if (level().isClientSide) {
            return;
        }

        level().addFreshEntity(itemEntity);
    }
}
