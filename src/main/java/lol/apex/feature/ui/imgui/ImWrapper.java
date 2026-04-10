package lol.apex.feature.ui.imgui;

import imgui.*;
import imgui.flag.ImDrawFlags;
import lol.apex.util.render.RenderUtil;
import lol.apex.util.math.ColorUtil;

import java.awt.*;

/**
 * @author markuss
 * @since 26th Sept 2025
 */
public class ImWrapper implements IImWrapper {
    private final ImDrawList drawList;
    private final ImGuiIO io;

    public ImWrapper(ImDrawList drawList) {
        this.drawList = drawList;
        this.io = ImGui.getIO();
    }

    @Override
    public ImGuiIO getIO() {
        return io;
    }

    @Override
    public void drawBlur(float x, float y, float width, float height) {
        RenderUtil.drawBlur((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void drawBlur(float x, float y, float width, float height, float rounding) {
        RenderUtil.drawBlur((int) x, (int) y, (int) width, (int) height, rounding);
    }

    @Override
    public void drawSelectiveBlur(float x, float y, float width, float height, float radius, boolean tl, boolean tr, boolean br, boolean bl) {
        RenderUtil.drawBlur((int) x, (int) y, (int) width, (int) height, radius, tl, tr, br, bl);
    }

    @Override
    public void drawRect(float x, float y, float width, float height, Color color) {
        drawList.addRectFilled(x, y, x + width, y + height, ColorUtil.conv(color), 0.0f);
    }

    @Override
    public void drawSelectiveRect(float x, float y, float width, float height, float radius,
                                  boolean cornerUprLeft, boolean cornerUprRight,
                                  boolean cornerBotRight, boolean cornerBotLeft, Color color) {
        int flags = 0;

        if (cornerUprLeft) flags |= ImDrawFlags.RoundCornersTopLeft;
        if (cornerUprRight) flags |= ImDrawFlags.RoundCornersTopRight;
        if (cornerBotRight) flags |= ImDrawFlags.RoundCornersBottomRight;
        if (cornerBotLeft) flags |= ImDrawFlags.RoundCornersBottomLeft;

        if (flags == 0) {
            flags = ImDrawFlags.RoundCornersNone;
        }

        drawList.addRectFilled(x, y, x + width, y + height, ColorUtil.conv(color), radius, flags);
    }

    @Override
    public void drawGradientRect(float x, float y, float width, float height, Color... color) {
        int c1 = ColorUtil.conv(color.length > 0 ? color[0] : Color.WHITE); // top-left
        int c2 = ColorUtil.conv(color.length > 1 ? color[1] : color[0]);   // top-right
        int c3 = ColorUtil.conv(color.length > 2 ? color[2] : color[0]);   // bottom-right
        int c4 = ColorUtil.conv(color.length > 3 ? color[3] : color[0]);   // bottom-left

        drawList.addRectFilledMultiColor(
                x, y,
                x + width, y + height,
                c1, c2, c3, c4
        );
    }

    @Override
    public void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        drawList.addRectFilled(x, y, x + width, y + height, ColorUtil.conv(color), radius);
    }

    @Override
    public void drawString(String text, float x, float y, Color color) {
        drawList.addText(x, y, ColorUtil.conv(color), text);
    }

    @Override
    public void drawStringGradient(String text, float x, float y, Color startColor, Color endColor) {
        drawStringGradient(ImGui.getFont(), ImGui.getFontSize(), text, x, y, startColor, endColor, false);
    }

    @Override
    public void drawStringGradient(String text, float x, float y, Color startColor, Color endColor, boolean shadow) {
        drawStringGradient(ImGui.getFont(), ImGui.getFontSize(), text, x, y, startColor, endColor, shadow);
    }

    @Override
    public void drawStringGradientShadow(String text, float x, float y, Color startColor, Color endColor) {
        drawStringGradient(text, x, y, startColor, endColor, true);
    }

    @Override
    public void drawString(String text, float x, float y, Color color, boolean shadow) {
        if (shadow) {
            this.drawString(text, x + 1.0f, y + 1.0f, ColorUtil.getShadowColor(color));
        }
        this.drawString(text, x, y, color);
    }

    @Override
    public void drawStringShadow(String text, float x, float y, Color color) {
        drawString(text, x, y, color, true);
    }

    @Override
    public void drawString(String font, int size, String text, float x, float y, Color color) {
        drawList.addText(ImGuiFonts.getFont(font, size), size, x, y, ColorUtil.conv(color), text);
    }

    @Override
    public void drawStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor) {
        drawStringGradient(ImGuiFonts.getFont(font, size), size, text, x, y, startColor, endColor, false);
    }

    @Override
    public void drawStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, boolean shadow) {
        drawStringGradient(ImGuiFonts.getFont(font, size), size, text, x, y, startColor, endColor, shadow);
    }

    @Override
    public void drawAnimatedStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, float spread, boolean shadow) {
        drawAnimatedStringGradient(ImGuiFonts.getFont(font, size), size, text, x, y, startColor, endColor, spread, 0, shadow);
    }

    @Override
    public void drawAnimatedStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, float spread, int offset, boolean shadow) {
        drawAnimatedStringGradient(ImGuiFonts.getFont(font, size), size, text, x, y, startColor, endColor, spread, offset, shadow);
    }

    @Override
    public void drawStringGradientShadow(String font, int size, String text, float x, float y, Color startColor, Color endColor) {
        drawStringGradient(font, size, text, x, y, startColor, endColor, true);
    }

    @Override
    public void drawString(String font, int size, String text, float x, float y, Color color, boolean shadow) {
        if (shadow) {
            this.drawString(font, size, text, x + 1.0f, y + 1.0f, ColorUtil.getShadowColor(color));
        }
        this.drawString(font, size, text, x, y, color);
    }

    @Override
    public void drawStringShadow(String font, int size, String text, float x, float y, Color color) {
        drawString(font, size, text, x, y, color, true);
    }

    private void drawStringGradient(ImFont font, int size, String text, float x, float y, Color startColor, Color endColor, boolean shadow) {
        if (text == null || text.isEmpty()) {
            return;
        }

        if (shadow) {
            drawStringGradient(font, size, text, x + 1.0f, y + 1.0f, ColorUtil.getShadowColor(startColor), ColorUtil.getShadowColor(endColor), false);
        }

        float textWidth = font.calcTextSizeAX(size, Float.MAX_VALUE, 0.0f, text);
        if (textWidth <= 0.0f) {
            drawList.addText(font, size, x, y, ColorUtil.conv(startColor), text);
            return;
        }

        float cursorX = x;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float charWidth = font.getCharAdvance(c);
            if (charWidth <= 0.0f) {
                charWidth = font.getFallbackAdvanceX();
            }

            float t = Math.clamp((cursorX - x) / textWidth, 0.0f, 1.0f);
            drawList.addText(font, size, cursorX, y, ColorUtil.lerpColorInt(startColor, endColor, t), String.valueOf(c));
            cursorX += charWidth;
        }
    }

    private void drawAnimatedStringGradient(ImFont font, int size, String text, float x, float y, Color startColor, Color endColor, float speed, int offset, boolean shadow) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float cursorX = x;

        for (int i = 0; i < text.length(); i++) {
            Color color = ColorUtil.getTwoColorGradient(offset + i, speed, startColor, endColor);
            char c = text.charAt(i);
            float charWidth = font.getCharAdvance(c);
            if (charWidth <= 0.0f) {
                charWidth = font.getFallbackAdvanceX();
            }

            if (shadow) {
                drawList.addText(font, size, cursorX + 1.0f, y + 1.0f, ColorUtil.conv(ColorUtil.getShadowColor(color)), String.valueOf(c));
            }
            drawList.addText(font, size, cursorX, y, ColorUtil.conv(color), String.valueOf(c));
            cursorX += charWidth;
        }
    }

    @Override
    public void drawImageRounded(int textureId, float x, float y, float width, float height, float rounding) {
        if (textureId == -1) {
            return;
        }

        drawList.addImageRounded(
                textureId,
                x, y,
                x + width, y + height,
                0.0f, 0.0f,   // UV min
                1.0f, 1.0f,          // UV max
                0xFFFFFFFF,                  // white tint
                rounding
        );
    }

    @Override
    public void drawImageRoundedWithUV(int textureId, float x, float y, float width, float height, float u0, float v0, float u1, float v1, float rounding) {
        if (textureId == -1) {
            return;
        }

        drawList.addImageRounded(
                textureId, x, y, x + width, y + height, u0, v0, u1, v1,
                0xFFFFFFFF,
                rounding
        );
    }
}