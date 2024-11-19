package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.duck.ItemEntityDuck;
import dev.doublekekse.super_mod.duck.LocalPlayerDuck;
import dev.doublekekse.super_mod.registry.SuperDamageTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityDuck {
    @Shadow
    public abstract @Nullable Entity getOwner();

    @Unique
    boolean canHurt = true;

    @Unique
    DamageTarget damageTarget = null;

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        if (level().isClientSide || !canHurt) {
            return;
        }

        if (damageTarget == null) {
            return;
        }

        if (getDeltaMovement().lengthSqr() < 0.1) {
            return;
        }

        var box = getBoundingBox().inflate(.5);

        var entities = level().getEntities(this, box, (entity -> damageTarget == DamageTarget.MOB ? entity instanceof LivingEntity && !(entity instanceof Player) : entity instanceof Player));

        var owner = getOwner();
        if (owner == null) {
            owner = this;
        }

        Entity finalOwner = owner;

        entities.forEach(entity -> {
            if (entity == finalOwner || !entity.isAlive() || entity.isRemoved()) {
                return;
            }

            var damageSource = new DamageSource(SuperDamageTypes.holder(level(), SuperDamageTypes.ITEM), finalOwner);

            var damage = 10f;
            var hasHurt = entity.hurt(damageSource, damage);

            if (hasHurt) {
                canHurt = false;
            }
        });

        if (!canHurt) {
            discard();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean isCurrentlyGlowing() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        return ((LocalPlayerDuck) Minecraft.getInstance().player).super_mod$selectedItemEntity() == (Object) this;
    }

    @Override
    public void super_mod$setDamaging(DamageTarget target) {
        this.damageTarget = target;
    }
}
