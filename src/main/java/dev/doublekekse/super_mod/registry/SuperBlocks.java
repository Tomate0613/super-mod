package dev.doublekekse.super_mod.registry;

import dev.doublekekse.super_mod.SuperMod;
import dev.doublekekse.super_mod.block.ComputerBlock;
import dev.doublekekse.super_mod.block.ComputerScreenBlock;
import dev.doublekekse.super_mod.block.ComputerScreenControllerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SuperBlocks {
    public static final ComputerBlock COMPUTER_BLOCK = registerWithItem(new ComputerBlock(BlockBehaviour.Properties.of().noOcclusion()), "computer_block");
    public static final ComputerScreenControllerBlock COMPUTER_SCREEN_CONTROLLER_BLOCK = registerWithItem(new ComputerScreenControllerBlock(BlockBehaviour.Properties.of()), "computer_screen_controller_block");
    public static final ComputerScreenBlock COMPUTER_SCREEN_BLOCK = registerWithItem(new ComputerScreenBlock(BlockBehaviour.Properties.of()), "computer_screen_block");

    private static <T extends Block> T register(T block, String path) {
        ResourceLocation blockId = SuperMod.id(path);
        return Registry.register(BuiltInRegistries.BLOCK, blockId, block);
    }

    private static <T extends Block> T registerWithItem(T block, String path) {
        ResourceLocation blockId = SuperMod.id(path);
        Registry.register(BuiltInRegistries.ITEM, blockId, new BlockItem(block, new Item.Properties()));
        return Registry.register(BuiltInRegistries.BLOCK, blockId, block);
    }

    public static void init() {
    }
}
