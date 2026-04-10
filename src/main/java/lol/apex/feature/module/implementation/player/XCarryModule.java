package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@ModuleInfo(
        name = "XCarry",
        description = "Lets you carry items in your crafting grid.",
        category = Category.PLAYER
)
public class XCarryModule extends Module {

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if(mc.player == null) return; 
        if(mc.world == null) return;

        Packet<?> packet = event.getPacket();
        if(packet instanceof CloseHandledScreenC2SPacket closeHandledScreenC2SPacket) {
            if(closeHandledScreenC2SPacket.getSyncId() == mc.player.playerScreenHandler.syncId) {
                event.setCancelled(true);
            }
        }
    }
}
