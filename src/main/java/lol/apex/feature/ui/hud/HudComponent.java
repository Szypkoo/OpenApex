package lol.apex.feature.ui.hud;

import lol.apex.event.render.Render2DEvent;
import lol.apex.util.CommonVars;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.math.Vec2f;

@RequiredArgsConstructor
public abstract class HudComponent implements CommonVars {
    @RequiredArgsConstructor
    public enum HorizontalAnchor {
        LEFT("Left"),
        CENTER("Center"),
        RIGHT("Right");

        private final String name;

        @Override
        public String toString() {
            return name;
        }

        public static HorizontalAnchor fromString(String input) {
            return switch (input.toUpperCase()) {
                case "LEFT" -> LEFT;
                case "RIGHT" -> RIGHT;
                default -> CENTER;
            };
        }
    }

    @RequiredArgsConstructor
    public enum VerticalAnchor {
        TOP("Top"),
        MIDDLE("Middle"),
        BOTTOM("Bottom");

        private final String name;

        @Override
        public String toString() {
            return name;
        }

        public static VerticalAnchor fromString(String input) {
            return switch (input.toUpperCase()) {
                case "TOP" -> TOP;
                case "BOTTOM" -> BOTTOM;
                default -> MIDDLE;
            };
        }
    }

    public final String name;
    public boolean enabled = true;

    public Vec2f
            position = new Vec2f(0, 0),
            size = new Vec2f(0, 0);

    public HorizontalAnchor horizontalAnchor = HorizontalAnchor.LEFT;
    public VerticalAnchor verticalAnchor = VerticalAnchor.TOP;
    public float anchorOffsetX;
    public float anchorOffsetY;
    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;
    private float lastResolvedWidth = -1.0f;
    private float lastResolvedHeight = -1.0f;

    public abstract void render(Render2DEvent event);

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void setAnchors(HorizontalAnchor horizontalAnchor, VerticalAnchor verticalAnchor) {
        this.horizontalAnchor = horizontalAnchor;
        this.verticalAnchor = verticalAnchor;
    }

    public void updateAnchorOffsetsFromPosition(int screenWidth, int screenHeight) {
        anchorOffsetX = position.x - getAnchorBaseX(screenWidth);
        anchorOffsetY = position.y - getAnchorBaseY(screenHeight);
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;
        lastResolvedWidth = size.x;
        lastResolvedHeight = size.y;
    }

    public void updateAnchorOffsetsFromPosition() {
        updateAnchorOffsetsFromPosition(getScreenWidth(), getScreenHeight());
    }

    public void chooseClosestAnchorAndUpdateOffsets(int screenWidth, int screenHeight) {
        horizontalAnchor = getClosestHorizontalAnchor(screenWidth);
        verticalAnchor = getClosestVerticalAnchor(screenHeight);
        updateAnchorOffsetsFromPosition(screenWidth, screenHeight);
    }

    public void resolvePositionForCurrentScreen() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        boolean screenChanged = lastScreenWidth != screenWidth || lastScreenHeight != screenHeight;
        boolean sizeChanged = lastResolvedWidth != size.x || lastResolvedHeight != size.y;
        if (screenChanged || sizeChanged) {
            position.x = clamp(getAnchorBaseX(screenWidth) + anchorOffsetX, 0.0f, getMaxX(screenWidth));
            position.y = clamp(getAnchorBaseY(screenHeight) + anchorOffsetY, 0.0f, getMaxY(screenHeight));
        }

        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;
        lastResolvedWidth = size.x;
        lastResolvedHeight = size.y;
    }

    public float getAnchorBaseX(int screenWidth) {
        return switch (horizontalAnchor) {
            case LEFT -> 0.0f;
            case CENTER -> (screenWidth - size.x) * 0.5f;
            case RIGHT -> screenWidth - size.x;
        };
    }

    public float getAnchorBaseY(int screenHeight) {
        return switch (verticalAnchor) {
            case TOP -> 0.0f;
            case MIDDLE -> (screenHeight - size.y) * 0.5f;
            case BOTTOM -> screenHeight - size.y;
        };
    }

    private HorizontalAnchor getClosestHorizontalAnchor(int screenWidth) {
        HorizontalAnchor bestAnchor = horizontalAnchor;
        float bestDistance = Float.MAX_VALUE;

        for (HorizontalAnchor anchor : HorizontalAnchor.values()) {
            horizontalAnchor = anchor;
            float distance = Math.abs(position.x - getAnchorBaseX(screenWidth));
            if (distance < bestDistance) {
                bestDistance = distance;
                bestAnchor = anchor;
            }
        }

        horizontalAnchor = bestAnchor;
        return bestAnchor;
    }

    private VerticalAnchor getClosestVerticalAnchor(int screenHeight) {
        VerticalAnchor bestAnchor = verticalAnchor;
        float bestDistance = Float.MAX_VALUE;

        for (VerticalAnchor anchor : VerticalAnchor.values()) {
            verticalAnchor = anchor;
            float distance = Math.abs(position.y - getAnchorBaseY(screenHeight));
            if (distance < bestDistance) {
                bestDistance = distance;
                bestAnchor = anchor;
            }
        }

        verticalAnchor = bestAnchor;
        return bestAnchor;
    }

    private float getMaxX(int screenWidth) {
        return Math.max(0.0f, screenWidth - size.x);
    }

    private float getMaxY(int screenHeight) {
        return Math.max(0.0f, screenHeight - size.y);
    }

    private int getScreenWidth() {
        return mc.getWindow().getFramebufferWidth();
    }

    private int getScreenHeight() {
        return mc.getWindow().getFramebufferHeight();
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
