package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;

@ModuleInfo(
        name = "Sprint",
        description = "Automatically sprints for you.",
        category = Category.MOVEMENT
)
public class SprintModule extends Module {

    @EventHook
    public void onTick(ClientTickEvent event) {
        mc.options.sprintKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        mc.options.sprintKey.setPressed(false);
    }
}
