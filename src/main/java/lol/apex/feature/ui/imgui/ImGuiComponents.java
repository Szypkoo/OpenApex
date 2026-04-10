package lol.apex.feature.ui.imgui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiCol;

public final class ImGuiComponents {
    private static final int GRADIENT_STEPS = 64;

    private ImGuiComponents() {
    }

    public static boolean coloredButton(final String label, final int textColor, final int bgColor1, final int bgColor2) {
        return coloredButton(label, 0.0f, 0.0f, textColor, bgColor1, bgColor2);
    }

    public static boolean coloredButton(final String label, final ImVec2 size, final int textColor, final int bgColor1, final int bgColor2) {
        return coloredButton(label, size.x, size.y, textColor, bgColor1, bgColor2);
    }

    public static boolean coloredButton(final String label, final float width, final float height, final int textColor, final int bgColor1, final int bgColor2) {
        final ImGuiStyle style = ImGui.getStyle();
        final String visibleLabel = getVisibleLabel(label);
        final ImVec2 labelSize = ImGui.calcTextSize(visibleLabel, true);

        final float resolvedWidth = width > 0.0f ? width : labelSize.x + style.getFramePaddingX() * 2.0f;
        final float resolvedHeight = height > 0.0f ? height : labelSize.y + style.getFramePaddingY() * 2.0f;

        final boolean pressed = ImGui.invisibleButton(label, resolvedWidth, resolvedHeight);
        final boolean hovered = ImGui.isItemHovered();
        final boolean held = ImGui.isItemActive();

        final float minX = ImGui.getItemRectMinX();
        final float minY = ImGui.getItemRectMinY();
        final float maxX = ImGui.getItemRectMaxX();
        final float maxY = ImGui.getItemRectMaxY();
        final float rounding = style.getFrameRounding();

        int drawBgColor1 = bgColor1;
        int drawBgColor2 = bgColor2;
        if (hovered || held) {
            drawBgColor1 = brightenInteractiveColor(bgColor1, held);
            drawBgColor2 = bgColor1 == bgColor2 ? drawBgColor1 : brightenInteractiveColor(bgColor2, held);
        }

        final ImDrawList drawList = ImGui.getWindowDrawList();
        final boolean gradient = drawBgColor1 != drawBgColor2;

        drawList.addRectFilled(minX, minY, maxX, maxY, drawBgColor1, rounding);
        if (gradient) {
            drawVerticalGradient(drawList, minX, minY, maxX, maxY, rounding, drawBgColor1, drawBgColor2);
        }

        final float borderSize = style.getFrameBorderSize();
        if (borderSize > 0.0f) {
            drawList.addRect(minX, minY, maxX, maxY, ImGui.getColorU32(ImGuiCol.Border), rounding, ImDrawFlags.None, borderSize);
        }

        if (!visibleLabel.isEmpty()) {
            final ImFont font = ImGui.getFont();
            final int fontSize = ImGui.getFontSize();
            final ImVec2 align = style.getButtonTextAlign();

            final float availableWidth = Math.max(0.0f, resolvedWidth - style.getFramePaddingX() * 2.0f);
            final float availableHeight = Math.max(0.0f, resolvedHeight - style.getFramePaddingY() * 2.0f);
            final float textX = minX + style.getFramePaddingX() + Math.max(0.0f, (availableWidth - labelSize.x) * align.x);
            final float textY = minY + style.getFramePaddingY() + Math.max(0.0f, (availableHeight - labelSize.y) * align.y);

            drawList.addText(
                    font,
                    fontSize,
                    textX,
                    textY,
                    textColor,
                    visibleLabel,
                    minX + style.getFramePaddingX(),
                    minY + style.getFramePaddingY(),
                    maxX - style.getFramePaddingX(),
                    maxY - style.getFramePaddingY()
            );
        }

        return pressed;
    }

    private static void drawVerticalGradient(
            final ImDrawList drawList,
            final float minX,
            final float minY,
            final float maxX,
            final float maxY,
            final float rounding,
            final int topColor,
            final int bottomColor
    ) {
        final float height = maxY - minY;
        if (height <= 0.0f) {
            return;
        }
        final float width = maxX - minX;
        final float effectiveRounding = Math.min(rounding, Math.min(width, height) * 0.5f);

        for (int i = 0; i < GRADIENT_STEPS; i++) {
            final float t0 = i / (float) GRADIENT_STEPS;
            final float t1 = (i + 1) / (float) GRADIENT_STEPS;
            final float y0 = minY + height * t0;
            final float y1 = minY + height * t1;
            final int stripColor = lerpColor(topColor, bottomColor, (t0 + t1) * 0.5f);

            final float insetLeft = Math.max(getHorizontalInset(y0 - minY, height, effectiveRounding), getHorizontalInset(y1 - minY, height, effectiveRounding));
            final float insetRight = insetLeft;
            final float stripMinX = minX + insetLeft;
            final float stripMaxX = maxX - insetRight;

            if (stripMaxX > stripMinX && y1 > y0) {
                drawList.addRectFilled(stripMinX, y0, stripMaxX, y1, stripColor);
            }
        }
    }

    private static float getHorizontalInset(final float localY, final float height, final float rounding) {
        if (rounding <= 0.0f) {
            return 0.0f;
        }

        final float clampedY = Math.max(0.0f, Math.min(height, localY));
        if (clampedY < rounding) {
            return getCornerInset(rounding, clampedY);
        }

        if (clampedY > height - rounding) {
            return getCornerInset(rounding, height - clampedY);
        }

        return 0.0f;
    }

    private static float getCornerInset(final float radius, final float distanceFromEdge) {
        final float clamped = Math.max(0.0f, Math.min(radius, distanceFromEdge));
        final float dy = radius - clamped;
        return radius - (float) Math.sqrt(Math.max(0.0f, radius * radius - dy * dy));
    }

    private static int brightenInteractiveColor(final int color, final boolean held) {
        final ImVec4 rgba = ImGui.colorConvertU32ToFloat4(color);
        final float[] rgb = {rgba.x, rgba.y, rgba.z};
        final float[] hsv = new float[3];
        ImGui.colorConvertRGBtoHSV(rgb, hsv);

        hsv[0] = Math.min(hsv[0] + 0.02f, 1.0f);
        hsv[2] = Math.min(hsv[2] + (held ? 0.20f : 0.07f), 1.0f);

        final float[] outRgb = new float[3];
        ImGui.colorConvertHSVtoRGB(hsv, outRgb);
        return ImGui.colorConvertFloat4ToU32(outRgb[0], outRgb[1], outRgb[2], rgba.w);
    }

    private static int lerpColor(final int startColor, final int endColor, final float t) {
        final float clampedT = Math.clamp(t, 0.0f, 1.0f);
        final ImVec4 start = ImGui.colorConvertU32ToFloat4(startColor);
        final ImVec4 end = ImGui.colorConvertU32ToFloat4(endColor);

        return ImGui.colorConvertFloat4ToU32(
                start.x + (end.x - start.x) * clampedT,
                start.y + (end.y - start.y) * clampedT,
                start.z + (end.z - start.z) * clampedT,
                start.w + (end.w - start.w) * clampedT
        );
    }

    private static String getVisibleLabel(final String label) {
        final int idMarker = label.indexOf("##");
        if (idMarker < 0) {
            return label;
        }
        return label.substring(0, idMarker);
    }
}
