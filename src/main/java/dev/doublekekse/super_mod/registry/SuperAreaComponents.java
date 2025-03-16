package dev.doublekekse.super_mod.registry;

import dev.doublekekse.area_lib.component.AreaDataComponentType;
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.component.SuperComponent;

public class SuperAreaComponents {
    public static final AreaDataComponentType<SuperComponent> SUPER_COMPONENT = AreaDataComponentTypeRegistry.register(SuperMod.id("profile"), SuperComponent::new);
}
