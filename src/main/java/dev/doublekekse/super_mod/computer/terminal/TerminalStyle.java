package dev.doublekekse.super_mod.computer.terminal;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TerminalStyle extends Style {
    final TextColor backgroundColor;

    public TerminalStyle(
        @Nullable TextColor textColor,
        @Nullable TextColor backgroundColor,
        @Nullable Boolean bold,
        @Nullable Boolean italic,
        @Nullable Boolean underline,
        @Nullable Boolean strikethrough,
        @Nullable Boolean obfuscated,
        @Nullable ClickEvent clickEvent,
        @Nullable HoverEvent hoverEvent,
        @Nullable String insertion,
        @Nullable ResourceLocation resourceLocation
    ) {
        super(
            textColor, bold, italic, underline, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, resourceLocation
        );

        this.backgroundColor = backgroundColor;
    }

    public static TerminalStyle of(Style style, @Nullable TextColor backgroundColor) {
        return new TerminalStyle(
            style.getColor(),
            backgroundColor,
            style.isBold(),
            style.isItalic(),
            style.isUnderlined(),
            style.isStrikethrough(),
            style.isObfuscated(),
            style.getClickEvent(),
            style.getHoverEvent(),
            style.getInsertion(),
            style.getFont()
        );
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public TerminalStyle withBackground(@Nullable TextColor backgroundColor) {
        return of(this, backgroundColor);
    }

    public TerminalStyle withBackground(@Nullable ChatFormatting chatFormatting) {
        return withBackground(chatFormatting != null ? TextColor.fromLegacyFormat(chatFormatting) : null);
    }

    @Override
    public @NotNull Style applyTo(Style style) {
        var s = super.applyTo(style);

        return TerminalStyle.of(s, backgroundColor);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!super.equals(object)) {
            return false;
        }

        if (object instanceof TerminalStyle style) {
            return Objects.equals(style.getBackgroundColor(), backgroundColor);
        }

        return false;
    }
}
