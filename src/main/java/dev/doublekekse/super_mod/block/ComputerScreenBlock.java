package dev.doublekekse.super_mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerScreenBlock extends Block {
    public ComputerScreenBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);

        updateControllerScreenSize(level, blockPos);
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);

        updateControllerScreenSize(level, blockPos);
    }

    public void updateControllerScreenSize(Level level, BlockPos blockPos) {
        for (var pos : BlockPos.betweenClosed(blockPos.offset(-10, -10, -10), blockPos.offset(10, 10, 10))) {
            var blockEntity = level.getBlockEntity(pos);

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof ComputerScreenControllerBlockEntity cbe) {
                cbe.calculateSize();
            }
        }
    }
}
