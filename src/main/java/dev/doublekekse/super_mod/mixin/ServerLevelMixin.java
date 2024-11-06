package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.LevelDuck;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements LevelDuck {
    @Shadow
    @Final
    EntityTickList entityTickList;

    @Shadow
    public abstract void tickNonPassenger(Entity entity);

    @Unique
    boolean t;

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    void tickNonPassenger(Entity entity, CallbackInfo ci) {
        if (SuperMod.isAffected(entity) && !t) {
            ci.cancel();
        }
    }

    @Unique
    float tickMs(float f) {
        return (float) Math.max(50 / SuperMod.speed, f);
    }


    @Unique
    private long lastMs;
    @Unique
    private float deltaTickResidual;

    @Unique
    int advanceTime(long l) {
        float deltaTicks = (float) (l - this.lastMs) / tickMs(50f);
        this.lastMs = l;
        this.deltaTickResidual = this.deltaTickResidual + deltaTicks;
        int i = (int) this.deltaTickResidual;
        this.deltaTickResidual -= (float) i;
        return i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        int i = advanceTime(Util.getMillis());

        for (int j = 0; j < Math.min(10, i); j++) {
            super_mod$tickAffected();
        }
    }

    @Override
    public void super_mod$tickAffected() {
        entityTickList.forEach((entity) -> {
            if (!entity.isRemoved() && SuperMod.isAffected(entity)) {
                t = true;
                guardEntityTick(this::tickNonPassenger, entity);
                t = false;
            }
        });
    }
}
