package dev.doublekekse.super_mod.block;

import dev.doublekekse.super_mod.luaj.TableUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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


    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        var pos = blockHitResult.getLocation();

        sendInteraction(level, blockPos, pos);

        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    public void sendInteraction(Level level, BlockPos blockPos, Vec3 interactPos) {
        for (var pos : BlockPos.betweenClosed(blockPos.offset(-10, -10, -10), blockPos.offset(10, 10, 10))) {
            var blockEntity = level.getBlockEntity(pos);

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof ComputerScreenControllerBlockEntity cbe) {
                cbe.triggerEvent("screen_interact_nearby", TableUtils.positionTable(interactPos), TableUtils.positionTable(blockPos), TableUtils.positionTable(pos));
            }
        }
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
