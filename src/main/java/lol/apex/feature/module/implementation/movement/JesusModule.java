package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.movement.jesus.JitterJesus;
import lol.apex.feature.module.implementation.movement.jesus.MatrixJesus;
import lol.apex.feature.module.implementation.movement.jesus.ThemisJesus;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Jesus",
        description = "Allows you to walk on water.",
        category = Category.MOVEMENT
)
public class JesusModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.JITTER);
    public final SliderSetting jitterStrength = new SliderSetting("Jitter Strength", 0.0015f, 0.0001f, 0.10f, 0.0001f);
    public final SliderSetting speed = new SliderSetting("Speed", 0.45f, 0.1f, 1.0f, 0.01f);

    public boolean water = false;

    @RequiredArgsConstructor
    public enum Mode {
        JITTER("Jitter"),
        MATRIX("Matrix"),
        THEMIS("Themis");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onMove(PlayerMoveEvent event) {
        switch (mode.getValue()) {
            case JITTER -> JitterJesus.onMove(this, event);
            case MATRIX -> MatrixJesus.onMove(this, event);
            case THEMIS -> ThemisJesus.onMove(event);
        }

        event.setCancelled(true);
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}