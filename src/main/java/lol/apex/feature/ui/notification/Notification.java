package lol.apex.feature.ui.notification;

import imgui.ImGui;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.util.CommonUtil;
import lol.apex.util.animation.api.AnimationUtil;
import lol.apex.util.animation.api.Easing;
import lombok.Getter;

import java.awt.*;

@Getter
public class Notification {
    private final NotificationType type;
    private final String title, description;
    private final long duration;

    private final long startTime;

    private final AnimationUtil animation;

    private boolean closing;

    public Notification(NotificationType type, String title, String description, long duration) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.duration = duration;

        this.startTime = System.currentTimeMillis();
        this.animation = new AnimationUtil(Easing.EASE_OUT_QUAD, 300);
    }

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, 3000);
    }

    public void render(IImWrapper wrapper, float x, float y) {
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed > duration) {
            closing = true;
        }

        animation.run(closing ? 0f : 1f);

        float alpha = animation.getValue();
        float offsetX = (1f - alpha) * 120f;

        float iconSpace = 24 + 9;

        float width = getWidth();
        float height = 40f;
        float renderX = x + offsetX;

        Color bg = applyAlpha(new Color(30, 30, 30), alpha);

        Color titleText = applyAlpha(new Color(CommonUtil.getFirstClientColor()), alpha);
        Color titleText2 = applyAlpha(new Color(CommonUtil.getSecondClientColor()), alpha);
        Color text = applyAlpha(Color.WHITE, alpha);

        wrapper.drawRoundedRect(renderX, y, width, height, 5, bg);

        float textX = renderX + iconSpace + 5;

        wrapper.drawString("icomoon", 24, type.icon, renderX + 5, y + 7, text);
        wrapper.drawAnimatedStringGradient("product-bold", 20, title, textX, y + 3, titleText, titleText2, 0.3f, true);
        wrapper.drawString("product-regular", 18, description, textX, y + 18, text);
    }

    public float getWidth() {
        ImGui.pushFont(ImGuiFonts.getFont("product-regular", 18));
        float descWidth = ImGui.calcTextSize(description).x;
        ImGui.popFont();

        ImGui.pushFont(ImGuiFonts.getFont("product-bold", 20));
        float titleWidth = ImGui.calcTextSize(title).x;
        ImGui.popFont();

        float textWidth = Math.max(titleWidth, descWidth);

        float iconSpace = 24 + 9;
        float padding = 10f;

        return Math.max(200f, textWidth + iconSpace + padding * 2);
    }

    public boolean shouldRemove() {
        return closing && animation.isFinished();
    }

    private Color applyAlpha(Color color, float alpha) {
        int a = (int) (alpha * 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
    }
}