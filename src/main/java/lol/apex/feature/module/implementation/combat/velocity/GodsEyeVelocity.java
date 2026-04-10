package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class GodsEyeVelocity extends SubModule {
    public GodsEyeVelocity() {
        super("GodsEye");
    }

    public static void onPacket(PacketEvent.Receive event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()))
            return;

        if (mc.player != null && mc.player.isOnFire() && (mc.player.hurtTime > 0)) {
            return;
        }

        if(!MoveUtil.isMoving()) {
            return;
        }

        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket velo) {
            if(velo.getEntityId() == mc.player.getId()) {
                event.setCancelled(true);
            }
        }
    }
}
