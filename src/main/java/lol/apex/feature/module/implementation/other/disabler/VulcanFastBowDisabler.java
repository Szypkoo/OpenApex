package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.other.DisablerModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.*;

import java.util.concurrent.LinkedBlockingQueue;

public class VulcanFastBowDisabler extends SubModule {
    public VulcanFastBowDisabler() {
        super("VulcanFastBow", "FastBow disabler for Vulcan", "Vulcan");
    }
    public static final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    @Override
    public void onEnable() {
     //   packets.clear();
    }

    @Override
    public void onDisable() {
     //   packets.forEach(PacketUtils::sendSilentPacket);
        packets.clear();
    }

    public static void onPacket(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof PlayerMoveC2SPacket ||
                packet instanceof PlayerMoveC2SPacket.PositionAndOnGround ||
                packet instanceof PlayerMoveC2SPacket.LookAndOnGround ||
                packet instanceof PlayerMoveC2SPacket.Full ||
                packet instanceof PlayerActionC2SPacket ||
                packet instanceof PlayerInteractBlockC2SPacket ||
                packet instanceof PlayerInteractEntityC2SPacket ||
                packet instanceof PlayerInteractItemC2SPacket ||
                packet instanceof KeepAliveC2SPacket ||
                packet instanceof ClientCommandC2SPacket ||
                packet instanceof HandSwingC2SPacket ||
                packet instanceof PlayerInputC2SPacket ||
                packet instanceof UpdateSelectedSlotC2SPacket ||
                packet instanceof ClickSlotC2SPacket ||
                packet instanceof CreativeInventoryActionC2SPacket ||
                packet instanceof CloseHandledScreenC2SPacket ||
                packet instanceof TeleportConfirmC2SPacket) {

            event.setCancelled(true);
            packets.add(packet);
        }
    }

    public static void onTick(DisablerModule parent, ClientTickEvent event) {
        if (mc.player == null || packets.isEmpty()) {
            return;
        }

        if (mc.player.age % 3 == 0) {
            packets.forEach(PacketUtil::sendSilentPacket);
            packets.clear();
        }
    }
}
