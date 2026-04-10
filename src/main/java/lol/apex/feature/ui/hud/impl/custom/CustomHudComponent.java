package lol.apex.feature.ui.hud.impl.custom;

import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.ui.hud.HudComponent;
import lol.apex.feature.ui.imgui.IImWrapper;

import java.util.List;

public abstract class CustomHudComponent extends HudComponent {
    protected CustomHudComponent(String name) {
        super(name);
    }

    public abstract String getComponentType();

    public abstract List<BaseSetting<?>> getSettings();

    public abstract void renderToWrapper(IImWrapper wrapper);
}
