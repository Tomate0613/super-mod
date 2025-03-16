package dev.doublekekse.super_mod.client;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.client.renderer.blockentity.ComputerDisplayRenderer;
import dev.doublekekse.super_mod.luaj.TableUtils;
import dev.doublekekse.super_mod.packet.ActivateProfilePacket;
import dev.doublekekse.super_mod.packet.RejectSessionPacket;
import dev.doublekekse.super_mod.registry.SuperBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.luaj.vm2.LuaValue;

import java.util.Objects;

public class SuperModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(SuperBlockEntities.COMPUTER_SCREEN_CONTROLLER_BLOCK_ENTITY, ComputerDisplayRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(ActivateProfilePacket.TYPE, (packet, context) -> {
            SuperMod.activateClient(packet.player(), packet.areaId());

            var level = context.client().level;

            assert level != null;

            if (Objects.equals(level.dimension().location(), packet.computerDim())) {
                var be = level.getBlockEntity(packet.computerPos());

                if (be == null) {
                    return;
                }

                if (be instanceof ComputerBlockEntity cbe) {
                    if (cbe.processStack.isEmpty()) {
                        return;
                    }

                    cbe.processStack.peek().superModLib.sessionCallback.call(LuaValue.TRUE, TableUtils.profileTable(packet.areaId()));
                }
            }
        });


        ClientPlayNetworking.registerGlobalReceiver(RejectSessionPacket.TYPE, (packet, context) -> {
            var level = context.client().level;

            assert level != null;


            if (!Objects.equals(level.dimension().location(), packet.computerDim())) {
                return;
            }

            var be = level.getBlockEntity(packet.computerPos());

            if (be == null) {
                return;
            }

            if (!(be instanceof ComputerBlockEntity cbe)) {
                return;
            }

            if (cbe.processStack.isEmpty()) {
                return;
            }

            var callback = cbe.processStack.peek().superModLib.sessionCallback;

            if (callback == null) {
                return;
            }

            callback.call(LuaValue.FALSE, LuaValue.valueOf(packet.reason()));
        });
    }
}
