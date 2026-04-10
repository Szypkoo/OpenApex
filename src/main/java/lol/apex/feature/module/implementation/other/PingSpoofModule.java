package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(
        name = "PingSpoof",
        description = "Attempts to spoof your client's latency.",
        category = Category.OTHER
)
public class PingSpoofModule extends Module {

    private static final Queue<DelayedPacket> packetQueue = new ConcurrentLinkedQueue<>();

    public final SliderSetting spoofDelay = new SliderSetting("Delay", 100, 0, 1000000, 1);

    @EventHook
    public void onPacketReceive(PacketEvent.Receive event) {

        if (event.getPacket() instanceof KeepAliveS2CPacket packet) {
            event.setCancelled(true);

            packetQueue.add(new DelayedPacket(
                    new KeepAliveC2SPacket(packet.getId()),
                    System.currentTimeMillis() + spoofDelay.getValue().intValue()
            ));
        }

        if (event.getPacket() instanceof CommonPingS2CPacket packet) {
            event.setCancelled(true);

            packetQueue.add(new DelayedPacket(
                    new CommonPongC2SPacket(packet.getParameter()),
                    System.currentTimeMillis() + spoofDelay.getValue().intValue()
            ));
        }
    }

    @EventHook
    public void onTick() {
        while (!packetQueue.isEmpty()) {
            DelayedPacket delayed = packetQueue.peek();

            if (System.currentTimeMillis() >= delayed.time) {
                PacketUtil.sendPacket(delayed.packet);
                packetQueue.poll();
            } else {
                break;
            }
        }
    }

    private record DelayedPacket(Packet<?> packet, long time) {
    }
}
