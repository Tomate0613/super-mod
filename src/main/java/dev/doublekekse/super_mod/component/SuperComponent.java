package dev.doublekekse.super_mod.component;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;

public class SuperComponent implements AreaDataComponent {
    public double rotInfluence = 0.001;
    public double speedInfluence = 4.9;
    public double jumpingInfluence = .7;
    public double itemUsageInfluence = .5;
    public double offset = .2;

    @Override
    public void load(AreaSavedData areaSavedData, CompoundTag tag) {
        rotInfluence = tag.getDouble("rotInfluence");
        speedInfluence = tag.getDouble("speedInfluence");
        jumpingInfluence = tag.getDouble("jumpingInfluence");
        itemUsageInfluence = tag.getDouble("itemUsageInfluence");
        offset = tag.getDouble("offset");
    }

    @Override
    public CompoundTag save() {
        var tag = new CompoundTag();

        tag.putDouble("rotInfluence", rotInfluence);
        tag.putDouble("speedInfluence", speedInfluence);
        tag.putDouble("jumpingInfluence", jumpingInfluence);
        tag.putDouble("itemUsageInfluence", itemUsageInfluence);
        tag.putDouble("offset", offset);

        return tag;
    }
}
