package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.movement.fly.MinibloxFly;
import lol.apex.feature.module.implementation.movement.fly.MotionFly;
import lol.apex.feature.module.implementation.movement.fly.VerusFly;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Fly",
        description = "Allows you to fly like a pelican.",
        category = Category.MOVEMENT
)
public class FlyModule extends Module {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.MOTION);

    @RequiredArgsConstructor
    public enum Mode {

        MOTION("Motion"),
        VERUS("Verus"),
        MINIBLOX("Miniblox");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public final SliderSetting speed = new SliderSetting("Speed", 0.42f, 0.1f, 1.0f, 0.01f).hide(()-> !(mode.getValue() == Mode.MOTION));
    public final SliderSetting increment = new SliderSetting("Increment", 0.42f, 0.1f, 1.0f, 0.01f).hide(()-> !(mode.getValue() == Mode.MOTION));

    @EventHook
    public void onPreMotion(PreMotionEvent event) {
        if (mode.getValue() == Mode.VERUS) {
            VerusFly.onPreMotion(event);
        }
    }

    @EventHook
    public void onMove(PlayerMoveEvent event) {
        if (mode.getValue() == Mode.MOTION) {
            MotionFly.onMove(this, event);
        }

        if (mode.getValue() == Mode.MINIBLOX) {
            MinibloxFly.onMove(this, event);
        }

        event.setCancelled(true);
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
