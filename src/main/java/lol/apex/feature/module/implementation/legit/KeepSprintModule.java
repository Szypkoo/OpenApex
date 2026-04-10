package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientPostEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.rotation.MathUtil;

@ModuleInfo(
        name = "KeepSprint",
        description = "Makes you keep your sprint.",
        category = Category.LEGIT
)
public class KeepSprintModule extends Module {
    private final SliderSetting chance = new SliderSetting("Chance", 100f, 0f, 100f, 0.1f);
    private final SliderSetting motion = new SliderSetting("Motion", 100, 0, 100, 0.01f);
    public static final KeepSprintModule INSTANCE = new KeepSprintModule();
    public boolean sprinting = false;

    @EventHook
    private void onPostTick(ClientPostEvent e) {
        sprinting = mc.player.isSprinting();
    }

    public double getMotion() {
        if (MathUtil.nextFloat(0, 100) > chance.getValue()) {
            return 0.6;
        }

        return motion.getValue() / 100;
    }
}
