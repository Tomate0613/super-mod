package dev.doublekekse.super_mod.gui.screen;

import dev.doublekekse.super_mod.block.ComputerBlockEntity;
import dev.doublekekse.super_mod.computer.terminal.TerminalStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.luaj.vm2.LuaValue;

public class ComputerScreen extends Screen {
    final ComputerBlockEntity cbe;
    final boolean isScreen;

    public ComputerScreen(ComputerBlockEntity cbe, boolean isScreen) {
        super(Component.translatable("gui.super_mod.screen.computer"));

        this.cbe = cbe;
        this.isScreen = isScreen && !Minecraft.getInstance().isSingleplayer();

        if(!cbe.isLoaded()) {
            cbe.init();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isScreen) {
            return;
        }

        cbe.triggerEvent("tick");
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float tickProgress) {
        super.render(graphics, x, y, tickProgress);

        if (!isScreen) {
            cbe.triggerEvent("render", LuaValue.valueOf(tickProgress));
        }

        var lines = cbe.terminalOutput.getLines();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) {
                break;
            }


            var backgroundX = 0;
            for (var si : lines[i].getSiblings()) {
                backgroundX += renderBackground(si, graphics, i, backgroundX);
            }

            graphics.drawString(font, lines[i], 100, 10 + i * 10, 0xffffff);
            //graphics.drawString(font, "-", 90, 10 + i * 10, 0xffffff);
        }
    }

    private int renderBackground(Component si, GuiGraphics graphics, int i, int backgroundX) {
        var width = font.width(si);

        if (!(si.getStyle() instanceof TerminalStyle style)) {
            return width;
        }

        var backgroundColor = style.getBackgroundColor();

        if (backgroundColor == null) {
            return width;
        }

        graphics.fill(100 + backgroundX, 9 + i * 10, 100 + backgroundX + width, 20 + i * 10, backgroundColor.getValue() | 0xFF000000);

        return width;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifier) {
        if (Screen.isCopy(key) && !cbe.processStack.isEmpty()) {
            cbe.processStack.peek().stop();
        }

        cbe.triggerEvent("on_key_pressed", LuaValue.valueOf(key), LuaValue.valueOf(scancode), LuaValue.valueOf(modifier));
        return super.keyPressed(key, scancode, modifier);
    }

    @Override
    public boolean charTyped(char c, int i) {
        cbe.triggerEvent("on_char_typed", LuaValue.valueOf(Character.toString(c)));
        return super.charTyped(c, i);
    }
}
