package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.util.player.PlayerUtil;

@ModuleInfo(
        name = "AntiAFK",
        description = "Stops you from being kicked by being afk.",
        category = Category.MOVEMENT
)
public class AntiAFKModule extends Module {

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(mc.player == null) return;

        PlayerUtil.jump();
    }
}
