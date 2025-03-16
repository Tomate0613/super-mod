package dev.doublekekse.super_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.component.SuperComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

import static dev.doublekekse.super_mod.registry.SuperAreaComponents.SUPER_COMPONENT;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SuperCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("super").requires((a) -> a.hasPermission(2)).then(literal("profile").then(literal("create").then(argument("area", AreaArgument.area()).executes(ctx -> {
                var server = ctx.getSource().getServer();
                var area = AreaArgument.getArea(ctx, "area");

                area.put(server, SUPER_COMPONENT, new SuperComponent());

                ctx.getSource().sendSuccess(() -> Component.translatable("command.super_mod.super.profile.create.success"), true);

                return 1;
            }))).then(literal("modify").then(argument("area", AreaArgument.area()).then(literal("rotInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                return modify(ctx, (component, value) -> component.rotInfluence = value);
            }))).then(literal("speedInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                return modify(ctx, (component, value) -> component.speedInfluence = value);
            }))).then(literal("jumpingInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                return modify(ctx, (component, value) -> component.jumpingInfluence = value);
            }))).then(literal("itemUseInfluence").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                return modify(ctx, (component, value) -> component.itemUsageInfluence = value);
            }))).then(literal("offset").then(argument("value", DoubleArgumentType.doubleArg(0, 10)).executes(ctx -> {
                return modify(ctx, (component, value) -> component.offset = value);
            }))))).then(literal("activate").then(argument("area", AreaArgument.area()).executes(ctx -> {
                var server = ctx.getSource().getServer();
                var area = AreaArgument.getArea(ctx, "area");

                var player = ctx.getSource().getPlayer();

                if (player == null) {
                    return 0;
                }

                if (!area.has(SUPER_COMPONENT)) {
                    ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.no_profile"));
                    return 0;
                }

                SuperMod.activateServer(server, player.getUUID(), area, null, null);
                return 1;
            })))).then(literal("stop").executes(ctx -> {
                SuperMod.activateServer(ctx.getSource().getServer(), null, null, null, null);
                return 1;
            }))
        );
    }

    static int modify(CommandContext<CommandSourceStack> ctx, BiConsumer<SuperComponent, Double> modifier) throws CommandSyntaxException {
        var server = ctx.getSource().getServer();
        var area = AreaArgument.getArea(ctx, "area");

        var component = area.get(SUPER_COMPONENT);

        if (component == null) {
            ctx.getSource().sendFailure(Component.translatable("command.super_mod.super.profile.no_profile"));
            return 0;
        }

        modifier.accept(component, DoubleArgumentType.getDouble(ctx, "value"));
        area.put(server, SUPER_COMPONENT, component);

        return 1;
    }

}
