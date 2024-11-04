package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.LevelDuck;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements LevelDuck {
    protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Shadow
    @Final
    EntityTickList tickingEntities;

    @Shadow
    public abstract void tickNonPassenger(Entity entity);

    @Unique
    boolean t;

    @Inject(
        method = "tickNonPassenger",
        cancellable = true,
        at = @At("HEAD")
    )
    private void tickNonPassenger(Entity entity, CallbackInfo ci) {
        if (SuperMod.isAffected(entity) && !t) {
            ci.cancel();
        }
    }

    @Override
    public void super_mod$tickAffected() {
        tickingEntities.forEach((entity) -> {
            if (!entity.isRemoved() && !entity.isPassenger() && SuperMod.isAffected(entity)) {
                //if (!player.hasVehicle() && !player.isRemoved() && gs_tpsModule.isPlayerFixedMovement(player))
                t = true;
                guardEntityTick(this::tickNonPassenger, entity);
                t = false;
            }
        });
    }
}
