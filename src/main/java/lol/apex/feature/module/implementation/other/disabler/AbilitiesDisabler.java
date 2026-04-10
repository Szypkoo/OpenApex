package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;

public class AbilitiesDisabler extends SubModule {
    public AbilitiesDisabler() {
        super("Abilities", "Abilities disabler.", "Specific");
    } 

    public static void onPacketSend(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof UpdatePlayerAbilitiesC2SPacket) {
            event.setCancelled(true);
        }
    }
}
