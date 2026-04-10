package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import net.minecraft.client.gui.screen.DeathScreen;

@ModuleInfo(
        name = "AutoRespawn",
        description = "Automatically respawns you when you die.",
        category = Category.PLAYER
)
public class AutoRespawnModule extends Module {
    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) return;

        if(mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
        }
    }
}
