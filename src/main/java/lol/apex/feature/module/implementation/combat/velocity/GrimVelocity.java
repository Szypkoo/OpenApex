package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class GrimVelocity extends SubModule {
    public GrimVelocity() {
        super("Grim", "Velocity for the Grim Anti-Cheat", "Specific");
    }
    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet
                && packet.getEntityId() == mc.player.getId()) {

            if (mc.player != null && mc.player.isOnGround()) {
                PlayerUtil.jump();
            }
        }
    }
}
