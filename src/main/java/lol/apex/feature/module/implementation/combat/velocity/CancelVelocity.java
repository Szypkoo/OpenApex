package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import lol.apex.event.packet.PacketEvent.Receive;

public class CancelVelocity extends SubModule {
    public CancelVelocity() {
        super("Cancel", "Cancels all velocity", "Generic");
    }


    public static void onPacket(VelocityModule parent, Receive event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()))
            return;

        if (mc.player != null && mc.player.isOnFire() && (mc.player.hurtTime > 0)) {
            return;
        } 

        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket velo) {
            if(velo.getEntityId() == mc.player.getId()) {
                event.setCancelled(true);
            }
        }
    }    
}
