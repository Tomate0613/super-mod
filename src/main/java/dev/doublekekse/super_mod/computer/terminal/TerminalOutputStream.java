package dev.doublekekse.super_mod.computer.terminal;

import dev.doublekekse.super_mod.CircularBuffer;
import dev.doublekekse.super_mod.SuperMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TerminalOutputStream extends OutputStream {
    private CircularBuffer<ArrayList<Component>> lines = new CircularBuffer<>(40);
    private TerminalStyle currentStyle = defaultStyle();

    int cursorX;
    int cursorLine;

    int savedCursorX;
    int savedCursorLine;

    public int screenSizeX = 1;
    public int screenSizeY = 1;

    private enum State {
        NORMAL, ESCAPE, CSI
    }

    private State state = State.NORMAL;
    private final StringBuilder escapeSequence = new StringBuilder();

    @Override
    public void write(int b) {
        // Convert the byte to a char and process it
        char c = (char) b;
        processChar(c);
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) {
        for (int i = off; i < off + len; i++) {
            write(b[i]);
        }
    }

    // Process each character and handle ANSI codes
    private void processChar(char c) {
        switch (state) {
            case NORMAL -> {
                if (c == '\u001B') {
                    state = State.ESCAPE;
                    escapeSequence.setLength(0);
                    escapeSequence.append(c);
                } else {
                    processNormal(c);
                }
            }

            case ESCAPE -> {
                if (c == '[') { // CSI sequence
                    state = State.CSI;
                    escapeSequence.append(c);
                } else {
                    //fragStr.append(escapeSequence);
                    processNormal('\u001B');
                    processNormal(c);
                    state = State.NORMAL;
                }
            }

            case CSI -> {
                escapeSequence.append(c);
                if (isCsiTerminator(c)) { // CSI sequences typically end with a letter
                    handleEscapeSequence(escapeSequence.toString()); // Process the complete CSI sequence
                    state = State.NORMAL; // Reset to normal state
                }
            }
        }
    }

    private void handleEscapeSequence(String sequence) {
        String params = sequence.substring(2);

        String[] paramParts = params.split(";");

        char command = paramParts[paramParts.length - 1].charAt(paramParts[paramParts.length - 1].length() - 1);

        switch (command) {
            case 'm': // Text formatting (SGR)
                handleSgr(paramParts);
                break;
            case 'A': // Move cursor up
            case 'B': // Move cursor down
            case 'C': // Move cursor right
            case 'D': // Move cursor left
            case 'H': // Move cursor
                handleCursorMovement(command, paramParts);
                break;
            case 'J': // Screen clearing
            case 'K': // Line clearing
                handleScreenOrLineClear(command, paramParts);
                break;
            case 's':
                savedCursorLine = cursorLine;
                savedCursorX = cursorX;
                break;
            case 'u':
                cursorLine = savedCursorLine;
                cursorX = savedCursorX;
                break;
            default:
                SuperMod.LOGGER.warn("Unknown ANSI sequence: {}", sequence);
                break;
        }
    }

    private void handleSgr(String[] params) {
        for (String param : params) {
            int code;
            try {
                code = Integer.parseInt(param.replaceAll("\\D", ""));
            } catch (NumberFormatException e) {
                continue;
            }

            switch (code) {
                case 0:
                    currentStyle = defaultStyle();
                    break;
                case 1:
                    currentStyle = TerminalStyle.of(currentStyle.withBold(true), currentStyle.getBackgroundColor());
                    break;
                case 3:
                    currentStyle = TerminalStyle.of(currentStyle.withItalic(true), currentStyle.getBackgroundColor());
                    break;
                case 4:
                    currentStyle = TerminalStyle.of(currentStyle.withUnderlined(true), currentStyle.getBackgroundColor());
                    break;
                case 7:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(currentStyle.getBackgroundColor()), currentStyle.getColor());
                    break;
                case 9:
                    currentStyle = TerminalStyle.of(currentStyle.withStrikethrough(true), currentStyle.getBackgroundColor());
                    break;
                case 22:
                    currentStyle = TerminalStyle.of(currentStyle.withBold(false), currentStyle.getBackgroundColor());
                    break;
                case 23:
                    currentStyle = TerminalStyle.of(currentStyle.withItalic(false), currentStyle.getBackgroundColor());
                    break;
                case 24:
                    currentStyle = TerminalStyle.of(currentStyle.withUnderlined(false), currentStyle.getBackgroundColor());
                    break;
                case 29:
                    currentStyle = TerminalStyle.of(currentStyle.withStrikethrough(false), currentStyle.getBackgroundColor());
                    break;


                case 30:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.BLACK), currentStyle.getBackgroundColor());
                    break;
                case 31:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.RED), currentStyle.getBackgroundColor());
                    break;
                case 32:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.GREEN), currentStyle.getBackgroundColor());
                    break;
                case 33:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.YELLOW), currentStyle.getBackgroundColor());
                    break;
                case 34:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.BLUE), currentStyle.getBackgroundColor());
                    break;
                case 35:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.DARK_PURPLE), currentStyle.getBackgroundColor());
                    break;

                case 36:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.AQUA), currentStyle.getBackgroundColor());
                    break;

                case 37:
                case 39:
                    currentStyle = TerminalStyle.of(currentStyle.withColor(ChatFormatting.WHITE), currentStyle.getBackgroundColor());
                    break;

                case 40:
                    currentStyle = currentStyle.withBackground(ChatFormatting.BLACK);
                    break;
                case 41:
                    currentStyle = currentStyle.withBackground(ChatFormatting.RED);
                    break;
                case 42:
                    currentStyle = currentStyle.withBackground(ChatFormatting.GREEN);
                    break;
                case 43:
                    currentStyle = currentStyle.withBackground(ChatFormatting.YELLOW);
                    break;
                case 44:
                    currentStyle = currentStyle.withBackground(ChatFormatting.BLUE);
                    break;
                case 45:
                    currentStyle = currentStyle.withBackground(ChatFormatting.DARK_PURPLE);
                    break;
                case 46:
                    currentStyle = currentStyle.withBackground(ChatFormatting.AQUA);
                    break;
                case 47:
                    currentStyle = currentStyle.withBackground(ChatFormatting.WHITE);
                    break;
                case 49:
                    currentStyle = currentStyle.withBackground((TextColor) null);
                    // Add more cases for other SGR codes as needed
                default:
                    SuperMod.LOGGER.warn("Unhandled SGR code: {}", code);
                    break;
            }
        }
    }

    int parseA(String[] params, int index, int fallback) {
        if (params.length <= index) {
            return fallback;
        }

        var filtered = params[index].replaceAll("\\D", "");

        return NumberUtils.toInt(filtered, fallback);
    }

    private void handleCursorMovement(char command, String[] params) {
        int n = parseA(params, 0, 1);

        switch (command) {
            case 'A':
                cursorLine -= n;
                if (cursorLine < 0) {
                    cursorLine = 0;
                }
                break;
            case 'B':
                cursorLine += n;
                if (cursorLine >= lines.limit()) {
                    cursorLine = lines.limit() - 1;
                }

                break;
            case 'C':
                cursorX += n;
                break;
            case 'D':
                cursorX -= n;

                if (cursorX < 0) {
                    cursorX = 0;
                }
                break;
            case 'H':
                int q = parseA(params, 1, 1);
                cursorLine = n - 1;
                cursorX = q - 1;

                cursorBounds();

                break;
        }
    }

    void cursorBounds() {
        if (cursorLine >= lines.limit()) {
            cursorLine = lines.limit() - 1;
        }

        if (cursorLine < 0) {
            cursorLine = 0;
        }

        if (cursorX < 0) {
            cursorX = 0;
        }
    }

    private void handleScreenOrLineClear(char command, String[] params) {
        if (command == 'J') {
            lines.clear();
        } else if (command == 'K') {
            var oldLines = lines;
            lines = new CircularBuffer<>(lines.limit());

            for (int i = 0; i < oldLines.size(); i++) {
                if (i == cursorLine) {
                    continue;
                }

                lines.add(oldLines.get(i));
            }
        }
    }

    public void reset() {
        lines.clear();
        cursorX = 0;
        cursorLine = 0;
        savedCursorLine = 0;
        savedCursorX = 0;
        currentStyle = defaultStyle();
        this.state = State.NORMAL;
    }

    static TerminalStyle defaultStyle() {
        return TerminalStyle.of(Style.EMPTY.withFont(SuperMod.id("departure_mono")), null);
    }

    private boolean isCsiTerminator(char c) {
        // CSI sequences usually end with letters or certain symbols
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '@' || c == '~');
    }

    private void processNormal(char c) {
        if (c == '\r') {
            cursorX = 0;
        } else if (c == '\n') {
            cursorLine++;

            if (cursorLine >= lines.limit()) {
                cursorLine = lines.limit() - 1;
                lines.add(emptyLine());
            }

            cursorX = 0;
        } else {
            putChar(c);
            cursorX++;
        }
    }

    private ArrayList<Component> emptyLine() {
        var n = new ArrayList<Component>();
        n.add(Component.literal(""));

        return n;
    }

    private void putChar(char c) {
        if (cursorLine >= lines.limit()) {
            cursorLine = lines.limit() - 1;
        }

        while (lines.size() < cursorLine + 1) {
            lines.add(emptyLine());
        }
        var line = lines.get(cursorLine);

        var totalCharacterCount = 0;
        var segmentStartCharacterCount = 0;
        var segmentLength = 0;
        int segmentIndex = 0;

        boolean end = false;

        for (int s = 0; s < line.size(); s++) {
            totalCharacterCount += line.get(s).getString().length();

            if (end) {
                continue;
            }

            if (totalCharacterCount > cursorX) {
                segmentLength = line.get(s).getString().length();
                segmentIndex = s;
                segmentStartCharacterCount = totalCharacterCount - segmentLength;
                end = true;
            }
        }

        if (totalCharacterCount == cursorX && !end) {
            segmentLength = line.getLast().getString().length();
            segmentIndex = line.size() - 1;
            segmentStartCharacterCount = totalCharacterCount - segmentLength;
        }

        if (cursorX > totalCharacterCount) {
            line.add(Component.literal(" ".repeat(cursorX - totalCharacterCount)));
            line.add(Component.literal("" + c).withStyle(currentStyle));

            return;
        }

        var existingStyle = line.get(segmentIndex).getStyle();
        var existingText = line.get(segmentIndex).getString();

        if (cursorX == totalCharacterCount) {
            if (existingStyle.equals(currentStyle)) {
                line.set(segmentIndex, Component.literal(existingText + c).withStyle(currentStyle));
            } else {
                line.add(Component.literal("" + c).withStyle(currentStyle));
            }

            return;
        }

        if (existingStyle.equals(currentStyle)) {
            var newContent = new StringBuilder(existingText).replace(cursorX - segmentStartCharacterCount, cursorX - segmentStartCharacterCount + 1, "" + c).toString();

            line.set(segmentIndex, Component.literal(newContent).withStyle(currentStyle));
        } else {
            String textBefore = existingText.substring(0, cursorX - segmentStartCharacterCount);
            var n = cursorX - segmentStartCharacterCount + 1;

            if (n >= existingText.length()) {
                n = existingText.length();
            }

            String textAfter = existingText.substring(n);

            line.set(segmentIndex, Component.literal(textBefore).withStyle(existingStyle));
            line.add(segmentIndex + 1, Component.literal("" + c).withStyle(currentStyle));
            if (!textAfter.isEmpty()) {
                line.add(segmentIndex + 2, Component.literal(textAfter).withStyle(existingStyle));
            }
        }
    }

    public MutableComponent[] getLines() {
        var n = new MutableComponent[lines.size()];

        for (int q = 0; q < lines.size(); q++) {
            var c = lines.get(q);

            var nn = Component.literal("");
            c.forEach(nn::append);

            n[q] = nn;
        }

        return n;
    }

    public void setOutputSize(int r, int d) {
        screenSizeX = r;
        screenSizeY = d;

        var newLineCount = (d * 10) - 2;
        var newLines = new CircularBuffer<ArrayList<Component>>(newLineCount);


        for (int i = 0; i < lines.size(); i++) {
            newLines.add(lines.get(i));
        }

        if (newLineCount < lines.size()) {
            cursorLine += newLineCount - lines.size();
        }

        lines = newLines;
        cursorBounds();
    }
}
