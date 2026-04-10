package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.MoveUtil;

@ModuleInfo(
        name = "FastStop",
        description = "Immediately stops your movement.",
        category = Category.MOVEMENT
)
public class FastStopModule extends Module {
    public final BoolSetting onGround = new BoolSetting("On Ground", true);
    public final BoolSetting inAir = new BoolSetting("In Air", false);
    public final SliderSetting multiplier = new SliderSetting("Multiplier", 0.5f, 0, 1, 0.05f);

    @EventHook
    public void onMove(PlayerMoveEvent event) {
        if (!this.enabled()) return;

        if ((mc.player.getVelocity().getX() != 0 || mc.player.getVelocity().getZ() != 0) && !MoveUtil.isMoving()) {
            if (!onGround.getValue() && !inAir.getValue()) {
                return;
            }

            if (!onGround.getValue() && mc.player.isOnGround()) {
                return;
            }

            if (!inAir.getValue() && !mc.player.isOnGround()) {
                return;
            }

            if (mc.player.hurtTime != 0) return;

            mc.player.setVelocity(mc.player.getVelocity().multiply(this.multiplier.getValue(), 1, this.multiplier.getValue()));
        }
    }
}