package dev.doublekekse.super_mod.registry;

import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class SuperDamageTypes {
    public static final ResourceKey<DamageType> ITEM = ResourceKey.create(Registries.DAMAGE_TYPE, SuperMod.id("item"));

    public static Holder<DamageType> holder(Level level, ResourceKey<DamageType> key) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(holder(level, key));
    }
}
