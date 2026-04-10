package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;

@ModuleInfo(
        name = "SpinBot",
        description = "Automatically makes the player derp / spin",
        category = Category.PLAYER
)
public class SpinBotModule extends Module {

    private float yaw;

    @Override
    public void onEnable() {
        if (mc.player != null) {
            yaw = mc.player.getYaw();
        }
    }

    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (mc.player == null) return;

        if (!exceptions()) return;

        yaw += 20.0f;

        if (yaw > 180f) yaw -= 360f;

        event.yaw(yaw);

        event.pitch(mc.player.getPitch());
    }

    private boolean exceptions() {
        var aura = Apex.moduleManager.getByClass(AuraRecodeModule.class);
        var scaffold = Apex.moduleManager.getByClass(ScaffoldModule.class);

        return (aura == null || aura.target == null)
                && (scaffold == null || !scaffold.isEnabled());
    }
}