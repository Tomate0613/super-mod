package dev.doublekekse.super_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.SuperProfile;
import dev.doublekekse.super_mod.data.SuperSavedData;
import dev.doublekekse.super_mod.packet.ActivateProfilePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

import static dev.doublekekse.super_mod.SuperMod.getProfile;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SuperCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("super").requires((a) -> a.hasPermission(2)).then(literal("profile").then(literal("create").then(argument("area", ResourceLocationArgument.id()).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");

                var profile = new SuperProfile();
                profile.area = id;

                if (AreaLib.getServerArea(ctx.getSource().getServer(), id) == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.create.no_area"));
                    return 0;
                }

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                savedData.profiles.add(profile);
                savedData.setDirty();

                ctx.getSource().sendSuccess(() -> Component.translatable("command.super_mod.super.profile.create.success"), true);

                return 1;
            }))).then(literal("modify").then(argument("area", ResourceLocationArgument.id()).then(literal("rotInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var value = DoubleArgumentType.getDouble(ctx, "value");

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                if (profile == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.modify.no_profile"));
                    return 0;
                }

                profile.rotInfluence = value;
                savedData.setDirty();

                return 1;
            }))).then(literal("speedInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var value = DoubleArgumentType.getDouble(ctx, "value");

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                if (profile == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.modify.no_profile"));
                    return 0;
                }

                profile.speedInfluence = value;
                savedData.setDirty();

                return 1;
            }))).then(literal("jumpingInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var value = DoubleArgumentType.getDouble(ctx, "value");

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                if (profile == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.modify.no_profile"));
                    return 0;
                }

                profile.jumpingInfluence = value;
                savedData.setDirty();

                return 1;
            }))).then(literal("itemUseInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var value = DoubleArgumentType.getDouble(ctx, "value");

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                if (profile == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.modify.no_profile"));
                    return 0;
                }

                profile.itemUsageInfluence = value;
                savedData.setDirty();

                return 1;
            }))).then(literal("offset").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var value = DoubleArgumentType.getDouble(ctx, "value");

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                if (profile == null) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.modify.no_profile"));
                    return 0;
                }

                profile.offset = value;
                savedData.setDirty();

                return 1;
            }))))).then(literal("activate").then(argument("area", ResourceLocationArgument.id()).executes(ctx -> {
                var id = ResourceLocationArgument.getId(ctx, "area");
                var player = ctx.getSource().getPlayer();

                if (player == null) {
                    return 0;
                }

                var savedData = SuperSavedData.getServerData(ctx.getSource().getServer());
                var profile = getProfile(savedData, id);

                for (var s : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send(s, new ActivateProfilePacket(profile, player.getUUID(), null, null));
                }

                SuperMod.activateServer(ctx.getSource().getServer(), player.getUUID(), profile);
                return 1;
            })))).then(literal("stop").executes(ctx -> {
                for (var s : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send(s, new ActivateProfilePacket(null, null, null, null));
                }

                SuperMod.activateServer(ctx.getSource().getServer(), null, null);
                return 1;
            }))
        );
    }

}
