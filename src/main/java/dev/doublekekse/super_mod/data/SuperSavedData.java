package dev.doublekekse.super_mod.data;

import dev.doublekekse.super_mod.SuperProfile;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SuperSavedData extends SavedData {
    public final List<SuperProfile> profiles = new ArrayList<>();

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var listTag = new ListTag();

        profiles.forEach((profile) -> {
            var tag = new CompoundTag();

            profile.write(tag);
            listTag.add(tag);
        });

        compoundTag.put("profiles", listTag);
        return compoundTag;
    }

    public static SuperSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new SuperSavedData();

        var listTag = compoundTag.getList("profiles", 10);

        listTag.forEach(tag -> {
            var profile = new SuperProfile();

            profile.read((CompoundTag) tag);
            data.profiles.add(profile);
        });

        return data;
    }

    public static SuperSavedData getServerData(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();
        SuperSavedData data = persistentStateManager.computeIfAbsent(factory, "supermod");
        data.setDirty();

        return data;
    }

    private static final SavedData.Factory<SuperSavedData> factory = new SavedData.Factory<>(
        SuperSavedData::new,
        SuperSavedData::load,
        null
    );
}
