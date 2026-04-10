package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerInteractionRangeEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;

@ModuleInfo(
        name = "Reach",
        description = "Gives you long arms.",
        category = Category.LEGIT
)
public class ReachModule extends Module {
    public final SliderSetting reachSlider = new SliderSetting("Distance", 1.0f, 3.0f, 6.0f, 0.1f);
    public final BoolSetting blockReach = new BoolSetting("Block Reach", false);

    public float getAmount() {
        return (float) Math.round(reachSlider.getValue() * 10) / 10f;
    }

    @EventHook
    public void onReachEntity(PlayerInteractionRangeEvent.Entity event) {
        if (!this.enabled()) return;

        event.setReach(getAmount());
        event.setCancelled(true);
    }

    @EventHook
    public void onReachBlock(PlayerInteractionRangeEvent.Block event) {
        if (!this.enabled()) return;

        if (!blockReach.getValue()) {
            return;
        }

        event.setReach(getAmount());
        event.setCancelled(true);
    }
}
