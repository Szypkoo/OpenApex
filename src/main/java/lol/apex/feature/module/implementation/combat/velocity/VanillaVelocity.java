package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VanillaVelocity extends SubModuleWithParent<VelocityModule> {

    public VanillaVelocity(VelocityModule parent) {
        super(parent,"Vanilla");
    }

    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if(mc.player == null) return;

        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if(packet.getEntityId() == mc.player.getId()) {
                double horizontal = parent.horizontalVanilla.getValue() / 100.0;
                double vertical = parent.verticalVanilla.getValue() / 100.0;

                double motionX = packet.getVelocity().x * horizontal;
                double motionY = packet.getVelocity().y * vertical;
                double motionZ = packet.getVelocity().z * horizontal;

                mc.player.addVelocity(motionX, motionY, motionZ);
            }
        }
    }

}
