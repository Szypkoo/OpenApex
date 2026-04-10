package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class SprintDisabler extends SubModule {
    public SprintDisabler() {
        super("Sprint");
    }

    public static void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket packet &&
                (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING ||
                        packet.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING)) {
            event.setCancelled(true);
        }
    }
}
