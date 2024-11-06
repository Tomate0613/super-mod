package dev.doublekekse.super_mod.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.duck.LocalPlayerDuck;
import dev.doublekekse.super_mod.duck.PlayerDuck;
import dev.doublekekse.super_mod.gui.screen.ComputerScreen;
import dev.doublekekse.super_mod.packet.PickupItemPacket;
import dev.doublekekse.super_mod.packet.SetSpeedPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements LocalPlayerDuck, PlayerDuck {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    private float xRotLast;

    @Shadow
    private float yRotLast;

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    @Final
    public ClientPacketListener connection;

    @Unique
    public ItemEntity itemEntity;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        if (!SuperMod.isHot()) {
            return;
        }

        var profile = SuperMod.activeProfile;

        if (profile == null) {
            return;
        }

        double dyRot = (this.getYRot() - this.yRotLast);
        double dxRot = (this.getXRot() - this.xRotLast);

        var rot = (Math.abs(dxRot) + Math.abs(dyRot)) * profile.rotInfluence;

        var currentSpeed = getDeltaMovement().horizontalDistance() * profile.speedInfluence + (jumping ? profile.jumpingInfluence : 0) + (isUsingItem() ? profile.itemUsageInfluence : 0) + rot;

        //SuperBlock.speed = .1f * currentSpeed + .9f * SuperBlock.speed;
        SuperMod.speed = currentSpeed + profile.offset;

        if (!minecraft.isSingleplayer()) {
            this.connection.send(ClientPlayNetworking.createC2SPacket(new SetSpeedPacket(SuperMod.speed)));
        }

        if (false) {
            this.itemEntity = null;
            return;
        }

        var hitResult = calculateHitResult(this);
        if (hitResult == null) {
            this.itemEntity = null;
            return;
        }

        if (!(hitResult.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        this.itemEntity = itemEntity;

    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    void drop(boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!SuperMod.isHot()) {
            return;
        }

        if (/*getMainHandItem().isEmpty() &&*/ itemEntity != null) {
            ClientPlayNetworking.send(new PickupItemPacket(itemEntity.getId()));
            cir.cancel();
        }
    }

    @Unique
    private EntityHitResult calculateHitResult(Player player) {
        Vec3 viewVector = player.getViewVector(0.0f).scale(player.blockInteractionRange() / 2);
        Level level = player.level();
        Vec3 eyePosition = player.getEyePosition();

        return getHitResult(eyePosition, player, entity -> entity.getType() == EntityType.ITEM, viewVector, level);
    }


    @Unique
    private static EntityHitResult getHitResult(Vec3 eyePosition, Entity entity, Predicate<Entity> predicate, Vec3 viewVector, Level level) {
        Vec3 endPosition = eyePosition.add(viewVector);

        return ProjectileUtil.getEntityHitResult(level, entity, eyePosition, endPosition, entity.getBoundingBox().expandTowards(viewVector).inflate(1.0), predicate, (float) 0.5);
    }

    @Override
    public ItemEntity super_mod$selectedItemEntity() {
        return itemEntity;
    }

    @Override
    public void super_mod$openComputer(ComputerBlockEntity cbe, boolean isScreen) {
        minecraft.setScreen(new ComputerScreen(cbe, isScreen));
    }
}
