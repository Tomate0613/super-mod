package dev.doublekekse.super_mod.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.doublekekse.super_mod.block.ComputerScreenControllerBlock;
import dev.doublekekse.super_mod.block.ComputerScreenControllerBlockEntity;
import dev.doublekekse.super_mod.computer.terminal.TerminalStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.luaj.vm2.LuaValue;

public class ComputerDisplayRenderer implements BlockEntityRenderer<ComputerScreenControllerBlockEntity> {
    Font font;

    public ComputerDisplayRenderer(BlockEntityRendererProvider.Context context) {
        font = Minecraft.getInstance().font;
    }

    @Override
    public void render(ComputerScreenControllerBlockEntity blockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        //var lines = blockEntity.terminalOutput.getLines();
        poseStack.pushPose();

        //var stack = new ItemStack(Items.ACACIA_FENCE, 1);

        // Calculate the current offset in the y value
        //double offset = Math.sin((blockEntity.getLevel().dayTime() + tickDelta) / 8.0) / 4.0;
        // Move the item
        //poseStack.translate(0.5, 1.25 + offset, 0.5);

        // Rotate the item
        //poseStack.mulPose(Direction.NORTH.getRotation());

        //Minecraft.getInstance().getItemRenderer().render(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        //Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, poseStack, multiBufferSource, blockEntity.getLevel(), 0);

        renderText(blockEntity, tickDelta, poseStack, multiBufferSource, false);


        //dir.
        // Mandatory call after GL calls
        poseStack.popPose();
    }


    private void translateSignText(PoseStack poseStack, boolean bl, ComputerScreenControllerBlockEntity blockEntity) {
        if (!bl) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }


        //poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        float f = 0.01f;
        //poseStack.translate(-1, 1, 0);

        var dir = blockEntity.getBlockState().getValue(ComputerScreenControllerBlock.FACING);

        switch (dir) {
            case UP -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.translate(-1, 1, 1);
            }
            case DOWN -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(-1, 0, 0);
            }
            case EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.translate(-1, 1, 1);
            }
            case WEST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(0, 1, 0);
            }
            case NORTH -> {
                poseStack.translate(-1, 1, 0);
            }
            case SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(0, 1, 1);
            }
        }

        //poseStack.mulPose(dir.getRotation());
        //poseStack.mulPose(Axis.XN.rotationDegrees(-90));
        poseStack.scale(f, -f, f);
    }

    void renderText(ComputerScreenControllerBlockEntity cbe, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, boolean bl) {
        poseStack.pushPose();

        translateSignText(poseStack, bl, cbe);

        cbe.triggerEvent("render", LuaValue.valueOf(tickDelta));

        var lines = cbe.terminalOutput.getLines();
        var canDraw = cbe.d * 10;

        for (int i = 0; i < lines.length; i++) {
            var backgroundX = 0;
            for (var si : lines[i].getSiblings()) {
                backgroundX += renderBackground(si, poseStack, multiBufferSource, i, backgroundX);
            }

            FormattedCharSequence formattedCharSequence = lines[i].getVisualOrderText();
            this.font.drawInBatch(formattedCharSequence, 10, 10 + (i) * 10, 0xffffff, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, 0xffffff);
        }

        poseStack.popPose();
    }

    private int renderBackground(Component si, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int backgroundX) {
        var width = font.width(si);

        if (!(si.getStyle() instanceof TerminalStyle style)) {
            return width;
        }

        var backgroundColor = style.getBackgroundColor();

        if (backgroundColor == null) {
            return width;
        }


        //graphics.fill(100 + backgroundX, 9 + i * 10, 100 + backgroundX + width, 20 + i * 10, backgroundColor.getValue() | 0xFF000000);

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.debugFilledBox());

        //vertexConsumer.addVertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), k, vertex.u, vertex.v, j, i, f, g, h);
        var matrix4f = poseStack.last();

        var light = 15728640;

        var color = backgroundColor.getValue() | 0xFF000000;

        var rpos = backgroundX + width;

        var o = 10;

        vertexConsumer.addVertex(matrix4f, rpos + o, i * 10 - 1 + o, 0).setColor(color).setLight(light).setNormal(0, 0, 1).setUv(1, 0).setUv1(1, 0);
        vertexConsumer.addVertex(matrix4f, backgroundX + o, i * 10 - 1 + o, 0).setColor(color).setLight(light).setNormal(0, 0, 1).setUv(0, 0).setUv1(0, 0);
        vertexConsumer.addVertex(matrix4f, rpos + o, i * 10 + 9 + o, 0).setColor(color).setLight(light).setNormal(0, 0, 1).setUv(1, 1).setUv1(1, 1);
        vertexConsumer.addVertex(matrix4f, backgroundX + o, i * 10 + 9 + o, 0).setColor(color).setLight(light).setNormal(0, 0, 1).setUv(0, 1).setUv1(0, 1);

        return width;
    }
}
