package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.math.TimerUtil;
import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
        name = "Tap",
        description = "Automatically makes you S-Tap or W-Tap.",
        category = Category.LEGIT
)
public class TapModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Type", Mode.W);

    @RequiredArgsConstructor
    private enum Mode {
        S("S"),
        W("W");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private BoolSetting onGround = new BoolSetting("On Ground", true);
    private SliderSetting chance = new SliderSetting("Chance", 0f, 1f, 100f, 1);
    private SliderSetting ms = new SliderSetting("Ms Delay", 0f, 1f, 500f, 1);

    private boolean tapping;
    private TimerUtil timer = new TimerUtil();

    public static boolean isKeyPressed(int key) {
        long window = mc.getWindow().getHandle();
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    @EventHook
    public void onAttack(PlayerAttackEventPre event) {
        if (mc.player == null) return;
        if (Math.random() * 100 > chance.getValue().floatValue()) return;

        var target = mc.targetedEntity;
        if (target == null || !target.isAlive()) return;
        if (onGround.getValue() && !mc.player.isOnGround()) return;
        if (!isKeyPressed(GLFW.GLFW_KEY_W)) return;
        if (!mc.player.isSprinting()) return;

        tapping = true;
        timer.reset();

        switch (mode.getValue()) {
            case W -> mc.options.forwardKey.setPressed(false);
            case S -> mc.options.backKey.setPressed(true);
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (!this.enabled()) return;
        if (mc.player == null || mc.world == null) return;
        if (!tapping) return;

        if (timer.getElapsedTime() > ms.getValue()) {
            switch (mode.getValue()) {
                case W -> mc.options.forwardKey.setPressed(true);
                case S -> mc.options.backKey.setPressed(false);
            }
            tapping = false;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
