package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

@ModuleInfo(
        name = "ResourcePackSpoof",
        description = "Bypasses servers requiring you to use a server Resource Pack.",
        category = Category.VISUAL
)
public class ResourcePackSpoofModule extends Module {

    public ResourcePackSpoofModule() {
        enabledNoNoise(true);
    }

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if(event.getPacket() instanceof ResourcePackSendS2CPacket packet) {
            PacketUtil.sendPacket(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
            PacketUtil.sendPacket(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            event.setCancelled(true);
        }
    }
}
