package lol.apex.feature.ui.hud.impl.custom;

import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiImpl;

import java.awt.*;
import java.util.List;

public class CustomRectangleHudComponent extends CustomHudComponent {
    private final SliderSetting width;
    private final SliderSetting height;
    private final BoolSetting blur;
    private final SliderSetting rounding;
    private final BoolSetting gradient;
    private final ColorSetting primaryColor;
    private final ColorSetting secondaryColor;
    private final List<BaseSetting<?>> settings;

    public CustomRectangleHudComponent(String name, float x, float y) {
        super(name);
        this.position.x = x;
        this.position.y = y;

        this.width = new SliderSetting("Width", 140.0f, 10.0f, 400.0f, 1.0f);
        this.height = new SliderSetting("Height", 60.0f, 10.0f, 300.0f, 1.0f);
        this.blur = new BoolSetting("Blur", false);
        this.rounding = new SliderSetting("Rounding", 0f, 0.0f, 48.0f, 1.0f);
        this.gradient = new BoolSetting("Gradient", false);
        this.primaryColor = new ColorSetting("Primary Color", new Color(255, 255, 255, 180));
        this.secondaryColor = new ColorSetting("Secondary Color", new Color(255, 255, 255, 90)).hide(() -> !gradient.getValue());
        this.settings = List.of(width, height, blur, rounding, gradient, primaryColor, secondaryColor);
    }

    @Override
    public String getComponentType() {
        return "custom_rectangle";
    }

    @Override
    public List<BaseSetting<?>> getSettings() {
        return settings;
    }

    @Override
    public void renderToWrapper(IImWrapper wrapper) {
        size.x = width.getValue();
        size.y = height.getValue();
        resolvePositionForCurrentScreen();

        if (blur.getValue()) {
            wrapper.drawBlur(position.x, position.y, size.x, size.y, rounding.getValue());
        }

        if (gradient.getValue()) {
            wrapper.drawGradientRect(position.x, position.y, size.x, size.y, primaryColor.getValue(), secondaryColor.getValue(), secondaryColor.getValue(), primaryColor.getValue());
            return;
        }

        wrapper.drawRoundedRect(position.x, position.y, size.x, size.y, rounding.getValue(), primaryColor.getValue());
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.options.hudHidden) {
            return;
        }

        ImGuiImpl.render(this::renderToWrapper);
    }
}
