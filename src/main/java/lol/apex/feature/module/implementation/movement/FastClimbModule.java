package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.MoveUtil;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "FastClimb",
        description = "Allows you to climb up surfaces faster.",
        category = Category.MOVEMENT
)
public class FastClimbModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<Mode>("Mode", Mode.VANILLA);
    public final SliderSetting speed = new SliderSetting("Speed", 0.3f, 0.1f, 1.0f, 0.01f);

    @RequiredArgsConstructor
    public enum Mode {
        VANILLA("Vanilla");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (climbing() && mc.player.input.playerInput.forward()) {
            MoveUtil.setMotionY(speed.getValue().floatValue());
        }
    }

    private boolean climbing() {
        return mc.player.horizontalCollision && mc.player.isHoldingOntoLadder();
    }
}
