package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import net.minecraft.util.hit.HitResult;

@ModuleInfo(
        name = "AntiMiss",
        description = "Makes you not able to miss while pvping.",
        category = Category.LEGIT
)
public class AntiMissModule extends Module {

    @EventHook
    public void onAttack(PlayerAttackEventPre event) {
        if(mc.player == null) return; if (mc.world == null) {
            return;
        }

        assert mc.crosshairTarget != null;
        if(mc.crosshairTarget.getType().equals(HitResult.Type.MISS)) {
            event.setCancelled(true);
        }
    }
}