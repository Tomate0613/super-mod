package dev.doublekekse.super_mod.block;

import dev.doublekekse.super_mod.registry.SuperBlockEntities;
import dev.doublekekse.super_mod.registry.SuperBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComputerScreenControllerBlockEntity extends ComputerBlockEntity {
    public int r;
    public int d;

    public ComputerScreenControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SuperBlockEntities.COMPUTER_SCREEN_CONTROLLER_BLOCK_ENTITY, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, ComputerScreenControllerBlockEntity instance) {
        instance.triggerEvent("tick");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.putInt("r", r);
        compoundTag.putInt("d", d);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        r = compoundTag.getInt("r");
        d = compoundTag.getInt("d");

        terminalOutput.setOutputSize(r, d);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var compoundTag = new CompoundTag();
        saveAdditional(compoundTag, provider);

        return compoundTag;
    }

    // TODO: RENAME
    public void calculateSize() {
        var dir = getBlockState().getValue(ComputerScreenControllerBlock.FACING);

        if (dir.getAxis().isHorizontal()) {
            var n = dir.getCounterClockWise();

            checkScreenSize(n, Direction.DOWN);
        }

        if (dir == Direction.DOWN) {
            checkScreenSize(Direction.WEST, Direction.SOUTH);
        }
        if (dir == Direction.UP) {
            checkScreenSize(Direction.WEST, Direction.NORTH);
        }

        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
    }

    void checkScreenSize(Direction r, Direction d) {
        this.r = checkDirection(r);
        this.d = checkDirection(d);
    }

    int checkDirection(Direction r) {
        var length = 0;

        var pos = getBlockPos();
        var keepChecking = true;
        while (keepChecking) {
            pos = pos.relative(r);
            length++;

            if (!level.getBlockState(pos).is(SuperBlocks.COMPUTER_SCREEN_BLOCK)) {
                keepChecking = false;
            }
        }

        return length;
    }
}
