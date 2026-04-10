package lol.apex.feature.ui.hud.impl.custom;

import imgui.ImGui;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.*;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.feature.ui.imgui.ImGuiImpl;

import java.awt.*;
import java.util.List;

public class CustomTextHudComponent extends CustomHudComponent {
    private static final String[] FONTS = {
            "google24-bold",
            "google24-light",
            "google24-medium",
            "google24-regular",
            "google24-semibold",
            "product-bold",
            "product-regular",
            "arimo-bold",
            "arimo-medium",
            "arimo-regular",
            "arimo-semibold",
            "ibm-bold",
            "ibm-medium",
            "ibm-regular",
            "ibm-semibold"
    };

    private final TextInputSetting text;
    private final BoolSetting gradient;
    private final BoolSetting animatedGradient;
    private final BoolSetting textShadow;
    private final BoolSetting background;
    private final SliderSetting backgroundRounding;
    private final BoolSetting blur;
    private final StringSetting font;
    private final SliderSetting fontSize;
    private final SliderSetting gradientSpread;
    private final ColorSetting primaryColor;
    private final ColorSetting secondaryColor;
    private final ColorSetting backgroundColor;
    private final List<BaseSetting<?>> settings;

    public CustomTextHudComponent(String name, String defaultText, float x, float y) {
        super(name);
        this.position.x = x;
        this.position.y = y;

        this.text = new TextInputSetting("Text", defaultText, 128);
        this.textShadow = new BoolSetting("Text Shadow", true);
        this.gradient = new BoolSetting("Gradient", false);
        this.animatedGradient = new BoolSetting("Animated Gradient", false).hide(() -> !gradient.getValue());
        this.background = new BoolSetting("Background", false);
        this.backgroundRounding = new SliderSetting("Background Rounding", 0f, 0.0f, 48.0f, 1.0f).hide(() -> !background.getValue());
        this.blur = new BoolSetting("Blur", false).hide(() -> !background.getValue());
        this.font = new StringSetting("Font", FONTS);
        this.fontSize = new SliderSetting("Font Size", 24, 10, 72, 1);
        this.gradientSpread = new SliderSetting("Gradient Spread", 0.3f, 0.0f, 3.0f, 0.1f).hide(() -> !gradient.getValue() && !animatedGradient.getValue());
        this.primaryColor = new ColorSetting("Primary Color", Color.RED);
        this.secondaryColor = new ColorSetting("Secondary Color", Color.PINK).hide(() -> !gradient.getValue());
        this.backgroundColor = new ColorSetting("Background Color", new Color(0, 0, 0, 120)).hide(() -> !background.getValue());
        this.settings = List.of(text, textShadow, gradient, animatedGradient, background, backgroundRounding, blur, font, fontSize, gradientSpread, primaryColor, secondaryColor, backgroundColor);
    }

    @Override
    public String getComponentType() {
        return "custom_text";
    }

    @Override
    public List<BaseSetting<?>> getSettings() {
        return settings;
    }

    public String getDisplayText() {
        return text.getValue();
    }

    public void setDisplayText(String value) {
        text.setValue(value);
    }

    public boolean hasBackground() {
        return background.getValue();
    }

    public String getFont() {
        return font.getValue();
    }

    public int getFontSize() {
        return fontSize.getValue().intValue();
    }

    public Color getTextColor() {
        return primaryColor.getValue();
    }

    public void setTextColor(Color color) {
        primaryColor.setValue(color);
    }

    public Color getBackgroundColor() {
        return backgroundColor.getValue();
    }

    public void setBackgroundColor(Color color) {
        backgroundColor.setValue(color);
    }

    public void setBackgroundEnabled(boolean enabled) {
        background.setValue(enabled);
    }

    public void setFont(String value) {
        font.setValue(value);
    }

    public void setFontSize(float value) {
        fontSize.setValue(value);
    }

    @Override
    public void renderToWrapper(IImWrapper wrapper) {
        String drawText = getDisplayText();
        String drawFont = font.getValue();
        int drawFontSize = fontSize.getValue().intValue();

        ImGui.pushFont(ImGuiFonts.getFont(drawFont, drawFontSize));
        var textSize = ImGui.calcTextSize(drawText);
        ImGui.popFont();

        float padding = 4;
        size.x = textSize.x + padding * 2;
        size.y = textSize.y + padding * 2;
        resolvePositionForCurrentScreen();

        if (background.getValue()) {
            if (blur.getValue())
                wrapper.drawBlur(position.x, position.y, size.x, size.y, backgroundRounding.getValue());
            wrapper.drawRoundedRect(position.x, position.y, size.x, size.y, backgroundRounding.getValue(), backgroundColor.getValue());
        }

        if (gradient.getValue()) {
            if (animatedGradient.getValue()) {
                wrapper.drawAnimatedStringGradient(
                        drawFont,
                        drawFontSize,
                        drawText,
                        position.x + padding,
                        position.y + padding,
                        primaryColor.getValue(),
                        secondaryColor.getValue(),
                        gradientSpread.getValue(),
                        textShadow.getValue()
                );
            } else {
                wrapper.drawStringGradient(
                        drawFont,
                        drawFontSize,
                        drawText,
                        position.x + padding,
                        position.y + padding,
                        primaryColor.getValue(),
                        secondaryColor.getValue(),
                        textShadow.getValue()
                );
            }
        } else {
            wrapper.drawString(
                    drawFont,
                    drawFontSize,
                    drawText,
                    position.x + padding,
                    position.y + padding,
                    primaryColor.getValue(),
                    textShadow.getValue()
            );
        }
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.options.hudHidden) {
            return;
        }

        ImGuiImpl.render(this::renderToWrapper);
    }
}
