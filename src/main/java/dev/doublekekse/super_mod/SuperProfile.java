package dev.doublekekse.super_mod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SuperProfile {
    public ResourceLocation area;

    public double rotInfluence = 0.001;
    public double speedInfluence = 4.9;
    public double jumpingInfluence = .7;
    public double itemUsageInfluence = .5;
    public double offset = .2;

    public void write(CompoundTag tag) {
        tag.putString("area", area.toString());

        tag.putDouble("rotInfluence", rotInfluence);
        tag.putDouble("speedInfluence", speedInfluence);
        tag.putDouble("jumpingInfluence", jumpingInfluence);
        tag.putDouble("itemUsageInfluence", itemUsageInfluence);
        tag.putDouble("offset", offset);
    }

    public SuperProfile read(CompoundTag tag) {
        area = ResourceLocation.parse(tag.getString("area"));

        rotInfluence = tag.getDouble("rotInfluence");
        speedInfluence = tag.getDouble("speedInfluence");
        jumpingInfluence = tag.getDouble("jumpingInfluence");
        itemUsageInfluence = tag.getDouble("itemUsageInfluence");
        offset = tag.getDouble("offset");

        return this;
    }
}
