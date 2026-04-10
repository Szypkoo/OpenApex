package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.mixin.LivingEntityAccessor;

@ModuleInfo(
        name = "NoJumpDelay",
        description = "Removes delay when jumping.",
        category = Category.LEGIT
)
public class NoJumpDelayModule extends Module {

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;
        if (mc.world == null) return;

        ((LivingEntityAccessor) mc.player).setJumpingCooldown(0);
    }
}
