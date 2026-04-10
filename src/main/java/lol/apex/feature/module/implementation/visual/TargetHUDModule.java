package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.visual.targethud.ClassicTargetHUD;
import lol.apex.feature.module.implementation.visual.targethud.ModernTargetHUD;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "TargetHUD",
        description = "Allows you to see information about the current combat target.",
        category = Category.VISUAL
)
public class TargetHUDModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.MODERN);
    public final BoolSetting showWinning = new BoolSetting("Show Winning", true);

    @RequiredArgsConstructor
    public enum Mode {
        MODERN("Modern"),
        CLASSIC("Classic"),
        NOVOLINE("Novoline");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onRender2D(Render2DEvent event) {
        switch (mode.getValue()) {
            case CLASSIC -> ClassicTargetHUD.onRender2D(this, event);
            case MODERN -> ModernTargetHUD.onRender2D(this, event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
