package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@ModuleInfo(
        name = "AntiHunger",
        description = "Stops you from loosing hunger. (DOES NOT WORK ON SOME ANTICHEATS!)",
        category = Category.PLAYER
)
public class AntiHungerModule extends Module {

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if(!(event.getPacket() instanceof PlayerMoveC2SPacket packet)) {
            return;
        }

        if(!mc.player.isOnGround() || mc.player.fallDistance > 0.5) {
            return;
        }

        PlayerMoveC2SPacket mod = PacketUtil.modifyOnGround(packet, false);
        event.setPacket(mod);
    }
}
