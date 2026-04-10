package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.Render2DEvent;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.visual.nametags.ClassicNameTags;
import lol.apex.feature.module.implementation.visual.nametags.ModernNameTags;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@ModuleInfo(
        name = "Nametags",
        description = "Shows custom nametags on top of players.",
        category = Category.VISUAL
)
public class NametagsModule extends Module {
    public final BoolSetting scaleByDistance = new BoolSetting("Scale Distance", false);

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.MODERN);

    public final ColorSetting friendColor = new ColorSetting("Friend Color", Color.GREEN).hide(()->!mode.getValue().equals(Mode.MODERN));
    public final ColorSetting defaultColor = new ColorSetting("Default Color", Color.WHITE).hide(()->!mode.getValue().equals(Mode.MODERN));


    @RequiredArgsConstructor
    public enum Mode {
        CLASSIC("Classic"),
        MODERN("Modern");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onRender2D(Render2DEvent event) {
        if (mode.getValue() == Mode.MODERN) {
            ModernNameTags.onRender2D(this, event);
        }
    }

    @EventHook
    public void onRenderWorld(RenderWorldEvent event) {
        if (mode.getValue() == Mode.CLASSIC) {
            ClassicNameTags.onRenderWorld(this, event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}