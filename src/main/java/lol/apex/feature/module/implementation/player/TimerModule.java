package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.game.GameTimer;

@ModuleInfo(
        name = "Timer",
        description = "Modifies the game's tick speed.",
        category = Category.PLAYER
)
public class TimerModule extends Module {

    public final SliderSetting amount = new SliderSetting("Speed", 0.1f, 1f, 10f, 0.1f);

    @Override
    public void onDisable() {
        GameTimer.setSpeed(0f);
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        GameTimer.setSpeed(amount.getValue().floatValue());
    }
}
