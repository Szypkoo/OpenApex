package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.util.game.ChatUtil;
import lol.apex.util.math.TimerUtil;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

@ModuleInfo(
        name = "LagbackDetector",
        description = "Helps you detects lag-backs",
        category = Category.OTHER
)
public class LagBackDetector extends Module {
    private final TimerUtil timer = new TimerUtil();

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if(mc.player == null || mc.world == null) {
            return;
        }

        if(event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            if(timer.passed(1000L, true)) {
                ChatUtil.sendChatMessage("Detected anticheat lagback.");
            }
        }
    }
}
