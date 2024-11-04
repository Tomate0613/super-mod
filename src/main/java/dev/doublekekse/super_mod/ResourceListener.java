package dev.doublekekse.super_mod;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.intellij.lang.annotations.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.MESAFramebufferSwapXY;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ResourceListener implements IdentifiableResourceReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
            var scripts = resourceManager.listResources("scripts", (s) -> s.getPath().endsWith(".lua"));

            scripts.forEach((location, resource) -> {
                try (var reader = resource.openAsReader()) {
                    var file = reader.lines().collect(Collectors.joining("\n"));
                    var filename = location.getPath().substring(8);

                    SuperMod.defaultFiles.put(filename, file.getBytes());
                } catch (Exception e) {
                    //TUTORIAL_LOG.error("Error occurred while loading resource json" + id.toString(), e);
                }
            });
        });
    }

    @Override
    public ResourceLocation getFabricId() {
        return SuperMod.id("scripts");
    }
}
