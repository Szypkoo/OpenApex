package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.implementation.movement.highjump.MatrixHighJump;
import lol.apex.feature.module.implementation.movement.highjump.SpartanFlagHighJump;
import lol.apex.feature.module.implementation.movement.highjump.VulcanHighJump;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;

import lol.apex.feature.module.base.*;
import lombok.RequiredArgsConstructor;

@ModuleInfo( 
    name = "HighJump",
    description = "Allows you to jump higher than usual.",
    category = Category.MOVEMENT
)
public class HighJumpModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VULCAN);
    public final BoolSetting glide = new BoolSetting("Glide", false);
    public final SliderSetting motion = new SliderSetting("Motion", 0.8f, 0.2f, 10f, 0.1f);

    @RequiredArgsConstructor
    public enum Mode {
        VULCAN("Vulcan"),
        MATRIX("Matrix"),
        SPARTAN("Spartan Flag");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mode.getValue() == Mode.VULCAN) {
            VulcanHighJump.onTick(this, event);
        }

        if (mode.getValue() == Mode.SPARTAN) {
            SpartanFlagHighJump.onTick(this, event);
        }
    }

    @EventHook
    public void onMotion(PreMotionEvent event) {
        if (mode.getValue() == Mode.MATRIX) {
            MatrixHighJump.onMotion(this, event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
