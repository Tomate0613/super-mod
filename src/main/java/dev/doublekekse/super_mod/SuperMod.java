package dev.doublekekse.super_mod;

import com.mojang.logging.LogUtils;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.command.SuperCommand;
import dev.doublekekse.super_mod.data.SuperSavedData;
import dev.doublekekse.super_mod.packet.*;
import dev.doublekekse.super_mod.registry.SuperBlockEntities;
import dev.doublekekse.super_mod.registry.SuperBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class SuperMod implements ModInitializer {
    public static double speed;
    public static final Map<String, byte[]> defaultFiles = new HashMap<>();
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SuperProfile activeProfile;
    static UUID activePlayer;
    static Area activeArea;

    public static final CreativeModeTab SUPER_MOD_STUFF = FabricItemGroup.builder()
        .icon(() -> new ItemStack(SuperBlocks.COMPUTER_BLOCK))
        .title(Component.translatable("itemGroup.super_mod.stuff"))
        .displayItems((context, entries) -> {
            entries.accept(SuperBlocks.COMPUTER_BLOCK);
            entries.accept(SuperBlocks.COMPUTER_SCREEN_BLOCK);
            entries.accept(SuperBlocks.COMPUTER_SCREEN_CONTROLLER_BLOCK);
        })
        .build();

    /**
     * Portions of this code were adapted from https://github.com/HamaIndustries/Rochefort-Mills
     * See {@link dev.doublekekse.super_mod.mixin.MouseHandlerMixin} for license
     */
    public static boolean putFile(Minecraft client, List<Path> paths, int invalidFilesCount) {
        if (client.isPaused()
            || client.player == null
            || client.level == null
            || !client.player.isAlive()
            || client.hitResult == null
            || !client.hitResult.getType().equals(HitResult.Type.BLOCK)
            || !(client.hitResult instanceof BlockHitResult bhr)
        ) {
            return false;
        }

        var be = client.level.getBlockEntity(bhr.getBlockPos());

        if (!(be instanceof ComputerBlockEntity cbe)) {
            return false;
        }

        paths.forEach(path -> {
            try (InputStream in = new FileInputStream(path.toFile())) {
                byte[] data = in.readAllBytes();
                cbe.vfs.createFile(String.valueOf(path.getFileName()), data);
            } catch (IOException e) {
                LOGGER.error("Failed to import file", e);
            }
        });

        return true;
    }

    @Override
    public void onInitialize() {
        SuperBlocks.init();
        SuperBlockEntities.init();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("stuff"), SUPER_MOD_STUFF);

        PayloadTypeRegistry.playC2S().register(PickupItemPacket.TYPE, PickupItemPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SetSpeedPacket.TYPE, SetSpeedPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(UploadComputerPacket.TYPE, UploadComputerPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSessionPacket.TYPE, RequestSessionPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(CallSuperFunctionPacket.TYPE, CallSuperFunctionPacket.STREAM_CODEC);

        PayloadTypeRegistry.playS2C().register(ActivateProfilePacket.TYPE, ActivateProfilePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RejectSessionPacket.TYPE, RejectSessionPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PickupItemPacket.TYPE, (payload, context) -> {
            var entity = context.player().level().getEntity(payload.id());

            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setNoPickUpDelay();
                itemEntity.playerTouch(context.player());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SetSpeedPacket.TYPE, (payload, context) -> {
            SuperMod.speed = payload.speed();
        });

        ServerPlayNetworking.registerGlobalReceiver(RequestSessionPacket.TYPE, (payload, context) -> {
            var currentPlayer = context.server().getPlayerList().getPlayer(activePlayer);

            if (currentPlayer != null) {
                context.responseSender().sendPacket(new RejectSessionPacket(payload.computerDim(), payload.computerPos(), "Active session"));
                return;
            }

            var savedData = SuperSavedData.getServerData(context.server());
            var profile = getProfile(savedData, payload.profileId());

            if (profile == null) {
                context.responseSender().sendPacket(new RejectSessionPacket(payload.computerDim(), payload.computerPos(), "Unknown profile"));
                return;
            }

            SuperMod.activateServer(context.server(), context.player().getUUID(), profile);

            context.responseSender().sendPacket(new ActivateProfilePacket(activeProfile, activePlayer, payload.computerDim(), payload.computerPos()));

            for (var player : context.server().getPlayerList().getPlayers()) {
                if (player == context.player()) {
                    continue;
                }

                ServerPlayNetworking.send(player, new ActivateProfilePacket(activeProfile, activePlayer, payload.computerDim(), payload.computerPos()));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(UploadComputerPacket.TYPE, (payload, context) -> {
            if (!context.player().canUseGameMasterBlocks()) {
                return;
            }

            var level = context.server().getLevel(payload.dimension());

            if (level == null) {
                return;
            }

            var be = level.getBlockEntity(payload.pos());

            if (!(be instanceof ComputerBlockEntity cbe)) {
                return;
            }

            cbe.load(payload.vfs());
        });


        ServerPlayNetworking.registerGlobalReceiver(CallSuperFunctionPacket.TYPE, (payload, context) -> {
            if (payload.function() == null) {
                return;
            }

            var stack = context.player().createCommandSourceStack().withSuppressedOutput().withMaximumPermission(2);

            context.player().connection.detectRateSpam();
            context.server().getCommands().performPrefixedCommand(stack, "function super_mod:" + payload.function());
        });

        ServerPlayConnectionEvents.JOIN.register((listener, packetSender, server) -> {
            packetSender.sendPacket(new ActivateProfilePacket(activeProfile, activePlayer, null, null));
        });

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                SuperCommand.register(dispatcher);
            }
        );

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ResourceListener());
    }

    public static boolean isAffected(Entity entity) {
        if (entity instanceof Player) {
            return false;
        }

        if (activeProfile == null || activeArea == null) {
            return false;
        }

        return activeArea.contains(entity);
    }

    public static void activateClient(UUID player, SuperProfile profile) {
        if (profile != null) {
            activeArea = AreaLib.getClientArea(profile.area);
        }

        activePlayer = player;
        activeProfile = profile;
    }

    public static void activateServer(MinecraftServer server, UUID player, SuperProfile profile) {
        if (profile != null) {
            activeArea = AreaLib.getServerArea(server, profile.area);
        }

        activePlayer = player;
        activeProfile = profile;
    }

    public static boolean isHot(Player player) {
        return Objects.equals(activePlayer, player.getUUID());
    }

    @Environment(EnvType.CLIENT)
    public static boolean isHot() {
        assert Minecraft.getInstance().player != null;

        return isHot(Minecraft.getInstance().player);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("super_mod", path);
    }

    public static @Nullable SuperProfile getProfile(SuperSavedData savedData, ResourceLocation id) {
        for (var profile : savedData.profiles) {
            if (profile.area.equals(id)) {
                return profile;
            }
        }

        return null;
    }
}
