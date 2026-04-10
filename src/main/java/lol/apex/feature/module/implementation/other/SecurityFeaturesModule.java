package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.ui.notification.Notification;
import lol.apex.feature.ui.notification.NotificationType;
import lol.apex.util.CommonUtil;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

@ModuleInfo(
        name = "SecurityFeatures",
        description = "Certain features to keep you safe.",
        category = Category.OTHER
)
public class SecurityFeaturesModule extends Module {
    private final double cubeCraftSpeedLimit = 8.5d;

    public final BoolSetting cubecraftSpeedCheck = new BoolSetting("CubeCraft Speed", true);

    private boolean ccWasFlagged = false;

    @EventHook
    public void onMove(PlayerMoveEvent event) {
        if (cubecraftSpeedCheck.getValue()) {
            handleCubeCraftBPSCheck();
        }
    }

    private void handleCubeCraftBPSCheck() {
        double bps = PlayerUtil.getPlayerBPS();

        if (bps > cubeCraftSpeedLimit) {
            if (!ccWasFlagged) {
                ccWasFlagged = true;

                //Apex.sendChatMessage("Your BPS (" + String.format("%.2f", bps) + ") is too fast!");
                Apex.notificationRenderer.push(new Notification(NotificationType.WARNING, "Warning.", String.format("%.2f", bps) + " is too fast!"));

                CommonUtil.warningSound();
                MoveUtil.stop();
            }
        } else {
            ccWasFlagged = false;
        }
    }
}
