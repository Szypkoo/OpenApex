package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketQueueEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.manager.implementation.BlinkManager;
import lol.apex.Apex;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.implementation.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "FakeLag", 
    description = "Simulates client lag.",
    category = Category.COMBAT
)
public class FakeLagModule extends Module {
    public final SliderSetting delaySetting = new SliderSetting("Delay", 200, 1, 1000, 5);
    public final BoolSetting flushOnWorldChange = new BoolSetting("Flush on world change", true);
    public final BoolSetting flushS2C = new BoolSetting("Flush S2C", false);

    @EventHook
    public void onPacket(PacketQueueEvent.Send event) {
        if(mc.player == null || mc.world == null) return;
        if (Apex.blinkManager.lagTimeC2S() <= delaySetting.getValue())
            event.action = switch (event.packet()) {
                case PlayerInteractEntityC2SPacket ignored -> BlinkManager.Action.FLUSH;
                case PlayerInteractBlockC2SPacket ignored -> BlinkManager.Action.FLUSH;
                default -> BlinkManager.Action.QUEUE;
            };
    }

    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        if (!flushOnWorldChange.getValue()) return;

        Apex.blinkManager.flushC2S(); // flush outgoing packets
        if(flushS2C.getValue()) {
            Apex.blinkManager.flushS2C();
        }

        Apex.notificationRenderer.push("FakeLag", "Flushed due to world change.");
    }
}