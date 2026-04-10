package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VulcanVelocity extends SubModule {
    public VulcanVelocity() {
        super("Vulcan", "StepC 25 switch cases to prevent falses. Wow", "Specific");
    }
    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()))
            return;

        if (mc.player != null && mc.player.isOnFire() && (mc.player.hurtTime > 0)) {
            return;
        }

        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket velo) {
            if(velo.getEntityId() == mc.player.getId()) {
                event.setCancelled(true);
                mc.player.addVelocity(0, velo.getVelocity().y, 0);
            }
        }
    }
}
