package dev.doublekekse.super_mod.duck;

public interface ItemEntityDuck {
    enum DamageTarget {
        PLAYER,
        MOB
    }

    void super_mod$setDamaging(DamageTarget target);
}
