package dev.doublekekse.super_mod.mixin;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.duck.LevelDuck;
import dev.doublekekse.super_mod.duck.MinecraftDuck;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftDuck {
    @Shadow
    @Nullable
    public ClientLevel level;

    @Unique
    DeltaTracker.Timer affectedTimer = new DeltaTracker.Timer(20, 0, this::tickMs);

    @Unique
    float tickMs(float f) {
        return (float) Math.max(50 / SuperMod.speed, f);
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    void runTick(boolean bl, CallbackInfo ci) {
        int i = affectedTimer.advanceTime(Util.getMillis(), bl);

        if (level == null) {
            return;
        }


        for (int j = 0; j < Math.min(10, i); j++) {
            ((LevelDuck) level).super_mod$tickAffected();
        }
    }

    @Override
    public DeltaTracker.Timer superBlock$getAffectedTimer() {
        return affectedTimer;
    }
}
