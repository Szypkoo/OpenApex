package lol.apex.feature.ui.imgui;

import imgui.ImGuiIO;

import java.awt.*;

public interface IImWrapper {
    ImGuiIO getIO();
    void drawBlur(float x, float y, float width, float height);
    void drawBlur(float x, float y, float width, float height, float rounding);
    void drawSelectiveBlur(float x, float y, float width, float height, float radius, boolean tl, boolean tr, boolean br, boolean bl);
    void drawRect(float x, float y, float width, float height, Color color);
    void drawSelectiveRect(float x, float y, float width, float height, float radius, boolean cornerUprLeft, boolean cornerUprRight, boolean cornerBotRight, boolean cornerBotLeft, Color color);
    void drawRoundedRect(float x, float y, float width, float height, float radius, Color color);
    void drawGradientRect(float x, float y, float width, float height, Color... colors);
    void drawString(String text, float x, float y, Color color);
    void drawStringGradient(String text, float x, float y, Color startColor, Color endColor);
    void drawStringGradient(String text, float x, float y, Color startColor, Color endColor, boolean shadow);
    void drawStringGradientShadow(String text, float x, float y, Color startColor, Color endColor);
    void drawString(String text, float x, float y, Color color, boolean shadow);
    void drawStringShadow(String text, float x, float y, Color color);
    void drawString(String font, int size, String text, float x, float y, Color color);
    void drawStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor);
    void drawStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, boolean shadow);
    void drawAnimatedStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, float spread, boolean shadow);
    void drawAnimatedStringGradient(String font, int size, String text, float x, float y, Color startColor, Color endColor, float spread, int offset, boolean shadow);
    void drawStringGradientShadow(String font, int size, String text, float x, float y, Color startColor, Color endColor);
    void drawString(String font, int size, String text, float x, float y, Color color, boolean shadow);
    void drawStringShadow(String font, int size, String text, float x, float y, Color color);

    void drawImageRounded(int textureId, float x, float y, float width, float height, float rounding);
    void drawImageRoundedWithUV(int textureId, float x, float y, float width, float height, float u0, float v0, float u1, float v1, float rounding);
}
