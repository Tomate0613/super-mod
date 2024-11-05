package dev.doublekekse.super_mod.computer.file_system;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.*;

public class VirtualFileSystem {
    private final Map<String, byte[]> files = new HashMap<>();

    public void createFile(String filename, byte[] data) {
        files.put(filename, data);
    }

    public byte[] readFile(String filename) {
        return files.get(filename);
    }

    public void deleteFile(String filename) {
        files.remove(filename);
    }

    public boolean fileExists(String filename) {
        return files.containsKey(filename);
    }

    public Map<String, byte[]> getCustomFiles() {
        return files;
    }

    public List<String> listFiles() {
        return files.keySet().stream().toList();
    }

    public void writeNbt(CompoundTag compoundTag) {
        var listTag = new ListTag();

        for (var fileName : files.keySet()) {
            var c = new CompoundTag();

            c.putString("key", fileName);
            c.putByteArray("value", files.get(fileName));

            listTag.add(c);
        }

        compoundTag.put("vfs", listTag);
    }

    public void readNbt(CompoundTag compoundTag) {
        var listTag = compoundTag.getList("vfs", 10);

        listTag.forEach((tag) -> {
            var c = (CompoundTag) tag;

            files.put(c.getString("key"), c.getByteArray("value"));
        });
    }
}
