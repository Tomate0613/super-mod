package dev.doublekekse.super_mod.registry;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.block.ComputerScreenControllerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SuperBlockEntities {
    public static final BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY = register(
        BlockEntityType.Builder.of(ComputerBlockEntity::new, SuperBlocks.COMPUTER_BLOCK).build(),
        "computer_block"
    );
    public static final BlockEntityType<ComputerScreenControllerBlockEntity> COMPUTER_SCREEN_CONTROLLER_BLOCK_ENTITY = register(
        BlockEntityType.Builder.of(ComputerScreenControllerBlockEntity::new, SuperBlocks.COMPUTER_SCREEN_CONTROLLER_BLOCK).build(),
        "computer_screen_controller_block"
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(BlockEntityType<T> type, String path) {
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            SuperMod.id(path),
            type
        );
    }

    public static void init() {
    }
}
